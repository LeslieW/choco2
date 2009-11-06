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

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 2 juil. 2008
 * Since : Choco 2.0.0
 *
 */
public abstract class MultipleVariables extends AbstractVariable {

    protected ArrayList<Variable> variables;
    /**
     * Indicate wether or not the MultipleVariable shoudl be stored as an object
     * (for example to be accessible by the Solver as an entire object)
     */
    protected boolean stored = false;
    
    protected MultipleVariables() {
    	super(VariableType.MULTIPLE_VARIABLES);
    	this.variables = new ArrayList<Variable>();
    }

    protected MultipleVariables(int initialCapacity) {
    	super(VariableType.MULTIPLE_VARIABLES);
    	this.variables = new ArrayList<Variable>(initialCapacity);
    }

    public void addConstraint(Constraint c) {
        for (Variable variable : variables) {
            variable.addConstraint(c);
        }
    }

    public void removeConstraint(Constraint c) {
        for (Variable variable : variables) {
            variable.removeConstraint(c);
        }
    }

    @Deprecated
    public int getNbConstraint() {
        int val = 0;
        for (Variable variable : variables) {
            val += variable.getNbConstraint();
        }
        return val;
    }

    public int getNbConstraint(Model m) {
        int val = 0;
        for (Variable variable : variables) {
            val += variable.getNbConstraint(m);
        }
        return val;
    }

    @Deprecated
    public Iterator<Constraint> getConstraintIterator(){
        return new Iterator<Constraint>() {
            int numVar = 0;

            public boolean hasNext() {
                while (numVar < variables.size() && !variables.get(numVar).getConstraintIterator().hasNext()) {
                    numVar++;
                }
                return numVar<variables.size() && variables.get(numVar).getConstraintIterator().hasNext();
            }

            public Constraint next() {
                return variables.get(numVar).getConstraintIterator().next();
            }

            public void remove() {
                variables.get(numVar).getConstraintIterator().remove();
            }
        };
    }

    public Iterator<Constraint> getConstraintIterator(final Model m){
        return new Iterator<Constraint>() {
            int numVar = 0;

            public boolean hasNext() {
                while (numVar < variables.size() && !variables.get(numVar).getConstraintIterator(m).hasNext()) {
                    numVar++;
                }
                return numVar<variables.size() && variables.get(numVar).getConstraintIterator(m).hasNext();
            }

            public Constraint next() {
                return variables.get(numVar).getConstraintIterator(m).next();
            }

            public void remove() {
                variables.get(numVar).getConstraintIterator(m).remove();
            }
        };
    }

    @Deprecated
    public int getNbTotConstraint() {
        int val = 0;
        for (Variable variable : variables) {
            val += variable.getNbConstraint();
        }
        return val;
    }

    public int getNbTotConstraint(Model m) {
        int val = 0;
        for (Variable variable : variables) {
            val += variable.getNbConstraint(m);
        }
        return val;
    }

    public void addVariable(Variable var){
        this.variables.add(var);
    }

    public void addVariables(ArrayList<Variable> var){
        this.variables.addAll(var);
    }

    public void removeVariable(Variable var){
        this.variables.remove(var);
    }

    public void removeVariables(ArrayList<Variable> var){
        this.variables.removeAll(var);
    }

    public Variable getVariable(int i){
        return variables.get(i);
    }

    public Variable[] getVariables(){
        Variable[] tabVar = new Variable[variables.size()];
        Arrays.copyOf(variables.toArray(tabVar), variables.size());
        return tabVar;
    }

    public Iterator<Variable> getVariableIterator(){
        return variables.iterator();
    }

    public int getNbVariables(){
        return variables.size();
    }

    public boolean isStored() {
        return stored;
    }

    /**
     * Extract first level sub-variables of a variable
     * and return an array of non redundant sub-variable.
     * In simple variable case, return a an array
     * with just one element.
     * Really usefull when expression variables.
     * @return a hashset of every sub variables contained in the Variable.
     */
     public Variable[] extractVariables() {
        if(listVars==null){
            listVars = ArrayUtils.getNonRedundantObjects(Variable.class, ArrayUtils.toArray(Variable.class, variables));
        }
        return listVars;
    }


    /**
     * Set the class manager
     *
     * @param properties properties
     */
    @Override
    public void findManager(Properties properties) {
        for (Variable variable : variables) {
            variable.findManager(properties);
        }
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
