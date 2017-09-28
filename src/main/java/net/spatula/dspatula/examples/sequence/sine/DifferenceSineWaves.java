package net.spatula.dspatula.examples.sequence.sine;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.examples.sequence.AbstractRealSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.Subtracter;
import net.spatula.dspatula.time.sequence.RealSequence;

public class DifferenceSineWaves extends AbstractRealSequenceChart {

    private static final long serialVersionUID = 1L;

    public DifferenceSineWaves() throws ProcessingException {
        super("Difference of Sine Waves", "60Hz - 30Hz @ 8kHz", "sample number", "value");
    }

    @Override
    protected RealSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        final RealSequence seq60Hz = generator.generate(60, 0.075, 16383, 0);
        final RealSequence seq30Hz = generator.generate(30, 0.075, 16383, 0);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Subtracter(), seq60Hz, seq30Hz);
        return seq60Hz; // now has had 30Hz subtracted from it.
    }

    public static void main(String[] args) throws ProcessingException {
        new DifferenceSineWaves().render();
    }

}
