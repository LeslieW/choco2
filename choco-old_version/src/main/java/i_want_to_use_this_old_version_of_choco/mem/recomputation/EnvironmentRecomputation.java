package i_want_to_use_this_old_version_of_choco.mem.recomputation;
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
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.mem.*;
import i_want_to_use_this_old_version_of_choco.mem.copy.EnvironmentCopying;
import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.search.IntBranchingTrace;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class EnvironmentRecomputation implements IEnvironment {


    public int currentWorld = 0;

    /**
     * Environment which performs the saves
     */
    public IEnvironment delegatedEnv;

    /**
     * Index of the world where the last fail occured
     */
    public int lastFail;

    /**
     * Pointer to the solver
     */
    AbstractGlobalSearchSolver solver;

    /**
     * Stack recording the index of the saved world
     */
    Stack<Integer> savedWorldIdxStack;

    /**
     * Contains the choices made by the solver
     */
    Map<Integer, Vector<BranchTrace>> contexts;

    /**
     * The last saved world equivalent to the top of savedWorldIdxStack
     */
    public int lastSavedWorld;

    /**
     * Counts how many times a worldPush has been called on the delegated
     * Environment
     */
    private int nbPush = 0 ;

    /**
     * Counts the number of time a Contradiction has been encountered
     */
    private int nbFail = 0 ;

    /**
     * The default gap between two saves
     */
    private int gap;

    public EnvironmentRecomputation() {
        this(new EnvironmentTrailing(), Integer.MAX_VALUE);
    }

    public EnvironmentRecomputation(int envType, int gap) {
        switch (envType) {
            case 0  : delegatedEnv = new EnvironmentTrailing(); break;
            case 1  : delegatedEnv = new EnvironmentCopying() ; break;
            default : delegatedEnv = new EnvironmentTrailing(); break;
        }
        currentWorld = 0;
        savedWorldIdxStack = new Stack<Integer>();
        contexts = new HashMap<Integer, Vector<BranchTrace>>();
        this.gap = gap;
    }

    public EnvironmentRecomputation(IEnvironment envD, int gap) {

        currentWorld = 0;
        savedWorldIdxStack = new Stack<Integer>();
        contexts = new HashMap<Integer, Vector<BranchTrace>>();
        this.gap = gap;
        delegatedEnv = envD;


    }



    public int getWorldIndex() {
        return currentWorld;
    }

    public void worldPush() {
        if ( currentWorld == 0|| (currentWorld -1) % gap == 0 || (lastFail - savedWorldIdxStack.peek())/2 == currentWorld ) {
            delegatedEnv.worldPush();
            lastSavedWorld = currentWorld;
            savedWorldIdxStack.push(currentWorld);
            nbPush++;
        }
        currentWorld++;


    }

    public void worldPop() {
        delegatedEnv.worldPop();
        lastSavedWorld = savedWorldIdxStack.peek();
        currentWorld--;

        if (lastSavedWorld == 0) {
            return;
        }
        if ( lastSavedWorld -currentWorld == 0) {
            savedWorldIdxStack.pop();
            lastSavedWorld = savedWorldIdxStack.peek();
        }
        else {
            delegatedEnv.worldPush();


            for (int i = (lastSavedWorld) ; i < currentWorld  ; i++) {

                for (BranchTrace trace : contexts.get(i)) {

                    try {
                        if (trace.isDown) {
                            trace.ctx.getBranching().goDownBranch(trace.ctx.getBranchingObject(), trace.ctx.getBranchIndex());
                        }
                        else
                            trace.ctx.getBranching().goUpBranch(trace.ctx.getBranchingObject(),trace.ctx.getBranchIndex());

                    } catch (ContradictionException e) {
                        System.out.println("bizarre");
                    }
                }
            }
            contexts.remove(currentWorld+1);

        }

    }

    public void worldCommit() {

    }

    public IStateInt makeInt() {
        return delegatedEnv.makeInt();
    }

    public IStateInt makeInt(int initialValue) {
        return delegatedEnv.makeInt(initialValue);
    }

    public IStateBool makeBool(boolean initialValue) {
        return delegatedEnv.makeBool(initialValue);
    }

    public IStateIntVector makeIntVector() {
        return delegatedEnv.makeIntVector();
    }

    public IStateIntVector makeIntVector(int size, int initialValue) {
        return delegatedEnv.makeIntVector(size,initialValue);
    }

    public IStateIntVector makeIntVector(int[] entries) {
        return delegatedEnv.makeIntVector(entries);
    }

    public PartiallyStoredVector makePartiallyStoredVector() {
        return delegatedEnv.makePartiallyStoredVector();
    }

    public PartiallyStoredIntVector makePartiallyStoredIntVector() {
        return delegatedEnv.makePartiallyStoredIntVector();
    }

    public IStateBitSet makeBitSet(int size) {
        return delegatedEnv.makeBitSet(size);
    }

    public IStateBitSet makeBitSet(int size, boolean fixed) {
        return delegatedEnv.makeBitSet(size,fixed);
    }

    public IStateBitSet makeBitSet(int[] entries) {
        return delegatedEnv.makeBitSet(entries);
    }

    public IStateFloat makeFloat() {
        return delegatedEnv.makeFloat();
    }

    public IStateFloat makeFloat(double initialValue) {
        return delegatedEnv.makeFloat(initialValue);
    }

    public IStateLong makeLong() {
        return delegatedEnv.makeLong();
    }

    public IStateLong makeLong(int init) {
        return delegatedEnv.makeLong(init);        
    }

    public IStateVector makeVector() {
        return delegatedEnv.makeVector();
    }

    public IStateIntInterval makeIntInterval(int initialInf, int initialSup) {
        return delegatedEnv.makeIntInterval(initialInf, initialSup);
    }

    /**
     * Add a choice made by the solver in contexts.
     * @param ctx   An IntBranchingTrace
     * @param isDown Boolean that states wether we are going down the search
     * tree, wether up.
     */
    public void pushContext(IntBranchingTrace ctx, boolean isDown) {
        if (contexts.get(currentWorld) == null) {
            Vector<BranchTrace> v = new Vector<BranchTrace>();
            contexts.put(currentWorld,v);
        }

        contexts.get(currentWorld).add(new BranchTrace(ctx,isDown));
    }

    /**
     * Records the world index when the last Contradiction occured
     * @param worldIndex index of the world
     */
    public void setLastFail(int worldIndex) {
        lastFail = worldIndex;
    }

    /**
     * Returns how many times saving operation has been performed.
     * @return int nbPush
     */
    public int getNbSaves() {
        return nbPush;
    }


    /**
     * Returns the last saved world index
     * @return  int lastSavedWorld
     */
    public int getLastSavedIndex() {
        return lastSavedWorld;
    }

    /**
     * Increments nbFail by one
     */
    public void incNbFail() {
        nbFail++;
    }

    /**
     * returns the number of fails
     * @return int nbFail
     */
    public int getNbFail() {
        return nbFail;
    }


    private final class BranchTrace {
        public final boolean isDown;
        public final IntBranchingTrace ctx ;

        public BranchTrace(IntBranchingTrace ctx, boolean isDown) {
            this.ctx = ctx;
            this.isDown = isDown;
        }
    }

}
