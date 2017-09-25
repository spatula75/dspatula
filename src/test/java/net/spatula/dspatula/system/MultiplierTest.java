package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.RealSequence;

public class MultiplierTest {

    @Test
    public void testMultiply() {
        final Multiplier multiplier = new Multiplier();
        final RealSequence threes = new RealSequence(10);
        final RealSequence twos = new RealSequence(10);

        new Filler(3).operate(Arrays.asList(threes));
        new Filler(2).operate(Arrays.asList(twos));

        multiplier.operate(Arrays.asList(threes, twos));

        for (int i = 0; i < 10; i++) {
            assertEquals(threes.getRealValues()[i], 6);
        }
    }
}
