package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * A constraint to state |x0 - x1| operator x2 + c
 * where operator can be =, <=, >= and x1, x2, x3 are variables
 * Warning: only achieves BoundConsistency for the moment !
 **/
public class DistanceXYZ extends AbstractTernIntConstraint {

	protected int operator;

	protected final int cste;

	protected final static int EQ = 0;

	protected final static int LT = 1;

	protected final static int GT = 2;

	/**
	 * Enforces |x0 - x1| op x2 + c
	 * where op can be =, <=, >=
	 * @param x0
	 * @param x1
	 * @param x2
	 * @param c the constant
	 * @param op the operator to be chosen among {0,1,2} standing for (eq,leq,geq)
	 */
	public DistanceXYZ(IntDomainVar x0, IntDomainVar x1, IntDomainVar x2, int c, int op) {
		super(x0, x1, x2);
		cste = c;
		operator = op;
	}

	//*************************************************************//
	//********************** Bounds on Z **************************//
	//*************************************************************//

	//update lower bound of v2 if we have v0 != v1
	public boolean filterFromXYtoLBZ() throws ContradictionException {
		if (v1.getInf() - v0.getSup() > 0) { // x < y
			return v2.updateInf(v1.getInf() - v0.getSup() - cste,cIdx2);
		} else if (v0.getInf() - v1.getSup() > 0) { // x > y
			return v2.updateInf(v0.getInf() - v1.getSup() - cste,cIdx2);
		} else return false;
	}

	//update upper bound of v2 as max(|v1.sup - v0.inf|, |v1.inf - v0.sup|)
	public boolean filterFromXYtoUBZ() throws ContradictionException {
		int a = Math.abs(v1.getSup() - v0.getInf());
		int b = Math.abs(v0.getSup() - v1.getInf());
		return v2.updateSup(Math.max(a,b) - cste,cIdx2);
	}

	//*************************************************************//
	//********************** EQ on XY *****************************//
	//*************************************************************//

	public boolean filterEQFromYZToX() throws ContradictionException {
		int lb = v1.getInf() - v2.getSup() - cste;
		int ub = v1.getSup() + v2.getSup() + cste;
		int lbv0 = v1.getSup() - v2.getInf() - cste + 1;
		int ubv0 = v1.getInf() + v2.getInf() + cste - 1;
		return 	v0.updateInf(lb,cIdx0) ||
				v0.updateSup(ub,cIdx0) ||
				v0.removeInterval(lbv0, ubv0, cIdx0);
	}

	public boolean filterEQFromXZToY() throws ContradictionException {
		int lb = v0.getInf() - v2.getSup() - cste;
		int ub = v0.getSup() + v2.getSup() + cste;
		int lbv1 = v0.getSup() - v2.getInf() - cste + 1;
		int ubv1 = v0.getInf() + v2.getInf() + cste - 1;
		return 	v1.updateInf(lb,cIdx1) ||
				v1.updateSup(ub,cIdx1) ||
				v1.removeInterval(lbv1, ubv1, cIdx1);
	}

	//*************************************************************//
	//********************** LT on XY *****************************//
	//*************************************************************//

	// LEQ: update x from the domain of z and y
	public boolean filterLTFromYZtoX() throws ContradictionException {
		int lb = v1.getInf() - v2.getSup() - cste + 1;
		int ub = v1.getSup() + v2.getSup() + cste - 1;
		return 	v0.updateInf(lb,cIdx0) || v0.updateSup(ub,cIdx0);
	}

	// LEQ: update x from the domain of z and y
	public boolean filterLTFromXZtoY() throws ContradictionException {
		int lb = v0.getInf() - v2.getSup() - cste + 1;
		int ub = v0.getSup() + v2.getSup() + cste - 1;
		return 	v1.updateInf(lb,cIdx1) || v1.updateSup(ub,cIdx1);
	}

	//*************************************************************//
	//********************** GT on XY *****************************//
	//*************************************************************//

	// GEQ: remove interval for x from the domain of z and y
	public boolean filterGTFromYZtoX() throws ContradictionException {
		int lbv0 = v1.getSup() - v2.getInf() - cste;
		int ubv0 = v1.getInf() + v2.getInf() + cste;
		// remove interval [lbv0, ubv0] from domain of v0
		return v0.removeInterval(lbv0, ubv0, cIdx0);
	}

	// GEQ: remove interval for y from the domain of x and y
	public boolean filterGTFromXZtoY() throws ContradictionException {
		int lbv1 = v0.getSup() - v2.getInf() - cste;
		int ubv1 = v0.getInf() + v2.getInf() + cste;
		// remove interval [lbv0, ubv0] from domain of v0
		return v1.removeInterval(lbv1, ubv1, cIdx1);
	}

	//*************************************************************//
	//********************** main filter **************************//
	//*************************************************************//

	public void filterFixPoint() throws ContradictionException {
		boolean change = true;
		while (change) {
			change = false;
			if (operator == EQ) {
			   change |= filterFromXYtoLBZ();
			   change |= filterFromXYtoUBZ();
			   change |= filterEQFromXZToY();
			   change |= filterEQFromYZToX();
			} else if (operator == LT) {
				change |= filterFromXYtoLBZ();
				change |= filterLTFromXZtoY();
				change |= filterLTFromYZtoX();
			} else { //GEQ
				change |= filterFromXYtoUBZ();
				change |= filterGTFromXZtoY();
				change |= filterGTFromYZtoX();
			}
		}
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		filterFixPoint();
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		filterFixPoint();
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		filterFixPoint();
	}


	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		//do nothing on removals for BC
	}

	public void propagate() throws ContradictionException {
		v2.updateInf(-cste,cIdx2);
		filterFixPoint();
	}

	public String toString() {
		String op;
		if (operator == EQ) op = "=";
		else if (operator == GT) op = ">";
		else if (operator == LT) op = "<";
		else throw new Error("unknown operator");
		return "|" + v0 + " - " + v1 + "| " + op + " " + v2 + " + " + cste;
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("| ").append(v0.pretty()).append(" - ").append(v1.pretty()).append(" | ");
		switch (operator) {
			case EQ:
				sb.append("=");
				break;
			case GT:
				sb.append(">");
				break;
			case LT:
				sb.append("<");
				break;
			default:
				sb.append("???");
				break;
		}
		sb.append(v2 + " + " + cste);
		return sb.toString();
	}

	public Boolean isEntailed() {
		throw new Error("isEntailed not yet implemented on DistanceXYC constraint");
	}

	public boolean isSatisfied(int[] tuple) {
		if (operator == EQ)
			return Math.abs(tuple[0] - tuple[1]) == tuple[2] + cste;
		else if (operator == LT)
			return Math.abs(tuple[0] - tuple[1]) <= tuple[2] + cste;
		else if (operator == GT)
			return Math.abs(tuple[0] - tuple[1]) >= tuple[2] + cste;
		else
			throw new Error("operator not known");
	}


}
