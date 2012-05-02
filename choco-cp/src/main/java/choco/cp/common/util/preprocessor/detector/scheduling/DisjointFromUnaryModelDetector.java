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

import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

public class DisjointFromUnaryModelDetector extends AbstractRscDetector {

	public DisjointFromUnaryModelDetector(CPModel model,
			DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.DISJUNCTIVE;
	}

	protected boolean isDisjoint(PPResource rsc, int i, int j) {
		return true;
	}		
	
	@Override
	protected final void apply(PPResource rsc) {
		final int n = rsc.getParameters().getNbRegularTasks();
		for (int i = 0; i < n; i++) {
			final TaskVariable t1 = rsc.getTask(i);
			for (int j = i+1; j < n; j++) {
				final TaskVariable t2 = rsc.getTask(j);
				if( ! disjMod.containsRelation(t1, t2) && isDisjoint(rsc, i, j)) {
					IntegerVariable dir = Choco.makeBooleanVar(StringUtils.dirRandomName(t1.getName(), t2.getName()));
					TemporalConstraint c = (TemporalConstraint) Choco.precedenceDisjoint(t1, t2, dir);
					disjMod.addEdge(t1.getHook(), t2.getHook(), c);
					add(dir);add(c);
				}
			}
		}
	}
}