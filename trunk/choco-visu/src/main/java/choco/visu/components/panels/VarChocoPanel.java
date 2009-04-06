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
package choco.visu.components.panels;

import choco.kernel.model.variables.Variable;
import choco.kernel.visu.components.IVisuVariable;
import choco.kernel.visu.components.panels.AVarChocoPanel;
import choco.visu.components.papplets.AChocoPApplet;
import choco.visu.components.papplets.ChocoPApplet;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 * Since : Choco 2.0.1
 *
 * {@code VarChocoPanel} is a specific {@code AVarChocoPanel} to add visualization of variables.
 */

public final class VarChocoPanel extends AVarChocoPanel {

    private final AChocoPApplet chopapplet;

    public VarChocoPanel(final String name, final Variable[] variables, final ChocoPApplet chocopapplet, final Object parameters) {
        this(name, variables, chocopapplet.path, parameters);
    }

    public VarChocoPanel(final String name, final Variable[] variables, final String path, Object parameters) {
        super(name, variables);
        //We get it by reflection !
        AChocoPApplet tmp = null;
        Class componentClass = null;
        try {
            componentClass = Class.forName(path);
        } catch (ClassNotFoundException e) {
            System.err.println("Component class could not be found: " + path);
            System.exit(-1);
        }
        try {
            Constructor constructeur =
                                 componentClass.getConstructor (new Class [] {Class.forName ("java.lang.Object")});
            tmp = (AChocoPApplet)constructeur.newInstance (new Object [] {parameters});
        } catch (InstantiationException e) {
            System.err.println("Component class could not be instantiated: " + path);
            System.exit(-1);
        } catch (IllegalAccessException e) {
            System.err.println("Component class could not be accessed: " + path);
            System.exit(-1);
        } catch (InvocationTargetException e) {
            System.err.println("Component class could not be invocated: " + path);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            System.err.println("Component class could not be found: " + path);
            System.exit(-1);
        } catch (NoSuchMethodException e) {
            System.err.println("Component class could not be get correct constructor: " + path);
            System.exit(-1);
        }
        this.setLayout(new BorderLayout());
        this.chopapplet = tmp;
        this.add(this.chopapplet);
    }

    public VarChocoPanel(final String name, final Variable[] variables, final Class classname, Object parameters) {
        super(name, variables);
        //We get it by reflection !
        AChocoPApplet tmp = null;
        try {
            Constructor constructeur =
                                 classname.getConstructor (new Class [] {Class.forName ("java.lang.Object")});
            tmp = (AChocoPApplet)constructeur.newInstance (parameters);
        } catch (InstantiationException e) {
            System.err.println("Component class could not be instantiated: " + classname);
            System.exit(-1);
        } catch (IllegalAccessException e) {
            System.err.println("Component class could not be accessed: " + classname);
            System.exit(-1);
        } catch (InvocationTargetException e) {
            System.err.println("Component class could not be invocated: " + classname);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            System.err.println("Component class could not be found: " + classname);
            System.exit(-1);
        } catch (NoSuchMethodException e) {
            System.err.println("Component class could not be get correct constructor: " + classname);
            System.exit(-1);
        }
        this.setLayout(new BorderLayout());
        this.chopapplet = tmp;
        this.add(this.chopapplet);
    }

    /**
     * Initialize every object of the frame.
     *
     * @param list list of visuvariables
     */
   public final void init(final ArrayList<IVisuVariable> list) {
        chopapplet.initialize(list);
    }

    /**
     * Return the dimensions of the Panel
     *
     * @return a {@code Dimension}
     */
    public final Dimension getDimensions() {
        return chopapplet.getDimension();
    }
}
