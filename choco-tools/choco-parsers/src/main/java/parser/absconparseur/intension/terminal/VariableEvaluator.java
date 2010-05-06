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
package parser.absconparseur.intension.terminal;

import parser.absconparseur.intension.EvaluationManager;
import parser.absconparseur.intension.types.IntegerType;

public class VariableEvaluator extends TerminalEvaluator implements IntegerType {

	private final EvaluationManager manager;

	private final int position;

	public int getPosition() {
		return position;
	}

	public VariableEvaluator(EvaluationManager manager, int position) {
		this.manager = manager;
		this.position = position;
	}

	public void evaluate() {
		stack[++top] = manager.getCurentValueOf(position);
	}

}
