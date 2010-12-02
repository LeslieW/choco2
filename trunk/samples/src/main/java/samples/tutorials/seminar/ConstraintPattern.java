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

package samples.tutorials.seminar;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class ConstraintPattern extends AbstractLargeIntSConstraint {

    public ConstraintPattern(IntDomainVar[] vars) {
        super(vars);
    }


    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    public String pretty() {
        return null;
    }


    /**
     * check wether the tuple satisfy the constraint
     * @param tuple value
     * @return true if satisfy
     */
    public boolean isSatisfied(int[] tuple) {
        return false;
    }


    /**
     * <i>Propagation:</i>
     * Propagating the constraint until local consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */

    public void propagate() throws ContradictionException {
        //Elementary method to implement
    }

    
    /**
     * <i>Propagation:</i>
     * Propagating the constraint for the very first time until local
     * consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */

    public void awake() throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on instantiation: full constraint re-propagation.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnInst(int varIdx) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */

    public void awakeOnInf(int varIdx) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnSup(int varIdx) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on one value removal: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnBounds(int varIdx) throws ContradictionException {
         //Change if necessary
        constAwake(false);
    }


    /**
     * Default propagation on one value removal: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @throws ContradictionException contradiction exception
     */

    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }

    /**
     * Default propagation on one value removal: propagation on domain revision.
     * @param varIdx index of the variable to reduce
     * @param deltaDomain iterator over remove values.
     * @throws ContradictionException contradiction exception
     */
    public void awakeOnRemovals(int varIdx, DisposableIntIterator deltaDomain) throws ContradictionException {
        //Change if necessary
        constAwake(false);
    }


}
