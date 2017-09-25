package net.spatula.dspatula.system;

import java.util.List;

import net.spatula.dspatula.concurrent.DiscreteSystemWorker;
import net.spatula.dspatula.time.sequence.RealSequence;

/**
 * Base class for Discrete Systems which take as inputs two signals of equal length.
 *
 * Note that the 'for' loop is not included in the 'operate' method for the sake of performance (to avoid a method call for every
 * iteration of the loop).
 *
 * @author spatula
 *
 */
public abstract class AbstractCongruentSystem implements DiscreteSystemWorker<RealSequence> {

    @Override
    public void operate(List<RealSequence> sequences) {
        final RealSequence first = sequences.get(0);
        final RealSequence second = sequences.get(1);
        final int start = first.getStart();
        final int end = first.getEnd();

        final int[] destinationValues = first.getRealValues();
        final int[] addValues = second.getRealValues();
        operate(start, end, destinationValues, addValues);
    }

    protected abstract void operate(final int start, final int end, final int[] destinationValues, final int[] operandValues);

}
