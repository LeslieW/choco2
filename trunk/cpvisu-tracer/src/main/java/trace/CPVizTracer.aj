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
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.AbstractSearchStrategy;
import choco.kernel.solver.ContradictionException;

import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractSearchLoop;
import choco.kernel.solver.branch.IntBranching;
import choco.kernel.solver.search.IntBranchingDecision;
import trace.Visualization;
import trace.VisuWrapper;

public aspect CPVizTracer{


    Visualization visu;

    /*************************************************/
	/** 											**/
	/** 				POINTCUTS 					**/
	/** 											**/
	/*************************************************/

    // creation of the visualization
    pointcut visualization(Visualization visu) : execution(Visualization.new(..)) && target(visu);

	// start of the Tree search
	pointcut incrementalRun() : execution(void AbstractGlobalSearchStrategy.incrementalRun());

    // initial propagation
	pointcut newTreeSearch() : execution(void AbstractGlobalSearchStrategy.newTreeSearch());

    // decision creation
    pointcut storeCurrentDecision(IntBranchingDecision decision) :
	    execution (void IntBranching+.goDownBranch(IntBranchingDecision)) && args(decision);

    // initial propagation
	pointcut intialPropagation() : execution(void Solver+.propagate()) && cflow(execution(void AbstractGlobalSearchStrategy.initialPropagation()));

    // decision application
	pointcut applyDecision() : execution(void Solver+.propagate()) && cflow(execution(void AbstractSearchLoop.downBranch()));

    // reconsider decision
	pointcut worldPop() : execution(void AbstractSearchLoop+.worldPop()) && cflow(execution(void AbstractSearchLoop.upBranch()));

    // reconsider decision
	pointcut reconsiderDecision() : execution(void Solver+.propagate()) && cflow(execution(void AbstractSearchLoop.upBranch()));

    // Solution recording
    pointcut recordSolution() : call(void AbstractSearchStrategy.recordSolution());

    /*************************************************/
	/** 											**/
	/** 				ADVICES 					**/
	/** 											**/
	/*************************************************/

    after(Visualization visu): visualization(visu){
        this.visu = visu;
    }

    // start of the tree search
	before(): incrementalRun() {
        VisuWrapper.init(visu);
	}

    before(IntBranchingDecision decision): storeCurrentDecision(decision){
        VisuWrapper.setBranchingDecision(visu, decision);
    }

    // start of the tree search
    before(): intialPropagation() {
        VisuWrapper.beforeInitialPropagation(visu);
    }


    // Try node
	after() returning: intialPropagation() {
		VisuWrapper.afterInitialPropagation(visu);
	}

	// Try node
	after() returning: applyDecision() {
		VisuWrapper.tryNode(visu);
	}

    // Fail node
	after() throwing(ContradictionException c): applyDecision() {
		VisuWrapper.hasFailed(visu);
	}

	after(): worldPop(){
	    VisuWrapper.failNode(visu);
	}

	// Reconsider a decision
	after() throwing(ContradictionException c): reconsiderDecision() {
		VisuWrapper.hasFailed(visu);
	}

    // Record a solution
	after(): recordSolution() {
		VisuWrapper.succNode(visu);
	}
}