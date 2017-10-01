package net.spatula.dspatula.examples.sequence;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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

        double peakPower = -Double.MIN_VALUE;
        double minPower = Double.MAX_VALUE;
        for (int i = 0; i < realValues.length; i++) {
            final double pointPower = Math.sqrt(Math.pow(realValues[i], 2) + Math.pow(imaginaryValues[i], 2));
            if (pointPower > peakPower) {
                peakPower = pointPower;
            }
            if (pointPower < minPower) {
                minPower = pointPower;
            }
        }

        final double minDbValue = 20 * Math.log10(minPower / peakPower);

        for (int i = 0; i < realValues.length; i++) {
            // See equation 3-31. Note that W(0) for purposes of that section refer to the peak power of the main lobe
            // not power at bin #0, which is why we calculated peak power just above this loop.
            final double value = Math.sqrt(Math.pow(realValues[i], 2) + Math.pow(imaginaryValues[i], 2));
            final double dBvalue = 20 * Math.log10(value / peakPower);
            series.add(getXValue(i), -minDbValue + dBvalue);
        }
    }

    @Override
    protected JFreeChart createPlot(final XYSeriesCollection collection) {
        final JFreeChart chart = ChartFactory.createXYBarChart(plotName, xAxisTitle, false, yAxisTitle, collection,
                PlotOrientation.VERTICAL, false, false, false);
        XYBarRenderer.class.cast(chart.getXYPlot().getRenderer()).setBarPainter(new StandardXYBarPainter());

        chart.getXYPlot();

        return chart;
    }

    protected abstract double getXValue(int i);

}
