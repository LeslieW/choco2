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
package choco.kernel.model.variables.real;

import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;

import java.util.Iterator;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class RealVariable extends RealExpressionVariable {

	public RealVariable(String name, VariableType type, double lowB, double uppB) {
        //noinspection NullArgumentToVariableArgMethod
        super(new double[]{lowB, uppB}, Operator.NONE, type, null);
		this.name = name;
		this.setLowB(lowB);
		this.setUppB(uppB);
	}


	public RealVariable(String name, double lowB, double uppB) {
		this(name, VariableType.REAL, lowB, uppB);
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
    public void addConstraint(Constraint c) {
        constraints.add(c);
    }

    @Override
    public void removeConstraint(Constraint c) {
        constraints.remove(c);
    }

    @Override
    @Deprecated
    public Iterator<Constraint> getConstraintIterator() {
        return constraints.iterator();
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

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration.
             * @throws java.util.NoSuchElementException
             *          iteration has no more elements.
             */
            @Override
            public Constraint next() {
                return c;
            }

            /**
             * Removes from the underlying collection the last element returned by the
             * iterator (optional operation).  This method can be called only once per
             * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
             * the underlying collection is modified while the iteration is in
             * progress in any way other than by calling this method.
             *
             * @throws UnsupportedOperationException if the <tt>remove</tt>
             *                                       operation is not supported by this Iterator.
             * @throws IllegalStateException         if the <tt>next</tt> method has not
             *                                       yet been called, or the <tt>remove</tt> method has already
             *                                       been called after the last call to the <tt>next</tt>
             *                                       method.
             */
            @Override
            public void remove() {
                it.remove();
            }
    };
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
     public Variable[] extractVariables() {
        if(listVars==null){
            listVars = new Variable[]{this};
        }
        return listVars;
    }
}
