/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
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
package choco.cp.solver.constraints.global.scheduling.trees;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import choco.kernel.common.opres.graph.IBinaryNode;
import choco.kernel.common.opres.graph.ProperBinaryTree;
import choco.kernel.common.util.TaskComparators;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.scheduling.ITask;




/**
 * @author Arnaud Malapert</br> 
 * @since 9 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractVilimTree extends ProperBinaryTree implements IVilimTree {

	enum NodeType {THETA, LAMBDA, NIL, INTERNAL}

	private TreeMode mode;

	private final Map<ITask, IBinaryNode> map;

	public AbstractVilimTree(List<? extends ITask> tasks) {
		this.map = new HashMap<ITask, IBinaryNode>(tasks.size());
		for (ITask task : tasks) {
			insert(task);
		}
	}

	protected  Comparator<ITask> getTaskComparator() {
		switch(mode) {
		case ECT : return TaskComparators.makeEarliestStartingTimeCmp(); //EST
		case LST : return TaskComparators.makeLatestCompletionTimeCmp(); //LCT
		default : return null;
		}
	}

	protected IBinaryNode getLeaf(ITask task) {
		return map.get(task);
	}

	protected void insertTask(ITask task, AbstractVilimStatus<?> leafStatus, AbstractVilimStatus<?> internalStatus) {
		leafStatus.setTask(task);
		IBinaryNode leaf = insert(leafStatus, internalStatus, false);
		map.put(task, leaf);		
		
	}

	public void reset() {
		for (IBinaryNode leaf : map.values()) {
			( (AbstractVilimStatus<?>) leaf.getNodeStatus() ).reset();
		}
		this.fireTreeChanged();
	}



	protected void applySort(IBinaryNode current, ListIterator<ITask> iter) {
		if(current.isLeaf()) {
			final ITask t = iter.next();
			AbstractVilimStatus<?> s = (AbstractVilimStatus<?>) current.getNodeStatus();
			s.setTask(t);
			s.reset();
			map.put(t, current);
		}else {
			applySort(current.getLeftChild(), iter);
			applySort(current.getRightChild(), iter);
			current.getNodeStatus().updateInternalNode(current);
		}
	}

	public void sort() {
		if(getNbLeaves()>1) {
			final ITask[] tmp = map.keySet().toArray(new ITask[map.keySet().size()]);
			Arrays.sort(tmp, getTaskComparator());
			map.clear(); //TODO avoid to clear the map.
			final ListIterator<ITask> iter = Arrays.asList(tmp).listIterator();
			applySort(getRoot(), iter);
			if(iter.hasNext()) {
				throw new SolverException("inconsitent vilim tree");
			}

		}
	}


	
	public final TreeMode getMode() {
		return mode;
	}

	@Override
	public void setMode(TreeMode mode) {
		this.mode = mode;
		sort();
	}


	@Override
	public boolean contains(ITask task) {
		return map.containsKey(task);
	}

	@Override
	public void remove(ITask task) {
		if(map.containsKey(task)) {
			remove(map.remove(task), false);
		}
	}


	@Override
	public String toDotty() {
		return getRoot().toDotty();
	}
	

}
