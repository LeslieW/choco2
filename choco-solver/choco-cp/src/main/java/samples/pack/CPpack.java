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
package samples.pack;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import choco.cp.solver.constraints.global.pack.PackSConstraint;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.branching.PackDynRemovals;
import choco.cp.solver.search.integer.valselector.BestFit;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.opres.pack.BestFit1BP;
import choco.kernel.common.opres.pack.LowerBoundFactory;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.pack.PackModeler;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.ValSelector;
import gnu.trove.TIntArrayList;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @author Arnaud Malapert</br>
 * @since 7 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public class CPpack {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	/////////// CONFIGURATION //////////////////

	public final static int DEFAULT_TIMELIMIT = 30;

	/** time limit (s) */
	protected int tlimit=DEFAULT_TIMELIMIT;

	enum Branching {BASIC,BEST_FIT,DYN_RM}

	public Branching branching = Branching.DYN_RM;


	//////////////// COMPUTATIONS /////////////////
	/** the number of bins of the optimal solution */
	public final int optimum;

	/** the sizes of the items sorted according to non increasing order*/
	public final int[] sizes;

	/** the capacity of the bins*/
	public final int capacity;

	/** Initial lower bound */
	protected int ilb;

	/** Initial upper bound */
	protected int iub;

	/** Final upper bound */
	protected int fub;

	//////////////// MODEL /////////////////

	/** variables of the model*/
	protected PackModeler modeler;

	protected CPModel model;
	
	protected CPSolver solver;

	protected Constraint pack;



	public CPpack(final int[] sizes, final int capacity, final int optimum) {
		super();
		this.sizes = sizes;
		this.capacity = capacity;
		this.optimum = optimum;
	}

	/**
	 * I/O function
	 */
	private void displayMessage() {
		final StringBuilder msg=new StringBuilder();
		msg.append("solving class:").append(this.getClass().getSimpleName());
		msg.append("\n1BP instance:\nOptimum=").append(optimum);
		msg.append(" bins\nCapacity=").append(capacity).append("\nnbItems=").append(sizes.length);
		msg.append("\nitems= ").append(Arrays.toString(sizes));
		msg.append('\n');
		LOGGER.info(msg.toString());
	}


	public final int getTimelimit() {
		return tlimit;
	}

	public final void setTimelimit(final int tlimit) {
		this.tlimit = tlimit>0 ? tlimit : DEFAULT_TIMELIMIT;
	}

	/**
	 * a good initial upper bound (bestFit) to improve propagation.
	 * @return
	 */
	public int computeHeuristicSolution() {
		final BestFit1BP bf = new BestFit1BP(capacity);
		TIntArrayList items = new TIntArrayList(sizes);
		items.reverse();
		return  bf.computeUB(items);

	}

	/**
	 * also a good lower to perform optimaliy check. the bound is updated during propagation
	 * @return
	 */
	public int computeLowerBound() {
		return LowerBoundFactory.computeL_DFF_1BP(sizes,capacity,iub); //choco library
	}

	protected void setObjective() {
		IntegerVariable o = modeler.nbNonEmpty;
		o.addOption(Options.V_OBJECTIVE);
		model.addConstraints(
				geq(o,ilb),
				leq(o,iub-1)
		);
		model.addConstraints(modeler.getNbNonEmptyBinsRC());
	}

	public void initializeModel() {
		model = new CPModel();
		modeler = new PackModeler(sizes,iub-1,capacity);
		pack = pack(modeler, SettingType.ADDITIONAL_RULES.getOptionName(),SettingType.DYNAMIC_LB.getOptionName());
		pack.addOption(SettingType.FILL_BIN.getOptionName());
		setObjective();
		modeler.statePackLargeItems(model, false, true); // best symmetry breaking ? seems better than sorting bins method
		model.addConstraint(pack);
	}


	public void makeBranching(Solver s) {
		final PackSConstraint cstr = (PackSConstraint) s.getCstr(pack);
		VarSelector varsel = new StaticVarOrder(s, s.getVar(modeler.bins));
		ValSelector valsel= branching==Branching.BASIC ? new MinVal() : new BestFit(cstr);
		s.attachGoal( branching==Branching.DYN_RM ?
					new PackDynRemovals(varsel,valsel,cstr) :
						new AssignVar(varsel,valsel)
					);
	}

	public CPSolver generateSearchStrategy() {
		final CPSolver s= new CPSolver(); //creates the solver
		s.read(model);  //read the model
		makeBranching(s); //create goal
		s.setFirstSolution(false); //do not stop at first solution
		s.setTimeLimit(tlimit*1000);
		s.setDoMaximize(false); //minimize
		s.setRestart(false); //no restart
		s.generateSearchStrategy();
		return s;
	}

	public final int cpPack() {
		displayMessage(); // pretty printing
		iub=computeHeuristicSolution();
		ilb=computeLowerBound();
		LOGGER.info("ILB="+ilb+" IUB="+iub);
		if(iub==ilb) {
			LOGGER.info("heuristic solution is optimal");
			return iub;
		}else {
			LOGGER.info("need search phase");
			initializeModel();
			//LOGGER.info(model.pretty());
			solver=generateSearchStrategy();
			solver.launch();
			fub=analyze();
			LOGGER.info("objective="+fub+"\n\n");
			return fub;
		}
	}

	/**
	 * determine solver status and I/O
	 * @return the final upper bound
	 */
	protected int analyze() {
		//Analyze
		solver.printRuntimeStatistics();
		if(solver.isFeasible()==Boolean.TRUE) {
			LOGGER.info(solver.solutionToString()+"\n");
			if(solver.isEncounteredLimit()) {
				LOGGER.info("improve heuristic solution but a limit was attempted.\nLimit: "+solver.getEncounteredLimit());
			}else {LOGGER.info("improve heuristic solution and prove optimality");}
			return solver.getVar(modeler.nbNonEmpty).getVal();
		}else if (solver.isFeasible()==Boolean.FALSE){
			LOGGER.info("do not improve solution but prove optimality for heuristic solution");
		}else if(solver.isEncounteredLimit()) {
			LOGGER.info("do not improve solution (limit  attempted).\nLimit: "+solver.getEncounteredLimit());
		}else {
			LOGGER.severe("error occured during search");
		}
		return iub;
	}

	public final PackModeler getModeler() {
		return modeler;
	}

	public final CPSolver getSolver() {
		return solver;
	}
	
	
}

