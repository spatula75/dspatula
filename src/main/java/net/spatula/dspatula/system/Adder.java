package net.spatula.dspatula.system;

/**
 * Discrete System worker which simply adds the values of the second sequence to the first sequence. Implements the "Addition"
 * operation from section 1.3 of Understanding DSP.
 *
 * @author spatula
 *
 */
public class Adder extends AbstractCongruentSystem {

    @Override
    protected void operate(final int start, final int end, final int[] destinationValues, final int[] addValues) {
        for (int index = start; index <= end; index++) {
            destinationValues[index] += addValues[index];
        }
    }

}
