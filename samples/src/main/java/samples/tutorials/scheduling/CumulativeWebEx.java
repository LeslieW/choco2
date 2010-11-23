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
package samples.tutorials.scheduling;


import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import samples.tutorials.PatternExample;

import java.util.Arrays;
import java.util.logging.Level;

import static choco.Choco.*;


public class CumulativeWebEx extends PatternExample {

	protected final static int NT = 11, NF = 3, N =  NT + NF;

	private final static int[] HEIGHTS_DATA =  new int[]{2, 1, 4, 2, 3, 1, 5, 6, 2, 1, 3, 1, 1, 2};
	                        
	private final static int[] DURATIONS_DATA =  new int[]{1, 1, 1, 2, 1, 3, 1, 1, 3, 4, 2, 3, 1, 1};
	                        
	private final static int HORIZON = 6;
	private final static IntegerVariable CAPACITY = constant(7);
	
	//the fake tasks to establish the profile capacity of the ressource are the NF firsts.
	private final static TaskVariable[] TASKS = makeTaskVarArray("t", 0, 6, DURATIONS_DATA, Options.V_BOUND);

	private final static IntegerVariable OBJ = makeIntVar("obj", 0, NT, Options.V_BOUND, Options.V_OBJECTIVE);
	
	private IntegerVariable[] usages, heights;
		
	private boolean useAlternativeResource;
		
	@Override
	public void setUp(Object parameters) {
		if (parameters instanceof Boolean) {
			useAlternativeResource = ((Boolean) parameters).booleanValue();
		}else {
			useAlternativeResource = true;
		}
		super.setUp(parameters);
	}

	@Override
	public void buildModel() {
		model = new CPModel();
		//set fake tasks to establish the profile capacity
		model.addConstraints(
				startsAt(TASKS[0], 1),
				startsAt(TASKS[1], 2),
				startsAt(TASKS[2], 3)
		);
		usages = makeBooleanVarArray("U", NT);
		//state the objective function
		model.addConstraint(eq( sum(usages), OBJ));
		
		if(useAlternativeResource) {
			heights = constantArray(HEIGHTS_DATA);
			model.addConstraint(cumulativeMax("alt-cumulative", TASKS, heights, usages, CAPACITY, ""));
			
		}else {
			heights = new IntegerVariable[N];
			//post the channeling to know if the task uses the resource or not.
			for (int i = 0; i < NF; i++) {
				heights[i] = constant(HEIGHTS_DATA[i]);
			}
			for (int i = NF; i < N; i++) {
				heights[i] =  makeIntVar("H_" + i, new int[]{0, HEIGHTS_DATA[i]});
				model.addConstraint(boolChanneling(usages[i- NF], heights[i], HEIGHTS_DATA[i]));
			}
			model.addConstraint(cumulativeMax("cumulative", TASKS, heights, CAPACITY, ""));
		}

	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) {
			final String str = ( 
					"model with "+ (useAlternativeResource ? "alternative resource" : "channeling constraints")+
					"\nobjective: "+ solver.getVar(OBJ)+"\n"+ Arrays.toString(solver.getVar(usages))
					+"\n"+ Arrays.toString(solver.getVar(heights))+"\n"+ StringUtils.pretty(solver.getVar(TASKS))
			);
			LOGGER.info(str);			
		}
	}

	@Override
	public void solve() {
		solver.maximize(false);
	}
	
	public static void main(String[] args) {
		new CumulativeWebEx().execute(Boolean.FALSE);
		new CumulativeWebEx().execute(Boolean.TRUE);
	}

}
