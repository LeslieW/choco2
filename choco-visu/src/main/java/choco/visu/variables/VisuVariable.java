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
package choco.visu.variables;

import choco.kernel.solver.variables.Var;
import choco.kernel.visu.components.IVisuVariable;
import choco.kernel.visu.components.bricks.IChocoBrick;

import java.util.ArrayList;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 24 oct. 2008
 * Since : Choco 2.0.1
 *
 * A {@code VisuVariable} is on object taht links a {@code Var} to its graphical representations
 * (one or more {@code IChocoBrick}).
 */

public final class VisuVariable implements IVisuVariable {

    protected final Var var;
    protected final ArrayList<IChocoBrick> brick;

    public VisuVariable(Var var) {
        this.var = var;
        brick = new ArrayList<IChocoBrick>(16);
    }

    /**
     * Return the solver variable
     * @return
     */
    public Var getSolverVar() {
        return var;
    }

    /**
     * Add a brock observer to the visuvariable
     * @param b
     */
    public final void addBrick(final IChocoBrick b){
        brick.add(b);
    }

    public final IChocoBrick getBrick(final int i){
        return brick.get(i);
    }


    /**
     * refresh every visual representation of the variable
     */
    public final void refresh(final Object arg){
        for(IChocoBrick b: brick){
            b.refresh(arg);
        }
    }

    @Override
    public long getIndex() {
        return var.getIndex();
    }
}
