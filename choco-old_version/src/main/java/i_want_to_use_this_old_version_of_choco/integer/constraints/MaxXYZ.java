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
public class MaxXYZ extends AbstractTernIntConstraint {
    public MaxXYZ(IntDomainVar x, IntDomainVar y, IntDomainVar max) {
        super(max, x, y);
    }

    public boolean isSatisfied(int[] tuple) {
        return (Math.max(tuple[2], tuple[1]) == tuple[0]);
    }

  public String pretty() {
    return "max(" + v2.pretty() + "," + v1.pretty() + ") = " + v0.pretty();
  }

    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx == 0) {
            v1.updateSup(v0.getSup(), cIdx1);
            v2.updateSup(v0.getSup(), cIdx2);
        } else {
            v0.updateSup(Math.max(v1.getSup(), v2.getSup()), cIdx0);
        }
    }

    public void awakeOnInf(int idx) throws ContradictionException {
        if (idx == 0) {
            if (v1.getInf() > v2.getSup()) {
                v1.updateInf(v0.getInf(), cIdx1);
            }

            if (v2.getInf() > v1.getSup()) {
                v2.updateInf(v0.getInf(), cIdx2);
            }
        } else {
            v0.updateInf(Math.max(v1.getInf(), v2.getInf()), cIdx2);
        }
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        if (idx == 0) {
            if (x > v2.getSup()) {
                v1.removeVal(x, cIdx1);
            }

            if (x > v1.getSup()) {
                v2.removeVal(x, cIdx2);
            }
        } else {
            if (!v1.canBeInstantiatedTo(x) && !v2.canBeInstantiatedTo(x)) {
                v0.removeVal(x, cIdx0);
            }
        }
    }

    public void awakeOnVar(int idx) throws ContradictionException {
        if (idx == 0) {
            v0.updateSup(Math.max(v1.getSup(), v2.getSup()), cIdx0);
            v0.updateInf(Math.max(v1.getInf(), v2.getInf()), cIdx0);

            if (v0.hasEnumeratedDomain()) {
                IntDomain dom0 = v0.getDomain();
                for (int valeur = dom0.getInf(); dom0.hasNextValue(valeur); valeur = dom0.getNextValue(valeur)) {
                    if (!v1.canBeInstantiatedTo(valeur) && !v2.canBeInstantiatedTo(valeur)) {
                        v0.removeVal(valeur, cIdx0);
                    }
                }
            }
        } else if (idx == 1) {
            v1.updateSup(v0.getSup(), cIdx1);
            if (v1.getInf() > v2.getSup()) {
                v1.updateInf(v0.getInf(), cIdx1);
            }

            if (v1.hasEnumeratedDomain()) {
                IntDomain dom1 = v1.getDomain();
                for (int valeur = dom1.getInf(); dom1.hasNextValue(valeur); valeur = dom1.getNextValue(valeur)) {
                    if (!v0.canBeInstantiatedTo(valeur) && (valeur > v2.getSup())) {
                        v1.removeVal(valeur, cIdx1);
                    }
                }
            }


        } else if (idx == 2) {
            v2.updateSup(v0.getSup(), cIdx2);
            if (v2.getInf() > v1.getSup()) {
                v2.updateInf(v0.getInf(), cIdx2);
            }
            if (v2.hasEnumeratedDomain()) {
                IntDomain dom2 = v2.getDomain();
                for (int valeur = dom2.getInf(); dom2.hasNextValue(valeur); valeur = dom2.getNextValue(valeur)) {
                    if (!v0.canBeInstantiatedTo(valeur) && (valeur > v1.getSup())) {
                        v2.removeVal(valeur, cIdx2);
                    }
                }
            }


        }
    }

    public void awakeOnInst(int idx, int val) throws ContradictionException {
        if (idx == 0) {
            v1.updateSup(val, cIdx1);
            v2.updateSup(val, cIdx2);
            if (!v1.canBeInstantiatedTo(val)) v2.instantiate(val, cIdx2);
            if (!v2.canBeInstantiatedTo(val)) v1.instantiate(val, cIdx1);
        } else if (idx == 1) {
            if (val > v2.getSup()) v0.instantiate(val, cIdx0);
        } else if (idx == 2) {
            if (val > v1.getSup()) v0.instantiate(val, cIdx0);
        }
    }


}


