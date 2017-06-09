package com.Omega.menus;

import com.Omega.event.Event;

/**
 * A finger or stylus was released above a window.
 * 
 * @author Ches Burks
 *
 */
public class EvtUp extends Event {

	private final IkWindow win;

	/**
	 * Creates a new {@link EvtUp} for the target window.
	 * 
	 * @param target the window that the touch happened above.
	 */
	public EvtUp(IkWindow target) {
		this.win = target;
	}

	/**
	 * Returns the window that was touched. May be null.
	 * 
	 * @return the window that was touched.
	 */
	public IkWindow getTarget() {
		return this.win;
	}

}
