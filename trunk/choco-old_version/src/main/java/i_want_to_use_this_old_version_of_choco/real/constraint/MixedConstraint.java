package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * An interface for mixed constraint : interger and flot variables.
 */
public interface MixedConstraint extends Constraint, Propagator, 
    RealListener, IntConstraint {
  /**
   * Returns the real variable with index i.
   * @param i the variable index
   * @return the variable with index i
   */
  RealVar getRealVar(int i);

  /**
   * Returns the number of real variables. Note that here the number of 
   * variables should equal the number of real variables plus the number
   * of integer variables.
   * @return the number of <i>real</i> variables.
   */
  int getRealVarNb();

  /**
   * Returns the integer variable with index i.
   * @param i the variable index
   * @return the variable with index i
   */
  IntDomainVar getIntVar(int i);

  /**
   * Returns the number of integer variables. Note that here the number of 
   * variables should equal the number of real variables plus the number
   * of integer variables.
   * @return the number of <i>integer</i> variables.
   */  
  int getIntVarNb();
}
