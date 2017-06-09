package com.Omega.event;

/**
 * A listener that handles events.
 */
class EventListener {
	private final Listener theListener;
	private final EventExecutor theExecutor;
	private final EventPriority thePriority;

	/**
	 * Constructs a new {@link EventListener}.
	 *
	 * @param listener The listener to call
	 * @param executor The executor for the events
	 * @param priority The priority of the listener
	 */
	public EventListener(final Listener listener, final EventExecutor executor,
			final EventPriority priority) {
		this.theListener = listener;
		this.theExecutor = executor;
		this.thePriority = priority;
	}

	/**
	 * Calls the event executor.
	 *
	 * @param event The event to execute
	 * @throws EventException If an exception occurs during execution
	 */
	public void callEvent(final Event event) throws EventException {
		this.theExecutor.execute(this.theListener, event);
	}

	/**
	 * Returns the {@link Listener listener}.
	 *
	 * @return The listener
	 */
	public Listener getListener() {
		return this.theListener;
	}

	/**
	 * Returns the priority for this listener
	 *
	 * @return The registered Priority
	 */
	public EventPriority getPriority() {
		return this.thePriority;
	}

}
