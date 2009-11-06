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
import choco.kernel.memory.IStateLong;

/*
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Since : Choco 2.0.0
 *
 */
public class RcLong implements IStateLong, RecomputableElement {

    private final EnvironmentCopying environment;
    private long currentValue;
    private int timeStamp;

    public RcLong(EnvironmentCopying env) {
        this(env, UNKNOWN_LONG);
    }

    public RcLong(EnvironmentCopying env, long i ) {
        environment = env;
        currentValue= i;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }


    @Override
	public void add(long delta) {
		set(currentValue + delta);
	}

	@Override
	public long get() {
		return currentValue;
	}

	public void set(long y) {
        currentValue = y;
        timeStamp = environment.getWorldIndex();
    }

    	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

	protected void _set(final long y, final int wstamp) {
		currentValue = y;
		timeStamp = wstamp;
	}

    public IEnvironment getEnvironment() {
        return environment;
    }

    public long deepCopy() {
        return currentValue;
    }



    public int getType() {
        return LONG;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
	public String toString() {
		return String.valueOf(currentValue);
	}
}