# Towards a Classification of Bugs Based on the Location of the Corrections: An Empirical Study

## Introduction

In the previous chapters, we investigated how to prevent clones and defect, proposes fixes at commit-time and reproduce field-crash. 

Our works [@Nayrolles2016d; @Nayrolles2016b; @Maiga2015; @Nayrolles2015g; @Nayrolles2018; @Nayrolles2015]) treat bug as equal in a sense that they do not assume any underlying classification of bugs. It also the case for other studies.
By a fix, we mean a modification (adding or deleting lines of code) to an existing file that is used to solve the bug. 

For example, there have been several studies (e.g., [@Weiß2007; @Zhang2013]) that study the factors that influence the bug fixing time.
These studies empirically investigate the relationship between bug report attributes (description, severity, etc.) and the fixing time.
Other studies take bug analysis to another level by investigating techniques and tools for bug prediction and reproduction (e.g., [@Chen2013; @Kim2007a]).

With this in mind, the relationship between bugs and fixes can be modelled using the UML diagram in Figure \ref{fig:bug-taxo-diag}. 
The diagram only includes bug reports that are fixed and not, for example, duplicate reports.
From this figure, we can think of four instances of this diagram, which we refer to as bug taxonomy or simply bug types (see Figure \ref{fig:bug-taxo}).

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.5]{media/chap9/bug-taxo-class-diag.png}
    \caption{Class diagram showing the relationship between bugs and fixed
    \label{fig:bug-taxo-diag}}
\end{figure}

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.6]{media/chap9/bug-taxo.png}
    \caption{Proposed Taxonomy of Bugs
    \label{fig:bug-taxo}}
\end{figure}

The first and second types are the ones we intuitively know about.
T1 refers to a bug being fixed in one single location (i.e., one file), while T2 refers to bugs being fixed in more than one location.
In Figure 2, only two locations are shown for the sake of clarity, but many more locations could be involved in the fix of a bug.
T3 refers to multiple bugs that are fixed in the same location.
T4 is an extension of T3, where multiple bugs are resolved by modifying the same set of locations.
Note that T3 and T4 bugs are not duplicates, they may occur when different features of the system fail due to the same root causes (faults).

In our dataset, composed of 388 projects and presented in section \ref{context-selection}, the proportion of each type of bug is as follows: T1 6.8 %, T2 3.7 %, T3 28.3 % and T4 61.2%. 
Also, classical measures of complexity such as duplication, fixing time, number of comments, number of time a bug is reopened, files impacted, severity, changesets, hunks, and chunks, also presented in section \ref{context-selection}, show that type 4 are significantly more complex than types 1, 2 and 3.

The existence of a high number of T4 bugs and the fact that they are more complex call for techniques that can effectively tag bug report as T4 at submission time for enhanced triaging. More particularly, we are interested in the following research questions:

- **RQ1:** *Are T4 bug predictable at submission time?* In this research question, we investigate if and how to predict the type of a bug report at submission time. Being able to build accurate classifiers predicting the bug type at submission time will allow improving the triaging and the bug handling process.
- **RQ2:** *What are the best predictors of type 4 bugs ?* This second research question aims to investigate what are the markups that allow for accurate prediction of type 4 bugs. 

Our objective is to propose a classification that can allow researchers in the field of mining bug repositories to use the taxonomy as a new criterion in triaging, prediction, and reproduction of bugs.
By analogy, we can look at the proposed bug taxonomy similarly as the clone taxonomy presented by Kapser and Godfrey [@CoryKapser].
The authors proposed seven types of source code clones and then conducted a case study, using their classification, on the file system module of the Linux operating system.
This clone taxonomy continues to be used by researchers to build better approaches for detecting a given clone type and being able to compare approaches with each other effectively.
Moreover, we build upon this proposed classification and predict the type of incoming bugs with a 65.40% precision 94.16% recall for $f_1$ measure of 77.19%.

## Experimental Setup

In this section, we present our datasets in length.
In this presentation, we explore the proportion of each type of bug as well as the complexity of each type.

### Context Selection\label{sec:context-selection}

The context of this study consists of the change history of 388 projects belonging to two software ecosystems, namely, Apache and Netbeans.
Table \ref{table:datasets} reports, for each of them, (i) the number of resolved-fixed reports, (ii) the number of commits, (iii) the overall number of files, and (iv) the number of projects analysed.

\begin{table}[h]
\begin{center}
\begin{tabular}{@{}c|c|c|c|c@{}}
\textbf{Dataset} & \textbf{R/F BR} & \textbf{CS} & \textbf{Files} & \textbf{Projects} \\ \hline \hline
Netbeans         & 53,258          & 122,632     & 30,595         & 39                \\
Apache           & 49,449          & 106,366     & 38,111         & 349               \\
Total            & 102,707         & 229,153     & 68,809         & 388               \\ \hline \hline

\end{tabular}
\end{center}

\caption{Datasets\label{table:datasets}}
\end{table}


All the analysed projects are hosted in *Git* or *Mercurial* repositories and have either a *Jira* or a *Bugzilla* issue tracker associated with them.
The Apache ecosystem consists of 349 projects written in various programming languages (C, C++, Java, Python, ...) and uses *Git* with *Jira*.
These projects represent the Apache ecosystem in its entirety. We did not exclude any system from our study.
The complete list can be found online\footnote{https://projects.apache.org/projects.html?name}.
The Netbeans ecosystem consists of 39 projects, mostly written in Java.
Similar to the Apache ecosystem, we selected all the projects belonging to the Netbeans ecosystem.
The Netbeans community uses *Bugzilla* with *Mercurial*.

The choice of these two ecosystems is driven by the motivation to consider projects are having (i) different sizes, (ii) different architectures, and (iii) different development bases and processes. 
Apache projects vary significantly in terms of the size of the development team, purpose and technical choices [@Bavota2013]. On the other side, Netbeans has a relatively stable list of core developers and a common vision shared by the 39 related projects [@Wang2011].

Cumulatively, these datasets span from 2001 to 2014. In summary, our consolidated dataset contains 102,707 closed issues, 229,153 changesets, 68,809 files that have been modified to fix the bugs, 462,848 comments, and 388 distinct systems.
We also collected 221 million lines of code modified to fix bugs, identified 3,284 sub-projects, and 17,984 unique contributors to these bug reports and source code version management systems.
The cumulated opening time for all the bugs reaches 10,661 working years (3,891,618 working days). 
The raw size of the cloned source code alone, excluding binaries, images, and other non-text files, is 163 GB.

To assign commits to issues, we used the regular expression based approach proposed by Fischer et al. [@Fischer], which matches the issue ID in the commit note to the commit.
Using this technique, we were able to link almost 40% (40,493 out of 102,707) of our resolved/fixed issues to 229,153 commits. Note that an issue can be fixed using several commits.

### Dataset Analysis\label{sec:dataset}

Using our dataset, we extracted the files $f_i$ impacted by each commit $c_i$ for each one of our 388 projects. 
Then, we classified the bugs according to each type, which we formulate as follows: 

- **Type 1:** A bug is tagged T1 if it is fixed by modifying a file $f_i$, and $f_i$ is not involved in any other bug fix.
- **Type 2:** A bug is tagged T2 if it is fixed by modifying by n files, $f_{i..n}$, where n > 1, and the files $f_{i..n}$ are not involved in any other bug fix.
- **Type 3:** A bug is tagged T3 if it is fixed by modifying a file $f_{i}$ and the file $f_{i}$ is involved in fixing other bugs.
- **Type 4:** A bug is tagged T4 if it is fixed by modifying several files $f_{i..n}$ and the files $f_{i..n}$ are involved in any other bug fix.

\input{tex/chap9/bugProportion}

Table \ref{tab:contingency-table} presents the contingency table and the results of the Pearson's chi-squared tests we performed on each type of bug.
We can see that the proportion of T4 (61.2%) largely higher than that of T1 (6.8%), 2 (3.7%) and 3 (28.3%) and that the difference is significant according to the Pearson's chi-squared test. 

Pearson's chi-squared independence test is used to analyse the relationship between two qualitative data, in our study the type bugs and the studied ecosystem. 
The results of Pearson's chi-square independence tests are considered statistically significant at = 0.05. 
If p-value $\leq$ 0.05, we can conclude that the proportion of each type is significantly different.

We analyse the complexity of each bug regarding duplication, fixing time, number of comments, number of time a bug is reopened, files impacted, severity, changesets, hunks, and chunks.

Complexity metrics are divided into two groups: (a) process and (b) code metrics.
Process metrics refer to metrics that have been extracted from the project tracking system (i.e., fixing time, comments, reopening and severity).
Code metrics are directly computed using the source code used to fix a given bug (i.e., files impacted, changesets required, hunks and chunks).
We acknowledge that these complexity metrics only represent an abstraction of the actual complexity of a given bug as they cannot account for the actual thought process and expertise required to craft a fix.
However, we believe that they are an accurate abstraction. Moreover, they are used in several studies in the field to approximate the complexity of a bug [@Weiß2007; @Saha2014; @Nam2013; @Anvik2006; @Nagappan].

Tables \ref{tab:apache-eco}, \ref{tab:netbeans-eco} and \ref{tab:overall-eco} present descriptive statistics about each metric for each bug type per ecosystem and for both ecosystems combined.
The descriptive statistics used are $\mu$:mean, $\sum$:sum, $\hat{x}$:median, $\sigma$:standard deviation and $\%$:percentage.
We also show the results of Mann-Whitney test for each metric and type.
We added the \checkmark symbol to the Mann-Whitney tests results columns when the value is statistically significant (e.g. $\alpha \textless 0.05$) and \xmark otherwise.

\input{tex/chap9/apache-types}
\input{tex/chap9/netbeans-types}
\input{tex/chap9/overall-types}

#### Duplicate

The duplicate metric represents the number of times a bug gets resolved using the  *duplicate* label while referencing one of the  *resolved/fixed* bug of our dataset.
The process metric is useful to approximate the impact of a given bug on the community.
For a bug to be resolved using the  *duplicate*, it means that the bug has been reported before.
The more a bug gets reported by the community, the more people are impacted enough to report it.
Note that, for a bug$\_a$ to be resolved using the  *duplicate* label and referencing bug$\_b$, bug$\_b$ does not have to be resolved itself.
Indeed, bug$\_b$ could be under investigation (i.e.  *unconfirmed*) or being fixed (i.e.  *new* or  *assigned*).
Automatically detecting duplicate bug report is a very active research field [@Sun2011; @Bettenburg2008a; @Nguyen2012; @Jalbert2008; @Tian2012a; @Runeson2007] and a well-known measure of bug impact.

Overall, the complexity of bug types in terms of the number of duplicates is as follows:  $T4_{dup}^{1} \gg T1_{dup}^{3} > T3_{dup}^{2} \gg T2_{dup}^{4}$.

#### Fixing time

The fixing time metric represents the time it took for the bug report to go from the  *new* state to the  *closed* state.
If the bug report is reopened, then the time it took for the bug to go from the  *assigned* state to the  *closed* state is added to the first time.
A bug report can be reopened several times and all the times are added.
In this section, the time is expressed in days [@Weiss2007; @Zhang2012; @Zhang2013].

When combined, both ecosystem amounts in the following order $T2_{time}^4 > T4_{time}^1 \gg T1_{time}^3 \gg T3_{time}^2$.
These findings contradict the finding of Saha  *et al.*, however, they did not study the Netbeans ecosystem in their paper [@Saha2014].

#### Comments

The "number of comments" metric refers to the comments that have been posted by the community on the project tracking system.
This third process metric evaluates the complexity of a given bug in a sense that if it takes more comments (explanation) from the reporter or the assignee to provide a fix, then the bug must be more complex to understand.
The number of comments has been shown to be useful in assessing the complexity of bugs [@Zhang2013; @Zhang2012]. 
It is also used in bug prediction approaches [@DAmbros2010; @Bhattacharya2011].

When combining both ecosystems, the results are: $T4_{comment}^1 \gg T2_{comment}^4 > T3_{comment}^2 \gg T1_{comment}^3$.

#### Bug Reopening

The bug is reopening metric counts how many times a given bug gets reopened.If a bug report is reopened, it means that the fix was arguably hard to come up with or the report was hard to understand [@Zimmermann2012; @Shihab2010; @Lo2013].

When combined, however, the order does change: $T4_{reop}^1 > T2_{reop}^4  > T1_{reop}^3 \gg T3_{reop}^2$.

#### Severity

The severity metric reports the degree of impact of the report on the software.
Predicting the severity of a given report is an active research field
[@Menzies2008; Guo2010; @Lamkanfi2010; @Tian2012; @ValdiviaGarcia2014; @Havelund2015] and it helps to prioritization of fixes [@Xuan2012].
The severity is a textual value (blocker, critical, major, normal, minor, trivial) and the Mann-Whitney test only accepts numerical input.
Consequently, we had to assign numerical values to each severity.
We chose to assign values from 1 to 6 for trivial, minor, normal, major, critical and blocker severities, respectively.

The bug type ordering according to the severity metrics is: $T4_{sev}^1 \gg T3_{sev}^2 \gg T2_{sev}^4 > T1_{sev}^3$, $T2_{sev}^4 > T1_{sev}^3 \gg T3_{sev}^2 \gg T4_{sev}^1$ and $T4_{sev}^1 \gg T3_{sev}^2 > T1_{sev}^3 > T2_{sev}^4$ for Apache, Netbeans, and both combined, respectively.

#### Files impacted

The number of files impacted measures how many files have been modified for the bug report to be closed.

Overall, T4 impacts more files than T2 while T1 and T2 impacts only 1 file ($T4_{files}^1 \gg T2_{files}^3 \gg T3_{files}^2 < = > T1_{files}^4$).

#### Changesets

The changeset metrics registers how many changesets (or commits/patch/fix) have been required to close the bug report.
In the project tracking system, changesets to resolve the bug are proposed and analysed by the community, automated quality insurance tools and the quality insurance team itself.
Each changeset can be either accepted and applied to the source code or dismissed.
The number of changesets (or versions of a given changeset) it takes before integration can hint us about the complexity of the fix.
In case the bug report gets reopen, and new changesets proposed, the new changesets (after the reopening) are added to the old ones (before the reopening).

Overall, T4 bugs are the most complex bugs regarding the number of submitted changesets ($T4_{changesets}^1 \gg T2_{changesets}^3 \gg T3_{changesets}^2 \gg T1_{changesets}^4$).

While results have been published on the bug-fix patterns [@Pan2008], smell introduction [@Tufano2015; @Eyolfson2011], to the best of our knowledge, no one interested themselves in how many iterations of a patch was required to close a bug report beside us.

#### Hunks

The hunks metric counts the number of consecutive code blocks of modified, added or deleted lines in textual files.
Hunks are used to determine, in each file, how many different places a developer has modified.
This metric is widely used for bug insertion prediction [@Kim2006; @Jung2009; @Rosen2015] and bug-fix comprehension [@Pan2008].
In our ecosystems, there is a relationship between the number of files modified and the hunks.
The number of code blocks modified is likely to rise as to the number of modified files as the hunks metric will be at least 1 per file.

We found that T2 and T4 bugs, that requires many files to get fixed, are the ones that have significantly higher scores for the hunks metric; Apache ecosystem: $T4_{hunks}^1 \gg T2_{hunks}^2 \gg T3_{hunks}^3 \gg T1_{hunks}^4$, Netbeans ecosystem: $T4_{hunks}^1 \gg T2_{hunks}^3 \gg T3_{hunks}^2 \gg T1_{hunks}^4$, and overall $T4_{hunks}^1 \gg T2_{hunks}^2 \gg T1_{hunks}^4 \gg T3_{hunks}^3$.

#### Churns

The last metric, churns, counts the number of lines modified.
The churn value for a line change should be at least two as the line has to be deleted first and then added back with the modifications.
Once again, this is a widely used metric in the field [@Kim2006; @Pan2008; @Jung2009; @Rosen2015].

Once again, T4 and T2 are the ones with the most churns; Apache ecosystem $T4_{churns}^1 \gg T2_{churns}^2 \gg T1_{churns}^4 > T3_{churns}^3$, Netbeans ecosystem: $T4_{churns}^1 \gg T2_{churns}^3 \gg T3_{churns}^2 \gg T1_{churns}^4$ and overall : $T4_{churns}^1 \gg T2_{churns}^2 \gg T1_{churns}^4 \gg T3_{churns}^3$.

To determine which type is the most complex, we counted how many times each bug type obtained each position in our nine rankings and multiply them by 4 for the first place, 3 for the second, 2 for the third and 1 for the fourth place.

We did the same simple analysis of the rank of each type for each metric, to take into account the frequency of bug types in our calculation, and multiply both values.
The complexity scores we calculated are as follows: 1330, 1750, 2580 and 7120 for T1, T2, T3 and T4 bugs, respectively.

Considering that type 4 bugs are (a) the most common, (b) the most complex and (c) not a type we intuitively know about; we decided to kick start our research into the different type of bugs and their impact by predicting whether an incoming bug report type 4 or not.

## Empirical Validation

In this section, we present the results of our experiences and interpret them to answer our two research questions.

### Are T4 bug predictable at submission time?

To answer this question, we used as features the words in the bug description contained in a bug report. We removed he stopwords (i.e. the, or, she, he) and truncated the remaining words to their roots (i.e. writing becomes write, failure becomes fail and so on). 
We experimented with 1-gram, 2-gram, and 3-gram words weighted using tf-idf. To build the classifier, we examined three machine learning techniques that have shown to yield satisfactory results in related studies: SVM, Random forest and linear regression [@Weiß2007; @Alencar2014; @Nam2013].

To answer **RQ$_1$**, we analyse the accuracy of predictors aiming at determining the type of a bug at submission time (i.e. when someone submits the bug report).  

Tables \ref{tab:1gram-svm}, \ref{tab:1gram-lr}, \ref{tab:1gram-rf}, \ref{tab:2gram-svm}, \ref{tab:2gram-lr}, \ref{tab:2gram-rf},  \ref{tab:3gram-svm}, \ref{tab:3gram-lr} and \ref{tab:3gram-rf} presents the results obtained while building classifiers for the most complex type of bug. According to the complexity analysis conducted in section \ref{sec:dataset}, the most complex type of bug, in terms of duplicate, time to fix, comments, reopening, files changed, severity, changesets, churns, and hunks is T4.

To answer our research question, we built nine different classifiers using three different machine learning techniques: Linear regression, support vector machines and random forest for ten different projects (5 from each ecosystem).

We selected the top 5 projects of each ecosystem with regard to their bug report count (Ambari, Cassandra, Flume, HBase and Hive for Apache; Cnd, Editor, Java, JavaEE and Platform for Netbeans).
For each machine learning techniques, we built classifiers using the text contained in the bug report and the comment of the first 48 hours as they are likely to provide additional insights on the bug itself. 
We eliminate the stop-words of the text and trim the words to their semantical roots using wordnet.
We experimented with 1-gram, 2-gram, and 3-gram words, weighted using tf/idf. 

The feature vectors are fed to the different machine learning techniques in order to build a classifier.
The data is separated into two parts with a 60%-40% ratio.
The 60% part is used for training purposes while the 40% is used for testing purposes.
During the training process we use the ten-folds technique iteratively and, for each iteration, we change the parameters used by the classifier building process (cost, mtry, etc).
At the end of the iterations, we select the best classifier and exercise it against the second part of 40%.
The results we report in this section are the performances of the nine classifiers trained on 60% of the data and classifying the remaining 40%.
The performances of each classifier are examined in terms of true positive, true negative, false negative and false positive classifications.
True positives and negative numbers refer to the cases where the classifier correctly classify a report.
The false negative represents the number of reports that are classified as non-T4 while they are and false positive represents the number of reports classified as T4 while they are not.
These numbers allow us to derive three common metrics: precision, recall and f_1 measure.

\begin{equation}
precision = \frac{TP+FN \cap TP+FP}{TP+FP}
\end{equation}

\begin{equation}
recall = \frac{TP+FN \cap TP+FP}{TP+FN}
\end{equation}

\begin{equation}
f_1 = \frac{2TP}{2TP + FP + FN}
\end{equation}

The performances of each classifier are compared to a tenth classifier. 
This last classifier is a random classifier that randomly predicts the type of a bug.
As we are in a two classes system (T4 and non-T4), 50% of the reports are classified as T4 by the random classifier.
The performances of the random classifier itself are presented in table \ref{tab:random}.

Finally, we compute the Cohen's Kappa metric [@Fleiss1973] for each classifier. 
The Kappa metric compares the observed accuracy and the expected accuracy to provide a less misleading assessment of the classifier performance than precision alone.

\begin{equation}
kappa = \frac{(observed accuracy - expected accuracy)}{1 - expected accuracy}
\end{equation}

The observed accuracy represents the number of items that were correctly classified, according to the ground truth, by our classifier. 
The expected accuracy represents the accuracy obtained by a random classifier.

\input{tex/chap9/1gram}

For the first three classifiers (SVM, linear regression and random forest with a 1-gram grouping of stemmed words) the best classifier the random forest one with 77.63% $F_1$ measure.
It is followed by SVM (77.19%) and, finally, linear regression (76.31%).
Regardless of the technique used to classify the report, there is no significant difference between ecosystems.
Indeed, the p-values obtained with chi-square tests are above 0.05, and a p-value below 0.05 is a marker of statistical significance.
While random forest emerges as the most accurate classifier, the difference between the three classifiers is not significant (p-value = 0.99).

\input{tex/chap9/2gram}

For the second three classifiers (SVM, linear regression and random forest with 2-grams grouping of stemmed words) the best classifier is once again random forest with 77.34% $F_1$ measure.
It is followed by SVM (76.91%) and, finally, linear regression (76.25%).
As for the first three classifiers, the difference between the classifiers and the ecosystems are not significant.
Moreover, the difference in performances between 1 and 2 grams are not significant either.

\input{tex/chap9/3gram}

Finally, the last three classifiers (SVM, linear regression and random forest with 3-grams grouping of stemmed words) the best classifier is once again random forest with 77.12% F1-measure.
It is followed by SVM (76.72%) and, finally, linear regression (75.89%).
Again, the difference between the classifiers and the ecosystems are not significant.
Neither are the differences in results between 1, 2 and 3 grams.

\input{tex/chap9/random}

Each one of our nine classifiers improves upon the random one on all projects and by a large margin ranging from 20.73% to 22.48% regarding F-Measure.

The last measure of performance for our classifier is the computation of the Cohen's Kappa metric presented in table \ref{tab:kappa}.

\input{tex/chap9/kappa}

The table presents the results of the Cohen's kappa metric for each of our nine classifiers. 
The metric is computed using the observed accuracy and the expected accuracy. 
The observed accuracy, in our bi-class system (i.e. T4 or not), is the number of correctly classified type 4 added to the number of correctly classified non-T4 bugs over the total of reports.
The expected accuracy follows the same principle but using the classification from the random classifier.
The expected accuracy is constant as the random classifier predicts 50% of the reports as T4 and 50% as non-T4.
Finally, the obtained Cohen's Kappa measures range from 0.27 to 0.32.
While there is no unified way to interpret the result of the Cohen's kappa statistic, Landis and Koch considers 0-0.20 as slight, 0.21-0.40 as fair, 0.41-0.60 as moderate, 0.61-0.80 as substantial, and 0.81-1 as almost perfect [@Landis1977]. 
Consequently, all of our classifiers show a fair improvement over a random classification regarding accuracy and a major improvement regarding F1-measure.

### What are the best predictors of type 4 bugs?

In this section, we answer our second research question: *What are the best predictors of type 4 bugs*. 
To do so, we extracted the best predictor of type 4 bugs for each one of the extracted grams (1, 2 and 3) for each of our ten test projects (Five Apache, Five Netbeans).
Then, we manually investigated the source code and the reports of these ten software projects to determine why a given word is a good predictor of type 4 bug.
In the remaining of this section, we present our findings by project and then provide a conclusion on the best predictors of type 4 bugs.

#### Ambari

Ambari is aimed at making Hadoop management simpler by developing software for provisioning, managing, and monitoring Apache Hadoop clusters.
One of the most acclaimed features of Ambari is the ability to visualise clusters' health, according to user-defined metric, with heat maps.
These heat maps give a quick overview of the system.

Figure \ref{fig:ambari-heatmap} shows a screenshot of such a heat map.

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.6]{media/chap9/ambari-heatmap.jpg}
    \caption{Ambari heatmap
    \label{fig:ambari-heatmap}}
\end{figure}

At every tested gram (i.e. 1, 2 and 3) the word "heat map" is a strong predictor of type 4 bugs. 
The heat map feature is a complex feature as it heavily relies on the underlying instrumentation of Hadoop and the consumption of many log format too, for example, extracts the remaining free space on a disk or the current load on a CPU.

Another word that is a strong predictor of type 4 bug is "nagio". 
Nagio is a log monitoring server belonging to the Apache constellation. 
It is used as an optional add-on for Ambari and, as for the heat map, is very susceptible to log format change and API breakage.

Versions of the "nagio" and "heatmap" keywords include: "heatmap displai", "ambari heatmap", "fix nagio", "nagio test", "ambari heatmap displai", "fix nagio test".

#### Cassandra

Cassandra is a database with high scalability and high availability without compromising performance.
While extracting the unique word combinations from the report of Cassandra, one word which is a strong predictor of type 4 bug is "snapshot".

As described in the documentation, in Cassandra terms, *a snapshot first flushes all in-memory writes to disk, then makes a hard link of the SSTable files for each keyspace. You must have enough free disk space on the node to accommodate making snapshots of your data files. A single snapshot requires little disk space. However, snapshots can cause your disk usage to grow more quickly over time because a snapshot prevents old obsolete data files from being deleted. After the snapshot is complete, you can move the backup files to another location if needed, or you can leave them in place.*

The definition gives the reader an insight into how complex this feature used regarding integration with the host system and how coupled it is to the Cassandra, data model.

Other versions of the "snapshot" keyword include "snapshot sequenti", "make snapshot", "snapshot sequenti repair", "make snapshot sequenti". 

#### Flume

Flume is a distributed, reliable, and available service for efficiently collecting, aggregating, and moving large amounts of log data.

One word which is a good predictor of type 4 in flume is "upgrad" and the 2-grams (upgrad flume) and the 3-grams ("upgrad flume to") versions.
Once again for the Apache dataset, a change in the software that induce a change in the underlying data model or data store, which is often the case when you upgrade flume to a new version, is a good indicator of the report complexity and the impact of said report on the sourcecode in terms of number of locations fixed.

On the reports manually analysed, Flume's developers and users have a hard time upgrading to new versions in a sense that logs and dashboard get corrupted or disappear post-upgrade.
Significant efforts are then made to prevent such losses in the subsequent version.

#### HBase 

HBase is a Hadoop database, a distributed, scalable, big data store provided by Apache.
The best predictor of type 4 bug in HBase is "bloom" as in "bloom filters". 
Bloom filters are a  probabilistic data structure that is used to test whether an element is a member of a set [@Broder2004].
Such a feature is hard to implement and hard to test because of its probabilistic nature.
Much feature commits (i.e. commit intended to add a feature) and fix commits (i.e. commit intended to fix a bug) belonging to the HBase source code are related to the bloom filters.
Given the nature of the feature, it is not surprising to find the word "bloom" and its 2-, 3-grams counterparts ("on Bloom", "Bloom filter", "on Bloom filter") as a good predictor of type 4 bug.

#### Hive 

Hive is a data warehouse software facilitates reading, writing, and managing large datasets residing in distributed storage using SQL.
Hive is different from its Apache counterpart as the words that are the best predictors of type 4 bugs do not translate into a particular feature of the product but are directly the name of the incriminated part of the system: thrift. 
Thrift is a software framework, for scalable cross-language services development, combines a software stack with a code generation engine to build services that work efficiently and seamlessly between C++, Java, Python, PHP, Ruby, Erlang, Perl, Haskell, C#, Cocoa, JavaScript, Node.js, Smalltalk, OCaml and Delphi and other languages.
While Thrift is supposed to solve many compatibility issues when building clients for a product such as Hive, it is the cause of many major problems in Hive. 
The top predictors for type 4 bugs in Hive are "thrifthttpcliservic" and "thriftbinarycliservic".

As Hive, and its client, are built on top of Thrift it makes sense that issues propagating from Thrift induce major refactoring and fixed across the whole Hive source code.

#### Cnd 

The CND projects is a part of the Netbeans IDE and provide support for C/C++.
The top two predictors of type 4 bugs are (1) parallelism and (2) observability of c/c++ code.
In each gram, we can find reference to the parallel code being problematic while developed and executed via the Netbeans IDE: "parallel comput", "parallel", "parallel comput advis".
The other word, related to the observability of c/c++ code inside the Netbeans IDE is "Gizmo". "Gizmo" is the codename for the C/C++ Observability Tool built on top of D-Light Toolkit. 
We can find occurrences of "Gizmo" in each gram: "gizmo" and "gizmo monitor" for example.

Once again, a complex cross-concern feature with a high impact on the end-user (i.e., the ability to code, execute and debug parallel code inside Netbeans) is the cause of most of the type 4 bugs and mention of said feature in the report is a bug predictor of types of the bug.

#### Editor 

The Editor component of Netbeans is the component which is handling all the textual edition, regardless of the programming language, in Netbeans.
For this component, the type 4 bugs are most likely related to the "trailing white spaces" and "spellcheck" features.

While these features do not, at first sight, be as complex as, for example, parallelism debugging, they have been the cause of the majority of type 4 bugs.
Upon manual inspection of the related code\footnote{https://netbeans.org/projects/editor/} in the Editor component of Netbeans the complexity of these feature becomes evident.
Indeed, theses features behave differently for almost each type of text-file and textboxes inside Netbeans.
For example, the end-user expects the spellchecking feature of the IDE to kick in while typing a comment inside a code file but not on the code itself. 
A similar example can be described for the identification and removing of trailing white spaces where users wish the trailing white spaces to be deleted in c/c++ code but not, for example, while typing HTML or a commit message.

Each new language supported or add-on supported by the Netbeans IDE and leveraging the features of the Editor component is susceptible to be the cause of a major refactoring to have a coherent behaviour regarding "trailing white spaces" and "spell checking".

#### Java

The Java component of Netbeans is responsible for the Java support of Netbeans in the same fashion as CND is responsible for c/c++ support.
For this particular component, the set of features that are a good predictor of type 4 are the ones related to the Java autocompletion and navigation optimisation.
The autocompletion has to be able to provide suggestions in a near-instantaneous manner if it is to be useful to the developer. 
To provide near-instantaneous suggestion on modest machines and despite the depth of the Java API, Netbeans developers opted of a statistical autocompletion.
The autocompletion *remembers* which of its suggestions you used before and only provide the ones
you are the most likely to want to be based on your previous usage.
Also, each suggestion is companioned with a percentage which describes the number of time you pick a given a suggestion over the other.
One can envision a such a system can be tricky to implement on new API being added in the Java language at each upgrade.
Indeed, when a new API comes to light following a Java upgrade on the developer's machine, then, the autocompletion has to make these new API appears in the autocompletion despite their 0\% chosen rate. The 0\% being linked to the fact that this suggestion was not available thus far and not to the fact that the developer never picked it.
When the new suggestion, related to the new API, has been ignored a given number of time, then, it can be safely removed from the list of suggestions.

Implementation of optimisations related to autocompletion and navigations are the root causes of many type 4 bugs, and we can find them in the gram extracted words that are good predictor: "implement optim", "move otim", "optim import implement", "call hierarchy implement".

#### JavaEE

The JavaEE component of Netbeans is responsible for the support of the JavaEE in Netbeans. 
This module is different from the CND and JAVA module in a sense that it uses and expands many functionalities from the JAVA component.
For the JavaEE component, the best predictor of type 4 bugs is the hibernate and webscoket features which can be found in many gram forms: "hibern revers", "websocket endpoint", "hibern", "websocket", "implement hibern revers", "hibern revers engin".

Hibernate is an ORM that enables developers to write applications whose data outlives the application process more easily. 
As an Object/Relational Mapping (ORM) framework, Hibernate is concerned with data persistence as it applies to relational databases (via JDBC). 

The shortcoming of Netbeans leading to most of the type 4 bugs is related to the annotation based persistence of Hibernate where developers can annotate their class attributes with the name of the column they wish the value of the attribute to be persisted.
While the annotation mechanism is supported by Java, it is not possible *compile* annotation and makes sure that their statically sound.
Consequently, much tooling around annotation has to be developed and maintained accordingly to new databases updates.
Such tooling, for example, is responsible for querying the database model to make sure that the annotated columns exists and can store the attribute data type-wise.

#### Platform

The last netbeans component we analyzed is the one named Platform. 
*The NetBeans Platform is a generic framework for Swing applications. It provides the "plumbing" that, before, every developer had to write themselves—saving state, connecting actions to menu items, toolbar items and keyboard shortcuts; window management, and so on.* (https://netbeans.org/features/platform/)

The best predictor of type 4 bug in the platform component is the "filesystem" word which refers to the ability of any application built atop of Platform to use the filesystem for saves and such.

What we can conclude for this second research question is that the best predictor of type 4 bugs is the mention of a cross-concern, complex, widely used feature in the targeted system.
Reports mentioning said feature are likely to create a type 4 structure with many bugs being fixed in the same set of files.
One noteworthy observation is that the 2- and 3-grams extraction do not add much to the precision about the 1-gram extraction as seen the first research question.
Upon the manual analysis required for this research question, we can deduct why.
Indeed, the problematic features of a given system are identified with a single word (i.e. hibernate, filesystem, spellcheck, ...).
While the 2- and 3-grams classifiers do not provide an additional performance in the classification process, they still become handy when trying to target which part of the feature a good predictor of type 4 ("implement optim", "gizmo monitor", "heatmap displai", ...).

## Threats to Validity

The selection of target systems is one of the common threats to validity for approaches that perform qualitative and quantitative analyses.

While is it possible the selected programs share common properties that we are not aware of and therefore, invalidate our results, this is highly unlikely. Indeed, our dataset is composed of 388 open source systems. 


In addition, we see a threat to validity that stems from the fact that we only used open-source systems. 
The results may not be generalizable to industrial systems. We intend to undertake these studies in future work.


## Chapter Summary

In this chapter, we proposed a taxonomy of bugs and performed an empirical study on two large open source datasets: the Netbeans IDE and the Apache Software Foundation's projects. Our study aimed to analyse: (1) the proportion of each type of bugs; (2) the complexity of each type in terms of severity, reopening and duplication; and (3) the required time to fix a bug depending on its type. The key findings are that Type 4 account for 61% of the bugs and have a bigger impact on software maintenance tasks than the other three types.

In the next chapter, we start a discussion covering several topics ranging from challenges sourrounding the adoption of tools to our advice for university-industry research collaboration.
