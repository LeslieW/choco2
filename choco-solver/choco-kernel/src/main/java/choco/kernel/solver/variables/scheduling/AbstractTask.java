package choco.kernel.solver.variables.scheduling;

import choco.kernel.common.util.tools.StringUtils;

public abstract class AbstractTask implements ITask {

	protected final int id;

	/**
	 * A name may be associated to each variable.
	 */
	protected final String name;


	public AbstractTask(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}


	@Override
	public final int getID() {
		return id;
	}


	@Override
	public final String getName() {
		return name;
	}

	
	@Override
	public String toDotty() {
		return StringUtils.toDotty(this, null, true);
	}


	@Override
	public String pretty() {
		return StringUtils.pretty(this);
	}


	@Override
	public String toString() {
		return getName()+"["+getEST()+", "+getLCT()+"]";
	}


}
