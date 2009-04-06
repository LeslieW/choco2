package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
public final class DomOverFailureDeg extends DoubleHeuristicIntVarSelector {
    public DomOverFailureDeg(AbstractProblem pb) {
        super(pb);
    }

    public DomOverFailureDeg(AbstractProblem pb, IntDomainVar[] vs) {
        super(pb);
        vars = vs;
    }

    public double getHeuristic(IntDomainVar v) {
        int dsize = v.getDomainSize();
        int weight = v.getNbFailure(); //weight can not be null
        return (double) dsize / ((double) weight);
    }
}
