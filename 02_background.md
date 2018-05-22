# Background

## Definitions\label{sec:version-control}

In this thesis, we use the following definitions that are based on  [@Avizienis2004; @Pratt2001; @Burnstein2006; @Radatz1990; @Whittaker2012].

- Software bug: A software bug is an error, flaw, failure, defect or fault in a computer program or system that causes it to violate at least one of its functional or non-functional requirement.
- Error: An error is a mistake, misconception, or misunderstanding on the part of a software developer.
- Fault/defect: A fault (defect) is defined as an abnormal condition or defect at the component, equipment, or subsystem level which may lead to a failure. A fault (defect) is not final (the system still works) and does not prevent a given feature to be accomplished.  A fault (defect) is a deviation (anomaly) of the healthy system that can be caused by an error or external factors (hardware, third parties, etc.).
- Failure: The inability of a software system or component to perform its required functions within specified requirements.
- Crash: The software system encountered a fault (defect) that triggered a fatal failure from which the system could not recover from/overcome. As a result, the system stops.
- Bug report: A bug report describes a behaviour observed in the field and considered abnormal by the reporter. Bug reports are submitted manually to bug report systems (bugzilla/jira). There is no mandatory format to report a bug. Nevertheless, a bug report should have the version of the software system, OS, and platform, steps to reproduce the bug, screen shots, stack trace and anything that could help a developer assess the internal state of the software system.
- Crash report: A crash report is issued as the last thing that a software system does before crashing. Crash reports are usually reported automatically (crash reporting systems are implemented as part of the software). A crash report contains data (that can be proprietary) to help developers understand the causes of the crash (e.g., memory dump,...).

In the remaining of this section, we introduce the two types of software repositories: version control and project tracking system.

## Version control systems\label{sec:version-control}

Version control consists of maintaining the versions of various artifacts such as source code files [@Zeller1997].
This activity is a complex task and cannot be performed manually in real world projects. To this end, there exist several tools that have been created to help practitioners manage the version of their software artifacts.
Each evolution of a software system is considered as a version (also called revision) and each version is linked to the one before through modifications of software artifacts. These modifications consist of updating, adding or deleting software artifacts. They can be referred as `diff`, `patch` or `commit`\footnote{These names are not to be used interchangeably as differences exists.}.
A `diff`, `patch` or `commit` has the following characteristics:

- Number of files: The number of software files that have been modified, added or deleted
- Number of hunks: The number of consecutive code blocks of modified, added or deleted lines in textual files. Hunks are used to determine, in each file, how many different places the developer has modified
- Number of churns:  The number of modified lines. However, the churn value for a line change should be at least two as the line has to be 

Modern version control systems also support branching.
A branch is a derivation in the evolution that contains a duplication of the source code so that both versions can be modified in parallel.
Branches can be reconciled with a merge operation that merges modifications of two or more branches.
This operation is completely automated with the exception of merging conflicts that arise when both branches contain modifications of the same line.
Such conflicts cannot be reconciled automatically and have to be dealt with by the developers.
This allows for greater agility among developers as changes in one branch do not affect the work of the developers that are on other branches.

Branching has been used for more than testing hazardous refactoring or testing framework upgrades.
Task branching is an agile branching strategy where a new branch is created for each task [@MartinFowler2009].
It is common to see a branch named `123_implement_X` where `123` is the `#id` of task `X` given by the project tracking system. Project tracking systems are presented in Section \ref{sec:issue-tracking}.

In modern versioning systems, when maintainers make modifications to the source code,  they have to commit their changes for the modifications to be effective. The commit operation versions the modifications applied to one or many files.

Figure \ref{fig:branching} presents the data structure used to store a commit.
Each commit is represented as a tree.
The root leaf (green) contains the commit, tree and parent hashes as same as the author and the description associated with the commit.
The second leaf (blue) contains the leaf hash and the hashes of the files of the project.

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.25]{media/commit-datastructure.png}
    \caption{Data structure of a commit.
    \label{fig:branching}}
\end{figure}

In this example, we can see that author "Mathieu" has created the file `file1.java` with the message "project init".
Figure \ref{fig:two-commits} represents an external modification.
In this second example, `file1.java` is modified while `file2.java` is created.
The second commit `98ca9` have `34ac2` as a parent.

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.25]{media/branching.png}
    \caption{Data structure of two commits.
    \label{fig:two-commits}}
\end{figure}

Branches point to a commit.
In a task-branching environment, a branch is created via a checkout operation for each task.
Tasks can be to fix the root cause of a crash or bug report or features to implement.
In figure \ref{fig:two-branches}, the `master` branch and the `1_fix_overflow` point on commit `98ca9`.

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.25]{media/2branches.png}
    \caption{Two branches pointing on one commit.
    \label{fig:two-branches}}
\end{figure}

Both branches can evolve separately and be merged when the task branch is ready.
In Figure \ref{fig:merge}, the `master` branch points on `a13ab2` while the `1_fix_overflow` points on `ahj23k`.

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.25]{media/merge.png}
    \caption{Two branches pointing on two commits.
    \label{fig:merge}}
\end{figure}

## Providers\label{sec:revision-provider}

In this proposal, we mainly refer to three version control systems: `Svn`, `Git` and, to a lesser extent, `Mercurial`.
`SVN` is distributed by the Apache Foundation and is a centralized concurrent version system that can handle conflicts in the different versions of different developers. SVN  is widely used in industry.
At the opposite, `Git` is a distributed revision control system --- originally developed by Linus Torvald --- where revisions can be kept locally for a while and then shared with the rest of the team.
Finally, `Mercurial` is also a distributed revision system, but shares many concepts with `SVN`.
It will be easier for people who are used to `SVN` to switch to a distributed revision system if they use `Mercurial`.

\subsection{Project Tracking Systems\label{sec:issue-tracking}}

Project tracking systems allow end users to create bug reports (BRs) to report unexpected system behaviour,
managers can create tasks to drive the evolution forward and crash report (CRs) can be automatically created.
These systems are also used by development teams to keep track of the modifications induced by bugs, crash reports, and keep track of the fixes.

\begin{figure}[h!]
	\centering
	\includegraphics[scale=0.7]{media/bzLifecycle.png}
	\caption{Lifecyle of a report}
	\label{fig:bug-lifecyle}
\end{figure}

Figure \ref{fig:bug-lifecyle} presents the life cycle of a report [@Bugzilla2008].
When an end-user submits a report, it is set to the `UNCONFIRMED` state until it receives enough votes or that a user with the proper permissions modifies its status to `NEW`.
The report is then assigned to a developer to be fixed.
When the report is in the `ASSIGNED` state, the assigned developer(s) starts working on the report.
A fixed report moves to the `RESOLVED` state. Developers have five different possibilities to resolve a report: `FIXED`, `DUPLICATE`, `WONTFIX`, `WORKSFORME` and `INVALID` [@Koponen2006].

- RESOLVED/FIXED: A modification to the source code has been pushed, i.e., a changeset (also called a patch) has been committed to the source code management system and fixes the root problem described in the report.
- RESOLVED/DUPLICATE: A previously submitted report is being processed. The report is marked as a duplicate of the original report.
- RESOLVED/WONTFIX: This is applied in the case where developers decide that a given report will not be fixed.
- RESOLVED/WORKSFORME: If the root problem described in the report cannot be reproduced on the reported OS/hardware.
- RESOLVED/INVALID: If the report is not related to the software itself.

Finally, the report is `CLOSED` after it is resolved.
A report can be reopened (sent to the `REOPENED` state) and then assigned again if the initial fix was not adequate (the fix did not resolve the problem).
The elapsed time between the report marked as the new one and the resolved status is known as the _fixing time_, usually in days.
In case of task branching, the branch associated with the report is marked as ready to be merged.
Then, the person in charge (quality assurance team, manager, ect...) will be able to merge the branch with the mainline.
If the report is reopened: the days between the time the report is reopened and the time it is marked again as `RESOLVED/FIXED` are cumulated.
Reports can be reopened many times.

Tasks follow a similar life cycle with the exception of the `UNCONFIRMED` and `RESOLVED` states.
Tasks are created by management and do not need to be confirmed to be `OPEN` and `ASSIGNED` to developers.
When a task is complete, it will not go to the `RESOLVED` state, but to the `IMPLEMENTED` state.
Bug and crash reports are considered as problems to eradicate in the program.
Tasks are considered as new features or amelioration to include in the program.

Reports and tasks can have a severity associated with them [@Bettenburg2008].
The severity indicates the degree of impact on the software system.
The possible severities are:

- blocker: blocks development and/or testing work.
- critical: crashes, loss of data, severe memory leak.
- major: major loss of function.
- normal: regular report, some loss of functionality under
 specific circumstances.
- minor: minor loss of function, or other problem where easy workaround is present.
- trivial: cosmetic problems like misspelled words or misaligned text.

The relationship between a report or a task and the actual modification can be hard to establish, and it has been a subject of various research studies (e.g., [@Antoniol2002; @Bachmann2010; @Wu2011]).
The reason is that they are on two different systems: the version control system and the project tracking system.
While it is considered a good practice to link each report with the versioning system by indicating the report `#id` on the modification message, more than half of the reports are not linked to a modification [@Wu2011].

## Providers\label{sec:bug-provider}

We have collected data from four different project tracking systems: `Bugzilla`, `Jira`, `Github` and `Sourceforge`.
`Bugzilla` belongs to the Mozilla foundation and has first been released in 1998.
`Jira`, provided by Altassian, has been released 14 years ago, in 2002.
`Bugzilla` is 100% open source and it is difficult to estimate how many projects use it.
However, we can envision that it owns a great share of the market as major organizations such as Mozilla, Eclipse and the Apache Software Foundation use it.
`Jira`, on the other hand, is a commercial software tool --- with a freemium business model --- and Altassian claims that they have 25,000 customers over the world.

`Github` and `Sourceforge` are different from `Bugzilla` and `Jira` in a sense that they were created as source code revision systems and evolved, later on, to add project tracking capabilities to their software tools.
This common particularity has the advantage to ease the link between bug reports and the source code.
