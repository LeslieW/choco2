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
        this(env,UNKNOWN_INT);
    }

    public RcInt(EnvironmentCopying env, int i ) {
        environment = env;
        currentValue= i;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }

    
    @Override
	public void add(int delta) {
		set(currentValue + delta);		
	}

	@Override
	public int get() {
		return currentValue;
	}

	public void set(int y) {
        //if (y != currentValue)
            currentValue = y;
        timeStamp = environment.getWorldIndex();
    }

    public IEnvironment getEnvironment() {
        return environment;
    }

    public int deepCopy() {
        return currentValue;
    }

    

    public int getType() {
        return INT;
    }

    public int getTimeStamp() {
        return timeStamp;
    }
    
    @Override
	public String toString() {
		return String.valueOf(currentValue);
	}
}
