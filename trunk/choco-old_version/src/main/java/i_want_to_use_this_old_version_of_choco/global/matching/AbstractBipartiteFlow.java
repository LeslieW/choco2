// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.global.matching;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateBool;
import i_want_to_use_this_old_version_of_choco.mem.IStateIntVector;

import java.util.logging.Level;

/**
 * A general assignment constraint with constraints on the flow bounds
 */

public abstract class AbstractBipartiteFlow extends AbstractBipartiteGraph {
  protected int[] minFlow;         // flow bounds
  protected int[] maxFlow;
  protected IStateIntVector flow;  // flow on the edges from v2 to the sink
  protected boolean compatibleFlow;
  protected IStateBool compatibleSupport;

  /**
   * Constructor for AbstractBipartiteFlow
   *
   * @param vars    nbLeft domain variables
   * @param nbLeft  number of domain variables to assign
   * @param nbRight number of values for assignment
   */
  public AbstractBipartiteFlow(IntDomainVar[] vars, int nbLeft, int nbRight) {
    super(vars, nbLeft, nbRight);
    initAbstractBipartiteFlow();
  }

  /**
   * Builds a clone of this object.
   *
   * @return
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException {
    AbstractBipartiteFlow newc = (AbstractBipartiteFlow) super.clone();
    newc.initAbstractBipartiteFlow();
    return newc;
  }

  protected void initAbstractBipartiteFlow() {
    this.flow = this.getProblem().getEnvironment().makeIntVector(this.nbRightVertices, 0);
    this.minFlow = new int[this.nbRightVertices];
    this.maxFlow = new int[this.nbRightVertices];
    this.left2rightArc = new int[this.nbLeftVertices + 1];
    this.queue = new IntQueue(this.nbVertices);
    this.compatibleSupport = this.getProblem().getEnvironment().makeBool(true);
  }

  protected int getMinFlow(int j) {
    return minFlow[j];
  }

  protected int getMaxFlow(int j) {
    return maxFlow[j];
  }

  /**
   * match the ith variable to value j
   *
   * @param i the variable to match
   * @param j the value to assign
   */
  public void setMatch(int i, int j) {
    assert (0 <= i && i < nbLeftVertices && 0 <= j && j < nbRightVertices);
    int j0 = this.refMatch.get(i);
    if (j0 != j) {
      if (j0 >= 0) {
        // i was already assign to j0, remove it!
        this.refMatch.set(i, -1);
        this.decreaseMatchingSize(j0);
      }
      // check if new assignment is compatible with capacity of value j
      if ((this.flow.get(j) < this.getMaxFlow(j))) {
        this.refMatch.set(i, j);
        this.increaseMatchingSize(j);
      }
    }
  }

  /**
   * remove the assignment of j to the ith variable
   *
   * @param i the variable to unmatch
   * @param j the value to remove
   */
  public void deleteMatch(int i, int j) {
    assert (0 <= i && i < nbLeftVertices && 0 <= j && j < nbRightVertices);
    if (j == this.refMatch.get(i)) {
      this.refMatch.set(i, -1);
      this.decreaseMatchingSize(j);
    }
  }


  /**
   * Assignment of j to the ith variable
   *
   * @param i the variable to assign
   * @param j the value
   */
  public void putRefMatch(int i, int j) {
    this.refMatch.set(i, j);
  }

  /**
   * check unassignement
   *
   * @param j the jth value
   * @return true if a variable assigned to j could be unassigned
   */
  public boolean mayDiminishFlowFromSource(int j) {
    return this.flow.get(j) > this.getMinFlow(j);
  }

  /**
   * check assignement
   *
   * @param j the jth value
   * @return true if a variable could be assigned to j
   */
  public boolean mayGrowFlowFromSource(int j) {
    return this.flow.get(j) < this.getMaxFlow(j);
  }

  /**
   * check if j should be assigned to other variables
   *
   * @param j the jth value
   * @return true if j has not been assigned to enough variables
   */
  public boolean mustGrowFlowFromSource(int j) {
    return this.flow.get(j) < this.getMinFlow(j);
  }

  /**
   * updates the matching size when one more left vertex is matched with j
   *
   * @param j indice of the assigned value
   */
  public void increaseMatchingSize(int j) {
    this.matchingSize.add(1);
    this.flow.set(j, this.flow.get(j) + 1);
    // We must check if this is still possible ...
    int delta = flow.get(j) - this.getMaxFlow(j);
    if (delta > 0) {
      this.compatibleSupport.set(false);
    }
  }

  /**
   * updates the matching size when the matching is rebuilt
   *
   * @param j indice of the removed assignement
   */
  public void decreaseMatchingSize(int j) {
    this.matchingSize.add(-1);
    this.flow.set(j, this.flow.get(j) - 1);
    // We must check if this is still possible ...
    int delta = this.getMinFlow(j) - flow.get(j);
    if (delta > 0) {
      this.compatibleSupport.set(false);
    }
  }

  /**
   * Search for an augmenting path
   *
   * @return
   */
  public int findAlternatingPath() {
    if (logger.isLoggable(Level.INFO))
      logger.info("Search for an augmenting path to grow matching above " + this.matchingSize + " nodes.");
    int eopath = -1;
    int n = this.nbLeftVertices;
    int m = this.nbRightVertices;
    this.queue.init();
    for (int j = 0; j < this.nbRightVertices; j++) {
      if (this.mustGrowFlowFromSource(j)) this.queue.push(j + n);
    }
    if (this.queue.getSize() == 0) {
      this.compatibleFlow = true;
      for (int j = 0; j < this.nbRightVertices; j++) {
        if (this.mayGrowFlowFromSource(j)) this.queue.push(j + n);
      }
    } else
      this.compatibleFlow = false;
    while (this.queue.getSize() > 0) {
      int x = this.queue.pop();
      if (logger.isLoggable(Level.FINE)) logger.fine("FIFO: pop " + x);
      if (x >= n && x < m + n) {
        x -= n;
        boolean shouldBreak = false;
        int[] yy = this.mayInverseMatch(x);
        for (int i = 0; i < yy.length; i++) { // For each value y in mayInverseMatch(x)
          int y = yy[i];
          if (this.mayGrowFlowBetween(x, y) && !this.queue.onceInQueue(y)) {
            if (logger.isLoggable(Level.FINE)) logger.fine(y + "." + x + "[vs." + this.match(y) + "]");
            this.left2rightArc[y] = x;
            if (this.mayGrowFlowToSink(y)) {
              eopath = y;
              shouldBreak = true;
              break;
            } else {
              this.queue.push(y);
            }
          }
        }

        // <grt> Added for exception when making a flow compatible ....
        if (!compatibleFlow && this.mayDiminishFlowFromSource(x) && !this.queue.onceInQueue(n + m)) {
          this.left2rightArc[n] = x;
          this.queue.push(n + m);
        }
        if (shouldBreak) break;
      } else if (x < n) {
        // assert (! this.mayGrowFlowToSink(x))
        int y = this.match(x);
        // assert (y >= 0)
        // assert (this.mayDiminishFlowBetween(y,x))
        if (!this.queue.onceInQueue(y + n)) {
          if (logger.isLoggable(Level.FINE)) logger.fine(x + "#" + y);
          this.right2leftArc[y] = x;
          this.queue.push(y + n);
        }
      } else if (!compatibleFlow) {
        for (int j = 0; j < this.nbRightVertices; j++) {
          if (this.mayGrowFlowFromSource(j) && !this.queue.onceInQueue(j + n)) {
            this.right2leftArc[j] = n;
            this.queue.push(j + n);
          }
        }
      }
    }
    if (logger.isLoggable(Level.INFO)) logger.info("Found an alternating path ending in " + eopath + " (-1 if none).");
    return eopath;
  }

  /**
   * Augment flow on the current matching
   *
   * @param x left extremity of one of the matching arc
   */
  public void augment(int x) {
    int y = this.left2rightArc[x];
    // TODO not in ice claire
    if (this.compatibleFlow) {
      while (!this.mayGrowFlowFromSource(y)) {
        if (logger.isLoggable(Level.FINE)) logger.fine("Add " + x + "." + y);
        this.putRefMatch(x, y);
        x = this.right2leftArc[y];
        if (logger.isLoggable(Level.FINE)) logger.fine("Rem " + x + "." + y);
        //assert (this.match(x) == y);
        y = this.left2rightArc[x];
        //assert (y >= 0);
      }
    } else {
      int n = this.nbLeftVertices;
      int m = this.nbRightVertices;
      while (!this.mustGrowFlowFromSource(y)) {
        if (logger.isLoggable(Level.FINE)) logger.fine("Add " + x + "." + y);
        this.putRefMatch(x, y);
        x = this.right2leftArc[y];
        if (x == n) {
          // The path go through the source vertex...
          this.increaseMatchingSize(y);
          y = this.left2rightArc[x];
          this.decreaseMatchingSize(y);
          x = this.right2leftArc[y];
        }
        if (logger.isLoggable(Level.FINE)) logger.fine("Rem " + x + "." + y);
        //assert (this.match(x) == y);
        y = this.left2rightArc[x];
        //assert (y >= 0);
      }
    }
    if (logger.isLoggable(Level.FINE)) logger.fine("[Matching] Add " + x + "." + y);
    this.putRefMatch(x, y);
    this.increaseMatchingSize(y);

    if (logger.isLoggable(Level.FINE)) {
      for (int i = 0; i < this.nbRightVertices; i++) {
        logger.fine("Flow between " + i + " and source : " + this.flow.get(i));
      }
    }
  }

  protected void removeUselessEdges() throws ContradictionException {
    if (!compatibleSupport.get()) {
      this.matchingSize.set(0);
      for (int i = 0; i < this.nbLeftVertices; i++) {
        this.refMatch.set(i, -1);
      }
      for (int j = 0; j < this.nbRightVertices; j++) {
        this.flow.set(j, 0);
      }
      this.augmentFlow();
    }
    super.removeUselessEdges();
  }
}
