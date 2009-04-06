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


package choco.cp.solver.constraints.global;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.common.util.IntIterator;

import java.util.logging.Logger;

public class Occurrence extends AbstractLargeIntSConstraint {
    private Logger logger = Logger.getLogger("choco.kernel.solver.propagation.const");

    /**
     * Store the number of variables which can still take the occurence value
     */
    public IStateInt nbPossible;

    /**
     * Store the number of variables which are instantiated to the occurence value
     */
    public IStateInt nbSure;

    public boolean constrainOnInfNumber = false;    // >=
    public boolean constrainOnSupNumber = false;    // <=

    //a table of variables that contain the occurrence value in their
    //initial domain.
    public IntDomainVar[] relevantVar;

    /**
     * Constructor,
     * API: should be used through the Model.createOccurrence API
     * Define an occurence constraint setting size{forall v in lvars | v = occval} <= or >= or = occVar
     * assumes the occVar variable to be the last of the variables of the constraint:
     * vars = [lvars | occVar]
     * with  lvars = list of variables for which the occurence of occval in their domain is constrained
     *
     * @param occval checking value
     * @param onInf  if true, constraint insures size{forall v in lvars | v = occval} <= occVar
     * @param onSup  if true, constraint insure size{forall v in lvars | v = occval} >= occVar
     */
    public Occurrence(IntDomainVar[] vars, int occval, boolean onInf, boolean onSup) {
        super(vars);
        init(occval, onInf, onSup);
    }

    public Object clone() throws CloneNotSupportedException {
        Occurrence newc = (Occurrence) super.clone();
        newc.init(this.cste, this.constrainOnInfNumber, this.constrainOnSupNumber);
        return newc;
    }

    public void init(int occval, boolean onInf, boolean onSup) {
        this.cste = occval;
        this.constrainOnInfNumber = onInf;
        this.constrainOnSupNumber = onSup;

        IEnvironment envi = vars[0].getSolver().getEnvironment();
        this.solver = vars[0].getSolver();
        nbPossible = envi.makeInt(0);
        nbSure = envi.makeInt(0);
        int cpt = 0;
        for (int i = 0; i < (vars.length - 1); i++) {
            if (vars[i].canBeInstantiatedTo(cste)) {
                nbPossible.add(1);
                cpt++;
            }
        }
        relevantVar = new IntDomainVar[cpt];
        cpt = 0;
        for (int i = 0; i < (vars.length - 1); i++) {
            if (vars[i].canBeInstantiatedTo(cste)) {
                relevantVar[cpt] = vars[i];
                cpt++;
            }
        }
    }

  public void awakeOnInf(int idx) throws ContradictionException {
    int nbVars = vars.length - 1;
    if (idx == nbVars)
      checkNbPossible();
  }

  public void awakeOnSup(int idx) throws ContradictionException {
      int nbVars = vars.length - 1;
      if (idx == nbVars)
        checkNbSure();
  }

    public void awakeOnInst(int idx) throws ContradictionException {
        int nbVars = vars.length - 1;
        if (idx < nbVars && vars[idx].getVal() == cste) {
            nbSure.add(1);
            checkNbSure();
        }
    }

    public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
        int nbVars = vars.length - 1;
        if (idx < nbVars) {
            while (deltaDomain.hasNext()) {
                int x = deltaDomain.next();
                if (x == cste) {
                    nbPossible.add(-1);
                }
            }
            checkNbPossible();
        }
    }

    public boolean isSatisfied(int[] tuple) {
        int nbVars = vars.length - 1;
        int cptVal = 0;
        for (int i = 0; i < nbVars; i++) {
            if (vars[i].getVal() == cste) cptVal++;
        }
        if (constrainOnInfNumber & constrainOnSupNumber)
            return cptVal == vars[nbVars].getVal();
        else if (constrainOnInfNumber)
            return cptVal >= vars[nbVars].getVal();
        else
            return cptVal <= vars[nbVars].getVal();
    }


    public void checkNbPossible() throws ContradictionException {
        int nbVars = vars.length - 1;
        if (constrainOnInfNumber) {
            vars[nbVars].updateSup(nbPossible.get(), cIndices[nbVars]);
            if (vars[nbVars].getInf() == nbPossible.get()) {
                for (int i = 0; i < relevantVar.length; i++) {
                    if (relevantVar[i].getDomain().contains(cste) && !relevantVar[i].isInstantiated()) {
                        //nbSure.add(1); // must be dealed by the event listener not here !!
                        relevantVar[i].instantiate(cste, -1 /*cIndices[i]*/);
                    }
                }
            }
        }
    }

    public void checkNbSure() throws ContradictionException {
        int nbVars = vars.length - 1;
        if (constrainOnSupNumber) {
            vars[nbVars].updateInf(nbSure.get(), cIndices[nbVars]);
            if (vars[nbVars].getSup() == nbSure.get()) {
                for (int i = 0; i < relevantVar.length; i++) {
                    if (relevantVar[i].getDomain().contains(cste) && !relevantVar[i].isInstantiated()) {
                        //nbPossible.add(-1);
                        relevantVar[i].removeVal(cste, -1 /*cIndices[i]*/);
                    }
                }
            }
        }
    }

    public void filter() throws ContradictionException {
        checkNbPossible();
        checkNbSure();
    }

    public void propagate() throws ContradictionException {
        int nbVars = vars.length - 1;
        int nbSure = 0, nbPossible = 0;
        for (int i = 0; i < (nbVars); i++) {
            if (vars[i].canBeInstantiatedTo(cste)) {
                nbPossible++;
                if (vars[i].isInstantiatedTo(cste)) {
                    nbSure++;
                }
            }
        }

        this.nbSure.set(nbSure);
        this.nbPossible.set(nbPossible);
        checkNbPossible();
        checkNbSure();
    }

    public void awake() throws ContradictionException {
        int nbVars = vars.length;
        nbSure.set(0);
        nbPossible.set(0);
        int cpt = 0;
        for (int i = 0; i < (nbVars - 1); i++) {
            if (vars[i].canBeInstantiatedTo(cste)) {
                nbPossible.add(1);
                cpt++;
            }
            if (vars[i].isInstantiatedTo(cste)) {
                nbSure.add(+1);
            }
        }
        relevantVar = new IntDomainVar[cpt];
        cpt = 0;
        for (int i = 0; i < (nbVars - 1); i++) {
            if (vars[i].canBeInstantiatedTo(cste)) {
                relevantVar[cpt] = vars[i];            
                cpt++;
            }
        }
        if (constrainOnInfNumber) vars[nbVars - 1].updateSup(nbVars - 1, cIndices[nbVars - 1]);
        if (constrainOnSupNumber) vars[nbVars - 1].updateInf(0, cIndices[nbVars - 1]);
        filter();
    }

    public Boolean isEntailed() {
        int nbVars = vars.length - 1;
        int nbPos = 0;
        int nbSur = 0;
        for (int i = 0; i < relevantVar.length; i++) {
            if (vars[i].getDomain().contains(cste)) {
                nbPos++;
                if (vars[i].isInstantiated() && vars[i].getVal() == cste)
                    nbSur++;
            }
        }
        if (constrainOnInfNumber & constrainOnSupNumber) {
          if (vars[nbVars].isInstantiated()) {
             if (nbPos == nbSur && nbPos == vars[nbVars].getVal())
                return Boolean.TRUE;
          } else {
              if (nbPos < vars[nbVars].getInf() ||
                  nbSur > vars[nbVars].getSup())
                return Boolean.FALSE;
          }
        } else if (constrainOnInfNumber) {
           if (nbPos >= vars[nbVars].getSup())
            return Boolean.TRUE;
           if (nbPos < vars[nbVars].getInf())
            return Boolean.FALSE;
        } else {
            if (nbPos <= vars[nbVars].getInf())
             return Boolean.TRUE;
            if (nbPos > vars[nbVars].getSup())
             return Boolean.FALSE;
        }
        return null;
    }

    public String pretty() {
        String s = "occur([";
        for (int i = 0; i < vars.length - 2; i++) {
            s += vars[i] + ",";
        }
        s += vars[vars.length - 2] + "], " + cste + ")";
        if (constrainOnInfNumber && constrainOnSupNumber)
            s += " = ";
        else if (constrainOnInfNumber)
            s += " >= ";
        else
            s += " <= ";
        s += vars[vars.length - 1];
        return s;
    }
}
