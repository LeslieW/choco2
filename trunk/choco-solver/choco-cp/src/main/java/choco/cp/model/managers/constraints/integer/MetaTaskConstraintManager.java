/**
 * 
 */
package choco.cp.model.managers.constraints.integer;

import choco.Options;
import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.List;

/**
 * @author Arnaud Malapert</br> 
 * @since 28 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public final class MetaTaskConstraintManager extends MixedConstraintManager {

	/**
	 * @see choco.kernel.model.constraints.ConstraintManager#makeConstraint(choco.kernel.solver.Solver,V[], Object,java.util.List
	 */
	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, List<String> options) {
		  if (solver instanceof CPSolver) {
			  if (parameters instanceof Constraint) {
				final Constraint ic = (Constraint) parameters;
                boolean decomp = false;
                if (ic.getOptions().contains(Options.E_DECOMP)) {
                    decomp = true;
                }
                return ( (CPSolver) solver).makeSConstraint(ic, decomp);
			}
		  }
		  throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
	}

	@Override
	public int[] getFavoriteDomains(List<String> options) {
		//because we are dealing with tasks
		return getBCFavoriteIntDomains();
	}

	
	

}
