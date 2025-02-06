
package ru.runa.notifier.util;

public class ExtendedThread extends Thread {
	protected boolean stopped = false;
	
	public ExtendedThread() {
		super();
		setDaemon(true);
	}
	
	public ExtendedThread(String name) {
		super(name);
		setDaemon(true);
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void startThread() {
		start();
	}
	
	public synchronized void stopThread() {
		stopped = true;
	}
}
