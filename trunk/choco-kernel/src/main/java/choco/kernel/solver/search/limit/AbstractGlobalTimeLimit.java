package choco.kernel.solver.search.limit;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;



public abstract class AbstractGlobalTimeLimit extends AbstractGlobalSearchLimit {

	protected long starth = Long.MIN_VALUE;

	protected long newh = Long.MIN_VALUE;
	
	public AbstractGlobalTimeLimit(AbstractGlobalSearchStrategy theStrategy,
			int theLimit, Limit type) {
		super(theStrategy, theLimit, type);
	}

	public abstract long getTimeStamp();
	
	public abstract void update();
		

	@Override
	public final void initialize() {
		starth = getTimeStamp();
		super.initialize();
	}

	@Override
	public final void reset() {
		newh = getTimeStamp();
		update();
		starth = newh;
		super.reset();
	}

	public final void newNode() throws ContradictionException {
		newh = getTimeStamp();
		update();
		checkLimit();
	}

	public final void endNode() throws ContradictionException {
		newh = getTimeStamp();
		update();
		checkLimit();
	}

	
}


