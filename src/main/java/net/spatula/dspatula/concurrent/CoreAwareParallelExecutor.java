package net.spatula.dspatula.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps an ExecutorService with the fixed number of available threads set to the number of cores available to us on the platform.
 *
 * @author spatula
 *
 */
public class CoreAwareParallelExecutor {

    protected final int cores;
    protected ExecutorService threadPool;
    private static CoreAwareParallelExecutor instance;

    private static final Logger LOG = LoggerFactory.getLogger(CoreAwareParallelExecutor.class);

    private static final class LoggingUncaughtExceptionHandler implements UncaughtExceptionHandler {

        private final Logger logger;

        private LoggingUncaughtExceptionHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.error("Uncaught exception in thread {}", t.getName(), e);
        }
    }

    private static final class CustomThreadFactory implements ThreadFactory {
        private static final Logger LOG = LoggerFactory.getLogger(CustomThreadFactory.class);
        private volatile int threadNumber = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            final Thread thread = new Thread(runnable, "CoreAwareParallelExecutor-threadPool-" + (threadNumber++));
            thread.setUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler(LOG));
            LOG.trace("Created thread {}", thread.getName());
            return thread;
        }
    }

    protected CoreAwareParallelExecutor(int cores) {
        this.cores = cores;

        threadPool = Executors.newFixedThreadPool(cores, new CustomThreadFactory());
        LOG.debug("Created a parallel execution environment using {} threads", cores);
    }

    public static synchronized CoreAwareParallelExecutor getInstance() {
        if (instance != null) {
            return instance;
        }
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        instance = new CoreAwareParallelExecutor(calculateNumberOfCores(availableProcessors));

        return instance;
    }

    protected static int calculateNumberOfCores(int availableProcessors) {
        return Math.max(availableProcessors - 1, 1);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return threadPool.invokeAll(tasks);
    }

    public int getCoreCount() {
        return cores;
    }

}
