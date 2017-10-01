package net.spatula.dspatula.examples.fourier.dft;

import net.spatula.dspatula.examples.sequence.AbstractRealSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

public class ForwardThenInverseSingle extends AbstractRealSequenceChart {

    private static final long serialVersionUID = 1L;

    public ForwardThenInverseSingle() throws ProcessingException {
        super("Forward, then inversed DFT sine wave", "60Hz @ 8kHz", "sample number", "value");
    }

    @Override
    protected RealSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        final RealSequence sequence = generator.generate(60, 0.075, 32767, 0);
        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence complexSequence = transformer.forward(sequence);
        return transformer.inverse(complexSequence);
    }

    public static void main(String[] args) throws ProcessingException {
        new ForwardThenInverseSingle().render();
    }

}
