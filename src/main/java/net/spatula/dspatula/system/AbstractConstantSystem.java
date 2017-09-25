package net.spatula.dspatula.system;

import java.util.List;

import net.spatula.dspatula.concurrent.DiscreteSystemWorker;
import net.spatula.dspatula.time.sequence.RealSequence;

/**
 * Base class for Discrete Systems which apply a constant value to all elements of a Sequence.
 *
 * @author spatula
 *
 */
public abstract class AbstractConstantSystem<T> implements DiscreteSystemWorker<RealSequence> {

    protected final T value;

    public AbstractConstantSystem(T value) {
        this.value = value;
    }

    @Override
    public void operate(List<RealSequence> sequences) {
        final RealSequence sequence = sequences.get(0);
        final int start = sequence.getStart();
        final int end = sequence.getEnd();
        final int[] values = sequence.getRealValues();

        operate(start, end, values);
    }

    protected abstract void operate(final int start, final int end, final int[] values);

}
