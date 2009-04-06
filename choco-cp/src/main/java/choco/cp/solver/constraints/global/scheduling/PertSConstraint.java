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
package choco.cp.solver.constraints.global.scheduling;



import gnu.trove.TIntArrayList;
import choco.kernel.common.opres.graph.DagDTC;
import choco.kernel.common.opres.graph.GraphDTC;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBool;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntProcedure;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.IPrecedenceNetwork;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class PertSConstraint extends AbstractResourceSConstraint implements IPrecedenceNetwork {

	private boolean initialContradiction = false;

	protected final IStateBool propagationControlMakespan;

	protected final IStateBool propagationControlInf;

	protected final IStateBool propagationControlSup;

	protected final DagDTC network;

	private final PrecedenceStack precStack;

	private final int[] allpaths;



	public PertSConstraint(Solver solver, IntDomainVar uppBound) {
		super("PERT",createTaskVarArray(solver), uppBound);
		final int n = getNbTasks();
		network = new DagDTC(n);
		network.setTransitiveArcAdded(false);
		IEnvironment env = solver.getEnvironment();
		this.precStack = new PrecedenceStack(env, 3*n);
		propagationControlInf = env.makeBool(true);
		propagationControlSup = env.makeBool(true);
		propagationControlMakespan = env.makeBool(false);
		allpaths = new int[n];
	}
	

	@Override
	public void awake() throws ContradictionException {
		if(initialContradiction) {this.fail();}
		super.awake();
	}


	public final void propagateLowerBounds() throws ContradictionException {
		final int[] order= network.getTopologicalOrder();
		for (int i = 0; i < order.length; i++) {
			final int idx=order[i];
			allpaths[idx]=this.taskvars[idx].getEST();
			TIntArrayList tmp = network.getPredecessors(idx);
			for (int j = 0; j < tmp.size(); j++) {
				final int orig = tmp.get(j);
				updateDuration(orig,idx);
				allpaths[idx] = Math.max(allpaths[idx], allpaths[orig]+ taskvars[orig].getMinDuration());
			}
			if(taskvars[idx].start().updateInf(allpaths[idx], getCIndiceStart(idx))) {
				updateCompulsoryPart(idx);
			}
		}
		propagationControlInf.set(false);
	}

	protected void updateDuration(final int i, final int j) throws ContradictionException {
		IntDomainVar d = taskvars[i].duration();
		if(! taskvars[i].duration().isInstantiated()) {
			d.updateSup( taskvars[j].getLST() - taskvars[i].getEST(), getCIndiceDuration(i));
		}
	}

	public final void propagateUpperBounds() throws ContradictionException {
		final int[] order= network.getTopologicalOrder();
		for (int i = order.length-1; i >=0; i--) {
			final int idx=order[i];
			allpaths[idx]=this.taskvars[idx].getLST();
			TIntArrayList tmp = network.getSuccessors(idx);
			for (int j = 0; j < tmp.size(); j++) {
				final int dest = tmp.get(j);
				updateDuration(idx, dest);
				allpaths[idx] = Math.min(allpaths[idx], allpaths[dest]- taskvars[idx].getMinDuration());
			}
			if(taskvars[idx].start().updateSup(allpaths[idx], getCIndiceStart(idx))) {
				updateCompulsoryPart(idx);
			}
		}
		propagationControlMakespan.set(false);
		propagationControlSup.set(false);
	}




	@Override
	public void awakeOnInf(int varIdx) throws ContradictionException {
		if(varIdx < taskIntVarOffset) {
			updateCompulsoryPart(varIdx % getNbTasks() );
			propagationControlInf.set(true);
			this.constAwake(false);
		}
	}


	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		if(idx < taskIntVarOffset) {
			updateCompulsoryPart(idx % getNbTasks() );
			propagationControlInf.set(true);
			propagationControlSup.set(true);
		}else {propagationControlMakespan.set(true);}
		this.constAwake(false);
	}


	@Override
	public void awakeOnSup(int varIdx) throws ContradictionException {
		if(varIdx < taskIntVarOffset) {
			updateCompulsoryPart(varIdx % getNbTasks() );
			propagationControlSup.set(true);
		}else {propagationControlMakespan.set(true);}
		this.constAwake(false);
	}


	@Override
	public void propagate() throws ContradictionException {
		if (propagationControlInf.get()) {
			propagateLowerBounds();
		}
		if(propagationControlSup.get() ||propagationControlMakespan.get()) {
			propagateUpperBounds();
		}		
	}




	@Override
	public void addStaticPrecedence(TaskVar t1, TaskVar t2) {
		final int i = t1.getID();
		final int j = t2.getID();
		final boolean initialWorld = getSolver().getEnvironment().getWorldIndex() == 0;
		switch(network.add(i, j)) {
		case GraphDTC.ADDED : break;
		case GraphDTC.CYCLE : {
			if(initialWorld) {initialContradiction=true;}
			else {throw new SolverException("cant add static precedence which create a contradiction during search");}
			break;
		}
		case GraphDTC.EXISTING : {
			if(initialWorld) {System.err.println("duplicate precedence posting");}
			else {throw new SolverException("cant add duplicate static precedence during search");}
			break;
		}
		case GraphDTC.TRANSITIVE : {
			if(initialWorld) {System.err.println("transitive static precedence not added");}
			else {throw new SolverException("cant add static transitive precedence during search");}
			break;
		}
		default : throw new SolverException("invalid precedence posting");
		}
	}



	@Override
	public void firePrecedenceAdded(TaskVar t1, TaskVar t2) throws ContradictionException {
		//CPSolver.flushLogs();
		final int i = t1.getID();
		final int j = t2.getID();
		switch(network.add(i, j)) {
		case GraphDTC.ADDED : {
			precStack.store(i, j);
			break;
		}
		case GraphDTC.CYCLE : this.fail();break;
		case GraphDTC.EXISTING : throw new SolverException("duplicate precedence posting");
		case GraphDTC.TRANSITIVE : throw new SolverException("transitive precedence posting");
		default : throw new SolverException("invalid precedence posting");
		}
	}

	@Override
	public boolean isConnected(TaskVar t1, TaskVar t2) {
		return network.isTransitive(t1.getID(), t2.getID());
	}

	@Override
	public boolean isOrdered(TaskVar t1, TaskVar t2) {
		final int i = t1.getID();
		final int j = t2.getID();
		return network.isTransitive(i,j) || network.isTransitive(j, i);
	}



	@Override
	public String toDotty() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < taskvars.length; i++) {
			if(  ! network.isDisconnected(i)) {
				buffer.append(taskvars[i].toDotty()).append('\n');
			}
		}
		buffer.append(network.toDotty());
		return new String(buffer);
	}



	final class PrecedenceStack implements IStateIntProcedure {

		protected int[] origin;

		protected int[] destination;

		protected IStateInt nbStoredPrec;

		public PrecedenceStack(IEnvironment env, int initialSize) {
			super();
			origin = new int[initialSize];
			destination = new int[initialSize];
			nbStoredPrec = env.makeIntProcedure(this, 0);
		}

		public void store(int i, int j) {
			final int idx = nbStoredPrec.get();
			if(idx + 1 == origin.length) {resizeCapacity();}
			origin[idx] = i;
			destination[idx] = j;
			nbStoredPrec.add(1);
		}

		protected void resizeCapacity() {
			final int c = (origin.length * 3)/2 +1;
			int[] tmp = new int[c];
			System.arraycopy(origin, 0, tmp, 0, origin.length);
			origin = tmp;
			tmp = new int[c];
			System.arraycopy(destination, 0, tmp, 0, destination.length);
			destination = tmp;
		}

		@Override
		public void apply(int oldVal, int newVal) {
			for (int i = newVal; i < oldVal; i++) {
				network.remove(origin[i], destination[i]);
			}
		}

	}

}
