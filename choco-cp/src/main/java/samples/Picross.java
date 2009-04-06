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
package samples;


import static choco.Choco.makeIntVar;
import static choco.Choco.regular;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 30 juil. 2007
 * Time: 09:30:26
 */

/**
 * This class implements a nonogram strategy.
 */


public class Picross extends CPModel {

    /**
     * The model variables.
     */
    public IntegerVariable[][] myvars;

    /**
     * The width of the nonogram
     */
    int X;

    /**
     * The height of the nonogram
     */
    int Y;

    /**
     * The pattern constraints on the rows
     */
    int[][] consRows;

    /**
     * The pattern constraints on the columns
     */
    int[][] consCols;


    /**
     * The automatons used to describe the pattern constraints
     */
    DFA[] dfas;


    /**
     * Create a new nanogram
     * @param consRows The constraint on the rows
     * @param consCols The constraint on the columns
     */

    public Picross(int[][] consRows,int[][] consCols, Solver s) {
        X=consCols.length;
        Y=consRows.length;


        this.consRows = consRows;
        this.consCols = consCols;



        this.makeVar();
        this.makeDFAs();
        this.makeConstraint();
        s.read(this);
    }

    /**
     * Creates the choco variable :
     * each variable represents a square, it is
     * wether 0 (white) or 1 (black)
     */
    public void makeVar() {
        myvars = new IntegerVariable[X][Y];
        for (int i = 0 ; i < X ; i++)
            for (int j = 0 ; j < Y ; j++)
                myvars[i][j] = makeIntVar("var "+i+" "+j+" ",0,1);

    }


    /**
     * Creates the Automaton used to describe the pattern constraints.
     * makeDFAs() first converts the two int[][] into regular expression
     * then it makes automatons out of the regular expressions.
     */
    public void makeDFAs() {
        dfas = new DFA[consRows.length + consCols.length];
        int idx = 0;
        for (int[] tab : consRows) {
            String regexp = "0*";
            for (int i = 0 ; i < tab.length ; i++) {
                for (int j = 0 ; j < tab[i] ; j++) {
                    regexp+="1";
                }
                if (i == tab.length - 1) {
                    regexp += "0*";
                }
                else {
                    regexp += "0+";
                }
            }
            System.out.println(regexp);
            dfas[idx++] = new DFA(regexp,X);
        }

        for (int[] tab : consCols) {
            String regexp = "0*";
            for (int i = 0 ; i < tab.length ; i++) {
                for (int j = 0 ; j < tab[i] ; j++) {
                    regexp+="1";
                }
                if (i == tab.length - 1) {
                    regexp += "0*";
                }
                else {
                    regexp += "0+";
                }
            }
            System.out.println(regexp);
            dfas[idx++] = new DFA(regexp,Y);
        }



    }


    /**
     * Post the regular constraint with the DFAs created earlier
     */
    public void makeConstraint() {

        Constraint[] cons = new Constraint[X+Y];

        for (int i = 0 ; i < myvars.length ; i++) {
            cons[i] = regular(dfas[i],myvars[i]);
        }

        for (int i = 0 ; i < myvars[0].length ; i ++) {
            IntegerVariable [] tmp = new IntegerVariable[Y];
            for (int j = 0 ; j < Y ; j++) {
                tmp[j] = myvars[j][i];

            }
            cons[i+X] = regular(dfas[i+X],tmp);
        }


        for (Constraint c : cons)
            this.addConstraint(c);

    }

    public String toString(Solver solver) {
        StringBuffer s = new StringBuffer();
        for (int i = 0 ; i < X ; i++) {
            for (int j = 0 ; j < Y ; j++) {
                s.append(solver.getVar(myvars[i][j]).getVal()).append("\t");

            }
            s.append("\n");
        }

        return s.toString();
    }

    /**
     * Class to draw the solution on a JPanel.
     */
    private class Drawing extends JPanel {

        public Drawing() {
            this.setSize(new Dimension(X,Y));
            this.setEnabled(true);
        }


        public void paintComponent(Graphics g, Solver solver) {
            super.paintComponent(g);
            int szX = 600/X;
            int szY = 600/Y ;
            for (int i = 0 ; i < X ; i++) {
                for (int j = 0 ; j < Y ; j++) {
                    g.setColor(java.awt.Color.gray);
                    g.fillRect(j*szY,i*szX,szY,szX);

                    if (solver.getVar(myvars[i][j]).getVal() == 1)
                        g.setColor(java.awt.Color.black);
                    else
                        g.setColor(java.awt.Color.white);

                    g.fillRect(j*szY+2,i*szX+2,szY-2,szX-2);

                }
            }

        }


    }

    /**
     * Draw the solution in a new Frame
     */
    public void showSolution()  {
        JFrame frame = new JFrame();
        frame.setTitle("NonoGram");
        frame.setSize(600, 600+Y);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Container contentPane = frame.getContentPane();
        contentPane.add(new Drawing());

        frame.setVisible(true);
    }


    public static void main(String[] args) {
        Picross p;
        Solver s = new CPSolver();

        /**
         * Creates a new nanogram, the parameters indicates how many black squares there must be and
         * the pattern it must follow. i.e. 1,1,1 means, 0 or more white, 1 black, 1 or more white, 1 black,
         * 1 or more white, 1 black, 0 or more white.
         */
        p = new Picross
                (
                        new int[][]
                                {
                                        new int[]{3},
                                        new int[]{5},
                                        new int[]{3,1},
                                        new int[]{2,1},
                                        new int[]{3,3,4},
                                        new int[]{2,2,7},
                                        new int[]{6,1,1},
                                        new int[]{4,2,2},
                                        new int[]{1,1},
                                        new int[]{3,1},
                                        new int[]{6},
                                        new int[]{2,7},
                                        new int[]{6,3,1},
                                        new int[]{1,2,2,1,1},
                                        new int[]{4,1,1,3},
                                        new int[]{4,2,2},
                                        new int[]{3,3,1},
                                        new int[]{3,3},
                                        new int[]{3},
                                        new int[]{2,1}
                                },
                        new int[][]
                                {
                                        new int[]{2},
                                        new int[]{1,2},
                                        new int[]{2,3},
                                        new int[]{2,3},
                                        new int[]{3,1,1},
                                        new int[]{2,1,1},
                                        new int[]{1,1,1,2,2},
                                        new int[]{1,1,3,1,3},
                                        new int[]{2,6,4},
                                        new int[]{3,3,9,1},
                                        new int[]{5,3,2},
                                        new int[]{3,1,2,2},
                                        new int[]{2,1,7},
                                        new int[]{3,3,2},
                                        new int[]{2,4},
                                        new int[]{2,1,2},
                                        new int[]{2,2,1},
                                        new int[]{2,2},
                                        new int[]{1},
                                        new int[]{1}
                                }, s
                );

        s.solve();


        System.out.println(p);

        p.showSolution();



    }



}
