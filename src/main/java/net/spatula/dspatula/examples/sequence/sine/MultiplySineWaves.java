package net.spatula.dspatula.examples.sequence.sine;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.examples.sequence.AbstractRealSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.Multiplier;
import net.spatula.dspatula.time.sequence.RealSequence;

public class MultiplySineWaves extends AbstractRealSequenceChart {

    private static final long serialVersionUID = 1L;

    public MultiplySineWaves() throws ProcessingException {
        super("Multiplication of Sine Waves", "60Hz * 6Hz @ 8kHz", "sample number", "value");
    }

    @Override
    protected RealSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        final RealSequence seq60Hz = generator.generate(60, 0.075, 4096, 0);
        final RealSequence seq30Hz = generator.generate(6, 0.075, 8, 0);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Multiplier(), seq60Hz, seq30Hz);
        return seq60Hz; // now has had 30Hz multiplied to it.
    }

    public static void main(String[] args) throws ProcessingException {
        new MultiplySineWaves().render();
    }
}
