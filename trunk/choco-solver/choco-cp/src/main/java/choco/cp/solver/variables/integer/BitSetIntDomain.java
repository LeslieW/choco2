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

import choco.cp.solver.variables.delta.BitSetDeltaDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.OneValueIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.variables.integer.IBitSetIntDomain;

import java.util.Random;

public class BitSetIntDomain extends AbstractIntDomain implements IBitSetIntDomain {
    /**
     * A random generator for random value from the domain
     */

    protected final static Random random = new Random();

    /**
     * The offset, that is the minimal value of the domain (stored at index 0).
     * Thus the entry at index i corresponds to x=i+offset).
     */

    protected final int offset;


    /**
     * Number of present values.
     */
    protected IStateInt size;

    /**
     * The backtrackable minimal value of the variable.
     */

    protected IStateInt inf;

    /**
     * The backtrackable maximal value of the variable.
     */

    protected IStateInt sup;


    /**
     * A bit set indicating for each value whether it is present or not
     */

    protected IStateBitSet contents;

    /**
     * the initial size of the domain (never increases)
     */
    protected int capacity;

    /**
     * Constructs a new domain for the specified variable and bounds.
     *
     * @param v The involved variable.
     * @param a Minimal value.
     * @param b Maximal value.
     */

    public BitSetIntDomain(IntDomainVarImpl v, int a, int b) {
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        capacity = b - a + 1;           // number of entries
        this.offset = a;
        size = env.makeInt(capacity);
        contents = env.makeBitSet(capacity);
        for (int i = 0; i < capacity; i++) {  // TODO : could be improved...
            contents.set(i);
        }
        deltaDom = new BitSetDeltaDomain(capacity, offset);
//        deltaDom = new StackDeltaDomain();
        inf = env.makeInt(a);
        sup = env.makeInt(b);
    }

    public BitSetIntDomain(IntDomainVarImpl v, int[] sortedValues) {
        int a = sortedValues[0];
        int b = sortedValues[sortedValues.length - 1];
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        capacity = b - a + 1;           // number of entries
        this.offset = a;
        size = env.makeInt(sortedValues.length);
        contents = env.makeBitSet(capacity);
        for (int i = 0; i < sortedValues.length; i++) {  // TODO : could be improved...
            contents.set(sortedValues[i] - a);
        }
        deltaDom = new BitSetDeltaDomain(capacity, offset);
//        deltaDom = new StackDeltaDomain();
        inf =  env.makeInt(a);
        sup =  env.makeInt(b);
    }

	public IStateBitSet getContent() {
		return contents;
	}

    /**
     * Returns the minimal present value.
     */
    public int getInf() {
        return inf.get();
        //return contents.nextSetBit(0) + offset;
    }


    /**
     * Returns the maximal present value.
     */
    public int getSup() {
        return sup.get();
        //return contents.prevSetBit(capacity - 1) + offset;
    }

    /**
     * Sets a new minimal value.
     *
     * @param x New bound value.
     */

    public int updateInf(int x) {
        int newi = x - offset;  // index of the new lower bound
        for (int i = inf.get() - offset; i < newi; i = contents.nextSetBit(i + 1)) {
            assert(contents.get(i));
            //LOGGER.severe("Bug in BitSetIntDomain.updateInf ?");
            removeIndex(i);
        }
        inf.set(contents.nextSetBit(newi) + offset);
        return inf.get();
    }

    /**
     * Sets a new maximal value.
     *
     * @param x New bound value.
     */

    public int updateSup(int x) {
        int newi = x - offset;  // index of the new lower bound
        for (int i = sup.get() - offset; i > newi; i = contents.prevSetBit(i - 1)) {
                assert(contents.get(i));
                //LOGGER.severe("Bug in BitSetIntDomain.updateSup ?");
                removeIndex(i);
        }
        sup.set(contents.prevSetBit(newi) + offset);
        return sup.get();
    }

    /**
     * Checks if the value is present.
     *
     * @param x The value to check.
     */

    public boolean contains(final int x) {
        return (contents.get(x - offset));
    }

    /**
     * Removes a value.
     */
    public boolean remove(int x) {
        int i = x - offset;
        if (contents.get(i)) {
            removeIndex(i);
            return true;
        } else {
            return false;
        }
    }

    private void removeIndex(int i) {
        //assert(i != firstIndexToBePropagated);
        //LOGGER.severe("Bug in BitSetIntDomain.removeIndex ?");
        contents.clear(i);
        deltaDom.remove(i+offset);
        assert(!contents.get(i)) ;
        //LOGGER.severe("Bug in BitSetIntDomain.removeIndex ?");
        size.add(-1);
    }

    /**
     * Removes all the value but the specified one.
     */

    public void restrict(int x) {
        int xi = x - offset;
      // IF NEED REMOVALS
        for (int i = contents.nextSetBit(0); i >= 0; i = contents.nextSetBit(i + 1)) {
            if (i != xi) {
                removeIndex(i);
            }
        }
      // ELSE
//      contents.clear();
//      contents.set(xi);
      // ENDIF
        sup.set(x);
        inf.set(x);
//        size.set(1);
    }

    /**
     * Returns the current size of the domain.
     */

    public int getSize() {
        return size.get();
    }

  public DisposableIntIterator getIterator() {
      if(getSize() == 1) return OneValueIterator.getOneValueIterator(getInf());
      DisposableIntIterator iter = _cachedIterator;
      if (iter != null && iter.reusable) {
          iter.init();
          return iter;
      }
      _cachedIterator = new BitSetIntDomainIterator(this);
      return _cachedIterator;
  }


  protected static class BitSetIntDomainIterator extends DisposableIntIterator {
      BitSetIntDomain domain;

      protected int nextValue;

    private BitSetIntDomainIterator(BitSetIntDomain domain) {
        this.domain = domain;
      init();
    }

    @Override
    public void init() {
          super.init();
      nextValue = domain.inf.get() - domain.offset;
    }

    public boolean hasNext() {
      return nextValue >= 0;
    }

    public int next() {
      int v = nextValue;
      nextValue = domain.contents.nextSetBit(nextValue + 1);
      return v + domain.offset;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
  }

    /**
     * Returns the value following <code>x</code>
     */

    public final int getNextValue(final int x) {
        int i = x - offset;
        if (i < 0 || x < inf.get()) return getInf();
        int bit = contents.nextSetBit(i + 1);
        if (bit < 0) return Integer.MAX_VALUE;
        else return bit + offset;
    }


    /**
     * Returns the value preceding <code>x</code>
     */

    public int getPrevValue(int x) {
        int i = x - offset;
        if (x > sup.get()) return sup.get();
        return contents.prevSetBit(i - 1) + offset;
    }


    /**
     * Checks if the value has a following value.
     */

    public boolean hasNextValue(int x) {
        //int i = x - offset;
        return x < sup.get();
        //return (contents.nextSetBit(i + 1) != -1);
    }


    /**
     * Checks if the value has a preceding value.
     */

    public boolean hasPrevValue(int x) {
        //int i = x - offset;
        return x > inf.get();
        //return (contents.prevSetBit(i - 1) != -1);
    }

    /**
     * Returns a value randomly choosed in the domain.
     */

    public int getRandomValue() {
        int size = getSize();
        if (size == 1) return this.getInf();
        else {
            int rand = random.nextInt(size);
            int val = this.getInf() - offset;
            for (int o = 0; o < rand; o++) {
                val = contents.nextSetBit(val + 1);
            }
            return val + offset;
        }
    }

    public boolean isEnumerated() {
        return true;
    }

    public boolean isBoolean() {
        return (offset == 0) && (offset + capacity - 1 == 1);
    }

  protected DisposableIntIterator _cachedDeltaIntDomainIterator = null;

    public String toString() {
        return "{" + getInf() + "..." + getSup() + "}";
    }

    public String pretty() {
        StringBuffer buf = new StringBuffer("{");
        int maxDisplay = 15;
        int count = 0;
        DisposableIntIterator it = this.getIterator();
        for (; (it.hasNext() && count < maxDisplay);) {
            int val = it.next();
            count++;
            if (count > 1) buf.append(", ");
            buf.append(val);
        }
        it.dispose();
        if (this.getSize() > maxDisplay) {
            buf.append("..., ");
            buf.append(this.getSup());
        }
        buf.append("}");
        return buf.toString();
    }
}
