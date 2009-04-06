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
package choco.cp.solver.constraints.set;

import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractTernSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A constraint stating that a set is the union of two others
 * There are seven propagation rules for the constraint sv3 = union(sv1, sv2)
 * Ker(sv1) contains Ker(sv3)
 * Ker(sv2) contains Ker(sv3)
 * Ker(sv3) contains (Ker(sv1) inter Ker(sv2))
 * Env(v3)  disjoint Complement(Env(v1))
 * Env(v3)  disjoint Complement(Env(v2))
 * Env(v2)  disjoint Ker(v1) inter Complement(Env(v3))
 * Env(v1)  disjoint Ker(v2) inter Complement(Env(v3))
 */
public class SetUnion extends AbstractTernSetSConstraint {

    /**
     * Enforce sv3 to be the union of sv1 and sv2
     * @param sv1 the first set
     * @param sv2 the second set
     * @param sv3 the union set
     */

    public SetUnion(SetVar sv1, SetVar sv2, SetVar sv3) {
        super(sv1, sv2, sv3);
        v0 = sv1;
        v1 = sv2;
        v2 = sv3;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        switch (varIdx) {
            case 0:
                v2.addToKernel(x, cIdx2);
                break;
            case 1:
                v2.addToKernel(x, cIdx2);
                break;
            case 2:
                if (!v0.isInDomainEnveloppe(x)) v1.addToKernel(x, cIdx1);
                if (!v1.isInDomainEnveloppe(x)) v0.addToKernel(x, cIdx0);
                break;
            default:
                break;
        }

    }

    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        switch (varIdx) {
	        case 0:
		        if (!v1.isInDomainEnveloppe(x))
			        v2.remFromEnveloppe(x, cIdx2);
		        break;
	        case 1:
		        if (!v0.isInDomainEnveloppe(x))
			        v2.remFromEnveloppe(x, cIdx2);
		        break;
            case 2:
                    v0.remFromEnveloppe(x, cIdx0);
		            v1.remFromEnveloppe(x, cIdx1);
                break;
            default:
                break;
        }
    }

    public void awakeOnInst(int varIdx) throws ContradictionException {
        switch (varIdx) {
            case 0:
                IntIterator it1 = v0.getDomain().getKernelIterator();
                while (it1.hasNext()) {
                    int val = it1.next();
                    v2.addToKernel(val, cIdx2);
                }

                IntIterator it4 = v2.getDomain().getEnveloppeIterator();
                while (it4.hasNext()) {
                    int val = it4.next();
                    if (!v0.isInDomainEnveloppe(val) && !v1.isInDomainEnveloppe(val)) v2.remFromEnveloppe(val, cIdx2);
                }
                break;
            case 1:
                IntIterator it2 = v1.getDomain().getKernelIterator();
                while (it2.hasNext()) {
                    int val = it2.next();
                    v2.addToKernel(val, cIdx2);
                }

                IntIterator it5 = v2.getDomain().getEnveloppeIterator();
                while (it5.hasNext()) {
                    int val = it5.next();
                    if (!v0.isInDomainEnveloppe(val) && !v1.isInDomainEnveloppe(val)) v2.remFromEnveloppe(val, cIdx2);
                }
                break;
            case 2:
                IntIterator it3 = v2.getDomain().getKernelIterator();
                while (it3.hasNext()) {
                    int val = it3.next();
                    if (!v0.isInDomainEnveloppe(val)) v1.addToKernel(val, cIdx1);
                    if (!v1.isInDomainEnveloppe(val)) v0.addToKernel(val, cIdx0);
                }
                break;
            default:
                break;
        }
    }

    public void propagate() throws ContradictionException {

        IntIterator it1 = v0.getDomain().getKernelIterator();
        while (it1.hasNext()) {
            int val = it1.next();
            v2.addToKernel(val, cIdx2);
        }
        IntIterator it2 = v1.getDomain().getKernelIterator();
        while (it2.hasNext()) {
            int val = it2.next();
            v2.addToKernel(val, cIdx2);
        }

        IntIterator it3 = v2.getDomain().getKernelIterator();
        while (it3.hasNext()) {
            int val = it3.next();
            if (!v0.isInDomainEnveloppe(val)) v1.addToKernel(val, cIdx1);
            if (!v1.isInDomainEnveloppe(val)) v0.addToKernel(val, cIdx0);
        }

        IntIterator it4 = v2.getDomain().getEnveloppeIterator();
        while (it4.hasNext()) {
            int val = it4.next();
            if (!v0.isInDomainEnveloppe(val) && !v1.isInDomainEnveloppe(val)) {
                v2.remFromEnveloppe(val, cIdx2);
            }
        }
    }

    public String toString() {
        return v0 + " UNION " + v1 + " = " + v2;
    }

    public String pretty() {
        return v0.pretty() + " UNION " + v1.pretty() + " = " + v2.pretty();
    }

    public boolean isSatisfied() {
        boolean allin = true;
        IntIterator it1 = v2.getDomain().getKernelIterator();
        while (it1.hasNext()) {
            int val = it1.next();
            if(!v0.isInDomainKernel(val) && !v1.isInDomainKernel(val)){
                allin=false;
            }
        }
        if(!allin) return false;
        IntIterator it2 = v1.getDomain().getKernelIterator();
        while (it2.hasNext()){
            int val = it2.next();
            if(!v2.isInDomainKernel(val)){
                allin = false;
            }
        }
        if(!allin) return false;
        IntIterator it3 = v0.getDomain().getKernelIterator();
        while (it3.hasNext()){
            int val = it3.next();
            if(!v2.isInDomainKernel(val)){
                allin = false;
            }
        }
        return allin;
    }

    public boolean isConsistent() {
        // TODO
        return false;
    }
}