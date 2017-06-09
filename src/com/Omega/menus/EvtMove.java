package com.Omega.menus;

import com.Omega.event.Event;

/**
 * A finger or stylus was moved while down and above a window.
 * 
 * @author Ches Burks
 *
 */
public class EvtMove extends Event {

	private final IkWindow win;

	/**
	 * Creates a new {@link EvtMove} for the target window.
	 * 
	 * @param target the window that the move happened above.
	 */
	public EvtMove(IkWindow target) {
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
