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

import java.util.Iterator;
import java.util.Properties;

import choco.kernel.model.IConstraintList;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.ModelObject;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ExpressionManager;


/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Time: 18:28:35
 * Abstract class for variable with basic methods
 */
public abstract class AbstractVariable extends ModelObject implements Variable, Comparable{

	private final static String NO_NAME= "";

	protected final VariableType type;
	protected String name = NO_NAME;
	private final IConstraintList constraints;
	private int hook = NO_HOOK; //utility field

	public AbstractVariable(VariableType type, boolean enableOption, IConstraintList constraints) {
		super(enableOption);
		this.type = type;
		this.constraints = constraints;
	}

	public AbstractVariable(VariableType type, boolean enableOptions) {
		super(enableOptions);
		this.type = type;
		this.constraints = new VConstraintsDataStructure();
	}

	public AbstractVariable(VariableType type, Variable[] variables, boolean enableOptions) {
		super(variables, enableOptions);
		this.type = type;
		this.constraints = new VConstraintsDataStructure();
	}

	protected final void throwConstantException() {
		throw new ModelException("Constant are immutable.");
	}
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}


	public final VariableType getVariableType() {
		return type;
	}

	@Override
	public String pretty() {
		return type.name() + super.pretty();
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return pretty();
	}


	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 * <p/>
	 * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
	 * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
	 * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
	 * <tt>y.compareTo(x)</tt> throws an exception.)
	 * <p/>
	 * <p>The implementor must also ensure that the relation is transitive:
	 * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
	 * <tt>x.compareTo(z)&gt;0</tt>.
	 * <p/>
	 * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
	 * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
	 * all <tt>z</tt>.
	 * <p/>
	 * <p>It is strongly recommended, but <i>not</i> strictly required that
	 * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
	 * class that implements the <tt>Comparable</tt> interface and violates
	 * this condition should clearly indicate this fact.  The recommended
	 * language is "Note: this class has a natural ordering that is
	 * inconsistent with equals."
	 * <p/>
	 * <p>In the foregoing description, the notation
	 * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
	 * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
	 * <tt>0</tt>, or <tt>1</tt> according to whether the value of
	 * <i>expression</i> is negative, zero or positive.
	 *
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 *         is less than, equal to, or greater than the specified object.
	 * @throws ClassCastException if the specified object's type prevents it
	 *                            from being compared to this object.
	 */
	@Override
	public int compareTo(Object o) {
		if(this.equals(o)){
			return 0;
		}else{
			return 1;
		}
	}

	public final void _addConstraint(Constraint c) {
		constraints._addConstraint(c);
	}

	public final void _removeConstraint(Constraint c) {
		constraints._removeConstraint(c);
	}

	@Deprecated
	public Iterator<Constraint> getConstraintIterator() {
		throw new UnsupportedOperationException("deprecated");
//		new Iterator<Constraint>(){
//			int n = 0;
//			Iterator<Constraint> it = (variables.length > 0? variables[n].getConstraintIterator(): null);
//
//			public boolean hasNext() {
//				if (it == null) {
//					return false;
//				}
//				while (n < variables.length && !it.hasNext()) {
//					n++;
//					if (n < variables.length) {
//						it = variables[n].getConstraintIterator();
//					}
//				}
//				return n < variables.length && it.hasNext();
//			}
//
//			public Constraint next() {
//				return it.next();
//			}
//
//			public void remove() {
//				it.remove();
//			}
//		};
	}

	public final Iterator<Constraint> getConstraintIterator(final Model m) {
		return constraints.getConstraintIterator(m);
	}



	public Constraint[] getConstraints() {
		return constraints.getConstraints();
	}

	public final Constraint getConstraint(final int idx) {
		return constraints.getConstraint(idx);
	}

	@Deprecated
	public int getNbConstraint() {
		throw new UnsupportedOperationException("deprecated");
	}

	public int getNbConstraint(Model m) {
		return constraints.getNbConstraint(m);
	}

	@Override
	public void removeConstraints() {
		constraints.removeConstraints();		
	}

	@Override
	public final int getHook() {
		return hook;
	}

	@Override
	public final void resetHook() {
		this.hook = NO_HOOK;		
	}

	@Override
	public final void setHook(int hook) {
		if( this.hook == NO_HOOK) {
			this.hook = hook;
		}else {
			throw new ModelException("The hook of the variable "+this.pretty()+" is already set to "+this.hook);
		}

	}
	
}
