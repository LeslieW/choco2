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
package samples.tutorials.scheduling.pert;

import choco.Choco;
import static choco.Choco.eq;
import static choco.Choco.nth;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;


public class ProbabilisticPert extends DeterministicPert {

	protected static final int[][] EXAMPLE_DURATIONS={
		{5,7,8},
		{2,4,9},
		{6,8,9},
		{1,3,6},
		{2,7,8},
		{2,4,5},
		{1,1,3},
		{2,2,4},
		{1,3,4},
		{1,2,3}
	};

	protected final int[][] durations;

	public final static int OPTIMISTIC=0;
	public final static int LIKELY=1;
	public final static int PESSIMISTIC=2;
	public final static int EXPECTED=3;
	public final static int NB_ESTIMATION=4;

	protected final IntegerVariable estimation;

    protected Constraint c;

	protected final CPSolver[] estimated=new CPSolver[NB_ESTIMATION];

	protected double[] expected = new double[NB_TASKS];

	public ProbabilisticPert(int horizon, int[][] durations) {
		super(horizon, createDurationVariables(durations));
		this.estimation= Choco.makeIntVar("estimatio", 0, NB_ESTIMATION);
        c = eq(estimation, LIKELY);
        model.addConstraint(c);
		this.durations=durations;
		for (int i = 0; i < NB_TASKS; i++) {
			model.addConstraint(nth(estimation, durations[i], tasks[i].duration()));
		}
	}

	public final static int[][] addExpectedTime(int[][] durations) {
		if(durations[0].length!=3){
			throw new ArrayIndexOutOfBoundsException("the argument should have three columns");
		}
		int[][] res=new int[durations.length][4];
		for (int i = 0; i < durations.length; i++) {
			for (int j = 0; j < 3; j++) {
				res[i][j]=durations[i][j];
			}
			res[i][3]= 10 * (res[i][0]+ 4*res[i][1] +res[i][2]);
			res[i][3]/=6;
		}
		return res;
	}

	public double getStandardDeviation(int i) {
		final double r= durations[i][PESSIMISTIC] - durations[i][OPTIMISTIC];
		return r/6;
	}

	public void computeAllCPM() {
		//compute CPM for each estimation
		for (int i = 0; i < NB_ESTIMATION; i++) {
			model.remove(c);
            c = eq(estimation, i);
            model.addConstraint(c);
			this.criticalPathMethod();
			this.estimated[i]=this.solver;
		}
	}

	public double computeProbability(int completionTime) {
		double r = ( (IntDomainVar) estimated[EXPECTED].getMakespan()).getInf();
		r = completionTime - r/10;
		double v=0;
		for (int i = 0; i < tasks.length; i++) {
			if(isCritical(i)) {
				final double sd = getStandardDeviation(i);
				v += sd*sd;
			}
		}
		v = Math.sqrt(v);
		r/= v;
		return r;
	}
	@Override
	protected StringBuilder toString(int i) {
		StringBuilder b=super.toString(i);
		b.append(" sd=").append(getStandardDeviation(i));
		return b;
	}



}
