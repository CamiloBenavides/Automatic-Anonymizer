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
 *
 * @author davl3232
 */
public class TaxoTree<T> implements MutableTreeNode {

	private T data;
	private TaxoTree<T> parent;
	private List<TaxoTree<T>> children;

	public TaxoTree() {
		this.parent = null;
		this.data = null;
		this.children = new ArrayList<TaxoTree<T>>();
	}

	public TaxoTree(T rootData) {
		this.parent = null;
		this.data = rootData;
		this.children = new ArrayList<TaxoTree<T>>();
	}

	public TaxoTree(T data, TaxoTree<T> parent) {
		this.parent = parent;
		this.data = data;
		this.children = new ArrayList<TaxoTree<T>>();
	}

	private TaxoTree<T> getParentAtHeight(int currH, int h) {
		if (currH == h) {
			return this;
		}
		return this.parent.getParentAtHeight(currH + 1, h);
	}

	public TaxoTree<T> getParentAtHeight(int h) {
		int currH = this.getHeight();
		return this.getParentAtHeight(currH, h);
	}

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

	public int getLevel() {
		if (this.parent == null) {
			return 0;
		}
		return 1 + this.parent.getLevel();
	}

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

	public void buildFromLineage(List<T> lineage) {
		T d = lineage.get(lineage.size() - 1);
		lineage.remove(lineage.size() - 1);
//		if (this.data != null && !this.data.equals(d)) {
//			throw new RuntimeException("Linaje no válido.");
//		}
		this.data = d;
		this.addLineage(lineage);
	}

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
			c = new TaxoTree<T>(d, this);
			this.children.add(c);
		}
		c.addLineage(lineage);
	}

	public void addChild(T data) {
		TaxoTree<T> child = new TaxoTree<T>(data, this);
		this.children.add(child);
	}
	public void addChild(TaxoTree<T> c) {
		this.children.add(c);
	}

	public void addChildren(Collection<? extends TaxoTree<T>> c) {
		this.children.addAll(c);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

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
