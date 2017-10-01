package net.spatula.dspatula.concurrent;

import java.util.List;

import net.spatula.dspatula.time.sequence.Sequence;

/**
 * Interface to be implemented by a class which will actually operate on a Discrete-Time Signal Sequence or Sequences.
 *
 * Whether it is one sequence or multiple depends on the type of operation being performed.
 *
 * The Sequences themselves know their start and end points; care should be taken in implementation because the data contained in
 * the Sequence might be considerably larger than the range on which the worker is being asked to operate; that is to say, this
 * Sequence might be a sub-Sequence of a larger sequence, and this worker is being asked to operate on only one part of it.
 *
 * Implementations must consider thread safety; the same class will be run in multiple threads and probably on multiple cores.
 *
 * @author spatula
 *
 */
public interface SummationWorker<T extends Sequence<T>, V extends Sequence<V>> {

    void forward(int index, List<T> inputSequences, V outputSequence);

    void inverse(int index, List<V> inputSequences, T outputSequence);

}
