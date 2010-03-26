package choco.kernel.solver.variables.scheduling;

import static choco.kernel.common.util.tools.TaskUtils.*;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class HTask implements ITask {

	private final ITask task;
	
	private IntDomainVar usage;
	
	private final IStateInt estH, lctH;
	
	
	public HTask(ITask task, IntDomainVar usage, IStateInt estH, IStateInt lctH) {
		super();
		this.task = task;
		this.usage = usage;
		this.estH = estH;
		this.lctH = lctH;
	}

	//ITask interface : integrate the hypothetical domains for filtering algorithms.
	@Override
	public int getECT() {
		final int valR = task.getECT();
		if( isRegular(usage))
			return valR;
		else
		{
			final int valH = estH.get() + task.getMinDuration();
			return valR >  valH ? valR : valH;
		}
	}

	@Override
	public int getEST() {
		final int valR = task.getEST();
		if(isRegular(usage))
			return valR;
		else
		{
			final int valH = estH.get();
			return valR >  valH ? valR : valH;
		}
	}

	@Override
	public int getID() {
		return task.getID();
	}

	@Override
	public int getLCT() {
		final int valR = task.getLCT();
		if(isRegular(usage))
			return valR;
		else
		{
			final int valH = lctH.get();
			return valR >  valH ? valH : valR;
		}
	}

	@Override
	public int getLST() {
		final int valR = task.getLST();
		if(isRegular(usage))
			return valR;
		else
		{
			final int valH = lctH.get() - task.getMinDuration();
			return valR >  valH ? valH : valR;
		}
	}

	@Override
	public int getMaxDuration() {
		final int valR = task.getMaxDuration();
		if(isRegular(usage))
			return valR;
		else{
			final int valH = lctH.get() - estH.get();
			return valR >  valH ? valH : valR;
		}
	}

	@Override
	public int getMinDuration() {
		return task.getMinDuration();
	}

	@Override
	public String getName() {
		return task.getName();
	}

	@Override
	public boolean hasConstantDuration() {
		return task.hasConstantDuration();
	}

	@Override
	public boolean isScheduled() {
		return task.isScheduled();
	}

	@Override
	public String pretty() {
		return StringUtils.pretty(this);
	}


	@Override
	public String toString() {
		return getName()+"["+getEST()+", "+getLCT()+"]";
	}

	@Override
	public String toDotty() {
		return StringUtils.toDotty(this, null, true);
	}
	
	

}
