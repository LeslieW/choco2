// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.global.matching;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * very simple version of the cardinality constraint where the values
 * the set of values whose occurrences are counted in the interval (minValue .. maxValue)
 */
public class GlobalCardinality extends AbstractBipartiteFlow implements IntConstraint {
  private Logger logger = Logger.getLogger("i_want_to_use_this_old_version_of_choco.prop.const");
  
  /**
   * Constructor, Global cardinality constraint API
   * note : maxVal - minVal + 1 = valueMinOccurence.length = valueMaxOccurence.length
   *
   * @param vars     the variable list
   * @param minValue smallest value that could be assigned to variable
   * @param maxValue greatest value that could be assigned to variable
   * @param low      minimum for each value
   * @param up       maximum occurences for each value
   */
  public GlobalCardinality(IntDomainVar[] vars, int minValue, int maxValue, int[] low, int[] up) {
    super(vars, vars.length, maxValue - minValue + 1);
    assert(low.length == maxValue - minValue + 1);
    assert(up.length == maxValue - minValue + 1);
    this.minValue = minValue;
    this.maxValue = maxValue;
    for (int i = 0; i < minFlow.length; i++) {
      minFlow[i] = low[i];
      maxFlow[i] = up[i];
    }
  }

  /**
   * Constructor, Global cardinality constraint API, short cut when smallest value equals 0
   * note : maxVal - minVal + 1 = low.length = up.length
   *
   * @param vars the variable list
   * @param low  minimum for each value
   * @param up   maximum occurences for each value
   */
  public GlobalCardinality(IntDomainVar[] vars, int[] low, int[] up) {
    super(vars, vars.length, low.length);
    assert(low.length == up.length);
    this.minValue = 1;
    this.maxValue = low.length;
    for (int i = 0; i < minFlow.length; i++) {
      minFlow[i] = low[i];
      maxFlow[i] = up[i];
    }
  }

  public Object clone() throws CloneNotSupportedException {
    GlobalCardinality newc = (GlobalCardinality) super.clone();
    System.arraycopy(this.minFlow, 0, newc.minFlow, 0, this.minFlow.length);
    System.arraycopy(this.maxFlow, 0, newc.maxFlow, 0, this.maxFlow.length);
    return newc;
  }

  /**
   * implement one of the two main events:
   * when an edge is definitely removed from the bipartite assignment graph
   *
   * @param i the variable to unmatch
   * @param j the value to remove
   * @throws ContradictionException if the removal generates a contradiction
   */
  public void deleteEdgeAndPublish(int i, int j) throws ContradictionException {
    assert(0 <= i && i < nbLeftVertices && 0 <= j && j < nbRightVertices);
    deleteMatch(i, j);
    vars[i].removeVal(j + minValue, cIndices[i]);
  }

  /**
   * implement the other main event:
   * when an edge is definitely set in the bipartite assignment graph
   *
   * @param i the variable to assign
   * @param j the assignement value
   * @throws ContradictionException
   */
  public void setEdgeAndPublish(int i, int j) throws ContradictionException {
    assert(1 <= i && i <= nbLeftVertices && 1 <= j && j <= nbRightVertices);
    setMatch(i, j);
    vars[i].instantiate(j + minValue, cIndices[i]);
  }

  // propagation functions: reacting to events
  /**
   * Implement reaction to edge removal
   *
   * @param idx variable index
   * @param x   value to remove
   * @throws ContradictionException
   */
  public void awakeOnRem(int idx, int x) throws ContradictionException {
    deleteEdgeAndPublish(idx, x - minValue);
    constAwake(false);
  }

  /**
   * Needs to be defined to update the reference matching before redoing the strongly
   * connected components analysis.
   *
   * @param idx variable index
   */
  public void awakeOnVar(int idx) {
    // TODO : use jchoco method for printing constraint when it will be implemented
    if (logger.isLoggable(Level.FINE)) logger.fine("awake " + "GCC" + " on var " + idx);
    IntDomainVar v = vars[idx];
    for (int val = minValue; val <= maxValue; val++) {
      if (!v.canBeInstantiatedTo(val)) {
        if (logger.isLoggable(Level.FINE))
          logger.fine("awakeOnVar: delete from matching " + idx + "-" + (val - minValue));
        deleteMatch(idx, val - minValue);
      }
    }
    if (logger.isLoggable(Level.FINE))
      logger.fine("awakeOnVar: call to delayed propagation " + "GCC");
    constAwake(false);
  }

  /**
   * update the reference matching before redoing the strongly connected components analysis
   * when removing value in the domain of variable idx
   *
   * @param idx variable index
   */
  public void awakeOnInf(int idx) throws ContradictionException {
    for (int j = this.minValue; j < this.vars[idx].getInf(); j++) {      // TODO : verifier modif par rapport Claire..
      //for (int j = 1; j < vars[idx].getInf() ; j++) {
      deleteMatch(idx, j - minValue);
    }
    constAwake(false);
  }

  /**
   * update the reference matching before redoing the strongly connected components analysis
   * when removing value in the domain of variable idx
   *
   * @param idx variable index
   * @throws ContradictionException
   */
  public void awakeOnSup(int idx) throws ContradictionException {
    for (int j = vars[idx].getSup() + 1; j <= maxValue; j++) {
      deleteMatch(idx, j - minValue);
    }
    constAwake(false);
  }

  /**
   * update the reference matching before redoing the strongly connected components analysis
   * when idx is instantiated
   *
   * @param idx variable index
   */
  public void awakeOnInst(int idx) throws ContradictionException {
    //System.out.println(this);
    //System.out.println(this.pretty());
    setMatch(idx, vars[idx].getVal() - minValue);
    constAwake(false);
  }

  /**
   * performing the initial propagation, reduce variables domain to the candidate assign values
   *
   * @throws ContradictionException
   */
  public void awake() throws ContradictionException {
    for (int i = 0; i < nbLeftVertices; i++) {
      vars[i].updateInf(minValue, cIndices[i]);
      vars[i].updateSup(maxValue, cIndices[i]);
    }
    propagate();
  }

  public boolean isSatisfied(int[] tuple) {
    int[] occurrences = new int[this.maxValue - this.minValue + 1];
    for (int i = 0; i < vars.length; i++) {
      occurrences[tuple[i]-this.minValue]++;
    }
    for (int i = 0; i < occurrences.length; i++) {
      int occurrence = occurrences[i];
      if ((this.minFlow[i] > occurrence) || (occurrence > this.maxFlow[i]))
        return false;
    }
    return true;
  }

  public String pretty() {
    StringBuffer buf = new StringBuffer("GCC[" + vars.length + "," + (maxValue - minValue + 1) + "]\n");
    for (int i = 0; i < vars.length; i++) {
      buf.append(vars[i].pretty());
      buf.append("\n");
    }
    return new String(buf);

  }
}
