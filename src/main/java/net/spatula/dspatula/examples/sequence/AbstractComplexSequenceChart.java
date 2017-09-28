package net.spatula.dspatula.examples.sequence;

import org.jfree.data.xy.XYSeries;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.ComplexSequence;

public abstract class AbstractComplexSequenceChart extends AbstractSequenceChart<ComplexSequence> {

    private static final long serialVersionUID = 1L;

    protected AbstractComplexSequenceChart(String title, String plotName, String xAxisTitle, String yAxisTitle)
            throws ProcessingException {
        super(title, plotName, xAxisTitle, yAxisTitle);
    }

    /**
     * Default implementation is to populate each element with the power of the point, which is the hypotenuse of the right triangle
     * given by the coordinates of the real and imaginary values from (0,0); in other words, the square root of the sum of the
     * squares of the two values.
     */
    @Override
    protected void populateSeries(final XYSeries series) throws ProcessingException {
        final ComplexSequence sequence = getSequence();

        final int[] realValues = sequence.getRealValues();
        final int[] imaginaryValues = sequence.getImaginaryValues();
        final double powerZero = Math.sqrt(Math.pow(realValues[0], 2) + Math.pow(imaginaryValues[0], 2));

        for (int i = 0; i < realValues.length; i++) {
            // See equation 3-31
            final double value = Math.sqrt(Math.pow(realValues[i], 2) + Math.pow(imaginaryValues[i], 2));
            final double dBvalue = 20 * Math.log10(value / powerZero);
            series.add(getXValue(i), dBvalue);
        }
    }

    protected abstract double getXValue(int i);

}
