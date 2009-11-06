package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.solver.ContradictionException;

public interface ICumulSweep {

	/**
	 * Build to cumulative profile and achieve the pruning  regarding this
	 * profile.
	 *@return <code><code>true</code> if any change was performed
	 */
	boolean sweep() throws ContradictionException;

}