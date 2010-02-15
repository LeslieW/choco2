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
package choco.kernel.model.constraints;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.ModelException;
import choco.kernel.model.ModelObject;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * @author Arnaud Malapert
 */
public abstract class AbstractConstraint extends ModelObject implements Constraint, Comparable {

	protected ConstraintType type;
	
	//null by default until it has been loaded
	protected String manager;

	public AbstractConstraint(final ConstraintType type, Variable[] variables) {
		super(variables, true);
		this.type = type;
	}

	public AbstractConstraint(final String consMan, Variable[] variables) {
		this(ConstraintType.NONE, variables);
		this.manager = consMan;
	}


	protected void variablesPrettyPrint(final StringBuilder buffer) {
		buffer.append(StringUtils.pretty(this.getVariableIterator()));
	}


	
	@Override
	public String getName() {
		return type == null || type.equals(ConstraintType.NONE) ? manager : type.getName();
	}

	@Override
	public String pretty() {
		final StringBuilder st = new StringBuilder(getName());
		st.append(" ( ");
		variablesPrettyPrint(st);
		st.append(" )");
		return st.toString();
	}

	@Override
	public final ConstraintType getConstraintType() {
		return type;
	}

	@Override
	public final void setType(final ConstraintType type) {
		this.type = type;
	}


	/**
	 * get rid of the constants within the returned scopes !
	 * @return scope of integervariable of the constraint
	 */
	public IntegerVariable[] getIntVariableScope() {
		Iterator<Variable> itvs = getVariableIterator();
		HashSet<IntegerVariable> vs = new HashSet<IntegerVariable>();
		while (itvs.hasNext()) {
			Variable v1 = itvs.next();
			if (v1.getVariableType() == VariableType.INTEGER &&
					!vs.contains(v1)) {
				vs.add((IntegerVariable) v1);
			} else if (v1.getVariableType().equals(VariableType.INTEGER_EXPRESSION)) {
				HashSet<Variable> tmp = extractEveryvariables((IntegerExpressionVariable) v1);
				for (Variable aTmp : tmp) {
					if (aTmp.getVariableType() == VariableType.INTEGER &&
							!vs.contains(aTmp)) {
						vs.add((IntegerVariable) aTmp);
					}
				}
			}
		}
		IntegerVariable[] vars = new IntegerVariable[vs.size()];
		int cpt = 0;
		for (IntegerVariable v : vs) {
			vars[cpt++] = v;

		}
		return vars;
	}

	/**
	 * Extract every sub variables of an IntegerExpressionVariable
	 * @param iev integer expression variable
	 * @return set of variable
	 */
	private HashSet<Variable> extractEveryvariables(IntegerExpressionVariable iev){
		HashSet<Variable> vs = new HashSet<Variable>();
		if(iev.getVariableType().equals(VariableType.INTEGER)){
			if(!vs.contains(iev))vs.add(iev);
		}
		else if(iev.getVariableType().equals(VariableType.INTEGER_EXPRESSION)){
			Variable[] tmp = iev.extractVariables();
			Iterator<Variable> it = IteratorUtils.iterator(tmp);
			while(it.hasNext()){
				vs.addAll(extractEveryvariables((IntegerExpressionVariable)it.next()));
			}
		}
		return vs;
	}


	/**
	 * @return a list of domains accepted by the constraint and sorted
	 *         by order of preference
	 */
	public int[] getFavoriteDomains() {
		return new int[]{IntDomainVar.BITSET,
				IntDomainVar.LINKEDLIST,
				IntDomainVar.BIPARTITELIST,
				IntDomainVar.BINARYTREE,
				IntDomainVar.BOUNDS,
		};
	}


	public ConstraintManager getConstraintManager() {
		return  ManagerFactory.loadConstraintManager(getManager());
	}

	public ExpressionManager getExpressionManager() {
		return ManagerFactory.loadExpressionManager(getManager());
	}

	public final String getManager() {
		return manager;
	}


	public void findManager(Properties propertiesFile) {
		if(manager==null){
			if(type.property == null)throw new ModelException("Empty property, can not read it!");
			manager = propertiesFile.getProperty(type.property);
			if(manager == null) throw new ModelException("No property found for "+type.property+" in application.properties");
		}
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
}
