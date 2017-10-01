package net.spatula.dspatula.examples.fourier.dft;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.examples.sequence.AbstractRealSequenceChart;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.signal.sine.SineWaveSignalGenerator;
import net.spatula.dspatula.system.Adder;
import net.spatula.dspatula.time.sequence.ComplexSequence;
import net.spatula.dspatula.time.sequence.RealSequence;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

public class ForwardThenInverseDual extends AbstractRealSequenceChart {

    private static final long serialVersionUID = 1L;

    public ForwardThenInverseDual() throws ProcessingException {
        super("Forward, then inversed DFT two sine waves", "60Hz + 30Hz @ 8kHz", "sample number", "value");
    }

    @Override
    protected RealSequence getSequence() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(8000);
        final RealSequence seq60Hz = generator.generate(60, 0.075, 16383, 0);
        final RealSequence seq30Hz = generator.generate(30, 0.075, 16383, 0);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(new Adder(), seq60Hz, seq30Hz);
        final DiscreteFourierTransformer transformer = new DiscreteFourierTransformer();
        final ComplexSequence complexSequence = transformer.forward(seq60Hz);
        return transformer.inverse(complexSequence);
    }

    public static void main(String[] args) throws ProcessingException {
        new ForwardThenInverseDual().render();
    }

}
