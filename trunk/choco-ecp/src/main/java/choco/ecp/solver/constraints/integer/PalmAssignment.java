//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.constraints.integer;

import choco.ecp.solver.search.dbt.DecisionSConstraint;
import choco.ecp.solver.variables.integer.dbt.PalmIntVar;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 26 ao?t 2003
 * Time: 10:47:40
 * To change this template use Options | File Templates.
 */
public class PalmAssignment extends PalmEqualXC implements DecisionSConstraint, Comparable {
  public PalmAssignment(IntDomainVar v0, int cste) {
    super(v0, cste);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public SConstraint negate() {
    PalmIntVar v = (PalmIntVar) this.v0;
    return v.getNegDecisionConstraint(this.cste);
    /*if (v.hasEnumeratedDomain())
      return v.getNegnConstraint(this.cste);
    else
      return v.getNegEnumerationConstraint(this.cste); */
  }

  public int getBranch() {
    return cste;
  }

  public int compareTo(Object o) {
    if (v0.hashCode() < ((PalmAssignment) o).v0.hashCode())
      return 1;
    else if (v0.hashCode() == ((PalmAssignment) o).v0.hashCode()) {
      if (cste < ((PalmAssignment) o).cste)
        return 1;
      else if (cste == ((PalmAssignment) o).cste)
        return 0;
      else
        return 1;
    } else
      return -1;
  }
}
