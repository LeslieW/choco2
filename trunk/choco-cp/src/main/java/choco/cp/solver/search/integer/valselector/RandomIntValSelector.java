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
package choco.cp.solver.search.integer.valselector;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Random;

public class RandomIntValSelector implements ValSelector {
  protected Random random;

  /**
   * Default constructor for a random value selector for banching.
   */
  public RandomIntValSelector() {
    random = new Random();
  }

  /**
   * Constructs a random value selector for branching with a specified seed.
   */
  public RandomIntValSelector(long seed) {
    random = new Random(seed);
  }

  public int getBestVal(IntDomainVar x) {
    if (x.hasEnumeratedDomain()) {
        if (x.isInstantiated()) return x.getVal();
        int val = (random.nextInt(x.getDomainSize()));
        DisposableIntIterator iterator = x.getDomain().getIterator();
        for (int i = 0; i < val; i++) {
            iterator.next();
        }
        int res = iterator.next();
        iterator.dispose();
        return res;
    } else {
        int val = (random.nextInt(2));
        if (val == 0) return x.getInf();
        else return x.getSup();
    }
  }
}
