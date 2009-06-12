package choco.cp.solver.constraints.integer.bool.sat;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TLongIntHashMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 * A global constraint to store and propagate all clauses
 * 
 */
public class  ClauseStore extends AbstractLargeIntSConstraint {


	public static boolean nonincprop = false;

	// a data structure for managing all variable/value pairs
	protected Lits voc;

	protected LinkedList<WLClause> listclause;

	protected LinkedList<WLClause> listToPropagate;

	// if we get clause of zie one, we instantiate them directly
	// to the correct value
	protected LinkedList<IntDomainVar> instToOne;
	protected LinkedList<IntDomainVar> instToZero;

	private final TLongIntHashMap indexes;

	protected int[] fineDegree;
	/**
	 * @param vars must be a table of BooleanVarImpl
	 */
	public ClauseStore(IntDomainVar[] vars) {
		this(vars, new LinkedList<WLClause>(), new Lits());
		voc.init(vars);
	}

	public ClauseStore(IntDomainVar[] vars, LinkedList<WLClause> listclause, Lits voc) {
		super(vars);
		solver = vars[0].getSolver();
		this.voc = voc;
		this.listclause = listclause;
		listToPropagate = new LinkedList<WLClause>();
		instToOne = new LinkedList<IntDomainVar>();
		instToZero = new LinkedList<IntDomainVar>();
		fineDegree = new int[vars.length];
		indexes = new TLongIntHashMap(vars.length);
		for(int v = 0; v < vars.length; v++){
			indexes.put(vars[v].getIndice(), v);
		}
	}

	public int getFilteredEventMask(int idx) {
		return IntVarEvent.INSTINTbitvector;
	}

	public Lits getVoc() {
		return voc;
	}


	public void awakeOnInst(int idx) throws ContradictionException {
		if (nonincprop) {
			constAwake(false);
		} else {
			filterOnInst(idx);
		}
	}

	public void filterOnInst(int idx) throws ContradictionException {
		int val = vars[idx].getVal();
		int sidx = idx + 1;
		if (val == 1) {
			int vocidx = -sidx;
			Vec<WLClause> wlist = voc.watches(vocidx);
			if (wlist != null) {
				for (int i = 0; i < wlist.size(); i++) {
					WLClause clause = wlist.get(i);
					if (clause.propagate(vocidx, i)) i--;
				}
			}
		} else {
			Vec<WLClause> wlist = voc.watches(sidx);
			if (wlist != null) {
				for (int i = 0; i < wlist.size(); i ++) {
					WLClause clause = wlist.get(i);
					if (clause.propagate(sidx, i)) i--;
				}
			}
		}
	}


	public void addClause(int[] lits) {
		listclause.add(new WLClause(lits, voc));
	}

	public IntDomainVar[] removeRedundantVars(IntDomainVar[] vs) {
		HashSet<IntDomainVar> filteredVars = new HashSet<IntDomainVar>();
		for (int i = 0; i < vs.length; i++) {
			if (!filteredVars.contains(vs[i]))
				filteredVars.add(vs[i]);
		}
		IntDomainVar[] filteredTab = new IntDomainVar[filteredVars.size()];
		filteredVars.toArray(filteredTab);
		return filteredTab;
	}

	public int[] computeLits(IntDomainVar[] plit,IntDomainVar[] nlit) {
		int[] lits = new int[plit.length + nlit.length];
		int cpt = 0;
		for (IntDomainVar aPlit : plit) {
			int lit = findIndex(aPlit);
			lits[cpt] = lit;
			cpt++;
		}
		for (IntDomainVar aNlit : nlit) {
			int lit = findIndex(aNlit);
			lits[cpt] = -lit;
			cpt++;
		}
		return lits;
	}

	public void updateDegree(int[] lit) {
		for (int i = 0; i < lit.length; i++) {
			int l = (lit[i] < 0) ? -lit[i] - 1 : lit[i] - 1;
			fineDegree[l]++;
		}
	}
	/**
	 * add a clause in the store
	 * WARNING : this method assumes that the variables are
	 * in the scope of the ClauseStore
	 * @param positivelits
	 * @param negativelits
	 */
	public void addClause(IntDomainVar[] positivelits,IntDomainVar[] negativelits) {
		IntDomainVar[] plit = removeRedundantVars(positivelits);
		IntDomainVar[] nlit = removeRedundantVars(negativelits);

		int[] lits = computeLits(plit,nlit);
		updateDegree(lits);
		if (lits.length == 1) { //dealing with clauses of size one
			if (plit.length == 1) {
				instToOne.add(vars[lits[0] - 1]);
			} else {
				instToZero.add(vars[-lits[0] - 1]);  
			}
		} else {
			listclause.add(new WLClause(lits, voc));
		}
	}

	public int findIndex(IntDomainVar v) {
		return indexes.get(v.getIndice())+1;
	}

	public void addDynamicClause(IntDomainVar[] positivelits,IntDomainVar[] negativelits) {
		IntDomainVar[] plit = removeRedundantVars(positivelits);
		IntDomainVar[] nlit = removeRedundantVars(negativelits);

		int[] lits = computeLits(plit,nlit);
		updateDegree(lits);
		if (lits.length == 1) { //dealing with clauses of size one
			if (plit.length == 1) {
				instToOne.add(vars[lits[0] - 1]);
			} else {
				instToZero.add(vars[-lits[0] - 1]);
			}
		} else {
			DynWLClause clause = new DynWLClause(lits,voc);                  
			listclause.add(clause);
			listToPropagate.addLast(clause);
		}
	}

	public void awake() throws ContradictionException {
		for (WLClause cl : listclause) {
			if (!cl.isRegistered())
				cl.register(this);
		}
		propagate();
	}

	public void propagateUnitClause() throws ContradictionException {
		for (IntDomainVar v : instToOne) {
			v.instantiate(1, -1);
		}
		for (IntDomainVar v : instToZero) {
			v.instantiate(0, -1);
		}
	}

	public void propagate() throws ContradictionException {
		if (nonincprop) {
			filterFromScratch();
		} else {
			for (Iterator<WLClause> iterator = listToPropagate.iterator(); iterator.hasNext();) {
				WLClause cl = iterator.next();
				if (cl.register(this)) {
					iterator.remove();
				}
			}            
			propagateUnitClause();
		}
	}

	public void filterFromScratch() throws ContradictionException {
		for (WLClause cl : listclause) {
			cl.simplePropagation(this);
		}
	}

	public boolean isSatisfied() {
		for (WLClause cl : listclause) {
			if (!cl.isSatisfied())
				return false;
		}
		return true;
	}

	//by default, no information is known
	public int getFineDegree(int idx) {
		return fineDegree[idx];    //To change body of overridden methods use File | Settings | File Templates.
	}

	public int getNbClause() {
		return listclause.size();
	}

	public final void printClauses() {
		if(LOGGER.isLoggable(Level.INFO)) {
			StringBuilder b = new StringBuilder();
			for(WLClause wlClause : listclause) {
				b.append(wlClause);
			}
			LOGGER.info(new String(b));
		}
	}
}

