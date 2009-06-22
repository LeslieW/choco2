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

import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.AbstractStateBitSet;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.solver.propagation.VarEvent;

import java.util.BitSet;

/*
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 25 avr. 2007
 * Since : Choco 2.0.0
 *
 */
public class RcBitSet extends AbstractStateBitSet implements RecomputableElement {

    private BitSet representedBy;
    private EnvironmentCopying env;
    private int timeStamp;


    public RcBitSet(final EnvironmentCopying env, final int initialSize) {
        representedBy = new BitSet(initialSize);
        timeStamp = env.getWorldIndex();
        this.env = env;
        env.add(this);

    }

    public int cardinality() {
        return representedBy.cardinality();
    }

    public int size(){
        return representedBy.size();
    }

    public void set(final int bitIndex) {
        timeStamp = env.getWorldIndex();
        representedBy.set(bitIndex);
    }

    public void clear(final int bitIndex) {
        timeStamp = env.getWorldIndex();
        representedBy.clear(bitIndex);

    }

    public void clear() {
        representedBy.clear();
    }

    public void set(final int index, final boolean value) {
        timeStamp = env.getWorldIndex();
        representedBy.set(index,value);

    }

    public void set(int fromIdex, int toIndex) {
        for (int i = fromIdex; i < toIndex;i++) set(i);
    }

    public void or(IStateBitSet other)
    {
        RcBitSet set = (RcBitSet) other;
        this.representedBy.or(set.getBitSet());
    }

    public void xor(IStateBitSet other)
    {
        RcBitSet set = (RcBitSet) other;
        this.representedBy.xor(set.getBitSet());
    }
    public void and(IStateBitSet other)
    {
        RcBitSet set = (RcBitSet) other;
        this.representedBy.and(set.getBitSet());
    }
    public void andNot(IStateBitSet other)
    {
        RcBitSet set = (RcBitSet) other;
        this.representedBy.andNot(set.getBitSet());
    }

    /**
     * Returns true if the specified <code>BitSet</code> has any bits set to
     * <code>true</code> that are also set to <code>true</code> in this
     * <code>BitSet</code>.
     *
     * @param	setI <code>BitSet</code> to intersect with
     * @return  boolean indicating whether this <code>BitSet</code> intersects
     *          the specified <code>BitSet</code>.
     * @since   1.4
     */
    public boolean intersects(IStateBitSet setI) {
        RcBitSet set = (RcBitSet) setI;
        return this.representedBy.intersects(set.getBitSet());
    }

    public void flip(int bitIndex){
        this.representedBy.flip(bitIndex);
    }
    
    public void flip(int fromIndex, int toIndex){
        this.representedBy.flip(fromIndex, toIndex);
    }


    public boolean get(final int bitIndex) {
        return representedBy.get(bitIndex);
    }

    public int nextSetBit(final int fromIndex) {
        return representedBy.nextSetBit(fromIndex);
    }

    public int prevSetBit(final int fromIndex) {
        /* int st = 0;
   int k = 0;
   int nbEdges = 0;
   while ((k = nextSetBit(st)) <= fromIndex && k != -1) {
       st = k+1;
       nbEdges++;

   }
   if (nbEdges == 0)
       return -1;
   else
       return st-1;*/


        int m = fromIndex;
        while (!representedBy.get(m) && m > -1) {
            m--;
        }
        return m;


    }

    public int capacity() {
        return representedBy.size();
    }

    public void ensureCapacity(final int bitIndex) {

    }

    public boolean isEmpty(){
        return representedBy.isEmpty();
    }

    public String pretty() {
        return "";
    }

    public IntIterator getCycleButIterator(final int avoidIndex) {
        int n = this.cardinality();
        if (avoidIndex != VarEvent.NOCAUSE && this.get(avoidIndex)) n -= 1;
        if (n > 0) {
            return new RcBitSet.CyclicIterator(this, avoidIndex);
        } else {
            return new RcBitSet.EmptyIterator();
        }
    }


    public int getType() {
        return BITSET;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void _set(BitSet bitSet) {
        timeStamp = env.getWorldIndex();
        BitSet b = (BitSet) bitSet.clone();
        representedBy = b ;
    }

    public BitSet getBitSet() {
        return representedBy;
    }

    public IStateBitSet copy() {
        //if (!sizeIsSticky.get()) trimToSize();
        RcBitSet result = new RcBitSet(env, this.size());
        result.representedBy = (BitSet)this.representedBy.clone();
        result.timeStamp = this.timeStamp;
        return result;
    }
}
