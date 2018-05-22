# Combining Code Metrics with Clone Detection for Just-In-Time Fault Prevention and Resolution

## Introduction



In the previous chapter, we presented BIANCA, an approach that relies on clone-detection to detect risky commits and propose fixes. While the performances of BIANCA are satisfactory, it still has major limitations. For instance, it only supports the Java programming language (tough ut could be expanded to support other languages) and building the model supporting it is incredibly expansive. 

There exist techniques that aim to detect risky commits (e.g., [@Briand1999a; @Chidamber1994; @Subramanyam2003]), among which the most recent approach is the one proposed by Rosen et al. [@Rosen2015]. The authors developed an approach and a supporting tool, Commit-guru, that relies on building models from historical commits using code and process metrics (e.g., code complexity, the experience of the developers, etc.) as main features. These models are used to classify new commits as risky or not. Commit-guru has been shown to outperform previous techniques (e.g., [@Kamei2013; @Kpodjedo2010]).

However, Commit-guru and similar tools suffer from some limitations. First, they tend to generate high false positive rates by classifying healthy commits as risky. The second limitation is that they do not provide recommendations to developers on how to fix the detected risky commits. They simply return measurements that are often difficult to interpret by developers. In addition, they tend to be slow, despite being based on code metrics rather than code comparision because they are not incremental. Indeed, they require pulling the complete history of a project for each analysis. On projects with tens of thousands of commit, this is problematic. Moreover, this tools only support git when the source-code versioning landscape is diverse. As shown by the 2018 StackOverflow survey, companies use Subversion, Team Foundation, Mercurial or other tools at 16.6%, 11.3%, 3.7% and, 19.1% respectively. Our industrial partner for this project uses Git and Mercurial, so we required a more versatile approach. Another shortcomming of Commit-Guru is that it only uses the commit-message to determinate if a commit is a fix rather than the issue control system. This is problematic because developers leverage shortcut hardcoded in commit-message to close issues automatically. For example, the following commit-message `fix #7` would be interpreted by Commit-Guru as a bug-fixing commit regardless of the fact issue #7, on the issue control system could be categorized as a feature to implement. In this case, the word `fix` was merely used to trigger the automatic closing of the issue. 


Finally, they have been mainly validated using open source systems. Their effectiveness, when applied to industrial systems, has yet to be shown.

In this chapter, we propose an approach, called CLEVER (Combining Levels of Bug Prevention and Resolution techniques), that relies on a two-phase process for intercepting risky commits before they reach the central repository. The first phase consists of building a metric-based model to assess the likelihood that an incoming commit is risky or not. This is similar to existing approaches. The next phase relies on clone detection to compare code blocks extracted from suspicious risky commits, detected in the first phase, with those of known historical fault-introducing commits. This additional phase provides CLEVER with two apparent advantages over Commit-guru. First, as we will show in the evaluation section, CLEVER is able to reduce the number of false positives by relying on code matching instead of mere metrics. The second advantage is that, with CLEVER, it is possible to use commits that were used to fix faults introduced by previous commits to suggest recommendations to developers on how to improve the risky commits at hand. This way, CLEVER goes one step further than Commit-guru (and similar techniques) by providing developers with a potential fix for their risky commits.

Another important aspect of CLEVER is its ability to detect risky commits not only by comparing them to commits of a single project but also to those belonging to other projects that share common dependencies. This is important in the context of an industrial setting where  software systems tend to have many dependencies that make them vulnerable to the same faults.

CLEVER was developed in collaboration with software developers from Ubisoft La Forge. Ubisoft is one of the world's largest video game development companies specializing  in the design and implementation of high-budget video games. Ubisoft software systems are highly coupled containing millions of files and commits, developed and maintained by  more than 8,000 developers scattered across 29 locations in six continents.

We tested CLEVER on 12 major Ubisoft systems. The results show that CLEVER  can detect risky commits with 79% precision and 65% recall, which outperforms the performance of Commit-guru (66% precision and 63% recall) when applied to the same dataset. In addition, 66.7% of the proposed fixes were accepted by at least one  Ubisoft software developer, making CLEVER an effective and practical approach for the detection and resolution of risky commits.

## Approach

Figures \ref{fig:CLEVERT1}, \ref{fig:CLEVERT3} and \ref{fig:CLEVERT2} show an overview of the CLEVER approach, which consists of two parallel processes.

In the first process (Figures \ref{fig:CLEVERT1} and \ref{fig:CLEVERT3}), CLEVER manages events happening on project tracking systems to extract fault-introducing commits and commits and their corresponding fixes. For simplicity reasons, in the rest of this paper, we refer to commits that are used to fix defects as _fix-commits_.
We use the term _defect-commit_ to mean a commit that introduces a fault.

The project tracking component of CLEVER listens to bug (or issue) closing events of Ubisoft projects.
Currently, CLEVER is tested on 12 large Ubisoft projects.
These projects share many dependencies.
We clustered them based on their dependencies with the aim to improve the accuracy of CLEVER.
This clustering step is important in order to identify faults that may exist due to dependencies, while enhancing the quality of the proposed fixes.

\input{tex/chap7/approach}

In the second process (Figure \ref{fig:CLEVERT2}), CLEVER intercepts incoming commits before they leave a developer's workstation using the concept of pre-commit hooks. 

Ubisoft's developers already use pre-commit hooks for all sorts of reasons such as identifying the tasks that are addressed by the commit at hand, specifying the reviewers who will review the commit, and so on. Implementing this part of CLEVER as a pre-commit hook is an important step towards the integration of CLEVER with the workflow of developers at Ubisoft. The developers do not have to download, install, and understand additional tools in order to use CLEVER.

Once the commit is intercepted, we compute code and process metrics associated with this commit. The selected metrics are discussed further in Section \ref{sec:offline}. The result is a feature vector (Step 4) that is used for classifying the commit as _risky_ or _non-risky_.

If  the commit is classified as _non-risky_, then the process stops, and the commit can be transferred from the developer's workstation to the central repository. _Risky_ commits, on the other hand, are further analysed in order to reduce the number of false positives (healthy commits that are detected as risky). We achieve this by first extracting the code blocks that are modified by the developer and then compare them to code blocks of known fault-introducing commits.

### Clustering Projects {#sec:clustering}


We cluster projects using the same technic as BIANCA (i.e. Newman algorithm [@Girvan2002; @Newman2004] on the dependcies) for the same reasons (i.e. projects that share dependencies are most likely to contain defects caused by misuse of these dependencies). 

Within the context of Ubisoft, dependencies can be _external_ or _internal_ depending on whether the products are created in-house or supplied by a third-party. For confidentiality reasons, we cannot reveal the name of the projects involved in the project dependency graph. We show the 12 projects in yellow color with their dependencies in blue color in Figure \ref{fig:dep-graph}. In total, we discovered 405 distinct dependencies. Dependencies can be internal (i.e. library developed at Ubisoft) or external (i.e. library provided by third parties).
The resulting partitioning is shown in Figure \ref{fig:network-sample}.

\input{tex/chap7/dependencies}

At Ubisoft, dependencies are managed within the framework of a single repository, which makes their automatic extraction possible. The dependencies could also be automatically retrieved if the projects use a dependency manager such as Maven.

\input{tex/chap7/network-sample}


### Building a Database of Code Blocks of Defect-Commits and Fix-Commits {#sec:offline}

In order to build of database of code blocks of defect-commits and fix-commits we use the same technic as for BIANCA. First, we listen to issue closing-events and extract the blocks of code belonging to incrimated commits (i.e. commit known to have introduced a bug) using our refined version of TXL and NICAD.

### Building a Metric-Based Model {#sec:metric-based}

We adapted Commit-guru [@Rosen2015] for building the metric-based model. Commit-guru uses a list of keywords proposed by Hindle *et al.* [@Hindle2008] to classify commit in terms of _maintenance_, _feature_ or _fix_.
Then, it uses the SZZ algorithm to find the defect-commit linked to the fix-commit. For each defect-commit, Commit-guru computes the following code metrics: _la_ (lines added), _ld_ (lines deleted), _nf_ (number of modified files), _ns_ (number of modified subsystems), _nd_ (number of modified directories), _en_ (distriubtion of modified code across each file), _lt_ (lines of code in each file (sum) before the commit), _ndev_ (the number of developers that modifed the files in a commit), _age_ (the average time interval between the last and current change), _exp_ (number of changes previously made by the author ), _rexp_ (experience weighted by age of files (1 / (n + 1))), _sexp_ (previous  changes made by the author in the same subsystem), _loc_ (total number of modified LOC across all files), _nuc_ (number of unique changes to the files). Then, a statistical model is built using the metric values of the defect-commits. Using linear regression, Commit-guru is able to predict whether incoming commits are _risky_ or not.

We had to modify Commit-guru to fit the context of this study. First, we used information found in Ubisoft's internal project tracking system to classify the purpose of a commit (i.e., _maintenance_, _feature_ or _fix_). In other words, CLEVER only classifies a commit as a defect-commit if it is the root cause of a fix linked to a crash or a bug in the internal project tracking system. Using internal pre-commit hooks, Ubisoft developers must link every commit to a given task #ID. If the task #ID entered by the developer matches a bug or crash report within the project tracking system, then we perform the SCM blame/annotate function on all the modified lines of code for their corresponding files on the fix-commit's parents. This returns the commits that previously modified these lines of code and are flagged as defect-commits. Another modification consists of the actual classification algorithm. We did not use linear regression but instead the random forest algorithm [@TinKamHo; @TinKamHo1998]. The random forest algorithm turned out to be more effective as described in Section \ref{sec:result-bianca}. Finally, we had to rewrite Commit-guru in GoLang for performance and internal reasons.

### Comparing Code Blocks  {#sec:online}

Each time a developer makes a commit, CLEVER intercepts it using a pre-commit hook and classifies it as _risky_ or not.
If the commit is classified as _risky_ by the metric-based classifier, then, we extract the corresponding code block (in a similar way as in the previous phase), and compare it to the code blocks of historical defect-commits.
If there is a match, then the new commit is deemed to be risky. A threshold $\alpha$ is used to assess the extent beyond which two commits are considered similar.

Once again, we reuse the comparing method approach created for BIANCA when it comes to compare blocks of code. 

### Classifying Incoming Commits

As discussed in Section \ref{sec:metric-based}, a new commit goes through the metric-based model first (Steps 1 to 4). If the commit is classified as _non-risky_, we simply let it through, and we stop the process. If the commit is classified as _risky_, however, we continue the process with Steps 5 to 8 in our approach.

One may wonder why we needed to have a metric-based model in the first place. We could have resorted to clone detection as the main mechanism as exposed in the previous chapter. The main reason for having the metric-based model is efficiency. If each commit had to be analysed against all known signatures using code clone similarity, then, it would have made CLEVER impractical at Ubisoft’s scale. We estimate that, in an average workday (i.e. thousands of commits), if all commits had to be compared against all signatures on the same cluster we used for our experiments it would take around 25 minutes to process a commit with the current processing power dedicated to *CLEVER*.
In comparison, it takes, on average, 3.75 seconds with the current two-step approach.

### Proposing Fixes

An important aspect in the design of CLEVER is the ability to provide guidance to developers on how to improve risky commits. We achieve this by extracting from the database the fix-commit corresponding to the top 1 matching defect-commits and present it to the developer. We believe that this makes CLEVER a practical approach. Developers can understand why a given modification has been reported as risky by looking at code instead of simple metrics as in the case of the studies reported in [@Kamei2013; @Rosen2015].

Finally, using the fixes of past defects, we can provide a solution, in the form of a contextualised diff, to developers. A contextualised diff is a diff that is modified to match the current workspace of the developer regarding variable types and names. In Step 8 of Figure 3, we adapt the matching fixes to the actual context of the developer by modifying indentation depth and variable name in an effort to reduce context switching. We believe that this would make it easier for developers to understand the proposed fixes and see if it applies in their situation.

All the proposed fixes will come from projects in the same cluster as the project where the _risky_ commit is.
Thus, developers have access to fixes that should be easier to understand as they come from projects similar to theirs inside the company.

## Experimental Setup {#sec:exp}

In this section, we present the setup of our case study in terms of repository selection, dependency analysis, comparison process and evaluation measures.

### Project Repository Selection {#sec:rep}

In collaboration with Ubisoft developers, we selected 12 major software projects (i.e., systems) developed at Ubisoft to evaluate the effectiveness of CLEVER. These systems continue to be actively maintained by thousands of developers. Ubisoft projects are organized by game engines. A game engine can be used in the development of many high-budget games. The projects selected for this case study are related to the same game engine. For confidentiality and security reasons, neither the names nor the characteristics of these projects are provided. We can, however, disclose that the size of these systems altogether consists of millions of files of code, hundreds of millions of lines of code and hundreds of thousands of commits. All 12 systems are AAA videos games.

### Project Dependency Analysis {#sec:dependencies}

Figure \ref{fig:dep-graph} shows the project dependency graph.
As shown in Figure \ref{fig:dep-graph}, these projects are highly interconnected.
A review of each cluster shows that this partitioning divides projects in terms of their high-level functionalities. For example, one cluster is related to a given family of video games, whereas the other cluster refers to another family. We showed this partitioning to 11 experienced software developers and ask them to validate it. They all agreed that the results of this automatic clustering are accurate and reflects well the various project groups of the company.
The clusters are used for decreasing the rate of false positive.
In addition, fixes mined across projects but within the cluster are qualitative as shown in our experiments.

### Building a Database of Defect-Commits and Fix-Commits {#sub:golden}

To build the database that we can use to assess the performance of CLEVER, we use the same process as discussed in Section \ref{sec:offline}.
We retrieve the full history of each project and label commits as defect-commits if they appear to be linked to a closed issue using the SZZ algorithm [@Kim2006c]. This baseline is used to compute the precision and recall of CLEVER. Each time CLEVER classifies a commit as _risky_; we can check if the _risky_ commit is in the database of defect-introducing commits. The same evaluation process is used by related studies  [@ElEmam2001; @Lee2011a; @Bhattacharya2011; @Kpodjedo2010;@Kamei2013].

### Process of Comparing New Commits {#sec:newcommits}

\input{tex/chap7/comparing}

Similarly to PRECINCT and CLEVER, the repository if cloned, rewinded and replayed commit by commit in order to evaluate the performances of CLEVER. 

While figure \ref{fig:COMPARING} explains the processes of *CLEVER* it does not encompasse all the cases. Indeed, the user that experiences the defect can be internal (i.e. another developer, a tester, ...) or external (i.e. a player). In addition, many other projects receive commits in parallel and they are all to be compared with all the known signatures.

## Empirical Validation {#sec:result-bianca}

In this section, we show the effectiveness of CLEVER in detecting risky commits using a combination of metric-based models and clone detection. The main research question addressed by this case study is: _Can we detect risky commits by combining metrics and code comparison within and across related Ubisoft projects, and if so, what would be the accuracy?_

The experiments took nearly two months using a cluster of six 12 3.6 Ghz cores with 32GB of RAM each. The most time consuming part of the experiment consists of building the baseline as each commit must be analysed with the SZZ algorithm. Once the baseline was established, the model built, it took, on average, 3.75 seconds to analyse an incoming commit on our cluster.

In the following subsections, we provide insights on the performance of CLEVER by comparing it to Commit-guru [@Rosen2015] alone, i.e., an approach that relies only on metric-based models. We chose Commit-guru because it has been shown to outperform other techniques (e.g., [@Kamei2013; @Kpodjedo2010]). Commit-guru is also open source and easy to use.

### Performance of CLEVER

When applied to  12 Ubisoft projects, CLEVER detects risky commits with an average precision, recall, and F1-measure of 79.10%, a 65.61%, and  71.72% respectively. For clone detection, we used a threshold of 30%. This is because Roy _et al._ [@Roy2008] showed through empirical studies that using NICAD with a threshold of around 30%, the default setting, provides good results for the detection of Type 3 clones. When applied to the same projects, Commit-guru achieves an average precision, recall, and F1-measure of 66.71%, 63.01% and 64.80%, respectively.

We can see that with the second phase of CLEVER (clone detection) there is considerable reduction in the number of false positives (precision of 79.10% for CLEVER compared to 66.71% for Commit-guru) while achieving similar recall (65.61% for CLEVER compared to 63.01% for Commit-guru).

\input{tex/chap7/workshop.tex}

### Analysis of the Quality of the Fixes Proposed by CLEVER

In order to validate the quality of the fixes proposed by CLEVER, we conducted an internal workshop where we invited a number of people from Ubisoft development team. The workshop was attended by six participants: two software architects, two developers, one technical lead, and one IT project manager. The participants have many years of experience at Ubisoft.

The participants were asked to review 12 randomly selected fixes that were proposed by CLEVER. These fixes are related to one system in which the participants have excellent knowledge. We presented them with the original buggy commits, the original fixes for these commits, and the fixes that were automatically extracted by CLEVER. We asked them the following question _"Is the proposed fix applicable in the given situation?"_ for each fix.

The review session took around 50 minutes. This does not include the time it took to explain the objective of the session, the setup, the collection of their feedback, etc.

We asked the participants to rank each fix proposed by CLEVER using this scheme:

- Fix Accepted: The participant found the fix proposed by CLEVER applicable to the risky commit.
- Unsure: In this situation, the participant is unsure about the relevance of the fix. There might be a need for more information to arrive to a verdict.
- Fix Rejected: The participant found the fix is not applicable to  the risky commit.

Table \ref{tab:Workshop} shows answers of the participants. The columns refer to the fixes proposed by CLEVER, whereas the rows refer to the participants that we denote using P1, P2, ..., P6.  As we can see from the table, 41.6% of the proposed fixes (F1, F3, F6, F10 and F12) have been accepted by all participants, while 25% have been accepted by at least one member (F4, F8, F11). We analysed the fixes that were rejected by some or all participants to understand the reasons.

$F2$ was rejected by our participants because the region of the commit that triggered a match is a generated code. Although this generated code was pushed into the repositories as part of bug fixing commit, the root cause of the bug lies in the code generator itself. Our proposed fix suggests to update the generated code. Because the proposed fix did not apply directly to the bug and the question we ask our reviewers was _"Is the proposed fix applicable in the given situation?"_ they rejected it.
In this occurrence, the proposed fix was not applicable.

$F4$ was accepted by two reviewers and marked as unsure by the other participants. We believe that this was due the lack of context surrounding the proposed fix. The participants were unable to determine if the fix was applicable or not without knowing what the original intent of the buggy commit was. In our review session, we only provided the reviewers with the regions of the commits that matched existing commits and not the full commit. Full commits can be quite lengthy as they can contain asset descriptions and generated code, in addition to the actual code. In this occurrence, the full context of the commit might have helped our reviewers to decide if $F4$ was applicable or not. $F5$ and $F7$ were classified as unsure by all our participants for the same reasons.

$F8$ was rejected by four of participants and accepted by two. The participants argued that the proposed fix was more a refactoring opportunity than an actual fix.

$F12$ was marked as unsure by all the reviewers because the code had to do with a subsystem that is maintained by another team and the participants felt that it was out of the scope of this session.

After the session, we asked the participants two additional questions: _Will you use CLEVER in the future?_ and _What aspects of CLEVER need to be improved?_

All the participants answered the first question favourably. They also proposed to embed CLEVER with Ubisoft's quality assurance tool suite. The participants reported that the most useful aspects of CLEVER are:

- Ability to leverage many years of historical data of inter-related projects, hence allowing development teams to share their experiences in fixing bugs. 
- Easy integration of CLEVER into developers' work flow based on the tool's ability to operate at commit-time.  
- Precision and recall of the tool (79% and 65% respectively) demonstrating CLEVER's capabilities to catch many defects that would otherwise end up in the code repository. 
For the second question, the participants proposed to add a feedback
loop to CLEVER where the input of software developers is taken into
account during classification. The objective is to reduce the number of
false negatives (risky commits that are flagged as non-risky) and false
positives (non-risky commits that are flagged as risky). The feedback
loop mechanism would work as follows: When a commit is misclassified by
the tool, the software developer can choose to ignore CLEVER's
recommendation and report the misclassified commit. If the fix
proposition is not used, then, we would give that particular pattern
less strength over other patterns automatically. 

We do not need manual input from the user because CLEVER knows the state of the commit before the recommendation
and after. If both versions are
identical then we can mark the recommendation as not helpful. This way, we can also
compensate for human error (i.e., a developer rejecting CLEVER recommendation when the commit was indeed introducing
a defect. We would know this by using the same processes that
allowed us to build our database of defect-commits as described in
Section \ref{sec:offline}. This feature is currently under development.
 
It is worth noting that Ubisoft developers who participated to this study did not think that CLEVER fixes that were 
deemed irrelevant were a barrier to the deployment of CLEVER. In their
point of view, the performance of CLEVER in terms of classification
should make a significant impact as suspicious commits
will receive extended reviews and/or further investigations.

We are also investigating the use of adaptive learning techniques to
improve the classification mechanism of CLEVER. In addition to this, the
participants discussed the limitation of CLEVER as to its inability to
deal with automatically generated code. We are currently working with
Ubisoft's developers to address this limitation.

## University-Industry Research Collaboration

In this section, we describe the lessons learned during this industrial collaboration.

### Deep understanding of the project requirements

Throughout the design of CLEVER, it was important to have a close collaboration between the research team and the Ubisoft developers. This allowed the research team to understand well the requirements of the project. Through this collaboration, both the research and development teams quickly realized that existing work in the literature was not sufficient to address the project’s requirements. In addition, existing studies were mainly tested using open source systems, which may be quite different in structure and size from large industrial systems. In our case, we found that a deep understanding of Ubisoft ecosystem was an important enabler for many decisions we made in this project including the fact that CLEVER operates on multiple systems and that it uses a two-phase mechanism. It was also important to come up with a solution that integrates well with the workflow of Ubisoft developers. This required the development of CLEVER in a way it integrates well with the entire suite of Ubisoft’s version control systems. The key lesson here is to understand well the requirements of a project and its complexity.

### Understanding the benefits of the project to both parties

Understanding how the project benefits the company and the university helps both parties align their vision and work towards a common goal and set of objectives. From Ubisoft’s perspective, the project provides sound mechanisms for building reliable systems. In addition, the time saved from detecting and fixing defects can be shifted to the development of new functionalities that add value to Ubisoft customers. For the university research team, the project provides an excellent opportunity for gaining a better understanding of the complexity of industrial systems and how research can provide effective and practical solutions. Also, working closely with software developers helps uncover the practical challenges they face within the company's context. Companies vary significantly in terms of culture, development processes, maturity levels, etc. Research effort should be directed to develop solutions that overcome these challenges, while taking into account the organizational context.

### Focusing in the Beginning on Low-Hanging Fruits

Low-hanging fruits are quick fixes and solutions. We found that it is a good idea to showcase some quick wins early in the project to show the potential of the proposed solutions. At the beginning of the project, we applied the two-phase process of CLEVER to some small systems with a reasonable number of commits. We showed that the approach improved over the use of metrics alone. We also showed that CLEVER was able to make suggestions on how to fix the detected risky commits. This encouraged us to continue on this path and explore additional features.  We continued to follow an iterative and incremental process throughout the project where knowledge transition between the University and Ubisoft teams is done on a regular basis. 
Building a Strong Technical Team: Working on industrial projects requires all sort of technical skills including programming in various programming languages, the use of tools, tool integration, etc. The strong technical skills of the lead student of this project were instrumental in the success of this project.  It should be noted that Ubisoft systems are programmed using different languages, which complicated the code matching phase of CLEVER. In addition, Ubisoft uses multiple bug management and version control systems. Downloading, processing, and manipulating commits from various environment requires excellent technical abilities.

### Communicating effectively

During the development of CLEVER, we needed to constantly communicate the steps of our research to developers and project owners. Adopting a communication strategy suitable to each stakeholder was important. For example, in our meetings with management, we focused more on the ability of CLEVER to improve code quality and reduce maintenance costs instead of the technical details of the proposed approach. Developers, on the other hand, were interested in the potential of CLEVER and its integration with their work environment.

In this talk, we share our experience conducting a research project at Ubisoft. The project consists of developing techniques and a tool for detecting defects before they reach the code repository. Our approach, called CLEVER, achieves this in two phases using a combination of metric-based machine learning models and clone detection. CLEVER is being deployed at Ubisoft.

### Managing change

Any new initiate brings with it important changes to the way people work. Managing these changes from the beginning of the project increases the chances for tool adoption. To achieve this, we used a communication strategy that involved all the stakeholders including software developers and management to make sure that potential changes that CLEVER would bring are thoroughly and smoothly implemented, and that the benefits of change are long-lasting.

## Threats to Validity

We identified two main limitations of our approach, CLEVER, which require further studies.

CLEVER is designed to work on multiple related systems. Applying CLEVER to a single system will most likely be less effective. The two-phase classification process of CLEVER would be hindered by the fact that it is unlikely to have a large number of similar bugs within the same system. For single systems, we recommend the use of metric-based models. A metric-based solution, however, may turn to be ineffective when applied across systems because of the difficulty associated with identifying common thresholds that are applicable to a wide range of systems.

The second limitation we identified has to do with the fact that CLEVER is designed to work with Ubisoft systems. Ubisoft uses C#, C, C++, Java and other internally developed languages. It is however common to have  other languages used in an environment with many inter-related systems. We intend to extend CLEVER to process commits from other languages as well.

The selection of target systems is one of the common threats to validity for approaches aiming to improve the analysis of software systems. It is possible that the selected programs share common properties that we are not aware of and therefore, invalidate our results. Because of the industrial nature of this study, we had to work with the systems developed by the company.

The programs we used in this study are all based on the C\#, C, C++ and Java programming languages.
This can limit the generalization of the results to projects written in other languages, especially that the main component of CLEVER is based on code clone matching.

Finally, part of the analysis of the CLEVER proposed fixes that we did was based on manual comparisons of the CLEVER fixes with those proposed by developers with a focus group composed of experienced engineers and software architects. Although, we exercised great care in analysing all the fixes, we may have misunderstood some aspects of the commits.

In conclusion, internal and external validity have both been minimized by choosing a set of 12 different systems, using input data that can be found in any programming languages and version systems (commits and changesets).

## Chapter Summary

In this chapter, we presented CLEVER (Combining Levels of Bug Prevention and Resolution Techniques), an approach that detects risky commits (i.e., a commit that is likely to introduce a bug) with an average of 79.10% precision and a 65.61% recall.
CLEVER combines code metrics, clone detection techniques, and project dependency analysis to detect risky commits within and across projects.  CLEVER operates at commit-time, i.e., before the commits reach the central code repository. Also, because it relies on code comparison, CLEVER does not only detect risky commits but also makes recommendations to developers on how to fix them. We believe that this makes CLEVER a practical approach for preventing bugs and proposing corrective measures that integrate well with the developer's workflow through the commit mechanism. CLEVER is still in its infancy and we expect it to be available this year to thousands of developers.

In the next chapter, we present JCHARMING, an approach to reproduce bugs using stacktraces contained in bug report. JCHARMING is meant to be used by developers when the commit-time approaches presented in Chapters 4, 6 and 7 did not manage to prevent the introduction of a defect.
