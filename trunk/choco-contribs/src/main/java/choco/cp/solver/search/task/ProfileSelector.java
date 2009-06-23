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

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.search.integer.VarValPairSelector;
import choco.kernel.solver.variables.scheduling.ITask;



public class ProfileSelector implements VarValPairSelector {

	protected ValSelector valSelector;

	protected PrecValSelector precSelector;

	private final IPrecedenceStore precStore;

	private final ProbabilisticProfile profiles;

	public final IResource<?>[] rscL;

	protected ProfileSelector(Solver solver, IResource<?>[] resources, IPrecedenceStore precStore) {
		super();
		this.precStore = precStore;
		profiles = new ProbabilisticProfile(solver);
		profiles.precStore = precStore;
		rscL = resources;
	}
	
	protected ProfileSelector(Solver solver, IPrecedenceStore precStore) {
		super();
		this.precStore = precStore;
		profiles = new ProbabilisticProfile(solver);
		profiles.precStore = precStore;
		rscL = new IResource<?>[solver.getModel().getNbConstraintByType(ConstraintType.DISJUNCTIVE)];
		Iterator<Constraint> iter = solver.getModel().getConstraintByType(ConstraintType.DISJUNCTIVE);
		int cpt = 0;
		while(iter.hasNext()) {
			rscL[cpt++] = (IResource<?>) solver.getCstr(iter.next());
		}
	}

	public ProfileSelector(Solver solver, IPrecedenceStore precStore, ValSelector valSelector) {
		this(solver, precStore);
		this.valSelector = valSelector;
	}

	public ProfileSelector(Solver solver, IPrecedenceStore precStore, PrecValSelector precSelector) {
		this(solver, precStore);
		this.precSelector = precSelector;
	}

	public ProfileSelector(Solver solver,  IResource<?>[] resources, IPrecedenceStore precStore, ValSelector valSelector) {
		this(solver, resources, precStore);
		this.valSelector = valSelector;
	}

	public ProfileSelector(Solver solver,  IResource<?>[] resources, IPrecedenceStore precStore, PrecValSelector precSelector) {
		this(solver, resources, precStore);
		this.precSelector = precSelector;
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
				StoredPrecedence prec = precStore.getStoredPrecedence(st1, st2);
				if(precSelector == null) {
					return new IntVarValPair(prec.direction, valSelector.getBestVal(prec.getDirection()));
				}else {
					return new IntVarValPair(prec.direction, precSelector.getBestVal(prec));
				}
			}
		}
		if(Choco.DEBUG && precStore.containsReifiedPrecedence()) {
			System.err.println("profile did not resolved all precedences !");
		}
		return null;
	}


}