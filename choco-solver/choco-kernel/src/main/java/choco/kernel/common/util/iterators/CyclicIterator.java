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
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.common.util.iterators;

import choco.kernel.memory.IStateBitSet;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 2 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class CyclicIterator extends DisposableIntIterator {
    /**
     * current index of the iteration
     * (the one just returned at the last call to next())
     */
    private int k, nextk;

    /**
     * the index where the iteration started when the iterator was created
     */
    private int endMarker;

    private IStateBitSet bset;

    /**
     * constructor
     */
    public CyclicIterator(final choco.kernel.memory.IStateBitSet bs, final int avoidIndex) {
      bset = bs;
          k = -1;
          nextk = -1;
      endMarker = avoidIndex;
      }

      public boolean hasNext() {
      nextk = bset.nextSetBit(k + 1);
      if (nextk < 0) {
        return false;
      } else if (nextk == endMarker) {
        nextk = bset.nextSetBit(nextk + 1);
          return nextk >= 0;
      } else {
        return true;
      }
    }

    public int next() {
      k = nextk;
      return k;
    }
  }
