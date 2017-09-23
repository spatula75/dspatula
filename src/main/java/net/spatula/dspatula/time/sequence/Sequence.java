package net.spatula.dspatula.time.sequence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents a Discrete-Time Signal Sequence. This is mostly just a convenient wrapper around
 * an integer buffer that contains the actual signal sequence values, with some helpful bookkeeping.
 * @author spatula
 *
 */
public class Sequence {

    private final int[] sequenceValues;
    private final int start;
    private final int end;
    private final int length;
    
    /**
     * Create a new Sequence with an empty buffer of length samples, initialized all to 0's.
     * @param samples The length of the sequence in number of integer samples.
     */
    public Sequence(int samples) {
        sequenceValues = new int[samples];
        start = 0;
        end = samples - 1;
        length = samples;
    }

    protected Sequence(int[] sequenceValues, int start, int end) {
        this.sequenceValues = sequenceValues;
        this.start = start;
        this.end = end;
        this.length = (end - start) + 1;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP") // We accept the risk for the sake of performance
    public int[] getSequenceValues() {
        return sequenceValues;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return length;
    }

    public Sequence subsequence(int start, int end) {
        return new Sequence(sequenceValues, start, end);
    }

}
