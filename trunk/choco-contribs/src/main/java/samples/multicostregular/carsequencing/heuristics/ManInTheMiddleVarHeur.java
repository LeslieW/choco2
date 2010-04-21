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
package samples.multicostregular.carsequencing.heuristics;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 28, 2009
 * Time: 7:37:45 PM
 */
public class ManInTheMiddleVarHeur extends AbstractIntVarSelector {


    int center;
    boolean l;


    public ManInTheMiddleVarHeur(Solver solver, IntDomainVar[] vars)
    {
    	super(solver, vars);
        this.l = true;
        center =   this.vars.length/2;
    }


    public IntDomainVar selectVar()
    {
        IntDomainVar out = null;

        if (l)
        {
            l=!l;
            for (int i = center ; i >= 0 ; i--)
                if(!vars[i].isInstantiated())
                    return vars[i];
            for (int i = center+1 ; i < vars.length ; i++)
                if(!vars[i].isInstantiated())
                    return vars[i];

        }
        else        {
            l=!l;
            for (int i = center+1 ; i < vars.length ; i++)
                if(!vars[i].isInstantiated())
                    return vars[i];

            for (int i = center ; i >= 0 ; i--)
                if(!vars[i].isInstantiated())
                    return vars[i];

        }

        return out;

    }
}