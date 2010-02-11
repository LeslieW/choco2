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
package choco.cp.solver.variables.set;

import choco.cp.solver.variables.delta.BitSetDeltaDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.variables.delta.IDeltaDomain;
import choco.kernel.solver.variables.set.SetSubDomain;
import choco.kernel.solver.variables.set.SetVar;

/*
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Since : Choco 2.0.0
 *
 */
public class BitSetEnumeratedDomain implements SetSubDomain {

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
   * A bit set indicating for each value whether it is present or not
   */

  protected IStateBitSet contents;

  /**
   * the initial size of the domain (never increases)
   */
  private int capacity;

    private final IDeltaDomain delatDom;

  /**
   * Constructs a new domain for the specified variable and bounds.
   *
   * @param v    The involved variable.
   * @param a    Minimal value.
   * @param b    Maximal value.
   * @param full indicate if the initial bitSetDomain is full or empty (env or ker)
   * @param environment
   */

  public BitSetEnumeratedDomain(SetVar v, int a, int b, boolean full, IEnvironment environment) {
      capacity = b - a + 1;           // number of entries
    this.offset = a;
    if (full)
      size = environment.makeInt(capacity);
    else
      size = environment.makeInt(0);
    contents = environment.makeBitSet(capacity);
    if (full) {
      for (int i = 0; i < capacity; i++)
        contents.set(i);
    }
    //delatDom = new ChainDeltaDomain(capacity, offset);
        delatDom = new BitSetDeltaDomain(capacity, offset);
  }

  public BitSetEnumeratedDomain(SetVar v, int[] sortedValues, boolean full, IEnvironment environment) {
      int a = sortedValues[0];
      int b = sortedValues[sortedValues.length - 1];
      capacity = b - a + 1;           // number of entries
      this.offset = a;
      if (full) {
          size = environment.makeInt(sortedValues.length);
      } else {
          size = environment.makeInt(0);
      }
      contents = environment.makeBitSet(capacity);
      if (full) {
          // TODO : could be improved...
          for (int sortedValue : sortedValues) {
              contents.set(sortedValue - a);
          }
      }
//      delatDom = new ChainDeltaDomain(capacity, offset);
      delatDom = new BitSetDeltaDomain(capacity, offset);
  }

    /**
     * Specific constructor for set variable with empty domain
     * @param v
     * @param environment
     */
    private BitSetEnumeratedDomain(SetVar v, IEnvironment environment) {
        capacity = 0;           // number of entries
      this.offset = 0;
      size = environment.makeInt(0);
      contents = environment.makeBitSet(capacity);
//      delatDom = new ChainDeltaDomain(capacity, offset);
        delatDom = new BitSetDeltaDomain(capacity, offset);
  }


    /**
     * Specific constructor for empty set variable
     * @param v the set variable with no value
     * @param environment
     * @return empty BitSetEnumeratedDomain
     */
    public static BitSetEnumeratedDomain empty(SetVar v, IEnvironment environment) {
        return new BitSetEnumeratedDomain(v, environment);
    }


  /**
   * Returns the minimal present value.
   */
  public int getFirstVal() {
    if (size.get() > 0)
      return contents.nextSetBit(0) + offset;
    else
      return -1;
  }


  /**
   * Returns the maximal present value.
   */

  public int getLastVal() {
    if (size.get() > 0)
      return contents.prevSetBit(capacity - 1) + offset;
    else
      return -1;
  }

  /**
   * Checks if the value is present.
   *
   * @param x The value to check.
   */

  public boolean contains(int x) {
    int i = x - offset;
    return (i >= 0 && i < capacity && contents.get(i));
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
    contents.clear(i);
    delatDom.remove(i+offset);
    if (contents.get(i))
      LOGGER.severe("etrange etrange");
    size.add(-1);
  }


  /**
   * add a value.
   * @param x value to add
   * @return true wether the value has been added
   */

  public boolean add(int x) {
    int i = x - offset;
    if (!contents.get(i)) {
      addIndex(i);
      return true;
    } else {
      return false;
    }
  }

  private void addIndex(int i) {
    contents.set(i);
    delatDom.remove(i+offset);
    if (!contents.get(i))
      LOGGER.severe("etrange etrange");
    size.add(1);
  }

  /**
   * Returns the current size of the domain.
   */

  public int getSize() {
    return size.get();
  }


  /**
   * Returns the value following <code>x</code>
   * if non exist return -1
   * @param x starting value
   * @return value following x
   */

  public int getNextValue(int x) {
    int i = x - offset;
    int val = contents.nextSetBit(i + 1);
    if (val > 0)
      return val + offset;
    else
      return -1;
  }


  /**
   * Returns the value preceding <code>x</code>
   * if non exist return -1
   * @param x starting value
   * @return value preceding x
   */

  public int getPrevValue(int x) {
    int i = x - offset;
    int val = contents.prevSetBit(i - 1);
    if (val > 0)
      return val + offset;
    else
      return -1;
  }


  /**
   * Checks if the value has a following value.
   * @param x starting value
   * @return true whether there is a following value
   */

  public boolean hasNextValue(int x) {
    int i = x - offset;
    return (contents.nextSetBit(i + 1) != -1);
  }


  /**
   * Checks if the value has a preceding value.
   * @param x starting value
   * @return true if there is a preceding value
   */

  public boolean hasPrevValue(int x) {
    int i = x - offset;
    return (contents.prevSetBit(i - 1) != -1);
  }

  public DisposableIntIterator getDeltaIterator() {
      return delatDom.iterator();
      }

      @Override
    public IDeltaDomain copyDelta() {
        return delatDom.copy();
      }

  /**
   * The delta domain container is "frozen" (it can no longer accept new value removals)
   * so that this set of values can be iterated as such
   */
  public void freezeDeltaDomain() {
    delatDom.freeze();
    }

  /**
   * after an iteration over the delta domain, the delta domain is reopened again.
   *
   * @return true iff the delta domain is reopened empty (no updates have been made to the domain
   *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
   *         were made to the domain, while the delta domain was frozen).
   */
  public boolean releaseDeltaDomain() {
    return delatDom.release();
    }

  public boolean getReleasedDeltaDomain() {
    return delatDom.isReleased();
  }

  /**
   * cleans the data structure implementing the delta domain
   */
  public void clearDeltaDomain() {
    delatDom.clear();
  }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        StringBuffer buf = new StringBuffer("{");
        int maxDisplay = 15;
        int count = 0;
        int val = getFirstVal();
        do{
            count++;
            if (count > 1) buf.append(", ");
            buf.append(val);
            val = getNextValue(val);
        }while(val>-1 && count < maxDisplay);
        if (this.getSize() > maxDisplay) {
            buf.append("..., ");
            buf.append(this.getLastVal());
        }
        buf.append("}");
        return buf.toString();
    }
}
