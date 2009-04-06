package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.set.SetConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.logging.Logger;


// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractSetConstraint extends AbstractConstraint implements SetConstraint {

  /**
   * Reference to an object for logging trace statements related to constraints over set
   * (using the java.util.logging package)
   */
  protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const");

  public void awakeOnKer(int varIdx, int x) throws ContradictionException {
    propagate();
  }

  public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
    propagate();
  }

  public void awakeOnInst(int varIdx) throws ContradictionException {
    propagate();
  }

  public void awakeOnEnvRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (deltaDomain != null) {
      for (; deltaDomain.hasNext();) {
        int val = deltaDomain.next();
        awakeOnEnv(idx, val);
      }
    } else
      throw new Error("deltaDomain should not be null in awakeOnEnvRemovals");
  }

  public void awakeOnkerAdditions(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (deltaDomain != null) {
      for (; deltaDomain.hasNext();) {
        int val = deltaDomain.next();
        awakeOnKer(idx, val);
      }
    } else
      throw new Error("deltaDomain should not be null in awakeOnKerAdditions");
  }

  public boolean isCompletelyInstantiated() {
    int n = getNbVars();
    for (int i = 0; i < n; i++) {
      if (!(getSetVar(i).isInstantiated()))
        return false;
    }
    return true;
  }

  /**
   * returns the (global) index of the constraint among all constraints of the problem
   */
  public int getSelfIndex() {
    AbstractProblem pb = getProblem();
    for (int i = 0; i < pb.getNbIntConstraints(); i++) {
      Constraint c = null;
      //c = pb.getSetConstraint(i);  //TODO
      if (c == this) {
        return i;
      }
    }
    return -1;
  }
}
