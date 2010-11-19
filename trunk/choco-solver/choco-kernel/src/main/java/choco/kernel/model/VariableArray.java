package choco.kernel.model;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;

import java.io.Serializable;
import java.util.Iterator;

public class VariableArray implements IVariableArray, Serializable {
	
	protected final static Constraint[] NO_CONSTRAINTS = {};
	
	private Variable[] variables;
	private Variable[] extractedVariables;

	
	public VariableArray() {
		super();
	}
	
	public VariableArray(final Variable[] variables) {
		super();
		this.variables = variables;
	}

	@Override
	public final int getNbVars() {
		return variables.length;
	}

	@Override
	public final Variable getVariable(final int i) {
		return variables[i];
	}

	@Override
	public final DisposableIterator<Variable> getVariableIterator() {
		return IteratorUtils.iterator(extractVariables());
	}

	@Override
	public final Variable[] getVariables() {
		return variables;
	}

	protected final void setVariables(final Variable variable) {
		this.variables = new Variable[]{variable};
		extractedVariables = null;
	}
	
	protected final void setVariables(final Variable[] variables) {
		this.variables = variables;
		extractedVariables = null;
	}

    public final void replaceBy(final Variable outVar, final Variable inVar){
        final long idx = outVar.getIndex();
        for(int i = 0; i < variables.length; i++){
            if(variables[i].getIndex() == idx){
                variables[i] = inVar;
            }
        }
    }

	protected Variable[] doExtractVariables() {
		return ArrayUtils.getNonRedundantObjects(Variable.class, variables);
	}
	
	@Override
	public final Variable[] extractVariables() {
		if(extractedVariables == null){
			extractedVariables = doExtractVariables();
		}
		return extractedVariables;
	}

    protected final class VConstraintsDataStructure implements IConstraintList {

		

		public VConstraintsDataStructure() {
			super();
		}

		@Override
		public void _addConstraint(final Constraint c) {
			for(final Variable v : variables){
				v._addConstraint(c);
			}
		}

		@Override
		public void _removeConstraint(final Constraint c) {
			for(final Variable v : variables){
				v._removeConstraint(c);
			}			
		}

        @Override
        public boolean _contains(final Constraint c) {
            for(final Variable v : variables){
				if(!v._contains(c)){
                    return false;
                }
		    }
            return true;
        }

        @Override
		public void removeConstraints() {
			for(final Variable v : variables){
				v.removeConstraints();
			}
		}

		@Override
		public Constraint getConstraint(final int i) {
			return null;

		}

		@Override
		public Iterator<Constraint> getConstraintIterator(final Model m) {
			return new Iterator<Constraint>(){
				int n = 0;
				Iterator<Constraint> it = (variables.length > 0? variables[n].getConstraintIterator(m):null);
				Constraint c = null;

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
							}else
								if(n < variables.length){
									n++;
									if (n < variables.length) {
										it = variables[n].getConstraintIterator(m);
									}
								}else{
									return false;
								}
					}
				}

				public Constraint next() {
					return c;
				}

				public void remove() {
					it.remove();
				}
			};
		}

		@Override
		public Constraint[] getConstraints() {
			return NO_CONSTRAINTS;
		}

		@Override
		public int getNbConstraint(final Model m) {
			int sum = 0;
			for(final Variable v : variables){
				sum += v.getNbConstraint(m);
			}	
			return sum;
		}
	}

	@Override
	public String pretty() {
		return StringUtils.pretty(variables);
	}
	
	

}