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
package choco.kernel.memory.recomputation;

import choco.kernel.memory.*;
import choco.kernel.memory.copy.EnvironmentCopying;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.memory.trailing.IndexedObject;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;
import gnu.trove.TIntStack;

import java.util.ArrayList;
import java.util.Stack;

public class EnvironmentRecomputation extends AbstractEnvironment {


    /**
     * Environment which performs the saves
     */
    public IEnvironment delegatedEnv;

    /**
     * Index of the world where the last fail occured
     */
    public int lastFail;

    /**
     * Pointer to the strategy
     */
    AbstractSearchStrategy strategy;

    /**
     * Stack recording the index of the saved world
     */
    Stack<Integer> savedWorldIdxStack;

    /**
     * Contains the choices made by the solver
     */
    Stack<BranchTrace> contexts;

    /**
     * Contains the indices, in contexts, of the first
     * context after a saved world
     */
    TIntStack indices;

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
        this(new EnvironmentTrailing(), 10);
    }

    public EnvironmentRecomputation(int envType, int gap) {
        switch (envType) {
            case 0  : delegatedEnv = new EnvironmentTrailing(); break;
            case 1  : delegatedEnv = new EnvironmentCopying() ; break;
            default : delegatedEnv = new EnvironmentTrailing(); break;
        }
        currentWorld = 0;
        savedWorldIdxStack = new Stack<Integer>();
        contexts = new Stack<BranchTrace>();
        indices = new TIntStack();
        this.gap = gap;
    }

    public EnvironmentRecomputation(IEnvironment envD, int gap) {

        currentWorld = 0;
        savedWorldIdxStack = new Stack<Integer>();
        contexts = new Stack<BranchTrace>();
        indices = new TIntStack();
        this.gap = gap;
        delegatedEnv = envD;


    }


    public void worldPush() {
        if ( currentWorld == 0|| (currentWorld-1) % gap == 0 || (lastFail - savedWorldIdxStack.peek())/2 == currentWorld ) {
            delegatedEnv.worldPush();
            lastSavedWorld = currentWorld;
            savedWorldIdxStack.push(currentWorld);
            indices.push(contexts.size());
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
        if (lastSavedWorld - currentWorld == 0) {
            savedWorldIdxStack.pop();
            lastSavedWorld = savedWorldIdxStack.peek();
            int ind = indices.pop();
            while(contexts.size()>ind){
                contexts.pop();
            }
        } else {
            delegatedEnv.worldPush();
            int ind = indices.peek();

            // Remove the last element (a goDown ctx).
            contexts.pop();

            // then apply the other
            for(int i = ind; i < contexts.size(); i++){
                BranchTrace trace = contexts.elementAt(i);
                try {
                    if (trace.isDown) {
                        trace.ctx.getBranching().goDownBranch(trace.ctx.getBranchingObject(), trace.ctx.getBranchIndex());
                    } else {
                        trace.ctx.getBranching().goUpBranch(trace.ctx.getBranchingObject(), trace.ctx.getBranchIndex());
                    }

                } catch (ContradictionException e) {
                    LOGGER.info("worldPop raised a contradiction (recomputation)");
                }
            }
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
    
    

    @Override
	public IStateInt makeIntProcedure(IStateIntProcedure procedure,
			int initialValue) {
    	return delegatedEnv.makeIntProcedure(procedure, initialValue);
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

    @Override
    public <T> PartiallyStoredVector<T> makePartiallyStoredVector() {
        return delegatedEnv.makePartiallyStoredVector();
    }

    @Override
    public PartiallyStoredIntVector makePartiallyStoredIntVector() {
        return delegatedEnv.makePartiallyStoredIntVector();
    }

    public IStateBitSet makeBitSet(int size) {
        return delegatedEnv.makeBitSet(size);
    }

    @Override
    public IStateIntVector makeBipartiteIntList(int[] entries) {
        return delegatedEnv.makeBipartiteIntList(entries);
    }

    @Override
    public IStateIntVector makeBipartiteSet(int[] entries) {
        return delegatedEnv.makeBipartiteSet(entries);
    }

    @Override
    public IStateIntVector makeBipartiteSet(int entries) {
        return delegatedEnv.makeBipartiteSet(entries);
    }

    @Override
    public IStateIntVector makeBipartiteSet(IndexedObject[] entries) {
        return delegatedEnv.makeBipartiteSet(entries);
    }

    @Override
    public IStateIntVector makeBipartiteSet(ArrayList<IndexedObject> entries) {
        return delegatedEnv.makeBipartiteSet(entries);
    }


    public IStateDouble makeFloat() {
        return delegatedEnv.makeFloat();
    }

    public IStateDouble makeFloat(double initialValue) {
        return delegatedEnv.makeFloat(initialValue);
    }

    public IStateLong makeLong() {
        return delegatedEnv.makeLong();
    }

    public IStateLong makeLong(int init) {
        return delegatedEnv.makeLong(init);        
    }

    public <T> IStateVector<T> makeVector() {
        return delegatedEnv.makeVector();
    }

    public IStateBinaryTree makeBinaryTree(int inf, int sup) {
        return delegatedEnv.makeBinaryTree(inf, sup);
    }

    @Override
    public IStateObject makeObject(Object obj) {
        return delegatedEnv.makeObject(obj);
    }

    /**
     * Add a choice made by the solver in contexts.
     * @param ctx   An IntBranchingTrace
     * @param isDown Boolean that states wether we are going down the search
     * tree, wether up.
     */
    public void pushContext(IntBranchingTrace ctx, boolean isDown) {
        contexts.push(new BranchTrace(ctx,isDown));
    }

    public void popContext(IntBranchingTrace ctx) {
        while(contexts.size()>0 && contexts.peek().ctx.getBranchingObject().equals(ctx.getBranchingObject())){
            contexts.pop();
        }
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
            this.ctx = ctx.copy();
            this.isDown = isDown;
        }

		@Override
		public String toString() {
			return ctx.toString()+":"+isDown;
		}
        
        
    }

}
