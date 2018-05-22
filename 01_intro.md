---
abstract:  Software maintenance activities such as debugging and feature enhancement are known to be challenging and costly. Studies have shown that the cost of software maintenance can reach up to 70% of the overall cost of the software development life cycle. Much of this is attributable to several factors including the increase in software complexity, the lack of traceability between the various artifacts of the software development process, the lack of proper documentation, and the unavailability of the original developers of the systems. The large adoption of these tools by practitioners remains limited, and factors that prevent such adoption are still an open question. In this thesis, we propose to address some of the issues mentioned above by focusing on developing techniques and tools that support software maintainers at commit-time. As part of the developer’s workflow, a commit marks the end of a given task or subtask as the developer is ready to version the source code. Commits are bite-sized units of work that are potentially ready to be shared with the rest of the organization. We propose three approaches named PRECINCT, BIANCA and CLEVER. PRECINCT prevents the insertion of new software clones at commit-time while BIANCA and CLEVER prevent the introduction of new defects at commit-time. BIANCA and CLEVER also propose potential fixes to identified defects in order to support the developer. We also propose JCHARMING that can reproduce on-field crashes in case commit-time approaches failed to prevent the introduction of a defect in the system. Overall, our approaches have been tested on over 400 open- and closed- sources systems with high levels of precision and recall while being scalable and non-intrusive for developers. Finally, we also propose a taxonomy of software fault that can help researchers to categorize the research in the various areas related to software maintenance.
acknowledgments: acknowledgments
csl: conf/acm-sig-proceedings.csl
---

\chapter{Introduction}

## Problem and Motivations

Software maintenance activities such as debugging and feature enhancement are known to be challenging and costly [@Pressman2005]. Studies have shown that the cost of software maintenance can reach up to 70% of the overall cost of the software development life cycle [@HealthSocial2002]. This is due to many factors including the increase in software complexity, the lack of traceability between the various artifacts of the software development process, the lack of proper documentation, and the unavailability of the original developers of the systems.

The last decades have seen increased attention in research in various software maintenance fields including mining software repository, default prevention, clone detection, program comprehension, etc. The main goal is to improve the productivity of software maintainers as they undertake maintenance tasks. There exist several tools to help with important software development tasks that can ease the maintenance burden. These include tools for clone detection (e.g., [@Baker; @StephaneDucasse; @Juergens2009]), bug prevention (e.g., [@Hassan2009; @Kamei2013]), and bug reproduction (e.g., [@Roehm2015; @Zuddas2014; @Chen2013a]).  Although these tools have been shown to be useful, they operate in an offline fashion (i.e., after the changes to the systems have been made). Software developers might be reluctant to use these tools as part of the continuous development process, unless they are involved in a major refactoring effort. Johnson et al. [@Johnson2013] showed that one of the main challenges with these tools lies in the fact that they do not integrate well with the workflow of developers.  Lewis et al. and Foss et al. [@Lewis2013; @Foss2015] added that developers tend to be reluctant to install external tools, which typically require extensive settings and a high learning curve. Another issue with existing techniques, especially bug prevention methods, is that they do not provide recommendations to developers on how to fix the detected bugs. They simply return measurements that are often difficult to interpret by developers. Finally, developers also expressed concerns regarding the numbers of warnings, the general heaviness of information provided by software maintenance tools and the lack of clear corrective actions to fix a given warning.

In this thesis, we propose novel approaches to support software developers at commit-time. As part of the developer's workflow, a commit marks the end of a given task or subtask as the developer is ready to version the source code. We show how commits can be used to catch unwanted modifications to the system before these modifications reach the central code repository. By doing so, we do not only propose solutions that integrate well with developers' workflow, but also eliminate the need for software developers to use any other external tools. In addition, shall these approaches failed at preventing defect to be introduced in the central code repository, we propose an approach to reproduce on-field crashes in a controlled environment. It is agreed upon that reproducing a bug is the first step towards fixing it. Finally, we provide a bug classification that can help classify the research.

## Research Contributions

In this thesis, we make the following contributions:

1. A Bug Metarepository for researchers to manipulate millions of bug reports and fixes (Chapter 4):
In this work, we introduce BUMPER (BUg Metarepository for dEvelopers and Researchers), a web-based infrastructure that can be used by software developers and researchers to access data from diverse repositories using natural language queries transparently, regardless of where the data was created and hosted [@Nayrolles2016b].
The idea behind BUMPER is that it can connect to any bug tracking and version control systems and download the data into a single database.
We created a common schema that represents data, stored in various bug tracking and version control systems.
BUMPER uses a web-based interface to allow users to search the aggregated database by expressing queries through a single point of access.
This way, users can focus on the analysis itself and not on the way the data is represented or located.
BUMPER supports many features including: (1) the ability to use multiple bug tracking and control version systems, (2) the ability to search very efficiently large data repositories using both natural language and a specialized query language, (3) the mapping between the bug reports and the fixes, and (4) the ability to export the search results in JSON, CSV and XML formats. In addition, BUMPER differs from other approaches such as Boa [@Dyer2013] because (a) it updates itself every day with the new closed reports, (b) it proposes a clear and concise JSON API that anyone can use to support their approaches or tools.

2. Online and incremental clone detection at commit-time (Chapter 5).
Code clones appear when developers reuse code with little to no modification to the original code. Studies have shown that clones can account for about 7% to 50% of the code in a given software system [@Baker; @StephaneDucasse].
Nevertheless, clones are considered a bad practice in software development since they can introduce new bugs in code [@Juergens2009]. If a bug is discovered in one segment of the code that has been copied and pasted several times, then the developers will have to remember the places where this segment has been reused to fix the bug in each place. 
In this research, we present PRECINCT (PREventing Clones INsertion at Commit-Time) that focuses on preventing the insertion of clones at commit time, i.e., before they reach the central code repository. PRECINCT is an online clone detection technique that relies on the use of pre-commit hooks capabilities of modern source code version control systems. 

3. An approach for preventing bug insertion at commit-time using clone detection and dependency analysis (Chapter 6).
We propose an approach for preventing the introduction of bugs at commit-time.
Many tools exist to prevent a developer from shipping *bad* code [@Hovemeyer2007]. Our approach, called BIANCA (Bug Insertion ANticipation by Clone Analysis at commit-time), is different than existing approaches because it mines and analyses the change patterns in commits and matches them against past commits known to have introduced a defect in the code (or that have just been replaced by better implementation).

4. An approach for preventing bug insertion at commit-time using metrics and code matching (Chapter 7).
Clone-based bug detection approaches suffer from scalability issues, hindering their application in industrial settings, where several repositories receive hundreds of commits per day. We created a two-step classifier that leverages the performances of metric based detection and the expressiveness of clone-based detection and resolution called CLEVER [@Nayrolles2018]. This work has been conducted in partnership with Ubisoft, one of the largest video games company in the world.

5. An approach for crash reproduction using crash traces and model checking (Chapter 8).
Crash reproduction is an expensive task because the data provided by end users is often scarce [@Chen2013].
It is, therefore, important to invest in techniques and tools for automatic bug reproduction to ease the maintenance process and accelerate the rate of bug fixes and patches. We propose an approach, called JCHARMING (Java CrasH Automatic Reproduction by directed Model checkING) that uses a combination of crash traces and model checking to reproduce bugs that caused field failures automatically [@Nayrolles2015; @Nayrolles2016d].
Unlike existing techniques, JCHARMING does not require instrumentation of the code.
It does not need access to the content of the heap either.
Instead, JCHARMING uses a list of functions output when an uncaught exception in Java occurs (i.e., the
crash trace) to guide a model checking engine to uncover the statements that caused the crash.
Such outputs are often found in bug reports.

6. A classification of bugs  based on the location of fixes  (Chapter 9).
In recent years, there has been an increase in attention in techniques and tools that mine large bug repositories to help software developers understand the causes of bugs and speed up the fixing process. These techniques, however, treat all bugs in the same way. Bugs that are fixed by changing a single location in the code are examined the same way as those that require complex changes.
After examining more than 100 thousand bug reports of 380 projects, we found that bugs can be classified into four types based on the location of their fixes.
Type 1 bugs are the ones that fixed by modifying a single location in the code, while Type 2 refers to bugs that are fixed in more than one location.
Type 3 refers to multiple bugs that are fixed in the exact same location.
Type 4 is an extension of Type 3, where multiple bugs are resolved by modifying the same set of locations. This classification can help companies put the resources where they are needed the most. It also provides useful insight into the quality of the code. Knowing, for example, that a system contains a large number of bugs of Type 4 suggests high maintenance efforts.
This classification can also be used for other tasks such as predicting the type of incoming bugs for an improved bug handling process. For example, if a bug is found to be of Type 4, then it should be directed to experienced developers.

## Thesis Organization

The thesis organization is as follows; in Chapter 2, we provide background information about the version control systems and project tracking systems.
In Chapter 3, we present the works related to ours. The works related to clone detection, fault-detection, fault-fixing and crash reproduction is extensive as researchers have been focusing on these areas for more than 20 years.
We focus, however, on the works that are the most related to ours.
Chapters 4, 5, 6, 7, 8 and, 9 are dedicated to the main contributions of this thesis we mentioned in the previous section.
Finally, Chapter 10 presents closing remarks and a conclusion accompanied by avenues for future works, respectively.

## Related Publications

Earlier versions of the work done in this thesis have been published in the following papers:

1. Abdelwahab Hamou-Lhadj, **Mathieu Nayrolles**: A Project on Software Defect Prevention at Commit-Time: A Success Story of University-Industry Research Collaboration. Accepted to the International Workshop on Software Engineering Research and Industrial Practice 2018 (SER&IP 2018, Co-located with the International Conference on Software Engineering 2018).

2. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj: CLEVER: Combining Code Metrics with Clone Detection for Just-In-Time Fault Prevention and Resolution in Large Industrial Projects. Accepted to Mining Software Repositories 2018 (MSR 2018, Co-located with the International Conference on Software Engineering 2018).

3. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj: Towards a Classification of Bugs to Facilitate Software Maintainibility Tasks. Accepted to the International Workshop on Software Qualities and their Dependencies (SQUADE 2018, Co-located with the International Conference on Software Engineering 2018).

4. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj, Sofiène Tahar, Alf Larsson: A bug reproduction approach based on directed model checking and crash traces. Journal of Software: Evolution and Process 29(3) (2017).

5. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj: BUMPER: A Tool for Coping with Natural Language Searches of Millions of Bugs and Fixes. International Conference on Software Analysis, Evolution and Reengineering 2016: 649-652.

6. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj, Sofiène Tahar, Alf Larsson: JCHARMING: A bug reproduction approach using crash traces and directed model checking. International Conference on Software Analysis, Evolution and Reengineering 2015: 101-110. **Best Paper Award**.

The following papers were published in parallel to the aforementioned publications.
While they are not directly related to this thesis, at the same time, they are not completely irrelevant, as their topics include crash report handling and quality oriented refactoring of service based applications.

7. Abdou Maiga, Abdelwahab Hamou-Lhadj, **Mathieu Nayrolles**, Korosh Koochekian Sabor, Alf Larsson: An empirical study on the handling of crash reports in a large software company: An experience report. International Conference on Software Maintenance and Evolution 2015: 342-351.

8. **Mathieu Nayrolles**, Eric Beaudry, Naouel Moha, Petko Valtchev, Abdelwahab Hamou-Lhadj: Towards Quality-Driven SOA Systems Refactoring Through Planning. International Multidisciplinary Conference on e-Technologies 2015: 269-284.

In addition, we seized the opportunity to disseminate the best practices discovered from our extensive investigation of software ecosystems in several books aimed at practitioners. Appendices of this thesis list the open-source systems that have been studied for our works.

9. **Mathieu Nayrolles**, Rajesh Gunasundaram, Sridhar Rao (2017). Expert Angular. (pp. 454). Packt Publishing.

10. **Mathieu Nayrolles** (2015). Magento Site Performance Optimization. (pp. 92). Packt Publishing.

11. **Mathieu Nayrolles** (2015). Xamarin Studio for Android Programming: A C# Cookbook. (pp. 298). Packt Publishing.

12. **Mathieu Nayrolles** (2014). Mastering Apache Solr. Inkstall Publishing. (pp. 152). Inkstall Publishing.

13. **Mathieu Nayrolles** (2013). Instant Magento Performance Optimization How-to. (pp. 56). Packt Publishing.

Finally, the work presented in this thesis also attracted media-coverage for its impact at Ubisoft, one of the world largest video game publisher. A google search for "commit+assistant+ubisoft" yields more than 114,000 results at the time of writing. Here is a curated list of the most interesting press articles.

14. Sinclair, B. (2018). Ubisoft’s “Minority Report of programming” - GamesIndustry.
\url{https://www.gamesindustry.biz/articles/2018-02-22-ubisofts-minority-report-of-programming}.

15. Maxime Johnson. (2018). Jeux videos : reunir les chercheurs et les créateurs - Techno - L’actualite.
\url{http://lactualite.com/techno/2018/02/23/jeu-video-reunir-les-chercheurs-et-les-createurs/}

16. Condliffe, J. (2018). AI can help spot coding mistakes before they happen. - MIT Technology Review.
\url{https://www.technologyreview.
com/the-download/610416/ai-can-help-spot-coding-mistakes-before-they-happen/}

17. Matt Kamen. (2018). Ubisoft’s AI in Far Cry 5 and Watch Dogs could change gaming - WIRED UK.\url{http://www.wired.co.uk/article/ubisoft-commit-assist-ai}

18. Kenneth Gibson. (2018). STEM SIGHTS: The Concordian who uses AI to fix software bugs - Concordia News. 
\url{http://www.concordia.ca/cunews/main/stories/2018/04/10/stem-sights-concordian-who-makes-bug-free-software.html}

19. Ryan Remiorz. (2018). Concordia develops tool with Ubisoft to detect glitches in gaming software - Financial Post.
\url{http://business.financialpost.com/pmn/business-pmn/concordia-develops-tool-with-ubisoft-to-detect-glitches-in-gaming-software}

20. The Canadian Press. (2018). Concordia partners with Ubisoft to detect glitches in gaming software - The Globe and Mail.
\url{https://www.theglobeandmail.com/business/technology/article-concordia-partners-with-ubisoft-to-detect-glitches-in-gaming-software/}

21. Cyrille Baron. (2018). Commit Assistant, l’IA qui aide les développeurs de jeux - iQ France.
\url{https://iq.intel.fr/commit-assistant-lia-qui-aide-les-developpeurs-de-jeux/?sf184907379=1}

In these publications, the work presented in this thesis is referred to as _commit-assistant_ which is the internal implementation of CLEVER (Chapter 7).
