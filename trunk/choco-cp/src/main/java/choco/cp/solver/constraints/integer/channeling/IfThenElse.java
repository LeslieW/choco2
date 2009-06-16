package choco.cp.solver.constraints.integer.channeling;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

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

    /**
     * TEMPORARY: if not overriden by the constraint, throws an error
     * to avoid bug using reified constraints in constraints
     * that have not been changed to fulfill this api yet !
     *
     * @param tuple
     * @return
     */
    @Override
    public boolean isSatisfied(int[] tuple) {
        int val = tuple[0];
        if (val == 1) {
            for (int i = 0; i < tupleCons.length; i++) {
                tupleCons[i] = tuple[scopeCons[i]];
            }
            return cons.isSatisfied(tupleCons);
        } else {
            for (int i = 0; i < tupleOCons.length; i++) {
                tupleOCons[i] = tuple[scopeOCons[i]];
            }
            return oppositeCons.isSatisfied(tupleOCons);
        }
    }
}
