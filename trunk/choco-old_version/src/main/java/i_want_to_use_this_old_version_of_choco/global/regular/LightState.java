package i_want_to_use_this_old_version_of_choco.global.regular;

import i_want_to_use_this_old_version_of_choco.util.IntEnumeration;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Richaud
 * Date: 22 juin 2006
 * Time: 11:02:58
 * To change this template use File | Settings | File Templates.
 */


/**
 *   Minimal data structure permitting to store a node and to enumerate his successors and predecessors.
 *   Predecessors are stored in a class Arcs composed of a previous state and the list of values leading
 *   this node.
 */
public class LightState {
    // Identifier of the state
    protected int idx;

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

    public void init(LightState ls) {
        htransitions = ls.htransitions;
        trPred = ls.trPred;
        idx = ls.idx;
    }


   // Enumeration on predecessors
    public Enumeration getEnumerationPred() {
        return new Enumerator<Arcs>();
    }

    // Enumeration on successors
    public Enumeration getEnumerationSucc() {
        return (this.htransitions.keys());
    }

    // Get delta(value, this)
    public LightState delta(int value) {
        return ((LightState) this.htransitions.get(value));
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
            return ((Arc) trPred[currentIdx++]);
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
