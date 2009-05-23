package choco.cp.solver.constraints.global.multicostregular.nsp;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractLargeIntBranching;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 9, 2008
 * Time: 1:03:46 PM
 */
public class NSPBranching extends AbstractLargeIntBranching {

    NSPVarSelector varselec;
    NSPValSelector valselec;
    IntDomainVar nextVar;

    public NSPBranching(NSPVarSelector varselec, NSPValSelector valselec)
    {
        this.varselec = varselec;
        this.valselec = valselec;
    }

    public int getFirstBranch(Object x) {
        return valselec.getBestVal((IntDomainVar)x);
    }

    public int getNextBranch(Object x, int i) {
        return Integer.MAX_VALUE;

    }

    public boolean finishedBranching(Object x, int i) {
        return (((IntDomainVar)x).getDomainSize() == 0 || varselec.selectIntVar() == null);

    }

    public Object selectBranchingObject() throws ContradictionException {
        return varselec.selectIntVar();
    }

    public void goDownBranch(Object x, int i) throws ContradictionException {
        IntDomainVar v;
        int val;
        if (i < Integer.MAX_VALUE)
        {
            v = (IntDomainVar) x;
            val = i;
        }
        else
        {
            v = varselec.selectIntVar();
            val = valselec.getBestVal(v);
        }
        super.goDownBranch(v, val);
        v.setVal(val);

    }

    public void goUpBranch(Object x, int i) throws ContradictionException {
        IntDomainVar v;
        int val;
        if (i < Integer.MAX_VALUE)
        {
            v = (IntDomainVar) x;
            val = i;
        }
        else
        {
            v = varselec.selectIntVar();
            val = valselec.getBestVal(v);
        }
        super.goUpBranch(v, val);
        v.remVal(val);
    }
}
