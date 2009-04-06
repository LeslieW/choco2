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
import gnu.trove.TIntArrayList;
import gnu.trove.TIntIntHashMap;

import java.util.HashSet;
import java.util.Iterator;


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
    protected TIntArrayList modelIndexes;
    private TIntIntHashMap indexes;

	public AbstractVariable(VariableType type) {
		this.type = type;
        modelIndexes = new TIntArrayList(1);
        indexes = new TIntIntHashMap();
	}

	public HashSet<String> getOptions(){
		return options;
	}

    public void addOption(String opts) {
		if (opts != null && !"".equals(opts)) {
			String[] optionsStrings = opts.split(" ");
			for (int j = 0; j < optionsStrings.length; j++) {
				String optionsString = optionsStrings[j];
				options.add(optionsString);
			}
		}
	}

    public void addOptions(String[] options) {
		for(int o = 0; o < options.length; o++){
            this.addOption(options[o]);
        }
	}


    public void addOptions(HashSet<String> tOptions){
        if(tOptions != null){
            Iterator<String> it = tOptions.iterator();
            while(it.hasNext()){
                this.addOption(it.next());
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
		return HashCoding.hashCodeMe(new Object[]{indexes});
	}

    /**
     * Unique index of an object in the master object
     * (Different from hashCode, can change from one execution to another one)
     *
     * @return
     */
    @Override
    public int getIndexIn(int masterIndex) {
        if(indexes.containsKey(masterIndex)){
            return indexes.get(masterIndex);
        }
        return -1;
    }

    /**
     * Attribute the value of the index
     *
     * @param ind
     */
    @Override
    public void setIndexIn(int masterInd, int ind) {
        indexes.put(masterInd, ind);
    }

    /**
     * Return wether a variable has been added to a model
     *
     * @param modelIndex
     * @return
     */
    @Override
    public Boolean alreadyIn(int modelIndex) {
        return modelIndexes.contains(modelIndex);
    }

    /**
     * Record the adition of the variable to the model
     *
     * @param modelIndex
     */
    @Override
    public void addModelIndex(int modelIndex) {
        if(!modelIndexes.contains(modelIndex)){
            modelIndexes.add(modelIndex);
        }
    }

    /**
     * Remove the adition of the variable to the model
     *
     * @param modelIndex
     */
    @Override
    public void remModelIndex(int modelIndex) {
        int ind = modelIndexes.indexOf(modelIndex);
        if(ind > -1){
            modelIndexes.remove(ind);
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
