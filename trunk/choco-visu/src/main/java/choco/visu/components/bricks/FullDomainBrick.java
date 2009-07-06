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
package choco.visu.components.bricks;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.Var;
import static choco.visu.components.ColorConstant.BLUE;
import static choco.visu.components.ColorConstant.GREEN;
import choco.visu.components.papplets.AChocoPApplet;

import java.util.BitSet;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code FullDomainBrick} is a {@code IChocoBrick} representing the domain of a variable as an array of colored
 * square.
 * Green square means inside the domain,
 * Blue square means outside the domain.
 *
 * Powered by Processing    (http://processing.org/)
 */

public final class FullDomainBrick extends AChocoBrick{

    private final BitSet values;
    private final int capacity;
    private final int lb;
    private final int up;
    private final int offset;
    private DisposableIntIterator it;
    private final int size;


    public FullDomainBrick(final AChocoPApplet chopapplet, final Var var, final int size) {
        super(chopapplet, var);
        this.size = size;
        this.lb = getLowBound();
        this.up = getUppBound();
        this.capacity = up - lb;
        this.offset = - lb;
        this.values = new BitSet(capacity);
        this.it = getDomainValues();
        while(it.hasNext()){
            this.values.set(it.next()+offset);
        }
        it.dispose();
    }

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public final void refresh(final Object arg) {
        values.clear();
        it = getDomainValues();
        while(it.hasNext()){
            values.set(it.next()+offset);
        }
    }


    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public final void drawBrick(final int x, final int y, final int width, final int height) {
        for(int j = 0; j < capacity+1; j++){
            if(values.get(j)){
                chopapplet.fill(GREEN);
            }else{
                chopapplet.fill(BLUE);
            }
            chopapplet.rect(y + size*j, x,  width, height);
        }
    }
}
