package net.spatula.dspatula.examples.fourier.dft;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import net.spatula.dspatula.examples.sequence.AbstractComplexSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

/**
 * Demonstrates a DFT where the frequency in use lands midway between two buckets.
 *
 * This shows the DFT Leakage described in section 3.8 of Understanding DSP.
 *
 * @author spatula
 *
 */
public class OneSineWaveOffCenter extends AbstractComplexSequenceChart {

    private static final long serialVersionUID = 1L;
    private static final int SAMPLE_FREQUENCY = 8000;
    private static final int SAMPLE_COUNT = 32;
    private static final double DURATION = SAMPLE_COUNT / (double) SAMPLE_FREQUENCY;

    protected OneSineWaveOffCenter(String title) throws ProcessingException {
        super(title, "1125Hz @ 8kHz", "frequency", "power");
    }

    @Override
    protected ComplexSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(SAMPLE_FREQUENCY);
        final RealSequence sequence1 = generator.generate(1125, DURATION, 16383, 0);

        final DiscreteFourierTransformer transformer = createDFTransformer();
        final ComplexSequence complexSequence = transformer.forward(sequence1);

        return complexSequence;
    }

    protected DiscreteFourierTransformer createDFTransformer() {
        return new DiscreteFourierTransformer();
    }

    @Override
    protected double getXValue(int pointNumber) {
        return (SAMPLE_FREQUENCY * pointNumber) / SAMPLE_COUNT;
    }

    @Override
    protected JFreeChart createPlot(final XYSeriesCollection collection) {
        return ChartFactory.createXYBarChart(plotName, xAxisTitle, false, yAxisTitle, collection, PlotOrientation.VERTICAL, false,
                false, false);
    }

    public static void main(String[] args) throws Exception {
        new OneSineWaveOffCenter("Off-center Sine Wave DFT, no window").render();
    }

}
