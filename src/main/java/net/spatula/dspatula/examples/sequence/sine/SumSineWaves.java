package net.spatula.dspatula.examples.sequence.sine;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.examples.sequence.AbstractRealSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.Adder;
import net.spatula.dspatula.time.sequence.RealSequence;

public class SumSineWaves extends AbstractRealSequenceChart {

    private static final long serialVersionUID = 1L;

    public SumSineWaves() throws ProcessingException {
        super("Sum of Sine Waves", "60Hz + 30Hz @ 8kHz", "sample number", "value");
    }

    @Override
    protected RealSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        final RealSequence seq60Hz = generator.generate(60, 0.075, 16383, 0);
        final RealSequence seq30Hz = generator.generate(30, 0.075, 16383, 0);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Adder(), seq60Hz, seq30Hz);
        return seq60Hz; // now has had 30Hz added to it.
    }

    public static void main(String[] args) throws ProcessingException {
        new SumSineWaves().render();
    }

}
