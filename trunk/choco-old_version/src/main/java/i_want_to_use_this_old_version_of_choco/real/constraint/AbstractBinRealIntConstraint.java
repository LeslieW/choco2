package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * Created by IntelliJ IDEA.
 * User: FLABURTHE
 * Date: 19 ao�t 2005
 * Time: 10:35:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBinRealIntConstraint extends AbstractConstraint {
  protected RealVar v0;
  protected int cIdx0;
  protected IntDomainVar v1;
  protected int cIdx1;

  public AbstractBinRealIntConstraint(RealVar v0, IntDomainVar v1) {
    this.v0 = v0;
    this.v1 = v1;
    this.problem = v0.getProblem();
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  // Variable management
  public RealVar getRealVar(int i) {
    if (i == 0) return v0;
    return null;
  }

  public int getRealVarNb() {
    return 1;
  }

  public IntDomainVar getIntVar(int i) {
    if (i == 0) return v1;
    return null;
  }

  public int getIntVarNb() {
    return 1;
  }

  public int getNbVars() {
    return 2;
  }

  public Var getVar(int i) {
    if (i == 0) return v0;
    if (i == 1) return v1;
    return null;
  }

  public void setVar(int i, Var v) {
    if (i == 0) {
      if (v instanceof RealVar) {
        this.v0 = (RealVar) v;
      } else {
        throw new Error("BUG in CSP network management: wrong type of Var for setVar");
      }
    } else if (i == 1) {
      if (v instanceof IntDomainVar) {
        this.v1 = (IntDomainVar) v;
      } else {
        throw new Error("BUG in CSP network management: wrong type of Var for setVar");
      }
    } else {
      throw new Error("BUG in CSP network management: too large index for setVar");
    }
  }

  public void setConstraintIndex(int i, int idx) {
    if (i == 0)
      cIdx0 = idx;
    else if (i == 1)
      cIdx1 = idx;
    else
      throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
  }

  public int getConstraintIdx(int idx) {
    if (idx == 0) return cIdx0;
    if (idx == 1) return cIdx1;
    return -1;
  }

  public boolean isCompletelyInstantiated() {
    return v1.isInstantiated() && v0.isInstantiated();
  }

	public boolean isSatisfied(int[] tuple) {
		throw new Error(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

}
