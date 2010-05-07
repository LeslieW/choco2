/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
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
package choco.cp.solver.preprocessor;

import choco.kernel.solver.Configuration;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 7 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 *
 * Specific {@link Configuration} extension for {@link PreProcessCPSolver}.
 */
public class PreProcessConfiguration extends Configuration {

     private static final long serialVersionUID = 683407604054648550L;

    /**
     * <br/><b>Goal</b>: Does it perform restart mode?
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = VALUE_FALSE)
    public static final String RESTART_MODE = "ppcp.restartMode";

    /**
     * <br/><b>Goal</b>: Active detection equalities between integer variables
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = VALUE_TRUE)
    public static final String INT_EQUALITY_DETECTION = "ppcp.detection.intEq";

    /**
     * <br/><b>Goal</b>: Active detection equalities between task variables
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = VALUE_TRUE)
    public static final String TASK_EQUALITY_DETECTION = "ppcp.detection.taskEq";

    /**
     * <br/><b>Goal</b>: Active disjunctive detection
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = VALUE_TRUE)
    public static final String DISJUNCTIVE_DETECTION = "ppcp.detection.disjunctive";

    /**
     * <br/><b>Goal</b>: Active expression detection
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = VALUE_TRUE)
    public static final String EXPRESSION_DETECTION = "ppcp.detection.expression";

    /**
     * <br/><b>Goal</b>: Active cliques detection
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = VALUE_TRUE)
    public static final String CLIQUES_DETECTION = "ppcp.detection.cliques";

    /**
     * <br/><b>Goal</b>: Active symetrie breaking detection during cliques detection
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = VALUE_TRUE)
    public static final String SYMETRIE_BREAKING_DETECTION = "ppcp.detection.cliques.symetrieBreaking";

public static String getPreProcessMsg(Configuration conf) {
		final StringBuilder b = new StringBuilder(18);
		if( conf.readBoolean(RESTART_MODE)) b.append(" RESTART    ");
		return new String(b);
	}


}
