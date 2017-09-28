package net.spatula.dspatula.transform.fourier.discrete;

/**
 * Perform DFT for a point, applying a Hamming window to the Discrete-Time Sequence
 * 
 * @author spatula
 *
 */
public class DFTHammingSummationWorker extends DFTCosWindowSummationWorker {

    public DFTHammingSummationWorker() {
        super(0.54D, 0.46D);
    }

}
