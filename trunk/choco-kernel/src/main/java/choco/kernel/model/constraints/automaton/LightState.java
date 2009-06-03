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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.IntEnumeration;
import choco.kernel.memory.trailing.IndexedObject;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

/*
 * Created by IntelliJ IDEA.
 * User: Richaud
 * Date: 22 juin 2006
 * Since : Choco 2.0.0
 *
 */

/**
 *   Minimal data structure permitting to store a node and to enumerate his successors and predecessors.
 *   Predecessors are stored in a class Arcs composed of a previous state and the list of values leading
 *   this node.
 */
public class LightState implements IndexedObject {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

    // Identifier of the state
    protected int idx;

    //Identifier of the state within its layer
    protected int layerIdx;

    // Successors
    protected Hashtable htransitions;

    // Predecessors
    protected Arcs[] trPred;


    // Set Idx
    public void setIdx(int idx) {
        this.idx = idx;
    }

    // Get Idx
    public int getIdx() {
        return this.idx;
    }

    // Set Layer Idx
    public void setLayerIdx(int idx) {
        this.layerIdx = idx;
    }

    // Get Layer Idx
    public int getLayerIdx() {
        return this.layerIdx;
    }

    //Interface to be used within a StoredBipartiteSet
    public int getObjectIdx() {
        return this.layerIdx;
    }

    public void init(LightState ls) {
        htransitions = ls.htransitions;
        trPred = ls.trPred;
        idx = ls.idx;
        layerIdx = ls.layerIdx;
    }


   // Enumeration on predecessors
    public Enumeration<? extends Arcs> getEnumerationPred() {
        return new Enumerator<Arcs>();
    }

    // Enumeration on successors
    public Enumeration<? extends Integer> getEnumerationSucc() {
        return (this.htransitions.keys());
    }

    // Get delta(value, this)
    public LightState delta(int value) {
        return (LightState)this.htransitions.get(value);
    }

    //  Has a successor
    public boolean hasDelta(int value) {
        return (this.htransitions.get(value) != null);
    }

    private class Enumerator<Arc> implements Enumeration<Arc> {
        int currentIdx = 0;
        int maxSize;

        Enumerator() {
            currentIdx = 0;
            maxSize = trPred.length;
        }

        public boolean hasMoreElements() {
            return (currentIdx < maxSize);
        }

        public Arc nextElement() {
            return (Arc)trPred[currentIdx++];
        }
    }

    // Structure used to store a node and a list of value corresponding
    //  to a predecessor and values leading to this node.
    public class Arcs {
        protected LightState st;
        protected int[] values;

        public Arcs(LightState st, BitSet values) {
            this.st = st;
            this.values = new int[values.cardinality()];
            int cTab = 0;
            for (int i = values.nextSetBit(0); i >= 0; i = values.nextSetBit(i + 1)) {
                this.values[cTab] = i;
                cTab++;
            }
        }

        public LightState getSt() {
            return st;
        }

        public int getValue(int idx) {
            return values[idx];
        }


        public IntEnumeration getEnumerationPred() {
            return new ValEnumerator();
        }

        private class ValEnumerator implements IntEnumeration {
            int currentIdx = 0;
            int maxSize;

            public ValEnumerator() {
                currentIdx = 0;
                maxSize = values.length;
            }

            public boolean hasMoreElements() {
                return (currentIdx < maxSize);
            }

            public int nextElement() {
                return values[currentIdx++];
            }
        }


    }


}
