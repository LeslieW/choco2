package samples.jobshop;

import java.util.HashSet;

import choco.cp.solver.constraints.strong.ISpecializedConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class SimpleDTConstraint extends AbstractBinIntSConstraint implements
        ISpecializedConstraint {

    final private int[] duration;

   // private static final int[] tuple = new int[2];

    public SimpleDTConstraint(IntDomainVar x0, IntDomainVar x1, int duration0,
            int duration1) {
        super(x0, x1);
        duration = new int[] { duration0, duration1 };
    }

    @Override
    public void awakeOnBounds(int varIdx) throws ContradictionException {
        propagate(1 - varIdx);
    }

    @Override
    public void awakeOnInf(int varIdx) throws ContradictionException {
        propagate(1 - varIdx);
    }

    @Override
    public void awakeOnSup(int varIdx) throws ContradictionException {
        propagate(1 - varIdx);
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        //
    }

    public void awakeOnInst(int idx) throws ContradictionException {
        propagate(1 - idx);
    }

    public void propagate() throws ContradictionException {
        propagate(0);
        propagate(1);
    }

    @Override
    public boolean isSatisfied(int[] tuple) {
        final int difference = tuple[0] - tuple[1];
        return (-difference >= duration[0] || difference >= duration[1]);
    }

     private void propagate(int position) throws ContradictionException {
        final IntDomainVar variable = getIntVar(position);
        final IntDomainVar otherVariable = getIntVar(1 - position);

        final int lBound = otherVariable.getSup() - duration[position];

        final int hBound = otherVariable.getInf() + duration[1 - position];

        if (lBound >= hBound) {
            return;
        }

        variable.removeInterval(lBound + 1, hBound - 1, position == 0 ? cIdx0
                : cIdx1);

    }

//    private void propagate(int position) throws ContradictionException {
//        final IntDomainVar variable = getIntVar(position);
//
//        final DisposableIntIterator itr = variable.getDomain().getIterator();
//        try {
//            while (itr.hasNext()) {
//                final int a = itr.next();
//                if (firstSupport(position, a) == Integer.MAX_VALUE) {
//                    variable.removeVal(a, position == 0 ? cIdx0 : cIdx1);
//                }
//            }
//        } finally {
//            itr.dispose();
//        }
//    }

    public static class SimpleDTConstraintManager implements ConstraintManager {

        @Override
        public int[] getFavoriteDomains(HashSet<String> options) {
            return new int[] { IntDomainVar.BITSET };
        }

        @Override
        public SConstraint makeConstraint(Solver solver, Variable[] variables,
                Object parameters, HashSet<String> options) {
            final int[] durations = (int[]) parameters;
            final IntDomainVar[] sv = new IntDomainVar[variables.length];
            for (int i = variables.length; --i >= 0;) {
                sv[i] = solver.getVar((IntegerVariable) variables[i]);
            }
            return new SimpleDTConstraint(sv[0], sv[1], durations[0],
                    durations[1]);
        }

        @Override
        public INode makeNode(Solver solver, Constraint[] cstrs,
                IntegerExpressionVariable[] vars) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Override
    public boolean check(int[] tuple) {
        return isSatisfied(tuple);
    }

    /**
     * @param position
     * @param value
     * @param last
     * @return last if allowed, first allowed else
     */
    int nextAllowed(int position, int value, int last) {
        if (last <= value - duration[1 - position]
                || last >= value + duration[position]) {
            return last;
        }
        return value + duration[position];
    }

    int nextSupportFrom(int position, int value, int last) {
        int current = nextAllowed(position, value, last);
        final IntDomainVar var = getIntVar(1 - position);

        while (!var.canBeInstantiatedTo(current)) {
            current = var.getNextDomainValue(current);
            if (current == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }

            current = nextAllowed(position, value, current);
        }
        return current;
    }

     @Override
    public int firstSupport(int position, int value) {
        return nextSupportFrom(position, value, getIntVar(1 - position)
                .getInf());
    }

    @Override
    public int nextSupport(int position, int value, int lastSupport) {
        final int next = getIntVar(1 - position)
                .getNextDomainValue(lastSupport);
        if (next == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return nextSupportFrom(position, value, next);
    }


}
