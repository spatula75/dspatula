package net.spatula.dspatula.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.testng.annotations.Test;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.Sequence;

public class DiscreteSystemParallelExecutorTest {

    @Test
    public void testConstruct() {
        DiscreteSystemParallelExecutor instance = DiscreteSystemParallelExecutor.getDefaultInstance();
        DiscreteSystemParallelExecutor instance2 = DiscreteSystemParallelExecutor.getDefaultInstance();
        assertSame(instance2, instance);
    }

    @Test
    public void testCoreCount() {
        DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(2, 100);
        assertEquals(2, executor.cores);
    }

    @Test
    public void testPoolSize() {
        DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(2, 100);
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
        DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(1, 100);

        Sequence sequence = new Sequence(8192);
        executor.execute(new DiscreteSystemWorker() {

            @Override
            public void operate(Sequence... sequences) {
                Sequence sequence = sequences[0];
                int[] sequenceValues = sequence.getSequenceValues();

                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        int[] sequenceValues = sequence.getSequenceValues();
        for (int index = 0; index < 8192; index++) {
            assertEquals(sequenceValues[index], index);
        }

    }

    @Test
    public void testSubmitAndDivideWork() throws ProcessingException {
        DiscreteSystemParallelExecutor executor = new DiscreteSystemParallelExecutor(5, 100);
        Sequence sequence = new Sequence(8192);

        Set<Integer> sequenceStarts = new HashSet<>();
        Set<Integer> sequenceEnds = new HashSet<>();

        executor.execute(new DiscreteSystemWorker() {

            @Override
            public void operate(Sequence... sequences) {
                Sequence sequence = sequences[0];
                int[] sequenceValues = sequence.getSequenceValues();

                sequenceStarts.add(sequence.getStart());
                sequenceEnds.add(sequence.getEnd());

                for (int index = sequence.getStart(); index <= sequence.getEnd(); index++) {
                    sequenceValues[index] = index;
                }
            }
        }, sequence);

        // Make sure we did all our work correctly first.
        int[] sequenceValues = sequence.getSequenceValues();
        for (int index = 0; index < 8192; index++) {
            assertEquals(sequenceValues[index], index);
        }

        // Make sure it was divided up the way we expected.
        assertTrue(sequenceStarts.contains(0));
        assertTrue(sequenceEnds.contains(1638));
        assertTrue(sequenceStarts.contains(1639));
        assertTrue(sequenceEnds.contains(3277));
        assertTrue(sequenceStarts.contains(3278));
        assertTrue(sequenceEnds.contains(4916));
        assertTrue(sequenceStarts.contains(4917));
        assertTrue(sequenceEnds.contains(6555));
        assertTrue(sequenceStarts.contains(6556));
        assertTrue(sequenceEnds.contains(8191));
    }

    // Goofy thing for helping to characterize performance.
    @Test(enabled = false)
    public void testFindSweetSpot() throws ProcessingException {
        DiscreteSystemParallelExecutor executor = DiscreteSystemParallelExecutor.getInstance(1);
        SineWaveSignalGenerator generator = new SineWaveSignalGenerator(44100);
        // warm things up first
        System.out.println("Warming up HotSpot");
        for (int j = 0; j < 1000; j++) {
            long start = 0, stop = 0;
            for (int i = 1; i < 60; i++) {
                start = System.nanoTime();
                Sequence sequence = generator.generate(25, i, 32767, 0);
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
                    Sequence sequence = generator.generate(25, i / 120F, 32767, 0);
                    stop = System.nanoTime();
                    long dur = stop - start;
                    sum += dur;
                    length = sequence.getLength();
                }
                System.out.println("Sequence of " + length + " samples took avg " + (sum / 100) / 1000 + "us");
            }
        }
    }

}
