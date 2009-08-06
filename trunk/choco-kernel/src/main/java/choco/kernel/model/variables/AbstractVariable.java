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

import choco.kernel.common.HashCoding;
import choco.kernel.common.IndexFactory;
import choco.kernel.model.ModelException;

import java.util.Arrays;
import java.util.HashSet;


/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Time: 18:28:35
 * Abstract class for variable with basic methods
 */
public abstract class AbstractVariable implements Variable, Comparable{

	protected VariableType type;
    protected int hashCode;
	protected HashSet<String> options = new HashSet<String>();
    protected Variable[] listVars;
    protected final long indice;
    protected int hook = NO_HOOK; //a utility field
    
	public AbstractVariable(VariableType type) {
		this.type = type;
        indice = IndexFactory.getId();
	}

	public HashSet<String> getOptions(){
		return options;
	}

    public void addOption(String opts) {
		if (opts != null && !"".equals(opts)) {
			String[] optionsStrings = opts.split(" ");
            options.addAll(Arrays.asList(optionsStrings));
		}
	}

    public final void addOptions(String[] options) {
        for (String option : options) {
            this.addOption(option);
        }
	}


    public final void addOptions(HashSet<String> tOptions){
        if(tOptions != null){
            for (String tOption : tOptions) {
                this.addOption(tOption);
            }
        }
    }


    public final VariableType getVariableType() {
		return type;
	}

	public final void setType(VariableType type) {
		this.type = type;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return pretty();
	}

	@Override
	public int hashCode() {
		return HashCoding.hashCodeMe(new Object[]{this});
	}


    /**
     * Unique index
     * (Different from hashCode, can change from one execution to another one)
     *
     * @return the indice of the objet
     */
    @Override
    public final long getIndex() {
        return indice;
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
		if( hook == NO_HOOK) {
			this.hook = hook;
		}else {
			throw new ModelException("The hook of the variable "+this.pretty()+" is already set to "+this.hook);
		}
		
	}
    
    
}
