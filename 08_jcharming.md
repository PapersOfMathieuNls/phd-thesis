# Bug Reproduction Using Crash Traces and Directed Model Checking

## Introduction

In previous chapters, we presented three approaches (PRECINCT, BIANCA and, CLEVER) that aim to improve code quality by performing maintenance operations at commit-time. While these approaches have shown good performances, they never will be 100% accurate in preventing defects from reaching the customers and on-field crashes will happen.

The first (and perhaps main) step in understanding the cause of a field crash is to reproduce the bug that caused the system to fail. A survey conducted with developers of major open source software systems such as Apache, Mozilla and Eclipse revealed that
one of the most valuable piece of information that can help locate and fix the cause of a crash is the one that can help reproduce it [@Bettenburg2008].

Bug reproduction is, however, a challenging task because of the limited amount of information provided by the end users.
There exist several bug reproduction techniques. They can be grouped into two categories: (a)
On-field record and in-house replay [@Narayanasamy2005; @Artzi2008; @Jaygarl], and (b) In-house crash explanation [@Manevich2004; @chandra2009snugglebug]. The first category relies on instrumenting the system in order to capture objects and other system components at run-time. When a faulty behavior occurs in the field, the stored objects, as well as the entire heap,  are sent to the developers along with the faulty methods to reproduce the crash. These techniques tend to be simple to implement and yield good results, but they suffer from two main limitations. First, code instrumentation comes with a  non-negligible overhead on the system. The second limitation is that the collected objects may contain sensitive information causing customer privacy issues. The second category is composed of tools leveraging proprietary data in order to provide hints on potential causes. While these techniques are efficient in improving our comprehension of the bugs, they are  not designed with the purpose of reproducing them.

JCHARMING (Java CrasH Automatic Reproduction by directed Model checkING) [@Nayrolles2015] is a hybrid approach that uses a combination of crash traces and model checking to reproduce bugs that caused field failures automatically. Unlike existing techniques, JCHARMING does not require instrumentation of the code. It does not need access to the content of the heap either. Instead, JCHARMING uses the list of functions, i.e., the crash trace, that are output when an uncaught exception in Java occurs to guide a model checking engine to uncover the statements that caused the crash. Note that a crash trace is sometimes referred to as a stack trace. In this chapter, we use these two terms interchangeably.

Model checking (also known as property checking) is a formal technique for automatically verifying a set of properties of finite-state systems [@Baier2008]. More specifically, this technique builds a graph where each node represents one state of the program and the set of properties that need to be verified in each state. For real-world programs, model checking is often computationally impracticable because of the state explosion problem [@Baier2008]. To address this challenge and apply model checking on large programs, we direct the model checking engine towards the crash using program slicing and the content of the crash trace, and hence, reduce the search space. When applied to reproducing bugs of seven open source systems, JCHARMING achieved  85% accuracy.

The accuracy of JCHARMING, when applied to 30 bugs, is 80%. 

## Preliminaries\label{sec:prelimenaries}

Model checking (also known as property checking) will, given a formally defined system (that could be software [@Visser2003] or hardware based [@kropf1999introduction]), check if the system meets a specification by testing exhaustively all the states of the system under test (SUT), which can be represented by a Kripke [@Kripke1963] structure:

\begin{equation}
SUT = <S, S_0, T, L>
\end{equation}

where $S$ is a set of states, $S_0$ the set of initial states, $T$ the transitions relations between states and $L$ the labeling function, which labels a state with a set of atomic properties.
Figure \ref{fig:mc-def} presents a system with four steps $S_0$ to $S_3$, which have five atomic properties $p_1$ to $p_5$.
The labeling function $L$ gives us the properties that are true in a state: $L(S_0) = {p_1}, L(S_1) = {p_1, p_2}, L(S_2) = {p_3}, L(S_3) = {p_4, p_5}$.

\begin{figure}[h!]
  \centering
    \includegraphics[scale=0.4]{media/chap8/mc-def.png}
    \caption{System with four steps $S_0$ to $S_3$, which have five atomic properties $p_1$ to $p_5$
    \label{fig:mc-def}}
\end{figure}


The SUT is said to satisfy a property $p$ at a given time if there exists a sequence of states $x$ leading to a state where $p$ holds.
This can be written as:

\begin{equation}
(SUT, x) \models p
\end{equation}

For the SUT of Figure \ref{fig:mc-def}, we can write $(SUT, {S_0, S_1, S_2})$ $\models$ $p_3$ because the sequence of states ${S_0, S_1, S_2}$ will lead to a state $S_2$ where $p_3$ holds.
However,

\begin{equation}
(SUT, S_0, S_1, S_2) \models p_3
\end{equation}

only ensures that $\exists x$ such that $p$ is reached at some point in the execution of the program and not that $p_3$ holds for $\forall x$.

In JCHARMING, we assume that SUTs must not crash in a typical environment. In the framework of this study, we consider a typical environment as any environment where the transitions between the states represent the functionalities offered by the program. For example, in a typical environment, the program heap or other memory spaces cannot be modified. Without this constraint, all programs could be tagged as buggy since we could, for example, destroy objects in memory while the program continues its execution. As we are interested in verifying the absence of unhandled exceptions in the SUT, we aim to verify that for all possible combinations of states and transitions there is no path leading towards a crash. Note that we might find other potential crashes while analyzing the SUT. We only report our findings, however, when we found a crash that matches the one we are trying to reproduce. That is:

\begin{equation}
\forall x.(SUT, x) \models \neg c
\end{equation}

If there exists a contradicting path (i.e., $\exists x$ such that $(SUT, x) \models c$) then the model checker engine will output the path $x$ (known as the counter-example), which can then be executed. The resulting Java exception crash trace is compared with the original crash trace to assess if the bug is reproduced. While being accurate and exhaustive in finding counter-examples, model checking suffers from the state explosion problem, which hinders its applicability to large software systems.

To show the contrast between testing and model checking, we use the hypothetical example of Figures \ref{fig:testing-toy}, \ref{fig:checking-toy} and \ref{fig:dchecking-toy} and sketch the possible results of each approach. These figures depict a toy program where from the entry point, unknown calls are made (dotted points) and, at some points, two methods are called. These methods, called \texttt{Foo.Bar} and \texttt{Bar.Foo}, implement a \texttt{for loop} from 0 to \texttt{loopCount}. The only difference between these two methods is that the \texttt{Bar.Foo} method throws an exception if $i$ becomes larger than two. Hereafter, we denote this property as $p_{i > 2}$.

\begin{figure}
  \centering
    \includegraphics[scale=0.8]{media/chap8/test.png}
    \caption{A toy program under testing
    \label{fig:testing-toy}}
\end{figure}

\begin{figure}
  \centering
    \includegraphics[scale=0.8]{media/chap8/mc.png}
    \caption{A toy program under model checking
    \label{fig:checking-toy}}
\end{figure}

\begin{figure}
  \centering
    \includegraphics[scale=0.8]{media/chap8/dmc.png}
    \caption{A toy program under directed model checking
    \label{fig:dchecking-toy}}
\end{figure}


Figure \ref{fig:testing-toy} shows the program statements that could be covered using testing approaches. Testing software is a demanding task where a set of techniques is used to test the SUT according to some input. Software testing depends on how well the tester understands the SUT in order to write relevant test cases that are likely to find errors in the program. Program testing is usually insufficient because it is not exhaustive. In our case, using testing will mean that the tester knows what to look for in order to detect the causes of the failure. We do not assume this knowledge in JCHARMING.

Model checking, on the other hand, explores each and every state of the program (Figure \ref{fig:checking-toy}), which makes it complete, but impractical for real-world and large systems. To overcome the state explosion problem of model checking, directed (or guided) model checking has been introduced [@Edelkamp2004;@Edelkamp2009]. Directed model checking uses insights --- generally heuristics --- about the SUT in order to reduce the number of states that need to be examined. Figure \ref{fig:dchecking-toy} explores only the states that may lead to a specific location, in our case, the location of the fault. The challenge, however, is to design techniques that can guide the model checking engine. As we will describe in the next section, we use crash traces and program slicing to overcome this challenge.

Unlike model checking, directed model checking is not complete. In this work, our objective is not to ensure absolute correctness of the program, but to use directed model checking to "hunt" for a bug within the program.

## Approach\label{sec:jcharming}

Figure \ref{fig:jcarming-approach} shows an overview of JCHARMING. The first step consists of collecting crash traces, which contain raw lines displayed to the standard output when an uncaught exception in Java occurs. In the second step, the crash traces are preprocessed by removing noise (mainly calls to Java standard library methods). The next step is to apply backward slicing using static analysis to expand the information contained in the crash trace while reducing the search space. The resulting slice along with the crash trace are given as input to the model checking engine. The model checker executes statements along the paths from the main function to the first line of the crash trace (i.e., the last method executed at crash time, also called the crash location point). Once the model checker finds inconsistencies in the program leading to a crash, we take the crash stack generated by the model checker and compare it to the original crash trace (after preprocessing). The last step is to build a JUnit test, to be used by software engineers to reproduce the bug in a deterministic way.

\begin{figure}
  \centering
    \includegraphics[scale=0.9]{media/chap8/jcharming-approach.png}
    \caption{Overview of JCHARMING.
    \label{fig:jcarming-approach}}
\end{figure}

### Collecting Crash Traces

The first step of JCHARMING is to collect the crash trace caused by an uncaught exception. Crash traces are usually included in crash reports and can, therefore, be automatically retrieved using a simple regular expression. Figure \ref{fig:jcarming-traces} shows an example of a crash trace that contains the exception thrown when executing the program depicted in Figures \ref{fig:testing-toy} to \ref{fig:dchecking-toy}. More formally, we define a Java crash trace $T$ of size $N$ as an ordered sequence of frames $T={f_0, f_1, f_2, ..., f_N}$. A frame represents either a method call (with the location of the called method in the source code), an exception, or a wrapped exception. In Figure \ref{fig:jcarming-traces}, the frame $f_0$ represents an exception, the frame  $f_7$ represents a method call to the method \texttt{jsep.Foo.buggy}, declared in file \texttt{Foo.java} at  line 17. In this example, this is the method that caused the exception. $F_6$ represents a wrapped exception.

It is common in Java to have crash traces that contain wrapped exceptions. Such crash traces are incomplete in the sense that they do not show all the method calls that are invoked from the entry point of the program to the crash point. According to the Java documentation [@Oracle2011], line 8 of Figure \ref{fig:jcarming-traces} should be interpreted as follows: _"This line indicates that the remainder of the stack trace for this exception matches the indicated number of frames from the bottom of the stack trace of the exception that was caused by this exception (the "enclosing exception"). This shorthand can greatly reduce the length of the output in the common case where a wrapped exception is thrown from the same method as the "causative exception" is caught."_

We are likely to find shortened traces in bug repositories as they are what the user sees without any possibility to expand their content.

\begin{figure}
  \noindent\fbox{%
      \parbox{\textwidth}{%
  1.javax.activity.InvalidActivityException:loopTimes \\
  should be $<$ 3 \\
  2. at Foo.bar(Foo.java:10) \\
  3. at GUI.buttonActionPerformed(GUI.java:88) \\
  4. at GUI.access\$0(GUI.java:85) \\
  5. at GUI\$1.actionPerformed(GUI.java:57) \\
  6. caused by java.lang.IndexOutOfBoundsException : 3 \\
  7. at jsep.Foo.buggy(Foo.java:17) \\
  8. and 4 more ...
      }%
  }
    \caption{Java InvalidActivityException is thrown in the Bar.Foo loop if the control variable is greater than~2.
    \label{fig:jcarming-traces}}
\end{figure}


In more details, Figure \ref{fig:jcarming-traces} contains a call to the $Bar.foo()$ method -- the crash location point -- and calls to Java standard library functions (in this case, GUI methods because the program was launched using a GUI). As shown in Figure \ref{fig:jcarming-traces}, we can see that the first line (referred to as frame _$f_0$_, subsequently the next line is called frame _$f_1$_, etc.) does not represent the real crash point but it is only the last exception of a chain of exceptions. Indeed, the $InvalidActivity$ has been triggered by an $IndexOutOfBoundsException$ in $jsep.Foo.buggy$. This crash trace shows also an example of nested try-catch blocks.



### Preprocessing

In the preprocessing step, we first reconstruct and reorganize the crash trace in order to address the problem of nested exceptions. Nested exception refers to the following structure in Java.

\noindent\fbox{%
  \parbox{\textwidth}{%

1 java.io.IOException: Spill failed \\
... \\
14 Caused by: java.lang.IllegalArgumentException \\
... \\
28 Caused by: java.lang.NullPointerException \\
	}%
 }

In such a case, we want to reproduce the root exception (line 28) that led to the other two (lines 14 and 1). This said, we remove the lines 1 to 14. Then, with the aim to guide the directed model checking engine optimally, we remove frames that are beyond our control. These refer to Java library methods and third party libraries. In Figure \ref{fig:jcarming-traces}, we can see that Java GUI and event management components appear in the crash trace. We assume that these methods are not the cause of the crash; otherwise, it means that there is something wrong with the JDK itself. If this is the case,  we will not be able to reproduce the crash. Note that removing these unneeded frames will also reduce the search space of the model checker.

### Building the Backward Static Slice

Static analysis of programs consists of analyzing programs without executing them or making assumptions about the inputs of the program.
There exist many techniques to perform static analysis (data flow analysis, control flow analysis, theorem proving, etc.) that can be used for debugging, program comprehension, and performance analysis.
Static slice is a type of static analysis, which takes a program and slicing properties as input in order to create a smaller program with respect to the slicing properties. An example of slicing properties could be to extract only the program statements that modify a certain variable.
Static slicing is particularly interesting for JCHARMING as the sliced program, being smaller, will have a smaller state space.
We illustrate the process of static slicing using the example in Figure \ref{fig:slicing}. In this figure, we show an example of an abstract syntax tree, generated from a given a program (not shown in this chapter). The black states could be states that are impacted by given slicing properties.
After the slicing, the final static slice contains the black states and the states that are needed to reach them.

\begin{figure}
  \centering
    \includegraphics[scale=.2]{media/chap8/slicing.png}
    \caption{Hypothetical example of static program slicing\label{fig:slicing}}
\end{figure}

In JCHARMING, we use a particular static slicing known as backward static slicing [@de2001program]. A backward slice contains all possible branches that may lead to a point _n_ in the program from a point _m_ as well as the definition of the variables that control these branches [@de2001program]. In other words, the slice of a program point _n_ is the program subset that may influence the reachability of point _n_ starting from point _m_. The backward slice containing the branches and the definition of the variables leading to _n_ from _m_ is noted as $bslice_{[m \leftarrow n]}$. Figure \ref{fig:backward-slicing} presents an example of a backward static slice.

\begin{figure}
  \centering
    \includegraphics[scale=.2]{media/chap8/backward-slicing.png}
    \caption{Hypothetical example of backward static program slicing\label{fig:backward-slicing}}
\end{figure}


In JCHARMING, we compute a backward static slice between the location of the crash (which we have in the crash trace) and the entry point of the program (i.e., the main function). However, for large systems, a crash trace does not necessarily contain all the methods that have been executed starting from the entry point of the program to the crash location point. We need to complete the content of the crash trace by identifying all the statements that have been executed starting from the main function until the last line of the preprocessed crash trace. In Figure \ref{fig:jcarming-traces}, this will be the function call `Bar.Foo()` which happens to be also the crash location point. To achieve this, we turn to static analyses by extracting a backward slice from the main function of the program to the `Bar.Foo()` method.

\begin{equation}
bslice_{[f_n \leftarrow f_0]} = bslice_{[f_1 \leftarrow f_0]} \cup bslice_{[f_2 \leftarrow f_1]} \cup ... \cup bslice_{[f_n \leftarrow f_{n\mymathhyphen1}]}
\end{equation}

Note that the union of the slices computed between each pair of frames must be a subset of the final slice between $f_0$ and the entry point of the program $f_n$. More formally:

\begin{equation}
\bigcup_{i=0}^{n-1} bslice_{[f_{i+1} \leftarrow f_i]} \subseteq bslice_{[f_n \leftarrow f_0]}
\label{eq:slice-unions}
\end{equation}

Figure \ref{fig:jcharming-slice} presents an example that will help understand Equation \ref{eq:slice-unions}.
In this example, a toy program composed of six methods $a...f$ crashes in $d$ with a crash trace $T = \{e, f, d\}$.
The backward static slice from the crash point $d$ to the entry point $a$ without using $T$ will be $\{a, b, c, d, e, f\}$ as we do not assume to know the path taken to reach $d$.
However, if we use the information in $T$ to compute the backward static slice, then, some methods can be disregarded. The slice directed by $T$ is $\{a, e, f, d\}$, which is a subset of $\{a, b, c, d, e, f\}$. This example illustrates how a backward slice can be generated using the information in the crash trace.

\begin{figure}
\centering
\includegraphics[scale=0.25]{media/chap8/jcharming-slices.png}
\caption{Hypothetical example representing $bslice_{[a \leftarrow d]}$ (left) vs. $\cup_{i=d}^{a} bslice_{[f_{i+1} \leftarrow f_i]}$ (right) for a crash trace $T={d, f, e}$.
\label{fig:jcharming-slice}}
\end{figure}

In the worst case scenario where there exists one and only one transition between each two frames (this is very unlikely for real and complex systems) $bslice_{[entry \leftarrow f_0]}$ and  $\cup_{i=0}^n bslice_{[f_{i+1} \leftarrow f_i]}$ yield the same set of states with a comparable computational cost since the number of branches to explore will be the same in both cases.

Algorithm \ref{alg:jcharming-slice} is a high-level representation of how we compute the backward slice between each frame. The algorithm takes as input the preprocessed crash trace $T=\{f_0, f_1, f_2, ..., f_N\}$, the byte code of the SUT, and the entry point. From line 1 to line 4, we initialize the different variables used by the algorithm. Our algorithm needs to have the crash trace as an array of frames (line 1), the size $N$ of the crash trace (line 2), a \texttt{null} backward static slice (line 4, and an offset set to 1 (line 3). The main loop of the algorithm begins at line 5 and ends at line 13. In this loop, we compute the static slice between the current frame and the next one. If the slice is not empty, then we update the final backward slice with the newly computed slice. In Algorithm 1, this is shown as a union between the two slices. Note that this union preserves the order of the elements of the two slices. This can also be seen as a concatenation operation. We describe this process in the subsequent paragraphs through an example. If the computed slice is empty, it means that Frame `i + 1` was corrupted then we move to Frame `i + 2`, and so on. At the end of the algorithm, we compute the slice between the last frame and the entry point of the program and update the final slice.

Figure \ref{fig:jcharming-algo} presents a step by step graphical representation of Algorithm \ref{alg:jcharming-slice}. In this figure, an hypothetical program, composed of eleven states ($a...k$), crashes at point $k$. The produced crash trace is composed of six frames $T=\{f_0, f_1, f_2, f_3, f_4, f_5\}$. The frames represent $k$, $i$, $h$, $d$, $b$ and $a$, respectively. In the crash trace, $f_3$ is a corrupt frame and no longer matches a location inside the SUT. This can be the result of a copy-paste error or a deliberate modification made by the reporter of the bug as shown in the case study (see Section \ref{sec:results}). In such a situation, Algorithm \ref{alg:jcharming-slice} will begin by computing the backward static slice between $f_0$ ($k$) and $f_1$ ($i$), then between  $f_1$ ($i$) and $f_2$ ($h$). At this point, we passed through the $for$ loop (lines 5 to 12) two times, and in both cases the backward static slice was not empty. Consequently, the $if$ statement was equal to $true$ and we combined both backward static slices in the $bSlice$ variable. $bSlice$ is equal to {$k$, $j$, $i$, $h$}. Then, we want to compute the backward static slice between $f_2$ ($h$) and $f_3$ ($d$). Unfortunately, $f_3$ is corrupted and does not point towards a valid location in the SUT. As a result, the slice between $f_2$ ($h$) and $f_3$ ($d$) will be empty, and we will go to the $else$ statement (line 10). Here, we simply increment $offset$ by one in order to compute the backward static slice from $f_2$ ($h$) and $f_4$ ($b$) instead of $f_2$ ($h$) and $f_3$ ($d$). $f_4$ is valid and the backward static slice from $f_2$ ($h$) and $f_4$ ($b$) can be computed and merged to $bSlice$. Finally, we compute the last slice between $f_4$ ($b$) and $f_5$ ($a$). The final backward static slice is $k$, $i$, $h$, $d$, $b$ and $a$.


\begin{algorithm}
 \KwData{Crash Stack, BCode, Entry Point}
 \KwResult{BSolve}
 Frame[]~frames $\leftarrow$ extract~frames~from~crash~stack; \\
 Int n $\leftarrow$ size of frames\;
 Int offset $\leftarrow$ 1\;
 Bslice bSlice $\leftarrow$ $\emptyset$\;
\For{i $\leftarrow$ 0; (i $<$ n \&\& offset $<$ n - 1); i++}{
  BSlice currentBSlice $\leftarrow$ backward slice from frames[i] to frames[i + offset]\;
  \eIf{currentBSlice $\not=$ $\emptyset$}{
   bSlice $\leftarrow$ bSlice $\cup$ currentBSlice\;
   offset $\leftarrow$ 1\;
  }{
   offset $\leftarrow$ offset +1\;
  }
}
\caption{High-level algorithm computing the union of the slices\label{alg:jcharming-slice}}
\end{algorithm}

\begin{figure}
  \centering
    \includegraphics[scale=.20]{media/chap8/algo.png}
    \caption{Steps of Algorithm \ref{alg:jcharming-slice} where $f_3$ is corrupted.
    \label{fig:jcharming-algo}}
\end{figure}

Our algorithm, given an uncorrupted stack trace, will be able to compute an optimum backward static slice based on the frames (Figure \ref{fig:jcharming-slice}) and compensate for frames corruption ,if need be, by \textit{offsetting} the corrupted frame (Figure \ref{fig:jcharming-algo}).
The compensation of corrupted frames, obviously, comes at the cost of a sub-optimum backward static slice.
In our previous example, the transition between $b$ and $h$ could have been omitted if $f_3$ was not corrupted.

In the rare cases where the final slice is empty (this may happen in situations where the content of the crash trace is seriously corrupted) JCHARMING would simply proceed with non-directed model checking.

Note that while we allow in JCHARMING the possibility to resort to non-directed model checking, none of the 30 bugs used in this study contained crash traces that were damaged enough for this fall back mechanism to be used.

In order to compute the backward slice, we implement our algorithm as an add-on to the T. J. Watson Libraries for Analysis (WALA) [@IBM2006], which provide static analysis capabilities for Java Byte code and JavaScript.  WALA offers a very comprehensive API to perform static backward slicing on Java Byte code from a specific point to another. We did not need to extend WALA to perform our analysis.

Using backward slicing, the search space of the model checker
that processes the example of Figures \ref{fig:testing-toy} to \ref{fig:dchecking-toy}, where a crash happens when $i>2$, is given by the following expression:


\begin{equation}
Sliced_{SUT} =
\begin{pmatrix}
\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]} \subseteq SUT, \\
S_0, \\
T.\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]}  \subseteq T.SUT, \\
L
\end{pmatrix}
\end{equation}

Where $\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]} \subseteq SUT$ is the subset of states that can be reached in the computed backward slice, $S_0$ the set of initial states, $T.\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]}$ the subset of transitions relations between states that exist in the computed backward slice and $L$ the labeling function which labels a state with a set of atomic properties.
Then, in the sliced SUT, we try to find:

\begin{equation}
    (Sliced_{SUT}, x) \models p_{i>2}
\end{equation}

That is, there exists a sequence of state transitions $x$ that satisfies $p_{i>2}$. The only frame that needs to be valid for the backward static slice to be meaningful is $f_0$. In Figure \ref{fig:jcarming-traces}, $f_0$ is $at~Foo.bar(Foo.java:10)$. If this line of the crash trace is corrupt, then JCHARMING cannot perform the slicing because it does not know where to start the backward slicing. The result is a non-directed model checking, which is likely to fail.

### Directed Model Checking

The model checking engine we use in this chapter is called JPF (Java PathFinder) [@Visser2004], which is an extensible JVM for Java byte code verification. This tool was first created as a front-end for the SPIN model checker [@holzmann1997model] in 1999 before being open-sourced in 2005. The JPF model checker can execute all the byte code instructions through a custom JVM --- known as JVM\textsuperscript{\textit{JPF}}. Furthermore, JPF is an explicit state model checker, very much like SPIN [@holzmann1997model]. This is contrasted with a symbolic model checker based on binary decision diagrams [@mcmillan1993symbolic]. JPF designers have opted for a depth-first traversal with backtracking because of its ability to check for temporal liveness properties.

More specifically, JPF's core checks for defects that can be checked without defining any property.
These defects are called \textit{non-functional properties} in JPF and cover deadlock, unhandled exceptions, and \texttt{assert} expressions.
In JCHARMING, we leverage the \textit{non-functional properties} of JPF as we want to compare the crash trace produced by unhandled exceptions to the crash trace of the bug at hand. In other words, we do not need to define any property ourselves.
This said, in JPF, one can define properties by implementing \texttt{listeners} --- very much like what we did in Section~\ref{sec:unit-tests} --- that can monitor all actions taken by JPF, which enables the verification of temporal properties for sequential and concurrent Java programs.
One of the popular \texttt{listeners} of JPF is \texttt{jpf-ltl}.
This listener supports the verification of method invocations or local and global program variables. \texttt{jpf-ltl} can verify temporal properties of method call sequences, linear relations between program variables, and the combination of both.
We intend to investigate the use of \texttt{jpf-ltl} and the LTL logic to check multi-threaded related crashes as part of future work.

JPF is organized around five simple operations: (i) _generate states_, (ii) _forward_, (iii) _backtrack_, (iv) _restore state_ and (v) _check_. In the forward operation, the model checking engine generates the next state $s_{t+1}$. Each state consists of three distinct components:

- The information of each thread. More specifically, a stack of frames corresponding to method calls.
- The static variables of a given class.
- The instance variables of a given object.

If $s_{t+1}$ has successors, then it is saved in a backtrack table to be restored later. The backtrack operation consists of restoring the last state in the backtrack table. The restore operation allows restoring any state. It can also be used to restore the entire program as it was the last time we chose between two branches. After each forward, backtrack and restore state operation the check properties operation is triggered.

In order to direct JPF, we have to modify the _generate states_ and the _forward_ steps. The _generate states_ is populated with the states in $\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]}  \subset SUT$ and we adjust the _forward step_ to explore a state if the target state $s_i+1$ and the transition $x$ to pass from the current state $s_i$ to $s_{i+1}$ are in $\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]}  \subset SUT$ and $x.\bigcup_{i=0}^{entry} bslice_{[f_{i+1} \leftarrow f_i]}  \subset x.SUT$.

In other words, JPF does not generate each possible state for the system under test. Instead, JPF generates and explores only the states that fall inside the backward static slice we computed in the previous step. As shown in figure \ref{fig:jcharming-slice}, our backward static slice can greatly reduce the space search and is able to compensate for corrupted frames. In short, the idea is that instead of going through all the states of the program as a complete model checker would do, the backward slice directs the model checker to explore only the states that might reach the targeted frame.

### Validation

To validate the result of directed model checking, we modify the _check properties_ step that checks if the current sequence of state transition $x$ satisfies a set of properties. If the current state transition $x$ can throw an exception, we execute $x$ and compare the exception thrown to the original crash trace (after preprocessing). If the two exceptions match, we conclude that the conditions needed to trigger the failure have been met and the bug is reproduced.

However, as argued by Kim et al. in [@Kim2013], the same failure can be reached from different paths of the program. Although the states executed to reach the defect are not exactly the same, they might be useful to enhance the understanding of the bug by software developers and speed up the deployment of a fix. Therefore, in this chapter, we consider a defect to be partially reproduced if the crash trace generated from the model checker matches the original crash trace by a factor of $t$, where $t$ is a threshold specified by the user. The threshold, $t$, represents the percentage of identical frames between both crash traces.

For our experiments (see Section \ref{sec:results}), we set the value of $t$ to 80%. The choice of $t$ should be guided by the need to find the best trade-off between the reproducibility of the bug and the relevance of the generated test cases (the tests should help reproduce the on-field crash). To determine the best $t$, we made several incremental attempts, starting  from $t = 10\%$.  For each attempt, we increased $t$ with a factor of 5% and observed the number of bugs reproduced and the quality of the generated tests. Having $t = 80\%$ provided the best trade-off. This said, we anticipate that the tool that implements our technique should allow software engineers to vary $t$ depending on the bugs and the systems under study. Based on our observations, we recommend, however, to set $t$ to 80% as a baseline. It should also be noted that we deliberately prevented JCHARMING to perform directed model checking with a threshold below 50%. This is because the tests generated with such a low threshold during our experiments did not yield qualitative results.


### Generating Test Cases for Bug Reproduction\label{sec:unit-tests}

To help software developers reproduce the crash in a lab environment, we automatically produce the JUnit test cases necessary to run the SUT to cause the bug.

To build a test suite that reproduces a defect, we need to create a set of objects used as arguments for the methods that will enable us to travel from the entry point of the program to the defect location. JPF has the ability to keep track of what happens during model checking in the form of traces containing the visited states and the value of the variables. We leverage this capability to create the required objects and call the methods leading to the failure location.

During the testing of the SUT, JPF emits a trace that is composed of the executed instructions.
For large systems, this trace can contain millions of instructions. It is stored in memory and therefore can be queried while JPF is running.
However, accessing the trace during the JPF execution considerably slows down the checking process as both the querying mechanism and the JPF engine compete with each other for resources.
In order to allow JPF to use 100\% of the available resources and still be able to query the executed instructions, we implemented a listener that listens to the JPF trace emitter.
Each time JPF processes a new instruction, our listener catches it and saves it into a MongoDB database to be queried in a post-mortem fashion. Figure \ref{fig:jcharming-unittest} presents a high-level architecture of the components of the JUnit test generation process.


\begin{figure}
  \centering
    \includegraphics[scale=0.8]{media/chap8/unittest.png}
    \caption{High level architecture of the Junit test case generation
    \label{fig:jcharming-unittest}}
\end{figure}

When the _validate_ step triggers a crash stack with a similarity larger than a factor _t_, the JUnit generation engine queries the MongoDB database and fetches the sequence of instructions that led to the crash of interest. Figure \ref{fig:jcharming-unittest} contains a hypothetical sequence of instructions related to the example of Figures \ref{fig:testing-toy}, \ref{fig:checking-toy}, \ref{fig:dchecking-toy}, which reads : _new jsme.Bar_,  _invokespecial jsme.Bar()_, _astore_1 [bar]_, _aload_1 [bar]_,   _iconst_3_, _invokevirtual jsme.Bar.foo(int)_, _const_0_,   _istore_2 [i]_, _goto_, _iload_2 [i]_, _iconst_2_,   _if_icmple iinc 2 1_, _new java.lang.Exception_. From this sequence we know that, to reproduce to crash of interest, we have to (1) create a new object _jsme.Bar_ (_new jsme.Bar_, _invokespecial jsme.Bar()_), (2) store the newly created object in a variable named _bar_ (_astore_1 [bar]_), (3) invoke the method _jsme.Bar.foo(int)_ of the _bar_ object with _3_ as value (_aload_1 [bar]_, _iconst_3_, _invokevirtual jsme.Bar.foo(int)_). Then, the _jsme.Bar.foo(int)_ method will execute the $for-loop$ from $i=0$ until $i=3$ and throw an exception at _i = 3_ (_const_0_, _istore_2 [i]_, _goto_, _iload_2 [i]_, _iconst_2_, _if_icmple iinc 2 1_, _new java.lang.Exception_).

The generation of the JUnit test itself is based on templates and targets directly the system under test. Templates are excerpts of Java source code with well-defined tags that will be replaced by values. We use templates because the JUnit test cases have common structures. Figure \ref{fig:jcharming-unittemplate} shows our template for generating JUnit test cases. In this figure, each _{% %}_ will be dynamically replaced by the corresponding value when the JUnit test is generated. For example, _{% SUT %}_ will be replaced by _Ant_ if the SUT is Ant. First, we declare four variables that contain the _failure_, the _threshold_ above which a given bug is said to be partially reproduced, the _differences_, which count how many lines differ between the original failure, the failure produced by JCHARMING, and a _StringTokenizer_. The _StringTokenizer_ allows breaking the original _failure_ into tokens. Second, the test method where _{%SUT%}_ is replaced by the name of the SUT and _{%STEPS%}_ by the steps to make the SUT crash. Then, the crash trace related to the crash is received in the _catch_ part of the _try-catch_ block. In the _catch_ part, we compute the number of lines that do not match the original exception and store it into _differences_ \footnote{This code has been replaced by $//Count~the~differences$ to ease the reading.}. Finally, the _assertTrue_ call will assert that the crash traces from the induced and the original crash are at least _threshold_ percent similar to each other.

\begin{figure}[h!]

\noindent\fbox{%
    \parbox{\textwidth}{%
    \lstinputlisting[language=Java]{tex/chap8/test.java}
}%
}
    \caption{Simplified Unit Test template
    \label{fig:jcharming-unittemplate}}
\end{figure}

## Experimental Setup\label{sec:cases}

In this section, we show the effectiveness of JCHARMING to reproduce bugs in ten open source systems. The case studies aim to answer the following question: _Can we use crash traces and directed model checking to reproduce on- field bugs in a reasonable amount of time?_

### Targeted Systems

Table \ref{tab:jacharming-systems} shows the systems and their characteristics in terms of
Kilo Line of Code (KLoC) and Number of Classes (NoC).

\begin{table}
\centering

\caption{List of target systems in terms of Kilo line of code (KLoC), number of classes (NoC) and Bug \# ID}
\begin{tabular}{c|c|c|c}
SUT        & KLOC & NoC  & Bug \#ID                                        \\ \hline \hline
Ant        & 265  & 1,233 & 38622, 41422                                    \\
ArgoUML    & 58   & 1,922 & 2603, 2558, 311, 1786                           \\
dnsjava    & 33   & 182  & 38                                              \\
jfreechart & 310  & 990  & 434, 664, 916                                   \\
Log4j      & 70   & 363  & 11570, 40212, 41186, 45335, 46271, 47912, 47957 \\
MCT        & 203  & 1267 & 440ed48                                         \\
pdfbox     & 201  & 957  & 1,412, 1,359 \\
Hadoop 	   & 308   & 6,337 & 2893, 3093, 11878\\
Mahout 	   & 287  & 1,242 &  486, 1367, 1594, 1635\\
ActiveMQ   & 205  & 3,797 & 1054, 2880, 2880\\ \hline
Total      & 1,517 & 17,348 & 30 \\
\hline \hline
\end{tabular}
\label{tab:jacharming-systems}
\end{table}


Apache Ant [@ApacheSoftwareFoundation] is a popular command-line tool to build Makefiles. While it is mainly known for Java applications, Apache Ant also allows building C and C++ applications. We choose to analyze Apache Ant because other researchers in similar studies have used it.

ArgoUML [@CollabNet] is one of the major players among the open source UML modeling tools. It has many years of bug management and, similar to Apache Ant, it has been extensively used as a test subject in many studies.

Dnsjava [@Wellington2013] is a tool for the implementation of the DNS mechanisms in Java. This tool can be used for queries, zone transfers, and dynamic updates. It is not as large as the other two, but it still makes an interesting case subject because it has been well maintained for the past decade. Also, this tool is used in many other popular tools such as Aspirin, Muffin and Scarab.

JfreeChart [@ObjectRefineryLimited2005] is a well-known library that enables the creation of professional charts. Similar to dnsjava, it has been maintained over a very long period of time. JfreeChart was created in 2005. It is a relatively large application.

Apache Log4j [@TheApacheSoftwareFoundation1999] is a logging library for Java. This is not a very large library, but thousands of programs extensively use it. As other Apache projects, this tool is well maintained by a strong open source community and allows developers to submit bugs. The bugs that are in the bug reporting system of Log4j are generally well documented. In addition, the majority of bugs contain crash traces, which makes Log4j a good candidate system for this study.

MCT [@NASA2009] stands for Mission Control technologies and was developed by the NASA Ames Research Center (the creators of JPF) for use in spaceflight mission operation. This tool benefits from two years of history and targets a very critical domain, Spacial Mission Control. Therefore, this tool has to be particularly and carefully tested and, consequently, the remaining bugs should be hard to discover and reproduce.

PDFBox [@ApacheSoftwareFoundation2014] is another tool supported by the Apache Software Foundation since 2009 and was created in 2008. PDFBox allows the creation of new PDF documents and the manipulation of existing documents.

Hadoop [@hadoop2011hadoop] is a framework for storing and processing large datasets in a distributed environment. It contains four main modules: _Common_, _HDFS_, _YARN_ and _MapReduce_. In this chapter, we study the _Common_ module that contains the different libraries required by the other three modules.

Mahout [@mahout2012scalable] is a relatively new software application, built on top of Hadoop. We used Mahout version 0.11, which was released in August 2015.
Mahout supports various machine learning algorithms with a focus on collaborative filtering, clustering, and classification.

Finally, ActiveMQ [@snyder2011activemq] is an open source messaging server that allows applications written in Java, C, C++, C\#, Ruby, Perl or PHP to exchange messages using various protocols. ActiveMQ has been actively maintained since it became an Apache Software Foundation project in 2005.


\begin{table}[]
\centering
\caption{Effectiveness of JCHARMING using directed model checking (DMC) in minutes, length of the generated JUnit tests (CE length) and model checking (MC) in minutes}
\begin{tabular}{c|c|c|c|c|c}
SUT                         & Bug \#ID & Reprod. & Time DMC & CE length & Time MC \\ \hline \hline
\multirow{2}{*}{Ant}        & 38622    & Yes     & 25.4   & 3  & -       \\
                            & 41422    & No      & 42.3   & -  & -       \\ \hline
\multirow{4}{*}{ArgoUML}    & 2558     & Partial & 10.6   & 3  & -       \\
                            & 2603     & Partial & 9.4    & 3  & -       \\
                            & 311      & Yes     & 11.3   & 10  & -       \\
                            & 1786     & Partial & 9.9    & 6  & -       \\  \hline
DnsJava                     & 38       & Yes     & 4      & 2  & 23      \\ \hline
\multirow{3}{*}{jFreeChart} & 434      & Yes     & 27.3   & 2  & -       \\
                            & 664      & Partial & 31.2   & 3   & -       \\
                            & 916      & Yes     & 26.4   & 4  & -       \\ \hline
\multirow{7}{*}{Log4j}      & 11570    & Yes     & 12.1   & 2  & -       \\
                            & 40212    & Yes     & 15.8   & 3  & -       \\
                            & 41186    & Partial & 16.7   & 9  & -       \\
                            & 45335    & No      & 3.2    & -  & -       \\
                            & 46271    & Yes     & 13.9   & 4  & -       \\
                            & 47912    & Yes     & 12.3   & 3  & -       \\
                            & 47957    & No      & 2      & -  & -       \\ \hline
MCT                         & 440ed48  & Yes     & 18.6   & 3  & -       \\ \hline
\multirow{2}{*}{PDFBox}     & 1412     & Partial & 19.7   & 4  & -       \\
                            & 1359     & No      & 7.5    & -    & - \\ \hline
\multirow{4}{*}{Mahout}     & 486      & Partial & 34.5   & 5    & -        \\
                            & 1367     & Partial & 21.1   & 7  & -       \\
                            & 1594 	   & No 	 & 14.8 	  & -  & -       \\
                            & 1635     & Yes     & 31.0   & 14  & - \\ \hline
\multirow{3}{*}{Hadoop}     & 2893     & Partial & 7.4    & 3  & 32       \\
						    & 3093      & Yes 	   & 13.1     & 2     & -   \\
                            & 11878    & Yes 	 & 17.4     & 6 & - \\  \hline
\multirow{3}{*}{ActiveMQ}   & 1054     & Yes     & 38.3   & 11  & -      \\
						    & 2880      & Partial  & 27.4    & 6    & -       \\
                            & 5035     & No 	 & 1  & -  & - \\  \hline \hline
\end{tabular}


\label{tab:jcharming-results}
\end{table}

### Bug Selection and Crash Traces

In this study, we have selected the reproduced bugs randomly in order to avoid the introduction of any bias. We selected a random number of bugs ranging from 1 to 10 for each SUT containing the word "exception" and where the description of the bug contains a match to a regular expression designed to find the pattern of a Java exception.


## Empirical Validation\label{sec:results}

Table \ref{tab:jcharming-results} shows the results of JCHARMING in terms of Bug #ID, reproduction status, and execution time (in minutes) of directed model checking (DMC), length of the counter-example (statements in the JUnit test), and  execution time (in minutes) for Model Checking (MC). The experiments have been conducted on a Linux machine (8 GB of RAM and using Java 1.7.0_51).

- The result is noted as "Yes" if the bug has been fully reproduced, meaning that the crash trace generated by the model checker is identical to the crash trace collected during the failure of the system.
- The result is "Partial" if the similarity between the crash trace generated by the model checker and the original crash trace is above t=80% and below t=100%. Given an 80\% similarity threshold, we consider partial reproduction as successful. A different threshold could be used.
- Finally, the result of the approach is reported as "No" if either the similarity is below $t < 80\%$ or the model checker failed to crash the system given the input we provided.

As we can see in Table \ref{tab:jcharming-results}, we were able to reproduce 24  out of 30 bugs either completely or partially (80% success ratio). The average time to reproduce a bug was 19 minutes. The average time in cases where JCHARMING failed to reproduce the bug was 11 minutes. The maximum fail time was 42.3 minutes, which was the time required for JCHARMING to fill all the available memory and stop and a $-$ denotes that JCHARMING reached a sixty-minute timeout. Finally, we report the number of statements in the produced JUnit test, which represents the length of the counter-example. While reproducing a bug is the first step in understanding the cause of a field crash, the steps to reproduce the bug should be as few as possible. It is important for counter-examples to be short to help the developers provide a fix effectively. In average, JCHARMING counter-examples were composed of 5.04 Java statements, which, in our view, is considered reasonable for our approach to be adopted by software developers.
This result demonstrates the effectiveness of our approach, more particularly, the use of backward slicing to create a manageable search space that guides the model checking engine adequately. We also demonstrated that our approach is usable in practice since it is also time efficient. Among the 30 different bugs we have tested, we will describe two bugs (chosen randomly) for each category (successfully reproduced, partially reproduced, and not reproduced) for further analysis. The bug report presented in the following sections are the original reports as submitted by the reporter. As such, they contain typos and spelling mistakes that we did not correct.

### Successfully Reproduced

The first bug we describe in this discussion is the bug #311 belonging to ArgoUML. This bug was submitted in an earlier version of ArgoUML. This bug is very simple to manually reproduce thanks to the extensive description provided by the reporter, which reads: _I open my first project (Untitled Model by default). I choose to draw a Class Diagram. I add a class to the diagram. The class name appears in the left browser panel. I can select the class by clicking on its name. I add an instance variable to the class. The attribute name appears in the left browser panel. I can't select the attribute by clicking on its name. Exception occurred during event dispatching:_ The reporter also attached the crash trace presented in Figure \ref{fig:argo} that we used as input for JCHARMING:

\begin{figure}[]
\noindent\fbox{%
    \parbox{\textwidth}{%
1. java.lang.NullPointerException:\\
2. at\\
3. uci.uml.ui.props.PropPanelAttribute
.setTargetInternal (PropPanelAttribute.java)\\
4. at uci.uml.ui.props.PropPanel.
setTarget(PropPanel.java)\\
5. at uci.uml.ui.TabProps.setTarget(TabProps.java)\\
6. at uci.uml.ui.DetailsPane.setTarget
(DetailsPane.java)\\
7. at uci.uml.ui.ProjectBrowser.select
(ProjectBrowser.java)\\
8. at uci.uml.ui.NavigatorPane.mySingleClick
(NavigatorPane.java)\\
9. at uci.uml.ui.NavigatorPane\$Navigator
MouseListener.mouse Clicked(NavigatorPane.java)\\
10.at
java.awt.AWTEventMulticaster.mouseClicked
(AWTEventMulticaster.java:211)\\
11.
at
java.awt.AWTEventMulticaster.mouseClicked
(AWTEvent
Multicast er.java:210)\\
12.at
java.awt.Component.processMouseEvent
(Component.java:3168)\\
...\\
19. java.awt.LightweightDispatcher
.retargetMouseEvent (Container.java:2068)\\
22.
at java.awt.Container
.dispatchEventImp l(Container.java:1046)\\
23.
at java.awt.Window
.dispatchEventImpl (Window.java:749)\\
24.
at java.awt.Component
.dispatchEvent (Component.java:2312)\\
25.
at java.awt.EventQueue
.dispatchEvent (EventQueue.java:301)\\
28.
at java.awt.EventDispatchThread.pumpEvents\\
(EventDispatch Thread.java:90) \\
29.
at java.awt.EventDispatchThread.run(EventDispatch
Thread.java:82)
    }%
}
\caption{Crash trace reported for bug ArgoUML \#311\label{fig:argo}}
\end{figure}


The cause of this bug is that the reference to the attribute of the class was lost after being displayed on the left panel of ArgoUML and therefore, selecting it through a mouse click throws a null pointer exception. In the subsequent version, ArgoUML developers added a TargetManager to keep the reference of such object in the program. Using the crash trace, JCHARMING's preprocessing step removed the lines between lines 11 and 29 because they belong to the Java standard library and we do not want either the static slice or the model checking engine to verify the Java standard library but only the SUT. Then, the third step performs the static analysis following the process described in Section IV.C. The fourth step performs the model checking on the static slice to produce the same crash trace. More specifically, the model checker identifies that the method _setTargetInternal(Object o)_ could receive a null object that will result in a _Null_ pointer exception.

The second reproduced bug we describe in this section is Bug #486 belonging to MAHOUT. The submitter (Robin Anil) named the bug entry as _Null Pointer Exception running DictionaryVectorizer with ngram=2 on Reuters dataset_. He simply copied the crash stack presented in Figure \ref{fig:mahout} without further explanation.



\begin{figure}[]

\noindent\fbox{%
    \parbox{\textwidth}{%
1 java.io.IOException: Spill failed \\
2 at org.apache.hadoop.mapred.MapTask\$MapOutputBuffer.collect \\ (MapTask.java:860) \\
...\\
14 Caused by: java.lang.NullPointerException \\
15 at java.io.ByteArrayOutputStream.write(ByteArrayOutputStream.java:86) \\
16 at java.io.DataOutputStream.write(DataOutputStream.java:90) \\
17 at org.apache.mahout.utils.nlp.collocations.llr.Gram.write(Gram.java:181) \\
18 at org.apache.hadoop.io.serializer.WritableSerialization\$WritableSerializer.serialize ... \\
19 at org.apache.hadoop.io.serializer.WritableSerialization\$WritableSerializer.serialize ... \\
20 at org.apache.hadoop.mapred.IFile\$Writer.append(IFile.java:179) \\
21 at org.apache.hadoop.mapred.Task\$CombineOutputCollector.collect \\ (Task.java:880) \\
22 at org.apache.hadoop.mapred.Task\$NewCombinerRunner\$OutputConverter.write ... \\
23 at org.apache.hadoop.mapreduce.TaskInputOutputContext.write ... \\
24 at org.apache.mahout.utils.nlp.collocations.llr.CollocCombiner \\ .reduce(CollocCombiner.java:40) \\
25 at org.apache.mahout.utils.nlp.collocations.llr.CollocCombiner \\ .reduce(CollocCombiner.java:25) \\
26 at org.apache.hadoop.mapreduce.Reducer.run(Reducer.java:176) \\
27 at org.apache.hadoop.mapred.Task\$NewCombinerRunner.combine(Task.java:1222) \\
28 at org.apache.hadoop.mapred.MapTask\$MapOutputBuffer.\\ sortAndSpill(MapTask.java:1265) \\
29 at org.apache.hadoop.mapred.MapTask\$MapOutputBuffer.\\ access\$1800(MapTask.java:686) \\
30 at org.apache.hadoop.mapred.MapTask\$MapOutputBuffer \\ \$SpillThread.run(MapTask.java:1173) \\
}%
}

\caption{Crash trace reported for bug Mahout \#486\label{fig:mahout}}
\end{figure}

Drew Farris\footnote{https://issues.apache.org/jira/browse/MAHOUT-486}, who was assigned to fix this bug, commented _Looks like this was due to an improper use of the Gram default constructor that happened as a part of the 0.20.2\footnote{Farris certainly meant 0.10.2 which was the last refactor of the incriminated class, and the current version of Mahout is 0.11} refactoring work._
While this quick comment, made only two and a half hours after the bug submission, was insightful as shown in our generated test case\footnote{As a reminder, the generated test cases are made available at research.mathieu-nayrolles.com/jcharming}, the fix happened in the _CollocCombiner_ class that is one of the _Reducer_\footnote{As in Map/Reduce} available in Mahout.
The fix (commit #f13833) involved creating an iterator to combine the frequencies of the _Gram_ and a null check of the final frequency.

JCHARMING's preprocessing step removed the lines between lines 1 to 14 because they belong to the second thrown exception since _java.lang.NullPointerException_ occurred when writing in a _ByteArrayOutputStream_. JCHARMING aims to reproduce the root exceptions and not the exceptions that derive from other exceptions. This said, the _java.io.IOException: Spill failed_ was ignored, and our directed model checking engine focused, with success, on reproducing the _java.lang.NullPointerException_.

### Partially Reproduced

As an example of a partially reproduced bug, we explore Bug #664 of the Jfreechart program. The description provided by the reporter is: _In ChartPanel.mouseMoved there's a line of code which creates a new ChartMouseEvent using as first parameter the object returned by getChart(). For getChart() is legal to return null if the chart is null, but ChartMouseEvent's constructor calls the parent constructor which throws an IllegalArgumentException if the object passed in is null._

The reporter provided the crash trace containing 42 lines and then replaced an unknown number of lines by the following statement _<\$deleted entry\$>_. While JCHARMING successfully reproduced a crash yielding almost the same trace as the original trace, the _\<$deleted entry\$>_ statement -- which was surrounded by calls to the standard Java library -- was not suppressed and stayed in the crash trace. That is, JCHARMING produced only the 6 (out of 7) first lines and reached 83% similarity, and thus a partial reproduction.


\begin{figure}[]

\noindent\fbox{%
    \parbox{\textwidth}{%

1. java.lang.IllegalArgumentException: null source\\
2. at java.util.EventObject.<init>(
EventObject.java:38)\\
3. at\\
4 org.jfree.chart.ChartMouseEvent.<init>
(ChartMouseEvent.java:83)\\
5. at org.jfree.chart.ChartPanel
.mouseMoved(ChartPanel.java:1692)\\
6. $<$deleted entry$>$

    }%
}

\caption{Crash trace reported for bug JFreeChart \#664\label{fig:jfree}}
\end{figure}


The second partially reproduced bug we present here is Bug #2893 belonging to Hadoop. This bug, reported in February 2008 by Lohit Vijayarenu, was titled _checksum exceptions on trunk_ and contained the following description: _While running jobs like Sort/WordCount on trunk I see few task failures with ChecksumException. Re-running the tasks on different nodes succeeds. Here is the stack_

```java
1 Map output lost, rescheduling: getMapOutput(task_200802251721_0004_m_000237_0,29) failed : 
2 org.apache.hadoop.fs.ChecksumException: Checksum error: /tmps/4/apred-tt/mapred-local/task_200802251721_0004_m_000237_0/file.out at 2085376 
3 at org.apache.hadoop.fs.FSInputChecker.verifySum(FSInputChecker.java:276) 
4 at org.apache.hadoop.fs.FSInputChecker.readChecksumChunk(FSInputChecker.java:238) 
5 at org.apache.hadoop.fs.FSInputChecker.read1(FSInputChecker.java:189) 
6 at org.apache.hadoop.fs.FSInputChecker.read(FSInputChecker.java:157) 
7 at java.io.DataInputStream.read(DataInputStream.java:132) 
8 at org.apache.hadoop.mapred.TaskTracker$MapOutputServlet.doGet(TaskTracker.java:2299) 
...
23 at org.mortbay.util.ThreadPool$PoolThread.run(ThreadPool.java:534)
```

Similarly to the first partially reproduced bug, the crash traces produced by our directed model checking engine and the related test case did not match 100% of the attached crash stack. While JCHARMING successfully reproduced the bug, the crash stack contains timestamps information (e.g., 200802251721), that was logically different in our produced stack trace as we ran the experiment years later.

In all bugs that were partially reproduced, we found that the differences between the crash trace generated from the model checker and the original crash trace (after preprocessing) consist of a few lines only.

### Not Reproduced

To conclude the discussion on the case study, we present a case where JCHARMING was unable to reproduce the failure. For the bug #47957, belonging to LOG4J and reported in late 2009 the author wrote: _Configure SyslogAppender with a Layout class that does not exist; it throws a NullPointerException. Following is the exception trace:_ and attached the following crash trace:

```java
1. 10052009 01:36:46 ERROR [Default: 1]
struts.CPExceptionHandler.execute
RID[(null;25KbxlK0voima4h00ZLBQFC;236Al8E60000045C3A
7D74272C4B4A61)] 
2. Wrapping Exception in ModuleException
3. java.lang.NullPointerException
4. at org.apache.log4j.net.SyslogAppender
.append(SyslogAppender.java:250)
5. at org.apache.log4j.AppenderSkeleton
.doAppend(AppenderSkeleton.java:230)
6. at org.apache.log4j.helper.AppenderAttachableImpl
.appendLoopOnAppenders(AppenderAttachableImpl
.java:65)
7. at org.apache.log4j.Category.callAppenders
(Category.java:203)
8. at org.apache.log4j.Category
.forcedLog(Category.java:388)
9. at org.apache.log4j.Category.info
(Category.java:663)
```



The first three lines are not produced by the standard execution of the SUT but by an ExceptionHandler belonging to Struts [@ApacheSoftwareFoundation2000]. Struts is an open source MVC (Model View Controller) framework for building Java web applications. JCHARMING examined the source code of Log4J for the crash location _struts.CPExceptionHandler.execute_ and did not find it since this method belongs to the source base of Struts -- which uses log4j as a logging mechanism. As a result, the backward slice was not produced, and we failed to perform the next steps. It is noteworthy that the bug is marked as a duplicate of the bug #46271 which contains a proper crash trace. We believe that JCHARMING could have successfully reproduced the crash if it was applied to the original bug.

The second bug that we did not reproduce and that we present in this section belongs to Mahout. Jaehoon Ko reported it on July 2014. Bug #1594 is titled _Example factorize-movielens-1M.sh does not use HDFS_ and reads _It seems that factorize-movielens-1M.sh does not use HDFS at all. All paths look local paths, not HDFS. So the example crashes because it cannot find input data from HDFS:_

```java
1 Exception in thread $$main'' org.apache.hadoop.mapreduce.lib.input.InvalidInputException: Input path does not exist: /tmp/mahout-work-hoseog.lee/movielens/ratings.csv 
2 at org.apache.hadoop.mapreduce.lib.input.FileInputFormat.singleThreadedListStatus ... 
3 at org.apache.hadoop.mapreduce.lib.input.FileInputFormat.listStatus ... 
... 
31 at org.apache.hadoop.util.RunJarm.theain(RunJar.java:212)
```

This entry was marked as _Not A Problem / WONT_FIX_ meaning that the reported bug was not a bug in the first place. The resolution of this bug\footnote{https://github.com/apache/mahout/pull/38\#issuecomment-51436303} involved the modification of a bash script that Ko (the submitter) was using to _query_ Mahout. In other words, the cause of the failure was external to Mahout itself, and this is why JCHARMING could not reproduce it.

## Threats to Validity\label{sec:threats}

The selection of SUTs is one of the common threats to validity for approaches aiming to improve the understanding of a program's behavior. It is possible that the selected programs share common properties that we are not aware of and therefore, invalidate our results. However, the SUTs analyzed by JCHARMING are the same as the ones used in similar studies. Moreover, the SUTs vary in terms of purpose, size and history.

Another threat to validity lies in the way we have selected the bugs used in this study. We selected the bugs randomly to avoid any bias. One may argue that a better approach would be to select bugs based on complexity or other criteria (severity, etc.). We believe that a complex bug (if complexity can at all be measured) may perhaps have an impact on the running time of the approach, but we are not convinced that the accuracy of our approach depends on the complexity or the type of bugs we use. Instead, it depends on the quality of the produced crash trace. This said, in theory, we may face situations where the crash trace is completely corrupted. In such cases, there is nothing that guides the model checker. In other words, we will end up running a full model checker. It is difficult to evaluate the number of times we may face this situation without conducting an empirical study on the quality of crash traces. We defer this to future work.

In addition, we see a threat to validity that stems from the fact that we only used open source systems. The results may not be generalizable to industrial systems. 

Field failures can also occur due to the running environment in which the program is executed. For instance, the failure may have been caused by the reception of a network packet or the opening of a given file located on the hard drive of the users. The resulting failures will hardly be reproducible by JCHARMING.

Finally, the programs we used in this study are all written in the Java programming language and JCHARMING leverages the crash traces produced by the JVM to reproduce bugs. This can limit the generalization of the results. However, similar to Java, .Net, Python and Ruby languages also produce crash traces. Therefore, JCHARMING could be applied to other object-oriented languages.

In conclusion, internal and external validity have both been minimised by choosing a relatively large set of different systems and using input data that can be found in other programming languages.

## Chapter Summary\label{sec:conclusion}

In this chapter, we presented JCHARMING (Java CrasH Automatic Reproduction by directed Model checking), an automatic bug reproduction technique that combines crash traces and directed model checking. JCHARMING relies on crash traces and backward program slices to direct a model checker. This way, we do not need to visit all the states of the subject program, which would be computationally taxing.  When applied to thirty bugs from ten open source systems, JCHARMING was able to successfully reproduce 80% of the bugs. The average time to reproduce a bug was 19 minutes, which is quite reasonable, given the complexity of reproducing bugs that cause field crashes. Given that in most open-source systems, reported bugs are fixed several days after they are reported [@Weiss2007], we thin that our solution is application despite the wait-time.

This said, JCHARMING suffers from three main limitations. The first one is that it cannot reproduce bugs caused by multi-threading. We can overcome this limitation by using advanced features of the JPF model checker such as the _jpf-ltl_ listener. The _jpf-ltl_ listener was designed to check temporal properties of concurrent Java programs. The second limitation is that JCHARMING cannot be used if external inputs cause the bug. We can always build a monitoring system to retrieve this data, but this may lead to privacy concerns. Finally, the third limitation is that the performance of JCHARMING relies on the quality of the crash traces. This limitation can be addressed by investigating techniques that can improve the reporting of crash traces. For the time being, the bug reporters simply copy and paste (and modify) the crash traces into the bug description. A better practice would be to automatically append the crash trace to the bug report, for example, in a different field than the bug description.
