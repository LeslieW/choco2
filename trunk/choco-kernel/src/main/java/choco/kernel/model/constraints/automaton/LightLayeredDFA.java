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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Automate
 *
 * @author Cambazard Hadrien
 * @author Richaud Guillaume
 * @version 0.1  Nov 19, 2005.
 */

public class LightLayeredDFA {

    // Size of the domain [0.. domSize -1]
    protected int[] domSizes;

    public int getOffset(int i) {
        return offsets[i];
    }

    // Size of the domain [0.. domSize -1]
    protected int[] offsets;


    public LightState getInitState() {
        return initState;
    }

    // Starting state
    protected LightState initState;

    public LightState getLastState() {
        return lastState;
    }

    // Last state
    protected LightState lastState;

    // Current number of state built (with deleted one)
    protected int nbState = 0;

    // Store nodes of each layer
    protected ArrayList[] etatsDeLaCouche;

    // Number of layer
    protected int nbLevel;

    // Get current number of states
    public int getAutomateSize() {
        int tot = 0;
        for (int i = 0; i < this.etatsDeLaCouche.length; i++)
            tot += this.etatsDeLaCouche[i].size();
        return tot;
    }

    public boolean isEmpty() {
        return this.etatsDeLaCouche[1].isEmpty();
    }


    /**
     * construct an initial automaton with different domain sizes per layer
     * The automaton initially accepts all words
     *
     */
    public LightLayeredDFA(LayeredDFA automata) {
        this.nbLevel = automata.getNbLevel();
        this.domSizes = new int[this.nbLevel];
        System.arraycopy(automata.domSizes, 0, this.domSizes, 0, this.nbLevel);
        this.domSizes[nbLevel - 1] = 0;
        this.offsets = new int[nbLevel];
        System.arraycopy(automata.offsets, 0, this.offsets, 0, this.nbLevel);


        this.etatsDeLaCouche = new ArrayList[nbLevel];
        for (int i = 0; i < etatsDeLaCouche.length; i++) {
            etatsDeLaCouche[i] = new ArrayList();
        }

        Hashtable ht = new Hashtable();
        nbState = 0;

        for (int i = 0; i < nbLevel; i++) {
            int layerIdx = 0;
            for (int j = 0; j < automata.levelStates[i].size(); j++) {
                ((State) automata.levelStates[i].get(j)).setIdx(nbState++);
                ((State) automata.levelStates[i].get(j)).setLayerIdx(layerIdx++);                
                LightState nls = new LightState();
                etatsDeLaCouche[i].add(nls);

                ht.put(automata.levelStates[i].get(j), nls);

            }
        }

        for(Enumeration e = ht.keys(); e.hasMoreElements();) {
            State currentState = (State)e.nextElement();
            LightState ff = (LightState)ht.get(currentState);
            ff.init(currentState.convertState(ht));
        }

        this.initState = (LightState)ht.get(automata.initState);
        this.lastState = (LightState)ht.get(automata.lastState);

    }
}
