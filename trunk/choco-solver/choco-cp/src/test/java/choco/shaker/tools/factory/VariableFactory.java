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
public class VariableFactory {

    public ArrayList<IntegerVariable> pool;

    public ArrayList<IntegerVariable> created = new ArrayList<IntegerVariable>();

    public ArrayList<V> scope = new ArrayList<V>();

    int id = 0;

    /**
     * specify the maximum number of created variables
     */
    int maxcreation=30;
    public int dsize = 10;

    public enum V {
        BOOLVAR, CST, ENUMVAR, BOUNDVAR, BTREEVAR, BLISTVAR, LINKVAR, UNBOUNDED
    }


    /**
     * Define a specific scope of variable to pick up in
     * @param variables the pool of variables
     */
    public void defines(IntegerVariable... variables){
        this.pool = new ArrayList<IntegerVariable>();
        this.pool.addAll(Arrays.asList(variables));
    }

    /**
     * Set a maximum number of created variables
     * @param n the maximum number of created variables
     */
    public void limits(int n){
        maxcreation = n;
    }

    /**
     * Set a maximum domain size
     * @param n the domain size
     */
    public void domainesize(int n){
        dsize = n;
    }

    /**
     * Define a specific scope of variable tupe to pick up in
     * @param vs the scope of variables
     */
    public void scopes(V... vs){
        scope.clear();
        scope.addAll(Arrays.asList(vs));
    }

    /**
     * Select randomly (among scope if defined)
     * and return a variable type
     * @param r random
     * @return type of variable
     */
    public V any(Random r) {
        if(scope.size()>0){
            return scope.get(r.nextInt(scope.size()));
        }
        V[] values = V.values();
        return values[r.nextInt(values.length)];
    }


    /**
     * Get one variable among all
     * @param r random
     * @return IntegerVariable
     */
    public IntegerVariable make(Random r){
        return make(any(r), r);
    }


    /**
     * Create and return the corresponding variable
     * @param v the type of variable
     * @param r random
     * @return IntegerVariable
     */
    public IntegerVariable make(V v, Random r) {
        // If there is a restricted pre-defined pool of variables
        // return one of them
        if(this.pool!=null){
            return this.pool.get(r.nextInt(this.pool.size()));
        }
        // If the number of new variable has been reached
        // return one of them
        if(created.size() >= maxcreation){
            return created.get(r.nextInt(created.size()));
        }
        //Otherwise, create a new variable
        IntegerVariable var = null;
        id++;
        int low, upp;
        switch (v) {
            case BOOLVAR:
                var = Choco.makeBooleanVar("b_"+id);
                break;
            case ENUMVAR:
                upp = r.nextInt(dsize);
                low = upp - r.nextInt(dsize);
                var = Choco.makeIntVar("v_"+id, low, upp, "cp:enum");
                break;
            case BOUNDVAR:
                upp = r.nextInt(dsize);
                low = upp - r.nextInt(dsize);
                var = Choco.makeIntVar("v_"+id, low, upp, "cp:bound");
                break;
            case BTREEVAR:
                upp = r.nextInt(dsize);
                low = upp - r.nextInt(dsize);
                var = Choco.makeIntVar("v_"+id, low, upp, "cp:btree");
                break;
            case BLISTVAR:
                upp = r.nextInt(dsize);
                low = upp - r.nextInt(dsize);
                var = Choco.makeIntVar("v_"+id, low, upp, "cp:blist");
                break;
            case LINKVAR:
                upp = r.nextInt(dsize);
                low = upp - r.nextInt(dsize);
                var = Choco.makeIntVar("v_"+id, low, upp, "cp:link");
                break;
            case UNBOUNDED:
                var = Choco.makeIntVar("v_"+id);
                break;
            case CST:
                int val = r.nextInt(dsize)-dsize/2;
                var = Choco.constant(val);
                break;
        }
        created.add(var);
        return var;
    }


    /**
     * Get an array of variables
     * @param nb number of variables to create
     * @param r random
     * @return array of IntegerVariables
     */
    public IntegerVariable[] make(int nb, Random r){
        IntegerVariable[] variables = new IntegerVariable[nb];
        for (int i = 0; i < variables.length; i++) {
            variables[i] = make(r);
        }
        return variables;
    }

    /**
     * Get an array of variables
     * @param nb number of variables to create
     * @param v the type of variable
     * @param r random
     * @return array of variables
     */
    public IntegerVariable[] make(int nb, V v, Random r){
        IntegerVariable[] variables = new IntegerVariable[nb];
        for (int i = 0; i < variables.length; i++) {
            variables[i] = make(v, r);
        }
        return variables;
    }

}
