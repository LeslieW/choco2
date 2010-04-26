/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package shaker.tools.search;

import choco.cp.solver.search.integer.varselector.*;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 5 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class IntVarSelectorFactory {

    public ArrayList<V> scope = new ArrayList<V>(10);

    public enum V {
        STATIC, DOMOVERDEG, DOMOVERDYNDEG, DOMOVERWDEG, MINDOMAIN, MAXDOMAIN,
        MAXREGRET, MINVALUEDOMAIN, MAXVALUEDOMAIN, MOSTCONSTRAINED, RANDOM
    }

    /**
     * Define a specific scope of value selector tuple to pick up in
     * @param vs the scope of value selector
     */
    public void scopes(V... vs){
        scope.clear();
        scope.addAll(Arrays.asList(vs));
    }

    /**
     * Select randomly (among scope if defined)
     * and return a value selector type
     * @param r random
     * @return type of value selector
     */
    public V any(Random r) {
        if(!scope.isEmpty()){
            return scope.get(r.nextInt(scope.size()));
        }
        V[] values = V.values();
        return values[r.nextInt(values.length)];
    }


    /**
     * Get one value selector among all
     * @param r random
     * @return value selector
     */
    public VarSelector<IntDomainVar> make(Random r, Solver s, IntDomainVar[] vars){
        return make(any(r), r, s, vars);
    }

    /**
     * Create and return the corresponding value selector
     * @param v the type of value selector
     * @param r random
     * @return value selector
     */
    public static VarSelector<IntDomainVar> make(V v, Random r, Solver s, IntDomainVar[] vars) {
        //Otherwise, select a new val selector
        VarSelector<IntDomainVar> ivs = null;

        switch (v) {
            case DOMOVERDEG:
                ivs = new DomOverDeg(s, vars);
                break;
            case DOMOVERDYNDEG:
                ivs = new DomOverDynDeg(s, vars);
                break;
            case DOMOVERWDEG:
                ivs = new DomOverWDeg(s, vars);
                break;
            case MAXDOMAIN:
                ivs = new MaxDomain(s, vars);
                break;
            case MAXREGRET:
                ivs = new MaxRegret(s, vars);
                break;
            case MAXVALUEDOMAIN:
                ivs = new MaxValueDomain(s, vars);
                break;
            case MINDOMAIN:
                ivs = new MinDomain(s, vars);
                break;
            case MINVALUEDOMAIN:
                ivs = new MinValueDomain(s, vars);
                break;
            case MOSTCONSTRAINED:
                ivs = new MostConstrained(s, vars);
                break;
            case RANDOM:
                ivs = new RandomIntVarSelector(s, vars, r.nextLong());
                break;
            case STATIC:
                ivs = new StaticVarOrder(s, vars);
                break;
        }
        return ivs;
    }
}