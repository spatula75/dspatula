package net.spatula.dspatula.system;

/**
 * Discrete System worker which simply subtracts the values of the second sequence to the first sequence. Implements the
 * "Subtraction" operation from section 1.3 of Understanding DSP.
 *
 * @author spatula
 *
 */
public class Subtracter extends AbstractCongruentSystem {

    @Override
    protected void operate(final int start, final int end, final int[] destinationValues, final int[] operandValues) {
        for (int index = start; index <= end; index++) {
            destinationValues[index] -= operandValues[index];
        }
    }

}
