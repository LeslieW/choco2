package i_want_to_use_this_old_version_of_choco.mem;
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
public interface IEnvironment {
    /**
     * Index of the Environment using trailing
     */
    int ENV_TRAILING = 0;

    /**
     * Index of the Environment using copying
     */
    int ENV_COPYING = 1;


    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredBoolTrail} for storing booleans.
     */

    int BOOL_TRAIL = 0;
    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredIntTrail} for storing integers.
     */

    int INT_TRAIL = 1;
    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredVectorTrail} for storing vectors.
     */

    int VECTOR_TRAIL = 2;
    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredIntVectorTrail} for storing
     * integer vectors.
     *
     */

    int INT_VECTOR_TRAIL = 3;
    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredFloatTrail} for storing
     * integer vectors.
     *
     */

    int FLOAT_TRAIL = 4;
    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredLongTrail} for storing
     * integer vectors.
     *
     */

    int LONG_TRAIL = 5;


    /**
     * Index of the {@link i_want_to_use_this_old_version_of_choco.mem.trailing.StoredIntIntervalTrail} for storing
     * integer vectors.
     *
     */

    int INT_INTERVAL_TRAIL = 6;



    /**
     * Returns the world number.
     *
     * @return current world index
     */

    int getWorldIndex();

    /**
     * Starts a new branch in the search tree.
     */

    void worldPush();

    /**
     * Backtracks to the previous choice point in the search tree.
     */
    void worldPop();


//    /**
//     *  Record the last branching choice in the Environment.
//     */
//
//    void pushContext(IntBranchingTrace ctx);
//
//    /**
//     *  Attaches a given search solver to this Environment
//     */
//
//    void setSearchSolver(AbstractGlobalSearchSolver solver);
//
//    /**
//     *  Record the last world index where the search failed
//     */
//
//    void setLastFail(int worldIndex);
//
//    /**
//     *  For recomputation, get how many times worlds have been saved
//     */
//
//    int getHowManySaves();
//
//    /**
//     *  Return the index of the last saved world
//     */
//
//    int getLastSavedWorldIndex();
//



    /**
     * Factory pattern: new IStateInt objects are created by the environment
     * (no initial value is assigned to the backtrackable search)
     */

    IStateInt makeInt();

    /**
     * Factory pattern: new IStateInt objects are created by the environment
     *
     * @param initialValue the initial value of the backtrackable integer
     */

    IStateInt makeInt(int initialValue);

    /**
     * Factory pattern: new IStateBool objects are created by the environment
     *
     * @param initialValue the initial value of the backtrackable boolean
     */

    IStateBool makeBool(boolean initialValue);

    /**
     * Factory pattern: new IStateIntVector objects are created by the environment.
     * Creates an empty vector
     */

    IStateIntVector makeIntVector();

    /**
     * Factory pattern: new IStateIntVector objects are created by the environment
     *
     * @param size         the number of entries in the vector
     * @param initialValue the common initial value for all entries (backtrackable integers)
     */

    IStateIntVector makeIntVector(int size, int initialValue);

    /**
     * Factory pattern: new IStateIntVector objects are created by the environment
     *
     * @param entries an array to be copied as set of initial contents of the vector
     */

    IStateIntVector makeIntVector(int[] entries);

    /**
     * Factory pattern: new IStateVector objects are created by the environment.
     * Creates an empty vector
     */

    IStateVector makeVector();

    PartiallyStoredVector makePartiallyStoredVector();

    PartiallyStoredIntVector makePartiallyStoredIntVector();

    /**
     * Factory pattern: new StoredBitSetVector objects are created by the environment
     */
    IStateBitSet makeBitSet(int size);

    IStateBitSet makeBitSet(int size, boolean fixed);

    IStateBitSet makeBitSet(int[] entries);

    /**
     * Factory pattern: new StoredFloat objects are created by the environment
     * (no initial value is assigned to the backtrackable search)
     */

    IStateFloat makeFloat();

    public IStateLong makeLong();

    public IStateLong makeLong(int init);


    /**
     * Factory pattern: new StoredFloat objects are created by the environment
     *
     * @param initialValue the initial value of the backtrackable search
     */

    IStateFloat makeFloat(double initialValue);


    IStateIntInterval makeIntInterval(int initialInf, int initialSup);



}
