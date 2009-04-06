package i_want_to_use_this_old_version_of_choco.global.costregular.FA;

import dk.brics.automaton.RegExp;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 31 oct. 2007
 * Time: 09:22:48
 * To change this template use File | Settings | File Templates.
 */
public class Automaton {


    private static class TokenComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Integer s1 = (Integer) o1;
            Integer s2 = (Integer) o2;
            return s1.compareTo(s2);
        }
    }


    protected Vector<int[]> representedBy;
    protected HashSet<Integer> acceptingStates;
    protected int startingState;
    protected int nbStates;
    protected Vector<Integer> symbols;
    protected HashSet<Integer> alphabet;
    protected Vector<Integer> indexs;
    //protected TreeMap<Integer,Integer> symbolMap;

    public Automaton() {

        this.symbols = new Vector<Integer>(10);
        this.alphabet = new HashSet<Integer>(10);
        this.representedBy = new Vector<int[]>();
        //this.symbolMap = new TreeMap<Integer,Integer>(new TokenComparator());
        this.indexs = new Vector<Integer>(10);
        this.nbStates = 0;
        this.acceptingStates = new HashSet<Integer>();

    }

    public Automaton(String regexp) {
        this();
        //System.out.println(regexp);
        RegExp r = new RegExp(regexp);
        dk.brics.automaton.Automaton a = r.toAutomaton();

        //a.minimize();
        // System.out.println(a);

        Set<dk.brics.automaton.State> dkStates = a.getStates();
        Hashtable<dk.brics.automaton.State,Integer> ct = new Hashtable<dk.brics.automaton.State,Integer>();
        for (dk.brics.automaton.State s : dkStates) {
            ct.put(s,addState());
        }
        setStartingState(ct.get(a.getInitialState()));
        for (dk.brics.automaton.State s : dkStates) {
            int tmp1 = ct.get(s);
            if (s.isAccept())
                setAcceptingState(tmp1);
            for (dk.brics.automaton.Transition t : s.getTransitions()) {
                int tmp2 = ct.get(t.getDest());
                for (char i = t.getMin(); i <= t.getMax(); i++) {
                    addTransition(tmp1,tmp2,Integer.parseInt(i+""));
                }
            }
        }

    }
    public Automaton(Automaton auto)
    {
        this.symbols = new Vector<Integer>();
        this.symbols.setSize(auto.symbols.size());
        for (int i = 0 ; i < auto.symbols.size(); i++)
            this.symbols.set(i,auto.symbols.get(i));
        this.alphabet = new HashSet<Integer>(auto.alphabet.size());
        for (int i : auto.alphabet)
            this.alphabet.add(i);
        this.representedBy = new Vector<int[]>();
        this.representedBy.setSize(auto.representedBy.size());
        for (int i = 0 ; i < auto.representedBy.size() ;i++)
        {
            int[] old = auto.representedBy.get(i);
            int[] nt = new int[old.length];
            System.arraycopy(old,0,nt,0,old.length);
            this.representedBy.set(i,nt);
        }
        this.indexs = new Vector<Integer>();
        this.indexs.setSize(auto.indexs.size());
        for (int i = 0 ; i < auto.indexs.size() ; i++)
            this.indexs.set(i,auto.indexs.get(i));
        this.nbStates = auto.nbStates;
        this.acceptingStates = new HashSet<Integer>(auto.acceptingStates.size());
        for (int i : auto.acceptingStates)
            this.acceptingStates.add(i);
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
        for (int i = 0 ; i < representedBy.size() ; i++)
        {
            for (int j = 0 ; j < representedBy.get(i).length ; j++) {
                int k = representedBy.get(i)[j];
                if (k == s)
                    representedBy.get(i)[j] = -1;
                else if ( k > s) {
                    representedBy.get(i)[j] = k-1;
                }
            }
        }
        int start = getStartingState();
        if (start > s)
            setStartingState(start-1);
        Integer[] aSt = acceptingStates.toArray(new Integer[acceptingStates.size()]);
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
            indexs.setSize(symbol+1);
        indexs.set(symbol,symbols.size()-1);
        //symbolMap.put(symbol,symbols.size()-1);

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

        Integer idx;
        if (symbol >= indexs.size())
            idx = null;
        else
            idx = indexs.get(symbol);

        if (idx == null) {
            idx = addSymbolToAutomaton(symbol);
        }

        if (source < representedBy.size() && destination < representedBy.size()) {
            representedBy.get(source)[idx] = destination;
        }
        else {
            System.err.println("state does not exist... not adding transition");
        }
    }

    public void deleteTransition(int source, int destination, int symbol) {
        Integer idx = indexs.get(symbol);//symbolMap.get(symbol);

        if (idx != null && source < representedBy.size() && destination < representedBy.size()) {
            representedBy.get(source)[idx] = -1;
        }
        else {
            System.err.println("Symbol or state does not exist");
        }
    }

    public int delta(int source, int symbol) {
        if (symbol >= indexs.size())
            return -1;
        Integer idx = indexs.get(symbol);//symbolMap.get(symbol);
        if (idx == null ||source >= nbStates) {
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
            for (int a : alphabet)
            {
                if (delta(q,a) == -1)
                    out.addTransition(q,fs,a);
            }
        }
        return out;
    }
    public class Partition {
        public Vector<TreeSet<Integer>> part;

        public Partition() {
            part = new Vector<TreeSet<Integer>>();
        }

        public Partition(Partition p) {
            this();
            for (TreeSet<Integer> S : p.part) {
                TreeSet<Integer> nS =  new TreeSet<Integer>(S);
                Vector<TreeSet<Integer>> to = partition(nS,p);
                part.addAll(to);
            }


        }

        public boolean equals(Object o) {
            if (o instanceof Partition) {
                Partition p = (Partition) o;
                return p.part.size() == part.size();
            }
            return false;
        }

        public Vector<TreeSet<Integer>> partition(TreeSet<Integer> S, Partition p) {
            Vector<TreeSet<Integer>> newSet = new Vector<TreeSet<Integer>>();
            Integer[] as = S.toArray(new Integer[S.size()]);
            int[] index = new int[nbStates];
            Arrays.fill(index,-1);

            for (int i = 0 ; i < as.length ; i++) {
                for (int j = i+1 ; j < as.length ; j++) {
                    int q1 = as[i];
                    int q2 = as[j];
                    boolean b = true;
                    for (int symb : alphabet) {
                        if (delta(q1,symb) != -1)
                            b &= p.set(delta(q1,symb)) == p.set(delta(q2,symb));
                    }
                    if (b) {
                        if (index[q1] == -1 && index[q2] == -1) {
                            TreeSet<Integer> n = new TreeSet<Integer>();
                            n.add(q1);
                            n.add(q2);
                            S.remove(q1);
                            S.remove(q2);
                            newSet.add(n);
                            index[q1] = newSet.size() -1;
                            index[q2] = newSet.size() -1;
                        }
                        else if (index[q1] == -1) {
                            int idx =index[q2];
                            S.remove(q1);
                            index[q1] =idx;
                            newSet.get(idx).add(q1);
                        }
                        else if (index[q2] == -1) {
                            int idx =index[q1];
                            S.remove(q2);
                            index[q2] =idx;
                            newSet.get(idx).add(q2);
                        }
                    }
                    else {
                        S.remove(q1);
                        S.remove(q2);
                        if (index[q1] == -1) {
                            TreeSet<Integer> n = new TreeSet<Integer>();
                            n.add(q1);
                            newSet.add(n);
                            index[q1] = newSet.size()-1;
                        }
                        if (index[q2] == -1) {
                            TreeSet<Integer> n = new TreeSet<Integer>();
                            n.add(q2);
                            newSet.add(n);
                            index[q2] = newSet.size()-1;
                        }

                    }
                }
            }
            if (!S.isEmpty())
                newSet.add(S);
            return newSet;

        }

        public TreeSet<Integer> set(int look) {
            for (TreeSet<Integer> S : part) {
                if (S.contains(look))
                    return S;
            }
            return null;
        }

        public void add(TreeSet<Integer> a) {
            part.add(a);
        }
        public String toString() {
            StringBuffer b = new StringBuffer("{");
            int ps = part.size();
            int ct = 1;
            for (TreeSet<Integer> ts : part) {
                b.append("{");
                int l = 1;
                int sz = ts.size();
                for (int k : ts) {
                    b.append(k).append((l == sz)?"}":",");
                    l++ ;
                }
                b.append((ct == ps)?"}":",");
                ct++;
            }
            return (b.toString());
        }
    }

    public Automaton minimize() {
        Partition p = partition();
        Automaton n = new Automaton();
        int[] rep = new int[p.part.size()];
        int[] smap = new int[rep.length];
        int i = 0;
        for (TreeSet<Integer> h : p.part)
        {
            rep[i] = h.first();
            smap[i] = n.addState();
            boolean b = false;
            for (int k : h)  {
                if (getStartingState() == k)
                    n.setStartingState(smap[i]);
                if (isAccepting(k))
                    b = true;
            }
            if (b)
                n.setAcceptingState(smap[i]);
            i++;

        }
        for (i = 0 ; i < p.part.size() ; i++) {
            for (int j = 0 ; j < p.part.size(); j++) {
                for (int k : p.part.get(i)) {
                    for (int l : p.part.get(j)) {
                        for (int a : alphabet) {
                            int next =delta(k,a);
                            boolean b = next == l;
                            if (b) {
                                n.addTransition(smap[i],smap[j],a);
                                //  System.out.println("adding transition "+rep[i]+"->"+a+"->"+rep[j]);
                                //  System.out.println("because there was "+k+"->"+a+"->"+next);
                            }
                        }
                    }
                }
            }
        }

        n.removeDeadState();
        return n;


    }

    public  Partition partition()
    {
        TreeSet<Integer> acc = new TreeSet<Integer>(this.acceptingStates);
        TreeSet<Integer> all = new TreeSet<Integer>();
        for (int i = 0 ; i < nbStates ; i++){
            if (!acc.contains(i))
                all.add(i);
        }
        Partition p = new Partition();
        p.add(acc);
        p.add(all);
        Partition p2 = new Partition(p);
        while (!p.equals(p2)) {
            p = p2;
            p2 = new Partition(p);
        }
        return p;

    }

    public void removeDeadState()
    {
        System.out.println("removing dead states of ");
        System.out.println(this);
        int save = -1;
        while (save != nbStates) {
            save = nbStates;
            HashSet<Integer> used = new HashSet<Integer>();
            used.add(getStartingState());
            for (int i = 0 ;i < nbStates ; i++)
            {
                if (!isAccepting(i)) {
                    boolean b = true;
                    for (int a : alphabet) {
                        int next = delta(i,a);
                        b&= next == -1 || next == i;

                    }
                    if (b) {
                        remState(i);
                        i--;
                    }
                }
                for (int a :alphabet) {
                    int next = delta(i,a);
                    if (next != i )
                        used.add(next);
                }

            }
            for (int i = 0; i < nbStates ; i++)
               if (!used.contains(i))
               {
                   remState(i);
                   Integer[] u = used.toArray(new Integer[used.size()]);
                   for (int k : u) {
                       if (k > i) {
                           used.remove(k);
                           used.add(k-1);
                       }
                   }
               }

        }

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
        for(Integer token : symbols) {
            sb.append(token);
            for (int i = 0 ; i < 9 - token.toString().length() ; i++) {
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
            sb.append("q"+state);
            tmpL+= Integer.toString(state).length();
            for(Integer token : symbols){
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




    public static void main(String[] args) {
        Automaton moi = new Automaton();
        for (int i = 0 ; i<5 ;i++)
            moi.addState();
        moi.addTransition(0,1,2);
        moi.addTransition(0,2,1);
        moi.addTransition(1,3,1);
        moi.addTransition(1,0,2);
        moi.addTransition(2,4,1);
        moi.addTransition(2,3,2);
        moi.addTransition(3,3,1);
        moi.addTransition(3,3,2);
        moi.addTransition(4,2,1);
        moi.addTransition(4,1,2);
        //moi.addTransition(5,4,1);
        //moi.addTransition(5,3,2);

        moi.setStartingState(0);
        moi.setAcceptingState(0);
        //moi.setAcceptingState(1);
        //moi.setAcceptingState(3);
        moi.setAcceptingState(4);
        //moi.setAcceptingState(5);

        System.out.println(moi);

        int[] t = new int[]{1,1,2,2,1,1};
        System.out.println(moi.run(t));

        Automaton min = moi.minimize();
        System.out.println("MIN");
        System.out.println(min);
        System.out.println(min.run(t));

        Automaton op1 = moi.opposite();
        Automaton op2 = min.opposite();

        System.out.println("OP1");
        System.out.println(op1);
        System.out.println(op1.run(t));
        System.out.println("OP2");
        System.out.println(op2);
        System.out.println(op2.run(t));

        Automaton minop1 = op1.minimize();
        Automaton minop2 = op2.minimize();

        System.out.println("MINOP1");
        System.out.println(minop1);
        System.out.println(minop1.run(t));
        System.out.println("MINOP2");
        System.out.println(minop2);
        System.out.println(minop2.run(t));




        /*
       Automaton op = moi.opposite();
       System.out.println(op);
       int[] t = new int[]{1,2,2,2,1,2};
       System.out.println(moi.run(t));
       System.out.println(op.run(t));
       Partition p = op.partition();
       for (TreeSet<Integer> h : p.part) {
           for (int k : h) {
               System.out.print(k+ " ");
           }
           System.out.println("");
       }

       Automaton min = op.minimize();
       System.out.println(min);
       System.out.println(min.run(t)); */
    }


}
