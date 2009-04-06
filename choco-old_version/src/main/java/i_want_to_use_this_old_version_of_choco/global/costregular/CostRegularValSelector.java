package i_want_to_use_this_old_version_of_choco.global.costregular;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.ValSelector;
import i_want_to_use_this_old_version_of_choco.search.AbstractSearchHeuristic;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 12, 2007
 * Time: 12:37:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class CostRegularValSelector extends AbstractSearchHeuristic implements ValSelector {

    int[] offset;
    boolean max;
    CostRegular cr;
    public CostRegularValSelector(CostRegular cr,boolean max)
    {
        this.cr = cr;
        this.max = max;
    }

    public int getBestVal(IntDomainVar x) {
        int idx = x.getVarIndex(cr.getConstraintIdx(0));
        if (idx == cr.myVars.length)
            return x.getInf();
        else
        {
            CostRegular.State s = null;//cr.source;
            CostRegular.State next = cr.source;
            for (int i = 0 ; i <= idx ; i++)
            {
                s = next;
                next = cr.getLayer(i+1).get(max ? s.pgcSucc.get() : s.pccSucc.get());
            }
            int bestCost = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            CostRegular.Arc out = null;
            for (int k = 0; k < s.outArcs.size() ; k++)
            {
                CostRegular.Arc n = (CostRegular.Arc) s.outArcs.get(k);
                if (n.getArcOrigin().equals(s) && n.getArcDestination().equals(next))
                {
                    int cost = cr.getCost(idx,n.getArcLabel());
                    boolean b = (max ? (bestCost < cost) : (bestCost > cost));
                    if (b)
                    {
                        bestCost = cost;
                        out = n;
                    }
                }

            }
            return out.getArcLabel();

        }
    }
}
