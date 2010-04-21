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
package choco.cp.solver.search.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.set.SetVar;

import java.util.Random;
import java.util.Vector;

public final class RandomSetValSelector implements ValSelector<SetVar> {
  protected Random random;

  /**
   * Default constructor for a random value selector for banching.
   */
  public RandomSetValSelector() {
    random = new Random();
  }

  /**
   * Constructs a random value selector for branching with a specified seed.
   * @param seed to replay a random palm
   */
  public RandomSetValSelector(long seed) {
    random = new Random(seed);
  }

  public int getBestVal(SetVar v) {
      int value = Integer.MIN_VALUE;
      Vector<Integer> vector;
      vector = new Vector<Integer>();
      DisposableIntIterator it = v.getDomain().getEnveloppeIterator();
      while (it.hasNext()){
          int val = it.next();
          if(!v.isInDomainKernel(val)) {
              vector.add(val);
          }
      }
      it.dispose();
      if(vector.size()>0)value = Integer.valueOf(vector.get(random.nextInt(vector.size())));
      return value;
  }
}