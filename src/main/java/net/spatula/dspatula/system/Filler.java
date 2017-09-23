package net.spatula.dspatula.system;

/**
 * Does nothing but fill a sequence with a particular value.
 *
 * @author spatula
 *
 */
public class Filler extends AbstractConstantSystem<Integer> {

    public Filler(int value) {
        super(value);
    }

    @Override
    protected void operate(final int start, final int end, final int[] values) {
        for (int index = start; index <= end; index++) {
            values[index] = value;
        }
    }

}
