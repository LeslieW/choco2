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
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint X == Y + C, with X and Y two variables and C a constant.
 */
public class EqualXYC extends AbstractBinIntSConstraint {

  /**
   * The search constant of the constraint
   */
  protected final int cste;

  /**
   * Constructs the constraint with the specified variables and constant.
   *
   * @param x0 first IntDomainVar
   * @param x1 second IntDomainVar
   * @param c  The search constant used in the disequality.
   */

  public EqualXYC(IntDomainVar x0, IntDomainVar x1, int c) {
    super(x0, x1);
    this.cste = c;
  }

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == 0){
            if(v0.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }else{
            if(v1.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }
    }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

///!\  Logging statements decrease performances
  /**
   * The one and only propagation method, using foward checking
   */

  public void propagate() throws ContradictionException {
//    if (LOGGER.isLoggable(Level.FINEST))
//    {LOGGER.log(Level.FINEST,"INF({0) >= INF({1}) + {2} = {3}", new Object[]{v0.toString(), v1.toString(), cste, v1.getInf() + cste});}
    v0.updateInf(v1.getInf() + cste, cIdx0);
//    if (LOGGER.isLoggable(Level.FINEST))
//    {LOGGER.log(Level.FINEST,"SUP({0}) <= SUP({1}) + {2} = {3}", new Object[]{v0.toString(), v1.toString(), cste, (v1.getSup() + cste)});}
    v0.updateSup(v1.getSup() + cste, cIdx0);
//    if (LOGGER.isLoggable(Level.FINEST))
//    {LOGGER.log(Level.FINEST,"INF({0}) >= INF({1}) - {2} = {3}", new Object[]{v1.toString(), v0.toString(), cste , (v0.getInf() - cste)});}
    v1.updateInf(v0.getInf() - cste, cIdx1);
//    if (LOGGER.isLoggable(Level.FINEST))
//    {LOGGER.log(Level.FINEST,"SUP({0}) <= SUP({1}) - {2} = {3}", new Object[]{v1.toString(), v0.toString(), cste, (v0.getSup() - cste)});}
    v1.updateSup(v0.getSup() - cste, cIdx1);

    // ensure that, in case of enumerated domains, holes are also propagated
    if (v1.hasEnumeratedDomain() && v0.hasEnumeratedDomain()) {
      IntDomain dom0 = v0.getDomain();
        DisposableIntIterator it = dom0.getIterator();
        while (it.hasNext()) {
            int val0 = it.next();
            if (!(v1.canBeInstantiatedTo(val0 - cste))) {
//                if (LOGGER.isLoggable(Level.FINEST))
//                {LOGGER.log(Level.FINEST,"{0} = {1} + {2} != {3}", new Object[]{v0.toString(), v1.toString(), cste , val0});}
                v0.removeVal(val0, cIdx0);
            }
        }
        it.dispose();
        
        IntDomain dom1 = v1.getDomain();
        it = dom1.getIterator();
        try{
        while (it.hasNext()) {
            int val1 = it.next();
            if (!(v0.canBeInstantiatedTo(val1 + cste))) {
//                if (LOGGER.isLoggable(Level.FINEST))
//                {LOGGER.log(Level.FINEST,"{0} = {1} - {2} != {3}", new Object[]{v1.toString(), v0.toString(), cste, val1});}
                v1.removeVal(val1, cIdx1);
            }
        }
        }finally{
            it.dispose();
        }
    }
  }


  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST,"INF({0}) >= INF({1}) - {2} = {3}", new Object[]{v1.toString(), v0.toString(), cste , (v0.getInf() - cste)});}
      v1.updateInf(v0.getInf() - cste, cIdx1);
    } else {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST,"INF({0}) >= INF({1}) - {2} = {3}", new Object[]{v0.toString(), v1.toString(), cste, (v1.getInf() + cste)});}
      v0.updateInf(v1.getInf() + cste, cIdx0);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST,"SUP({0}) <= SUP({1}) - {2} = {3}", new Object[]{v1.toString(), v0.toString(), cste, (v0.getSup() - cste)});}
      v1.updateSup(v0.getSup() - cste, cIdx1);
    } else {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST,"SUP({0}) <= SUP({1}) + {2}  = {3}", new Object[]{v0.toString(), v1.toString(), cste, (v1.getSup() + cste)});}
      v0.updateSup(v1.getSup() + cste, cIdx0);
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST, "{0} = {1} - {2} = {3}", new Object[]{v1.toString(), v0.toString(), cste, (v0.getVal() - cste)});}
      v1.instantiate(v0.getVal() - cste, cIdx1);
    } else {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST, "{0} = {1} + {2} = {3}", new Object[]{v0.toString(), v1.toString(), cste, (v1.getVal() + cste)});}
      v0.instantiate(v1.getVal() + cste, cIdx0);
    }
  }


  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx == 0) {
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST,"{0} = {1} - {2} != {3}", new Object[]{v1.toString(), v0.toString(), cste, (x - cste)});}
      v1.removeVal(x - cste, cIdx1);
    } else {
      assert(idx == 1);
//      if (LOGGER.isLoggable(Level.FINEST))
//      {LOGGER.log(Level.FINEST, "{0} = {1} + {2} != {3}", new Object[]{v0.toString(), v1.toString(), cste, (x + cste)});}
      v0.removeVal(x + cste, cIdx0);
    }
  }

  /**
   * Checks if the listeners must be checked or must fail.
   */

  public Boolean isEntailed() {
    if ((v0.getSup() < v1.getInf() + cste) ||
        (v0.getInf() > v1.getSup() + cste))
      return Boolean.FALSE;
    else if (v0.isInstantiated() &&
        v1.isInstantiated() &&
        (v0.getVal() == v1.getVal() + cste))
      return Boolean.TRUE;
    else
      return null;
  }

  /**
   * Checks if the constraint is satisfied when the variables are instantiated.
   */

  public boolean isSatisfied(int[] tuple) {
    return (tuple[0] == tuple[1] + this.cste);
  }

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   *
   * @return true iff the constraint is bound consistent (weaker than arc consistent)
   */
  public boolean isConsistent() {
    return ((v0.getInf() == v1.getInf() + cste) && (v0.getSup() == v1.getSup() + cste));
  }

  public AbstractSConstraint opposite() {
    Solver solver = getSolver();
    return (AbstractSConstraint) solver.neq(v0, solver.plus(v1, cste));
    // return NotEqualXYC(v0, v1, cste);
  }


  public String pretty() {
    StringBuffer sb = new StringBuffer();
    sb.append(v0.toString());
    sb.append(" = ");
    sb.append(v1.toString());
    sb.append(StringUtils.pretty(this.cste));
    return sb.toString();
  }

}
