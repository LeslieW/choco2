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
package choco.kernel.memory;

/**
 * Super class of all environments !
 */
public abstract class AbstractEnvironment implements IEnvironment {
    /**
     * The current world index.
     */
    protected int currentWorld = 0;

    private static final int SIZE = 128;

    /**
     * Shared BitSet
     */
    public IStateIntVector currentBitSet;
    /**
     * Nex free bit in the shared BitSet
     */
    protected int nextOffset;

    public final int getWorldIndex() {
        return currentWorld;
    }

    public final void createSharedBipartiteSet(int size){
        currentBitSet = makeBipartiteSet(size);
        nextOffset = -1;
    }

    /**
     * Factory pattern : shared StoredBitSetVector objects is return by the environment
     *
     * @return
     */
    @Override
    public final IStateIntVector getSharedBipartiteSetForBooleanVars() {
        if(currentBitSet == null){
            createSharedBipartiteSet(SIZE);
        }
        nextOffset++;
        if(nextOffset > currentBitSet.size()-1){
            increaseSizeOfSharedBipartiteSet(SIZE-(nextOffset % SIZE));
        }
        return currentBitSet;
    }

    /**
     * Return the next free bit in the shared StoredBitSetVector object
     *
     * @return
     */
    @Override
    public final int getNextOffset() {
        return nextOffset;
    }
}
