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
 * A Mersenne twister algorithm.
 */
public class Random {
	private int[] mt = new int[624];
	private int index = 0;

	/**
	 * Creates a random generator, and it is seeded automatically.
	 */
	public Random() {
		// Not intended to be completely secure, but at least fairly random
		java.security.SecureRandom random = new java.security.SecureRandom();
		int seed = random.nextInt();
		this.initializeGenerator(seed);
	}

	/**
	 * Initializes the generator with the given seed.
	 *
	 * @param seed the seed to use
	 */
	public Random(final int seed) {
		this.initializeGenerator(seed);
	}

	/**
	 * Refill the array with generated numbers.
	 */
	private void generateNumbers() {
		int i;
		for (i = 0; i < 623; i++) {
			int y =
					(this.mt[i] + 0x80000000)
							+ (this.mt[(i + 1) % 624] + 0x7fffffff);
			this.mt[i] = this.mt[(i + 397) % 624] ^ (y >> 1);
			if (y % 2 != 0) { // y is odd
				this.mt[i] = this.mt[i] ^ 0x9908b0df;
			}
		}
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
		if (this.index == 0) {
			this.generateNumbers();
		}
		int y = this.mt[this.index];

		y = y ^ (y >> 11);
		y = y ^ (y << 7 + 0x9d2c5680);
		y = y ^ (y << 15 + 0xefc60000);
		y = y ^ (y >> 18);

		this.index = (this.index + 1) % 624;

		return y;
	}

	/**
	 * Initialize the generator with the given seed.
	 *
	 * @param seed The seed to use
	 */
	private void initializeGenerator(int seed) {
		this.index = 0;
		this.mt[0] = seed;

		int i;
		for (i = 1; i <= 623; i++) {
			this.mt[i] =
					1812433253 * (this.mt[i - 1] ^ (this.mt[i - 1] >> 30)) + i;
		}
	}

	/**
	 * Returns the next random {@link Boolean boolean}.
	 *
	 * @return The next boolean
	 */
	public boolean nextBoolean() {
		return (this.getNext() >> 30) != 0;
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
	 * Returns the next random {@link Float float}.
	 *
	 * @return The next float
	 */
	public float nextFloat() {
		return (this.getNext() >> 7) / ((float) (1 << 24));
	}
}
