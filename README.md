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

Sine and Cosine values are approximated using *FastMath*, which implements a lookup table with linear interpolation.

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

*Filler*, *Adder*, *Subtracter*, and *Multiplier* are some basic Discrete Systems. Strictly speaking, *Filler* is not mentioned
in the text, but I found it helpful when writing unit tests to have a System which did nothing but fill an entire Sequence with
one particular value.  Note that I completely made up the term "Congruent" (or at least I think I did) when naming
*AbstractCongruentSystem* with the intention of describing two things which have the same length (like congruent line segments).
Note that it was tempting to only refactor out the interior of the repeated `for` loop as the abstract method here, but the
performance of that has the potential to suck due to (probably) making a method call for every iteration.

From section 1.8 of Understanding DSP I noticed a Multiplier that takes a single value, so I also created *ConstantMultiplier*
for multiplying all values of a Sequence by the same value.  It works a lot like *Filler* except that it's multiplying instead
of simply filling.

*SimpleSineWave*, *OffsetSineWave*, *SumSineWaves*, *DifferenceSineWaves*, and *MultiplySineWaves* are example Java applications
demonstrating the discrete sequences for a sine wave, a sine wave offset by 180 degrees (pi radians), and various Discrete Systems
applied to pairs of sine waves.  These use the excellent and highly versatile JFreeChart package.  Note that the text
recommends against giving in to the temptation to connect the points with line segments, because nothing is actually known about
the values between points.  To that end, we configure JFreeChart to render a scatter plot using small circles.

I've opted to leave the implementation of Summation and Unit Delay until the chapter on filters when they're needed.

### Chapter Three (The Discrete Fourier Transform)

*SummationParallelExecutor* plays an important role in reducing the time taken by this O(n^2) algorithm by running summations
simultaneously on all your cores via the *CoreAwareParallelExecutor*.  Analgous to the *DiscreteSystemWorker* is the
*SummationWorker* which accepts as its input the summation point it is working on, the input sequences, and the sequence 
to use to store its results.

Note that some typical Java conventions are sacrificed somewhat for the sake of performance; notably, we're pre-allocating
our Sequences (arrays) and passing those in to the methods that use them, rather than passing around results and copying things.

We also introduce the *DiscreteFourierTransformer* and the *DFTSummationWorker*. The actual effort of the DFT is performed in the
*DFTSummationWorker* with the aid of the *SummationParallelExecutor*.  Advantage is taken of the symmetry of the DFT. Only the
first half is calculated, and then the symmetric results are populated, also in parallel on as many cores as we're using, using
a little bit of a cheat with the DiscreteSystemParallelExecutor.

The *DiscreteFourierTransformer* accepts an optional *DFTSummationWorker* argument which can be the default 
*DFTSummationWorker*, or one of the windowing workers, *DFTHammingSummationWorker* or *DFTHanningSummationWorker*.  Some usual
coding style conventions have been deliberately disregarded for the sake of performance, which is most obvious with respect to
the copypasta you'll find between *DFTSummationWorker* and *DFTCosWindowSummationWorker*.  Don't worry too much about it. It's
for a good cause: a significant improvement in performance over making method calls within the `for` loop to apply the window
functions.

#### Performance

Since I brought up performance trade-offs...

When investigating how to apply windows to the Discrete Time Sequence, I found some rather unexpected behaviors with respect to
performance from the JVM that are probably worth noting here.

Expected is that method calls slow things down some, but the slowdown isn't as much as you might expect.  For a N=22050 DFT, on 
my 8-core desktop, the average (over 20 trials) computation time for declaring a double value equal to the window multiplier
versus making a method call that calculated the value and returned it was nearly a dead heat, but calling a method was actually
a tiny bit _faster_. Inlining the double in the calculation (with no method call and no variable declaration) sped things up 
somewhat more.  This gives the appearance that declaring a `double` and then using it is more costly than calling a _method_
which returns the calculated double, which is more costly than just inlining the calculation. Declaring the `double` as final
caused it to perform almost identically to the method call.

Incidentally, method calls to bean accessors (e.g., `getLength()`) are relatively cheap, being only slightly more costly
over a very large number of calculations than pre-fetching the value into an `int` and then referencing the `int`.

Curiously enough, the results were different for `int` calculations.  Declaring a `final int` containing the array index of the
sample value we want is very slightly faster than inlining the calculation in the array index.  Moving the `int` into the 
`for` loop was much worse, suggesting that perhaps doing an addition of two ints may be less costly than incrementing an int
in the setup of a `for` loop, possibly because the internals of the loop do not get optimized as well as what happens inside
the loop.

Another interesting artifact is that casting is expensive.  Avoiding one early cast from a `double` to an `int` made a 
difference of nearly 60ms to the execution time of a N=22050 DFT.

But removing a `double` up-cast (to get a floating point result of a division instead of an integer one) by creating a `double`
to hold the up-casted value actually made the timing worse.  The trend appears to be that declaring a double is expensive,
relatively speaking.

Inlining a `double` calculation instead of declaring it as a variable, even a final one, resulted in a significant performance
improvement (over 300ms for N=22050).

Changing `+= -1 * (stuff)` to `-= stuff` saved 100ms over N=22050. Correcting a math mistake where I failed to upcast an
`int` to a `double` further shaved off just a little time, suggesting that doing purely `double` math is just a little faster
than doing math with mixed types.

Amazingly, getting rid of a variable (`points`) which was always identical to another variable (`samples`) caused a
_significant_ slowdown in calculation.  Looking for a pattern, I also replaced another repeated variable from the same calculation
with a duplicate, and lo and behold, this sped things up as well!  It looks as though referencing the same variable multiple times
in the same calculation actually slows things down, and in some cases you're actually better off declaring a variable which is
an exact duplicate of an extant variable and using the second variable rather than using the one variable twice.  I speculate that
under the hood this makes better use of CPU registers or cache, but I'm only guessing.

Sometimes it matters _where_ you declare a variable.  Declaring `sequenceStartIndex` immediately before the loop that uses it
provided a significant performance boost over declaring it earlier in the method.  Moving it up one line at a time, once I got it
as high as being declared before both `double` variables, performance took a nose-dive.  I can only imagine that this must
be related to caching.  It didn't make any difference to move `samples` and `points` earlier in the method. Possibly the
gating factor is the use of the variable in an assignment just one line down.

The general rules of thumb appear to be:
* You need to actually measure this stuff and play around with your calculation internals some.
* Avoid declaring doubles unless you need to; prefer inlining.
* Avoid method calls unless you need to; they're about as bad as declaring doubles.
* Accessors aren't as bad as normal method calls, but they're still worse than not making method calls.
* Avoid performing calculations inside array indices (e.g., `array[index + offset]`); prefer `final int arrayIndex =`
* Avoid downcasting too early or in a loop.
* But feel good about upcasting in denominators to get `double` results from `int` variables over declaring doubles.
* Check your math and make sure you aren't doing anything weird that may result in unnecessary operations.
* In long formulas, avoid referencing the same variable more than once.  Yes, that's weird.
* If you're going to use a variable you're declaring shortly thereafter in another variable assignment, declare it as close to the
variable assignment as you can.

### Chapter Four (The Fast Fourier Transform)

Three things I have observed from reading about the FFT from Understanding DSP, from other books, and from online postings about it
are: (1) it's incredibly difficult to explain and hard to understand, (2) most of the code in the wild looks like it was written
by mathematicians in Fortran in the 1960's and almost none of it contains descriptive variable names or comments, and (3) very,
very smart people have spent countless hours doing everything in their power to optimize the hell out of it, reducing it in 
computational complexity to the bare minimum number of multiplications that are absolutely necessary in order to produce the 
desired result.

It's probably easiest to understand the recursive versions of the algorithm, because there the "divide and conquer" strategy is
more obviously apparent.  The trouble with recursive "divide and conquer" algorithms, of course, is one typically has a finite
stack depth, but would like not to put an upper bound on the size of the transform which can be executed successfully.  There's
also a computational cost to making the method call to recurse, and while it's probably pretty insignificant on modern hardware,
it is an additional cost nonetheless.  A recursive algorithm is also more difficult to parallelize, and one of my goals for this
whole project is to ensure that everything can take advantage of multiple cores.

Fortunately we know from computer science that for any recursive algorithm, there also exists an iterative algorithm which can
perform the same function.

I have therefore opted to implement the iterative FFT algorithm, and to use better variable names than one normally finds in
example code, with comments explaining what in the hell is going on.