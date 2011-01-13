package choco.cp.solver.constraints.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractLargeSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

/**
 * Created by IntelliJ IDEA.
 * User: njussien
 * Date: 15 déc. 2010
 * Time: 12:59:12
 <p/>
 * X collection set-variable
 * Y collection set-variable
 *
 * Y should have enough slots to handle X domain size (ie. Y.length <= X.max)
 *
 * j in X[i]  <=> i in Y[j]
 *
 * cf. http://www.emn.fr/z-info/sdemasse/gccat/Cinverse_set.html
 *
 *
 * <p/>
 * */
public class InverseSet extends AbstractLargeSetSConstraint {

    int varoffset;
    SetVar[] x;
    SetVar[] y;

    public InverseSet(SetVar[] x, SetVar[] y) {
        super(ArrayUtils.append(x, y));
        varoffset = x.length;
        this.x = x;
        this.y = y;
    }

    @Override
    public void awake() throws ContradictionException {

        // première étape : nettoyer les domaines initiaux
        int maxYvalue = Integer.MIN_VALUE;
        for (SetVar var : y) {
            maxYvalue = Math.max(maxYvalue, var.getEnveloppeSup());
        }

        int maxXvalue = Integer.MIN_VALUE;
        for (SetVar var : x) {
            maxXvalue = Math.max(maxXvalue, var.getEnveloppeSup());
        }

        // x should not take values from y.length to maxXvalue
        for (int i = y.length ; i <= maxXvalue ; i++) {
            for (SetVar var : x) {
               var.remFromEnveloppe(i, this, false);
            }
        }

        // y should not take values from x.length to maxYvalue
        for (int i = x.length ; i <= maxYvalue ; i++) {
            for (SetVar var : y) {
               var.remFromEnveloppe(i, this, false);
            }
        }
    }

    @Override
    public void propagate() throws ContradictionException {

        boolean allinstance = true;
        for (SetVar var : vars) {
            if (! var.isInstantiated()) {
                allinstance = false;
                break;
            }
        }

        if (allinstance && ! isSatisfied()) this.fail();
    }


    // X[varIdx] <- x
    // Y[x] <- varIdx

    @Override
    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        int var = (varIdx < varoffset) ? x + varoffset : x;
        int val = (varIdx < varoffset) ? varIdx : varIdx - varoffset;
        vars[var].addToKernel(val, this, false);
    }

    @Override
    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        int var = (varIdx < varoffset) ? x + varoffset : x;
        int val = (varIdx < varoffset) ? varIdx : varIdx - varoffset;
        vars[var].remFromEnveloppe(val, this, false);
    }

    @Override
    public boolean isConsistent() {
        return isSatisfied();  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isSatisfied() {
        for (int i = 0; i < vars.length; i++) {
            SetVar var = vars[i];
            DisposableIntIterator itker = var.getDomain().getKernelIterator();

            while (itker.hasNext()) {
                int val = itker.next();
                int ov = (i < varoffset) ? val + varoffset : val;
                int v  = (i < varoffset) ? i : i - varoffset;

                if (! vars[ov].isInDomainKernel(v)) {
                    return false;
                }
            }
        }

        return true;
    }
}
