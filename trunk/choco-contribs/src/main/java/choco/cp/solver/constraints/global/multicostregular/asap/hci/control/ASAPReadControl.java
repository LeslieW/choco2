/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  �(..)  |                           *
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
package choco.cp.solver.constraints.global.multicostregular.asap.hci.control;

import choco.cp.solver.constraints.global.multicostregular.asap.hci.abstraction.ASAPDataHandler;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 3:34:08 PM
 */

public class ASAPReadControl implements ActionListener {

	ASAPDataHandler model;

	public ASAPReadControl(ASAPDataHandler md) {
		model = md;
        
	}

    public void actionPerformed(ActionEvent arg0) {
		JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        File f = new File("/Users/julien/These/benchs/RosteringPbs");
        if (f.exists())
            chooser.setCurrentDirectory(f);
        
        FileFilter ff = new FileFilter()
        {

            public boolean accept(File file) {
                String s = file.getAbsolutePath().toLowerCase();
                return s.endsWith(".ros") || s.endsWith(".xml") || file.isDirectory();
            }

            public String getDescription() {
                return ("ROS AND XML");
            }
        };
        chooser.setFileFilter(ff);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			model.feed(chooser.getSelectedFile().getAbsolutePath());

		}

	}

}