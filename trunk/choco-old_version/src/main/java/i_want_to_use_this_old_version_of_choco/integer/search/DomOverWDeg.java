package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.Iterator;

/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
public class DomOverWDeg extends DoubleHeuristicIntVarSelector {

    public DomOverWDeg(AbstractProblem pb) {
        super(pb);
    }

    public DomOverWDeg(AbstractProblem pb, IntDomainVar[] vs) {
        super(pb);
        vars = vs;
    }

    public double getHeuristic(IntDomainVar v) {
        int dsize = v.getDomainSize();
        int weight  = 0;
        // Calcul du poids:
        Iterator c = v.getConstraintsIterator();
        while (c.hasNext()) {
            AbstractConstraint cstr = (AbstractConstraint) c.next();
            if (cstr.getNbVarNotInst().get() > 1) {
                weight += cstr.getNbFailure();
            }
        }
        if (weight == 0)
            return Double.POSITIVE_INFINITY;
        else
            return (double) dsize / ((double) weight);
    }
}
