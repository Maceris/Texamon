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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Omega.Saveable;

public class SaveUtil {
	/**
	 * escape all characters in the string that might have special meaning in
	 * the save functions
	 *
	 * @param item the item to escape
	 * @return the escaped string
	 */
	public static String escape(final String item) {
		String ret = item;
		ret =
				ret.replaceAll(
						Pattern.quote(Saveable.ESCAPE),
						Matcher.quoteReplacement(Saveable.ESCAPE)
								+ Matcher.quoteReplacement(Saveable.ESCAPE));
		ret =
				ret.replaceAll(
						Pattern.quote(Saveable.DELIMITER),
						Matcher.quoteReplacement(Saveable.ESCAPE)
								+ Matcher.quoteReplacement(Saveable.DELIMITER));
		ret =
				ret.replaceAll(
						Pattern.quote(Saveable.NEST_BEGIN),
						Matcher.quoteReplacement(Saveable.ESCAPE)
								+ Matcher.quoteReplacement(Saveable.NEST_BEGIN));
		ret =
				ret.replaceAll(
						Pattern.quote(Saveable.NEST_END),
						Matcher.quoteReplacement(Saveable.ESCAPE)
								+ Matcher.quoteReplacement(Saveable.NEST_END));
		return ret;
	}

	public static String[] split(final String item) {
		ArrayList<String> strings = new ArrayList<String>();
		int i = 0;
		int start = 0;
		final int size = item.length();
		int deepness = 0;
		for (i = 0; i < size; ++i) {
			if (item.regionMatches(i, Saveable.ESCAPE, 0,
					Saveable.ESCAPE.length())) {
				continue;
			}
			if (item.regionMatches(i, Saveable.NEST_BEGIN, 0,
					Saveable.NEST_BEGIN.length())) {
				if (deepness == 0) {
					++start;
				}
				++deepness;
			}
			if (deepness > 0) {
				if (item.regionMatches(i, Saveable.NEST_END, 0,
						Saveable.NEST_END.length())) {
					if (deepness > 0) {
						--deepness;
						if (deepness == 0) {
							strings.add(item.substring(start, i));
							i += Saveable.NEST_END.length();
							start = i + 1;// skip next delimiter
						}
					}
				}
			}
			else {
				if (item.regionMatches(i, Saveable.DELIMITER, 0,
						Saveable.DELIMITER.length())) {
					strings.add(item.substring(start, i));
					i += Saveable.DELIMITER.length() - 1;
					start = i + 1;
				}
			}
			if (i == size - 1) {
				strings.add(item.substring(start, i + 1));
			}
		}
		String[] retVal = new String[strings.size()];
		return strings.toArray(retVal);
	}
}
