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
 * Methods useful for Arrays.
 *
 * @author Ches Burks
 *
 */
public class ArrayOperations {

	/**
	 * Converts the given array into a String that can be output. The String
	 * will start with '[', end with ']' and have values separated by ','. Empty
	 * arrays will output "[]". The values are determined by the objects
	 * {@code toString()} method.
	 *
	 * @param array the array to convert
	 * @return the formatted string
	 */
	public static String convertToString(Object[] array) {
		String output = "";

		if (array == null) {
			return "[" + "]";
		}
		if (array.length <= 0) {
			return "[" + "]";
		}
		output = output.concat("[");
		for (int i = 0; i <= array.length - 2; ++i) {
			output = output.concat(array[i].toString());
			output = output.concat(",");
		}
		output = output.concat(array[array.length - 1].toString());
		output = output.concat("]");

		return output;
	}
}
