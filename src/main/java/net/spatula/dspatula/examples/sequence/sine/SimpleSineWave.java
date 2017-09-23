package net.spatula.dspatula.examples.sequence.sine;

import net.spatula.dspatula.examples.sequence.AbstractSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.Sequence;

public class SimpleSineWave extends AbstractSequenceChart {

    private static final long serialVersionUID = 1L;

    public SimpleSineWave() throws ProcessingException {
        super("Simple Sine Wave", "60Hz @ 8kHz", "sample number", "value");
    }

    @Override
    protected Sequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        return generator.generate(60, 0.075, 32767, 0);
    }

    public static void main(String[] args) throws ProcessingException {
        new SimpleSineWave().render();
    }

}
