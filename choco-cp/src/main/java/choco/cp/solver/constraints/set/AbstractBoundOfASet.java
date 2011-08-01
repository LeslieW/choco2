/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.cp.solver.constraints.set;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.cp.solver.variables.set.SetVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.set.AbstractLargeSetIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetDomain;
import choco.kernel.solver.variables.set.SetVar;

/**
 * An abstract class used for MaxOfASet and MinOfaSet constraints
 *
 * @author Arnaud Malapert</br>
 * @version 2.0.1</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 */
public abstract class AbstractBoundOfASet extends AbstractLargeSetIntSConstraint {


    /**
     * Index of the set variable
     */
    public static final int SET_INDEX = 0;

    /**
     * Index of the maximum variable.
     */
    public static final int BOUND_INDEX = 0;

    /**
     * First index of the variables among which the maximum should be chosen.
     */
    public static final int VARS_OFFSET = 1;

    protected final static int SET_EVENTMASK = SetVarEvent.INSTSET_MASK + SetVarEvent.ADDKER_MASK + SetVarEvent.REMENV_MASK;

    protected final static int INT_EVENTMASK = IntVarEvent.INSTINT_MASK + IntVarEvent.BOUNDS_MASK;

    protected final Integer defaultValueEmptySet;

    public AbstractBoundOfASet(IntDomainVar[] intvars, SetVar setvar, Integer defaultValueEmptySet) {
        super(intvars, new SetVar[]{setvar});
        this.defaultValueEmptySet = defaultValueEmptySet;
        if (setvar.getEnveloppeInf() < 0 || setvar.getEnveloppeSup() > intvars.length - 2) {
            throw new SolverException("The enveloppe of the set variable " + setvar.pretty() + " is greater than the array");
        }
    }


    @Override
    public int getFilteredEventMask(int idx) {
        return idx > 0 ? INT_EVENTMASK : SET_EVENTMASK;
    }


    protected final boolean isInKernel(int idx) {
        return svars[SET_INDEX].isInDomainKernel(idx);
    }

    protected final boolean isInEnveloppe(int idx) {
        return svars[SET_INDEX].isInDomainEnveloppe(idx);
    }

    protected final SetDomain getSetDomain() {
        return svars[SET_INDEX].getDomain();
    }


    protected final boolean isEmptySet() {
        return this.svars[SET_INDEX].getEnveloppeDomainSize() == 0;
    }

    protected final boolean isNotEmptySet() {
        return this.svars[SET_INDEX].getKernelDomainSize() > 0;
    }

    protected final boolean isSetInstantiated() {
        return svars[SET_INDEX].isInstantiated();
    }

    protected final boolean updateBoundInf(int val) throws ContradictionException {
        return ivars[BOUND_INDEX].updateInf(val, this, false);
    }

    protected final boolean updateBoundSup(int val) throws ContradictionException {
        return ivars[BOUND_INDEX].updateSup(val, this, false);
    }

    protected abstract boolean removeFromEnv(int idx) throws ContradictionException;

    protected final boolean removeGreaterFromEnv(int idx, int maxValue) throws ContradictionException {
        return ivars[VARS_OFFSET + idx].getInf() > maxValue && this.svars[SET_INDEX].remFromEnveloppe(idx, this, false);
    }

    protected final boolean removeLowerFromEnv(int idx, int minValue) throws ContradictionException {
        return ivars[VARS_OFFSET + idx].getSup() < minValue && this.svars[SET_INDEX].remFromEnveloppe(idx, this, false);
    }

    protected abstract boolean updateEnveloppe() throws ContradictionException;

    @Override
    public void awakeOnEnvRemovals(int idx, DisposableIntIterator deltaDomain)
            throws ContradictionException {
        if (idx == SET_INDEX && deltaDomain.hasNext()) {
            awakeOnEnv(idx, deltaDomain.next());
        }
    }


    @Override
    public void awakeOnkerAdditions(int idx, DisposableIntIterator deltaDomain)
            throws ContradictionException {
        if (idx == SET_INDEX && deltaDomain.hasNext()) {
            awakeOnKer(idx, deltaDomain.next());
        }
    }

    @Override
    public boolean isConsistent() {
        return false;
    }

    protected void filterEmptySet() throws ContradictionException {
        if (defaultValueEmptySet != null) {
            //a default value is assigned to the variable when the set is empty
            ivars[BOUND_INDEX].instantiate(defaultValueEmptySet, this, false);
        }
        setEntailed();
    }

    @Override
    public final void propagate() throws ContradictionException {
        if (isEmptySet()) filterEmptySet();
        else filter();
    }

    protected abstract void filter() throws ContradictionException;


    /**
     * Propagation when a variable is instantiated.
     *
     * @param idx the index of the modified variable.
     * @throws choco.kernel.solver.ContradictionException
     *          if a domain becomes empty.
     */
    @Override
    public final void awakeOnInst(final int idx) throws ContradictionException {
        //CPSolver.flushLogs();
        if (idx >= 2 * VARS_OFFSET) { //of the list
            final int i = idx - 2 * VARS_OFFSET;
            if (isInEnveloppe(i)) { //of the set
                awakeOnInstL(i);
            }
        } else if (idx == VARS_OFFSET) { // Minimum/Maximum variable
            awakeOnInstV();
        } else {
            //set is instantiated, propagate
            propagate();
        }
    }


    protected abstract void awakeOnInstL(int i) throws ContradictionException;

    protected abstract void awakeOnInstV() throws ContradictionException;


    protected abstract int isSatisfiedValue(DisposableIntIterator iter);

    @Override
    public boolean isSatisfied() {
        final DisposableIntIterator iter = svars[SET_INDEX].getDomain().getKernelIterator();
        if (iter.hasNext()) {
            return isSatisfiedValue(iter) == ivars[BOUND_INDEX].getVal();
        } else {
            iter.dispose();
            return defaultValueEmptySet == null || defaultValueEmptySet == ivars[BOUND_INDEX].getVal();
        }
    }

    protected String pretty(String name) {
        StringBuilder sb = new StringBuilder(32);
        sb.append(ivars[BOUND_INDEX].pretty());
        sb.append(" = ").append(name).append('(');
        sb.append(svars[SET_INDEX].pretty()).append(", ");
        sb.append(StringUtils.pretty(ivars, VARS_OFFSET, ivars.length));
        if (defaultValueEmptySet != null) sb.append(", defVal:").append(defaultValueEmptySet);
        sb.append(')');
        return new String(sb);

    }

}