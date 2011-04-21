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

import choco.Choco;
import choco.Options;
import choco.Reformulation;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import gnu.trove.TIntArrayList;
import samples.tutorials.PatternExample;


// TODO : a little bit long...
public class RehearsalProblem extends PatternExample {

	public final static int[] CSPLIB_DURATIONS = {2, 4, 1, 3, 3, 2, 5, 7, 6};

	public final static int[][] CSPLIB_REQUIREMENTS = {
		{1, 1, 0, 1, 0, 1, 1, 0, 1},
		{1, 1, 0, 1, 1, 1, 0, 1, 0},
		{1, 1, 0, 0, 0, 0, 1, 1, 0},
		{1, 0, 0, 0, 1, 1, 0, 0, 1},
		{0, 0, 1, 0, 1, 1, 1, 1, 0}
	};

	public final static int CSPLIB_OBJECTIVE = 17;

	private int nbPieces;

	private int nbPlayers;

	private TIntArrayList[] requirements;

	private int[] durations;

	private int totalDuration;

	private int cumulatedDuration;

	private IntegerVariable totalWaitingTime;

	private TaskVariable[] musicPieces;

	private IntegerVariable[] arrivals;

	private IntegerVariable[] departures;

	public boolean isDisjunctiveModel = true;

	public boolean isPrecOnlyDecision = false;

	public RehearsalProblem() {
		super();
	}


	@Override
	public void setUp(Object paramaters) {
		//read duration
		durations = (int[]) ((Object[]) paramaters)[0];
		nbPieces = durations.length;
		totalDuration = 0;
		for (int i = 0; i < nbPieces; i++) {
			totalDuration += durations[i];
		}

		//read assignment matrix
		int[][] matrix = (int[][]) ((Object[]) paramaters)[1];
		nbPlayers = matrix.length;
		requirements = new TIntArrayList[nbPlayers];
		cumulatedDuration = 0;
		for (int i = 0; i < matrix.length; i++) {
			requirements[i] = new TIntArrayList();
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] == 1) {
					cumulatedDuration += durations[j];
					requirements[i].add(j);
				}
			}
		}
	}



	@Override
	public void buildModel() {
		model = new CPModel();
		totalWaitingTime = Choco.makeIntVar("totalWaitingTime", 0, totalDuration * nbPlayers -cumulatedDuration, Options.V_BOUND, Options.V_OBJECTIVE);
		musicPieces = Choco.makeTaskVarArray("piece", 0, totalDuration, durations, Options.V_BOUND);
		arrivals = Choco.makeIntVarArray("arrival", nbPlayers, 0, totalDuration);
		departures = Choco.makeIntVarArray("departure", nbPlayers, 0, totalDuration);
		IntegerExpressionVariable expr = Choco.constant(-cumulatedDuration);

		model.addVariables(musicPieces);
		//define arrival time, departure time and staying time of each player
		for (int i = 0; i < nbPlayers; i++) {
			int n = requirements[i].size();
			IntegerVariable[] atmp = new IntegerVariable[n];
			IntegerVariable[] dtmp = new IntegerVariable[n];
			for (int j = 0; j < n; j++) {
				TaskVariable t= musicPieces[requirements[i].get(j)];
				atmp[j] = t.start();
				dtmp[j] = t.end();
			}
			model.addConstraints(
					Choco.min(atmp, arrivals[i]),
					Choco.max(dtmp, departures[i])
			);
			expr = Choco.plus(expr, Choco.minus(departures[i], arrivals[i]));
		}
		//obj. constraint
		model.addConstraint(Choco.eq(totalWaitingTime, expr));

		if(isDisjunctiveModel) {
			//add an unary resource
			model.addConstraint(Choco.disjunctive(musicPieces));
		}else {
			//define all possible precedence between tasks
			model.addConstraints( Reformulation.disjunctive(musicPieces, isPrecOnlyDecision ? Options.NO_OPTION : Options.V_NO_DECISION));
		}
	}

	@Override
	public void buildSolver() {
		CPSolver s = new CPSolver();
		s.read(model);
		solver = s;
	}

	@Override
	public void prettyOut() {
		LOGGER.info(""+ solver.getVar(totalWaitingTime));
		LOGGER.info(StringUtils.pretty(solver.getVar(musicPieces)));

	}

	@Override
	public void solve() {
		solver.minimize(false);
	}
	
	@Override
	public void execute() {
		execute(new Object[]{CSPLIB_DURATIONS, CSPLIB_REQUIREMENTS});
	}


	public static void main(String[] args) {
		RehearsalProblem pb = new RehearsalProblem();
		pb.isDisjunctiveModel = true;
		pb.isPrecOnlyDecision = false;
		pb.execute(new Object[]{CSPLIB_DURATIONS, CSPLIB_REQUIREMENTS});
		//pb.execute(new Object[]{ dur, req});
	}


}
