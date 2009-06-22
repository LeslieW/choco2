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
package choco.kernel.memory.copy;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateObject;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 22 juin 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class RcObject implements IStateObject, RecomputableElement {

    private final EnvironmentCopying environment;
    private Object currentObject;
    private int timeStamp;


    public RcObject(EnvironmentCopying env, Object obj ) {
        environment = env;
        currentObject = obj;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }

	public Object get() {
		return currentObject;
	}

	public void set(Object y) {
        currentObject = y;
        timeStamp = environment.getWorldIndex();
    }

    	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

	protected void _set(final Object y, final int wstamp) {
		currentObject = y;
		timeStamp = wstamp;
	}

    public IEnvironment getEnvironment() {
        return environment;
    }

    public Object deepCopy() {
        return currentObject;
    }



    public int getType() {
        return OBJECT;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
	public String toString() {
		return String.valueOf(currentObject.toString());
	}
}
