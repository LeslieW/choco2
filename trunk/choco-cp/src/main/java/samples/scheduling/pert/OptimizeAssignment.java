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
package samples.scheduling.pert;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashMap;
import java.util.Map;

public class OptimizeAssignment extends DeterministicPert {


	public static final int[][] EXAMPLE_DURATIONS={
		{5,7,8,10},
		{2,3,5,7},
		{6,8,10,12},
		{2,3,5,7},
		{2,3,8,10},
		{5,7,9,13},
		{1,2,3,7},
		{2,3,4,8},
		{1,3,5,7},
		{1,2,3,4}
	};

	public static final int[][] EXAMPLE_COSTS={
		{12,10,9,8},
		{15,10,7,5},
		{15,10,9,8},
		{10,5,4,3},
		{13,12,5,4},
		{14,12,9,5},
		{15,10,7,3},
		{14,12,10,5},
		{15,11,8,5},
		{6,4,2,1}
	};

	public IntegerVariable costPerDay=makeIntVar("costPerDay",0, 21);

    private Constraint c = eq(costPerDay, 0);

	protected final int[][] durations;

	protected final int[][] costs;

	protected final int nbAlternatives;

	protected final IntegerVariable[] assignments;

	protected final IntegerVariable[] vcosts;

	protected final IntegerVariable objective;

	public OptimizeAssignment(int horizon, int[][] durations,int[][] costs) {
		super(horizon,createDurationVariables(durations));
		this.nbAlternatives=durations[0].length;
		this.durations=durations;
		this.costs=costs;
		this.assignments = makeIntVarArray("assignment", NB_TASKS, 0, nbAlternatives-1,"cp:no_decision");
		this.vcosts=new IntegerVariable[NB_TASKS];
		for (int i = 0; i < NB_TASKS; i++) {
			//costs are the only decision variable
			tasks[i].start().addOption("cp:no_decision");
			tasks[i].end().addOption("cp:no_decision");
			tasks[i].duration().addOption("cp:no_decision");
			this.vcosts[i]= makeIntVar("cost-"+tasks[i].getName(), costs[i]);
			model.addConstraints(
					nth(assignments[i], durations[i], tasks[i].duration()),
					nth(assignments[i], costs[i],vcosts[i])
			);
		}
		//objective
		objective=makeIntVar("objective", 0, NB_TASKS*1000, "cp:bound","cp:objective"); //max cost should be lower than 1000
		model.addConstraint(eq(objective,plus(sum(vcosts),mult(costPerDay,Choco.makeIntVar("makespan", 0, Integer.MAX_VALUE, "cp:bound","cp:makespan")))));
        model.addConstraint(c);
	}


	protected String printVariable(IntDomainVar v) {
		return v.isInstantiated() ? Integer.toString(v.getVal()) : v.getDomain().toString();
	}

	@Override
	public void generateDottyFile() {
		Map<Integer,String> labels = new HashMap<Integer, String>();
		for (int i = 0; i < tasks.length; i++) {
			StringBuilder b=new StringBuilder();
			b.append("|{");
			b.append(printVariable(solver.getVar(assignments[i])));
			b.append('|');
			b.append(printVariable(solver.getVar(vcosts[i])));
			b.append("}");
			Integer id = Integer.valueOf(solver.getVar(tasks[i]).getID());
			labels.put(id, new String(b));
		}
		//FIXME VizFactory.toDotty(solver.toDotty(solver.getNoStartEndSubgraph(),labels,Collections.EMPTY_MAP,true));
	}

	public void computeAll() {
		System.out.println("\n%%%%%%%%%%%%%%% OPTIMIZATION PROBLEM %%%%%%%%%%%%%%%%%%%");
		System.out.println("MIN Cmax SOLUTION");
		model.remove(c);
        c = eq(costPerDay, 0);
        model.addConstraint(c);
        this.costPerDay = constant(0);
		this.criticalPathMethod();
		int min = solver.getMakespan().getInf();
		this.solver.post( solver.eq(solver.getMakespan(), min));
		this.solver.minimize(false);
		System.out.println(this);
		System.out.println("MIN COST SOLUTION");
		this.minimize();
		int max=solver.getMakespanValue();
		System.out.println(this);
		this.getCmaxFunction();
		this.getParetoFront(min, max+1);
	}


	public void minimize() {
		solver =new CPSolver();
		solver.read(model);
		//CPSolver.setVerbosity(CPSolver.SOLUTION);
		//solver.setTimeLimit(1*1000);
		solver.minimize(false);
		//System.out.println(this);
		//solver.printRuntimeSatistics();
	}

	public void getCmaxFunction() {
		System.out.println("Cmax = F(cost)");
		System.out.println("#cost/day Cmax");
		for (int i = 0; i < 21; i++) {
            model.remove(c);
            c = eq(costPerDay, i);
            model.addConstraint(c);
			this.minimize();
			System.out.println(solver.getVar(costPerDay).getVal()+" "+solver.getMakespanValue());
		}

	}

	public void getParetoFront(int min,int max) {
		System.out.println("\nPARETO FRONT");
		System.out.println("Cmax objective");
		this.costPerDay = constant(0);
		for (int h = min; h < max; h++) {
			solver =new CPSolver();
			solver.read(model);
			this.solver.post( solver.eq(solver.getMakespan(), h));
			solver.minimize(false);
			System.out.println(solver.getMakespanValue()+" "+solver.getOptimumValue());
		}
	}

	@Override
	public String toString() {
		StringBuffer b=new StringBuffer();
		b.append("objective=").append(solver.getVar(objective).pretty());
		b.append('\n');
		b.append(super.toString());
		return new String(b);
	}



}

