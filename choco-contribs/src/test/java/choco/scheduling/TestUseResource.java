package choco.scheduling;

import static choco.Choco.disjunctive;
import static choco.Choco.makeBooleanVar;
import static choco.Choco.makeTaskVar;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import choco.ChocoContrib;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;


public class TestUseResource {

	private Model model;
	
	private CPSolver solver;

	//**************************
	//Unique solution Res1 {C,A} Res2{B,D}
	private int durA = 6;
	private int durB = durA + 1;
	private int durC = 10;
	private int durD = durC - 1;
	private int horizon = durA + durC;

	private TaskVariable JobA = makeTaskVar("JobA", 0, horizon, durA);
	private TaskVariable JobB = makeTaskVar("JobB", 0, durB, durB);
	private TaskVariable JobC = makeTaskVar("JobC", 0, durC, durC);
	private TaskVariable JobD = makeTaskVar("JobD", horizon, durD);
	private TaskVariable JobE = makeTaskVar("JobE", horizon-durB, horizon, durB);
	private TaskVariable JobF = makeTaskVar("JobF", horizon-durC, horizon, durC);

	//***************************

	private IntegerVariable JobA_Res1 = makeBooleanVar("JobA_Res1");
	private IntegerVariable JobA_Res2 = makeBooleanVar("JobA_Res2");
	private IntegerVariable JobA_Res3 = makeBooleanVar("JobA_Res3");
	private IntegerVariable JobB_Res1 = makeBooleanVar("JobB_Res1");
	private IntegerVariable JobB_Res2 = makeBooleanVar("JobB_Res2");
	private IntegerVariable JobC_Res1 = makeBooleanVar("JobC_Res1");
	private IntegerVariable JobC_Res2 = makeBooleanVar("JobC_Res2");

	private Constraint rsc1;

	private Constraint rsc2;



	@Test
	public void testUseResource() {
		ChocoLogging.setVerbosity(Verbosity.SEARCH);
		model = new CPModel();
		rsc1 = disjunctive( new TaskVariable[]{JobA, JobB, JobC}, 
				new IntegerVariable[] {JobA_Res1, JobB_Res1, JobC_Res1});
		rsc2 = disjunctive( new TaskVariable[]{JobD, JobA, JobB, JobC}, 
				new IntegerVariable[] {JobA_Res2, JobB_Res2, JobC_Res2});
		model.addConstraints(rsc1, rsc2);
		CPSolver solver = new CPSolver();
		solver.setHorizon(horizon);
		solver.read(model);
		final Constraint[] resources= {rsc1, rsc2};
		solver.addConstraint(ChocoContrib.useResource(JobA, resources));
		solver.addConstraint(ChocoContrib.useResource(JobB, resources));
		solver.addConstraint(ChocoContrib.useResource(JobC, resources));
		solver.setRandomSelectors();
		solver.solveAll();
		assertTrue("is Feasible", solver.isFeasible());
		assertEquals("Solution Count", 1, solver.getSolutionCount());
	}

	private void testPropagation(int k, Constraint...resources) throws ContradictionException {
		model = new CPModel();
		model.addConstraints(resources);
		solver = new CPSolver();
		solver.setHorizon(horizon);
		solver.read(model);
		final Constraint useRsc = ChocoContrib.useResources(JobA, k, resources);
		solver.addConstraint(useRsc);
		solver.propagate();
		//FIXME no propagation of task events
		( (AbstractSConstraint) solver.getCstr(useRsc) ).propagate();
	}
	@Test
	public void testUseResourceEST() throws ContradictionException {
		testPropagation(1,
				disjunctive( new TaskVariable[]{JobB, JobA},new IntegerVariable[] {JobA_Res1}),
				disjunctive( new TaskVariable[]{JobC, JobA}, new IntegerVariable[] {JobA_Res2})
		);
		assertEquals("est_A", Math.min(durB, durC), solver.getVar(JobA).getEST());
	}
	
	@Test
	public void testUseResourceLCT() throws ContradictionException {
		ChocoLogging.setVerbosity(Verbosity.SEARCH);
		model = new CPModel();
		testPropagation(1,
				disjunctive( new TaskVariable[]{JobE, JobA},new IntegerVariable[] {JobA_Res1}),
				disjunctive( new TaskVariable[]{JobF, JobA}, new IntegerVariable[] {JobA_Res2})
		);
		assertEquals("lct_A", Math.max(horizon - durB, horizon - durC), solver.getVar(JobA).getLCT());
	}
	
	@Test
	public void testUseResourceKEST() throws ContradictionException {
		testPropagation(2,
				disjunctive( new TaskVariable[]{JobB, JobA},new IntegerVariable[] {JobA_Res1}),
				disjunctive( new TaskVariable[]{JobC, JobA}, new IntegerVariable[] {JobA_Res2}),
				disjunctive( new TaskVariable[]{JobA}, new IntegerVariable[] {JobA_Res3})
		);
		assertEquals("est_A", Math.min(durB, durC), solver.getVar(JobA).getEST());
	}
	
	@Test
	public void testUseResourceKLCT() throws ContradictionException {
		model = new CPModel();
		testPropagation(2,
				disjunctive( new TaskVariable[]{JobE, JobA},new IntegerVariable[] {JobA_Res1}),
				disjunctive( new TaskVariable[]{JobF, JobA}, new IntegerVariable[] {JobA_Res2}),
				disjunctive( new TaskVariable[]{JobA}, new IntegerVariable[] {JobA_Res3})
		);
		assertEquals("lct_A", Math.max(horizon - durB, horizon - durC), solver.getVar(JobA).getLCT());
	}

}

