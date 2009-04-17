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
package samples.Examples;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 *
 * compute euclidean distance between to points.
 * see https://sourceforge.net/forum/message.php?msg_id=5164540
 * @author Arnaud Malapert
 *
 */
public class Distance extends PatternExample{


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
        _m =new CPModel();
        x = makeIntVarArray("x", 2, -4, 3, "cp:bound");
		y = makeIntVarArray("y", 2, 2, 10, "cp:bound");
		d = makeIntVar("dist", 0, 20, "cp:bound");
		a  = power2( minus(x[0], x[1]) );
		b = power2(minus(y[0], y[1]));
		ub = power2(d);
		//add constraint : (d-1)^2 +1 <= a + b <= d^2
		if(ceil) {
			// ceil(d) = sqrt( a +b )
			IntegerExpressionVariable lb = plus( power2(minus(d,1)), 1);
			_m.addConstraint(geq( plus(a,b), lb));
			_m.addConstraint(leq( plus(a,b), ub));
		}else {
			// d = sqrt( a +b )
			_m.addConstraint(eq( plus(a,b), ub));
		}
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
		_s.read(_m);
    }

    @Override
    public void solve() {
        _s.maximize(_s.getVar(d), false);
    }

    @Override
    public void prettyOut() {
        LOGGER.info("maximize distantce between "+ChocoUtil.pretty(x[0],y[0],x[1],y[1]));
        LOGGER.info("maximal distance = 10,63");
        LOGGER.info(" d = sqrt( (x0-x1)^2 + (y0-y1)^2 ) "+(ceil?"-- ceil":"--floor"));
        LOGGER.info("x = ["+_s.getVar(x[0]).getVal()+","+_s.getVar(x[1]).getVal()+"]");
        LOGGER.info("y = ["+_s.getVar(y[0]).getVal()+","+_s.getVar(y[1]).getVal()+"]");
        LOGGER.info("d = " + _s.getVar(d).getVal());
    }

	private IntegerExpressionVariable power2(IntegerExpressionVariable v) {
		return mult(v,v);
	}

	public static void main(String[] args) {
        new Distance().execute(true);
        new Distance().execute(false);
	}
}
