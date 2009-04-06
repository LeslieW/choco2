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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.shaker.tools.factory;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class MetaConstraintFactory {

    ConstraintFactory cf;

    public enum MC {AND, IFTHENELSE, IFONLYIF, IMPLIES, NOT, NONE, OR}

    public ArrayList<MC> scope = new ArrayList<MC>();


    /**
     * Declare factory dependencies
     * @param cf constraint factory
     */
    public void depends(ConstraintFactory cf){
        this.cf = cf;
    }

    /**
     * Define a specific scope of metaconstraint type to pick up in
     * @param mcs metaconstraint types
     */
    public void scopes(MC... mcs){
        scope.clear();
        scope.addAll(Arrays.asList(mcs));
    }

    /**
     * Select randomly (among scope if defined)
     * and return a metaconstraint type
     * @param r random
     * @return metaconstraint type
     */
    public MC any(Random r){
        if(scope.size()>0){
            return scope.get(r.nextInt(scope.size()));
        }
        MC[] values = MC.values();
        return values[r.nextInt(values.length)];
    }

    /**
     * Make a metaconstraint
     * @param r random
     * @return Constraint
     */
    public Constraint make(Random r) {
        return make(any(r), r);
    }


    /**
     * Make a specific metaconstraint
     * @param mc metaconstraint
     * @param r random
     * @return Constraint
     */
    public Constraint make(MC mc, Random r) {
        Constraint[] cs;
        switch (mc){
            case AND:
                return Choco.and(cf.make(r.nextInt(5), r));
            case IFTHENELSE:
                cs = cf.make(3, r);
                return  Choco.ifThenElse(cs[0], cs[1], cs[2]);
            case IFONLYIF:
                cs = cf.make(2, r);
                return  Choco.ifOnlyIf(cs[0], cs[1]);
            case IMPLIES:
                cs = cf.make(2, r);
                return  Choco.implies(cs[0], cs[1]);
            case NOT :
                cs = cf.make(1, r);
                return  Choco.not(cs[0]);
            case NONE:
                return cf.make(r);
            case OR :
                return Choco.or(cf.make(r.nextInt(5), r));
        }
        return null;
    }

}
