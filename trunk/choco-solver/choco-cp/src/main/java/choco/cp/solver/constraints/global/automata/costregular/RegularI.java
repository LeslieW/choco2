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

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 20, 2007
 * Time: 8:41:46 AM
 */
public class RegularI extends AbstractLargeIntSConstraint
{


    /**
     * flag to turn off incremental propagation
     */
    public final static boolean INCREMENTAL = true;


    /**
     * Automaton describing the authorized tuples
     */
    public Automaton automaton;


    /**
     * number of nodes in the automaton
     */
    private int nbNodes;


    /**
     * index of variable i in Qij array
     */
    private int[] start;


    /**
     * Offset of each variable
     */
    private int[] offset;


    /**
     * Domain size of each variable
     */
    private int[] size;


    /**
     * Representation of the Qij data structures
     */
    private DLList[] Q;


    /**
     * Outgoing arcs store :)
     */
    private DLList[] outarc;


    /**
     * Incomin arcs store :)
     */
    private DLList[] inarc;


    /**
     * Trail saving removed arcs during propagation
     */
    private DLList[][] outSave;


    /**
     * memory of the last propagated world index
     */
    private int lastPropagatedWorld;

    private final IEnvironment environment;


    /**
     * Constuct a new constraint RegularI
     * @param vars Variables that must respect the sequence allowed by the automaton
     * @param auto Automaton that describes the allowed tuples.
     * @param solver
     */
    public RegularI(IntDomainVar[] vars, Automaton auto, Solver solver)
    {
        super(vars);

        this.automaton = auto;
        this.nbNodes = automaton.size();
        this.environment = solver.getEnvironment();

        this.start = new int[vars.length];
        this.size = new int[vars.length];
        this.offset = new int[vars.length];

        start[0] = 0;
        int totalSize = 0 ;

        for (int i = 0 ; i < vars.length ; i++)
        {
            offset[i] = vars[i].getInf();
            int sz = vars[i].getSup() - vars[i].getInf() +1;
            size[i] = sz;
            if (i > 0) start[i] = size[i-1] + start[i-1];
            totalSize+=sz;
        }

        Q = new DLList[totalSize];
        outarc = new DLList[(nbNodes+1)*vars.length];
        inarc = new DLList[(nbNodes+1)*(vars.length)];
        outSave = new DLList[solver.getNbIntVars()+2][];
        outSave = new DLList[solver.getNbIntVars()+2][];

    }


    /**
     * Constuct a new constraint RegularI
     * @param vars Variables that must respect the sequence allowed by the automaton
     * @param regexp Regular expression, equivalent to a DFA, that describes the allowed tuples.
     * @param solver
     */
    public RegularI(IntDomainVar[] vars, String regexp, Solver solver)
    {
        this(vars,new Automaton(regexp), solver);
    }


    /**
     * This function is a simplified call to retrieve the index of the current world
     * @return index of the current world
     */
    private int getCurrentWorld()
    {
        return environment.getWorldIndex();
    }


    /**
     *
     * @param i indice of the variable
     * @param j transition symbol
     * @return The set of nodes, reachable in i transition, having a transition with label j,
     */
    private DLList getQij(int i, int j)
    {
        return Q[start[i]+j-offset[i]];
    }


    /**
     *
     * This function adds a state (node) to the set Qij in constant time
     *
     * @param i indice of the variable
     * @param j transition symbol
     * @param state state to add
     */
    private void addToQij(int i, int j, int state)
    {
        int idx = start[i]+j-offset[i];
        if (Q[idx] == null)
            Q[idx] =  new DLList(nbNodes);
        Q[idx].add(state);
    }


    /**
     * This function removes a state from the set Qij in constant time
     * @param i indice of the variable
     * @param j transition symbol
     * @param state state to remove

    private void remFromQij(int i, int j, int state)
    {
        int idx = start[i]+j-offset[i];
        Q[idx].remove(state);
    }     */


    /**
     * Add an arc to the outgoing arcs list
     * @param orig  origin of the arc
     * @param dest  destination of the arc
     * @param symb  label of the arc
     * @param var   indice of the variable (reachability of the origin state)
     */
    private void addToOutarc(int orig, int dest, int symb, int var)
    {
        int representation = symb*nbNodes+dest;
        int idx = var*nbNodes+orig;
        if (outarc[idx] == null)
            outarc[idx] = new DLList(size[var]*nbNodes);
        outarc[idx].add(representation);
    }


    /**
     * This function is used to retrieve the set of arcs outgoing of a given state
     * @param var indice of the variable
     * @param node number of the state
     * @return set of the outgoing arcs from the given node
     */
    private DLList getOutArc(int var, int node)
    {
        int idx = var*nbNodes+node;
        return outarc[idx];
    }


    /**
     * This function removes an arc from the outgoing arcs list
     * @param orig state from whom the arc is going
     * @param dest destination of the arc
     * @param symb label of the arc
     * @param var indice of the variable
     */
    private void remFromOutarc(int orig, int dest, int symb, int var)
    {
        int representation = symb*nbNodes+dest;
        int idx = var*nbNodes+orig;
        outarc[idx].remove(representation);
        save(idx,representation);
    }


    /**
     * Add an arc to the incoming arcs list
     * @param orig  origin of the arc
     * @param dest  destination of the arc
     * @param symb  label of the arc
     * @param var   indice of the variable (reachability of the destination state)
     */
    private void addToInarc(int orig, int dest, int symb, int var)
    {
        int representation = symb*nbNodes+orig;
        int idx = var*nbNodes+dest;
        if (inarc[idx] == null)
            inarc[idx] = new DLList(nbNodes*size[var-1]);
        inarc[idx].add(representation);
    }


    /**
     * This function is used to retrieve the set of arcs arriving to a given state
     * @param var indice of the variable
     * @param node number of the state
     * @return set of the incoming arcs at the given node
     */
    private DLList getInArc(int var, int node)
    {
        int idx = var*nbNodes+node;
        return inarc[idx];
    }


    /**
     * This function removes an arc from the incoming arcs list
     * @param orig starting state of the arc
     * @param dest destination of the arc
     * @param symb label of the arc
     * @param var indice of the variable
     */
    private void remFromInarc(int orig, int dest, int symb, int var)
    {
        int representation = symb*nbNodes+orig;
        int idx = var*nbNodes+dest;
        inarc[idx].remove(representation);
    }


    /**
     * This function add an arc to the saving trail
     * @param orig origin of the arc
     * @param dest destination of the arc
     * @param symb symbol of the arc
     * @param var  indice of the variable

    private void save(int orig, int dest, int symb, int var)
    {
        int representation = symb*nbNodes+dest;
        int idx = var*nbNodes+orig;
        save(idx, representation);
    }*/


    /**
     * This function add an arc to the saving trail, using the index and an integer to encode
     * the different needed information to describe an arc.
     * @param idx where the arc must be saved (idx = var*nbNodes+orig)
     * @param representation representation of the arc, that is (representation = symb*nbNodes+dest)
     */
    private void save(int idx, int representation)
    {
        DLList[] tmp = outSave[getCurrentWorld()];
        if (tmp == null)
        {
            outSave[getCurrentWorld()] = new DLList[(nbNodes+1)*vars.length];
        }
        DLList l = outSave[getCurrentWorld()][idx];

        if (l == null)
        {
            outSave[getCurrentWorld()][idx] = new DLList(nbNodes*size[idx/nbNodes]);
        }
        outSave[getCurrentWorld()][idx].add(representation);
    }


    /**
     * Rebuild the graph as it was at a given world, using the arc trail to resconstruct the three main data structures
     * @param world depth in the search tree
     */
    private void restoreGraph(int world)
    {
        for (int sw = lastPropagatedWorld ; sw > world ; sw--)
        {
            DLList[] rem = outSave[sw];
            for (int idx = 0 ;idx < rem.length ; idx++)
            {
                DLList l = rem[idx];
                if (l!=null)
                {
                    int orig = idx%nbNodes;
                    int var = idx / nbNodes;
                    DisposableIntIterator lit = l.getIterator();
                    while (lit.hasNext())
                    {
                        int repr = lit.next();
                        int dest = repr % nbNodes;
                        int symb = repr / nbNodes;
                        addToOutarc(orig,dest,symb,var);
                        addToInarc(orig,dest,symb,var+1);
                        addToQij(var,symb,orig);
                    }
                    lit.dispose();
                    l.clear();
                }
            }
        }

    }


    /**
     * Clears all the data structure, used for non incremental propagation.
     */
    private void resetData()
    {
        for (DLList qij : Q)
            if (qij != null) qij.clear();
        for (DLList a : outarc)
            if (a != null) a.clear();
        for (DLList a : inarc)
            if (a != null) a.clear();
    }


    /**
     * This function creates the initial layerd graph which is used for the initial filtering
     * The graph is constructed in two phases as explained in Pesant paper
     */
    private void initGraph()
    {
        int n = vars.length;
        int i,j,k;
        DisposableIntIterator varIter;
        DisposableIntIterator layerIter;
        DisposableIntIterator qijIter;

        DLList[] layer = new DLList[vars.length+1];

        for (i = 0 ; i <= n ; i++)
        {
            layer[i] = new DLList(nbNodes);
        }

        //forward pass, construct all paths described by the automaton for word of length nbVars.

        layer[0].add(automaton.getStartingState());

        for (i = 0 ; i < n ; i++)
        {
            varIter = vars[i].getDomain().getIterator();
            while(varIter.hasNext())
            {
                j = varIter.next();
                layerIter = layer[i].getIterator();
                while(layerIter.hasNext())
                {
                    k = layerIter.next();
                    int succ = automaton.delta(k,j);
                    if (succ >= 0)
                    {
                        layer[i+1].add(succ);
                        addToQij(i,j,k);
                    }
                }
            }
            varIter.dispose();
        }

        //removing reachable non accepting states

        layerIter = layer[n].getIterator();
        while (layerIter.hasNext())
        {
            k = layerIter.next();
            if (!automaton.isAccepting(k))
            {
                layerIter.remove();
            }

        }


        //backward pass, removing arcs that does not lead to an accepting state
        BitSet mark = new BitSet(nbNodes);

        for (i = n -1 ; i >=0 ; i--)
        {
            mark.clear(0,nbNodes);
            varIter = vars[i].getDomain().getIterator();
            while (varIter.hasNext())
            {
                j = varIter.next();
                DLList l = getQij(i,j);
                if (l!= null)
                {
                    qijIter = getQij(i,j).getIterator();
                    while (qijIter.hasNext())
                    {
                        k = qijIter.next();
                        int qn = automaton.delta(k,j);
                        if (layer[i+1].contains(qn))
                        {
                            addToOutarc(k,qn,j,i);
                            addToInarc(k,qn,j,i+1);
                            mark.set(k);
                        }
                        else
                            qijIter.remove();
                    }
                }
            }
            layerIter = layer[i].getIterator();

            // If no more arcs go out of a given state in the layer, then we remove the state from that layer
            while (layerIter.hasNext())
                if(!mark.get(layerIter.next()))
                    layerIter.remove();
        }
    }


    /**
     * this function performs the initial filtering from the constructed layered graph,
     * @throws ContradictionException exception if a domain gets empty
     */
    public void initialFilter() throws ContradictionException
    {
        for (int i = 0 ;i < vars.length ; i++)
        {
            DisposableIntIterator it = vars[i].getDomain().getIterator();
            int j;
            try{
                while (it.hasNext())
                {
                    j = it.next();
                    DLList l = getQij(i,j);
                    if (l == null || l.isEmpty())
                    {
                        vars[i].removeVal(j,cIndices[i]);
                    }
                }
            }finally {
                it.dispose();
            }
        }
    }


    /**
     * This recursive functions checks wether a given node at a given layer have outgoing arcs,
     * if not, it will remove all its incoming arcs and propagate those removals to the previous states.
     * @param i indice of the variable (layer number)
     * @param k number of the node in the layer
     * @throws ContradictionException expection if a domain gets empty
     */
    public void decrementOutdeg(int i, int k) throws ContradictionException
    {
        DLList lst = getOutArc(i,k);
        if (lst.size() == 0  && i > 0)
        {
            DLList inarc = getInArc(i,k);
            DisposableIntIterator it = inarc.getIterator();
            int repr;
            try{
                while (it.hasNext())
                {
                    repr = it.next();
                    int l = repr%nbNodes;
                    int j = repr / nbNodes;
                    remFromOutarc(l,k,j,i-1);
                    DLList qij = getQij(i-1,j);
                    qij.remove(l);

                    if (qij.isEmpty())
                        vars[i-1].removeVal(j,cIndices[i-1]);
                    decrementOutdeg(i-1,l);
                }
            }finally {
                it.dispose();
            }
            getInArc(i,k).clear();
        }
    }


    /**
     * This recursive functions checks wether a given node at a given layer have incoming arcs,
     * if not, it will remove all its outgoing arcs and propagate those removals to the following states.
     * @param i indice of the variable (layer number)
     * @param k number of the node in the layer
     * @throws ContradictionException expection if a domain gets empty
     */
    public void decrementIndeg(int i, int k) throws ContradictionException
    {
        DLList lst = getInArc(i,k);
        if (lst.size() == 0  && i <= vars.length)
        {
            DLList outarc = getOutArc(i,k);
            if (outarc != null)
            {
                DisposableIntIterator it = outarc.getIterator();
                int repr;
                try{
                    while (it.hasNext())
                    {
                        repr = it.next();
                        save(i*nbNodes+k,repr);

                        int l = repr % nbNodes;
                        int j = repr / nbNodes;

                        remFromInarc(k,l,j,i+1);

                        DLList qij = getQij(i,j);
                        qij.remove(k);

                        if (qij.isEmpty())
                            vars[i].removeVal(j,cIndices[i]);
                        decrementIndeg(i+1,l);
                    }
                }finally {
                    it.dispose();
                }
                getOutArc(i,k).clear();
            }
        }
    }


    /**
     * Choco initial call to the constraint
     * @throws ContradictionException expetion if a domain gets empty
     */
    public void awake() throws ContradictionException
    {
        propagate();
    }


    /**
     * This function constructs a new graph to perform GAC
     * @throws ContradictionException exception if a domain gets empty
     */
    public void propagate() throws ContradictionException
    {
        lastPropagatedWorld = getCurrentWorld();
        resetData();
        initGraph();
        initialFilter();
    }


    /**
     * Choco method for incremental propagation, called when a value is removed from a variable
     * that belongs to this constraint
     * @param idx index of the variable
     * @param val removed value
     * @throws ContradictionException exception if a domain gets empty because of that removal
     */
    public void awakeOnRem(int idx, int val) throws ContradictionException
    {
        if (INCREMENTAL)
        {

            int world = getCurrentWorld();

            if (lastPropagatedWorld > world)
            {
                restoreGraph(world);
            }

            lastPropagatedWorld = world;

            DLList qij = getQij(idx,val);
            if (qij != null)
            {
                DisposableIntIterator it = qij.getIterator();
                int k;
                try{
                    while (it.hasNext())
                    {
                        k = it.next();
                        int kn = automaton.delta(k,val);

                        remFromOutarc(k,kn,val,idx);
                        remFromInarc(k,kn,val,idx+1);

                        decrementOutdeg(idx,k);
                        decrementIndeg(idx+1,kn);
                    }
                }finally {
                    it.dispose();
                }
                qij.clear();
            }

        }

        else
            this.constAwake(false);
    }


    /**
     * Choco method called when a variable has been instantiated
     * @param idx index of the variable
     * @throws choco.kernel.solver.ContradictionException exception if a domain gets empty
     */
    public void awakeOnInst(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
            this.filter(idx);
        else
            this.constAwake(false);
    }


    /**
     * Choco method called when the upper bound of a given variable has been modified
     * @param idx index of the variable
     * @throws ContradictionException exception if a domain gets empty
     */
    public void awakeOnSup(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
            this.filter(idx);
        else
            this.constAwake(false);
    }


    /**
     * Choco method called when the lower bound of a given variable has been modified
     * @param idx index of the variable
     * @throws ContradictionException exception if a domain gets empty
     */
    public void awakeOnInf(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
            this.filter(idx);
        else
            this.constAwake(false);
    }


    /**
     * Choco method called when a given variable has been modified
     * @param idx index of the variable
     * @throws ContradictionException exception if a domain gets empty
     */
    public void filter(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
        {
            int upper = (idx+1 == vars.length)?Q.length:start[idx+1];
            for (int j = offset[idx] ; j < upper - start[idx] + offset[idx] ; j++)
            {
                if (getQij(idx,j)!= null && !getQij(idx,j).isEmpty() && !vars[idx].canBeInstantiatedTo(j))
                {
                    awakeOnRem(idx,j);
                }
            }
        }
        else
            this.constAwake(false);
    }


    /**
     * Choco method called when the bounds of a given variable have been modified
     * @param idx index of the variable
     * @throws ContradictionException exception if a domain gets empty
     */
    public void awakeOnBounds(int idx) throws ContradictionException
    {
        if (INCREMENTAL)
            this.filter(idx);
        else
            this.constAwake(false);
    }


    /**
     * <i>Semantic:</i>
     * Testing if the constraint is satisfied.
     * Note that all variables involved in the constraint must be
     * instantiated when this method is called.
     */
    public boolean isSatisfied()
    {
        int[] str = new int[vars.length];
        int idx =0 ;

        for (IntDomainVar var : vars)
        {
            if (!var.isInstantiated())
                return false;
            str[idx++] = var.getVal();
        }

        return this.automaton.run(str);
    }


    /**
     * Inner class that provides fixed size set that performs add and remove and contains operation in constant time
     * A set is implemented as a double-linked list
     */
    private static class DLList extends DisposableIntIterator
    {
        int[] succ;
        int[] pred;
        int first;
        int last;
        int nbEl;
        int current;
        public static int nbDLL = 0;

        public DLList(int size)
        {
            succ = new int[size];
            pred = new int[size];
            Arrays.fill(succ,Integer.MIN_VALUE);
            Arrays.fill(pred,Integer.MIN_VALUE);
            first  = -1;
            last = -1;
            nbEl = 0;
        }


        public DLList()
        {
            this(1);
        }


        private DLList(int[] su, int[] pr, int fi, int la, int nb, int cu)
        {
            super();
            succ = new int[su.length];
            pred = new int[pr.length];
            System.arraycopy(su,0,succ,0,su.length);
            System.arraycopy(pr,0,pred,0,pr.length);

            first = fi;
            last = la;
            nbEl = nb;
            current = cu;
        }


        public Object clone() throws CloneNotSupportedException
        {
            return new DLList(succ,pred,first,last,nbEl,current);
        }


        public int size()
        {
            return nbEl;
        }

        public void clear()
        {
            Arrays.fill(succ,Integer.MIN_VALUE);
            Arrays.fill(pred,Integer.MIN_VALUE);
            first  = -1;
            last = -1;
            nbEl = 0;
        }


        private void ensureCapacity(int size)
        {
            int [] su = new int[size*3/2+1];
            int [] pr = new int[size*3/2+1];
            Arrays.fill(su,Integer.MIN_VALUE);
            Arrays.fill(pr,Integer.MIN_VALUE);

            System.arraycopy(succ,0,su,0,succ.length);
            System.arraycopy(pred,0,pr,0,pred.length);
            succ = su;
            pred = pr;
        }

        public void add(int elem)
        {
            if (elem >= succ.length)
                ensureCapacity(elem);
            if (succ[elem] == Integer.MIN_VALUE)
            {
                if (nbEl == 0)
                {
                    first = elem;
                    last = -1;
                }
                succ[elem] = -1;
                pred[elem] = last;
                if (last != -1)
                    succ[last] = elem;
                last = elem;
                nbEl++;
            }

        }

        public boolean isEmpty()
        {
            return (nbEl ==0);
        }

        public void remove(int elem)
        {
            int tsucc = succ[elem];
            int tpred = pred[elem];
            if (tsucc != Integer.MIN_VALUE)
            {


                if (tsucc != -1)
                    pred[tsucc] = tpred;
                else
                    last = tpred;
                if (tpred != -1)
                {
                    succ[tpred] = tsucc;
                }
                else
                    first = tsucc;
                nbEl--;
            }
            succ[elem] = Integer.MIN_VALUE;
            pred[elem] = Integer.MIN_VALUE;
        }

        public boolean contains(int elem)
        {
            return (succ[elem] != Integer.MIN_VALUE);
        }

        public String toString()
        {
            StringBuffer s = new StringBuffer("{");
            int k = first;
            if (!isEmpty())
            {
                while(k != -1)
                {
                    s.append(k);
                    k = succ[k];
                    if (k != -1)
                        s.append(",");
                }
            }
            s.append("}");
            return s.toString();
        }

        public DisposableIntIterator getIterator()
        {
            current = first;
            return this;
        }

        public boolean hasNext()
        {
            return (current != -1);
        }

        public int next()
        {
            int out = current;
            current = succ[out];
            return out;
        }

        public void remove()
        {
            int toRemove;
            if (current == -1)
                toRemove = last;
            else
                toRemove = pred[current];
            this.remove(toRemove);
        }
    }


    //TODO: uncomment
//    public static void main(String[] args) {
//        Model pb = new Model();
//
//        IntDomainVar[] x = pb.makeEnumIntVarArray("x",60,0,9);
//        IntDomainVar[] y = pb.makeEnumIntVarArray("y",60,0,9);
//        for (int i = 0 ; i < x.length ; i++)
//        {
//            pb.post(pb.neq(x[i],y[i]));
//        }
//        Automaton a = new Automaton("0+1+2+3+(7|8)+(4|5|6|9)+");
//        for (int i = 0 ; i < 10 ; i++)
//            a.addToAlphabet(i);
//
//
//        RegularI constraint = new RegularI(x,a);
//        Automaton op = a.opposite();
//        Automaton min = op.minimize();
//
//        //int[] word = new int[]{3,2,2,2,2,3,7};
//
//        RegularI opposite = new RegularI(y,min);
//
//        pb.post(constraint);
//        pb.post(opposite);
//
//        pb.solve();
//
//    }


    public static class RegularIManager extends IntConstraintManager
    {

        public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
            if (parameters instanceof Automaton)
            {
                Automaton auto = (Automaton) parameters;
                IntDomainVar[] vs = solver.getVar((IntegerVariable[]) variables);
                return new RegularI(vs,auto, solver);

            }
            return null;
        }
    }
}
