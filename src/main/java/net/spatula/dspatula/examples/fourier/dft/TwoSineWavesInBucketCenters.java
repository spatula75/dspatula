package net.spatula.dspatula.examples.fourier.dft;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.examples.sequence.AbstractComplexSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.Adder;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

/**
 * Demonstrates a DFT where the frequencies in use land precisely in the bucket centers.
 *
 *
 * @author spatula
 *
 */
public class TwoSineWavesInBucketCenters extends AbstractComplexSequenceChart {

    private static final long serialVersionUID = 1L;
    private static final int SAMPLE_FREQUENCY = 8000;
    private static final int SAMPLE_COUNT = 32;
    private static final double DURATION = SAMPLE_COUNT / (double) SAMPLE_FREQUENCY;

    protected TwoSineWavesInBucketCenters() throws ProcessingException {
        super("Sum of Sine Waves, DFT", "1000Hz + 2000Hz @ 8kHz", "frequency", "power");
    }

    @Override
    protected ComplexSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(SAMPLE_FREQUENCY);
        final RealSequence sequence1 = generator.generate(1000, DURATION, 16383, 0);
        final RealSequence sequence2 = generator.generate(2000, DURATION, 8191, 0);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Adder(), sequence1, sequence2);

        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence complexSequence = transformer.forward(sequence1);

        return complexSequence;
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
        new TwoSineWavesInBucketCenters().render();
    }

}
