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
package choco.kernel.model.variables;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import choco.kernel.model.IConstraintList;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.constraints.ExpressionManager;
import choco.kernel.model.constraints.ManagerFactory;

/*
 * User:    charles
 * Date: 8 août 2008
 */
public abstract class ComponentVariable extends AbstractVariable {


	public final static IConstraintList NO_CONSTRAINTS_DS = new NoConstraintDataStructure();
	
	protected final Object parameters;
	protected String variableManager;
	protected String expressionManager;
	protected Operator operator;
	
	/**
	 * For IntegerVariable, RealVariable, SetVariable.
	 */
	protected ComponentVariable(final VariableType variableType, boolean enableOption, final Object parameters,IConstraintList constraints) {
		super(variableType,enableOption,constraints);
		this.parameters = parameters;
		this.operator = Operator.NONE;
	}
	
	/**
	 * For expressions 
	 */
	protected ComponentVariable(final VariableType variableType, final Object parameters, final ComponentVariable... vars) {
		super(variableType, vars, false); //disable options
		this.parameters=parameters;
	}
	
	/**
	 * For expressions 
	 */
	public ComponentVariable(final VariableType variableType, final Operator operator, final Object parameters, final ComponentVariable... vars) {
		this(variableType, parameters, vars);
		this.operator = operator;
	}

	/**
	 * For Expressions 
	 */
	public ComponentVariable(final VariableType variableType, final String operatorManager, final Object parameters, final ComponentVariable... vars) {
		this(variableType, parameters, vars);
		this.expressionManager = operatorManager;
	}


	protected final String getComponentClass() {
		return variableManager;
	}

	protected final String getOperatorClass(){
		if(expressionManager!=null){
			return expressionManager;
		}
		return variableManager;
	}


	public final Object getParameters() {
		return parameters;
	}

	public final Operator getOperator() {
		return operator;
	}

//	public final void setOperator(Operator operator) {
//		this.operator = operator;
//	}

	



	public void findManager(Properties propertiesFile) {
		if (variableManager == null && !type.equals(VariableType.NONE)){
			variableManager = propertiesFile.getProperty(type.property);
		}
		if(expressionManager == null && !operator.equals(Operator.NONE)){
			expressionManager = propertiesFile.getProperty(operator.property);
		}
		if(variableManager == null && expressionManager == null){
			throw new ModelException("Can not find "+type.property+" or "
					+ operator.property+" in application.properties");
		}
	}

	public VariableManager<?> getVariableManager() {
		return ManagerFactory.loadVariableManager(getComponentClass());
	}


	public ExpressionManager getExpressionManager() {
		return ManagerFactory.loadExpressionManager(getOperatorClass());
	}
	
	@Override
	public ConstraintManager<?> getConstraintManager() {
		return ManagerFactory.loadConstraintManager(getOperatorClass());
	}





	protected final static class ConstraintsDataStructure implements IConstraintList {

		List<Constraint> constraints;
		Constraint[] reuseConstraints;

		public ConstraintsDataStructure() {
			super();
			constraints = new LinkedList<Constraint>();
		}

		@Override
		public void _addConstraint(Constraint c) {
			reuseConstraints=null;
			constraints.add(c);
		}

		@Override
		public void _removeConstraint(Constraint c) {
			if(constraints.remove(c)) reuseConstraints=null;			
		}

		@Override
		public void removeConstraints() {
			constraints.clear();
			reuseConstraints=null;

		}

		@Override
		public Constraint getConstraint(int i) {
			return constraints.get(i);

		}

		@Override
		public Iterator<Constraint> getConstraintIterator(final Model m) {
			  return new Iterator<Constraint>(){
		            Constraint c;
		            Iterator<Constraint> it = constraints.iterator();

		            public boolean hasNext() {
		                while(true){
		                    if(it == null){
		                        return false;
		                    }else
		                    if(it.hasNext()){
		                        c = it.next();
		                        if(Boolean.TRUE.equals(m.contains(c))){
		                            return true;
		                        }
		                    }else{
		                        return false;
		                    }
		                }
		            }

		            @Override
		            public Constraint next() {
		                return c;
		            }

		            @Override
		            public void remove() {
		                it.remove();
		            }
		    };
		    
		}

		@Override
		public Constraint[] getConstraints() {
			if(reuseConstraints == null) {
				reuseConstraints = constraints.toArray(new Constraint[constraints.size()]);
			}
			return reuseConstraints;
		}

		@Override
		public int getNbConstraint(Model m) {
			int sum = 0;
			for(Constraint c: constraints){
				if(Boolean.TRUE.equals(m.contains(c))){
					sum++;
				}
			}
			return sum;
		}
	}

	private final static class NoConstraintDataStructure implements IConstraintList {

		@Override
		public void _addConstraint(Constraint c) {}

		@Override
		public void _removeConstraint(Constraint c) {}

		@Override
		public void removeConstraints() {}

		@Override
		public Constraint getConstraint(int i) {
			return null;
		}

		@Override
		public Iterator<Constraint> getConstraintIterator(Model m) {
			return Collections.<Constraint>emptyList().iterator();
		}

		@Override
		public Constraint[] getConstraints() {
			return NO_CONSTRAINTS;
		}

		@Override
		public int getNbConstraint(Model m) {
			return 0;
		}


	}

}
