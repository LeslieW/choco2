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

import choco.kernel.solver.search.ISearchLoop;
import choco.kernel.solver.variables.Var;
import choco.visu.components.ColorConstant;
import choco.visu.components.papplets.AChocoPApplet;
import choco.visu.components.papplets.TreeSearchPApplet;
import choco.visu.searchloop.ObservableStepSearchLoop;
import choco.visu.searchloop.State;
import traer.physics.Particle;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 6 nov. 2008
 * Since : Choco 2.0.1
 *
 * {@code NodeBrick} is a {@code IChocoBrick} representing a node in a tree search.
 * To pretty print it, it is based on a {@code ParticleSystem} object (@see http://www.cs.princeton.edu/~traer/physics/}.
 *
 * Powered by Processing        (http://processing.org/),
 *            traer.physics     (http://www.cs.princeton.edu/~traer/physics/),
 *            traer.animation   (http://www.cs.princeton.edu/~traer/animation/) 
 * 
 */

public final class NodeBrick extends AChocoBrick {

    private int color;
    private String name;

    public NodeBrick(AChocoPApplet chopapplet, Var var) {
        super(chopapplet, var);
    }



    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public final void refresh(final Object arg) {
        //Condition
        if (arg instanceof ISearchLoop) {
            ObservableStepSearchLoop ossl = (ObservableStepSearchLoop)arg;
            State state = ossl.state;
            switch (state) {
                case SOLUTION:
                    color = ColorConstant.GREEN;
                    CParticle p = ((TreeSearchPApplet) chopapplet).q.getLast();
                    Object[] o = ((TreeSearchPApplet) chopapplet).settings.remove(p.particle);
                    ((TreeSearchPApplet) chopapplet).settings.put(p.particle, new Object[]{o[0], color});
                    break;
                case DOWN:
                    color = ColorConstant.BLUE;
                    name = var.getName()+" = "+getValues();
                    ((TreeSearchPApplet) chopapplet).tsdepth++;
                    ((TreeSearchPApplet) chopapplet).q.add(addNode());
                    break;
                case UP:
                    if (((TreeSearchPApplet) chopapplet).q.size() > 1) {
                        ((TreeSearchPApplet) chopapplet).q.removeLast();
                    }
                    if((((TreeSearchPApplet) chopapplet).q.size() == 1)){
                        ((TreeSearchPApplet) chopapplet).tsdepth = 0;
                    }
                    ((TreeSearchPApplet) chopapplet).tswidth++;

                    break;
                case END:
                    break;
                case RESTART:
                    while(((TreeSearchPApplet) chopapplet).q.size() > 1) {
                        ((TreeSearchPApplet) chopapplet).q.removeLast();
                    }
                    ((TreeSearchPApplet) chopapplet).tsdepth = 0;
                    ((TreeSearchPApplet) chopapplet).tswidth++;
                default:
                    break;
            }
        }
    }

    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public final void drawBrick(final int x, final int y, final int widht, final int height) {
        //Not used
    }

    private CParticle addNode() {
        final CParticle p = new CParticle();
        p.particle = ((TreeSearchPApplet) chopapplet).physics.makeParticle();
        p.particle.setMass(1);
        p.name = var.getName();
        final CParticle q = ((TreeSearchPApplet) chopapplet).q.getLast();
        addSpacersToNode(p, q);
        makeEdgeBetween(p, q);
        p.particle.moveTo(q.particle.position().x() + ((TreeSearchPApplet) chopapplet).tswidth,
                q.particle.position().y() + ((TreeSearchPApplet) chopapplet).tsdepth, 0);
        ((TreeSearchPApplet) chopapplet).settings.put(p.particle, new Object[]{name, color});
        return p;
    }

    private void addSpacersToNode(final CParticle p, final CParticle r) {
        for (int i = 0; i < ((TreeSearchPApplet) chopapplet).physics.numberOfParticles(); ++i) {
            Particle q = ((TreeSearchPApplet) chopapplet).physics.getParticle(i);
            if (p.particle != q && p.particle != r.particle)
                ((TreeSearchPApplet) chopapplet).physics.makeAttraction(p.particle, q,
                        -TreeSearchPApplet.SPACER_STRENGTH, 20);
        }
    }

    private void makeEdgeBetween(final CParticle a, final CParticle b) {
        ((TreeSearchPApplet) chopapplet).physics.makeSpring(a.particle, b.particle, TreeSearchPApplet.EDGE_STRENGTH,
                TreeSearchPApplet.EDGE_STRENGTH, TreeSearchPApplet.EDGE_LENGTH);
    }

    public static CParticle createParticle(){
        return new CParticle();
    }


    public static final class CParticle {
        public Particle particle;
        public String name;
    }

}
