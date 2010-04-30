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
package choco.cp.solver.configure;

import static choco.kernel.solver.Configuration.NOGOOD_RECORDING_FROM_RESTART;
import static choco.kernel.solver.Configuration.RESTART_AFTER_SOLUTION;
import static choco.kernel.solver.Configuration.RESTART_BASE;
import static choco.kernel.solver.Configuration.RESTART_GEOMETRICAL;
import static choco.kernel.solver.Configuration.RESTART_GEOM_GROW;
import static choco.kernel.solver.Configuration.RESTART_LUBY;
import static choco.kernel.solver.Configuration.RESTART_LUBY_GROW;
import static choco.kernel.solver.Configuration.RESTART_POLICY_LIMIT;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.restart.GeometricalRestartStrategy;
import choco.kernel.solver.search.restart.LubyRestartStrategy;
import choco.kernel.solver.search.restart.UniversalRestartStrategy;
/**
 * @author Arnaud Malapert</br> 
 * @since 27 juil. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class RestartFactory {


	private RestartFactory() {
		super();
	}

	public static void setLubyRestartPolicy(Solver solver, int base, int grow) {
		final Configuration conf = solver.getConfiguration();
		conf.putTrue(RESTART_LUBY);
		conf.putFalse(RESTART_GEOMETRICAL);
		conf.putInt(RESTART_BASE, base);
		conf.putInt(RESTART_LUBY_GROW, grow);
	}

	public static void setGeometricalRestartPolicy(Solver solver, int base, double grow) {
		final Configuration conf = solver.getConfiguration();
		conf.putFalse(RESTART_LUBY);
		conf.putTrue(RESTART_GEOMETRICAL);
		conf.putInt(RESTART_BASE, base);
		conf.putDouble(RESTART_GEOM_GROW, grow);
	}
	
		
	public static void setRecordNogoodFromRestart(Solver solver) {
		solver.getConfiguration().putTrue(Configuration.NOGOOD_RECORDING_FROM_RESTART);
	}
	
	public static void unsetRecordNogoodFromRestart(Solver solver) {
		solver.getConfiguration().putFalse(Configuration.NOGOOD_RECORDING_FROM_RESTART);
	}

	public static void cancelRestarts(Solver solver) {
		final Configuration conf = solver.getConfiguration();
		conf.putFalse(RESTART_AFTER_SOLUTION);
		conf.putFalse(RESTART_LUBY);
		conf.putFalse(RESTART_GEOMETRICAL);
		//FIXME set automatically the default value
		conf.remove(NOGOOD_RECORDING_FROM_RESTART);
		conf.remove(RESTART_BASE);
		conf.remove(RESTART_LUBY_GROW);
		conf.remove(RESTART_GEOM_GROW);
		conf.remove(RESTART_POLICY_LIMIT);
	}

	
	
	public static UniversalRestartStrategy createRestartStrategy(Solver solver) {
		final Configuration conf = solver.getConfiguration();
		final boolean bL = conf.readBoolean(RESTART_LUBY);
		final boolean bG = conf.readBoolean(RESTART_GEOMETRICAL);
		if(bL) {
			if(bG) throw new SolverException("Invalid Restart Settings: Two policies");
			else return new LubyRestartStrategy(conf.readInt(RESTART_BASE), conf.readInt(RESTART_LUBY_GROW));
		}else if( bG) {
			return new GeometricalRestartStrategy(conf.readInt(RESTART_BASE), conf.readDouble(RESTART_GEOM_GROW));
		}else return null;
	}

}
