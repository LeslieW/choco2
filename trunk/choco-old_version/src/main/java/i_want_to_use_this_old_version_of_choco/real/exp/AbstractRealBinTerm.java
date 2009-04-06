package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

import java.util.List;
import java.util.Set;

/**
 * A binary real expression.
 */
public abstract class AbstractRealBinTerm extends AbstractRealCompoundTerm {
  protected RealExp exp1, exp2;

  public AbstractRealBinTerm(AbstractProblem pb, RealExp exp1, RealExp exp2) {
    super(pb);
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public List subExps(List l) {
    exp1.subExps(l);
    exp2.subExps(l);
    l.add(this);
    return l;
  }

  public Set collectVars(Set s) {
    exp1.collectVars(s);
    exp2.collectVars(s);
    return s;
  }

  public boolean isolate(RealVar var, List wx, List wox) {
    boolean dependsOnX = exp1.isolate(var, wx, wox) | exp2.isolate(var, wx, wox);
    if (dependsOnX) wx.add(this); else wox.add(this);
    return dependsOnX;
  }
}
