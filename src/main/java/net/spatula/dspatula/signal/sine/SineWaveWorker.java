package net.spatula.dspatula.signal.sine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.spatula.dspatula.concurrent.DiscreteSystemWorker;
import net.spatula.dspatula.time.sequence.Sequence;
import net.spatula.dspatula.util.FastMath;

public final class SineWaveWorker implements DiscreteSystemWorker {

    private final int sampleRate;
    private final double phaseOffset;
    private final double frequency;
    private final int amplitude;

    private static final Logger LOG = LoggerFactory.getLogger(SineWaveWorker.class);

    public SineWaveWorker(int sampleRate, double frequency, int amplitude, double phaseOffset) {
        this.frequency = frequency;
        this.phaseOffset = phaseOffset;
        this.sampleRate = sampleRate;
        this.amplitude = amplitude;
    }

    @Override
    public void operate(Sequence... sequences) {
        final Sequence sequence = sequences[0];
        final int[] sequenceValues = sequence.getSequenceValues();

        LOG.trace("Building sine wave from sample {} to {}", sequence.getStart(), sequence.getEnd());

        for (int sampleNumber = sequence.getStart(); sampleNumber <= sequence.getEnd(); sampleNumber++) {
            sequenceValues[sampleNumber] = (int) (amplitude
                    * FastMath.sin(2 * Math.PI * frequency * sampleNumber / sampleRate + phaseOffset));
        }
    }

}
