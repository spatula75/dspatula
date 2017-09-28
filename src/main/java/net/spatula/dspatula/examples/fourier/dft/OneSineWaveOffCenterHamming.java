package net.spatula.dspatula.examples.fourier.dft;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.transform.fourier.discrete.DFTHammingSummationWorker;
import net.spatula.dspatula.transform.fourier.discrete.DiscreteFourierTransformer;

/**
 * Demonstrates a DFT where the frequency in use lands midway between two buckets, with a Hamming window to minimize the first lobe
 * leakage.
 *
 * @author spatula
 *
 */
public class OneSineWaveOffCenterHamming extends OneSineWaveOffCenter {

    private static final long serialVersionUID = 1L;

    protected OneSineWaveOffCenterHamming() throws ProcessingException {
        super("Off-center Sine Wave, Hamming Window");
    }

    @Override
    protected DiscreteFourierTransformer createDFTransformer() {
        return new DiscreteFourierTransformer(new DFTHammingSummationWorker());
    }

    public static void main(String[] args) throws Exception {
        new OneSineWaveOffCenterHamming().render();
    }

}
