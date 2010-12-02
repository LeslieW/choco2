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

/* * * * * * * * * * * * * * * * * * * * * * * * * 
s *          _       _                            *
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

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectProcedure;

import java.util.HashSet;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;

public class PrecedenceStore implements ITemporalStore {

	private final static ReifiedChecker CHECK = new ReifiedChecker();

	private final static ReifiedCounter COUNT = new ReifiedCounter();

	private final TIntObjectHashMap<ITemporalSRelation> precMap = new TIntObjectHashMap<ITemporalSRelation>();

	private final int offset;

	public PrecedenceStore(int n) {
		super();
		offset = n;
	}

	protected int getTaskPairKey(ITask t1, ITask t2) {
		return t1.getID() < t2.getID() ? t1.getID() * offset + t2.getID() : t2.getID() * offset + t1.getID();
	}

	@Override
	public ITemporalSRelation getTemporalRelation(ITask t1, ITask t2) {
		return precMap.get(getTaskPairKey(t1, t2));
	}


	@Override
	public void addPrecedence(ITask t1, ITask t2, IntDomainVar direction) {
		final int key = getTaskPairKey(t1, t2);
		if(precMap.contains(key)) {
			throw new SolverException("duplicate or opposite precedence");
		}
		//FIXME precMap.put(key, new StoredPrecedence(t1, t2, direction));

	}

	
	@Override
	public boolean isReified(ITask t1, ITask t2) {
		final ITemporalSRelation prec = getTemporalRelation(t1, t2);
		if(prec == null) {return false;}
		return ! prec.getDirection().isInstantiated();
	}

	

	@Override
	public boolean containsReifiedPrecedence() {
		return !precMap.forEachValue(CHECK);
	}

	@Override
	public int getNbReifiedPrecedence() {
		COUNT.count=0;
		precMap.forEachValue(COUNT);
		return COUNT.count;
	}



	@Override
	public ITemporalSRelation[] getValues() {
		return precMap.getValues(new ITemporalSRelation[precMap.size()]);
	}

	@Override
	public String toDotty() {
		final DotProcedure td = new DotProcedure();
		precMap.forEachValue(td);
		return td.toString();
	}
	
	private static class ReifiedCounter implements TObjectProcedure<ITemporalSRelation> {

		public int count = 0;
		
		@Override
		public boolean execute(ITemporalSRelation arg0) {
			if(!arg0.getDirection().isInstantiated()) {count++;}
			return true;
		}
	}

	private static class ReifiedChecker implements TObjectProcedure<ITemporalSRelation> {

		@Override
		public boolean execute(ITemporalSRelation arg0) {
			return arg0.getDirection().isInstantiated();
		}
	}

	private static final class DotProcedure implements TObjectProcedure<ITemporalSRelation> {

		private final StringBuilder dotGraph = new StringBuilder();
		private final HashSet<ITask> nodeM = new HashSet<ITask>();
		
		
		public void clear() {
			dotGraph.delete(0, dotGraph.length());
			nodeM.clear();
		}
		
		private void writeNode(ITask t) {
			//dotGraph.append(t.toDotty()).append("\n");
		}

		private void writeArc(ITask t1, ITask t2) {
			//dotGraph.append(StringUtils.getDotArc(t2, t1));
		}
		
		
		@Override
		public boolean execute(ITemporalSRelation arg0) {
//			if( nodeM.add(arg0.getOrigin()) ) writeNode(arg0.getOrigin());
//			if( nodeM.add(arg0.getDestination()) ) writeNode(arg0.getDestination());
//			if(arg0.getDirection().isInstantiatedTo(0)) writeArc(arg0.getOrigin(), arg0.getDestination());
//			else if(arg0.getDirection().isInstantiatedTo(1)) writeArc(arg0.getDestination(), arg0.getOrigin());
//			else dotGraph.append(StringUtils.getDotEdge(arg0.getOrigin(), arg0.getDestination()));
//			dotGraph.append('\n');
			return true;
		}

		@Override
		public String toString() {
			return dotGraph.toString();
		}
		
		
	}
	
	

	
}
