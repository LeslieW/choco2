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

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.strong.DomOverDDegRPC;
import choco.cp.solver.constraints.strong.maxrpcrm.MaxRPCrm;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.constraints.SConstraint;
import parser.absconparseur.tools.InstanceParser;
import parser.chocogen.XmlModel;

import java.io.File;
import java.text.MessageFormat;

/**
 * @author vion
 * 
 */
public class XmlModelRPC extends XmlModel {

	enum Filter {
		AC, MaxRPC, MaxRPCLight
	}

	private final boolean light;

	public XmlModelRPC(boolean light) {
		this.light = light;
	}

	public CPModel buildModel(InstanceParser parser) throws Exception, Error {
		boolean forceExp = false; // force all expressions to be handeled by arc
		// consistency

		CPModel m = new CPModel();
		ChocoFactoryRPC chocofact = new ChocoFactoryRPC(parser, m);
		chocofact.createVariables();
		chocofact.createRelations();
		chocofact.createConstraints(forceExp, light);

		return m;
	}

	public static void main(String[] args) throws Exception, Error {
		Filter filter = Filter.AC;
		for (int i = 0; i < args.length / 2; i++) {
			if ("-f".equals(args[2 * i])) {
				filter = Filter.valueOf(args[2 * i + 1]);
			}
		}
		String fichier = args[args.length - 1];

		File instance = new File(fichier);

		if (instance.isDirectory()) {
			for (File f : instance.listFiles()) {
				solve(f, filter);
			}
		} else {
			solve(instance, filter);
		}

	}

	private static void solve(File instance, Filter filter) throws Exception,
			Error {
		LOGGER.info("--------------");
		LOGGER.info(filter + " - " + instance);
		LOGGER.info("--------------");
		final XmlModel xs;
		switch (filter) {
		case MaxRPC:
			xs = new XmlModelRPC(false);
			break;
		case MaxRPCLight:
			xs = new XmlModelRPC(true);
			break;
		default:
			xs = new XmlModel();
		}

		InstanceParser parser = xs.load(instance);
		CPModel model = xs.buildModel(parser);

		// use the blackbox solver and blackbox search
		// PreProcessCPSolver s = xs.solve(model);
		// xs.postAnalyze(instance, parser, s);

		// use a blackbox solver or a standart CP solver
		// and perform the search by yourself
		// BlackBoxCPSolver s = new BlackBoxCPSolver();
		CPSolver s = new CPSolver();
		s.read(model);
		s.setVarIntSelector(new DomOverDDegRPC(s));
		s.setGeometricRestart(Math.min(Math.max(s.getNbIntVars(), 200), 400),
				1.4d);
		Boolean result = s.solve();

		if (result == null) {
			LOGGER.info("s UNKNOWN");
		} else if (!result) {
			LOGGER.info("s UNSATISFIABLE");
		} else {
			LOGGER.info("s SATISFIABLE");
			StringBuffer st =  new StringBuffer("v ");
			for (int i = 0; i < parser.getVariables().length; i++) {
				try {
					st.append(s.getVar(
							parser.getVariables()[i].getChocovar()).getVal());
				} catch (NullPointerException e) {
					LOGGER.severe(MessageFormat.format("{0}", parser.getVariables()[i].getChocovar().getLowB()));
				}
			}
            LOGGER.info(st.toString());
		}

		// LOGGER.info(s.pretty());
		s.printRuntimeStatistics();

        DisposableIterator<SConstraint> itr = s.getConstraintIterator();
		for (; itr
				.hasNext();) {

			SConstraint c = itr.next();
			if (c instanceof MaxRPCrm) {
				LOGGER.info("MaxRPC awake : " + MaxRPCrm.nbPropag);
				LOGGER.info("MaxRPC arc revises : "
						+ MaxRPCrm.nbArcRevise);
				LOGGER.info("MaxRPC pc revises : "
						+ MaxRPCrm.nbPCRevise);
			} else {
				LOGGER.info(c.pretty());
			}
		}
        itr.dispose();

	}
}
