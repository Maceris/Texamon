/*******************************************************************************
 * Copyright (C) 2016 David Burks
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.Omega.util;

/**
 * A collision was found when inserting an object into a BinaryTree.
 */
public class DuplicateEntry extends Exception {

	private static final long serialVersionUID = -3959290844034459793L;

	private final Throwable theCause;

	/**
	 * Constructs a new {@link DuplicateEntry} with no cause.
	 */
	public DuplicateEntry() {
		this.theCause = null;
	}

	/**
	 * Constructs a new {@link DuplicateEntry} with no cause and the supplied
	 * detail message.
	 *
	 * @param message The detail message
	 */
	public DuplicateEntry(String message) {
		super(message);
		this.theCause = null;
	}

	/**
	 * Constructs a new {@link DuplicateEntry} with the given {@link Throwable}
	 *
	 * @param throwable The throwable that was thrown
	 */
	public DuplicateEntry(Throwable throwable) {
		this.theCause = throwable;
	}

	/**
	 * Constructs a new {@link DuplicateEntry} with the given {@link Throwable}
	 * and message.
	 *
	 * @param cause The throwable that was thrown
	 * @param message The detail message
	 */
	public DuplicateEntry(Throwable cause, String message) {
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