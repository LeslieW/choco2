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
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code NameOrValueBrick} is a {@code IChocoBrick} representing the value of a variable in two ways:
 * - the name of the variable if not yet instanciated,
 * - otherwise, print the instanciated values.
 *
 * Powered by Processing    (http://processing.org/)
 */

public final class NameOrValueBrick extends AChocoBrick{

    private final String name;
    private String value;
    private IntIterator it;

    public NameOrValueBrick(final AChocoPApplet chopapplet, final Var var, final int namelength, final int policy) {
        super(chopapplet, var);
        this.policy = policy;
        
        String n = var.getName();
        if(n.length() > namelength){
            n = n.substring(0, namelength-3);
            n  = n.concat("...");
        }
        this.name = n;
        this.value = name;
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
        }else{
            value = name;
        }
    }


    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public final void drawBrick(final int x, final int y, final int width, final int height) {
        chopapplet.fill(BLACK);
        chopapplet.text(value, alignText(y, value.length()), x);
        chopapplet.fill(WHITE);
    }
}