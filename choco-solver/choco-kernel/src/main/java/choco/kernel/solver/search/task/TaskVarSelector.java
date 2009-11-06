/**
 * 
 */
package choco.kernel.solver.search.task;

import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.Collection;

/**
 * @author Arnaud Malapert</br> 
 * @since 25 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface TaskVarSelector {

	public TaskVar selectTaskVar(Collection<TaskVar> vars);
	
}
