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
package choco.kernel.model.variables.set;


import choco.kernel.model.IConstraintList;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class SetVariable extends SetExpressionVariable{

	protected int[] values;

	protected IntegerVariable card;


	protected SetVariable(VariableType variableType, boolean enableOption,
			int[] parameter, IConstraintList constraints,IntegerVariable card) {
		super(variableType, enableOption, new Object[]{parameter, card}, constraints);
		this.card=card;
		if(parameter.length>0){
			this.lowB = parameter[0];
			this.uppB = parameter[parameter.length - 1];
		}
		setVariables(this);
	}

	
	public SetVariable(String name, int lowB, int uppB, IntegerVariable card) {
		this(VariableType.SET, true, new int[]{lowB, uppB}, new ConstraintsDataStructure(), card);
		this.setName(name);
	}

	public SetVariable(String name, int[] values, IntegerVariable card) {
		//noinspection NullArgumentToVariableArgMethod
		this(VariableType.SET, true, values, new ConstraintsDataStructure(), card);
		this.setName(name);
		this.values = values;
	}

	/**
	 * @return the card
	 */
	public final IntegerVariable getCard() {
		return card;
	}

	/**
	 * @param card the card to set
	 */
	public final void setCard(IntegerVariable card) {
		this.card = card;
	}

	public int[] getValues() {
		return values;
	}

	/**
	 * pretty printing of the object. This String is not constant and may depend on the context.
	 *
	 * @return a readable string representation of the object
	 */
	@Override
	public String pretty() {
		return name+" ["+getLowB()+", "+getUppB()+"]";
	}

	@Override
	public void addOption(String options) {
		super.addOption(options);
		this.card.addOption(options);
	}

	/**
	 * Extract first level sub-variables of a variable
	 * and return an array of non redundant sub-variable.
	 * In simple variable case, return a an array
	 * with just one element.
	 * Really usefull when expression variables.
	 * @return a hashset of every sub variables contained in the Variable.
	 */
	@Override
	public Variable[] doExtractVariables() {
		return getVariables();
	}
}
