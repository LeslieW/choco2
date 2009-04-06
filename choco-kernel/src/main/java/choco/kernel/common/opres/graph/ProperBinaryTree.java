/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  Â°(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.common.opres.graph;

import static choco.kernel.common.util.MathUtil.isPowerOfTwo;


/**
 * @author Arnaud Malapert</br> 
 * @since 5 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class ProperBinaryTree implements ITree {

	private int nodeCount = 0;

	private IBinaryNode root;

	private int nbLeafs = 0;

	private int depth = -1;

	public ProperBinaryTree() {
		super();
	}

	protected void setRoot(IBinaryNode node) {
		node.setFather(null);
		root = node;
	}


	@Override
	public int getNbInternalNodes() {
		return getNbLeaves() - 1;
	}

	public final int getNbLeaves() {
		return nbLeafs;
	}
	
	public final int getDepth() {
		return depth;
	}

	public IBinaryNode insert(INodeLabel leafStatus, INodeLabel internalStatus, boolean fireChanged) {
		final IBinaryNode leaf = new Leaf(nodeCount++, leafStatus);
		if(root == null) {
			setRoot(leaf);
			depth = 0;
		} else {
			int cpt = nbLeafs;
			int icpt = 1 << depth;
			IBinaryNode subtree = root;
			while ( ! isPowerOfTwo(cpt)) {
				while(icpt >cpt ) {
					icpt =  icpt >> 1;
				}
				cpt -=  icpt;
				subtree = subtree.getRightChild();
			}
			IBinaryNode internal =new InternalNode(nodeCount++, internalStatus);
			if(subtree.hasFather()) {
				subtree.getFather().setRightChild(internal);
			}else {
				setRoot(internal);
				depth++;
			}
			internal.setLeftChild(subtree);
			internal.setRightChild(leaf);
		}
		nbLeafs++;
		if(fireChanged) {leaf.fireStatusChanged();}
		return leaf;
	}

	protected boolean isLeftOrRight(IBinaryNode node) {
		if(node.hasFather()) {
			if(node.getFather().getLeftChild() == node) {return true;}
			else if(node.getFather().getRightChild() == node) {return false;}
		}
		throw new UnsupportedOperationException("root node or inconsistent data structure");
	}


	public void removeLast(boolean fireChanged) {
		if(root.isLeaf()) {
			root.clear();
			root = null;
			depth = -1;
		}else {
			//get the last leaf (right)
			//log(n)
			IBinaryNode last = root;
			while( ! last.isLeaf()) {
				last = last.getRightChild();
			}
			//performs operation (constant time)
			IBinaryNode irm= last.getFather();
			//remove the internal node
			if(irm.hasFather()) {
				IBinaryNode grandfather = irm.getFather();
				grandfather.setRightChild(irm.getLeftChild());
				if(fireChanged) {grandfather.getRightChild().fireStatusChanged();}
			}else {
				setRoot(irm.getLeftChild());
				depth--;
			}
			last.clear();
			irm.clear();
		}
		nbLeafs--;
	}

	/**
	 * remove a leaf from the tree.
	 * throws {@link UnsupportedOperationException} if the argument is an internal node or is not in the tree.
	 * @param leaf 
	 * @param fireChanged
	 */
	public void remove(IBinaryNode leaf, boolean fireChanged) {
		if(leaf.isLeaf()) {
			if(leaf.equals(root)) {
				root = null;
				depth = -1;
			}else {
				//check that the leaf belongs to the tree
				//compare root log(n)
				IBinaryNode lroot = leaf;
				while(lroot.hasFather()) {
					lroot = lroot.getFather();
				}
				if( lroot.equals(root)) {
					//get the last leaf (right)
					//log(n)
					IBinaryNode last = root;
					while( ! last.isLeaf()) {
						last = last.getRightChild();
					}
					//performs operation (constant time)
					IBinaryNode irm= last.getFather();
					if( ! last.equals(leaf)) {
						//incomplete swap: put the last leaf at the empty position
						if (isLeftOrRight(leaf)) {leaf.getFather().setLeftChild(last);}
						else {leaf.getFather().setRightChild(last);}
						if(fireChanged) {last.fireStatusChanged();}
					}
					//remove the internal node
					if(irm.hasFather()) {
						IBinaryNode grandfather = irm.getFather();
						grandfather.setRightChild(irm.getLeftChild());
						if(fireChanged) {grandfather.getRightChild().fireStatusChanged();}
					}else {
						setRoot(irm.getLeftChild());
						depth--;
					}
					irm.clear();
				} else {
					throw new UnsupportedOperationException("can't remove: the leaf does not belong to the tree..");
				}
			}
			leaf.clear();
			nbLeafs--;
		} else {
			throw new UnsupportedOperationException("cant remove an internal node from a proper binary tree.");
		}

	}

	protected void fireTreeChanged(IBinaryNode node) {
		if( ! node.isLeaf()) {
			fireTreeChanged(node.getLeftChild());
			fireTreeChanged(node.getRightChild());
			node.getNodeStatus().updateInternalNode(node);
		}
	}

	public void fireTreeChanged() {
		if(root !=null) {fireTreeChanged(root);}
	}


	public final IBinaryNode getRoot() {
		return root;
	}


}


abstract class AbstractBinaryNode implements IBinaryNode {

	public final int id;

	protected IBinaryNode father;

	public final INodeLabel status;

	public AbstractBinaryNode(int id, INodeLabel status) {
		super();
		this.id = id;
		this.status = status;
	}

	@Override
	public final IBinaryNode getFather() {
		return father;
	}

	@Override
	public final int getID() {
		return id;
	}

	@Override
	public final INodeLabel getNodeStatus() {
		return status;
	}

	@Override
	public final boolean hasFather() {
		return father != null;
	}

	@Override
	public final void setFather(IBinaryNode father) {
		this.father = father;

	}
	@Override
	public void clear() {
		this.father = null;
	}

	@Override
	public void fireStatusChanged() {
		if(hasFather()) {
			getFather().getNodeStatus().updateInternalNode(getFather());
			getFather().fireStatusChanged();
		}
	}

	private final void childToDotty(StringBuilder buffer, IBinaryNode child) {
		buffer.append(getID()).append("->").append(child.getID()).append('\n');
		buffer.append(child.toDotty());
	}

	@Override
	public String toDotty() {
		StringBuilder b = new StringBuilder();
		if(getNodeStatus() !=null) {
			String str = getNodeStatus().toDotty();
			if( ! str.isEmpty()) {
				b.append(getID()).append('[').append(str).append("]\n");	
			}
		}
		if(hasLeftChild()) {
			childToDotty(b, getLeftChild());
		}
		if(hasRightChild()) {
			childToDotty(b, getRightChild());
		}
		return new String(b);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getID());
		if(! isLeaf()) {
			b.append(" (");
			if(hasLeftChild()) {b.append(getLeftChild().getID());}
			else {b.append('-');}
			b.append(", ");
			if(hasRightChild()) {b.append(getRightChild().getID());}
			else {b.append('-');}
			b.append(')');	
		}
		return new String(b);
	}





}

class InternalNode extends AbstractBinaryNode {

	protected IBinaryNode leftChild;

	protected IBinaryNode rightChild;



	public InternalNode(int id, INodeLabel status) {
		super(id, status);
	}


	@Override
	public IBinaryNode getLeftChild() {
		return leftChild;
	}


	@Override
	public IBinaryNode getRightChild() {
		return rightChild;
	}


	@Override
	public boolean hasLeftChild() {
		return true;
	}

	@Override
	public boolean hasRightChild() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public void setLeftChild(IBinaryNode leftChild) {
		this.leftChild = leftChild;
		this.leftChild.setFather(this);

	}

	@Override
	public void setRightChild(IBinaryNode rightChild) {
		this.rightChild = rightChild;
		this.rightChild.setFather(this);
	}


	@Override
	public void clear() {
		super.clear();
		leftChild = null;
		rightChild = null;
	}


}



class Leaf extends AbstractBinaryNode {

	public Leaf(int id, INodeLabel status) {
		super(id, status);
	}

	@Override
	public IBinaryNode getLeftChild() {
		throw new UnsupportedOperationException("is a leaf of a proper binary tree");
	}

	@Override
	public IBinaryNode getRightChild() {
		throw new UnsupportedOperationException("is a leaf of a proper binary tree");
	}

	@Override
	public boolean hasLeftChild() {
		return false;
	}

	@Override
	public boolean hasRightChild() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}


	@Override
	public void setLeftChild(IBinaryNode leftChild) {
		throw new UnsupportedOperationException("can't add children to a leaf of the proper binary tree");

	}

	@Override
	public void setRightChild(IBinaryNode rightChild) {
		throw new UnsupportedOperationException("can't add children to a leaf of the proper binary tree");

	}


}

