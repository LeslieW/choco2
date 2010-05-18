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


public class DisjunctiveWebEx extends PatternExample {

	           
	protected final static int[] DURATIONS_DATA =  new int[]{1, 2, 3, 2, 5, 3, 7, 9, 3, 4, 2, 3, 5, 7};
	
	protected final static int[] RELEASE_DATES_DATA=  new int[]{0, 6, 2, 9, 5, 0, 1, 3, 0, 8, 10, 11, 2, 4};
	
	protected final static int N = DURATIONS_DATA.length;
	                        	
	protected final static IntegerVariable[] USAGES = makeBooleanVarArray("U", N);
	
	protected IntegerVariable[] durations;
	
	protected TaskVariable[] tasks;
	
	protected final static IntegerVariable OBJ = makeIntVar("obj", 0, N, Options.V_BOUND, Options.V_OBJECTIVE);

	protected boolean useAlternativeResource;
	
	
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
		tasks =new TaskVariable[N];
		if(useAlternativeResource) {
			durations = constantArray(DURATIONS_DATA);
			for (int i = 0; i < N; i++) {
				tasks[i] = makeTaskVar("t"+i, RELEASE_DATES_DATA[i], 20, durations[i]);
			}
			//post the disjunctive
			model.addConstraint(disjunctive("unique unary resource", tasks, USAGES));
			//
		}else {
			durations = new IntegerVariable[N];
			for (int i = 0; i < N; i++) {
				durations[i] = makeIntVar("H_" + i, new int[]{0, DURATIONS_DATA[i]});
				tasks[i] = makeTaskVar("t"+i, RELEASE_DATES_DATA[i], 20, durations[i]);
			}	
			//post the channeling to know if the task is scheduled or not
			for (int i = 0; i < N; i++) {
				model.addConstraint(boolChanneling(USAGES[i], durations[i], DURATIONS_DATA[i]));
			}
			//post the disjunctive
			model.addConstraint(disjunctive("unique unary resource", tasks));
			
		}
		//state the objective function
		model.addConstraint(eq((sum(USAGES)), OBJ));

	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
		//System.out.println(solver.pretty());
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) {
			final String str = ( 
					"model with "+ (useAlternativeResource ? "alternative resource" : "channeling constraints")+
					"\nobjective: "+ solver.getVar(OBJ)+"\n"+ Arrays.toString(solver.getVar(USAGES))
					+"\n"+ StringUtils.pretty(solver.getVar(tasks))
			);
			LOGGER.info(str);			
		}
	}

	@Override
	public void solve() {
		solver.maximize(false);
	}
	
	public static void main(String[] args) {
		new DisjunctiveWebEx().execute(Boolean.FALSE);
		new DisjunctiveWebEx().execute(Boolean.TRUE);
	}

}
