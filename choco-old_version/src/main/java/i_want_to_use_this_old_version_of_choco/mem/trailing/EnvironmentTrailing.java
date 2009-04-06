package i_want_to_use_this_old_version_of_choco.mem.trailing;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
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
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/
import i_want_to_use_this_old_version_of_choco.mem.*;

import java.util.logging.Logger;

/**
 * The root class for managing memory and sessions.
 * <p/>
 * A environment is associated to each problem.
 * It is responsible for managing backtrackable data.
 */
public class EnvironmentTrailing implements IEnvironment {


    /**
     * The maximum numbers of objects that a
     * {@link i_want_to_use_this_old_version_of_choco.mem.ITrailStorage} can handle.
     */

    private int maxHist;


    /**
     * The maximum numbers of worlds that a
     * {@link i_want_to_use_this_old_version_of_choco.mem.ITrailStorage} can handle.
     */

    private int maxWorld;


    /**
     * The current world number (should be less
     * than <code>maxWorld</code>).
     */

    public int currentWorld = 0;


    /**
     * Contains all the {@link i_want_to_use_this_old_version_of_choco.mem.ITrailStorage} trails for
     * storing different kinds of data.
     */

    protected ITrailStorage[] trails;

    /**
     * Reference to the root Logger object (using the java.util.logging package)
     */

    private static Logger logger = Logger.getLogger("choco");

    /**
     * capacity of the trailing stack (in terms of number of worlds that can be handled)
     */
    private int maxWorlds = 0;

    /**
     * Constructs a new <code>IEnvironment</code> with
     * the default stack sizes : 50000 and 1000.
     */

    public EnvironmentTrailing() {
        maxHist = 50; //50000;
        maxWorld = 10; //1000;
        trails = new ITrailStorage[]{
                new StoredBoolTrail(this, maxHist, maxWorld),
                new StoredIntTrail(this, maxHist, maxWorld),
                new StoredVectorTrail(this, maxHist, maxWorld),
                new StoredIntVectorTrail(this, maxHist, maxWorld),
                new StoredFloatTrail(this, maxHist, maxWorld),
                new StoredLongTrail(this, maxHist, maxWorld),
                new StoredIntIntervalTrail(this, maxHist, maxWorld),
        };
    }

    /**
     * Returns the <code>i</code>th trail in the trail array.
     *
     * @param i index of the trail.
     */

    public ITrailStorage getTrail(int i) {
        return trails[i];
    }


    public int getWorldIndex() {
        return currentWorld;
    }


    public void worldPush() {
        for (ITrailStorage trail : trails) {
            trail.worldPush();
        }
        currentWorld++;
        if (currentWorld + 1 == maxWorld)
            resizeWorldCapacity(maxWorld * 3 / 2);
    }


    public void worldPop() {
        for (ITrailStorage trail : trails) {
            trail.worldPop();
        }
        currentWorld--;
    }

    public void worldCommit() {
        if (currentWorld == 0) throw new IllegalStateException("Commit in world 0?");
        for (ITrailStorage trail : trails) {
            trail.worldCommit();
        }
        currentWorld--;
    }

    public IStateInt makeInt() {
        return new StoredInt(this);
    }

    public IStateInt makeInt(int initialValue) {
        return new StoredInt(this, initialValue);
    }

    public IStateBool makeBool(boolean initialValue) {
        return new StoredBool(this, initialValue);
    }

    public IStateIntVector makeIntVector() {
        return new StoredIntVector(this);
    }

    public IStateIntVector makeIntVector(int size, int initialValue) {
        return new StoredIntVector(this, size, initialValue);
    }

    public IStateIntVector makeIntVector(int[] entries) {
        return new StoredIntVector(this, entries);
    }


    public IStateVector makeVector() {
        return new StoredVector(this);
    }

    public PartiallyStoredVector makePartiallyStoredVector() {
        return new PartiallyStoredVector(this);
    }

    public PartiallyStoredIntVector makePartiallyStoredIntVector() {
        return new PartiallyStoredIntVector(this);
    }

    public IStateBitSet makeBitSet(int size) {
        return new StoredJavaBitSet(this, size);
    }

    public IStateBitSet makeBitSet(int size, boolean fixed) {
        return new StoredJavaBitSet(this, size, fixed);
    }

    public IStateBitSet makeBitSet(int[] entries) {
        return new StoredJavaBitSet(this, 0);  // TODO
    }

    public IStateFloat makeFloat() {
        return new StoredFloat(this);
    }

    public IStateFloat makeFloat(double initialValue) {
        return new StoredFloat(this, initialValue);
    }

    public IStateIntInterval makeIntInterval(int initialInf, int initialSup) {
        return new StoredIntInterval(this, initialInf, initialSup);
    }

    public IStateLong makeLong() {
        return new StoredLong(this);
    }

    public IStateLong makeLong(int init) {
        return new StoredLong(this,init);
    }

    public int getTrailSize() {
        int s = 0;
        for (ITrailStorage trail : trails) s += trail.getSize();
        return s;
    }

    public int getIntTrailSize() {
        return trails[INT_TRAIL].getSize();
    }

    public int getIntVectorTrailSize() {
        return trails[INT_VECTOR_TRAIL].getSize();
    }

    private void resizeWorldCapacity(int newWorldCapacity) {
        for (ITrailStorage trail : trails) trail.resizeWorldCapacity(newWorldCapacity);
        maxWorld = newWorldCapacity;
    }
}

