package choco.kernel.model;

import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.List;

public interface Model2 extends Model {
	void lt(IntegerExpressionVariable x, IntegerExpressionVariable y);

	void eq(IntegerExpressionVariable intV, int c);

	void eq(IntegerExpressionVariable x, IntegerExpressionVariable y);

	void allDifferent(String string, IntegerVariable[] vars);

	void leq(IntegerExpressionVariable x, IntegerExpressionVariable y);

	void leq(IntegerExpressionVariable x, int c);
	
	void feasTupleAC(List<int[]> tuples, IntegerVariable... vars);

	IntegerVariable makeIntVar(String name, int lb, int ub, String... options);


}
