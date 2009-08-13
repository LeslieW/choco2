/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package parser.chocogen.mzn;

import choco.kernel.model.ModelException;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import noNamespace.FlatzincDocument;
import noNamespace.MaximizeDocument;
import noNamespace.MinimizeDocument;
import noNamespace.SolveDocument;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 13 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class SolveExpressionParser {
    final ExpressionParser ep;


    public SolveExpressionParser(ExpressionParser ep) {
        this.ep = ep;
    }

    void readSolveExpression(FlatzincDocument.Flatzinc fzn, Solver solver){
        SolveDocument.Solve s = fzn.getSolve();
        if(s.isSetSatisfy()){
            solver.setFirstSolution(true);
		    solver.generateSearchStrategy();
        }else if(s.isSetMinimize()){
            MinimizeDocument.Minimize min = s.getMinimize();

            solver.setDoMaximize(false);

            Variable obj = (Variable)ep.buildId(min.getId());
            solver.setObjective(solver.getVar(obj));
//		    solver.setRestart(restart);

		    solver.setFirstSolution(false);
		    solver.generateSearchStrategy();
        }else if(s.isSetMaximize()){
            MaximizeDocument.Maximize max = s.getMaximize();

            solver.setDoMaximize(true);

            Variable obj = (Variable)ep.buildId(max.getId());
            solver.setObjective(solver.getVar(obj));
//		    solver.setRestart(restart);
		    solver.setFirstSolution(false);
		    solver.generateSearchStrategy();
        }else{
            throw new ModelException("SolveExpressionParser:readSolveExpression: unknown type");
        }
    }
}
