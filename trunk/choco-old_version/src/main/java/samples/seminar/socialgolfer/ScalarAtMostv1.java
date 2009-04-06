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
package samples.seminar.socialgolfer;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import i_want_to_use_this_old_version_of_choco.mem.IStateBitSet;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 2 juin 2008
 * Since : Choco 2.0.0
 *
 */
public class ScalarAtMostv1 extends AbstractLargeIntConstraint {

    public IStateBitSet instPairs; // est-ce que la paire i à déjà été comptabilisé dans nbEqs
    public IStateInt nbEqs;        // nombre de paires instanciées à la même valeur 1
    public int n;
    public int k;                  // le nombre de rencontres (nombre de valeur à 1)


    // vars is the complete table of pairs
    // invariant vars.length == 2*n
    // les paires correspondent à (vars[i],vars[i + n])
    // impose le produit sigma_i (vars[i] * vars[i + n]) = k
    public ScalarAtMostv1(IntDomainVar[] vars, int n, int k) {
        super(vars);
        this.n = n;
        this.k = k;
        nbEqs = vars[0].getProblem().getEnvironment().makeInt(0);
        instPairs = vars[0].getProblem().getEnvironment().makeBitSet(n);
    }

    public boolean productNull(IntDomainVar v1, IntDomainVar v2) {
        return v1.isInstantiatedTo(0) || v2.isInstantiatedTo(0);
    }

    public boolean productOne(IntDomainVar v1, IntDomainVar v2) {
        return v1.isInstantiatedTo(1) && v2.isInstantiatedTo(1);
    }

    public void updateDataStructure(int idx) {
        if (!instPairs.get(idx) && productOne(vars[idx], vars[idx + n])) {
            instPairs.set(idx);
            nbEqs.add(1);
        }
    }

    @Override
	public void awake() throws ContradictionException {
        nbEqs.set(0);
        for (int i = 0; i < n; i++) {
            updateDataStructure(i);
        }
        propagate();
    }

    public void propagateDiff(int i) throws ContradictionException {
        if (vars[i].isInstantiatedTo(1)) {
            vars[i + n].removeVal(1, cIndices[i + n]);
        } else if (vars[i + n].isInstantiatedTo(1)) {
            vars[i].removeVal(1, cIndices[i]);
        }
    }

    public void filter() throws ContradictionException {
        if (nbEqs.get() > k) {
			this.fail();
		}
        if (nbEqs.get() == k) {
            for (int i = 0; i < n; i++) {
                if (!instPairs.get(i)) {
					propagateDiff(i);
				}
            }
        }
    }


    @Override
	public void propagate() throws ContradictionException {
        filter();
    }


    @Override
	public void awakeOnInst(int idx) throws ContradictionException {
        //System.out.println("awake on " + vars[idx] + " to " + vars[idx].getVal());
        if (idx < n) {         // paire (idx, idx + n)
           updateDataStructure(idx);
        } else if (idx >= n) { // paire (idx - n, idx)
           updateDataStructure(idx - n);
        }
        filter();
    }

    @Override
	public String toString() {
        String s = "ScalarAtMost";
        for (int i = 0; i < n; i++) {
            s += vars[i] + "*" + vars[i + n] + "+";
        }
        return s + " = " + k;
    }

    public static void main(String[] args) {
        for (int seed = 0; seed < 100; seed++) {
            AbstractProblem pb = new Problem();
            int n = 4;
            int k = 1;
            IntDomainVar[] vs1 = new IntDomainVar[2*n];
            for (int i = 0; i < 2*n; i++) {
                vs1[i] = pb.makeEnumIntVar("" + i, 0, 1);
            }
            pb.post(new ScalarAtMostv1(vs1, n, k));
            pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb, seed));
            pb.getSolver().setValIntSelector(new RandomIntValSelector(seed));
            //pb.getSolver().setValIterator(new IncreasingTrace());
            pb.solveAll();

            int nbthsol = Cnk(n, k) * (int) Math.pow(3, n - k);
            System.out.println("NbSol : " + pb.getSolver().getNbSolutions() + " The " + nbthsol);
        }
    }

    public static int Cnk(int n, int k) {
        int num = n;
        for (int i = n - 1; i > (n - k); i--) {
            num *= i;
        }
        int den = 1;
        for (int i = 2; i <= k; i++) {
            den *= i;
        }
        return num / den;
    }

    @Override
	public boolean isSatisfied() {
        throw new UnsupportedOperationException("isSatisfied not yet implemented");
    }

    public void propagateEq(int i) throws ContradictionException {
         //System.out.println(this + " instantiate " + vars[i + n] + " et " + vars[i] + " a 1");
         vars[i + n].instantiate(1, cIndices[i + n]);
         vars[i].instantiate(1, cIndices[i]);
     }

}
