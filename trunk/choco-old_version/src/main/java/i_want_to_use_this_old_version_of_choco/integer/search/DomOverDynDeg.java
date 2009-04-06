package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 10 oct. 2006
 * Time: 11:05:46
 * To change this template use File | Settings | File Templates.
 */
public final class DomOverDynDeg extends DoubleHeuristicIntVarSelector {
  public DomOverDynDeg(AbstractProblem pb) {
    super(pb);

  }

  public DomOverDynDeg(AbstractProblem pb, IntDomainVar[] vs) {
    super(pb);
    vars = vs;
  }

  public double getHeuristic(IntDomainVar v) {
    int dsize = v.getDomainSize();
    int deg = getDynDeg(v);
    if (deg == 0)
      return Double.POSITIVE_INFINITY;
    else
      return (double) dsize / (double) deg;
  }

  public int getDynDeg(IntDomainVar v) {
       int ddeg = 0;
       PartiallyStoredVector cts = v.getConstraintVector();
       IntIterator it = cts.getIndexIterator();
       while (it.hasNext()) {
           AbstractConstraint ct = (AbstractConstraint) v.getConstraint(it.next());
           if (!ct.isCompletelyInstantiated()) {
               ddeg += 1;
           }
       }
       return ddeg;
   }

}
