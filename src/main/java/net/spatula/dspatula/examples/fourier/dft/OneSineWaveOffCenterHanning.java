package net.spatula.dspatula.examples.fourier.dft;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.transform.fourier.discrete.DFTHanningSummationWorker;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

/**
 * Demonstrates a DFT where the frequency in use lands midway between two buckets, with a Hanning window to minimize overall
 * leakage.
 *
 * @author spatula
 *
 */
public class OneSineWaveOffCenterHanning extends OneSineWaveOffCenter {

    private static final long serialVersionUID = 1L;

    protected OneSineWaveOffCenterHanning() throws ProcessingException {
        super("Off-center Sine Wave, Hanning Window");
    }

    @Override
    protected DiscreteFourierTransformer createDFTransformer() {
        return new DiscreteFourierTransformer(new DFTHanningSummationWorker());
    }

    public static void main(String[] args) throws Exception {
        new OneSineWaveOffCenterHanning().render();
    }

}
