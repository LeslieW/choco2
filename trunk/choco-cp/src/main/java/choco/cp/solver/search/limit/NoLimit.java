package choco.cp.solver.search.limit;

import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;

public final class NoLimit extends AbstractGlobalSearchLimit {

	public final static NoLimit SINGLOTON = new NoLimit();
	
	private NoLimit() {
		super(null, Integer.MAX_VALUE, "unlimited");
	}

	@Override
	public String pretty() {
		return unit;
	}

	@Override
	public int getNb() {
		return 0;
	}

	@Override
	public void setNbMax(int nbMax) {}

	
}
