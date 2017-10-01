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
    public void forward(int pointNumber, List<RealSequence> inputSequences, ComplexSequence outputSequence) {
        final RealSequence realSequence = inputSequences.get(0);
        final double samples = realSequence.getLength();
        final double scale = samples / 2D;
        final int[] realValues = realSequence.getRealValues();
        final int[] resultReal = outputSequence.getRealValues();
        final int[] resultImaginary = outputSequence.getImaginaryValues();

        double realSum = 0;
        double imaginarySum = 0;
        for (int sampleNumber = 0; sampleNumber < samples; sampleNumber++) {
            /*
             * It's faster to multiply together the sampleNumber and pointNumber here, and assign them to a double which we'll use
             * twice later, than it is to multiply together the sampleNumber, pointNumber, AND TWO_PI and use that value twice
             * later.
             *
             * Speculation: operations involving multiple doubles might be handled by the FPU simultaneously, maybe cached, even to
             * the extent that doing the multiplication and division twice is cheaper than doing the operation once AND paying the
             * price of assignment to a double here. But introducing integers into double math is even worse than assignment, so
             * we're better off paying the assignment price once here to perform inlined, purely-double math later.
             *
             * So here we're calculating the radians of the analytic frequency without TWO_PI, and multiplying TWO_PI inline later.
             */
            final double analytic = sampleNumber * pointNumber;
            realSum += realValues[sampleNumber] * FastMath.cos(FastMath.TWO_PI * analytic / samples); // See note above.
            imaginarySum -= realValues[sampleNumber] * FastMath.sin(FastMath.TWO_PI * analytic / samples);
        }

        /*
         * It turns out that we want to apply the scale here, while we still have a double value, rather than later, after we've
         * quantized to an int, because otherwise we will experience artifacts from the quantization if we later perform an inverse
         * DFT. It looks like the reason for this is would end up quantizing twice: once to an integer value here, and then again
         * after performing the division on an integer value. If we scale the values here, we will quantize only once, to the
         * integer values in the real array.
         */
        resultReal[pointNumber] = (int) (realSum / scale);
        resultImaginary[pointNumber] = (int) (imaginarySum / scale);
    }

    @Override
    public void inverse(int pointNumber, List<ComplexSequence> inputSequences, RealSequence outputSequence) {
        final ComplexSequence complexSequence = inputSequences.get(0);
        final double samples = complexSequence.getLength();
        final int[] realValues = outputSequence.getRealValues();
        final int[] inputReal = complexSequence.getRealValues();
        final int[] inputImaginary = complexSequence.getImaginaryValues();

        double realSum = 0;
        for (int sampleNumber = 0; sampleNumber < samples; sampleNumber++) {
            final double analytic = sampleNumber * pointNumber;
            // (a + jb)(c + jd) = ac - bd + j(ad + cb)
            // Here, c = cos(2 * pi * mn / N) and d = sin(2 * pi * mn / N)
            // and a = the sample's real value, b is the sample's imaginary value.
            // For the inverse DFT, the j components should cancel out. In practice
            // they would actually work out to be very, very small numbers due to rounding error.
            //@formatter:off
            realSum +=  inputReal[sampleNumber] * FastMath.cos(FastMath.TWO_PI * analytic / samples)
                      - inputImaginary[sampleNumber] * FastMath.sin(FastMath.TWO_PI * analytic / samples);
            //@formatter:on
        }
        realValues[pointNumber] = (int) (realSum / 2D);
    }
}
