//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 7 janv. 2004
 * Time: 15:43:05
 * To change this template use Options | File Templates.
 */
public class TraceState extends PalmState {

  public TraceState(PalmExplanation expl) {
    super(expl);
  }

  public void addDecision(AbstractConstraint constraint) {
    logger.info("Constraint " + constraint + " added.");
    super.addDecision(constraint);
  }

  public void removeDecision(AbstractConstraint constraint) {
    logger.info("Constraint " + constraint + " removed.");
    super.removeDecision(constraint);
  }
}
