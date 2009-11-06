/**
 * 
 */
package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;

/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface ICumulativeResource<T extends ITask> extends ICapacitedResource<T> {

	IntDomainVar getHeight(int idx);

	boolean isInstantiatedHeights();
		
	boolean hasOnlyPosisiveHeights();	
	

}
