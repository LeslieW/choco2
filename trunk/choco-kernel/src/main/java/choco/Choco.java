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
package choco;

import choco.kernel.common.IndexFactory;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.*;
import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.constraints.geost.GeostOptions;
import choco.kernel.model.constraints.geost.externalConstraints.DistGeqModel;
import choco.kernel.model.constraints.geost.externalConstraints.DistLeqModel;
import choco.kernel.model.constraints.geost.externalConstraints.IExternalConstraint;
import choco.kernel.model.constraints.pack.PackModeler;
import choco.kernel.model.variables.ConstantFactory;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.integer.MetaIntegerExpressionVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.model.variables.tree.TreeParametersObject;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.RscData;
import choco.kernel.solver.constraints.integer.extension.*;
import gnu.trove.TIntArrayList;

import static java.lang.System.arraycopy;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 6 août 2008
 * Time: 16:08:55
 * <p/>
 * This class must contain every current variable constructors and constraint constructors.
 * The class must be uptodate permanently, because, it is the way common users declare Model.
 */
public class Choco{

	protected final static Logger LOGGER = ChocoLogging.getModelLogger();

	public static boolean DEBUG = false;

	public final static int MIN_LOWER_BOUND  = Integer.MIN_VALUE / 100;

	public final static int MAX_UPPER_BOUND  = Integer.MAX_VALUE / 100;



	// ############################################################################################################
	// ######                                        VARIABLES                                                  ###
	// ############################################################################################################

	public final static IntegerVariable ZERO = constant(0);

	public final static IntegerVariable ONE= constant(1);

	private static void checkIntVarBounds(int lowB, int uppB) {
		if (lowB > uppB) {
			throw new ModelException("makeIntVar : lowB > uppB");
		}
		if(lowB < MIN_LOWER_BOUND || uppB > MAX_UPPER_BOUND){
			LOGGER.warning("WARNING! Domains over ["+MIN_LOWER_BOUND+", "+MAX_UPPER_BOUND+"] are strongly inadvisable ! ");
		}
	}




	/**
	 * Make an integer variable
	 *
	 * @param name : name of the variable
	 * @param lowB : lower bound of the variable
	 * @param uppB : upper bound of the variable
	 * @param options : options of the variable
	 * @return an integer variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable makeIntVar(String name, int lowB, int uppB, String... options) {
		checkIntVarBounds(lowB, uppB);
		IntegerVariable v = new IntegerVariable(name, VariableType.INTEGER, lowB, uppB);
		for (String option : options) {
			v.addOption(option);
		}
		return v;
	}

	/**
	 * Make an integer variable with undefined bounds
	 * Create an integer variable with the bounded domain [MIN_LOWER_BOUND,MAX_LOWER_BOUND]
	 * BEWARE: bigger domain have unexpected behaviour
	 *
	 * @param name : name of the variable
	 * @param options : options of the variable
	 * @return an integer variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable makeIntVar(String name, String... options) {
		return makeIntVar(name, MIN_LOWER_BOUND, MAX_UPPER_BOUND, options);
	}

	/**
	 * Make an integer variable
	 *
	 * @param name       : name of the variable
	 * @param valuesList : list of unsorted values
	 * @param options : options of the variable
	 * @return an integer variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable makeIntVar(String name, TIntArrayList valuesList, String... options) {
		int[] values = ArrayUtils.getNonRedundantSortedValues(valuesList);
		checkIntVarBounds(values[0], values[values.length-1]);
		IntegerVariable v = new IntegerVariable(name, VariableType.INTEGER, values);
		for (String option : options) {
			v.addOption(option);
		}
		return v;
	}

	/**
	 * Make an integer variable
	 *
	 * @param name        : name of the variable
	 * @param valuesArray : array of values
	 * @param options : options of the variable
	 * @return an integer variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable makeIntVar(String name, int[] valuesArray, String... options) {
		int[] values = ArrayUtils.getNonRedundantSortedValues(valuesArray);
		checkIntVarBounds(values[0], values[values.length-1]);
		IntegerVariable v = new IntegerVariable(name, VariableType.INTEGER, values);
		for (String option : options) {
			v.addOption(option);
		}
		return v;
	}


	/**
	 * Make a boolean variable
	 *
	 * @param name        : name of the variable
	 * @param options : options of the variable
	 * @return a boolean variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable makeBooleanVar(String name, String... options) {
		IntegerVariable v = new IntegerVariable(name, VariableType.INTEGER, 0,1);
		for (String option : options) {
			v.addOption(option);
		}
		return v;
	}

	/**
	 * Make an array of boolean variables
	 *
	 * @param name      : name of the variable
	 * @param dim       : dimension of the array
	 * @param options   : options of the variable
	 * @return a boolean variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[] makeBooleanVarArray(String name, int dim, String... options) {
		IntegerVariable[] vars = new IntegerVariable[dim];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeBooleanVar(name + "_" + i, options);
		}
		return vars;
	}
	/**
	 * Make a array of integer variable with same lower and upper bound
	 *
	 * @param name : prefixe name of each variable
	 * @param dim  : dimension of the array
	 * @param lowB : lower bound of each variable
	 * @param uppB : upper bound of each variable
	 * @param options : options of the variable
	 * @return an array of integer variables
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[] makeIntVarArray(String name, int dim, int lowB, int uppB, String... options) {

		IntegerVariable[] vars = new IntegerVariable[dim];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeIntVar(name + "_" + i, lowB, uppB, options);
		}
		return vars;
	}

	/**
	 * Make a array of integer variable with undefined bounds
	 * Create an integer variable array with the following bounded domain [MIN_LOWER_BOUND,MAX_LOWER_BOUND]
	 * BEWARE: bigger domain have unexpected behaviour
	 *
	 * @param name : name of the variable
	 * @param dim   : dimension of the array
	 * @param options : options of the variable
	 * @return an integer variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[] makeIntVarArray(String name, int dim, String... options) {
		return makeIntVarArray(name, dim, MIN_LOWER_BOUND, MAX_UPPER_BOUND, options);
	}

	/**
	 * Make an array of integer variable wih the same values
	 *
	 * @param name        : prefix name of each variable
	 * @param dim         : dimension of the array
	 * @param valuesArray : values of each variable
	 * @param options : options of the variable
	 * @return an array of integer variables
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[] makeIntVarArray(String name, int dim, int[] valuesArray, String... options) {
		IntegerVariable[] vars = new IntegerVariable[dim];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeIntVar(name + "_" + i, valuesArray, options);
		}
		return vars;
	}

	/**
	 * Make a matrix of integer variable with same lower and upper bound
	 *
	 * @param name : prefixe name of each variable
	 * @param dim1 : first dimension of the matrix
	 * @param dim2 : second dimension of the matrix
	 * @param lowB : lower bound of each variable
	 * @param uppB : upper bound of each variable
	 * @param options : options of the variable
	 * @return an array of integer variables
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[][] makeIntVarArray(String name, int dim1, int dim2, int lowB, int uppB, String... options) {
		IntegerVariable[][] vars = new IntegerVariable[dim1][dim2];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeIntVarArray(name + "_" + i, dim2, lowB, uppB, options);
		}
		return vars;
	}

	/**
	 * Make a matrix of integer variable with undefined bounds
	 * Create an integer variable matrix with the following bounded domain [MIN_LOWER_BOUND,MAX_LOWER_BOUND]
	 * BEWARE: bigger domain have unexpected behaviour
	 *
	 * @param name : name of the variable
	 * @param dim1 : first dimension of the matrix
	 * @param dim2 : second dimension of the matrix
	 * @param options : options of the variable
	 * @return an integer variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[][] makeIntVarArray(String name, int dim1, int dim2, String... options) {
		return makeIntVarArray(name, dim1, dim2, MIN_LOWER_BOUND, MAX_UPPER_BOUND, options);
	}

	/**
	 * Make a matrix of integer variable with the same values
	 *
	 * @param name        : prefixe name of each variable
	 * @param dim1        : first dimension of the matrix
	 * @param dim2        : second dimension of the matrix
	 * @param valuesArray : values of each variable
	 * @param options : options of the variable
	 * @return an array of integer variables
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For IntegerVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create bounded variables</i>
	 *                <i> cp:btree to force Solver to create binary tree variables</i>
	 *                <i> cp:blist to force Solver to create bipartite list variables</i>
	 *                <i> cp:link to force Solver to create linked list variables</i>
	 *                <i> cp:binary to force Solver to create boolean variables</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static IntegerVariable[][] makeIntVarArray(String name, int dim1, int dim2, int[] valuesArray, String... options) {
		IntegerVariable[][] vars = new IntegerVariable[dim1][dim2];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeIntVarArray(name + "_" + i, dim2, valuesArray, options);
		}
		return vars;
	}


	/**
	 * Make a set variable
	 *
	 * @param name : name of the variable
	 * @param lowB : lower bound of the variable
	 * @param uppB : upper bound of the variable
	 * @param options : options of the variable
	 * @return a set variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For SetVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create set variables with enumerated caridinality (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create set variables with bounded cardinality</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static SetVariable makeSetVar(String name, int lowB, int uppB, String... options) {
		int c = uppB - lowB + 1;
		IntegerVariable card = makeIntVar(name, 0, c, options);
		SetVariable var = new SetVariable(name, VariableType.SET, lowB, uppB, card);
		for (String option : options) {
			var.addOption(option);
		}
		return var;
	}

	/**
	 * Make a set variable
	 *
	 * @param name : name of the variable
	 * @param valuesArray : array of values
	 * @param options : options of the variable
	 * @return a set variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For SetVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create set variables with enumerated caridinality (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create set variables with bounded cardinality</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static SetVariable makeSetVar(String name, int[] valuesArray, String... options) {
		int[] values2 = new int[valuesArray.length];
		arraycopy(valuesArray, 0, values2, 0, valuesArray.length);
		Arrays.sort(values2);
		int c = values2.length;
		IntegerVariable card = makeIntVar("|"+name+"|", 0, c, options);
		SetVariable var = new SetVariable(name, VariableType.SET, values2, card);
		for (String option : options) {
			var.addOption(option);
		}
		return var;
	}


	/**
	 * Make an array of set variables
	 *
	 * @param name : name of the variable
	 * @param dim  : dimension of the array
	 * @param lowB : lower bound of the variable
	 * @param uppB : upper bound of the variable
	 * @param options : options of the variable
	 * @return a set variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For SetVariable, available options are :
	 *                <ul>
	 *                <i> cp:enum to force Solver to create set variables with enumerated caridinality (default options if options is empty)</i>
	 *                <i> cp:bound to force Solver to create set variables with bounded cardinality</i>
	 *                </ul>
	 *                <p/>
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static SetVariable[] makeSetVarArray(String name, int dim, int lowB, int uppB, String... options) {
		SetVariable[] vars = new SetVariable[dim];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeSetVar(name + "_" + i, lowB, uppB, options);
		}
		return vars;
	}

	/**
	 * Make a real variable
	 *
	 * @param name : name of the variable
	 * @param lowB : lower bound of the variable
	 * @param uppB : upper bound of the variable
	 * @param options : options of the variable
	 * @return a real variable
	 *
	 * <p/>
	 *                Options of CPModel must be prefix with "cp:".
	 *                For RealVariable, available options are :
	 *                Options for decisionnal/undecisionnal variables
	 *                <ul>
	 *                <i>cp:decision to force variable to be a decisional one</i>
	 *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
	 *                </ul>
	 *                Options for optimization
	 *                <ul>
	 *                <i>cp:objective to define the variable to optimize
	 *                </ul>
	 */
	public static RealVariable makeRealVar(String name, double lowB, double uppB, String... options) {
		if (lowB > uppB) {
			throw new ModelException("makeRealVar : lowB > uppB");
		}
		RealVariable v = new RealVariable(name, VariableType.REAL, lowB, uppB);
		for (String option : options) {
			v.addOption(option);
		}
		return v;
	}

	/**
	 * ***** Tasks **********
	 */


	/**
	 *
	 * create a task variable.
	 * @param name the name of the task
	 * @param start starting time integer variable
	 * @param end ending time integer variable
	 * @param duration duration integer variable
	 * @param options options are added to the task but not added to the integer variables.
	 * @return a task variable
	 */
	public static TaskVariable makeTaskVar(final String name,final IntegerVariable start, final IntegerVariable end, final IntegerVariable duration, String... options) {
		final TaskVariable tv = new TaskVariable(name,start, end, duration);
		for (String opt : options) {
			tv.addOption(opt);
		}
		return tv;
	}

	/**
	 *
	 * create a task variable. The ending variable is created.
	 * @param name the name of the task
	 * @param start starting time integer variable
	 * @param duration duration integer variable
	 * @param options options are added to the task and the end variable.
	 * @return a task variable
	 */
	public static TaskVariable makeTaskVar(final String name,final IntegerVariable start, final IntegerVariable duration, String... options) {
		IntegerVariable end = makeIntVar("end-"+name, 0, start.getUppB() + duration.getUppB(), options);
		final TaskVariable tv = new TaskVariable(name,start, end, duration);
		for (String opt : options) {
			tv.addOption(opt);
		}
		return tv;
	}

	/**
	 * Make a task variable.
	 * @param name the name of the task
	 * @param binf release time (earliest starting time)
	 * @param bsup due date (latest completion time)
	 * @param duration duration of the task.
	 * @param options options are also added to the start and end variables.
	 * @return a task variable
	 */
	public static TaskVariable makeTaskVar(final String name,final int binf,final int bsup,final IntegerVariable duration, String... options) {
		final IntegerVariable start = makeIntVar("start-"+name, binf, bsup, options);
		final IntegerVariable end = makeIntVar("end-"+name, binf, bsup, options);
		return makeTaskVar(name, start,end, duration, options);
	}

	public static TaskVariable makeTaskVar(final String name,final int binf,final int bsup,final int duration, String... options) {
		return makeTaskVar(name, binf, bsup, constant(duration), options);
	}

	public static TaskVariable makeTaskVar(final String name,final int bsup,final IntegerVariable duration, String... options) {
		return makeTaskVar(name, 0, bsup, duration, options);
	}

	public static TaskVariable makeTaskVar(final String name,final int bsup,final int duration, String... options) {
		return makeTaskVar(name, 0, bsup, constant(duration), options);

	}

	/**
	 * Create an array of task variables.
	 * @param prefix The name's prefix
	 * @param starts start variables
	 * @param ends end variables (could be null)
	 * @param durations duration variables
	 * @param options options are also added to the start and end variables.
	 * @return a task variable
	 */
	public static TaskVariable[] makeTaskVarArray(final String prefix,final IntegerVariable[] starts, final IntegerVariable[] ends, final IntegerVariable[] durations, String... options) {
		if(starts != null && durations != null && starts.length == durations.length) {
			if(ends == null) {
				TaskVariable[] vars = new TaskVariable[starts.length];
				for (int i = 0; i < vars.length; i++) {
					vars[i] = makeTaskVar(prefix+"_"+i, starts[i], durations[i], options);
				}
				return vars;
			}else if(starts.length == ends.length) {
				TaskVariable[] vars = new TaskVariable[starts.length];
				for (int i = 0; i < vars.length; i++) {
					vars[i] = makeTaskVar(prefix+"_"+i, starts[i], ends[i], durations[i], options);
				}
				return vars;
			}else {
				throw new ModelException("invalid ends array length.");
			}
		}else {
			throw new ModelException("starts and durations are required and should have equal lengths.");
		}
	}
	/**
	 * Create an array of task variables.
	 * @param name name of the variable
	 * @param binf release times (earliest starting time)
	 * @param bsup due dates (latest completion time)
	 * @param durations duration variables
	 * @param options options are also added to the start and end variables.
	 * @return a task variable
	 */
	public static TaskVariable[] makeTaskVarArray(final String name, final int binf,final int bsup,final IntegerVariable[] durations, String... options) {
		final int n = durations.length;
		TaskVariable[] vars = new TaskVariable[n];
		for (int i = 0; i < n; i++) {
			vars[i] = makeTaskVar(name+"_"+i, binf, bsup, durations[i], options);
		}
		return vars;
	}

	public static TaskVariable[] makeTaskVarArray(final String name, final int binf,final int bsup,final int[] durations, String... options) {
		return makeTaskVarArray(name, binf, bsup, constantArray(durations), options);
	}


	public static TaskVariable[][] makeTaskVarArray(final String name, final int binf,final int bsup,final IntegerVariable[][] durations, String... options) {
		final int n = durations.length;
		final int m = durations[0].length;
		TaskVariable[][] vars = new TaskVariable[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				vars[i][j] = makeTaskVar(name+"_"+i+"_"+j, binf, bsup, durations[i][j], options);
			}
		}
		return vars;
	}


	public static TaskVariable[][] makeTaskVarArray(final String name, final int binf,final int bsup,final int[][] durations, String... options) {
		return makeTaskVarArray(name, binf, bsup, constantArray(durations), options);
	}

	/**
	 * ***** Constant **********
	 */

	/**
	 * Create a constant set variable
	 * @param name name of the constant
	 * @param value value of the constant
	 * @return the set constant
	 */
	public static SetVariable constant(String name, int... value) {
		return ConstantFactory.getConstant(name, value);
	}

	public static RealConstantVariable constant(String name, double value) {
		return ConstantFactory.getConstant(name, value);
	}

	public static IntegerConstantVariable constant(String name, int value) {
		return ConstantFactory.getConstant(name, value);
	}

	public static IntegerConstantVariable constant(int a) {
		return ConstantFactory.getConstant(a);
	}

	public static SetConstantVariable constant(int[] a) {
		return ConstantFactory.getConstant(a);
	}

	public static SetConstantVariable emptySet() {
		return ConstantFactory.getConstant(new int[0]);
	}

	public static RealConstantVariable constant(double a) {
		return ConstantFactory.getConstant(a);
	}

	public static IntegerConstantVariable[] constantArray(int[] values) {
		IntegerConstantVariable[] tmp = new IntegerConstantVariable[values.length];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = constant(values[i]);
		}
		return tmp;
	}

	public static IntegerConstantVariable[][] constantArray(int[][] values) {
		IntegerConstantVariable[][] tmp = new IntegerConstantVariable[values.length][values[0].length];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				tmp[i][j] = constant(values[i][j]);
			}

		}
		return tmp;
	}

	public static RealConstantVariable[] constantArray(double[] values) {
		RealConstantVariable[] tmp = new RealConstantVariable[values.length];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = constant(values[i]);
		}
		return tmp;
	}

	// ############################################################################################################
	// ######                                       CONSTRAINTS                                                 ###
	// ############################################################################################################

	public static final Constraint TRUE = new ComponentConstraint<IntegerVariable>(ConstraintType.TRUE, true, new IntegerVariable[0]);

	public static final Constraint FALSE = new ComponentConstraint<IntegerVariable>(ConstraintType.FALSE, false, new IntegerVariable[0]);

	/**
	 * Creates a constraint by stating that a term is not equal than a constant
	 *
	 * @param x the expression
	 * @param c the constant
	 * @return the linear disequality constraint
	 */
	public static Constraint neq(IntegerExpressionVariable x, int c) {
		return new ComponentConstraint<Variable>(ConstraintType.NEQ, ConstraintType.NEQ, new Variable[]{x, constant(c)});
	}

	public static Constraint neq(int c, IntegerExpressionVariable x) {
		return neq(x, c);
	}

	public static Constraint neq(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.NEQ, ConstraintType.NEQ, new Variable[]{x, y});
	}

	public static Constraint geq(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{x, y});
	}

	public static Constraint geq(IntegerExpressionVariable x, int c) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{x, constant(c)});
	}

	public static Constraint geq(int c, IntegerExpressionVariable x) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{constant(c), x});
	}

	public static Constraint geq(RealExpressionVariable x, RealExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{x, y});
	}

	public static Constraint geq(RealExpressionVariable x, double c) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{x, constant(c)});
	}

	public static Constraint geq(double c, RealExpressionVariable x) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{constant(c), x});
	}

	/**
	 * Return a constraint that ensures x > y
	 * @param x an expression variable or an integer variable
	 * @param y an expression variable or an integer variable
	 * @return a constraint that ensures x > y
	 */
	public static Constraint gt(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.GT, ConstraintType.GT, new Variable[]{x, y});
	}

	/**
	 * Return a constraint that ensures x > c
	 * @param x an expression variable or an integer variable
	 * @param c an integer
	 * @return a constraint that ensures x > y
	 */
	public static Constraint gt(IntegerExpressionVariable x, int c) {
		return new ComponentConstraint<Variable>(ConstraintType.GT, ConstraintType.GT, new Variable[]{x, constant(c)});
	}

	/**
	 * Return a constraint that ensures c > x
	 * @param x an expression variable or an integer variable
	 * @param c an integer
	 * @return a constraint that ensures x > y
	 */
	public static Constraint gt(int c, IntegerExpressionVariable x) {
		return new ComponentConstraint<Variable>(ConstraintType.GT, ConstraintType.GT, new Variable[]{constant(c), x});
	}

	/**
	 * Return a constraint that ensure x == y
	 * @param x an expression variable or integer variable
	 * @param y an expression variable or integer variable
	 * @return a constraint that ensures x == y
	 */
	public static Constraint eq(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{x, y});
	}

	/**
	 * Return a constraint that ensure x == c
	 * @param x an expression variable or integer variable
	 * @param c a constant
	 * @return a constraint that ensure x == c
	 */
	public static Constraint eq(IntegerExpressionVariable x, int c) {
		return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{x, constant(c)});
	}

	/**
	 * Return a constraint that ensure x == c
	 * @param x an expression variable or integer variable
	 * @param c a constant
	 * @return a constraint that ensure x == c
	 */
	public static Constraint eq(int c, IntegerExpressionVariable x) {
		return eq(x, c);
	}

	/**
	 * Return a constraint that ensures x == y
	 * @param x an expression variable or real variable
	 * @param y an expression variable or real variable
	 * @return a constraint that ensures x == y
	 */
	public static Constraint eq(RealExpressionVariable x, RealExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{x, y});
	}

	/**
	 * Return a constraint that ensures x == c
	 * @param x an expression variable or real variable
	 * @param c a double
	 * @return a constraint that ensures x == c
	 */
	public static Constraint eq(RealExpressionVariable x, double c) {
		return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{x, constant(c)});
	}

	/**
	 * Return a constraint that ensures x == c
	 * @param x an expression variable or real variable
	 * @param c a double
	 * @return a constraint that ensures x == c
	 */
	public static Constraint eq(double c, RealExpressionVariable x) {
		return eq(x, c);
	}

	/**
	 * Return a constraint that ensures r == i
	 * @param r a real variable
	 * @param i an integer variable
	 * @return a constraint that ensures r == i
	 */
	public static Constraint eq(RealVariable r, IntegerVariable i) {
		return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{r, i});
	}

	/**
	 * Return a constraint that ensures r == i
	 * @param r a real variable
	 * @param i an integer variable
	 * @return a constraint that ensures r == i
	 */
	public static Constraint eq(IntegerVariable i, RealVariable r) {
		return eq(r, i);
	}

	public static Constraint leq(IntegerExpressionVariable x, int c) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{x, constant(c)});
	}

	public static Constraint leq(int c, IntegerExpressionVariable x) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{constant(c), x});
	}

	public static Constraint leq(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{x, y});
	}

	public static Constraint leq(RealExpressionVariable x, double c) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{x, constant(c)});
	}

	public static Constraint leq(double c, RealExpressionVariable x) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{constant(c), x});
	}

	public static Constraint leq(RealExpressionVariable x, RealExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{x, y});
	}

	/**
	 * Return a constraint that ensures x < c
	 * @param x an integer expression variable
	 * @param c an integer
	 * @return a constraint that ensures x < c
	 */
	public static Constraint lt(IntegerExpressionVariable x, int c) {
		return new ComponentConstraint<Variable>(ConstraintType.LT, ConstraintType.LT, new Variable[]{x, constant(c)});
	}

	/**
	 * Return a constraint that ensures x < c
	 * @param x an integer expression variable
	 * @param c an integer
	 * @return a constraint that ensures x < c
	 */
	public static Constraint lt(int c, IntegerExpressionVariable x) {
		return new ComponentConstraint<Variable>(ConstraintType.LT, ConstraintType.LT, new Variable[]{constant(c), x});
	}

	/**
	 * Return a constraint that ensures x < y
	 * @param x an integer expression variable
	 * @param y an integer expression variable
	 * @return a constraint that ensures x < y
	 */
	public static Constraint lt(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		return new ComponentConstraint<Variable>(ConstraintType.LT, ConstraintType.LT, new Variable[]{x, y});
	}


	/**
	 * Enforce z = x * y
	 *
	 * @param x the first integer variable
	 * @param y the second integer variable
	 * @param z the result of x*y
	 * @return the times constraint
	 */
	public static Constraint times(IntegerVariable x, IntegerVariable y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.TIMES, null, new IntegerVariable[]{x, y, z});
	}

	/**
	 * Enforce z = x * y
	 *
	 * @param x the first integer variable
	 * @param y the second integer variable
	 * @param z the result of x*y
	 * @return the times constraint
	 */
	public static Constraint times(int x, IntegerVariable y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.TIMES, null, new IntegerVariable[]{constant(x), y, z});
	}

	/**
	 * Enforce z = x * y
	 *
	 * @param x the first integer variable
	 * @param y the second integer variable
	 * @param z the result of x*y
	 * @return the times constraint
	 */
	public static Constraint times(IntegerVariable x, int y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.TIMES, null, new IntegerVariable[]{x, constant(y), z});
	}


	/**
	 * Enforce z = x / y
	 *
	 * @param x the first integer variable
	 * @param y the second integer variable
	 * @param z the result of x/y
	 * @return the intDiv constraint
	 */
	public static Constraint intDiv(IntegerVariable x, IntegerVariable y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.EUCLIDEANDIVISION, null, new IntegerVariable[]{x, y, z});
	}

	//******************************************************//
	//***************** Table Constraints ******************//
	//******************************************************//

	//***************** Binary Relations ******************//

	/**
	 * Create a binary relation that represent the list of compatible or
	 * incompatible pairs of values (depending on feas) given in argument tp
	 * be stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument.
	 * So such that : min[0] <= x.getInf(), max[0] >= x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >= y.getSup()
	 * for any pairs of variable x,y where an ac algorithm will be used with this relation.
	 * This is mandatory in the api to be able to compute the opposite of the relation if needed so the min[i]/max[i] can be smaller/bigger than min_{j \in pairs} pairs.get(j)[i] or max_{j \in pairs} pairs.get(j)[i]
	 *
	 * @param min array of min values
	 * @param max array of max values
	 * @param mat    the list of tuples defined as int[] of size 2
	 * @param feas   specify if the relation is defined in feasibility or not i.e. if the tuples corresponds to feasible or infeasible tuples
	 * @param bitset specify if the relation is intended to be used in ac3rm enhanced with bitwise operations
	 * @return a binary relation that can be used with the api relationPairAC(v1,v2,relation)
	 */
	public static BinRelation makeBinRelation(int[] min, int[] max, List<int[]> mat, boolean feas, boolean bitset) {
		int n1 = max[0] - min[0] + 1;
		int n2 = max[1] - min[1] + 1;
		ExtensionalBinRelation relation;
		if (bitset) {
			relation = new CouplesBitSetTable(feas, min[0], min[1], n1, n2);
		} else {
			relation = new CouplesTable(feas, min[0], min[1], n1, n2);
		}
		for (int[] couple : mat) {
			if (couple.length != 2) {
				throw new ModelException("Wrong dimension : " + couple.length + " for a couple");
			}
			relation.setCouple(couple[0], couple[1]);
		}
		return relation;
	}


	/**
	 * Create a binary relation that represent the list of compatible or
	 * incompatible pairs of values (depending on feas) given in argument tp
	 * be stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument.
	 * So such that : min[0] <= x.getInf(), max[0] >= x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >= y.getSup()
	 * for any pairs of variable x,y where an ac algorithm will be used with this relation.
	 * This is mandatory in the api to be able to compute the opposite of the relation if needed so the min[i]/max[i] can be smaller/bigger than min_{j \in pairs} pairs.get(j)[i] or max_{j \in pairs} pairs.get(j)[i]
	 * This relation can not be used with ac3 with residues and BitSet.
	 * @param min array of min values
	 * @param max arrau of max values
	 * @param mat  the list of tuples defined as int[] of size 2
	 * @param feas specify if the relation is defined in feasibility or not i.e. if the tuples corresponds to feasible or infeasible tuples
	 * @return a binary relation that can be used with the api relationPairAC(v1,v2,relation)
	 */
	public static BinRelation makeBinRelation(int[] min, int[] max, List<int[]> mat, boolean feas) {
		return makeBinRelation(min, max, mat, feas, false);
	}


	/**
	 * Create a binary relation represented by the matrix of feasible/infeasible (depending on the feas parameters) pairs of values
	 * incompatible pairs of values (depending on feas) given in argument tp
	 * be stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument.
	 * So such that : min[0] <= x.getInf(), max[0] >= x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >= y.getSup()
	 * for any pairs of variable x,y where an ac algorithm will be used with this relation.
	 * This is mandatory in the api to be able to compute the opposite of the relation if needed.
	 *
	 * This constructor allows toshare a relation among binary constraints
	 * @param min array of min values
	 * @param max array of max values
	 * @param mat  the consistency matrix
	 * @param feas specify if the relation is defined in feasibility or not
	 * @param bitset specify if the relation is intended to be used in ac3rm enhanced with bitwise operations
	 * @return a binary relation that can be used with the api relationPairAC(v1,v2,relation)
	 */
	public static BinRelation makeBinRelation(int[] min, int[] max, boolean[][] mat, boolean feas, boolean bitset) {
		int n1 = max[0] - min[0] + 1;
		int n2 = max[1] - min[1] + 1;
		if (n1 == mat.length && n2 == mat[0].length) {
			ExtensionalBinRelation relation;
			relation = bitset ? new CouplesBitSetTable(feas, min[0], min[1], n1, n2) :
				new CouplesTable(feas, min[0], min[1], n1, n2);

			for (int i = 0; i < n1; i++) {
				for (int j = 0; j < n2; j++) {
					if (mat[i][j]) {
						relation.setCouple(i + min[0], j + min[1]);
					}
				}
			}
			return relation;
		} else {
			throw new SolverException("Wrong dimension for the matrix of consistency : "
					+ mat.length + " X " + mat[0].length + " instead of " + n1 + "X" + n2);
		}
	}

	/**
	 * Create a binary relation represented by the matrix of feasible/infeasible (depending on the feas parameters) pairs of values
	 * incompatible pairs of values (depending on feas) given in argument tp
	 * be stated on any pair of variables (x,y) whose domain is included in the min
	 * max given in argument.
	 * So such that : min[0] <= x.getInf(), max[0] >= x.getSup(), min[1] <= x.getSup(), min[1] >= y.getInf(), max[1] >= y.getSup()
	 * for any pairs of variable x,y where an ac algorithm will be used with this relation.
	 * This is mandatory in the api to be able to compute the opposite of the relation if needed.
	 *
	 * This constructor allows toshare a relation among binary constraints
	 * @param min array of min values
	 * @param max array of max values
	 * @param mat  the consistency matrix
	 * @param feas specify if the relation is defined in feasibility or not
	 * @return a binary relation that can be used with the api relationPairAC(v1,v2,relation)
	 */

	public static BinRelation makeBinRelation(int[] min, int[] max, boolean[][] mat, boolean feas) {
		return makeBinRelation(min, max, mat, feas, false);
	}

	private static Constraint makePairAC(String options, IntegerVariable v1, IntegerVariable v2, Object mat, boolean feas) {
		if (options == null) {
			return new ComponentConstraint<IntegerVariable>(ConstraintType.TABLE,
					new Object[]{feas, mat},
					new IntegerVariable[]{v1, v2});
		} else {
			Constraint c = new ComponentConstraint<IntegerVariable>(ConstraintType.TABLE,
					new Object[]{feas, mat},
					new IntegerVariable[]{v1, v2});
			c.addOption(options);
			return c;
		}
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of infeasibles pairs of values
	 * for the corresponding variables. Default ac algorithm is ac3 with residues.
	 *
	 * @param v1  : first variable
	 * @param v2  : second variable
	 * @param mat : the list of tuples defining the relation (the infeasible pairs)
	 * @return Constraint
	 */
	public static Constraint infeasPairAC(IntegerVariable v1, IntegerVariable v2, List<int[]> mat) {
		return makePairAC(null, v1, v2, mat, false);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of infeasibles pairs of values
	 * for the corresponding variables
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param options : used to define the desired arc consistency algorithm
	 *                "cp:ac3"    to get ac3
	 *                "cp:ac2001" to get ac2001
	 *                "cp:ac32"   to get ac3 with residues (ac2001 where the support is not stored)
	 *                "cp:ac322"  to get ac3 with the used of BitSet to know if a support still exists
	 * @param mat     : the list of tuples defining the relation (the infeasible pairs)
	 * @return Constraint
	 */
	public static Constraint infeasPairAC(String options, IntegerVariable v1, IntegerVariable v2, List<int[]> mat) {
		return makePairAC(options, v1, v2, mat, false);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of feasibles pairs of values
	 * for the corresponding variables.  Default ac algorithm is ac3 with residues.
	 *
	 * @param v1  : first variable
	 * @param v2  : second variable
	 * @param mat : the list of tuples defining the relation (the feasible pairs)
	 * @return Constraint
	 */
	public static Constraint feasPairAC(IntegerVariable v1, IntegerVariable v2, List<int[]> mat) {
		return makePairAC(null, v1, v2, mat, true);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of feasibles pairs of values
	 * for the corresponding variables
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param options : used to define the desired arc consistency algorithm
	 *                "cp:ac3"    to get ac3
	 *                "cp:ac2001" to get ac2001
	 *                "cp:ac32"   to get ac3 with residues (ac2001 where the support is not stored)
	 *                "cp:ac322"  to get ac3 with the used of BitSet to know if a support still exists
	 * @param mat     : the list of tuples defining the relation (the feasible pairs)
	 * @return Constraint
	 */
	public static Constraint feasPairAC(String options, IntegerVariable v1, IntegerVariable v2, List<int[]> mat) {
		return makePairAC(options, v1, v2, mat, true);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of infeasibles pairs of values
	 * for the corresponding variables. Default ac algorithm is ac3 with residue
	 *
	 * @param v1  : first variable
	 * @param v2  : second variable
	 * @param mat : a boolean matrice indicating the consistency relation (the infeasible pairs)
	 * @return Constraint
	 */
	public static Constraint infeasPairAC(IntegerVariable v1, IntegerVariable v2, boolean[][] mat) {
		return makePairAC(null, v1, v2, mat, false);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of infeasibles pairs of values
	 * for the corresponding variables
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param options : used to define the desired arc consistency algorithm
	 *                "cp:ac3"    to get ac3
	 *                "cp:ac2001" to get ac2001
	 *                "cp:ac32"   to get ac3 with residues (ac2001 where the support is not stored)
	 *                "cp:ac322"  to get ac3 with the used of BitSet to know if a support still exists
	 * @param mat     : a boolean matrice indicating the consistency relation (the infeasible pairs)
	 * @return Constraint
	 */
	public static Constraint infeasPairAC(String options, IntegerVariable v1, IntegerVariable v2, boolean[][] mat) {
		return makePairAC(options, v1, v2, mat, false);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of feasibles pairs of values
	 * for the corresponding variables. Default ac algorithm is ac3 with residues.
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param mat     : a boolean matrice indicating the consistency relation (the feasible pairs)       *
	 * @return Constraint
	 */
	public static Constraint feasPairAC(IntegerVariable v1, IntegerVariable v2, boolean[][] mat) {
		return makePairAC(null, v1, v2, mat, true);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of feasibles pairs of values
	 * for the corresponding variables
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param options : used to define the desired arc consistency algorithm
	 *                "cp:ac3"    to get ac3
	 *                "cp:ac2001" to get ac2001
	 *                "cp:ac32"   to get ac3 with residues (ac2001 where the support is not stored)
	 *                "cp:ac322"  to get ac3 with the used of BitSet to know if a support still exists
	 * @param mat     : a boolean matrice indicating the consistency relation (the feasible pairs)
	 * @return Constraint
	 */
	public static Constraint feasPairAC(String options, IntegerVariable v1, IntegerVariable v2, boolean[][] mat) {
		return makePairAC(options, v1, v2, mat, true);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of infeasibles pairs of values
	 * for the corresponding variables. Default algorithm is ac3 with residues.
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param binR : a binary relation standing for the consistency relation. Notice that the same relation can therefore
	 *                   be shared among constraints.
	 * @return Constraint
	 */
	public static Constraint relationPairAC(IntegerVariable v1, IntegerVariable v2, BinRelation binR) {
		return makePairAC(null, v1, v2, binR, false);
	}

	/**
	 * Build a Table constraint defined extensionnaly by the set of infeasibles pairs of values
	 * for the corresponding variables
	 *
	 * @param v1      : first variable
	 * @param v2      : second variable
	 * @param options : used to define the desired arc consistency algorithm
	 *                "cp:ac3"    to get ac3
	 *                "cp:ac2001" to get ac2001
	 *                "cp:ac32"   to get ac3 with residues (ac2001 where the support is not stored)
	 *                "cp:ac322"  to get ac3 with the used of BitSet to know if a support still exists
	 * @param binR : a binary relation standing for the consistency relation. Notice that the same relation can therefore
	 *                   be shared among constraints.
	 * @return Constraint
	 */
	public static Constraint relationPairAC(String options, IntegerVariable v1, IntegerVariable v2, BinRelation binR) {
		return makePairAC(options, v1, v2, binR, false);
	}

	//***************** Nary Relations ******************//


	/**
	 * Create a nary relationship that can be used to state a GAC constraint using
	 * after the api relationTupleAC(relation).
	 * Typically GAC algorithms uses two main schemes to seek the next support :
	 * - either by looking in the domain of the variable (here put feas = false to get such a relation and give the negative tuples)
	 * - or in the table itself in which case one need to be able to iterate over the tuples and not only check consistency (here put feas = true to get such a relation
	 * and gives the positive tuples)
	 * So the scheme is choosed automatically depending if you are in feasible or infeasible tuples. See the api that allows
	 * to select the scheme to have more control.
	 * @param min    : min[i] has to be greater or equal the minimum value of any i-th variable on which this relation will be used
	 * @param max    : max[i] has to be greater or equal the maximum value of any i-th variable on which this relation will be used
	 * @param tuples : list of tuples
	 * @param feas   : specifies if the tuples are feasible or infeasible tuples
	 * @return an nary relation.
	 */
	public static LargeRelation makeLargeRelation(int[] min, int[] max, List<int[]> tuples, boolean feas) {
		return makeLargeRelation(min, max, tuples, feas, (feas ? 0 : 1));
	}

	/**
	 * Create a nary relationship that can be used to state a GAC constraint using
	 * after the api relationTupleAC(relation).
	 * Typically GAC algorithms uses two main schemes to seek the next support :
	 * - either by looking in the domain of the variable (here put scheme = 1 to get such a relation)
	 * - or in the table itself in which case one need to be able to iterate over the tuples and not only check consistency
	 * (here put scheme = 0 or 2 (for GACstr) to get such a relation)
	 * and gives the positive tuples)
	 *
	 * @param min    : min[i] has to be greater or equal the minimum value of any i-th variable on which this relation will be used
	 * @param max    : max[i] has to be greater or equal the maximum value of any i-th variable on which this relation will be used
	 * @param tuples : list of tuples
	 * @param feas   : specifies if the tuples are feasible or infeasible tuples
	 * @param scheme : specifies the desired scheme allowed tuples (0) or valid tuples (1) or both (2). The GAC constraint stated on this relation will
	 *               then work in the corresponding scheme. Allowed means that the search for support is made through the lists of tuples and valid that it is made
	 *               through the domains of the variables.
	 * @return an nary relation.
	 */
	public static LargeRelation makeLargeRelation(int[] min, int[] max, List<int[]> tuples, boolean feas, int scheme) {
		int n = min.length;
		int[] offsets = new int[n];
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++) {
			sizes[i] = max[i] - min[i] + 1;
			offsets[i] = min[i];
		}
		LargeRelation relation;
		if (scheme == 0) {
			relation = new IterTuplesTable(tuples, offsets, sizes);
		} else if (scheme == 1) {
			relation = new TuplesTable(feas, offsets, sizes);
			for (int[] tuple : tuples) {
				if (tuple.length != n) {
					throw new SolverException("Wrong dimension : " + tuple.length + " for a tuple (should be " + n + ")");
				}
				((TuplesTable) relation).setTuple(tuple);
			}
		} else {
			relation = new TuplesList(tuples);
		}

		return relation;
	}

	private static Constraint makeTupleACFC(String options, IntegerVariable[] vs, Object mat, boolean feas) {
		if (options == null) {
			return new ComponentConstraint<IntegerVariable>(ConstraintType.TABLE,
					new Object[]{feas, mat},
					vs);
		} else {
			Constraint c = new ComponentConstraint<IntegerVariable>(ConstraintType.TABLE,
					new Object[]{feas, mat},
					vs);
			c.addOption(options);
			return c;
		}

	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list
	 * of infeasible tuples
	 *
	 * @param tuples :  a list of int[] corresponding to infeasible tuples
	 * @param vars : scope of variables
	 * @return Constraint
	 */
	public static Constraint infeasTupleFC(List<int[]> tuples, IntegerVariable... vars) {
		return makeTupleACFC("cp:fc", vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Forward Checking on a given a given list
	 * of feasible tuples
	 *
	 * @param tuples :  a list of int[] corresponding to feasible tuples
	 * @param vars : scope of variables
	 * @return Constraint
	 */
	public static Constraint feasTupleFC(List<int[]> tuples, IntegerVariable... vars) {
		return makeTupleACFC("cp:fc", vars, tuples, true);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list
	 * of infeasible tuples
	 *
	 * @param tuples :  a list of int[] corresponding to infeasible tuples
	 * @param vars : scope of variables
	 * @return Constraint
	 */
	public static Constraint infeasTupleAC(List<int[]> tuples, IntegerVariable... vars) {
		return makeTupleACFC("cp:ac32", vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list
	 * of feasible tuples
	 *
	 * @param tuples :  a list of int[] corresponding to feasible tuples
	 * @param vars : scope of variables
	 * @return Constraint
	 */
	public static Constraint feasTupleAC(List<int[]> tuples, IntegerVariable... vars) {
		return makeTupleACFC("cp:ac32", vars, tuples, true);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list
	 * of infeasible tuples
	 *
	 * @param options :      specifies the desired ac algorithm : 32 or 2001
	 * @param tuples  :  a list of int[] corresponding to infeasible tuples
	 * @param vars : scope variables
	 * @return Constraint
	 */
	public static Constraint infeasTupleAC(String options, List<int[]> tuples, IntegerVariable... vars) {
		return makeTupleACFC(options, vars, tuples, false);
	}

	/**
	 * Create a constraint enforcing Arc Consistency on a given a given list
	 * of feasible tuples
	 *
	 * @param options :      specifies the desired ac algorithm among : 32 or 2001
	 * @param tuples :  a list of int[] corresponding to feasible tuples
	 * @param vars : scope of variables
	 * @return Constraint
	 */
	public static Constraint feasTupleAC(String options, List<int[]> tuples, IntegerVariable... vars) {
		return makeTupleACFC(options, vars, tuples, true);
	}


	/**
	 * Create a constraint enforcing Forward Checking on a given consistency
	 * relation
	 *
	 * @param vs array of variables
	 * @param rela relation
	 * @return Constraint
	 */
	public static Constraint relationTupleFC(IntegerVariable[] vs, LargeRelation rela) {
		return makeTupleACFC("cp:fc", vs, rela, false);
	}


	/**
	 * Create a constraint enforcing Arc Consistency on a given consistency
	 * relation defined by infeasible tuples. It can also be used for feasible
	 * tuples but will be less efficient than the use of an IterLargeRelation
	 *
	 * @param vs array of variables
	 * @param rela relation
	 * @return Constraint
	 */
	public static Constraint relationTupleAC(IntegerVariable[] vs, LargeRelation rela) {
		return makeTupleACFC(null, vs, rela, false);
	}

	public static Constraint relationTupleAC(String options, IntegerVariable[] vs, LargeRelation rela) {
		return makeTupleACFC(options, vs, rela, false);
	}


	/**
	 * Ensures |x-y| = c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param c : the distance
	 * @return Constraint
	 */
	public static Constraint distanceEQ(IntegerVariable x, IntegerVariable y, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 0, new IntegerVariable[]{x, y, constant(c)});
	}

	/**
	 * Ensures |x-y| != c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param c : the distance
	 * @return Constraint
	 */
	public static Constraint distanceNEQ(IntegerVariable x, IntegerVariable y, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 3, new IntegerVariable[]{x, y, constant(c)});
	}


	/**
	 * Ensures |x-y| < c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param c : the distance
	 * @return Constraint
	 */
	public static Constraint distanceLT(IntegerVariable x, IntegerVariable y, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 1, new IntegerVariable[]{x, y, constant(c)});
	}

	/**
	 * Ensures |x-y| > c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param c : the distance
	 * @return Constraint
	 */
	public static Constraint distanceGT(IntegerVariable x, IntegerVariable y, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 2, new IntegerVariable[]{x, y, constant(c)});
	}

	/**
	 * Ensures |x-y| = z + c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param z = the third variable
	 * @param c : the constant
	 * @return Constraint
	 */
	public static Constraint distanceEQ(IntegerVariable x, IntegerVariable y, IntegerVariable z, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 0, new IntegerVariable[]{x, y, z, constant(c)});
	}

	/**
	 * Ensures |x-y| = z;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param z the third variable
	 * @return Constraint
	 */
	public static Constraint distanceEQ(IntegerVariable x, IntegerVariable y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 0, new IntegerVariable[]{x, y, z, constant(0)});
	}


	/**
	 * Ensures |x-y| < z + c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param z the third variable
	 * @param c the constant
	 * @return Constraint
	 */
	public static Constraint distanceLT(IntegerVariable x, IntegerVariable y, IntegerVariable z, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 1, new IntegerVariable[]{x, y, z, constant(c)});
	}

	/**
	 * Ensures |x-y| < z;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param z the third variable
	 * @return Constraint
	 */
	public static Constraint distanceLT(IntegerVariable x, IntegerVariable y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 1, new IntegerVariable[]{x, y, z, constant(0)});
	}

	/**
	 * Ensures |x-y| > z + c;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param z the third variable
	 * @param c the constant
	 * @return Constraint
	 */
	public static Constraint distanceGT(IntegerVariable x, IntegerVariable y, IntegerVariable z, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 2, new IntegerVariable[]{x, y, z, constant(c)});
	}

	/**
	 * Ensures |x-y| > z;
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param z the third variable
	 * @return Constraint
	 */
	public static Constraint distanceGT(IntegerVariable x, IntegerVariable y, IntegerVariable z) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.DISTANCE, 2, new IntegerVariable[]{x, y, z, constant(0)});
	}

	/**
	 * Ensures x = Math.abs(y);
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @return Constraint
	 */
	public static Constraint abs(IntegerVariable x, IntegerVariable y) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.ABS, null, new IntegerVariable[]{x, y});
	}

	/**
	 * Ensures the variable min to represent the minimum value in vars that occurs in the sublist associated with set.
	 * An element vars[i] belongs to the sublist if the set contains i.
	 * @param svar the set which defined the sublist
	 * @param vars  List of variables
	 * @param min Variable to represent the maximum among the sublist
	 * @return Constraint
	 */
	public static Constraint min(SetVariable svar,IntegerVariable[] vars, IntegerVariable min) {
		final Variable[] tmp = new Variable[vars.length + 2];
		tmp[0] = svar;
		arraycopy(vars, 0, tmp, 1, vars.length);
		tmp[tmp.length - 1] = min;
		return new ComponentConstraint<Variable>(ConstraintType.MIN, true, tmp);
	}
	/**
	 * Ensures the variable "min" to represent the minimum value
	 * that occurs in the list vars
	 *
	 * @param vars List of variables
	 * @param min  Variable to represent the minimum among vars
	 * @return Constraint
	 */
	public static Constraint min(IntegerVariable[] vars, IntegerVariable min) {
		Variable[] tmp = new Variable[vars.length + 1];
		arraycopy(vars, 0, tmp, 0, vars.length);
		tmp[tmp.length - 1] = min;
		return new ComponentConstraint<Variable>(ConstraintType.MIN, true, tmp);
	}



	/**
	 * Ensures the variable "max" to represent the maximum value vars that occurs in the sublist associated with the set.
	 * An element vars[i] belongs to the sublist if the set contains i.
	 * @param svar the set which defined the sublist
	 * @param vars  List of variables
	 * @param max Variable to represent the maximum among the sublist
	 * @return Constraint
	 */
	public static Constraint max(SetVariable svar,IntegerVariable[] vars, IntegerVariable max) {
		final Variable[] tmp = new Variable[vars.length + 2];
		tmp[0] = svar;
		arraycopy(vars, 0, tmp, 1, vars.length);
		tmp[tmp.length - 1] = max;
		return new ComponentConstraint<Variable>(ConstraintType.MAX, false, tmp);
	}
	/**
	 * Ensures the variable "max" to represent the maximum value
	 * that occurs in the list vars
	 *
	 * @param vars List of variables
	 * @param max  Variable to represent the maximum among vars
	 * @return Constraint
	 */
	public static Constraint max(IntegerVariable[] vars, IntegerVariable max) {
		Variable[] tmp = new Variable[vars.length + 1];
		arraycopy(vars, 0, tmp, 0, vars.length);
		tmp[tmp.length - 1] = max;
		return new ComponentConstraint<Variable>(ConstraintType.MAX, false, tmp);
	}

	/**
	 * Ensures the variable "min" to represent the minimum value
	 * of x and y.
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param min Variable to represent the minimum among vars
	 * @return Constraint
	 */
	public static Constraint min(IntegerVariable x, IntegerVariable y, IntegerVariable min) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MIN, true, new IntegerVariable[]{x, y, min});
	}

	/**
	 * Ensures the variable "min" to represent the minimum value
	 * of x and y.
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param min Variable to represent the minimum among vars
	 * @return Constraint
	 */
	public static Constraint min(int x, IntegerVariable y, IntegerVariable min) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MIN, true, new IntegerVariable[]{constant(x), y, min});
	}

	/**
	 * Ensures the variable "min" to represent the minimum value
	 * of x and y.
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param min Variable to represent the minimum among vars
	 * @return Constraint
	 */
	public static Constraint min(IntegerVariable x, int y, IntegerVariable min) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MIN, true, new IntegerVariable[]{x, constant(y), min});
	}

	/**
	 * Ensures the variable "max" to represent the maximum value
	 * of x and y.
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param max Variable to represent the maximum among vars
	 * @return Constraint
	 */
	public static Constraint max(IntegerVariable x, IntegerVariable y, IntegerVariable max) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MAX, false, new IntegerVariable[]{x, y, max});
	}

	/**
	 * Ensures the variable "max" to represent the maximum value
	 * of x and y.
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param max Variable to represent the maximum among vars
	 * @return Constraint
	 */
	public static Constraint max(int x, IntegerVariable y, IntegerVariable max) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MAX, false, new IntegerVariable[]{constant(x), y, max});
	}

	/**
	 * Ensures the variable "max" to represent the maximum value
	 * of x and y.
	 *
	 * @param x the first variable
	 * @param y the second variable
	 * @param max Variable to represent the maximum among vars
	 * @return Constraint
	 */
	public static Constraint max(IntegerVariable x, int y, IntegerVariable max) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MAX, false, new IntegerVariable[]{x, constant(y), max});
	}

	/**
	 * Ensures that the occurrence variable contains the number of occurrences of the given value in the list of
	 * variables
	 *
	 * @param value : the observed value
	 * @param vars       List of variables where the value can appear
	 * @param occurrence The variable that should contain the occurence number
	 * @return Constraint
	 */

	public static Constraint occurrence(int value, IntegerVariable occurrence, IntegerVariable... vars) {
		IntegerVariable[] variables = new IntegerVariable[vars.length + 2];
		variables[0] = constant(value);
		variables[1] = occurrence;
		arraycopy(vars, 0, variables, 2, vars.length);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.OCCURRENCE, 0, variables);
	}

	/**
	 * Ensures that the lower bound of occurrence is at least equal to the number of occurrences
	 * size{forall v in vars | v = value} <= occurence
	 * @param value the observed value
	 * @param occurrence the variable that should contain the occurence numbre
	 * @param vars list of variable where the value can appear
	 * @return Constraint
	 */
	public static Constraint occurrenceMin(int value, IntegerVariable occurrence, IntegerVariable... vars) {
		IntegerVariable[] variables = new IntegerVariable[vars.length + 2];
		variables[0] = constant(value);
		variables[1] = occurrence;
		arraycopy(vars, 0, variables, 2, vars.length);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.OCCURRENCE, -1, variables);
	}

	/**
	 * Ensures that the upper bound of occurrence is at most equal to the number of occurrences
	 * size{forall v in vars | v = value} >= occurence
	 * @param value the observed value
	 * @param occurrence the variable that should contain the occurence numbre
	 * @param vars list of variable where the value can appear
	 * @return Constraint
	 */
	public static Constraint occurrenceMax(int value, IntegerVariable occurrence, IntegerVariable... vars) {
		IntegerVariable[] variables = new IntegerVariable[vars.length + 2];
		variables[0] = constant(value);
		variables[1] = occurrence;
		arraycopy(vars, 0, variables, 2, vars.length);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.OCCURRENCE, 1, variables);
	}

	/**
	 * subscript constraint: accessing an array with a variable index
	 * @param index the index variable
	 * @param values the possible value
	 * @param val the indexth value
	 * @return Constraint
	 */
	public static Constraint nth(IntegerVariable index, int[] values, IntegerVariable val) {
		return nth(index, values, val, 0);
	}

	/**
	 * subscript constraint: accessing an array with a variable index
	 * The offset can be used when the index variable needs to be shifted of a given value (the offset)
	 * @param index the index variable
	 * @param values the possible value
	 * @param offset the offset value
	 * @param val the indexth value
	 * @return Constraint
	 */
	public static Constraint nth(IntegerVariable index, int[] values, IntegerVariable val, int offset) {
		IntegerVariable[] vars = new IntegerVariable[values.length+2];
		for (int i = 0; i < values.length; i++) {
			vars[i] = constant(values[i]);
		}
		vars[vars.length-2] = index;
		vars[vars.length-1] = val;
		return new ComponentConstraint<IntegerVariable>(ConstraintType.NTH, offset, vars);
	}


	/**
	 * subscript constraint: accessing an array of variables with a variable index
	 * @param index the index variable
	 * @param varArray array of possible variables
	 * @param val indexth variable
	 * @return Constraint
	 */
	public static Constraint nth(IntegerVariable index, IntegerVariable[] varArray, IntegerVariable val) {
		IntegerVariable[] vars = ArrayUtils.append(varArray, new IntegerVariable[]{index, val});
		return new ComponentConstraint<IntegerVariable>(ConstraintType.NTH, 0, vars);
	}

	/**
	 * subscript constraint: accessing a matix of variables with two variables indexes
	 * @param index variable index in the first dimension
	 * @param index2 variable index in the first dimension
	 * @param varArray matrix of value
	 * @param val the resulting variable
	 * @return Constraint
	 */
	public static Constraint nth(IntegerVariable index, IntegerVariable index2, int[][] varArray, IntegerVariable val) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.NTH, varArray, new IntegerVariable[]{index, index2, val});
	}

	/**
	 * subscript constraint: accessing an array of variables with a variable index
	 * The offset can be used when the index variable needs to be shifted of a given value (the offset)
	 * @param index index variable in the array
	 * @param varArray array of variables
	 * @param val resulting variable
	 * @param offset the offset value
	 * @return Constraint
	 */
	public static Constraint nth(IntegerVariable index, IntegerVariable[] varArray, IntegerVariable val, int offset) {
		IntegerVariable[] vars = ArrayUtils.append(varArray, new IntegerVariable[]{index, val});
		return new ComponentConstraint<IntegerVariable>(ConstraintType.NTH, offset, vars);
	}


	/**
	 * State a simple channeling bewteen a boolean variable and an interger variable
	 * Ensures for that b = 1 iff x = j
	 *
	 * @param b : a boolean variable
	 * @param x : an integer variable
	 * @param j : the value such that b = 1 ssi x = j, and b = 0 otherwise
	 * @return Constraint
	 */
	public static Constraint boolChanneling(IntegerVariable b, IntegerVariable x, int j) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.CHANNELING, ConstraintType.CHANNELING, new IntegerVariable[]{b, x, constant(j)});
	}

	/**
	 * State a channeling bewteen two arrays of integer variables x and y with the same domain which enforces
	 * x[i] = j <=> y[j] = i
	 * @param x the first array of variables
	 * @param y the second array of variables
	 * @return Constraint
	 */
	public static Constraint inverseChanneling(IntegerVariable[] x, IntegerVariable[] y) {
		if (y.length != x.length) {
			throw new SolverException("not a valid inverse channeling constraint with two arrays of different sizes");
		}
		return new ComponentConstraint<IntegerVariable>(ConstraintType.INVERSECHANNELING, ConstraintType.INVERSECHANNELING, ArrayUtils.append(x, y));
	}

	/**
	 * All different constraints with a global filtering :
	 * v1 != v2, v1 != v3, v2 != v3 ... For each (i,j), v_i != v_j
	 * If vars is a table of BoundIntegerVariable a dedicated algorithm is used. In case
	 * of EnumIntegerVariable it is the regin alldifferent.
	 * @param vars list of variables
	 * @return Constraint
	 */
	public static Constraint allDifferent(IntegerVariable... vars) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.ALLDIFFERENT, null, vars);
	}

	/**
	 * All different constraints with a global filtering :
	 * v1 != v2, v1 != v3, v2 != v3 ... For each (i,j), v_i != v_j
	 * The options can contain the folowing options :
	 * <ul>
	 * <li> cp:ac for Regin implementation
	 * <li> cp:bc for bound all different using the propagator of
	 * A. Lopez-Ortiz, C.-G. Quimper, J. Tromp, and P. van Beek.
	 * A fast and simple algorithm for bounds consistency of the alldifferent
	 * constraint. IJCAI-2003.
	 * <li> cp:clique for propagating the clique of differences
	 * </ul>
	 * @param options options of the constraint
	 * @param vars list of variables
	 * @return Constraint
	 */
	public static Constraint allDifferent(String options, IntegerVariable... vars) {
		Constraint c = allDifferent(vars);
		c.addOption(options);
		return c;
	}

	private static int getMinOfLowB(IntegerVariable[] vars) {
		int minval = Integer.MAX_VALUE;
		for (IntegerVariable var : vars) {
			if (var.getLowB() < minval)
				minval = var.getLowB();
		}
		return minval;
	}

	private static int getMaxOfUppB(IntegerVariable[] vars) {
		int maxval = Integer.MIN_VALUE;
		for (IntegerVariable var : vars) {
			if (var.getUppB() > maxval)
				maxval = var.getUppB();
		}
		return maxval;
	}

	private static void globalCardinalityTest(IntegerVariable[] vars, int[] low, int[] up){
		if (low.length != up.length) {
			throw new ModelException("globalCardinality : low and up do not have same size");
		}
		int sumL = 0;
		for(int i = 0; i < low.length; i++){
			sumL += low[i];
			if(low[i] > up[i]) {
				throw new ModelException("globalCardinality : incorrect low and up ("+i+")");
			}
		}

		if (vars.length < sumL) {
			throw new ModelException("globalCardinality : not enough minimum values");
		}
	}

	/**
	 * Concerns GCC and boundGCC
	 * <p/>
	 * Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 * <p/>
	 * Bound Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 * Use the propagator of :
	 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
	 * An efficient bounds consistency algorithm for the global cardinality constraint.
	 * CP-2003.
	 * @param vars list of variables
	 * @param min minimum allowed value
	 * @param max maximum allowed value
	 * @param low array of lower occurence
	 * @param up array of upper occurence
	 * @return Constraint
	 * @deprecated
	 * @see Choco#globalCardinality(IntegerVariable[] vars, int[] low, int[] up)
	 */

	public static Constraint globalCardinality(IntegerVariable[] vars, int min, int max, int[] low, int[] up) {
		globalCardinalityTest(vars, low, up);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.GLOBALCARDINALITY,
				new Object[]{ConstraintType.GLOBALCARDINALITYMAX, min, max, low, up}, vars);
	}

	/**
	 * Concerns GCC and boundGCC
	 * <p/>
	 * Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 * <p/>
	 * Bound Global cardinality : Given an array of variables vars, min the minimal value over all variables,
	 * and max the maximal value over all variables, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1.
	 * Use the propagator of :
	 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
	 * An efficient bounds consistency algorithm for the global cardinality constraint.
	 * CP-2003.
	 * <p/>
	 * Available options are:
	 * <ul>
	 * <i>cp:ac for Regin impelmentation</i>
	 * <i>cp:bc for bound consistency</i>
	 * </ul>
	 * @param options options of the constraint
	 * @param vars list of variables
	 * @param min minimum allowed value
	 * @param max maximum allowed value
	 * @param low array of lower occurence
	 * @param up array of upper occurence
	 * @return Constraint
	 * @deprecated
	 * @see Choco#globalCardinality(String options, IntegerVariable[] vars, int[] low, int[] up)
	 */
	public static Constraint globalCardinality(String options, IntegerVariable[] vars, int min, int max, int[] low, int[] up) {
		@SuppressWarnings({"deprecation"})
		Constraint c = globalCardinality(vars, min, max, low, up);
		c.addOption(options);
		return c;
	}

	/**
	 * Concerns GCC and boundGCC
	 * <p/>
	 * Global cardinality : Given an array of variables vars, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1. (min is the minimal value over all variables,
	 * and max the maximal value over all variables)
	 * <p/>
	 * Bound Global cardinality : Given an array of variables vars, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1. (min is the minimal value over all variables,
	 * and max the maximal value over all variables)
	 * Use the propagator of :
	 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
	 * An efficient bounds consistency algorithm for the global cardinality constraint.
	 * CP-2003.
	 * <p/>
	 * Available options are:
	 * <ul>
	 * <i>cp:ac for Regin impelmentation</i>
	 * <i>cp:bc for bound consistency</i>
	 * </ul>
	 * @param vars list of variables
	 * @param low array of lower occurence
	 * @param up array of upper occurence
	 * @return Constraint
	 */
	public static Constraint globalCardinality(IntegerVariable[] vars, int[] low, int[] up) {
		int min  = getMinOfLowB(vars);
		int max = getMaxOfUppB(vars);
		globalCardinalityTest(vars, low, up);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.GLOBALCARDINALITY,
				new Object[]{ConstraintType.GLOBALCARDINALITYMAX, min, max, low, up}, vars);
	}

	/**
	 * Concerns GCC and boundGCC
	 * <p/>
	 * Global cardinality : Given an array of variables vars, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1. (min is the minimal value over all variables,
	 * and max the maximal value over all variables)
	 * <p/>
	 * Bound Global cardinality : Given an array of variables vars, the constraint ensures that the number of occurences
	 * of the value i among the variables is between low[i - min] and up[i - min]. Note that the length
	 * of low and up should be max - min + 1. (min is the minimal value over all variables,
	 * and max the maximal value over all variables)
	 * Use the propagator of :
	 * C.-G. Quimper, P. van Beek, A. Lopez-Ortiz, A. Golynski, and S.B. Sadjad.
	 * An efficient bounds consistency algorithm for the global cardinality constraint.
	 * CP-2003.
	 * <p/>
	 * Available options are:
	 * <ul>
	 * <i>cp:ac for Regin impelmentation</i>
	 * <i>cp:bc for bound consistency</i>
	 * </ul>
	 * @param options options of the constraint
	 * @param vars list of variables
	 * @param low array of lower occurence
	 * @param up array of upper occurence
	 * @return Constraint
	 */
	public static Constraint globalCardinality(String options, IntegerVariable[] vars, int[] low, int[] up) {
		Constraint c = globalCardinality(vars, low, up);
		c.addOption(options);
		return c;
	}


	/**
	 * * Bound Global cardinality : Given an array of variables vars, an array of variables card to represent the cardinalities, the constraint ensures that the number of occurences
	 * of the value i among the variables is equal to card[i].
	 * this constraint enforces :
	 * - Bound Consistency over vars regarding the lower and upper bounds of cards
	 * - maintain the upperbound of card by counting the number of variables in which each value
	 * can occur
	 * - maintain the lowerbound of card by counting the number of variables instantiated to a value
	 * - enforce card[0] + ... + card[m] = n (n = the number of variables, m = number of values)
	 *
	 * @param vars list of variables
	 * @param min minimum allowed value
	 * @param max maximum allowed value
	 * @param card array of cardinality variables
	 * @return Constraint
	 * @deprecated
	 * @see Choco#globalCardinality(IntegerVariable[] vars, IntegerVariable[] card)
	 */
	public static Constraint globalCardinality(IntegerVariable[] vars, int min, int max, IntegerVariable[] card) {
		int n = vars.length;
		IntegerVariable[] variables = new IntegerVariable[vars.length + card.length];
		arraycopy(vars, 0, variables, 0, n);
		arraycopy(card, 0, variables, n, card.length);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.GLOBALCARDINALITY, new Object[]{ConstraintType.GLOBALCARDINALITYVAR, min, max, n}, variables);
	}

	/**
	 * * Bound Global cardinality : Given an array of variables vars, an array of variables card to represent the cardinalities, the constraint ensures that the number of occurences
	 * of the value i among the variables is equal to card[i].
	 * this constraint enforces :
	 * - Bound Consistency over vars regarding the lower and upper bounds of cards
	 * - maintain the upperbound of card by counting the number of variables in which each value
	 * can occur
	 * - maintain the lowerbound of card by counting the number of variables instantiated to a value
	 * - enforce card[0] + ... + card[m] = n (n = the number of variables, m = number of values)
	 *
	 * @param vars list of variables
	 * @param card array of cardinality variables
	 * @return Constraint
	 */
	public static Constraint globalCardinality(IntegerVariable[] vars, IntegerVariable[] card) {
		int n = vars.length;
		IntegerVariable[] variables = new IntegerVariable[vars.length + card.length];
		arraycopy(vars, 0, variables, 0, n);
		arraycopy(card, 0, variables, n, card.length);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.GLOBALCARDINALITY,
				new Object[]{ConstraintType.GLOBALCARDINALITYVAR, getMinOfLowB(vars), getMaxOfUppB(vars), n}, variables);
	}

	/**
	 * Enforce the minimal and maximal sizes of the streches of any value given in strechesParameters.
	 * Usefull for Rostering Problems. The constraint is implemented by a Regular constraint that
	 * perform GAC.
	 *
	 * @param vars                : a sequence of variables
	 * @param stretchesParameters : a list of triples of integers :
	 *                            (value, occmin, occmax) denoting for each value the minimal and maximal
	 *                            lenght of any stretch of the corresponding value.
	 * @return Constraint
	 */
	public static Constraint stretchPath(List<int[]> stretchesParameters, IntegerVariable... vars) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.STRETCHPATH, stretchesParameters, vars);
	}




	public static Constraint pack(int[] sizes, int nbBins, int capacity, String... options) {
		return pack(new PackModeler(sizes, nbBins, capacity),options);
	}

	public static Constraint pack(PackModeler modeler,String... options) {
		return pack(modeler.itemSets, modeler.loads, modeler.bins, modeler.sizes, modeler.nbNonEmpty, options);
	}
	public static Constraint pack(SetVariable[] itemSets, IntegerVariable[] loads, IntegerVariable[] bins, IntegerConstantVariable[] sizes, String... options) {
		return pack(itemSets, loads, bins, sizes, makeIntVar("nbNonEmpty", 0,loads.length,"cp:bound"),options);
	}

	public static Constraint pack(SetVariable[] itemSets, IntegerVariable[] loads, IntegerVariable[] bins, IntegerConstantVariable[] sizes,IntegerVariable nbNonEmpty, String... options) {
		//check pre-conditions
		if(itemSets.length!=loads.length || bins.length != sizes.length){
			throw new ModelException("lenght of arrays are invalid");
		}
		final int n= bins.length;
		final int m=loads.length;
		for (int i = 1; i < n; i++) {
			if(sizes[i].getValue() > sizes[i-1].getValue()) {
				throw new ModelException("sizes must be sorted according to non increasing order.");
			}
		}
		Variable[] vars = new Variable[2*(n+m)+1];
		arraycopy(itemSets, 0, vars, 0, m);
		arraycopy(loads, 0, vars, m, m);
		arraycopy(bins, 0, vars, 2*m, n);
		arraycopy(sizes, 0, vars, 2*m+n,n);
		vars[vars.length-1]=nbNonEmpty;
		Constraint pack = new ComponentConstraint<Variable>(ConstraintType.PACK, new Object[]{n,m},vars);
		pack.addOptions(options);
		return pack;
	}

	//Cumulative Min-Max
	/**
	 * Cumulative : Given a set of tasks defined by their starting dates, ending dates, durations and
	 * heights, the cumulative ensures that at any time t, the sum of the heights of the tasks
	 * which are executed at time t does not exceed a given limit C (the capacity of the ressource) and is not lower than C2 (minimal consumption of the resource).
	 * The implementation is based on the paper of Bediceanu and al :
	 * "A new multi-resource cumulatives constraint with negative heights" in CP02
	 * @param name name of the resource (<code>null</code> is authorized)
	 * @param tasks the set of involved tasks
	 * @param heights the heights of the tasks for the given resource
	 * @param usages is a boolean variable array which indicates if tasks are used by the resource. If usages.lenght is lower than tasks.length, then the usages.length last tasks are optional and the first ones are regular tasks.
	 * @param consumption the minimal consumption required for the resource
	 * @param capacity the capacity of the resource
	 * @param uppBound An upper bound for the makespan of the given resource. If the value is  (<code>null</code>, then we set the global solver makespan variable.
	 * @param options filtering options, see SettingType enum.
	 * @return Constraint
	 */
	public static Constraint cumulative(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable[] usages, IntegerVariable consumption, IntegerVariable capacity, IntegerVariable uppBound, String... options) {
		//check parameters
		if(tasks == null || heights == null || tasks.length == 0 || tasks.length != heights.length) {
			throw new ModelException("can't build cumulative constraint "+name+" : Tasks and heights arrays are nil or have different size.");
		}
		if( usages != null && usages.length > tasks.length) {
			throw new ModelException("can't build cumulative constraint "+name+" : usage array have a greater length than task array.");
		}
		if(consumption == null) {
			LOGGER.log(Level.WARNING, "replace nil consumption in cumulative {0}", name);
			consumption = constant(0);
		}
		if(capacity == null) {
			LOGGER.log(Level.WARNING, "replace nil capacity in cumulative {0}", name);
			consumption = constant(MAX_UPPER_BOUND);
		}
		//build constraint
		RscData param = new RscData(name, tasks, usages, uppBound);
		final Variable[] vars=  ArrayUtils.append(tasks, usages, heights, new IntegerVariable[]{consumption, capacity}, uppBound == null ? null : new IntegerVariable[]{uppBound});
		final ComponentConstraint c=new ComponentConstraint<Variable>(ConstraintType.CUMULATIVE, param,vars);
		c.addOptions(options);
		return c;

	}

	public static Constraint cumulative(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable[] usages, IntegerVariable consumption, IntegerVariable capacity, String... options) {
		return cumulative(name, tasks, heights, usages, consumption, capacity, null, options);
	}


	public static Constraint cumulative(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable consumption, IntegerVariable capacity, String... options) {
		return cumulative(name, tasks, heights,null, consumption, capacity, null, options);
	}

	//Cumulative Max
	public static Constraint cumulativeMax(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable[] usages, IntegerVariable capacity, String... options) {
		return cumulative(name, tasks, heights, usages, constant(0), capacity, null, options);
	}

	public static Constraint cumulativeMax(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable capacity, String... options) {
		return cumulative(name, tasks, heights, null, constant(0), capacity, null, options);
	}

	public static Constraint cumulativeMax(TaskVariable[] tasks, int[] heights, int capacity, String... options) {
		return cumulativeMax(null, tasks, constantArray(heights), constant(capacity), options);
	}

	//Cumulative Min
	public static Constraint cumulativeMin(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable[] usages, IntegerVariable consumption, String... options) {
		return cumulative(name, tasks, heights, usages, consumption, constant(Integer.MAX_VALUE), null, options);
	}

	public static Constraint cumulativeMin(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable consumption, String... options) {
		return cumulative(name, tasks, heights, null, consumption, constant(Integer.MAX_VALUE), null, options);
	}

	public static Constraint cumulativeMin(TaskVariable[] tasks, int[] heights, int consumption, String... options) {
		return cumulativeMin(null, tasks, constantArray(heights), constant(consumption), options);
	}



	@Deprecated
	public static Constraint cumulative(String name, TaskVariable[] tasks, IntegerVariable[] heights, IntegerVariable capa, String... options) {
		return cumulative(name, tasks, heights,null, constant(0),  capa, null, options);
	}

	@Deprecated
	public static Constraint cumulative(TaskVariable[] tasks, int[] heights, int capa, String... options) {
		return cumulative(null, tasks, constantArray(heights), null, constant(0),constant(capa), null,options);
	}

	@Deprecated
	public static Constraint cumulative(String name, IntegerVariable[] starts, IntegerVariable[] ends, IntegerVariable[] durations, IntegerVariable[] heights, IntegerVariable capa, String... options) {
		final TaskVariable[] tasks = makeTaskVarArray("t", starts, ends, durations);
		return cumulative(name, tasks, heights, constant(0), capa, options);

	}
	@Deprecated
	public static Constraint cumulative(IntegerVariable[] starts, IntegerVariable[] ends, IntegerVariable[] durations, IntegerVariable[] heights, IntegerVariable capa, String... options) {
		TaskVariable[] t = new TaskVariable[starts.length];
		for(int i = 0; i < starts.length; i++){
			t[i] = makeTaskVar("", starts[i], ends[i], durations[i]);
		}
		return cumulative(null, t, heights, constant(0), capa, options);
	}
	@Deprecated
	public static Constraint cumulative(IntegerVariable[] starts, IntegerVariable[] ends, IntegerVariable[] durations, int[] heights, int capa, String... options) {
		TaskVariable[] t = new TaskVariable[starts.length];
		for(int i = 0; i < starts.length; i++){
			t[i] = makeTaskVar("", starts[i], ends[i], durations[i]);
		}
		return cumulative(null, t, constantArray(heights), constant(0), constant(capa), options);
	}
	@Deprecated
	public static Constraint cumulative(IntegerVariable[] starts, IntegerVariable[] durations, IntegerVariable[] heights, IntegerVariable capa, String... options) {
		TaskVariable[] t = new TaskVariable[starts.length];
		for(int i = 0; i < starts.length; i++){
			t[i] = makeTaskVar("", starts[i], durations[i]);
		}
		return cumulative(null, t, heights, constant(0),capa, options);
	}



	/**
	 * Returns a disjunctive constraint
	 *
	 * @param starts to schedule
	 * @param durations of each task
	 * @param options options of the variable
	 * @return the disjunctive constraint
	 */
	@Deprecated
	public static Constraint disjunctive(IntegerVariable[] starts, int[] durations,String...options) {
		TaskVariable[] t = new TaskVariable[starts.length];
		for(int i = 0; i < starts.length; i++){
			t[i] = makeTaskVar("", starts[i], constant(durations[i]));
		}
		return disjunctive(null, t,  null, options);
	}

	@Deprecated
	public static Constraint disjunctive(IntegerVariable[] starts, IntegerVariable[] durations, String... options) {
		TaskVariable[] t = new TaskVariable[starts.length];
		for(int i = 0; i < starts.length; i++){
			t[i] = makeTaskVar("", starts[i], durations[i]);
		}
		return disjunctive(null, t,  null, options);
	}

	@Deprecated
	public static Constraint disjunctive(IntegerVariable[] starts, IntegerVariable[] ends, IntegerVariable[] durations,String... options) {
		TaskVariable[] t = new TaskVariable[starts.length];
		for(int i = 0; i < starts.length; i++){
			t[i] = makeTaskVar("", starts[i], ends[i], durations[i]);
		}
		return disjunctive(null, t,  null, options);
	}

	@Deprecated
	public static Constraint disjunctive(String name, IntegerVariable[] starts, IntegerVariable[] ends, IntegerVariable[] durations,IntegerVariable uppBound, String... options) {
		final TaskVariable[] tasks = makeTaskVarArray("task-", starts, ends, durations);
		return disjunctive(name, tasks,null, uppBound, options);
	}

	public static Constraint disjunctive(TaskVariable[] tasks, String... options) {
		return disjunctive(null, tasks, options);
	}

	public static Constraint disjunctive(String name, TaskVariable[] tasks, String... options) {
		return disjunctive(name, tasks,null, options);
	}


	public static Constraint disjunctive(TaskVariable[] tasks,IntegerVariable[] usages, String... options) {
		return disjunctive(null, tasks, usages, options);
	}

	public static Constraint disjunctive(String name, TaskVariable[] tasks,IntegerVariable[] usages, String... options) {
		return disjunctive(name, tasks, usages, null, options);
	}

	/**
	 * create a disjunctive constraint. A disjunctive constraint holds if All the regular tasks that have a duration strictly greater than 0 should not overlap.
	 * @param name name of the resource (<code>null</code> is authorized)
	 * @param tasks the set of involved tasks
	 * @param usages is a boolean variable array which indicates if tasks are used by the resource. If usages.lenght is lower than tasks.length, then the usages.length last tasks are optional and the first ones are regular tasks.
	 * @param uppBound An upper bound for the makespan of the given resource. If the value is  (<code>null</code>, then we set the global solver makespan variable.
	 * @param options filtering options, see SettingType enum.
	 * @return Constraint
	 */
	public static Constraint disjunctive(String name, TaskVariable[] tasks,IntegerVariable[] usages, IntegerVariable uppBound, String... options) {
		RscData param = new RscData(name, tasks, usages, uppBound);
		Variable[] vars = uppBound==null ? ArrayUtils.<Variable>append(tasks, usages) :   ArrayUtils.append(tasks, usages, new IntegerVariable[]{ uppBound});
		final ComponentConstraint c=new ComponentConstraint<Variable>(ConstraintType.DISJUNCTIVE, param,vars);
		c.addOptions(options);
		return c;
	}


	/**
	 * Each task of the collection tasks1 should not overlap any task of the collection tasks2.
	 * The model only provides a decomposition with reified precedences because the coloured cumulative is not available.
	 * @see {http://www.emn.fr/x-info/sdemasse/gccat/Cdisjoint_tasks.html#uid11633}
	 */
	public static Constraint[] disjoint(TaskVariable[] tasks1,TaskVariable[] tasks2) {
		final Constraint[] decomp = new Constraint[tasks1.length * tasks2.length];
		int idx = 0;
		for (TaskVariable t1 : tasks1) {
			for (TaskVariable t2 : tasks2) {
				decomp[idx++] = precedenceDisjoint(t1, t2, VariableUtils.createDirectionVar(t1, t2));
			}	
		}
		return decomp;		
	}



	/**
	 * @see {@link Choco#precedence(TaskVariable, TaskVariable)}
	 */
	@Deprecated 
	public static Constraint preceding(TaskVariable t1, TaskVariable t2) {
		return precedenceDisjoint(t1, t2, constant(1));
	}


	/**
	 * T1 ends before t2 starts or t1 precedes t2.
	 */
	public static Constraint precedence(TaskVariable t1, TaskVariable t2) {
		return precedenceDisjoint(t1, t2, ONE);
	}

	/**
	 * @see {@link Choco#precedenceDisjoint(IntegerVariable, int, IntegerVariable, int, IntegerVariable)}
	 */
	@Deprecated 
	public static Constraint preceding(IntegerVariable v1, int dur1, IntegerVariable v2, int dur2, IntegerVariable bool) {
		return precedenceDisjoint(v1, dur1, v2, dur2, bool);
	}


	/**
	 * 
	 * <ul>
	 * <li> direction = 1 => v1 + dur1 <= v2 (T1 << T2);
	 * <li> direction = 0 => v2 + dur2 <= v1 (T2 << T1);
	 * </ul>  
	 */
	public static Constraint precedenceDisjoint(IntegerVariable v1, int dur1, IntegerVariable v2, int dur2, IntegerVariable bool) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.PRECEDENCE_DISJOINT, Boolean.FALSE, new IntegerVariable[]{v1, constant(dur1), v2, constant(dur2), bool});
	}


	/**
	 * @see {@link Choco#precedenceDisjoint(TaskVariable, TaskVariable, IntegerVariable)}
	 */
	@Deprecated 
	public static Constraint preceding(TaskVariable t1, TaskVariable t2, IntegerVariable direction) {
		return precedenceDisjoint(t1, t2, direction);
	}


	/**
	 * represents a disjunction without setup times
	 */
	public static Constraint precedenceDisjoint(TaskVariable t1, TaskVariable t2, IntegerVariable direction) {
		return precedenceDisjoint(t1, t2, direction, 0, 0);
	}


	/**
	 * precedence disjoint with setup times:
	 * <ul>
	 * <li> direction = 1 => t1.end() + forwardSetup <= t2.start() (T1 << T2);
	 * <li> direction = 0 => t2.end() + backwardSetup <= t1.start() (T2 << T1);
	 * </ul>  
	 * @param t1 a task
	 * @param t2 the other task
	 * @param direction  boolean variable which reified the precedence relation. 
	 * @param forwardSetup setup times between t1 and t2
	 * @param backwardSetup setup times between t2 and t1.
	 */
	public static Constraint precedenceDisjoint(TaskVariable t1, TaskVariable t2, IntegerVariable direction, int forwardSetup, int backwardSetup) {
		return new ComponentConstraint<Variable>(ConstraintType.PRECEDENCE_DISJOINT, Boolean.TRUE, new Variable[]{t1,constant(forwardSetup), t2, constant(backwardSetup), direction});
	}


	public static Constraint[] precedenceDisjoint(TaskVariable[] clique, String... boolvarOptions) {
		final int n = clique.length;
		Constraint[] cstr = new Constraint[ (n * (n-1) )/2];
		int idx = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				cstr[idx++] = precedenceDisjoint(clique[i], clique[j], VariableUtils.createDirectionVar(clique[i], clique[j], boolvarOptions));
			}
		}
		return cstr;
	}



	/**
	 * represents a reidied precedence: 
	 * <ul>
	 * <li> b = 1 => x1 + k1 <= x2
	 * <li> b = 0 => x1 + k1 > x2
	 * </ul>  
	 * @param x1 the first integer variable.
	 * @param k1 the duration of the precedence.
	 * @param x2 the other integer variable.
	 * @param b the reification boolean variable
	 *
	 */
	public static Constraint precedenceReified(IntegerVariable x1, int k1, IntegerVariable x2, IntegerVariable b) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.PRECEDENCE_REIFIED, Boolean.FALSE, new IntegerVariable[]{x1, constant(k1), x2, ZERO, b});
	}

	/**
	 * represents a reidied precedence with setup times between a pair of tasks: 
	 * <ul>
	 * <li> b = 1 => e1 + k1 <= s2
	 * <li> b = 0 => e1 + k1 > s2
	 * </ul>  
	 */
	public static Constraint precedenceReified(TaskVariable t1, int k1, TaskVariable t2, IntegerVariable b) {
		return new ComponentConstraint<Variable>(ConstraintType.PRECEDENCE_REIFIED, Boolean.TRUE, new Variable[]{t1, constant(k1), t2, ZERO, b});
	}

	/**
	 * represents an implied precedence: 
	 * <ul>
	 * <li> b = 1 => x1 + k1 <= x2
	 * <li> b = 0 => TRUE
	 * </ul>  
	 */
	public static Constraint precedenceImplied(IntegerVariable x1, int k1, IntegerVariable x2, IntegerVariable b) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.PRECEDENCE_IMPLIED, Boolean.FALSE, new IntegerVariable[]{x1, constant(k1), x2, ZERO, b});
	}

	/**
	 * represents a reidied precedence with setup times between a pair of tasks: 
	 * <ul>
	 * <li> b = 1 => e1 + k1 <= s2
	 * <li> b = 0 => TRUE;
	 * </ul>  
	 */
	public static Constraint precedenceImplied(TaskVariable t1, int k1, TaskVariable t2, IntegerVariable b) {
		return new ComponentConstraint<Variable>(ConstraintType.PRECEDENCE_IMPLIED, Boolean.TRUE, new Variable[]{t1, constant(k1), t2, ZERO, b});
	}


	public static Constraint geost(int dim, Vector<GeostObject> objects, Vector<ShiftedBox> shiftedBoxes, Vector<IExternalConstraint> eCtrs) {
		return geost(dim, objects, shiftedBoxes, eCtrs, null);
	}

	public static Constraint geost(int dim, Vector<GeostObject> objects, Vector<ShiftedBox> shiftedBoxes, Vector<IExternalConstraint> eCtrs, Vector<int[]> ctrlVs) {
		return geost(dim, objects, shiftedBoxes, eCtrs, ctrlVs, null);

		//		int originOfObjects = objects.size() * dim; //Number of domain variables to represent the origin of all objects
		//		int otherVariables = objects.size() * 4; //each object has 4 other variables: shapeId, start, duration; end
		//		//vars will be stored as follows: object 1 coords(so k coordinates), sid, start, duration, end,
		//		//                                object 2 coords(so k coordinates), sid, start, duration, end and so on ........
		//		//To retrieve the index of a certain variable, the formula is (nb of the object in question = objId assuming objIds are consecutive and start from 0) * (k + 4) + number of the variable wanted
		//		//the number of the variable wanted is decided as follows: 0 ... k-1 (the coords), k (the sid), k+1 (start), k+2 (duration), k+3 (end)
		//		IntegerVariable[] vars = new IntegerVariable[originOfObjects + otherVariables];
		//        for(int i = 0; i < objects.size(); i++)
		//		{
		//			for (int j = 0; j < dim; j++)
		//			{
		//				vars[(i * (dim + 4)) + j] = objects.elementAt(i).getCoordinates()[j];
		//			}
		//			vars[(i * (dim + 4)) + dim] = objects.elementAt(i).getShapeId();
		//			vars[(i * (dim + 4)) + dim + 1] = objects.elementAt(i).getStartTime();
		//			vars[(i * (dim + 4)) + dim + 2] = objects.elementAt(i).getDurationTime();
		//			vars[(i * (dim + 4)) + dim + 3] = objects.elementAt(i).getEndTime();
		//		}
		//
		//		return new ComponentConstraint(ConstraintType.GEOST, new Object[]{dim, shiftedBoxes, eCtrs, objects, ctrlVs}, vars);
		//		//return new GeostConstraint(dim, objects, shiftedBoxes, eCtrs, ctrlVs);
	}

	//    public static Constraint geost(int dim, Vector<GeostObject> objects, Vector<ShiftedBox> shiftedBoxes, Vector<IExternalConstraint> eCtrs, boolean memo, HashMap<Pair<Integer,Integer>, Boolean> included, Long a, Long b) {
	//        return geost(dim, objects, shiftedBoxes, eCtrs, null,memo,included,a,b,false);
	//    }

	public static Constraint geost(int dim, Vector<GeostObject> objects, Vector<ShiftedBox> shiftedBoxes, Vector<IExternalConstraint> eCtrs, Vector<int[]> ctrlVs, GeostOptions opt) {
		int originOfObjects = objects.size() * dim; //Number of domain variables to represent the origin of all objects
		int otherVariables = objects.size() * 4; //each object has 4 other variables: shapeId, start, duration; end

		/*Collect distance variales due to ditance constraints*/
		Vector<Integer> distVars = new Vector<Integer>();
		for (int i=0; i< eCtrs.size(); i++) {
			IExternalConstraint ectr=eCtrs.elementAt(i);
			if ((ectr instanceof DistLeqModel) && (((DistLeqModel) ectr).hasDistanceVar()))
				distVars.add(i);
			if ((ectr instanceof DistGeqModel) && (((DistGeqModel) ectr).hasDistanceVar()))
				distVars.add(i);
		}


		//vars will be stored as follows: object 1 coords(so k coordinates), sid, start, duration, end,
		//                                object 2 coords(so k coordinates), sid, start, duration, end and so on ........
		//To retrieve the index of a certain variable, the formula is (nb of the object in question = objId assuming objIds are consecutive and start from 0) * (k + 4) + number of the variable wanted
		//the number of the variable wanted is decided as follows: 0 ... k-1 (the coords), k (the sid), k+1 (start), k+2 (duration), k+3 (end)
		/*IntegerVariable model variable*/
		IntegerVariable[] vars = new IntegerVariable[originOfObjects + otherVariables + distVars.size()];

		for(int i = 0; i < objects.size(); i++)
		{
			for (int j = 0; j < dim; j++)
			{
				vars[(i * (dim + 4)) + j] = objects.elementAt(i).getCoordinates()[j];
			}
			vars[(i * (dim + 4)) + dim] = objects.elementAt(i).getShapeId();
			vars[(i * (dim + 4)) + dim + 1] = objects.elementAt(i).getStartTime();
			vars[(i * (dim + 4)) + dim + 2] = objects.elementAt(i).getDurationTime();
			vars[(i * (dim + 4)) + dim + 3] = objects.elementAt(i).getEndTime();
		}

		int ind=0;
		for (int i : distVars) {
			IExternalConstraint ectr=eCtrs.elementAt(i);
			if (ectr instanceof DistLeqModel) {
				vars[originOfObjects + otherVariables  +ind] = (( DistLeqModel) ectr).getDistanceVar();
				System.out.println("Adding "+((DistLeqModel) ectr).modelDVar+" for "+ ectr);
			}
			if (ectr instanceof DistGeqModel) {
				vars[originOfObjects + otherVariables  +ind] = (( DistGeqModel) ectr).getDistanceVar();
				System.out.println("Adding "+((DistGeqModel) ectr).modelDVar+" for "+ ectr);
			}

			ind++;
		}

		return new ComponentConstraint<IntegerVariable>(ConstraintType.GEOST, new Object[]{dim, shiftedBoxes, eCtrs, objects, ctrlVs, opt}, vars);
		//return new GeostConstraint(dim, objects, shiftedBoxes, eCtrs, ctrlVs);
	}

	/**
	 * Enforce a lexicographic ordering on two vectors of integer
	 * variables x <_lex y with x = <x_0, ..., x_n>, and y = <y_0, ..., y_n>.
	 * ref : Global Constraints for Lexicographic Orderings (Frisch and al)
	 * @param v1 the first array of variables
	 * @param v2 the second array of variables
	 * @return Constraint
	 */
	public static Constraint lexeq(IntegerVariable[] v1, IntegerVariable[] v2) {
		int offset = v1.length;
		return new ComponentConstraint<IntegerVariable>(ConstraintType.LEX, new Object[]{ConstraintType.LEXEQ, offset}, ArrayUtils.append(v1, v2));
	}

	/**
	 * Enforce a strict lexicographic ordering on two vectors of integer
	 * variables x <_lex y with x = <x_0, ..., x_n>, and y = <y_0, ..., y_n>.
	 * ref : Global Constraints for Lexicographic Orderings (Frisch and al)
	 * @param v1 the first array of variables
	 * @param v2 the second array of variables
	 * @return Constraint
	 */
	public static Constraint lex(IntegerVariable[] v1, IntegerVariable[] v2) {
		int offset = v1.length;
		return new ComponentConstraint<IntegerVariable>(ConstraintType.LEX, new Object[]{ConstraintType.LEX, offset}, ArrayUtils.append(v1, v2));
	}


	/**
	 * Enforce a  strict  lexicographic ordering on a chain of integer
	 * vectors (X1 ,X2 ,X3,......) with X1 < lex X2 <  lex X3 ....
	 * X1  = <x_10,x_11,x_12,....... upto n variables>
	 * ref : Arc- Consistency for a chain of Lexicographic Ordering Constraints ( N. Beldiceanu and Carlsson)
	 *
	 * @param arrayOfVectors array of variables
	 * @return Constraint
	 */
	public static Constraint lexChain(IntegerVariable[]... arrayOfVectors) {
		int n = arrayOfVectors[0].length;
		IntegerVariable[] vs = new IntegerVariable[arrayOfVectors.length * n];
		for (int i = 0; i < arrayOfVectors.length; i++) {
			if(arrayOfVectors[i].length != n) {
				throw new ModelException("LexChain : every arrays in parameters are of different size");
			}
			arraycopy(arrayOfVectors[i], 0, vs, n * i, n);
		}
		return new ComponentConstraint<IntegerVariable>(ConstraintType.LEXCHAIN, new Object[]{true, n}, vs);
	}

	/**
	 * Enforce a  lexicographic ordering on a chain of integer
	 * vectors (X1 ,X2 ,X3,......) with X1 <= lex X2 <= lex X3 ....
	 * X1  = <x_10,x_11,x_12,....... upto n variables>
	 * ref : Arc- Consistency for a chain of Lexicographic Ordering Constraints ( N. Beldiceanu and Carlsson)
	 *
	 * @param arrayOfVectors array of variables
	 * @return Constraint
	 */
	public static Constraint lexChainEq(IntegerVariable[]... arrayOfVectors) {
		int n = arrayOfVectors[0].length;
		IntegerVariable[] vs = new IntegerVariable[arrayOfVectors.length * n];
		for (int i = 0; i < arrayOfVectors.length; i++) {
			arraycopy(arrayOfVectors[i], 0, vs, n * i, n);
		}
		return new ComponentConstraint<IntegerVariable>(ConstraintType.LEXCHAIN, new Object[]{false, n}, vs);
	}

	/**
	 * Let x and x' be two vectors of variables of the same length, and
	 * v be an instantiation. The sorting constraint sorting(x, x') holds
	 * on the set of variables being either in x or in x',
	 * and is satisfied by v if and only if v(x') is the sorted
	 * version of v(x) in increasing order.
	 * This constraint is called the Sortedness Constraint
	 * in [Bleuzen-Guernalec and Colmerauer 1997] and in [Mehlhorn and Thiel 2000].
	 *
	 * @param v1 the first array of variables
	 * @param v2 the second array of variables
	 * @return Constraint
	 */
	public static Constraint sorting(IntegerVariable[] v1, IntegerVariable[] v2) {
		int offset = v1.length;
		IntegerVariable[] vars = ArrayUtils.append(v1, v2);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.SORTING, offset, vars);
	}

	/**
	 * Let x and y be two vectors of n integers, and let x? and y? be the version of x and y rearranged in increasing order.
	 * x and y are said leximin-indifferent if x?=y?. y is leximin-preferred to x (written y>leximinx if and only if there is an i< n such that for all j� i:
	 * - the jth component of x? is equal to the jth component of y?
	 * - the ith component of x? is lower than the ith component of y?
	 * Let x and x' be two vectors of variables, and v be an instantiation.
	 * The constraint Leximin(x, x') holds on the set of variables belonging to x or x', and is satisfied by v if and only if v(x) <leximin v(x').
	 * [Frisch et al. 2003]	A. Frisch, B. Hnich, Z. Kiziltan, I. Miguel, and T. Walsh. Multiset ordering constraints. In Proc. of IJCAI'03. Acapulco, Mexico, 2003.
	 * @param v1 the first array of variables
	 * @param v2 the second array of variables
	 * @return Constraint
	 */
	public static Constraint leximin(IntegerVariable[] v1, IntegerVariable[] v2) {
		IntegerVariable[] vars = new IntegerVariable[v1.length + v2.length];
		arraycopy(v1, 0, vars, 0, v1.length);
		arraycopy(v2, 0, vars, v1.length, v2.length);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.LEXIMIN,
				null, vars);
	}


	/**
	 * Let x and y be two vectors of n integers, and let x? and y? be the version of x and y rearranged in increasing order.
	 * x and y are said leximin-indifferent if x?=y?. y is leximin-preferred to x (written y>leximinx if and only if there is an i< n such that for all j� i:
	 * - the jth component of x? is equal to the jth component of y?
	 * - the ith component of x? is lower than the ith component of y?
	 * Let x and x' be two vectors of variables, and v be an instantiation.
	 * The constraint Leximin(x, x') holds on the set of variables belonging to x or x', and is satisfied by v if and only if v(x) <leximin v(x').
	 * [Frisch et al. 2003]	A. Frisch, B. Hnich, Z. Kiziltan, I. Miguel, and T. Walsh. Multiset ordering constraints. In Proc. of IJCAI'03. Acapulco, Mexico, 2003.
	 * @param v1 array of values
	 * @param v2 array of variables
	 * @return Constraint
	 */
	public static Constraint leximin(int[] v1, IntegerVariable[] v2) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.LEXIMIN,
				v1, v2);
	}

	/**
	 * Enforce the number of distinct values among vars to be less than nvalue;
	 * @param vars list of variables
	 * @param nvalue number of distinct values
	 * @return Constraint
	 */
	public static Constraint atMostNValue(IntegerVariable[] vars, IntegerVariable nvalue) {
		IntegerVariable[] tmp = new IntegerVariable[vars.length + 1];
		arraycopy(vars, 0, tmp, 0, vars.length);
		tmp[tmp.length - 1] = nvalue;
		return new ComponentConstraint<IntegerVariable>(ConstraintType.ATMOSTNVALUE, null, tmp);
	}

	// ------------- Constraints over sets -------------------------------
	/**
	 * Enforce a set to be the intersection of two others.
	 *
	 * @param sv1 the first set variable
	 * @param sv2 the second set variable
	 * @param inter the intersection of sv1 and sv2
	 * @return Constraint
	 */
	public static Constraint setInter(SetVariable sv1, SetVariable sv2, SetVariable inter) {
		return new ComponentConstraint<SetVariable>(ConstraintType.SETINTER, null, new SetVariable[]{sv1, sv2, inter});
	}

	/**
	 * Enforce a set to be the union of two others
	 *
	 * @param sv1 the first set variable
	 * @param sv2 the second set variable
	 * @param union the union of sv1 and sv2
	 * @return the union constraint
	 */
	public static Constraint setUnion(SetVariable sv1, SetVariable sv2, SetVariable union) {
		return new ComponentConstraint<SetVariable>(ConstraintType.SETUNION, null, new SetVariable[]{sv1, sv2, union});
	}

	//UNDERDEVELOPMENT
	/*
    public static Constraint setUnion(SetVariable[] sv, SetVariable union) {
		return new ComponentConstraint(ConstraintType.SETUNION, null, UtilAlgo.append(new SetVariable[]{union}, sv));
	}
	 */

	/**
	 * Return a constraint that ensures sv1 == sv2
	 * @param sv1 a set variable
	 * @param sv2 a set variable
	 * @return a constraint that ensures sv1 == sv2
	 */
	public static Constraint eq(SetVariable sv1, SetVariable sv2) {
		return new ComponentConstraint<SetVariable>(ConstraintType.EQ, ConstraintType.EQ, new SetVariable[]{sv1, sv2});
	}

	/**
	 * Return a constraint that ensures |sv| = v
	 * @param sv a set variable
	 * @param v an integer variable
	 * @return a constraint that ensures |sv| = v
	 */
	public static Constraint eqCard(SetVariable sv, IntegerVariable v) {
		//return new GenericConstraint<Variable>(ConstraintType.EQCARD, sv, v);
		return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{sv, v});
	}

	/**
	 * Return a constraint that ensures |sv| = val
	 * @param sv a set variable
	 * @param val an integer
	 * @return a constraint that ensures |sv| = val
	 */
	public static Constraint eqCard(SetVariable sv, int val) {
		//        return new GenericConstraint<Variable>(ConstraintType.EQCARD, sv, constant(val));
        return new ComponentConstraint<Variable>(ConstraintType.EQ, ConstraintType.EQ, new Variable[]{sv, constant(val)});
	}

	/**
	 * Return a constraint that ensures |sv| != v
	 * @param sv a set variable
	 * @param v an integer variable
	 * @return a constraint that ensures |sv| != v
	 */
	public static Constraint neqCard(SetVariable sv, IntegerVariable v) {
		//return new GenericConstraint<Variable>(ConstraintType.EQCARD, sv, v);
		return new ComponentConstraint<Variable>(ConstraintType.NEQ, ConstraintType.NEQ, new Variable[]{sv, v});
	}

	/**
	 * Return a constraint that ensures |sv| != val
	 * @param sv a set variable
	 * @param val an integer
	 * @return a constraint that ensures |sv| != val
	 */
	public static Constraint neqCard(SetVariable sv, int val) {
		//        return new GenericConstraint<Variable>(ConstraintType.EQCARD, sv, constant(val));
		return new ComponentConstraint<Variable>(ConstraintType.NEQ, ConstraintType.NEQ, new Variable[]{sv, constant(val)});
	}

	public static Constraint geqCard(SetVariable sv, IntegerVariable v) {
		//return new GenericConstraint<Variable>(ConstraintType.GEQCARD, sv, v);
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{sv, v});
	}

	public static Constraint geqCard(SetVariable sv, int val) {
		return new ComponentConstraint<Variable>(ConstraintType.GEQ, ConstraintType.GEQ, new Variable[]{sv, constant(val)});
	}

	public static Constraint leqCard(SetVariable sv, IntegerVariable v) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{sv, v});
	}

	public static Constraint leqCard(SetVariable sv, int val) {
		return new ComponentConstraint<Variable>(ConstraintType.LEQ, ConstraintType.LEQ, new Variable[]{sv, constant(val)});
	}

	public static Constraint setDisjoint(SetVariable sv1, SetVariable sv2) {
		return new ComponentConstraint<SetVariable>(ConstraintType.SETDISJOINT, null, new SetVariable[]{sv1, sv2});
	}

	// UNDERDEVELOPMENT
	/*
    public static Constraint setDisjoint(SetVariable[] sv) {
		return new ComponentConstraint(ConstraintType.SETDISJOINT, null, sv);
	}
	 */

	/**
	 * Ensures that a value is contained in a set variable.
	 *
	 * @param val the obligatory value
	 * @param sv1 the set variable
	 * @return the new constraint
	 */
	public static Constraint member(int val, SetVariable sv1) {
		return new ComponentConstraint<Variable>(ConstraintType.MEMBER, val, new Variable[]{sv1});
	}

	/**
	 * Ensures that a value is contained in a set variable.
	 *
	 * @param val the obligatory value
	 * @param sv1 the set variable
	 * @return the new constraint
	 */
	public static Constraint member(SetVariable sv1, int val) {
		return new ComponentConstraint<Variable>(ConstraintType.MEMBER, val, new Variable[]{sv1});
	}

	/**
	 * Ensures that the value of an integer variable is contained in a set variable.
	 *
	 * @param sv1 the set variable
	 * @param var the integer variable whose value should be contained in the set
	 * @return the new constraint
	 */
	public static Constraint member(SetVariable sv1, IntegerVariable var) {
		return new ComponentConstraint<Variable>(ConstraintType.MEMBER, null, new Variable[]{sv1, var});
	}

	/**
	 * Ensures that the value of an integer variable is contained in a set variable.
	 *
	 * @param sv1 the set variable
	 * @param var the integer variable whose value should be contained in the set
	 * @return the new constraint
	 */
	public static Constraint member(IntegerVariable var, SetVariable sv1) {
		return new ComponentConstraint<Variable>(ConstraintType.MEMBER, null, new Variable[]{sv1, var});
	}

	/**
	 * Ensures that a value is not contained ina set variable.
	 *
	 * @param val the forbidden value
	 * @param sv1 the set variable
	 * @return the new constraint
	 */
	public static Constraint notMember(int val, SetVariable sv1) {
		return new ComponentConstraint<Variable>(ConstraintType.NOTMEMBER,
				val, new Variable[]{sv1});
	}

	/**
	 * Ensures that a value is not contained ina set variable.
	 *
	 * @param val the forbidden value
	 * @param sv1 the set variable
	 * @return the new constraint
	 */
	public static Constraint notMember(SetVariable sv1, int val) {
		return new ComponentConstraint<Variable>(ConstraintType.NOTMEMBER,
				val, new Variable[]{sv1});
	}

	/**
	 * Ensures that the value of an integer variable is not contained in a set variable.
	 *
	 * @param sv1 the set variable
	 * @param var the integer variable whose value should not be contained in the set
	 * @return the new constraint
	 */
	public static Constraint notMember(SetVariable sv1, IntegerVariable var) {
		//return notMember(var, sv1);
		return new ComponentConstraint<Variable>(ConstraintType.NOTMEMBER,
				null, new Variable[]{sv1, var});
	}

	/**
	 * Ensures that the value of an integer variable is not contained in a set variable.
	 *
	 * @param sv1 the set variable
	 * @param var the integer variable whose value should not be contained in the set
	 * @return the new constraint
	 */
	public static Constraint notMember(IntegerVariable var, SetVariable sv1) {
		//return new GenericConstraint<Variable>(ConstraintType.NOTMEMBER, var, sv1);
		return new ComponentConstraint<Variable>(ConstraintType.NOTMEMBER,
				null, new Variable[]{sv1, var});
	}

	/**
	 * A constraint stating that value j belongs to the sv[i] set variable iff integer variable iv[j] equals to i.
	 * This constraint models the inverse s: I -> P(J) of a function x: J -> I (I and J sets of integers)
	 * adapted from InverseChanneling, see gccat.
	 * @param iv the integer variables
	 * @param sv the set variables 
	 * @return the new constraint
	 */
	public static Constraint inverseSet(IntegerVariable[] iv, SetVariable[] sv) {
		return new ComponentConstraint<Variable>(ConstraintType.INVERSE_SET,
                iv.length, ArrayUtils.<Variable>append(iv, sv));
	}
	/**
	 * Ensure that the two variables are not equal (not exactly the same values in the set)
	 *
	 * @param sv1 first variable
	 * @param sv2 second variable
	 * @return the new constraint
	 */
	public static Constraint neq(SetVariable sv1, SetVariable sv2) {
		//return new GenericConstraint<Variable>(ConstraintType.NEQ, sv1, sv2);
		return new ComponentConstraint<Variable>(ConstraintType.NEQ,
				ConstraintType.NEQ, new SetVariable[]{sv1, sv2});
	}

	/**
	 * Checks that variable sv1 is included in sv2
	 *
	 * @param sv1 variable that should be included (smaller)
	 * @param sv2 variable that should include (bigger)
	 * @return the new constraint
	 */
	public static Constraint isIncluded(SetVariable sv1, SetVariable sv2) {
		//return new GenericConstraint<Variable>(ConstraintType.ISINCLUDED, sv, in);
		return new ComponentConstraint<Variable>(ConstraintType.ISINCLUDED,
				null, new SetVariable[]{sv1, sv2});
	}

	/**
	 * Checks that variable sv1 is not included in sv2
	 *
	 * @param sv1 variable that should not be included (bigger)
	 * @param sv2 variable that should not include (smaller)
	 * @return the new constraint
	 */
	public static Constraint isNotIncluded(SetVariable sv1, SetVariable sv2) {
		//return new GenericConstraint<Variable>(ConstraintType.ISNOTINCLUDED, sv, in);
		return new ComponentConstraint<Variable>(ConstraintType.ISNOTINCLUDED,
				null, new SetVariable[]{sv1, sv2});
	}


	/**
	 * Create a Regular constraint that enforce the sequence of variables to be a word
	 * recognized by the dfa auto.
	 * For example regexp = "(1|2)(3*)(4|5)";
	 * The same dfa can be used for different propagators.
	 *
	 * @param auto the DFA
	 * @param vars the variables of the constraint
	 * @return the new constraint
	 */
	public static Constraint regular(DFA auto, IntegerVariable[] vars) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.REGULAR,
				auto, vars);
	}


	/**
	 * Create a Regular constraint that enforce the sequence of variables to match the regular
	 * expression.
	 *
	 * @param regexp a regexp for the DFA
	 * @param vars   the variables of the constraint
	 * @return the new constraint
	 */
	public static Constraint regular(String regexp, IntegerVariable[] vars) {
		//return new Regular2Constraint(vars, regexp);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.REGULAR,
				regexp, vars);
	}

	/**
	 * A Regular constraint based on a DFA which is built from a list of FEASIBLE tuples.
	 * This api provides a GAC algorithm for a constraint defined by its allowed tuples.
	 * This can be more efficient than a standart GAC algorithm if the tuples are really structured
	 * so that the dfa is compact.
	 * The minimal dfa is built from the list by computing incrementally the minimal dfa after each addition of tuple.
	 *
	 * @param vars   variables of the constraint
	 * @param tuples : a list of int[] corresponding to the allowed tuples
	 * @return the new constraint
	 */
	public static Constraint regular(IntegerVariable[] vars, List<int[]> tuples) {
		//return new Regular3Constraint(vars, tuples);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.REGULAR,
				tuples, vars);
	}

	/**
	 * A Regular constraint based on a DFA which is built from a list of INFEASIBLE tuples
	 * As the relation is defined by infeasible tuples and we build the feasible automaton,
	 * we need to know the range of values by the max and min fields...
	 * This api provides a GAC algorithm for a constraint defined by its allowed tuples.
	 * This can be more efficient than a standart GAC algorithm if the tuples are really structured
	 * so that the dfa is compact.
	 * The minimal dfa is built from the list by computing incrementally the minimal dfa after each addition of tuple.
	 *
	 * @param vars   : scope of the constraint
	 * @param tuples : a list of int[] corresponding to tuple
	 * @param max    : The maximum value of the alphabet used for each layer (upper bound of each variables).
	 * @param min    : The minimum value of the alphabet used for each layer (lower bound of each variables).
	 * @return a constraint
	 */
	public static Constraint regular(IntegerVariable[] vars, List<int[]> tuples, int[] min, int[] max) {
		//return new Regular4Constraint(vars, tuples, min, max);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.REGULAR,
				new Object[]{tuples, min, max}, vars);
	}

	/**
	 * Constructs a new CostRegular constraint
	 * This constraint ensures that the sequence of variables values
	 * will follow a pattern defined by a DFA and that this sequence has a cost bounded by the cost variable
	 * @param vars the sequence of variables the constraint must ensure it belongs to the regular language
	 * @param cvar the cost variable
	 * @param auto  the automaton describing the regular language
	 * @param costs the cost of taking value j for the variable i
	 * @return  a instance of the constraint
	 */
	public static Constraint costRegular(IntegerVariable[] vars, IntegerVariable cvar, Automaton auto, int[][] costs){
		return new ComponentConstraint<IntegerVariable>(ConstraintType.COSTREGULAR, new Object[]{auto, costs},
				ArrayUtils.append(vars, new IntegerVariable[]{cvar}));
	}

	/**
	 * Constructs a new CostRegular constraint
	 * This constraint ensures that the sequence of variables values
	 * will follow a pattern defined by a DFA and that this sequence has a cost bounded by the cost variable
	 * @param vars the sequence of variables the constraint must ensure it belongs to the regular language
	 * @param cvar the cost variable
	 * @param auto  the automaton describing the regular language
	 * @param costs the cost of taking value j for the variable i
	 * @return  a instance of the constraint
	 */
	public static Constraint multiCostRegular(IntegerVariable[] vars, IntegerVariable[] cvar, Automaton auto, int[][][] costs){
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MULTICOSTREGULAR, new Object[]{vars.length,auto,costs},
				ArrayUtils.append(vars, cvar));
	}
	/**
	 * Constructs a new CostRegular constraint
	 * This constraint ensures that the sequence of variables values
	 * will follow a pattern defined by a DFA and that this sequence has a cost bounded by the cost variable
	 * @param vars the sequence of variables the constraint must ensure it belongs to the regular language
	 * @param cvar the cost variable
	 * @param auto  the automaton describing the regular language
	 * @param costs the cost of taking value j for the variable i from state s
	 * @return  a instance of the constraint
	 */
	public static Constraint multiCostRegular(IntegerVariable[] vars, IntegerVariable[] cvar, Automaton auto, int[][][][] costs){
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MULTICOSTREGULAR, new Object[]{vars.length,auto,costs},
				ArrayUtils.append(vars, cvar));
	}



	public static Constraint tree(TreeParametersObject param){
		return new ComponentConstraint<Variable>(ConstraintType.TREE, param, param.extractVariables());
	}

	/**
	 * State a constraint to enforce GAC on Sum_i coeffs[i] * vars[i] = val.
	 * It is using the regular to state a "knapsack" constraint.
	 * @param vars   : a table of variables
	 * @param coeffs : a table of coefficients
	 * @param val    : the value to reach
	 * @return a constraint
	 */
	public static Constraint equation(IntegerVariable[] vars,int[] coeffs, int val) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.REGULAR,
				new int[][]{coeffs,new int[]{val}}, vars);
	}

	public static Constraint sameSign(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new ComponentConstraint<Variable>(ConstraintType.SIGNOP, true, new Variable[]{n1, n2});
	}

	public static Constraint oppositeSign(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new ComponentConstraint<Variable>(ConstraintType.SIGNOP, false, new Variable[]{n1, n2});
	}

	public static Constraint mod(IntegerVariable v0, IntegerVariable v1, int c) {
		return new ComponentConstraint<IntegerVariable>(ConstraintType.MOD, null, new IntegerVariable[]{v0, v1, constant(c)});
	}

	public static Constraint reifiedIntConstraint(IntegerVariable binVar, Constraint cst) {
		Variable[] vars = ArrayUtils.append(new IntegerVariable[]{binVar}, cst.getVariables());
		return new ComponentConstraintWithSubConstraints(ConstraintType.REIFIEDINTCONSTRAINT, vars, null, cst);
	}

	public static Constraint reifiedIntConstraint(IntegerVariable binVar, Constraint cst, Constraint oppCst) {
		return new ComponentConstraintWithSubConstraints(ConstraintType.REIFIEDINTCONSTRAINT, new IntegerVariable[]{binVar}, null, cst, oppCst);
	}

	/**
	 * A global constraint to store and propagate all clauses
	 * @param positiveLiterals list of positive literals
	 * @param negativeLiterals list of negative lliterals
	 * @return Constraint
	 */
	public static Constraint clause(IntegerVariable[] positiveLiterals, IntegerVariable[] negativeLiterals){
		IntegerVariable[] literals = ArrayUtils.append(positiveLiterals, negativeLiterals);
		return new ComponentConstraint<IntegerVariable>(ConstraintType.CLAUSES, positiveLiterals.length, literals);

	}

    /**
     * A constraint for logical disjunction between boolean variables
     * @param literals list of boolean variables
     * @return Constraint
     */
    public static Constraint or(IntegerVariable... literals){
        for(IntegerVariable lit : literals){
            if(!lit.isBoolean())throw new ModelException("OR constraint must be used with boolean variables");
        }
        return new ComponentConstraint<IntegerVariable>(ConstraintType.OR, null, literals);
    }

    /**
     * A reified constraint for logical disjunction between boolean variables
     * @param binVar reified variable
     * @param literals list of boolean variables
     * @return Constraint
     */
    public static Constraint reifiedOr(IntegerVariable binVar, IntegerVariable... literals){
        IntegerVariable[] vars = ArrayUtils.append(new IntegerVariable[]{binVar}, literals);
        for(IntegerVariable var : vars){
            if(!var.isBoolean())throw new ModelException("reifiedOr constraint must be used with boolean variables");
        }
        return new ComponentConstraint<IntegerVariable>(ConstraintType.REIFIEDOR, null, vars);
    }

    /**
     * A constraint for logical conjunction between boolean variables
     * @param literals list of boolean variables
     * @return Constraint
     */
    public static Constraint and(IntegerVariable... literals){
        for(IntegerVariable lit : literals){
            if(!lit.isBoolean())throw new ModelException("AND constraint must be used with boolean variables");
        }
        return new ComponentConstraint<IntegerVariable>(ConstraintType.AND, null, literals);
    }

    /**
     * A reified constraint for logical conjunction between boolean variables
     * @param binVar reified variable
     * @param literals list of boolean variables
     * @return Constraint
     */
    public static Constraint reifiedAnd(IntegerVariable binVar, IntegerVariable... literals){
        IntegerVariable[] vars = ArrayUtils.append(new IntegerVariable[]{binVar}, literals);
        for(IntegerVariable var : vars){
            if(!var.isBoolean())throw new ModelException("reifiedOr constraint must be used with boolean variables");
        }
        return new ComponentConstraint<IntegerVariable>(ConstraintType.REIFIEDAND, null, vars);
    }

	// ############################################################################################################
	// ######                                       EXPRESSIONS                                                 ###
	// ############################################################################################################

	// ################################################ INTEGER ###################################################

	/*
	 * Creates a simple linear term from one coefficient and one variable
	 *
	 * @param a the coefficient
	 * @param x the variable
	 * @return the term
	 */


	public static IntegerExpressionVariable mult(IntegerExpressionVariable t1, int a) {
		return new IntegerExpressionVariable(null, Operator.MULT, t1, constant(a));
	}

	public static IntegerExpressionVariable mult(int a, IntegerExpressionVariable t1) {
		return new IntegerExpressionVariable(null, Operator.MULT, t1, constant(a));
	}

	public static IntegerExpressionVariable mult(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MULT, n1, n2);
	}


	/**
	 * Adding two terms one to another
	 *
	 * @param t1 first term
	 * @param t2 second term
	 * @return the term (a fresh one)
	 */
	public static IntegerExpressionVariable plus(IntegerExpressionVariable t1, IntegerExpressionVariable t2) {
		return new IntegerExpressionVariable(null, Operator.PLUS, t1, t2);
	}

	public static IntegerExpressionVariable plus(IntegerExpressionVariable t, int c) {
		return plus(t, constant(c));
	}

	public static IntegerExpressionVariable plus(int c, IntegerExpressionVariable t) {
		return plus(t, constant(c));
	}

	/**
	 * Subtracting two terms one from another
	 *
	 * @param t1 first term
	 * @param t2 second term
	 * @return the term (a fresh one)
	 */
	public static IntegerExpressionVariable minus(IntegerExpressionVariable t1, IntegerExpressionVariable t2) {
		return new IntegerExpressionVariable(null, Operator.MINUS, t1, t2);
	}

	public static IntegerExpressionVariable minus(IntegerExpressionVariable t, int c) {
		return new IntegerExpressionVariable(null, Operator.MINUS, t, constant(c));
	}

	public static IntegerExpressionVariable minus(int c, IntegerExpressionVariable t) {
		return new IntegerExpressionVariable(null, Operator.MINUS, constant(c), t);
	}

	/**
	 * Building a term from a scalar product of coefficients and variables
	 *
	 * @param lc the array of coefficients
	 * @param lv the array of variables
	 * @return the term
	 */
	public static IntegerExpressionVariable scalar(int[] lc, IntegerVariable[] lv) {
		if (lc.length != lv.length) {
			throw new ModelException("scalar: parameters length are differents");
		}
		IntegerVariable[] tmp = new IntegerVariable[lc.length + lv.length];
		for (int i = 0; i < lc.length; i++) {
			tmp[i] = constant(lc[i]);
		}
		arraycopy(lv, 0, tmp, lc.length, lv.length);
		return new IntegerExpressionVariable(null, Operator.SCALAR, tmp);
	}


	/**
	 * Building a term from a scalar product of coefficients and variables
	 *
	 * @param lv the array of variables
	 * @param lc the array of coefficients
	 * @return the term
	 */
	public static IntegerExpressionVariable scalar(IntegerVariable[] lv, int[] lc) {
		return scalar(lc, lv);
	}

	/**
	 * Building a term from a sum of integer expressions
	 *
	 * @param lv the array of integer expressions
	 * @return the term
	 */
	public static IntegerExpressionVariable sum(IntegerExpressionVariable... lv) {
		return new IntegerExpressionVariable(null, Operator.SUM, lv);
	}

	public static IntegerExpressionVariable abs(IntegerExpressionVariable n) {
		return new IntegerExpressionVariable(null, Operator.ABS, n);
	}

	public static IntegerExpressionVariable distEq(
			IntegerExpressionVariable n1, IntegerExpressionVariable n2, IntegerExpressionVariable n3) {
		return new IntegerExpressionVariable(null, Operator.DISTANCEEQ, n1, n2, n3);
	}

	public static IntegerExpressionVariable distGt(
			IntegerExpressionVariable n1, IntegerExpressionVariable n2, IntegerExpressionVariable n3) {
		return new IntegerExpressionVariable(null, Operator.DISTANCEGT, n1, n2, n3);
	}

	public static IntegerExpressionVariable distLt(
			IntegerExpressionVariable n1, IntegerExpressionVariable n2, IntegerExpressionVariable n3) {
		return new IntegerExpressionVariable(null, Operator.DISTANCELT, n1, n2, n3);
	}

	public static IntegerExpressionVariable distNeq(
			IntegerExpressionVariable n1, IntegerExpressionVariable n2, IntegerExpressionVariable n3) {
		return new IntegerExpressionVariable(null, Operator.DISTANCENEQ, n1, n2, n3);
	}

	public static IntegerExpressionVariable div(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.DIV, n1, n2);
	}

	public static IntegerExpressionVariable div(IntegerExpressionVariable n1, int n2) {
		return new IntegerExpressionVariable(null, Operator.DIV, n1, constant(n2));
	}

	public static IntegerExpressionVariable div(int n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.DIV, constant(n1), n2);
	}

	public static IntegerExpressionVariable max(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MAX, n1, n2);
	}

	public static IntegerExpressionVariable max(int n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MAX, constant(n1), n2);
	}

	public static IntegerExpressionVariable max(IntegerExpressionVariable n1, int n2) {
		return new IntegerExpressionVariable(null, Operator.MAX, n1, constant(n2));
	}

	public static IntegerExpressionVariable max(IntegerExpressionVariable[] n1) {
		return new IntegerExpressionVariable(null, Operator.MAX, n1);
	}

	public static IntegerExpressionVariable min(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MIN, n1, n2);
	}

	public static IntegerExpressionVariable min(int n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MIN, constant(n1), n2);
	}

	public static IntegerExpressionVariable min(IntegerExpressionVariable n1, int n2) {
		return new IntegerExpressionVariable(null, Operator.MIN, n1, constant(n2));
	}

	public static IntegerExpressionVariable min(IntegerExpressionVariable[] n1) {
		return new IntegerExpressionVariable(null, Operator.MIN, n1);
	}

	public static IntegerExpressionVariable mod(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MOD, n1, n2);
	}

	public static IntegerExpressionVariable mod(int n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.MOD, constant(n1), n2);
	}

	public static IntegerExpressionVariable mod(IntegerExpressionVariable n1, int n2) {
		return new IntegerExpressionVariable(null, Operator.MOD, n1, constant(n2));
	}

	public static IntegerExpressionVariable neg(IntegerExpressionVariable n) {
		return new IntegerExpressionVariable(null, Operator.NEG, n);
	}

	public static IntegerExpressionVariable power(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.POWER, n1, n2);
	}

	public static IntegerExpressionVariable power(int n1, IntegerExpressionVariable n2) {
		return new IntegerExpressionVariable(null, Operator.POWER, constant(n1), n2);
	}

	public static IntegerExpressionVariable power(IntegerExpressionVariable n1, int n2) {
		return new IntegerExpressionVariable(null, Operator.POWER, n1, constant(n2));
	}

	public static IntegerExpressionVariable ifThenElse(Constraint n1, IntegerExpressionVariable n2, IntegerExpressionVariable n3) {
		return new MetaIntegerExpressionVariable(Operator.IFTHENELSE, n1, n2, n3);
	}


	// ################################################# REAL ####################################################

	public static RealExpressionVariable mult(double a, RealExpressionVariable x) {
		return new RealExpressionVariable(null, Operator.MULT, constant(a), x);
	}

	public static RealExpressionVariable mult(RealExpressionVariable x, int a) {
		return mult(a, x);
	}

	public static RealExpressionVariable mult(RealExpressionVariable x, RealExpressionVariable y) {
		return new RealExpressionVariable(null, Operator.MULT, x, y);
	}

	public static RealExpressionVariable plus(RealExpressionVariable t1, RealExpressionVariable t2) {
		return new RealExpressionVariable(null, Operator.PLUS, t1, t2);
	}

	public static RealExpressionVariable plus(RealExpressionVariable t, double c) {
		return plus(t, constant(c));
	}

	public static RealExpressionVariable plus(double c, RealExpressionVariable t) {
		return plus(t, constant(c));
	}

	public static RealExpressionVariable minus(RealExpressionVariable t1, RealExpressionVariable t2) {
		return new RealExpressionVariable(null, Operator.MINUS, t1, t2);
	}

	public static RealExpressionVariable minus(RealExpressionVariable t, double c) {
		return new RealExpressionVariable(null, Operator.MINUS, t, constant(c));
	}

	public static RealExpressionVariable minus(double c, RealExpressionVariable t) {
		return new RealExpressionVariable(null, Operator.MINUS, constant(c), t);
	}

	/**
	 * Power of an expression.
	 *
	 * @param exp   the expression to x
	 * @param power the second expression
	 * @return the difference of exp1 and exp2 (exp1-exp2)
	 */
	public static RealExpressionVariable power(RealExpressionVariable exp, int power) {
		return new RealExpressionVariable(null, Operator.POWER, exp, constant((double) power));
	}

	/**
	 * Cosinus of an expression.
	 * @param exp the real expression
	 * @return RealExpression
	 */
	public static RealExpressionVariable cos(RealExpressionVariable exp) {
		return new RealExpressionVariable(null, Operator.COS, exp);
	}

	/**
	 * Sinus of an expression.
	 * @param exp the real expression
	 * @return RealExpression
	 */
	public static RealExpressionVariable sin(RealExpressionVariable exp) {
		return new RealExpressionVariable(null, Operator.SIN, exp);
	}

	/*public static Constraint gt(IntegerExpressionVariable n1, IntegerExpressionVariable n2) {
       Node node1 = n1;
       Node node2 = n2;
       if (node1.getNbSubNode() == 2 &&
       node1.getNodeType() == ConstraintType.MULT &&
       node2.isCsteEqualto(0)) {
     return new GenericConstraint<Variable>(ConstraintType.SAMESIGN, node1.getSubNode(0), node1.getSubNode(1));
   } else if (node2.getNbSubNode() == 2 &&
       node2.getNodeType() == ConstraintType.MULT &&
       node1.isCsteEqualto(0)) {
     return new GenericConstraint<Variable>(ConstraintType.OPPSIGN, node2.getSubNode(0), node2.getSubNode(1));
   }
   return new GenericConstraint<Variable>(ConstraintType.GT, node1, node2);
 }   */

	// ############################################################################################################
	// ######                                    META CONSTRAINTS                                               ###
	// ############################################################################################################

	public static Constraint and(Constraint... n) {
		if(n.length == 0) return TRUE;
		if (n.length == 1)
			return n[0];
		return new MetaConstraint<Constraint>(ConstraintType.AND, n);
	}

	public static Constraint ifOnlyIf(Constraint n1, Constraint n2) {
		return new MetaConstraint<Constraint>(ConstraintType.IFONLYIF, n1, n2);
	}

	public static Constraint ifThenElse(Constraint n1, Constraint n2, Constraint n3) {
		return new MetaConstraint<Constraint>(ConstraintType.IFTHENELSE, n1, n2, n3);
	}

	public static Constraint implies(Constraint n1, Constraint n2) {
		return new MetaConstraint<Constraint>(ConstraintType.IMPLIES, n1, n2);
	}

	public static Constraint not(Constraint n) {
		return new MetaConstraint<Constraint>(ConstraintType.NOT, n);
	}

	public static Constraint or(Constraint... n) {
		if(n.length == 0) return TRUE;
		if (n.length == 1)
			return n[0];
		else return new MetaConstraint<Constraint>(ConstraintType.OR, n);
	}

	//*****************************************************************//
	//*******************  Time Windows  ********************************//
	//***************************************************************//
	protected static Constraint timeWindow(final IntegerVariable var, final int min, final int max) {
		return eq(var,makeIntVar("internal-tw-"+ IndexFactory.getId(), min, max, "cp:bound","cp:no_decision"));
	}

	/**
	 * 	This task ends between min and max
	 * @param t the task
	 * @param min the minimum ending time
	 * @param max the maximum ending time
	 * @return Constraint
	 */
	public static  Constraint endsBetween(final TaskVariable t, final int min, final int max) 	{
		return timeWindow(t.end(), min, max);
	}

	/**
	 * This task ends between min and max
	 * @param t the task
	 * @param min the minimum starting time
	 * @param max the maximum starting time
	 * @return Constraint
	 */
	public static Constraint startsBetween(final TaskVariable t, final int min, final int max) 	{
		return timeWindow(t.start(), min, max);
	}

	/**
	 * 	This task ends before max
	 * @param t the task
	 * @param max the maximum ending time
	 * @return Constraint
	 */
	public static Constraint endsBefore(final TaskVariable t, final int max) 	{
		return leq(t.end(),max);
	}

	/**
	 * 	This task starts before max
	 * @param t the task
	 * @param max the maximum starting time
	 * @return Constraint
	 */
	public static Constraint startsBefore(final TaskVariable t, final int max) 	{
		return leq(t.start(),max);
	}

	/**
	 * 	This task ends after min
	 * @param t the task
	 * @param min the minimum ending time
	 * @return Constraint
	 */
	public static Constraint endsAfter(final TaskVariable t, final int min) 	{
		return geq(t.end(),min);
	}

	/**
	 * 	This task begins before max
	 * @param t the task
	 * @param min the minimum starting time
	 * @return Constraint
	 */
	public static Constraint startsAfter(final TaskVariable t, final int min) 	{
		return geq(t.start(),min);
	}

	//*****************************************************************//
	//*******************  Temporal constraints **********************//
	//***************************************************************//
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Temporal constraint : start(t1) +delta <= start(s2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @param delta the delta
	 * @return Constraint
	 */
	public static  Constraint startsBeforeBegin(final TaskVariable t1,final TaskVariable t2, final int delta) {
		return new MetaTaskConstraint(new Variable[]{t1, t2}, leq(plus(t1.start(), delta),t2.start()));
	}

	/**
	 * Temporal constraint : start(t1) <= start(s2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint startsBeforeBegin(final TaskVariable t1,final TaskVariable t2) {
		return startsBeforeBegin(t1,t2,0);
	}

	/**
	 * Temporal constraint : start(t1) >= start(t2) +delta
	 * @param t1 the first task
	 * @param t2 the second task
	 * @param delta the delta
	 * @return Constraint
	 */
	public static Constraint startsAfterBegin(final TaskVariable t1, final TaskVariable t2, final int delta) 	{
		return startsBeforeBegin(t2, t1, delta);
	}

	/**
	 * Temporal constraint : start(t1) >= start(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint startsAfterBegin(final TaskVariable t1, final TaskVariable t2) 	{
		return startsAfterBegin(t1,t2,0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Temporal constraint: start(t1) >= end(t2)  + delta
	 * @param t1 the starting task
	 * @param t2 the ending task
	 * @param delta the setup time between t1 and t2
	 * @return Constraint
	 */
	public static Constraint startsAfterEnd(final TaskVariable t1, final TaskVariable t2, final int delta) {
		//return new MetaTaskConstraint(new Variable[]{t1, t2}, geq(t1.start(), plus(t2.end(), delta)));
		return endsBeforeBegin(t2, t1, delta);
	}

	/**
	 * Temporal constraint: start(t1) >= end(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint startsAfterEnd(final TaskVariable t1, final TaskVariable t2) {
		return startsAfterEnd(t1, t2, 0);
	}

	/**
	 *  Temporal constraint: end(t1) + delta <= start(t2)
	 * @param t1 the ending task
	 * @param t2 the starting task
	 * @param delta the setup between t1 and t2
     * @return Constraint
	 */
	public static Constraint endsBeforeBegin(final TaskVariable t1, final TaskVariable t2, final int delta) 	{
		//return startsAfterEnd(t2, t1, delta);
		return precedenceDisjoint(t1,t2, ONE, delta, 0);
	}

	/**
	 *  Temporal constraint: end(t1)<= start(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint endsBeforeBegin(final TaskVariable t1, final TaskVariable t2) 	{
		//return preceding(t1, t2);
		return endsBeforeBegin(t1, t2, 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Temporal constraint : start(t1) + delta <= end(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @param delta the delta
	 * @return Constraint
	 */
	public static Constraint startsBeforeEnd(final TaskVariable t1, final TaskVariable t2, final int delta) {
		return new MetaTaskConstraint(new Variable[]{t1, t2}, leq( plus(t1.start(), delta), t2.end()));
	}

	/**
	 * Temporal constraint : start(t1) + delta <= end(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint startsBeforeEnd(final TaskVariable t1, final TaskVariable t2) 	{
		return startsBeforeEnd(t1,t2,0);
	}

	/**
	 * Temporal constraint: end(t1) >= start(t2) + delta
	 * @param t1 the first task
	 * @param t2 the second task
	 * @param delta the delta
	 * @return Constraint
	 */
	public static Constraint endsAfterBegin(final TaskVariable t1, final TaskVariable t2, final int delta) {
		return  startsBeforeEnd(t2, t1, delta);
	}

	/**
	 *  This task ends after the start of the task 2
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint endsAfterBegin(final TaskVariable t1, final TaskVariable t2) {
		return endsAfterBegin(t1,t2,0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Temporal constraint: end(t1) + delta <= end(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @param delta the delta
	 * @return Constraint
	 */
	public static  Constraint endsBeforeEnd(final TaskVariable t1, final TaskVariable t2, final int delta) {
		return  new MetaTaskConstraint(new Variable[]{t1, t2},leq(t1.end(), plus(t2.end(), delta)));
	}

	/**
	 * Temporal constraint: end(t1) <= end(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint endsBeforeEnd(final TaskVariable t1, final TaskVariable t2) {
		return endsBeforeEnd(t1,t2,0);
	}

	/**
	 * Temporal constraint: end(t1) >= end(t2) + delta
	 * @param t1 the first task
	 * @param t2 the second task
	 * @param delta the delta
	 * @return Constraint
	 */
	public static Constraint endsAfterEnd(final TaskVariable t1, final TaskVariable t2, final int delta) {
		return endsBeforeEnd(t2, t1,delta);
	}

	/**
	 * Temporal constraint: end(t1) >= end(t2)
	 * @param t1 the first task
	 * @param t2 the second task
	 * @return Constraint
	 */
	public static Constraint endsAfterEnd(final TaskVariable t1, final TaskVariable t2) 	{
		return endsAfterEnd(t1,t2,0);
	}


}



