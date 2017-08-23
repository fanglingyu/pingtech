package com.pingtech.hgqw.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @title WebServiceThreadPool
 * @description WebService请求线程池
 * @author zuolong
 * @date 2013-6-24
 * @version V1.0
 */
public class ThreadPool {
	/** 线程池中最大线程数 */
	private static final int THREAD_MAX_NUM = 5;

	private ExecutorService service;

	private ThreadPool() {
		// int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(THREAD_MAX_NUM);
	}

	private static ThreadPool threadPool;

	public synchronized static ThreadPool getInstance() {
		if (threadPool == null) {
			threadPool = new ThreadPool();
		}
		return threadPool;
	}

	public Future addTask(Runnable runnable) {
		// service.execute(runnable);
		return service.submit(runnable);
	}

	public void cancelAllTasks() {
		service.shutdownNow();
		service = Executors.newFixedThreadPool(THREAD_MAX_NUM);
	}

	// ////////////////////
	/**
	 * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
	 * 
	 * 现行大多数GUI程序都是单线程的。 Android中单线程可用于数据库操作，文件操作，应用批量安装，
	 * 应用批量删除等不适合并发但可能IO阻塞性及影响UI线程响应的操作。
	 * 
	 * @param runnable
	 */
	private static ExecutorService singleThreadExecutor = null;

	public static void addToSingleThreadExecutor(Runnable runnable) {
		if (singleThreadExecutor == null) {
			singleThreadExecutor = Executors.newSingleThreadExecutor();
		}
		singleThreadExecutor.execute(runnable);
	}
}
