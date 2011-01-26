package samples.tutorials.lns.rcpsp;/*
 * Created by IntelliJ IDEA.
 * User: sofdem - sophie.demassey{at}mines-nantes.fr
 * Date: 11/01/11 - 14:49
 */

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.BranchingFactory;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import parser.instances.AbstractInstanceModel;
import samples.tutorials.lns.lns.LNSCPSolver;
import samples.tutorials.lns.lns.Neighborhood;
import samples.tutorials.lns.lns.RandomNeighborhoodOperator;
import samples.tutorials.lns.lns.RandomTaskNeighborhoodOperator;

import java.util.logging.Level;

/** @author Sophie Demassey */
public class RCPSPModeler extends AbstractInstanceModel {

RCPSPData data;
TaskVariable[] tasks;
Constraint[] cumulatives;
Solution lastSolution;
int incumbent;
boolean lns = true;


public RCPSPModeler(Configuration defaultConfiguration)
{
	super(new RCPSPFileParser(), defaultConfiguration);
}

/**
 * Executes preprocessing ( bounding, heuristics ...)
 * default implementation: do nothing.
 * @return <code>true</code> if a solution has been found, <code>false</code> if the infeasibility has been proven and <code>null</code> otherwise.
 */
@Override
public Boolean preprocess()
{
	return null;
}

/** create the choco model after the preprocessing phase. */
@Override
public Model buildModel()
{
	data = ((RCPSPFileParser) parser).getData();
	int nAct = data.nAct();
	int nRes = data.nRes();
	int horizon = data.getDurationSum();

	Model m = new CPModel();

	tasks = Choco.makeTaskVarArray("A", 0, horizon, data.getDurations());

	for (int a1 = 0; a1 < nAct; a1++) {
		for (int a2 = 0; a2 < nAct; a2++) {
			if (data.isPrecedence(a1, a2)) m.addConstraint(Choco.endsAfterBegin(tasks[a1], tasks[a2]));
		}
	}


	cumulatives = new Constraint[data.nRes()];

	for (int k = 0; k < nRes; k++) {
		int nTasks = 0;
		for (int a = 0; a < nAct; a++) {
			if (data.getRequest(a, k) != 0) {
				nTasks++;
			}
		}
		TaskVariable[] subTasks = new TaskVariable[nTasks];
		IntegerVariable[] subRequests = new IntegerVariable[nTasks];
		for (int a = 0, i = 0; i < nTasks; a++) {
			if (data.getRequest(a, k) != 0) {
				subTasks[i] = tasks[a];
				subRequests[i] = Choco.constant(data.getRequest(a, k));
				i++;
			}
		}
		cumulatives[k] = Choco.cumulativeMax("R" + k, subTasks, subRequests, Choco.constant(data.getCapacity(k)), "");
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "resource " + k + ": cumulative / " + nTasks + " variables");
		}
		m.addConstraint(cumulatives[k]);
		m.addVariable(Options.V_OBJECTIVE, tasks[nAct - 1].end());
	}
	return m;
}

/** create a solver from the current model */
@Override
public Solver buildSolver()
{
	return (lns) ? buildLNSCPSolver() : buildCPSolver();
}

/** create a solver from the current model */
public Solver buildLNSCPSolver()
{
	LNSCPSolver s = new LNSCPSolver(defaultConf);
	s.read(model);
	s.addGoal(BranchingFactory.setTimes(s));
	s.addNeighborhood(new Neighborhood(new RandomNeighborhoodOperator(38), BranchingFactory.minDomMinVal(s), 2));
	s.addNeighborhood(new Neighborhood(new RandomTaskNeighborhoodOperator(10, 18, 0), BranchingFactory.minDomMinVal(s), 5));
	return s;
}

/** create a solver from the current model */
public Solver buildCPSolver()
{
	Solver s = new CPSolver(defaultConf);
	s.read(model);
	s.clearGoals();
	s.addGoal(BranchingFactory.setTimes(s));
	return s;
}


/** configure and launch the resolution. */
@Override
public Boolean solve()
{
	return solver.minimize(solver.getMakespan(), true);
}

}
