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
package choco.kernel.visu.components.panels;

import choco.kernel.model.variables.Variable;

import java.awt.*;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 24 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code AVarChocoPanel} is a specific {@code Panel} object to add a {@code AChocoPApplet} observing
 * variables interactions.
 * 
 */

public abstract class AVarChocoPanel extends Panel implements IChocoPanel {

    protected final String name;

    protected final Variable[] variables;


    public AVarChocoPanel(String name, Variable[] variables){
        this.name = name;
        this.variables = variables;
    }

    /**
     * Return every variables of the panel
     * @return an array of the visu variables observed
     */
    public final Variable[] getVariables() {
        return variables;
    }

    /**
     * Return the name of the ChocoPanel
     *
     * @return name the name of the panel
     */
    public final String getPanelName() {
        return name;
    }
}
