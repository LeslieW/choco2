package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.ConstraintSelector;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * A class that composes two heuristics for selecting a variable:
 * a first heuristic is appled for selecting a constraint.
 * from that constraint a second heuristic is applied for selecting the variable
 */
public class CompositeIntVarSelector extends AbstractIntVarSelector implements VarSelector {
    protected ConstraintSelector cs;
    protected HeuristicIntVarSelector cvs;

    public CompositeIntVarSelector(ConstraintSelector cs, HeuristicIntVarSelector cvs) {
        this.cs = cs;
        this.cvs = cvs;
    }

    public IntDomainVar selectIntVar() throws ContradictionException {
        Constraint c = cs.getConstraint();
        if (c == null) return null;
        else return cvs.getMinVar((IntConstraint) c);
    }

    public ConstraintSelector getCs() {
        return cs;
    }

    public HeuristicIntVarSelector getCvs() {
        return cvs;
    }
}
