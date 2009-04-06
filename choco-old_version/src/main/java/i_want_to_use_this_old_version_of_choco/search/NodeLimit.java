// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;


public final class NodeLimit extends AbstractGlobalSearchLimit {

  public NodeLimit(AbstractGlobalSearchSolver theSolver, int theLimit) {
    super(theSolver, theLimit);
    unit = "nodes";
  }

  public boolean newNode(AbstractGlobalSearchSolver solver) {
    nb++;
    return ((nb + nbTot) < nbMax);
  }

  public boolean endNode(AbstractGlobalSearchSolver solver) {
    return ((nb + nbTot) < nbMax);  //<hca> currentElement also the limit while backtracking
  }

}
