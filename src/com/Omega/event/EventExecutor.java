package com.Omega.event;

/**
 * An interface for events call backs.
 */
interface EventExecutor {

	/**
	 * Executes the event using a given {@link Listener listener}.
	 *
	 * @param listener The listener to use
	 * @param event The event to execute
	 * @throws EventException If the listener throws an exception
	 */
	public void execute(Listener listener, Event event) throws EventException;

}
