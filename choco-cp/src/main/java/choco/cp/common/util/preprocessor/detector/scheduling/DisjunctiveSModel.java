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

package choco.cp.common.util.preprocessor.detector.scheduling;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectProcedure;

import java.util.Arrays;
import java.util.Iterator;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class DisjunctiveSModel extends DisjunctiveGraph<ITemporalSRelation> {

	public final Solver solver;

	public DisjunctiveSModel(PreProcessCPSolver solver) {
		this(solver, solver.getDisjMod());
	}
	public DisjunctiveSModel(Solver solver, DisjunctiveModel dmod) {
		super(solver.getNbTaskVars());
		this.solver = solver;
		if(solver.getModel() != dmod.getModel()) throw new SolverException("cant build disjunctive model");
		final TIntIntHashMap hookToIndexM = new TIntIntHashMap(nbNodes);
		final Iterator<MultipleVariables> iter = solver.getModel().getMultipleVarIterator();
		while(iter.hasNext()) {
			final MultipleVariables mv = iter.next();
			if (mv instanceof TaskVariable) {
				final TaskVariable tv = (TaskVariable) mv;
				final TaskVar<?> t = solver.getVar(tv);
				hookToIndexM.put(tv.getHook(), t.getID());
			}
		}
		for (int i = 0; i < dmod.nbNodes; i++) {
			int o = hookToIndexM.get(i);
			for (int j = dmod.precGraph[i].nextSetBit(0); j >= 0; j = dmod.precGraph[i].nextSetBit(j + 1)) {
				final int d = hookToIndexM.get(j);
				if(dmod.containsArcConstraint(i, j)) addArc(o, d, dmod.setupTime(i, j));
			}
			for (int j = dmod.disjGraph[i].nextSetBit(0); j >= 0; j = dmod.disjGraph[i].nextSetBit(j + 1)) {
				if(dmod.containsConstraint(i, j)) {
					final int d = hookToIndexM.get(j);
					final ITemporalSRelation rel = (ITemporalSRelation) solver.getCstr(dmod.getConstraint(i, j));
					addEdge(o, d, dmod.setupTime(i, j), dmod.setupTime(j, i), rel);
				}
			}
		}
	}

	public final Solver getSolver() {
		return solver;
	}


	@Override
	public String toDotty() {
		final StringBuilder  b = new StringBuilder();
		final DisposableIterator<TaskVar> iter = solver.getTaskVarIterator();
		while(iter.hasNext()) {
			b.append(iter.next().toDotty()).append('\n');
		}
		iter.dispose();
		b.append(super.toDotty());
		return b.toString();
	}


	public static void main(String[] args) {
		TIntObjectHashMap<String> map = new TIntObjectHashMap<String>();
		String str = "test";
		map.put(2, str);
		map.put(3, str);
		map.forEachEntry(new TIntObjectProcedure<String>() {
			
			@Override
			public boolean execute(int arg0, String arg1) {
				System.out.println(arg1);
				return true;
			}
		});
		System.out.println(Arrays.toString(map.getValues()));
	}

}