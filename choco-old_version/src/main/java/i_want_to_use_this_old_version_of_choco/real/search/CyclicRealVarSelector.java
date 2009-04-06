package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * A cyclic variable selector : since a dichotomy algorithm is used, cyclic assiging is nedded for instantiate
 * a real interval variable.
 */
public class CyclicRealVarSelector extends AbstractRealVarSelector implements RealVarSelector {
  protected int current;
  //protected double precision = 1.e-6;

  public CyclicRealVarSelector(AbstractProblem pb) {
    this.problem = pb;
    current = -1;
  }

  public RealVar selectRealVar() {
    int nbvars = problem.getNbRealVars();
    if (nbvars == 0) return null;
    int start = current == -1 ? nbvars - 1 : current;
    int n = (current + 1) % nbvars;
    while (n != start && problem.getRealVar(n).isInstantiated()) {
      n = (n + 1) % nbvars;
    }
    if (problem.getRealVar(n).isInstantiated()) return null;
    current = n;
    return problem.getRealVar(n);
  }
}
