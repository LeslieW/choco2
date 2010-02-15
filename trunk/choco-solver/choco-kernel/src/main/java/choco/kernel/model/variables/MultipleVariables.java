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

import java.util.Properties;

import choco.kernel.model.constraints.ExpressionManager;

/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 2 juil. 2008
 * Since : Choco 2.0.0
 *
 */
public abstract class MultipleVariables extends AbstractVariable {

	/**
     * Indicate wether or not the MultipleVariable shoudl be stored as an object
     * (for example to be accessible by the Solver as an entire object)
     */
    protected final boolean enableStorage;
    
    protected MultipleVariables() {
    	this(false,false);
    }
    
    protected MultipleVariables(boolean enableOptions, boolean enableStorage) {
    	super(VariableType.MULTIPLE_VARIABLES, enableOptions);
    	this.enableStorage= enableStorage;
    }
    
    public MultipleVariables(boolean enableOptions, boolean enableStorage, Variable... variables) {
    	super(VariableType.MULTIPLE_VARIABLES, variables, enableOptions);
    	this.enableStorage= enableStorage;
    }

  
    public boolean isStored() {
        return enableStorage;
    }


    /**
     * Set the class manager
     *
     * @param properties properties
     */
    @Override
    public void findManager(Properties properties) {
    	final int n = getNbVars();
    	for (int i = 0; i < n; i++) {
			getVariable(i).findManager(properties);
		}
    }
    
    

    @Override
	public ExpressionManager getExpressionManager() {
    	return null;
	}

	@Override
	public VariableManager<?> getVariableManager() {
		return null;
	}

	/**
     * Check wether to Multiple variables are equivalents
     * @param mv
     * @return
     */
    public boolean isEquivalentTo(MultipleVariables mv) {
        return false;
    }

	
	    
    
}
