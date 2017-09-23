package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.Sequence;

public class MultiplierTest {

    @Test
    public void testMultiply() {
        final Multiplier multiplier = new Multiplier();
        final Sequence threes = new Sequence(10);
        final Sequence twos = new Sequence(10);

        new Filler(3).operate(threes);
        new Filler(2).operate(twos);

        multiplier.operate(threes, twos);

        for (int i = 0; i < 10; i++) {
            assertEquals(threes.getSequenceValues()[i], 6);
        }
    }
}
