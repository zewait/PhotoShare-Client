package share.photo.utility;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 源码中取过来修改而成的
 * 
 */
public final class WorkerThread {
	private static final int CORE_POOL_SIZE = 3;
	private static final int MAXIMUM_POOL_SIZE = 5;
	private static final int KEEP_ALIVE = 1;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "Task #" + mCount.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> sPoolQueue = new LinkedBlockingQueue<Runnable>(
			10);

	private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			sPoolQueue, sThreadFactory);

	public static void execute(Runnable runnable) {
		THREAD_POOL_EXECUTOR.execute(runnable);
	}

}
