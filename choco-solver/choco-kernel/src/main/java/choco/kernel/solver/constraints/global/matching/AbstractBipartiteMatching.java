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

package choco.kernel.solver.constraints.global.matching;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A subclass of AbtractBipartiteGraph restricted only to matchings
 * (and not flows).
 */
public abstract class AbstractBipartiteMatching
    extends AbstractBipartiteGraph {
  /**
   * Vector with the reverse matching.
   */
  protected IStateIntVector refInverseMatch;
  // the reverse assignment is stored

  /**
   * Builds a new instance for the specified vars.
   * @param environment
   * @param vars the variables
   * @param nbLeft number of nodes in the first part of the bipartite matching
   * @param nbRight number of nodes in the second part
   */
  public AbstractBipartiteMatching(IEnvironment environment, final IntDomainVar[] vars,
                                   final int nbLeft, final int nbRight) {
    super(environment, vars, nbLeft, nbRight);
    this.refInverseMatch = environment.makeIntVector(this.nbRightVertices, -1);
  }
//
//  /**
//   * Builds a copy of this contraint.
//   * @return a clone of this constraint
//   * @throws CloneNotSupportedException if an error occurs during cloning
//   */
//  public Object clone() throws CloneNotSupportedException {
//    AbstractBipartiteMatching newc = (AbstractBipartiteMatching) super.clone();
//    newc.initAbstractBipartiteMatching();
//    return newc;
//  }

//  /**
//   * Initializes the matching by building the backtrackable vector for the
//   * right vertices values.
//   */
//  public void initAbstractBipartiteMatching() {
//    this.refInverseMatch =
//        this.getSolver().getEnvironment()
//        .makeIntVector(this.nbRightVertices, -1);
//  }

  /**
   * Accessing the left vertex matched to j.
   * @param j the vertex
   * @return the left vertex matched to j
   */
  public int inverseMatch(final int j) {
    return this.refInverseMatch.get(j);
  }

  /**
   * Matching size has been increase by 1.
   * @param j useless here
   */
  public void increaseMatchingSize(final int j) {
    this.matchingSize.set(this.matchingSize.get() + 1);
  }

  /**
   * Matching size has been decrease by 1.
   * @param j useless here
   */
  public void decreaseMatchingSize(final int j) {
    this.matchingSize.set(this.matchingSize.get() - 1);
  }

  /**
   * Removing the arc i-j from the reference matching.
   * @param i the left vertex
   * @param j the right vertex
   */
  public void deleteMatch(final int i, final int j) {
    if (j == this.refMatch.get(i)) {
      this.refMatch.set(i, -1);
      this.refInverseMatch.set(j, -1);
      this.decreaseMatchingSize(j);
    }
  }

  /**
   * Adding the arc i-j in the reference matching without any updates.
   * @param i the left vertex
   * @param j the right vertex
   */
  public void putRefMatch(final int i, final int j) {
    this.refMatch.set(i, j);
    this.refInverseMatch.set(j, i);
  }

  /**
   * Adding the arc i-j in the reference matching.
   * @param i the left vertex
   * @param j the right vertex
   */
  public void setMatch(final int i, final int j) {
    int j0 = this.refMatch.get(i);
    int i0 = this.refInverseMatch.get(j);
    if (j0 != j) {
      // assert (i0 != i);
      if (j0 >= 0) {
        this.refInverseMatch.set(j0, -1);
        this.decreaseMatchingSize(j0);
      }
      if (i0 >= 0) {
        this.refMatch.set(i0, -1);
        this.decreaseMatchingSize(j);
      }
      this.refMatch.set(i, j);
      this.refInverseMatch.set(j, i);
      this.increaseMatchingSize(j);
    }
  }

  /**
   * Checks if the flow can be decreased between source and a vertex.
   * @param j the vertex
   * @return whether the flow from the source to j (a right vertex) 
   * may be decreased
   */
  public boolean mayDiminishFlowFromSource(final int j) {
    return this.refInverseMatch.get(j) != -1;
  }

  /**
   * Checks if the flow can be increased between source and a vertex.
   * @param j the vertex
   * @return whether the flow from the source to j (a right vertex)
   * may be increased
   */
  public boolean mayGrowFlowFromSource(final int j) {
    return this.refInverseMatch.get(j) == -1;
  }

  /**
   * Checks if the flow must be increased between the source and a vertex.
   * @param j the vertex
   * @return whether the flow from the source to j (a right vertex) 
   * must be increased in order to get a maximal
   * (sink/left vertex set saturating) flow
   */
  public boolean mustGrowFlowFromSource(final int j) {
    return false; // TODO in not in claire ice, cf code below
    //    assert(1 <= j & j <= c.nbRightVertices),
    //    inverseMatch(c,j) = 0
  }
}
