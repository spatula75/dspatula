package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.RealSequence;

public class ConstantMultiplierTest {

    @Test
    public void testMultiply() {
        final ConstantMultiplier multiplier = new ConstantMultiplier(.5);
        final RealSequence twos = new RealSequence(10);

        new Filler(2).operate(Arrays.asList(twos));

        multiplier.operate(Arrays.asList(twos));

        for (int i = 0; i < 10; i++) {
            assertEquals(twos.getRealValues()[i], 1);
        }
    }
}
