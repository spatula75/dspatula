package net.spatula.dspatula.system;

import net.spatula.dspatula.concurrent.DiscreteSystemWorker;
import net.spatula.dspatula.time.sequence.Sequence;

/**
 * Base class for Discrete Systems which apply a constant value to all elements of a Sequence.
 *
 * @author spatula
 *
 */
public abstract class AbstractConstantSystem<T> implements DiscreteSystemWorker {

    protected final T value;

    public AbstractConstantSystem(T value) {
        this.value = value;
    }

    @Override
    public void operate(Sequence... sequences) {
        final Sequence sequence = sequences[0];
        final int start = sequence.getStart();
        final int end = sequence.getEnd();
        final int[] values = sequence.getSequenceValues();

        operate(start, end, values);
    }

    protected abstract void operate(final int start, final int end, final int[] values);

}
