/**
 * created the 1 oct. 07
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling;

import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjTreeT;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjTreeTL;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 *
 */
public class DisjRules {


	/** data structure used for sorting tasks. */
	private final LinkedList<Integer> tasks;

	/** an other data structure used for sorting tasks. */
	private final DisjQueue queue;


	private final ITasksSet tset;



	/** The data structure used for Not-First/Not Last, overload checking and detectable precedence rules. */
	protected final DisjTreeT disjTreeT;

	/** The data structure used for EdgeFinding rule. */
	protected final DisjTreeTL disjTreeTL;


	/** The lst comp. */
	private final LatestStartingTimeComparator lstComp;

	/** The lct comp. */
	private final LatestCompletionTimeComparator lctComp;

	/** The est comp. */
	private final EarliestStartingTimeComparator estComp;

	/** The time comp. */
	private final EarliestCompletionTimeComparator ectComp;


	/**
	 * Instantiates a new disjunctive.
	 * @param tasksSet TODO
	 * @param vars the vars the tasks involved in the constraint
	 * @param processingTimes their processing times
	 */
	public DisjRules(ITasksSet tasksSet,IntDomainVar[] vars, int[] processingTimes) {
		tset=tasksSet;
		tasks=new LinkedList<Integer>();
		for (int i = 0; i < vars.length; i++) {tasks.add(i);}
		queue=new DisjQueue(tasks);
		ectComp=new EarliestCompletionTimeComparator();
		lctComp=new LatestCompletionTimeComparator();
		estComp=new EarliestStartingTimeComparator();
		lstComp=new LatestStartingTimeComparator();
		disjTreeT=new DisjTreeT(this.tset,AbstractTree.ECT_TREE);
		disjTreeTL=new DisjTreeTL(this.tset,AbstractTree.ECT_TREE);
	}



	private List<Point> intialize(final Comparator<Integer> taskComp,final Comparator<Integer> queueComp) {
		if(taskComp!=null) {Collections.sort(tasks,taskComp);}
		this.queue.sort(queueComp);
		return new LinkedList<Point>();

	}


	//****************************************************************//
	//********* Overload checking *************************************//
	//****************************************************************//


	/**
	 * Overload checking rule.
	 *
	 */
	public boolean overloadChecking() {
		Collections.sort(tasks, lctComp);
		for(Integer i : tasks) {
			disjTreeT.insert(i);
			if(disjTreeT.getTime()>tset.getLCT(i)) {return false;}
		}

		return true;
	}

//	rajouter optimisation article
	/**
	 * NotFirst rule.
	 *
	 */

	//****************************************************************//
	//********* NotFirst/NotLast *************************************//
	//****************************************************************//


	//TODO optimisation papier
	public List<Point> notFirst() {
		final List<Point> newInfs=this.intialize(Collections.reverseOrder(estComp), Collections.reverseOrder(ectComp));
		int j=-1;
		for (Integer i : tasks) {
			while(!queue.isEmpty()  && tset.getEST(i) <tset.getECT(queue.getFirst())) {
				j=queue.remove();
				disjTreeT.insert(j);
			}
			final boolean rm=disjTreeT.remove(i);
			if(disjTreeT.getTime()<tset.getECT(i) && tset.getEST(i)<tset.getECT(j)){
					newInfs.add(new Point(i,tset.getECT(j)));
			}
			if(rm) {disjTreeT.insert(i);}

		}
		return newInfs;
	}


	/**
	 * NotLast rule.
	 *
	 */
	public List<Point> notLast() {
		final List<Point> newSups=intialize(lctComp, lstComp);
		int j=-1;
		for (Integer i : tasks) {
			//update tree
			while(!queue.isEmpty() && tset.getLCT(i)>tset.getLST(queue.getFirst())) {
				j=queue.remove();
				disjTreeT.insert(j);
			}
			//compute pruning
			disjTreeT.remove(i);
			if(disjTreeT.getTime()>tset.getLST(i)) {
				newSups.add(new Point(i,tset.getLST(j)));
			}
			disjTreeT.insert(i);
		}
		return newSups;

	}

	//****************************************************************//
	//********* detectable Precedence*********************************//
	//****************************************************************//
	/**
	 * DetectablePrecedence rule.
	 *
	 */
	public List<Point> detectablePrecedenceEST() {
		final List<Point> newInfs=this.intialize(ectComp, lstComp);
		for (Integer i : tasks) {
			while( !queue.isEmpty() && tset.getECT(i)>tset.getLST(queue.getFirst())) {
				disjTreeT.insert(queue.remove());
			}
			final boolean rm=disjTreeT.remove(i);
			if(tset.getEST(i)<disjTreeT.getTime()) {
				newInfs.add(new Point(i,disjTreeT.getTime()));
			}
			//we have to be sure that i was active in disjTreeT
			if(rm) {disjTreeT.insert(i);}
		}
		return newInfs;
	}

	/**
	 * symmetric DetectablePrecedence rule.
	 *
	 */
	public List<Point> detectablePrecedenceLCT() {
		final List<Point> newSups=this.intialize(Collections.reverseOrder(lstComp), Collections.reverseOrder(ectComp));
		for (Integer i : tasks) {
			while(!queue.isEmpty() && tset.getLCT(i)<=tset.getECT(queue.getFirst())) {
				disjTreeT.insert(queue.remove());
			}
			final boolean rm=disjTreeT.remove(i);
			newSups.add(new Point(i,disjTreeT.getTime()));
			//we have to be sure that i was active in disjTreeT
			if(rm) {disjTreeT.insert(i);}
		}
		return newSups;

	}

	//****************************************************************//
	//********* Edge Finding *****************************************//
	//****************************************************************//



	/**
	 * EdgeFinding rule.
	 *
	 */
	public List<Point> edgeFindingEST() {
		final List<Point> newInfs=this.intialize(null, Collections.reverseOrder(lctComp));
		int j=queue.getFirst();
		if(disjTreeTL.getTime()>tset.getLCT(j)) {return null;}//erreur pseudo-code papier on ne traite pas la tete de la queue sinon
		do {
			disjTreeTL.rmThetaAndInsertLambda(j);
			queue.remove();
			if(!queue.isEmpty()) {j=queue.getFirst();}
			else {break;}
			if(disjTreeTL.getTime()>tset.getLCT(j)) {return null;}
			while(disjTreeTL.getGrayTime()>tset.getLCT(j)) {
				final int i=disjTreeTL.getGrayResponsibleTask();
				if(disjTreeTL.getTime()>tset.getEST(i)) {
					newInfs.add(new Point(i,disjTreeTL.getTime()));
				}
				disjTreeTL.rmLambda(i);
			}
		} while (!queue.isEmpty());
		return newInfs;
	}


	/**
	 * symmetric EdgeFinding rule.
	 *
	 */
	public List<Point> edgeFindingLCT()  {
		final List<Point> newSups=this.intialize(null, estComp);
		int j=queue.getFirst();
		if(disjTreeTL.getTime()<tset.getEST(j)) {return null;}
		do {
			disjTreeTL.rmThetaAndInsertLambda(j);
			queue.remove();
			if(!queue.isEmpty()) {j=queue.getFirst();}
			else {break;}
			if(disjTreeTL.getTime()<tset.getEST(j)) {return null;}
			while(disjTreeTL.getGrayTime() <tset.getEST(j)) {
				final int i=disjTreeTL.getGrayResponsibleTask();
				newSups.add(new Point(i,disjTreeTL.getTime()));
				disjTreeTL.rmLambda(i);
			}
		} while (!queue.isEmpty());
		return newSups;
	}


	//******************************************************************//
	//********** COMPARATOR *******************************************//
	//****************************************************************//


	/**
	 * an abstract class comparator. It is used to implement all tasks comparator.
	 */
	protected abstract class AbstractTaskComparator implements Comparator<Integer> {


		/**
		 * Computes the comparison criteria for a task.
		 *
		 * @param o the index of the task
		 *
		 * @return the value of the criteria
		 */
		public abstract int getValue(Integer o);


		/**
		 * Use the criteria defined by {@link AbstractTaskComparator#getValue(Integer)} to compare two task indices.
		 *
		 * @param o1 the o1
		 * @param o2 the o2
		 *
		 * @return the int
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(final Integer o1,final  Integer o2) {
			final int c1=getValue(o1);
			final int c2=getValue(o2);
			if(c1<c2) {return -1;}
			else if(c1>c2){return 1;}
			else {return 0;}
		}
	}


	/**
	 * The Class EarliestCompletionTimeComparator.
	 */
	protected class EarliestCompletionTimeComparator extends AbstractTaskComparator {

		@Override
		public int getValue(final Integer o) {
			return tset.getECT(o);
		}

	}

	/**
	 * The Class LatestCompletionTimeComparator.
	 */
	protected class LatestCompletionTimeComparator extends AbstractTaskComparator{

		@Override
		public int getValue(final Integer o) {
			return tset.getLCT(o);
		}
	}

	/**
	 * The Class EarliestStartingTimeComparator.
	 */
	protected class EarliestStartingTimeComparator extends AbstractTaskComparator {

		@Override
		public int getValue(final Integer o) {
			return tset.getEST(o);
		}

	}

	/**
	 * The Class LatestStartingTimeComparator.
	 */
	protected class LatestStartingTimeComparator extends AbstractTaskComparator{

		@Override
		public int getValue(final Integer o) {
			return tset.getLST(o);
		}

	}


}
/**
 * The Class DisjQueue uses a fixed linkedList as a queue.no add operation.
 * a constant item is better for memory performance (atleast in theory).
 */
class DisjQueue {

	/** The queue. */
	private  final LinkedList<Integer> queue;

	/** The queue's iterator. */
	private ListIterator<Integer> iter;

	/**
	 * Instantiates a new disj queue.
	 *
	 * @param tasks the tasks of he queue
	 */
	public DisjQueue(LinkedList<Integer> tasks) {
		queue=new LinkedList<Integer>(tasks);
	}

	/**
	 * Sort.
	 *
	 * @param comparator the task's comparator
	 */
	public void sort(final Comparator<Integer> comparator) {
		Collections.sort(queue, comparator);
		iter=queue.listIterator();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return !iter.hasNext();
	}

	/**
	 * Gets the first element.
	 *
	 * @return the first
	 */
	public Integer getFirst() {
		iter.next();
		return iter.previous();
	}

	/**
	 * Remove first element.
	 *
	 * @return the integer
	 */
	public Integer remove() {
		return iter.next();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return queue.toString();
	}


}