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
package choco.cp.solver.variables.integer;

import static java.lang.System.*;

import java.security.KeyStore.Builder;
import java.util.Arrays;

import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;

/**
 * Implements linear terms: Sigma_i(a_i*X_i), where a_i are search coefficients,
 * and X_i are search domain variable
 */
public class IntTerm implements IntExp {

	/**
	 * The coefficients
	 */
	protected final int[] coefficients;

	/**
	 * The variables
	 */
	protected final IntVar[] variables;

	/**
	 * number of variables involved in the term
	 */
	protected final int nbVars;

	/**
	 * the integer constant involved in the term
	 */
	protected int constant;

	/**
	 * Constructor
	 *
	 * @param capacity number of variables that will be involved in the term
	 */
	public IntTerm(final int capacity) {
		super();
		coefficients = new int[capacity];
		variables = new IntVar[capacity];
		nbVars = capacity;
		constant = 0;
	}

	/**
	 * Constructor by copy
	 *
	 * @param t1 the IntTerm to be copied
	 */
	public IntTerm(IntTerm t1) {
		this(t1.getSize());
		arraycopy(t1.variables, 0, variables, 0, nbVars);
		arraycopy(t1.coefficients, 0, coefficients, 0, nbVars);
		constant = t1.constant;
	}
	
	public IntTerm(int[] lc, IntVar[] lv) {
		this( lc.length);
		if( lc.length != lv.length) throw new SolverException("cant build scalar");
		arraycopy(lv, 0, variables, 0, nbVars);
		arraycopy(lc, 0, coefficients, 0, nbVars);
	}
	
	public IntTerm(IntVar[] lv) {
		this( lv.length);
		arraycopy(lv, 0, variables, 0, nbVars);
		Arrays.fill(coefficients, 1);
	}
	

	
	protected IntTerm(IntTerm t1, int nbMore, boolean moreFirst) {
		this(t1.getSize() + nbMore);
		final int n = t1.getSize();
		final int offset = moreFirst ? nbMore : 0;
		arraycopy(t1.variables, 0, variables, offset, n);
		arraycopy(t1.coefficients, 0, coefficients, offset, n);
		constant = t1.constant;
	}
	
	
	public final static IntTerm opposite(IntTerm t1) {
		final int n = t1.getSize();
		IntTerm res = new IntTerm(n);
		arraycopy(t1.variables, 0, res.variables, 0, n);
		buildOpposite(t1, res, 0);
		res.constant = -t1.constant;
		return res ;
	}
	
	public final static IntTerm plus(IntTerm t1, int coeff, IntVar var, boolean varFirst) {
		IntTerm res = new IntTerm( t1, 1, varFirst);
		final int idx = varFirst ? 0 : res.nbVars - 1;
		res.setCoefficient(idx, coeff);
		res.setVariable(idx, var);
		return res ;
	}
	
	public final static IntTerm minus(int coeff, IntVar var, IntTerm t1) {
		IntTerm res = new IntTerm( t1.getSize() + 1);
		res.setCoefficient(0, coeff);
		res.setVariable(0, var);
		arraycopy(t1.variables, 0, res.variables, 1, t1.getSize());
		buildOpposite(t1, res, 1);
		res.constant = -t1.getConstant();
		return res ;
	}
	
	
	public final static IntTerm plus(IntTerm t1, IntTerm t2) {
		final int n1 = t1.getSize();
		final int n2 = t2.getSize();
		IntTerm res = new IntTerm( t1, n2, false);
		arraycopy(t2.variables, 0, res.variables, n1, n2);
		arraycopy(t2.coefficients, 0, res.coefficients, n1, n2);
		res.constant +=  t2.constant;
		return res ;
	}
	
	public final static IntTerm minus(IntTerm t1, IntTerm t2) {
		final int n1 = t1.getSize();
		final int n2 = t2.getSize();
		IntTerm res = new IntTerm( t1, n2, false);
		arraycopy(t2.variables, 0, res.variables, n1, n2);
		buildOpposite(t2, res, n1);
		res.constant -=  t2.constant;
		return res ;
	}
	
	
	private final static void buildOpposite( IntTerm src, IntTerm dest, int destPos) {
		final int n = src.getSize();
		for (int i = 0; i < n; i++) {
			dest.coefficients[ destPos + i ] = - src.coefficients[i];
		}
	}

	public final boolean isConstant() {
		return nbVars == 0;
	}
	
	public final boolean isUnary() {
		return nbVars == 1;
	}

	public final boolean isBinary() {
		return nbVars == 2;
	}

	public final boolean isBinaryMinus() {
		return coefficients[0] + coefficients[1] == 0;
	}

	public final boolean isBinaryPlus() {
		return coefficients[0] - coefficients[1] == 0;
	}

//	public final boolean isCoefficientNull(int i) {
//		return coefficients[i] == 0;
//	}
//	
//	public final boolean isCoefficientPositive(int i) {
//		return coefficients[i] > 0;
//	}
//	
//	public final boolean isCoefficientNegative(int i) {
//		return coefficients[i] < 0;
//	}
//
//	public final boolean isCoefficientDivisorOf(int i, int val) {
//		return val % coefficients[i] == 0;
//	}
//
//	public final int diviseByCoefficient(int val, int i) {
//		return val / coefficients[i];
//	}

	/**
	 * Pretty print of the expression
	 */
	public String pretty() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < coefficients.length; i++) {
			int coefficient = coefficients[i];
			IntVar variable = variables[i];
			if (i > 0) buf.append(" + ");
			buf.append(coefficient + "*" + variable);
		}
		buf.append(" + " + constant);
		return buf.toString();
	}

	/**
	 * retrieve the array of coefficients
	 *
	 * @return the integer coefficients that are involved in the term
	 */
	public final int[] getCoefficients() {
		return coefficients;
	}

	/**
	 * retrieve the array of variables
	 *
	 * @return the variables that are involved in the term
	 */
	public final IntVar[] getVariables() {
		return variables;
	}

	/**
	 * retrieve the i-th coefficient
	 *
	 * @param index the index of the variable/coefficient in the expression
	 * @return the coefficient
	 */
	public final int getCoefficient(int index) {
		return coefficients[index];
	}

	/**
	 * retrieve the i-th variable
	 *
	 * @param index the index of the variable/coefficient in the expression
	 * @return the coefficient
	 */
	public final IntVar getVariable(int index) {
		return variables[index];
	}

	public final IntDomainVar getIntDVar(int index) {
		return (IntDomainVar) variables[index];
	}

	/**
	 * sets the i-th coefficient
	 *
	 * @param index the index of the variable/coefficient in the expression
	 * @param coef  the coefficient
	 */
	public final void setCoefficient(int index, int coef) {
		coefficients[index] = coef;
	}

	/**
	 * sets the i-th variable
	 *
	 * @param index the index of the variable/coefficient in the expression
	 * @param var   the variable
	 */
	public final void setVariable(int index, IntVar var) {
		variables[index] = var;
	}

	/**
	 * returns the term capacity
	 *
	 * @return the capacity that has been reserved for storing coefficients and varibales
	 */
	public final int getSize() {
		return nbVars;
	}

	/**
	 * returns the integer constant involved in the linear term
	 *
	 * @return the value of the integer constant
	 */
	public final int getConstant() {
		return constant;
	}

	/**
	 * sets the integer constant involved in the linear term
	 *
	 * @param constant the target value
	 */
	public final void setConstant(int constant) {
		this.constant = constant;
	}
	
	public final int[] getOppositeCoefficients() {
		int[] oc = new int[coefficients.length];
		for (int i = 0; i < coefficients.length; i++) {
			oc[i] = - coefficients[i];
		}
		return oc;
	}
	
	
	
}