# DSPatula

The goal of this project is to work through Richard G Lyons's excellent "Understanding Digital Signal Processing" book and provide
high-performance, well-tested, idiomatic Java example code to go along with each chapter in the book.

This is very much a work in progress.  The primary goal is to help myself in understanding DSP (something I didn't learn in my CS
courses), with the secondary goal of helping other people who either also want to learn DSP, want a Java reference to go along with
Lyons's book, or who have a need of doing some simple Java DSP work.

I hope to take some of my experience with Java concurrency to ensure that every example will take advantage of as many cores as are
offered by the platform, while still attempting to be sure that the examples are clear in expressing intention in the code.

It is also my aim to ensure that ample unit tests exist, because correctness is of course very important to DSP work, and often 
unit tests are an excellent way of expressing one's intention.

When running Java DSP code, especially for relatively short-running programs (like tests, examples), it helps consistency and 
performance considerably to add -XX:CompileThreshold=1500 for Java 8 and later.

## Preface
### Parallel Execution

For Discrete Sequences of finite length, when the length exceeds some practical threshold where the overhead of launching and
maintaining threads is worthwhile (which happens somewhere between 8000 and 9000 samples), the *DiscreteSystemParallelExecutor*
will divide up the workload equally, and farm it off to *DiscreteSystemWorkers* to do the actual work. The number of threads 
launched equals the number of cores asserted by the system, minus 2 (we want to leave a couple cores free for the operating
system and for Java internals like Hotspot compilers and Garbage Collection).

When looking for the formulas that are doing the bulk of the work, look at the classes which implement *DiscreteSystemWorker*.

## Chapter Content

### Chapter One (Discrete Sequences and Their Notation)

*Sequence* is a simple implementation of a Discrete Time Signal.  Under the hood, it's a signed integer array and also encapsulates
a start and end point and length.  This is done to facilitate parallelization of signal processing; each thread gets a
subsequence of the original Sequence to work on.  It's important that DiscreteSystemWorkers behave themselves and stay within
the bounds described by their Sequence, of course, because they're all sharing the same array.  (It would be extremely wasteful
and time-consuming to copy arrays all over the place.)

*SineWaveSignalGenerator* (and its counterpart *SineWaveWorker*) implement the formula for generating a sine wave of a given 
frequency and sample rate.  I added the bit about the phase offset, because I'm going to need that later myself.  For Chapter 1, 
just specify an offset of 0 and the formula is equivalent.
