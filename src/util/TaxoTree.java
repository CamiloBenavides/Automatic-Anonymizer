/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class implements a MutableTreeNode used as a TreeModel for the JTree
 * Swing component, including extra methods for tree building and traversal.
 *
 * @author davl3232
 * @param <T> Type of the data inside each node.
 */
public class TaxoTree<T> implements MutableTreeNode {

	private T data;
	private TaxoTree<T> parent;
	private List<TaxoTree<T>> children;

	/**
	 * Empty tree node constructor.
	 */
	public TaxoTree() {
		this.parent = null;
		this.data = null;
		this.children = new ArrayList<>();
	}

	/**
	 * Constructor for tree node with data.
	 * 
	 * @param rootData data for the root node.
	 */
	public TaxoTree(T rootData) {
		this.parent = null;
		this.data = rootData;
		this.children = new ArrayList<>();
	}

	/**
	 * Constructor for tree node with data, attaches the node to the parent.
	 * 
	 * @param data data for leaf node.
	 * @param parent parent to attach to.
	 */
	public TaxoTree(T data, TaxoTree<T> parent) {
		this.parent = parent;
		this.data = data;
		this.parent.addChild(this);
		this.children = new ArrayList<>();
	}

	private TaxoTree<T> getParentAtHeight(int currH, int h) {
		if (currH == h) {
			return this;
		}
		return this.parent.getParentAtHeight(currH + 1, h);
	}

	/**
	 * Finds the parent of this tree node that is at the given tree height.
	 * 
	 * @param h the target tree height.
	 * @return tree node found at the given height.
	 */
	public TaxoTree<T> getParentAtHeight(int h) {
		int currH = this.getHeight();
		return this.getParentAtHeight(currH, h);
	}

	/**
	 * Searches the node that has the given value as data, inside this sub-tree.
	 * 
	 * @param d the data to be found.
	 * @return if found, the tree node that was found, null otherwise.
	 */
	public TaxoTree<T> find(T d) {
		if (d == null) {
			return null;
		}
		if (this.data.equals(d)) {
			return this;
		}
		TaxoTree<T> t = null;
		for (int i = 0; i < this.children.size(); i++) {
			t = this.children.get(i).find(d);
			if (t != null) {
				break;
			}
		}
		return t;
	}

	/**
	 * Obtain the height of this node in the tree.
	 * 
	 * @return integer with the height of this node.
	 */
	public int getHeight() {
		int maxHeight = 0;
		for (int i = 0; i < this.children.size(); ++i) {
			int currHeight = this.children.get(i).getHeight();
			if (currHeight > maxHeight) {
				maxHeight = currHeight;
			}
		}
		return 1 + maxHeight;
	}

	/**
	 * Obtains the level at which this tree node is inside the whole tree.
	 * 
	 * @return the level at which this tree node is.
	 */
	public int getLevel() {
		if (this.parent == null) {
			return 0;
		}
		return 1 + this.parent.getLevel();
	}

	/**
	 * Searches this sub-tree for a node with the given data and obtains its
	 * level.
	 * 
	 * @param data the data to be found.
	 * @return the level at which the data was found inside this subtree.
	 */
	public int getLevelOf(T data) {
		int level = 0;
		if (this.data.equals(data)) {
			return level;
		}
		boolean found = false;
		for (TaxoTree<T> t : this.children) {
			int nextLevel = t.getLevelOf(data);
			if (nextLevel != -1) {
				found = true;
				level = nextLevel + 1;
				break;
			}
		}
		if (found) {
			return level;
		} else {
			return -1;
		}
	}

	/**
	 * Builds this sub-tree from a lineage.
	 * 
	 * @param lineage list of linear descentants data objects including
	 * this(e.g. [this, child, grandchild, grandgrandchild, ...]).
	 */
	public void buildFromLineage(List<T> lineage) {
		T d = lineage.get(lineage.size() - 1);
		lineage.remove(lineage.size() - 1);
//		if (this.data != null && !this.data.equals(d)) {
//			throw new RuntimeException("Linaje no válido.");
//		}
		this.data = d;
		this.addLineage(lineage);
	}

	/**
	 * Adds a lineage to this sub-tree.
	 * 
	 * @param lineage list of linear descentants data objects without this(e.g. [child, grandchild, grandgrandchild, ...]).
	 */
	public void addLineage(List<T> lineage) {
		if (lineage.isEmpty()) {
			return;
		}
		T d = lineage.get(lineage.size() - 1);
		lineage.remove(lineage.size() - 1);
		TaxoTree<T> c = null;
		for (int i = 0; i < this.children.size() && c == null; i++) {
			if (this.children.get(i).getData().equals(d)) {
				c = this.children.get(i);
			}
		}
		if (c == null) {
			c = new TaxoTree<>(d, this);
			this.children.add(c);
		}
		c.addLineage(lineage);
	}

	/**
	 * Creates and adds a child with the given data to this sub-tree.
	 * 
	 * @param data the data for the child node.
	 */
	public void addChild(T data) {
		TaxoTree<T> child = new TaxoTree<>(data, this);
	}

	/**
	 * Adds the given sub-tree as a child of this sub-tree.
	 * 
	 * @param c the child to be added.
	 */
	public void addChild(TaxoTree<T> c) {
		this.children.add(c);
	}

	/**
	 * Adds all sub-trees in the given collection as children in this sub-tree.
	 *
	 * @param c the children collection.
	 */
	public void addChildren(Collection<? extends TaxoTree<T>> c) {
		this.children.addAll(c);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public TaxoTree<T> getParent() {
		return parent;
	}

	public void setParent(TaxoTree<T> parent) {
		this.parent = parent;
	}

	public List<TaxoTree<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TaxoTree<T>> children) {
		this.children = children;
	}

	/**
	 * Converts to string using tabs to indicate tree levels.
	 * 
	 * @return a string with tabs representing tree levels
	 */
	public String toStringRecursive() {
		int l = this.getLevel();
		System.out.println(l);
		String tabsStr = "";
		for (int i = 0; i < l; ++i) {
			tabsStr += "\t";
		}
		String childrenStr = "";
		for (int i = 0; i < this.children.size(); i++) {
			childrenStr += "\n" + this.children.get(i).toStringRecursive();
		}
		return tabsStr + this.data + childrenStr;
	}

	@Override
	public String toString() {
		if (this.data == null) {
			return "";
		}
		return this.data.toString();
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		this.children.add(index, (TaxoTree<T>) child);
	}

	@Override
	public void remove(int index) {
		this.children.remove(index);
	}

	@Override
	public void remove(MutableTreeNode node) {
		this.children.remove(node);
	}

	@Override
	public void setUserObject(Object object) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void removeFromParent() {
		this.parent.children.remove(this);
		this.parent = null;
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		this.parent = (TaxoTree<T>) newParent;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return this.children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return this.children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return this.children.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return children.isEmpty();
	}

	@Override
	public Enumeration children() {
		return Collections.enumeration(this.children);
	}
}
