package choco.cp.solver.constraints.global.multicostregular.structure;

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

public interface INode extends Comparable {
    void resetShortestPathValues();

    void resetLongestPathValues();

    int getLayer();

    int getId();

    int getState();

    double getSpfs();

    void setSpfs(double spfs);

    double getSpft();

    void setSpft(double spft);

    IArc getSpts();

    void setSpts(IArc spts);

    IArc getSptt();

    void setSptt(IArc sptt);

    double getLpfs();

    void setLpfs(double lpfs);

    double getLpft();

    void setLpft(double lpft);

    IArc getLpts();

    void setLpts(IArc lpts);

    IArc getLptt();

    void setLptt(IArc lptt);

    boolean equals(Object o);

    int compareTo(Object o);
}
