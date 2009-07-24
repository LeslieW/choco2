package choco.kernel.solver.search.limit;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.restart.UniversalRestartStrategy;

public final class RestartLimit extends AbstractGlobalSearchLimit {

	protected final UniversalRestartStrategy restartStrategy;
	
	protected final AbstractGlobalSearchLimit watchedLimit;
	
	protected int cutoff;
	
	public RestartLimit(UniversalRestartStrategy restartStrategy, AbstractGlobalSearchLimit watchedLimit) {
		super(watchedLimit.getSearchStrategy(), Integer.MAX_VALUE, Limit.RESTART);
		this.restartStrategy = restartStrategy;
		this.watchedLimit = watchedLimit;
		//Often, we could restart when opening or ending a node
		//However, we should only restart while opening a node when we use nogood recording.
		//Furthermore, it speedup the limits to check only at opening.
		limitMask = NEW_NODE; 
	}

	
	
	@Override
	public void initialize() {
		nbMax = restartStrategy.getScaleFactor();	
		cutoff = nbMax;
		super.initialize();
	}

	@Override
	public void reset() {
		cutoff = nbMax;	
		super.reset();
	}

	private int limitNb;
	
	private final void checkRestart() throws ContradictionException {
		limitNb = watchedLimit.getNb();
		if(limitNb > cutoff) {
			nbMax = restartStrategy.getNextCutoff(getNbAll()); //update the cutoff
			cutoff =  nbMax + limitNb; //cutoff with an offset due to the current state of the search
			nb++; //count restart 
			raiseContradiction(AbstractGlobalSearchStrategy.RESTART);
		}
	}
	@Override
	public void endNode() throws ContradictionException {
		checkRestart();
	}

	@Override
	public void newNode() throws ContradictionException {
		checkRestart();
	}
	
	
	@Override
	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append(nbTot).append("[+").append(nb).append("] ");
		b.append(unit).append(" <- cutoff ");
		b.append(watchedLimit.getNb()).append('/').append(cutoff).append(' ').append(watchedLimit.getUnit());
		b.append(" -> ");
		return new String(b);
	}

	
	public int[] getExample(int lenght) {
		initialize();
		final int[] r=new int[lenght];
		r[0] = nbMax;
		for (int i = 1; i < r.length; i++) {
			nb++;
			r[i] = restartStrategy.getNextCutoff(getNbAll());

		}
		initialize();
		return r;
	}
}
