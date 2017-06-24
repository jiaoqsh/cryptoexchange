package com.itranswarp.crypto.store;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.itranswarp.crypto.RunnableResource;

/**
 * Base class for service interface.
 * 
 * @author liaoxuefeng
 */
public abstract class AbstractRunnableService extends AbstractService implements RunnableResource {

	private Thread processThread = null;

	/**
	 * Process in a background thread.
	 * 
	 * @throws InterruptedException
	 */
	protected abstract void process() throws InterruptedException;

	/**
	 * Do clean job when background thread is about to end.
	 * 
	 * @throws InterruptedException
	 */
	protected abstract void clean() throws InterruptedException;

	/**
	 * Start new background thread to process job.
	 */
	@PostConstruct
	@Override
	public final synchronized void start() {
		if (processThread != null) {
			throw new IllegalStateException("Cannot re-invoke start()");
		}
		logger.info("starting background thread in " + getClass().getName() + "...");
		processThread = new Thread() {
			public void run() {
				try {
					process();
				} catch (InterruptedException e) {
				}
				try {
					clean();
				} catch (InterruptedException e) {
				}
			}
		};
		processThread.start();
		while (!processThread.isAlive()) {
		}
		logger.info("background thread was started ok in " + getClass().getName() + ".");
	}

	/**
	 * Shutdown background thread.
	 */
	@PreDestroy
	@Override
	public final synchronized void shutdown() {
		if (processThread != null) {
			logger.info("shutting down background thread in " + getClass().getName() + "...");
			processThread.interrupt();
			try {
				processThread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			processThread = null;
			logger.info("background thread was shutdown ok in " + getClass().getName() + ".");
		}
	}
}
