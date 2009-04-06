package choco.cp.solver.constraints.integer.channeling;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.ContradictionException;

/**
 * A constraint to state :
 * if (bool = 1) cons else othercons
 */
public class IfThenElse extends ReifiedIntSConstraint {

     public IfThenElse(IntDomainVar bool, AbstractIntSConstraint cons, AbstractIntSConstraint othercons) {
        super(bool, cons, othercons);
     }
    
     @Override
     public void filterReifiedConstraintFromCons() throws ContradictionException {        
            Boolean isEntailed = cons.isEntailed();
            if (isEntailed != null) {
                if (!isEntailed) {
                    vars[0].instantiate(0, -1);
                }
            }
    }
}
