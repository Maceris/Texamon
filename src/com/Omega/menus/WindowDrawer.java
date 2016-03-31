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
package com.Omega.menus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Draws windows on a canvas given certain styles. This is not threadsafe.
 *
 * @author Ches Burks
 *
 */
public class WindowDrawer {
	/**
	 * Defines the different borders a window can have.
	 */
	public enum Border {
		/**
		 * Just a plain line.
		 */
		LINE,
		/**
		 * No border at all, just the background
		 */
		BORDERLESS;
	}

	/**
	 * The colors of a window and its border.
	 *
	 */
	public enum ColorScheme {
		/**
		 * White with grey borders
		 */
		LIGHT,
		/**
		 * Red with darker red borders
		 */
		RED,
		/**
		 * Blue with darker blue borders
		 */
		BLUE,
		/**
		 * Green with darker green borders
		 */
		GREEN,
		/**
		 * Grey with dark grey borders
		 */
		DARK,
		/**
		 * Yellow with dark yellow borders
		 */
		YELLOW;
	}

	/**
	 * Defines the different styles of the box.
	 */
	public enum WinStyle {
		/**
		 * A square box with no border
		 */
		SQUARE,
		/**
		 * A rectangle but with rounded corners. These do not have corner
		 * pieces.
		 */
		ROUNDED,
		/**
		 * Just the border if there is one, but rounded
		 */
		EMPTY_SQUARE,
		/**
		 * Just the border if there is one, but square
		 */
		EMPTY_ROUNDED;
	}

	/**
	 * Darkens any color, leaving the alpha value alone
	 */
	public static final ColorMatrix COLOR_M_DARKEN = new ColorMatrix() {
		{
			this.setScale(0.5f, 0.5f, 0.5f, 1.0f);
		}
	};
	/**
	 * Darkens any color, leaving the alpha value alone
	 */
	public static final ColorMatrixColorFilter COLOR_FILTER_DARKEN =
			new ColorMatrixColorFilter(WindowDrawer.COLOR_M_DARKEN);
	/**
	 * The identity matrix
	 */
	public static final ColorMatrix COLOR_M_NORMAL = new ColorMatrix() {
		{
			this.reset();
		}
	};

	/**
	 * The identity matrix
	 */
	public static final ColorMatrixColorFilter COLOR_FILTER_NORMAL =
			new ColorMatrixColorFilter(WindowDrawer.COLOR_M_NORMAL);
	private static Paint paint = new Paint();

	public static void drawWindow(Canvas c, IkWindow win, WindowStyle style) {
		if (c == null) {
			// TODO log error
			return;
		}
		if (win == null) {
			// TODO log error
			return;
		}
		if (style == null) {
			// TODO log error
			return;
		}
		WindowDrawer.drawWindow(c, win, style.style, style.border,
				style.colorScheme);
	}

	public static void drawWindow(Canvas c, IkWindow win, WinStyle style,
			Border border, ColorScheme colorScheme) {
		if (c == null || win == null || style == null || border == null
				|| colorScheme == null) {
			// TODO log error
			return;
		}

		Rect boundingRect = win.getBoundingRect(c);
		RectF boundingFRect = new RectF(boundingRect);
		/*
		 * 0.04 is roughly the corner radius percentage that credit cards use
		 */
		float cornerMult = 0.04f;
		WindowDrawer.paint.reset();

		// sets the color to the background color
		WindowDrawer.setColor(WindowDrawer.paint, colorScheme);

		WindowDrawer.paint.setStyle(android.graphics.Paint.Style.FILL);
		WindowDrawer.paint.setColorFilter(WindowDrawer.COLOR_FILTER_NORMAL);
		// draw background
		switch (style) {
		case ROUNDED:

			c.drawRoundRect(boundingFRect, cornerMult * boundingFRect.width(),
					cornerMult * boundingFRect.height(), WindowDrawer.paint);
			break;
		case SQUARE:
			c.drawRect(boundingRect, WindowDrawer.paint);
			break;
		case EMPTY_ROUNDED:
			break;
		case EMPTY_SQUARE:
			break;
		default:
			c.drawRect(boundingRect, WindowDrawer.paint);
			break;

		}
		// set the color to border color
		WindowDrawer.paint.setColorFilter(WindowDrawer.COLOR_FILTER_DARKEN);
		WindowDrawer.paint.setStyle(android.graphics.Paint.Style.STROKE);

		// the width or height of the rec, whichever is larger
		float recLargerSize =
				(boundingFRect.width() > boundingFRect.height()) ? boundingFRect
						.width() : boundingFRect.height();
		// draw the border
		switch (border) {
		case BORDERLESS:
			// Nothing needs to be done here
			break;
		case LINE:
			WindowDrawer.paint.setStrokeWidth(recLargerSize * 0.01f);
			switch (style) {
			case ROUNDED:
			case EMPTY_ROUNDED:
				WindowDrawer.paint.setStrokeCap(Cap.ROUND);
				WindowDrawer.paint.setStrokeJoin(Join.ROUND);
				c.drawRoundRect(boundingFRect,
						cornerMult * boundingFRect.width(), cornerMult
								* boundingFRect.height(), WindowDrawer.paint);
				break;
			case SQUARE:
			case EMPTY_SQUARE:
				WindowDrawer.paint.setStrokeCap(Cap.SQUARE);
				WindowDrawer.paint.setStrokeJoin(Join.BEVEL);
				c.drawRect(boundingRect, WindowDrawer.paint);
				break;
			default:
				WindowDrawer.paint.setStrokeCap(Cap.SQUARE);
				WindowDrawer.paint.setStrokeJoin(Join.BEVEL);
				c.drawRect(boundingRect, WindowDrawer.paint);
				break;
			}
			break;
		default:
			WindowDrawer.paint.setStrokeWidth(recLargerSize * 0.01f);
			switch (style) {
			case ROUNDED:
			case EMPTY_ROUNDED:
				WindowDrawer.paint.setStrokeCap(Cap.ROUND);
				WindowDrawer.paint.setStrokeJoin(Join.ROUND);
				c.drawRoundRect(boundingFRect,
						cornerMult * boundingFRect.width(), cornerMult
								* boundingFRect.height(), WindowDrawer.paint);
				break;
			case SQUARE:
			case EMPTY_SQUARE:
				WindowDrawer.paint.setStrokeCap(Cap.SQUARE);
				WindowDrawer.paint.setStrokeJoin(Join.BEVEL);
				c.drawRect(boundingRect, WindowDrawer.paint);
				break;
			default:
				WindowDrawer.paint.setStrokeCap(Cap.SQUARE);
				WindowDrawer.paint.setStrokeJoin(Join.BEVEL);
				c.drawRect(boundingRect, WindowDrawer.paint);
				break;
			}
			break;
		}
	}

	private static void setColor(Paint p, final ColorScheme c) {
		/*
		 * These use the Natural Color System colors where possible because it
		 * looks nicer than strictly FF0000 type colors.
		 */
		switch (c) {
		case BLUE:
			p.setColor(Color.rgb(0, 135, 189));
			break;
		case DARK:
			p.setColor(Color.rgb(128, 128, 128));
			break;
		case GREEN:
			p.setColor(Color.rgb(0, 159, 107));
			break;
		case LIGHT:
			p.setColor(Color.rgb(255, 255, 255));
			break;
		case RED:
			p.setColor(Color.rgb(196, 2, 51));
			break;
		case YELLOW:
			p.setColor(Color.rgb(255, 211, 0));
			break;
		default:
			// White
			p.setColor(Color.rgb(255, 255, 255));
			break;
		}
	}

}
