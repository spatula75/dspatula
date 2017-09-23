package net.spatula.dspatula.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.Sequence;

public class DiscreteSystemParallelExecutorTest {

    @Test
    public void testConstruct() {
        final DiscreteSystemParallelExecutor instance = DiscreteSystemParallelExecutor.getDefaultInstance();
        final DiscreteSystemParallelExecutor instance2 = DiscreteSystemParallelExecutor.getDefaultInstance();
        assertSame(instance2, instance);
    }

    @Test
    public void testCoreCount() {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(2, 100);
        assertEquals(2, executor.cores);
    }

    @Test
    public void testPoolSize() {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(2, 100);
        assertEquals(ThreadPoolExecutor.class.cast(executor.threadPool).getCorePoolSize(), 2);
    }

    @Test
    public void testMulticore() {
        assertEquals(DiscreteSystemParallelExecutor.calculateNumberOfCores(2), 1);
    }

    @Test
    public void testSingleCore() {
        assertEquals(DiscreteSystemParallelExecutor.calculateNumberOfCores(1), 1);
    }

    @Test
    public void testOneWorkerThreadDoesItAll() throws ProcessingException {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(1, 100);

        final int samples = 16384;
        final Sequence sequence = new Sequence(samples);
        executor.execute(new DiscreteSystemWorker() {

            @Override
            public void operate(Sequence... sequences) {
                final Sequence sequence = sequences[0];
                final int[] sequenceValues = sequence.getSequenceValues();

                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        final int[] sequenceValues = sequence.getSequenceValues();
        for (int index = 0; index < samples; index++) {
            assertEquals(sequenceValues[index], index);
        }

    }

    @Test
    public void testSubmitAndDivideWork() throws ProcessingException {
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(5, 100);
        final int samples = 16384;
        final Sequence sequence = new Sequence(samples);

        final Set<Integer> sequenceStarts = Collections.newSetFromMap(new ConcurrentHashMap<>());
        final Set<Integer> sequenceEnds = Collections.newSetFromMap(new ConcurrentHashMap<>());

        executor.execute(new DiscreteSystemWorker() {

            @Override
            public void operate(Sequence... sequences) {
                final Sequence sequence = sequences[0];
                final int[] sequenceValues = sequence.getSequenceValues();

                sequenceStarts.add(sequence.getStart());
                sequenceEnds.add(sequence.getEnd());
                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        final int[] sequenceValues = sequence.getSequenceValues();
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
        final DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(5, 8001);
        final int samples = 8000;
        final Sequence sequence = new Sequence(samples);

        final AtomicInteger threadCount = new AtomicInteger(0);

        executor.execute(new DiscreteSystemWorker() {

            @Override
            public void operate(Sequence... sequences) {
                final Sequence sequence = sequences[0];
                final int[] sequenceValues = sequence.getSequenceValues();

                threadCount.incrementAndGet();

                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        final int[] sequenceValues = sequence.getSequenceValues();
        for (int index = 0; index < samples; index++) {
            assertEquals(sequenceValues[index], index);
        }

        assertEquals(threadCount.get(), 1);
    }

    // Goofy thing for helping to characterize performance.
    @Test(enabled = false)
    public void testFindSweetSpot() throws ProcessingException {
        final DiscreteSystemParallelExecutor executor = DiscreteSystemParallelExecutor.getInstance(1);
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(44100);
        // warm things up first
        System.out.println("Warming up HotSpot");
        for (int j = 0; j < 1000; j++) {
            long start = 0, stop = 0;
            for (int i = 1; i < 60; i++) {
                start = System.nanoTime();
                final Sequence sequence = generator.generate(25, i, 32767, 0);
                stop = System.nanoTime();
            }
            if (j % 50 == 0) {
                System.out.println(j);
            }
        }
        long start = 0, stop = 0;
        for (int j = 0; j < 3; j++) {
            for (int i = 1; i < 60; i++) {
                long sum = 0;
                long length = 0;
                for (int k = 1; k < 100; k++) {
                    start = System.nanoTime();
                    final Sequence sequence = generator.generate(25, i / 120F, 32767, 0);
                    stop = System.nanoTime();
                    final long dur = stop - start;
                    sum += dur;
                    length = sequence.getLength();
                }
                System.out.println("Sequence of " + length + " samples took avg " + (sum / 100) / 1000 + "us");
            }
        }
    }

}
