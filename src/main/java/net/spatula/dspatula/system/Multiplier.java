package net.spatula.dspatula.system;

/**
 * Discrete System worker which simply multiplies the values of the second sequence by the first sequence, storing the result in the
 * first sequence. Implements the "Multiplication" operation from section 1.3 of Understanding DSP.
 *
 * @author spatula
 *
 */
public class Multiplier extends AbstractCongruentSystem {

    @Override
    protected void operate(final int start, final int end, final int[] destinationValues, final int[] addValues) {
        for (int index = start; index <= end; index++) {
            destinationValues[index] *= addValues[index];
        }
    }

}
