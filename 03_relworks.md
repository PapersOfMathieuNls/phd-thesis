
# Related work



## Clone Detection

Clone detection is an important and difficult task. Throughout the years, researchers and practitioners have developed a considerable number of methods and tools to efficiently detect source code clones. In this section, we first describe the classical clone detection approaches and then present works that focus on local and remote detection.

### Traditional Clone Detection Techniques

Tree-matching and metric-based methods are two sub-categories of syntactic analysis for clone detection. Syntactic analyses consist in building abstract syntax trees (AST) and analyze them with a set of dedicated metrics or searching for identical sub-trees. Many existing AST-based approaches rely on sub-tree comparison to detect clone, including the work of Baxter et al.[@Baxter1998], Wahleret et al. [@Wahler], and the work of Jian et al. with Deckard [@Jiang2007]. An AST-based approach compares metrics computed on the AST, rather than the code itself, to identify clones [@Patenaude1999; @Balazinska].

Text-based techniques use the code and compare sequences of code blocks to each other to identify potential clones. Johnson was perhaps the first one to use fingerprints to detect clones [@Johnson1993; @Johnson1994]. Blocks of code are hashed, producing fingerprints that can be compared. If two blocks share the same fingerprint, they are considered as clones. Manber et al. [@Manber1994] and Ducasse et al. [@StephaneDucasse] refined the fingerprint technique by using leading keywords and dot-plots.

Another approach for detecting clones is to use static analysis and to leverage the semantics of a program to improve the detection. These techniques rely on program dependency graphs, where nodes are statements and edges are dependencies. Then, the problem of finding clones is reduced to the problem of finding identical sub-groups in the program dependency graph. Examples of existing techniques that fall into this category are the ones presented by Krinke et al.[@Krinke2001] and Gabel et al. [@Gabel2008].

Many clone detection tools resort to lexical approaches for clone detection. Here, the code is transformed into a series of tokens. If sub-series repeat themselves, it means that a potential clone is in the code. Some popular tools that use this technique include Dup [@Baker], CCFinder [@Kamiya2002], and CP-Miner [@Li2006].

In 2010, Hummel et al. proposed an approach that is both incremental and scalable using index-based clone detection [@Hummel2010]. Incremental clone detection is a technique where only the changes from one version to another are analysed. Thus, the required computational time is greatly reduced. Using more than 100 machines in a cluster, they managed to drop the computation time of Type 1 and 2 to less than a second while comparing a new version. The time required to find all the clones on a 73 MLOC system was 36 minute. We reach similar performances, for one revision, using a single machine. While being extremely fast and reliable, Hummel et al.'s approach required an industrial cluster to achieve such performance. In our opinion, it is unlikely that standard practitioners have access to such computational power. Moreover, the authors' approach only targets Type 1 and 2 clones. Higo et al. proposed an incremental clone detection approach based on program dependency graphs (PDG) [@Higo2011]. Using PDG is arguably more complex than text comparison and allows the detection of clone structures that are scattered in the program. They were able to analyze 5,903 revisions in 15 hours in Apache Ant.

### Remote Detection of Clones

Yuki et al. conducted one of the few studies on the application of clone management to 
industrial systems [@yamanaka2012industrial]. They implemented a tool named Clone Notifier at 
NEC with the help of experienced practitioners. They specifically focus on clone insertion 
notification, very much like PRECINCT. Unlike PRECINCT however, their approach uses a remote 
approach in which the changes are committed (i.e., they reach the central repository, and 
anyone can pull them into their machines) and a central server analyses the changes. If the 
committed changes contain newly inserted clones, then an email notification is sent.

Zhang et al. proposed CCEvents (Code Cloning Events) [@Zhang2013a]. Their approach monitors code repository continuously and allows stakeholders to use a domain specific language called CCEML to specify which email notifications they wish to receive.

In addition, many commercial tools now include clone detection as part of continuous integration. Codeclimate ^codeclimate, Codacy ^codacy, Scrutinizer ^scrutinizer and Coveralls^coveralls are some examples. These tools will perform various tasks such as executing unit test suites, computing quality metricsm performing clone detection and, provide a report by email.

We argue that remotely detecting clones is not practical because clones can be synchronized by other team members, which may lead to challenging merges when the clones are removed. In addition, the authors did not report performance measurements and the longer it takes for the notification to be sent to the developer, the harder it can be to reconstruct the mind-map required for clone removal.

### Local Detection of Clones

Gode and Koschke [@Gode2009] proposed an incremental clone detector that relies on the results of analysis from past versions of a system to only analyze the new changes. Their clone detector takes the form of an IDE plugin that alerts developers as soon as a clone is inserted into the program.

Zibran and Roy [@Zibran2011; @Zibran2012] proposed another IDE-based clone management system to detect and refactor near-miss clones for Eclipse. Their approach uses a k-difference hybrid suffix tree algorithm. It can detect clones in real-time and propose a semi-automated refactoring process.

Robert el al. [@Tairas2006a] proposed another IDE plugin for Eclipse called CloneDR based on ASTs that introduced novel visualization for clone detection such as scatter-plots.

IDE-based methods tend to issue many warnings to developers that may interrupt their work, hence hindering their productivity [@Ko2006d]. In addition, Latoza et al. [@LaToza2006] found that there exist six different reasons that trigger the use of clones (e.g., copy and paste of code examples, reimplementations of the same functionality in different languages, etc. ). Developers are aware that they are creating clones in five out of six situations. In such cases, warnings provided by IDE-based local detection techniques can be quite disturbing.

Nguyen et al. [@Nguyen2009] proposed an advanced clone-aware source code management system. Their approach uses abstract syntax trees to detect, update, and manage clones. While efficient, their approach does not prevent the introduction of clones, and it is not incremental. Developers have to run a project-wide detection for each version of the program. The same teams [@Nguyen2012a] conducted follow-up study by making Clever incremental. Their new tool, JSync, is an incremental clone detector that will only perform the detection of clones on the new changes.

Niko el al. [@Niko2012] proposed techniques revolving around hashing to obtain a quick answer while detecting Type 1, Type 2, and Type 3 clones in Squeaksource. While their approach works on a single system (i.e., detecting clones on one version of one system), they found that more than 14% of all clones are copied from project to project, stressing the need for fast and scalable approaches for clone detection to detect clone across a large number of projects. On the performance side, Niko el al. were able to perform clone detection on 74,026 classes in 14:45 hours (4,747 class per hour) with an eight core Xeon at 2.3 GHz and 16 GB of RAM. While these results are promising, especially because the approach detects clones across projects and versions, the computing power required is still considerable.

Similarly, Saini et al. [@Saini2016] and Sajnani et al. [@Sajnani2016] proposed an approach, called SourcererCC. SourcererCC targets fast clone detection on developers' workstation (12 GB RAM). SourcererCC is a token-based clone detector that uses an optimized inverted-index. It was tested on 25K projects cumulating 250 MLOC. The technique achieves a precision of 86% and a recall of 86%-100% for clones of Type 1, 2 and 3.

Toomey el al. [@Toomey2012] also proposed an efficient token based approach for detecting clones called ctcompare. Their tokenization is, however, different than most approaches as they used lexical analysis to produce sequences of tokens that can be transformed into token tuples. ctcompare is accurate, scalable and fast but does not detect Type 3 clones.

## Reports and source code relationships

Mining bug repositories is perhaps one of the most active research fields today. The reason is that the analysis of bug reports (BRs) provides useful insights that can help with many maintenance activities such as bug fixing [@Weiß2007; @Saha2014] bug reproduction [@Chen2013; @Artzi2008; @Jin2012], fault analysis [@Nessa2008], etc.
This increase of attention can be further justified by the emergence of many open source bug tracking systems, allowing software teams to make their bug reports available online to researchers.

These studies, however, treat all bugs as the same. For
example, a bug that requires only one fix is analyzed the same way as a bug that necessitates multiple fixes.
Similarly, if multiple bugs are fixed by modifying the exact same locations in the code, then we should investigate how these bugs are related in order to predict them in the future.

Researchers have been studying the relationships between bug and source code repositories for more than two decades.
To the best of our knowledge, the first ones who conducted this type of study on a significant scale were Perry and Stieg [@PerryDewayneE.1993].
In these two decades, many aspects of these relationships have been studied in length.
For example, researchers were interested in improving the bug reports themselves by proposing guidelines [@Bettenburg2008], and by further simplifying existing bug reporting models [@Herraiz2008].

Another field of study consists of assigning these bug reports, automatically if possible, to the right developers during triaging [@Anvik2006; @Jeong2009; @Tamrawi2011a; @Bortis2013].
Another set of approaches focus on how long it takes to fix a bug [@Bhattacharya2011; @Zhang2013; @Saha2014] and where it should be fixed [@Zeller2013a; @Zhou2012].
With the rapidly increasing number of bugs, the community was also interested in prioritizing bug reports [@Kim2011c], and in predicting the severity of a bug [@Lamkanfi2010].
Finally, researchers proposed approaches to predict which bug will get reopened [@Zimmermann2012; @Lo2013], which bug report is a duplicate of another one [@Bettenburg2008a; @Tian2012a; @Jalbert2008] and which locations are likely to yield new bugs [@Kim2006; @Kim2007a].

## Fault Prediction

The majority of previous file/module-level prediction work use code or process metrics. Approaches using code metrics only rely on the information from the code itself and do not use any historical data. Chidamber and Kemerer published the well-known CK metrics suite [@Chidamber1994] for object oriented designs and inspired Moha et al. to publish similar metrics for service-oriented programs [@Moha]. Another famous metric suite for assessing the quality of a given software design is Briand's coupling metrics [@Briand1999a].

The CK and Briand’s metrics suites have been used, for example, by Basili et al. [@Basili1996], El Emam et al. [@ElEmam2001], Subramanyam et al. [@Subramanyam2003] and Gyimothy et al. [@Gyimothy2005] for object-oriented designs. Service oriented designs have been far less studied than object oriented design as they are relatively new, but, Nayrolles et al. [@Nayrolles; @Nayrolles2013a], Demange et al. [@demange2013] and Palma et al. [@Palma2013] used Moha et al. metric suites to detect software defects. All these approaches, proved software metrics to be useful at detecting software faults for object oriented and service oriented designs, respectively. In addition, Nagappan et al. [@Nagappan2005; @Nagappan2006] and Zimmerman et al. [@Zimmermann2007; @Zimmermann2008] further refined metrics-based detection by using statical analysis and call-graph analysis.

Other approaches use historical development data, often referred to as process metrics. Naggapan and Ball [@Nagappan] studied the feasibility of using relative churn metrics to predict buggy modules in Windows Server 2003. Other work by Hassan et al. and Ostrand et al. used past changes and defects to predict buggy locations (e.g., [@Hassan2005], [@Ostrand2005]). Hassan and Holt proposed an approach that highlights the top ten most susceptible locations to have a bug using heuristics based on file-level metrics [@Hassan2005]. They find that locations that have been recently modified and fixed are the most defect-prone. Similarly, Ostrand et al. [@Ostrand2005] predict future crash location by combining the data from changed and past defect locations. They validate their approach on industrial systems at AT&T. They showed that data from prior changes and defects can effectively predict defect-prone locations for open-source and industrial systems. Kim et al. [@Kim2007a] proposed the bug cache approach, which is an improved technique over Hassan and Holt's approach [@Hassan2005]. Rahman and Devanbu found that, in general, process-based metrics perform as good as or better than code-based metrics [@rahman2013].

Other work focused on the prediction of risky changes. Kim et al. proposed the change classification problem, which predicts whether a change is buggy or clean [@SunghunKim2008]. Hassan [@Hassan2009] used the entropy of changes to predict risky changes. They find that the more complex a change is, the more likely it is to introduce a defect. Kamei et al. performed a large-scale empirical study on change classification [@Kamei2013]. The studies above find that size of a change and the history of the files being changed (i.e., how buggy they were in the past) are the best indicators of risky changes.


## Automatic Patch Generation

Pan et al. [@Pan2008] identified 27 bug fixing patterns that can be applied to fix software bugs in Java programs. They showed that between 45.7 - 63.6% of the bugs could be fixed with their patterns. Later, Kim et al. [@Kim2013] generated patches from human-written patches and showed that their tool, PAR, successfully generated patches for 27 of 119 bugs. Tao et al. [@tao2014automatically] also showed that automatically generated patches can assist developers in debugging tasks. Other work also focused on determining how to best generate acceptable and high quality patches, e.g. [@Dallmeier; @le2012systematic], and determine what bugs are best fit for automatic patch generation [@le2015should].

Our work differs from the work on automated patch generation in that we do not generate patches; rather we use clone detection to determine the similarity of a modification to a previous risky change and suggest to the developer the fixes of the prior risky changes.

## Crash Reproduction

In this section, we put the emphasis on how crash traces are used in crash reproduction tasks.
Existing studies can be divided into two distinct categories: (A) on-field record and in-house replay techniques [@Steven2000;@Narayanasamy2005;@Artzi2008;@Roehm2015], and (B) on-house crash understanding [@Jin2012;@Jin2013;@Zuddas2014;@Chen2013a;@Nayrolles2015].

These two categories yield varying results depending on the selected approach and are mainly differentiated by the need for instrumentation.
The first category of techniques oversees -- by means of instrumentation -- the execution of the target system on the field in order to reproduce the crashes in-house, whereas tools and approaches belonging to the second category only use data produced by the crash such as the crash stack or the core dump at crash time. In the first category, tools record different types of data such as the invoked methods [@Narayanasamy2005], try-catch exceptions [@Rossler2013], or objects [@Jaygarl]. In the second category, existing tools and approaches are aimed towards understanding the causes of a crash, using data produced by the crash itself, such as a crash stack [@Chen2013a], previous -- and controlled -- execution [@Zuddas2014], etc.

Tools and approaches that rely on instrumentation face common limitations such as the need to instrument the source code in order to introduce logging mechanisms [@Narayanasamy2005; @Jaygarl; @Artzi2008], which is known to slow down the subject system.
In addition,  recording system behavior by means of instrumentation may yield privacy concerns.
Tools and approaches that only use data about a crash -- such as core dump or exception stack crashes -- face a different set of limitations. They have to reconstruct the timeline of events that have led to the crash [@Chen2013a; @Nayrolles2015]. Computing all the paths from the initial state of the software to the crash point is an NP-complete problem, and may cause state space explosion [@Chen2013a;@Clause2007].

In order to overcome these limitations, some researchers have proposed to
use various SMT (satisfiability modulo theories) solvers [@Dutertre2006]
and model checking techniques [@Visser2003].
However, these techniques require knowledge that goes beyond traditional
software engineering, which hinders their adoption  [@Visser2004].

It is worth mentioning that both categories share a common limitation.
It is possible for the required condition to reproduce a crash to be purely external such as the reading of a file that is only present on the hard drive of the customer or the reception of a faulty network packet
[@Chen2013a; @Nayrolles2015].
It is almost impossible to reproduce the bug without this input.

### On-field Record and In-house Replay

Jaygarl et al. created OCAT (Object Capture based Automated Testing)
[@Jaygarl].
The authors' approach starts by capturing objects created by the program when it runs on-field in order to provide them with an automated test process.
The coverage of automated tests is often low due to lack of correctly constructed objects. Also, the objects can be mutated by means of evolutionary algorithms.
These mutations target primitive fields in order to create even more objects and, therefore, improve the code coverage.
While not directly targeting the reproduction of a bug, OCAT is an approach that was used as the main mechanism for bug reproduction systems.

Narayanasamy et al. [@Narayanasamy2005] proposed BugNet, a tool that continuously records program execution for deterministic replay debugging. According to the authors, the size of the recorded data needed to reproduce a bug with high accuracy is around 10MB. This recording is then sent to the developers and allows the deterministic replay of a bug. The authors argued that with nowadays Internet bandwidth the size of the recording is not an issue during the transmission of the recorded data.

Another approach in this category was  proposed by Clause et al.  [@Clause2007]. The approach records the execution of the program on the client side and compresses the generated data. Moreover, the approach keeps compressed traces of all accessed documents in the operating system.
This data is sent to the developers to replay the execution of the program in a sandbox, simulating the client's environment.
This special feature of the approach proposed by Clause et al.  addresses the limitation where crashes are caused by external causes. While the authors broaden the scope of reproducible bugs, their approach records a lot of data that may be deemed private such as files used for the proper operation of the operating system.

Timelapse [@Burg2013] also addresses the problem of reproducing bugs using external data. The tool focuses on web applications and allows developers to browse and visualize the execution traces recorded by Dolos. Dolos captures and reuses user inputs and network responses to deterministically replay a field crash. Also, both Timelapse and Dolos allow developers to use conventional tools such as breakpoints and classical debuggers. Similar to the approach proposed by Clause  et al.  [@Clause2007], private data are recorded without obfuscation of any sort.

Another approach was proposed by Artzi  et al.  and named ReCrash. ReCrash records the object states of the targeted programs [@Artzi2008]. The authors use an in-memory stack, which contains every argument and object clone of the real execution in order to reproduce a crash via the automatic generation of unit test cases.
Unit test cases are used to provide hints to the developers about the buggy code.
This approach particularly suffers from the limitation related to slowing down the execution.
The overhead for full monitoring is considerably high (between 13\% and 64\% in some cases).
The authors propose an alternative solution in which they record only the methods surrounding the crash. For this to work, the crash has to occur at least once so they could use the information causing the crash to identify the methods surrounding it.ReCrash was able to reproduce 100% (11/11) of the submitted bugs.

Similar to ReCrash, JRapture [@Steven2000] is a capture/replay tool for observation-based testing. The tool captures the execution of Java programs to replay it in-house. To capture the execution of a Java program, the creators of JRapture used their own version of the Java Virtual Machine (JVM) and a lightweight, transparent capture process.
Using a customized JVM allows capturing any interactions between a Java program and the system including GUI, files, and console inputs.
These interactions can be replayed later with exactly the same input sequence as seen during the capture phase.
However, using a custom JVM is not a practical solution. This is because the authors' approach requires users to install a JVM that might have some discrepancies with the original one and yield bugs if used with other software applications.
In our view, JRapture fails to address the limitations caused by instrumentation because it imposes the installation of another JVM that can also monitor other software systems in addition to the intended ones. RECORE (REconstructing CORE dumps) is a tool proposed by Robler et al. The tool instruments Java byte code to wrap every method in a try-catch block while keeping a quasi-null overhead [@Rossler2013]. RECORE starts from the core dump and tries (with evolutionary algorithms) to reproduce the same dump by executing the subject program many times. When the generated dump matches the collected one, the approach has found the set of inputs responsible for the failure and was able to reproduce 85% (6/7) of the submitted bugs.

The approaches presented at this point operate at the code level.
There exist also techniques that focus on recording user-GUI interactions [@Herbold2011; @Roehm2015].
Roehm et al.  extract the recorded data using delta debugging [@Zeller2002], sequential pattern mining, and their combination to reproduce between 75% and 90% of the submitted bugs while pruning 93% of the actions.

Among the approaches presented here, only the ones proposed by Clause et al.  and Burg et al.  address the limitations incurred due to the need for external data at the cost, however, of privacy. To address the limitations caused by instrumentation,  the RECORE approach proposes to let users choose where to put the bar between the speed of the subject program, privacy, and bug reproduction efficiency. As an example, users can choose to contribute or not to improving the software -- policy employed by many major players such as Microsoft in Visual Studio or Mozilla in Firefox -- and propose different types of monitoring where the cost in terms of speed, privacy leaks, and efficiency for reproducing the bug is clearly explained.

### On-house Crash Explanation

On the other side of the picture, we have tools and approaches belonging to the on-house crash explanation  (or understanding), which are fewer but newer than on-field record and replay tools.

Jin et al.  proposed BugRedux for reproducing field failures for in-house debugging [@Jin2012].
The tool aims to synthesize in-house executions that mimic field failures.
To do so, the authors use several types of data collected in the field such as stack traces, crash stack at points of failure, and call sequences.
The data that successfully reproduced the field crash is sent to software developers to fix the bug.
BugRedux relies on several in-house executions that are synthesized so as to narrow down the search scope,  find the crash location, and finally reproduce the bug.
However, these in-house executions have to be conducted before the work on the bug really begins.
Also, the in-house executions suffer from the same limitation as unit testing, i.e., the executions are based on the developer's knowledge and ability to develop exceptional scenarios in addition to the normal ones.
Based on the success of BugRedux, the authors built F3 (Fault
localization for Field Failures) [@Jin2013] and MIMIC [@Zuddas2014]. F3 performs many executions of a program on top of BugRedux in order to cover different paths that are leading to the fault.
It then generates many _pass_ and _fail_ paths, which can lead to a better understanding of the bug. They also use grouping, profiling and filtering, to improve the fault localization process. MIMIC further extends F3 by comparing a model of correct behavior to failing executions and identifying violations of the model as potential explanations for failures.

While being close to our approach, BugRedux and F3 may require the call sequence and/or the complete execution trace in order to achieve bug reproduction. When using only the crash traces (referred to as call stack at crash time in their paper), the success rate of BugRedux significantly drops to 37.5% (6/16). The call sequence and the complete execution trace required to reach 100% accuracy can only be obtained through instrumentation and with an overhead ranging from 10% to 1066%. Chronicle [@Bell2013] is an approach that supports remote debugging by capturing inputs in the application through code instrumentation.
The approach seems to have a low overhead on the instrumented application, around 10%.

Likewise, Zamfir et al.  proposed ESD [@Zamfir2010], an execution synthesis approach that automatically synthesizes failure execution using only the stack trace information. However, this stack trace is extracted from the core dump and may not always contain the components that caused the crash.

To the best of our knowledge, the most complete work in this category is the one of Chen in his PhD thesis [@Chen2013a]. Chen proposed an approach named STAR (Stack Trace based Automatic crash Reproduction). Using only the crash stack, STAR starts from the crash line and goes backward towards the entry point of the program. During the backward process, STAR computes the required condition using an SMT solver named Yices [@Dutertre2006]. The objects that satisfy the required conditions are generated and orchestrated inside a JUnit test case. The test is run, and the resulting crash stack is compared to the original one. If both match, the bug is said to be reproduced. STAR aims to tackle the state explosion problem of reproducing a bug by reconstructing the events in a backward fashion and therefore saving numerous states to explore. STAR was able to reproduce 38 bugs out of 64 (54.6%).
Also, STAR is relatively easy to implement as it uses Yices [@Dutertre2006] and potentially Z3 [@de2008z3] (stated in their future work) that are well-supported SMT solvers.

Except for STAR, existing approaches that target the reproduction of
field crashes require the instrumentation of the code or the running
platform in order to save the stack call or the objects to successfully reproduce bugs. As we discussed earlier, such approaches yield good results 37.5% to 100%, but the instrumentation can cause a massive
overhead (1% to 1066%) while running the system.
In addition, the data generated at run-time using instrumentation
may contain sensitive information.

## Bugs Classification

Another field of study consists of assigning these bug reports, automatically if possible, to the right developers during triaging [@Anvik2006; @Jeong2009; @Tamrawi2011a; @Bortis2013]. It also exist approaches that focus on how long it takes to fix a bug [@Zhang2013; @Bhattacharya2011; @Saha2014] and where it should be fixed [@Zhou2012; @Zeller2013a]. With the rapidly increasing number of bugs, the community was also interested in prioritizing bug reports [@Kim2011c], and in predicting the severity of a bug [@Lamkanfi2010]. Finally, researchers proposed approaches to predict which bug will get reopened [@Zimmermann2012; @Lo2013], which bug report is a duplicate of another one [@Bettenburg2008a; @Tian2012a; @Jalbert2008] and which locations are likely to yield new bugs [@Kim2007; @Kim2006; @Tufano2015]. However, to the best of our knowledge, there are not many attempts to classify bugs the way we present in this paper. In her PhD thesis [@Eldh2001], Sigrid Eldh discussed the classification of trouble reports concerning a set of fault classes that she identified. Fault classes include computational faults, logical faults, ressource faults, function faults, etc. She conducted studies on Ericsson systems and showed the distributions of trouble reports with respect to these fault classes. A research paper was published on the topic [@Eldh2001]. Hamill et al. [@Hamill2014] proposed a classification of faults and failures in critical safety systems. They proposed several types of faults and showed how failures in critical safety systems relate to these classes. They found that only a few fault types were responsible for the majority of failures. They also compare on pre-release and post-release faults and showed that the distributions of fault types differed for pre-release and post-release failures. Another finding is that coding faults are the most predominant ones.

Our study differs from theses studies in the way that we focus on the bugs and their fixes across a wide range of systems, programming languages, and purposes. This is done independently from a specific class of faults (such as coding faults, resource faults, etc.). This is because our aim is not to improve testing as it is the case in the work of Eldh [@Eldh2001] and Hamill et al. [@Hamill2014]. Our objective is to propose a classification that can allow researchers in the filed of mining bug repositories to use the taxonomy as a new criterion in triaging, prediction, and reproduction of bugs. By analogy, we can look at the proposed bug taxonomy similarly to the clone taxonomy presented by Kapser and Godfrey [@CoryKapser]. The authors proposed seven types of source code clones and then conducted a case study, using their classification, on the file system module of the Linux operating system. This clone taxonomy continues to be used by researchers to build better approaches for detecting a given clone type and being able to compare approaches with each other effectively.
