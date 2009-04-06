package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;

/**
 * Created by IntelliJ IDEA.
 * User: Richaud
 * Date: 8 janv. 2007
 * Time: 15:45:35
 * To change this template use File | Settings | File Templates.
 */
public class MinXYZ extends AbstractTernIntConstraint {
    public MinXYZ(IntDomainVar x, IntDomainVar y, IntDomainVar min) {
        super(min, x, y);
    }

    public boolean isSatisfied(int[] tuple) {
        return (Math.min(tuple[2], tuple[1]) == tuple[0]);
    }

  public String pretty() {
    return "min(" + v2.pretty() + "," + v1.pretty() + ") = " + v0.pretty();
  }

    public void awakeOnInf(int idx) throws ContradictionException {
        if (idx == 0) {
            v1.updateInf(v0.getInf(), cIdx0);
            v2.updateInf(v0.getInf(), cIdx0);
        } else {
            v0.updateInf(Math.min(v1.getInf(), v2.getInf()), cIdx0);
        }
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx == 0) {
            if (v1.getInf() > v2.getSup()) {
                v2.updateSup(v0.getSup(), cIdx2);
            }
            if (v2.getInf() > v1.getSup()) {
                v1.updateSup(v0.getSup(), cIdx1);
            }
        } else {
            v0.updateSup(Math.min(v1.getSup(), v2.getSup()), cIdx0);
        }
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        if (idx == 0) {
            if (v1.getInf() > x) {
                v2.removeVal(x, cIdx2);
            }
            if (v2.getInf() > x) {
                v1.removeVal(x, cIdx1);
            }
        } else {
            if (!v1.canBeInstantiatedTo(x) && !v2.canBeInstantiatedTo(x)) {
                v0.removeVal(x, cIdx0);
            }
        }
    }

    public void awakeOnVar(int idx) throws ContradictionException {
        if (idx == 0) {
            v0.updateInf(Math.min(v1.getInf(), v2.getInf()), cIdx0);
            v0.updateSup(Math.min(v1.getSup(), v2.getSup()), cIdx0);

            if (v0.hasEnumeratedDomain()) {
                IntDomain dom0 = v0.getDomain();
                for (int valeur = dom0.getInf(); dom0.hasNextValue(valeur); valeur = dom0.getNextValue(valeur)) {
                    if (!v1.canBeInstantiatedTo(valeur) && !v2.canBeInstantiatedTo(valeur)) {
                        v0.removeVal(valeur, cIdx0);
                    }
                }
            }
        } else if (idx == 1) {
            v1.updateInf(v0.getInf(), cIdx1);

            if (v1.getSup() < v2.getInf()) {
                v1.updateSup(v0.getSup(), cIdx1);
            }
            if (v1.hasEnumeratedDomain()) {
                IntDomain dom1 = v1.getDomain();
                for (int valeur = dom1.getInf(); dom1.hasNextValue(valeur); valeur = dom1.getNextValue(valeur)) {
                    if (!v0.canBeInstantiatedTo(valeur) && (v2.getInf() > valeur)) {
                        v1.removeVal(valeur, cIdx1);
                    }
                }

            }

        } else if (idx == 2) {
            v2.updateInf(v0.getInf(), cIdx2);
            if (v2.getSup() < v1.getInf()) {
                v2.updateSup(v0.getSup(), cIdx2);
            }
            if (v2.hasEnumeratedDomain()) {
                IntDomain dom2 = v2.getDomain();
                for (int valeur = dom2.getInf(); dom2.hasNextValue(valeur); valeur = dom2.getNextValue(valeur)) {
                    if (!v0.canBeInstantiatedTo(valeur) && (v1.getInf() > valeur)) {
                        v2.removeVal(valeur, cIdx2);
                    }
                }

            }

        }
    }

    public void awakeOnInst(int idx, int val) throws ContradictionException {
        if (idx == 0) {
            v1.updateInf(val, cIdx1);
            v2.updateInf(val, cIdx2);
            if (!v1.canBeInstantiatedTo(val)) v2.instantiate(val, cIdx2);
            if (!v2.canBeInstantiatedTo(val)) v1.instantiate(val, cIdx1);
        } else if (idx == 1) {
            //TODO ??
            if (val < v2.getInf()) v0.instantiate(val, cIdx0);
        } else if (idx == 2) {
            //TODO ??
            if (val < v1.getInf()) v0.instantiate(val, cIdx0);
        }
    }


}


