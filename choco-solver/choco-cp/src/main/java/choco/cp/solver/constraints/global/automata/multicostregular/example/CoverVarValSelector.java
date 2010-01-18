package choco.cp.solver.constraints.global.automata.multicostregular.example;

import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.memory.IStateInt;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 5 janv. 2010
 * Time: 13:39:56
 */
public class CoverVarValSelector extends AbstractIntVarSelector implements ValSelector {


    AbstractIntVarSelector other;

    IntDomainVar[][] vars;
    IntDomainVar selected;
    int nVal;

    IStateInt lastCol;
    int[][] lowb;

    public CoverVarValSelector(IntDomainVar[][] vars, int[][] lowb)
    {
        this.vars = vars;
        this.lowb = lowb;
        this.other = new StaticVarOrder(ArrayUtils.flatten(vars));

        lastCol = vars[0][0].getSolver().getEnvironment().makeInt(0);

    }
    public CoverVarValSelector(Solver s, IntegerVariable[][] mvars, int[][] lowb)
       {
           IntegerVariable[][] tmp = ArrayUtils.transpose(mvars);
           this.vars = new IntDomainVar[tmp.length][];
           for (int i = 0 ; i < this.vars.length ; i++)
               this.vars[i] = s.getVar(tmp[i]);

           this.lowb = lowb;

           this.other = new StaticVarOrder(ArrayUtils.flatten(vars));

           lastCol = vars[0][0].getSolver().getEnvironment().makeInt(0);
       }


    private int scanCol(int idx)
    {
        int[] tmp = new int[lowb[idx].length];
        IntDomainVar[] col = vars[idx];
        int[] low = lowb[idx];
        for (IntDomainVar v : col)
        {
            if (v.isInstantiated())
                tmp[v.getVal()]++;
        }
        for (int i = 0;  i < tmp.length ; i++)
        {
            if (tmp[i] < low[i])
                return i;
        }
        return Integer.MAX_VALUE;
    }

    private IntDomainVar getFirstVar(int idx)
    {
        for (IntDomainVar v : vars[idx])
            if (!v.isInstantiated())
                return v;
        return null;
    }

    @Override
    public IntDomainVar selectIntVar() throws ContradictionException {
        int tmp = 0;
        while (lastCol.get() < vars.length && (tmp = scanCol(lastCol.get())) == Integer.MAX_VALUE)
        {
            lastCol.add(1);
        }
        if (lastCol.get() == vars.length)
        {
            selected= other.selectIntVar();
            nVal = selected == null ? 0 : selected.getSup();

        }
        else
        {
            selected = getFirstVar(lastCol.get());
            nVal = tmp;
        }



        return selected;

    }

    @Override
    public int getBestVal(IntDomainVar x) {
        if (x == selected && x.canBeInstantiatedTo(nVal))
            return nVal;
        else
            return x.getSup();

    }
}
