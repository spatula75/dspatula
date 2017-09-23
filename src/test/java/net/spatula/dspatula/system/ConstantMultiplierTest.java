package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.Sequence;

public class ConstantMultiplierTest {

    @Test
    public void testMultiply() {
        final ConstantMultiplier multiplier = new ConstantMultiplier(.5);
        final Sequence twos = new Sequence(10);

        new Filler(2).operate(twos);

        multiplier.operate(twos);

        for (int i = 0; i < 10; i++) {
            assertEquals(twos.getSequenceValues()[i], 1);
        }
    }
}
