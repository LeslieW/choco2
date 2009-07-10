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
package choco.cp.solver.search.real;

import choco.kernel.solver.search.real.RealValIterator;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealVar;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 2 nov. 2004
 */
public class RealIncreasingDomain implements RealValIterator {

  public boolean hasNextVal(RealVar x, int i) {
    return i < 2;
  }

  public int getFirstVal(RealVar x) {
    return 1;
  }

  public int getNextVal(RealVar x, int i) {
    return 2;
  }

    /**
     * testing whether more branches can be considered after branch i, on the alternative associated to variable x
     *
     * @param x the variable under scrutiny
     * @param i the index of the last branch explored
     * @return true if more branches can be expanded after branch i
     */
    public boolean hasNextVal(Var x, int i) {
        return this.hasNextVal((RealVar)x, i);
    }

    /**
     * Accessing the index of the first branch for variable x
     *
     * @param x the variable under scrutiny
     * @return the index of the first branch (such as the first value to be assigned to the variable)
     */
    public int getFirstVal(Var x) {
        return this.getFirstVal((RealVar)x);
    }

    /**
     * generates the index of the next branch after branch i, on the alternative associated to variable x
     *
     * @param x the variable under scrutiny
     * @param i the index of the last branch explored
     * @return the index of the next branch to be expanded after branch i
     */
    public int getNextVal(Var x, int i) {
        return this.getNextVal((RealVar)x, i);
    }
}