package com.Omega.event;

/**
 * An abstract event to be extended.
 */
public abstract class Event {
	private String name;

	/**
	 * Returns the name of the event.
	 *
	 * @return The events name (simple class name if no name is specified)
	 */
	public String getEventName() {
		if (this.name == null) {
			this.name = this.getClass().getSimpleName();
		}
		return this.name;
	}

}
