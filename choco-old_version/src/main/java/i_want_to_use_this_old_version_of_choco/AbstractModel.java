// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.bool.ConstantConstraint;
import i_want_to_use_this_old_version_of_choco.global.regular.DFA;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntExp;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.Absolute;
import i_want_to_use_this_old_version_of_choco.integer.constraints.DistanceXYC;
import i_want_to_use_this_old_version_of_choco.integer.constraints.DistanceXYZ;
import i_want_to_use_this_old_version_of_choco.integer.constraints.IntLinComb;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.*;
import i_want_to_use_this_old_version_of_choco.integer.var.IntTerm;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.reified.gacreified.*;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

import java.util.*;
import java.util.logging.Logger;


public abstract class AbstractModel implements IntModeler, BoolModeler { // extends AbstractEntity

	/**
	 * Reference to an object for logging trace statements related to Abtract Model (using the java.util.logging package)
	 */

	private static Logger logger = Logger.getLogger("choco");


	/**
	 * A constant denoting the true constraint (always satisfied)
	 */
	public final Constraint TRUE = new ConstantConstraint(true);
	/**
	 * A constant denoting the false constraint (never satisfied)
	 */
	public final Constraint FALSE = new ConstantConstraint(false);
	/**
	 * A constant denoting a null integer term. This is useful to make the API
	 * more robust, for instance with linear expression with null coefficients.
	 */
	public final IntTerm ZERO = new IntTerm(0);
	/**
	 * All the constraints of the problem.
	 */
	protected PartiallyStoredVector constraints;

	/**
	 * All the search intVars in the problem.
	 */
	protected ArrayList intVars;
	/**
	 * All the set intVars in the problem.
	 */
	protected ArrayList setVars;
	/**
	 * All the float vars in the problem.
	 */
	protected ArrayList floatVars;
	/**
	 * The variable modelling the objective function
	 */
	protected IntVar objective;
	/**
	 * Maximization / Minimization problem
	 */
	protected boolean doMaximize;

	/**
	 * Tell the solver wether or not use recomputation
	 */
	protected boolean useRecomputation = false;

	/**
	 * Decide if redundant constraints are automatically
	 * to the model to reason on cardinalities on sets
	 * as well as kernel and enveloppe
	 */
	protected boolean cardinalityReasonningsOnSETS = true;

	public AbstractModel() {
		intVars = new ArrayList();
		setVars = new ArrayList();
		floatVars = new ArrayList();
	}

	public abstract void post(Constraint c);

	public abstract void postCut(Constraint c);

	public IntDomainVar makeIntVar(String name, int domainType, int min, int max) {
		IntDomainVar v = createIntVar(name, domainType, min, max);
		intVars.add(v);
		return v;
	}

	/**
	 * Creates a new search variable with an enumerated domain
	 *
	 * @param name the name of the variable
	 * @param min  minimal allowed value (included in the domain)
	 * @param max  maximal allowed value (included in the domain)
	 * @return the variable
	 */
	public IntDomainVar makeEnumIntVar(String name, int min, int max) {
		IntDomainVar v = createIntVar(name, IntDomainVar.BITSET, min, max);
		intVars.add(v);
		return v;
	}

	/**
	 * Creates a new search variable with an enumerated domain
	 *
	 * @param name   the name of the variable
	 * @param values allowed in the domain (may be unsorted, but not with duplicates !)
	 * @return the variable
	 */
	public IntDomainVar makeEnumIntVar(String name, int[] values) {
		int[] values2 = new int[values.length];
		System.arraycopy(values, 0, values2, 0, values.length);
		Arrays.sort(values2);
		IntDomainVar v = createIntVar(name, values2);
		intVars.add(v);
		return v;
	}

	/**
	 * Creates a new search variable with an interval domain
	 *
	 * @param name the name of the variable
	 * @param min  minimal allowed value (included in the domain)
	 * @param max  maximal allowed value (included in the domain)
	 * @return the variable
	 */
	public IntDomainVar makeBoundIntVar(String name, int min, int max) {
		IntDomainVar v = createIntVar(name, IntDomainVar.BOUNDS, min, max);
		intVars.add(v);
		return v;
	}

	public IntDomainVar makeBoundIntVar(String name, int min, int max, boolean toAdd) {
		IntDomainVar v = createIntVar(name, IntDomainVar.BOUNDS, min, max);
		if (toAdd) intVars.add(v);
		return v;
	}

	public final void setMinimizationObjective(IntVar obj) {
		objective = obj;
		doMaximize = false;
	}

	public final void setMaximizationObjective(IntVar obj) {
		objective = obj;
		doMaximize = true;
	}

	/**
	 * Creates a one dimensional array of integer variables
	 *
	 * @param name the name of the array (a prefix shared by all individual IntVars)
	 * @param dim  the number of entries
	 * @param min  the minimal domain value for all variables in the array
	 * @param max  the maximal domain value for all variables in the array
	 */
	public IntDomainVar[] makeBoundIntVarArray(String name, int dim, int min, int max) {
		IntDomainVar[] res = new IntDomainVar[dim];
		for (int i = 0; i < dim; i++) {
			res[i] = makeBoundIntVar(name + "[" + String.valueOf(i) + "]", min, max);
		}
		return res;
	}

	/**
	 * Creates a one dimensional array of integer variables
	 *
	 * @param name the name of the array (a prefix shared by all individual IntVars)
	 * @param dim1 the number of entries for the first index
	 * @param dim2 the number of entries for the second index
	 * @param min  the minimal domain value for all variables in the array
	 * @param max  the maximal domain value for all variables in the array
	 */
	public IntDomainVar[][] makeBoundIntVarArray(String name, int dim1, int dim2, int min, int max) {
		IntDomainVar[][] res = new IntDomainVar[dim1][dim2];
		for (int i = 0; i < dim1; i++) {
			for (int j = 0; j < dim2; j++) {
				res[i][j] = makeBoundIntVar(name + "[" + String.valueOf(i) + ", " + String.valueOf(j) + "]", min, max);
			}
		}
		return res;
	}

	/**
	 * Creates a one dimensional array of integer variables
	 *
	 * @param name the name of the array (a prefix shared by all individual IntVars)
	 * @param dim  the number of entries
	 * @param min  the minimal domain value for all variables in the array
	 * @param max  the maximal domain value for all variables in the array
	 * @return array of integer variables
	 */
	public IntDomainVar[] makeEnumIntVarArray(String name, int dim, int min, int max) {
		IntDomainVar[] res = new IntDomainVar[dim];
		for (int i = 0; i < dim; i++) {
			res[i] = makeEnumIntVar(name + "[" + String.valueOf(i) + "]", min, max);
		}
		return res;
	}

	/**
	 * Creates a one dimensional array of integer variables
	 *
	 * @param name the name of the array (a prefix shared by all individual IntVars)
	 * @param dim1 the number of entries for the first index
	 * @param dim2 the number of entries for the second index
	 * @param min  the minimal domain value for all variables in the array
	 * @param max  the maximal domain value for all variables in the array
	 * @return array of integer variables
	 */
	public IntDomainVar[][] makeEnumIntVarArray(String name, int dim1, int dim2, int min, int max) {
		IntDomainVar[][] res = new IntDomainVar[dim1][dim2];
		for (int i = 0; i < dim1; i++) {
			for (int j = 0; j < dim2; j++) {
				res[i][j] = makeEnumIntVar(name + "[" + String.valueOf(i) + ", " + String.valueOf(j) + "]", min, max);
			}
		}
		return res;
	}

	/**
	 * Creates a real variable.
	 *
	 * @param name of the real variable
	 * @param min  the lower bound of the domain
	 * @param max  the upper bound of the domain
	 * @return the new object (RealVar)
	 */
	public RealVar makeRealVar(String name, double min, double max) {
		RealVar v = createRealVal(name, min, max);
		floatVars.add(v);
		return v;
	}

	/**
	 * Creates an anonymous real variable.
	 *
	 * @param inf lower bound of the domain
	 * @param sup upper bound of the domain
	 * @return the anonumous real variable
	 */
	public RealVar makeRealVar(double inf, double sup) {
		return makeRealVar("", inf, sup);
	}

	/**
	 * Creates an unbounded real variable.
	 *
	 * @param name of the real variable
	 * @return an unbounded real variable
	 */
	public RealVar makeRealVar(String name) {
		return makeRealVar(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	/**
	 * Creates a set variable where the cardinality
	 * is represented by default as a bound variable
	 *
	 * @param name the name of the variable
	 * @param a    : the first value of the initial enveloppe
	 * @param b    : the last value of the initial enveloppe
	 * @return the new object (RealVar)
	 */
	public SetVar makeSetVar(String name, int a, int b) {
		SetVar v = createSetVar(name, a, b, false);
		setVars.add(v);
		post(createSetCard(v, v.getCard(), true, true)); //post |v| = v.getCard()
		return v;
	}

	/**
	 * Creates a set variable where the cardinality
	 * is represented by an enum variable
	 *
	 * @param name the name of the variable
	 * @param a    : the first value of the initial enveloppe
	 * @param b    : the last value of the initial enveloppe
	 * @return the new object (RealVar)
	 */
	public SetVar makeSetVarEnumCard(String name, int a, int b) {
		SetVar v = createSetVar(name, a, b, true);
		setVars.add(v);
        post(createSetCard(v, v.getCard(), true, true)); //post |v| = v.getCard()
        return v;
	}

	/**
	 * Creates a set variable where the cardinality
	 * is represented by an enum variable
	 *
	 * @param name the name of the variable
	 * @param a    : the first value of the initial enveloppe
	 * @param b    : the last value of the initial enveloppe
	 * @return the new object (RealVar)
	 */
	public SetVar makeSetVarBoundCard(String name, int a, int b) {
		SetVar v = createSetVar(name, a, b, false);
		setVars.add(v);
        post(createSetCard(v, v.getCard(), true, true)); //post |v| = v.getCard()
        return v;
	}


	// TODO: can be optimized (no need to attach propagation events)
	public IntVar makeConstantIntVar(String name, int val) {
		return makeBoundIntVar(name, val, val);
	}

	public IntVar makeConstantIntVar(int val) {
		return makeConstantIntVar("", val);
	}

	/**
	 * Creates a simple linear term from one coefficient and one variable
	 *
	 * @param a the coefficient
	 * @param x the variable
	 * @return the term
	 */
	public IntExp mult(int a, IntExp x) {
		if (a != 0 && x != ZERO) {
			IntTerm t = new IntTerm(1);
			t.setCoefficient(0, a);
			t.setVariable(0, (IntVar) x);
			return t;
		} else {
			return ZERO;
		}
	}

	/**
	 * Utility method for constructing a term from two lists of variables, list of coeffcicients and constants
	 *
	 * @param coeffs1 coefficients from the first term
	 * @param vars1   variables from the first term
	 * @param cste1   constant from the fisrt term
	 * @param coeffs2 coefficients from the second term
	 * @param vars2   variables from the second term
	 * @param cste2   constant from the second term
	 * @return the term (a fresh one)
	 */
	protected IntExp plus(int[] coeffs1, IntVar[] vars1, int cste1, int[] coeffs2, IntVar[] vars2, int cste2) {
		int n1 = vars1.length;
		int n2 = vars2.length;
		IntTerm t = new IntTerm(n1 + n2);
		for (int i = 0; i < n1; i++) {
			t.setVariable(i, vars1[i]);
			t.setCoefficient(i, coeffs1[i]);
		}
		for (int i = 0; i < n2; i++) {
			t.setVariable(n1 + i, vars2[i]);
			t.setCoefficient(n1 + i, coeffs2[i]);
		}
		t.setConstant(cste1 + cste2);
		return t;
	}

	/**
	 * Adding two terms one to another
	 *
	 * @param t1 first term
	 * @param t2 second term
	 * @return the term (a fresh one)
	 */
	public IntExp plus(IntExp t1, IntExp t2) {
		if (t1 == ZERO) return t2;
		if (t2 == ZERO) return t1;
		if (t1 instanceof IntTerm) {
			if (t2 instanceof IntTerm) {
				return plus(((IntTerm) t1).getCoefficients(),
						((IntTerm) t1).getVariables(),
						((IntTerm) t1).getConstant(),
						((IntTerm) t2).getCoefficients(),
						((IntTerm) t2).getVariables(),
						((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				return plus(((IntTerm) t1).getCoefficients(),
						((IntTerm) t1).getVariables(),
						((IntTerm) t1).getConstant(),
						new int[]{1},
						new IntVar[]{(IntVar) t2},
						0);
			} else {
				throw new Error("IntExp not a term, not a var");
			}
		} else if (t1 instanceof IntVar) {
			if (t2 instanceof IntTerm) {
				return plus(new int[]{1},
						new IntVar[]{(IntVar) t1},
						0,
						((IntTerm) t2).getCoefficients(),
						((IntTerm) t2).getVariables(),
						((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				IntTerm t = new IntTerm(2);
				t.setCoefficient(0, 1);
				t.setCoefficient(1, 1);
				t.setVariable(0, (IntVar) t1);
				t.setVariable(1, (IntVar) t2);
				t.setConstant(0);
				return t;
			} else
				throw new Error("IntExp not a term, not a var");
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public IntExp plus(IntExp t, int c) {
		if (t == ZERO) {
			IntTerm t2 = new IntTerm(0);
			t2.setConstant(c);
			return t2;
		} else if (t instanceof IntTerm) {
			IntTerm t2 = new IntTerm((IntTerm) t);
			t2.setConstant(((IntTerm) t).getConstant() + c);
			return t2;
		} else if (t instanceof IntVar) {
			IntTerm t2 = new IntTerm(1);
			t2.setCoefficient(0, 1);
			t2.setVariable(0, (IntVar) t);
			t2.setConstant(c);
			return t2;
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public final IntExp plus(int c, IntExp t1) {
		return plus(t1, c);
	}

	/**
	 * Subtracting two terms one from another
	 *
	 * @param t1 first term
	 * @param t2 second term
	 * @return the term (a fresh one)
	 */
	public IntExp minus(IntExp t1, IntExp t2) {
		if (t1 == ZERO) return mult(-1, t2);
		if (t2 == ZERO) return t1;
		if (t1 instanceof IntTerm) {
			if (t2 instanceof IntTerm) {
				int[] coeffs2 = ((IntTerm) t2).getCoefficients();
				int n2 = coeffs2.length;
				int[] oppcoeffs2 = new int[n2];
				for (int i = 0; i < n2; i++) {
					oppcoeffs2[i] = -(coeffs2[i]);
				}
				return plus(((IntTerm) t1).getCoefficients(),
						((IntTerm) t1).getVariables(),
						((IntTerm) t1).getConstant(),
						oppcoeffs2,
						((IntTerm) t2).getVariables(),
						-((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				return plus(((IntTerm) t1).getCoefficients(),
						((IntTerm) t1).getVariables(),
						((IntTerm) t1).getConstant(),
						new int[]{-1},
						new IntVar[]{(IntVar) t2},
						0);
			} else {
				throw new Error("IntExp not a term, not a var");
			}
		} else if (t1 instanceof IntVar) {
			if (t2 instanceof IntTerm) {
				int[] coeffs2 = ((IntTerm) t2).getCoefficients();
				int n2 = coeffs2.length;
				int[] oppcoeffs2 = new int[n2];
				for (int i = 0; i < n2; i++) {
					oppcoeffs2[i] = -(coeffs2[i]);
				}
				return plus(new int[]{1},
						new IntVar[]{(IntVar) t1},
						0,
						oppcoeffs2,
						((IntTerm) t2).getVariables(),
						-((IntTerm) t2).getConstant());
			} else if (t2 instanceof IntVar) {
				IntTerm t = new IntTerm(2);
				t.setCoefficient(0, 1);
				t.setCoefficient(1, -1);
				t.setVariable(0, (IntVar) t1);
				t.setVariable(1, (IntVar) t2);
				t.setConstant(0);
				return t;
			} else
				throw new Error("IntExp not a term, not a var");
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public IntExp minus(IntExp t, int c) {
		if (t == ZERO) {
			IntTerm t2 = new IntTerm(0);
			t2.setConstant(-c);
			return t2;
		} else if (t instanceof IntTerm) {
			IntTerm t2 = new IntTerm((IntTerm) t);
			t2.setConstant(((IntTerm) t).getConstant() - c);
			return t2;
		} else if (t instanceof IntVar) {
			IntTerm t2 = new IntTerm(1);
			t2.setCoefficient(0, 1);
			t2.setVariable(0, (IntVar) t);
			t2.setConstant(-c);
			return t2;
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public IntExp minus(int c, IntExp t) {
		if (t instanceof IntTerm) {
			IntTerm t1 = (IntTerm) t;
			int n = t1.getSize();
			IntTerm t2 = new IntTerm(n);
			for (int i = 0; i < n; i++) {
				t2.setCoefficient(i, -t1.getCoefficient(i));
				t2.setVariable(i, t1.getVariable(i));
			}
			t2.setConstant(c - t1.getConstant());
			return t2;
		} else if (t instanceof IntVar) {
			IntTerm t2 = new IntTerm(1);
			t2.setCoefficient(0, -1);
			t2.setVariable(0, (IntVar) t);
			t2.setConstant(c);
			return t2;
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	/**
	 * Building a term from a scalar product of coefficients and variables
	 *
	 * @param lc the array of coefficients
	 * @param lv the array of variables
	 * @return the term
	 */
	public IntExp scalar(int[] lc, IntVar[] lv) {
		int n = lc.length;
		assert (lv.length == n);
		IntTerm t = new IntTerm(n);
		for (int i = 0; i < n; i++) {
			t.setCoefficient(i, lc[i]);
			if (lv[i] instanceof IntVar) {
				t.setVariable(i, lv[i]);
			} else {
				throw new Error("unknown kind of IntDomainVar");
			}
		}
		return t;
	}

	/**
	 * Building a term from a scalar product of coefficients and variables
	 *
	 * @param lv the array of variables
	 * @param lc the array of coefficients
	 * @return the term
	 */
	public final IntExp scalar(IntVar[] lv, int[] lc) {
		return scalar(lc, lv);
	}

	/**
	 * Building a term from a sum of integer expressions
	 *
	 * @param lv the array of integer expressions
	 * @return the term
	 */
	public IntExp sum(IntExp[] lv) {
		int n = lv.length;
		IntTerm t = new IntTerm(n);
		for (int i = 0; i < n; i++) {
			t.setCoefficient(i, 1);
			if (lv[i] instanceof IntVar) {
				t.setVariable(i, (IntVar) lv[i]);
			} else {
				throw new Error("unexpected kind of IntExp");
			}
		}
		return t;
	}

	/**
	 * Creates a constraint by stating that a term is not equal than a constant
	 *
	 * @param x the expression
	 * @param c the constant
	 * @return the linear disequality constraint
	 */
	public Constraint neq(IntExp x, int c) {
		if (x instanceof IntTerm) {
			IntTerm t = (IntTerm) x;
			if ((t.getSize() == 2) && (t.getCoefficient(0) + t.getCoefficient(1) == 0)) {
				return createNotEqualXYC(t.getVariable(0), t.getVariable(1), (c - t.getConstant()) / t.getCoefficient(0));
			} else {
				return makeIntLinComb(((IntTerm) x).getVariables(), ((IntTerm) x).getCoefficients(), -(c), IntLinComb.NEQ);
			}
		} else if (x instanceof IntVar) {
			return createNotEqualXC((IntVar) x, c);
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public final Constraint neq(int c, IntExp x) {
		return neq(x, c);
	}

	public Constraint neq(IntExp x, IntExp y) {
		if (x instanceof IntTerm) {
			return neq(minus(x, y), 0);
		} else if (x instanceof IntVar) {
			if (y instanceof IntTerm) {
				return neq(minus(x, y), 0);
			} else if (y instanceof IntVar) {
				return createNotEqualXYC((IntVar) x, (IntVar) y, 0);
			} else if (y == null) {
				return neq(x, 0);
			} else {
				throw new Error("IntExp not a term, not a var");
			}
		} else if (x == null) {
			return neq(0, y);
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public Constraint geq(IntExp x, IntExp y) {
		if (x instanceof IntVar && y instanceof IntVar) {
			return createGreaterOrEqualXYC((IntVar) x, (IntVar) y, 0);
		} else if ((x instanceof IntTerm || x instanceof IntVar) && (y instanceof IntTerm || y instanceof IntVar)) {
			return geq(this.minus(x, y), 0);
		} else if (y == null) {
			return geq(x, 0);
		} else if (x == null) {
			return leq(x, 0);
		} else {
			throw new Error("IntExp not a good exp");
		}
	}

	// rewriting utility: remove all null coefficients
	// TODO: could be improved to remove duplicates (variables that would appear twice in the linear combination)
	public static int countNonNullCoeffs(int[] lcoeffs) {
		int nbNonNull = 0;
		for (int lcoeff : lcoeffs) {
			if (lcoeff != 0)
				nbNonNull++;
		}
		return nbNonNull;
	}

	protected Constraint makeIntLinComb(IntVar[] lvars, int[] lcoeffs, int c, int linOperator) {
		int nbNonNullCoeffs = countNonNullCoeffs(lcoeffs);
		int nbPositiveCoeffs;
		int[] sortedCoeffs = new int[nbNonNullCoeffs];
		IntVar[] sortedVars = new IntVar[nbNonNullCoeffs];

		int j = 0;
		// fill it up with the coefficients and variables in the right order
		for (int i = 0; i < lvars.length; i++) {
			if (lcoeffs[i] > 0) {
				sortedVars[j] = lvars[i];
				sortedCoeffs[j] = lcoeffs[i];
				j++;
			}
		}
		nbPositiveCoeffs = j;

		for (int i = 0; i < lvars.length; i++) {
			if (lcoeffs[i] < 0) {
				sortedVars[j] = lvars[i];
				sortedCoeffs[j] = lcoeffs[i];
				j++;
			}
		}
		if (nbNonNullCoeffs == 0) { //All coefficients of the linear combination are null !
			if (linOperator == IntLinComb.EQ && c == 0) return TRUE;
			else if (linOperator == IntLinComb.GEQ && 0 <= c) return TRUE;
			else if (linOperator == IntLinComb.LEQ && 0 >= c) return TRUE;
			else return FALSE;
		}
		return createIntLinComb(sortedVars, sortedCoeffs, nbPositiveCoeffs, c, linOperator);
	}

	/**
	 * Creates a constraint by stating that a term is greater or equal than a constant
	 *
	 * @param x the expression
	 * @param c the constant
	 * @return the linear inequality constraint
	 */
	public Constraint geq(IntExp x, int c) {
		if (x instanceof IntTerm) {
			IntTerm t = (IntTerm) x;
			if ((t.getSize() == 2) && (t.getCoefficient(0) + t.getCoefficient(1) == 0)) {
				if (t.getCoefficient(0) > 0) {
					return createGreaterOrEqualXYC(t.getVariable(0), t.getVariable(1), (c - t.getConstant()) / t.getCoefficient(0));
				} else {
					return createGreaterOrEqualXYC(t.getVariable(1), t.getVariable(0), (c - t.getConstant()) / t.getCoefficient(1));
				}
			} else {
				return makeIntLinComb(((IntTerm) x).getVariables(), ((IntTerm) x).getCoefficients(), ((IntTerm) x).getConstant() - c, IntLinComb.GEQ);
			}
		} else if (x instanceof IntVar) {
			return createGreaterOrEqualXC((IntVar) x, c);
		} else if (x == null) {
			if (c <= 0) {
				return TRUE;
			} else {
				return FALSE;
			}
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public Constraint geq(int c, IntExp x) {
		if (x instanceof IntTerm) {
			int[] coeffs = ((IntTerm) x).getCoefficients();
			int n = coeffs.length;
			int[] oppcoeffs = new int[n];
			for (int i = 0; i < n; i++) {
				oppcoeffs[i] = -(coeffs[i]);
			}
			return makeIntLinComb(((IntTerm) x).getVariables(), oppcoeffs, c - ((IntTerm) x).getConstant(), IntLinComb.GEQ);
		} else if (x instanceof IntVar) {
			return createLessOrEqualXC((IntVar) x, c);
		} else if (x == null) {
			if (c <= 0) {
				return TRUE;
			} else {
				return FALSE;
			}
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public final Constraint gt(IntExp x, IntExp y) {
		return geq(minus(x, y), 1);
	}

	public final Constraint gt(IntExp x, int c) {
		return geq(x, c + 1);
	}

	public final Constraint gt(int c, IntExp x) {
		return geq(c - 1, x);
	}

	public Constraint eq(IntExp x, IntExp y) {
		if (x instanceof IntVar && y instanceof IntVar) {
			return createEqualXYC((IntVar) x, (IntVar) y, 0);
		} else if ((x instanceof IntTerm || x instanceof IntVar) && (y instanceof IntTerm || y instanceof IntVar)) {
			return eq(this.minus(x, y), 0);
		} else if (x == null) {
			return eq(0, y);
		} else if (y == null) {
			return eq(x, 0);
		} else {
			throw new Error("IntExp not a good exp");
		}
	}

	public Constraint eq(IntExp x, int c) {
		if (x instanceof IntTerm) {
			IntTerm t = (IntTerm) x;
			int nbvars = t.getSize();
			int c2 = c - t.getConstant();
			if (t.getSize() == 1) {
				if (c2 % t.getCoefficient(0) == 0)
					return createEqualXC(t.getVariable(0), c2 / t.getCoefficient(0));
				else
					return FALSE;
			} else if ((nbvars == 2) && (t.getCoefficient(0) + t.getCoefficient(1) == 0)) {
				return createEqualXYC(t.getVariable(0), t.getVariable(1), c2 / t.getCoefficient(0));
			} else {
				return makeIntLinComb(t.getVariables(), t.getCoefficients(), -(c2), IntLinComb.EQ);
			}
		} else if (x instanceof IntVar) {
			return createEqualXC((IntVar) x, c);
		} else {
			throw new Error("IntExp not a term, not a var");
		}
	}

	public final Constraint eq(int c, IntExp x) {
		return eq(x, c);
	}

	public final Constraint leq(IntExp x, int c) {
		return geq(c, x);
	}

	public final Constraint leq(int c, IntExp x) {
		return geq(x, c);
	}

	public final Constraint leq(IntExp x, IntExp y) {
		return geq(y, x);
	}

	public final Constraint lt(IntExp x, int c) {
		return gt(c, x);
	}

	public final Constraint lt(int c, IntExp x) {
		return gt(x, c);
	}

	public final Constraint lt(IntExp x, IntExp y) {
		return gt(y, x);
	}

	/**
	 * Enforce z = x * y
	 *
	 * @param x the first integer variable
	 * @param y the second integer variable
	 * @param z the result of x*y
	 * @return the times constraint
	 */
	public Constraint times(IntVar x, IntVar y, IntVar z) {
		return createTimesXYZ(x, y, z);
	}

	// ------------------------ constraints over reals
	/**
	 * Makes an equation from an expression and a constantt interval. It is used by all methods building
	 * constraints. This is  useful for subclassing this modeller  for another kind of problem (like PaLM).
	 *
	 * @param exp The expression
	 * @param cst The interval this expression should be in
	 * @return the equation constraint
	 */
	public Constraint makeEquation(RealExp exp, RealIntervalConstant cst) {
		// Collect the variables
		Set collectedVars = new HashSet();
		exp.collectVars(collectedVars);
		RealVar[] tmpVars = new RealVar[0];
		tmpVars = (RealVar[]) collectedVars.toArray(tmpVars);
		return createEquation(tmpVars, exp, cst);
	}

	/**
	 * Eqality constraint.
	 *
	 * @param exp1 the fisrt expression
	 * @param exp2 the second expression
	 * @return the constraint enforcing exp1=exp2
	 */
	public Constraint eq(RealExp exp1, RealExp exp2) {
		if (exp1 instanceof RealIntervalConstant) {
			return makeEquation(exp2, (RealIntervalConstant) exp1);
		} else if (exp2 instanceof RealIntervalConstant) {
			return makeEquation(exp1, (RealIntervalConstant) exp2);
		} else {
			return makeEquation(minus(exp1, exp2), cst(0.0));
		}
	}

	public Constraint eq(RealExp exp, double cst) {
		return makeEquation(exp, cst(cst));
	}

	public Constraint eq(double cst, RealExp exp) {
		return makeEquation(exp, cst(cst));
	}

	/**
	 * Inferority constraint.
	 *
	 * @param exp1 the fisrt expression
	 * @param exp2 the second expression
	 * @return the constraint enforcing exp1<=exp2
	 */
	public Constraint leq(RealExp exp1, RealExp exp2) {
		if (exp1 instanceof RealIntervalConstant) {
			return makeEquation(exp2, cst(exp1.getInf(), Double.POSITIVE_INFINITY));
		} else if (exp2 instanceof RealIntervalConstant) {
			return makeEquation(exp1, cst(Double.NEGATIVE_INFINITY, exp2.getSup()));
		} else {
			return makeEquation(minus(exp1, exp2), cst(Double.NEGATIVE_INFINITY, 0.0));
		}
	}

	public Constraint leq(RealExp exp, double cst) {
		return makeEquation(exp, cst(Double.NEGATIVE_INFINITY, cst));
	}

	public Constraint leq(double cst, RealExp exp) {
		return makeEquation(exp, cst(cst, Double.POSITIVE_INFINITY));
	}

	/**
	 * Superiority constraint.
	 *
	 * @param exp1 the fisrt expression
	 * @param exp2 the second expression
	 * @return the constraint enforcing exp1>=exp2
	 */
	public Constraint geq(RealExp exp1, RealExp exp2) {
		return leq(exp2, exp1);
	}

	public Constraint geq(RealExp exp, double cst) {
		return leq(cst, exp);
	}

	public Constraint geq(double cst, RealExp exp) {
		return leq(exp, cst);
	}

	/**
	 * Addition of two expressions.
	 *
	 * @param exp1 the first expression
	 * @param exp2 the second expression
	 * @return the sum of exp1 and exp2  (exp1+exp2)
	 */
	public RealExp plus(RealExp exp1, RealExp exp2) {
		return createRealPlus(exp1, exp2);
	}

	/**
	 * Substraction of two expressions.
	 *
	 * @param exp1 the first expression
	 * @param exp2 the second expression
	 * @return the difference of exp1 and exp2 (exp1-exp2)
	 */
	public RealExp minus(RealExp exp1, RealExp exp2) {
		return createRealMinus(exp1, exp2);
	}

	/**
	 * Multiplication of two expressions.
	 *
	 * @param exp1 the first expression
	 * @param exp2 the second expression
	 * @return the product of exp1 and exp2 (exp1*exp2)
	 */
	public RealExp mult(RealExp exp1, RealExp exp2) {
		return createRealMult(exp1, exp2);
	}

	/**
	 * Power of an expression.
	 *
	 * @param exp   the expression to x
	 * @param power the second expression
	 * @return the difference of exp1 and exp2 (exp1-exp2)
	 */
	public RealExp power(RealExp exp, int power) {
		return createRealIntegerPower(exp, power);
	}

	/**
	 * Cosinus of an expression.
	 */
	public RealExp cos(RealExp exp) {
		return createRealCos(exp);
	}

	/**
	 * Sinus of an expression.
	 */
	public RealExp sin(RealExp exp) {
		return createRealSin(exp);
	}

	/**
	 * Arounds a double d to <code>[d - epsilon, d + epilon]</code>.
	 */
	public RealIntervalConstant around(double d) {
		return cst(RealMath.prevFloat(d), RealMath.nextFloat(d));
	}

	/**
	 * Makes a constant interval from a double d ([d,d]).
	 */
	public RealIntervalConstant cst(double d) {
		return createRealIntervalConstant(d, d);
	}

	/**
	 * Makes a constant interval between two doubles [a,b].
	 */
	public RealIntervalConstant cst(double a, double b) {
		return createRealIntervalConstant(a, b);
	}

	// ------------------------ Boolean connectors
	/*public Constraint makeDisjunction(Constraint[] alternatives) {
		if (alternatives.length == 0) {
			throw new UnsupportedOperationException();
		} else if (alternatives.length == 1) {
			return alternatives[0];
		} else if (alternatives.length == 2) {
			return createBinDisjunction(alternatives[0], alternatives[1]);
		} else {
			return createLargeDisjunction(alternatives);
		}
	}*/

	public Constraint makeDisjunction(Constraint[] branches) {
		if (branches.length == 0) {
			throw new UnsupportedOperationException();
		} else if (branches.length == 1) {
			return new OrLeaves((Propagator) branches[0]);
		} else if (branches.length == 2) {
			if (allLeaves(branches))
				return new OrLeaves((Propagator) branches[0], (Propagator) branches[1]);
			else return new OrPredicat(convert(branches, true));
		} else {
			if (allLeaves(branches)) {
				Propagator[] allprops = new Propagator[branches.length];
				System.arraycopy(branches, 0, allprops, 0, branches.length);
				return new OrLeaves(allprops);
			} else return new OrPredicat(convert(branches, true));
		}
	}

	public final Constraint or(Constraint[] constList) {
		return makeDisjunction(constList);
	}

	public final Constraint or(Constraint c0, Constraint c1, Constraint c2) {
		return makeDisjunction(new Constraint[]{c0, c1, c2});
	}

	public final Constraint or(Constraint c0, Constraint c1, Constraint c2, Constraint c3) {
		return makeDisjunction(new Constraint[]{c0, c1, c2, c3});
	}

	public final Constraint or(Constraint c0, Constraint c1) {
		Constraint[] alternatives;
		alternatives = new Constraint[2];
		alternatives[0] = c0;
		alternatives[1] = c1;
		return makeDisjunction(alternatives);
	}

	public Constraint makeConjunction(Constraint[] branches) {
		if (branches.length == 0) {
			throw new UnsupportedOperationException();
		} else if (branches.length == 1) {
			return new AndLeaves((Propagator) branches[0]);
		} else if (branches.length == 2) {
			if (allLeaves(branches))
				return new AndLeaves((Propagator) branches[0], (Propagator) branches[1]);
			else return new AndPredicat(convert(branches, false));
		} else {
			if (allLeaves(branches)) {
				Propagator[] allprops = new Propagator[branches.length];
				System.arraycopy(branches, 0, allprops, 0, branches.length);
				return new AndLeaves(allprops);
			} else return new AndPredicat(convert(branches, false));
		}
	}

	public boolean allLeaves(Constraint[] leaves) {
		for (int i = 0; i < leaves.length; i++) {
			if (leaves[i] instanceof Predicat)
				return false;
		}
		return true;
	}

	/**
	 * Create a unique leaf for all Constraint of cts that are
	 * Propagators and gather all the Predicat ones.
	 *
	 * @param cts
	 * @param or
	 * @return
	 */
	public Predicat[] convert(Constraint[] cts, boolean or) {
		int nbprop = 0;
		for (int i = 0; i < cts.length; i++) {
			if (cts[i] instanceof Propagator) nbprop++;
		}
		int add = (nbprop != 0) ? 1 : 0;
		Predicat[] preds = new Predicat[cts.length - nbprop + add];
		Propagator[] leaf = new Propagator[nbprop];
		int cptPred = 0;
		int cptProp = 0;
		for (int i = 0; i < cts.length; i++) {
			if (cts[i] instanceof Propagator) {
				leaf[cptProp] = (Propagator) cts[i];
				cptProp++;
			} else if (cts[i] instanceof Predicat) {
				preds[cptPred] = (Predicat) cts[i];
				cptPred++;
			} else throw new Error("what is " + cts[i] + "? (should be a Predicat or a Propagator)");
		}
		if (nbprop != 0) {
			if (or) preds[cts.length - nbprop] = new OrLeaves(leaf);
			else preds[cts.length - nbprop] = new AndLeaves(leaf);
		}
		return preds;
	}

	/*public Constraint makeConjunction(Constraint[] branches) {
		if (branches.length == 0) {
			throw new UnsupportedOperationException();
		} else if (branches.length == 1) {
			return branches[0];
		} else if (branches.length == 2) {
			return createBinConjunction(branches[0], branches[1]);
		} else {
			return createLargeConjunction(branches);
		}
	}*/

	public final Constraint and(Constraint[] constList) {
		return makeConjunction(constList);
	}

	public final Constraint and(Constraint c0, Constraint c1, Constraint c2) {
		return makeConjunction(new Constraint[]{c0, c1, c2});
	}

	public final Constraint and(Constraint c0, Constraint c1, Constraint c2, Constraint c3) {
		return makeConjunction(new Constraint[]{c0, c1, c2, c3});
	}

	public final Constraint and(Constraint c0, Constraint c1) {
		Constraint[] branches;
		branches = new Constraint[2];
		branches[0] = c0;
		branches[1] = c1;
		return makeConjunction(branches);
	}

	public final Constraint implies(Constraint c1, Constraint c2) {
		return or(not(c1), c2);
	}

	public Constraint ifThen(Constraint c1, Constraint c2) {
		return or(not(c1), c2);
	}

	public Constraint ifOnlyIf(Constraint c1, Constraint c2) {
		return and(or(not(c1), c2), or(c1, not(c2)));
	}


	public final Constraint not(Constraint c) {
		if (c instanceof Propagator)
			return createNegation(c);
		else return ((Predicat) c).getOpposite();
	}

	public Constraint atleast(Constraint[] constList, int nbTrueConstraints) {
		IntVar cardVar = makeConstantIntVar(nbTrueConstraints);
		return createCardinality(constList, cardVar, true, false);
	}

	public Constraint atmost(Constraint[] constList, int nbTrueConstraints) {
		IntVar cardVar = makeConstantIntVar(nbTrueConstraints);
		return createCardinality(constList, cardVar, false, true);
	}

	public Constraint card(Constraint[] constList, IntVar nbTrueConstraints) {
		return createCardinality(constList, nbTrueConstraints, true, true);
	}

	/**
	 * Create a binary relation that represent the list of compatible or
	 * incompatible pairs of values (depending on feas) given in argument tp
	 * be stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument.
	 * So such that : min[0] <= x.getInf(), max[0] >= x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >= y.getSup()
	 * for any pairs of variable x,y where an ac algorithm will be used with this relation.
	 * This is mandatory in the api to be able to compute the opposite of the relation if needed so the min[i]/max[i] can be smaller/bigger than min_{j \in pairs} pairs.get(j)[i] or max_{j \in pairs} pairs.get(j)[i]
	 * @param min
	 * @param max
	 * @param mat the list of tuples defined as int[] of size 2
	 * @param feas specify if the relation is defined in feasibility or not i.e. if the tuples corresponds to feasible or infeasible tuples
	 * @return
	 */
	public BinRelation makeBinRelation(int[] min, int[] max, List<int[]> mat, boolean feas) {
		int n1 = max[0] - min[0] + 1;
		int n2 = max[1] - min[1] + 1;
		CouplesTable relation = new CouplesTable(feas, min[0], min[1], n1, n2);
		for (int[] couple : mat) {
			if (couple.length != 2) throw new Error("Wrong dimension : " + couple.length + " for a couple");
			relation.setCouple(couple[0], couple[1]);
		}
		return relation;
	}

	/**
	 * Create a binary relation from the given matrix of consistency
	 * @param v1
	 * @param v2
	 * @param mat the consistency matrix
	 * @param feas specify if the relation is defined in feasibility or not
	 * @return
	 */
	protected BinRelation makeBinRelation(IntVar v1, IntVar v2, boolean[][] mat, boolean feas) {
		IntDomainVar x = (IntDomainVar) v1;
		IntDomainVar y = (IntDomainVar) v2;
		int n1 = x.getSup() - x.getInf() + 1;
		int n2 = y.getSup() - y.getInf() + 1;
		if (n1 == mat.length && n2 == mat[0].length) {
			CouplesTable relation = new CouplesTable(feas, ((IntDomainVar) v1).getInf(), ((IntDomainVar) v2).getInf(), n1, n2);
			for (int i = 0; i < n1; i++) {
				for (int j = 0; j < n2; j++) {
					if (mat[i][j])
						relation.setCoupleWithoutOffset(i, j);
				}
			}
			return relation;
		} else
			throw new Error("Wrong dimension for the matrix of consistency : "
					+ mat.length + " X " + mat[0].length + " instead of " + n1 + "X" + n2);
	}


	public Constraint makePairAC(IntVar v1, IntVar v2, List<int[]> mat, boolean feas, int ac) {
		int[] min = new int[]{((IntDomainVar) v1).getInf(),((IntDomainVar) v2).getInf()};
		int[] max = new int[]{((IntDomainVar) v1).getSup(),((IntDomainVar) v2).getSup()};
		BinRelation relation = makeBinRelation(min, max, mat, feas);
		return relationPairAC(v1,v2,relation);
	}

	public Constraint makePairAC(IntVar v1, IntVar v2, boolean[][] mat, boolean feas, int ac) {
		BinRelation relation = makeBinRelation(v1, v2, mat, feas);
		return relationPairAC(v1,v2,relation);
	}

	public Constraint relationPairAC(IntVar v1, IntVar v2, BinRelation binR, int ac) {
		if (ac == 3)
			return createAC3BinConstraint(v1, v2, binR);
		else if (ac == 4)
			return createAC4BinConstraint(v1, v2, binR);
		else if (ac == 2001)
			return createAC2001BinConstraint(v1, v2, binR);
		else if (ac == 32)
			return createAC3rmBinConstraint(v1, v2, binR);
		else {
			throw new Error("Ac " + ac + " algorithm not yet implemented");
		}
	}

	public Constraint makeTupleFC(IntVar[] vs, List tuples, boolean feas) {
		int n = vs.length;
		int[] offsets = new int[n];
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++) {
			IntDomainVar vi = (IntDomainVar) vs[i];
			sizes[i] = vi.getSup() - vi.getInf() + 1; //vi.getDomainSize();
			offsets[i] = vi.getInf();
		}
		TuplesTable relation = new TuplesTable(feas, offsets, sizes);
		Iterator it = tuples.iterator();
		while (it.hasNext()) {
			int[] tuple = (int[]) it.next();
			if (tuple.length != n)
				throw new Error("Wrong dimension : " + tuple.length + " for a tuple (should be " + n + ")");
			relation.setTuple(tuple);
		}
		return createFCLargeConstraint(vs, relation);
	}

	/**
	 * @deprecated use makeLargeRelation instead
	 */
	public LargeRelation makeRelation(IntVar[] vs, List<int[]> tuples, boolean feas) {
		int[] min = new int[vs.length];
		int[] max = new int[vs.length];
		for (int i = 0; i < vs.length; i++) {
			min[i] = ((IntDomainVar) vs[i]).getInf();
			max[i] = ((IntDomainVar) vs[i]).getSup();
		}
		return makeLargeRelation(min, max, tuples, feas);
	}


	/**
	 * Create a nary relationship that can be used to state a GAC constraint using
	 * after the api relationTupleAC(relation).
	 * Typically GAC algorithms uses two main schemes to seek the next support :
	 * - either by looking in the domain of the variable (here put feas = false to get such a relation)
	 * - or in the table itself in which case one need to be able to iterate over the tuples and not only check consistency (here put feas = true to get such a relation)
	 *
	 * @param min : min[i] has to be greater or equal the minimum value of any i-th variable on which this relation will be used
	 * @param max : max[i] has to be greater or equal the maximum value of any i-th variable on which this relation will be used
	 * @param tuples
	 * @param feas   specifies if you want an Iterable relation or not
	 * @return an nary relation.
	 */
	public LargeRelation makeLargeRelation(int[] min, int[] max, List<int[]> tuples, boolean feas) {
		int n = min.length;
		int[] offsets = new int[n];
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++) {
			sizes[i] = max[i] - min[i] + 1;
			offsets[i] = min[i];
		}
		LargeRelation relation;
		if (feas) {
			relation = new IterIndexedTuplesTable(tuples, offsets, sizes);
		} else {
			relation = new TuplesTable(feas, offsets, sizes);
			Iterator it = tuples.iterator();
			while (it.hasNext()) {
				int[] tuple = (int[]) it.next();
				if (tuple.length != n)
					throw new Error("Wrong dimension : " + tuple.length + " for a tuple (should be " + n + ")");
				((TuplesTable) relation).setTuple(tuple);
			}
		}


		return relation;
	}

	/**
	 * Create a constraint to enforce GAC on a list of feasible or infeasible tuples
	 * @param vs
	 * @param tuples the list of tuples
	 * @param feas specify if the tuples are feasible or infeasible tuples
	 * @return
	 */
	public Constraint makeTupleAC(IntVar[] vs, List<int[]> tuples, boolean feas) {
		int[] min = new int[vs.length];
		int[] max = new int[vs.length];
		for (int i = 0; i < vs.length; i++) {
			min[i] = ((IntDomainVar) vs[i]).getInf();
			max[i] = ((IntDomainVar) vs[i]).getSup();
		}
		LargeRelation relation = makeLargeRelation(min, max, tuples, feas);
		if (feas) {
			return createGAC3rmPositiveLargeConstraint(vs, (IterIndexedTuplesTable) relation);
		} else {
			return createGAC3rmNegativeLargeConstraint(vs, relation);
		}
	}

	/**
	 * @deprecated use relationTupleFC or relationTupleAC
	 */
	public Constraint relationTuple(IntVar[] vs, LargeRelation rela) {
		return relationTupleFC(vs, rela);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given consistency
	 * relation
	 *
	 * @param vs
	 * @param rela
	 */
	public Constraint relationTupleFC(IntVar[] vs, LargeRelation rela) {
		return createFCLargeConstraint(vs, rela);
	}


	/**
	 * Create a constraint enforcing Arc Consistency on a given consistency
	 * relation where iteration over feasible tuples is possible.
	 *
	 * @param vs
	 * @param rela
	 */
	public Constraint relationTupleAC(IntVar[] vs, IterIndexedLargeRelation rela) {
		return createGAC3rmPositiveLargeConstraint(vs, rela);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given consistency
	 * relation defined by infeasible tuples. It can also be used for feasible
	 * tuples but will be less efficient than the use of an IterLargeRelation
	 *
	 * @param vs
	 * @param rela
	 */
	public Constraint relationTupleAC(IntVar[] vs, LargeRelation rela) {
		if (rela instanceof IterIndexedLargeRelation) {
			return createGAC3rmPositiveLargeConstraint(vs, (IterIndexedLargeRelation) rela);
		}
		return createGAC3rmNegativeLargeConstraint(vs, rela);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list
	 * of infeasible tuples
	 *
	 * @param vars
	 * @param tuples :  a list of int[] corresponding to infeasible tuples
	 */
	public Constraint infeasTupleFC(IntVar[] vars,  List<int[]> tuples) {
		return makeTupleFC(vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list
	 * of feasible tuples
	 *
	 * @param vars
	 * @param tuples :  a list of int[] corresponding to feasible tuples
	 */
	public Constraint feasTupleFC(IntVar[] vars, List<int[]> tuples) {
		return makeTupleFC(vars, tuples, true);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list
	 * of infeasible tuples
	 *
	 * @param vars
	 * @param tuples :  a list of int[] corresponding to infeasible tuples
	 */
	public Constraint infeasTupleAC(IntVar[] vars, List<int[]> tuples) {
		return makeTupleAC(vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list
	 * of feasible tuples
	 *
	 * @param vars
	 * @param tuples :  a list of int[] corresponding to feasible tuples
	 */
	public Constraint feasTupleAC(IntVar[] vars,  List<int[]> tuples) {
		return makeTupleAC(vars, tuples, true);
	}

	public Constraint relationPairAC(IntVar v1, IntVar v2, BinRelation binR) {
		return relationPairAC(v1, v2, binR, 32);
	}

	public Constraint infeasPairAC(IntVar v1, IntVar v2,  List<int[]> mat) {
		return makePairAC(v1, v2, mat, false, 32);
	}

	public Constraint infeasPairAC(IntVar v1, IntVar v2,  List<int[]> mat, int ac) {
		return makePairAC(v1, v2, mat, false, ac);
	}

	public Constraint feasPairAC(IntVar v1, IntVar v2,  List<int[]> mat) {
		return makePairAC(v1, v2, mat, true, 32);
	}

	public Constraint feasPairAC(IntVar v1, IntVar v2,  List<int[]> mat, int ac) {
		return makePairAC(v1, v2, mat, true, ac);
	}

	public Constraint infeasPairAC(IntVar v1, IntVar v2, boolean[][] mat) {
		return makePairAC(v1, v2, mat, false, 32);
	}

	public Constraint infeasPairAC(IntVar v1, IntVar v2, boolean[][] mat, int ac) {
		return makePairAC(v1, v2, mat, false, ac);
	}

	public Constraint feasPairAC(IntVar v1, IntVar v2, boolean[][] mat) {
		return makePairAC(v1, v2, mat, true, 32);
	}

	public Constraint feasPairAC(IntVar v1, IntVar v2, boolean[][] mat, int ac) {
		return makePairAC(v1, v2, mat, true, ac);
	}

	/**
	 * Ensures |x-y| = c;
	 *
	 * @param x
	 * @param y
	 * @param c : the distance
	 */
	public Constraint distanceEQ(IntVar x, IntVar y, int c) {
		return new DistanceXYC((IntDomainVar) x, (IntDomainVar) y, c, 0);
	}

	/**
	 * Ensures |x-y| < c;
	 *
	 * @param x
	 * @param y
	 * @param c : the distance
	 */
	public Constraint distanceLT(IntVar x, IntVar y, int c) {
		return new DistanceXYC((IntDomainVar) x, (IntDomainVar) y, c, 1);
	}

	/**
	 * Ensures |x-y| > c;
	 *
	 * @param x
	 * @param y
	 * @param c : the distance
	 */
	public Constraint distanceGT(IntVar x, IntVar y, int c) {
		return new DistanceXYC((IntDomainVar) x, (IntDomainVar) y, c, 2);
	}

	/**
	 * Ensures |x-y| = z + c;
	 * Warning: only achieves BoundConsistency for the moment !
	 * @param x
	 * @param y
	 * @param z : the variable part of the distance
	 * @param c : the fix part of the distance
	 */
	public Constraint distanceEQ(IntVar x, IntVar y, IntVar z, int c) {
		return new DistanceXYZ((IntDomainVar) x, (IntDomainVar) y, (IntDomainVar) z, c, 0);
	}

	/**
	 * Ensures |x-y| < z + c;
	 * Warning: only achieves BoundConsistency for the moment !
	 * @param x
	 * @param y
	 * @param z : the variable part of the distance
	 * @param c : the fix part of the distance
	 */
	public Constraint distanceLT(IntVar x, IntVar y, IntVar z, int c) {
		return new DistanceXYZ((IntDomainVar) x, (IntDomainVar) y, (IntDomainVar) z, c, 1);
	}

	/**
	 * Ensures |x-y| > z + c;
	 * Warning: only achieves BoundConsistency for the moment !
	 * @param x
	 * @param y
	 * @param z : the variable part of the distance
	 * @param c : the fix part of the distance
	 */
	public Constraint distanceGT(IntVar x, IntVar y, IntVar z, int c) {
		return new DistanceXYZ((IntDomainVar) x, (IntDomainVar) y, (IntDomainVar) z, c, 2);
	}


	/**
	 * Ensures x = Math.abs(y);
	 *
	 * @param x
	 * @param y
	 */
	public Constraint abs(IntVar x, IntVar y) {
		return new Absolute((IntDomainVar) x, (IntDomainVar) y);
	}


	/**
	 * Ensures the variable "min" to represent the minimum value
	 * that occurs in the list vars
	 *
	 * @param vars List of variables
	 * @param min  Variable to represent the minimum among vars
	 */
	public Constraint min(IntVar[] vars, IntVar min) {
		return createMin(vars, min);
	}

	/**
	 * Ensures the variable "max" to represent the maximum value
	 * that occurs in the list vars
	 *
	 * @param vars List of variables
	 * @param max  Variable to represent the maximum among vars
	 */
	public Constraint max(IntVar[] vars, IntVar max) {
		return createMax(vars, max);
	}

	/**
	 * Ensures the variable "min" to represent the minimum value
	 * of x and y.
	 *
	 * @param min Variable to represent the minimum among vars
	 */
	public Constraint min(IntVar x, IntVar y, IntVar min) {
		IntVar[] vars = new IntVar[2];
		vars[0] = x;
		vars[1] = y;
		return createMin(vars, min);
	}

	/**
	 * Ensures the variable "max" to represent the maximum value
	 * of x and y.
	 *
	 * @param max Variable to represent the maximum among vars
	 */
	public Constraint max(IntVar x, IntVar y, IntVar max) {
		IntVar[] vars = new IntVar[2];
		vars[0] = x;
		vars[1] = y;
		return createMax(vars, max);
	}


	protected Constraint makeOccurrence(IntVar[] lvars, IntVar occVar, int occval, boolean onInf, boolean onSup) {
		IntVar[] tmpvars = new IntVar[lvars.length + 1];
		System.arraycopy(lvars, 0, tmpvars, 0, lvars.length);
		tmpvars[lvars.length] = occVar;
		return this.createOccurrence(tmpvars, occval, onInf, onSup);
	}

	/**
	 * Ensures that the occurrence variable contains the number of occurrences of the given value in the list of
	 * variables
	 *
	 * @param vars       List of variables where the value can appear
	 * @param occurrence The variable that should contain the occurence number
	 */

	public Constraint occurrence(IntVar[] vars, int value, IntVar occurrence) {
		return makeOccurrence(vars, occurrence, value, true, true);
	}

	/**
	 * Ensures that the lower bound of occurrence is at least equal to the number of occurences
	 * size{forall v in vars | v = value} <= occurence
	 */
	public Constraint occurenceMin(IntVar[] vars, int value, IntVar occurrence) {
		return makeOccurrence(vars, occurrence, value, true, false);
	}

	/**
	 * Ensures that the upper bound of occurrence is at most equal to the number of occurences
	 * size{forall v in vars | v = value} >= occurence
	 */
	public Constraint occurenceMax(IntVar[] vars, int value, IntVar occurrence) {
		return makeOccurrence(vars, occurrence, value, false, true);
	}

	/**
	 * subscript constraint: accessing an array with a variable index
	 */
	public Constraint nth(IntVar index, int[] values, IntVar val) {
		return createSubscript(index, values, val, 0);
	}

	/**
	 * subscript constraint: accessing an array of variables with a variable index
	 */
	public Constraint nth(IntVar index, IntVar[] varArray, IntVar val) {
		return createSubscript(index, varArray, val, 0);
	}

	/**
	 * subscript constraint: accessing a matix of variables with two variables indexes
	 */
	public Constraint nth(IntVar index, IntVar index2, int[][] varArray, IntVar val) {
		return createSubscript(index, index2, varArray, val);
	}

	/**
	 * State a simple channeling bewteen a boolean variable and an interger variable
	 * Ensures for that b = 1 iff x = j
	 *
	 * @param b : a boolean variable
	 * @param x : an integer variable
	 * @param j : the value such that b = 1 ssi x = j, and b = 0 otherwise
	 */
	public Constraint boolChanneling(IntVar b, IntVar x, int j) {
		IntVar boolv, intv;
		if ((((IntDomainVar) b).getInf() >= 0) && (((IntDomainVar) b).getSup() <= 1)) {
			boolv = b;
			intv = x;
		} else {
			boolv = x;
			intv = b;
		}
		if ((((IntDomainVar) boolv).getInf() >= 0) && (((IntDomainVar) boolv).getSup() <= 1) && ((IntDomainVar) intv).canBeInstantiatedTo(j))
			return createBoolChanneling(boolv, intv, j);
		else
			throw new Error(b + " should be a boolean variable and " + j + " should belongs to the domain of " + x);
	}

	/**
	 * State a channeling bewteen two arrays of integer variables x and y with the same domain which enforces
	 * x[i] = j <=> y[j] = i
	 */
	public Constraint inverseChanneling(IntVar[] x, IntVar[] y) {
		if (x.length == y.length) {
			for (int i = 0; i < x.length; i++) {
				if ((((IntDomainVar) x[i]).getInf() != ((IntDomainVar) y[i]).getInf()) ||
						(((IntDomainVar) x[i]).getSup() != ((IntDomainVar) y[i]).getSup()))
					throw new Error(x[i] + " and " + y[i] + " should have the same domain in inverseChanneling");
			}
			return createInverseChanneling(x, y);
		} else
			throw new Error("intvar arrays of inverseChanneling should have the same size " + x.length + " - " + y.length);


	}

	/**
	 * All different constraints with a global filtering :
	 * v1 != v2, v1 != v3, v2 != v3 ... For each (i,j), v_i != v_j
	 * If vars is a table of BoundIntVar a dedicated algorithm is used. In case
	 * of EnumIntVar it is the regin alldifferent.
	 */
	public Constraint allDifferent(IntVar[] vars) {
		return allDifferent(vars, true);
	}

	/**
	 * All different constraints : v1 != v2, v1 != v3, v2 != v3 ... For each (i,j), v_i != v_j.
	 * parameter global specifies if a global filtering algorithm is used for propagation or not.
	 */
	public Constraint allDifferent(IntVar[] vars, boolean global) {
		if (global) {
			if (((IntDomainVar) vars[0]).hasEnumeratedDomain())
				return createAllDifferent(vars);
			else
				return createBoundAllDiff(vars, true);
		} else {
			return createBoundAllDiff(vars, false);
		}
	}

	/**
	 * Bound all different constraint using the propagator of
	 * A. Lopez-Ortiz, C.-G. Quimper, J. Tromp, and P. van Beek.
	 * A fast and simple algorithm for bounds consistency of the alldifferent
	 * constraint. IJCAI-2003.
	 * parameter global specifies if the BC algorithm is used or not.
	 */
	public Constraint boundAllDifferent(IntVar[] vars, boolean global) {
		return createBoundAllDiff(vars, global);
	}

	/**
	 * Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 */

	public Constraint globalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up) {
		if (((IntDomainVar) vars[0]).hasEnumeratedDomain())
			return createGlobalCardinality(vars, min, max, low, up);
		else return createBoundGlobalCardinality(vars, min, max, low, up);
	}

	/**
	 * Global cardinality : Given an array of variables vars such that their domains are subsets of
	 * [1, n], the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - 1] and up[i - 1]. Note that the length
	 * of low and up should be exactly n.
	 */

	public Constraint globalCardinality(IntVar[] vars, int[] low, int[] up) {
		if (((IntDomainVar) vars[0]).hasEnumeratedDomain())
			return createGlobalCardinality(vars, 1, low.length, low, up);
		else return createBoundGlobalCardinality(vars, 1, low.length, low, up);
	}

	/**
	 * Bound Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 * Use the propagator of :
	 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
	 * An efficient bounds consistency algorithm for the global cardinality constraint.
	 * CP-2003.
	 */
	public Constraint boundGcc(IntVar[] vars, int min, int max, int[] low, int[] up) {
		return createBoundGlobalCardinality(vars, min, max, low, up);
	}

	/**
	 * * Bound Global cardinality : Given an array of variables vars, an array of variables card to represent the cardinalities, the constraint ensures that the number of occurences
	 * of the value i among the variables is equal to card[i].
	 * this constraint enforces :
	 * - Bound Consistency over vars regarding the lower and upper bounds of cards
	 * - maintain the upperbound of card by counting the number of variables in which each value
	 * can occur
	 * - maintain the lowerbound of card by counting the number of variables instantiated to a value
	 * - enforce card[0] + ... + card[m] = n (n = the number of variables, m = number of values)
	 */
	public Constraint boundGccVar(IntVar[] vars, int min, int max, IntVar[] card) {
		return createBoundGlobalCardinalityVar(vars, min, max, card);
	}

	/**
	 * Create a Regular constraint that enforce the sequence of variables to be a word
	 * recognized by the dfa auto.
	 * For example regexp = "(1|2)(3*)(4|5)";
	 * The same dfa can be used for different propagators.
	 */
	public Constraint regular(DFA auto, IntVar[] vars) {
		return createRegular(vars, auto);
	}


	/**
	 * Create a Regular constraint that enforce the sequence of variables to match the regular
	 * expression.
	 */
	public Constraint regular(String regexp, IntVar[] vars) {
		return createRegular(vars, new DFA(regexp, vars.length));
	}

	/**
	 * A Regular constraint based on a DFA which is built from a list of FEASIBLE tuples.
	 * This api provides a GAC algorithm for a constraint defined by its allowed tuples.
	 * This can be more efficient than a standart GAC algorithm if the tuples are really structured
	 * so that the dfa is compact.
	 * The minimal dfa is built from the list by computing incrementally the minimal dfa after each addition of tuple.
	 *
	 * @param tuples : a list of int[] corresponding to the allowed tuples
	 */
	public Constraint regular(IntVar[] vars, List<int[]> tuples) {
		return createRegular(vars, tuples);
	}

	/**
	 * A Regular constraint based on a DFA which is built from a list of INFEASIBLE tuples
	 * As the relation is defined by infeasible tuples and we build the feasible automaton,
	 * we need to know the range of values by the max and min fields...
	 * This api provides a GAC algorithm for a constraint defined by its allowed tuples.
	 * This can be more efficient than a standart GAC algorithm if the tuples are really structured
	 * so that the dfa is compact.
	 * The minimal dfa is built from the list by computing incrementally the minimal dfa after each addition of tuple.
	 *
	 * @param tuples : a list of int[] corresponding to tuple
	 * @param max    : The maximum value of the alphabet used for each layer (upper bound of each variables).
	 * @param min    : The minimum value of the alphabet used for each layer (lower bound of each variables).
	 */
	public Constraint regular(IntVar[] vars, List<int[]> tuples, int[] min, int[] max) {
		return createRegular(vars, tuples, min, max);
	}

	/**
	 * Enforce the minimal and maximal sizes of the streches of any value given in strechesParameters.
	 * Usefull for Rostering Problems. The constraint is implemented by a Regular constraint that
	 * perform GAC.
	 *
	 * @param vars                : a sequence of variables
	 * @param stretchesParameters : a list of triples of integers :
	 *                            (value, occmin, occmax) denoting for each value the minimal and maximal
	 *                            lenght of any stretch of the corresponding value.
	 */
	public Constraint stretchPath(IntVar[] vars, List<int[]> stretchesParameters) {
		assert (stretchesParameters.get(0).length == 3);
		return createStretch(vars, stretchesParameters);
	}

	/**
	 * Cumulative : Given a set of tasks defined by their starting dates, ending dates, durations and
	 * consumptions/heights, the cumulative ensures that at any time t, the sum of the heights of the tasks
	 * which are executed at time t does not exceed a given limit C (the capacity of the ressource).
	 * The notion of task does not exist yet in choco. The cumulative takes therefore as input three arrays
	 * of integer variables (of same size n) denoting the starting, ending, and duration of each task.
	 * The heights of the tasks are considered constant and given via an array of size n of positive integers.
	 * The last parameter Capa denotes the Capacity of the cumulative (of the ressource).
	 * The implementation is based on the paper of Bediceanu and al :
	 * "A new multi-resource cumulatives constraint with negative heights" in CP02
	 */

	public Constraint cumulative(IntVar[] starts, IntVar[] ends, IntVar[] durations, int[] heights, int Capa) {
		int n = starts.length;
		if (ends.length != n || durations.length != n || heights.length != n) {
			throw new Error("starts, ends, durations and heigts should be of the same size " + n);
		}
		return createCumulative(starts, ends, durations, heights, Capa);
	}

	public Constraint cumulative(IntVar[] starts, IntVar[] ends, IntVar[] durations, IntVar[] heights, int Capa) {
		int n = starts.length;
		if (ends.length != n || durations.length != n || heights.length != n) {
			throw new Error("starts, ends, durations and heigts should be of the same size " + n);
		}
		return createCumulative(starts, ends, durations, heights, Capa);
	}

	/**
	 * Enforce a lexicographic ordering on two vectors of integer
	 * variables x <_lex y with x = <x_0, ..., x_n>, and y = <y_0, ..., y_n>.
	 * ref : Global Constraints for Lexicographic Orderings (Frisch and al)
	 */
	public Constraint lexeq(IntVar[] v1, IntVar[] v2) {
		if (v1.length != v2.length)
			throw new Error("the vectors of variables should be of same size for lex " + v1.length + " and " + v2.length);
		return createLex(v1, v2, false);
	}

	/**
	 * Enforce a strict lexicographic ordering on two vectors of integer
	 * variables x <_lex y with x = <x_0, ..., x_n>, and y = <y_0, ..., y_n>.
	 * ref : Global Constraints for Lexicographic Orderings (Frisch and al)
	 */
	public Constraint lex(IntVar[] v1, IntVar[] v2) {
		if (v1.length != v2.length)
			throw new Error("the vectors of variables should be of same size for lex " + v1.length + " and " + v2.length);
		return createLex(v1, v2, true);
	}


	/**
	 * Let x and x' be two vectors of variables of the same length, and
	 * v be an instantiation. The sorting constraint sorting(x, x') holds
	 * on the set of variables being either in x or in x',
	 * and is satisfied by v if and only if v(x') is the sorted
	 * version of v(x) in increasing order.
	 * This constraint is called the Sortedness Constraint
	 * in [Bleuzen-Guernalec and Colmerauer 1997] and in [Mehlhorn and Thiel 2000].
	 */
	public Constraint sorting(IntVar[] v1, IntVar[] v2) {
		if (v1.length != v2.length)
			throw new Error("the vectors of variables should be of same size for sorting " + v1.length + " and " + v2.length);

		return createSorting(v1, v2);
	}

	/**
	 * Let x and y be two vectors of n integers, and let x? and y? be the version of x and y rearranged in increasing order.
	 * x and y are said leximin-indifferent if x?=y?. y is leximin-preferred to x (written y>leximinx if and only if there is an i< n such that for all j� i:
	 * - the jth component of x? is equal to the jth component of y?
	 * - the ith component of x? is lower than the ith component of y?
	 * Let x and x' be two vectors of variables, and v be an instantiation.
	 * The constraint Leximin(x, x') holds on the set of variables belonging to x or x', and is satisfied by v if and only if v(x) <leximin v(x').
	 * [Frisch et al. 2003]	A. Frisch, B. Hnich, Z. Kiziltan, I. Miguel, and T. Walsh. Multiset ordering constraints. In Proc. of IJCAI'03. Acapulco, Mexico, 2003.
	 */
	public Constraint leximin(IntVar[] v1, IntVar[] v2) {
		if (v1.length != v2.length)
			throw new Error("the vectors of variables should be of same size for leximin " + v1.length + " and " + v2.length);
		return createLeximin(v1, v2);
	}


	/**
	 * Let x and y be two vectors of n integers, and let x? and y? be the version of x and y rearranged in increasing order.
	 * x and y are said leximin-indifferent if x?=y?. y is leximin-preferred to x (written y>leximinx if and only if there is an i< n such that for all j� i:
	 * - the jth component of x? is equal to the jth component of y?
	 * - the ith component of x? is lower than the ith component of y?
	 * Let x and x' be two vectors of variables, and v be an instantiation.
	 * The constraint Leximin(x, x') holds on the set of variables belonging to x or x', and is satisfied by v if and only if v(x) <leximin v(x').
	 * [Frisch et al. 2003]	A. Frisch, B. Hnich, Z. Kiziltan, I. Miguel, and T. Walsh. Multiset ordering constraints. In Proc. of IJCAI'03. Acapulco, Mexico, 2003.
	 */
	public Constraint leximin(int[] v1, IntVar[] v2) {
		if (v1.length != v2.length)
			throw new Error("the vectors should be of same size for leximin " + v1.length + " and " + v2.length);
		return createLeximin(v1, v2);
	}

	/**
	 * Enforce the number of distinct values among vars to be less than nvalue;
	 */
	public Constraint atMostNValue(IntVar[] vars, IntVar nvalue) {
		return createAtMostNvalue(vars, nvalue); //new AtMostNValue(ivars,nvalue);
	}

	// ------------- Constraints over sets -------------------------------

	public void setCardReasoning(boolean creas) {
		cardinalityReasonningsOnSETS = creas;
	}

	/**
	 * Enforce a set to be the intersection of two others.
	 *
	 * @param inter the intersection of sv1 and sv2
	 */
	public Constraint setInter(SetVar sv1, SetVar sv2, SetVar inter) {
		return createSetIntersection(sv1, sv2, inter);
	}

	/**
	 * Enforce a set to be the union of two others
	 *
	 * @param union the union of sv1 and sv2
	 * @return the union constraint
	 */
	public Constraint setUnion(SetVar sv1, SetVar sv2, SetVar union) {
		return createSetUnion(sv1, sv2, union);
	}

	public Constraint eqCard(SetVar sv, IntVar v) {
		return eq(sv.getCard(), v);
		//return createSetCard(sv, v, true, true);
	}

	public Constraint eqCard(SetVar sv, int val) {
		return eq(sv.getCard(), val);
		//IntVar v = makeConstantIntVar("cste: ", val);
		//return createSetCard(sv, v, true, true);
	}

	public Constraint geqCard(SetVar sv, IntVar v) {
		return geq(sv.getCard(), v);
		//return createSetCard(sv, v, false, true);
	}

	public Constraint geqCard(SetVar sv, int val) {
		return geq(sv.getCard(), val);
		//IntVar v = makeConstantIntVar("cste: ", val);
		//return createSetCard(sv, v, false, true);
	}

	public Constraint leqCard(SetVar sv, IntVar v) {
		return leq(sv.getCard(), v);
		//return createSetCard(sv, v, true, false);
	}

	public Constraint leqCard(SetVar sv, int val) {
		return eq(sv.getCard(), val);
		//IntVar v = makeConstantIntVar("cste: ", val);
		//return createSetCard(sv, v, true, false);
	}

	public Constraint setDisjoint(SetVar sv1, SetVar sv2) {
		return createDisjoint(sv1, sv2);
	}

	public Constraint member(int val, SetVar sv1) {
		return createMemberX(sv1, val);
	}

	public Constraint member(SetVar sv1, int val) {
		return createMemberX(sv1, val);
	}

	public Constraint member(SetVar sv1, IntVar var) {
		return createMemberXY(sv1, var);
	}

	public Constraint member(IntVar var, SetVar sv1) {
		return createMemberXY(sv1, var);
	}

	public Constraint notMember(int val, SetVar sv1) {
		return createNotMemberX(sv1, val);
	}

	public Constraint notMember(SetVar sv1, int val) {
		return notMember(val, sv1);
	}

	public Constraint notMember(SetVar sv1, IntVar var) {
		return notMember(var, sv1);
	}

	public Constraint notMember(IntVar var, SetVar sv1) {
		return createNotMemberXY(sv1, var);
	}

	// -------------------------------------------------------------------

	// All abstract methods for constructing constraint
	// that need be defined by a Problem implementing a model

	protected abstract Constraint createEqualXC(IntVar v0, int c);

	protected abstract Constraint createNotEqualXC(IntVar v0, int c);

	protected abstract Constraint createGreaterOrEqualXC(IntVar v0, int c);

	protected abstract Constraint createLessOrEqualXC(IntVar v0, int c);

	protected abstract Constraint createEqualXYC(IntVar intVar, IntVar intVar1, int i);

	protected abstract Constraint createNotEqualXYC(IntVar variable, IntVar variable1, int i);

	protected abstract Constraint createGreaterOrEqualXYC(IntVar intVar, IntVar intVar1, int i);

	protected abstract Constraint createIntLinComb(IntVar[] sortedVars, int[] sortedCoeffs, int nbPositiveCoeffs, int c, int linOperator);

	protected abstract Constraint createTimesXYZ(IntVar x, IntVar y, IntVar z);

	protected abstract Constraint createBinDisjunction(Constraint c0, Constraint c1);

	protected abstract Constraint createLargeDisjunction(Constraint[] alternatives);

	protected abstract Constraint createBinConjunction(Constraint c0, Constraint c1);

	protected abstract Constraint createLargeConjunction(Constraint[] alternatives);

	protected abstract Constraint createNegation(Constraint c);

	protected abstract Constraint createCardinality(Constraint[] constList, IntVar cardVar, boolean b, boolean b1);

	protected abstract Constraint createGuard(Constraint c0, Constraint c1);

	protected abstract Constraint createEquiv(Constraint c0, Constraint c1);

	protected abstract Constraint createAC3BinConstraint(IntVar v1, IntVar v2, BinRelation relation);

	protected abstract Constraint createAC4BinConstraint(IntVar v1, IntVar v2, BinRelation relation);

	protected abstract Constraint createAC2001BinConstraint(IntVar v1, IntVar v2, BinRelation relation);

	protected abstract Constraint createAC3rmBinConstraint(IntVar v1, IntVar v2, BinRelation relation);

	protected abstract Constraint createFCLargeConstraint(IntVar[] vs, LargeRelation relation);

	protected abstract Constraint createGAC2001PositiveLargeConstraint(IntVar[] vs, IterLargeRelation relation);

	protected abstract Constraint createGAC3rmPositiveLargeConstraint(IntVar[] vs, IterIndexedLargeRelation relation);

	protected abstract Constraint createGAC2001NegativeLargeConstraint(IntVar[] vs, LargeRelation relation);

	protected abstract Constraint createGAC3rmNegativeLargeConstraint(IntVar[] vars, LargeRelation relation);	

	protected abstract Constraint createMin(IntVar[] lvars, IntVar min);

	protected abstract Constraint createMax(IntVar[] lvars, IntVar max);

	protected abstract Constraint createOccurrence(IntVar[] lvars, int occval, boolean onInf, boolean onSup);

	protected abstract Constraint createSorting(IntVar[] v1, IntVar[] v2);

	protected abstract Constraint createAllDifferent(IntVar[] vars);

	protected abstract Constraint createLeximin(IntVar[] v1, IntVar[] v2);

	protected abstract Constraint createLeximin(int[] v1, IntVar[] v2);

	protected abstract Constraint createBoundAllDiff(IntVar[] vars, boolean global);

	protected abstract Constraint createGlobalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up);

	protected abstract Constraint createBoundGlobalCardinality(IntVar[] vars, int min, int max, int[] low, int[] up);

	protected abstract Constraint createBoundGlobalCardinalityVar(IntVar[] vars, int min, int max, IntVar[] card);

	protected abstract Constraint createRegular(IntVar[] vars, DFA auto);

	protected abstract Constraint createRegular(IntVar[] vars, List<int[]> feasibleTuples);

	protected abstract Constraint createRegular(IntVar[] vars, List<int[]> infeasibleTuples, int[] min, int[] max);

	protected abstract Constraint createStretch(IntVar[] vars, List<int[]> stretchParameters);

	protected abstract Constraint createCumulative(IntVar[] sts, IntVar[] ends, IntVar[] durations, int[] h, int capa);

	protected abstract Constraint createCumulative(IntVar[] sts, IntVar[] ends, IntVar[] durations, IntVar[] heights, int capa);

	protected abstract Constraint createLex(IntVar[] v1, IntVar[] v2, boolean strict);

	protected abstract Constraint createSubscript(IntVar index, int[] values, IntVar val, int offset);

	protected abstract Constraint createSubscript(IntVar index, IntVar[] varArray, IntVar val, int offset);

	protected abstract Constraint createSubscript(IntVar index, IntVar index2, int[][] valArray, IntVar val);

	protected abstract Constraint createInverseChanneling(IntVar[] x, IntVar[] y);

	protected abstract Constraint createBoolChanneling(IntVar b, IntVar x, int j);

	protected abstract Constraint createAtMostNvalue(IntVar[] vars, IntVar nvalue);

	protected abstract IntDomainVar createIntVar(String name, int domainType, int min, int max);

	protected abstract IntDomainVar createIntVar(String name, int[] sortedValues);

	protected abstract RealVar createRealVal(String name, double min, double max);

	protected abstract SetVar createSetVar(String name, int a, int b, boolean enumcard);

	protected abstract RealIntervalConstant createRealIntervalConstant(double a, double b);

	protected abstract RealExp createRealSin(RealExp exp);

	protected abstract RealExp createRealCos(RealExp exp);

	protected abstract RealExp createRealIntegerPower(RealExp exp, int power);

	protected abstract RealExp createRealPlus(RealExp exp1, RealExp exp2);

	protected abstract RealExp createRealMinus(RealExp exp1, RealExp exp2);

	protected abstract RealExp createRealMult(RealExp exp1, RealExp exp2);

	protected abstract Constraint createEquation(RealVar[] tmpVars, RealExp exp, RealIntervalConstant cst);

	protected abstract Constraint createMemberXY(SetVar sv1, IntVar var);

	protected abstract Constraint createNotMemberXY(SetVar sv1, IntVar var);

	protected abstract Constraint createMemberX(SetVar sv1, int val);

	protected abstract Constraint createNotMemberX(SetVar sv1, int val);

	protected abstract Constraint createDisjoint(SetVar sv1, SetVar sv2);

	protected abstract Constraint createSetIntersection(SetVar sv1, SetVar sv2, SetVar inter);

	protected abstract Constraint createSetUnion(SetVar sv1, SetVar sv2, SetVar union);

	protected abstract Constraint createSetCard(SetVar sv, IntVar v, boolean b1, boolean b2);

	/**
	 * <i>Network management:</i>
	 * Retrieve a variable by its index (all integer variables of
	 * the problem are numbered in sequence from 0 on)
	 *
	 * @param i index of the variable in the problem
	 */

	public final IntVar getIntVar(int i) {
		return (IntVar) intVars.get(i);
	}

	public int getIntVarIndex(IntVar c) {
		return intVars.indexOf(c);
	}

	/**
	 * retrieving the total number of variables
	 *
	 * @return the total number of variables in the problem
	 */
	public final int getNbIntVars() {
		return intVars.size();
	}

	/**
	 * Returns a real variable.
	 *
	 * @param i index of the variable
	 * @return the i-th real variable
	 */
	public final RealVar getRealVar(int i) {
		return (RealVar) floatVars.get(i);
	}

	/**
	 * Returns the number of variables modelling real numbers.
	 */
	public final int getNbRealVars() {
		return floatVars.size();
	}

	/**
	 * Returns a set variable.
	 *
	 * @param i index of the variable
	 * @return the i-th real variable
	 */
	public final SetVar getSetVar(int i) {
		return (SetVar) setVars.get(i);
	}

	/**
	 * Returns the number of variables modelling real numbers.
	 */
	public final int getNbSetVars() {
		return setVars.size();
	}
}
