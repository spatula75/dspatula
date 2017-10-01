package net.spatula.dspatula.transform.fourier.discrete;

/**
 * Perform DFT for a point, applying a Hanning window to the Discrete-Time Sequence
 *
 * @author spatula
 *
 */
public class DFTHanningSummationWorker extends DFTCosWindowSummationWorker {

    public DFTHanningSummationWorker() {
        super(0.5D, 0.5D);
    }

}
