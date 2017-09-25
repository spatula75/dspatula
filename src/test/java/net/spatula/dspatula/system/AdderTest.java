package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.RealSequence;

public class AdderTest {

    @Test
    public void testAdd() {
        final Adder adder = new Adder();
        final RealSequence ones = new RealSequence(10);
        final RealSequence twos = new RealSequence(10);

        new Filler(1).operate(Arrays.asList(ones));
        new Filler(2).operate(Arrays.asList(twos));

        adder.operate(Arrays.asList(ones, twos));

        for (int i = 0; i < 10; i++) {
            assertEquals(ones.getRealValues()[i], 3);
        }
    }
}
