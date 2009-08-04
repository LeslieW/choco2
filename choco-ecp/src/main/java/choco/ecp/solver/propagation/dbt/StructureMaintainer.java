//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.propagation.dbt;

import choco.ecp.solver.propagation.PalmIntVarListener;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Jan 14, 2004
 * Time: 11:25:01 AM
 * To change this template use Options | File Templates.
 */
public final class StructureMaintainer {
  public static void updateDataStructures(IntDomainVar var, int select, int newValue, int oldValue) {
    updateDataStructuresOnVariable(var, select, newValue, oldValue);
    updateDataStructuresOnConstraints(var, select, newValue, oldValue);
  }

  public static void updateDataStructuresOnVariable(IntDomainVar var, int select, int newValue, int oldValue) {
    // A redefinir si necessaire
  }

  public static void updateDataStructuresOnConstraints(IntDomainVar v, int select, int newValue, int oldValue) {
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

      DisposableIntIterator cit = constraints.getIndexIterator();
    for (; cit.hasNext();) {
      int idx = cit.next();
      PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
      if (c.isActive()) {
        int i = indices.get(idx);
        c.updateDataStructuresOnConstraint(i, select, newValue, oldValue);
      }
    }
      cit.dispose();
  }

  public static void updateDataStructuresOnRestore(IntDomainVar var, int select, int newValue, int oldValue) {
    updateDataStructuresOnRestoreVariable(var, select, newValue, oldValue);
    updateDataStructuresOnRestoreConstraints(var, select, newValue, oldValue);
  }

  public static void updateDataStructuresOnRestoreVariable(IntDomainVar var, int select, int newValue, int oldValue) {
    // A redefinir si necessaire
  }

  public static void updateDataStructuresOnRestoreConstraints(IntDomainVar v, int select, int newValue, int oldValue) {
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

      DisposableIntIterator cit = constraints.getIndexIterator();
    for (; cit.hasNext();) {
      int idx = cit.next();
      PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
      if (c.isActive()) {
        int i = indices.get(idx);
        c.updateDataStructuresOnRestoreConstraint(i, select, newValue, oldValue);
      }
    }
      cit.dispose();
  }

}
