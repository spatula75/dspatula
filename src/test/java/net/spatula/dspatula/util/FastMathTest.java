package net.spatula.dspatula.util;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class FastMathTest {

    @Test
    public void testSineAccuracy() {
        final double frequency = 2.3D;
        final int rate = 192000;
        for (int i = 0; i < rate; i++) {
            final double radians = i * 2 * Math.PI * frequency / rate;
            final double javaMath = Math.sin(radians);
            final double fastMath = FastMath.sin(radians);
            final double difference = Math.abs(javaMath - fastMath);
            assertTrue(difference < 0.000001, "Failure at " + radians + ", difference=" + difference);
        }
    }

    @Test
    public void testCosineAccuracy() {
        final double frequency = 2.3D;
        final int rate = 192000;
        for (int i = 0; i < rate; i++) {
            final double radians = i * 2 * Math.PI * frequency / rate;
            final double javaMath = Math.cos(radians);
            final double fastMath = FastMath.cos(radians);
            final double difference = Math.abs(javaMath - fastMath);
            assertTrue(difference < 0.000001, "Failure at " + radians + ", difference=" + difference);
        }
    }

}
