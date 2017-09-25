package net.spatula.dspatula.concurrent;

import java.util.ArrayList;
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
public class SummationParallelExecutor {

    protected CoreAwareParallelExecutor executor;
    private static SummationParallelExecutor instance;

    private static final Logger LOG = LoggerFactory.getLogger(SummationParallelExecutor.class);

    protected SummationParallelExecutor(CoreAwareParallelExecutor executor) {
        this.executor = executor;
    }

    /**
     * Create a SummationParallelExecutor with the number of parallel threads equal to the number of available cores minus one.
     *
     * @return a singleton SummationParallelExecutor
     */
    public synchronized static SummationParallelExecutor getDefaultInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new SummationParallelExecutor(CoreAwareParallelExecutor.getInstance());

        return instance;
    }

    protected synchronized static SummationParallelExecutor getInstance(CoreAwareParallelExecutor executor) {
        if (instance != null) {
            return instance;
        }

        instance = new SummationParallelExecutor(executor);

        return instance;
    }

    private static class WorkerSummationCallable<T extends Sequence<T>, V extends Sequence<V>> implements Callable<Void> {

        private final SummationWorker<T, V> discreteSystemWorker;
        private final List<T> inputSequences;
        private final V outputSequence;
        private final int point;

        private WorkerSummationCallable(int point, SummationWorker<T, V> discreteSystemWorker, List<T> inputSequences,
                V outputSequence) {
            this.inputSequences = inputSequences;
            this.outputSequence = outputSequence;
            this.discreteSystemWorker = discreteSystemWorker;
            this.point = point;
        }

        @Override
        public Void call() throws Exception {
            discreteSystemWorker.operate(point, inputSequences, outputSequence);
            return null;
        }

    }

    /**
     * Perform a parallel summation using the input sequences and output sequence.
     *
     * As many summations will be performed in parallel as cores are available on the machine. Care must be taken to ensure that
     * workers write only to their alotted location in the output sequence.
     *
     * (In actual practice, the internals of the sequence are never isolated/copied for the sake of performance; only the indices
     * indicating where to start and end are updated. Because of this, in reality, each thread of the discreteSystemWorker *can* see
     * the entire sequence if it cheats. Care must therefore be taken to ensure that DiscreteSystemWorkers are thread-safe.)
     *
     * @param summationWorker
     * @param sequences
     * @throws ProcessingException
     *             If errors occur while running the job
     */
    public <T extends Sequence<T>, V extends Sequence<V>> void execute(final SummationWorker<T, V> summationWorker,
            List<T> inputSequences, V outputSequence) throws ProcessingException {

        final int summationLength = outputSequence.getLength();

        final List<Callable<Void>> callables = new ArrayList<>(summationLength);

        for (int point = 0; point < summationLength; point++) {
            callables.add(new WorkerSummationCallable<T, V>(point, summationWorker, Collections.unmodifiableList(inputSequences),
                    outputSequence));
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
