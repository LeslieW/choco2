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
package choco.cp.solver.constraints.strong.xmlmodel;

import choco.cp.CPOptions;
import choco.cp.solver.constraints.strong.StrongConsistencyManager;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import parser.absconparseur.components.PConstraint;
import parser.absconparseur.components.PExtensionConstraint;
import parser.absconparseur.components.PGlobalConstraint;
import parser.absconparseur.components.PIntensionConstraint;
import parser.absconparseur.tools.InstanceParser;
import parser.chocogen.ChocoFactory;
import parser.chocogen.ExtConstraintFactory;
import parser.chocogen.GloConstraintFactory;
import parser.chocogen.ModelConstraintFactory;

import java.util.*;

public class ChocoFactoryRPC extends ChocoFactory {
	public ChocoFactoryRPC(InstanceParser parser, Model m) {
		super(parser, m);
	}

	public void createConstraints(boolean forceExp, boolean light) {
		ExtConstraintFactory extFact = new ExtConstraintFactory(m, parser);
		extFact.preAnalyse(relfactory);

		final Collection<Constraint> maxRPCConstraints = new ArrayList<Constraint>();

		Map<String, PConstraint> pcstr = parser.getMapOfConstraints();
		String options = (forceExp ? CPOptions.E_DECOMP : "");
		for (PConstraint pc : pcstr.values()) {
			for (Constraint c : makeModelConstraint(pc)) {
				if (nbVariables(c) == 2) {
					maxRPCConstraints.add(c);
				} else {
					m.addConstraint(options, c);
				}
			}
		}
		if (maxRPCConstraints.size() > 2) {
			final Set<Variable> variables = new HashSet<Variable>();
			for (Constraint c : maxRPCConstraints) {
				for (Variable v : c.extractVariables()) {
					if (v instanceof IntegerVariable
							&& !(v instanceof IntegerConstantVariable)) {
						variables.add(v);
					}
				}
				// variables.addAll(Arrays.asList(c.getVariables()));
			}
			final Constraint c = new ComponentConstraint(StrongConsistencyManager.class,
					maxRPCConstraints.toArray(new Constraint[maxRPCConstraints
							.size()]), variables.toArray(new Variable[variables
							.size()]));

			if (light) {
				c.addOption("light");
			}

			m.addConstraint(c);
		} else {
			for (Constraint c : maxRPCConstraints) {
				m.addConstraint(options, c);
			}
		}
	}

	private int nbVariables(Constraint c) {
		int nb = 0;
		for (Variable v : c.extractVariables()) {
			if (v instanceof IntegerVariable
					&& !(v instanceof IntegerConstantVariable)) {
				nb++;
			}
		}
		return nb;
	}

	public Constraint[] makeModelConstraint(PConstraint pc) {
		Constraint[] c = null;
		if (pc instanceof PExtensionConstraint) {
			c = ExtConstraintFactory
					.makeExtConstraint((PExtensionConstraint) pc);
		} else if (pc instanceof PIntensionConstraint) {
			ModelConstraintFactory mcf = new ModelConstraintFactory(m, parser);
			c = mcf.makeIntensionConstraint((PIntensionConstraint) pc);
		} else if (pc instanceof PGlobalConstraint) {
			GloConstraintFactory gf = new GloConstraintFactory(m, parser);
			c = gf.makeGlobalConstraint((PGlobalConstraint) pc);
		}
		return c;
	}
}
