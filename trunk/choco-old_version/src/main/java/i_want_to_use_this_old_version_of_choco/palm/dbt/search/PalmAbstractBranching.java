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

import i_want_to_use_this_old_version_of_choco.branch.AbstractBranching;
import i_want_to_use_this_old_version_of_choco.branch.ObjectBranching;

/**
 * Abstract class for branching algorithms.
 */

public abstract class PalmAbstractBranching extends AbstractBranching implements ObjectBranching {

  /**
   * The extender the branching which uses this branching.
   */

  protected PalmExtend extender;


  /**
   * Gets the extender which uses this branching.
   */

  public PalmExtend getExtender() {
    return extender;
  }


  /**
   * Sets the extender which uses this branching.
   */

  public void setExtender(PalmExtend extender) {
    this.extender = extender;
  }


//  /**
//   * Checks if decisions are acceptable.
//   * @param csts The decisions that will be taken.
//   */

//  public abstract boolean checkAcceptable(List csts);


//  /**
//   * Learns from rejection for unacceptable decisions.
//   */

//  public abstract void learnFromRejection();

}
