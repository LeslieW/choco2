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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.visu.components.IVisuVariable;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 24 oct. 2008
 * Since : Choco 2.0.1
 *
 * Interface {@code IChocoPanel} defined methods for Choco Panel.
 */

public interface IChocoPanel {

    final static Logger LOGGER = ChocoLogging.getSolverLogger();


    /**
     * Return the name of the ChocoPanel
     * @return name the name of the panel
     */
    public String getPanelName();

    /**
     * Return the dimensions of the Panel
     * @return a {@code Dimension} 
     */
    public Dimension getDimensions();
    
     /**
     * Initialize every object of the frame.
     * @param list list of visuvariables
     */
    public void init(final ArrayList<IVisuVariable> list);

}
