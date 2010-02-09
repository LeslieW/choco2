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
package choco.kernel.memory.copy;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBool;

/* 
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Since : Choco 2.0.0
 *
 */
public final class RcBool implements IStateBool, RecomputableElement {

    private EnvironmentCopying environment;
    private boolean currentValue;
    private int timeStamp;

    public RcBool(EnvironmentCopying env, boolean b) {
        environment = env;
        currentValue = b;
        environment.add(this);
        timeStamp = env.getWorldIndex();
    }

    public boolean get() {
        return currentValue;
    }

    public void set(boolean b) {
        timeStamp = environment.getWorldIndex();
        currentValue = b ;
    }

    public void _set(boolean b, int timeStamp) {
        this.timeStamp = timeStamp;
        currentValue = b ;
    }

    public boolean deepCopy() {
        return currentValue;
    }

    public IEnvironment getEnvironment() {
        return environment;
    }

    public int getType() {
        return BOOL;
    }

    public int getTimeStamp() {
        return timeStamp;
    }
}
