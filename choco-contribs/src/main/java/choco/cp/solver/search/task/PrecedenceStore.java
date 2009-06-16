package choco.cp.solver.search.task;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectProcedure;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;

public class PrecedenceStore implements IPrecedenceStore {

	private final static ReifiedChecker CHECK = new ReifiedChecker();

	private final static ReifiedCounter COUNT = new ReifiedCounter();

	private final TIntObjectHashMap<StoredPrecedence> precMap = new TIntObjectHashMap<StoredPrecedence>();

	private final int offset;

	public PrecedenceStore(int n) {
		super();
		offset = n;
	}

	protected int getTaskPairKey(ITask t1, ITask t2) {
		return t1.getID() < t2.getID() ? t1.getID() * offset + t2.getID() : t2.getID() * offset + t1.getID();
	}

	@Override
	public StoredPrecedence getStoredPrecedence(ITask t1, ITask t2) {
		return precMap.get(getTaskPairKey(t1, t2));
	}


	@Override
	public void addPrecedence(ITask t1, ITask t2, IntDomainVar direction) {
		final int key = getTaskPairKey(t1, t2);
		if(precMap.contains(key)) {
			throw new SolverException("duplicate or opposite precedence");
		}
		precMap.put(key, new StoredPrecedence(t1,t2,direction));

	}

	
	@Override
	public boolean isReified(ITask t1, ITask t2) {
		final StoredPrecedence prec = getStoredPrecedence(t1, t2);
		if(prec == null) {return false;}
		return ! prec.direction.isInstantiated();
	}

	

	@Override
	public boolean containsReifiedPrecedence() {
		return !precMap.forEachValue(CHECK);
	}

	@Override
	public int getNbReifiedPrecedence() {
		COUNT.count=0;
		precMap.forEachValue(COUNT);
		return COUNT.count;
	}



	@Override
	public StoredPrecedence[] getValues() {
		return precMap.getValues(new StoredPrecedence[precMap.size()]);
	}



	private static class ReifiedChecker implements TObjectProcedure<StoredPrecedence> {

		@Override
		public boolean execute(StoredPrecedence arg0) {
			return arg0.direction.isInstantiated();
		}
	}

	private static class ReifiedCounter implements TObjectProcedure<StoredPrecedence> {

		public int count = 0;
		
		@Override
		public boolean execute(StoredPrecedence arg0) {
			if(!arg0.direction.isInstantiated()) {count++;}
			return true;
		}
	}

}
