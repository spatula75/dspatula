package net.spatula.dspatula.transform.fourier.discrete;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.Adder;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.util.FastMath;

public class DiscreteFourierTransformerTest {

    // This is example 3.1.1 right out of the book, except that since we're dealing
    // with integers, we're using amplitudes of 10000 and 5000 for the two sine waves
    @Test
    public void testSimpleForward() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);

        // Generate 8 points of a 1000 Hz signal at 8000 samples per second.
        final RealSequence real1kHz = generator.generate(1000, 8 / 8000D, 10000, 0);
        final RealSequence real2kHz = generator.generate(2000, 8 / 8000D, 5000, (3 * FastMath.PI / 4D));
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Adder(), real1kHz, real2kHz);

        // real1kHz has been summed with real2kHz at this point.
        assertEquals(real1kHz.getLength(), 8);

        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence frequencyDomainSequence = transformer.forward(real1kHz);
        final int[] realValues = frequencyDomainSequence.getRealValues();
        final int[] imaginaryValues = frequencyDomainSequence.getImaginaryValues();
        assertEquals(realValues[1], 0);
        assertEquals(imaginaryValues[1], -9999); // not exactly the "4" from the book, but there's some rounding error.
                                                 // also we are scaling our values, so we need to divide by 4.

        assertEquals(realValues[2], 3535);
        assertEquals(imaginaryValues[2], 3535);

        assertEquals(realValues[3], 0);
        assertEquals(imaginaryValues[3], 0);

        assertEquals(realValues[4], 0);
        assertEquals(imaginaryValues[4], 0);

        assertEquals(realValues[5], 0);
        assertEquals(imaginaryValues[5], 0);

        assertEquals(realValues[6], 3535);
        assertEquals(imaginaryValues[6], -3535);

        assertEquals(realValues[7], 0);
        assertEquals(imaginaryValues[7], 9999);
    }

    @Test
    public void testForwardInverse() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);

        // Generate 8 points of a 1000 Hz signal at 8000 samples per second.
        final RealSequence real1kHz = generator.generate(1000, 8 / 8000D, 10000, 0);
        final RealSequence real2kHz = generator.generate(2000, 8 / 8000D, 5000, (3 * FastMath.PI / 4D));
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Adder(), real1kHz, real2kHz);

        // real1kHz has been summed with real2kHz at this point.
        assertEquals(real1kHz.getLength(), 8);

        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence frequencyDomainSequence = transformer.forward(real1kHz);
        final RealSequence inverse = transformer.inverse(frequencyDomainSequence);

        for (int i = 0; i < inverse.getLength(); i++) {
            assertTrue(Math.abs(inverse.getRealValues()[i] - real1kHz.getRealValues()[i]) <= 1); // account for rounding error
        }

    }

    @Test
    public void testSimpleForwardOddSymmetry() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(9000);

        // Generate 8 points of a 1000 Hz signal at 8000 samples per second.
        final RealSequence real1kHz = generator.generate(1000, 9 / 9000D, 10000, 0);
        final RealSequence real2kHz = generator.generate(2000, 9 / 9000D, 5000, (3 * FastMath.PI / 4D));
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Adder(), real1kHz, real2kHz);

        // real1kHz has been summed with real2kHz at this point.
        assertEquals(real1kHz.getLength(), 9);

        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence frequencyDomainSequence = transformer.forward(real1kHz);
        final int[] imaginaryValues = frequencyDomainSequence.getImaginaryValues();

        assertEquals(imaginaryValues[1], -1 * imaginaryValues[8]);
        assertEquals(imaginaryValues[2], -1 * imaginaryValues[7]);
    }

    @Test(enabled = false)
    public void testBigDft() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(22050);

        final RealSequence real1kHz = generator.generate(1000, 1, 1000, 0);
        assertEquals(real1kHz.getLength(), 22050);

        final int trials = 3;

        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer(new DFTSummationWorker());
        int min = Integer.MAX_VALUE;
        int max = 0;
        int sum = 0;
        for (int i = 0; i < trials; i++) {
            final long start = System.currentTimeMillis();
            final ComplexSequence frequencyDomainSequence = transformer.forward(real1kHz);
            final long end = System.currentTimeMillis();
            System.out.println("22050 point DFT took " + (end - start) + "ms");
            sum += end - start;
            if (end - start < min) {
                min = (int) (end - start);
            }
            if (end - start > max) {
                max = (int) (end - start);
            }
        }
        System.out.println("Min execution time " + min);
        System.out.println("Max execution time " + max);
        System.out.println("Avg execution time " + sum / trials);
    }

}
