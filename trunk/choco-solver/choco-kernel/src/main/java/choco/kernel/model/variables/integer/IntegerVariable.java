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
package choco.kernel.model.variables.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.model.IConstraintList;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.iterators.IVIterator;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class IntegerVariable extends IntegerExpressionVariable {

	protected int[] values;
		
	protected IntegerVariable(final VariableType variableType, final int[] parameters,
			final boolean enableOption, final IConstraintList constraints) {
		super(variableType, parameters, enableOption, constraints);
		if(parameters.length>0) {
			this.lowB = parameters[0];
			this.uppB = parameters[parameters.length-1];
		}
		setVariables(this);
	}

	
	public IntegerVariable(final String name, final int binf, final int bsup) {
		//noinspection NullArgumentToVariableArgMethod
		this(VariableType.INTEGER, new int[]{binf, bsup}, true, new ConstraintsDataStructure());
		this.setName(name);
	}

	public IntegerVariable(final String name, final int[] theValues) {
		//noinspection NullArgumentToVariableArgMethod
		this(VariableType.INTEGER, theValues, true, new ConstraintsDataStructure());
		this.values = theValues;
		this.setName(name);
	}

	public final int[] getValues() {
		return values;
	}

	public final int getDomainSize() {
		if (values != null) {
			return values.length;
		} else {
			return getUppB() - getLowB() + 1;
		}
	}

	public final boolean canBeEqualTo(final int v) {
		if (values != null) {
			for (final int value : values) {
				if (value == v) {
					return true;
				}
			}
			return false;
		} else {
			return v >= getLowB() && v <= getUppB();
		}
	}


	public final boolean isBoolean(){
		return (getLowB()==0 && ( getUppB() == 0 || getUppB()==1) )||
		(getLowB()==1 && getUppB()==1);
	}

	public final boolean isConstant(){
		return getLowB() == getUppB();
	}

	/**
	 * pretty printing of the object. This String is not constant and may depend on the context.
	 *
	 * @return a readable string representation of the object
	 */
	@Override
	public String pretty() {
		return name+" ["+getLowB()+", "+getUppB()+ ']';
	}

	public final DisposableIntIterator getDomainIterator() {
		return IVIterator.getIterator(lowB, uppB, values);
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

	 //    public IntegerExpressionVariable[] getVariables() {
	 //        return new IntegerVariable[]{IntegerVariable.this};
	 //    }

	 public final void removeVal(final int remvalue){
		 if(this.canBeEqualTo(remvalue)){
			 final int size;
			 final int[] vals;
			 if(values == null){
				 size = this.getUppB() - this.getLowB();
				 vals = new int[size];
				 int idx  =0;
				 int val = this.getLowB();
				 while(idx < size){
					 if(val!=remvalue){
						 vals[idx++] = val;
					 }
					 val++;
				 }
			 }else{
				 size = values.length-1;
				 vals = new int[size];
				 int idx = 0;
				 for (final int value : values) {
					 if (value != remvalue) {
						 vals[idx++] = value;
					 }
				 }
			 }
			 this.values = vals;
			 this.setLowB(values[0]);
			 this.setUppB(values[size-1]);
		 }
	 }


	 /**
	  * Return the enumerated values of a domain
	  * @return the enumerated values of a domain
	  */
	 public final int[] enumVal(){
		 if(values == null){
			 final int[] val = new int[getUppB()-getLowB()+1];
			 for(int o = 0; o < val.length; o++){
				 val[o] = getLowB()+o;
			 }
			 return val;
		 }else if(values.length==2 && values[0]==values[1]){
			 return new int[]{values[0]};
		 }
		 return values;
	 }

}
