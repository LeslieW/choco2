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
package choco.model.constraints.global;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.pack.PackModeler;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal","PMD.MethodArgumentCouldBeFinal"})
public class PackTest {

	public final static Logger LOGGER = ChocoLogging.getTestLogger();

	public final static int MIN_ITEMS=4;

	public final static int MAX_ITEMS=13;

	public final static int NB_TESTS=5;

	private final static Random RND=new Random();

	public final static int NB_RND_TESTS=1;

	private final static int RND_PB_CAPA=25;

	private final static int RND_NB_ITEMS=7;

	protected CPModel model;

	protected PackModeler modeler;

	protected List<CPModel> models = new ArrayList<CPModel>();

	protected List<CPSolver> solvers = new ArrayList<CPSolver>();


	private CPModel initializeModel(Constraint[] cstr,SettingType... options) {
		CPModel m =new CPModel();
		Constraint pack = pack(modeler);
		for (SettingType o : options) {
			pack.addOption(o.getOptionName());
		}
		m.addConstraint(pack);
		if(cstr!=null) {m.addConstraints(cstr);}
		return m;
	}

	private void initializeModels(Constraint[] cstr) {
		models.add(initializeModel(cstr));
		models.add(initializeModel(cstr,SettingType.ADDITIONAL_RULES));
		models.add(initializeModel(cstr,SettingType.DYNAMIC_LB));
		models.add(initializeModel(cstr,SettingType.DYNAMIC_LB,SettingType.ADDITIONAL_RULES));
	}

	private void initializeModels(int[] sizes,int nbBins,int capacity) {
		modeler = new PackModeler(sizes, nbBins, capacity);
		modeler.setDefaultDecisionVariable();
		models.clear();
		initializeModels(null);
	}


	protected void initializeSolvers() {
		solvers.clear();
		int seed = RND.nextInt(100000);
		initializeSolvers(false, seed);
		initializeSolvers(true, seed);
	}

	protected void initializeSolvers(boolean set,int seed) {
		for (CPModel m : models) {
			solvers.add(createSolver(m, set, seed));
		}
	}
	private CPSolver createSolver(CPModel model, boolean set,int seed) {
		CPSolver solver = new CPSolver();
		solver.read(model);
		if(seed==-1) {
			solver.setVarIntSelector(new MinDomain(solver));
		}else {
			solver.setRandomSelectors(seed);
		}
		if(!set) {
			solver.attachGoal(solver.generateIntGoal());
			solver.addGoal(solver.generateSetGoal());
		}
		solver.setFirstSolution(false);
		solver.generateSearchStrategy();
		return solver;
	}


	protected void testAll(int nbSol) {
		LOGGER.info("%%%%%%% PACK TEST ALL%%%%%%%%%%%");
		CPSolver last=null;
		for (int i = 0; i < NB_TESTS; i++) {
			initializeSolvers();
			for (CPSolver s : this.solvers) {
				s.launch();
				assertEquals("sat ", nbSol != 0 , s.isFeasible());
				if(nbSol>0){
					assertEquals("nb Sol.",nbSol,s.getNbSolutions());
				}
				if(last!=null) {
					assertEquals("sat ", last.getNbSolutions() , s.getNbSolutions());
				}
				last=s;
			}
		}
	}



	@Test(expected=ModelException.class)
	public void notSorted() {
		model = new CPModel();
		IntegerConstantVariable[] s= constantArray(new int[]{3,3,3,4,3});
		Constraint pack = pack(Choco.makeSetVarArray("set",s.length, 0, 1),s,s,s);
		model.addConstraint(pack);

	}

	@Test(expected=ModelException.class)
	public void increasing() {
		model = new CPModel();
		IntegerConstantVariable[] s= constantArray(new int[]{2,3,3,4,6});
		Constraint pack = pack(Choco.makeSetVarArray("set",s.length, 0, 1),s,s,s);
		model.addConstraint(pack);
	}

	@Test
	public void binForTwo() {
		for (int i = MIN_ITEMS; i < MAX_ITEMS; i=i+2) {
			int nbBins=(i+1)/2;
			initializeModels(ArrayUtils.oneToN(i), nbBins, i+1);
			testAll( (int) MathUtils.factoriel(nbBins));
		}
	}

	@Test
	public void sat() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		int[] s1={2,3,6,4,3};
		initializeModels(s1, 3, 10);
		testAll(138);
	}

	@Test
	public void unsat() {
		int[] s1={8,5,4,4,4,3};
		int[] s2={8,6,6,5,4,4,4,4,4,3};
		initializeModels(s1, 3, 10);
		testAll(0);
		initializeModels(s2, 5, 10);
		testAll(0);
	}


	@Test
	public void notIdenticalBins1() {
		int[] sizes={7,4,4,3,3,2};
		initializeModels(sizes, 3, 10);
		for (CPModel model : models) {
			model.addConstraints( eq(modeler.loads[0], 8),
					eq(modeler.loads[1], 6),
					geq(modeler.loads[2], 9));
		}
		testAll(1);
	}

	@Test
	public void notIdenticalBins2() {
		int[] sizes={8,7,4,3,3};
		model = new CPModel();
		initializeModels(sizes,3,12);
		for (CPModel model : models) {
			model.addConstraints(
					geq(modeler.loads[0], 11),
					leq(modeler.loads[1], 3),
					geq(modeler.loads[2], 9),
					leq(modeler.loads[2], 11));
		}
		testAll(6);
	}

	private void initializeRandom(int nbItems,int capacity) {
		int[] sizes=new int[nbItems];
		int nbBins=1;
		int lbin=0;
		for (int i = 0; i < sizes.length; i++) {
			sizes[i]=RND.nextInt(capacity)+1;
			if(lbin+sizes[i]>capacity) {
				lbin=0;
				nbBins++;
			}
			lbin+=sizes[i];
		}
		initializeRandomModels(sizes, nbBins, capacity);
	}

	private void initializeRandomModels(int[] sizes,int nbBins,int capacity) {
		modeler = new PackModeler(sizes, nbBins, capacity);
		modeler.setDefaultDecisionVariable();
		models.clear();
		initializeModels(null);
		initializeModels(modeler.redundantCstrNbNonEmptyBins());
		initializeModels(modeler.symBreakEndsWithEmptyBins(0, nbBins));
		initializeModels(modeler.symBreakLoadOrdering(true));
		initializeModels(modeler.symBreakPackLargeItems(true));
		initializeModels(modeler.symBreakEqualSizedItems());
		initializeModels(new Constraint[]{modeler.redundantCstrAllDiffLargeItems()});
	}
	
	

	protected void testRandom() {
		LOGGER.info("%%%%%%%% TEST RANDOM %%%%%%%%");
		int ub = modeler.nbBins;
		CPSolver last;
		do {
			LOGGER.info("\n%%%%%% ub = "+ub+"\n");
			last=null;
			initializeSolvers();
			for (CPSolver s : this.solvers) {
				s.post(s.leq(s.getVar(modeler.nbNonEmpty), ub));
				//CPSolver.setVerbosity(CPSolver.SEARCH);
				s.launch();
				LOGGER.log(Level.INFO, "Nb solutions: {0}\n{1}", new Object[]{s.getNbSolutions(), s.runtimeStatistics()});
				if(last!=null) {
					assertEquals("sat ", last.isFeasible(), s.isFeasible());
				}
				last=s;
			}
			ub--;
		}while(last.isFeasible() && ub>0);
	}

	@Ignore
	public void testRandomProblems() {
		for (int i = 0; i < NB_RND_TESTS; i++) {
			initializeRandom(RND_NB_ITEMS,RND_PB_CAPA);
			testRandom();
		}
	}

}