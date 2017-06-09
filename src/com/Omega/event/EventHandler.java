package com.Omega.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark methods as being event handler methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

	/**
	 * Define the priority of the event handler.
	 * <p>
	 * Order of execution from first to last:
	 * <ol>
	 * <li>LOWEST
	 * <li>LOW
	 * <li>NORMAL
	 * <li>HIGH
	 * <li>HIGHEST
	 * <li>MONITOR
	 * </ol>
	 *
	 * @return Returns this handler's priority
	 */
	EventPriority priority() default EventPriority.NORMAL;
}
