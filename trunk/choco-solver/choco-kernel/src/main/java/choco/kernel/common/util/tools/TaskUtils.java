package choco.kernel.common.util.tools;

import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public final class TaskUtils {

	private TaskUtils() {
		super();
	}
	
	//*****************************************************************//
	//*******************  Utilities  ********************************//
	//***************************************************************//
	
	public static boolean hasCompulsoryPart(final ITask t) {
		return t.getECT() > t.getLST();
	}
	
	public static boolean hasEnumeratedDomain(TaskVar task) {
		return task.start().hasEnumeratedDomain() || task.end().hasEnumeratedDomain();
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

	//*****************************************************************//
	//*******************  Alternative Resource  *********************//
	//***************************************************************//
	public static boolean isRegular(IntDomainVar usage) {
		return usage.isInstantiatedTo(IRTask.REGULAR);
	}

	public static boolean isOptional(IntDomainVar usage) {
		return !usage.isInstantiated();
	}

	public static boolean isEliminated(IntDomainVar usage) {
		return usage.isInstantiatedTo(IRTask.ELIMINATED);
	}


	//*****************************************************************//
	//*******************  Centroid Measure **************************//
	//***************************************************************//
	public static double getCentroid(final ITask t) {
		return ( (double) getCentroidMultByTwo(t) )/2;
	}
	
	public static int getCentroidMultByTwo(final ITask t) {
		return t.getECT()+ t.getLST();
	}
	//*****************************************************************//
	//*******************  Slack Measure  ****************************//
	//***************************************************************//

	public static int getSlack(final ITask t) {
		return t.getLST()- t.getEST();
	}
	
	public static int getTotalSlack(final ITask t1, final ITask t2) {
		return getSlack(t1) + getSlack(t2);
	}
	
	public static int getTotalSlack(IPrecedence prec) {
		return getTotalSlack(prec.getOrigin(), prec.getDestination());
	}
	

	//*****************************************************************//
	//*******************  Preserved Measure  ************************//
	//***************************************************************//
	

	public static long getA(IntDomainVar x, IntDomainVar y) {
		return ( (long) (y.getSup() - y.getInf() + 1) ) * (x.getSup() - x.getInf() + 1);
	}

	public static long getB(IntDomainVar x, IntDomainVar y) {
		final long v = y.getSup() - x.getInf();
		return (v + 1) * (v + 2);
	}

	public static long getCmin(IntDomainVar x, IntDomainVar y) {
		final long a = y.getInf() - x.getInf();
		return a> 0 ? a * (a+1) : 0;
	}

	public static long getCmax(IntDomainVar x, IntDomainVar y) {
		final long a = y.getSup() - x.getSup();
		return a> 0 ? a * (a+1) : 0;
	}

	public static long getPreservedDividend(IntDomainVar x, IntDomainVar y) {
		return getB(x, y) - getCmin(x, y) - getCmax(x, y);
	}

	public static long getPreservedDivisor(IntDomainVar x, IntDomainVar y) {
		return 2*getA(x, y);
	}

	public final static double getPreserved(IntDomainVar x, IntDomainVar y) {
		assert getPreservedDividend(x, y) <=  getPreservedDivisor(x, y);
		return ( (double) getPreservedDividend(x, y) ) / getPreservedDivisor(x, y);
	}

	public final static double getPreserved(final TaskVar t1, final TaskVar t2) {
		return getPreserved(t1.end(), t2.start());
	}

	public final static double getTotalPreserved(final TaskVar t1, final TaskVar t2) {
		return getPreserved(t1, t2) + getPreserved(t2, t1);
	}

	public static long getPreservedDividend(final TaskVar t1, final TaskVar t2) {
		return getPreservedDividend(t1.end(), t2.start());
	}

	public static long getPreservedDivisor(final TaskVar t1, final TaskVar t2) {
		return getPreservedDivisor(t1.end(), t2.start());
	}
}
