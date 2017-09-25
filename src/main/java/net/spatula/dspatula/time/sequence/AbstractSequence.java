package net.spatula.dspatula.time.sequence;

public abstract class AbstractSequence<T extends Sequence<T>> implements Sequence<T> {

    protected final int start;
    protected final int end;
    protected final int length;

    public AbstractSequence(int start, int end, int length) {
        this.start = start;
        this.end = end;
        this.length = length;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public abstract T subsequence(int start, int end);

}