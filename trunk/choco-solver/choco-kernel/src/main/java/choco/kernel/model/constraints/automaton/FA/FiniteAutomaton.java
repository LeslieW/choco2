package choco.kernel.model.constraints.automaton.FA;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.StringUtils;
import dk.brics.automaton.*;
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
 * Date: Mar 15, 2010
 * Time: 12:53:23 PM
 */
public class FiniteAutomaton {


    protected static TIntIntHashMap charFromIntMap = new TIntIntHashMap();
    protected static TIntIntHashMap intFromCharMap = new TIntIntHashMap();

    public static Logger LOGGER = ChocoLogging.getEngineLogger();



    static
    {
        int delta = 0;
        for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE ;i++)
        {
            while ((char)(i+delta) == '"' || (char)(i+delta) == '{' || (char)(i+delta) == '}' || (char)(i+delta) =='<' ||
                    (char)(i+delta) =='>' || (char)(i+delta) =='[' || (char)(i+delta)==']' ||
                    (char)(i+delta) == '(' || (char)(i+delta) == ')') delta++;
            charFromIntMap.put(i,i+delta);
            intFromCharMap.put(i+delta,i);
        }

    }

    public static int getIntFromChar(char c) { return intFromCharMap.get(c);}
    public static char getCharFromInt(int i) { return (char) charFromIntMap.get(i);}



    private Automaton representedBy;

    TObjectIntHashMap<State> stateToIndex;
    ArrayList<State>  states;
    TIntHashSet alphabet;
    int nbStates = 0;



    public FiniteAutomaton()
    {
        this.representedBy = new Automaton();
        this.stateToIndex = new TObjectIntHashMap<State>();
        this.states = new ArrayList<State>();

        this.alphabet = new TIntHashSet();

    }

    public FiniteAutomaton(String regexp)
    {
        this();
        String correct = StringUtils.toCharExp(regexp);
        this.representedBy = new RegExp(correct).toAutomaton();
        for (State s : representedBy.getStates())
        {
            for (Transition t : s.getTransitions())
            {
                for (char c = t.getMin() ; c <= t.getMax() ; c++)
                {
                    alphabet.add(getIntFromChar(c));
                }
            }
        }
        syncStates();
    }

    public FiniteAutomaton(FiniteAutomaton other)
    {
        this(other.representedBy,other.alphabet);
    }

    private FiniteAutomaton(Automaton a,TIntHashSet alphabet)
    {
        this();
        fill(a,alphabet);

    }


    public static int max(TIntHashSet hs)
    {
        int max = Integer.MIN_VALUE;
        for (TIntIterator it = hs.iterator(); it.hasNext();)
        {
            int n = it.next();
            if (n > max)
                max = n;
        }
        return max;
    }
    private static int min(TIntHashSet hs)
    {
        int min = Integer.MAX_VALUE;
        for (TIntIterator it = hs.iterator(); it.hasNext();)
        {
            int n = it.next();
            if (n < min)
                min = n;
        }
        return min;
    }

    public void fill(Automaton a, TIntHashSet alphabet) {

        int max = max(alphabet);
        int min = min(alphabet);


        this.setDeterministic(a.isDeterministic());

        HashMap<State, State> m = new HashMap<State, State>();
        Set<State> states = a.getStates();
        for (State s : states)
        {
            this.addState();
            State ns = this.states.get(this.states.size()-1);
            m.put(s,ns);

        }
        for (State s : states) {
            State p = m.get(s);
            int source = stateToIndex.get(p);
            p.setAccept(s.isAccept());
            if (a.getInitialState().equals(s))
                representedBy.setInitialState(p);
            for (Transition t : s.getTransitions())
            {
                int tmin = getIntFromChar(t.getMin());
                int tmax = getIntFromChar(t.getMax());
                State dest = m.get(t.getDest());
                int desti = stateToIndex.get(dest);
                int minmax = Math.min(max,tmax);
                for (int i = Math.max(min,tmin) ; i <= minmax ; i++)
                {
                    if (alphabet.contains(i))
                        this.addTransition(source,desti,i);
                }
            }

        }
    }

    public int size() {
        return nbStates;
    }

    public int getNbSymbols() {
        return alphabet.size();
    }

    public int addState()
    {
        int idx = states.size();
        State s = new State();
        states.add(s);
        stateToIndex.put(s,idx);
        nbStates++;
        return idx;
    }


    public void removeSymbolFromAutomaton(int symbol) {
        char c = getCharFromInt(symbol);
        ArrayList<Triple> triples = new ArrayList<Triple>();
        for (int i  = 0 ; i < states.size() ; i++)
        {
            State s = states.get(i);
            for (Transition t : s.getTransitions())
            {

                if (t.getMin() <= c && t.getMax() >= c )
                {
                    triples.add(new Triple(i,stateToIndex.get(t.getDest()),symbol));
                }
            }
            for (Triple t : triples)
            {
                this.deleteTransition(t.a,t.b,t.c);
            }
            triples.clear();
        }
        alphabet.remove(symbol);
        //this.representedBy.reduce();
        // this.syncStates();
    }

    public void addTransition(int source, int destination, int... symbols) {
        for (int symbol : symbols)
        {
            addTransition(source,destination,symbol);
        }
    }

    public void addTransition(int source, int destination, int symbol) {
        try {
            checkState(source,destination);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to addTransition : "+e);
        }
        alphabet.add(symbol);
        State s = states.get(source);
        State d = states.get(destination);
        s.addTransition(new Transition(getCharFromInt(symbol),d));
    }

    public void deleteTransition(int source, int destination, int symbol) {
        try {
            checkState(source,destination);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to delete transition : "+e);
        }
        State s = states.get(source);
        State d = states.get(destination);
        Set<Transition> transitions = s.getTransitions();
        Set<Transition> nTrans = new HashSet<Transition>();
        char c = getCharFromInt(symbol);
        Iterator<Transition> it = transitions.iterator();
        for (;it.hasNext();)
        {
            Transition t = it.next();
            if (t.getDest().equals(d) && t.getMin() <= c && t.getMax() >= c)
            {
                it.remove();

                if (t.getMin() ==c && c  <t.getMax())
                {
                    nTrans.add(new Transition((char)(c+1),t.getMax(),d));
                }
                else if (t.getMin() > c && c == t.getMax())
                {
                    nTrans.add(new Transition(t.getMin(),(char)(c-1),d));
                }
                else if (t.getMin() < c && c < t.getMax())
                {
                    nTrans.add(new Transition(t.getMin(),(char)(c-1),d));
                    nTrans.add(new Transition((char)(c+1),t.getMax(),d));
                }
            }
        }
        transitions.addAll(nTrans);
    }

    private void checkState(int... state) throws StateNotInAutomatonException {
        int sz = states.size();
        for (int s : state)
            if (s >= sz)
            {
                throw new StateNotInAutomatonException(s);
            }
    }

    public int delta(int source, int symbol) throws NonDeterministicOperationException{
        if (!representedBy.isDeterministic()){
            throw new NonDeterministicOperationException();
        }
        try {
            checkState(source);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to perform delta lookup, state not in automaton : "+e);
        }        State s = states.get(source);
        State d = s.step(getCharFromInt(symbol));
        if (d != null)
        {
            return stateToIndex.get(d);
        }
        else
            return -1;

    }

    public void delta(int source, int symbol, TIntHashSet states) {
        try {
            checkState(source);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable perform delta lookup, state not in automaton : "+e);
        }        State s = this.states.get(source);
        HashSet<State> nexts = new HashSet<State>();
        s.step(getCharFromInt(symbol),nexts);
        for (State to : nexts)
        {
            states.add(stateToIndex.get(to));
        }
    }


    public TIntArrayList getOutSymbols(int source) {
        try {
            checkState(source);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to get outgoing transition, state not in automaton : "+e);
        }        TIntHashSet set = new TIntHashSet();
        State s = states.get(source);
        for (Transition t : s.getTransitions())
        {
            for (char c =  t.getMin() ; c <= t.getMax() ; c++)
            {
                set.add(getIntFromChar(c));
            }
        }
        return new TIntArrayList(set.toArray());

    }

    public void addToAlphabet(int a) {
        alphabet.add(a);
    }

    public void removeFromAlphabet(int a) {
        alphabet.remove(a);
    }

    public int getInitialState() {
        State s = representedBy.getInitialState();
        if (s == null)
            return -1;
        else
            return stateToIndex.get(s);
    }

    public boolean isFinal(int state) {
        try {
            checkState(state);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to check if this state is final : "+e);
        }        return states.get(state).isAccept();
    }

    public void setInitialState(int state) {
        try {
            checkState(state);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to set initial state, state is not in automaton : "+e);
        }
        representedBy.setInitialState(states.get(state));
    }

    public void setFinal(int state)  {
        try {
            checkState(state);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to set final state, state is not in automaton : "+e);
        }
        states.get(state).setAccept(true);
    }

    public void setFinal(int... states)  {
        for (int s : states) setFinal(s);
    }

    public void setNonFinal(int state)  {
        try {
            checkState(state);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to set non final state, state is not in automaton : "+e);
        }
        states.get(state).setAccept(false);
    }

    public void setNonFInal(int... states) {
        for (int s : states) setNonFinal(s);
    }

    public boolean run(int[] word) {
        StringBuffer b = new StringBuffer();
        for (int i : word)
        {
            char c = getCharFromInt(i);
            b.append(c);
        }
        return representedBy.run(b.toString());
    }

    public Automaton makeBricsAutomaton() {
        return representedBy.clone();
    }

    public FiniteAutomaton repeat() {
        return new FiniteAutomaton(this.representedBy.repeat(),alphabet);
    }

    public FiniteAutomaton repeat(int min) {
        return new FiniteAutomaton(this.representedBy.repeat(min),alphabet);
    }

    public FiniteAutomaton repeat(int min, int max) {
        return new FiniteAutomaton(this.representedBy.repeat(min,max),alphabet);
    }

    public void minimize() {
        this.representedBy.minimize();
        syncStates();
    }

    private void syncStates() {
        this.alphabet.clear();
        this.states.clear();
        this.stateToIndex.clear();
        int idx = 0;
        for (State s : representedBy.getStates())
        {
            states.add(s);
            stateToIndex.put(s,idx++);
            for (Transition t : s.getTransitions())
            {
                for (char c = t.getMin() ; c <= t.getMax() ; c++)
                {
                    alphabet.add(getIntFromChar(c));
                }
            }
        }
        nbStates = states.size();
    }

    public void reduce() {
        this.representedBy.reduce();
        syncStates();
    }

    public void removeDeadTransitions() {
        this.representedBy.removeDeadTransitions();
        syncStates();
    }

    public FiniteAutomaton union(FiniteAutomaton other) {
        Automaton union = this.representedBy.union(other.representedBy);
        TIntHashSet alphabet = new TIntHashSet(this.alphabet.toArray());
        alphabet.addAll(other.alphabet.toArray());
        return  new FiniteAutomaton(union,alphabet);
    }

    public FiniteAutomaton intersection(FiniteAutomaton other) {
        Automaton inter = this.representedBy.intersection(other.representedBy);
        TIntHashSet alphabet = new TIntHashSet();
        for (int a : this.alphabet.toArray())
        {
            if (other.alphabet.contains(a))
                alphabet.add(a);
        }
        return new FiniteAutomaton(inter,alphabet);
    }

    public FiniteAutomaton complement(TIntHashSet alphabet) {
        Automaton comp = this.representedBy.complement();
        return new FiniteAutomaton(comp,alphabet);
    }

    public FiniteAutomaton complement() {
        return complement(alphabet);
    }


    public FiniteAutomaton concatenate(FiniteAutomaton other) {
        Automaton conc = this.representedBy.concatenate(other.representedBy);
        TIntHashSet alphabet = new TIntHashSet(this.alphabet.toArray());
        alphabet.addAll(other.alphabet.toArray());
        return new FiniteAutomaton(conc,alphabet);

    }

    public void addEpsilon(int source, int destination) {
        try {
            checkState(source,destination);
        } catch (StateNotInAutomatonException e) {
            LOGGER.severe("Unable to add epsilon transition, a state is not in the automaton : "+e);

        }
        State s = states.get(source);
        State d = states.get(destination);


        ArrayList<StatePair> pairs = new ArrayList<StatePair>();
        pairs.add(new StatePair(s,d));
        this.representedBy.addEpsilons(pairs);
    }

    public boolean isDeterministic()
    {
        return this.representedBy.isDeterministic();
    }

    public void setDeterministic(boolean deterministic)
    {
        this.representedBy.setDeterministic(deterministic);
    }

    public TIntHashSet getFinalStates()
    {
        TIntHashSet finals = new TIntHashSet();
        for (int i = 0 ; i < states.size() ; i++)
        {
            if (states.get(i).isAccept())
                finals.add(i);
        }
        return finals;
    }



    public int getNbStates() {
        return size();
    }

    public void toDotty(String f) {
        String s = this.toDot();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(f)));
            bw.write(s);
            bw.close();
        } catch (IOException e) {
            System.err.println("Unable to write dotty file "+f);
        }

    }

    public String toDot()
    {
        StringBuilder b = new StringBuilder("digraph Automaton {\n");
        b.append("  rankdir = LR;\n");
        Set<State> states = this.representedBy.getStates();
       // setStateNumbers(states);
        for (State s : states) {
            int idx = stateToIndex.get(s);
            b.append("  ").append(idx);
            if (s.isAccept())
                b.append(" [shape=doublecircle];\n");
            else
                b.append(" [shape=circle];\n");
            if (s == this.representedBy.getInitialState()) {
                b.append("  initial [shape=plaintext,label=\"\"];\n");
                b.append("  initial -> ").append(idx).append("\n");
            }
            for (Transition t : s.getTransitions()) {
                b.append("  ").append(idx);
                appendDot(t,b);
            }
        }
        return b.append("}\n").toString();
    }

     private void appendDot(Transition t,StringBuilder b) {
        int destIdx = stateToIndex.get(t.getDest());

		b.append(" -> ").append(destIdx).append(" [label=\"");
        b.append("{");
        b.append(getIntFromChar(t.getMin()));
		if (t.getMin() != t.getMax()) {
            for (char c = (char)(t.getMin()+1) ; c <= t.getMax() ; c++)
            {
                b.append(",");
                b.append(getIntFromChar(c));
            }
		}
        b.append("}");
		b.append("\"]\n");
	}

    public TIntHashSet getAlphabet() {
        return alphabet;
    }

    List<int[]> getTransitions()
    {
        List<int[]> transitions = new ArrayList<int[]>();
        for (int i = 0 ; i < states.size() ; i++)
        {
            State s = states.get(i);
            for (Transition t : s.getTransitions())
            {
                int dest = stateToIndex.get(t.getDest());
                for (char c = t.getMin() ; c <= t.getMax()  ; c++)
                {
                  int symbol = getIntFromChar(c);
                  transitions.add(new int[]{i,dest,symbol});
                }
            }
        }
        return transitions;
    }



    public static class StateNotInAutomatonException extends Exception
    {
        public StateNotInAutomatonException(int state)
        {
            super("State "+state+ " is not in the automaton, please add it using addState");
        }
    }
    public static class NonDeterministicOperationException extends Exception
    {
        public NonDeterministicOperationException()
        {
            super("This operation can oly be called on a determinitic automaton, please use determinize()");
        }
    }

    private static class Triple
    {
        int a;
        int b;
        int c;

        public Triple(int a, int b, int c)
        {
            this.a=a;
            this.b=b;
            this.c=c;
        }
    }

}
