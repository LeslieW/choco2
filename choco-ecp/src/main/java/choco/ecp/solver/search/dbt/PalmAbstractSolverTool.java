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

public abstract class PalmAbstractSolverTool {
  protected PalmGlobalSearchStrategy manager;

  public PalmGlobalSearchStrategy getManager() {
    return manager;
  }

  public void setManager(PalmGlobalSearchStrategy manager) {
    this.manager = manager;
  }
}
