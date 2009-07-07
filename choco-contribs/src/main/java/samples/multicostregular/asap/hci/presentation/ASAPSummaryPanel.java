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
package samples.multicostregular.asap.hci.presentation;

import choco.kernel.solver.Solver;

import javax.swing.*;
import java.awt.*;

import samples.multicostregular.asap.data.base.ASAPDate;
import samples.multicostregular.asap.hci.abstraction.ASAPDataHandler;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 1:57:07 PM
 */
public class ASAPSummaryPanel extends JPanel
{

    String name;
    int nbe;
    int nbd;
    ASAPDate start;
    ASAPDate end;
    int width;
    private boolean solving;
    private boolean solved;
    private long time  = 0;
    private Solver solver;

    public ASAPSummaryPanel(int width)
    {
        super(new BorderLayout());
        this.width = width;
        this.name = "";
        this.start = new ASAPDate(1970,1,1);
        this.end = new ASAPDate(1970,1,1);
        this.solving = false;
        this.solved = false;
        this.setBorder(BorderFactory.createBevelBorder(1));

    }


    public void paint(Graphics g)
    {
        super.paint(g);
        String s = "";
        for (int i = 0; i < width/12 - 6 ; i++)
            s+=" ";


        int x = 30;
        int y = 30;
        g.drawString(s+"SUMMARY",x,y);
        y+=10;
        g.drawString(s+"--------",x,y);
        x = 15;
        y = 80;
        g.drawString("File name \t:\t"+name,x,y);
        y+=20;
        g.drawString("Start Date \t:\t"+start.toString(),x,y);
        y+=20;
        g.drawString("End Date \t:\t"+end.toString(),x,y);
        y+=20;
        g.drawString("Nb employees \t:\t"+nbe,x,y);
        y+=20;
        g.drawString("Nb Days \t:\t"+nbd,x,y);
        y+=100;
        if (solving || solved)
        {
            long a;
            if (solving)
            {
                a = System.currentTimeMillis()-time;

            }
            else
            {
                a = this.solver.getTimeCount();
            }
            long i = a/1000;
            long d = (a/100)%10;
            String t = i+","+d;


            g.drawString("Time \t:\t"+t+" s",x,y);
            y+=20;
            try{
            g.drawString("Fails \t:\t"+this.solver.getFailCount(),x,y);
            }
            catch (NullPointerException ignored)
            {

            }
        }
    }

    public void setFile(String n)
    {
        this.name = n;
    }

    public void setStart(ASAPDate d)
    {
        this.start =d;
    }
    public void setEnd(ASAPDate d)
    {
        this.end =d;
    }

    public void setNbEmployee(int n)
    {
        this.nbe =n;
    }

    public void setNbDays(int n)
    {
        this.nbd =n;
    }




    public void setSolving(ASAPDataHandler d,boolean b) {
        this.solving = b;
        this.solver = d.getCPSolver();
        
        
        if (b){
            time = System.currentTimeMillis();
            UpdateThread t = new UpdateThread();
            t.start();
        }
    }

    public void setSolved(boolean b)
    {
        this.solved = b;
    }



    private class UpdateThread extends Thread
    {

        public void run()
        {
            while (solving)
            {
                repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Should not be interrupted");
                }
            }

        }

    }
}