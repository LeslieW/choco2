/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
