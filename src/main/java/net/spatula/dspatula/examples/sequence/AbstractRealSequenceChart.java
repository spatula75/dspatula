package net.spatula.dspatula.examples.sequence;

import org.jfree.data.xy.XYSeries;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.RealSequence;

public abstract class AbstractRealSequenceChart extends AbstractSequenceChart<RealSequence> {

    private static final long serialVersionUID = 1L;

    protected AbstractRealSequenceChart(String title, String plotName, String xAxisTitle, String yAxisTitle)
            throws ProcessingException {
        super(title, plotName, xAxisTitle, yAxisTitle);
    }

    @Override
    protected void populateSeries(final XYSeries series) throws ProcessingException {
        final RealSequence sequence = getSequence();

        for (int i = 0; i < sequence.getRealValues().length; i++) {
            final int value = sequence.getRealValues()[i];
            series.add(i, value);
        }
    }

}
