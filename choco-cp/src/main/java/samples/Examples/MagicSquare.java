package samples.Examples;

import java.text.MessageFormat;
import java.util.logging.Level;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.integer.IntegerVariable;

public class MagicSquare extends PatternExample {
	/** order of the magic square */
	public int n;

	public int magicSum;

	protected IntegerVariable[][] vars;
	
	@Override
	public void setUp(Object parameters) {
		n = (Integer) parameters;
		magicSum = getMagicSum(n);
	}
	
	public static int getMagicSum(int n) {
		return n * (n * n + 1) / 2;
	}

	@Override
	public void buildModel() {
		_m = new CPModel();
		final int ub = n*n;
		vars = Choco.makeIntVarArray("v", n, n, 1, ub);
		// All cells of the matrix must be different
		_m.addConstraint( Choco.allDifferent(ArrayUtils.flatten(vars)));	
		final IntegerVariable[] varDiag1 = new IntegerVariable[n];
		final IntegerVariable[] varDiag2 = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			// All rows must be equal to the magic sum
			_m.addConstraint(Choco.eq(Choco.sum(vars[i]), magicSum));
			// All columns must be equal to the magic sum
			_m.addConstraint(Choco.eq(Choco.sum( ArrayUtils.getColumn(vars, i)), magicSum));
			//record diagonals variable
			varDiag1[i] = vars[i][i];
			varDiag2[i] = vars[(n - 1) - i][i];
		}
		// Every diagonal have to be equal to the magic sum
		_m.addConstraint(Choco.eq(Choco.sum(varDiag1), magicSum));
		_m.addConstraint(Choco.eq(Choco.sum(varDiag2), magicSum));
		//symmetry breaking constraint: enforce that the upper left corner contains the minimum corner value.
		_m.addConstraint( Choco.and( 
				Choco.lt(vars[0][0], vars[0][n-1]),
				Choco.lt(vars[0][0], vars[n-1][n-1]),
				Choco.lt(vars[0][0], vars[n-1][0])
		));

	}

	@Override
	public void buildSolver() {
		_s = new CPSolver();
		_s.read(_m);
		_s.setTimeLimit(500*1000);
	}

	@Override
	public void prettyOut() {
		if( LOGGER.isLoggable(Level.INFO)) {
			StringBuilder st = new StringBuilder();
			// Print of the solution
			st.append("order: ").append(n);
			st.append("\nmagic sum: ").append(magicSum);
			if( _s.existsSolution()) {
			for (int i = 0; i < n; i++) {
				st.append('\n');
				for (int j = 0; j < n; j++) {
					st.append(MessageFormat.format("{0} ", _s.getVar(vars[i][j]).getVal()));
				}
			}
			}else st.append("\nno solution to display!");
			LOGGER.info(st.toString());
		}
	}

	@Override
	public void solve() {
		_s.solve();
	}

	public static void main(String[] args) {
		new MagicSquare().execute(7);
	}

}
