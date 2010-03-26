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

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class IntegerVariable extends IntegerExpressionVariable {

	protected int[] values;
		
	protected IntegerVariable(VariableType variableType, int[] parameters,
			boolean enableOption, IConstraintList constraints) {
		super(variableType, parameters, enableOption, constraints);
		if(parameters.length>0) {
			this.lowB = parameters[0];
			this.uppB = parameters[parameters.length-1];
		}
		setVariables(this);
	}

	
	public IntegerVariable(String name, int binf, int bsup) {
		//noinspection NullArgumentToVariableArgMethod
		this(VariableType.INTEGER, new int[]{binf, bsup}, true, new ConstraintsDataStructure());
		this.setName(name);
	}

	public IntegerVariable(String name, int[] values) {
		//noinspection NullArgumentToVariableArgMethod
		this(VariableType.INTEGER, values, true, new ConstraintsDataStructure());
		this.values = values;
		this.setName(name);
	}

	public int[] getValues() {
		return values;
	}

	public int getDomainSize() {
		if (values != null) {
			return values.length;
		} else {
			return getUppB() - getLowB() + 1;
		}
	}

	public boolean canBeEqualTo(int v) {
		if (values != null) {
			for (int value : values) {
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
		return name+" ["+getLowB()+", "+getUppB()+"]";
	}

	

	protected IntDomainIterator _cachedIterator = null;

	public DisposableIntIterator getDomainIterator() {
		IntDomainIterator iter = _cachedIterator;
		if (iter != null && iter.isReusable()) {
			iter.init();
			return iter;
		}
		_cachedIterator = new IntDomainIterator(this);
		return _cachedIterator;
	}


	protected static class IntDomainIterator extends DisposableIntIterator {
		public int idx;
		public int currentVal;
		public IntegerVariable v;

		public IntDomainIterator(IntegerVariable v) {
			this.v = v;
			init();
		}

		@Override
		public void init() {
			super.init();
			idx = 0;
			currentVal = v.getLowB();
		}

		public boolean hasNext() {
			if (v.getValues() == null) {
				return currentVal <= v.getUppB();
			} else {
				return idx < v.getValues().length;
			}
		}

		public int next() {
			if (v.getValues() == null)
				return currentVal++;
			else return v.getValues()[idx++];
		}
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

	 public void removeVal(int remvalue){
		 if(this.canBeEqualTo(remvalue)){
			 int size;
			 int[] vals;
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
				 for (int value : values) {
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
	 public int[] enumVal(){
		 if(values == null){
			 int[] val = new int[getUppB()-getLowB()+1];
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
