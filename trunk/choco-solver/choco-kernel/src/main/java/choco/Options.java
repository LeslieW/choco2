/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _      _                             *
 *         |  (..)  |                            *
 *         |_ J||L _|         CHOCO solver       *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 25 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 *
 * A class to declare options concerning variables and constraints.
 * Available for module choco-cp only.
 *
 */
public class Options {

    public static final String NO_OPTION = "";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// VARIABLE //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <br/><b>Goal</b> : force Solver to create bounded domain variable.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}'s cardinality variable.
     */
   public static final String V_BOUND = "cp:bound";

    /**
     * <br/><b>Goal</b> : force Solver to create enumerated domain variable (default options if options is empty).
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}'s cardinality variable
     * (default option)
     */
    public static final String V_ENUM = "cp:enum";

    /**
     * <br/><b>Goal</b> : force Solver to create binary tree domain variable.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable}.
     */
    public static final String V_BTREE = "cp:btree";

    /**
     * <br/><b>Goal</b> : force Solver to create bipartite list domain variable.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable}.
     */
    public static final String V_BLIST = "cp:blist";


    /**
     * <br/><b>Goal</b> : force Solver to create linked list domain variable.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable}.
     */
    public static final String V_LINK = "cp:link";

    /**
     * <br/><b>Goal</b> : declare the current variable as makespan.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable}.
     */
    public static final String V_MAKESPAN = "cp:makespan";

    /**
     * <br/><b>Goal</b> : declare variable as a decisional one.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}
     * and {@link choco.kernel.model.variables.real.RealVariable}.
     * @deprecated This option has no longer effect
     * as by default every variables are put in the decision variable pool.
     */
    public static final String V_DECISION = "cp:decision";

    /**
     * <br/><b>Goal</b> : force variable to be removed from the pool of decisionnal variables.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}
     * and {@link choco.kernel.model.variables.real.RealVariable}.
     */
    public static final String V_NO_DECISION = "cp:no_decision";

    /**
     * <br/><b>Goal</b> : declare objective variable.
     * <br/><b>Scope</b>: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}
     * and {@link choco.kernel.model.variables.real.RealVariable}.
     */
    public static final String V_OBJECTIVE = "cp:objective";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// EXPRESSION ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <br/><b>Goal</b> : force decomposition of the <b>scoped</b> expression.
     * <br/><b>Scope</b> : {@link choco.kernel.model.variables.integer.IntegerExpressionVariable}.
     */
    public static final String E_DECOMP = "cp:decomp";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRAINT ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <br/><b>Goal</b> : to get AC3 algorithm (searching from scratch for supports on all values).
     * <br/><b>Scope</b> :
     * {@link choco.Choco#feasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, boolean[][])},
     * {@link choco.Choco#relationPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.solver.constraints.integer.extension.BinRelation)},
     */
    public static final String C_EXT_AC3 = "cp:ac3";

    /**
     * <br/><b>Goal</b> : to get AC3rm algorithm (maintaining the current support of each value in a non backtrackable way).
     * <br/><b>Scope</b> :
     * {@link choco.Choco#feasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#feasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, boolean[][])},
     * {@link choco.Choco#relationPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.solver.constraints.integer.extension.BinRelation)},
     * {@link choco.Choco#relationTupleAC(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.solver.constraints.integer.extension.LargeRelation)}
     */
    public static final String C_EXT_AC32 = "cp:ac32";

    /**
     * <br/><b>Goal</b> : to get AC3 with the used of {@link java.util.BitSet} to know if a support still exists.
     * <br/><b>Scope</b> :
     * {@link choco.Choco#feasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, boolean[][])},
     * {@link choco.Choco#relationPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.solver.constraints.integer.extension.BinRelation)},
     */
    public static final String C_EXT_AC322 = "cp:ac322";

    /**
     * <br/><b>Goal</b> : to get AC2001 algorithm (maintaining the current support of each value).
     * <br/><b>Scope</b> :
     * {@link choco.Choco#feasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#feasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, java.util.List)},
     * {@link choco.Choco#infeasPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable, boolean[][])},
     * {@link choco.Choco#relationPairAC(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.solver.constraints.integer.extension.BinRelation)},
     * {@link choco.Choco#relationTupleAC(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.solver.constraints.integer.extension.LargeRelation)}
     */
    public static final String C_EXT_AC2001 = "cp:ac2001";

    /**
     * <br/><b>Goal</b> : to get AC2008 algorithm (maintained by STR).
     * <br/><b>Scope</b> :
     * {@link choco.Choco#feasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#infeasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#relationTupleAC(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.solver.constraints.integer.extension.LargeRelation)}
     */
    public static final String C_EXT_AC2008 = "cp:ac2008";

    /**
     * <br/><b>Goal</b> : set filter policy to forward checking.
     * <br/><b>Scope</b> :
     * {@link choco.Choco#feasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#infeasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#relationTupleAC(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.solver.constraints.integer.extension.LargeRelation)}.
     */
    public static final String C_EXT_FC = "cp:fc";

    /**
     * <br/><b>Goal</b> : for Regin implementation.
     * <br/><b>Scope</b>: {@link choco.Choco#allDifferent(String, choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_ALLDIFFERENT_AC = "cp:ac";

        /**
     * <br/><b>Goal</b> : for bound all different using the propagator of
	 * A. Lopez-Ortiz, C.-G. Quimper, J. Tromp, and P. van Beek.
	 * A fast and simple algorithm for bounds consistency of the alldifferent
	 * constraint. IJCAI-2003.
     * <br/><b>Scope</b>: {@link choco.Choco#allDifferent(String, choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_ALLDIFFERENT_BC = "cp:bc";

    /**
     * <br/><b>Goal</b> : propagate on the clique of differences.
     * <br/><b>Scope</b>: {@link choco.Choco#allDifferent(String, choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_ALLDIFFERENT_CLIQUE = "cp:clique";


    /**
     * <br/><b>Goal</b> : for Regin implementation.
     * <br/><b>Scope</b>: {@link choco.Choco#globalCardinality(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * int[], int[], int)} .
     */
    public static final String C_GCC_AC = "cp:ac";

    /**
     * <br/><b>Goal</b> : for Quimper implementation.
     * <br/><b>Scope</b>: {@link choco.Choco#globalCardinality(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * int[], int[], int)} .
     */
    public static final String C_GCC_BC = "cp:bc";

    /**
     * <br/><b>Goal</b> : set filtering policy to filter on lower bound only.
     * <br/><b>Scope</b>: {@link choco.Choco#increasing_nvalue(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_INCREASING_NVALUE_ATLEAST = "cp:atleast";

    /**
     * <br/><b>Goal</b> : set filtering policy to filter on upper bound only.
     * <br/><b>Scope</b>: {@link choco.Choco#increasing_nvalue(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_INCREASING_NVALUE_ATMOST = "cp:atmost";

    /**
     * <br/><b>Goal</b> : set filtering policy to filter on lower and upper bound only.
     * <br/><b>Scope</b>: {@link choco.Choco#increasing_nvalue(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_INCREASING_NVALUE_BOTH = "cp:both";


    /**
     * <br/><b>Goal</b> : global consistency.
     * <br/><b>Scope</b> : {@link choco.Choco#nth(String, choco.kernel.model.variables.integer.IntegerVariable, int[],
     * choco.kernel.model.variables.integer.IntegerVariable)},
     * {@link choco.Choco#nth(String, choco.kernel.model.variables.integer.IntegerVariable, int[],
     * choco.kernel.model.variables.integer.IntegerVariable, int)}
     */
    public static final String C_NTH_G = "cp:G";


    /**
     * <br/><b>Goal</b>: Ensure quick entailment tests.
     * <br/><b>Scope</b> : {@link choco.Choco#clause(choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.model.variables.integer.IntegerVariable[])}
     */
    public static final String C_CLAUSES_ENTAIL = "cp:entail";

    /**
     * <br/><b>Goal</b>: postponed a constraint.
     * <br/><b>Scope</b> : {@link choco.kernel.model.constraints.Constraint}.
     */
    public static final String C_POST_PONED = "cp:postponed";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// SOLVER ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <br/><b>Goal</b>: Allow a solver to read a model more than one time.
     * <br/><i>In that case, the redundant constraints for scheduling must be posted explicitly</i>.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_MULTIPLE_READINGS = "cp:multiple_readings";

    /**
     * <br/><b>Goal</b>: Search for all solutions of a problem.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_SOLVE_ALL = "cp:solve:all";

    /**
     * <br/><b>Goal</b>: Search for the first solution of a problem.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_SOLVE_FIRST = "cp:solve:first";

    /**
     * <br/><b>Goal</b>: Search for a maximization of an declared objective variable.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_OPT_MAXIMIZE = "cp:optimize:max";

    /**
     * <br/><b>Goal</b>: Search for a minimization of an declared objective variable.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_OPT_MINIMIZE = "cp:optimize:min";

    /**
     * <br/><b>Goal</b>: For optimization, do restart from root node after each solution.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_RESTART_AFTER_SOLUTION = "cp:restart:solution";

    /**
     * <br/><b>Goal</b>: To enable luby restart.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_RESTART_LUBY = "cp:restart:luby";

    /**
     * <br/><b>Goal</b>: To enable geometrical restart.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_RESTART_GEOMETRICAL = "cp:restart:geo";

    /**
     * <br/><b>Goal</b>: Enable nogood recording from restart.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_NOGOOD_RECORDING_FROM_RESTART = "cp:nogood:restart";

    /**
     * <br/><b>Goal</b>: Allow recomputation : worlds are not saved at each node, but every <i>gap</i> nodes.
     * Between two unsaved worlds, branching statements are applied and propagated.
     * <br/> By default, <i>gap</i>=10.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_RECOMPUTATION = "cp:recomputation";

    /**
     * <br/><b>Goal</b>: Enable card reasonning: decide if redundant constraints are automatically added
     * to the model to reason on cardinalities on sets as well as kernel and enveloppe.
     * <br/><b>Scope</b> : {@link choco.kernel.solver.Solver}.
     */
    public static final String S_CARD_REASONNING = "cp:card:reasonning";
}
