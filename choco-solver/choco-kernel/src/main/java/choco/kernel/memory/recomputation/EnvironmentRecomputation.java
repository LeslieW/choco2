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
package choco.kernel.memory.recomputation;

import choco.kernel.memory.IEnvironment;

@Deprecated
public class EnvironmentRecomputation{

    /**
     * SINCE 12.03.2010 NOT SUPPORTED
     */

    public EnvironmentRecomputation() {
        throw new UnsupportedOperationException("EnvironmentRecomputation is not supported anymore. Please use CPSolver#setRecomputation(..) instead!");
    }

    public EnvironmentRecomputation(int envType, int gap) {
        throw new UnsupportedOperationException("EnvironmentRecomputation is not supported anymore. Please use CPSolver#setRecomputation(..) instead!");
    }
    
    
	public EnvironmentRecomputation(IEnvironment envD, int gap) {
        throw new UnsupportedOperationException("EnvironmentRecomputation is not supported anymore. Please use CPSolver#setRecomputation(..) instead!");
    }

}
