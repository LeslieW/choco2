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
import choco.kernel.common.util.UtilAlgo;
import dk.brics.automaton.RegExp;
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

public class Automaton {

    protected final static Logger LOGGER = ChocoLogging.getModelLogger();


    protected ArrayList<int[]> representedBy;
    protected TIntHashSet acceptingStates;
    protected int startingState;
    protected int nbStates;
    protected TIntArrayList symbols;
    protected TIntHashSet alphabet;
    protected TIntArrayList indexs;

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

    public Automaton() {

        this.symbols = new TIntArrayList(10);
        this.alphabet = new TIntHashSet(10);
        this.representedBy = new ArrayList<int[]>();
        //this.symbolMap = new TreeMap<Integer,Integer>(new TokenComparator());
        this.indexs = new TIntArrayList(10);
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

    public Automaton(dk.brics.automaton.Automaton a)
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
                int imin = t.getMin();
                int imax = t.getMax();
                if (imax >= amin && imin <= amax)
                for (int i = Math.max(imin,amin); i <= Math.min(imax,amax); i++) {
                    int k = getIntFromChar(i);
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


    public Automaton(String regexp) {
        this(new RegExp(UtilAlgo.toCharExp(regexp)).toAutomaton());


    }
    public Automaton(Automaton auto)
    {
        this.symbols = new TIntArrayList();
        this.symbols.ensureCapacity(auto.symbols.size());
        for (int i = 0 ; i < auto.symbols.size(); i++)
            this.symbols.add(auto.symbols.get(i));
        this.alphabet = new TIntHashSet(auto.alphabet.size());
        this.alphabet.addAll(alphabet.toArray());
        this.representedBy = new ArrayList<int[]>();
        this.representedBy.ensureCapacity(auto.representedBy.size());
        for (int i = 0 ; i < auto.representedBy.size() ;i++)
        {
            int[] old = auto.representedBy.get(i);
            int[] nt = new int[old.length];
            System.arraycopy(old,0,nt,0,old.length);
            this.representedBy.add(nt);
        }
        this.indexs = new TIntArrayList();
        this.indexs.ensureCapacity(auto.indexs.size());
        for (int i = 0 ; i < auto.indexs.size() ; i++)
            this.indexs.add(auto.indexs.get(i));
        this.nbStates = auto.nbStates;
        this.acceptingStates = new TIntHashSet(auto.acceptingStates.size());
        this.acceptingStates.addAll(auto.acceptingStates.toArray());
        this.setStartingState(auto.getStartingState());
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
        representedBy.add(tmp);
        return nbStates -1;

    }
    public void remState(int s) {
        representedBy.remove(s);
        acceptingStates.remove(s);
        nbStates--;
        for (int[] aRepresentedBy : representedBy) {
            for (int j = 0; j < aRepresentedBy.length; j++) {
                int k = aRepresentedBy[j];
                if (k == s)
                    aRepresentedBy[j] = -1;
                else if (k > s) {
                    aRepresentedBy[j] = k - 1;
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
        if (symbol >= indexs.size())
            indexs.fill(indexs.size(),symbol+1,-1);
        indexs.set(symbol,symbols.size()-1);

        for (int i = 0 ; i < representedBy.size() ; i++) {
            int [] tmp = representedBy.get(i);
            int[] newTab = new int[symbols.size()];
            System.arraycopy(tmp,0,newTab,0,tmp.length);
            newTab[symbols.size()-1] = -1;
            representedBy.set(i,newTab);
        }
        return symbols.size() -1;
    }

    public void addTransition(int source, int destination, int[] symbols)
    {
        for (int i : symbols)
            addTransition(source,destination,i);
    }

    public void addTransition(int source, int destination, int symbol) {

        int idx;
        if (symbol >= indexs.size())
            idx = -1;
        else
            idx = indexs.get(symbol);

        if (idx == -1) {
            idx = addSymbolToAutomaton(symbol);
        }

        if (source < representedBy.size() && destination < representedBy.size()) {
            representedBy.get(source)[idx] = destination;
        }
        else {
            LOGGER.severe("state does not exist... not adding transition");
        }
    }

    public void deleteTransition(int source, int destination, int symbol) {
        int idx = indexs.get(symbol);//symbolMap.get(symbol);

        if (idx != -1 && source < representedBy.size() && destination < representedBy.size()) {
            representedBy.get(source)[idx] = -1;
        }
        else {
            LOGGER.severe("Symbol or state does not exist");
        }
    }

    public int delta(int source, int symbol) {
        if (symbol >= indexs.size())
            return -1;
        int idx = indexs.get(symbol);//symbolMap.get(symbol);
        if (idx == -1 ||source >= nbStates) {
            return -1;
        }
        else {
            return representedBy.get(source)[idx];
        }
    }



    public Automaton opposite()
    {

        Automaton out = new Automaton(this);
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
    public void setNonAcceptingState(int state)
    {
        acceptingStates.remove(state);
    }


    public boolean run(int[] word) {
        int start = this.getStartingState();

        return (run(start,word));

    }

    private boolean run(int state, int[] word) {
        if (word.length == 1) {
            int last = delta(state,word[0]);
            return (last >= 0 && this.isAccepting(last));

        }
        else {
            int first = word[0];
            int next = delta(state,first);
            if (next >= 0) {
                int[] queue = new int[word.length -1];
                System.arraycopy(word,1,queue,0,queue.length);
                return (run(next,queue));
            }
            else
                return false;
        }
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
                    String s = "";
                    for (TIntIterator it3 = smb.iterator() ; it3.hasNext();)
                        s+=it3.next()+",";
                    s = s.substring(0,s.length()-1);
                    bw.write("   "+i+" -> "+j+"  [ label = \"{"+s+"}\" ];");
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