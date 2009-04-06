package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;




/**
 * The Class InternalNode. This class represents virtual task,<i>i.e.</i> it contains the NodeInfo's value for a subset of task.
 * his is an internal node of the tree, so it has exactly two children.
 */
public class InternalNode extends AbstractNode{

	/** The left child. */
	protected AbstractNode leftChild;

	/** The right child. */
	protected AbstractNode rightChild;



	/**
	 * The Constructor.
	 *
	 * @param task the virtual task associated to this node
	 * @param leftChild the left child
	 * @param rightChild the right child
	 */
	public InternalNode(int task, final AbstractNode leftChild, final AbstractNode rightChild,final AbstractNodeInfo infos) {
		super(task,infos);
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}




	@Override
	public void reset(final ITasksSet tasks, final boolean ectOrLst) {
		leftChild.reset(tasks, ectOrLst);
		rightChild.reset(tasks, ectOrLst);
		infos.reset(this,ectOrLst);

	}



	/**
	 * update the {@link AbstractNode#infos} and propagate to father
	 * @param ectOrLst the type of tree
	 */
	public final void update(final boolean ectOrLst) {
		infos.update(this,ectOrLst);
		if(hasFather()) {father.update(ectOrLst);}
	}



	/**
	 * Sets the father.
	 */
	protected final void setFather() {
		leftChild.setFather(this);
		rightChild.setFather(this);
	}
	/**
	 * assume that the right child is a leaf and check for the left child.
	 *
	 */
	protected final void resetChildren() {
		if (leftChild instanceof Leaf) {
			this.leftChild=null;

		}
		if (rightChild instanceof Leaf) {
			this.rightChild=null;
		}
	}

	/**
	 * Write dot links to it children.
	 *
	 * @param buf the ouput buffer
	 * @param father the father
	 * @param child the child
	 */
	protected final static void writeDotLinks(final StringBuffer buf,final AbstractNode father,final AbstractNode child) {
		buf.append(father.getDotName()).append("->").append(child.getDotName()).append(";\n");
	}


	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractNode#getDotStyle()
	 */
	@Override
	protected String getDotStyle() {
		return "rounded";
	}




	/**
	 * To dot string.
	 *
	 * @return the node and it subtree in dot format.
	 *
	 * @see AbstractNode#toDotString()
	 */
	@Override
	protected String toDotString() {
		final StringBuffer buf=new StringBuffer();
		buf.append(super.toDotString());
		writeDotLinks(buf, this, this.leftChild);
		buf.append(leftChild.toDotString());
		writeDotLinks(buf, this, this.rightChild);
		buf.append(rightChild.toDotString());
		return buf.toString();
	}

	public final boolean hasLeftChild() {
		return leftChild!=null;
	}

	public final boolean hasRightChild() {
		return rightChild!=null;
	}
	/**
	 * Gets the left child.
	 *
	 * @return the left child
	 */
	public final AbstractNode getLeftChild() {
		return leftChild;
	}


	/**
	 * Gets the right child.
	 *
	 * @return the right child
	 */
	public final AbstractNode getRightChild() {
		return rightChild;
	}




}
