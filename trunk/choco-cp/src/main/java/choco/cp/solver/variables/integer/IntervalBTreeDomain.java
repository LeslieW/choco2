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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBinaryTree;
import choco.kernel.memory.IStateInt;
import gnu.trove.TIntStack;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 25, 2008
 * Time: 9:34:40 AM
 *
 * A choco domain class represented by a binary tree of int interval
 */
public class IntervalBTreeDomain extends AbstractIntDomain {

    /**
     * static instance of Random to get random number
     */
    protected static Random random = new Random(System.currentTimeMillis());

    /**
     * The binary tree representing the domain
     */
    public IStateBinaryTree btree;

    /**
     * Backtrackable int to avoid recalculating the number of value in the domain
     */
    IStateInt size;

    /**
     * the initial size of the domain (never increases)
     */
    protected int capacity;

        /**
     * A chained list implementing two subsets of values:
     * - the removed values waiting to be propagated
     * - the removed values being propagated
     * (each element points to the index of the enxt element)
     * -1 for the last element
     */
//    protected Stack stack;
    protected TIntStack stack;

    protected int lastSizeBeforePropagation;


    /**
     * Construct a new domain represented by a Binary Tree of Interval
     * @param v the associatede variable
     * @param a the lower bound
     * @param b the upper bound
     */
    public IntervalBTreeDomain(IntDomainVarImpl v, int a, int b)
    {
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        btree= env.makeBinaryTree(a,b);
        capacity = b - a + 1;
        size = env.makeInt(capacity);
//        stack = new Stack();
        stack = new TIntStack();
        lastSizeBeforePropagation = 0;
    }

    /**
     * Construct a new domain represented by a Binary Tree of Interval
     * @param v the associatede variable
     * @param sortedValues array of values
     */
    public IntervalBTreeDomain(IntDomainVarImpl v, int[] sortedValues)
    {
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        int a = sortedValues[0];
        btree= env.makeBinaryTree(a,a);
        capacity = sortedValues.length;
        IStateBinaryTree.Node n = btree.getRoot();
        for(int i = 1; i< capacity; i++){
            int b = sortedValues[i];
            if(b == a+1){
                n.setSup(b);
            }else{
                n = new IStateBinaryTree.Node(btree, b,b);
                btree.add(n, false);
            }
            a = b;
        }

        size = env.makeInt(btree.getSize());
//        stack = new Stack();
        stack = new TIntStack();
        lastSizeBeforePropagation=0;
    }

    /**
     * Gets the lowest value of the domain
     * @return the lower bound of the domain
     */
    public int getInf() {
        IStateBinaryTree.Node  n = btree.getFirstNode();
        return n.inf;
    }

    /**
     * Gets the greatest value of the domain
     * @return the greater bound of the domain
     */
    public int getSup() {
        IStateBinaryTree.Node  n = btree.getLastNode();
        return n.sup;
    }

    /**
     * Updates the inf bound of the domain to the given value
     * TODO: Must exist a better way than removing all values until the new inf is reached
     * @param x integer value
     * @return the new lower bound
     */
    public int updateInf(int x) {
        for (int i = this.getInf() ; i < x ; i++)
        {
            this.remove(i);
        }
        return this.getInf();
    }

    /**
     * Updates the sup of the domain to the given value
     * TODO: Must exist a better way than removing all values (e.g. removing all the node but the one containing the sup)
     * @param x integer value
     * @return the new greater bound
     */
    public int updateSup(int x) {
        for (int i = this.getSup() ; i > x ; i--)
        {
            this.remove(i);
        }
        return this.getSup();
    }

    /**
     * Checks wether a value is in the domain
     * @param x the value to be checked
     * @return true if x is in the domain, false otherwise
     */
    public boolean contains(int x) {
        return btree.find(x) != null;
    }

    /**
     * Removes a value from the domain
     * @param x the value to be removed
     * @return true if removal is a success, false otherwise
     */
    public boolean remove(int x) {
        boolean b = btree.remove(x);
        if (b){
            removeIndex(x);
            this.size.add(-1);
        }
        return b;
    }

//    private void updateStack(int i){
//        if(removedvalues==null)removedvalues=new Stack<Integer>();
//        removedvalues.push(i);
//    }

    private void removeIndex(int i) {
        stack.push(i);
    }

    /**
     * Restrict the domain to one value
     * TODO: must existe a better way to remove all the other Node in the tree
     * @param x integer value
     */
    public void restrict(int x) {
        IStateBinaryTree.Node current = btree.getRoot();
        while (current.leftNode != null)
        {
            btree.remove(current.leftNode);
        }
        while (current.rightNode != null)
            btree.remove(current.rightNode);
        btree.getRoot().setInf(x);
        btree.getRoot().setSup(x);
        this.size.set(1);
    }

    /**
     * Indicates the number of value in the domain
     * @return the size of the domain
     */
    public int getSize() {
        return this.size.get();
    }

    /**
     * gets the next value of x in this domain
     * @param x integer value
     * @return -infinity if not found, the next value otherwise
     */
    public int getNextValue(int x) {
        IStateBinaryTree.Node n = btree.nextNode(x);
        if (n == null)
            return Integer.MAX_VALUE;
        else if (n.contains(x+1))
            return x+1;
        else
            return n.getInf();
    }

    /**
     * gets the previous value of x in this domain
     * @param x integer value
     * @return -infinity if not found, the previous value otherwise
     */
    public int getPrevValue(int x) {
        IStateBinaryTree.Node n =btree.prevNode(x);
        if (n == null)
            return Integer.MIN_VALUE;
        else if (n.contains(x-1))
            return x-1;
        else
            return n.getSup();

    }

    /**
     * Has this domain a value greater than the parameter
     * TODO: getSup is to slow for that kind of operation
     * @param x integer value
     * @return wether the domain contains a greater value
     */
    public boolean hasNextValue(int x)
    {
        return x < getSup();
    }

    /**
     * Has this domain a value lower than the parameter
     * TODO: it is not efficient to call getInf
     * @param x integer value
     * @return wether this domain contains a lower value
     */
    public boolean hasPrevValue(int x)
    {
        return x > getInf();
    }

    /**
     * Easy way to get a random value in the domain
     * Definitely not selected through an uniform distribution
     * TODO: Find a better way to select a value really at random
     * @return a value selected at random in the domain
     */
    public int getRandomValue()
    {
        ArrayList<IStateBinaryTree.Node> tmp = new ArrayList<IStateBinaryTree.Node>();
        IStateBinaryTree.Node  current = btree.getRoot();
        while (current != null)
        {
            tmp.add(current);
            if (random.nextBoolean())
            {
                current = current.leftNode;

            }
            else
            {
                current = current.rightNode;
            }
        }
        IStateBinaryTree.Node selected = tmp.get(random.nextInt(tmp.size()));
        int val = random.nextInt(selected.sup-selected.inf+1);
        return val+selected.inf;

    }


    protected DisposableIntIterator _cachedDeltaIntDomainIterator = null;

    public DisposableIntIterator getDeltaIterator() {
      DeltaIntDomainIterator iter = (DeltaIntDomainIterator) _cachedDeltaIntDomainIterator;
      if (iter != null && iter.reusable) {
        iter.init();
        return iter;
      }
      _cachedDeltaIntDomainIterator = new DeltaIntDomainIterator(this);
        return _cachedDeltaIntDomainIterator;
    }

    protected static class DeltaIntDomainIterator extends DisposableIntIterator {
        protected IntervalBTreeDomain domain;
        protected int currentVal;

        private DeltaIntDomainIterator(IntervalBTreeDomain dom) {
            domain = dom;
            init();
        }

      public void init() {
          super.init();
      }

        public boolean hasNext() {
            if(domain.stack.size()>0){
                currentVal = domain.stack.pop();
                return true;
            }else{
                return false;
            }
        }

        public int next() {
            return currentVal;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such
     */
    public void freezeDeltaDomain() {
        // freeze all data associated to bounds for the the event
        // if the delta domain is already being iterated, it cannot be frozen
        if (lastSizeBeforePropagation  ==  0){
            // the set of values waiting to be propagated is now "frozen" as such,
            // so that those value removals can be iterated and propagated
              lastSizeBeforePropagation = stack.size();
            // the container (link list) for values waiting to be propagated is reinitialized to an empty set
//            firstIndexToBePropagated = -1;
        }
    }

    /**
     * after an iteration over the delta domain, the delta domain is reopened again.
     *
     * @return true iff the delta domain is reopened empty (no updates have been made to the domain
     *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
     *         were made to the domain, while the delta domain was frozen).
     */
    public boolean releaseDeltaDomain() {
        // release all data associated to bounds for the the event
        // special case: the set of removals was not being iterated (because the variable was instantiated, or a bound was updated)
        if (lastSizeBeforePropagation == stack.size()) {
//        if (firstIndexBeingPropagated == -1) {
            stack.clear();
            lastSizeBeforePropagation = 0;
            // return true because the event has been "flushed" (nothing more is awaiting)
            return true;
        } else { // standard case: the set of removals was being iterated
            // empty the set of values that were being propagated
            lastSizeBeforePropagation  = 0;
            // if more values are waiting to be propagated, return true
            return (stack.size()==0);
        }
    }

    public boolean getReleasedDeltaDomain() {
        return (stack.size()==0 && lastSizeBeforePropagation == 0);
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    public void clearDeltaDomain() {
        lastSizeBeforePropagation = 0;
        stack.clear();
    }


    /**
     * Check wether this domain is Enumerated or not
     * @return true
     */
    public boolean isEnumerated() {
        return true;
    }

    /**
     * Check if this domain is a 0-1 domain
     * @return false
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * pretty print of the domain
     * @return a new string
     */
    public String pretty()
    {
        return btree.toString();
    }

    public String toString(){
        return btree.toString();
    }
}