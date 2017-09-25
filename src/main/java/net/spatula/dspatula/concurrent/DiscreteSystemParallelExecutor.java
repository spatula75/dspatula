package net.spatula.dspatula.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    protected final int minimumDivisionSize;
    protected CoreAwareParallelExecutor executor;
    private static DiscreteSystemParallelExecutor instance;

    private static final Logger LOG = LoggerFactory.getLogger(DiscreteSystemParallelExecutor.class);

    private static final int DEFAULT_MIN_DIVISION_SIZE = 8820; // Magic number found empirically to be ~8500

    protected DiscreteSystemParallelExecutor(CoreAwareParallelExecutor executor, int minimumDivisionSize) {
        this.minimumDivisionSize = minimumDivisionSize;
        this.executor = executor;
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
        instance = new DiscreteSystemParallelExecutor(CoreAwareParallelExecutor.getInstance(), DEFAULT_MIN_DIVISION_SIZE);

        return instance;
    }

    protected synchronized static DiscreteSystemParallelExecutor getInstance(CoreAwareParallelExecutor executor) {
        if (instance != null) {
            return instance;
        }

        instance = new DiscreteSystemParallelExecutor(executor, DEFAULT_MIN_DIVISION_SIZE);

        return instance;
    }

    private static class WorkerSequenceCallable<T extends Sequence<T>> implements Callable<Void> {

        private final DiscreteSystemWorker<T> discreteSystemWorker;
        private final List<T> sequences;

        private WorkerSequenceCallable(DiscreteSystemWorker<T> discreteSystemWorker, List<T> sequences) {
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
    public <T extends Sequence<T>> void execute(final DiscreteSystemWorker<T> discreteSystemWorker,
            @SuppressWarnings("unchecked") T... sequences) throws ProcessingException {
        final int firstSequenceStart = sequences[0].getStart();
        final int firstSequenceLength = sequences[0].getLength();
        final int firstSequenceEnd = sequences[0].getEnd();

        if (firstSequenceLength < minimumDivisionSize) {
            discreteSystemWorker.operate(Collections.unmodifiableList(Arrays.asList(sequences)));
            return;
        }

        final int chunkSize = (int) Math.ceil((double) sequences[0].getLength() / (double) executor.getCoreCount());

        final List<Callable<Void>> callables = new ArrayList<>(executor.getCoreCount());
        for (int start = firstSequenceStart; start <= firstSequenceEnd; start += chunkSize) {
            final int end = Math.min(start + chunkSize - 1, firstSequenceEnd);

            final List<T> subsequences = new ArrayList<>(sequences.length);

            for (int seqNum = 0; seqNum < sequences.length; seqNum++) {
                if (sequences[seqNum].getLength() < firstSequenceLength) {
                    subsequences.add(seqNum, sequences[seqNum]);
                } else {
                    subsequences.add(seqNum, sequences[seqNum].subsequence(start, end));
                }
            }

            callables.add(new WorkerSequenceCallable<T>(discreteSystemWorker, Collections.unmodifiableList(subsequences)));
        }

        try {
            final List<Future<Void>> futures = executor.invokeAll(callables);
            for (final Future<Void> future : futures) {
                try {
                    future.get();
                } catch (final ExecutionException e) {
                    LOG.error("Exception during execution", e);
                    throw new ProcessingException("Internal error", e);
                }

            }
        } catch (final InterruptedException e) {
            throw new ProcessingException("Interrupted", e);
        }
    }

}
