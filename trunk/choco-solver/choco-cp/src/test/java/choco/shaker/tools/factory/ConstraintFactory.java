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
import choco.kernel.model.variables.integer.IntegerVariable;

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
public class ConstraintFactory {

    OperatorFactory of;
    VariableFactory vf;


    public enum C {
        ALLDIFFERENT, BALLDIFFERENT,
        GCC,
        BGCC_1, //BGCC_2, BGCC_3,
//        CUMULATIVE_1, CUMULATIVE_2, CUMULATIVE_3, CUMULATIVE_4, CUMULATIVE_5,
//        CUMULATIVE_6, CUMULATIVE_7, CUMULATIVE_8, CUMULATIVE_9,
//        DIJUNCTIVE_1,  DIJUNCTIVE_2, DIJUNCTIVE_3, DIJUNCTIVE_4,  DIJUNCTIVE_5,
//        ELEMENT_1, ELEMENT_2, ELEMENT_3, ELEMENT_4, ELEMENT_5,
        LEXLESS, LEXLESSEQ,
        DISTANCE_EQ_1, DISTANCE_EQ_2, DISTANCE_EQ_3,
        DISTANCE_NEQ,
        DISTANCE_GT_1, DISTANCE_GT_2, DISTANCE_GT_3,
        DISTANCE_LT_1, DISTANCE_LT_2, DISTANCE_LT_3,
        MIN_1, MIN_2, MIN_3, MIN_4,
        MAX_1, MAX_2, MAX_3, MAX_4,
        EQ, GEQ, GT, LEQ, LT, NEQ,
        REIFIEDINTCONSTRAINT_1, REIFIEDINTCONSTRAINT_2,
        SIGNOPP, SAMESIGN,
        TRUE, FALSE
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
     * @param c type of constraint
     * @param r Random
     * @return a constraint
     */
    public Constraint make(C c, Random r) {
        switch (c) {
            case ALLDIFFERENT:
                return Choco.allDifferent( "cp:ac", of.make(7, r));
            case BALLDIFFERENT:
                return Choco.allDifferent( "cp:bc", of.make(7, r));
            case BGCC_1:
                return makeGcc1(r, "cp:bc");
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
            case GCC:
                return makeGcc1(r, "cp:ac");
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
                return Choco.reifiedConstraint(vf.make(VariableFactory.V.BOOLVAR, r), make(r));
            case REIFIEDINTCONSTRAINT_2:
                return Choco.reifiedConstraint(vf.make(VariableFactory.V.BOOLVAR, r), make(r), make(r));
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
     * Create a globalcardinality constraint of type 1
     * @param r Random
     * @param option "cp:ac" or "cp:bc"
     * @return gcc
     */
    private Constraint makeGcc1(Random r, String option) {
        IntegerVariable[] vars = vf.make(5, r);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for(IntegerVariable v : vars){
            min = Math.min(min, v.getLowB());
            max = Math.max(max, v.getUppB());
        }
        int[] low = new int[max-min+1];
        int[] up = new int[max-min+1];

        Arrays.fill(low, 0);
        Arrays.fill(up, vars.length);

        // Fill low array with value that do not exceed n
        int n = vars.length;
        while(n>0){
            int i = next(low, 0, r);
            int v = r.nextInt(n+1);
            low[i] = v;
            n-= v;
        }
        n = vars.length;
        while(n>0){
            int i = next(up, vars.length, r);
            int v = Math.max(low[i], r.nextInt(n+1));
            up[i] = v;
            n-= v;
        }
//        if(minMax){
//            return Choco.globalCardinality(option, vars, min, max, low, up);
//        }
        return Choco.globalCardinality(option, vars, low, up, 0);
    }


    /**
     * Retrieve the next value in an array, where the cell is different from diffn
     * @param vals array of values
     * @param diffn the value to avoid
     * @param r Random
     * @return the next int value
     */
    private int next(int[] vals, int diffn, Random r){
        int i = r.nextInt(vals.length);
        while(vals[i]!=diffn){
            i = r.nextInt(vals.length);
        }
        return i;
    }

    /**
     * Make an array of constraints
     * @param nb number of constraints to create
     * @param r Random
     * @return array of constraints
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
     * @param nb number of constraints to create
     * @param c type of constraint to create
     * @param r Random
     * @return array of specific constraint
     */
    public Constraint[] make(int nb, C c, Random r) {
        Constraint[] cs = new Constraint[nb];
        for(int i = 0 ; i < nb; i++){
            cs[i] = make(c, r);
        }
        return cs;
    }


}
