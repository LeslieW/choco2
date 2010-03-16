package choco.kernel.common.util.tools;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public final class TaskUtils {

	private TaskUtils() {
		super();
	}

	public static int getSlack(final ITask t) {
		return t.getLST()- t.getEST();
	}

	public static double getCentroid(final ITask t) {
		return ( (double) (t.getECT()+ t.getLST()) )/2;
	}

	public static boolean hasCompulsoryPart(final ITask t) {
		return t.getECT() > t.getLST();
	}
	
	
	public static int getMinConsumption(IRTask t) {
		final int h = t.getMinHeight();
		final int d = h>0 ? t.getTaskVar().getMinDuration() : t.getTaskVar().getMaxDuration();
		return h*d;
	}
	
	public static int getMaxConsumption(IRTask t) {
		final int h = t.getMaxHeight();
		final int d = h>0 ? t.getTaskVar().getMaxDuration() : t.getTaskVar().getMinDuration();
		return h*d;
	}
	
	public static boolean isRegular(IntDomainVar usage) {
		return usage.isInstantiatedTo(IRTask.REGULAR);
	}
	
	public static boolean isOptional(IntDomainVar usage) {
		return !usage.isInstantiated();
	}
	
	public static boolean isEliminated(IntDomainVar usage) {
		return usage.isInstantiatedTo(IRTask.ELIMINATED);
	}
	
	public static boolean hasEnumeratedDomain(TaskVar task) {
		return task.start().hasEnumeratedDomain() || task.end().hasEnumeratedDomain();
	}
}
