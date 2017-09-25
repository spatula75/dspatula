package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.RealSequence;

public class SubtracterTest {

    @Test
    public void testSubtract() {
        final Subtracter subtracter = new Subtracter();
        final RealSequence ones = new RealSequence(10);
        final RealSequence twos = new RealSequence(10);

        new Filler(1).operate(Arrays.asList(ones));
        new Filler(2).operate(Arrays.asList(twos));

        subtracter.operate(Arrays.asList(ones, twos));

        for (int i = 0; i < 10; i++) {
            assertEquals(ones.getRealValues()[i], -1);
        }
    }
}
