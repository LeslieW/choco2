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
package choco.kernel.model.factories;

import choco.kernel.model.constraints.Constraint;

import java.util.ArrayList;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 19 déc. 2008
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ConstraintListFactory {

    private static ArrayList<Constraint> constraintList = new ArrayList<Constraint>();
    private static int indice = 0;

    public static ArrayList<Constraint> getConstraintList(){
        return ConstraintListFactory.constraintList;
    }

    public static int add(Constraint c){
        constraintList.add(c);
        return indice++;
    }

    public static Constraint get(int i){
        return constraintList.get(i);
    }


    public static boolean remove(int i, Constraint removeOne){
        if(constraintList.get(i).equals(removeOne)){
            constraintList.set(i, null);
            return true;
        }
        return false;
    }

    public static void remove(int i){
        constraintList.set(i, null);
    }


}
