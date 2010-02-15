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
package choco.kernel.model.variables.integer;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;

import java.util.Arrays;
import java.util.Properties;

/*
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 5 aout 2008
 * Since : Choco 2.0.0
 *
 */
public class MetaIntegerExpressionVariable extends IntegerExpressionVariable {
	protected Constraint[] constraints;

	public MetaIntegerExpressionVariable(Operator operator, Constraint c, IntegerExpressionVariable... variables) {
		super(null, operator, variables);
		constraints = new Constraint[]{c};
	}

	public Constraint[] getConstraints() {
		return constraints;
	}


	@Override
	protected Variable[] doExtractVariables() {
		Variable[] tmp = getExpVariables();
		for(Constraint c : constraints){
			tmp = ArrayUtils.append(tmp, c.extractVariables());
		}
		return ArrayUtils.getNonRedundantObjects(Variable.class, tmp);
	}
	

	public final void findManager(Properties propertiesFile) {
		super.findManager(propertiesFile);
		for (int i = 0; i < constraints.length; i++) {
			Constraint constraint = constraints[i];
			constraint.findManager(propertiesFile);
		}
	}

}
