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

import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.ConstantFactory;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.variables.real.RealMath;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 6 août 2008
 * Time: 16:08:55
 *
 * This class must contain every deprecated method of the Choco class.
 * The idea is that we can easily unbranch deprecated method to update tests.
 */
public class DeprecatedChoco extends Choco{


    @Deprecated
    public static IntegerVariable makeIntVar(String name, VariableType type, int binf, int bsup) {
        if(binf>bsup) {
			throw new ModelException("makeIntVar : binf > bsup");
		}
		return new IntegerVariable(name,type, binf, bsup);
	}

    @Deprecated
    protected static IntegerVariable[] makeIntVarArray(String name,VariableType type, int n,int binf, int bsup) {
		if(binf>bsup) {
			throw new ModelException("makeIntVarArray : binf > bsup");
		}
		IntegerVariable[] vars=new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			vars[i]=makeIntVar(name+"_"+i, type, binf, bsup);
		}
		return vars;
	}

    @Deprecated
    protected static IntegerVariable[][] makeIntVarArray(String name,VariableType type, int n,int m,int binf, int bsup) {
        if(binf>bsup) {
			throw new ModelException("makeIntVarArray : binf > bsup");
		}
		IntegerVariable[][] vars=new IntegerVariable[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				vars[i][j]=makeIntVar(name+"_"+i+"_"+j, type, binf, bsup);
			}
		}
		return vars;
	}

    @Deprecated
    public static IntegerVariable makeEnumIntVar(String name, int binf, int bsup) {
		return makeIntVar(name, binf, bsup, "cp:enum");
	}

	/**
	 * Creates a new search variable with an enumerated domain
	 *
	 * @param name   the name of the variable
	 * @param values allowed in the domain (may be unsorted, but not with duplicates !)
	 * @return the variable
     * @Deprecated
	 */
	public static IntegerVariable makeEnumIntVar(String name, int[] values) {
		int[] values2 = new int[values.length];
		System.arraycopy(values, 0, values2, 0, values.length);
		Arrays.sort(values2);
		return makeIntVar(name, values2, "cp:enum");
	}

	/**
	 * Creates a new search variable with an enumerated domain
	 *
	 * @param name   the name of the variable
	 * @param values allowed in the domain (may be unsorted, but not with duplicates !)
	 * @return the variable
     * @Deprecated
	 */
	public static IntegerVariable makeEnumIntVar(String name, ArrayList<Integer> values) {
		int[] values2 = new int[values.size()];
		for (int i = 0; i < values.size(); i++) {
			values2[i] = values.get(i);
		}
		Arrays.sort(values2);
		return makeIntVar(name, values2, "cp:enum");
	}

    @Deprecated
    public static IntegerVariable[] makeEnumIntVarArray(String name, int size, int binf, int bsup) {
		return makeIntVarArray(name, size, binf, bsup, "cp:enum");
	}

    @Deprecated
    public static IntegerVariable[][] makeEnumIntVarArray(String name, int dim, int dim2, int binf, int bsup) {
		return makeIntVarArray(name, dim,dim2, binf, bsup, "cp:enum");
	}

	/******** BINARY_TREE ***********/
	/**
	 * Create a binary tree domain for integer variable
	 * @param name the name of the variable
	 * @param binf the lower bound of the variable
	 * @param bsup the upper bound of the variables
	 * @return an IntegerVariable object
     * @Deprecated
	 */
	public static IntegerVariable makeBinTreeIntVar(String name, int binf, int bsup){
		return makeIntVar(name, binf, bsup, "cp:btree");
	}

	/**
	 * Create a binary tree domain for integer variable
	 * @param name the name of the variable
	 * @param values array of allowed values
	 * @return an IntegerVariable object
     * @Deprecated
	 */
	public static IntegerVariable makeBinTreeIntVar(String name, int[] values) {
		int[] values2 = new int[values.length];
		System.arraycopy(values, 0, values2, 0, values.length);
		Arrays.sort(values2);
		IntegerVariable v = makeIntVar(name, values2, "cp:enum");
		return v;
	}

	/**
	 * Create a binary tree domain for integer variable
	 * @param name the name of the variable
	 * @param values list of allowed values
	 * @return an IntegerVariable object
     * @Deprecated
	 */
	public static IntegerVariable makeBinTreeIntVar(String name, ArrayList<Integer> values) {
		int[] values2 = new int[values.size()];
		for(int i =0; i < values.size(); i++){
			values2[i] = values.get(i);
		}
		Arrays.sort(values2);
		return makeIntVar(name, values2, "cp:btree");
	}

	/**
	 * Create an array of integer variable with a binary tree domain
	 * @param name the name of the variable
	 * @param size size of the array
	 * @param binf the lower bound of every  variable
	 * @param bsup the upper bound of every variables
	 * @return an IntegerVariable array
     * @Deprecated
	 */
	public static IntegerVariable[] makeBinTreeIntVarArray(String name, int size, int binf, int bsup){
		return makeIntVarArray(name, size, binf, bsup, "cp:btree");
	}

	/**
	 * Create an double array of integer variable with a binary tree domain
	 * @param name the name of the variable
	 * @param dim size of the array
	 * @param dim2 size of the array
	 * @param binf the lower bound of every  variable
	 * @param bsup the upper bound of every variables
	 * @return an IntegerVariable array
     * @Deprecated
	 */
	public static IntegerVariable[][] makeBinTreeIntVarArray(String name, int dim, int dim2, int binf, int bsup){
		return makeIntVarArray(name, dim,dim2, binf, bsup, "cp:btree");
	}

	/******** INTEGER_BOUNDED ***********/
    @Deprecated
    public static IntegerVariable makeBoundIntVar(String name, int binf, int bsup) {
		return makeIntVar(name, binf, bsup, "cp:bound");
	}

    @Deprecated
    public static IntegerVariable[] makeBoundIntVarArray(String name, int size, int binf, int bsup) {
		return makeIntVarArray(name, size, binf, bsup, "cp:bound");
	}

    @Deprecated
    public static IntegerVariable[][] makeBoundIntVarArray(String name, int dim1, int dim2, int binf, int bsup) {
		return makeIntVarArray(name, dim1,dim2, binf, bsup, "cp:bound");
	}

    @Deprecated
    public static IntegerVariable makeLinkedListIntVar(String name, int binf, int bsup) {
		return makeIntVar(name, binf, bsup, "cp:link");
	}

    @Deprecated
    public static IntegerVariable makeLinkedListIntVar(String name, int[] values) {
		return makeIntVar(name, values, "cp:link");
	}

    /**************REAL*********/


		/**
		* Arounds a double d to <code>[d - epsilon, d + epilon]</code>.
		*/
        @Deprecated
        public RealConstantVariable around(double d) {
		return cst(RealMath.prevFloat(d), RealMath.nextFloat(d));
		}

		/**
		* Makes a constant interval from a double d ([d,d]).
		*/
        @Deprecated
        public RealConstantVariable cst(double d) {
		    return new RealConstantVariable(d, d);
		}

		/**
		* Makes a constant interval between two doubles [a,b].
		*/
        @Deprecated
        public RealConstantVariable cst(double a, double b) {
		return new RealConstantVariable(a, b);
	}

//    @Deprecated
//    public static RealVariable makeRealVar(String name, double binf, double bsup) {
//		return new RealVariable(name, binf, bsup);
//	}

	/******** SET ***********/
    @Deprecated
    public static SetVariable[] makeSetVarArray(String name,int n,int binf,int bsup) {
		SetVariable[] vars=new SetVariable[n];
		for (int i = 0; i < vars.length; i++) {
			vars[i]=makeSetVar(name+"-"+i, binf, bsup, "cp:enumCard");
		}
		return vars;
	}


    @Deprecated
    public static SetVariable makeBoundSetVar(String name, int binf, int bsup) {
		return makeSetVar(name,binf, bsup, "cp:boundCard");
	}

    @Deprecated
    public static SetVariable[] makeBoundSetVarArray(String name,int n,int binf, int bsup) {
		return makeSetVarArray(name, n, binf, bsup, "cp:boundCard");
	}


    @Deprecated
    public static SetVariable makeEnumSetVar(String name, int binf, int bsup) {
		return makeSetVar(name,binf, bsup,"cp:enumCard");
	}

    @Deprecated
    public static SetVariable[] makeEnumSetVarArray(String name,int n,int binf, int bsup) {
		return makeSetVarArray(name, n, binf, bsup,"cp:enumCard");
	}

    /**
	 * Ensures that the lower bound of occurrence is at least equal to the number of occurences
	 * size{forall v in vars | v = value} <= occurence
     * @see Choco#occurrenceMin(int, choco.kernel.model.variables.integer.IntegerVariable, choco.kernel.model.variables.integer.IntegerVariable[])
	 */
    @Deprecated
    public static Constraint occurenceMin(int value, IntegerVariable occurence, IntegerVariable... vars) {
		IntegerVariable[] variables = new IntegerVariable[vars.length + 2];
		variables[0] = constant(value);
		variables[1] = occurence;
		System.arraycopy(vars, 0, variables, 2, vars.length);
		return new ComponentConstraint(ConstraintType.OCCURRENCE, -1, variables);
	}

	/**
	 * Ensures that the upper bound of occurrence is at most equal to the number of occurences
	 * size{forall v in vars | v = value} >= occurence
     * @see Choco#occurrenceMax(int, choco.kernel.model.variables.integer.IntegerVariable, choco.kernel.model.variables.integer.IntegerVariable[])
	 */
    @Deprecated
    public static Constraint occurenceMax(int value, IntegerVariable occurence, IntegerVariable... vars) {
		IntegerVariable[] variables = new IntegerVariable[vars.length + 2];
		variables[0] = constant(value);
		variables[1] = occurence;
		System.arraycopy(vars, 0, variables, 2, vars.length);
		return new ComponentConstraint(ConstraintType.OCCURRENCE, 1, variables);
	}

    
    @Deprecated
    public static SetVariable makeConstantSetVar(String name, int... value) {
		return new SetConstantVariable(constant(value.length), value);
	}

    @Deprecated
	public static RealConstantVariable makeConstantVar(String name, double value) {
		return new RealConstantVariable(value);
	}

    @Deprecated
	public static IntegerConstantVariable makeConstantVar(String name, int value) {
		return new IntegerConstantVariable(value);
	}

    @Deprecated
    public static SetVariable constant(String name, int... value) {
		return ConstantFactory.getConstant(value);
	}

    @Deprecated
	public static RealConstantVariable constant(String name, double value) {
		return ConstantFactory.getConstant(value);
	}

    @Deprecated
	public static IntegerConstantVariable constant(String name, int value) {
		return ConstantFactory.getConstant(value);
	}
}
