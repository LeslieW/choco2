package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 14 avr. 2008
 * Time: 10:54:57
 * To change this template use File | Settings | File Templates.
 */
public class GAC3rmLargeConstraint extends CspLargeConstraint {

	// Last valid supports Last(x_i, val) = supports( (blocks(i) + val) * size )

	protected int[] supports;

	protected int[] blocks;

	// Cardinality
	protected int size;

	// offsets(i) = Min(x_i)
	protected int[] offsets;


	public GAC3rmLargeConstraint(IntDomainVar[] vs, LargeRelation relation) {
		super(vs, relation);

		this.size = vs.length;
		this.blocks = new int[size];
		this.offsets = new int[size];

		int nbElt = 0;

		for (int i = 0; i < size; i++) {
			offsets[i] = vs[i].getInf();
			blocks[i] = nbElt;
			if (!vars[i].hasEnumeratedDomain()) {
				nbElt += 2;
			} else nbElt += vars[i].getSup() - vars[i].getInf() + 1;
		}

		this.supports = new int[nbElt * size];
		Arrays.fill(supports, -1);

	}


	// updates the support for all values in the domain of variable
	// and remove unsupported values for variable
	public void reviseVar(int indexVar) throws ContradictionException {
		int[] currentSupport;
		IntDomain dom = vars[indexVar].getDomain();
		IntIterator itvar = vars[indexVar].getDomain().getIterator();
		int val;
		if (vars[indexVar].hasEnumeratedDomain()) {
			for (val = vars[indexVar].getInf(); val <= vars[indexVar].getSup(); val = dom.getNextValue(val)) {
				val = itvar.next();
				if (!isValid(lastSupport(indexVar, val))) {
					currentSupport = seekNextSupport(indexVar, val);
					if (currentSupport != null) {
						setSupport(indexVar, val, currentSupport);
					} else {
						vars[indexVar].removeVal(val, cIndices[indexVar]);
					}
				}
			}
		} else {
			if (!isValid(lastBoundSupport(indexVar, 0, vars[indexVar].getInf()))) {
				for (val = vars[indexVar].getInf(); val <= vars[indexVar].getSup(); val++) {
					currentSupport = seekNextSupport(indexVar, val);
					if (currentSupport != null) {
						setBoundSupport(indexVar, 0, val, currentSupport);
						break; //stop at the first consistent lower bound !
					}
				}
				vars[indexVar].updateInf(val, cIndices[indexVar]);
			}
			if (!isValid(lastBoundSupport(indexVar, 1, vars[indexVar].getSup()))) {
				for (val = vars[indexVar].getSup(); val >= vars[indexVar].getInf(); val--) {
					currentSupport = seekNextSupport(indexVar, val);
					if (currentSupport != null) {
						setBoundSupport(indexVar, 1, val, currentSupport);
						break; //stop at the first consistent upper bound !
					}
				}
				vars[indexVar].updateSup(val, cIndices[indexVar]);
			}
		}
	}

	// Store Last(x_i, val) = support
	public void setSupport(int indexVar, int value, int[] support) {
		for (int i = 0; i < vars.length; i++) {
			supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i] = support[i];
		}
	}

	// Store Last(x_i, val) = support
	public void setBoundSupport(int indexVar, int idxBound, int value, int[] support) {
		for (int i = 0; i < vars.length; i++) {
			if (!vars[i].hasEnumeratedDomain())
				supports[(blocks[indexVar] + idxBound) * size + i] = support[i];
			else supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i] = support[i];
		}
	}


	// Get Last(x_i, val)
	public int[] getSupport(int indexVar, int value) {
		int[] resultat = new int[size];
		for (int i = 0; i < size; i++) {
			resultat[i] = supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i];
		}
		return resultat;
	}


	// return the support standing for the lower bound
	// of indexVar if idxBound = 0 or upperbound if idxBound = 1
	public int[] getBoundSupport(int indexVar, int idxBound, int value) {
		int[] resultat = new int[size];
		for (int i = 0; i < size; i++) {
			if (!vars[i].hasEnumeratedDomain())
				resultat[i] = supports[(blocks[indexVar] + idxBound) * size + i];
			else resultat[i] = supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i];
		}
		return resultat;
	}


	// Get Last(x_i, val)
	public int[] lastSupport(int indexVar, int value) {
		return getSupport(indexVar, value);
	}

	// return the support standing for the lower bound
	// of indexVar if idxBound = 0 or upperbound if idxBound = 1
	public int[] lastBoundSupport(int indexVar, int idxBound, int value) {
		return getBoundSupport(indexVar, idxBound, value);
	}

	// Is tuple valide ?
	public boolean isValid(int[] tuple) {
		for (int i = 0; i < size; i++)
			if (!vars[i].canBeInstantiatedTo(tuple[i])) return false;
		return true;
	}

	// seek a new support for (variable, value), the smallest tuple greater than currentSupport
	public int[] seekNextSupport(int indexVar, int val) {
		int[] currentSupport = new int[size];
		int k = 0;
		for (int i = 0; i < size; i++) {
			if (i != indexVar)
				currentSupport[i] = vars[i].getInf();
			else currentSupport[i] = val;
		}
		if (relation.isConsistent(currentSupport)) {
			return currentSupport;
		}

		while (k < vars.length) {
			if (k == indexVar) k++;
			if (k < vars.length) {
				if (!vars[k].getDomain().hasNextValue(currentSupport[k])) {
					currentSupport[k] = vars[k].getInf();
					k++;
				} else {
					currentSupport[k] = vars[k].getDomain().getNextValue(currentSupport[k]);

					if ((relation.isConsistent(currentSupport))) {
						return currentSupport;
					}
					k = 0;
				}
			}
		}

		return null;
	}


	public void awake() throws ContradictionException {
		propagate();
	}


	public void propagate() throws ContradictionException {
		for (int i = 0; i < size; i++)
			reviseVar(i);
	}


	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		for (int i = 0; i < size; i++)
			if (idx != i) reviseVar(i);
		if (!vars[idx].hasEnumeratedDomain()) {
			reviseVar(idx);
		}

	}

	public void awakeOnInf(int idx) throws ContradictionException {
		for (int i = 0; i < size; i++)
			if (idx != i) reviseVar(i);
		if (!vars[idx].hasEnumeratedDomain()) {
			reviseVar(idx);
		}
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		for (int i = 0; i < size; i++)
			if (idx != i) reviseVar(i);
		if (!vars[idx].hasEnumeratedDomain()) {
			reviseVar(idx);
		}
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		for (int i = 0; i < size; i++)
			if (idx != i) reviseVar(i);
		if (!vars[idx].hasEnumeratedDomain()) {
			reviseVar(idx);
		}
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		for (int i = 0; i < size; i++)
			if (varIndex != i) reviseVar(i);
		if (!vars[varIndex].hasEnumeratedDomain()) {
			reviseVar(varIndex);
		}
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		for (int i = 0; i < size; i++)
			if (idx != i) reviseVar(i);
		if (!vars[idx].hasEnumeratedDomain()) {
			reviseVar(idx);
		}
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("GAC2001Large({");
		for (int i = 0; i < vars.length; i++) {
			if (i > 0) sb.append(", ");
			IntDomainVar var = vars[i];
			sb.append(var.pretty());
		}
		sb.append("})");
		return sb.toString();
	}

}
