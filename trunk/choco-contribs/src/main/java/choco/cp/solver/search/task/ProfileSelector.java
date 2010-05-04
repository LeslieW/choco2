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
package choco.cp.solver.search.task;

import java.util.Iterator;
import java.util.List;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.VarValPairSelector;
import choco.kernel.solver.variables.scheduling.ITask;



public final class ProfileSelector implements VarValPairSelector {

	private final OrderingValSelector precSelector;

	private final IPrecedenceStore precStore;

	private final ProbabilisticProfile profiles;

	public final IResource<?>[] rscL;

	public ProfileSelector(Solver solver, IResource<?>[] resources, IPrecedenceStore precStore, OrderingValSelector precSelector) {
		super();
		this.precStore = precStore;
		this.precSelector = precSelector;
		profiles = new ProbabilisticProfile(solver);
		profiles.precStore = precStore;
		rscL = resources;
	}
	
	public ProfileSelector(Solver solver, IPrecedenceStore precStore, OrderingValSelector precSelector) {
		super();
		this.precStore = precStore;
		this.precSelector = precSelector;
		profiles = new ProbabilisticProfile(solver);
		profiles.precStore = precStore;
		rscL = new IResource<?>[solver.getModel().getNbConstraintByType(ConstraintType.DISJUNCTIVE)];
		Iterator<Constraint> iter = solver.getModel().getConstraintByType(ConstraintType.DISJUNCTIVE);
		int cpt = 0;
		while(iter.hasNext()) {
			rscL[cpt++] = (IResource<?>) solver.getCstr(iter.next());
		}
	}
	
	@Override
	public IntVarValPair selectVarValPair() throws ContradictionException {
		//compute maximal contention point
		profiles.initializeEvents();
		profiles.computeMaximum(rscL);
		//find best task pair
		List<ITask> taskL = profiles.getMaxProfInvolved();
		int c = profiles.getMaxProfileCoord();
		ITask st1 = null,st2 = null;
		if(taskL.size()>1) {
			double maxContrib = Double.MIN_VALUE;
			for (int i = 0; i < taskL.size(); i++) {
				final ITask t1 =  taskL.get(i);
				final double contribT1 = profiles.getIndividualContribution(t1,c);
				for (int j = i+1; j < taskL.size(); j++) {
					final ITask t2 =  taskL.get(j);
					final double contrib = contribT1 + profiles.getIndividualContribution(t2,c);
					if(contrib > maxContrib && precStore.isReified(t1, t2)) {
						st1 = t1; 
						st2 = t2;
						maxContrib = contrib;
					}
				}
			}
			if(st1 != null) {
				final IPrecedence prec = precStore.getStoredPrecedence(st1, st2);
				return new IntVarValPair(prec.getBoolVar(), precSelector.getBestVal(prec));
			}
		}
		if(!precStore.containsReifiedPrecedence()) {
			System.out.println();
		}
        //assert(!precStore.containsReifiedPrecedence());
		return null;
	}


}