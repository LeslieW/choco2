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
package choco.kernel.model.constraints.automaton;

import choco.kernel.solver.SolverException;
import choco.kernel.common.util.UtilAlgo;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import dk.brics.automaton.RegExp;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * A structure of DFA defined by a table of Transitions and a set of final states.
 * The algorithm is based on the interesting paper:
 * Gilles Pesant, A Regular Language Membership Constraint
 * for Finite Sequences of Variables, CP 2004.
 * Pages 482-495, LNCS 3258, Springer-Verlag, 2004.
 * The layered graph used by the algorithm of Pesant is computed from the DFA. The algorithm
 * is a bit less incremental : the lists of outgoing and ingoing edges are not stored
 * upon backtracking. The size of the ingoing egdes is indeed bounded by the number of
 * states of the previous layer which can be very big.
 * An acyclic DFA is only defined by its layeredgraph attribute.
 */
public class DFA {
    /**
     * The table of transitions
     */
    protected Transition[] automaton;

    /**
     * Number of states
     */
    protected int nbState;

    /**
     * The set of final states
     */
    protected List<Integer> finalStates;

    /**
     * Only words of size sizeword will be recognized
     */
    protected int sizeword;

    /**
     * The layered graph build according to that dfa
     */
    protected LayeredDFA graph;
    public LightLayeredDFA lightGraph;

    private int[] idxStates;

    /**
     * Build a DFA based on a list of FEASIBLE tuples
     *
     * @param tuples : a list of int[] corresponding to tuple
     */
    public DFA(List<int[]> tuples) {
        graph = new LayeredDFA(0, tuples.get(0).length + 1);
        setFeasibleOffsets(tuples);
        graph.clearAutomate();
        for (int[] tuple : tuples) {
            graph.union(tuple);
        }
        //graph.convertAutomate();
        lightGraph = new LightLayeredDFA(graph);
    }

    /**
     * Build a DFA based on a list of INFEASIBLE tuples
     * As the relation is defined by infeasible tuples and we build the feasible automaton,
     * we need to know the range of values by the max and min fields...
     *
     * @param tuples : a list of int[] corresponding to tuple
     * @param max    : The maximum value of the alphabet used for each layer (upper bound of each variables).
     * @param min    : The minimum value of the alphabet used for each layer (lower bound of each variables).
     */
    public DFA(List<int[]> tuples, int[] min, int[] max) {
        graph = new LayeredDFA(0, tuples.get(0).length + 1);
        for (int i = 0; i < min.length; i++) {
            graph.setDomSize(i, max[i] - min[i] + 1);
            graph.setOffset(i, min[i]);
        }
        graph.automatAll();
        for (int[] tuple : tuples) {
            graph.substract(tuple);
        }
        //graph.convertAutomate();
        lightGraph = new LightLayeredDFA(graph);
    }


    /**
     * Build a DFA (deterministic finite automaton) to enforce a set of sizeword
     * variables to be assigned to a word recognized by that dfa.
     * The same dfa can be used for different propagators.
     *
     * @param trans       : the list of transitions defining the dfa.
     * @param finalStates : the set of final states.
     * @param sizeword    : Only words of size sizeword will be recognized
     */
    public DFA(List<Transition> trans, List<Integer> finalStates, int sizeword) {
        this.automaton = new Transition[trans.size()];
        trans.toArray(this.automaton);
        Arrays.sort(automaton, new TransitionComparator());
        this.finalStates = finalStates;
        this.sizeword = sizeword;
        initializeNbState();
        initializeSpeedUpData();
        graph = new LayeredDFA(0, sizeword + 1);
        graph.clearAutomate();
        computeOffsetsAndDomains(sizeword);
        buildLayeredGraph(sizeword);
        //graph.convertAutomate();
        lightGraph = new LightLayeredDFA(graph);
    }

    /**
     * Build a DFA (deterministic finite automaton) to enforce a set of sizeword
     * variables to be assigned to a word matching the given regexp.
     * For example regexp = "(1|2)(3*)(4|5)";
     * The same dfa can be used for different propagators.
     *
     * @param strRegExp :
     * @param sizeword  : Only words of size sizeword will be recognized
     */
    public DFA(String strRegExp, int sizeword) {
        try {
        String formated = UtilAlgo.toCharExp(strRegExp);
        dk.brics.automaton.RegExp regexp = new RegExp(formated);
        dk.brics.automaton.Automaton a = regexp.toAutomaton();
        List<Transition> ts = new LinkedList<Transition>();
        List<Integer> fs = new LinkedList<Integer>();
        int nbStates = 0;
        Hashtable ct = new Hashtable();
        ct.put(a.getInitialState(), nbStates++);

        Set<dk.brics.automaton.State> states = a.getStates();
        for (dk.brics.automaton.State s : states) {
            if (!ct.containsKey(s)) {
				ct.put(s, nbStates++);
			}
            if (s.isAccept()) {
				fs.add((Integer) ct.get(s));
			}

            for (dk.brics.automaton.Transition t : s.getTransitions()) {
                for (int i = t.getMin(); i <= t.getMax(); i++) {
                    if (!ct.containsKey(t.getDest())) {
						ct.put(t.getDest(), nbStates++);
					}
                    int k = Automaton.getIntFromChar(i);
                    ts.add(new Transition((Integer) ct.get(s), k, (Integer) ct.get(t.getDest())));
                }

            }
        }

        this.automaton = new Transition[ts.size()];
        ts.toArray(this.automaton);
        Arrays.sort(automaton, new TransitionComparator());
        this.finalStates = fs;
        this.sizeword = sizeword;
        initializeNbState();
        initializeSpeedUpData();
        graph = new LayeredDFA(0, sizeword + 1);
        graph.buildAnEmptyAutomaton();
        computeOffsetsAndDomains(sizeword);
        buildLayeredGraph(sizeword);
        //graph.convertAutomate();
        lightGraph = new LightLayeredDFA(graph);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SolverException("Regular expression requires the package automaton.jar that you can download on http://www.brics.dk/automaton/");
        }
    }


    public LayeredDFA getGraph() {
        return graph;
    }

    public void setGraph(LayeredDFA graph) {
        this.graph = graph;
    }

    public LightLayeredDFA getLightGraph() {
        return lightGraph;
    }

    public void setLightGraph(LightLayeredDFA lightGraph) {
        this.lightGraph = lightGraph;
    }

    private boolean isFinal(int i) {
        return finalStates.contains(i);
    }

    private void initializeNbState() {
        BitSet b = new BitSet();
        for (int i = 0; i < automaton.length; i++) {
            if (!b.get(automaton[i].origin)){
                b.set(automaton[i].origin);
                nbState++;
            }
            if (!b.get(automaton[i].destination)) {
                b.set(automaton[i].destination);
                nbState++;
            }
        }
    }
    // use the fact that the transition table is sorted
    private void initializeSpeedUpData() {
        int lastState = -1;
        idxStates = new int[nbState];
        for (int i = 0; i < automaton.length; i++) {
            if (lastState != automaton[i].origin) {
                idxStates[automaton[i].origin] = i;
            }
            lastState = automaton[i].origin;
        }
    }

    public List<Transition> getOutEdges(int s) {
        LinkedList<Transition> outEdges = new LinkedList<Transition>();
        int cpt = idxStates[s];
        while (cpt < automaton.length && automaton[cpt].origin == s) {
            outEdges.add(automaton[cpt]);
            cpt++;
        }
        return outEdges;
    }

    private void computeOffsets(int layer, Enumeration<Integer> stateLi) {
        int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
        while (stateLi.hasMoreElements()) {
            int o_state = stateLi.nextElement();
            //  System.out.println("Layer : " + layer + " , State:" + o_state);
            List<Transition> outedges = getOutEdges(o_state);
            for (Transition t : outedges) {
                if (t.value < minValue) {
					minValue = t.value;
				}
                if (t.value > maxValue) {
					maxValue = t.value;
				}
            }
        }
        graph.setDomSize(layer, maxValue - minValue + 1);
        graph.setOffset(layer, minValue);
    }

    private void computeInitOffsets() {
        int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
        List<Transition> outedges = getOutEdges(0);
        for (Transition t : outedges) {
            if (t.value < minValue) {
				minValue = t.value;
			}
            if (t.value > maxValue) {
				maxValue = t.value;
			}
        }
        graph.setDomSize(0, maxValue - minValue + 1);
        graph.setOffset(0, minValue);
    }

    private void computeOffsetsAndDomains(int nbvar) {
        Set<Integer> states = new HashSet();
        states.add(0);

        int layer = 0;
        while ((states.size() != 0) && (layer < nbvar)) {
            states = computeOffsetAndDomain(layer++,states);
        }

    }

    private Set<Integer> computeOffsetAndDomain(int layer, Set<Integer> stateLi) {
        Set<Integer> states = new HashSet();
        int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
        Iterator it = stateLi.iterator();
        while (it.hasNext()) {
            int o_state = (Integer) it.next();
            //  System.out.println("Layer : " + layer + " , State:" + o_state);
            List<Transition> outedges = getOutEdges(o_state);
            for (Transition t : outedges) {
                if (t.value < minValue) {
					minValue = t.value;
				}
                if (t.value > maxValue) {
					maxValue = t.value;
				}
                states.add(t.destination);
            }
        }
        graph.setDomSize(layer, maxValue - minValue + 1);
        graph.setOffset(layer, minValue);
        return states;
    }


    protected void buildLayeredGraph(int nbvar) {
        // N[i].get(j) : get the state of the layered graph within layer i associated to
        // the state number j of the original dfa
        Hashtable<Integer, State>[] N = new Hashtable[nbvar + 1];
        computeInitOffsets();
        State initState = graph.makeState(graph, 0);
        State finalState = graph.makeState(graph, nbvar);
        graph.setInitState(initState);
        graph.setLastState(finalState);
        //TODO:   i <= nbvar ??
        for (int i = 0; i <= nbvar; i++) {
			N[i] = new Hashtable<Integer, State>();
		}
        N[0].put(0, initState);
        forwardPhase(N, nbvar);           // forward phase
        graph.removeUnreachablesNodes();  // backward phase
        graph.removeGarbageNodes();       // backward phase
    }

    /**
     * Build the layered graph made of all states reachable from the start state
     */
    protected void forwardPhase(Hashtable<Integer, State>[] N, int nbvar) {
        for (int i = 0; i < nbvar; i++) {
          //  if (i > 0) computeOffsets(i, N[i].keys());
            Enumeration<Integer> stateLi = N[i].keys();
            while (stateLi.hasMoreElements()) {
                int o_state = stateLi.nextElement();
                List<Transition> outedges = getOutEdges(o_state);
                State curState = N[i].get(o_state);
                for (Transition t : outedges) {
                    int o_nextstate = t.destination;
                    if (i < nbvar - 1) {
                        State nextState = N[i + 1].get(o_nextstate);
                        if (nextState == null) {
                            nextState = graph.makeState(graph, i + 1);
                            N[i + 1].put(o_nextstate, nextState);
                        }
                        graph.addTransition(curState, nextState, t.value - graph.getOffset(i));
                    } else if (isFinal(o_nextstate)) {
                        graph.addTransition(curState, graph.getLastState(), t.value - graph.getOffset(i));
                    }
                }
            }
        }
    }

    private void setFeasibleOffsets(List<int[]> tuples) {
        int tsize = tuples.get(0).length;
        int[] min = new int[tsize];
        int[] max = new int[tsize];
        for (int i = 0; i < tsize; i++) {
            min[i] = Integer.MAX_VALUE;
            max[i] = Integer.MIN_VALUE;
        }
        for (int[] tuple : tuples) {
            for (int i = 0; i < tsize; i++) {
                if (tuple[i] < min[i]) {
					min[i] = tuple[i];
				}
                if (tuple[i] > max[i]) {
					max[i] = tuple[i];
				}
            }
        }
        for (int i = 0; i < tsize; i++) {
            graph.setDomSize(i, max[i] - min[i] + 1);
            graph.setOffset(i, min[i]);
        }
    }


    class TransitionComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            int c1 = ((Transition) o1).origin;
            int c2 = ((Transition) o2).origin;
            if (c1 < c2) {
                return -1;
            } else if (c1 == c2) {
                int c1bis = ((Transition) o1).value;
                int c2bis = ((Transition) o2).value;
                if (c1bis < c2bis) {
                    return -1;
                } else if (c1bis == c2bis) {
                    return 0;
                } else {
					return 1;
				}
            } else {
				return 1;
			}
        }
    }
}
