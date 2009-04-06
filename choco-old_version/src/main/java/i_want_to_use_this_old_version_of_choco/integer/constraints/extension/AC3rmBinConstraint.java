package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Arrays;

/**
 *
 * The AC3rm algorithm. When a support is lost, we first check is the last one
 **/
public class AC3rmBinConstraint extends CspBinConstraint {

	protected int[] currentSupport0;
	protected int[] currentSupport1;

	protected int offset0;
	protected int offset1;

	public AC3rmBinConstraint(IntDomainVar x0, IntDomainVar x1, BinRelation relation) {
		super(x0, x1, relation);
		offset0 = x0.getInf();
		offset1 = x1.getInf();
		currentSupport0 = new int[x0.getSup() - x0.getInf() + 1];
		currentSupport1 = new int[x1.getSup() - x1.getInf() + 1];
		Arrays.fill(currentSupport0, -1);
		Arrays.fill(currentSupport1, -1);
	}

	public Object clone() {
		return new AC3rmBinConstraint(this.v0, this.v1, this.relation);
	}

	// updates the support for all values in the domain of v1, and remove unsupported values for v1
	public void reviseV1() throws ContradictionException {
		DisposableIntIterator itv1 = v1.getDomain().getIterator();
		while (itv1.hasNext()) {
			int y = itv1.next();
			if (!v0.canBeInstantiatedTo(currentSupport1[y - offset1]))
				updateSupportVal1(y);
		}
		itv1.dispose();
	}

	// updates the support for all values in the domain of v0, and remove unsupported values for v0
	public void reviseV0() throws ContradictionException {
		DisposableIntIterator itv0 = v0.getDomain().getIterator();
		while (itv0.hasNext()) {
			int x = itv0.next();
			if (!v1.canBeInstantiatedTo(currentSupport0[x - offset0]))
				updateSupportVal0(x);
		}
		itv0.dispose();
	}

	protected void updateSupportVal0(int x) throws ContradictionException {
		boolean found = false;
		int support = v1.getInf();
		int max2 = v1.getSup();
		if (!relation.isConsistent(x, support)) {
			while (!found && support < max2) {
				support = v1.getDomain().getNextValue(support);
				if (relation.isConsistent(x, support)) found = true;
			}
		} else found = true;
		if (found) {
			currentSupport0[x - offset0] = support;
			currentSupport1[support - offset1] = x;
		} else {
			v0.removeVal(x, cIdx0);
		}
	}

	protected void updateSupportVal1(int y) throws ContradictionException {
		boolean found = false;
		int support = v0.getInf();
		int max1 = v0.getSup();
		if (!relation.isConsistent(support, y)) {
			while (!found && support < max1) {
				support = v0.getDomain().getNextValue(support);
				if (relation.isConsistent(support, y)) found = true;
			}
		} else found = true;
		if (found) {
			currentSupport1[y - offset1] = support;
			currentSupport0[support - offset0] = y;
		} else {
			v1.removeVal(y, cIdx1);
		}
	}

	public void awake() throws ContradictionException {
		DisposableIntIterator itv0 = v0.getDomain().getIterator();
		int support = 0;
		boolean found = false;
		while (itv0.hasNext()) {
			DisposableIntIterator itv1 = v1.getDomain().getIterator();
			int val0 = itv0.next();
			while (itv1.hasNext()) {
				int val1 = itv1.next();
				if (relation.isConsistent(val0, val1)) {
					support = val1;
					found = true;
					break;
				}
			}
			itv1.dispose();
			if (!found) {
				v0.removeVal(val0, cIdx0);
			} else {
				currentSupport0[val0 - offset0] = support;
				currentSupport1[support - offset1] = val0;
			}
			found = false;
		}
		itv0.dispose();
		found = false;
		DisposableIntIterator itv1 = v1.getDomain().getIterator();
		while (itv1.hasNext()) {
			itv0 = v0.getDomain().getIterator();
			int val1 = itv1.next();
			while (itv0.hasNext()) {
				int val0 = itv0.next();
				if (relation.isConsistent(val0, val1)) {
					support = val0;
					found = true;
					break;
				}
			}
			itv0.dispose();
			if (!found) {
				v1.removeVal(val1, cIdx1);
			} else {
				currentSupport1[val1 - offset1] = support;
				currentSupport0[support - offset0] = val1;
			}
			found = false;
		}
		itv1.dispose();
		//propagate();
	}

	public void propagate() throws ContradictionException {
		reviseV0();
		reviseV1();
	}

	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		if (idx == 0)
			reviseV1();
		else
			reviseV0();
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		if (idx == 0)
			reviseV1();
		else
			reviseV0();
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		if (idx == 0)
			reviseV1();
		else
			reviseV0();
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		if (idx == 0)
			reviseV1();
		else
			reviseV0();
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		if (varIndex == 0)
			reviseV1();
		else
			reviseV0();
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) {
			int value = v0.getVal();
			DisposableIntIterator itv1 = v1.getDomain().getIterator();
			while (itv1.hasNext()) {
				int val = itv1.next();
				if (!relation.isConsistent(value, val)) {
					v1.removeVal(val, cIdx1);
				}
			}
			itv1.dispose();
		} else {
			int value = v1.getVal();
			DisposableIntIterator itv0 = v0.getDomain().getIterator();
			while (itv0.hasNext()) {
				int val = itv0.next();
				if (!relation.isConsistent(val, value)) {
					v0.removeVal(val, cIdx0);
				}
			}
			itv0.dispose();
		}
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("AC3rm(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
				append(this.relation.getClass().getSimpleName()).append(")");
		return sb.toString();
	}
}

