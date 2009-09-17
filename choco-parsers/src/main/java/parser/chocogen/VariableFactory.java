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
package parser.chocogen;

import static choco.Choco.makeIntVar;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import parser.absconparseur.components.PConstraint;
import parser.absconparseur.components.PExtensionConstraint;
import parser.absconparseur.components.PVariable;
import parser.absconparseur.tools.InstanceParser;

import java.util.Iterator;

/*
 * User:    hcambaza
 * Date:    15 avr. 2008
 */
public class VariableFactory extends ObjectFactory {

	public VariableFactory(Model m, InstanceParser parser) {
		super(m, parser);
	}

	/**
	 * Create a variable for pvar except if it is a fake variable
	 * (equal to another)
	 *
	 * @param pvar
	 * @return
	 */
	public IntegerVariable makeVariable(PVariable pvar) {
		IntegerVariable var;
		if (pvar.getChocovar() == null) {
			var = createVar(pvar);
			pvar.setChocovar(var);
		} else var = pvar.getChocovar();
		return var;
	}

	/**
	 * Make the internal decision to create Bound, Enum, LinkedList var
	 *
	 * @param pvar
	 * @return
	 */
	public IntegerVariable createVar(PVariable pvar) {
		IntegerVariable var;
		int nbvalues = pvar.getDomain().getValues().length;
		int span = pvar.getDomain().getMaxValue() - pvar.getDomain().getMinValue() + 1;
        if (span > nbvalues || nbvalues < 300) { //there are some holes
			var = makeIntVar(pvar.getName(), pvar.getDomain().getValues());
            //the second condition is for very sparse variables !
            if (isVarOnlyInvolvedInExtConstraint(pvar) || 20 * span > 100 * nbvalues) {
                m.addVariable("cp:blist", var);
            } else {
                m.addVariable("cp:enum", var);
            }
        } else {
			if (isVarOnlyInvolvedInExtConstraint(pvar)) {
				var = makeIntVar(pvar.getName(), pvar.getDomain().getMinValue(), pvar.getDomain().getMaxValue());					
                m.addVariable("cp:blist", var);
            } else {
				var = makeIntVar(pvar.getName(), pvar.getDomain().getMinValue(), pvar.getDomain().getMaxValue());
                m.addVariable("cp:bound", var);
            }
		}
		return var;
	}

	public boolean isVarOnlyInvolvedInExtConstraint(PVariable pvar) {
		if (parser.getNbExtensionConstraints() != 0) {
			Iterator it = parser.getMapOfConstraints().values().iterator();
			for (; it.hasNext();) {
				PConstraint pc = (PConstraint) it.next();
				if (pc instanceof PExtensionConstraint) {
					PVariable[] scope = pc.getScope();
					for (int i = 0; i < scope.length; i++) {
						if (scope[i] == pvar) return true;
					}
				}
			}
		}
		return false;
	}

    public int getNbvar() {
		return parser.getNbVariables();
	}

}
