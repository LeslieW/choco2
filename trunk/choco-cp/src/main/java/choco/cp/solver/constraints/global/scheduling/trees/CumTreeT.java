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

import choco.cp.solver.constraints.global.scheduling.trees.status.ConsumptionStatus;
import choco.kernel.common.opres.graph.IBinaryNode;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;




/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public final class CumTreeT<T extends ITask> extends AbstractThetaTree {

	protected final ICumulativeResource<T> rsc;
	
	public CumTreeT(ICumulativeResource<T> rsc) {
		super(rsc.asList());
		this.rsc = rsc;
	}

	@Override
	public void insert(final ITask task) {
		insertTask(task, new CumStatusT(NodeType.NIL), new CumStatusT(NodeType.INTERNAL));
	}
	
	
	public long getEnergy() {
		return ( (CumStatusT) getRoot().getNodeStatus()).getStatus().getTime();
	}
	
	

	@Override
	public boolean insertInTheta(ITask task) {
		throw new UnsupportedOperationException("unauthorized operation");
	}

	@Override
	public int getTime() {
		throw new UnsupportedOperationException("energy is given instead of a time.");
	}

	
	@Override
	public void setMode(final TreeMode mode) {
		if(mode.value()) {
		super.setMode(mode);
		}else {
			throw new UnsupportedOperationException("unsupported tree mode:"+mode);
		}
	}


	final class CumStatusT extends AbstractVilimStatus<ConsumptionStatus> implements ThetaTreeLeaf {

		public CumStatusT(final NodeType type) {
			super(type, new ConsumptionStatus());
		}

		public void insertInTheta(final IRTask task) {
			setType(NodeType.THETA);
			final long cons = task.getMinConsumption();
			status.setTime( rsc.getMaxCapacity()*task.getTaskVar().getEST()+cons);
			status.setConsumption(cons);
			
		}
		
		public void insertInTheta() {
			throw new UnsupportedOperationException("cant insert without argument.");
		}

		public void removeFromTheta() {
			setType(NodeType.NIL);
			getStatus().setTime( getResetLongValue(getMode()));
			getStatus().setConsumption(0);
		}

		@Override
		public void reset() {
			removeFromTheta();
		}

		@Override
		protected void writeDotStatus(final StringBuilder buffer) {
			writeRow(buffer, "Energ.", format(status.getTime()),"C", String.valueOf(status.getConsumption()));
		}

		@Override
		public void updateInternalNode(final IBinaryNode node) {
			if(node.getLeftChild().getNodeStatus() instanceof CumTreeT.CumStatusT) {
				final ConsumptionStatus left = ( (CumStatusT) node.getLeftChild().getNodeStatus()).getStatus();
				if(node.getRightChild().getNodeStatus() instanceof CumTreeT.CumStatusT) {
					final ConsumptionStatus right = ( (CumStatusT) node.getRightChild().getNodeStatus()).getStatus();
					this.status.update(getMode(), left, right);
					return;
				}
			}
			throw new SolverException("cant update node");
		}
	}
}
