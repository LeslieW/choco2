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
package choco.cp.solver.constraints.global.automata.costregular;


import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateVector;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 21, 2007
 * Time: 1:38:31 PM
 */
public class CostRegular extends AbstractLargeIntSConstraint
{
    /**
     * Switch to turn on/off the event based propagation
     */
    public static boolean INCREMENTAL = true;


    /**
     * The memory manager
     */
    protected IEnvironment env;


    /**
     * Cost of a transistion from layer i with labeled j 
     */
    protected int[][] costs;


    /**
     * Automaton describing
     */
    //private DFA automaton;
    private FiniteAutomaton automaton;


    /**
     * Number of nodes in the automaton
     */
    protected int nbNodes;

    /**
     * Supports
     */
    protected IStateVector[][] Q;


    /**
     * Structure to speed up Q access
     */
    protected int[] size;


    /**
     * offsets of the variables
     */
    public int[] offset;


    /**
     * Cost Variable
     */
    public IntDomainVar cVar;


    /**
     * Variables that must satisfy the regular constraint
     */
    public IntDomainVar[] myVars;


    /**
     * LayeredGraph representing the automaton
     */
    @SuppressWarnings("unchecked")
    protected HashMap<Integer,State>[] layer;


    /**
     * Stack of the Arc whose vertices must be re-checked
     */
    public Stack<Arc> toVisit;

    public HashSet<State>[] toUpdate = new HashSet[4];


    /**
     * Pointer to the first state in the graph
     */
    public State source;


    /**
     * Pointer to the last state in the graph (usually a fake one)
     */
    protected State puit;




    /**
     * Constructs a new CostRegular constraint
     * @param vars the sequence of variables the constraint must ensure it belongs to the regular language
     * @param costVar the cost variable
     * @param auto  the automaton describing the regular language
     * @param costs the cost of taking value j for the variable i
     * @param environment
     * @return  a instance of the constraint
     */
    public static CostRegular make(IntDomainVar[] vars, IntDomainVar costVar, FiniteAutomaton auto, int[][] costs, IEnvironment environment)
    {
        IntDomainVar[] tab = new IntDomainVar[vars.length+1];
        System.arraycopy(vars,0,tab,0,vars.length);
        tab[vars.length] = costVar;
        return new CostRegular(tab,auto,costs, environment);
    }



    /**
     * Constructs a new CostRegular constraint, this constraint ensures that the sequence of variables values
     * will follow a pattern defined by a DFA and that this sequence has a cost bounded by the cost variable
     * @param vars The variables of the constraint, the last variable is the cost one
     * @param auto the automaton that defined the allowed tuples
     * @param costs the cost of taking value j for the variable i
     * @param environment
     */
    @SuppressWarnings("unchecked")
    protected CostRegular(IntDomainVar[] vars, FiniteAutomaton auto, int[][] costs, IEnvironment environment)
    {
        super(vars);
        this.automaton = auto;
        if (costs != null)
        {
            this.costs = new int[costs.length+1][];
            System.arraycopy(costs, 0, this.costs, 0, costs.length);
            this.costs[costs.length] = new int[1] ;
        }

        this.env = environment;
        if (auto != null)
            this.nbNodes = auto.size();

        this.cVar = vars[vars.length -1];
        myVars = new IntDomainVar[vars.length -1];
        System.arraycopy(vars,0,myVars,0,vars.length -1);

        size = new int[myVars.length];
        offset = new int[vars.length];
        offset[myVars.length] = 0;
        //int totalSize = 0;
        this.Q = new IStateVector[myVars.length][];
        for (int i = 0 ; i < myVars.length ; i++)
        {
            size[i] = myVars[i].getSup() - myVars[i].getInf() +1;
            //totalSize+= size[i];
            offset[i] = myVars[i].getInf();
            this.Q[i] = new IStateVector[size[i]];
        }

        layer = (HashMap<Integer, State>[]) new HashMap[myVars.length+2];
        toVisit = new Stack<Arc>();
        for (int i = 0 ;i < toUpdate.length; i++)
        {
            toUpdate[i] = new HashSet<State>();
        }
    }


    /**
     * adds a State to the support QIj
     * @param i the index of the variable
     * @param j the value of the variable
     * @param s the State to add
     */
    protected void addToQij(int i, int j, State s)
    {
        if (Q[i][j-offset[i]] == null)
            Q[i][j-offset[i]] = env.makeVector();
        getQij(i,j).add(s);
    }


    /**
     * removes a State from the support Qij
     * @param i the index of the variable
     * @param j the value of the variable
     * @param s the State to remove
     */
    protected void remFromQij(int i, int j, State s)
    {
        remove(getQij(i,j),s);
    }

    /**
     * return the set of support for (i,j)
     * @param i index of the variable
     * @param j value of the variable
     * @return an Vector of states
     */
    protected IStateVector getQij(int i, int j)
    {
        //int index = i * size[i] + (j-offset[i]);
        return Q[i][j-offset[i]];
    }


    /**
     * retrieve the cost of transition (ij)
     * @param i index of the variable
     * @param j  value of the transition
     * @return cost double
     */
    protected int getCost(int i, int j)
    {
        return costs[i][j-offset[i]];
    }


    /**
     * clears the data structure for non event-based propagation
     */
    protected void clearStructures()
    {
        if (!toVisit.isEmpty())
            toVisit.clear();
        for (IStateVector[] aQ : Q) {
            for (IStateVector anAQ : aQ) {
                int l = anAQ == null ? 0 : anAQ.size();
                for (int k = 0; k < l; k++)
                    anAQ.removeLast();
            }
        }
    }


    /**
     * Return the ith layer in the layered graph
     * @param i the requested layer
     * @return an HashMap of states in the layer
     */
    protected HashMap<Integer,State> getLayer(int i)
    {
        return layer[i];
    }

    protected int delta(int i, int j, int k)
    {
        return this.automaton.delta(k,j);
    }

    protected boolean isAccepting(int idx)
    {
        return this.automaton.isAccepting(idx);
    }

    protected int getStart()
    {
        return this.automaton.getStartingState();
    }


    /**
     * Creates the initial directed layered graph and removes the inconsistent arcs
     * @throws ContradictionException if a domain gets empty
     */
    protected void initGraph() throws ContradictionException
    {

        for (int i = 0 ; i < layer.length ; i++)
        {
            layer[i] = new HashMap<Integer,State>();
        }

        source = new State(0,getStart(),this);
        source.pccS.set(0);
        source.pgcS.set(0);
        layer[0].put(source.index,source);

        DisposableIntIterator varIter;
        Iterator<State> layerIter;
        for (int i = 0 ; i < myVars.length ; i++)
        {
            varIter = myVars[i].getDomain().getIterator();
            int j;
            try{
            while (varIter.hasNext())
            {
                j = varIter.next();
                layerIter = layer[i].values().iterator();
                while (layerIter.hasNext())
                {
                    State s = layerIter.next();
                    int k = s.index;
                    int succ= delta(i,j,k);
                    if (succ >= 0)
                    {

                        State next = layer[i+1].get(succ);
                        if (next == null)
                        {
                            next = new State(i+1,succ,this);
                            next.pccS.set(s.pccS.get() + getCost(i,j));
                            next.pccPred.set(s.index);

                            next.pgcS.set(s.pgcS.get() + getCost(i,j));
                            next.pgcPred.set(s.index);

                            layer[i+1].put(succ,next);
                        }
                        else
                        {
                            int pccSTmp = next.pccS.get();
                            int pccSNew = s.pccS.get()+getCost(i,j);
                            if (pccSTmp > pccSNew)
                            {
                                next.pccS.set(pccSNew);
                                next.pccPred.set(s.index);
                            }

                            int pgcSTmp = next.pgcS.get();
                            int pgcSNew = s.pgcS.get() + getCost(i,j);

                            if (pgcSTmp < pgcSNew)
                            {
                                next.pgcS.set(pgcSNew);
                                next.pgcPred.set(s.index);
                            }
                        }
                        Arc tmp = new Arc(s,j,next);
                        addToQij(i,j,s);
                        next.addInarc(tmp);
                        s.addOutarc(tmp);

                    }
                }

            }
            }finally {
                varIter.dispose();
            }

        }

        puit = new State(myVars.length+1,Integer.MAX_VALUE,this);
        layer[myVars.length+1].put(Integer.MAX_VALUE,puit);


        HashSet<Integer> toRemoveA = new HashSet<Integer>(nbNodes);
        Iterator<Integer> it = layer[myVars.length].keySet().iterator();
        int idx;
        while (it.hasNext())
        {
            idx = it.next();
            if (!isAccepting(idx))
                toRemoveA.add(idx);
            else
            {
                State s = layer[myVars.length].get(idx);

                if (s.pccS.get() < puit.pccS.get())
                {
                    puit.pccS.set(s.pccS.get());
                    puit.pccPred.set(s.index);

                }
                if (s.pgcS.get() > puit.pgcS.get())
                {
                    puit.pgcS.set(s.pgcS.get());
                    puit.pgcPred.set(s.index);
                }
                s.pccP.set(0);
                s.pgcP.set(0);
                s.pccSucc.set(Integer.MAX_VALUE);
                s.pgcSucc.set(Integer.MAX_VALUE);

                Arc tmp = new Arc(s,0,puit);
                s.addOutarc(tmp);
                puit.addInarc(tmp);
                puit.pccP.set(0);
                puit.pgcP.set(0);



            }
        }
        for (Integer r : toRemoveA)
            layer[myVars.length].remove(r);


        BitSet mark = new BitSet(nbNodes);
        for (int i = myVars.length -1  ; i >= 0 ; i--)
        {
            mark.set(0,nbNodes);
            varIter = myVars[i].getDomain().getIterator();
            try{
            while(varIter.hasNext())
            {
                HashSet<State> toRemove = new HashSet<State>(nbNodes);
                int j = varIter.next();

                int sz = (getQij(i,j) == null) ? 0 : getQij(i,j).size();
                for (int m = 0 ; m < sz ; m++)
                {
                    State s = (State) getQij(i,j).get(m);
                    int succ = delta(i,j,s.index);
                    if (layer[i+1].containsKey(succ))
                    {
                        State next = (layer[i+1].get(succ));
                        int c1 = s.pccS.get()+next.pccP.get()+getCost(i,j);
                        int c2 = s.pgcS.get()+next.pgcP.get()+getCost(i,j);
                        if (c1 > cVar.getSup() || c2 < cVar.getInf())
                        {
                            toRemove.add(s);
                            Arc tmp = new Arc(s,j,next);
                            s.remOutArc(tmp);
                            next.remInarc(tmp);
                            if (next.pccPred.get() == s.index)
                                next.updatePccS();
                            if (next.pgcPred.get() == s.index)
                                next.updatePgcS();

                        }

                        else
                        {
                            int pccPTmp = s.pccP.get();
                            int pccPNew = next.pccP.get() + getCost(i,j);

                            if(pccPNew < pccPTmp)
                            {
                                s.pccP.set(pccPNew);
                                s.pccSucc.set(succ);
                            }

                            int pgcPTmp = s.pgcP.get();
                            int pgcPNew = next.pgcP.get() + getCost(i,j);

                            if (pgcPNew > pgcPTmp)
                            {
                                s.pgcP.set(pgcPNew);
                                s.pgcSucc.set(succ);
                            }
                            mark.clear(s.index);
                        }
                    }
                    else
                    {
                        toRemove.add(s);
                        s.remOutArc(new Arc(s,j,new State(0,succ,this)));
                    }
                }
                for (State s : toRemove)
                    remFromQij(i,j,s);
            }
            }finally {
                varIter.dispose();
            }
            for (int b = mark.nextSetBit(0) ; b >= 0  ; b= mark.nextSetBit(b+1))
            {
                layer[i].remove(b);
            }
        }

    }

    /**
     * Print all Arcs from the source to the sink
     */
    public void printAll()
    {
        printAll(source);
    }

    protected void printAll(State s) {
       if(LOGGER.isLoggable(Level.INFO)) {
    	@SuppressWarnings("unchecked")
        HashSet<Arc>[] map = new HashSet[vars.length+1];
        StringBuffer st = new StringBuffer();
        st.append(puit).append("\n");
        st.append(size[0]).append("\n");
        for (int i = 0 ; i < map.length ; i++)
            map[i] = new HashSet<Arc>();
        printAll(s,map);
        for (int i = 0 ; i < map.length ; i++)
        {
            st.append("\n");
            st.append("VAR = ").append(i).append("  AT WORLD : ").append(env.getWorldIndex()).append("\n");
            for (Object a : map[i])
                st.append(a).append("\n");
        }
        LOGGER.info(st.toString());
       }
    }

    protected void printAll(State s, HashSet<Arc>[] map)
    {
        HashSet<State> next = new HashSet<State>();
        for (int i = 0 ; i < s.outArcs.size() ; i++)
        {
            Arc a = (Arc) s.outArcs.get(i);
            map[s.layer].add(a);
            next.add(a.getArcDestination());
        }
        for ( State sn : next)
            printAll(sn,map);

    }


    /**
     * initial value pruning deduced from the layered graph
     * @throws ContradictionException if a domain gets empty
     */
    protected void filter() throws ContradictionException
    {
        for (int i = 0 ; i < myVars.length ; i++)
        {
            for (int j = 0 ; j < Q[i].length ; j++)
            {
                if(Q[i][j] == null || Q[i][j].isEmpty())
                    vars[i].removeVal(j+offset[i], this, false);
            }
        }

        updateCostBound();
    }


    /**
     * Updates the cost variable bounds in relation to the shortest and longest path in the layered graph
     * @throws ContradictionException if a domain gets empty
     */
    protected void updateCostBound() throws ContradictionException
    {
        for (State s : layer[0].values())
        {
            int pgcP = s.pgcP.get();
            if(pgcP < cVar.getSup())
            {
                cVar.updateSup(pgcP, this, true);
            }
        }
        for (State s : layer[myVars.length+1].values())
        {
            int pccS = s.pccS.get();
            if(pccS > cVar.getInf())
            {
                cVar.updateInf(pccS, this, true);
            }
        }

    }


    /**
     * TODO Check wether this could be done incrementaly
     * Removes arcs that became inconsistent after the cost variable has been trimmed
     * @throws ContradictionException if a domain gets empty
     */
    protected void awakeOnCost() throws ContradictionException
    {
        for (int i = 0 ; i < myVars.length ; i++)
        {
            for (State s : layer[i].values())
            {

                for (int k = 0 ; k < s.outArcs.size(); k++) //Arc n : tmp) {
                {
                    Arc n = (Arc) s.outArcs.get(k);
                    boolean removed = false;
                    boolean delete = n.getArcOrigin().pccS.get() == Integer.MAX_VALUE || n.getArcDestination().pccP.get() == Integer.MAX_VALUE;
                    if (delete || n.getArcOrigin().pccS.get() + getCost(i, n.getArcLabel()) + n.getArcDestination().pccP.get() > cVar.getSup()) {
                        n.getArcOrigin().remArc(n);
                        removed = true;

                    }
                    delete = n.getArcOrigin().pgcS.get() == Integer.MIN_VALUE || n.getArcDestination().pgcP.get() == Integer.MIN_VALUE;
                    if (!removed && delete || n.getArcOrigin().pgcS.get() + getCost(i, n.getArcLabel()) + n.getArcDestination().pgcP.get() < cVar.getInf()) {
                        n.getArcOrigin().remArc(n);
                        removed = true;

                    }
                    emptyStack();
                    if (removed && i!= myVars.length)
                        k--;

                }
            }
        }
    }


    protected void emptyStack() throws ContradictionException
    {
       /* while (!toVisit.isEmpty() || !toUpdate[0].isEmpty() ||!toUpdate[1].isEmpty() ||!toUpdate[2].isEmpty() || !toUpdate[3].isEmpty())
        { for (int i = 0 ; i < toUpdate.length ; i++)
        {
            while (!toUpdate[i].isEmpty()){
                Iterator k = toUpdate[i].iterator();
                State s = (State)k.next();
                if (i == 0)
                    s.updatePccS();
                else if (i == 1)
                    s.updatePccP();
                else if (i == 2)
                    s.updatePgcS();
                else if (i == 3)
                    s.updatePgcP();
                toUpdate[i].remove(s);

            }
        }      */
        while (!toVisit.isEmpty())
        {
            Arc toRemove = remFromQueue();
            toRemove.getArcOrigin().remArc(toRemove);
            updateCostBound();

        }


    }

    protected Arc remFromQueue()
    {
        Arc tmp = toVisit.pop();
        tmp.inQueue = false;
        return tmp;
    }


    /**
     * Default initial propagation: full constraint re-propagation.
     * @throws ContradictionException if a domain get empty
     */
    public void awake() throws ContradictionException
    {
        initGraph();
        filter();
    }


    /**
     * Propagates the constraint awake events.
     * @throws ContradictionException if a domain gets empty
     */
    public void propagate() throws ContradictionException
    {
        clearStructures();
        initGraph();
        filter();
    }

    public int getFilteredEventMask(int idx) {
        return (idx < myVars.length ? IntVarEvent.REMVALbitvector + IntVarEvent.INSTINTbitvector : IntVarEvent.INSTINTbitvector + IntVarEvent.INCINFbitvector + IntVarEvent.DECSUPbitvector);
        // return 0x0B;
    }

    /**
     * Propagates a value removal
     * @param idx index of the variable
     * @param val removed value
     * @throws ContradictionException if a domain gets empty
     */
    public void awakeOnRem(int idx, int val) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            toVisit.clear();
            IStateVector v = getQij(idx,val);
            if (v != null)
            {
                for (int k = 0 ; k < v.size() ; k++)
                {
                    State s = (State) v.get(k);
                    int succ = delta(idx,val,s.index);
                    State next = layer[idx+1].get(succ);

                    Arc tmp = new Arc(s,val,next);
                    if (s.remArc(tmp)) {
                        k--;
                    }
                }
                emptyStack();
                
            }
        }
        else
        {
            this.constAwake(false);
        }
    }


    /**
     * propagates a modification on a variable
     * @param idx index of the modified variable
     * @throws ContradictionException if a domain gets empty
     */
    public void filter(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            if (idx  != myVars.length)
            {
                for (int j = 0 ; j < Q[idx].length ; j++)
                {
                    if (Q[idx][j] !=null && !Q[idx][j].isEmpty() && !vars[idx].canBeInstantiatedTo(j+offset[idx]))
                    {
                        awakeOnRem(idx,j+offset[idx]);
                    }
                }
            }
            else
            {
                toVisit.clear();
                awakeOnCost();
            }

        }
        else
        {
            this.constAwake(false);
        }
    }



    /**
     * propagates a variable instantiation
     * @param idx index of the instantiated variable
     * @throws ContradictionException if a domain gets empty
     */
    public void awakeOnInst(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            filter(idx);
        }
        else
        {
            this.constAwake(false);
        }
    }


    /**
     * propagates a variable sup modification
     * @param idx index of the modified variable
     * @throws ContradictionException if a domain gets empty
     */
    public void awakeOnSup(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            filter(idx);
        }
        else
        {
            this.constAwake(false);
        }
    }

    /**
     * propagate a vairable inf modification
     * @param idx index of the modified variable
     * @throws ContradictionException if a domain gets empty
     */
    public void awakeOnInf(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            filter(idx);
        }
        else
        {
            this.constAwake(false);
        }
    }

    /*
    /**
     * propagates an incoming arc removal in a State
     * @param s state from whom the incoming arc has been removed
     * @throws ContradictionException if a domain gets empty

    private void decrementIndeg(State s) throws ContradictionException
    {
        if (s.inArcs.isEmpty() && s.layer < myVars.length)
        {
            IStateVector v = s.outArcs;
            for (int k = 0 ; k < v.size();  k++)
            {
                Arc n = (Arc) v.get(k);
                State next = n.getArcDestination();
                int i = s.layer;
                int j = n.getArcLabel();
                s.remArc(n);
                IStateVector qij = getQij(i,j);
                remove(qij,s);
                if (qij.isEmpty())
                {
                    vars[i].removeVal(j,-1);
                }
                decrementIndeg(next);

            }
        }
    }


    /**
     * propagates an outgoing arc removal in a State
     * @param s state from whom the outgoing arc has been removed
     * @throws ContradictionException if a domain gets empty

    private void decrementOutdeg(State s) throws ContradictionException
    {
        if (s.outArcs.isEmpty() && s.layer > 0)
        {
            IStateVector v = s.inArcs;
            for (int k = 0 ; k < v.size();  k++)
            {
                Arc n = (Arc) v.get(k);
                State pred = n.getArcOrigin();
                int i = pred.layer;
                int j = n.getArcLabel();
                IStateVector qij = getQij(i,j);
                pred.remArc(n);
                remove(qij,pred);
                if (qij.isEmpty())
                {
                    vars[i].removeVal(j,-1);
                }
                decrementOutdeg(pred);
            }
        }
    }  */


    /**
     * <i>Semantic:</i>
     * Testing if the constraint is satisfied.
     * Note that all variables involved in the constraint must be
     * instantiated when this method is called.
     */
    public boolean isSatisfied()
    {
        int[] str = new int[myVars.length];
        int idx =0 ;

        int sum = 0;
        for (IntDomainVar myVar : myVars) {
            if (!myVar.isInstantiated())
                return false;
            str[idx] = myVar.getVal();
            sum += getCost(idx++, myVar.getVal());//-offset[idx++]];
        }

        return this.automaton.run(str) && cVar.getVal() == sum;
    }



    /**
     * Class Arc represents an Arc in the directed layered graph
     */
    public static class Arc
    {
        State from;
        int val;
        State to;
        boolean inQueue;

        public Arc(State from, int val,State to)
        {
            this.from = from;
            this.val = val;
            this.to = to;
            this.inQueue = false;
        }

        public State getArcOrigin()
        {
            return from;
        }
        public State getArcDestination()
        {
            return to;
        }
        public int getArcLabel()
        {
            return val;
        }

        public boolean equals (Object o)
        {
            if (o instanceof Arc)
            {
                Arc oa = (Arc) o;
                return from.equals(oa.from) && val == oa.val && to.equals(oa.to);
            }
            else
                return false;
        }
        public String toString()
        {
            return (this.from+" -> "+this.val+" -> "+this.to);
        }
    }



    /**
     * Class State represents Nodes in the layered graph
     */
    public static class State
    {
        /**
         * index of the layer this state belongs to
         */
        public int layer;

        /**
         * Index of the state in the layer
         */
        public int index;


        /**
         * Shortest path cost from the source to this state
         */
        public IStateInt pccS;


        /**
         * Longest path cost from the source to this state
         */
        public IStateInt pgcS;


        /**
         * Shortest path cost from this state to the sink
         */
        public IStateInt pccP;


        /**
         * Longest path cost from this state to the sink
         */
        public IStateInt pgcP;


        /**
         * Index of the previous state in the shortest path to the source
         */
        public IStateInt pccPred;


        /**
         * Index of the next state int the shortest path to the sink
         */
        public IStateInt pccSucc;


        /**
         * Index of the previous state in the longest path to the source
         */
        public IStateInt pgcPred;


        /**
         * Index of the next state in the longest path to the sink
         */
        public IStateInt pgcSucc;


        /**
         * Set of incoming arcs
         */
        public IStateVector inArcs;


        /**
         * Set of the outgoing arcs
         */
        public IStateVector outArcs;


        /**
         * Constraint using this state
         */
        CostRegular cr;


        /**
         * Constructs a new state
         * @param layer the layer index in the layered graph
         * @param index the index in the automaton
         * @param constraint the constraint it belongs to
         */
        public State(int layer, int index, CostRegular constraint)
        {
            IEnvironment env = constraint.env;
            cr = constraint;
            this.layer = layer;
            this.index = index;
            pccS = env.makeInt(Integer.MAX_VALUE);
            pgcS = env.makeInt(Integer.MIN_VALUE);
            pccP = env.makeInt(Integer.MAX_VALUE);
            pgcP = env.makeInt(Integer.MIN_VALUE);
            pccPred = env.makeInt(-1);
            pccSucc = env.makeInt(-1);
            pgcPred = env.makeInt(-1);
            pgcSucc = env.makeInt(-1);

            inArcs = env.makeVector();
            outArcs = env.makeVector();

        }


        /**
         * Updates the shortest path from the source to this state and propagate it if necessary
         * @throws ContradictionException If a domain gets empty
         */
        public void updatePccS() throws ContradictionException
        {
            appel++;
            int newCost = Integer.MAX_VALUE;
            int newPred = Integer.MIN_VALUE;
            for (int k = 0 ;k < inArcs.size() ; k++)
            {
                Arc p = (Arc) inArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();

                boolean used = p.getArcOrigin().pccS.get() != Integer.MAX_VALUE;
                int pccT = p.getArcOrigin().pccS.get() + getCost(i,j);
                if (used && pccT < newCost)
                {
                    newCost = pccT;
                    newPred = p.getArcOrigin().index;
                }



            }
            boolean updated = newCost != pccS.get();

            pccS.set(newCost);
            pccPred.set(newPred);


            if (updated)
            {
                for (int k = 0 ; k < outArcs.size() ; k++)
                {
                    Arc n = (Arc) outArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();

                    if (n.getArcDestination().pccPred.get() == index)
                    {
                        //cr.toUpdate[0].add(n.getArcDestination());
                        n.getArcDestination().updatePccS();
                    }
                    boolean deleted = newCost == Integer.MAX_VALUE || n.getArcDestination().pccP.get() == Integer.MAX_VALUE;
                    if (deleted || newCost + n.getArcDestination().pccP.get() + getCost(i,j) > cr.cVar.getSup())
                        addToQueue(n);


                }
            }
        }


        /**
         * Updates the longest path from the source to this state and propagate it
         * @throws ContradictionException if a domain gets empty
         */
        public void updatePgcS() throws ContradictionException
        {
            int newCost = Integer.MIN_VALUE;
            int newPred = Integer.MIN_VALUE;
            for (int k = 0 ;k < inArcs.size() ; k++)
            {
                Arc p = (Arc) inArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();
                boolean used = p.getArcOrigin().pgcS.get() != Integer.MIN_VALUE;
                int pgcT = p.getArcOrigin().pgcS.get() + getCost(i,j);
                if (used && pgcT > newCost)
                {
                    newCost = pgcT;
                    newPred = p.getArcOrigin().index;
                }
            }
            boolean updated = newCost != pgcS.get();
            pgcS.set(newCost);
            pgcPred.set(newPred);
            if (updated)
            {
                for (int k = 0 ; k < outArcs.size() ; k++)
                {
                    Arc n = (Arc) outArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();
                    if (n.getArcDestination().pgcPred.get() == index)
                    {
                        //cr.toUpdate[1].add(n.getArcDestination());
                        n.getArcDestination().updatePgcS();
                    }
                    boolean deleted = newCost == Integer.MIN_VALUE || n.getArcDestination().pgcP.get() == Integer.MIN_VALUE;

                    if (deleted || newCost + n.getArcDestination().pgcP.get() + getCost(i,j) < cr.cVar.getInf())
                        addToQueue(n);

                }
            }
        }


        /**
         * Update the shortest path from this state to the sink and propagate it
         * @throws ContradictionException If a domain gets empty
         */
        public void updatePccP() throws ContradictionException
        {
            int newCost = Integer.MAX_VALUE;
            int newSucc = Integer.MIN_VALUE;
            for (int k = 0 ;k < outArcs.size() ; k++)
            {
                Arc p = (Arc) outArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();

                boolean used = p.getArcDestination().pccP.get() != Integer.MAX_VALUE;
                int pccT = p.getArcDestination().pccP.get() + getCost(i,j);//[lay][p.val];
                if (used && pccT < newCost)
                {
                    newCost = pccT;
                    newSucc = p.getArcDestination().index;
                }



            }
            boolean updated = newCost != pccP.get();
            pccP.set(newCost);
            pccSucc.set(newSucc);

            if (updated)
            {
                for (int k = 0 ; k < inArcs.size() ; k++)
                {
                    Arc n = (Arc) inArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();
                    if (n.getArcOrigin().pccSucc.get() == index)
                    {

                        //cr.toUpdate[2].add(n.getArcOrigin());
                        n.getArcOrigin().updatePccP();

                    }

                    boolean deleted = newCost == Integer.MAX_VALUE || n.getArcOrigin().pccS.get() == Integer.MAX_VALUE;
                    if (deleted || newCost + getCost(i,j)+n.getArcOrigin().pccS.get() > cr.cVar.getSup())
                        addToQueue(n);

                }
            }
        }


        /**
         * Updates the longest path from this state to the sink and propagate it
         * @throws ContradictionException If a domain gets empty
         */
        public void updatePgcP() throws ContradictionException
        {
            int newCost = Integer.MIN_VALUE;
            int newSucc = Integer.MIN_VALUE;
            for (int k = 0 ;k < outArcs.size() ; k++)
            {
                Arc p = (Arc) outArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();

                boolean used = p.getArcDestination().pgcP.get() != Integer.MIN_VALUE;
                int pgcT = p.getArcDestination().pgcP.get() + getCost(i,j);//lay][p.val];
                if (used && pgcT > newCost)
                {
                    newCost = pgcT;
                    newSucc = p.getArcDestination().index;
                }



            }
            boolean updated = newCost != pgcP.get();
            pgcP.set(newCost);
            pgcSucc.set(newSucc);

            if (updated) {
                for (int k = 0 ; k < inArcs.size() ; k++)
                {
                    Arc n = (Arc) inArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();
                    if (n.getArcOrigin().pgcSucc.get() == index)
                    {
                        //cr.toUpdate[3].add(n.getArcOrigin());
                        n.getArcOrigin().updatePgcP();
                    }

                    boolean deleted = newCost == Integer.MIN_VALUE || n.getArcOrigin().pgcS.get() == Integer.MIN_VALUE;
                    if (deleted || newCost + getCost(i,j)+n.getArcOrigin().pgcS.get() < cr.cVar.getInf())
                        addToQueue(n);

                }
            }
        }


        /**
         * Removes an Arc form the layered graph and propagates this removal
         * @param a The arc to be removed
         * @throws ContradictionException if a domain gets empty
         * @return true if a state is remove from Qij
         */
        public boolean remArc(Arc a) throws ContradictionException
        {

            boolean out = false;
            int i = a.getArcOrigin().layer;
            int j = a.getArcLabel();

            if (i < cr.myVars.length)
            {
                remOutArc(a);
                a.getArcDestination().remInarc(a);
                cr.remFromQij(i,j,a.getArcOrigin());
                out = true;
            }

            if (a.getArcDestination().pccPred.get() == a.getArcOrigin().index)
                a.getArcDestination().updatePccS();
            if (a.getArcDestination().pgcPred.get() == a.getArcOrigin().index)
                a.getArcDestination().updatePgcS();
            if (a.getArcOrigin().pccSucc.get() == a.getArcDestination().index)
                a.getArcOrigin().updatePccP();
            if (a.getArcOrigin().pgcSucc.get() == a.getArcDestination().index)
                a.getArcOrigin().updatePgcP();

            cr.updateCostBound();
           /* while (!cr.toVisit.isEmpty())
            {
              Arc toV = remFromQueue();


             toV.getArcOrigin().remArc(toV);
              }                             */
            if (i < cr.myVars.length)
                if (getQij(i,j).isEmpty())
                    getVar(i).removeVal(j, this.cr, false);
            return out;
        }

        /**
         * Retrieve the set of states supporting i,j
         * @param i index of the variable
         * @param j value of the variable
         * @return the set of states supporting i,j
         */
        protected IStateVector getQij(int i, int j)
        {
            return cr.getQij(i,j);
        }


        /**
         * Retrieves the cost of transition ij
         * @param i index of the variable
         * @param j value of the vairable
         * @return the cost of transition ij
         */
        protected int getCost(int i, int j)
        {
            return cr.getCost(i,j);
        }

        protected IntDomainVar getVar(int idx)
        {
            return cr.vars[idx];
        }

        protected void addToQueue(Arc a)
        {
            if (!a.inQueue)
            {
                a.inQueue = true;
                cr.toVisit.add(a);
            }
        }

        protected Arc remFromQueue()
        {
            Arc tmp = cr.toVisit.pop();
            tmp.inQueue = false;
            return tmp;
        }



        /**
         * adds an incoming arc to this state
         * @param a the arc to add
         */
        public void addInarc(Arc a)
        {
            inArcs.add(a);
        }


        /**
         * removes an incoming arc from this state
         * @param a the arc to remove
         */
        public void remInarc(Arc a)
        {
            CostRegular.remove(inArcs,a);
        }


        /**
         * adds an outgoing arc to the state
         * @param a the arc to add
         */
        public void addOutarc(Arc a)
        {
            outArcs.add(a);
        }


        /**
         * removes an outgoing arc from this state
         * @param a Arc to remove from outgoing arcs
         */
        public void remOutArc(Arc a)
        {
            CostRegular.remove(outArcs,a);
        }




        public boolean equals(Object o)
        {
            if (o instanceof State)
            {
                State os = (State) o;
                return index == os.index;
            }
            else
                return false;
        }

        public String toString()
        {
            return ("State "+index+"{"+layer+"}"+" ["+pccS.get()+","+pgcS.get()+" ; "+pccP.get()+","+pgcP.get()+"]");
        }
    }



    /**
     * missing api on storedVector
     * find the object and swap the last object with the one to delete
     * @param sv storedVector
     * @param idx index of the object to remove
     */
    public static void remove(IStateVector sv, int idx) {
        if (sv.size() == (idx + 1)) { // if idx is the last element
            sv.removeLast();
        } else {
            Object o = sv.get(sv.size() - 1);
            sv.removeLast();
            sv.set(idx, o);
        }
    }

    /**
     * missing api on storedVector
     * find the object and swap the last object with the one to delete
     * @param sv storedVector
     * @param idx object to remove
     * @return true or false :)
     */
    public static boolean remove(IStateVector sv, Object idx) {
        int s = sv.size();
        for (int i = 0; i < s; i++) {
            if (sv.get(i).equals(idx)) {
                remove(sv, i);
                return false;
            }
        }
        return true;
    }


    public static int appel = 0;


}
