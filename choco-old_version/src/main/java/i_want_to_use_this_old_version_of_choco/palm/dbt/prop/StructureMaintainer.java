//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.prop;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.palm.integer.PalmIntVarListener;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

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

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
      if (c.isActive()) {
        int i = indices.get(idx);
        c.updateDataStructuresOnConstraint(i, select, newValue, oldValue);
      }
    }
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

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
      if (c.isActive()) {
        int i = indices.get(idx);
        c.updateDataStructuresOnRestoreConstraint(i, select, newValue, oldValue);
      }
    }
  }

}
