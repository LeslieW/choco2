//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 7 janv. 2004
 * Time: 13:50:57
 * To change this template use Options | File Templates.
 */
public interface PalmIntDomain extends ExplainedIntDomain {
  int DOM = 0;
  int INF = 1;
  int SUP = 2;
  int VAL = 3;


  /**
   * When a value is restored, it deletes the explanation associated to the value removal.
   */

  public void resetExplanationOnVal(int val);


  /**
   * When a lower bound is restored, it deletes the explanation associated to the value removal.
   */

  public void resetExplanationOnInf();


  /**
   * When an upper bound is restored, it deletes the explanation associated to the value removal.
   */

  public void resetExplanationOnSup();


  /**
   * Allows to get an explanation for the domain or a bound of the variable. This explanation is merge to the
   * explanation in parameter.
   *
   * @param select Should be <code>PalmIntDomain.INF</code>, <code>PalmIntDomain.SUP</code>, or <code>PalmIntDomain.DOM</code>
   */

  public void self_explain(int select, Explanation expl);


  /**
   * Allows to get an explanation for a value removal from the variable. This explanation is merge to the
   * explanation in parameter.
   *
   * @param select Should be <code>PalmIntDomain.VAL</code>
   */

  public void self_explain(int select, int x, Explanation expl);


  /**
   * Updates the upper bound and posts the event.
   */

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException;


  /**
   * Updates the lower bound and posts the event.
   */

  public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException;


  /**
   * Removes a value and posts the event.
   */

  public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException;


  /**
   * Restores a lower bound and posts the event. Not supported for such a domain.
   */

  public void restoreInf(int newValue);


  /**
   * Restores an upper bound and posts the event. Not supported for such a domain.
   */

  public void restoreSup(int newValue);


  /**
   * Restores a value and posts the event.
   */

  public void restoreVal(int val);


  /**
   * Returns the decision constraint assigning the domain to the specified value. The constraint is created if
   * it is not yet created.
   */

  public Constraint getDecisionConstraint(int val);


  /**
   * Returns the negated decision constraint.
   */

  public Constraint getNegDecisionConstraint(int val);

}
