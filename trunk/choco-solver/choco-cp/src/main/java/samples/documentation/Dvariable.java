/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
package samples.documentation;

import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 9 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class Dvariable {

    public static void vintegervariable() {
        //totex vintegervariable
        IntegerVariable ivar1 = makeIntVar("ivar1", -10, 10);
        IntegerVariable ivar2 = makeIntVar("ivar2", 0, 10000, CPOptions.V_BOUND, CPOptions.V_NO_DECISION);
        IntegerVariable bool = makeBooleanVar("bool");
        //totex
    }

    public static void vsetvariable() {
        //totex vsetvariable
        SetVariable svar1 = makeSetVar("svar1", -10, 10);
        SetVariable svar2 = makeSetVar("svar2", 0, 10000, CPOptions.V_BOUND, CPOptions.V_NO_DECISION);
        //totex
    }

    public static void vrealvariable() {
        //totex vprecision
        Model m = new CPModel();
        m.setPrecision(0.01);        
        //totex

        //totex vrealvariable
        RealVariable rvar1 = makeRealVar("rvar1", -10.0, 10.0);
        RealVariable rvar2 = makeRealVar("rvar2", 0.0, 100.0, CPOptions.V_NO_DECISION, CPOptions.V_OBJECTIVE);
        //totex
    }

    public static void vtaskvariable() {
        //totex vtaskvariable
        TaskVariable tvar1 = makeTaskVar("tvar1", 0, 123, 18, CPOptions.V_ENUM);
        IntegerVariable start = makeIntVar("start", 0, 30);
        IntegerVariable end = makeIntVar("end", 10, 60);
        IntegerVariable duration = makeIntVar("duration", 7, 13);
        TaskVariable tvar2 = makeTaskVar("tvar2", start, end, duration);
        //totex
    }
}
