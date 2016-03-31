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
 * A {@link BinaryTree} subclass for storing integers. Allows the finding of the
 * smallest int not stored in the tree.
 *
 * @author Ches Burks
 *
 */
public class IntegerTree extends BinaryTree<Integer> {

	/**
	 * Returns the smallest integer not stored in the tree.
	 *
	 * @return the smallest available int not in the tree.
	 */
	public int getSmallestUnusedInt() {
		if (this.treeRoot == null) {
			return 0;
		}
		BinaryTreeNode<Integer> node = this.getSmallestSubnode(this.treeRoot);
		int smallest = 0;
		boolean exitNextLoop = false;

		while (true) {
			if ((this.find(smallest, node) != null)) {
				++smallest;
				exitNextLoop = false;
			}
			else if (this.find(smallest, node.getParent()) != null) {
				++smallest;
				exitNextLoop = false;
			}
			else if (node.getParent() != null) {
				node = node.getParent();
				exitNextLoop = false;
			}
			else {
				if (exitNextLoop) {
					break;
				}
				exitNextLoop = true;
			}
		}
		return smallest;
	}
}
