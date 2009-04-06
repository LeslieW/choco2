package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * A constraint to enforce BoundConsistency on a global cardinality
 * based on the implementation of :
 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
 * An efficient bounds consistency algorithm for the global cardinality constraint. CP-2003.
 */
public class BoundGcc extends BoundGccVar {

	protected int[] maxOccurrences;
	protected int[] minOccurrences;

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
	public BoundGcc(IntDomainVar[] vars,
	                int firstDomainValue,
	                int lastDomainValue,
	                int[] minOccurrences,
	                int[] maxOccurrences) {
		super(vars, null, firstDomainValue, lastDomainValue);
		this.maxOccurrences = maxOccurrences;
		this.minOccurrences = minOccurrences;
		l = new PartialSum(firstDomainValue, range, minOccurrences);
		u = new PartialSum(firstDomainValue, range, maxOccurrences);

	}

	public int getMaxOcc(int i) {
		return maxOccurrences[i];
	}

	public int getMinOcc(int i) {
		return minOccurrences[i];
	}


	public void updateSup(IntDomainVar v, int nsup, int idx) throws ContradictionException {
		v.updateSup(nsup, cIndices[idx]);
	}

	public void updateInf(IntDomainVar v, int ninf, int idx) throws ContradictionException {
		v.updateInf(ninf, cIndices[idx]);
	}


	public void awake() throws ContradictionException {
		for (int i = 0; i < vars.length; i++) {
			if (vars[i].isInstantiated())
				awakeOnInst(i);
		}
		propagate();
	}


	public void propagate() throws ContradictionException {
		Solver.flushLogs();
		sortIt();
		if ((l.sum(l.minValue(), minsorted[0].var.getInf() - 1) > 0) ||
				(l.sum(maxsorted[getNbVars() - 1].var.getSup() + 1, l.maxValue()) > 0)) {
			this.fail();
		}
		//System.out.println("Filter lower !");
		filterLowerMax();
		filterLowerMin();
		//System.out.println("Filter upper");
		filterUpperMax();
		filterUpperMin();
	}

	public void awakeOnInf(int i) throws ContradictionException {
		this.constAwake(false);
		if (!vars[i].hasEnumeratedDomain()) {
			filterBCOnInf(i);
		}
	}

	public void awakeOnSup(int i) throws ContradictionException {
		this.constAwake(false);
		if (!vars[i].hasEnumeratedDomain()) {
			filterBCOnSup(i);
		}
	}

	public void awakeOnInst(int i) throws ContradictionException {   // Propagation classique
		int val = vars[i].getVal();
		constAwake(false);
		// if a value has been instantiated to its max number of occurrences
		// remove it from all variables
		filterBCOnInst(val);
	}

	public void awakeOnRem(int idx, int val) throws ContradictionException {
		filterBCOnRem(val);
	}

	public boolean isSatisfied() {
		int[] occurrences = new int[this.range];
    for (int i = 0; i < vars.length; i++) {
      IntDomainVar var = vars[i];
      occurrences[var.getVal()-this.offset]++;
    }
    for (int i = 0; i < occurrences.length; i++) {
      int occurrence = occurrences[i];
      if ((this.minOccurrences[i] > occurrence) || (occurrence > this.maxOccurrences[i]))
        return false;
    }
    return true;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("BoundGcc({");
    for (int i = 0; i < vars.length; i++) {
        if (i > 0) sb.append(", ");
        IntDomainVar var = vars[i];
        sb.append(var.pretty());
      }
    sb.append("}, {");
    for (int i = 0; i < minOccurrences.length; i++) {
      if (i > 0) sb.append(", ");
      int minOccurrence = minOccurrences[i];
      int maxOccurrence = maxOccurrences[i];
      sb.append(minOccurrence).append(" <= #").append(this.offset + i).append(" <= ").append(maxOccurrence);
    }
    sb.append("})");
    return sb.toString();
  }

  public Boolean isEntailed() {
		throw new Error("isEntailed not yet implemented on package i_want_to_use_this_old_version_of_choco.global.BoundAlldiff");
	}

}
