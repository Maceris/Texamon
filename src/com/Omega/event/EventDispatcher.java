package com.Omega.event;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

/**
 * Holds an EventQueue and dispatches the events in order when possible.
 *
 * @author Ches Burks
 *
 */
class EventDispatcher extends Thread {

	/**
	 * The number of milliseconds to wait before timing out and checking if
	 * there are more items again.
	 */
	private static final long WAIT_TIMEOUT = 10000;

	private ArrayDeque<Event> queue;

	private EventManager eventManager;

	private boolean running;
	private boolean hasEvents;

	/**
	 * Used to handle synchronization and waiting for events
	 */
	private Object syncObject;

	/**
	 * Creates and starts the thread. It will begin attempting to dispatch
	 * events immediately if there are any available.
	 *
	 * @param manager the event manager that this dispatcher belongs to
	 */
	public EventDispatcher(EventManager manager) {
		this.setName("EventDispatcher");
		this.queue = new ArrayDeque<Event>();
		this.eventManager = manager;
		this.hasEvents = false;
		this.running = true;
		this.syncObject = new Object();
	}

	private void dispatch(Event event) {
		if (event == null) {
			return;
		}
		if (this.eventManager == null) {
			Log.e("Texamon.EventDispatcher", "There is no event manager!");
			return;
		}
		HandlerList handlers = this.eventManager.getHandlers(event);
		if (handlers == null) {
			return;
		}
		EventListener[] listeners = handlers.getRegisteredListeners();
		for (EventListener registration : listeners) {
			try {
				registration.callEvent(event);
			}
			catch (EventException e) {
				Log.w("Texamon.EventDispatcher",
					"There was a problem sending an event", e);
			}
		}
	}

	/**
	 * Adds the {@link Event event} to the queue pending dispatch.
	 *
	 * @param event The event to send out
	 * @throws IllegalStateException if the element cannot be added at this time
	 *             due to capacity restrictions
	 */
	public void dispatchEvent(Event event) throws IllegalStateException {
		if (event == null) {
			return;
		}
		try {
			synchronized (this.queue) {
				this.queue.add(event);
			}
			this.hasEvents = true;
		}
		catch (IllegalStateException illegalState) {
			throw illegalState;
		}
		catch (NullPointerException nullPointer) {
			;// do nothing since its a null event
			return;// don't wake up thread
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return;// don't wake up thread
		}
		this.wakeUp();
	}

	private void handleEvent() {
		synchronized (this.queue) {
			if (this.queue.isEmpty()) {
				this.hasEvents = false;
				return;
			}
		}
		Event event;
		try {
			event = this.queue.remove();
		}
		catch (NoSuchElementException noElement) {
			// the queue is empty
			this.hasEvents = false;
			System.err.println(noElement.toString());
			return;
		}
		this.dispatch(event);

	}

	/**
	 * Checks for events in the queue, and dispatches them if possible. Does not
	 * do anything if {@link #terminate()} has been called.
	 */
	@Override
	public void run() {
		while (this.running) {
			while (!this.hasEvents) {
				synchronized (this.syncObject) {
					try {
						// block this thread until an item is added
						this.syncObject.wait(EventDispatcher.WAIT_TIMEOUT);
					}
					catch (InterruptedException e) {
						Log.w("Texamon.EventDispatcher",
							"InterruptedException in dispatch loop", e);
					}
				}
				// in case it was terminated while waiting
				if (!this.running) {
					break;
				}
			}
			if (this.hasEvents) {
				this.handleEvent();
			}
		}
		// Done running
		this.queue.clear();
	}

	/**
	 * Stops the thread from executing its run method in preparation for
	 * shutting down the thread.
	 */
	public void terminate() {
		this.hasEvents = false;
		this.running = false;
		this.eventManager = null;
		this.wakeUp();
	}

	/**
	 * Wakes this thread up when it is sleeping
	 */
	private void wakeUp() {
		synchronized (this.syncObject) {
			// Wake the thread up as there is now an event
			this.syncObject.notify();
		}
	}
}
