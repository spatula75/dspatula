package net.spatula.dspatula.system;

/**
 * Multiply all values of a Sequence by a constant multiple.
 *
 * @author spatula
 *
 */
public class ConstantMultiplier extends AbstractConstantSystem<Double> {

    public ConstantMultiplier(double value) {
        super(value);
    }

    @Override
    protected void operate(final int start, final int end, final int[] values) {
        for (int index = start; index <= end; index++) {
            values[index] *= value;
        }
    }

}
