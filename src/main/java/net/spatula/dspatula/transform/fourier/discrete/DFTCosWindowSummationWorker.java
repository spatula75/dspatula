package net.spatula.dspatula.transform.fourier.discrete;

import java.util.List;

import net.spatula.dspatula.concurrent.SummationWorker;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.util.FastMath;

/**
 * Perform DFT for a point, applying a cosine-based window to the Discrete-Time Sequence
 * 
 * @author spatula
 *
 */
public class DFTCosWindowSummationWorker implements SummationWorker<RealSequence, ComplexSequence> {

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
    public final void operate(int pointNumber, List<RealSequence> inputSequences, ComplexSequence outputSequence) {
        final RealSequence realSequence = inputSequences.get(0);
        final int samples = realSequence.getLength();
        final int points = samples;
        final int[] realValues = realSequence.getRealValues();
        final int[] resultReal = outputSequence.getRealValues();
        final int[] resultImaginary = outputSequence.getImaginaryValues();

        double realSum = 0;
        double imaginarySum = 0;
        final int sequenceStartIndex = realSequence.getStart(); // Don't move this line. See README.md.
        for (int sampleNumber = 0; sampleNumber < samples; sampleNumber++) {
            final int sampleIndex = sampleNumber + sequenceStartIndex;
            final int sampleNumberDup = sampleNumber; // See README.md under Chapter 3 for an explanation
            //@formatter:off
            realSum += (
                            realValues[sampleIndex]
                            * (offset - multiple * FastMath.cos(FastMath.TWO_PI * sampleNumber / samples))
                       )
                        * FastMath.cos(FastMath.TWO_PI * sampleNumberDup * pointNumber / points);

            imaginarySum -= (
                                realValues[sampleIndex]
                                * (offset - multiple * FastMath.cos(FastMath.TWO_PI * sampleNumber / samples))
                            )
                             * FastMath.sin(FastMath.TWO_PI * sampleNumberDup * pointNumber / points);
            //@formatter:on
        }
        resultReal[pointNumber] = (int) realSum;
        resultImaginary[pointNumber] = (int) imaginarySum;
    }
}
