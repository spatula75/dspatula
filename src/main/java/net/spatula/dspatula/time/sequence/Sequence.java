package net.spatula.dspatula.time.sequence;

public interface Sequence<T extends Sequence<T>> {
    int getStart();

    int getEnd();

    int getLength();

    T subsequence(int start, int end);
}
