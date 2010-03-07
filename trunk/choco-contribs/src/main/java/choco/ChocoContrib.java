package choco;

import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.scheduling.TaskVariable;

public final class ChocoContrib {

	protected final static Logger LOGGER = ChocoLogging.getEngineLogger();
	
	private ChocoContrib() {}
	
	public static Constraint useAtLeastOne(TaskVariable task, Constraint... resources) {
		return useAtLeast(task, 1, resources);
	}
	
	public static Constraint useAtLeast(TaskVariable task, int k, Constraint... resources) {
		return useResources(task, k, false, resources);
	}
	
	public static Constraint useExactlyOne(TaskVariable task, Constraint... resources) {
		return useExactly(task, 1, resources);
	}
	
	public static Constraint useExactly(TaskVariable task, int k, Constraint... resources) {
		return useResources(task, k, true, resources);
	}

	/**
	 * Transversal Constraint among resources that defines the usage of a task: U_1 + ... + U_n ( == or >= ) k.
	 * U_i is the usage variable of the task in the i-th resource.
	 * If Hypothetical domains are enbaled, then additional filtering algorithms are used.
	 * @param k minimal number of resources where the task is regular.
	 * @param eq if <code>true</code> equality constraint, otherwise geq constraint.
	 * @param resources if null the requirement concerns all resources.
	 * @return
	 */
	private static Constraint useResources(TaskVariable task, int k, boolean eq,Constraint[] resources) {
		final int nbR = resources == null ? 0 : resources.length;
		if( k > nbR) {
			LOGGER.log(Level.WARNING, "{0} use {1} resources among {2} resources", new Object[]{task,k,nbR});
			return Choco.FALSE;
		}else if(k >= 0){
			if( k == 0) LOGGER.log(Level.WARNING, "{0} use {1} resources among {2} resources", new Object[]{task,k,nbR});
			return new ComponentConstraint(ConstraintType.USE_RESOURCES, new Object[] {resources, k, eq}, new TaskVariable[]{task});
		} else throw new ModelException( task+ " uses "+k+" resources");
		
	}
	

}
