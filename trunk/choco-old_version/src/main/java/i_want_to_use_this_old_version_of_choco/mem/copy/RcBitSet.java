package i_want_to_use_this_old_version_of_choco.mem.copy;

import i_want_to_use_this_old_version_of_choco.mem.IStateBitSet;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.BitSet;

/**
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 25 avr. 2007
 * Time: 11:26:04
 * To change this template use File | Settings | File Templates.
 */
public class RcBitSet implements IStateBitSet, RecomputableElement {

    private BitSet representedBy;
    private EnvironmentCopying env;
    private int timeStamp;




        public RcBitSet(final EnvironmentCopying env, final int initialSize) {
        this(env, initialSize, false);
    }

    public RcBitSet(final EnvironmentCopying env, final int initialSize,
                    final boolean fixed) {
        representedBy = new BitSet(initialSize);
        timeStamp = env.getWorldIndex();
        this.env = env;
        env.add(this);

    }

    public int cardinality() {
        return representedBy.cardinality();
    }

    public void set(final int bitIndex) {
        timeStamp = env.getWorldIndex();
        representedBy.set(bitIndex);
    }

    public void clear(final int bitIndex) {
        timeStamp = env.getWorldIndex();
        representedBy.clear(bitIndex);

    }

    public void set(final int index, final boolean value) {
        timeStamp = env.getWorldIndex();
        representedBy.set(index,value);

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
        int count = 0;
        while ((k = nextSetBit(st)) <= fromIndex && k != -1) {
            st = k+1;
            count++;

        }
        if (count == 0)
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
        if (bitSet.cardinality() <= 0) {
            System.out.println("bui");
            System.out.println("2");
        }
        timeStamp = env.getWorldIndex();
        BitSet b = (BitSet) bitSet.clone();
        if (b.cardinality() <= 0) {
            System.out.println("bui");
            System.out.println("2");
        }
        representedBy = b ;

    }

   public BitSet getBitSet() {
       return representedBy;
    }
}
