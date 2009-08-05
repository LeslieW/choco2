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
package choco.shaker.tools.search;

import choco.cp.solver.search.integer.branching.*;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractIntBranching;
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
public class IntBranchingFactory {

    public ArrayList<V> scope = new ArrayList<V>();

    IntVarSelectorFactory varsf;
    IntValSelectorFactory valsf;
    IntValIteratorFactory valif;


    public enum V {
        ASSIGNVAR, ASSIGNORFORBID, DOMOWERWDEGBIN, DOMOWERWDEG, IMPACT
    }

    public IntBranchingFactory() {
        varsf = new IntVarSelectorFactory();
        valsf = new IntValSelectorFactory();
        valif = new IntValIteratorFactory();
    }

    /**
     * Declare factory dependencies
     * @param varsf
     * @param valsf
     * @param valif
     */
    public void depends(IntVarSelectorFactory varsf, IntValSelectorFactory valsf, IntValIteratorFactory valif){
        this.varsf = varsf;
        this.valsf = valsf;
        this.valif = valif;
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
        if(scope.size()>0){
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
    public AbstractIntBranching make(Random r, Solver s, IntDomainVar[] vars){
        return make(any(r), r, s, vars);
    }

    /**
     * Create and return the corresponding value selector
     * @param v the type of value selector
     * @param r random
     * @return value selector
     */
    public AbstractIntBranching make(V v, Random r, Solver s, IntDomainVar[] vars) {
        //Otherwise, select a new val selector
        AbstractIntBranching ib = null;

        switch (v) {
            case ASSIGNORFORBID:
                ib = new AssignOrForbidIntVarVal(varsf.make(r, s, vars), valsf.make(r));
                break;
            case ASSIGNVAR:
                if (r.nextInt(2) == 1) {
                    ib = new AssignVar(varsf.make(r, s, vars), valsf.make(r));
                } else {
                    ib = new AssignVar(varsf.make(r, s, vars), valif.make(r));
                }
                break;
            case DOMOWERWDEG:
                ib = new DomOverWDegBranching2(s, valif.make(r), vars);
                break;

            case DOMOWERWDEGBIN:
                ib = new DomOverWDegBinBranching2(s, valsf.make(r), vars);
                break;
            case IMPACT:
                ib = new ImpactBasedBranching(s, vars);
                break;
        }
        return ib;
    }
}
