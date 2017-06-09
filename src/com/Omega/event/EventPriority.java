package com.Omega.event;

/**
 * Represents an event handler's priority in receiving events
 */
public enum EventPriority {

	/**
	 * Event handling is of very low importance and is run first, to allow other
	 * packages to modify the outcome
	 */
	LOWEST(0),
	/**
	 * Event handling is of low importance
	 */
	LOW(1),
	/**
	 * Event handling is neither important nor unimportant, and may be run
	 * normally
	 */
	NORMAL(2),
	/**
	 * Event handling is of high importance
	 */
	HIGH(3),
	/**
	 * Event handling is critical and must have the final say in what happens to
	 * the event
	 */
	HIGHEST(4),
	/**
	 * Event is listened to purely for monitoring the outcome of an event.
	 * <p>
	 * No changes to the event should be made in handlers with this priority
	 */
	MONITOR(5);

	private final int pLevel;

	private EventPriority(int slot) {
		this.pLevel = slot;
	}

	/**
	 * Returns the index of this priority
	 *
	 * @return the level of importance of this priority
	 */
	public int getLevel() {
		return this.pLevel;
	}
}
