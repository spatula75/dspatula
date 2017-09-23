package net.spatula.dspatula.examples.sequence.sine;

import net.spatula.dspatula.examples.sequence.AbstractSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.Sequence;

public class OffsetSineWave extends AbstractSequenceChart {

    private static final long serialVersionUID = 1L;

    public OffsetSineWave() throws ProcessingException {
        super("Sine Wave Offset 180 degrees", "60Hz @ 8kHz (offset 180 degrees)", "sample number", "value");
    }

    @Override
    protected Sequence getSequence() throws ProcessingException {
        SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        return generator.generate(60, 0.075, 32767, Math.PI);
    }
    
    public static void main(String[] args) throws ProcessingException {
        new OffsetSineWave().render();
    }

}