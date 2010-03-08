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
package choco.kernel.solver.variables.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.Domain;

/*
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Since : Choco 2.0.0
 *
 */
public interface SetDomain extends Domain {

    public boolean isInstantiated();

    public SetSubDomain getKernelDomain();

    public SetSubDomain getEnveloppeDomain();

    boolean addToKernel(int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException;

    boolean remFromEnveloppe(int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException;

    boolean instantiate(int[] x, final SConstraint cause, final boolean forceAwake) throws ContradictionException;

    public DisposableIntIterator getKernelIterator();

    public DisposableIntIterator getEnveloppeIterator();

    public DisposableIntIterator getOpenDomainIterator();
}
