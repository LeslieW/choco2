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
import choco.kernel.memory.IStateDouble;

/*
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Since : Choco 2.0.0
 *
 */
public final class RcDouble implements IStateDouble, RecomputableElement {

    private final EnvironmentCopying environment;
    private double currentValue;
    private int timeStamp;

    public RcDouble(EnvironmentCopying env) {
        this(env,Double.MAX_VALUE);
    }

    public RcDouble(EnvironmentCopying env, double i ) {
        environment = env;
        currentValue= i;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }


    @Override
	public void add(double delta) {
		set(currentValue + delta);
	}

	@Override
	public double get() {
		return currentValue;
	}

	public void set(double y) {
        currentValue = y;
        timeStamp = environment.getWorldIndex();
    }

    	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

	protected void _set(final double y, final int wstamp) {
		currentValue = y;
		timeStamp = wstamp;
	}

    public IEnvironment getEnvironment() {
        return environment;
    }

    public double deepCopy() {
        return currentValue;
    }



    public int getType() {
        return DOUBLE;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
	public String toString() {
		return String.valueOf(currentValue);
	}
}