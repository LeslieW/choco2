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
package choco.kernel.solver.search.limit;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 juin 2008
 * Since : Choco 2.0.0
 *
 */
public enum Limit {
    NODE("limit.node","nodes"),
    TIME("limit.time","millis."),
    FAIL("limit.fail","fails"),
    BACKTRACK("limit.backtrack","backtracks"),
    CPU_TIME("limit.cputime","millis. (cpu time)"),
    RESTART("","restarts");
   // SOLUTION("limit.solution", "first solutions");

    private final String property;

    private final String unit;


	private Limit(String property, String unit) {
		this.property = property;
		this.unit = unit;
	}


	public final String getUnit() {
		return unit;
	}

	public final String getProperty(){
        return property;
    }



}
