//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.search.dbt;

import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.kernel.solver.constraints.AbstractSConstraint;

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

  public void addDecision(AbstractSConstraint constraint) {
    logger.info("Constraint " + constraint + " added.");
    super.addDecision(constraint);
  }

  public void removeDecision(AbstractSConstraint constraint) {
    logger.info("Constraint " + constraint + " removed.");
    super.removeDecision(constraint);
  }
}
