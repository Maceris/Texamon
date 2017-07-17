/*******************************************************************************
 * Copyright (C) 2016, 2017 David Burks
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
 * A binary AVL tree for storing (comparable) objects.
 *
 * @author Ches Burks
 *
 * @param <T> the type of object to store. Must extend {@link Comparable}
 */
public class BinaryTree<T extends Comparable<T>> {

	/**
	 * A binary tree node used in the {@link BinaryTree} class.
	 *
	 * @author Ches Burks
	 * @param <N> the Type of object this node holds
	 *
	 */
	protected class BinaryTreeNode<N extends Comparable<N>> {
		/**
		 * The value stored in this node.
		 */
		protected N key;
		/**
		 * The height of the largest subtree that starts at this node.
		 */
		protected int height;
		/**
		 * A pointer to the left child. Null if there is no left child.
		 */
		protected BinaryTreeNode<N> left;
		/**
		 * A pointer to the right child. Null if there is no right child.
		 */
		protected BinaryTreeNode<N> right;
		/**
		 * A pointer to the parent node. The parent should be null if and only
		 * if it is the root of the tree.
		 */
		protected BinaryTreeNode<N> parent;

		/**
		 * Constructs a new BinaryTree node for the given object with null
		 * pointers for left, right, and parent.
		 *
		 * @param newKey the key to store
		 */
		public BinaryTreeNode(N newKey) {
			this(newKey, null, null, null);
		}

		/**
		 * Constructs a new BinaryTree node for the given object and
		 * children/parent. The other nodes may be null. Height defaults to 1.
		 *
		 * @param newKey the key to store
		 * @param leftNode the left node
		 * @param parentNode the parent node
		 * @param rightNode the right node
		 */
		public BinaryTreeNode(N newKey, BinaryTreeNode<N> leftNode,
				BinaryTreeNode<N> parentNode, BinaryTreeNode<N> rightNode) {
			this.key = newKey;
			this.height = 1;
			this.left = leftNode;
			this.parent = parentNode;
			this.right = rightNode;
		}

		/**
		 * Recursively calls delete on all children then removes all references
		 * to the children, parents and keys. Also zeroes the height.
		 */
		public void delete() {
			if (this.left != null) {
				this.left.delete();
			}
			this.left = null;
			if (this.right != null) {
				this.right.delete();
			}
			this.right = null;
			this.parent = null;
			this.key = null;
			this.height = 0;
		}

		/**
		 * Dereferences all other objects this object has pointers to. This will
		 * cause any children to be cleaned up as well.
		 */
		@Override
		protected void finalize() throws Throwable {
			this.left = null;
			this.right = null;
			this.parent = null;
			this.key = null;
			this.height = 0;
			super.finalize();
		}

		/**
		 * Returns the height of the largest subtree starting at this node.
		 * Stored in the node for speed.
		 *
		 * @return this nodes height
		 */
		public int getHeight() {
			return this.height;
		}

		/**
		 * Returns the key stored in this node.
		 *
		 * @return the key
		 */
		public N getKey() {
			return this.key;
		}

		/**
		 * Returns the left child of this node. This will be null if there is no
		 * left child.
		 *
		 * @return the left child, or null
		 */
		public BinaryTreeNode<N> getLeft() {
			return this.left;
		}

		/**
		 * Returns the parent of this node. This will be null if this node is
		 * the root of the tree.
		 *
		 * @return the parent, or null
		 */
		public BinaryTreeNode<N> getParent() {
			return this.parent;
		}

		/**
		 * Returns the right child of this node. This will be null if there is
		 * no right child.
		 *
		 * @return the right child, or null
		 */
		public BinaryTreeNode<N> getRight() {
			return this.right;
		}

		/**
		 * Sets the height of this node.
		 *
		 * @param newHeight the new height of this node
		 */
		public void setHeight(int newHeight) {
			this.height = newHeight;
		}

		/**
		 * Set the key value stored in this node.
		 *
		 * @param newKey the new key
		 */
		public void setKey(N newKey) {
			this.key = newKey;
		}

		/**
		 * Sets the left child of this node to the supplied node.
		 *
		 * @param newLeft the new left child
		 */
		public void setLeft(BinaryTreeNode<N> newLeft) {
			this.left = newLeft;
		}

		/**
		 * Sets the parent of this node to the supplied node.
		 *
		 * @param newParent the new parent
		 */
		public void setParent(BinaryTreeNode<N> newParent) {
			this.parent = newParent;
		}

		/**
		 * Sets the right child of this node to the supplied node.
		 *
		 * @param newRight the new right child
		 */
		public void setRight(BinaryTreeNode<N> newRight) {
			this.right = newRight;
		}

	}

	/**
	 * The base of the tree. This is null if the tree is empty.
	 */
	protected BinaryTreeNode<T> treeRoot;
	/**
	 * How many elements are stored in the tree.
	 */
	protected int size;

	/**
	 * Removes all objects from the tree.
	 */
	public void clear() {
		if (this.treeRoot != null) {
			this.treeRoot.delete();
		}
		this.treeRoot = null;
		this.size = 0;
	}

	/**
	 * Returns true if the tree contains the given object
	 *
	 * @param toFind the entry to search for
	 * @return true if the object exists or false if it is not in the tree
	 */
	public boolean contains(T toFind) {
		// true if the find method returns something that is not null
		// (that is, it exists in the tree)
		return (this.find(toFind, this.treeRoot) != null);
	}

	/**
	 * Recursively searches for the entry and returns the node that contains it
	 * or null if it is not found.
	 *
	 * @param toFind the entry to search for
	 * @param root where to start looking
	 * @return null or the node that has toFind as its key
	 */
	protected BinaryTreeNode<T> find(T toFind, BinaryTreeNode<T> root) {
		if (root == null || root.key == null) {
			return null;
		}
		int compareVal = toFind.compareTo(root.key);
		if (compareVal == 0) {
			// they match
			return root;
		}
		else if (compareVal < 0) {
			// less than the roots value, so in the left subtree
			return this.find(toFind, root.left);
		}
		else if (compareVal > 0) {
			// greater than the roots value, so in the right subtree
			return this.find(toFind, root.right);
		}
		// won't reach here but just in case.
		return null;
	}

	/**
	 * Returns the AVL balance factor of the node. That is, 0 if the node is
	 * null, or the height of the left subtree minus the height of the right
	 * subtree. This should be -1, 0, or 1 unless the node needs to be
	 * rebalanced.
	 *
	 * @see #getHeight(BinaryTreeNode)
	 * @param node the node to find the balance of
	 * @return the nodes AVL balance factor
	 */
	protected int getBalance(BinaryTreeNode<T> node) {
		if (node == null) {
			return 0;
		}
		return this.getHeight(node.left) - this.getHeight(node.right);
	}

	/**
	 * Returns the height of the node, or -1 if it is null.
	 *
	 * @param node the node to get the height of
	 * @return the tree's (with node as the root) maximum height
	 */
	protected int getHeight(BinaryTreeNode<T> node) {
		if (node == null) {
			return -1;
		}
		return node.height;
	}

	/**
	 * Returns the smallest child node of the tree that has root as its parent.
	 * If root is null it just returns null. If root has no children, it returns
	 * itself.
	 *
	 * @param root the root of the tree to search
	 * @return the smallest node in that subtree
	 */
	protected BinaryTreeNode<T> getSmallestSubnode(BinaryTreeNode<T> root) {
		if (root == null) {
			return null;
		}
		BinaryTreeNode<T> node = root;
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	/**
	 * Inserts an object into the tree using recursive calls, with a root at
	 * theRoot. Returns the new root of the tree. The root of the tree should be
	 * passed as theRoot and then set to the return value.
	 *
	 * @param theRoot the root of the tree to insert into
	 * @param ins the value to insert
	 * @return the new root of the tree (where theRoot was before)
	 * @throws DuplicateEntry if the entry already exists in the tree
	 *
	 */
	@SuppressWarnings("all")
	protected BinaryTreeNode<T> insert(BinaryTreeNode<T> theRoot, T ins)
			throws DuplicateEntry {
		if (this.treeRoot == null) {
			this.treeRoot = new BinaryTreeNode<T>(ins, null, null, null);
			return this.treeRoot;
		}

		if (theRoot == null || theRoot.key == null) {
			BinaryTreeNode<T> newRoot =
					new BinaryTreeNode<T>(ins, null, null, null);
			return newRoot;
		}

		// insert node, looking for it recursively
		if (ins.compareTo(theRoot.key) == 0) {
			throw new DuplicateEntry(theRoot.key.toString());
		}
		else if (ins.compareTo(theRoot.key) < 1) {
			// inserting on the left of this node
			theRoot.left = this.insert(theRoot.left, ins);
			theRoot.left.parent = theRoot;
		}
		else {
			// inserting on the right of this node
			theRoot.right = this.insert(theRoot.right, ins);
			theRoot.right.parent = theRoot;
		}

		// update the height
		/*
		 * recalculate the height of the left node. this will be called on each
		 * parent until the root because this is a recursive function and this
		 * occurs after the recursive function call, so it will update the
		 * height appropriately
		 */
		theRoot.height =
				this.max(this.getHeight(theRoot.left),
						this.getHeight(theRoot.right)) + 1;

		int balance = this.getBalance(theRoot);

		// if it is unbalanced, handle the 4 special cases

		// single right, because its larger on the left and inserting on
		// left.left
		if (theRoot.left != null && theRoot.left.key != null) {
			if (balance > 1 && ins.compareTo(theRoot.left.key) < 1) {
				theRoot = this.rightRotate(theRoot);
				if (theRoot.parent == null) {
					this.treeRoot = theRoot;
				}
				return theRoot;
			}
			// double right, because its larger on the left and inserting on
			// left.right
			if (balance > 1 && ins.compareTo(theRoot.left.key) > 1) {
				theRoot.left = this.leftRotate(theRoot.left);
				theRoot = this.rightRotate(theRoot);
				if (theRoot.parent == null) {
					this.treeRoot = theRoot;
				}
				return theRoot;
			}
		}
		// single left, because its larger on the right and its inserting on
		// right.right
		if (theRoot.right != null && theRoot.right.key != null) {
			if (balance < -1 && ins.compareTo(theRoot.right.key) > 1) {
				theRoot = this.leftRotate(theRoot);
				if (theRoot.parent == null) {
					this.treeRoot = theRoot;
				}
				return theRoot;
			}
			// double left
			if (balance < -1 && ins.compareTo(theRoot.right.key) > 1) {
				theRoot.right = this.rightRotate(theRoot.right);
				theRoot = this.leftRotate(theRoot);
				if (theRoot.parent == null) {
					this.treeRoot = theRoot;
				}
				return theRoot;
			}
		}

		return theRoot;// No rebalancing needed
	}

	/**
	 * Inserts the given value into the tree.
	 *
	 * @param toInsert the object to store
	 * @throws DuplicateEntry if the entry already exists in the tree
	 */
	public void insert(T toInsert) throws DuplicateEntry {
		this.treeRoot = this.insert(this.treeRoot, toInsert);
		++this.size;
	}

	/**
	 * Rotates the given node left and returns the new root that is where root
	 * used to be. Updates heights of nodes and parents appropriately. If the
	 * root's right child is null then it just returns the root because it can't
	 * rotate.
	 *
	 * @param root the root of the three nodes to rotate
	 * @return the new root
	 *
	 */
	protected BinaryTreeNode<T> leftRotate(BinaryTreeNode<T> root) {
		BinaryTreeNode<T> rightChild = root.right;
		if (rightChild == null) {
			return root;
		}
		BinaryTreeNode<T> rightsLeftChild = rightChild.left;

		// Change root.parents reference to root to root.left
		if (root.parent != null) {
			if (root.parent.left == root) {
				root.parent.left = rightChild;
			}
			else if (root.parent.right == root) {
				root.parent.right = rightChild;
			}
		}

		// rotate
		rightChild.left = root;
		rightChild.parent = root.parent;
		root.parent = rightChild;
		root.right = rightsLeftChild;
		if (rightsLeftChild != null) {
			rightsLeftChild.parent = root;
		}

		// update height, starting with the smallest changed child
		if (rightsLeftChild != null) {
			this.updateHeight(rightsLeftChild);
		}
		else {
			this.updateHeight(root);
		}

		return rightChild;
	}

	/**
	 * Returns the larger of the two numbers.
	 *
	 * @param a the first number
	 * @param b the second number
	 * @return whichever number is largest
	 */
	protected int max(int a, int b) {
		/*
		 * If a is less than b, return b. otherwise, return a.
		 */
		return (a < b) ? b : a;
	}

	/**
	 * Rebalances the tree starting at the given the lowest possibly unbalanced
	 * node and working up the path to the root.
	 *
	 * @param lowest the lowest node in the tree that might be unbalance or was
	 *            changed
	 */
	@SuppressWarnings("all")
	protected void rebalance(BinaryTreeNode<T> lowest) {
		if (lowest == null) {
			return;
		}
		int balance = this.getBalance(lowest);
		// single right, because its larger on the left and inserting on
		// left.left
		if (balance > 1) {
			// left is too big
			lowest = this.rightRotate(lowest);
		}
		else if (balance < -1) {
			// right is too big
			lowest = this.leftRotate(lowest);
		}
		this.rebalance(lowest.parent);
	}

	/**
	 * Removes the given node from the tree. This will nullify the pointer to
	 * toRemove.
	 *
	 * @param toRemove the node to remove
	 */
	@SuppressWarnings("all")
	protected void remove(BinaryTreeNode<T> toRemove) {
		if (toRemove == null) {
			return;// just ignore it
		}

		// If and only if only one child is not null. (a logical exclusive or)
		// Basically, there is a child but only one child.
		if (!(toRemove.left != null) != !(toRemove.right != null)) {
			// set this nodes child's parent pointer to this nodes parent
			// if its null then those are the new root.
			BinaryTreeNode<T> child;
			if (toRemove.left != null) {
				// its the left child
				child = toRemove.left;
				toRemove.left.parent = toRemove.parent;
			}
			else {
				// its the right child
				child = toRemove.right;
				toRemove.right.parent = toRemove.parent;
			}

			if (toRemove.parent == null) {
				// this is the root node
				this.treeRoot = child;
				this.updateHeight(this.treeRoot);
			}
			else if (toRemove.parent.left == toRemove) {
				// this is a left child
				toRemove.parent.left = child;
				child.parent = toRemove.parent;
				this.updateHeight(child);
			}
			else if (toRemove.parent.right == toRemove) {
				// this is a right child
				toRemove.parent.right = child;
				child.parent = toRemove.parent;
				this.updateHeight(child);
			}
			this.rebalance(child);
		}
		/*
		 * No children, just delete this node. this works because the null
		 * status of both children is the same, so only checking one is fine
		 * (and faster)
		 */
		else if (toRemove.left == null) {
			if (toRemove.parent == null) {
				// this is the root node, and the only node in the tree
				this.treeRoot = null;
			}
			else if (toRemove.parent.left == toRemove) {
				// its a left child
				toRemove.parent.left = null;// remove parents reference to this
				// node
				this.updateHeight(toRemove.parent);
				BinaryTreeNode<T> parent = toRemove.parent;
				toRemove.parent = null;
				this.rebalance(parent);

			}
			else if (toRemove.parent.right == toRemove) {
				toRemove.parent.right = null;// remove parents reference to this
				// node
				this.updateHeight(toRemove.parent);
				BinaryTreeNode<T> parent = toRemove.parent;
				toRemove.parent = null;
				this.rebalance(parent);
			}
		}
		// there are two children
		// Because the null status of both children is the same
		// and the children cant be null because of the previous else in the
		// if-else chain
		else {
			BinaryTreeNode<T> smallestRight =
					this.getSmallestSubnode(toRemove.right);
			BinaryTreeNode<T> parent = smallestRight.parent;
			if (toRemove.parent == null) {
				// this is the root node
				if (smallestRight.parent.left == smallestRight) {
					smallestRight.parent.left = null;
				}
				else if (smallestRight.parent.right == smallestRight) {
					smallestRight.parent.right = null;
				}
				if (toRemove.left != null && toRemove.left != smallestRight) {
					toRemove.left.parent = smallestRight;
				}
				if (toRemove.right != null && toRemove.right != smallestRight) {
					toRemove.right.parent = smallestRight;
				}
				if (toRemove.left != smallestRight) {
					smallestRight.left = toRemove.left;
				}
				if (toRemove.right != smallestRight) {
					smallestRight.right = toRemove.right;
				}
				smallestRight.parent = null;

				toRemove.parent = null;
				toRemove.left = null;
				toRemove.right = null;

				this.treeRoot = smallestRight;
			}
			else if (toRemove.parent.left == toRemove) {
				// this is a left child
				if (smallestRight.parent.left == smallestRight) {
					smallestRight.parent.left = null;
				}
				else if (smallestRight.parent.right == smallestRight) {
					smallestRight.parent.right = null;
				}
				if (toRemove.left != null && toRemove.left != smallestRight) {
					toRemove.left.parent = smallestRight;
				}
				if (toRemove.right != null && toRemove.right != smallestRight) {
					toRemove.right.parent = smallestRight;
				}

				toRemove.parent.left = smallestRight;
				if (toRemove.left != smallestRight) {
					smallestRight.left = toRemove.left;
				}
				if (toRemove.right != smallestRight) {
					smallestRight.right = toRemove.right;
				}
				smallestRight.parent = toRemove.parent;
				toRemove.parent = null;
				toRemove.left = null;
				toRemove.right = null;
			}
			else if (toRemove.parent.right == toRemove) {
				// this is a right child
				if (toRemove.left != null) {
					toRemove.left.parent = smallestRight;
				}
				if (toRemove.right != null) {
					toRemove.right.parent = smallestRight;
				}

				if (smallestRight.parent.left == smallestRight) {
					smallestRight.parent.left = null;
				}
				else if (smallestRight.parent.right == smallestRight) {
					smallestRight.parent.right = null;
				}

				toRemove.parent.right = smallestRight;

				if (toRemove.left != smallestRight) {
					smallestRight.left = toRemove.left;
				}
				if (toRemove.right != smallestRight) {
					smallestRight.right = toRemove.right;
				}
				smallestRight.parent = toRemove.parent;
				toRemove.parent = null;
				toRemove.left = null;
				toRemove.right = null;
			}

			// update heights and balance
			if (parent != null) {
				this.updateHeight(parent);
				this.rebalance(parent);
			}
			else {
				this.updateHeight(smallestRight);
				this.rebalance(smallestRight);
			}
		}
		// ensure the element to remove is gone
		toRemove.delete();
		toRemove = null;
	}

	/**
	 * Removes the object form the tree if it exists.
	 *
	 * @param toRemove the object to remove
	 */
	public void remove(T toRemove) {
		BinaryTreeNode<T> to_remove = this.find(toRemove, this.treeRoot);
		if (to_remove != null) {
			this.remove(to_remove);
		}
		--this.size;
	}

	/**
	 * Rotates the given node right and returns the new root that is where root
	 * used to be. Updates heights of nodes and parents appropriately. If the
	 * root's left child is null, it just returns the root because it can't
	 * rotate.
	 *
	 * @param root the root of the three nodes to rotate
	 * @return the new root
	 */
	protected BinaryTreeNode<T> rightRotate(BinaryTreeNode<T> root) {
		BinaryTreeNode<T> leftChild = root.left;
		if (leftChild == null) {
			return root;
		}
		BinaryTreeNode<T> leftsRightChild = leftChild.right;

		// Change root.parents reference to root to the root.left
		if (root.parent != null) {
			if (root.parent.left == root) {
				root.parent.left = leftChild;
			}
			else if (root.parent.right == root) {
				root.parent.right = leftChild;
			}
		}

		// rotate
		leftChild.right = root;
		leftChild.parent = root.parent;
		root.parent = leftChild;
		root.left = leftsRightChild;
		if (leftsRightChild != null) {
			leftsRightChild.parent = root;
		}

		// update height, starting with the smallest changed child
		if (leftsRightChild != null) {
			this.updateHeight(leftsRightChild);
		}
		else {
			this.updateHeight(root);
		}

		return leftChild;
	}

	/**
	 * Returns the number of elements in this tree.
	 *
	 * @return the data structures size
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Recursively updates the heights of the parents of this node
	 *
	 * @param changed the lowest node that needs to be updated
	 */
	protected void updateHeight(BinaryTreeNode<T> changed) {
		if (changed == null) {
			return;
		}
		changed.height =
				this.max(this.getHeight(changed.left),
						this.getHeight(changed.right)) + 1;
		if (changed.parent != null) {
			this.updateHeight(changed.parent);
		}
	}
}
