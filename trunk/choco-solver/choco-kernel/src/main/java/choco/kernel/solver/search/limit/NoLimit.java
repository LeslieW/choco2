package choco.kernel.solver.search.limit;


public final class NoLimit extends AbstractGlobalSearchLimit {

	public final static String NO_LIMIT_UNIT = "unlimited";
	
	public final static NoLimit SINGLOTON = new NoLimit();
	
	private NoLimit() {
		super(null, Integer.MAX_VALUE, NO_LIMIT_UNIT);
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
