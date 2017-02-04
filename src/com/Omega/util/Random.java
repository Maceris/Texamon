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

import android.annotation.SuppressLint;
import java.security.SecureRandom;

/**
 * A Mersenne twister algorithm.
 */
public class Random {
	private SecureRandom random;

	/**
	 * Creates a random generator, and it is seeded automatically.
	 */
	@SuppressLint("TrulyRandom")
	public Random() {
		// Not intended to be completely secure, but at least fairly random
		random = new SecureRandom();
		random.setSeed(SecureRandom.getSeed(16));
	}

	/**
	 * Returns a random {@link Integer int} between the given values, inclusive.
	 *
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return The generated int
	 */
	public int getIntBetween(int min, int max) {
		return min + (int) (this.nextFloat() * ((max - min) + 1));
	}

	/**
	 * Returns the next random {@link Integer integer}.
	 *
	 * @return The next int
	 */
	public int getNext() {
		return random.nextInt();
	}

	/**
	 * Returns the next random {@link Boolean boolean}.
	 *
	 * @return The next boolean
	 */
	public boolean nextBoolean() {
		return random.nextBoolean();
	}

	/**
	 * Generates a {@link Boolean boolean} with a given probability of being
	 * true. The probability is a float from 0.0f to 1.0f, with 0 being no
	 * chance of returning true and 1 being a 100% chance of returning true.
	 *
	 * @param probablilty The chance of returning true
	 * @return The generated boolean
	 */
	public boolean nextBoolean(float probablilty) {
		if (this.nextFloat() < probablilty) {
			return true;
		}
		return false;
	}

	/**
	 * Generates and stores random bytes in the given byte array.
	 * 
	 * @param bytes The byte array to store values in.
	 * @see java.security.SecureRandom#nextBytes(byte[])
	 */
	public void nextBytes(byte[] bytes) {
		this.random.nextBytes(bytes);
	}

	/**
	 * Returns the next double.
	 * 
	 * @return A double.
	 * @see java.util.Random#nextDouble()
	 */
	public double nextDouble() {
		return this.random.nextDouble();
	}

	/**
	 * Returns the next random {@link Float float}.
	 *
	 * @return The next float
	 */
	public float nextFloat() {
		return random.nextFloat();
	}

	/**
	 * Returns a normally distributed double.
	 * 
	 * @return A double.
	 * @see java.util.Random#nextGaussian()
	 */
	public double nextGaussian() {
		return this.random.nextGaussian();
	}

	/**
	 * Returns the next integer.
	 * 
	 * @return An int.
	 * @see java.util.Random#nextInt()
	 */
	public int nextInt() {
		return this.random.nextInt();
	}

	/**
	 * Returns an int in the half open range [0, n).
	 * 
	 * @param n the upper bound.
	 * @return A random int in a range.
	 * @see java.util.Random#nextInt(int)
	 */
	public int nextInt(int n) {
		return this.random.nextInt(n);
	}

	/**
	 * Returns a random long.
	 * 
	 * @return A long.
	 * @see java.util.Random#nextLong()
	 */
	public long nextLong() {
		return this.random.nextLong();
	}
}
