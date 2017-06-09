package com.Omega.menus;

import com.Omega.event.Event;

/**
 * A finger or stylus was pressed down above a window.
 * 
 * @author Ches Burks
 *
 */
public class EvtDown extends Event {

	private final IkWindow win;

	/**
	 * Creates a new {@link EvtDown} for the target window.
	 * 
	 * @param target the window that the touch happened above.
	 */
	public EvtDown(IkWindow target) {
		this.win = target;
	}

	/**
	 * Returns the window that was touched.
	 * 
	 * @return the window that was touched.
	 */
	public IkWindow getTarget() {
		return this.win;
	}

}
