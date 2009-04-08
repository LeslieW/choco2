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
package choco.cp.solver.variables.integer;

import choco.Choco;
import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;

import java.util.Random;

public class IntervalIntDomain extends AbstractIntDomain {
    private static int eventBitMask = IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector;
    /**
     * A random generator for random value from the domain
     */

    protected static Random random = new Random(System.currentTimeMillis());

    /**
     * The backtrackable minimal value of the variable.
     */

    protected final IStateInt inf;

    /**
     * The backtrackable maximal value of the variable.
     */

    protected final IStateInt sup;

  /**
   * The last value since the last coherent state
   */
    protected int lastInfPropagated;
  /**
   * The last value since the last coherent state
   */
  protected int lastSupPropagated;

    public IntervalIntDomain(IntDomainVarImpl v, int a, int b) {
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        inf = env.makeInt(a);
        sup = env.makeInt(b);
        lastInfPropagated = a;
        lastSupPropagated = b;
        currentInfPropagated = Integer.MIN_VALUE;
        currentSupPropagated = Integer.MAX_VALUE;
    }

    public boolean contains(int x) {
        return ((x >= getInf()) && (x <= getSup()));
    }

    public int getNextValue(int x) {
        if (x < getInf()) {
            return getInf();
        } else if (x < getSup()) {
            return x + 1;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public int getPrevValue(int x) {
        if (x > getSup()) {
            return getSup();
        } else if (x > getInf()) {
            return x - 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public int getRandomValue() {
        int inf = getInf();
        int rand = random.nextInt(this.getSize());
        return inf + rand;
    }

    public final int getSize() {
        return getSup() - getInf() + 1;
    }

    public boolean hasNextValue(int x) {
        return (x < getSup());
    }

    public boolean hasPrevValue(int x) {
        return (x > getInf());
    }

    public DisposableIntIterator getIterator() {
        if(getSize() == 1) return DisposableIntIterator.getOneValueIterator(getInf());
        return new DisposableIntIterator() {
            int x = getInf() - 1;

            public boolean hasNext() {
                return x < getSup();
            }

            public int next() {
                return ++x;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public boolean remove(int x) {
        return false;
    }

    public final int getSup() {
        return sup.get();
    }

    public final int getInf() {
        return inf.get();
    }

    public void restrict(int x) {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0 &&
          lastInfPropagated == Integer.MIN_VALUE) {
        lastInfPropagated = inf.get();
        lastSupPropagated = sup.get();
      }
        inf.set(x);
        sup.set(x);
    }

    public int updateInf(int x) {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0 &&
          lastInfPropagated == Integer.MIN_VALUE) {
        lastInfPropagated = inf.get();
        lastSupPropagated = sup.get();
      }
        inf.set(x);
        return x;
    }

    public int updateSup(int x) {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0 &&
          lastInfPropagated == Integer.MIN_VALUE) {
        lastInfPropagated = inf.get();
        lastSupPropagated = sup.get();
      }
        sup.set(x);
        return x;
    }

    protected boolean _removeVal(int x, int idx) throws ContradictionException {
        int infv = getInf(), supv = getSup();
        if (x == infv) {
            _updateInf(x + 1, idx);
            if (getInf() == supv) _instantiate(supv, idx);
            return true;
        } else if (x == supv) {
            _updateSup(x - 1, idx);
            if (getSup() == infv) _instantiate(infv, idx);
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnumerated() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

  public void freezeDeltaDomain() {
    if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
      currentInfPropagated = getInf();
      currentSupPropagated = getSup();
    }
  }

  /**
   * release the delta domain
   *
   * @return wether it was a new update
   */
  public boolean releaseDeltaDomain() {
    if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
      boolean noNewUpdate = ((getInf() == currentInfPropagated) && (getSup() == currentSupPropagated));
      if (noNewUpdate) {
        lastInfPropagated = Integer.MIN_VALUE;
        lastSupPropagated = Integer.MAX_VALUE;
      } else {
        lastInfPropagated = currentInfPropagated;
        lastSupPropagated = currentSupPropagated;
      }
      currentInfPropagated = Integer.MIN_VALUE;
      currentSupPropagated = Integer.MAX_VALUE;
      return noNewUpdate;
    }
    return true;
  }

  public void clearDeltaDomain() {
    if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
      lastInfPropagated = Integer.MIN_VALUE;
      lastSupPropagated = Integer.MAX_VALUE;
      currentInfPropagated = Integer.MIN_VALUE;
      currentSupPropagated = Integer.MAX_VALUE;
    }
  }

  /**
   * @return a boolean
   */
  public boolean getReleasedDeltaDomain() {
    if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
      return currentInfPropagated == Integer.MIN_VALUE
          && currentSupPropagated == Integer.MAX_VALUE;
    }
    return true;
  }

    public DisposableIntIterator getDeltaIterator() {
      if ((variable.getEvent().getPropagatedEvents() & eventBitMask) != 0) {
      return new DisposableIntIterator() {
            int x = lastInfPropagated-1;

            public boolean hasNext() {
              if (x+1 == currentInfPropagated) return currentSupPropagated < lastSupPropagated;
              if (x > currentSupPropagated) return x < lastSupPropagated;
              return (x+1 < currentInfPropagated);
            }

            public int next() {
              x++;
              if (x == currentInfPropagated) {
                x = currentSupPropagated + 1;
              }
              return x;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public void dispose() {}
        };
      } else {
        return new DisposableIntIterator() {

          public void dispose() {
          }

          public boolean hasNext() {
            return false;
          }

          public int next() {
            return 42;
          }

          public void remove() {
          }
        };
      }
    }

    public String pretty() {
        String ret = "[" + this.getInf() + " .. " + this.getSup() + "]";
        if(Choco.DEBUG){
            ret += lastInfPropagated + "->" + lastSupPropagated;
        }
        return ret;
    }
}
