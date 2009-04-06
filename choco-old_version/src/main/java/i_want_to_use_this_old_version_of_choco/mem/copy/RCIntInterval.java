package i_want_to_use_this_old_version_of_choco.mem.copy;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
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
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateIntInterval;

public class RCIntInterval implements IStateIntInterval, RecomputableElement {

    private int inf;
    private int sup;

    private int size;

    private int timeStamp;

    private final EnvironmentCopying environment;

    public RCIntInterval(EnvironmentCopying environment, int inf, int sup) {
        this.environment = environment;
        this.inf = inf;
        this.sup = sup;
        this.size = sup - inf +1;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }

    public int getInf() {
        return inf;
    }

    public void setInf(int inf) {
        this.inf = inf;
        timeStamp = environment.getWorldIndex();
    }

    public void addInf(int delta) {
        setInf(getInf() + delta);
    }

    public int getSup() {
        return sup;
    }

    public void setSup(int sup) {
        this.sup = sup;
        timeStamp = environment.getWorldIndex();
    }

    public void addSup(int delta) {
        setSup(getSup() + delta);
    }

    public IEnvironment getEnvironment() {
        return environment;
    }

    public int[] deepCopy() {
        return new int[]{inf, sup};
    }

    public int getType() {
        return INTINTERVAL;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public int getSize(){
        return size;
    }

    public boolean contains(int x){
        return x <= sup && x >= inf;
    }
}
