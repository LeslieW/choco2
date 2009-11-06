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


package choco.cp.solver.constraints.global.matching;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.global.matching.AbstractBipartiteMatching;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Standard alldiff constraint with generalized AC
 * integer valued variables are used only for the left vertex set
 * no explicit variables are used for the right vertex set
 * the right vertex set is the interval (minValue .. maxValue)
 */
public class AllDifferent extends AbstractBipartiteMatching implements IntSConstraint {

  /**
   * API entry point: creating an ice alldifferent constraint (before posting it)
   *
   * @param vars
   */
  public AllDifferent(IntDomainVar[] vars) {
    super(vars, vars.length, AllDifferent.getValueGap(vars));
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    for (int i = 0; i < vars.length; i++) {
      IntDomainVar var = vars[i];
      minValue = Math.min(var.getInf(), minValue);
      maxValue = Math.max(var.getSup(), maxValue);
    }
  }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.REMVALbitvector + IntVarEvent.INSTINTbitvector;
        // return 0x0B;
    }


    /**
   * AllDiff constraint constructor
   *
   * @param vars     the choco variable list
   * @param minValue minimal value in vars domain
   * @param maxValue maximal value in vars domain
   */
  public AllDifferent(IntDomainVar[] vars, int minValue, int maxValue) {
    super(vars, vars.length, maxValue - minValue + 1);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Static method for one parameter constructor
   *
   * @param vars domain variable list
   * @return gap between min and max value
   */
  private static int getValueGap(IntDomainVar[] vars) {
    int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
    for (int i = 0; i < vars.length; i++) {
      IntDomainVar var = vars[i];
      minValue = Math.min(var.getInf(), minValue);
      maxValue = Math.max(var.getSup(), maxValue);
    }
    return maxValue - minValue + 1;
  }


  // The next two functions implement the main two events:
  /**
   * when an edge is definitely chosen in the bipartite assignment graph.
   *
   * @param i
   * @param j
   * @throws ContradictionException
   */
  public void setEdgeAndPublish(int i, int j) throws ContradictionException {
    this.setMatch(i, j);
    for (int i2 = 0; i2 < this.nbLeftVertices; i2++) {
      if (i2 != i) {
        this.vars[i2].removeVal(j + this.minValue, this.cIndices[i2]);
      }
    }
  }

  /**
   * when an edge is definitely removed from the bipartite assignment graph.
   *
   * @param i
   * @param j
   * @throws ContradictionException
   */
  public void deleteEdgeAndPublish(int i, int j) throws ContradictionException {
    this.deleteMatch(i, j);
    this.vars[i].removeVal(j + this.minValue, this.cIndices[i]);
  }

  // propagation functions: reacting to choco events

  /**
   * when a value is removed from a domain var, removed the corresponding edge in current matching
   *
   * @param idx the variable index
   * @param val the removed value
   */
  public void awakeOnRem(int idx, int val) {
    this.deleteMatch(idx, val - this.minValue);
    this.constAwake(false);
  }


  /**
   * update current matching when a domain inf is increased
   *
   * @param idx the variable index
   */
  public void awakeOnInf(int idx) {
    for (int j = this.minValue; j < this.vars[idx].getInf(); j++) {
      this.deleteMatch(idx, j - this.minValue);
    }
    this.constAwake(false);
  }

  /**
   * update current matching when a domain sup is decreased
   *
   * @param idx the variable index
   */
  public void awakeOnSup(int idx) {
    for (int j = this.vars[idx].getSup() + 1; j <= this.maxValue; j++) {
      this.deleteMatch(idx, j - this.minValue);
    }
    this.constAwake(false);
  }

  /**
   * update current matching when a variable has been instantiated
   *
   * @param idx the variable index
   * @throws ContradictionException
   */
  public void awakeOnInst(int idx) throws ContradictionException {
    this.setEdgeAndPublish(idx, this.vars[idx].getVal() - this.minValue);
    this.constAwake(false);
  }

  /**
   * no specific initial propagation (awake does the same job as propagate)
   *
   * @throws ContradictionException
   */
  public void awake() throws ContradictionException {
    this.propagate();
  }

  /**
   * Checks if the constraint is satisfied when all variables are instantiated.
   */
  public boolean isSatisfied(int[] tuple) {
     for (int i = 0; i < vars.length; i++) {
         for (int j = 0; j < i; j++) {
             if (tuple[i] == tuple[j]) {
                 return false;
             }
         }
     }
     return true;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("AllDifferent({");
    for (int i = 0; i < vars.length; i++) {
      if (i > 0) sb.append(", ");
      IntDomainVar var = vars[i];
      sb.append(var.pretty());
    }
    sb.append("})");
    return sb.toString();
  }

    //by default, no information is known
    public int getFineDegree(int idx) {
        return vars.length - 1;
    }
}
