package net.spatula.dspatula.signal.sine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import net.spatula.dspatula.concurrent.DiscreteSystemParallelExecutor;
import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.Sequence;

public class SineWaveSignalGenerator {

    private final int sampleRate;

    public SineWaveSignalGenerator(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Generate a sine wave.
     * 
     * @param frequency
     *            Frequency of the sine wave, in Hertz
     * @param duration
     *            Duration of the sine wave, in seconds.
     * @param phaseOffset
     *            The phase offset, given in radians (0 - 2pi)
     * @return A sine wave with the given frequency, duration, and phaseOffset for the sample rate at initialization.
     * 
     *         The number of samples in the Sequence will be rounded up to the next full sample.
     * @throws ProcessingException
     *             If errors are encountered during execution
     */
    public Sequence generate(double frequency, double duration, double phaseOffset) throws ProcessingException {
        // Do this calculation with BigDecimal, to avoid float error values like 44100.0000000001 getting rounded up to 44101.
        int sequenceLength = BigDecimal.valueOf(duration).multiply(BigDecimal.valueOf(sampleRate)).setScale(0, RoundingMode.CEILING)
                .intValue();
        Sequence sequence = new Sequence(sequenceLength);

        SineWaveWorker discreteSystemWorker = new SineWaveWorker(sampleRate, frequency, phaseOffset);
        DiscreteSystemParallelExecutor.getDefaultInstance().execute(discreteSystemWorker, sequence);
        
        return sequence;
    }

}
