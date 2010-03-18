package choco.cp.solver.constraints.global.automata.fast_costregular;

import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Mar 18, 2010
 * Time: 9:22:14 AM
 */
public class FCostRegularValSelector extends AbstractSearchHeuristic implements ValSelector {

    boolean max;
    FastCostRegular cr;
    public FCostRegularValSelector(FastCostRegular cr,boolean max)
    {
        this.cr = cr;
        this.max = max;
    }

    public int getBestVal(IntDomainVar x) {
        int idx = x.getVarIndex(cr.getConstraintIdx(0));
        if (idx == cr.vs.length)
            return max ? x.getSup(): x.getInf();
        else
        {
            int s = cr.graph.sourceIndex;
            int e = -1;
            for (int i = 0 ; i <= idx ; i++)
            {
                e = max ? cr.graph.GNodes.nextLP.get(s) : cr.graph.GNodes.nextSP.get(s);
                s = cr.graph.GArcs.dests[e];
            }
            return cr.graph.GArcs.values[e];
        }
    }
}
