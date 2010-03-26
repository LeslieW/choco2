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
package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: Richaud
 * Date: 8 janv. 2007
 * Since : Choco 2.0.0
 *
 */
public final class MaxXYZ extends AbstractTernIntSConstraint {
    public MaxXYZ(IntDomainVar x, IntDomainVar y, IntDomainVar max) {
        super(max, x, y);
    }

    public boolean isSatisfied(int[] tuple) {
        return (Math.max(tuple[2], tuple[1]) == tuple[0]);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == 0){
            if(v0.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }else if(idx == 1){
            if(v1.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }else{
            if(v2.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }
    }


    public String pretty() {
        return "max(" + v2.pretty() + "," + v1.pretty() + ") = " + v0.pretty();
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx == 0) {
            v1.updateSup(v0.getSup(), this, false);
            v2.updateSup(v0.getSup(), this, false);
        } else {
            v0.updateSup(Math.max(v1.getSup(), v2.getSup()), this, false);
        }
    }

    public void awakeOnInf(int idx) throws ContradictionException {
        if (idx == 0) {
            if (v1.getInf() > v2.getSup()) {
                v1.updateInf(v0.getInf(), this, false);
            }

            if (v2.getInf() > v1.getSup()) {
                v2.updateInf(v0.getInf(), this, false);
            }
        } else {
            v0.updateInf(Math.max(v1.getInf(), v2.getInf()), this, false);
        }
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        if (idx == 0) {
            if (x > v2.getSup()) {
                v1.removeVal(x, this, false);
            }

            if (x > v1.getSup()) {
                v2.removeVal(x, this, false);
            }
        } else {
            if (!v1.canBeInstantiatedTo(x) && !v2.canBeInstantiatedTo(x)) {
                v0.removeVal(x, this, false);
            }
        }
    }

    /**
     * Propagation for the constraint awake var.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          if a domain becomes empty or the
     *          filtering algorithm infers a contradiction
     */
    public void propagate() throws ContradictionException {
        filter(0);
        filter(1);
        filter(2);
    }

    public void filter(int idx) throws ContradictionException {
        if (idx == 0) {
            v0.updateSup(Math.max(v1.getSup(), v2.getSup()), this, false);
            v0.updateInf(Math.max(v1.getInf(), v2.getInf()), this, false);

            if (v0.hasEnumeratedDomain()) {
                IntDomain dom0 = v0.getDomain();
                DisposableIntIterator it = dom0.getIterator();
                try{
                    while (it.hasNext()) {
                        int valeur = it.next();
                        if (!v1.canBeInstantiatedTo(valeur) && !v2.canBeInstantiatedTo(valeur)) {
                            v0.removeVal(valeur, this, false);
                        }
                    }
                }finally {
                    it.dispose();
                }
            }
        } else if (idx == 1) {
            v1.updateSup(v0.getSup(), this, false);
            if (v1.getInf() > v2.getSup()) {
                v1.updateInf(v0.getInf(), this, false);
            }

            if (v1.hasEnumeratedDomain()) {
                IntDomain dom1 = v1.getDomain();
                DisposableIntIterator it = dom1.getIterator();
                try{
                    while (it.hasNext()) {
                        int valeur = it.next();
                        if (!v0.canBeInstantiatedTo(valeur) && (valeur > v2.getSup())) {
                            v1.removeVal(valeur, this, false);
                        }
                    }
                }finally {
                    it.dispose();
                }
            }


        } else if (idx == 2) {
            v2.updateSup(v0.getSup(), this, false);
            if (v2.getInf() > v1.getSup()) {
                v2.updateInf(v0.getInf(), this, false);
            }
            if (v2.hasEnumeratedDomain()) {
                IntDomain dom2 = v2.getDomain();
                DisposableIntIterator it = dom2.getIterator();
                try{
                    while (it.hasNext()) {
                        int valeur = it.next();
                        if (!v0.canBeInstantiatedTo(valeur) && (valeur > v1.getSup())) {
                            v2.removeVal(valeur, this, false);
                        }
                    }
                }finally {
                    it.dispose();
                }
            }


        }
    }

    public void awakeOnInst(int idx, int val) throws ContradictionException {
        if (idx == 0) {
            v1.updateSup(val, this, false);
            v2.updateSup(val, this, false);
            if (!v1.canBeInstantiatedTo(val)) v2.instantiate(val, this, false);
            if (!v2.canBeInstantiatedTo(val)) v1.instantiate(val, this, false);
        } else if (idx == 1) {
            if (val > v2.getSup()) v0.instantiate(val, this, false);
        } else if (idx == 2) {
            if (val > v1.getSup()) v0.instantiate(val, this, false);
        }
    }


}


