/*
 * Created on 19 août 08 by coletta 
 *
 */
package choco.cp.solver.constraints.set;

import choco.cp.solver.variables.set.SetVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractLargeSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

import java.util.HashMap;
import java.util.Map;

public final class AllDisjoint extends AbstractLargeSetSConstraint {

    public AllDisjoint(SetVar[] setvars) {
        super(setvars);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return SetVarEvent.INSTSET_MASK + SetVarEvent.ADDKER_MASK;
    }

    public void filter(int idx) throws ContradictionException {
        DisposableIntIterator it = vars[idx].getDomain().getKernelIterator();
        try {
            while (it.hasNext()) {
                int val = it.next();
                for (int idxi = 0; idxi < vars.length; idxi++)
                    if (idxi != idx) vars[idxi].remFromEnveloppe(val, this, false);
            }

        } finally {
            it.dispose();
        }
    }

    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        for (int idx = 0; idx < vars.length; idx++)
            if (idx != varIdx) vars[idx].remFromEnveloppe(x, this, false);
    }

    public void awakeOnInst(int varIdx) throws ContradictionException {
        filter(varIdx);
    }

    public void propagate() throws ContradictionException {
        for (int idx = 0; idx < vars.length; idx++)
            filter(idx);
    }

    public boolean isSatisfied() {
        DisposableIntIterator it = null;
        try {
            Map<Integer, SetVar> map = new HashMap<Integer, SetVar>();
            for (SetVar v : vars) {
                it = v.getDomain().getKernelIterator();
                while (it.hasNext()) {
                    int val = it.next();
                    if (map.get(val) != null) return false;
                    map.put(val, v);
                }
                it.dispose();
            }
            for (SetVar v : vars) {
                it = v.getDomain().getEnveloppeIterator();
                while (it.hasNext()) {
                    int val = it.next();
                    SetVar v2 = map.get(val);
                    if (v2 != null && !v2.equals(v))
                        return false;
                }
                it.dispose();
            }
            return true;
        } finally {
            it.dispose();
        }
    }

    public boolean isConsistent() {
        return isSatisfied();
    }

    public String pretty() {
        StringBuilder sb = new StringBuilder();
        sb.append("AllDisjoint({");
        for (int i = 0; i < vars.length; i++) {
            if (i > 0) sb.append(", ");
            SetVar var = vars[i];
            sb.append(var.pretty());
        }
        sb.append("})");
        return sb.toString();
    }


    public String toString() {
        String autstring = "AllDisjoint : ";
        for (int i = 0; i < vars.length; i++) {
            autstring += vars[i] + " ";
        }
        return autstring;
    }

}
