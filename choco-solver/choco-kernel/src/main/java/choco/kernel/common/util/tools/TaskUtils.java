package choco.kernel.common.util.tools;

import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

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
}
