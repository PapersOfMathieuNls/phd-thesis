---
abstract:  Software maintenance activities such as debugging and feature enhancement are known to be challenging and costly. Studies have shown that the cost of software maintenance can reach up to 70% of the overall cost of the soft- ware development life cycle (Health, Social and Research 2002). Much of this is attributable to several factors including the increase in software complexity, the lack of traceability between the various artifacts of the software development process, the lack of proper documentation, and the unavailability of the original developers of the systems. The large adoption of these tools by practitioners remains limited, and factors that prevent such adoption are still an open question. In this thesis, we propose to address some of the issues mentioned above by focusing on developing techniques and tools that support software maintainers at commit-time. As part of the developer’s workflow, a commit marks the end of a given task or subtask as the developer is ready to version the source code. Commits are bite-sized units of work that are potentially ready to be shared with the rest of the organization. We propose three approaches named PRECINCT, BIANCA and CLEVER. PRECINCT prevents the insertion of new software clones at commit-time while BIANCA and CLEVER prevent the introduction of new defects at commit-time. BIANCA and CLEVER also propose potential fixes to identified defects in order to support the developer. We also propose JCHARMING that can reproduce on-field crashes in case commit-time approaches failed to prevent the introduction of a defect in the system. Overall, our approaches have been tested on over 400 open- and closed- sources systems with high levels of precision and recall while being scalable and non-intrusive for developers. Finally, we also propose a taxonomy of software fault that can help researchers to categorize the research in the various areas related to software maintenance.
acknowledgments: acknowledgments
---

\chapter{Introduction}

Software maintenance activities such as debugging and feature enhancement are known to be challenging and costly [@Pressman2005]. Studies have shown that the cost of software maintenance can reach up to 70% of the overall cost of the software development life cycle [@HealthSocial2002]. Much of this is attributable to several factors including the increase in software complexity, the lack of traceability between the various artifacts of the software development process, the lack of proper documentation, and the unavailability of the original developers of the systems.

More than three decades of research in different fields such mining software repository, default prevention, clone detection, program comprehension or software maintenance aimed to understand better the needs of and challenges faced by developers when maintaining a program.
Researchers have many approaches and tools to improve the maintenance processes.

The large adoption of these tools by practitioners remains limited [@Lewis2013; @Foss2015; @Layman2007; @Ayewah2007; @Johnson2013].
Factors that prevent such adoption are still an open question.
Nevertheless, based on developers interviews, several hypotheses have been formulated.
For example, developers are known to have trust issues with statistical models based on process or code metrics.
Another hypothesis for the limited adoption of software maintenance approaches is the lack of integration with developers' workflow.
Finally, developers also express concerns regarding the numbers of warnings, the general heaviness of information provided by software maintenance tools and the lack of clear corrective actions to fix a given warning.

In this thesis, we propose to address some of the issues mentioned above by focusing on developing techniques and tools that support software maintainers at commit-time.
As part of the developer's workflow, a commit marks the end of a given task or subtask as the developer is ready to version the source code.
Commits are bite-sized units of work that are potentially ready to be shared with the rest of the organization [@OSullivan2009].

We propose a set of approaches in which we intercept the commits and analyze them with the objective of preventing unwanted modifications to the system. By doing so, we do not only propose solutions that integrate well with the developer's work
flow, but also there is no need for software developers to use any other
external tools.

## Thesis Contributions

In this thesis, we make the following contributions:

- We create a metamodel that allows researchers to manipulate millions of bug reports and fixes (Chapter 4).

In this work, we introduce BUMPER (BUg Metarepository for dEvelopers and Researchers), a web-based infrastructure that can be used by software developers and researchers to access data from diverse repositories using natural language queries transparently, regardless of where the data was created and hosted [@Nayrolles2016b].
The idea behind BUMPER is that it can connect to any bug tracking and version control systems and download the data into a single database.
We created a common schema that represents data, stored in various bug tracking and version control systems.
BUMPER uses a web-based interface to allow users to search the aggregated database by expressing queries through a single point of access.
This way, users can focus on the analysis itself and not on the way the data is represented or located.
BUMPER supports many features including: (1) the ability to use multiple bug tracking and control version systems, (2) the ability to search very efficiently large data repositories using both natural language and a specialized query language, (3) the mapping between the bug reports and the fixes, and (4) the ability to export the search results in JSON, CSV and XML formats.

Importantly, BUMPER differs from other approaches such as Boa [@Dyer2013] because (a) it updates itself every day with the new closed reports, (b) it proposes a clear and concise JSON API that anyone can use to support their approaches or tools.

- We study online and incremental clone detection at commit-time that build upon existing offline techniques (Chapter 5).

Code clones appear when developers reuse code with little to no modification to the original code. Studies have shown that clones can account for about 7% to 50% of the code in a given software system [@Baker; @StephaneDucasse].
Nevertheless, clones are considered a bad practice in software development since they can introduce new bugs in code [@Juergens2009].
If a bug is discovered in one segment of the code that has been copied and pasted several times, then the developers will have to remember the places where this segment has been reused to fix the bug in each place.

In this research, we present PRECINCT (PREventing Clones INsertion at Commit-Time) that focuses on preventing the insertion of clones at commit time, i.e., before they reach the central code repository.
PRECINCT is an online clone detection technique that relies on the use of pre-commit hooks capabilities of modern source code version control systems.

A pre-commit hook is a process that one can implement to receive the latest modification to the source code done by a given developer just before the code reaches the central repository.
PRECINCT intercepts this modification and analyses its content to see whether a suspicious clone has been introduced or not.
A flag is raised if a code fragment is suspected to be a clone of an existing code segment. In fact, PRECINCT, itself, can be seen as a pre-commit hook that detects clones that might have been inserted in the latest changes with regard to the rest of the source code.

- We use incremental clone detection to prevent bug insertion and provide possible fixes (Chapter 6).

Similar to clone detection, we propose an approach for preventing the introduction of bugs at commit-time.
Many tools exist to prevent a developer to ship *bad* code [@Hovemeyer2007].

Our approach, called BIANCA (Bug Insertion ANticipation by Clone Analysis at commit-time), is different than the approaches presented in the literature because it mines and analyses the change patterns in commits and matches them against past commits known to have introduced a defect in the code (or that have just been replaced by better implementation).

- We address scalability and efficiency issues of clone-based bug detection and resolution approaches by combining them with metric based approaches (Chapter 7).

Clone-based bug detection approaches suffer from scalability issues as Type 2, and Type 3 clone-comparison are expensive computation wise. In industrial settings, where several repositories receive hundreds of commits per day they are not applicable.

We created a two-step classifier that leverage the performances of metric based detection and the expressiveness of clone-base detection and resolution called CLEVER [@Nayrolles2018].

This work has been conducted in partnership with Ubisoft, one of the largest video games company in the world.

- We present an approach for crash reproductions in a controlled environment that can be used if the aforementioned approaches aiming to avoid defect introduction failed (Chapter 8).

When preventing measures have failed, and bugs have to be fixed; the first step is to reproduce what happened on sites.
Crash reproduction is an expensive task because the
data provided by end users is often scarce [@Chen2013].
It is, therefore, important to invest in techniques and tools for automatic bug reproduction to ease the maintenance process and accelerate the rate of bug fixes and patches.
Existing techniques can be divided into two categories: (a) On-field record and in-house replay [@Roehm2015], and (b) In-house crash explanation [@Zuddas2014].

We propose an approach, called JCHARMING (Java CrasH Automatic Reproduction by directed Model checkING) that uses a combination of crash traces and model checking to reproduce bugs that caused field failures automatically [@Nayrolles2015; @Nayrolles2016d].
Unlike existing techniques, JCHARMING does not require instrumentation of the code.
It does not need access to the content of the heap either.
Instead, JCHARMING uses a list of functions output when an uncaught exception in Java occurs (i.e., the
crash trace) to guide a model checking engine to uncover the statements that caused the crash.
Such outputs are often found in bug reports.

- We propose a taxonomy of bugs based on the location of their fixes based on a two software ecosystems (Chapter 9).

In recent years, there has been an increase in attention in techniques and tools that mine large bug repositories to help software developers understand the causes of bugs and speed up the fixing process. These techniques, however, treat all bugs in the same way. Bugs that are fixed by changing a single location in the code are examined the same way as those that require complex changes.
After examining more than 100 thousand bug reports of 380 projects, we found that bugs can be classified into four types based on the location of their fixes.
Type 1 bugs are the ones that fixed by modifying a single location in the code, while Type 2 refers to bugs that are fixed in more than one location.
Type 3 refers to multiple bugs that are fixed in the exact same location.
Type 4 is an extension of Type 3, where multiple bugs are resolved by modifying the same set of locations. This classification can help companies put the resources where they are needed the most. It also provides useful insight into the quality of the code. Knowing, for example, that a system contains a large number of bugs of Type 4 suggests high maintenance efforts.
This classification can also be used for other tasks such as predicting the type of incoming bugs for an improved bug handling process. For example, if a bug is found to be of Type 4, then it should be directed to experienced developers.

## Thesis Organization

This thesis organization is as follows; in Chapter 2, we provide background information about the version control systems and project tracking systems.
In Chapter 3, we present the works related to ours. The works related to clone detection, fault-detection, fault-fixing and crash reproduction is extensive as researchers have been focusing on these areas for more than 20 years.
We focus, however, on the works that are the most related to ours.
Chapters 4, 5, 6, 7, 8 and, 9 are dedicated to the main contributions of this thesis we mentioned in the previous section.
Finally, Chapter 10 and 11 present a discussion that reflects on the entirety of our works and a conclusion accompanied by avenues for future works, respectively.

## Related Publications

Earlier versions of the work done in this thesis have been published in the following papers:

1. Abdelwahab Hamou-Lhadj, **Mathieu Nayrolles**: A Project on Software Defect Prevention at Commit-Time: A Success Story of University-Industry Research Collaboration. Accepted to SER&IP 2018 (Co-located with ICSE'18).

2. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj: CLEVER: Combining Code Metrics with Clone Detection for Just-In-Time Fault Prevention and Resolution in Large Industrial Projects. Accepted to MSR 2018 (Co-located with ICSE'18).

3. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj: Towards a Classification of Bugs to Facilitate Software Maintainibility Tasks. Accepted to SQUADE 2018 (Co-located with ICSE'18).

4. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj, Sofiène Tahar, Alf Larsson: A bug reproduction approach based on directed model checking and crash traces. Journal of Software: Evolution and Process 29(3) (2017).

5. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj: BUMPER: A Tool for Coping with Natural Language Searches of Millions of Bugs and Fixes. SANER 2016: 649-652.

6. **Mathieu Nayrolles**, Abdelwahab Hamou-Lhadj, Sofiène Tahar, Alf Larsson: JCHARMING: A bug reproduction approach using crash traces and directed model checking. SANER 2015: 101-110. **Best Paper Award**.

The following papers were published in parallel to the aforementioned publications.
While they are not directly related to this thesis, at the same time, they are not completely irrelevant, as their topics include crash report handling and quality oriented refactoring of service based applications.

7. Abdou Maiga, Abdelwahab Hamou-Lhadj, **Mathieu Nayrolles**, Korosh Koochekian Sabor, Alf Larsson: An empirical study on the handling of crash reports in a large software company: An experience report. ICSME 2015: 342-351.

8. **Mathieu Nayrolles**, Eric Beaudry, Naouel Moha, Petko Valtchev, Abdelwahab Hamou-Lhadj: Towards Quality-Driven SOA Systems Refactoring Through Planning. MCETECH 2015: 269-284.

In addition, we seized the opportunity to disseminate the best practices discovered from our extensive investigation of software ecosystems in several books aimed at practitioners. Appendices of this thesis list the open-source systems that have been studied for our works.

9. **Mathieu Nayrolles**, Rajesh Gunasundaram, Sridhar Rao (2017). Expert Angular. (pp. 454). Packt Publishing.

10. **Mathieu Nayrolles** (2015). Magento Site Performance Optimization. (pp. 92). Packt Publishing.

11. **Mathieu Nayrolles** (2015). Xamarin Studio for Android Programming: A C# Cookbook. (pp. 298). Packt Publishing.

12. **Mathieu Nayrolles** (2014). Mastering Apache Solr. Inkstall Publishing. (pp. 152). Inkstall Publishing.

13. **Mathieu Nayrolles** (2013). Instant Magento Performance Optimization How-to. (pp. 56). Packt Publishing.

Finally, the work presented in this thesis also attracted media-coverage for its impact at Ubisoft, one of the world largest video game publisher. A google search for "commit+assistant+ubisoft" yields more than 114,000 results at the time of writing. Here is a curated list of the most interesting press articles.

14. Sinclair, B. (2018). Ubisoft’s “Minority Report of programming” - GamesIndustry.
https://www.gamesindustry.biz/articles/2018-02-22-ubisofts-minority-report-of-programming.

15. Maxime Johnson. (2018). Jeux videos : reunir les chercheurs et les créateurs - Techno - L’actualite.
http://lactualite.com/techno/2018/02/23/jeu-video-reunir-les-chercheurs-et-les-createurs/

16. Condliffe, J. (2018). AI can help spot coding mistakes before they happen. - MIT Technology Review.
https://www.technologyreview.
com/the-download/610416/ai-can-help-spot-coding-mistakes-before-they-happen/

17. Matt Kamen. (2018). Ubisoft’s AI in Far Cry 5 and Watch Dogs could change gaming - WIRED UK.http://www.wired.co.uk/article/ubisoft-commit-assist-ai

18. Kenneth Gibson. (2018). STEM SIGHTS: The Concordian who uses AI to fix software bugs - Concordia News. 
http://www.concordia.ca/cunews/main/stories/2018/04/10/stem-sights-concordian-who-makes-bug-free-software.html

19. Ryan Remiorz. (2018). Concordia develops tool with Ubisoft to detect glitches in gaming software - Financial Post.
http://business.financialpost.com/pmn/business-pmn/concordia-develops-tool-with-ubisoft-to-detect-glitches-in-gaming-software

20. The Canadian Press. (2018). Concordia partners with Ubisoft to detect glitches in gaming software - The Globe and Mail.
https://www.theglobeandmail.com/business/technology/article-concordia-partners-with-ubisoft-to-detect-glitches-in-gaming-software/

21. Cyrille Baron. (2018). Commit Assistant, l’IA qui aide les développeurs de jeux - iQ France.
https://iq.intel.fr/commit-assistant-lia-qui-aide-les-developpeurs-de-jeux/?sf184907379=1

In these publications, the work presented in this thesis is referred to as _commit-assistant_ which is the internal implementation of CLEVER (Chapter 7).
