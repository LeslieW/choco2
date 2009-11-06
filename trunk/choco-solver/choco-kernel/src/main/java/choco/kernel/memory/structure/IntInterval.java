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
package choco.kernel.memory.structure;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 22 juin 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class IntInterval {

    private IStateInt inf;
    private IStateInt sup;

    private final IEnvironment environment;

    public IntInterval(IEnvironment environment, int inf, int sup) {
        this.environment = environment;
        this.inf = environment.makeInt(inf);
        this.sup = environment.makeInt(sup);
    }

    public int getInf() {
        return inf.get();
    }

    public void setInf(int inf) {
        this.inf.set(inf);
    }

    public void addInf(int delta) {
        this.inf.add(delta);
    }

    public int getSup() {
        return sup.get();
    }

    public void setSup(int sup) {
        this.sup.set(sup);
    }

    public void addSup(int delta) {
        this.sup.add(delta);
    }

    public IEnvironment getEnvironment() {
        return environment;
    }

    public boolean contains(int x){
        return x <= sup.get() && x >= inf.get();
    }

}
