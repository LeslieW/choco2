package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.mem.IStateBitSet;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

/**
 * User: hcambaza
 * Bound Global cardinality : Given an array of variables vars, an array of variables card to represent the cardinalities, the constraint ensures that the number of occurences
 * of the value i among the variables is equal to card[i].
 * this constraint enforces :
 * - Bound Consistency over vars regarding the lower and upper bounds of cards
 * - maintain the upperbound of card by counting the number of variables in which each value
 * can occur
 * - maintain the lowerbound of card by counting the number of variables instantiated to a value
 * - enforce card[0] + ... + card[m] = n (n = the number of variables, m = number of values)
 */
public class BoundGccVar extends AbstractLargeIntConstraint {

	int[] t; // Tree links
	int[] d; // Diffs between critical capacities
	int[] h; // Hall interval links
	int[] bounds;

	int[] stableInterval;
	int[] potentialStableSets;
	int[] newMin;

	int offset = 0;

	int nbBounds;
	int nbVars;  //number of variables (without the cardinalities variables)
	IntDomainVar[] card;

	Interval[] intervals;
	Interval[] minsorted;
	Interval[] maxsorted;

	PartialSum l;
	PartialSum u;

	boolean infBoundModified = true;
	boolean supBoundModified = true;

	int firstValue, range;

	//desynchornized copy of domains to make sure we properly counting
	//the number of variables that still have value i in their domain
	//(table val_maxOcc)
	IStateBitSet[] deSynchronizedDomain;
	IStateInt[] val_maxOcc;
	int[] offsets;

	public static IntDomainVar[] makeVarTable(IntDomainVar[] vars,
	                                          IntDomainVar[] card) {
		if (card != null) {
			IntDomainVar[] allvars = new IntDomainVar[vars.length + card.length];
			System.arraycopy(vars, 0, allvars, 0, vars.length);
			System.arraycopy(card, 0, allvars, vars.length, card.length);
			return allvars;
		} else return vars;
	}

	/**
	 * Bound Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables (or a table IntDomainVar to represent the cardinalities), the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 * Use the propagator of :
	 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
	 * An efficient bounds consistency algorithm for the global cardinality constraint.
	 * CP-2003.
	 */
	public BoundGccVar(IntDomainVar[] vars,
	                   IntDomainVar[] card,
	                   int firstDomainValue,
	                   int lastDomainValue) {
		super(makeVarTable(vars, card));
		this.card = card;
		build(vars.length, firstDomainValue, lastDomainValue);
	}

	public void build(int n,
	                  int firstDomainValue,
	                  int lastDomainValue) {
		int range = lastDomainValue - firstDomainValue + 1;
		this.nbVars = n;
		t = new int[2 * n + 2];
		d = new int[2 * n + 2];
		h = new int[2 * n + 2];
		bounds = new int[2 * n + 2];
		stableInterval = new int[2 * n + 2];
		potentialStableSets = new int[2 * n + 2];
		newMin = new int[n];

		intervals = new Interval[n];
		minsorted = new Interval[n];
		maxsorted = new Interval[n];

		for (int i = 0; i < nbVars; i++) {
			intervals[i] = new Interval();
			intervals[i].var = vars[i];
			intervals[i].idx = i;
			minsorted[i] = intervals[i];
			maxsorted[i] = intervals[i];
		}
		this.offset = firstDomainValue;
		this.firstValue = firstDomainValue;
		this.range = range;
	}

	public int getMaxOcc(int i) {
		return card[i].getSup();
	}

	public int getMinOcc(int i) {
		return card[i].getInf();
	}

	public void updateSup(IntDomainVar v, int nsup, int idx) throws ContradictionException {
		v.updateSup(nsup, cIndices[idx]);
	}

	public void updateInf(IntDomainVar v, int ninf, int idx) throws ContradictionException {
		v.updateInf(ninf, cIndices[idx]);
	}

	//factorize this code with the boundalldiff
	protected void sortmin() {
		boolean sorted = false;
		int current = nbVars - 1;
		while (!sorted) {
			sorted = true;
			for (int i = 0; i < current; i++) {
				if (minsorted[i].var.getInf() > minsorted[i + 1].var.getInf()) {
					Interval t = minsorted[i];
					minsorted[i] = minsorted[i + 1];
					minsorted[i + 1] = t;
					sorted = false;
				}
			}
			current--;
		}
	}

	//factorize this code with the boundalldiff
	protected void sortmax() {
		boolean sorted = false;
		int current = 0;
		while (!sorted) {
			sorted = true;
			for (int i = nbVars - 1; i > current; i--) {
				if (maxsorted[i].var.getSup() < maxsorted[i - 1].var.getSup()) {
					Interval t = maxsorted[i];
					maxsorted[i] = maxsorted[i - 1];
					maxsorted[i - 1] = t;
					sorted = false;
				}
			}
			current++;
		}
	}

	protected void sortIt() {
		this.sortmin();
		this.sortmax();

		int min = minsorted[0].var.getInf();
		int max = maxsorted[0].var.getSup() + 1;
		int last = l.firstValue + 1; //change here compared to the boundalldiff
		int nb = 0;
		bounds[0] = last;

		int i = 0, j = 0;
		while (true) {
			if (i < nbVars && min <= max) {
				if (min != last)
					bounds[++nb] = last = min;
				minsorted[i].minrank = nb;
				if (++i < nbVars)
					min = minsorted[i].var.getInf();
			} else {
				if (max != last)
					bounds[++nb] = last = max;
				maxsorted[j].maxrank = nb;
				if (++j == nbVars) break;
				max = maxsorted[j].var.getSup() + 1;
			}
		}

		this.nbBounds = nb;
		bounds[nb + 1] = u.lastValue + 1; //change here compared to the boundalldiff
	}

	protected void pathset(int[] tab, int start, int end, int to) {
		int next = start;
		int prev = next;

		while (prev != end) {
			next = tab[prev];
			tab[prev] = to;
			prev = next;
		}
	}

	protected int pathmin(int[] tab, int i) {
		while (tab[i] < i) {
			i = tab[i];
		}
		return i;
	}

	protected int pathmax(int[] tab, int i) {
		while (tab[i] > i) {
			i = tab[i];
		}
		return i;
	}

	/**
	 * Shrink the lower bounds for the max occurences
	 *
	 * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
	 */
	protected void filterLowerMax() throws ContradictionException {
		int i, j, w, x, y, z;

		for (i = 1; i <= nbBounds + 1; i++) {
			t[i] = h[i] = i - 1;
			d[i] = u.sum(bounds[i - 1], bounds[i] - 1);
		}
		for (i = 0; i < nbVars; i++) { // visit intervals in increasing max order
			// get interval bounds
			x = maxsorted[i].minrank;
			y = maxsorted[i].maxrank;
			j = t[z = pathmax(t, x + 1)];
			if (--d[z] == 0) {
				t[z = pathmax(t, t[z] = z + 1)] = j;
			}
			pathset(t, x + 1, z, z);
			if (d[z] < u.sum(bounds[y], bounds[z] - 1)) {
				this.fail();
			}
			if (h[x] > x) {
				w = pathmax(h, h[x]);
				updateInf(maxsorted[i].var, bounds[w], maxsorted[i].idx);
				pathset(h, x, w, w);
			}
			if (d[z] == u.sum(bounds[y], bounds[z] - 1)) {
				pathset(h, h[y], j - 1, y); // mark hall interval
				h[y] = j - 1; //("hall interval [%d,%d)\n",bounds[j],bounds[y]);
			}
		}
	}

	/**
	 * Shrink the upper bounds for the max occurences
	 *
	 * @throws ContradictionException
	 */
	protected void filterUpperMax() throws ContradictionException {
		int i, j, w, x, y, z;

		for (i = 0; i <= nbBounds; i++) {
			d[i] = u.sum(bounds[i], bounds[t[i] = h[i] = i + 1] - 1);
		}
		for (i = nbVars; --i >= 0;) { // visit intervals in decreasing min order
			// get interval bounds
			x = minsorted[i].maxrank;
			y = minsorted[i].minrank;
			j = t[z = pathmin(t, x - 1)];
			if (--d[z] == 0) {
				t[z = pathmin(t, t[z] = z - 1)] = j;
			}
			pathset(t, x - 1, z, z);
			if (d[z] < u.sum(bounds[z], bounds[y] - 1)) {
				this.fail();
			}
			if (h[x] < x) {
				w = pathmin(h, h[x]);
				updateSup(minsorted[i].var, bounds[w] - 1, minsorted[i].idx);
				pathset(h, x, w, w);
			}
			if (d[z] == u.sum(bounds[z], bounds[y] - 1)) {
				pathset(h, h[y], j + 1, y);
				h[y] = j + 1;
			}
		}
	}

	/*
	 * Shrink the lower bounds for the min occurrences problem.
	 * called as: filterLowerMin(t, d, h, stableInterval, potentialStableSets, newMin);
	 */
	public void filterLowerMin() throws ContradictionException {
		int i, j, w, x, y, z, v;

		for (w = i = nbBounds + 1; i > 0; i--) {
			potentialStableSets[i] = stableInterval[i] = i - 1;
			d[i] = l.sum(bounds[i - 1], bounds[i] - 1);
			// If the capacity between both bounds is zero, we have
			// an unstable set between these two bounds.
			if (d[i] == 0) {
				h[i - 1] = w;
			} else {
				w = h[w] = i - 1;
			}
		}

		for (i = w = nbBounds + 1; i >= 0; i--) {
			if (d[i] == 0)
				t[i] = w;
			else
				w = t[w] = i;
		}

		for (i = 0; i < nbVars; i++) { // visit intervals in increasing max order
			// Get interval bounds
			x = maxsorted[i].minrank;
			y = maxsorted[i].maxrank;
			j = t[z = pathmax(t, x + 1)];
			if (z != x + 1) {
				// if bounds[z] - 1 belongs to a stable set,
				// [bounds[x], bounds[z]) is a sub set of this stable set
				v = potentialStableSets[w = pathmax(potentialStableSets, x + 1)];
				pathset(potentialStableSets, x + 1, w, w); // path compression
				w = y < z ? y : z;
				pathset(potentialStableSets, potentialStableSets[w], v, w);
				potentialStableSets[w] = v;
			}

			if (d[z] <= l.sum(bounds[y], bounds[z] - 1)) {
				// (potentialStableSets[y], y] is a stable set
				w = pathmax(stableInterval, potentialStableSets[y]);
				pathset(stableInterval, potentialStableSets[y], w, w); // Path compression
				pathset(stableInterval, stableInterval[y], v = stableInterval[w], y);
				stableInterval[y] = v;
			} else {
				// Decrease the capacity between the two bounds
				if (--d[z] == 0) {
					t[z = pathmax(t, t[z] = z + 1)] = j;
				}

				// If the lower bound belongs to an unstable or a stable set,
				// remind the new value we might assigned to the lower bound
				// in case the variable doesn't belong to a stable set.
				if (h[x] > x) {
					w = newMin[i] = pathmax(h, x);
					pathset(h, x, w, w); // path compression
				} else {
					newMin[i] = x; // Do not shrink the variable
				}

				// If an unstable set is discovered
				if (d[z] == l.sum(bounds[y], bounds[z] - 1)) {
					if (h[y] > y) // Consider stable and unstable sets beyong y
						y = h[y]; // Equivalent to pathmax since the path is fully compressed
					pathset(h, h[y], j - 1, y); // mark the new unstable set
					h[y] = j - 1;
				}
			}
			pathset(t, x + 1, z, z); // path compression
		}

		// If there is a failure set
		if (h[nbBounds] != 0) {
			this.fail();
		}

		// Perform path compression over all elements in
		// the stable interval data structure. This data
		// structure will no longer be modified and will be
		// accessed n or 2n times. Therefore, we can afford
		// a linear time compression.
		for (i = nbBounds + 1; i > 0; i--) {
			if (stableInterval[i] > i)
				stableInterval[i] = w;
			else
				w = i;
		}

		// For all variables that are not a subset of a stable set, shrink the lower bound
		for (i = nbVars - 1; i >= 0; i--) {
			x = maxsorted[i].minrank;
			y = maxsorted[i].maxrank;
			if ((stableInterval[x] <= x) || (y > stableInterval[x])) {
				updateInf(maxsorted[i].var, l.skipNonNullElementsRight(bounds[newMin[i]]), maxsorted[i].idx);
			}
		}
	}

	/*
    * Shrink the upper bounds for the min occurrences problem.
    * called as: filterUpperMin(t, d, h, stableInterval, newMin);
    */
	public void filterUpperMin() throws ContradictionException {
		int i, w = 0, n = nbVars;
		for (i = 0; i <= nbBounds; i++) {
			d[i] = l.sum(bounds[i], bounds[i + 1] - 1);
			if (d[i] == 0)
				t[i] = w;
			else
				w = t[w] = i;
		}
		t[w] = i;
		w = 0;
		for (i = 1; i <= nbBounds; i++) {
			if (d[i - 1] == 0)
				h[i] = w;
			else
				w = h[w] = i;
		}
		h[w] = i;
		for (i = n - 1; i >= 0; i--) { // visit intervals in decreasing min order
			// Get interval bounds
			int x = minsorted[i].maxrank;
			int y = minsorted[i].minrank;

			// Solve the lower bound problem
			int z = pathmin(t, x - 1);
			int j = t[z];

			// If the variable is not in a discovered stable set
			// Possible optimization: Use the array stableInterval to perform this test
			if (d[z] > l.sum(bounds[z], bounds[y] - 1)) {
				if (--d[z] == 0) {
					t[z] = z - 1;
					z = pathmin(t, t[z]);
					t[z] = j;
				}
				if (h[x] < x) {
					w = pathmin(h, h[x]);
					newMin[i] = w;       // re-use the table newMin to store the max
					pathset(h, x, w, w); // path compression
				} else {
					newMin[i] = x;
				}
				if (d[z] == l.sum(bounds[z], bounds[y] - 1)) {
					if (h[y] < y) {
						y = h[y];
					}
					pathset(h, h[y], j + 1, y);
					h[y] = j + 1;
				}
			}
			pathset(t, x - 1, z, z);
		}
		// For all variables that are not subsets of a stable set, shrink the upper bound
		for (i = n - 1; i >= 0; i--) {
			int x = minsorted[i].minrank;
			int y = minsorted[i].maxrank;
			if ((stableInterval[x] <= x) || (y > stableInterval[x]))
				updateSup(minsorted[i].var, l.skipNonNullElementsLeft(bounds[newMin[i]] - 1), minsorted[i].idx);
		}

	}

	public void initBackDataStruct() {
		deSynchronizedDomain = new IStateBitSet[nbVars];
		val_maxOcc = new IStateInt[nbVars];
		offsets = new int[nbVars];
		for (int i = 0; i < nbVars; i++) {
			deSynchronizedDomain[i] = problem.getEnvironment().makeBitSet(range, true);
			for (int j = 0; j < range; j++) {
				if (vars[i].canBeInstantiatedTo(j + offset))
					deSynchronizedDomain[i].set(j);
			}
			offsets[i] = vars[i].getInf();
		}
		for (int i = 0; i < range; i++) {
			val_maxOcc[i] = problem.getEnvironment().makeInt(0);
			for (int j = 0; j < nbVars; j++) {
				if (vars[j].canBeInstantiatedTo(i + offset))
					val_maxOcc[i].add(1);
			}
		}
	}


	public void awake() throws ContradictionException {
		initBackDataStruct();
		for (int i = 0; i < vars.length; i++) {
			if (vars[i].isInstantiated())
				awakeOnInst(i);
		}
		propagate();
	}

	public void dynamicInitOfPartialSum() {
		int[] minOccurrences = new int[range];//todo: maintain an accurate range
		int[] maxOccurrences = new int[range];

		for (int i = 0; i < range; i++) {
			maxOccurrences[i] = card[i].getSup();
			minOccurrences[i] = card[i].getInf();
		}
		l = new PartialSum(firstValue, range, minOccurrences);
		u = new PartialSum(firstValue, range, maxOccurrences);
	}

	public void propagate() throws ContradictionException {
		propagateSumCard();
		dynamicInitOfPartialSum();
		sortIt();
		if ((l.sum(l.minValue(), minsorted[0].var.getInf() - 1) > 0) ||
				(l.sum(maxsorted[nbVars - 1].var.getSup() + 1, l.maxValue()) > 0)) {
			this.fail();
		}
		//System.out.println("Filter lower !");
		filterLowerMax();
		filterLowerMin();
		//System.out.println("Filter upper");
		filterUpperMax();
		filterUpperMin();
		//System.out.println("Finished !");
	}


	public void awakeOnInf(int i) throws ContradictionException {
		this.constAwake(false);
		if (i < nbVars) {
			int newInf = vars[i].getInf() - offsets[i];
			int prevInf = deSynchronizedDomain[i].nextSetBit(0);
			for (int j = prevInf; j < newInf && j >= 0; j = deSynchronizedDomain[i].nextSetBit(j + 1)) {
				deSynchronizedDomain[i].clear(j);
				val_maxOcc[j].add(-1);
				card[j].updateSup(val_maxOcc[j].get(), -1);
			}
			if (!vars[i].hasEnumeratedDomain()) {
				filterBCOnInf(i);
			}
		}
	}

	//in case of bound variables, the bound has to be checked
	public void filterBCOnInf(int i) throws ContradictionException {
		int inf = vars[i].getInf();
		int nbInf = 0;
		for (int j = 0; j < nbVars; j++) {
			if (i != j && vars[j].isInstantiatedTo(inf)) nbInf++;
		}
		if (nbInf == getMaxOcc(inf - offset)) {
			vars[i].updateInf(inf + 1, -1);
		}
	}

	public void awakeOnSup(int i) throws ContradictionException {
		this.constAwake(false);
		if (i < nbVars) {
			int newSup = vars[i].getSup() - offsets[i];
			int prevSup = deSynchronizedDomain[i].prevSetBit(range);
			for (int j = prevSup; j > newSup && j >= 0; j = deSynchronizedDomain[i].prevSetBit(j - 1)) {
				deSynchronizedDomain[i].clear(j);
				val_maxOcc[j].add(-1);
				card[j].updateSup(val_maxOcc[j].get(), -1);
			}
			if (!vars[i].hasEnumeratedDomain()) {
				filterBCOnSup(i);
			}
		}
	}

	//in case of bound variables, the bound has to be checked
	public void filterBCOnSup(int i) throws ContradictionException {
		int sup = vars[i].getSup();
		int nbSup = 0;
		for (int j = 0; j < nbVars; j++) {
			if (i != j && vars[j].isInstantiatedTo(sup)) nbSup++;
		}
		if (nbSup == getMaxOcc(sup - offset)) {
			vars[i].updateSup(sup - 1, -1);
		}
	}

	public void awakeOnInst(int i) throws ContradictionException {   // Propagation classique
		int val = vars[i].getVal();
		constAwake(false);
		// if a value has been instantiated to its max number of occurrences
		// remove it from all variables
		if (i < nbVars) {
			//update lower bounds of cardinalities
			int newlb = 0;
			for (int j = 0; j < nbVars; j++) {
				if (vars[j].isInstantiatedTo(val))
					newlb++;
			}
			card[val - offset].updateInf(newlb, cIndices[nbVars + val - offset]);
			//update the upperbounds
			for (int j = deSynchronizedDomain[i].nextSetBit(0); j < range && j >= 0; j = deSynchronizedDomain[i].nextSetBit(j + 1))
			{
				if ((j + offsets[i]) != val) {
					deSynchronizedDomain[i].clear(j);
					val_maxOcc[j].add(-1);
					card[j].updateSup(val_maxOcc[j].get(), -1);
				}
			}
			filterBCOnInst(val);
		} else {
			filterBCOnInst(i - nbVars + offset);
		}
	}

	/**
	 * Enforce simple occurrences reasonnings on value val
	 * no need to reason on the number of possible (instead of sure) values
	 * as this will be done as part of the BC on vars
	 *
	 * @param val
	 * @throws ContradictionException
	 */
	public void filterBCOnInst(int val) throws ContradictionException {
		int nbvalsure = 0;
		for (int j = 0; j < nbVars; j++) {
			if (vars[j].isInstantiatedTo(val)) nbvalsure++;
		}
		if (nbvalsure > getMaxOcc(val - offset)) {
			this.fail();
		} else if (nbvalsure == getMaxOcc(val - offset)) {
			for (int j = 0; j < nbVars; j++) {
				if (!vars[j].isInstantiatedTo(val)) {
					vars[j].removeVal(val, cIndices[j]);
				}
			}
		}
	}

	//todo <hca>: this should be done by BC !!! check this
	public void filterBCOnRem(int val) throws ContradictionException {
		int nbpos = 0;
		for (int j = 0; j < nbVars; j++) {
			if (vars[j].canBeInstantiatedTo(val)) nbpos++;
		}
		if (nbpos < getMinOcc(val - offset)) {
			this.fail();
		} else if (nbpos == getMinOcc(val - offset)) {
			for (int j = 0; j < nbVars; j++) {
				if (vars[j].canBeInstantiatedTo(val)) {
					vars[j].instantiate(val, cIndices[j]);
				}
			}
		}

	}

	/**
	 * Only maintain the data structure and update upperbounds of card
	 *
	 * @throws ContradictionException
	 */
	public void awakeOnRem(int idx, int i) throws ContradictionException {
		if (idx < nbVars) {
			if (deSynchronizedDomain[idx].get(i - offset)) {
				deSynchronizedDomain[idx].clear(i - offset);
				val_maxOcc[i - offset].add(-1);
				card[i - offset].updateSup(val_maxOcc[i - offset].get(), -1);
			}
		}
	}

	/**
	 * Enforce sum of the cardinalities = nbVariable
	 *
	 * @throws ContradictionException
	 */
	public void propagateSumCard() throws ContradictionException {
		boolean fixpoint = true;
		while (fixpoint) {
			fixpoint = false;
			int lb = 0;
			int ub = 0;
			for (int i = 0; i < range; i++) {
				lb += card[i].getInf();
				ub += card[i].getSup();

			}
			for (int i = 0; i < range; i++) {
				fixpoint |= card[i].updateSup(nbVars - (lb - card[i].getInf()), cIndices[i + nbVars]);
				fixpoint |= card[i].updateInf(nbVars - (ub - card[i].getSup()), cIndices[i + nbVars]);
			}
		}
	}

	public boolean isSatisfied(int[] tuple) {
		int[] occurrences = new int[this.range];
		for (int i = 0; i < nbVars; i++) {
			occurrences[tuple[i] - this.offset]++;
		}
		for (int i = 0; i < occurrences.length; i++) {
			int occurrence = occurrences[i];
			if (tuple[nbVars + i] != occurrence)
				return false;
		}
		return true;
	}

	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("BoundGcc({");
		for (int i = 0; i < nbVars; i++) {
			if (i > 0) sb.append(", ");
			IntDomainVar var = vars[i];
			sb.append(var.pretty());
		}
		sb.append("}, {");
		for (int i = 0; i < this.range; i++) {
			if (i > 0) sb.append(", ");
			sb.append("#").append(this.offset + i).append(" = ").append(vars[nbVars + i].pretty());
		}
		sb.append("})");
		return sb.toString();
	}

	public Boolean isEntailed() {
		throw new Error("isEntailed not yet implemented on package i_want_to_use_this_old_version_of_choco.global.BoundAlldiff");
	}

	protected class Interval {
		int minrank, maxrank;
		IntDomainVar var;
		int idx;
	}

	/**
	 * A class to deal with partial sum data structure adapted to
	 * the filterLower{Min,Max} and filterUpper{Min,Max} functions.
	 * Two elements before and after the element list will be added with a weight of 1
	 */
	protected class PartialSum {
		int[] sum;
		int[] ds;
		int firstValue, lastValue;

		public PartialSum(int firstValue, int count, int[] elt) {
			this.sum = new int[count + 5];
			this.firstValue = firstValue - 3;
			this.lastValue = firstValue + count + 1;
			sum[0] = 0;
			sum[1] = 1;
			sum[2] = 2;
			int i, j;
			for (i = 2; i < count + 2; i++) {
				sum[i + 1] = sum[i] + elt[i - 2];
			}
			sum[i + 1] = sum[i] + 1;
			sum[i + 2] = sum[i + 1] + 1;
			ds = new int[count + 5];
			i = count + 3;
			for (j = i + 1; i > 0;) {
				while (sum[i] == sum[i - 1]) {
					ds[i--] = j;
				}
				j = ds[j] = i--;
			}
			ds[j] = 0;
		}

		public int sum(int from, int to) {
			if (from <= to) {
				return sum[to - firstValue] - sum[from - firstValue - 1];
			} else {
				return sum[to - firstValue - 1] - sum[from - firstValue];
			}
		}

		public int minValue() {
			return firstValue + 3;
		}

		public int maxValue() {
			return lastValue - 2;
		}

		public int skipNonNullElementsRight(int value) {
			value -= firstValue;
			return (ds[value] < value ? value : ds[value]) + firstValue;
		}

		public int skipNonNullElementsLeft(int value) {
			value -= firstValue;
			return (ds[value] > value ? ds[ds[value]] : value) + firstValue;
		}
	}


}