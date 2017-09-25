package net.spatula.dspatula.time.sequence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents a real number Discrete-Time Signal Sequence. This is mostly just a convenient wrapper around an integer buffer that
 * contains the actual signal sequence values, with some helpful bookkeeping.
 *
 * @author spatula
 *
 */
public class RealSequence extends AbstractSequence<RealSequence> {

    protected final int[] realValues;

    /**
     * Create a new Sequence with an empty buffer of length samples, initialized all to 0's.
     *
     * @param samples
     *            The length of the sequence in number of integer samples.
     */
    public RealSequence(int samples) {
        super(0, samples - 1, samples);
        realValues = new int[samples];
    }

    protected RealSequence(int[] realValues, int start, int end) {
        super(start, end, (end - start) + 1);
        this.realValues = realValues;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP") // We accept the risk for the sake of performance
    public int[] getRealValues() {
        return realValues;
    }

    @Override
    public RealSequence subsequence(int start, int end) {
        return new RealSequence(realValues, start, end);
    }

}
