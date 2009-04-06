// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.AbstractEntity;

/**
 * An abstract class for limiting tree search (imposing conditions on depth, ...)
 */
public abstract class AbstractGlobalSearchLimit extends AbstractEntity implements GlobalSearchLimit {
  /**
   * the solver that delegates the limit checking task to such AbstractGlobalSearchLimit objects
   */
  protected AbstractGlobalSearchSolver solver;

  /**
   * for pretty printing
   */
  protected String unit = "";

  /**
   * maximal value limitting the search exploration
   */
  protected int nbMax = Integer.MAX_VALUE;

  /**
   * a counter who is limited to values below max
   */
  protected int nb = 0;

  /**
   * counting for successive tree search
   */
  protected int nbTot = 0;

  public AbstractGlobalSearchLimit(AbstractGlobalSearchSolver theSolver, int theLimit) { //<hca> Il faut ajouter le probl�me pour pouvoir obtenir la contradiction en cas de d�passement
    solver = theSolver;
    problem = solver.problem;
    nb = 0;
    nbTot = 0;
    nbMax = theLimit;
  }

  public void reset(boolean first) {
    if (first) {
      nbTot = 0;
    } else {
      nbTot += nb;
    }
    nb = 0;
  }

  public String pretty() {
    String res = nbTot + "[+" + nb + "]";
    if (nbMax != Integer.MAX_VALUE) {
      res += "/" + nbMax;
    }
    return res + " " + unit;
  }

  /**
   * get the current counter
   */

  public int getNb() {
    return nb;
  }

  /**
   * get the total counter
   */

  public int getNbTot() {
    return nbTot;
  }

  /**
   * @return the limit value
   */

  public int getNbMax() {
    return nbMax;
  }

  /**
   * Sets the limits
   *
   * @param nbMax new value of the limit
   */

  public void setNbMax(int nbMax) {
    this.nbMax = nbMax;
  }
}

