package net.spatula.dspatula.examples.sequence.sine;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.examples.sequence.AbstractSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.ConstantMultiplier;
import net.spatula.dspatula.time.sequence.Sequence;

public class ConstantMultiplySineWaves extends AbstractSequenceChart {

    private static final long serialVersionUID = 1L;

    public ConstantMultiplySineWaves() throws ProcessingException {
        super("Multiplication of Sine Wave", "60Hz @ 32767 * 0.25 @ 8kHz", "sample number", "value");
    }

    @Override
    protected Sequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        final Sequence seq60Hz = generator.generate(60, 0.075, 32767, 0);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new ConstantMultiplier(.25D), seq60Hz);
        return seq60Hz; // now divided by 4
    }

    public static void main(String[] args) throws ProcessingException {
        new ConstantMultiplySineWaves().render();
    }
}
