package net.spatula.dspatula.system;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.spatula.dspatula.time.sequence.Sequence;

public class AdderTest {

    @Test
    public void testAdd() {
        final Adder adder = new Adder();
        final Sequence ones = new Sequence(10);
        final Sequence twos = new Sequence(10);

        new Filler(1).operate(ones);
        new Filler(2).operate(twos);

        adder.operate(ones, twos);

        for (int i = 0; i < 10; i++) {
            assertEquals(ones.getSequenceValues()[i], 3);
        }
    }
}
