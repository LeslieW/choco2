//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.var.BitSetIntDomain;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain.IRemovalExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.StructureMaintainer;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmAssignment;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmNotEqualXC;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PalmBitSetIntDomain extends BitSetIntDomain implements PalmIntDomain {
    /**
     * A list of explanations for value withdrawals.
     */

    public IRemovalExplanation[] explanationOnVal;


    /**
     * Decision constraints on the variable for branching purpose.
     */

    public Constraint[] decisionConstraints;


    /**
     * Negation of decision constraints on the variable for branching purpose.
     */

    public Constraint[] negDecisionConstraints;


    /**
     * A linked list for restoration events.
     */

    int[] restoreChain;


    /**
     * The begin of the <code>restoreChain</code> linked list.
     */

    int firstRestValueToBePropagated = -1;


    /**
     * States wether a value will be restored.
     */

    BitSet willBeRestored;

    /**
     * original domain of the variable
     */
    public BitSet originalDomain;

    public PalmBitSetIntDomain(IntDomainVarImpl v, int a, int b) {
        super(v, a, b);
        this.explanationOnVal = new IRemovalExplanation[b - a + 1];
        this.restoreChain = new int[b - a + 1];
        this.willBeRestored = new BitSet();
        this.decisionConstraints = new Constraint[b - a + 1];
        this.negDecisionConstraints = new Constraint[b - a + 1];
        originalDomain = new BitSet(capacity);
        for (int i = 0; i < capacity; i++) {
            originalDomain.set(i);
        }
        inf = null;
        sup = null;
    }

    /**
     * constructor with a set of discrete values
     *
     * @param v
     * @param sortedValues
     */
    public PalmBitSetIntDomain(IntDomainVarImpl v, int[] sortedValues) {
        super(v, sortedValues);
        int a = sortedValues[0];
        int b = sortedValues[sortedValues.length - 1];
        this.explanationOnVal = new IRemovalExplanation[b - a + 1];
        this.restoreChain = new int[b - a + 1];
        this.willBeRestored = new BitSet();
        this.decisionConstraints = new Constraint[b - a + 1];
        this.negDecisionConstraints = new Constraint[b - a + 1];

        originalDomain = new BitSet(capacity);
        for (int i = 0; i < sortedValues.length; i++) {
            originalDomain.set(sortedValues[i] - a);
        }
        inf = null;
        sup = null;
    }

    /**
     * Returns the original lower bound.
     */

    public int getOriginalInf() {
        return this.offset;
    }


    /**
     * Returns the original upper bound.
     */

    public int getOriginalSup() {
        return this.offset + this.chain.length - 1;
    }

    /**
     * Returns the minimal present value.
     */
    public int getInf() {
        return contents.nextSetBit(0) + offset;
    }


    /**
     * Returns the maximal present value.
     */
    public int getSup() {
        return contents.prevSetBit(capacity - 1) + offset;
    }

     /**
     * Returns the value following <code>x</code>
     */

    public int getNextValue(int x) {
        int i = x - offset;
        if (i < 0) return getInf();
        int bit = contents.nextSetBit(i + 1);
        if (bit < 0) return Integer.MAX_VALUE;
        else return bit + offset;
    }


    /**
     * Returns the value preceding <code>x</code>
     */

    public int getPrevValue(int x) {
        int i = x - offset;
        return contents.prevSetBit(i - 1) + offset;
    }


    /**
     * Checks if the value has a following value.
     */

    public boolean hasNextValue(int x) {
        int i = x - offset;
        return (contents.nextSetBit(i + 1) != -1);
    }


    /**
     * Checks if the value has a preceding value.
     */

    public boolean hasPrevValue(int x) {
        int i = x - offset;
        return (contents.prevSetBit(i - 1) != -1);
    }

    /**
     * Returns all the value currently in the domain.
     */

    public int[] getAllValues() {
        int[] ret = new int[this.getSize()];
        int idx = 0;
        for (int val = contents.nextSetBit(0); val >= 0; val = contents.nextSetBit(val + 1)) {
            ret[idx] = val + offset;
            idx++;
        }
        return ret;
    }


    /**
     * Returns the decision constraint assigning the domain to the specified value. The constraint is created if
     * it is not yet created.
     */

    public Constraint getDecisionConstraint(int val) {
        Constraint cons = this.decisionConstraints[val - this.getOriginalInf()];
        if (cons != null) {
            return cons;
        } else {
            cons = new PalmAssignment(this.variable, val);
            this.decisionConstraints[val - this.getOriginalInf()] = cons;
            this.negDecisionConstraints[val - this.getOriginalInf()] = new PalmNotEqualXC(this.variable, val);
            return cons;
        }
    }


    /**
     * Returns the negated decision constraint.
     */

    public Constraint getNegDecisionConstraint(int val) {
        return this.negDecisionConstraints[val - this.getOriginalInf()];
    }


    /**
     * Updates the lower bound and posts the event.
     */

    public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException {
        boolean rep = false;
        int newi = x - offset;  // index of the new lower bound
        for (int i = contents.nextSetBit(0); i < newi; i = contents.nextSetBit(i + 1)) {
            rep |= this.removeVal(i + offset, idx, (Explanation) e.copy());
        }
        return rep;
    }


    /**
     * Updates the upper bound and posts the event.
     */

    public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException {
        boolean rep = false;
        int newi = x - offset;  // index of the new lower bound
        for (int i = contents.prevSetBit(capacity - 1); i > newi; i = contents.prevSetBit(i - 1)) {
            rep |= this.removeVal(i + offset, idx, (Explanation) e.copy());
        }
        return rep;
    }


    /**
     * Removes a value and posts the event.
     */

    public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException {
        if (this.removeVal(value, e)) {
            ((PalmEngine) this.getProblem().getPropagationEngine()).postRemoveVal(this.variable, value, idx);
            if (this.getSize() == 0) {
                ((PalmEngine) this.getProblem().getPropagationEngine()).raisePalmContradiction((PalmIntVar) this.variable);
            }
            return true;
        }
        return false;
    }


    /**
     * Restores a lower bound and posts the event.
     */

    public void restoreInf(int newValue) {
        System.err.println("restoreInf should not be called on a BitSetIntdomain !");
    }


    /**
     * Restores an upper bound and posts the event.
     */

    public void restoreSup(int newValue) {
        System.err.println("restoreSup should not be called on a BitSetIntdomain !");
    }


    /**
     * Restores a value and posts the event.
     */

    public void restoreVal(int val) {
        this.add(val);
/*if (val < this.inf) {
            this.inf = val;
        }
        if (val > this.sup) {
            this.sup = val;
        } */
        if (this.getInf() == this.getSup()) {
            this.variable.value.set(this.getInf());
        } else {
            this.variable.value.set(IStateInt.UNKNOWN_INT);
        }
        StructureMaintainer.updateDataStructuresOnRestore(this.variable, VAL, val, 0);
        //((PalmIntVar) this.variable).updateDataStructuresOnRestore(PalmIntVar.VAL, val, 0);
        ((PalmEngine) this.getProblem().getPropagationEngine()).postRestoreVal((PalmIntVar) this.variable, val);
    }


    /**
     * Allows to get an explanation for the domain or a bound of the variable. This explanation is merge to the
     * explanation in parameter.
     *
     * @param select Should be <code>PalmIntDomain.INF</code>, <code>PalmIntDomain.SUP</code>, or <code>PalmIntDomain.DOM</code>
     */

    public void self_explain(int select, Explanation expl) {
        switch (select) {
            case DOM:
                for (int i = originalDomain.nextSetBit(0); i >= 0; i = originalDomain.nextSetBit(i + 1))
                    this.self_explain(VAL, i + offset, expl);
                break;
            case INF:
                int inf = this.getInf() - 1 - offset;
                for (int i = originalDomain.nextSetBit(0); i <= inf; i = originalDomain.nextSetBit(i + 1))
                    this.self_explain(VAL, i + offset, expl);
                break;
            case SUP:
                int sup = this.getSup() + 1 - offset;
                for (int i = sup; i >= 0; i = originalDomain.nextSetBit(i + 1))
                    this.self_explain(VAL, i + offset, expl);
                break;
            default:
                if (Logger.getLogger("choco").isLoggable(Level.WARNING))
                    Logger.getLogger("choco").warning("PaLM: VAL needs another parameter in self_explain (IntDomainVar)");
        }
    }


    /**
     * Allows to get an explanation for a value removal from the variable. This explanation is merge to the
     * explanation in parameter.
     *
     * @param select Should be <code>PalmIntDomain.VAL</code>
     */

    public void self_explain(int select, int x, Explanation expl) {
        if (select == VAL) {
            int realVal = x - this.getOriginalInf(); //this.bucket.getOffset();
            ConstraintCollection expla = null;
            if ((realVal >= 0) && (realVal < this.getOriginalSup() -
                    this.getOriginalInf() + 1)) { //this.bucket.size())) {
                expla = this.explanationOnVal[realVal];
            }
            if (expla != null) expl.merge(expla);
        } else {
            if (Logger.getLogger("choco").isLoggable(Level.WARNING))
                Logger.getLogger("choco").warning("PaLM: INF, SUP or DOM do not need a supplementary parameter in self_explain (IntDomainVar)");
        }
    }


    /**
     * When a value is restored, it deletes the explanation associated to the value removal.
     */

    public void resetExplanationOnVal(int val) {
        this.explanationOnVal[val - this.getOriginalInf()] = null;
    }


    /**
     * When a lower bound is restored, it deletes the explanation associated to the value removal.
     */

    public void resetExplanationOnInf() {
    }


    /**
     * When an upper bound is restored, it deletes the explanation associated to the value removal.
     */

    public void resetExplanationOnSup() {
    }


    /**
     * When restoration are raised, some value removal can be inappropiate. This removes such past events.
     */

    public void checkRemovalChain() {
        int cidx = firstIndexToBePropagated;
        int precidx = -1;
        while (cidx != -1) {
            if (contents.get(cidx)) { // Un element dans le domaine n'a pas a etre propager en removeVal
                //clear chain
                if (cidx == firstIndexToBePropagated)
                    firstIndexToBePropagated = chain[cidx];
                else {
                    chain[precidx] = chain[cidx];
                }

            } else
                precidx = cidx;

            cidx = chain[cidx];
        }
    }


    /**
     * If a contradiction occure during restoration handling, the chain must be reinitialized in the previous state.
     */

    public void resetRemovalChain() {
        firstIndexToBePropagated = firstIndexBeingPropagated;
        firstIndexBeingPropagated = -1;
    }


    /**
     * If restoration handling completes, the chain must reinitlized to null.
     */

    public void releaseRepairDomain() {
        firstRestValueToBePropagated = -1;
        this.willBeRestored.clear();
    }


    /**
     * Returns an iterator on restored values.
     */

    public IntIterator getRepairIterator() {
        return new PalmBitSetIntDomain.RepairIntDomainIterator(this);
    }


    /**
     * Checks if the value is in the domain. Basically it checks if the value is in the original domain and if this case
     * only calls the super method.
     */

    public boolean contains(int val) {
        if (val < this.getOriginalInf() || val > this.getOriginalSup()) {
            return false;
        }
        return super.contains(val);
    }

    // Private Stuf ...

    protected class RepairIntDomainIterator implements IntIterator {
        protected BitSetIntDomain domain;
        protected int currentIndex = -1;

        private RepairIntDomainIterator(BitSetIntDomain dom) {
            domain = dom;
            currentIndex = -1;
        }

        public boolean hasNext() {
            if (currentIndex == -1) {
                return (firstRestValueToBePropagated != -1);
            } else {
                return (restoreChain[currentIndex] != -1);
            }
        }

        public int next() {
            if (currentIndex == -1) {
                currentIndex = firstRestValueToBePropagated;
            } else {
                currentIndex = restoreChain[currentIndex];
            }
            return currentIndex + offset;
        }

        public void remove() {
            if (currentIndex == -1) {
                throw new IllegalStateException();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private boolean add(int x) {
        // ajoute la valeur si necessaire.
        int i = x - offset;
        if (!contents.get(i)) {
            addIndex(i);
            return true;
        } else {
            return false;
        }
    }

    private void addIndex(int i) {
        contents.set(i);
        if (!this.willBeRestored.get(i)) {
            // verifie d'abord que l'evenement de restauration n'est pas deja la avant de ne l'ajouter.
            restoreChain[i] = firstRestValueToBePropagated;
            firstRestValueToBePropagated = i;
            this.willBeRestored.set(i);
        }
        size.add(1);
    }

    protected boolean removeVal(int value, Explanation e) {
        if (this.contains(value)) {
            this.explanationOnVal[value - this.getOriginalInf()] =
                    ((PalmExplanation) e).makeRemovalExplanation(value, (PalmIntVar) this.variable);
            this.remove(value);
            if (this.getSize() == 1) {
                this.variable.value.set(getInf());
            } else if (this.getSize() == 0) {
                this.variable.value.set(IStateInt.UNKNOWN_INT);
            }
            StructureMaintainer.updateDataStructures(this.variable, VAL, value, 0);
            return true;
        }
        return false;
    }
}

