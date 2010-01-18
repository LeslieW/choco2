/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.automata.costregular;


import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jan 31, 2008
 * Time: 3:09:00 PM
 */
public class CostKnapsack extends CostRegular
{

    IntDomainVar bVar;
    IntDomainVar cVar;
    int[] cost;
    int[] gain;


    private static IntDomainVar[] merge(IntDomainVar[] vars, IntDomainVar bound, IntDomainVar cost)
    {
        IntDomainVar[] nyv = new IntDomainVar[vars.length+2];
        System.arraycopy(vars,0,nyv,0,vars.length);
        nyv[vars.length] = bound;
        nyv[vars.length+1] = cost;
        return nyv;
    }

    public static CostKnapsack make(IntDomainVar[] vars, IntDomainVar bVar, IntDomainVar cVar, int[] cost, int[] gain)
    {
        return new CostKnapsack(vars,bVar,cVar,cost,gain);
    }

    protected CostKnapsack(IntDomainVar[] vars, IntDomainVar bVar, IntDomainVar cVar,  int[] cost, int[] gain)
    {
        super(merge(vars,bVar,cVar),null,null);
        this.bVar = bVar;
        this.cVar = cVar;
        this.cost = cost;
        this.gain = gain;
     

    }


    protected int getCost(int i, int j)
    {

        if (i >= myVars.length -1)
            return 0;
        else
            return j * gain[i];
    }

    protected int delta(int i, int j, int k)
    {
        if (i == myVars.length -1)
        {
            return (j==k)?0:-1;
        }
        else
        {
            int lgth = k+(cost[i])*(j+offset[i]);
            if (lgth <= bVar.getSup())
                return lgth;
            else
                return -1;
        }
    }

    protected boolean isAccepting(int idx)
    {
        return idx == 0;
    }

    protected int getStart()
    {
        return 0;
    }


    public static int findIdx(ArrayList<Integer> arr,int el)
    {
        int i = 0;
        boolean stop = false;
        while (i < arr.size() && !stop)
        {
            if (arr.get(i) > el)
                i++;
            else
                stop = true;
        }
        return i;
    }




    /**
     * <i>Semantic:</i>
     * Testing if the constraint is satisfied.
     * Note that all variables involved in the constraint must be
     * instantiated when this method is called.
     */

    public boolean isSatisfied() {
        return true;
    }



}
