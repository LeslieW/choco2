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

import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.variables.Var;
import static choco.visu.components.ColorConstant.BLACK;
import static choco.visu.components.ColorConstant.WHITE;
import choco.visu.components.papplets.AChocoPApplet;

import java.util.Random;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code HazardOrValueBrick} is a {@code IChocoBrick} representing the value of a variable in two ways:
 * - the variable is not yet instanciated, print a value inside the domain (in increasing order)
 * - otherwise, print the instanciated values.
 *
 * Powered by Processing    (http://processing.org/)
 */

public final class HazardOrValueBrick extends AChocoBrick{

    private String value;
    private boolean inst;
    private int last;
    private int low;
    private int upp;
    private IntIterator it;

    public HazardOrValueBrick(final AChocoPApplet chopapplet, final Var var, final int policy) {
        super(chopapplet, var);
        this.low = getLowBound();
        this.upp = getUppBound();
        this.value = Integer.toString(low);
        this.policy = policy;
        this.inst = false;
        Random r= new Random();
        this.last = r.nextInt(upp+1);
    }

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public final void refresh(final Object arg) {
        if(var.isInstantiated()){
            value = "";
           it = getDomainValues();
            while(it.hasNext()){
                if(value.length()>0){
                    value +=" - ";
                }
                value+=it.next();
            }
            this.inst = true;
        }else{
            low = getLowBound();
            upp = getUppBound();
            this.inst = false;
        }
    }


    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public final void drawBrick(final int x, final int y, final int width, final int height) {
        if(!inst){
            value = Integer.toString(last++);
            if(last > upp)last = low;
        }
        chopapplet.fill(BLACK);
        chopapplet.text(value, alignText(y, value.length()), x);
        chopapplet.fill(WHITE);
    }
}