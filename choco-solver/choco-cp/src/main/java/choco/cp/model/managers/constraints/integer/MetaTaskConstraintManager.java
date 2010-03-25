/**
 * 
 */
package choco.cp.model.managers.constraints.integer;

import choco.cp.CPOptions;
import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

/**
 * @author Arnaud Malapert</br> 
 * @since 28 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class MetaTaskConstraintManager extends MixedConstraintManager {

	/**
	 * @see choco.kernel.model.constraints.ConstraintManager#makeConstraint(choco.kernel.solver.Solver, choco.kernel.model.variables.Variable[], java.lang.Object, Set)
	 */
	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, Set<String> options) {
		  if (solver instanceof CPSolver) {
			  if (parameters instanceof Constraint) {
				final Constraint ic = (Constraint) parameters;
				if(ic != null) {
					boolean decomp = false;
					if (ic.getOptions().contains(CPOptions.E_DECOMP)) {
						decomp = true;
					}
					return ( (CPSolver) solver).makeSConstraint(ic, decomp);
				}
			}
		  }
		  throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
	}

	@Override
	public int[] getFavoriteDomains(Set<String> options) {
		//because we are dealing with tasks
		return getBCFavoriteIntDomains();
	}

	
	

}
