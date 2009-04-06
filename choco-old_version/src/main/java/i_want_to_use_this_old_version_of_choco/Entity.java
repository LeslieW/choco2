// **************************************************
// *                   CHOCO                        *
// *  An open-source Constraint Programming system  *
// *  primarily designed for research & education   *
// *                                                *
// * Copyright (C) F. Laburthe, 1999-2005           *
// *                                                *
// * Contributors: Thierry Benoist,                 *
// *               Hadrien Cambazard,               *
// *               Etienne Gaudin,                  *
// *               Narendra Jussien,                *
// *               Francois Laburthe,               *
// *               Michel Lemaitre,                 *
// *               Guillaume Rochart ...            *
// **************************************************

package i_want_to_use_this_old_version_of_choco;


/**
 * An interface for all objects from constraint programs.
 */
public interface Entity {
  /**
   * Retrieves the problem of the entity.
   */

  public AbstractProblem getProblem();


  public void setProblem(AbstractProblem problem);  
  /**
   * pretty printing of the object. This String is not constant and may depend on the context.
   *
   * @return a readable string representation of the object
   */
  public String pretty();

}

