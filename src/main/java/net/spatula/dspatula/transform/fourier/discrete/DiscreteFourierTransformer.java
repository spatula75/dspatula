package net.spatula.dspatula.transform.fourier.discrete;

import java.util.Arrays;
import java.util.List;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.concurrent.DiscreteSystemWorker;
import net.spatula.dspatula.concurrent.SummationParallelExecutor;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;

/**
 * Calculate Discrete Fourier Transforms in forward and reverse, in parallel on multiple cores where appropriate, of course.
 *
 * @author spatula
 *
 */
public class DiscreteFourierTransformer {

    /**
     * Calculate the forward DFT of a RealSequence, returning the result as a ComplexSequence containing both the real and imaginary
     * components of the frequency domain.
     *
     * @param sequence
     * @return
     * @throws ProcessingException
     */
    public ComplexSequence forward(RealSequence sequence) throws ProcessingException {
        final int points = sequence.getLength();
        final ComplexSequence result = new ComplexSequence(sequence.getLength());
        final int independentPoints = (points % 2 == 0) ? points / 2 + 1 : (points + 1) / 2;

        SummationParallelExecutor.getDefaultInstance().execute(new DFTSummationWorker(), Arrays.asList(sequence), result);

        // Symmetry
        // We don't need to bother with the sampleIndex math from above, because we're operating
        // on the new Frequency Domain sequence here, which always starts at 0 and has a number of points
        // equal to the length of the RealSequence on which we are operating.
        //
        // In other words, if the RealSequence has values that go from 0 to 1000, and we're operating on
        // the range from 100-199, of that RealSequence, then the length of the ComplexSequence here is
        // 100 and starts at 0.

        // Let's also do our symmetry in parallel on many cores. Why not.
        final ComplexSequence symmetricSequence = result.subsequence(independentPoints, result.getEnd());
        final DiscreteSystemParallelExecutor dsExecutor = DiscreteSystemParallelExecutor.getDefaultInstance();
        dsExecutor.execute(new DiscreteSystemWorker<ComplexSequence>() {

            @Override
            public void operate(List<ComplexSequence> sequences) {
                final ComplexSequence sequence = sequences.get(0);
                final int[] imaginaryValues = sequence.getImaginaryValues();
                final int[] realValues = sequence.getRealValues();
                final int start = sequence.getStart();
                final int end = sequence.getEnd();

                final int offset = (points % 2 == 0) ? 2 : 1;

                for (int pointNumber = start; pointNumber <= end; pointNumber++) {
                    final int fromIndex = independentPoints - (pointNumber - independentPoints) - offset;

                    // We're cheating the contract for DiscreteSystemWorker here by reaching into part of the
                    // array that's outside our subsequence.
                    imaginaryValues[pointNumber] = -1 * imaginaryValues[fromIndex];
                    realValues[pointNumber] = realValues[fromIndex];
                }
            }
        }, symmetricSequence);

        return result;
    }

}
