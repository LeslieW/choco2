package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

/**
 * The Class AbstractTree. This class is used as interface and mainly for the construction of the two types of binary tree.
 * These two types of tree are balanced binary tree.
 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjTreeT DisjTreeTL
 * the leaves of the tree are all real tasks to schedule.
 * The internal nodes are all virtual tasks used for computation.
 * The tree is the smallest possible tree with n leaves.
 * It has the property numberOfLeaves= numberOfInternalNode+1.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 */
public abstract class AbstractTree {

	public static final  boolean ECT_TREE=true;

	public static final  boolean LST_TREE=false;

	private final AbstractLeafComparator leafCmpEST;

	private final AbstractLeafComparator leafCmpLCT;

	/** The root node of the tree. */
	public AbstractNode root;

	protected final ITasksSet tasks;

	private Comparator<Leaf> leafCmp;

	/** The leaves. */
	protected final ArrayList<Leaf> leaves;

	/** The tasks permutation. tasks are sorted by EST values*/
	private final int[] tasksPermutation;

	/** The depth of the tree. */
	protected final int depth;

	protected boolean ectOrLst;
	/**
	 * Instantiates a new abstract tree.
	 * @param tasks the set of tasks represented by the tree
	 * @param ectOrLst the type of tree
	 */
	public AbstractTree(ITasksSet tasks,boolean ectOrLst) {
		this.tasks=tasks;
		leafCmpEST= new LeafComparatorEST(this.tasks);
		leafCmpLCT= new LeafComparatorLCT(this.tasks);
		leaves=new ArrayList<Leaf>(tasks.getNbTasks());
		createLeaves(this.tasks);
		setEctOrLst(ectOrLst);
		tasksPermutation=new int[leaves.size()];
		depth=computeTreeDepth(tasks.getNbTasks());
		createTree();
	}


	private final void createLeaves(final ITasksSet tasks) {
		for (int i = 0; i < tasks.getNbTasks(); i++) {
			leaves.add(new Leaf(i,createEmptyNodeInfo()));
		}
	}


	/**
	 * @return the ectOrLst
	 */
	public final boolean isEctOrLst() {
		return ectOrLst;
	}


	/**
	 * nodeInfo factory.
	 */
	protected abstract AbstractNodeInfo createEmptyNodeInfo();

	/**
	 * the inital leaf color. used in {@link AbstractTree#reset()}
	 *
	 */
	protected abstract int resetColor();

	public final  int getNumberOfLeaves() {
		return leaves.size();
	}

	public final  int getNumberOfInternalNodes() {
		return getNumberOfLeaves()-1;
	}

	/**
	 * set the type of the tree between ECT and LST tree
	 * @param ectOrLst the ectOrLst to set
	 */
	public final void setEctOrLst(final boolean ectOrLst) {
		this.ectOrLst = ectOrLst;
		this.leafCmp= this.ectOrLst ? leafCmpEST : leafCmpLCT;
	}


	/**
	 * sort the leaves using {@link AbstractTree#leafCmp}
	 */
	private final void sortLeaves() {
		Collections.sort(leaves,leafCmp);
		for (int i = 0; i < leaves.size(); i++) {
			tasksPermutation[leaves.get(i).task]=i;
		}
	}



	/**
	 * Creates the complete tree using the mathematic relation and recursion.
	 */
	private final void createTree() {
		sortLeaves();
		final ListIterator<Leaf> iter=leaves.listIterator();
		final Counter internalNodeCount=new Counter();
		root=createSubtree(iter, internalNodeCount, 1);
	}


	/**
	 * Creates the subtree.recursive method
	 *
	 * @param iter the iter the leaves iterator
	 * @param internalNodeCount the internal node counter
	 * @param currentDepth the current depth
	 *
	 * @return the root node of the subtree
	 */
	private final AbstractNode createSubtree(final ListIterator<Leaf> iter,final Counter internalNodeCount,final int currentDepth) {
		if(currentDepth==depth || internalNodeCount.getValue()==leaves.size()-1) {
			//add leaf
			final Leaf l=iter.next();
			l.reset(tasks, this.ectOrLst);
			l.setColor(this.resetColor());
			return l;
		}else {
			//add InternalNode
			final InternalNode node=new InternalNode(internalNodeCount.increment()+leaves.size(),
					createSubtree(iter, internalNodeCount, currentDepth+1),
					createSubtree(iter, internalNodeCount, currentDepth+1),
					createEmptyNodeInfo());
			node.setFather();
			node.reset(tasks, this.ectOrLst);
			return node;
		}


	}

	/**
	 * method used by {@link AbstractTree#updateSubtree(ListIterator, InternalNode)}
	 * @param iter the leaf iterator
	 * @return the updated leaf;
	 */
	private Leaf updateLeaf(final ListIterator<Leaf> iter) {
		final Leaf l=iter.next();
		l.reset(tasks, this.ectOrLst);
		l.setColor(this.resetColor());
		return l;
	}
	/**
	 * sort leaves and update tree.
	 * don't create new objects. Only modify the tree structure.
	 * @param iter the next leaf to insert
	 * @param currentInternalNode the current Internal node
	 */
	private final void updateSubtree(final ListIterator<Leaf> iter,final InternalNode currentInternalNode) {
		if(currentInternalNode.hasLeftChild()) {
			updateSubtree(iter, (InternalNode) currentInternalNode.getLeftChild());
			if(currentInternalNode.hasRightChild()) {
				updateSubtree(iter, (InternalNode) currentInternalNode.getRightChild());
			}else {
				currentInternalNode.rightChild=updateLeaf(iter);
				currentInternalNode.setFather();
			}
		}
		else {
			currentInternalNode.leftChild=updateLeaf(iter);
			currentInternalNode.rightChild=updateLeaf(iter);
			currentInternalNode.setFather();
		}
		currentInternalNode.reset(tasks, this.ectOrLst);

	}

	/**
	 * You must call this method to update tree when :
	 * <ul><li>you use {@link AbstractTree#setEctOrLst(boolean)}</li> <li>the domain of some tasks has changed </li> </ul>
	 *
	 */
	public void update() {
		if(leaves.size()==1) {this.reset();}
		else {
			for (int i = 0; i < leaves.size(); i++) {
				leaves.get(i).father.resetChildren();
			}
			sortLeaves();
			if (root instanceof InternalNode) {
				final InternalNode r = (InternalNode) root;
				updateSubtree(leaves.listIterator(),r);
			}
		}
	}


	/**
	 * Compute the tree's depth.
	 *
	 * @param nbLeaves the number leaves
	 *
	 * @return the depth
	 */
	protected static final int computeTreeDepth(final int nbLeaves) {
		return (int) Math.ceil(Math.log(nbLeaves)/Math.log(2))+1;
	}


	/**
	 * Gets the leaf associated to the task.
	 *
	 * @param task the task
	 *
	 * @return the leaf
	 */
	protected final Leaf getLeaf(final int task) {
		return leaves.get(tasksPermutation[task]);
	}


	/**
	 * Reset the tree structure.
	 */
	public final void reset() {
		root.reset(tasks, this.ectOrLst);
		for (Leaf leaf : leaves) {
			leaf.setColor(this.resetColor());
		}
	}

	/**
	 * To dot file.
	 * the full transformation to dot format
	 * @param pathname the dot file
	 */
	public final void toDotty(final String pathname) {
		try {
			final FileWriter fw=new FileWriter(pathname);
			fw.write("digraph G {\n");
			fw.write(root.toDotString());
			fw.write("\n}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

/**
 * The Class Counter. used to set virtual task of internal node
 */
class Counter {

	/**
	 * the counter of internal nodes
	 */
	private int theCounter;


	/**
	 *init to 0
	 */
	public Counter() {
		this(0);
	}
	/**
	 * init to a value
	 * @param initialValue the value
	 */
	public Counter(int initialValue) {
		super();
		theCounter=initialValue;
	}
	/**
	 * increment counter
	 * @return {@link Counter#theCounter}
	 */
	public final int increment() {
		theCounter++;
		return theCounter;
	}

	/**
	 * getter
	 * @return {@link Counter#theCounter}
	 */
	public final int getValue() {
		return theCounter;
	}
}