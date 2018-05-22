
# Conclusion and Future Work

In this chapter, we present the conclusion of this thesis and future work that remain to be done.

Software maintenance activities such as debugging and feature enhancement are known to be challenging and costly [@Pressman2005]. Studies have shown that the cost of software maintenance can reach up to 70% of the overall cost of the software development life cycle [@HealthSocial2002]. Much of this is attributable to several factors including the increase in software complexity, the lack of traceability between the various artifacts of the software development process, the lack of proper documentation, and the unavailability of the original developers of the systems.

In this thesis, we presented three approaches that perform software maintenance at commit-time (PRECINCT, BIANCA and, CLEVER). We also presented JCHARMING that can reproduce on field crash when commit-time approaches did not catch the defect before its release. Finally, we also propose a taxonomy of bugs that could be used by researchers to categorize the research in many areas related to software maintenance. 

## Summary of the Findings

- We created BUMPER, an aggregated, searchable bug-fix repository that contains 1,930 projects, 732,406 resolved/fixed, 1,645,252 changesets from Eclipse, Gnome, Netbeans and the Apache Software foundation.

- We proposed PRECINCT, an incremental, online clone-detection approach that operates at commit-time. It was able to achieve an average 97.7% precision and 100% recall while requiring a fraction of the time thanks to its incremental approach.

- We presented BIANCA, that detects risky commits and proposes potential fixes using clone-detection and dependency clustering. It was able to detect risky-commit with an average of 90.75% precision and 37.15% recall at commit-time. In addition, 78% of the proposed fixes were automatically classified as qualitative using a similarity threshold with the actual fix.

- Out of the 15,316 commits BIANCA classified as _risky_, only 1,320 (8.6%) were because they were matching a defect-commit inside the same project. This supports the idea that within the same project, developers are not likely to introduce the same defect twice. Over similar projects, however, similar bugs are introduced.

- We manually reviewed 250 fixes proposed by BIANCA. We were able to identify the statements from the proposed fixes that can be reused to create fixes similar to the ones that developers had proposed in 84% of the cases.

- While the recall of BIANCA 37.15%, it is important to note that we do not claim that of issues in open-source systems are caused by project dependencies. 

- We built upon BIANCA with CLEVER that combines clone- and metric-based detection of risky commit and proposes potential fixes. It significantly reduced to scalability concerns of BIANCA while obtaining an average of 79.10% precision and a 65.61% recall. 

- 66% of the fixes proposed by CLEVER were accepted by software developer within Ubisoft.

- We introduced JCHARMING, an automatic bug reproduction technique that combines crash traces and directed model checking. When applied to thirty bugs from ten open source systems, JCHARMING was able to successfully reproduce 80% of the bugs. The average time to reproduce a bug was 19 minutes, which is quite reasonable, given the complexity of reproducing bugs that cause field crashes.

- Finally, we proposed a taxonomy of bugs and performed an empirical. The key findings are that Type 4 account for 61% of the bugs and have a bigger impact on software maintenance tasks than the other three types.

## Closing Remarks

In this thesis, we proposed many approaches to help software developers build high quality software systems. We chose to focus on techniques that operate at commit-time. By doing so, this thesis' contributions adhere to the broader concept of just-in-time software quality improvement methods that promote the integration of software maintenance and quality assurance tasks with day-to-day development [@Kamei2013; @Yang2015; @Tourani2016]. Today's software systems are becoming ultra large, composed of many components that interact with each other in a complex way. It is often challenging for software developers to apply existing methods for improving software quality such as clone detection, refactoring after the system in its entirety has been built. This is because the mental models and thought processes that led to these changes or design decisions are long gone and forgotten. Just-in-time software improvement techniques encourage an agile and lean process in which improvements are made as soon as changes happen in the system, while the decisions motivating the changes are still fresh in the mind of practitioners. Agility and lean thinking are not new concepts. They date back to the seventies where Toyota, the car manufacturer, proposed just-in-time manufacturing techniques to produce high quality products that meet customers' needs [@suzaki1987new].

Just-in-time manufacturing relies on several management philosophies and tools: continuous improvement (product-oriented layout of plants, division of systems, simplicity), elimination of waste (overproduction, waiting time, inventory waste, transportation, product defects), Hausukipingu (clean workspaces), Kabans (pulling the right number of items from the right shelves at just the right time), Jidoka (autonomous machines with judgment capabilities) and Andons (signal problems for corrective action) [^jap].

One of the cornerstones of the just-in-time manufacturing philosophy of the seventies and the agile manifesto of the 2000's is continuous improvement [@fowler2001agile]. Other notions cross the gap between just-in-time manufacturing and just-in-time software improvement. For example, integrated development environments (IDEs) are Jidokas in a sense that they auto-complete us and auto-correct us based on past behavior and, unit tests, bug report management systems and quality assurance (QA) bots are Andons.

PRECINCT, BIANCA, and CLEVER are designed to support just-in-time software quality improvement and maintenance techniques by continuously checking developer's code before ending up integrated into a larger software system.

We believe that commit-time software maintenance is the appropriate time to perform just-in-time software maintenance as commits mark the end of a task or sub-task that is ready to be versioned.

JCHARMING adds value by proposing a mechanism for reproducing and fixing the remaining defects. We hope that software engineering researchers and practitioners find this work useful either as a starting point for exciting new research or for building software products that are of good quality.

## Future Work

### Current Limitations

We should acknowledge that the most notable shortcoming of this thesis is the fact that we did not incorporate developers' opinions enough in our studies. Indeed, we only gathered developers opinions' in two separate occasions (BUMPER, Chapter 4 and CLEVER, Chapter 7). While their opinions were positives, we should continue to ask practitioners for their feedbacks on our works.

Another limitation of our work is that most of the approaches (BUMPER, BIANCA and, CLEVER) will most likely be ineffective if applied to a single-system. Indeed, these approaches rely on the large amount of data acquired from multiple software ecosystems to perform.

This leads to the scalability issues of our work. The model required to operate BIANCA took nearly three months using 48 Amazon Virtual Private Servers running in parallel to built and tested. While CLEVER addresses some of the scalability issues with its two-step classifier, the search of potential solution is still computationally expansive.

### Other Possible Opportunities for Future Research

To build on this work, we additional experiment with additional (and larger) systems with the dual aim of fine-tuning the approach, and assessing the scalability of our approach when applied to even larger systems could be conducted. Also, we want to improving PRECINCT to support Type 4 clones will be a significant advantage over other clone-detectors. In addition, conducting user studies with developers to assess the concrete effectiveness of PRECINCT compared to remote and local clone detection techniques.

Conducting human studies with developers in order to gather their feedback on BIANCA and CLEVER would be beneficial. The feedback obtained could help fine-tune the approaches. Also, examining the relationship between project cluster measures (such as betweenness) and the performance of BIANCA. Finally, another improvement to BIANCA would be to support Type 4 clones.

For BIANCA, building a feedback loop between the users and the clusters of known buggy commits and their fixes. If a fix is never used by the end-users, then we could remove it from the clusters and improve our accuracy.

For JCHARMING's, more experiments with more (and complex) bugs with the dual aim to (a) improve and fine tune the approach, and (b) assess the scalability of our approach when applied to even larger (and proprietary) systems. Finally, comparing JCHARMING to STAR [@Chen2013a], which is the closest approach to ours by running both methods on the same set of systems and bugs, could yield interesting results. This comparative study can provide insights into the differences in the use of symbolic analysis as it is in STAR and directed model checking, the core mechanism of JCHARMING.

[^jap]: We kept the Japanese version of most of the words as they are used without translation in the literature.

