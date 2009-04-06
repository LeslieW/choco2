// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

public class TimeLimit extends AbstractGlobalSearchLimit {
  protected long starth = 0;

  public TimeLimit(AbstractGlobalSearchSolver theSolver, int theLimit) {
    super(theSolver, theLimit);
    unit = "millis.";
  }

  public void reset(boolean first) {
    super.reset(first);
    starth = System.currentTimeMillis();
  }

  public boolean newNode(AbstractGlobalSearchSolver solver) {
    nb = (int) (System.currentTimeMillis() - starth);
    return ((nb + nbTot) < nbMax);
  }

  public boolean endNode(AbstractGlobalSearchSolver solver) {
      nb = (int) (System.currentTimeMillis() - starth);
      return ((nb + nbTot) < nbMax);
  }
}
