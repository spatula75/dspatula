package net.spatula.dspatula.time.sequence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents a Discrete-Time Signal Sequence of Complex numbers, with underlying arrays of the real parts and the imaginary parts.
 *
 * @author spatula
 *
 */
public class ComplexSequence extends AbstractSequence<ComplexSequence> {

    private final int[] imaginaryValues;
    private final int[] realValues;

    /**
     * Create a new Sequence with an empty buffer of length samples, initialized all to 0's.
     *
     * @param samples
     *            The length of the sequence in number of integer samples.
     */
    public ComplexSequence(int samples) {
        super(0, samples - 1, samples);
        imaginaryValues = new int[samples];
        realValues = new int[samples];
    }

    protected ComplexSequence(int[] realValues, int[] imaginaryValues, int start, int end) {
        super(start, end, (end - start) + 1);
        this.imaginaryValues = imaginaryValues;
        this.realValues = realValues;
    }

    /**
     * Return the underlying array of the imaginary components of the Discrete-Time Signal Sequence.
     *
     * @return
     */
    @SuppressFBWarnings("EI_EXPOSE_REP") // We accept the risk for the sake of performance
    public int[] getImaginaryValues() {
        return imaginaryValues;
    }

    /**
     * Return the underlying array of the real components of the Discrete-Time Signal Sequence.
     * 
     * @return
     */
    @SuppressFBWarnings("EI_EXPOSE_REP") // We accept the risk for the sake of performance
    public int[] getRealValues() {
        return realValues;
    }

    @Override
    public ComplexSequence subsequence(int start, int end) {
        return new ComplexSequence(realValues, imaginaryValues, start, end);
    }

}
