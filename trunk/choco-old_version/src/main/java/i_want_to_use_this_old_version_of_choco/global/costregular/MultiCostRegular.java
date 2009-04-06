package i_want_to_use_this_old_version_of_choco.global.costregular;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.global.costregular.FA.Automaton;
import i_want_to_use_this_old_version_of_choco.global.regular.DFA;
import i_want_to_use_this_old_version_of_choco.global.regular.Transition;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.search.StaticVarOrder;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.mem.IStateVector;
import i_want_to_use_this_old_version_of_choco.mem.recomputation.EnvironmentRecomputation;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 21, 2007
 * Time: 1:38:31 PM
 */
public class MultiCostRegular extends AbstractLargeIntConstraint
{
    /**
     * Switch to turn on/off the event based propagation
     */
    public static boolean INCREMENTAL = true;


    /**
     * The memory manager
     */
    private IEnvironment env;


    /**
     * Cost of a transistion from layer i with labeled j
     */
    private int[][][] costs;


    /**
     * Automaton describing
     */
    private Automaton automaton;


    /**
     * Number of nodes in the automaton
     */
    private int nbNodes;

    /**
     * Supports
     */
    private IStateVector[][] Q;


    /**
     * Structure to speed up Q access
     */
    private int[] size;


    /**
     * offsets of the variables
     */
    public int[] offset;


    /**
     * Cost Variables
     */
    public IntDomainVar[] cVar;


    /**
     * Variables that must satisfy the regular constraint
     */
    public IntDomainVar[] myVars;


    /**
     * LayeredGraph representing the automaton
     */
    @SuppressWarnings("unchecked")
    private HashMap<Integer,MState>[] layer;


    /**
     * Stack of the Arc whose vertices must be re-checked
     */
    public Stack<MArc> toVisit;


    /**
     * Pointer to the first state in the graph
     */
    public MState source;


    /**
     * Pointer to the last state in the graph (usually a fake one)
     */
    private MState puit;


    /**
     * Number of cost variables.
     */
    private int nbCostVar;

    public static MultiCostRegular make(IntDomainVar[] vars, IntDomainVar[] costVars, Automaton auto, int[][][] costs)
    {
        int nbcvar = costVars.length;
        IntDomainVar[] allVars = new IntDomainVar[vars.length+nbcvar];
        System.arraycopy(vars,0,allVars,0,vars.length);
        System.arraycopy(costVars,0,allVars,vars.length,nbcvar);
        return new MultiCostRegular(allVars,nbcvar,auto,costs);

    }



    /**
     * Constructs a new CostRegular constraint, this constraint ensures that the sequence of variables values
     * will follow a pattern defined by a DFA and that this sequence has a cost bounded by the cost variable
     * @param vars The variables of the constraint, the last variables are the cost ones
     * @param nbCostVar the number of cost variables
     * @param auto the automaton that defined the allowed tuples
     * @param costs the cost of taking value j for the variable i in relation to cost variable q
     */
    @SuppressWarnings("unchecked")
    private MultiCostRegular(IntDomainVar[] vars,int nbCostVar, Automaton auto, int[][][] costs)
    {
        super(vars);
        this.nbCostVar = nbCostVar;
        this.automaton = auto;
        this.costs = new int[costs.length+1][][];
        System.arraycopy(costs, 0, this.costs, 0, costs.length);
        this.costs[costs.length] = new int[1][nbCostVar] ;

        this.env = vars[0].getProblem().getEnvironment();
        this.nbNodes = auto.size();

        this.cVar = new IntDomainVar[nbCostVar];
        System.arraycopy(vars,vars.length-nbCostVar,cVar,0,nbCostVar);
        myVars = new IntDomainVar[vars.length -nbCostVar];
        System.arraycopy(vars,0,myVars,0,vars.length -nbCostVar);

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

        layer = (HashMap<Integer, MState>[]) new HashMap[myVars.length+2];
        toVisit = new Stack<MArc>();
    }


    /**
     * adds a State to the support QIj
     * @param i the index of the variable
     * @param j the value of the variable
     * @param s the State to add
     */
    private void addToQij(int i, int j, MState s)
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
    public void remFromQij(int i, int j, MState s)
    {
        remove(getQij(i,j),s);
    }

    /**
     * return the set of support for (i,j)
     * @param i index of the variable
     * @param j value of the variable
     * @return an Vector of states
     */
    public IStateVector getQij(int i, int j)
    {
        //int index = i * size[i] + (j-offset[i]);
        return Q[i][j-offset[i]];
    }


    /**
     * retrieve the cost of transition (ij)
     * @param i index of the variable
     * @param j  value of the transition
     * @param q related cost variable index
     * @return cost double
     */
    public int getCost(int i, int j, int q)
    {
        return costs[i][j-offset[i]][q];
    }


    /**
     * clears the data structure for non event-based propagation
     */
    private void clearStructures()
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
    public HashMap<Integer,MState> getLayer(int i)
    {
        return layer[i];
    }


    /**
     * Creates the initial directed layered graph and removes the inconsistent arcs
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    private void initGraph() throws ContradictionException
    {

        for (int i = 0 ; i < layer.length ; i++)
        {
            layer[i] = new HashMap<Integer,MState>(nbNodes);
        }

        source = new MState(0,this.automaton.getStartingState(),this);
        for (int q = 0 ; q < nbCostVar ; q++)
        {
            source.pccS[q].set(0);
            source.pgcS[q].set(0);
        }
        layer[0].put(source.index,source);

        IntIterator varIter;
        Iterator<MState> layerIter;
        for (int i = 0 ; i < myVars.length ; i++)
        {
            varIter = myVars[i].getDomain().getIterator();
            int j;
            while (varIter.hasNext())
            {
                j = varIter.next();
                layerIter = layer[i].values().iterator();
                while (layerIter.hasNext())
                {
                    MState s = layerIter.next();
                    int k = s.index;
                    int succ= this.automaton.delta(k,j);
                    if (succ >= 0)
                    {

                        MState next = layer[i+1].get(succ);
                        if (next == null)
                        {
                            next = new MState(i+1,succ,this);
                            for (int q = 0 ; q < nbCostVar; q++)
                            {
                                next.pccS[q].set(s.pccS[q].get() + getCost(i,j,q));
                                next.pccPred[q].set(s.index);

                                next.pgcS[q].set(s.pgcS[q].get() + getCost(i,j,q));
                                next.pgcPred[q].set(s.index);
                            }

                            layer[i+1].put(succ,next);
                        }
                        else
                        {
                            for (int q = 0 ; q< nbCostVar ;q++)
                            {
                                int pccSTmp = next.pccS[q].get();
                                int pccSNew = s.pccS[q].get()+getCost(i,j,q);
                                if (pccSTmp > pccSNew)
                                {
                                    next.pccS[q].set(pccSNew);
                                    next.pccPred[q].set(s.index);
                                }

                                int pgcSTmp = next.pgcS[q].get();
                                int pgcSNew = s.pgcS[q].get() + getCost(i,j,q);

                                if (pgcSTmp < pgcSNew)
                                {
                                    next.pgcS[q].set(pgcSNew);
                                    next.pgcPred[q].set(s.index);
                                }
                            }
                        }
                        MArc tmp = new MArc(s,j,next);
                        addToQij(i,j,s);
                        next.addInarc(tmp);
                        s.addOutarc(tmp);

                    }
                }

            }

        }

        puit = new MState(myVars.length+1,Integer.MAX_VALUE,this);
        layer[myVars.length+1].put(Integer.MAX_VALUE,puit);


        HashSet<Integer> toRemoveA = new HashSet<Integer>(nbNodes);
        Iterator<Integer> it = layer[myVars.length].keySet().iterator();
        int idx;
        while (it.hasNext())
        {
            idx = it.next();
            if (!automaton.isAccepting(idx))
                toRemoveA.add(idx);
            else
            {
                MState s = layer[myVars.length].get(idx);

                for (int q = 0 ; q < nbCostVar ; q++)
                {
                    if (s.pccS[q].get() < puit.pccS[q].get())
                    {
                        puit.pccS[q].set(s.pccS[q].get());
                        puit.pccPred[q].set(s.index);

                    }
                    if (s.pgcS[q].get() > puit.pgcS[q].get())
                    {
                        puit.pgcS[q].set(s.pgcS[q].get());
                        puit.pgcPred[q].set(s.index);
                    }
                    s.pccP[q].set(0);
                    s.pgcP[q].set(0);
                    s.pccSucc[q].set(Integer.MAX_VALUE);
                    s.pgcSucc[q].set(Integer.MAX_VALUE);
                    puit.pccP[q].set(0);
                    puit.pgcP[q].set(0);
                }
                MArc tmp = new MArc(s,0,puit);
                s.addOutarc(tmp);
                puit.addInarc(tmp);




            }
        }
        for (Integer r : toRemoveA)
            layer[myVars.length].remove(r);


        BitSet mark = new BitSet(nbNodes);
        for (int i = myVars.length -1  ; i >= 0 ; i--)
        {
            mark.set(0,nbNodes);
            varIter = myVars[i].getDomain().getIterator();
            while(varIter.hasNext())
            {
                HashSet<MState> toRemove = new HashSet<MState>(nbNodes);
                int j = varIter.next();

                int sz = (getQij(i,j) == null) ? 0 : getQij(i,j).size();
                for (int m = 0 ; m < sz ; m++)
                {
                    MState s = (MState) getQij(i,j).get(m);
                    int succ = automaton.delta(s.index,j);
                    if (layer[i+1].containsKey(succ))
                    {
                        MState next = (layer[i+1].get(succ));
                        if (!isAdmissible(s,next,j))
                        {
                            toRemove.add(s);
                            MArc tmp = new MArc(s,j,next);
                            s.remOutArc(tmp);
                            next.remInarc(tmp);
                            for (int q = 0 ; q < nbCostVar ;q++)
                            {
                                if (next.pccPred[q].get() == s.index)
                                    next.updatePccS(q);
                                if (next.pgcPred[q].get() == s.index)
                                    next.updatePgcS(q);
                            }
                        }

                        else
                        {
                            for (int q = 0 ; q < nbCostVar ; q++)
                            {
                                int pccPTmp = s.pccP[q].get();
                                int pccPNew = next.pccP[q].get() + getCost(i,j,q);

                                if(pccPNew < pccPTmp)
                                {
                                    s.pccP[q].set(pccPNew);
                                    s.pccSucc[q].set(succ);
                                }

                                int pgcPTmp = s.pgcP[q].get();
                                int pgcPNew = next.pgcP[q].get() + getCost(i,j,q);

                                if (pgcPNew > pgcPTmp)
                                {
                                    s.pgcP[q].set(pgcPNew);
                                    s.pgcSucc[q].set(succ);
                                }
                            }
                            mark.clear(s.index);
                        }
                    }
                    else
                    {
                        toRemove.add(s);
                        s.remOutArc(new MArc(s,j,new MState(0,succ,this)));
                    }
                }
                for (MState s : toRemove)
                    remFromQij(i,j,s);
            }
            for (int b = mark.nextSetBit(0) ; b >= 0  ; b= mark.nextSetBit(b+1))
            {
                layer[i].remove(b);
            }
        }
    }

    public IntDomainVar getCostVar(int idx)
    {
        return cVar[idx];
    }

    public boolean isAdmissible(MState left, MState right, int val)
    {
        boolean out = true;
        int i = left.layer;

        for (int q = 0 ; q < nbCostVar && out ; q++)
        {
            out &= left.pccS[q].get()+right.pccP[q].get()+getCost(i,val,q) <= getCostVar(q).getSup();
            out &= left.pgcS[q].get()+right.pgcP[q].get()+getCost(i,val,q) >= getCostVar(q).getInf();


        }


        return out;
    }

    /*
        private void printLayer(HashMap<Integer,State> layer[])
        {
            for (int i = 0 ; i < layer.length ; i++) {
                HashMap<Integer, State> aLayer = layer[i];
                for (Integer k : aLayer.keySet()) {
                    System.out.print("LAYER : "+i+"  ");
                    System.out.println(aLayer.get(k));
                }
            }
        }
    */


    /**
     * Print all Arcs from the source to the sink
     */
    public void printAll()
    {
        printAll(source);
    }

    private void printAll(MState s) {
        @SuppressWarnings("unchecked")
        HashSet<MArc>[] map = new HashSet[vars.length+1];
        System.out.println(puit);
        System.out.println(size[0]);
        for (int i = 0 ; i < map.length ; i++)
            map[i] = new HashSet<MArc>();
        printAll(s,map);
        for (int i = 0 ; i < map.length ; i++)
        {
            System.out.println("");
            System.out.println("VAR = "+ i+"  AT WORLD : "+vars[0].getProblem().getEnvironment().getWorldIndex());
            for (Object a : map[i])
                System.out.println(a);
        }
    }

    private void printAll(MState s, HashSet<MArc>[] map)
    {
        HashSet<MState> next = new HashSet<MState>();
        for (int i = 0 ; i < s.outArcs.size() ; i++)
        {
            MArc a = (MArc) s.outArcs.get(i);
            map[s.layer].add(a);
            next.add(a.getArcDestination());
        }
        for ( MState sn : next)
            printAll(sn,map);

    }


    /**
     * initial value pruning deduced from the layered graph
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    private void filter() throws ContradictionException
    {
        for (int i = 0 ; i < myVars.length ; i++)
        {
            for (int j = 0 ; j < Q[i].length ; j++)
            {
                if(Q[i][j] == null || Q[i][j].isEmpty())
                    vars[i].removeVal(j+offset[i],cIndices[i]);
            }
        }
        updateCostsBound();
    }


    /**
     * Updates the cost variable bounds in relation to the shortest and longest path in the layered graph
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    private void updateCostsBound() throws ContradictionException
    {
        for (int q = 0 ; q < nbCostVar ; q++)
        {
            for (MState s : layer[0].values())
            {
                int pgcP = s.pgcP[q].get();
                if(pgcP < getCostVar(q).getSup())
                    getCostVar(q).updateSup(pgcP,-1/*cIndices[myVars.length+q]*/);
            }
            for (MState s : layer[myVars.length+1].values())
            {
                int pccS = s.pccS[q].get();
                if(pccS > getCostVar(q).getInf())
                    getCostVar(q).updateInf(pccS,-1/*cIndices[myVars.length+q]*/);
            }
        }

    }


    /**
     * TODO Check wether this could be done incrementaly
     * Removes arcs that became inconsistent after the cost variable has been trimmed
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    private void awakeOnCost() throws ContradictionException
    {
        for (int i = 0 ; i < myVars.length ; i++)
        {
            for (MState s : layer[i].values())
            {
                for (int q = 0 ;  q < nbCostVar ; q++)
                {

                    for (int k = 0 ; k < s.outArcs.size(); k++) //Arc n : tmp) {
                    {
                        MArc n = (MArc) s.outArcs.get(k);
                        boolean removed = false;
                        boolean delete = n.getArcOrigin().pccS[q].get() == Integer.MAX_VALUE || n.getArcDestination().pccP[q].get() == Integer.MAX_VALUE;
                        if (delete || n.getArcOrigin().pccS[q].get() + getCost(i, n.getArcLabel(),q) + n.getArcDestination().pccP[q].get() > getCostVar(q).getSup()) {
                            n.getArcOrigin().remArc(n);
                            removed = true;
                        }
                        delete = n.getArcOrigin().pgcS[q].get() == Integer.MIN_VALUE || n.getArcDestination().pgcP[q].get() == Integer.MIN_VALUE;
                        if (!removed && delete || n.getArcOrigin().pgcS[q].get() + getCost(i, n.getArcLabel(),q) + n.getArcDestination().pgcP[q].get() < getCostVar(q).getInf()) {
                            n.getArcOrigin().remArc(n);
                            removed = true;
                        }
                        if (removed && i!= myVars.length)
                            k--;

                    }
                }
            }
        }
    }


    /**
     * Default initial propagation: full constraint re-propagation.
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain get empty
     */
    public void awake() throws ContradictionException
    {
        initGraph();
        filter();
        System.out.println("bui");
    }


    /**
     * Propagates the constraint awake events.
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    public void propagate() throws ContradictionException
    {
        clearStructures();
        initGraph();
        filter();
    }


    /**
     * Propagates a value removal
     * @param idx index of the variable
     * @param val removed value
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
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
                    MState s = (MState) v.get(k);
                    int succ = automaton.delta(s.index,val);
                    MState next = layer[idx+1].get(succ);

                    MArc tmp = new MArc(s,val,next);
                    if (s.remArc(tmp))
                        k--;
                }
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
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    public void awakeOnVar(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            if (idx  < myVars.length)
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
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    public void awakeOnInst(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            if (idx == 4 && vars[idx].getVal() == 7)
                System.out.println("STOP");
            awakeOnVar(idx);
        }
        else
        {
            this.constAwake(false);
        }
    }


    /**
     * propagates a variable sup modification
     * @param idx index of the modified variable
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    public void awakeOnSup(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            awakeOnVar(idx);
        }
        else
        {
            this.constAwake(false);
        }
    }

    /**
     * propagate a vairable inf modification
     * @param idx index of the modified variable
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
     */
    public void awakeOnInf(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            awakeOnVar(idx);
        }
        else
        {
            this.constAwake(false);
        }
    }

    public void awakeOnBounds(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            awakeOnVar(idx);
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

        int[] sum = new int[nbCostVar];
        for (IntDomainVar myVar : myVars) {
            if (!myVar.isInstantiated())
                return false;
            str[idx] = myVar.getVal();
            for (int q = 0 ; q < nbCostVar ; q++)
                sum[q] += getCost(idx, myVar.getVal(),q);//-offset[idx++]];
            idx++;
        }
        boolean sumOk = true;
        for (int q = 0 ;  q < nbCostVar ; q++)
            sumOk &= getCostVar(q).getVal() == sum[q];

        return this.automaton.run(str) && sumOk;
    }



    /**
     * Class Arc represents an Arc in the directed layered graph
     */
    public class MArc
    {
        MState from;
        int val;
        MState to;
        boolean inQueue;

        public MArc(MState from, int val,MState to)
        {
            this.from = from;
            this.val = val;
            this.to = to;
            this.inQueue = false;
        }

        public MState getArcOrigin()
        {
            return from;
        }
        public MState getArcDestination()
        {
            return to;
        }
        public int getArcLabel()
        {
            return val;
        }

        public boolean equals (Object o)
        {
            if (o instanceof MArc)
            {
                MArc oa = (MArc) o;
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
    public class MState
    {
        /**
         * index of the layer this state belongs to
         */
        int layer;

        /**
         * Index of the state in the layer
         */
        int index;


        /**
         * Shortest path cost from the source to this state
         */
        public IStateInt[] pccS;


        /**
         * Longest path cost from the source to this state
         */
        public IStateInt[] pgcS;


        /**
         * Shortest path cost from this state to the sink
         */
        public IStateInt[] pccP;


        /**
         * Longest path cost from this state to the sink
         */
        public IStateInt[] pgcP;


        /**
         * Index of the previous state in the shortest path to the source
         */
        public IStateInt[] pccPred;


        /**
         * Index of the next state int the shortest path to the sink
         */
        public IStateInt[] pccSucc;


        /**
         * Index of the previous state in the longest path to the source
         */
        public IStateInt[] pgcPred;


        /**
         * Index of the next state in the longest path to the sink
         */
        public IStateInt[] pgcSucc;


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
        MultiCostRegular cr;


        /**
         * Constructs a new state
         * @param layer the layer index in the layered graph
         * @param index the index in the automaton
         * @param constraint the constraint it belongs to
         */
        public MState(int layer, int index, MultiCostRegular constraint)
        {
            IEnvironment env = constraint.getProblem().getEnvironment();
            cr = constraint;
            this.layer = layer;
            this.index = index;
            pccS = new IStateInt[nbCostVar];
            pgcS = new IStateInt[nbCostVar];
            pccP = new IStateInt[nbCostVar];
            pgcP = new IStateInt[nbCostVar];
            pccPred = new IStateInt[nbCostVar];
            pccSucc = new IStateInt[nbCostVar];
            pgcPred = new IStateInt[nbCostVar];
            pgcSucc = new IStateInt[nbCostVar];


            for (int i = 0 ; i < nbCostVar ; i++)
            {
                pccS[i] = env.makeInt(Integer.MAX_VALUE);
                pgcS[i] = env.makeInt(Integer.MIN_VALUE);
                pccP[i] = env.makeInt(Integer.MAX_VALUE);
                pgcP[i] = env.makeInt(Integer.MIN_VALUE);
                pccPred[i] = env.makeInt(-1);
                pccSucc[i] = env.makeInt(-1);
                pgcPred[i] = env.makeInt(-1);
                pgcSucc[i] = env.makeInt(-1);
            }

            inArcs = env.makeVector();
            outArcs = env.makeVector();

        }


        /**
         * Updates the shortest path from the source to this state and propagate it if necessary
         * @param q related cost variable index
         * @throws i_want_to_use_this_old_version_of_choco.ContradictionException If a domain gets empty
         */
        public void updatePccS(int q) throws ContradictionException
        {
            int newCost = Integer.MAX_VALUE;
            int newPred = Integer.MIN_VALUE;
            for (int k = 0 ;k < inArcs.size() ; k++)
            {
                MArc p = (MArc) inArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();

                boolean used = p.getArcOrigin().pccS[q].get() != Integer.MAX_VALUE;
                int pccT = p.getArcOrigin().pccS[q].get() + getCost(i,j,q);
                if (used && pccT < newCost)
                {
                    newCost = pccT;
                    newPred = p.getArcOrigin().index;
                }




            }
            boolean updated = newCost != pccS[q].get();

            pccS[q].set(newCost);
            pccPred[q].set(newPred);

            if (updated)
            {
                for (int k = 0 ; k < outArcs.size() ; k++)
                {
                    MArc n = (MArc) outArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();

                    boolean deleted = newCost == Integer.MAX_VALUE || n.getArcDestination().pccP[q].get() == Integer.MAX_VALUE;
                    if (deleted || newCost + n.getArcDestination().pccP[q].get() + getCost(i,j,q) > cr.cVar[q].getSup())
                    {
                        addToQueue(n);
                        // break;
                    }
                    if (n.getArcDestination().pccPred[q].get() == index)
                        n.getArcDestination().updatePccS(q);



                }
            }
        }


        /**
         * Updates the longest path from the source to this state and propagate it
         * @param q related cost variable index
         * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
         */
        public void updatePgcS(int q) throws ContradictionException
        {
            int newCost = Integer.MIN_VALUE;
            int newPred = Integer.MIN_VALUE;
            for (int k = 0 ;k < inArcs.size() ; k++)
            {
                MArc p = (MArc) inArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();
                boolean used = p.getArcOrigin().pgcS[q].get() != Integer.MIN_VALUE;
                int pgcT = p.getArcOrigin().pgcS[q].get() + getCost(i,j,q);
                if (used && pgcT > newCost)
                {
                    newCost = pgcT;
                    newPred = p.getArcOrigin().index;
                }
            }
            boolean updated = newCost != pgcS[q].get();

            pgcS[q].set(newCost);
            pgcPred[q].set(newPred);

            if (updated)
            {
                for (int k = 0 ; k < outArcs.size() ; k++)
                {
                    MArc n = (MArc) outArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();
                    boolean deleted = newCost == Integer.MIN_VALUE || n.getArcDestination().pgcP[q].get() == Integer.MIN_VALUE;

                    if (deleted || newCost + n.getArcDestination().pgcP[q].get() + getCost(i,j,q) < cr.getCostVar(q).getInf())
                    {
                        addToQueue(n);
                        // break;
                    }
                    if (n.getArcDestination().pgcPred[q].get() == index)
                        n.getArcDestination().updatePgcS(q);


                }
            }
        }


        /**
         * Update the shortest path from this state to the sink and propagate it
         * @param q related cost variable index
         * @throws i_want_to_use_this_old_version_of_choco.ContradictionException If a domain gets empty
         */
        public void updatePccP(int q) throws ContradictionException
        {
            int newCost = Integer.MAX_VALUE;
            int newSucc = Integer.MIN_VALUE;
            for (int k = 0 ;k < outArcs.size() ; k++)
            {
                MArc p = (MArc) outArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();

                boolean used = p.getArcDestination().pccP[q].get() != Integer.MAX_VALUE;
                int pccT = p.getArcDestination().pccP[q].get() + getCost(i,j,q);//[lay][p.val];
                if (used && pccT < newCost)
                {
                    newCost = pccT;
                    newSucc = p.getArcDestination().index;
                }



            }
            boolean updated = newCost != pccP[q].get();

            pccP[q].set(newCost);
            pccSucc[q].set(newSucc);

            if (updated)
            {
                for (int k = 0 ; k < inArcs.size() ; k++)
                {
                    MArc n = (MArc) inArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();

                    boolean deleted = newCost == Integer.MAX_VALUE || n.getArcOrigin().pccS[q].get() == Integer.MAX_VALUE;
                    if (deleted || newCost + getCost(i,j,q)+n.getArcOrigin().pccS[q].get() > cr.getCostVar(q).getSup())
                    {
                        addToQueue(n);
                        //break;
                    }
                    if (n.getArcOrigin().pccSucc[q].get() == index)
                        n.getArcOrigin().updatePccP(q);



                }
            }
        }


        /**
         * Updates the longest path from this state to the sink and propagate it
         * @param q related cost variable index
         * @throws i_want_to_use_this_old_version_of_choco.ContradictionException If a domain gets empty
         */
        public void updatePgcP(int q) throws ContradictionException
        {
            int newCost = Integer.MIN_VALUE;
            int newSucc = Integer.MIN_VALUE;
            for (int k = 0 ;k < outArcs.size() ; k++)
            {
                MArc p = (MArc) outArcs.get(k);
                int i = p.getArcOrigin().layer;
                int j = p.getArcLabel();

                boolean used = p.getArcDestination().pgcP[q].get() != Integer.MIN_VALUE;
                int pgcT = p.getArcDestination().pgcP[q].get() + getCost(i,j,q);//lay][p.val];
                if (used && pgcT > newCost)
                {
                    newCost = pgcT;
                    newSucc = p.getArcDestination().index;
                }



            }
            boolean updated = newCost != pgcP[q].get();
            pgcP[q].set(newCost);
            pgcSucc[q].set(newSucc);

            if (updated)
            {
                for (int k = 0 ; k < inArcs.size() ; k++)
                {
                    MArc n = (MArc) inArcs.get(k);
                    int i = n.getArcOrigin().layer;
                    int j = n.getArcLabel();

                    boolean deleted = newCost == Integer.MIN_VALUE || n.getArcOrigin().pgcS[q].get() == Integer.MIN_VALUE;
                    if (deleted || newCost + getCost(i,j,q)+n.getArcOrigin().pgcS[q].get() < cr.getCostVar(q).getInf())
                    {
                        addToQueue(n);
                        // break;
                    }
                    if (n.getArcOrigin().pgcSucc[q].get() == index)
                        n.getArcOrigin().updatePgcP(q);



                }
            }
        }


        /**
         * Removes an Arc form the layered graph and propagates this removal
         * @param a The arc to be removed
         * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain gets empty
         * @return true if a state is remove from Qij
         */
        public boolean remArc(MArc a) throws ContradictionException
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

            for (int q = 0 ; q < nbCostVar ; q++)
            {
                if (a.getArcDestination().pccPred[q].get() == a.getArcOrigin().index)
                    a.getArcDestination().updatePccS(q);
                if (a.getArcDestination().pgcPred[q].get() == a.getArcOrigin().index)
                    a.getArcDestination().updatePgcS(q);
                if (a.getArcOrigin().pccSucc[q].get() == a.getArcDestination().index)
                    a.getArcOrigin().updatePccP(q);
                if (a.getArcOrigin().pgcSucc[q].get() == a.getArcDestination().index)
                    a.getArcOrigin().updatePgcP(q);

            }

            cr.updateCostsBound();
            while (!cr.toVisit.isEmpty())
            {
                MArc toV = remFromQueue();
                toV.getArcOrigin().remArc(toV);
                cr.updateCostsBound();
            }
            if (i < cr.myVars.length)
                if (getQij(i,j).isEmpty())
                    getVar(i).removeVal(j,cr.cIndices[i]);
            return out;
        }

        /**
         * Retrieve the set of states supporting i,j
         * @param i index of the variable
         * @param j value of the variable
         * @return the set of states supporting i,j
         */
        private IStateVector getQij(int i, int j)
        {
            return cr.getQij(i,j);
        }


        /**
         * Retrieves the cost of transition ij
         * @param i index of the variable
         * @param j value of the vairable
         * @param q related cost variable index
         * @return the cost of transition ij
         */
        private int getCost(int i, int j, int q)
        {
            return cr.getCost(i,j,q);
        }

        private IntDomainVar getVar(int idx)
        {
            return cr.vars[idx];
        }

        private void addToQueue(MArc a)
        {
            if (!a.inQueue)
            {
                a.inQueue = true;
                cr.toVisit.add(a);
            }
        }

        private MArc remFromQueue()
        {
            MArc tmp = cr.toVisit.pop();
            tmp.inQueue = false;
            return tmp;
        }



        /**
         * adds an incoming arc to this state
         * @param a the arc to add
         */
        public void addInarc(MArc a)
        {
            inArcs.add(a);
        }


        /**
         * removes an incoming arc from this state
         * @param a the arc to remove
         */
        public void remInarc(MArc a)
        {
            CostRegular.remove(inArcs,a);
        }


        /**
         * adds an outgoing arc to the state
         * @param a the arc to add
         */
        public void addOutarc(MArc a)
        {
            outArcs.add(a);
        }


        /**
         * removes an outgoing arc from this state
         * @param a Arc to remove from outgoing arcs
         */
        public void remOutArc(MArc a)
        {
            CostRegular.remove(outArcs,a);
        }




        public boolean equals(Object o)
        {
            if (o instanceof MState)
            {
                MState os = (MState) o;
                return index == os.index;
            }
            else
                return false;
        }

        public String toString()
        {
            return "";//("State "+index+"{"+layer+"}"+" ["+pccS.get()+","+pgcS.get()+" ; "+pccP.get()+","+pgcP.get()+"]");
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


    public static Automaton makeAutomaton()
    {
        Automaton auto = new Automaton();

        for (int i = 0 ; i < 6 ; i++)
        {
            auto.addState();
        }
        auto.setStartingState(0);
        auto.setAcceptingState(5);
        auto.setAcceptingState(4);
        auto.addTransition(0,1,new int[]{1,2,3});
        auto.addTransition(0,2,new int[]{14,15,16});
        auto.addTransition(1,2,new int[]{13});
        auto.addTransition(2,2,new int[]{4,10});
        auto.addTransition(2,3,new int[]{7});
        auto.addTransition(3,4,new int[]{1,2,3});
        auto.addTransition(2,5,new int[]{8,9});
        auto.addTransition(4,5,new int[]{8,9});
        auto.addTransition(5,4,new int[]{1,2,3});



        return auto;
    }

    public static DFA makeDFA()
    {
        List<Transition> list = new ArrayList<Transition>();
        int[] tr = new int[]{0,1,1,0,2,1,0,3,1,0,14,2,0,15,2,0,16,2,1,13,2,2,4,2,2,10,2,2,7,3,3,1,4,3,2,4,3,3,4,2,8,5,2,9,5,4,8,5,4,9,5,5,1,4,5,2,4,5,3,4};
        for (int i = 0 ; i  < tr.length ; i+=3)
            list.add(new Transition(tr[i],tr[i+1],tr[i+2]));

        List<Integer> fin = new ArrayList<Integer>();
        fin.add(4);
        fin.add(5);

        return new DFA(list,fin,16);
    }


    public static Object[] gccEtRegular()
    {
        Problem pb = new Problem();
        DFA auto = makeDFA();
        IntDomainVar[] vars = pb.makeEnumIntVarArray("x",16,0,20);

        HashSet<Integer> allDomVal = new HashSet<Integer>();
        for (IntDomainVar v : vars)
        {
            IntIterator it = v.getDomain().getIterator();
            while (it.hasNext())
            {
                allDomVal.add(it.next());
            }
        }


        IntDomainVar[] cVars = pb.makeBoundIntVarArray("cost",allDomVal.size(),0,2);
        //cVars[1] = pb.makeBoundIntVar("cost[1]",2,2);
        //pb.post(pb.regular("(4|5)*",vars));
        pb.post(pb.regular(auto,vars));
        int[] low = new int[21];
        Arrays.fill(low,0);
        low[4] = 2;
        int[] up = new int[21];
        Arrays.fill(up,2);
        //pb.post(pb.boundGcc(vars,0,20,low,up));
        pb.post(pb.globalCardinality(vars,0,20,low,up));
        pb.getSolver().setVarSelector(new StaticVarOrder(vars));

        return new Object[]{pb,vars,cVars};


    }

    public static Object[] MCR() {

        IEnvironment env;
        env = new EnvironmentRecomputation(0,2);
        //env = new EnvironmentTrailing();
        Problem pb = new Problem(env);
        IntDomainVar[] vars = pb.makeEnumIntVarArray("x",16,0,20);
        HashSet<Integer> allDomVal = new HashSet<Integer>();
        for (IntDomainVar v : vars)
        {
            IntIterator it = v.getDomain().getIterator();
            while (it.hasNext())
            {
                allDomVal.add(it.next());
            }
        }


        IntDomainVar[] cVars = pb.makeBoundIntVarArray("cost",allDomVal.size(),0,2);
        //cVars[4] = pb.makeBoundIntVar("cost[1]",2,2);
        try {cVars[4].setInf(2);}
        catch (ContradictionException e) {
            System.out.println("pas de raison");}
        int[][][] costs = new int[vars.length][][];
        int[][] costs2 = new int[vars.length][];
        for (int i = 0 ; i < vars.length ; i++)
        {
            costs[i] = new int[vars[i].getDomainSize()][];
            costs2[i] = new int[vars[i].getDomainSize()];

            for (int j = 0 ; j < costs[i].length ; j++)
            {
                costs[i][j] = new int[cVars.length];
                costs2[i][j] = j;

                for (int q = 0 ; q < costs[i][j].length ; q++)
                {
                    costs[i][j][q] = (j == q)? 1 : 0;
                }
            }

        }


        Automaton auto = makeAutomaton();//new Automaton("(0|1|2|3|4|5|6|7|8|9)*");

        //auto = new Automaton("(4|5)*");
        System.out.println(auto);

        MultiCostRegular mcr = MultiCostRegular.make(vars,cVars,auto,costs);
        pb.getSolver().setVarSelector(new StaticVarOrder(vars));
        //CostRegular cr = new CostRegular(allVars,auto,costs2);
        //Regular r = (Regular) pb.regular("(1|2)3*4+5+6+7+",vars);
        pb.post(mcr);
        return new Object[]{pb,vars,cVars};

    }
    public static void main (String[]args){

        int nbTest = 2;
        String[] sol = new String[nbTest];
        int[] nbNodes = new int[nbTest];
        int[] nbSols = new int[nbTest];
        String [] intit = new String[nbTest];


        for (int i = 0 ; i < nbTest ; i++)
        {
            Problem pb;
            IntDomainVar[] vars;
            IntDomainVar[] cVars;
            Object[] tmp = null;
            if (i==1) {tmp = gccEtRegular(); intit[i] = "gcc Et Regular";}
            if (i==0) {tmp = MCR(); intit[i] = "Multi CostREgular";}
            pb = (Problem) tmp[0];
            vars = (IntDomainVar[]) tmp[1];
            cVars = (IntDomainVar[]) tmp[2];




            Boolean b = pb.solve();
            int nbSol = 0 ;
            String out = "";

            if (b) {
                do {
                    nbSol++;

                    for (Object var : vars) out += (((IntDomainVar)var).getVal() + " ");

                    out+="\n";
                    // for (IntDomainVar var : cVars) System.out.print(var.getVal() + " ");
                    // System.out.println("");
                }while (false && pb.nextSolution());
            }
            out = out.substring(0,out.length()-1);
            sol[i] = out+"   "+vars[0].getConstraint(1000000).isSatisfied();
;
            nbSols[i] = nbSol;
            nbNodes[i] = pb.getSolver().getSearchSolver().getNodeCount();

        }
        for (int i = 0 ; i < nbTest; i++)
        {
            System.out.println(intit[i]);
            System.out.println(sol[i]);
            System.out.println("NBSOL : "+nbSols[i]);
            System.out.println("NBNODES : "+nbNodes[i]);

            System.out.println("");
        }



    }

}