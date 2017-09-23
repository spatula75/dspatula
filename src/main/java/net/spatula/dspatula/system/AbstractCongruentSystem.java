package net.spatula.dspatula.system;

import net.spatula.dspatula.concurrent.DiscreteSystemWorker;
import net.spatula.dspatula.time.sequence.Sequence;

/**
 * Base class for Discrete Systems which take as inputs two signals of equal length.
 *
 * Note that the 'for' loop is not included in the 'operate' method for the sake of performance (to avoid a method call for every
 * iteration of the loop).
 *
 * @author spatula
 *
 */
public abstract class AbstractCongruentSystem implements DiscreteSystemWorker {

    @Override
    public void operate(Sequence... sequences) {
        final Sequence first = sequences[0];
        final Sequence second = sequences[1];
        final int start = first.getStart();
        final int end = first.getEnd();

        final int[] destinationValues = first.getSequenceValues();
        final int[] addValues = second.getSequenceValues();
        operate(start, end, destinationValues, addValues);
    }

    protected abstract void operate(final int start, final int end, final int[] destinationValues, final int[] operandValues);

}
