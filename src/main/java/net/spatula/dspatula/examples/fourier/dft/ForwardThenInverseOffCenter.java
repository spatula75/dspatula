package net.spatula.dspatula.examples.fourier.dft;

import net.spatula.dspatula.examples.sequence.AbstractRealSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

/**
 * Shows an inverse DFT on a signal that fell between two bins when doing a forward DFT.
 *
 * We will still recover the original signal because math.
 *
 * @author spatula
 *
 */
public class ForwardThenInverseOffCenter extends AbstractRealSequenceChart {

    private static final long serialVersionUID = 1L;
    private static final int SAMPLE_FREQUENCY = 8000;

    protected ForwardThenInverseOffCenter(String title) throws ProcessingException {
        super(title, "70Hz @ 8kHz", "frequency", "power");
    }

    @Override
    protected RealSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(SAMPLE_FREQUENCY);
        final RealSequence sequence1 = generator.generate(70, 0.075, 16383, 0);

        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence complexSequence = transformer.forward(sequence1);

        return transformer.inverse(complexSequence);
    }

    public static void main(String[] args) throws Exception {
        new ForwardThenInverseOffCenter("Off-center Sine Wave DFT, no window").render();
    }

}
