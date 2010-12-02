/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco;

import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 24 f�vr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 *
 * A class for reformulated constraints.
 */
public class Reformulation {

    /**
     * AMONG constraint reformulated like: <br/>
     * <ul>
     * <li>introducing BOOL variable for each VARIABLE,</li>
     * <li>adding following constraints:</li>
     * <ul>
     * <li>for each VARIABLE : REIFIED(BOOL_i, MEMBER(VARIABLE_i, S)),</li>
     * <li>EQ(SUM(BOOL), N),</li>
     * </ul>
     * </ul>
     * @param variables scope variable
     * @param s set variable, containing values to count
     * @param nvar integer variable counter
     * @return AMONG constraint reformulated
     */
    public static Constraint[] among(IntegerVariable[] variables, SetVariable s, IntegerVariable nvar) {
        IntegerVariable[] bools = new IntegerVariable[variables.length];
        for(int i = 0; i< variables.length; i++){
            bools[i] = Choco.makeBooleanVar(StringUtils.randomName());
        }
        Constraint[] cs = new Constraint[variables.length + 1];
        for (int i = 0; i < variables.length; i++) {
            cs[i] = Choco.reifiedConstraint(bools[i], Choco.member(variables[i], s), Choco.notMember(variables[i], s));
        }
        cs[variables.length] = Choco.eq(nvar, Choco.sum(bools));
        return cs;
    }

    /**
     * AMONG constraint reformulated like: <br/>
     * <ul>
     * <li>introducing BOOL variable for each VARIABLE,</li>
     * <li>adding following constraints:</li>
     * <ul>
     * <li>for each VARIABLE : REIFIED(BOOL_i, AMONG(VARIABLE_i, VALUES)),</li>
     * <li>EQ(SUM(BOOL), N),</li>
     * </ul>
     * </ul>
     * @param nvar counter variable
     * @param variables counted variables
     * @param values array of values
     * @return AMONG constraint reformulated
     */
    public static Constraint[] among(IntegerVariable nvar, IntegerVariable[] variables, int[]values){
        IntegerVariable[] bools = new IntegerVariable[variables.length];
        for(int i = 0; i< variables.length; i++){
            bools[i] = Choco.makeBooleanVar(StringUtils.randomName());
        }
        Constraint[] cs = new Constraint[variables.length + 1];
        for(int j = 0; j < bools.length; j++){
            cs[j] = Choco.reifiedConstraint(bools[j], Choco.member(variables[j], values));
        }
        cs[variables.length] = Choco.eq(Choco.sum(bools), nvar);
        return cs;
    }
    
    /**
    * AMONG constraint reformulated like: <br/>
    * <ul>
    * <li>introducing BOOL variable for each VARIABLE,</li>
    * <li>adding following constraints:</li>
    * <ul>
    * <li>for each VARIABLE : REIFIED(BOOL_i, AMONG(VARIABLE_i, VALUES)),</li>
    * <li>EQ(SUM(BOOL), N),</li>
    * </ul>
    * </ul>
    */
    public static Constraint[] disjunctive(TaskVariable[] clique, String... boolvarOptions) {
		final int n = clique.length;
		Constraint[] cstr = new Constraint[ (n * (n-1) )/2];
		int idx = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				cstr[idx++] = Choco.precedenceDisjoint(clique[i], clique[j], VariableUtils.createDirVariable(clique[i], clique[j], boolvarOptions));
			}
		}
		return cstr;
	}

}
