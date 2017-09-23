package net.spatula.dspatula.signal.sine;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.Sequence;

public class SineWaveSignalGeneratorTest {

    @Test
    public void testConstruct() {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(44100);
        assertEquals(generator.getSampleRate(), 44100);
    }

    private void sineWave(int sampleRate, double frequency, int amplitude, double phaseOffset, int[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            final double value = amplitude * Math.sin(2 * Math.PI * frequency * i / sampleRate + phaseOffset);
            buffer[i] = (int) value;
        }
    }

    @Test
    public void testCreateSimpleSineWave() throws ProcessingException {
        final SineWaveSignalGenerator generator = new SineWaveSignalGenerator(44100);

        final Sequence generated = generator.generate(1000D, 1D, 32767, 0D);
        final int[] buffer = new int[44100];
        sineWave(44100, 1000D, 32767, 0D, buffer);

        final int[] generatedValues = generated.getSequenceValues();

        assertEquals(generatedValues.length, buffer.length);
        for (int i = 0; i < buffer.length; i++) {
            assertEquals(generatedValues[i], buffer[i], "Found the wrong value at index " + i);
        }
    }

}
