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

package choco.cp.solver.constraints.integer.extension;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * GAC 2001 in valid tuples (do not support bound variables)
 */

public class GAC2001LargeSConstraint extends CspLargeSConstraint {

    // Last valid supports Last(x_i, val) = supports( (blocks(i) + val) * size )

    protected IStateInt[] supports;

    protected int[] blocks;

    // Cardinality
    protected int size;

    // offsets(i) = Min(x_i)
    protected int[] offsets;

    // check if none of the tuple is trivially outside
    //the domains and if yes use a fast valid check
    //by avoiding checking the bounds
    protected ValidityChecker valcheck;

    public GAC2001LargeSConstraint(IntDomainVar[] vs, LargeRelation relation) {
        super(vs, relation);
        this.solver = vs[0].getSolver();
        this.size = vs.length;
        this.blocks = new int[size];
        this.offsets = new int[size];

        int nbElt = 0;
        boolean allboolean = true;
        for (int i = 0; i < size; i++) {
            offsets[i] = vs[i].getInf();
            blocks[i] = nbElt;
            if (!vars[i].hasBooleanDomain()) allboolean = false;
            if (!vars[i].hasEnumeratedDomain()) {
                throw new SolverException("GAC2001 can not be used with bound variables");
            } else nbElt += vars[i].getSup() - vars[i].getInf() + 1;
        }
        this.supports = new IStateInt[nbElt * size];

        for (int i = 0; i < supports.length; i++) {
            supports[i] = solver.getEnvironment().makeInt(Integer.MIN_VALUE);
        }
        if (allboolean)
            valcheck = new FastBooleanValidityChecker(size, vars);
        else
            valcheck = new FastValidityChecker(size, vars);        
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
    }

    /**
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    public int[] getFavoriteDomains() {
        return new int[]{IntDomainVar.BITSET,
                IntDomainVar.LINKEDLIST,
                IntDomainVar.BINARYTREE,
                IntDomainVar.BOUNDS,
        };
    }
    /**
     * initialize the supports of each value of indexVar
     *
     * @param indexVar
     * @throws ContradictionException
     */
    public void initializeSupports(int indexVar) throws ContradictionException {
        int[] currentSupport;
        IntDomain dom = vars[indexVar].getDomain();
        int val;
            for (val = vars[indexVar].getInf(); val <= vars[indexVar].getSup(); val = dom.getNextValue(val)) {
                currentSupport = seekNextSupport(indexVar, val, true);
                if (currentSupport != null) {
                    setSupport(indexVar, val, currentSupport);
                } else {
                    vars[indexVar].removeVal(val, cIndices[indexVar]);
                }
            }
    }


    // updates the support for all values in the domain of variable
    // and remove unsupported values for variable
    public void reviseVar(int indexVar) throws ContradictionException {
        int[] currentSupport;
        IntDomain dom = vars[indexVar].getDomain();
        int val;
            for (val = vars[indexVar].getInf(); val <= vars[indexVar].getSup(); val = dom.getNextValue(val)) {
                currentSupport = seekNextSupport(indexVar, val, false);
                if (currentSupport != null) {
                    setSupport(indexVar, val, currentSupport);
                } else {
                    vars[indexVar].removeVal(val, cIndices[indexVar]);
                }
            }
    }

    // Store Last(x_i, val) = support
    public void setSupport(int indexVar, int value, int[] support) {
        for (int i = 0; i < vars.length; i++) {
            supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i].set(support[i]);
        }
    }


    // Get Last(x_i, val)
    public int[] getSupport(int indexVar, int value) {
        int[] resultat = new int[size];
        for (int i = 0; i < size; i++) {
            resultat[i] = supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i].get();
        }
        return resultat;
    }


    // Get Last(x_i, val)
    public int[] lastSupport(int indexVar, int value) {
        return getSupport(indexVar, value);
    }
    /**
     * seek a new support for (variable, value), the smallest tuple greater than currentSupport
     * the search is made through valid tuples until and allowed one is found.
     */
    public int[] seekNextSupport(int indexVar, int val, boolean fromscratch) {
        int[] currentSupport = new int[size];
        int k = 0;
        if (fromscratch) {
            for (int i = 0; i < size; i++) {
                if (i != indexVar)
                    currentSupport[i] = vars[i].getInf();
                else currentSupport[i] = val;
            }
            if (relation.isConsistent(currentSupport)) {
                return currentSupport;
            }
        } else {
            currentSupport = getSupport(indexVar, val);
            if (valcheck.isValid(currentSupport)) {
                return currentSupport;
            } else {
                currentSupport = getFirstValidTupleFrom(currentSupport, indexVar);
                if (currentSupport == null) return null;
                if (relation.isConsistent(currentSupport))
                    return currentSupport;
            }
        }

        while (k < vars.length) {
            if (k == indexVar) k++;
            if (k < vars.length) {
                if (!vars[k].getDomain().hasNextValue(currentSupport[k])) {
                    currentSupport[k] = vars[k].getInf();
                    k++;
                } else {
                    currentSupport[k] = vars[k].getDomain().getNextValue(currentSupport[k]);
                    if ((relation.isConsistent(currentSupport))) {
                        return currentSupport;
                    }
                    k = 0;
                }
            }
        }

        return null;
    }

    /**
     * t is a consistent tuple not valid anymore, we need to go to the first valid tuple
     * greater than t before searching among the valid tuples
     * @param t
     * @param indexVar
     * @return
     */
    public int[] getFirstValidTupleFrom(int[] t, int indexVar) {
        int k = 0;
        while (k < vars.length) {
            if (k == indexVar) k++;            
            if (k < vars.length) {
                if (!vars[k].getDomain().hasNextValue(t[k])) {
                   t[k] = vars[k].getInf();
                    k++;
                } else {
                    t[k] = vars[k].getDomain().getNextValue(t[k]);
                    if (valcheck.isValid(t)) {
                        return t;
                    }
                    k = 0;
                }
            }
        }
        return null;
    }

    public void awake() throws ContradictionException {
        for (int i = 0; i < vars.length; i++) {
            initializeSupports(i);
        }
        propagate();
    }


    public void propagate() throws ContradictionException {
        for (int i = 0; i < size; i++)
            reviseVar(i);
    }


     public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
        filter(idx);
    }

    public void awakeOnInf(int idx) throws ContradictionException {
        filter(idx);
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        filter(idx);
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        filter(idx);
    }

    public void awakeOnBounds(int varIndex) throws ContradictionException {
        filter(varIndex);
    }

    public void awakeOnInst(int idx) throws ContradictionException {
        filter(idx);
    }


    public void filter(int idx) throws ContradictionException {
        //sort variables regarding domain sizes to speedup the check !
        valcheck.sortvars();
        if (vars[idx].hasEnumeratedDomain()) {
        for (int i = 0; i < size; i++)
            if (idx != valcheck.position[i])
               reviseVar(valcheck.position[i]);
        } else {
            for (int i = 0; i < size; i++)
               reviseVar(valcheck.position[i]);            
        }
    }


    public String pretty() {
        StringBuilder sb = new StringBuilder();
        sb.append("GAC2001ValidLarge({");
        for (int i = 0; i < vars.length; i++) {
            if (i > 0) sb.append(", ");
            IntDomainVar var = vars[i];
            sb.append(var.pretty());
        }
        sb.append("})");
        return sb.toString();
    }
}

