/**
 * 
 */
package choco.kernel.model.constraints;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.scheduling.TaskVariable;

import java.util.Properties;

/**
 * An wrapper for constraint involving some Taskvariable.
 * It contains additional variables (tasks) added to the model.
 * For example, if you have the constraint (T1 precedes T2) then T1 and T2 should be added to the model with the constraint end(T1) <= start(T2). 
 * @author Arnaud Malapert</br> 
 * @since 28 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class MetaTaskConstraint extends ComponentConstraint {
	
	private static final long serialVersionUID = 5309541509214967423L;

	protected Constraint constraint;

	public MetaTaskConstraint(TaskVariable[] taskvariables,
			Constraint constraint) {
		super(ConstraintType.METATASKCONSTRAINT,constraint,taskvariables);
		this.constraint = constraint;
	}

    /**
     * Extract variables of a constraint
     * and return an array of variables.
     * @return an array of every variables contained in the Constraint.
     */
	@Override
	public Variable[] doExtractVariables() {
		Variable[] listVars = super.doExtractVariables();
		listVars = ArrayUtils.append(listVars, constraint.extractVariables());
		return ArrayUtils.getNonRedundantObjects(Variable.class, listVars);
	}

	@Override
	public void findManager(Properties propertiesFile) {
		super.findManager(propertiesFile);
		constraint.findManager(propertiesFile);
	}


	@Override
	public int[] getFavoriteDomains() {
		return constraint.getFavoriteDomains();
	}
	
	
}
