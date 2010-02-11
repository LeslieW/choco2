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
package choco.cp.solver.constraints.integer.bool;

import choco.cp.solver.constraints.integer.IntLinComb;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A constraint to enforce Sigma_i coef[i]*x_i + k OP y_i where :
 * - OP belongs to >=, <=, =
 * - k is a constant
 * - x_i are boolean variable
 * - t_i is an enum variable
 * It improves the general IntLinComb by storing lower and upper bound of the expression
 * and sorting coefficient for filtering.
 * User: Hadrien
 * Date: 29 oct. 2006
 */
public class BoolIntLinComb extends AbstractLargeIntSConstraint {

	/**
	 * Field representing the type of linear constraint
	 * (equality, inequality, disequality).
	 */
	protected int op = -1;

	/**
	 * Lower bound of the expression
	 */
	protected IStateInt lb;

	/**
	 * upper bound of the expression
	 */
	protected IStateInt ub;


	/**
	 * index of the maximum coefficient of positive sign
	 */
	protected IStateInt maxPosCoeff;

	/**
	 * index of the maximum coefficient of negative sign
	 */
	protected IStateInt maxNegCoeff;

	/**
	 * coefs and vars are sorted in increasing value of the coef
	 */
	protected int[] sCoeffs;

	/**
	 * number of negative coefficients
	 */
	protected int nbNegCoef;

	/**
	 * coefficients of the integer variable
	 */
	protected int objCoef;

	/**
	 * coefficients of the integer variable
	 */
	protected int addcste;


	protected RightMemberBounds rmemb;

	final IntDomainVarImpl varCste;

	public static IntDomainVar[] makeTableVar(IntDomainVar[] vs, IntDomainVar v) {
		IntDomainVar[] nvars = new IntDomainVar[vs.length + 1];
		System.arraycopy(vs, 0, nvars, 0, vs.length);
		nvars[vs.length] = v;
		return nvars;
	}


	/**
	 * Constructs the constraint with the specified variables and constant.
	 * Use the Model.createIntLinComb API instead of this constructor.
	 * WARNING : This constructor assumes :
	 * - there are no null coefficient
	 * - coefficients "coefs" are sorted from the smallest to the biggest (negative coefs first).
	 * - objcoef is strictly POSITIVE
	 * - op belongs to EQ, GT, NEQ and LEQ
	 */
	public BoolIntLinComb(IEnvironment environment, IntDomainVar[] vs, int[] coefs, IntDomainVar c, int objcoef, int scste, int op) {
		super(makeTableVar(vs, c));
		this.sCoeffs = coefs;
		this.op = op;
		this.cste = vs.length;
		this.varCste = (IntDomainVarImpl) vars[cste];
		this.objCoef = objcoef;
		this.addcste = scste;
		nbNegCoef = 0;
		while (nbNegCoef < cste && sCoeffs[nbNegCoef] < 0) {
			nbNegCoef++;
		}
		if (op == IntLinComb.EQ || op == IntLinComb.GEQ || op == IntLinComb.LEQ) {
			this.maxPosCoeff = environment.makeInt();
			this.maxNegCoeff = environment.makeInt();
		}
		if (op == IntLinComb.EQ || op == IntLinComb.GEQ) {
			this.ub = environment.makeInt();
		}
		if (op == IntLinComb.EQ || op == IntLinComb.LEQ) {
			this.lb = environment.makeInt();
		}
		if (objCoef == 1) {
			rmemb = new SimpleRightMemberBounds();
		} else {
			rmemb = new RightMemberBounds();
		}
	}


    public int getFilteredEventMask(int idx) {
        if(idx<cste){
            return IntVarEvent.INSTINTbitvector;
        }else{
            return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
        }
    }    


    public class RightMemberBounds {

		public int getInfRight() {
			return objCoef * varCste.getInf();
		}

		public int getSupRight() {
			return objCoef * varCste.getSup();
		}

		public int getNewInfForObj() {
			return MathUtils.divCeil(lb.get(), objCoef);
		}

		public int getNewSupForObj() {
			return MathUtils.divFloor(ub.get(), objCoef);
		}
	}

	public class SimpleRightMemberBounds extends RightMemberBounds {

		@Override
		public final int getInfRight() {
			return varCste.getInf();
		}

		@Override
		public final int getSupRight() {
			return varCste.getSup();
		}

		@Override
		public final int getNewInfForObj() {
			return lb.get();
		}

		@Override
		public final int getNewSupForObj() {
			return ub.get();
		}

	}



	/************************************************************************/
	/************** update of data structures *******************************/
	/**
	 * ********************************************************************
	 */

	public final void updateUbLbOnInst(int idx, int i) {
		if (sCoeffs[idx] < 0) {
			if (i == 1) {
				ub.add(sCoeffs[idx]);
			} else {
				lb.add(-sCoeffs[idx]);
			}
		} else {
			if (i == 1) {
				lb.add(sCoeffs[idx]);
			} else {
				ub.add(-sCoeffs[idx]);
			}
		}
	}

	public final void lookForNewMaxPosCoeff() {
		int i = maxPosCoeff.get() - 1;
		while (i >= nbNegCoef && vars[i].isInstantiated()) {
			i--;
		}
		maxPosCoeff.set(i);
	}

	public final void lookForNewMaxNegCoeff() {
		int i = maxNegCoeff.get() + 1;
		while (i < nbNegCoef && vars[i].isInstantiated()) {
			i++;
		}
		maxNegCoeff.set(i);
	}

	/************************************************************************/
	/************** Main methods for filtering ******************************/
	/************************************************************************/

	public final boolean updateForGEQ() throws ContradictionException {
		boolean change = false;
		change |= filterPosCoeffUb();
		change |= filterNegCoeffUb();
		return change;
	}

	public final boolean updateForLEQ() throws ContradictionException {
		boolean change = false;
		change |= filterPosCoeffLb();
		change |= filterNegCoeffLb();
		return change;
	}

	public final void fixPointOnEQ() throws ContradictionException {
		boolean fixpoint = true;
		while (fixpoint) {
			fixpoint = false;
			varCste.updateSup(rmemb.getNewSupForObj(), cIndices[cste]);
			varCste.updateInf(rmemb.getNewInfForObj(), cIndices[cste]);
			fixpoint |= updateForGEQ();
			fixpoint |= updateForLEQ();
		}
	}

	/* ***********************************************************************/
	/* ************* filtering based on the upperbound of the expression *****/
	/* ***********************************************************************/

	public boolean filterNegCoeffUb() throws ContradictionException {
		boolean change = false;
		int cpt = maxNegCoeff.get();
		while (cpt < nbNegCoef && vars[cpt].isInstantiated()) {
			cpt++;
		}
		while (cpt < nbNegCoef && ub.get() + sCoeffs[cpt] < rmemb.getInfRight()) {
			IntDomainVarImpl v = (IntDomainVarImpl) vars[cpt];
			v.instantiate(0, cIndices[cpt]);
			change = true;
			if (op == IntLinComb.EQ) {
				lb.add(-sCoeffs[cpt]);
			}
			do {
				cpt++;
			} while (cpt < nbNegCoef && vars[cpt].isInstantiated());
		}
		maxNegCoeff.set(cpt);
		return change;
	}

	public boolean filterPosCoeffUb() throws ContradictionException {
		boolean change = false;
		int cpt = maxPosCoeff.get();
		while (cpt >= nbNegCoef && vars[cpt].isInstantiated()) {
			cpt--;
		}
		while (cpt >= nbNegCoef && ub.get() - sCoeffs[cpt] < rmemb.getInfRight()) {
			IntDomainVarImpl v = (IntDomainVarImpl) vars[cpt];
			v.instantiate(1, cIndices[cpt]);
			change = true;
			if (op == IntLinComb.EQ) {
				lb.add(sCoeffs[cpt]);
			}
			do {
				cpt--;
			} while (cpt >= nbNegCoef && vars[cpt].isInstantiated());
		}
		maxPosCoeff.set(cpt);
		return change;
	}

	/************************************************************************/
	/************** filtering based on the lower bound of the expression ****/
	/************************************************************************/

	public final boolean filterPosCoeffLb() throws ContradictionException {
		boolean change = false;
		int cpt = maxPosCoeff.get();
		while (cpt >= nbNegCoef && vars[cpt].isInstantiated()) {
			cpt--;
		}
		while (cpt >= nbNegCoef && lb.get() + sCoeffs[cpt] > rmemb.getSupRight()) {
			vars[cpt].instantiate(0, cIndices[cpt]);
			change = true;
			if (op == IntLinComb.EQ) {
				ub.add(-sCoeffs[cpt]);
			}
			do {
				cpt--;
			} while (cpt >= nbNegCoef && vars[cpt].isInstantiated());
		}
		maxPosCoeff.set(cpt);
		return change;
	}

	/**
	 * enforce variables that would otherwise make the upper bound unreachable
	 */
	public final boolean filterNegCoeffLb() throws ContradictionException {
		boolean change = false;
		int cpt = maxNegCoeff.get();
		while (cpt < nbNegCoef && vars[cpt].isInstantiated()) {
			cpt++;
		}
		while (cpt < nbNegCoef && lb.get() - sCoeffs[cpt] > rmemb.getSupRight()) {
			vars[cpt].instantiate(1, cIndices[cpt]);
			change = true;
			if (op == IntLinComb.EQ) {
				ub.add(sCoeffs[cpt]);
			}
			do {
				cpt++;
			} while (cpt < nbNegCoef && vars[cpt].isInstantiated());
		}
		maxNegCoeff.set(cpt);
		return change;
	}

	/************************************************************************/
	/************** React on event of the constraint ************************/
	/************************************************************************/


	@Override
	public void awakeOnInst(int idx) throws ContradictionException {

		if (idx < cste) {
			int i = vars[idx].getVal();
			//if (!initCopy.get(idx)) {
				if (op == IntLinComb.GEQ) {
					if (sCoeffs[idx] < 0 && i == 1) {
						ub.add(sCoeffs[idx]);
						varCste.updateSup(rmemb.getNewSupForObj(), cIndices[cste]);
						updateForGEQ();
					} else if (sCoeffs[idx] > 0 && i == 0) {
						ub.add(-sCoeffs[idx]);
						varCste.updateSup(rmemb.getNewSupForObj(), cIndices[cste]);
						updateForGEQ();
					} else if (idx == maxPosCoeff.get()) {
						lookForNewMaxPosCoeff();
					} else if (idx == maxNegCoeff.get()) {
						lookForNewMaxNegCoeff();
					}
				} else if (op == IntLinComb.LEQ) {
					if (sCoeffs[idx] > 0 && i == 1) {
						lb.add(sCoeffs[idx]);
						varCste.updateInf(rmemb.getNewInfForObj(), cIndices[cste]);
						updateForLEQ();
					} else if (sCoeffs[idx] < 0 && i == 0) {
						lb.add(-sCoeffs[idx]);
						varCste.updateInf(rmemb.getNewInfForObj(), cIndices[cste]);
						updateForLEQ();
					} else if (idx == maxPosCoeff.get()) {
						lookForNewMaxPosCoeff();
					} else if (idx == maxNegCoeff.get()) {
						lookForNewMaxNegCoeff();
					}
					//updateForLEQ();
				} else if (op == IntLinComb.EQ) {
					updateUbLbOnInst(idx, i);
					fixPointOnEQ();
				} else {
					//TODO
				}
			//}
		} else {
			if (op == IntLinComb.GEQ) {
				filterPosCoeffUb();
				filterNegCoeffUb();
			} else if (op == IntLinComb.EQ) {
				fixPointOnEQ();
			} else if (op == IntLinComb.LEQ) {
				filterPosCoeffLb();
				filterNegCoeffLb();
			} else {
				//TODO
			}
		}
	}

	// can only be called on idx = cste
	@Override
	public void awakeOnInf(int idx) throws ContradictionException {
		if (op == IntLinComb.GEQ) {
			filterPosCoeffUb();
			filterNegCoeffUb();
		} else if (op == IntLinComb.EQ) {
			fixPointOnEQ();
		} else {
			//TODO
		}
	}

	// can only be called on idx = cste
	@Override
	public void awakeOnSup(int idx) throws ContradictionException {
		if (op == IntLinComb.EQ) {
			fixPointOnEQ();
		} else if (op == IntLinComb.LEQ) {
			filterPosCoeffLb();
			filterNegCoeffLb();
		} else {
			//TODO
		}
	}

	//@Override
	public void propagate() throws ContradictionException {
		if (op == IntLinComb.GEQ || op == IntLinComb.EQ || op == IntLinComb.LEQ) {
			maxNegCoeff.set(0);
			maxPosCoeff.set(cste - 1);
		}
		if (op == IntLinComb.NEQ) {
			//TODO
		}
		if (op == IntLinComb.GEQ || op == IntLinComb.EQ) {
			initUb();
		}
		if (op == IntLinComb.EQ || op == IntLinComb.LEQ) {
			initlb();
		}
		if (op == IntLinComb.EQ) {
			propagateEQ();
		} else if (op == IntLinComb.GEQ) {
			propagateGEQ();
		} else if (op == IntLinComb.LEQ) {
			propagateLEQ();
		} else {
			//TODO
		}
	}

	public void propagateEQ() throws ContradictionException {
		for (int i = 0; i < nbNegCoef; i++) {
			if (ub.get() + sCoeffs[i] < rmemb.getInfRight()) {
				vars[i].instantiate(0, cIndices[i]);
			}
		}
		for (int i = nbNegCoef; i < cste; i++) {
			if (ub.get() - sCoeffs[i] < rmemb.getInfRight()) {
				vars[i].instantiate(1, cIndices[i]);
			}
		}

		for (int i = 0; i < nbNegCoef; i++) {
			if (lb.get() - sCoeffs[i] > rmemb.getSupRight()) {
				vars[i].instantiate(1, cIndices[i]);
			}
		}

		for (int i = nbNegCoef; i < cste; i++) {
			if (lb.get() + sCoeffs[i] > rmemb.getSupRight()) {
				vars[i].instantiate(0, cIndices[i]);
			}
		}

		for (int i = 0; i < cste; i++) {
			if (vars[i].isInstantiated()) {
				updateUbLbOnInst(i, vars[i].getVal());
			}
		}

		fixPointOnEQ();
	}

	public void propagateGEQ() throws ContradictionException {
		for (int i = 0; i < nbNegCoef; i++) {
			if (ub.get() + sCoeffs[i] < rmemb.getInfRight()) {
				vars[i].instantiate(0, cIndices[i]);
			}

			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
		for (int i = nbNegCoef; i < cste; i++) {
			if (ub.get() - sCoeffs[i] < rmemb.getInfRight()) {
				vars[i].instantiate(1, cIndices[i]);
			}

			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
		varCste.updateSup(rmemb.getNewSupForObj(), cIndices[cste]);
		updateForGEQ();
	}

	public void propagateLEQ() throws ContradictionException {
		for (int i = 0; i < nbNegCoef; i++) {
			if (lb.get() - sCoeffs[i] > rmemb.getSupRight()) {
				vars[i].instantiate(1, VarEvent.domOverWDegIdx(cIndices[i]));
			}

			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
		for (int i = nbNegCoef; i < cste; i++) {
			if (lb.get() + sCoeffs[i] > rmemb.getSupRight()) {
				vars[i].instantiate(0, VarEvent.domOverWDegIdx(cIndices[i]));
			}
			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
		varCste.updateInf(rmemb.getNewInfForObj(), cIndices[cste]);
		updateForLEQ();
	}

	public final void initUb() {
		int upb = addcste;
		for (int i = 0; i < sCoeffs.length; i++) {
			if (sCoeffs[i] > 0) {
				upb += sCoeffs[i];
			}
		}
		ub.set(upb);
	}

	public final void initlb() {
		int lpb = addcste;
		for (int i = 0; i < sCoeffs.length; i++) {
			if (sCoeffs[i] < 0) {
				lpb += sCoeffs[i];
			}
		}
		lb.set(lpb);
	}

	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 *
	 * @return true iff the constraint is bound consistent
	 *         (weaker than arc consistent)
	 */
	@Override
	public boolean isConsistent() {
		if (op == IntLinComb.EQ) {
			return (hasConsistentLowerBound() && hasConsistentUpperBound());
		} else if (op == IntLinComb.GEQ) {
			return hasConsistentUpperBound();
		} else if (op == IntLinComb.LEQ) {
			return hasConsistentLowerBound();
		}
		return true;
	}

	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 *
	 * @return true iff the constraint is bound consistent
	 *         (weaker than arc consistent)
	 */
	protected boolean hasConsistentUpperBound() {
		if (ub.get() < rmemb.getInfRight()) {
			return false;
		} else {
			for (int i = 0; i < nbNegCoef; i++) {
				if (ub.get() + vars[i].getSup() * sCoeffs[i] < rmemb.getInfRight()) {
					return false;
				}
			}
			for (int i = nbNegCoef; i < cste; i++) {
				if (ub.get() - vars[i].getInf() * sCoeffs[i] < rmemb.getInfRight()) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 *
	 * @return true iff the constraint is bound consistent
	 *         (weaker than arc consistent)
	 */
	protected boolean hasConsistentLowerBound() {
		if (lb.get() > rmemb.getSupRight()) {
			return false;
		} else {
			for (int i = 0; i < nbNegCoef; i++) {
				if (lb.get() - vars[i].getInf() * sCoeffs[i] > rmemb.getSupRight()) {
					return false;
				}
			}
			for (int i = nbNegCoef; i < cste; i++) {
				if (lb.get() + vars[i].getSup() * sCoeffs[i] > rmemb.getSupRight()) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Checks if the constraint is entailed.
	 *
	 * @return Boolean.TRUE if the constraint is satisfied, Boolean.FALSE if it
	 *         is violated, and null if the filtering algorithm cannot infer yet.
	 */
	@Override
	public Boolean isEntailed() {
		if (op == IntLinComb.EQ) {
			int lb = computeLbFromScratch();
			int ub = computeUbFromScratch();
			int cstelb = objCoef * varCste.getInf();
			int csteub = objCoef * varCste.getSup();
			if (lb > csteub || ub < cstelb) {
				return Boolean.FALSE;
			} else if (lb == ub &&
					varCste.isInstantiated() &&
					objCoef * varCste.getVal() == lb) {
				return Boolean.TRUE;
			} else {
				return null;
			}
		} else if (op == IntLinComb.GEQ) {
			if (computeLbFromScratch() >= rmemb.getSupRight()) {
				return Boolean.TRUE;
			} else if (computeUbFromScratch() < rmemb.getInfRight()) {
				return Boolean.FALSE;
			} else {
				return null;
			}
		} else if (op == IntLinComb.LEQ) {
			if (computeUbFromScratch() <= rmemb.getInfRight()) {
				return Boolean.TRUE;
			} else if (computeLbFromScratch() > rmemb.getSupRight()) {
				return Boolean.FALSE;
			} else {
				return null;
			}
		} else {
			throw new SolverException("NEQ not managed by boolIntLinComb");
		}
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		int exp = 0;
		for (int i = 0; i < cste; i++) {
			exp += tuple[i] * sCoeffs[i];
		}
		if (op == IntLinComb.GEQ) {
			return exp + addcste >= objCoef * tuple[cste];
		} else if (op == IntLinComb.LEQ) {
			return exp + addcste <= objCoef * tuple[cste];
		} else if (op == IntLinComb.EQ) {
			return exp + addcste == objCoef * tuple[cste];
		} else if (op == IntLinComb.NEQ) {
			return exp + addcste != objCoef * tuple[cste];
		} else {
			throw new SolverException("operator unknown for BoolIntLinComb");
		}
	}

	@Override
	public String pretty() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cste; i++) {
			if (i > 0) {
				sb.append(" + ");
			}
			sb.append(sCoeffs[i]).append("*").append(vars[i].pretty());
		}
		sb.append(" + ").append(addcste);
		switch (op) {
		case IntLinComb.GEQ:
			sb.append(" >= ");
			break;
		case IntLinComb.LEQ:
			sb.append(" <= ");
			break;
		case IntLinComb.EQ:
			sb.append(" = ");
			break;
		case IntLinComb.NEQ:
			sb.append(" != ");
			break;
		default:
			sb.append(" ??? ");
		break;
		}
		sb.append(objCoef).append("*").append(varCste.pretty());
		return sb.toString();
	}

	/**
	 * Computes the opposite of this constraint.
	 *
	 * @return a constraint with the opposite semantic  @param solver
	 */
	@Override
	public AbstractSConstraint opposite(Solver solver) {
		IntDomainVar[] bvs = new IntDomainVar[cste];
		System.arraycopy(vars, 0, bvs, 0, cste);
		if (op == IntLinComb.EQ) {
			IntDomainVar[] vs = new IntDomainVar[vars.length];
			System.arraycopy(vars, 0, vs, 0, vars.length);
			int[] coeff = new int[cste + 1];
			System.arraycopy(sCoeffs, 0, coeff, 0, cste);
			coeff[cste] = -objCoef;
			return (AbstractSConstraint) solver.neq(solver.scalar(vs, coeff), -addcste);
			//throw new Error("NEQ not yet implemented in BoolIntLinComb for opposite");
		} else if (op == IntLinComb.NEQ) {
			return new BoolIntLinComb(solver.getEnvironment(), bvs, sCoeffs, varCste, objCoef, addcste, IntLinComb.EQ);
		} else if (op == IntLinComb.GEQ) {
			return new BoolIntLinComb(solver.getEnvironment(), bvs, sCoeffs, varCste, objCoef, addcste + 1, IntLinComb.LEQ);
		} else if (op == IntLinComb.LEQ) {
			return new BoolIntLinComb(solver.getEnvironment(), bvs, sCoeffs, varCste, objCoef, addcste - 1, IntLinComb.GEQ);
		} else {
			throw new SolverException("operator unknown for BoolIntLinComb");
		}
	}

	/**
	 * Computes an upper bound estimate of a linear combination of variables.
	 *
	 * @return the new upper bound value
	 */
	protected final int computeUbFromScratch() {
		int s = addcste;
		int i;
		for (i = 0; i < nbNegCoef; i++) {
			s += (vars[i].getInf() * sCoeffs[i]);
		}
		for (i = nbNegCoef; i < cste; i++) {
			s += (vars[i].getSup() * sCoeffs[i]);
		}
		return s;
	}

	/**
	 * Computes a lower bound estimate of a linear combination of variables.
	 *
	 * @return the new lower bound value
	 */
	protected final int computeLbFromScratch() {
		int s = addcste;
		int i;
		for (i = 0; i < nbNegCoef; i++) {
			s += (vars[i].getSup() * sCoeffs[i]);
		}
		for (i = nbNegCoef; i < cste; i++) {
			s += (vars[i].getInf() * sCoeffs[i]);
		}
		return s;
	}
}
