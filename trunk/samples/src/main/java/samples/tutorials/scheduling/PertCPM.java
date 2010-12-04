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

package samples.tutorials.scheduling;

import static choco.Choco.constantArray;
import static choco.Choco.makeTaskVar;
import static choco.Choco.startsAfterEnd;

import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.Options;
import choco.cp.common.util.preprocessor.detector.scheduling.DisjunctiveSModel;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.preprocessor.PreProcessConfiguration;
import choco.kernel.common.VisuFactory;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * The following example is inspired from an example of the Ilog Scheduler userguide.
 * The task network has been slightly modified.
 *
 * @author Arnaud Malapert
 *         Date : 2 déc. 2008
 *         Since : 2.0.1
 *         Update : 2.1.1
 */
public class PertCPM extends PatternExample {


	public final int nbTasks = 10;

	private final int horizon; // sum of durations (default)
	
	private TaskVariable masonry, carpentry, plumbing, ceiling, 
	roofing, painting, windows,facade, garden, moving;

	public PertCPM() {
		this(29);
	}

	
	public PertCPM(int horizon) {
		super();
		this.horizon = horizon;
	}


	@Override
	public void buildModel() {
		model = new CPModel();
		masonry=makeTaskVar("masonry",horizon, 7,Options.V_BOUND);
		carpentry=makeTaskVar("carpentry",horizon, 3);
		plumbing=makeTaskVar("plumbing",horizon, 8);
		ceiling=makeTaskVar("ceiling",horizon, 3);
		roofing=makeTaskVar("roofing",horizon, 1);
		painting=makeTaskVar("painting",horizon, 2);
		windows=makeTaskVar("windows",horizon, 1);
		facade=makeTaskVar("facade",horizon, 2);
		garden=makeTaskVar("garden",horizon, 1);
		moving=makeTaskVar("moving",horizon, 1);
		
		//add temporal constraints
		model.addConstraints(
				startsAfterEnd(carpentry,masonry),
				startsAfterEnd(plumbing,masonry),
				startsAfterEnd(ceiling,masonry),
				startsAfterEnd(roofing,carpentry),
				startsAfterEnd(roofing,ceiling),
				startsAfterEnd(windows,roofing),
				startsAfterEnd(painting,windows),
				startsAfterEnd(facade,roofing),
				startsAfterEnd(facade,plumbing),
				startsAfterEnd(garden,roofing),
				startsAfterEnd(garden,plumbing),
				startsAfterEnd(moving,facade),
				startsAfterEnd(moving,garden),
				startsAfterEnd(moving,painting)
		);
	}


	@Override
	public void buildSolver() {
		solver = new PreProcessCPSolver();
		PreProcessConfiguration.keepSchedulingPreProcess(solver);
		((CPSolver) solver).createMakespan();
		solver.read(model);
		VisuFactory.createAndShowGUI(((PreProcessCPSolver) solver).getDisjMod());
	}

	@Override
	public void solve() {
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			LOGGER.log(Level.SEVERE, "Infeasible Pert Problem", e);
		}
		try {
			//then we instantiate the makespan variable and compute slack times
			final IntDomainVar makespan = solver.getMakespan();
			makespan.instantiate(makespan.getInf(), null, true);
			solver.propagate();
		} catch (ContradictionException e) {
			LOGGER.log(Level.SEVERE, "CPM should not lead to a contradiction", e);
		}
		//VizFactory.toDotty( new DisjunctiveSModel((PreProcessCPSolver) solver));
	}



	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) LOGGER.info( StringUtils.prettyOnePerLine(solver.getTaskVarIterator()));
	}

	public static void main(String[] args) {
		(new PertCPM()).execute();
	}
}
