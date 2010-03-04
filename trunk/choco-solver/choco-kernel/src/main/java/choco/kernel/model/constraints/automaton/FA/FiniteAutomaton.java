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

package choco.kernel.model.constraints.automaton.FA;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.StringUtils;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import gnu.trove.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 31 oct. 2007
 * Time: 09:22:48
 */

public class FiniteAutomaton {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();


    protected ArrayList<TIntArrayList> representedBy;
    protected TIntHashSet acceptingStates;
    protected int startingState;
    protected int nbStates;
    protected TIntArrayList symbols;
    protected TIntHashSet alphabet;
    protected TIntIntHashMap indexs;
    protected TIntIntHashMap indexToSymbol;

    protected static TIntIntHashMap charFromIntMap = new TIntIntHashMap();
    protected static TIntIntHashMap intFromCharMap = new TIntIntHashMap();


    static
    {
        int delta = 0;
        for (int i = Character.MIN_VALUE; i < Character.MAX_VALUE-10 ;i++)
        {
            while ((char)(i+delta) == '"' || (char)(i+delta) == '{' || (char)(i+delta) == '}' || (char)(i+delta) =='<' ||
                    (char)(i+delta) =='>' || (char)(i+delta) =='[' || (char)(i+delta)==']' ||
                    (char)(i+delta) == '(' || (char)(i+delta) == ')') delta++;
            charFromIntMap.put(i,i+delta);
            intFromCharMap.put(i+delta,i);
        }

    }

    public static int getIntFromChar(int c) { return intFromCharMap.get(c);}
    public static int getCharFromInt(int i) { return charFromIntMap.get(i);}

    //protected TreeMap<Integer,Integer> symbolMap;

    public FiniteAutomaton() {

        this.symbols = new TIntArrayList(10);
        this.alphabet = new TIntHashSet(10);
        this.indexToSymbol = new TIntIntHashMap();
        this.representedBy = new ArrayList<TIntArrayList>();
        //this.symbolMap = new TreeMap<Integer,Integer>(new TokenComparator());
        this.indexs = new TIntIntHashMap(10);
        this.nbStates = 0;
        this.acceptingStates = new TIntHashSet();

    }


    private Vector<dk.brics.automaton.State> orderState(dk.brics.automaton.Automaton a)
    {
        Vector<dk.brics.automaton.State> out = new Vector<dk.brics.automaton.State>();
        Queue<dk.brics.automaton.State> toVisit = new LinkedList<dk.brics.automaton.State>();

        toVisit.add(a.getInitialState());

        while(!toVisit.isEmpty())
        {
            dk.brics.automaton.State tmp = toVisit.remove();
            out.add(tmp);
            Set<Transition> tr = tmp.getTransitions();
            for (Transition t : tr)
                if (!out.contains(t.getDest()) && !toVisit.contains(t.getDest()))
                    toVisit.add(t.getDest());
        }


        return out;
    }

    public FiniteAutomaton(dk.brics.automaton.Automaton a)
    {
        this();
        Set<dk.brics.automaton.State> dkStates = a.getStates();
        TObjectIntHashMap<dk.brics.automaton.State> ct = new TObjectIntHashMap<dk.brics.automaton.State>();
        Vector<dk.brics.automaton.State> tmp = orderState(a);

        for (dk.brics.automaton.State s : tmp)
            ct.put(s,addState());

        setStartingState(ct.get(a.getInitialState()));
        for (dk.brics.automaton.State s : dkStates) {
            int tmp1 = ct.get(s);
            if (s.isAccept())
                setAcceptingState(tmp1);
            for (dk.brics.automaton.Transition t : s.getTransitions()) {
                int tmp2 = ct.get(t.getDest());
                for (int i = t.getMin(); i <= t.getMax(); i++) {
                    addTransition(tmp1,tmp2,getIntFromChar(i));
                }
            }
        }
    }

    public void fill(dk.brics.automaton.Automaton a,TIntHashSet alpha)
    {

        int amin = min(alpha);
        int amax = max(alpha);

        Set<dk.brics.automaton.State> dkStates = a.getStates();
        TObjectIntHashMap<dk.brics.automaton.State> ct = new TObjectIntHashMap<dk.brics.automaton.State>();
        Vector<dk.brics.automaton.State> tmp = orderState(a);

        for (dk.brics.automaton.State s : tmp)
            ct.put(s,addState());
        setStartingState(ct.get(a.getInitialState()));
        for (dk.brics.automaton.State s : dkStates) {
            int tmp1 = ct.get(s);
            if (s.isAccept())
                setAcceptingState(tmp1);
            for (dk.brics.automaton.Transition t : s.getTransitions()) {
                int tmp2 = ct.get(t.getDest());
                int imin = getIntFromChar(t.getMin());
                int imax = getIntFromChar(t.getMax());
                if (imax >= amin && imin <= amax)
                    for (int i = Math.max(imin,amin); i <= Math.min(imax,amax); i++) {
                        int k = i;
                        if (alpha.contains(k))
                        {
                            addTransition(tmp1,tmp2,k);
                        }
                    }
            }
        }
    }

    private static int min(TIntHashSet alpha)
    {
        TIntIterator it ;
        int min = Integer.MAX_VALUE;
        for (it = alpha.iterator() ;it.hasNext() ;)
        {
            int tmp = it.next();
            if (tmp < min)
                min = tmp;
        }
        return min;
    }
    public static int max(TIntHashSet alpha)
    {
        TIntIterator it ;
        int max = Integer.MIN_VALUE;
        for (it = alpha.iterator() ;it.hasNext() ;)
        {
            int tmp = it.next();
            if (tmp > max)
                max = tmp;
        }
        return max;
    }


    public FiniteAutomaton(String regexp) {
        this(new RegExp(StringUtils.toCharExp(regexp)).toAutomaton());


    }
    public FiniteAutomaton(FiniteAutomaton auto)
    {
        this.symbols = new TIntArrayList();
        this.symbols.ensureCapacity(auto.symbols.size());
        for (int i = 0 ; i < auto.symbols.size(); i++)
            this.symbols.add(auto.symbols.get(i));
        this.alphabet = new TIntHashSet(auto.alphabet.size());
        this.alphabet.addAll(auto.alphabet.toArray());
        this.representedBy = new ArrayList<TIntArrayList>();
        this.representedBy.ensureCapacity(auto.representedBy.size());
        for (int i = 0 ; i < auto.representedBy.size() ;i++)
        {
            int[] old = auto.representedBy.get(i).toNativeArray();
            TIntArrayList nt = new TIntArrayList(old);
            this.representedBy.add(nt);
        }
        this.indexs = (TIntIntHashMap) auto.indexs.clone();

        this.nbStates = auto.nbStates;
        this.acceptingStates = new TIntHashSet(auto.acceptingStates.size());
        this.acceptingStates.addAll(auto.acceptingStates.toArray());
        this.setStartingState(auto.getStartingState());
        this.indexToSymbol = (TIntIntHashMap) auto.indexToSymbol.clone();
    }



    public int size() {
        return nbStates;
    }

    public int getNbSymbols() {
        return symbols.size();
    }



    public int addState() {
        nbStates++;
        int[] tmp = new int[symbols.size()];
        Arrays.fill(tmp,-1);
        TIntArrayList cont = new TIntArrayList(tmp);
        representedBy.add(cont);
        return nbStates -1;

    }
    public void remState(int s) {
        representedBy.remove(s);
        acceptingStates.remove(s);
        nbStates--;
        for (TIntArrayList aRepresentedBy : representedBy) {
            for (int j = 0; j < aRepresentedBy.size(); j++) {
                int k = aRepresentedBy.get(j);
                if (k == s)
                    aRepresentedBy.set(j,-1);
                else if (k > s) {
                    aRepresentedBy.set(j,k - 1);
                }
            }
        }
        int start = getStartingState();
        if (start > s)
            setStartingState(start-1);
        int[] aSt = acceptingStates.toArray();
        for (int k : aSt) {
            if (k > s) {
                acceptingStates.remove(k);
                acceptingStates.add(k-1);
            }
        }
    }

    private int addSymbolToAutomaton(int symbol) {
        symbols.add(symbol);
        alphabet.add(symbol);

        indexs.put(symbol,symbols.size()-1);
        indexToSymbol.put(symbols.size()-1,symbol);

        for (int i = 0 ; i < representedBy.size() ; i++) {
            representedBy.get(i).add(-1);
        }
        return symbols.size() -1;
    }

    protected void removeSymbolFromAutomaton(int symbol)
    {
        removeFromAlphabet(symbol);
        int idx = symbols.indexOf(symbol);
        symbols.remove(idx);

        indexs.clear();
        indexToSymbol.clear();
        for (int i = 0 ;i < symbols.size(); i++)
        {
            int symb = symbols.get(i);
            indexs.put(symb,i);
            indexToSymbol.put(i,symb);
        }

      //  indexs.remove(symbol);
       // indexToSymbol.remove(idx);

        for (int i = 0 ; i < representedBy.size() ; i++) {
            representedBy.get(i).remove(idx);
        }
          

    }

    public void addTransition(int source, int destination, int... symbols)
    {
        for (int i : symbols)
            addTransition(source,destination,i);
    }

    public void addTransition(int source, int destination, int symbol) {

        int idx;
        if (!indexs.containsKey(symbol))
            idx = addSymbolToAutomaton(symbol);
        else
            idx = indexs.get(symbol);

        if (source < representedBy.size() && destination < representedBy.size()) {
            representedBy.get(source).set(idx,destination);
        }
        else {
            LOGGER.severe("state does not exist... not adding transition");
        }
    }





    public void deleteTransition(int source, int destination, int symbol) {
        int idx = indexs.get(symbol);//symbolMap.get(symbol);

        if (idx != -1 && source < representedBy.size() && destination < representedBy.size()) {
            representedBy.get(source).set(idx,-1);
        }
        else {
            LOGGER.severe("Symbol or state does not exist");
        }
    }

    public int delta(int source, int symbol) {
        if (!alphabet.contains(symbol))
            return -1;
        int idx = indexs.get(symbol);//symbolMap.get(symbol);
        if (idx == -1 ||source >= nbStates) {
            return -1;
        }
        else {
            return representedBy.get(source).get(idx);
        }
    }

    public TIntArrayList getOutSymbols(int source)
    {
        TIntArrayList out  = new TIntArrayList();
        if (source >=0 && source < nbStates)
        {
            TIntArrayList list = representedBy.get(source);
            for (int i = 0 ; i < list.size() ; i++)
            {
                if (list.get(i) >= 0)
                    out.add(indexToSymbol.get(i));
            }

        }
        return out;
    }


    public FiniteAutomaton opposite()
    {

        FiniteAutomaton out = new FiniteAutomaton(this);
        int fs = out.addState();
        for (int q = 0 ; q < out.representedBy.size()  ; q++)
        {
            if (isAccepting(q))
                out.setNonAcceptingState(q);
            else
                out.setAcceptingState(q);
            for (TIntIterator it = alphabet.iterator() ; it.hasNext() ;)
            {
                int a = it.next();
                if (delta(q,a) == -1)
                    out.addTransition(q,fs,a);
            }
        }
        return out;
    }


    public void addToAlphabet(int a)
    {
        alphabet.add(a);
    }
    public void removeFromAlphabet(int a)
    {
        alphabet.remove(a);
    }


    public int getStartingState() {
        return startingState;
    }

    public boolean isAccepting(int state)
    {
        return acceptingStates.contains(state);
    }


    public void setStartingState(int state)
    {
        startingState = state;
    }

    public void setAcceptingState(int state)
    {
        acceptingStates.add(state);
    }
    public void setAcceptingState(int... states)
    {
        acceptingStates.addAll(states);
    }

    public void setNonAcceptingState(int state)
    {
        acceptingStates.remove(state);
    }
    public void setNonAcceptingState(int... states)
    {
        acceptingStates.removeAll(states);
    }


    public boolean run(int[] word) {
        int start = this.getStartingState();

        return (run(start,word,0));

    }

    private boolean run(int state, int[] word,int pos) {
        if (word.length == pos+1) {
            int last = delta(state,word[pos]);
            return (last >= 0 && this.isAccepting(last));

        }
        else {
            int first = word[pos];
            int next = delta(state,first);
            if (next >= 0) {
                return (run(next,word,pos+1));
            }
            else
                return false;
        }
    }


    public Automaton makeBricsAutomaton()
    {
        Automaton auto = new Automaton();
        int nb = getNbStates();
        State[] states = new State[nb];
        for (int i = 0 ; i < nb; i++)
        {
            states[i] = new State();
            if (isAccepting(i))
                states[i].setAccept(true);
        }
        for (int i = 0 ; i < nb ; i++)
        {
            State orig = states[i];
            TIntArrayList out = this.getOutSymbols(i);
            for (int k = 0 ; k < out.size(); k++)
            {
                int symbol = out.get(k);
                char equi = (char) getCharFromInt(symbol);
                State next = states[delta(i,symbol)];
                orig.addTransition(new Transition(equi,next));
            }
        }
        auto.setInitialState(states[this.getStartingState()]);
        return auto;


    }

    private void clearStructures()
    {
        this.alphabet.clear();
        this.indexs.clear();
        this.indexToSymbol.clear();
        this.acceptingStates.clear();
        this.nbStates = 0;
        this.startingState = -1;
        this.representedBy.clear();
        this.symbols.reset();
    }

    public void minimize()
    {
        Automaton tmp = makeBricsAutomaton();
        tmp.minimize();
        TIntHashSet alpha = (TIntHashSet)this.alphabet.clone();
        clearStructures();
        this.fill(tmp,alpha);
    }

    public void reduce()
    {
        Automaton tmp = makeBricsAutomaton();
        tmp.reduce();
        TIntHashSet alpha = (TIntHashSet)this.alphabet.clone();
        clearStructures();
        this.fill(tmp,alpha);
    }

    public void removeDeadTransitions()
    {
        Automaton tmp = makeBricsAutomaton();
        tmp.removeDeadTransitions();
        TIntHashSet alpha = (TIntHashSet)this.alphabet.clone();
        clearStructures();
        this.fill(tmp,alpha);
    }


    public FiniteAutomaton union(FiniteAutomaton other)
    {
        Automaton fa = this.makeBricsAutomaton();
        Automaton fb = other.makeBricsAutomaton();
        TIntHashSet alpha = (TIntHashSet) this.alphabet.clone();
        TIntHashSet alphab = (TIntHashSet) other.alphabet.clone();
        alpha.addAll(alphab.toArray());

        Automaton tmp = fa.union(fb);
        FiniteAutomaton out = new FiniteAutomaton();
        out.fill(tmp,alpha);


        return out;
    }

    public FiniteAutomaton intersection(FiniteAutomaton other)
    {
        Automaton fa = this.makeBricsAutomaton();
        Automaton fb = other.makeBricsAutomaton();
        TIntHashSet alpha = (TIntHashSet) this.alphabet.clone();
        TIntHashSet alphab = (TIntHashSet) other.alphabet.clone();
        alpha.addAll(alphab.toArray());
        Automaton tmp = fa.intersection(fb);
        FiniteAutomaton out = new FiniteAutomaton();
        out.fill(tmp,alpha);

        return out;
    }
    public FiniteAutomaton complement()
    {
        FiniteAutomaton fa = new FiniteAutomaton();
        fa.fill(this.makeBricsAutomaton().complement(),this.alphabet);
        return fa;
    }

    public FiniteAutomaton concatenate(FiniteAutomaton other)
    {
        Automaton fa = this.makeBricsAutomaton();
        Automaton fb = other.makeBricsAutomaton();
        TIntHashSet alpha = (TIntHashSet) this.alphabet.clone();
        TIntHashSet alphab = (TIntHashSet) other.alphabet.clone();
        alpha.addAll(alphab.toArray());

        Automaton tmp = fa.concatenate(fb);
        FiniteAutomaton out = new FiniteAutomaton();
        out.fill(tmp,alpha);
        return out;

    }








    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("          ");
        for(int m = 0 ; m < symbols.size() ; m++) {
            int token = symbols.get(m);
            sb.append(token);
            for (int i = 0 ; i < 9 - (""+token).length() ; i++) {
                sb.append(" ");
            }
        }
        sb.append("\n");
        for(int state = 0 ; state < representedBy.size() ; state++){
            int tmpL = 0 ;
            if(getStartingState() == state) {
                sb.append("->");
                tmpL+=2;
            }
            if(isAccepting(state)) {
                sb.append("*");
                tmpL+=1;
            }
            sb.append("q").append(state);
            tmpL+= Integer.toString(state).length();
            for(int m = 0 ; m < symbols.size() ; m++) {
                int token = symbols.get(m);
                int next = delta(state, token);
                for (int i = 0 ; i < 9 - tmpL  ; i++)
                    sb.append(" ");
                String symb = (next == -1 ? " @ " : "[q"+next+"]");
                tmpL = symb.length();
                sb.append(symb);
            }
            sb.append("\n");
        }

        return sb.toString();

    }



    public int getNbStates()
    {
        return this.nbStates;
    }

    private TIntObjectHashMap<TIntObjectHashMap<TIntHashSet>> makeMap()
    {
        TIntObjectHashMap<TIntObjectHashMap<TIntHashSet>> out = new TIntObjectHashMap<TIntObjectHashMap<TIntHashSet>>();

        for (int i = 0 ; i < nbStates ; i++)
        {
            for(int m = 0 ; m < symbols.size() ; m++) {
                int s = symbols.get(m);
                int tmp = delta(i,s);
                if (tmp >= 0)
                {
                    if (out.get(i) == null)
                        out.put(i,new TIntObjectHashMap<TIntHashSet>());
                    if( out.get(i).get(tmp) == null)
                        out.get(i).put(tmp,new TIntHashSet());
                    out.get(i).get(tmp).add(s);
                }
            }
        }


        return out;
    }

    public void toDotty(String f)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(f)));
            bw.write("digraph finite_state_machine {");
            bw.newLine();
            bw.write("rankdir=LR;");
            bw.newLine();
            bw.write("node [shape = circle];");
            bw.newLine();

            bw.write("init [shape = plaintext,label=\"\"];");
            bw.newLine();
            bw.write("init -> "+getStartingState());
            bw.newLine();
            for (int i = 0 ; i < nbStates; i++)
            {
                if (isAccepting(i))
                {
                    bw.write(i+" [shape = doublecircle];");
                    bw.newLine();
                }

            }

            TIntObjectHashMap<TIntObjectHashMap<TIntHashSet>> map = makeMap();

            for (TIntObjectIterator it  = map.iterator(); it.hasNext();)
            {
                it.advance();
                int i = it.key();// map.keySet())

                TIntObjectHashMap<TIntHashSet> ot = map.get(i);
                for (TIntObjectIterator it2  = ot.iterator(); it2.hasNext();)
                {
                    it2.advance();
                    int j = it2.key();
                    TIntHashSet smb = ot.get(j);
                    StringBuilder s = new StringBuilder();
                    for (TIntIterator it3 = smb.iterator() ; it3.hasNext();)
                        s.append(it3.next()).append(",");
                    bw.write("   "+i+" -> "+j+"  [ label = \"{"+s.substring(0,s.length()-1)+"}\" ];");
                    bw.newLine();



                }

            }

            bw.write("}");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}