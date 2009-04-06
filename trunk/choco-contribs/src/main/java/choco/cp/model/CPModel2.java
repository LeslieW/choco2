package choco.cp.model;

import choco.Choco;
import choco.kernel.model.Model2;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.List;

public class CPModel2 extends CPModel implements Model2 {

	public CPModel2() {
		super();
	}

	/**
	 * A constraint that ensures x < y
	 * 
	 * @param x
	 *            an integer expression variable
	 * @param y
	 *            an integer expression variable
	 */
	@Override
	public void lt(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		addConstraint(Choco.lt(x, y));
	}

	@Override
	public void eq(IntegerExpressionVariable intV, int c) {
		addConstraint(Choco.eq(intV, c));
	}

	@Override
	public void eq(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		addConstraint(Choco.eq(x, y));
	}

	@Override
	public void allDifferent(String options, IntegerVariable[] vars) {
		addConstraint(Choco.allDifferent(options, vars));
	}

	@Override
	public void leq(IntegerExpressionVariable x, IntegerExpressionVariable y) {
		addConstraint(Choco.leq(x, y));
	}

	@Override
	public void leq(IntegerExpressionVariable x, int c) {
		addConstraint(Choco.leq(x, c));
	}

	@Override
	public void feasTupleAC(List<int[]> tuples, IntegerVariable... vars) {
		addConstraint(Choco.feasTupleAC(tuples, vars));
	}

	@Override
	public IntegerVariable makeIntVar(String name, int lb, int ub,
			String... options) {
		final IntegerVariable var = Choco.makeIntVar(name, lb, ub, options);
		addVariable(var);
		return var;
	}

}
