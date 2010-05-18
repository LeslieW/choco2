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
package samples.tutorials.trunk;

import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.tutorials.PatternExample;

import static choco.Choco.*;

/**
 *
 * compute euclidean distance between to points.
 * see https://sourceforge.net/forum/message.php?msg_id=5164540
 * @author Arnaud Malapert
 *
 */
public class Distance extends PatternExample {


	public IntegerVariable[] x,y;
    public IntegerVariable d;
    public IntegerExpressionVariable a, b, ub;

    public boolean ceil;

    @Override
    public void setUp(Object paramaters) {
        ceil = paramaters instanceof Boolean && (Boolean) paramaters;
    }

    @Override
    public void buildModel() {
        model =new CPModel();
        x = makeIntVarArray("x", 2, -4, 3, Options.V_BOUND);
		y = makeIntVarArray("y", 2, 2, 10, Options.V_BOUND);
		d = makeIntVar("dist", 0, 20, Options.V_BOUND);
		a  = power2( minus(x[0], x[1]) );
		b = power2(minus(y[0], y[1]));
		ub = power2(d);
		//add constraint : (d-1)^2 +1 <= a + b <= d^2
		if(ceil) {
			// ceil(d) = sqrt( a +b )
			IntegerExpressionVariable lb = plus( power2(minus(d,1)), 1);
			model.addConstraint(geq( plus(a,b), lb));
			model.addConstraint(leq( plus(a,b), ub));
		}else {
			// d = sqrt( a +b )
			model.addConstraint(eq( plus(a,b), ub));
		}
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
		solver.read(model);
    }

    @Override
    public void solve() {
        solver.maximize(solver.getVar(d), false);
    }

    @Override
    public void prettyOut() {
        LOGGER.info("maximize distantce between "+ StringUtils.pretty(x[0],y[0],x[1],y[1]));
        LOGGER.info("maximal distance = 10,63");
        LOGGER.info(" d = sqrt( (x0-x1)^2 + (y0-y1)^2 ) "+(ceil?"-- ceil":"--floor"));
        LOGGER.info("x = ["+ solver.getVar(x[0]).getVal()+","+ solver.getVar(x[1]).getVal()+"]");
        LOGGER.info("y = ["+ solver.getVar(y[0]).getVal()+","+ solver.getVar(y[1]).getVal()+"]");
        LOGGER.info("d = " + solver.getVar(d).getVal());
    }

	private IntegerExpressionVariable power2(IntegerExpressionVariable v) {
		return mult(v,v);
	}
	
	
	@Override
	public void execute() {
		super.execute(true);
	}

	public static void main(String[] args) {
        new Distance().execute(true);
        new Distance().execute(false);
	}
}
