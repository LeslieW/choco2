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
import choco.Options;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashMap;
import java.util.Map;

import static choco.Choco.*;

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

	protected int[][] durations;

	protected int[][] costs;

	protected int nbAlternatives;

	protected IntegerVariable[] assignments;

	protected IntegerVariable[] vcosts;

	protected IntegerVariable objective;

	public OptimizeAssignment(int horizon, int[][] durations,int[][] costs) {

	}

    @Override
    public void setUp(Object parameters) {
        Object[] params = (Object[]) parameters;
        this.horizon = (Integer) params[0];
        this.nb_tasks = (Integer) params[1];
        this.activities = (String[]) params[2];
        this.durations = (int[][]) params[3];
        this.temporalconstraints = (int[][]) params[4];

        super.durations = createDurationVariables(this.nb_tasks, this.durations);

		this.nbAlternatives=durations[0].length;
		this.durations=durations;
		this.costs=costs;
		this.assignments = makeIntVarArray("assignment", nb_tasks, 0, nbAlternatives-1, Options.V_NO_DECISION);
		this.vcosts=new IntegerVariable[nb_tasks];
		for (int i = 0; i < nb_tasks; i++) {
			//costs are the only decision variable
			tasks[i].start().addOption(Options.V_NO_DECISION);
			tasks[i].end().addOption(Options.V_NO_DECISION);
			tasks[i].duration().addOption(Options.V_NO_DECISION);
			this.vcosts[i]= makeIntVar("cost-"+tasks[i].getName(), costs[i]);
			model.addConstraints(
					nth(assignments[i], durations[i], tasks[i].duration()),
					nth(assignments[i], costs[i],vcosts[i])
			);
		}
		//objective
		objective=makeIntVar("objective", 0, nb_tasks*1000, Options.V_BOUND, Options.V_OBJECTIVE); //max cost should be lower than 1000
		model.addConstraint(eq(objective,plus(sum(vcosts),mult(costPerDay,Choco.makeIntVar("makespan", 0, Integer.MAX_VALUE, Options.V_BOUND, Options.V_MAKESPAN)))));
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
			Integer id = solver.getVar(tasks[i]).getID();
			labels.put(id, new String(b));
		}
		//FIXME VizFactory.toDotty(solver.toDotty(solver.getNoStartEndSubgraph(),labels,Collections.EMPTY_MAP,true));
	}

	public void computeAll() {
		LOGGER.info("\n%%%%%%%%%%%%%%% OPTIMIZATION PROBLEM %%%%%%%%%%%%%%%%%%%");
		LOGGER.info("MIN Cmax SOLUTION");
//		model.remove(c);
        c = eq(costPerDay, 0);
        model.addConstraint(c);
        this.costPerDay = constant(0);
		this.criticalPathMethod();
		int min = solver.getMakespan().getInf();
		this.solver.post( solver.eq(solver.getMakespan(), min));
		this.solver.minimize(false);
		LOGGER.info(""+this);
		LOGGER.info("MIN COST SOLUTION");
		this.minimize();
		int max=solver.getMakespanValue();
		LOGGER.info(""+this);
		this.getCmaxFunction();
		this.getParetoFront(min, max+1);
	}


	public void minimize() {
		solver =new CPSolver();
		solver.read(model);
		//CPSolver.setVerbosity(CPSolver.SOLUTION);
		//solver.setTimeLimit(1*1000);
		solver.minimize(false);
		//LOGGER.info(this);
		//solver.printRuntimeSatistics();
	}

	public void getCmaxFunction() {
		LOGGER.info("Cmax = F(cost)");
		LOGGER.info("#cost/day Cmax");
		for (int i = 0; i < 21; i++) {
//            model.remove(c);
            c = eq(costPerDay, i);
            model.addConstraint(c);
			this.minimize();
			LOGGER.info(solver.getVar(costPerDay).getVal()+" "+solver.getMakespanValue());
		}

	}

	public void getParetoFront(int min,int max) {
		LOGGER.info("\nPARETO FRONT");
		LOGGER.info("Cmax objective");
		this.costPerDay = constant(0);
		for (int h = min; h < max; h++) {
			solver =new CPSolver();
			solver.read(model);
			this.solver.post( solver.eq(solver.getMakespan(), h));
			solver.minimize(false);
			LOGGER.info(solver.getMakespanValue()+" "+solver.getObjectiveValue());
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


    protected static IntegerVariable[] createDurationVariables(int nb_tasks, int[][] durations) {
        IntegerVariable[] vars = new IntegerVariable[nb_tasks];
        for (int i = 0; i < vars.length; i++) {
            vars[i] = makeIntVar("p-" + i, durations[i]);
        }
        return vars;
    }


}

