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
package choco.cp.solver.constraints.global.multicostregular.asap.hci.presentation;

import choco.cp.solver.constraints.global.multicostregular.asap.hci.abstraction.ASAPDataHandler;
import choco.cp.solver.constraints.global.multicostregular.asap.hci.control.ASAPSummaryControl;
import choco.cp.solver.constraints.global.multicostregular.asap.hci.control.ASAPButtonControl;
import choco.cp.solver.constraints.global.multicostregular.asap.hci.control.ASAPResultControl;
import choco.cp.solver.constraints.global.multicostregular.asap.hci.control.ASAPReadControl;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 1:02:07 PM
 */
public class ASAPMainWindow extends JFrame {


    protected Container pane;
    protected ASAPDataHandler model;

    public ASAPMainWindow(ASAPDataHandler data)
    {
        super("A.S.A.P. Planner");
        this.model = data;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit tools = Toolkit.getDefaultToolkit();
        Dimension d = tools.getScreenSize();
        d.setSize(d.getWidth()*2/3, d.getHeight()*2/3);
        this.setSize(d);
        this.setPreferredSize(d);
        this.pane  = this.getContentPane();
        this.pane.setLayout(new BorderLayout());

        addMenuBar();
        buildLeft();
        buildCenter();



    }

    public void buildLeft()
    {
        int w = this.getWidth()/5;
        ASAPSummaryPanel jPanelSummary  = new ASAPSummaryPanel(w);
        Dimension d = new Dimension(w,this.getHeight());
        jPanelSummary.setPreferredSize(d);


        pane.add(jPanelSummary,BorderLayout.WEST);

        ASAPSummaryControl sc = new ASAPSummaryControl(jPanelSummary);
        model.addObserver(sc);

        JButton bsolve = new JButton("Solve");
        ASAPButtonControl bc = new ASAPButtonControl(model,bsolve);
        model.addObserver(bc);
        jPanelSummary.add(bsolve,BorderLayout.SOUTH);


    }

    public void buildCenter()
    {
        ASAPResultPanel jPanelResult = new ASAPResultPanel(model);
        //jPanelResult.setPreferredSize(new Dimension(this.getWidth()*4/5,this.getHeight()));
        ASAPResultControl rc = new ASAPResultControl(jPanelResult);
        model.addObserver(rc);
        

        pane.add(jPanelResult, BorderLayout.CENTER);



      //  model.addObserver(mtj);
    }


    public void addMenuBar()
    {
        JMenuBar jmb = new JMenuBar();
        setJMenuBar(jmb);
        JMenu mfile = new JMenu("File");
        jmb.add(mfile);
        JMenuItem oimage = new JMenuItem("Import XML file...");
        ASAPReadControl cr = new ASAPReadControl(model);
        oimage.addActionListener(cr);
        mfile.add(oimage);

    }


}