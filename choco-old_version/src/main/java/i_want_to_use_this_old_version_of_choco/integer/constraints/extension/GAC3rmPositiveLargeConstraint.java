package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 9 avr. 2008
 * Time: 15:11:55
 * To change this template use File | Settings | File Templates.
 */
public class GAC3rmPositiveLargeConstraint extends CspLargeConstraint {

	protected int[] supports;

	protected int[] blocks;

	protected int arity;

	protected int[] offsets;

	protected static final int NO_SUPPORT = Integer.MAX_VALUE;
	protected static final int INIT_VALUE = -1;

	protected IterIndexedTuplesTable relation;

	public GAC3rmPositiveLargeConstraint(IntDomainVar[] vs, IterIndexedLargeRelation relation) {
		super(vs, null);
		this.relation = (IterIndexedTuplesTable) relation;
		this.arity = vs.length;
		this.blocks = new int[arity];
		this.offsets = new int[arity];

		int nbElt = 0;

		for (int i = 0; i < arity; i++) {
			offsets[i] = vs[i].getInf();
			blocks[i] = nbElt;
			nbElt += vs[i].getDomainSize();
		}
		this.supports = new int[nbElt];
		Arrays.fill(supports, INIT_VALUE);
	}


	public void propagate() throws ContradictionException {
		//System.out.println("propagate");
		for (int indexVar = 0; indexVar < arity; indexVar++)
			reviseVar(indexVar);
		//System.out.println("done");
	}

	public void awake() throws ContradictionException {
		propagate();
	}

	// updates the support for all values in the domain of variable
	// and remove unsupported values for variable
	public void reviseVar(int indexVar) throws ContradictionException {
		IntIterator itv = vars[indexVar].getDomain().getIterator();
		while (itv.hasNext()) {
			int val = itv.next();
			int currentIdxSupport = seekNextSupport(indexVar, val);
			if (currentIdxSupport == NO_SUPPORT) {
				//System.out.println("remove " + val + " from " + vars[indexVar]);
				vars[indexVar].removeVal(val, cIndices[indexVar]);
			} else {
				setSupport(indexVar, val, currentIdxSupport);
			}
		}
	}


	// seek a new support for (variable, value), the smallest tuple greater than currentSupport
	public int seekNextSupport(int indexVar, int value) {
		int currentIdxSupport = getSupport(indexVar, value);
		int[] currentSupport = null;
		if (currentIdxSupport != INIT_VALUE) {
			currentSupport = relation.getTuple(currentIdxSupport);
			if (isValid(currentSupport)) return currentIdxSupport;
		}
		int[][][] tab = ((IterIndexedTuplesTable) relation).getTableLists();
		int nva = value - relation.getRelationOffset(indexVar);
		for (int i = 0; i < relation.getNbSupport(indexVar, nva); i++) {
			currentIdxSupport = tab[indexVar][nva][i];
			currentSupport = relation.getTuple(currentIdxSupport);
			if (isValid(currentSupport)) return currentIdxSupport;
		}
		return NO_SUPPORT;
	}


	//  ACCESSORS...

	public void setSupport(int indexVar, int value, int idxSupport) {
		supports[blocks[indexVar] + value - offsets[indexVar]] = idxSupport;
	}

	// Get Last(x_i, val)
	public int getSupport(int indexVar, int value) {
		return supports[blocks[indexVar] + value - offsets[indexVar]];
	}

	// Is tuple valide ?
	public boolean isValid(int[] tuple) {
		if (tuple == null) return false;
		for (int i = 0; i < arity; i++)
			if (!vars[i].canBeInstantiatedTo(tuple[i])) return false;
		return true;
	}

	// REACTION TO EVENTS

	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		for (int i = 0; i < arity; i++)
			if (idx != i) reviseVar(i);
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		for (int i = 0; i < arity; i++)
			if (idx != i) reviseVar(i);
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		for (int i = 0; i < arity; i++)
			if (idx != i) reviseVar(i);
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		for (int i = 0; i < arity; i++)
			if (idx != i) reviseVar(i);
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		for (int i = 0; i < arity; i++)
			if (varIndex != i) reviseVar(i);
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		for (int i = 0; i < arity; i++)
			if (idx != i) reviseVar(i);
	}


}
