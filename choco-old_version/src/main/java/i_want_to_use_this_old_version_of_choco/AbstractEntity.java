// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

/**
 * An overall root abstract class.
 */
public abstract class AbstractEntity implements Entity {
  /**
   * All objects may be linked to an external object
   */

  public Object hook = null;


  /**
   * The (optimization or decision) problem to which the entity belongs.
   */

  public AbstractProblem problem;

  protected AbstractEntity() {
    ;
  }

  protected AbstractEntity(AbstractProblem pb) {
    problem = pb;
  }

  /**
   * Retrieves the problem of the entity
   */

  public AbstractProblem getProblem() {
    return problem;
  }

  public void setProblem(AbstractProblem problem) {
    this.problem = problem;
  }

  public String pretty() {
    return toString();
  }

}

