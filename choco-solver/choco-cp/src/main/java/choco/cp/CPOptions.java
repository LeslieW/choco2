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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp;

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
public class CPOptions {

    public static final String NO_OPTION = "";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// VARIABLE //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Goal : force Solver to create bounded domain variable.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}'s cardinality variable.<br/>
     */
   public static final String V_BOUND = "cp:bound";

    /**
     * Goal : force Solver to create enumerated domain variable (default options if options is empty).<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}'s cardinality variable
     * and {@link choco.kernel.model.variables.real.RealVariable}.<br/>
     * (default option)
     */
    public static final String V_ENUM = "cp:enum";

    /**
     * Goal : force Solver to create binary tree domain variable.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable}.<br/>
     */
    public static final String V_BTREE = "cp:btree";

    /**
     * Goal : force Solver to create bipartite list domain variable.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable}.<br/>
     */
    public static final String V_BLIST = "cp:blist";


    /**
     * Goal : force Solver to create linked list domain variable.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable}.<br/>
     */
    public static final String V_LINK = "cp:link";

    /**
     * Goal : declare the current variable as makespan.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable}.<br/>
     */
    public static final String V_MAKESPAN = "cp:makespan";

    /**
     * Goal : declare variable as a decisional one.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}
     * and {@link choco.kernel.model.variables.real.RealVariable}.<br/>
     */
    public static final String V_DECISION = "cp:decision";

    /**
     * Goal : force variable to be removed from the pool of decisionnal variables.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}
     * and {@link choco.kernel.model.variables.real.RealVariable}.<br/>
     */
    public static final String V_NO_DECISION = "cp:no_decision";

    /**
     * Goal : declare objective variable.<br/>
     * Scope: {@link choco.kernel.model.variables.integer.IntegerVariable},
     * {@link choco.kernel.model.variables.set.SetVariable}
     * and {@link choco.kernel.model.variables.real.RealVariable}.<br/>
     */
    public static final String V_OBJECTIVE = "cp:objective";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// EXPRESSION ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Goal : force decomposition of the scoped expression.<br/>
     * Scope : {@link choco.kernel.model.variables.integer.IntegerExpressionVariable}.
     */
    public static final String E_DECOMP = "cp:decomp";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRAINT ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Goal : to get AC3 algorithm (searching from scratch for supports on all values).<br/>
     * Scope :
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
     * Goal : to get AC3rm algorithm (maintaining the current support of each value in a non backtrackable way).<br/>
     * Scope :
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
     * Goal : to get AC3 with the used of {@link java.util.BitSet} to know if a support still exists.<br/>
     * Scope :
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
     * Goal : to get AC2001 algorithm (maintaining the current support of each value).<br/>
     * Scope :
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
     * Goal : to get AC2008 algorithm (maintained by STR).<br/>
     * Scope :
     * {@link choco.Choco#feasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#infeasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#relationTupleAC(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.solver.constraints.integer.extension.LargeRelation)}
     */
    public static final String C_EXT_AC2008 = "cp:ac2008";

    /**
     * Goal : set filter policy to forward checking.<br/>
     * Scope :
     * {@link choco.Choco#feasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#infeasTupleAC(String, java.util.List, choco.kernel.model.variables.integer.IntegerVariable[])},
     * {@link choco.Choco#relationTupleAC(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.solver.constraints.integer.extension.LargeRelation)}.
     */
    public static final String C_EXT_FC = "cp:fc";

    /**
     * Goal : for Regin implementation.<br/>
     * Scope: {@link choco.Choco#allDifferent(String, choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_ALLDIFFERENT_AC = "cp:ac";

        /**
     * Goal : for bound all different using the propagator of
	 * A. Lopez-Ortiz, C.-G. Quimper, J. Tromp, and P. van Beek.
	 * A fast and simple algorithm for bounds consistency of the alldifferent
	 * constraint. IJCAI-2003.<br/>
     * Scope: {@link choco.Choco#allDifferent(String, choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_ALLDIFFERENT_BC = "cp:bc";

    /**
     * Goal : propagate on the clique of differences.<br/>
     * Scope: {@link choco.Choco#allDifferent(String, choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_ALLDIFFERENT_CLIQUE = "cp:clique";


    /**
     * Goal : for Regin implementation.<br/>
     * Scope: {@link choco.Choco#globalCardinality(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * int[], int[], int)} . 
     */
    public static final String C_GCC_AC = "cp:ac";

    /**
     * Goal : for Quimper implementation.<br/>
     * Scope: {@link choco.Choco#globalCardinality(String, choco.kernel.model.variables.integer.IntegerVariable[],
     * int[], int[], int)} .
     */
    public static final String C_GCC_BC = "cp:bc";

    /**
     * Goal : set filtering policy to filter on lower bound only.<br/>
     * Scope: {@link choco.Choco#increasing_nvalue(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_INCREASING_NVALUE_ATLEAST = "cp:atleast";

    /**
     * Goal : set filtering policy to filter on upper bound only.<br/>
     * Scope: {@link choco.Choco#increasing_nvalue(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_INCREASING_NVALUE_ATMOST = "cp:atmost";

    /**
     * Goal : set filtering policy to filter on lower and upper bound only.<br/>
     * Scope: {@link choco.Choco#increasing_nvalue(String, choco.kernel.model.variables.integer.IntegerVariable,
     * choco.kernel.model.variables.integer.IntegerVariable[])} .
     */
    public static final String C_INCREASING_NVALUE_BOTH = "cp:both";


    /**
     * Goal : global consistency.
     * Scope : {@link choco.Choco#nth(String, choco.kernel.model.variables.integer.IntegerVariable, int[],
     * choco.kernel.model.variables.integer.IntegerVariable)},
     * {@link choco.Choco#nth(String, choco.kernel.model.variables.integer.IntegerVariable, int[],
     * choco.kernel.model.variables.integer.IntegerVariable, int)}
     */
    public static final String C_NTH_G = "cp:G";


    /**
     * Goal: Ensure quick entailment tests.
     * Scope : {@link choco.Choco#clause(choco.kernel.model.variables.integer.IntegerVariable[],
     * choco.kernel.model.variables.integer.IntegerVariable[])}
     */
    public static final String C_CLAUSES_ENTAIL = "cp:entail";

    /**
     * Goal: postponed a constraint.
     * Scope : {@link choco.kernel.solver.constraints.SConstraint}.
     */
    public static final String C_POST_PONED = "cp:postponed";


}
