/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.cp.solver.search.task;

import java.util.Iterator;
import java.util.List;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.ITemporalRelation;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.VarValPairSelector;
import choco.kernel.solver.variables.scheduling.ITask;



public final class ProfileSelector implements VarValPairSelector {

	private final OrderingValSelector precSelector;

	private final ITemporalStore precStore;

	private final ProbabilisticProfile profiles;

	public final IResource<?>[] rscL;

	public ProfileSelector(Solver solver, IResource<?>[] resources, ITemporalStore precStore, OrderingValSelector precSelector) {
		super();
		this.precStore = precStore;
		this.precSelector = precSelector;
		profiles = new ProbabilisticProfile(solver);
		profiles.precStore = precStore;
		rscL = resources;
	}
	
	public ProfileSelector(Solver solver, ITemporalStore precStore, OrderingValSelector precSelector) {
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
				final ITemporalSRelation prec = precStore.getTemporalRelation(st1, st2);
				return new IntVarValPair(prec.getDirection(), precSelector.getBestVal(prec));
			}
		}
		assert(!precStore.containsReifiedPrecedence());
		return null;
	}


}