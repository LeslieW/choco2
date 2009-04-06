package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A constraint stating that two sets have to be disjoint :
 * It is based on two propagation rules :
 * Env(v1) disjoint Ker(v2)
 * Env(v2) disjoint Ker(v1)
 */
public class Disjoint extends AbstractBinSetConstraint {

	public Disjoint(SetVar sv1, SetVar sv2) {
		v0 = sv1;
		v1 = sv2;
	}

	public void filter(int idx) throws ContradictionException {
		if (idx == 0) {
			IntIterator it1 = v0.getDomain().getKernelIterator();
			while (it1.hasNext()) {
				v1.remFromEnveloppe(it1.next(), cIdx1);
			}
		} else if (idx == 1) {
			IntIterator it2 = v1.getDomain().getKernelIterator();
			while (it2.hasNext()) {
				v0.remFromEnveloppe(it2.next(), cIdx0);
			}
		}
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			v1.remFromEnveloppe(x, cIdx1);
		} else
			v0.remFromEnveloppe(x, cIdx0);
	}

	public void awakeOnEnvRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		//Nothing to do
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		filter(varIdx);
	}

	public void propagate() throws ContradictionException {
		filter(0);
		filter(1);
	}

	public boolean isSatisfied() {
		IntIterator it2 = v1.getDomain().getKernelIterator();
		while (it2.hasNext()) {
			if (v0.isInDomainKernel(it2.next())) return false;
		}
		return true;
	}

	public boolean isConsistent() {
		return isSatisfied();
	}

	public String toString() {
		return v0 + " disjoint " + v1;
	}

	public String pretty() {
		return v0.pretty() + " disjoint " + v1.pretty();
	}

	public Boolean isEntailed() {
		boolean someSureIn = false, somePossibleIn = false;
		IntIterator it1 = v0.getDomain().getEnveloppeIterator();
		while (it1.hasNext()) {
			int val = it1.next();
			if (v1.isInDomainEnveloppe(val)) {
				if (v0.isInDomainKernel(val) && v1.isInDomainKernel(val)) {
					someSureIn = true;
					break;
				}
				somePossibleIn = true;
			}
		}
		if (someSureIn)
			return Boolean.FALSE;
		else if (!somePossibleIn)
			return Boolean.TRUE;
		else
			return null;
	}
}
