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

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class Occurrence extends AbstractLargeIntSConstraint {
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

    public int nbListVars;

    private int occval;

    /**
     * Constructor,
     * API: should be used through the Model.createOccurrence API
     * Define an occurence constraint setting size{forall v in lvars | v = occval} <= or >= or = occVar
     * assumes the occVar variable to be the last of the variables of the constraint:
     * vars = [lvars | occVar]
     * with  lvars = list of variables for which the occurence of occval in their domain is constrained
     *
     * @param vars variables
     * @param occval checking value
     * @param onInf  if true, constraint insures size{forall v in lvars | v = occval} <= occVar
     * @param onSup  if true, constraint insure size{forall v in lvars | v = occval} >= occVar
     * @param environment
     */
    public Occurrence(IntDomainVar[] vars, int occval, boolean onInf, boolean onSup, IEnvironment environment) {
        super(vars);
        init(occval, onInf, onSup, environment);
    }

//    public Object clone() throws CloneNotSupportedException {
//        Occurrence newc = (Occurrence) super.clone();
//        newc.init(this.cste, this.constrainOnInfNumber, this.constrainOnSupNumber);
//        return newc;
//    }

    public void init(int occval, boolean onInf, boolean onSup, IEnvironment environment) {
        this.occval = occval;
        this.constrainOnInfNumber = onInf;
        this.constrainOnSupNumber = onSup;
        this.nbListVars = vars.length - 1;
        nbPossible = environment.makeInt(0);
        nbSure = environment.makeInt(0);
        int cpt = 0;
        for (int i = 0; i < (vars.length - 1); i++) {
            if (vars[i].canBeInstantiatedTo(this.occval)) {
                nbPossible.add(1);
                cpt++;
            }
        }
        relevantVar = new IntDomainVar[cpt];
        cpt = 0;
        for (int i = 0; i < (vars.length - 1); i++) {
            if (vars[i].canBeInstantiatedTo(this.occval)) {
                relevantVar[cpt] = vars[i];
                cpt++;
            }
        }
    }

   public int getFilteredEventMask(int idx) {
       if (idx == vars.length - 1)
            return IntVarEvent.BOUNDSbitvector;
       else return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
    }

  public void awakeOnInf(int idx) throws ContradictionException {
      //assumption : we only get the bounds events on the occurrence variable
      checkNbPossible();
  }

  public void awakeOnSup(int idx) throws ContradictionException {
      //assumption : we only get the bounds events on the occurrence variable
      checkNbSure();
  }

    public void awakeOnInst(int idx) throws ContradictionException {
        //assumption : we only get the inst events on all variables except the occurrence variable
        if (vars[idx].getVal() == occval) {
            nbSure.add(1);
            checkNbSure();
        }
    }

    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
        //assumption : we only get the inst events on all variables except the occurrence variable
            while (deltaDomain.hasNext()) {
                int x = deltaDomain.next();
                if (x == occval) {
                    nbPossible.add(-1);
                }
            }
        deltaDomain.dispose();
            checkNbPossible();
    }

    public boolean isSatisfied(int[] tuple) {
        int nbVars = vars.length - 1;
        int cptVal = 0;
        for (int i = 0; i < nbVars; i++) {
            if (tuple[i] == occval) cptVal++;
        }
        if (constrainOnInfNumber & constrainOnSupNumber)
            return cptVal == tuple[nbVars];
        else if (constrainOnInfNumber)
            return cptVal >= tuple[nbVars];
        else
            return cptVal <= tuple[nbVars];
    }


    public void checkNbPossible() throws ContradictionException {
        if (constrainOnInfNumber) {
            vars[nbListVars].updateSup(nbPossible.get(), cIndices[nbListVars]);
            if (vars[nbListVars].getInf() == nbPossible.get()) {
                for(int i = 0; i < relevantVar.length; i++){
                //for (IntDomainVar aRelevantVar : relevantVar) {
                    IntDomainVar aRelevantVar = relevantVar[i];
                    if (aRelevantVar.getDomain().contains(occval) && !aRelevantVar.isInstantiated()) {
                        //nbSure.add(1); // must be dealed by the event listener not here !!
                        aRelevantVar.instantiate(occval, VarEvent.domOverWDegIdx(cIndices[i]) /*cIndices[i]*/);
                    }
                }
            }
        }
    }

    public void checkNbSure() throws ContradictionException {
        if (constrainOnSupNumber) {
            vars[nbListVars].updateInf(nbSure.get(), cIndices[nbListVars]);
            if (vars[nbListVars].getSup() == nbSure.get()) {
                for(int i = 0; i< relevantVar.length; i++){
//                for (IntDomainVar aRelevantVar : relevantVar) {
                    IntDomainVar aRelevantVar = relevantVar[i];
                    if (aRelevantVar.getDomain().contains(occval) && !aRelevantVar.isInstantiated()) {
                        //nbPossible.add(-1);
                        aRelevantVar.removeVal(occval, VarEvent.domOverWDegIdx(cIndices[i]) /*cIndices[i]*/);
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
        int nbSure = 0, nbPossible = 0;
        for (int i = 0; i < (nbListVars); i++) {
            if (vars[i].canBeInstantiatedTo(occval)) {
                nbPossible++;
                if (vars[i].isInstantiatedTo(occval)) {
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
        propagate();
    }

    public Boolean isEntailed() {
        int nbPos = 0;
        int nbSur = 0;
        for (int i = 0; i < relevantVar.length; i++) {
            if (vars[i].getDomain().contains(occval)) {
                nbPos++;
                if (vars[i].isInstantiated() && vars[i].getVal() == occval)
                    nbSur++;
            }
        }
        if (constrainOnInfNumber & constrainOnSupNumber) {
          if (vars[nbListVars].isInstantiated()) {
             if (nbPos == nbSur && nbPos == vars[nbListVars].getVal())
                return Boolean.TRUE;
          } else {
              if (nbPos < vars[nbListVars].getInf() ||
                  nbSur > vars[nbListVars].getSup())
                return Boolean.FALSE;
          }
        } else if (constrainOnInfNumber) {
           if (nbPos >= vars[nbListVars].getSup())
            return Boolean.TRUE;
           if (nbPos < vars[nbListVars].getInf())
            return Boolean.FALSE;
        } else {
            if (nbPos <= vars[nbListVars].getInf())
             return Boolean.TRUE;
            if (nbPos > vars[nbListVars].getSup())
             return Boolean.FALSE;
        }
        return null;
    }

    public String pretty() {
        StringBuilder s = new StringBuilder("occur([");
        for (int i = 0; i < vars.length - 2; i++) {
            s.append(vars[i]).append(",");
        }
        s.append(vars[vars.length - 2]).append("], ").append(occval).append(")");
        if (constrainOnInfNumber && constrainOnSupNumber)
            s.append(" = ");
        else if (constrainOnInfNumber)
            s.append(" >= ");
        else
            s.append(" <= ");
        s.append(vars[vars.length - 1]);
        return s.toString();
    }
}
