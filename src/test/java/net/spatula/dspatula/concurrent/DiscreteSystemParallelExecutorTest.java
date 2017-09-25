package net.spatula.dspatula.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.RealSequence;

public class DiscreteSystemParallelExecutorTest {

    @Test
    public void testOneWorkerThreadDoesItAll() throws ProcessingException {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(new CoreAwareParallelExecutor(1), 100);

        final int samples = 16384;
        final RealSequence sequence = new RealSequence(samples);
        executor.execute(new DiscreteSystemWorker<RealSequence>() {

            @Override
            public void operate(List<RealSequence> sequences) {
                final RealSequence sequence = sequences.get(0);
                final int[] sequenceValues = sequence.getRealValues();

                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    System.out.println(index);
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        final int[] sequenceValues = sequence.getRealValues();
        for (int index = 0; index < samples; index++) {
            assertEquals(sequenceValues[index], index);
        }

    }

    @Test
    public void testSubmitAndDivideWork() throws ProcessingException {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(new CoreAwareParallelExecutor(5), 100);
        final int samples = 16384;
        final RealSequence sequence = new RealSequence(samples);

        final Set<Integer> sequenceStarts = Collections.newSetFromMap(new ConcurrentHashMap<>());
        final Set<Integer> sequenceEnds = Collections.newSetFromMap(new ConcurrentHashMap<>());

        executor.execute(new DiscreteSystemWorker<RealSequence>() {

            @Override
            public void operate(List<RealSequence> sequences) {
                final RealSequence sequence = sequences.get(0);
                final int[] sequenceValues = sequence.getRealValues();

                sequenceStarts.add(sequence.getStart());
                sequenceEnds.add(sequence.getEnd());
                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        final int[] sequenceValues = sequence.getRealValues();
        for (int index = 0; index < samples; index++) {
            assertEquals(sequenceValues[index], index);
        }

        // Make sure it was divided up the way we expected.
        assertTrue(sequenceStarts.contains(0));
        assertTrue(sequenceEnds.contains(3276));
        assertTrue(sequenceStarts.contains(3277));
        assertTrue(sequenceEnds.contains(6553));
        assertTrue(sequenceStarts.contains(6554));
        assertTrue(sequenceEnds.contains(9830));
        assertTrue(sequenceStarts.contains(9831));
        assertTrue(sequenceEnds.contains(13107));
        assertTrue(sequenceStarts.contains(13108));
        assertTrue(sequenceEnds.contains(16383));
    }

    @Test
    public void testSmallSampleSizeSingleThread() throws ProcessingException {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(new CoreAwareParallelExecutor(5), 8001);
        final int samples = 8000;
        final RealSequence sequence = new RealSequence(samples);

        final AtomicInteger threadCount = new AtomicInteger(0);

        executor.execute(new DiscreteSystemWorker<RealSequence>() {

            @Override
            public void operate(List<RealSequence> sequences) {
                final RealSequence sequence = sequences.get(0);
                final int[] sequenceValues = sequence.getRealValues();

                threadCount.incrementAndGet();

                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        final int[] sequenceValues = sequence.getRealValues();
        for (int index = 0; index < samples; index++) {
            assertEquals(sequenceValues[index], index);
        }

        assertEquals(threadCount.get(), 1);
    }
}
