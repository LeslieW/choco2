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
import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ConstraintFactory {

    OperatorFactory of;
    VariableFactory vf;


    public enum C {
        ALLDIFFERENT, BALLDIFFERENT, /*GCC_1, GCC_2, BGCC_1, BGCC_2, BGCC_3,
        CUMULATIVE_1, CUMULATIVE_2, CUMULATIVE_3, CUMULATIVE_4, CUMULATIVE_5,
        CUMULATIVE_6, CUMULATIVE_7, CUMULATIVE_8, CUMULATIVE_9,
        DIJUNCTIVE_1,  DIJUNCTIVE_2, DIJUNCTIVE_3, DIJUNCTIVE_4,  DIJUNCTIVE_5,
        ELEMENT_1, ELEMENT_2, ELEMENT_3, ELEMENT_4, ELEMENT_5,
        DISJUNCTIVE,*/ LEXLESS, LEXLESSEQ,
        DISTANCE_EQ_1, DISTANCE_EQ_2, DISTANCE_EQ_3, DISTANCE_NEQ,
        DISTANCE_GT_1, DISTANCE_GT_2, DISTANCE_GT_3, DISTANCE_LT_1, DISTANCE_LT_2, DISTANCE_LT_3,
        MIN_1, MIN_2, MIN_3, MIN_4, MAX_1, MAX_2, MAX_3, MAX_4,

        EQ, FALSE, GEQ, GT, LEQ, LT, NEQ, REIFIEDINTCONSTRAINT_1,
        REIFIEDINTCONSTRAINT_2, SIGNOPP, SAMESIGN, TRUE
    }

    /**
     * Declare factory dependencies
     * @param of
     */
    public void depends(OperatorFactory of, VariableFactory vf){
        this.of = of;
        this.vf = vf;
    }

    public ArrayList<C> scope = new ArrayList<C>();


    /**
     * Define a specific scope of constraint type to pick up in
     * @param cs
     */
    public void scopes(C... cs){
        scope.clear();
        for(int i = 0; i < cs.length; i++){
            scope.add(cs[i]);
        }
    }

    /**
     * Select randomly (among scope if defined)
     * and return a constraint type
     * @param r
     * @return
     */
    public C any(Random r) {
        if(scope.size()>0){
            return scope.get(r.nextInt(scope.size()));
        }
        C[] values = C.values();
        return values[r.nextInt(values.length)];
    }

    /**
     * Make a constraint
     * @param r
     * @return
     */
    public Constraint make(Random r) {
        return make(any(r), r);
    }

    /**
     * Make a specific constraint
     * @param c
     * @param r
     * @return
     */
    public Constraint make(C c, Random r) {
        switch (c) {
            case ALLDIFFERENT:
                return Choco.allDifferent( "cp:ac", of.make(7, r));
            case BALLDIFFERENT:
                return Choco.allDifferent( "cp:bc", of.make(7, r));
            case DISTANCE_EQ_1:
                return Choco.distanceEQ(vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case DISTANCE_EQ_2:
                return Choco.distanceEQ(vf.make(r), vf.make(r), vf.make(r));
            case DISTANCE_EQ_3:
                return Choco.distanceEQ(vf.make(r), vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case DISTANCE_GT_1:
                return Choco.distanceGT(vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case DISTANCE_GT_2:
                return Choco.distanceGT(vf.make(r), vf.make(r), vf.make(r));
            case DISTANCE_GT_3:
                return Choco.distanceGT(vf.make(r), vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case DISTANCE_LT_1:
                return Choco.distanceLT(vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case DISTANCE_LT_2:
                return Choco.distanceLT(vf.make(r), vf.make(r), vf.make(r));
            case DISTANCE_LT_3:
                return Choco.distanceLT(vf.make(r), vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case DISTANCE_NEQ:
                return Choco.distanceNEQ(vf.make(r), vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2);
            case EQ:
                return Choco.eq(of.make(r), of.make(r));
            case FALSE:
                return Choco.FALSE;
            case GEQ:
                return Choco.geq(of.make(r), of.make(r));
            case GT:
                return Choco.gt(of.make(r), of.make(r));
            case LEQ:
                return Choco.leq(of.make(r), of.make(r));
            case LEXLESS:
                return Choco.lex(of.make(5, r), of.make(5, r));
            case LEXLESSEQ:
                return Choco.lexeq(of.make(5, r), of.make(5, r));
            case LT:
                return Choco.lt(of.make(r), of.make(r));
            case MAX_1:
                return Choco.max(vf.make(7, r), vf.make(r));
            case MAX_2:
                return Choco.max(vf.make(r), vf.make(r), vf.make(r));
            case MAX_3:
                return Choco.max(r.nextInt(vf.dsize)-vf.dsize/2, vf.make(r), vf.make(r));
            case MAX_4:
                return Choco.max(vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2, vf.make(r));
            case MIN_1:
                return Choco.min(vf.make(7, r), vf.make(r));
            case MIN_2:
                return Choco.min(vf.make(r), vf.make(r), vf.make(r));
            case MIN_3:
                return Choco.min(r.nextInt(vf.dsize)-vf.dsize/2, vf.make(r), vf.make(r));
            case MIN_4:
                return Choco.min(vf.make(r), r.nextInt(vf.dsize)-vf.dsize/2, vf.make(r));
            case NEQ:
                return Choco.neq(of.make(r), of.make(r));
            case REIFIEDINTCONSTRAINT_1:
                return Choco.reifiedIntConstraint(vf.make(VariableFactory.V.BOOLVAR, r), make(r));
            case REIFIEDINTCONSTRAINT_2:
                return Choco.reifiedIntConstraint(vf.make(VariableFactory.V.BOOLVAR, r), make(r), make(r));
            case SIGNOPP:
                return Choco.oppositeSign(of.make(r), of.make(r));
            case SAMESIGN:
                return Choco.sameSign(of.make(r), of.make(r));
            case TRUE:
                return Choco.TRUE;
        }
        return null;
    }


    /**
     * Make an array of constraints
     * @param nb
     * @param r
     * @return
     */
    public Constraint[] make(int nb, Random r) {
        Constraint[] cs = new Constraint[nb];
        for(int i = 0 ; i < nb; i++){
            cs[i] = make(r);
        }
        return cs;
    }

    /**
     * Make an array of specific constraints
     * @param nb
     * @param r
     * @return
     */
    public Constraint[] make(int nb, C c, Random r) {
        Constraint[] cs = new Constraint[nb];
        for(int i = 0 ; i < nb; i++){
            cs[i] = make(c, r);
        }
        return cs;
    }


}
