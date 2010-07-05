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
import choco.kernel.memory.IStateInt;

/* 
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Since : Choco 2.0.0
 *
 */
public class RcInt implements IStateInt, RecomputableElement {

    private final EnvironmentCopying environment;
    private int currentValue;
    private int timeStamp;

    public RcInt(EnvironmentCopying env) {
        this(env,0);
    }

    public RcInt(EnvironmentCopying env, int i ) {
        environment = env;
        currentValue= i;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }

    
    @Override
	public final void add(int delta) {
		set(currentValue + delta);		
	}

	@Override
	public final int get() {
		return currentValue;
	}

	public final void set(int y) {
        //if (y != currentValue)
            currentValue = y;
        timeStamp = environment.getWorldIndex();
    }

    	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

	protected void _set(final int y, final int wstamp) {
		currentValue = y;
		timeStamp = wstamp;
	}

    public final IEnvironment getEnvironment() {
        return environment;
    }

    public final int deepCopy() {
        return currentValue;
    }

    

    public final int getType() {
        return INT;
    }

    public final int getTimeStamp() {
        return timeStamp;
    }
    
    @Override
	public final String toString() {
		return String.valueOf(currentValue);
	}
}
