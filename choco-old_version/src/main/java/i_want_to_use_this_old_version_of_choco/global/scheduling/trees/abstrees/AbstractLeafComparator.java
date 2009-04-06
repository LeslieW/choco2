package i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees;

import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;

import java.util.Comparator;



/**
 * the abstract Leaf comparator. used to compare leaf along a criteria.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
abstract class AbstractLeafComparator implements Comparator<Leaf> {


	protected final ITasksSet tasks;


	/**
	 * @param tasks
	 */
	public AbstractLeafComparator(ITasksSet tasks) {
		super();
		this.tasks = tasks;
	}

	/**
	 * get the comparison criteria value.
	 * @param leaf the concerned leaf
	 * @return the criteria
	 */
	public abstract long getCriteria(Leaf leaf);

	/**
	 * Compare.
	 *
	 * @param leaf1 the first leaf
	 * @param leaf2 the second leaf
	 *
	 *
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public int compare(final Leaf leaf1, final Leaf leaf2) {
		final long c1=getCriteria(leaf1);
		final long c2=getCriteria(leaf2);
		if(c1<c2) {return -1;}
		else if(c1>c2) {return 1;}
		else {
			if(leaf1.task<leaf2.task) {return -1;}
			else if(leaf1.task>leaf2.task) {return 1;}
			else {return 0;}
		}
	}

}

class LeafComparatorEST extends AbstractLeafComparator {


	/**
	 * @param tasks
	 */
	public LeafComparatorEST(ITasksSet tasks) {
		super(tasks);
	}

	@Override
	public long getCriteria(final Leaf leaf) {
		return tasks.getEST(leaf.task);
	}

}


class LeafComparatorLCT extends AbstractLeafComparator {


	/**
	 * @param tasks
	 */
	public LeafComparatorLCT(ITasksSet tasks) {
		super(tasks);
	}

	@Override
	public long getCriteria(final Leaf leaf) {
		return tasks.getLCT(leaf.task);
	}

}
