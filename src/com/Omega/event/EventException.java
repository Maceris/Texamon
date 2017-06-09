package com.Omega.event;

/**
 * Fired when something went wrong in the event system.
 */
public class EventException extends Exception {

	private static final long serialVersionUID = 5399364798206259554L;

	private final Throwable theCause;

	/**
	 * Constructs a new {@link EventException} with no cause.
	 */
	public EventException() {
		this.theCause = null;
	}

	/**
	 * Constructs a new {@link EventException} with no cause and the supplied
	 * detail message.
	 *
	 * @param message The detail message
	 */
	public EventException(String message) {
		super(message);
		this.theCause = null;
	}

	/**
	 * Constructs a new {@link EventException} with the given {@link Throwable}
	 *
	 * @param throwable The throwable that was thrown
	 */
	public EventException(Throwable throwable) {
		this.theCause = throwable;
	}

	/**
	 * Constructs a new {@link EventException} with the given {@link Throwable}
	 * and message.
	 *
	 * @param cause The throwable that was thrown
	 * @param message The detail message
	 */
	public EventException(Throwable cause, String message) {
		super(message);
		this.theCause = cause;
	}

	/**
	 * If applicable, returns the Exception that triggered this Exception.
	 *
	 * @return Inner exception, or null if one does not exist
	 */
	@Override
	public Throwable getCause() {
		return this.theCause;
	}
}