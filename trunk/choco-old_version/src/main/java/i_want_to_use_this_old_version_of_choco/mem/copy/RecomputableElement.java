package i_want_to_use_this_old_version_of_choco.mem.copy;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
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
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/
public interface RecomputableElement {

    public static int BOOL = 0;
    public static int INT = 1;
    public static int VECTOR = 2;
    public static int INTVECTOR = 3;
    public static int BITSET = 4;
    public static int INTINTERVAL=5;
    
    public int getType();

    public int getTimeStamp();
}
