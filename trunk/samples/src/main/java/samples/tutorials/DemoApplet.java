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
package samples.tutorials;

import choco.kernel.common.logging.ChocoLogging;

import javax.swing.*;
import java.util.logging.Logger;

// *********************************************
// *                   J-CHOCO                 *
// *   Copyright (c) F. Laburthe, 1999-2003    *
// *********************************************
// * Event-base contraint programming Engine   *
// *********************************************

public class DemoApplet extends JApplet {
    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

  @Override
public void init() {
    //Execute a job on the event-dispatching thread:
    //creating this applet's GUI.
    try {
      javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          DemoUI ui = new DemoUI();
          ui.createGUI(getContentPane());
        }
      });
    } catch (Exception e) {
      LOGGER.severe("createGUI didn't successfully complete");
    }
  }

}
