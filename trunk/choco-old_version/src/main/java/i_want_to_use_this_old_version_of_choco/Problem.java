// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.bool.Cardinality;
import i_want_to_use_this_old_version_of_choco.bool.Equiv;
import i_want_to_use_this_old_version_of_choco.bool.Guard;
import i_want_to_use_this_old_version_of_choco.global.*;
import i_want_to_use_this_old_version_of_choco.global.matching.AllDifferent;
import i_want_to_use_this_old_version_of_choco.global.matching.GlobalCardinality;
import i_want_to_use_this_old_version_of_choco.global.regular.DFA;
import i_want_to_use_this_old_version_of_choco.global.regular.Regular;
import i_want_to_use_this_old_version_of_choco.global.regular.Transition;
import i_want_to_use_this_old_version_of_choco.global.scheduling.Cumulative;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.*;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.*;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import i_want_to_use_this_old_version_of_choco.prop.ConstraintEvent;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEngine;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.Equation;
import i_want_to_use_this_old_version_of_choco.real.constraint.RealConstraint;
import i_want_to_use_this_old_version_of_choco.real.exp.*;
import i_want_to_use_this_old_version_of_choco.real.var.RealVarImpl;
import i_want_to_use_this_old_version_of_choco.reified.BinaryConjunction;
import i_want_to_use_this_old_version_of_choco.reified.BinaryDisjunction;
import i_want_to_use_this_old_version_of_choco.reified.gacreified.NegationConstraint;
import i_want_to_use_this_old_version_of_choco.reified.gacreified.Predicat;
import i_want_to_use_this_old_version_of_choco.set.SetConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.set.constraint.*;
import i_want_to_use_this_old_version_of_choco.set.var.SetVarImpl;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;
import i_want_to_use_this_old_version_of_choco.util.UtilAlgo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * A problem is a global structure containing variables bound by listeners
 * as well as solutions or solver parameters
 */
public class Problem extends AbstractProblem {

    /**
     * an index useful for re-propagating cuts (static constraints)
     * upon backtracking
     */
    public IStateInt indexOfLastInitializedStaticConstraint;

    /**
     * Constructs a problem.
     *
     * @param env the IEnvironment responsible for all the memory management
     */

    public Problem(IEnvironment env) {
        super(env);
        this.indexOfLastInitializedStaticConstraint = env.makeInt(PartiallyStoredVector.getFirstStaticIndex() - 1);
    }

    /**
     * Constructs a problem.
     */

    public Problem() {
        this(new EnvironmentTrailing());
    }

    /**
     * <i>Network management:</i>
     * adding a constraint to the problem. Note that this does not propagate anything !
     * This addition of a constraint is local to the current search (sub)tree: the constraint
     * will be un-posted upon backtracking
     *
     * @param cc the constraint to add
     */

    public void post(Constraint cc) {
        if (cc instanceof Propagator) {
            if ((!cc.equals(TRUE) || !constraints.contains(TRUE))
                    && (!cc.equals(FALSE) || !constraints.contains(FALSE))) { // avoid adding the TRUE or FALSE constraint more than one time
                Propagator c = (Propagator) cc;
                c.setProblem(this);
                constraints.add(c);
                c.addListener(true);
                ConstraintEvent event = (ConstraintEvent) c.getEvent();
                PropagationEngine pe = getPropagationEngine();
                pe.registerEvent(event);
                pe.postConstAwake(c, true);
	            postRedundantSetConstraints(cc);
            }
        } else if (cc instanceof Predicat) {
	         Predicat p = (Predicat) cc;
	         p.setScope();
	         post(relationTupleAC(p.getVars(), p));
        } else {
	        throw new Error("impossible to post to a Problem constraints that are not Propagators");
        }
    }

    /**
     * <i>Network management:</i>
     * adding a constraint to the problem. Note that this does not propagate anything !
     * This addition of a constraint is global: the constraint
     * will NOT be un-posted upon backtracking
     *
     * @param cc the constraint to add
     */

    public void postCut(Constraint cc) {
        if (cc instanceof Propagator) {
            if ((!cc.equals(TRUE) || !constraints.contains(TRUE))
                    && (!cc.equals(FALSE) || !constraints.contains(FALSE))) { // avoid adding the TRUE or FALSE constraint more than one time
                Propagator c = (Propagator) cc;
                c.setProblem(this);
                int idx = constraints.staticAdd(c);
                indexOfLastInitializedStaticConstraint.set(idx);
                c.addListener(false);
                ConstraintEvent event = (ConstraintEvent) c.getEvent();
                PropagationEngine pe = getPropagationEngine();
                pe.registerEvent(event);
                pe.postConstAwake(c, true);
            }
        } else if (cc instanceof Predicat) {
	         Predicat p = (Predicat) cc;
	         p.setScope();
	         postCut(relationTupleAC(p.getVars(), p));
        } else {
            throw new Error("impossible to post to a Problem cuts that are not Propagators");
        }
    }

	/**
	 * Post the redundant constraint that allows to capture
	 * the reasonnings on cardinalities
	 * @param p
	 */
	public void postRedundantSetConstraints(Constraint p) {
		if (cardinalityReasonningsOnSETS &&
			p instanceof SetConstraint &&
			p.getNbVars() > 1) {

			if (p instanceof MemberXY) {
				IntDomainVar card0 = ((SetVar) p.getVar(1)).getCard();
				post(geq(card0,1));				
			} else if (p instanceof SetUnion) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();
				IntDomainVar card3 = ((SetVar) p.getVar(2)).getCard();
				post(geq(plus(card0,card1),card3));
			} else if (p instanceof SetIntersection) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();
				IntDomainVar card3 = ((SetVar) p.getVar(2)).getCard();
				post(geq(card0,card3));
				post(geq(card1,card3));
			} else if (p instanceof Disjoint) {
				IntDomainVar card0 = ((SetVar) p.getVar(0)).getCard();
				IntDomainVar card1 = ((SetVar) p.getVar(1)).getCard();								
				int ub = Math.max(((SetVar) p.getVar(0)).getEnveloppeSup(),((SetVar) p.getVar(1)).getEnveloppeSup());
				int lb = Math.min(((SetVar) p.getVar(0)).getEnveloppeInf(),((SetVar) p.getVar(1)).getEnveloppeInf());
				SetVar z = makeSetVar("var_inter: " + p,lb,ub);
				post(setUnion((SetVar) p.getVar(0), (SetVar) p.getVar(1),z));
				post(eq(plus(card0,card1), z.getCard()));
			}
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

    // All abstract methods for constructing constraint
    // that need be defined by a Problem implementing a model

    protected Constraint createEqualXC(IntVar v, int c) {
        if (v instanceof IntDomainVar) {
            return new EqualXC((IntDomainVar) v, c);
        } else
            return null;
    }

    protected Constraint createNotEqualXC(IntVar v, int c) {
        if (v instanceof IntDomainVar) {
            return new NotEqualXC((IntDomainVar) v, c);
        } else {
            return null;
        }
    }

    protected Constraint createGreaterOrEqualXC(IntVar v, int c) {
        if (v instanceof IntDomainVar) {
            return new GreaterOrEqualXC((IntDomainVar) v, c);
        } else {
            return null;
        }
    }

    protected Constraint createLessOrEqualXC(IntVar v, int c) {
        if (v instanceof IntDomainVar) {
            return new LessOrEqualXC((IntDomainVar) v, c);
        } else {
            return null;
        }
    }

    protected Constraint createEqualXYC(IntVar v0, IntVar v1, int c) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new EqualXYC((IntDomainVar) v0, (IntDomainVar) v1, c);
        } else {
            return null;
        }
    }

    protected Constraint createNotEqualXYC(IntVar v0, IntVar v1, int c) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new NotEqualXYC((IntDomainVar) v0, (IntDomainVar) v1, c);
        } else {
            return null;
        }
    }

    protected Constraint createGreaterOrEqualXYC(IntVar v0, IntVar v1, int c) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new GreaterOrEqualXYC((IntDomainVar) v0, (IntDomainVar) v1, c);
        } else {
            return null;
        }
    }

    protected Constraint createTimesXYZ(IntVar x, IntVar y, IntVar z) {
        if ((x instanceof IntDomainVar) &&
                (y instanceof IntDomainVar) &&
                (z instanceof IntDomainVar)) {
            /*if (!((IntDomainVar) x).hasEnumeratedDomain() || !((IntDomainVar) y).hasEnumeratedDomain()
                || !((IntDomainVar) z).hasEnumeratedDomain()) {
                throw new Error("Times do not work with bound int var for the moment");
            } */
            return new TimesXYZ((IntDomainVar) x, (IntDomainVar) y, (IntDomainVar) z);
        } else {
            return null;
        }
    }

    protected Constraint createIntLinComb(IntVar[] sortedVars, int[] sortedCoeffs, int nbPositiveCoeffs, int c, int linOperator) {
        IntDomainVar[] tmpVars = new IntDomainVar[sortedVars.length];
        System.arraycopy(sortedVars, 0, tmpVars, 0, sortedVars.length);
        if (isBoolLinComb(tmpVars, sortedCoeffs, linOperator))
            return createBoolLinComb(tmpVars, sortedCoeffs, c, linOperator);
        else return new IntLinComb(tmpVars, sortedCoeffs, nbPositiveCoeffs, c, linOperator);
    }

    /**
     * Check if the combination is made of a single integer variable and only boolean variables
     */
    protected boolean isBoolLinComb(IntDomainVar[] lvars, int[] lcoeffs, int linOperator) {
        if (linOperator == IntLinComb.NEQ) return false;
        if (lvars.length <= 1) return false;
        int nbEnum = 0;
        for (IntDomainVar lvar : lvars) {
            if (!lvar.hasBooleanDomain())
                nbEnum++;
            if (nbEnum > 1) return false;
        }
        return true;
    }

    protected Constraint createBoolLinComb(IntVar[] vars, int[] lcoeffs, int c, int linOperator) {
        IntDomainVar[] lvars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, lvars, 0, vars.length);
        int idxSingleEnum = -1;                  // index of the enum intvar (the single non boolean var)
        int coefSingleEnum = Integer.MIN_VALUE;  // coefficient of the enum intvar
        for (int i = 0; i < lvars.length; i++) {
            if (!lvars[i].hasBooleanDomain()) {
                idxSingleEnum = i;
                coefSingleEnum = -lcoeffs[i];
            }
        }
        // construct arrays of coefficients and variables
        int nbVar = (idxSingleEnum == -1) ? lvars.length : lvars.length - 1;
        IntDomainVar[] vs = new IntDomainVar[nbVar];
        int[] coefs = new int[nbVar];
        int cpt = 0;
        for (int i = 0; i < lvars.length; i++) {
            if (i != idxSingleEnum) {
                vs[cpt] = lvars[i];
                coefs[cpt] = lcoeffs[i];
                cpt++;
            }
        }
        if (idxSingleEnum == -1)        // the constant c has  already been reversed
            return createBoolLinComb(vs, coefs, null, Integer.MAX_VALUE, c, linOperator);
        else
            return createBoolLinComb(vs, coefs, lvars[idxSingleEnum], coefSingleEnum, c, linOperator);
    }

    protected Constraint createBoolLinComb(IntDomainVar[] vs, int[] coefs, IntDomainVar obj, int objcoef, int c, int linOperator) {
        UtilAlgo.quicksort(coefs, vs, 0, coefs.length - 1);
        if (obj == null) { // is there an enum variable ?
            IntDomainVar dummyObj = (IntDomainVar) makeConstantIntVar(-c);
            return new BoolIntLinComb(vs, coefs, dummyObj, 1, 0, linOperator);
        } else {
            int newLinOp = linOperator;
            if (objcoef < 0) {
                if (linOperator != IntLinComb.NEQ) {
                    objcoef = -objcoef;
                    c = -c;
                    UtilAlgo.reverse(coefs, vs);
                    UtilAlgo.inverseSign(coefs);
                }
                if (linOperator == IntLinComb.GEQ) {
                    newLinOp = IntLinComb.LEQ;
                } else if (linOperator == IntLinComb.LEQ) {
                    newLinOp = IntLinComb.GEQ;
                }
            }
            return new BoolIntLinComb(vs, coefs, obj, objcoef, c, newLinOp);
        }
    }


    protected Constraint createAC3BinConstraint(IntVar v0, IntVar v1, BinRelation relation) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new AC3BinConstraint((IntDomainVar) v0, (IntDomainVar) v1, relation);
        } else {
            return null;
        }
    }

    protected Constraint createAC4BinConstraint(IntVar v0, IntVar v1, BinRelation relation) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new AC4BinConstraint((IntDomainVar) v0, (IntDomainVar) v1, relation);
        } else {
            return null;
        }
    }

    protected Constraint createAC2001BinConstraint(IntVar v0, IntVar v1, BinRelation relation) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new AC2001BinConstraint((IntDomainVar) v0, (IntDomainVar) v1, relation);
        } else {
            return null;
        }
    }

	protected Constraint createAC3rmBinConstraint(IntVar v0, IntVar v1, BinRelation relation) {
        if ((v0 instanceof IntDomainVar) && (v1 instanceof IntDomainVar)) {
            return new AC3rmBinConstraint((IntDomainVar) v0, (IntDomainVar) v1, relation);
        } else {
            return null;
        }
    }

    protected Constraint createFCLargeConstraint(IntVar[] vars, LargeRelation relation) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new CspLargeConstraint(tmpVars, relation);
    }

    protected Constraint createGAC2001NegativeLargeConstraint(IntVar[] vars, LargeRelation relation) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new GAC2001LargeConstraint(tmpVars, relation);
    }

	protected Constraint createGAC3rmNegativeLargeConstraint(IntVar[] vars, LargeRelation relation) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new GAC3rmLargeConstraint(tmpVars, relation);
    }

    protected Constraint createGAC2001PositiveLargeConstraint(IntVar[] vars, IterLargeRelation relation) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new GAC2001PositiveLargeConstraint(tmpVars, relation);
    }

	protected Constraint createGAC3rmPositiveLargeConstraint(IntVar[] vars, IterIndexedLargeRelation relation) {
		IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
		System.arraycopy(vars, 0, tmpVars, 0, vars.length);
		return new GAC3rmPositiveLargeConstraint(tmpVars, relation);
	}

	protected Constraint createSubscript(IntVar index, int[] values, IntVar val, int offset) {
        if ((index instanceof IntDomainVar) && (val instanceof IntDomainVar)) {
            return new Element((IntDomainVar) index, values, (IntDomainVar) val, offset);
        } else {
            return null;
        }
    }

    protected Constraint createSubscript(IntVar index, IntVar index2, int[][] values, IntVar val) {
        if ((index2 instanceof IntDomainVar) && (index instanceof IntDomainVar) && (val instanceof IntDomainVar)) {
            return new Element2D((IntDomainVar) index, (IntDomainVar) index2, (IntDomainVar) val, values);
        } else {
            return null;
        }
    }

    protected Constraint createSubscript(IntVar index, IntVar[] varArray, IntVar val, int offset) {
        if ((index instanceof IntDomainVar) && (val instanceof IntDomainVar)) {
            if (((IntDomainVar) index).hasEnumeratedDomain()) {
                IntDomainVar[] allVars = new IntDomainVar[varArray.length + 2];
                for (int i = 0; i < varArray.length; i++) {
                    allVars[i] = (IntDomainVar) varArray[i];
                }
                allVars[varArray.length] = (IntDomainVar) index;
                allVars[varArray.length + 1] = (IntDomainVar) val;
                return new ElementV(allVars, offset);
            } else {
                throw new Error("BoundConsistency is not implemented on nth! " + index + " should be an Enumerated variable instead of a BoundIntVar");
            }
        } else {
            throw new Error("variables " + index + " and " + val + " should be IntDomainVar in nth");
        }
    }

    protected Constraint createBoolChanneling(IntVar boolv, IntVar intv, int j) {
        return new BooleanChanneling((IntDomainVar) boolv, (IntDomainVar) intv, j);
    }

    protected Constraint createInverseChanneling(IntVar[] x, IntVar[] y) {
        int n = x.length;
        if (y.length != n) {
            throw new Error("not a valid inverse channeling constraint with two arrays of different sizes");
        }
        IntDomainVar[] allVars = new IntDomainVar[2 * n];
        for (int i = 0; i < n; i++) {
            allVars[i] = (IntDomainVar) x[i];
        }
        for (int i = 0; i < n; i++) {
            allVars[n + i] = (IntDomainVar) y[i];
        }
        return new InverseChanneling(allVars, n);
    }

    protected Constraint createBinDisjunction(Constraint c0, Constraint c1) {
        return new BinaryDisjunction((AbstractConstraint) c0, (AbstractConstraint) c1);
    }

    protected Constraint createLargeDisjunction(Constraint[] alternatives) {
        Constraint cstr = new BinaryDisjunction((AbstractConstraint) alternatives[0], (AbstractConstraint) alternatives[1]);
        for (int i = 2; i < alternatives.length; i++){
            cstr = new BinaryDisjunction((AbstractConstraint)cstr, (AbstractConstraint)alternatives[i]);
        }
        return cstr;
    }

    protected Constraint createBinConjunction(Constraint c0, Constraint c1) {
        return new BinaryConjunction((AbstractConstraint) c0, (AbstractConstraint) c1);
    }

    protected Constraint createLargeConjunction(Constraint[] alternatives) {
        Constraint cstr = new BinaryConjunction((AbstractConstraint) alternatives[0], (AbstractConstraint) alternatives[1]);
        for (int i = 2; i < alternatives.length; i++){
            cstr = new BinaryConjunction((AbstractConstraint)cstr, (AbstractConstraint)alternatives[i]);
        }
        return cstr;
    }

    protected Constraint createNegation(Constraint c){
        if (c instanceof SetConstraint || c instanceof RealConstraint) {
	        throw new Error("Negation can not be used with Set or Real constraints : " + c);
        }
	    return new NegationConstraint((AbstractConstraint) c);
    }

    protected Constraint createGuard(Constraint c0, Constraint c1) {
        return new Guard((AbstractConstraint) c0, (AbstractConstraint) c1);
    }

    protected Constraint createEquiv(Constraint c0, Constraint c1) {
        return new Equiv((AbstractConstraint) c0, (AbstractConstraint) c1);
    }

    protected Constraint createCardinality(Constraint[] constList, IntVar cardVar, boolean constrainOnInf, boolean constrainOnSup) {
        if (cardVar instanceof IntDomainVar) {
            IntDomainVar v = (IntDomainVar) cardVar;
            return new Cardinality(constList, v, constrainOnInf, constrainOnSup);
        } else
            return null;
    }


    protected Constraint createMin(IntVar[] lvars, IntVar min) {
        if (lvars.length == 2) {
            return new MinXYZ((IntDomainVar) lvars[0],(IntDomainVar) lvars[1], (IntDomainVar) min);
        } else {
            IntDomainVar[] tmpVars = new IntDomainVar[lvars.length + 1];
            tmpVars[0] = (IntDomainVar) min;
            System.arraycopy(lvars, 0, tmpVars, 1, lvars.length);
            return new MinOfAList(tmpVars);
        }
    }

    protected Constraint createMax(IntVar[] lvars, IntVar max) {
        if (lvars.length == 2) {
            return new MaxXYZ((IntDomainVar) lvars[0],(IntDomainVar) lvars[1], (IntDomainVar) max);
        } else {
            IntDomainVar[] tmpVars = new IntDomainVar[lvars.length + 1];
            tmpVars[0] = (IntDomainVar) max;
            System.arraycopy(lvars, 0, tmpVars, 1, lvars.length);
            return new MaxOfAList(tmpVars);
        }
    }

    protected Constraint createOccurrence(IntVar[] vars, int occval, boolean onInf, boolean onSup) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new Occurrence(tmpVars, occval, onInf, onSup);
    }

    protected Constraint createAllDifferent(IntVar[] vars) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new AllDifferent(tmpVars);
    }

    protected Constraint createBoundAllDiff(IntVar[] vars, boolean global) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new BoundAllDiff(tmpVars, global);
    }

    protected Constraint createGlobalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new GlobalCardinality(tmpVars, min, max, low, up);
    }

	protected Constraint createBoundGlobalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new BoundGcc(tmpVars, min, max, low, up);
    }

	protected Constraint createBoundGlobalCardinalityVar(IntVar[] vars, int min, int max, IntVar[] card) {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
		IntDomainVar[] tmpCVars = new IntDomainVar[card.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        System.arraycopy(card, 0, tmpCVars, 0, card.length);
		return new BoundGccVar(tmpVars, tmpCVars, min, max);
    }


    protected Constraint createRegular(IntVar[] vars, DFA auto) {
      try {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new Regular(auto, tmpVars);
      } catch(NoClassDefFoundError e) {
        System.err.println("Warning!! To use DFA based constraints, you need automaton library" +
            " from http://www.brics.dk/automaton/");
        return null;
      }
    }

    protected Constraint createRegular(IntVar[] vars, List<int[]> feasibleTuples) {
      try {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new Regular(new DFA(feasibleTuples), tmpVars);
      } catch(NoClassDefFoundError e) {
        System.err.println("Warning!! To use DFA based constraints, you need automaton library" +
            " from http://www.brics.dk/automaton/");
        return null;
      }
    }

    protected Constraint createRegular(IntVar[] vars, List<int[]> infeasibleTuples, int[] min, int[] max) {
      try {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);
        return new Regular(new DFA(infeasibleTuples, min, max), tmpVars);
      } catch(NoClassDefFoundError e) {
        System.err.println("Warning!! To use DFA based constraints, you need automaton library" +
            " from http://www.brics.dk/automaton/");
        return null;
      }
    }

    /**
     * Build the generic DFA corresponding to stretch
     *
     * @param vars
     * @param stretchParameters
     */
    protected Constraint createStretch(IntVar[] vars, List<int[]> stretchParameters) {
      try {
        IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, tmpVars, 0, vars.length);

        ArrayList alphabet = new ArrayList();
        for (int i = 0; i < vars.length; i++) {
            for (IntIterator it = tmpVars[i].getDomain().getIterator(); it.hasNext();) {
                int val = it.next();
                if (!alphabet.contains(val)) {
                    alphabet.add(val);
                }
            }
        }

        int nbStates = 1;
        Hashtable tab = new Hashtable();
        List<Transition> t = new LinkedList<Transition>();
        List<Integer> fs = new LinkedList<Integer>();
        fs.add(0);

          for (int i = 0; i < stretchParameters.size(); i++) {
              int[] stretchParameter1 = stretchParameters.get(i);
              int[] vals = (int[]) stretchParameter1;
              int valState = nbStates++;
              tab.put(vals[0], valState);
              t.add(new Transition(0, vals[0], valState));
              if (vals[1] == 1) {
                  fs.add(valState);
              }
          }

          for (int i = 0; i < alphabet.size(); i++) {
              Object anAlphabet1 = alphabet.get(i);
              int val = (Integer) anAlphabet1;
              if (!tab.containsKey(val)) {
                  t.add(new Transition(0, val, 0));
              }
          }

          for (int i = 0; i < stretchParameters.size(); i++) {
              int[] stretchParameter = stretchParameters.get(i);
              int[] vals = (int[]) stretchParameter;
              int lastState = (Integer) tab.get(vals[0]);
              for (int j = 2; j <= vals[2]; j++) {
                  int newState = nbStates++;
                  t.add(new Transition(lastState, vals[0], newState));
                  if ((j > vals[1])) {
                      for (int i1 = 0; i1 < alphabet.size(); i1++) {
                          Object anAlphabet = alphabet.get(i1);
                          int val = (Integer) anAlphabet;
                          if ((vals[0] != val)) {
                              if (tab.containsKey(val)) {
                                  int dest = (Integer) tab.get(val);
                                  t.add(new Transition(lastState, val, dest));
                              } else {
                                  t.add(new Transition(lastState, val, 0));
                              }
                          }
                      }
                  }

                  if (j >= vals[1]) {
                      fs.add(newState);
                  }
                  lastState = newState;
              }

              for (int i1 = 0; i1 < alphabet.size(); i1++) {
                  Object anAlphabet = alphabet.get(i1);
                  int val = (Integer) anAlphabet;
                  if (vals[0] != val) {
                      if (tab.containsKey(val)) {
                          int dest = (Integer) tab.get(val);
                          t.add(new Transition(lastState, val, dest));
                      } else {
                          t.add(new Transition(lastState, val, 0));
                      }
                  }
              }
          }


        DFA auto = new DFA(t, fs, vars.length);

        return new Regular(auto, tmpVars);
      } catch(NoClassDefFoundError e) {
        System.err.println("Warning!! To use DFA based constraints, you need automaton library" +
            " from http://www.brics.dk/automaton/");
        return null;
      }
    }


    protected Constraint createCumulative(IntVar[] sts, IntVar[] ends, IntVar[] durations, int[] h, int Capa) {
        int n = sts.length;
        IntVar[] heigthVars = new IntDomainVar[n];
        for (int i = 0; i < n; i++) {
            heigthVars[i] = makeConstantIntVar(h[i]);
        }
        return createCumulative(sts, ends, durations, heigthVars, Capa);
    }

    protected Constraint createCumulative(IntVar[] sts, IntVar[] ends, IntVar[] durations, IntVar[] heigths, int Capa) {
        int n = sts.length;
        IntDomainVar[] startVars = new IntDomainVar[n];
        System.arraycopy(sts, 0, startVars, 0, n);
        IntDomainVar[] endsVars = new IntDomainVar[n];
        System.arraycopy(ends, 0, endsVars, 0, n);
        IntDomainVar[] durationsVars = new IntDomainVar[n];
        System.arraycopy(durations, 0, durationsVars, 0, n);
        IntDomainVar[] heigthVars = new IntDomainVar[n];
        System.arraycopy(heigths, 0, heigthVars, 0, n);
        return new Cumulative(startVars, endsVars, durationsVars, heigthVars, Capa);
    }

    protected Constraint createLex(IntVar[] v1, IntVar[] v2, boolean strict) {
        int n = v1.length;
        IntDomainVar[] vs = new IntDomainVar[2 * n];
        System.arraycopy(v1, 0, vs, 0, n);
        System.arraycopy(v2, 0, vs, n, n);
        return new Lex(vs, v1.length, strict);
    }

    protected Constraint createSorting(IntVar[] v1, IntVar[] v2) {
        int n = v1.length;
        IntDomainVar[] vs1 = new IntDomainVar[n];
        IntDomainVar[] vs2 = new IntDomainVar[n];
        System.arraycopy(v1, 0, vs1, 0, n);
        System.arraycopy(v2, 0, vs2, n, n);
        return new SortingConstraint(vs1, vs2);
    }

    protected Constraint createLeximin(IntVar[] v1, IntVar[] v2) {
        int n = v1.length;
        IntDomainVar[] vs1 = new IntDomainVar[n];
        IntDomainVar[] vs2 = new IntDomainVar[n];
        System.arraycopy(v1, 0, vs1, 0, n);
        System.arraycopy(v2, 0, vs2, n, n);
        return new LeximinConstraint(vs1, vs2);
    }

    protected Constraint createLeximin(int[] v1, IntVar[] v2) {
        int n = v1.length;
        IntDomainVar[] vs2 = new IntDomainVar[n];
        System.arraycopy(v2, 0, vs2, n, n);
        return new SemiLeximinConstraint(v1, vs2);
    }

    protected Constraint createAtMostNvalue(IntVar[] vars, IntVar nvalue) {
        IntDomainVar[] ivars = new IntDomainVar[vars.length];
        System.arraycopy(vars, 0, ivars, 0, vars.length);
        return new AtMostNValue(ivars, (IntDomainVar) nvalue); 
    }

    protected IntDomainVar createIntVar(String name, int domainType, int min, int max) {
        return new IntDomainVarImpl(this, name, domainType, min, max);
    }

    protected IntDomainVar createIntVar(String name, int[] sortedValues) {
        return new IntDomainVarImpl(this, name, sortedValues);
    }

    protected RealVar createRealVal(String name, double min, double max) {
        return new RealVarImpl(this, name, min, max);
    }

    protected RealIntervalConstant createRealIntervalConstant(double a, double b) {
        return new RealIntervalConstant(a, b);
    }

    protected RealExp createRealSin(RealExp exp) {
        return new RealSin(this, exp);
    }

    protected RealExp createRealCos(RealExp exp) {
        return new RealCos(this, exp);
    }

    protected RealExp createRealIntegerPower(RealExp exp, int power) {
        return new RealIntegerPower(this, exp, power);
    }

    protected RealExp createRealPlus(RealExp exp1, RealExp exp2) {
        return new RealPlus(this, exp1, exp2);
    }

    protected RealExp createRealMinus(RealExp exp1, RealExp exp2) {
        return new RealMinus(this, exp1, exp2);
    }

    protected RealExp createRealMult(RealExp exp1, RealExp exp2) {
        return new RealMult(this, exp1, exp2);
    }

    protected Constraint createEquation(RealVar[] tmpVars, RealExp exp, RealIntervalConstant cst) {
        return new Equation(this, tmpVars, exp, cst);
    }

    protected SetVar createSetVar(String name, int a, int b, boolean enumcard) {
        return new SetVarImpl(this, name, a, b, enumcard);
    }

    protected Constraint createMemberXY(SetVar sv1, IntVar var) {
        if (var instanceof IntDomainVar) {
            IntDomainVar v = (IntDomainVar) var;
            return new MemberXY(sv1, v);
        } else
            return null;
    }

    protected Constraint createNotMemberXY(SetVar sv1, IntVar var) {
        if (var instanceof IntDomainVar) {
            IntDomainVar v = (IntDomainVar) var;
            return new NotMemberXY(sv1, v);
        } else
            return null;
    }

    protected Constraint createMemberX(SetVar sv1, int val) {
        return new MemberX(sv1, val);
    }

    protected Constraint createNotMemberX(SetVar sv1, int val) {
        return new NotMemberX(sv1, val);
    }

    protected Constraint createDisjoint(SetVar sv1, SetVar sv2) {
        return new Disjoint(sv1, sv2);
    }

    protected Constraint createSetIntersection(SetVar sv1, SetVar sv2, SetVar inter) {
        return new SetIntersection(sv1, sv2, inter);
    }

    protected Constraint createSetUnion(SetVar sv1, SetVar sv2, SetVar union) {
        return new SetUnion(sv1, sv2, union);
    }

    protected Constraint createSetCard(SetVar sv, IntVar var, boolean b1, boolean b2) {
        if (var instanceof IntDomainVar) {
            IntDomainVar v = (IntDomainVar) var;
            return new SetCard(sv, v, b1, b2);
        } else
            return null;
    }


    public Boolean solve(boolean all) {
        solver.firstSolution = !all;
        solver.generateSearchSolver(this);
//    solver.getSearchSolver().incrementalRun();
        solver.launch();
        return isFeasible();
    }

    public Boolean solve() {
        solver.firstSolution = true;
        solver.generateSearchSolver(this);
        solver.launch();
        return isFeasible();
    }

    public Boolean solveAll() {
        solver.firstSolution = false;
        solver.generateSearchSolver(this);
        solver.launch();
        return isFeasible();
    }

    /**
     * <i>Resolution:</i>
     * Searches for the solution minimizing the objective criterion.
     *
     * @param obj     The variable modelling the optimization criterion
     * @param restart If true, then a new search is restarted from scratch
     *                after each solution is found;
     *                otherwise a single branch-and-bound search is performed
     */

    public Boolean minimize(Var obj, boolean restart) {
        return optimize(false, obj, restart);
    }

    protected Boolean optimize(boolean maximize, Var obj, boolean restart) {
        solver.setDoMaximize(maximize);
        solver.setObjective(obj);
        solver.setRestart(restart);
        solver.setFirstSolution(false);
        solver.generateSearchSolver(this);
        solver.launch();
        return this.isFeasible();
    }

    /**
     * <i>resolution:</i>
     * Searches for the solution maximizing the objective criterion.
     *
     * @param obj     The variable modelling the optimization criterion
     * @param restart If true, then a new search is restarted from scratch
     *                after each solution is found;
     *                otherwise a single branch-and-bound search is performed
     */
    public Boolean maximize(Var obj, boolean restart) {
        return optimize(true, obj, restart);
    }

}