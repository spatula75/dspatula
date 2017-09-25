package net.spatula.dspatula.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.concurrent.ThreadPoolExecutor;

import org.testng.annotations.Test;

public class CoreAwareParallelExecutorTest {
    @Test
    public void testConstruct() {
        final CoreAwareParallelExecutor instance = CoreAwareParallelExecutor.getInstance();
        final CoreAwareParallelExecutor instance2 = CoreAwareParallelExecutor.getInstance();
        assertSame(instance2, instance);
    }

    @Test
    public void testCoreCount() {
        final CoreAwareParallelExecutor executor = new CoreAwareParallelExecutor(2);
        assertEquals(2, executor.cores);
    }

    @Test
    public void testPoolSize() {
        final CoreAwareParallelExecutor executor = new CoreAwareParallelExecutor(2);
        assertEquals(ThreadPoolExecutor.class.cast(executor.threadPool).getCorePoolSize(), 2);
    }

    @Test
    public void testMulticore() {
        assertEquals(CoreAwareParallelExecutor.calculateNumberOfCores(2), 1);
    }

    @Test
    public void testSingleCore() {
        assertEquals(CoreAwareParallelExecutor.calculateNumberOfCores(1), 1);
    }

}
