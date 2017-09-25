package net.spatula.dspatula.transform.fourier.discrete;

import java.util.List;

import net.spatula.dspatula.concurrent.SummationWorker;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.util.FastMath;

public class DFTSummationWorker implements SummationWorker<RealSequence, ComplexSequence> {

    @Override
    public void operate(int pointNumber, List<RealSequence> inputSequences, ComplexSequence outputSequence) {
        final RealSequence realSequence = inputSequences.get(0);
        final int samples = realSequence.getLength();
        final int[] realValues = realSequence.getRealValues();
        final int[] resultReal = outputSequence.getRealValues();
        final int[] resultImaginary = outputSequence.getImaginaryValues();
        final int points = outputSequence.getLength();

        double realSum = 0;
        double imaginarySum = 0;
        for (int sampleNumber = 0; sampleNumber < samples; sampleNumber++) {
            final int sampleIndex = sampleNumber + realSequence.getStart();
            final int sampleValue = realValues[sampleIndex];
            realSum += sampleValue * FastMath.cos(FastMath.TWO_PI * sampleNumber * pointNumber / points);
            imaginarySum += -1 * sampleValue * FastMath.sin(FastMath.TWO_PI * sampleNumber * pointNumber / points);
        }
        resultReal[pointNumber] = (int) realSum;
        resultImaginary[pointNumber] = (int) imaginarySum;
    }

}
