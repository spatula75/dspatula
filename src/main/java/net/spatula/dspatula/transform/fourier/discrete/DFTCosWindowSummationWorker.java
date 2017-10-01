package net.spatula.dspatula.transform.fourier.discrete;

import java.util.List;

import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.util.FastMath;

/**
 * Perform DFT for a point, applying a cosine-based window to the Discrete-Time Sequence
 *
 * @author spatula
 *
 */
public class DFTCosWindowSummationWorker extends DFTSummationWorker {

    protected final double offset;
    protected final double multiple;

    /**
     * Create a window that applies an offset and multiple to a cosine-based window function using the equation offset - multiple *
     * cos(2 * pi * n / N)
     *
     * @param offset
     *            the constant window offset
     * @param multiple
     *            the (positive) constant multiplied by the cosine function
     */
    protected DFTCosWindowSummationWorker(double offset, double multiple) {
        this.offset = offset;
        this.multiple = multiple;
    }

    @Override
    public final void forward(int pointNumber, List<RealSequence> inputSequences, ComplexSequence outputSequence) {
        final RealSequence realSequence = inputSequences.get(0);
        final double samples = realSequence.getLength();
        final double points = samples;
        final int[] realValues = realSequence.getRealValues();
        final int[] resultReal = outputSequence.getRealValues();
        final int[] resultImaginary = outputSequence.getImaginaryValues();

        double realSum = 0;
        double imaginarySum = 0;
        for (int sampleNumber = 0; sampleNumber < samples; sampleNumber++) {
            /*
             * See the commentary in DFTSummationWorker for an explanation of the curious order of operations you see here.
             */
            final double dSampleNumber = sampleNumber; // Yes, really.
            final double analytic = sampleNumber * pointNumber;
            //@formatter:off
            realSum += (
                            realValues[sampleNumber]
                            * (offset - multiple * FastMath.cos(FastMath.TWO_PI * dSampleNumber / samples))
                       )
                        * FastMath.cos(FastMath.TWO_PI * analytic / points);

            imaginarySum -= (
                                realValues[sampleNumber]
                                * (offset - multiple * FastMath.cos(FastMath.TWO_PI * dSampleNumber / samples))
                            )
                             * FastMath.sin(FastMath.TWO_PI * analytic / points);
            //@formatter:on
        }
        resultReal[pointNumber] = (int) realSum;
        resultImaginary[pointNumber] = (int) imaginarySum;
    }
}
