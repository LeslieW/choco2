package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.global.regular.DFA;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.BinRelation;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.IterIndexedLargeRelation;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.IterLargeRelation;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.LargeRelation;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.palm.cbj.JumpSolver;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.integer.JumpIntVar;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.GenericExplanation;
import i_want_to_use_this_old_version_of_choco.palm.global.matching.PalmAllDifferent;
import i_want_to_use_this_old_version_of_choco.palm.global.matching.PalmCardinality;
import i_want_to_use_this_old_version_of_choco.palm.global.matching.PalmOccurence;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.*;
import i_want_to_use_this_old_version_of_choco.palm.real.constraints.PalmEquation;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealMinus;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealMult;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealPlus;
import i_want_to_use_this_old_version_of_choco.prop.ChocEngine;
import i_want_to_use_this_old_version_of_choco.prop.ConstraintEvent;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEngine;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

import java.util.BitSet;
import java.util.List;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpProblem extends AbstractProblem implements ExplainedProblem {
    protected static final Logger logger = Logger.getLogger("choco");

    /**
     * States if the release information should be displayed during the next problem instantiation.
     */
    public static boolean displayRelease = false;


    /**
     * Maximum relaxation level acceptable without user interaction.
     */

    public int maxRelaxLevel = 0;

    /**
     * Set with last erased constraints (index that can be used for posted constraints)
     */

    protected BitSet erasedCst;

    /**
     * Contradiction explanation: a conflict set justifying that the problem is inconsistent.
     */
    protected Explanation contradictionExplanation = null;


    /**
     * an index useful for re-propagating cuts (static constraints)
     * upon backtracking
     */
    public IStateInt indexOfLastInitializedStaticConstraint;

    /**
     * Displays release information (date, verions, ...).
     */

    public static void ReleaseJumpDisplay() {
        logger.info("** Palm : Constraint Programming with Efficient Explanations");
        logger.info("** Palm Copyright (c) 2004 B206");
        displayRelease = false;
    }


    /**
     * Creates a Palm Problem with the specified environment.
     */
    public JumpProblem() {
        super();

        // Ensures a determinist behaviour
        GenericExplanation.reinitTimestamp();
        erasedCst = new BitSet();

        // Specialized engine and solver for Palm
        this.propagationEngine = new ChocEngine(this);
        this.solver = new JumpSolver(this);
        this.indexOfLastInitializedStaticConstraint = environment.makeInt(PartiallyStoredVector.getFirstStaticIndex() - 1);
        // Displays information about Palm
        if (displayRelease) ReleaseJumpDisplay();
    }

    /**
     * Factory to create explanation.
     * It offers the possibility to make another kind of explanation, only by extending PalmProblem
     *
     * @return the new explanation object
     */
    public Explanation makeExplanation() {
        return new JumpExplanation(this);
    }

    /**
     * Factory to create explanation.
     * It offers the possibility to make another kind of explanation, only by extending PalmProblem
     *
     * @return the new explanation object
     */
    public Explanation makeExplanation(int level) {
        return new JumpExplanation(level, this);
    }

    /**
     * Returns all variables of the variables.
     *
     * @deprecated
     */
    public IntDomainVar[] getVars() {
        IntDomainVar[] array = new IntDomainVar[0];
        return (IntDomainVar[]) this.intVars.toArray(array);
    }

    public void explainedFail(Explanation exp) throws ContradictionException {
        throw new JumpContradictionException(this, exp);
    }

    /**
     * Posts a constraints in the problem.
     * This is a local constraint post that will be undone upon backtracking
     * If it has ever been posted (but deactivated), it is
     * only reactivated and repropagated.
     *
     * @param cc The constraint to post.
     */
    public void post(Constraint cc) {
        if (cc instanceof PalmConstraint) {
            PalmConstraint c = (PalmConstraint) cc;
            int idx = constraints.add(c);
            c.addListener(true);
            ((JumpConstraintPlugin) c.getPlugIn()).setConstraintIdx(idx);//constraints.size() - 1);
            ConstraintEvent event = (ConstraintEvent) c.getEvent();
            PropagationEngine pe = getPropagationEngine();
            pe.registerEvent(event);
            pe.postConstAwake(c, true);
        } else {
            throw new Error("impossible to post to a JumpProblem constraints that are not PalmConstraints");
        }
    }

    /**
     * Posts a cut constraint in the problem.
     * (the constraint will not be undone upon backtracking)
     *
     * @param cc The constraint to post.
     */
    public void postCut(Constraint cc) {
        if (cc instanceof PalmConstraint) {
            PalmConstraint c = (PalmConstraint) cc;
            int idx = constraints.staticAdd(c);
            c.addListener(false);
            indexOfLastInitializedStaticConstraint.set(idx);
            ((JumpConstraintPlugin) c.getPlugIn()).setConstraintIdx(idx);//constraints.size() - 1);
            ConstraintEvent event = (ConstraintEvent) c.getEvent();
            PropagationEngine pe = getPropagationEngine();
            pe.registerEvent(event);
            pe.postConstAwake(c, true);
        } else {
            throw new Error("impossible to post to a JumpProblem cuts that are not PalmConstraints");
        }
    }

    /**
     * popping one world from the stack:
     * overrides AbstractProblem.worldPop because the Problem class adds
     * the notion of static constraints that need be repropagated upon backtracking
     */
    public final void worldPop() {
        super.worldPop();
        int lastStaticIdx = constraints.getLastStaticIndex();
        for (int i = indexOfLastInitializedStaticConstraint.get() + 1; i <= lastStaticIdx; i++) {
            Propagator c = (Propagator) constraints.get(i);
            if (c != null) {
                c.constAwake(true);
            }
        }
    }

    public ExplainedConstraintPlugin makeConstraintPlugin(AbstractConstraint ct) {
        return new JumpConstraintPlugin(ct);
    }

    /**
     * @param nb Constraint number (number affected when posting and stored
     *           in the variable plugin)
     * @return Returns the constraint
     */
    public AbstractConstraint getConstraintNb(int nb) {
        return (AbstractConstraint) this.constraints.get(nb);
    }

    public Explanation getContradictionExplanation() {
        return contradictionExplanation;
    }

    public void setContradictionExplanation(Explanation contradictionExplanation) {
        this.contradictionExplanation = contradictionExplanation;
    }

    public Boolean solve(boolean all) {
        throw new Error("solve not implemented on JumpProblem");
    }

    // ------------------------------------------------------------------------

    // All abstract methods for constructing constraint
    // that need be defined by a Problem implementing a model

    protected Constraint createEqualXC(IntVar v0, int c) {
        if (v0 instanceof IntDomainVar) {
            return new PalmEqualXC((IntDomainVar) v0, c);
        } else
            return null;
    }

    protected Constraint createNotEqualXC(IntVar v0, int c) {
        if (v0 instanceof IntDomainVar) {
            return new PalmNotEqualXC((IntDomainVar) v0, c);
        } else
            return null;
    }

    protected Constraint createGreaterOrEqualXC(IntVar v0, int c) {
        if (v0 instanceof IntDomainVar) {
            return new PalmGreaterOrEqualXC((IntDomainVar) v0, c);
        } else
            return null;
    }

    protected Constraint createLessOrEqualXC(IntVar v0, int c) {
        if (v0 instanceof IntDomainVar) {
            return new PalmLessOrEqualXC((IntDomainVar) v0, c);
        } else
            return null;
    }

    protected Constraint createEqualXYC(IntVar v0, IntVar v1, int c) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new PalmEqualXYC((IntDomainVar) v0, (IntDomainVar) v1, c);
        } else
            return null;
    }

    protected Constraint createGreaterOrEqualXYC(IntVar v0, IntVar v1, int c) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new PalmGreaterOrEqualXYC((IntDomainVar) v0, (IntDomainVar) v1, c);
        } else
            return null;
    }

    protected Constraint createNotEqualXYC(IntVar v0, IntVar v1, int c) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new PalmNotEqualXYC((IntDomainVar) v0, (IntDomainVar) v1, c);
        } else
            return null;
    }

    protected Constraint createTimesXYZ(IntVar x, IntVar y, IntVar z) {
        throw new UnsupportedOperationException("Multiplication not implemented in Palm");
    }

    protected Constraint createIntLinComb(IntVar[] sortedVars, int[] sortedCoeffs, int nbPositiveCoeffs, int c, int linOperator) {
        IntDomainVar[] tmpVars = new IntDomainVar[sortedVars.length];
        System.arraycopy(sortedVars, 0, tmpVars, 0, sortedVars.length);
        return new PalmIntLinComb(tmpVars, sortedCoeffs, nbPositiveCoeffs, c, linOperator);
    }

    protected Constraint createSubscript(IntVar index, int[] values, IntVar val, int offset) {
        if ((index instanceof IntDomainVar) && (val instanceof IntDomainVar)) {
            return new PalmElt((IntDomainVar) index, (IntDomainVar) val, offset, values);
        } else {
            return null;
        }
    }

    public Constraint abs(IntVar x, IntVar y) {
       throw new Error("Absolute constraint are not yet available in Palm"); 
    }

    protected Constraint createSubscript(IntVar index, IntVar index2, int[][] valArray, IntVar val) {
        throw new Error("EltV constraints are not yet available in Palm in 2D");
    }

    protected Constraint createSubscript(IntVar index, IntVar[] varArray, IntVar val, int offset) {
        throw new Error("EltV constraints are not yet available in Palm");
    }

    protected Constraint createInverseChanneling(IntVar[] x, IntVar[] y) {
        throw new Error("Inverse channeing is not yet available in Palm");
    }

    protected Constraint createBoolChanneling(IntVar b, IntVar x, int j) {
        throw new Error("Bool channeling is not yet available in Palm");
    }

    protected Constraint createBinDisjunction(Constraint c0, Constraint c1) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createLargeDisjunction(Constraint[] alternatives) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createBinConjunction(Constraint c0, Constraint c1) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createLargeConjunction(Constraint[] alternatives) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createNegation(Constraint c){
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createCardinality(Constraint[] constList, IntVar cardVar, boolean b, boolean b1) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createGuard(Constraint c0, Constraint c1) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    protected Constraint createEquiv(Constraint c0, Constraint c1) {
        throw new Error("Boolean connectors are not yet available in Palm");
    }

    public Constraint createAC3BinConstraint(IntVar v1, IntVar v2, BinRelation relation) {
        throw new Error("Extension Constraints are not yet available in Palm");
    }

    public Constraint createAC4BinConstraint(IntVar v1, IntVar v2, BinRelation relation) {
        throw new Error("Extension Constraints are not yet available in Palm");
    }

    public Constraint createAC2001BinConstraint(IntVar v1, IntVar v2, BinRelation relation) {
        throw new Error("Extension Constraints are not yet available in Palm");
    }

	public Constraint createAC3rmBinConstraint(IntVar v1, IntVar v2, BinRelation relation) {
	    throw new Error("Extension Constraints are not yet available in Palm");
	}

    protected Constraint createFCLargeConstraint(IntVar[] vs, LargeRelation relation) {
        throw new Error("Extension Constraints are not yet available in Palm");
    }

	protected Constraint createGAC3rmNegativeLargeConstraint(IntVar[] vars, LargeRelation relation) {
		throw new Error("Extension Constraints are not yet available in Palm");
	}

	protected Constraint createGAC2001PositiveLargeConstraint(IntVar[] vs, IterLargeRelation relation) {
        throw new Error("Extension Constraints are not yet available in Palm");
    }

	protected Constraint createGAC3rmPositiveLargeConstraint(IntVar[] vs, IterIndexedLargeRelation relation) {
		throw new Error("Extension Constraints are not yet available in Palm");
	}

	protected Constraint createGAC2001NegativeLargeConstraint(IntVar[] vs, LargeRelation relation) {
        throw new Error("Extension Constraints are not yet available in Palm"); 
    }

    protected Constraint createOccurrence(IntVar[] vars, int occval, boolean onInf, boolean onSup) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new PalmOccurence(tmpVars, occval, onInf, onSup);
    }

    public Constraint createAllDifferent(IntVar[] vars) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return PalmAllDifferent.newAllDiff(tmpVars);
    }


    protected Constraint createMin(IntVar[] lvars, IntVar min) {
        throw new UnsupportedOperationException("Min Constraints are not yet available in Palm ");
    }

    protected Constraint createMax(IntVar[] lvars, IntVar max) {
        throw new UnsupportedOperationException("Max Constraints are not yet available in Palm ");
    }

    protected Constraint createBoundAllDiff(IntVar[] vars, boolean global) {
        throw new UnsupportedOperationException("BoundAllDiff Constraints are not yet available in Palm ");
    }

	protected Constraint createBoundGlobalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up) {
		throw new UnsupportedOperationException("BoundGCC Constraints are not yet available in Palm ");
 	}

	protected Constraint createBoundGlobalCardinalityVar(IntVar[] vars, int min, int max, IntVar[] card) {
		throw new UnsupportedOperationException("BoundGCC Constraints are not yet available in Palm ");
 	}

	public Constraint createGlobalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new PalmCardinality(tmpVars, min, max, low, up);
    }

    protected Constraint createCumulative(IntVar[] sts, IntVar[] ends, IntVar[] durations, int[] h, int Capa) {
        throw new UnsupportedOperationException("The Cumulative Constraint is not yet available in Palm ");
    }

    protected Constraint createCumulative(IntVar[] sts, IntVar[] ends, IntVar[] durations, IntVar[] h, int Capa) {
        throw new UnsupportedOperationException("The Cumulative Constraint is not yet available in Palm ");
    }

    protected Constraint createAtMostNvalue(IntVar[] vars, IntVar nvalue) {
        throw new UnsupportedOperationException("The AtMostNValue Constraint is not yet available in Palm");
    }

    protected IntDomainVar createIntVar(String name, int domainType, int min, int max) {
        return new JumpIntVar(this, name, domainType, min, max);
    }

    protected IntDomainVar createIntVar(String name, int[] sortedValues) {
        return new JumpIntVar(this, name, sortedValues);
    }

    protected RealVar createRealVal(String name, double min, double max) {
        throw new UnsupportedOperationException("no RealVars handled by Palm");
    }

    protected SetVar createSetVar(String name, int a, int b, boolean enumcard) {
        throw new UnsupportedOperationException("no SetVars handled by Palm");
    }

    protected RealIntervalConstant createRealIntervalConstant(double a, double b) {
        return new PalmRealIntervalConstant(a, b);
    }

    protected RealExp createRealSin(RealExp exp) {
        throw new UnsupportedOperationException("sin on RealVars handled by Palm");
    }

    protected RealExp createRealCos(RealExp exp) {
        throw new UnsupportedOperationException("cos on RealVars handled by Palm");
    }

    protected RealExp createRealIntegerPower(RealExp exp, int power) {
        throw new UnsupportedOperationException("power RealVars handled by Palm");
    }

    protected RealExp createRealPlus(RealExp exp1, RealExp exp2) {
        return new PalmRealPlus(this, exp1, exp2);
    }

    protected RealExp createRealMinus(RealExp exp1, RealExp exp2) {
        return new PalmRealMinus(this, exp1, exp2);
    }

    protected RealExp createRealMult(RealExp exp1, RealExp exp2) {
        return new PalmRealMult(this, exp1, exp2);
    }

    protected Constraint createEquation(RealVar[] tmpVars, RealExp exp, RealIntervalConstant cst) {
        return new PalmEquation(this, tmpVars, exp, cst);
    }

    // -------- API for SetVars ---------------
    protected Constraint createMemberXY(SetVar sv1, IntVar var) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createNotMemberXY(SetVar sv1, IntVar var) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createMemberX(SetVar sv1, int val) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createNotMemberX(SetVar sv1, int val) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createDisjoint(SetVar sv1, SetVar sv2) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createSetIntersection(SetVar sv1, SetVar sv2, SetVar inter) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createSetUnion(SetVar sv1, SetVar sv2, SetVar union) {
        throw new UnsupportedOperationException("SetVars not implemented in Palm");
    }

    protected Constraint createSetCard(SetVar sv, IntVar v, boolean b1, boolean b2) {
        throw new UnsupportedOperationException("Set not implemented in Palm");
    }

    public Constraint boolChanneling(IntVar b, IntVar x, int j) {
        throw new UnsupportedOperationException("boolChanneling not yet explained");
    }

    public Constraint inverseChanneling(IntVar[] x, IntVar[] y) {
        throw new UnsupportedOperationException("inverseChanneling not yet explained");
    }

    protected Constraint createLex(IntVar[] v1, IntVar[] v2, boolean strict) {
        throw new UnsupportedOperationException("Lexicographic not yet explained");
    }

    protected Constraint createRegular(IntVar[] vars, DFA auto) {
        throw new UnsupportedOperationException("Regular not yet explained");
    }

    protected Constraint createRegular(IntVar[] vars, List<int[]> feasibleTuples) {
        throw new UnsupportedOperationException("Regular not yet explained");
    }

    protected Constraint createRegular(IntVar[] vars, List<int[]> infeasibleTuples, int[] min, int[] max) {
        throw new UnsupportedOperationException("Regular not yet explained");
    }

    protected Constraint createStretch(IntVar[] vars, List<int[]> stretchParameters) {
        throw new UnsupportedOperationException("Regular not yet explained");
    }


    protected Constraint createSorting(IntVar[] v1, IntVar[] v2) {
        throw new UnsupportedOperationException("Sorting not yet explained");
    }

    protected Constraint createLeximin(IntVar[] v1, IntVar[] v2) {
        throw new UnsupportedOperationException("Leximin not yet explained");
    }

    protected Constraint createLeximin(int[] v1, IntVar[] v2) {
        throw new UnsupportedOperationException("Leximin not yet explained");
    }
}
