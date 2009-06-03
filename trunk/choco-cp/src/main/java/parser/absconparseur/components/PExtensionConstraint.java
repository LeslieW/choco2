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
package parser.absconparseur.components;

import parser.chocogen.ModelConstraintFactory;


public class PExtensionConstraint extends PConstraint {

	private PRelation relation;

	/**
	 * The Relation might have been identified as a
	 * known intensional constraint such as >, =, < , >=
	 */
	protected ModelConstraintFactory.ConstExp intensionCts;

	public ModelConstraintFactory.ConstExp getIntensionCts() {
		return intensionCts;
	}

	public void setIntensionCts(ModelConstraintFactory.ConstExp intensionCts) {
		this.intensionCts = intensionCts;
	}
	
	public PRelation getRelation() {
		return relation;
	}

	public PExtensionConstraint(String name, PVariable[] scope, PRelation relation) {
		super(name, scope);
		this.relation = relation;
	}

	public int getMaximalCost() {
		return relation.getMaximalCost();
	}
	
	public long computeCostOf(int[] tuple) {
		return relation.computeCostOf(tuple);
	}

	public String toString() {
		return super.toString() + ", and associated relation " + relation.getName();
	}
}
