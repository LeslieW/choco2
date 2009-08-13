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

import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import noNamespace.FlatzincDocument;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*
* A class to read flatzinc file.
* For the moment, it just read xml file resulting of fzn2xml cmd.
*/
public class FlatzincParser {

    public static Solver parseFile(File file) throws XmlException, IOException {
        Model m = new CPModel();

        FlatzincDocument.Flatzinc fzn = FlatzincDocument.Factory.parse(file).getFlatzinc();
        HashMap<String, Object> items = new HashMap<String, Object>();
        ExpressionParser ep = new ExpressionParser(items);

        // 1: read predicats
        //TODO : read predicats

        // 2: read parameters and variables
        VariablesParser vp = new VariablesParser(items, ep);
        vp.readParametersAndVariables(fzn);


        // 3: read constraints
        ConstraintParser cp = new ConstraintParser(ep);
        cp.readConstraints(fzn, m);

        Solver s = new PreProcessCPSolver();
        s.read(m);

        // 4: read resolution parameters
        SolveExpressionParser sep = new SolveExpressionParser(ep);
        sep.readSolveExpression(fzn, s);

        return s;
    }
}
