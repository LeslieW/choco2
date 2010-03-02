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

import java.util.List;

import choco.cp.solver.constraints.global.scheduling.trees.AltDisjTreeTLTO.AltDisjStatusTLTO;
import choco.kernel.common.opres.graph.IBinaryNode;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

/**
 * @author Arnaud Malapert</br> 
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public final class AltDisjTreeTL extends DisjTreeTL {

	
	public AltDisjTreeTL(List<? extends ITask> tasks) {
		super(tasks);
	}
	
	public int getTaskType(IRTask rtask){
		final IBinaryNode leaf = getLeaf(rtask.getHTask());
		final AltDisjStatusTL status = (AltDisjStatusTL) leaf.getNodeStatus();
		switch(status.getType()){
		case THETA:
			return 1;
		case LAMBDA:
			return 2;
		case NIL:
			return 3;
		default:
			throw new SolverException("Leaf node has an invalid node type");
		}
	}
	public void initializeEdgeFinding(final TreeMode mode, final Iterable<IRTask> rtasks) {
		this.setMode(mode);
		for (IRTask rtask : rtasks) {
			if(rtask.isRegular()) {
				final IBinaryNode leaf = getLeaf(rtask.getHTask());
				final ThetaTreeLeaf status =  (ThetaTreeLeaf) leaf.getNodeStatus();
				if(status.getType() == AbstractVilimTree.NodeType.NIL) {
					status.insertInTheta();
				}else {
					throw new SolverException("cant initialize Alternative Edge finding TL Tree.");
				}
			}
		}
		fireTreeChanged();
	}
	
	@Override
	public void insert(final ITask task) {
		insertTask(task, new AltDisjStatusTL(NodeType.NIL), new DisjStatusTL(NodeType.INTERNAL));
	}


	protected final class AltDisjStatusTL extends DisjStatusTL {

		public AltDisjStatusTL(NodeType type) {
			super(type);
		}

		@Override
		public void reset() {
			removeFromTheta();
			status.setRespGrayTime(null);
			status.setRespGrayDuration(null);
		}
	}

}
