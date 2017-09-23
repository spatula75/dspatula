package net.spatula.dspatula.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.Sequence;

/**
 * Execution context which manages dividing up a Discrete-Time Signal Sequence into multiple smaller work units that can be operated
 * on by Discrete System workers simultaneously.
 *
 * Very short sequences may be run on a single core to avoid the overhead of division.
 *
 * @author spatula
 *
 */
public class DiscreteSystemParallelExecutor {

    protected final int cores;
    protected final int minimumDivisionSize;
    protected ExecutorService threadPool;

    private static DiscreteSystemParallelExecutor instance;

    private static final Logger LOG = LoggerFactory.getLogger(DiscreteSystemParallelExecutor.class);
    private static final int DEFAULT_MIN_DIVISION_SIZE = 8820; // Magic number found empirically to be ~8500

    protected DiscreteSystemParallelExecutor(int cores, int minimumDivisionSize) {
        this.cores = cores;
        this.minimumDivisionSize = minimumDivisionSize;

        threadPool = Executors.newFixedThreadPool(cores, new ThreadFactory() {

            private volatile int threadNumber = 1;

            @Override
            public Thread newThread(Runnable runnable) {
                final Thread thread = new Thread(runnable, "DiscreteSystemParallelExecutor-threadPool-" + (threadNumber++));
                thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        LOG.error("Uncaught exception in thread {}", thread.getName(), throwable);
                    }
                });
                LOG.trace("Created thread {}", thread.getName());
                return thread;
            }
        });
        LOG.debug("Created a parallel execution environment using {} threads", cores);
    }

    /**
     * Create a DiscreteSystemParallelExecutor with the number of parallel threads equal to the number of available cores minus one.
     *
     * @return a singleton DiscreteSystemParallelExecutor
     */
    public synchronized static DiscreteSystemParallelExecutor getDefaultInstance() {
        if (instance != null) {
            return instance;
        }
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        instance = new DiscreteSystemParallelExecutor(calculateNumberOfCores(availableProcessors), DEFAULT_MIN_DIVISION_SIZE);

        return instance;
    }

    protected synchronized static DiscreteSystemParallelExecutor getInstance(int cores) {
        if (instance != null) {
            return instance;
        }

        instance = new DiscreteSystemParallelExecutor(cores, DEFAULT_MIN_DIVISION_SIZE);

        return instance;
    }

    protected static int calculateNumberOfCores(int availableProcessors) {
        return Math.max(availableProcessors - 2, 1);
    }

    private static class WorkerSequenceCallable implements Callable<Void> {

        private final DiscreteSystemWorker discreteSystemWorker;
        private final Sequence[] sequences;

        private WorkerSequenceCallable(DiscreteSystemWorker discreteSystemWorker, Sequence... sequences) {
            this.sequences = sequences;
            this.discreteSystemWorker = discreteSystemWorker;
        }

        @Override
        public Void call() throws Exception {
            discreteSystemWorker.operate(sequences);
            return null;
        }

    }

    /**
     * Perform a parallel execution on the Sequence(s) by a DiscreteSystemWorker.
     *
     * sequence[0] is special. It is expected to contain the largest / full-length sample, and it is also expected to contain the
     * result of all the work once it has been completed. So, for example, if multiple sequences are being added together, the
     * result goes back into sequence[0].
     *
     * Sequences of the same length will be chunked to the same sizes, it being assumed that the operation being performed is
     * sample-for-sample.
     *
     * Sequences of shorter length than sequence[0] will not be chunked but will be passed through whole.
     *
     * (In actual practice, the internals of the sequence are never isolated/copied for the sake of performance; only the indices
     * indicating where to start and end are updated. Because of this, in reality, each thread of the discreteSystemWorker *can* see
     * the entire sequence if it cheats. Care must therefore be taken to ensure that DiscreteSystemWorkers are thread-safe.)
     *
     * @param discreteSystemWorker
     * @param sequences
     * @throws ProcessingException
     *             If errors occur while running the job
     */
    public void execute(final DiscreteSystemWorker discreteSystemWorker, Sequence... sequences) throws ProcessingException {
        final int firstSequenceLength = sequences[0].getLength();
        final int firstSequenceEnd = sequences[0].getEnd();

        if (firstSequenceLength < minimumDivisionSize) {
            discreteSystemWorker.operate(sequences);
            return;
        }

        final int chunkSize = (int) Math.ceil((double) sequences[0].getLength() / (double) cores);

        final List<Callable<Void>> callables = new ArrayList<>(cores);
        for (int start = 0; start <= firstSequenceEnd; start += chunkSize) {
            final int end = Math.min(start + chunkSize - 1, firstSequenceEnd);

            final Sequence[] subsequences = new Sequence[sequences.length];
            for (int seqNum = 0; seqNum < sequences.length; seqNum++) {
                if (sequences[seqNum].getLength() < firstSequenceLength) {
                    subsequences[seqNum] = sequences[seqNum];
                } else {
                    subsequences[seqNum] = sequences[seqNum].subsequence(start, end);
                }
            }

            callables.add(new WorkerSequenceCallable(discreteSystemWorker, subsequences));
        }

        try {
            threadPool.invokeAll(callables);
        } catch (final InterruptedException e) {
            throw new ProcessingException("Interrupted", e);
        }
    }

}
