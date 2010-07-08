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
package samples.tutorials.scheduling.pack.binpacking;

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
import samples.tutorials.PatternExample;

import java.util.Arrays;
import java.util.logging.Logger;

import static choco.Choco.*;

/**
 * @author Arnaud Malapert</br>
 * @since 7 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 *
 * TODO: this example is not enough complex, heuristic find the optimal solution...
 */
public class CPpack extends PatternExample{

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	/////////// CONFIGURATION //////////////////

	public final static int DEFAULT_TIMELIMIT = 30;

	/** time limit (s) */
	protected int tlimit=DEFAULT_TIMELIMIT;


    enum Branching {BASIC,BEST_FIT,DYN_RM}

	public Branching branching = Branching.DYN_RM;


	//////////////// COMPUTATIONS /////////////////
	/** the number of bins of the optimal solution */
	public int optimum = 25; // default value, for example

	/** the sizes of the items sorted according to non increasing order*/
	public int[] sizes = new int[]{
                    99, 99, 96, 96, 92,
                    92, 91, 88, 87, 86,
                    85, 76, 74, 72, 69,
                    67, 67, 62, 61, 56,
                    52, 51, 49, 46, 44,
                    42, 40, 40, 33, 33,
                    30, 30, 29, 28, 28,
                    27, 25, 24, 23, 22,
                    21, 20, 17, 14, 13,
                    11, 10, 7, 7, 3
                }; // default value, for example

	/** the capacity of the bins*/
	public int capacity = 100; // default value, for example

	/** Initial lower bound */
	protected int ilb;

	/** Initial upper bound */
	protected int iub;

	/** Final upper bound */
	protected int fub;

	//////////////// MODEL /////////////////

	/** variables of the model*/
	protected PackModeler modeler;

	protected Constraint pack;


    @Override
    public void printDescription() {
		final StringBuilder msg=new StringBuilder();
		msg.append("solving class:").append(this.getClass().getSimpleName());
		msg.append("\n1BP instance:\n\tOptimum = ").append(optimum>-1?optimum:"??");
		msg.append(" bins\n\tCapacity = ").append(capacity).append("\n\tnbItems = ").append(sizes.length);
		msg.append("\n\titems : ").append(Arrays.toString(sizes));
		msg.append('\n');
		LOGGER.info(msg.toString());
	}

    public void setUp(Object parameters){
        if(parameters != null){
            Object[] params = (Object[])parameters;
            this.sizes = (int[])params[0];
            this.capacity = (Integer)params[1];
            if(params.length == 3){
                this.optimum = (Integer)params[2];
            }else{
                this.optimum = -1;
            }
        }
        iub=computeHeuristicSolution();
		ilb=computeLowerBound();
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

    @Override
	public void buildModel() {
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

	@Override
    public void buildSolver() {
        solver = new CPSolver(); // create the solver
		solver.read(model);  //read the model
		makeBranching(solver); //create goal
		solver.setFirstSolution(false); //do not stop at first solution
		solver.setTimeLimit(tlimit*1000);
		solver.setDoMaximize(false); //minimize
		solver.setRestart(false); //no restart
		solver.generateSearchStrategy();
	}

    @Override
    public void solve() {
        solver.launch();
    }

	public final int cpPack() {
		printDescription(); // pretty printing
		LOGGER.info("ILB="+ilb+" IUB="+iub);
		if(iub==ilb) {
			LOGGER.info("heuristic solution is optimal");
			return iub;
		}else {
			LOGGER.info("need search phase");
			buildModel();
			buildSolver();
			solve();
			prettyOut();
			return fub;
		}
	}

	/**
	 * determine solver status and I/O
	 * @return the final upper bound
	 */
    @Override
	public void prettyOut() {
		//Analyze
		solver.printRuntimeStatistics();
		if(solver.isFeasible()==Boolean.TRUE) {
			LOGGER.info(solver.solutionToString()+"\n");
			if(solver.isEncounteredLimit()) {
				LOGGER.info("improve heuristic solution but a limit was attempted.\nLimit: "+solver.getEncounteredLimit());
			}else {LOGGER.info("improve heuristic solution and prove optimality");}
            LOGGER.info("objective="+solver.getVar(modeler.nbNonEmpty).getVal()+"\n\n");
		}else if (solver.isFeasible()==Boolean.FALSE){
			LOGGER.info("do not improve solution but prove optimality for heuristic solution");
		}else if(solver.isEncounteredLimit()) {
			LOGGER.info("do not improve solution (limit  attempted).\nLimit: "+solver.getEncounteredLimit());
		}else {
			LOGGER.severe("error occured during search");
		}
        LOGGER.info("objective="+iub+"\n\n");
	}


    public PackModeler getModeler(){
        return modeler;
    }


    public static void main(String[] args) {
        // Solve the loaded instance
        CPpack pack = new CPpack();
        pack.execute();
    }
}

