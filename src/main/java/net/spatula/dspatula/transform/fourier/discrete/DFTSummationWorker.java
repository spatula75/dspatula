package net.spatula.dspatula.transform.fourier.discrete;

import java.util.List;

import net.spatula.dspatula.concurrent.SummationWorker;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.util.FastMath;

/**
 * Perform DFT for a point, applying no window to the Discrete-Time sequence.
 * 
 * @author spatula
 *
 */
public class DFTSummationWorker implements SummationWorker<RealSequence, ComplexSequence> {

    @Override
    public void operate(int pointNumber, List<RealSequence> inputSequences, ComplexSequence outputSequence) {
        final RealSequence realSequence = inputSequences.get(0);
        final int samples = realSequence.getLength();
        final int[] realValues = realSequence.getRealValues();
        final int[] resultReal = outputSequence.getRealValues();
        final int[] resultImaginary = outputSequence.getImaginaryValues();

        double realSum = 0;
        double imaginarySum = 0;
        final int sequenceStartIndex = realSequence.getStart();
        for (int sampleNumber = 0; sampleNumber < samples; sampleNumber++) {
            final int sampleIndex = sampleNumber + sequenceStartIndex;
            final int sampleNumberDup = sampleNumber;
            //@formatter:off
            realSum += (
                            realValues[sampleIndex]
                       )
                        * FastMath.cos(FastMath.TWO_PI * sampleNumberDup * pointNumber / samples);

            imaginarySum -= (
                                realValues[sampleIndex]
                            )
                             * FastMath.sin(FastMath.TWO_PI * sampleNumberDup * pointNumber / samples);
            //@formatter:on
        }
        resultReal[pointNumber] = (int) realSum;
        resultImaginary[pointNumber] = (int) imaginarySum;
    }
}
