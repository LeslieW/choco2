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
package choco.kernel.model;

import choco.IPretty;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;

import java.util.Iterator;

public interface Model extends IPretty {

    /**
     * Return the index of the Model
     * @return int
     */
    public int getIndex();


    /**
     * Retrieves the index of an IntDomainVar
     * @param c Solver variable
     * @return the indexe of the solver variable in the model.
     * @deprecated
     */
    @Deprecated
    public int getIntVarIndex(IntDomainVar c);

    /**
     * retrieving the total number of constraints over integers
     *
     * @return the total number of constraints over integers in the model
     */
    public int getNbConstraints();

    /**
     * <i>Network management:</i>
     * Retrieve a constraint by its index.
     *
     * @param i index of the constraint in the model
     * @return Constraint
     */
    public Constraint getConstraint(int i);

    /**
     * Return an iterator over the integer constraints of the model
     * @return an iterator over the integer constraints of the model
     * @deprecated
     * @see Model#getConstraintIterator()
     */
    public Iterator<Constraint> getIntConstraintIterator();

    /**
     * Return an iterator over the constraints of the model
     *
     * @return an iterator over the constraints of the model
     */
    public Iterator<Constraint> getConstraintIterator();

    /**
     * Return an iterator over constraint of a certain type
     * @param t type
     * @return iterator over constraint of type t
     */
    public Iterator<Constraint> getConstraintByType(ConstraintType t);

    /**
     * Return the number of constraint of a certain type
     * @param t the type of constraint
     * @return a integer
     */
    public int getNbConstraintByType(ConstraintType t);

    /**
     * Create and return a string representation of the variables of the Model
     * @return a string print of variables of the model
     */
    public String varsToString();

    /**
     * Create and return a string representation of the constraints of the Model
     * @return a string print of the constraints of the Model
     */
    public String constraintsToString();

    /**
     * Return a string representation of a solution.
     * !! Beware, not really correct!!
     * Use Solver API instead
     * @return a string represenstation of the variables of the model
     * @see choco.kernel.solver.Solver#pretty()
     */
    public String solutionToString();

    /**
     * Return the precision of RealVariable of the Model.
     * @return the precision of RealVariable
     */
    public double getPrecision();

    /**
     * Set the precision of RealVariable of the model to <i>precision</i>.
     * @param precision new precision to take into account
     */
    public void setPrecision(double precision);

    /**
     * Return minimal width reduction between two propagations.
     * @return Minimal width reduction between two propagations.
     */
    public double getReduction();

    /**
     * Set minimal width reduction between two propagations.
     * @param reduction the new minimal width reduction between two propagations.
     */
    public void setReduction(double reduction);


    /**
     * <i>Network management:</i>
     * Retrieve a variable by its index (all integer variables of
     * the model are numbered in sequence from 0 on)
     *
     * @param i index of the variable in the model
     * @return IntegerVariable
     */

    public IntegerVariable getIntVar(int i);

    /**
     * Return the index of the intVar in the model
     * @param c the int var
     * @return the index
     * @deprecated
     */
    @Deprecated
    public int getIntVarIndex(IntVar c);

    /**
     * retrieving the total number of variables
     *
     * @return the total number of variables in the model
     */
    public int getNbIntVars();
    
    /**
     * Returns a real variable.
     *
     * @param i index of the variable
     * @return the i-th real variable
     */
    public RealVariable getRealVar(int i);

    /**
     * Returns the number of variables modelling real numbers.
     * @return int
     */
    public int getNbRealVars();

    /**
     * Returns a constant variable.
     *
     * @param i index of the variable
     * @return the i-th real variable
     */
    public IntegerConstantVariable getConstantVar(int i);

    /**
     * Returns the number of variables modelling constant.
     * @return int
     */
    public int getNbConstantVars();

    /**
     * Returns a set variable.
     *
     * @param i index of the variable
     * @return the i-th real variable
     */
    public SetVariable getSetVar(int i);

    /**
     * Returns the number of variables modelling real numbers.
     * @return int
     */
    public int getNbSetVars();

    /**
     * Returns a multiple variable.
     *
     * @param i index of the variable
     * @return the i-th stored multiple variable
     */
    public MultipleVariables getStoredMultipleVar(int i);

    /**
     * Returns the number of stored multiple variables.
     * @return int
     */
    public int getNbStoredMultipleVars();
    
    /**
     * Return the total numbers of variables of the model
     * @return total number of variables of the model
     */
    public int getNbTotVars();


    public <E extends IOptions> void addOption(String option, E... element);

    /**
     * Add a variable to the model
     * @param v a variable
     */
    public void addVariable(Variable v);

    /**
     * Add one variable with options to the model
     * @param options define options of the variables
     * @param v one or more variables
     */
    public void addVariable(String options, Variable v);

    /**
     * Add one or more variables to the model
     * @param v one or more variables
     * @deprecated
     * @see Model#addVariables(choco.kernel.model.variables.Variable[])
     */
    @Deprecated
    public void addVariable(Variable... v);

    /**
     * Add one or more variables to the model with particular options
     * @param options defines options of the variables
     * @param v one or more variables
     * @deprecated
     * @see Model#addVariables(String, choco.kernel.model.variables.Variable[]) 
     */
    @Deprecated
    public void addVariable(String options, Variable... v);

    /**
     * Add one or more variables to the model
     * @param v one or more variables
     */
    public void addVariables(Variable... v);

    /**
     * Add one or more variables to the model with particular options
     * @param options defines options of the variables
     * @param v one or more variables
     */
    public void addVariables(String options, Variable... v);


    /**
     * Remove one or more variables from the model
     * (also remove constraints linked to the variables)
     * @param v variables to remove
     * @deprecated
     * @see Model#removeVariables(choco.kernel.model.variables.Variable[])
     */
    @Deprecated
    public void removeVariable(Variable... v);

    /**
     * Remove one variable from the model
     * (also remove constraints linked to the variable)
     * @param v the variable to remove
     */
    public void removeVariable(Variable v);

    /**
     * Remove one or more variables from the model
     * (also remove constraints linked to the variables)
     * @param v variables to remove
     */
    public void removeVariables(Variable... v);
    
    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     * @param c one or more constraint
     * @deprecated
     * @see Model#addConstraints(choco.kernel.model.constraints.Constraint[])
     */
    @Deprecated
    public void addConstraint(Constraint... c);

    /**
     * Add one constraint to the model.
     * Also add variables to the model if necessary.
     * @param c one constraint
     */
    public void addConstraint(Constraint c);

    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     * @param c one or more constraint
     */
    public void addConstraints(Constraint... c);

    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     * @param options defines options of the constraint
     * @param c one or more constraint
     * @deprecated
     * @see Model#addConstraints(choco.kernel.model.constraints.Constraint[])
     */
    @Deprecated
    public void addConstraint(String options, Constraint... c);

    /**
     * Add one constraint to the model.
     * Also add variables to the model if necessary.
     * @param options defines options of the constraint
     * @param c one constraint
     */
    public void addConstraint(String options, Constraint c);

    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     * @param options defines options of the constraint
     * @param c one or more constraint
     */
    public void addConstraints(String options, Constraint... c);

    /**
     * Remove a constraint from the model.
     * (Also remove variable if not even linked to existant constraints).
      * @param c the constraint to remove
     */
    public void removeConstraint(Constraint c);


    /**
     * Retrieves an iterator over IntegerVariables of the model
     * @return an iterator over IntegerVariables of the model
     */
    public Iterator<IntegerVariable> getIntVarIterator();

    /**
     * Retrieves an iterator over RealVariables of the model
     * @return an iterator over RealVariables of the model
     */
    public Iterator<RealVariable> getRealVarIterator();

    /**
     * Retrieves an iterator over SetVariables of the model
     * @return an iterator over SetVariables of the model
     */
    public Iterator<SetVariable> getSetVarIterator();

    /**
     * Retrieves an iterator over <i>constantes</i> variables of the model
     * @return an iterator over <i>constantes</i> variables of the model
     */
    public Iterator<Variable> getConstVarIterator();

    /**
     * Retrieves an iterator over MultipleVariables of the model (if stored)
     * @return an iterator over IntegerVariables of the model
     */
    public Iterator<MultipleVariables> getMultipleVarIterator();


    /**
     * Return the default expression decomposition
     * @return the default expression decomposition
     */
    public Boolean getDefaultExpressionDecomposition();

    /**
     * Set the default expression decomposition (BEWARE : it only concerns expression without particular decomposition option)
     *
     * If decomposedExp is set to <b>false</b>:
     * Every expression is then used to check a tuple in a dynamic way just like a nary relation that is defined without
     * listing all the possible tuples. The expression is then propagated using the GAC3rm algorithm.
     * This is very powerful as arc-consistency is obtained on the corresponding constraints.
     *
     * If decomposedExp is set to <b>true</b>, every expression will be decomposed 
     * automatically by introducing intermediate variables and eventually the generic reifiedIntConstraint if
     * reified operators are present in the expression.
     * By doing so, the level of pruning will decrease but expressions of larger arity involving
     * large domains can be represented.
     * @param decomposedExp the new default expression decomposition
     */
    public void setDefaultExpressionDecomposition(Boolean decomposedExp);

}
