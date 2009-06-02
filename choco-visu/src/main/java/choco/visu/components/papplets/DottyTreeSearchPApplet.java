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
package choco.visu.components.papplets;

import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.visu.components.IVisuVariable;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.bricks.DotBrick;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 12 nov. 2008
 * Since : Choco 2.0.1
 *
 * {@code DottyTreeSearchPApplet} is the {@code AChocoPApplet} that creates a dotty file of the tree search as output.
 *
 * Powered by Processing    (http://processing.org/)
 */

public final class DottyTreeSearchPApplet extends AChocoPApplet {

    private Var watch = null;
    private int intobjective = 0;
    double realobjective = 0.0;
    int last = -1;
    Boolean maximize = null;
    Boolean restart = null;
    FileWriter fw;
    private final String fileName;
    private final LinkedList<String> q = new LinkedList();
    private final LinkedList<String> nodes = new LinkedList();
    private final LinkedList<String> edges = new LinkedList();
    int width = -1;
    int depth = -1;
    protected static final String ROOT = "ROOT";
    int nodeLimit = 100;


    public DottyTreeSearchPApplet(final Object parameters) {
        super(parameters);

        nodes.add(ROOT + "[shape=circle,style=filled,fillcolor=darkorange,fontcolor=black]\n");
        q.add(ROOT);

        Object[] params = (Object[]) parameters;
        fileName = (String) params[0];
        nodeLimit = (Integer) params[1];
        watch = (Var) params[2];
        maximize = (Boolean) params[3];
        restart = (Boolean) params[4];

        if (watch != null) {
            if (watch instanceof IntDomainVar) {
                if (maximize) {
                    this.intobjective = Integer.MIN_VALUE;
                } else {
                    this.intobjective = Integer.MAX_VALUE;
                }
            } else {
                if (maximize) {
                    this.realobjective = Double.MIN_VALUE;
                } else {
                    this.realobjective = Double.MAX_VALUE;
                }
            }
        }
    }


    //////////////////////////////////////////////////Methods to dot the tree search////////////////////////////////////
    /**
     * Change the node limit
     * Default value : 100
     *
     * @param nodeLimit
     */
    public final void setNodeLimit(final int nodeLimit) {
        this.nodeLimit = nodeLimit;
    }

    /**
     * Update the edges of the graph
     *
     * @param back
     */
    public final void updateEdges(final boolean back) {
        String to = null;
        String from = null;
        StringBuffer sb = new StringBuffer();
        if (q.size() > 1) {
            if (back) {
                from = q.removeLast();
                //Cas d'un solveAll ou d'un optimize
                if (from.equals(q.getLast())) from = q.removeLast();
                to = q.getLast();
            } else {
                to = q.getLast();
                from = q.get(q.size() - 2);
            }
            sb.append(from).append(" -> ").append(to);
            if (back) {
                sb.append("[color=red]\n");
            } else {
                sb.append("[color=blue]\n");
            }

            edges.add(sb.toString());
        }
    }

    /**
     * Update the node of the graph
     *
     * @param sol
     */
    public final void updateNodes(final String name, final boolean sol) {
        if (q.getLast().equals(ROOT)) {
            width++;
            depth = -1;
        }
        if (!sol) depth++;
        final StringBuffer node = new StringBuffer("\"").append(name).append("(")
                .append(width).append(",").append(depth).append(")\"");
        q.add(node.toString());
        node.append("[label=\"").append(name).append("\",");
        if (sol) {
            nodes.removeLast();
            node.append("shape=circle,style=filled,fillcolor=green,fontcolor=black]\n");
        } else {
            node.append("shape=circle]\n");
        }
        nodes.add(node.toString());

    }

    /**
     * Update the objective if necessary
     */
    public final void updateObjective() {
        if (watch.isInstantiated()) {
            if (watch instanceof IntDomainVar) {
                IntDomainVar v = (IntDomainVar) watch;
                if (((maximize == Boolean.TRUE && v.getVal() > intobjective)
                        || (maximize == Boolean.FALSE && v.getVal() < intobjective))) {
                    intobjective = v.getVal();
                    if (last != -1) {
                        String tmp = nodes.remove(last).replace("green", "palegreen");
                        nodes.add(last, tmp);
                    }
                    last = nodes.size() - 1;
                }
            } else if (watch instanceof RealVar) {
                //TODO : do the realVar  case
            }
        }
    }

    /**
     * Print the dotty graph
     */
    public final void printGraph() {
        if (nodes.size() < nodeLimit) {
            if (watch != null) updateObjective();
            try {
                fw = new FileWriter(fileName);
                fw.write("digraph G {\n");

            } catch (IOException e) {
                throw new SolverException("Cannot create the graph file - " + e.getMessage());
            }
            try {
                for (int i = 0; i < nodes.size(); i++) {
                    fw.write(nodes.get(i));
                }
                for (int i = 0; i < edges.size(); i++) {
                    fw.write(edges.get(i));
                }
                fw.write("}");
                fw.close();
            } catch (IOException e) {
                throw new SolverException("Cannot write into the graph file - " + e.getMessage());
            }
        } else {
            LOGGER.warning("TOO MANY NODES :  the dot file will not be generated!");
        }

    }

    /**
     * Clean the queue and restart from scratch
     */
    public final void clean(final boolean checkRestart) {
        if (!checkRestart || restart == Boolean.TRUE) {
            q.clear();
            q.add(ROOT);
        }
    }


    /////////////////////////////////////////////Methods of AChocoPApplet///////////////////////////////////////////////
    /**
     * Initialize the ChocoPApplet with the list of concerning VisuVariables
     *
     * @param list of visu variables o watch
     */
    public final void initialize(final ArrayList<IVisuVariable> list) {
        Var[] vars = new Var[list.size()];
        for (int i = 0; i < list.size(); i++) {
            vars[i] = list.get(i).getSolverVar();
        }
        bricks = new AChocoBrick[list.size()];
        for (int i = 0; i < list.size(); i++) {
            IVisuVariable vv = list.get(i);
            Var v = vv.getSolverVar();
            bricks[i] = new DotBrick(this, v);
            vv.addBrick(bricks[i]);
        }
        this.init();
    }


    float s = 0;
    float xincrement = (float)0.1;

    /**
     * build the specific PApplet.
     * This method is called inside the {@code PApplet#setup()} method.
     */
    public final void build() {
        size(200, 200);
        stroke(255);
        smooth();
    }

    /**
     * draws the back side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, the sudoku grid is considered as a back side
     */
    public final void drawBackSide() {
        background(80);
        fill(100);
        noStroke();
        ellipse(100, 100, 155, 155);
        s += xincrement;
        for (int i = 0; i < 20; i++) {
            stroke(100 + 5 * i);
            strokeWeight(10);
            float j = (float)(0.05 * i);
            line(100, 100, cos(s + j) * 72 + 100, sin(s + j) * 72 + 100);
        }
    }

        /**
         * draws the front side of the representation.
         * This method is called inside the {@code PApplet#draw()} method.
         * For exemple, values of cells in a sudoku are considered as a back side
         */

    public final void drawFrontSide() {
        // nothing to do
    }

    /**
     * Return the ideal dimension of the chopapplet
     *
     * @return
     */
    public final Dimension getDimension() {
        return new Dimension(200, 300);
    }
}
