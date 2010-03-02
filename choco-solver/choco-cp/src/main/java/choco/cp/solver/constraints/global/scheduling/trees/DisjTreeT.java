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

import choco.cp.solver.constraints.global.scheduling.trees.AbstractVilimTree.NodeType;
import choco.cp.solver.constraints.global.scheduling.trees.status.ThetaStatus;
import choco.kernel.common.opres.graph.IBinaryNode;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.List;


interface ThetaTreeLeaf {

	NodeType getType();

	void insertInTheta();

	void insertInTheta(IRTask rtask);

	void removeFromTheta();
}

abstract class AbstractThetaTree extends AbstractVilimTree implements IThetaTree {

	public AbstractThetaTree(final List<? extends ITask> tasks) {
		super(tasks);
	}


	private ThetaTreeLeaf getLeafStatus(final IBinaryNode node) {
		return  (ThetaTreeLeaf) node.getNodeStatus();
	}


	@Override
	public boolean insertInTheta(final ITask task) {
		final IBinaryNode leaf = getLeaf(task);
		final ThetaTreeLeaf status = getLeafStatus(leaf);
		if(status.getType() == AbstractVilimTree.NodeType.NIL) {
			status.insertInTheta();
			leaf.fireStatusChanged();
			return true;
		}
		return false;
	}



	@Override
	public final boolean insertInTheta(final IRTask rtask) {
		final IBinaryNode leaf = getLeaf(rtask.getHTask());
		final ThetaTreeLeaf status = getLeafStatus(leaf);
		if(status.getType() == AbstractVilimTree.NodeType.NIL) {
			status.insertInTheta(rtask);
			leaf.fireStatusChanged();
			return true;
		}
		return false;
	}


	@Override
	public final boolean removeFromTheta(final ITask task) {
		final IBinaryNode leaf = getLeaf(task);
		final ThetaTreeLeaf status = getLeafStatus(leaf);
		if(status.getType() == AbstractVilimTree.NodeType.THETA) {
			status.removeFromTheta();
			leaf.fireStatusChanged();
			return true;
		}
		return false;
	}
}


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public final class DisjTreeT extends AbstractThetaTree {

	public DisjTreeT(final List<? extends ITask> tasks) {
		super(tasks);
	}

	@Override
	public void insert(final ITask task) {
		insertTask(task, new DisjStatusT(NodeType.NIL), new DisjStatusT(NodeType.INTERNAL));
	}

	@Override
	public int getTime() {
		return ( (DisjStatusT) getRoot().getNodeStatus()).getStatus().getTime();
	}


	final class DisjStatusT extends AbstractVilimStatus<ThetaStatus> implements ThetaTreeLeaf {

		public DisjStatusT(final NodeType type) {
			super(type, new ThetaStatus());
		}

		public void insertInTheta() {
			setType(NodeType.THETA);
			getStatus().setTime( getMode().value() ? task.getEST()+task.getMinDuration()  : task.getLCT()-task.getMinDuration());
			getStatus().setDuration(task.getMinDuration());
		}


		@Override
		public void insertInTheta(final IRTask rtask) {
			this.insertInTheta();			
		}

		public void removeFromTheta() {
			setType(NodeType.NIL);
			getStatus().setTime( getResetIntValue(getMode()));
			getStatus().setDuration(0);
		}

		@Override
		public void reset() {
			removeFromTheta();
		}

		@Override
		protected void writeDotStatus(final StringBuilder buffer) {
			writeRow(buffer, getMode().label(), format(status.getTime()),"P", String.valueOf(status.getDuration()));
		}

		@Override
		public void updateInternalNode(final IBinaryNode node) {
			if(node.getLeftChild().getNodeStatus() instanceof DisjStatusT) {
				final ThetaStatus left = ( (DisjStatusT) node.getLeftChild().getNodeStatus()).getStatus();
				if(node.getRightChild().getNodeStatus() instanceof DisjStatusT) {
					final ThetaStatus right = ( (DisjStatusT) node.getRightChild().getNodeStatus()).getStatus();
					this.status.update(getMode(), left, right);
					return;
				}
			}
			throw new SolverException("cant update node");
		}
	}
}