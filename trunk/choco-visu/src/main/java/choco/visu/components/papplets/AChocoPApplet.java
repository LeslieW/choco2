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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.visu.components.IVisuVariable;
import choco.visu.components.ColorConstant;
import choco.visu.components.bricks.AChocoBrick;
import gnu.trove.TIntObjectHashMap;
import processing.core.PApplet;
import processing.core.PFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;
/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 30 oct. 2008
 * Since : Choco 2.0.1
 *
 * A {@code AChocoPApplet} is an abstract class, a specialization of Processing.PApplet.
 * It allows Choco to visualize Variables or Constraints modifications.
 * It is based on {@code Processing.PApplet} object to visualize in a pretty way.
 *
 * Powered by Processing    (http://processing.org/)
 */

public abstract class AChocoPApplet extends PApplet {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

    protected AChocoBrick[] bricks;

    protected PFont font;

    private final Object parameters;

    public static TIntObjectHashMap<PFont> listFont;

    static{
        listFont = new TIntObjectHashMap<PFont>();
    }

    /**
     * Create a new instanciation of AChocoPApplet
     * @param parameters parameters of the build (size, numnber of variables,...
     */
    protected AChocoPApplet(final Object parameters) {
        this.parameters = parameters;
        //this.font = createFont("Serif", 18, true);
        try{
            this.font = loadFont("./fonts/FreeMono-18.vlw") ;
        }catch(Exception e){
            LOGGER.warning("\"FreeMono-18.vlw\" not found. Use default one instead (can be slower)");
            this.font = getFont(18);
        }
    }

    /**
     * Initialize the ChocoPApplet with the list of concerning VisuVariables
     * @param list of visu variables o watch
     */
    public abstract void initialize(final ArrayList<IVisuVariable> list);


    /**
     * build the specific PApplet.
     * This method is called inside the {@code PApplet#setup()} method.
     */
    public abstract void build();

    /**
     * draws the back side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, the sudoku grid is considered as a back side
     */
    public abstract void drawBackSide();

    /**
     * draws the front side of the representation.
     * This method is called inside the {@code PApplet#draw()} method.
     * For exemple, values of cells in a sudoku are considered as a back side
     */
    public abstract void drawFrontSide();


    /**
     *Called once when the program is started.
     * Used to define initial enviroment properties such as screen size, background color, loading images, etc.
     * before the {@code PApplet#draw()} begins executing.
     * Variables declared within {@code PApplet#setup()} are not accessible within other functions,
     * including {@code PApplet#draw()}.
     * There can only be one {@code PApplet#setup()} function for each program and it should not be called
     * again after it's initial execution. 
     */
    public final void setup() {
        frameRate(15);
        build();
        
    }


    /**
     * Called directly after {@code PApplet#setup()} and continuously executes the lines of code contained
     * inside its block until the program is stopped or {@code PApplet#noLoop()} is called.
     * The {@code PApplet#draw()} function is called automatically and should never be called explicitly.
     * It should always be controlled with {@code PApplet#noLoop()}, {@code PApplet#redraw()} and {@code PApplet#Loop()}.
     * After {@code PApplet#noLoop()} stops the code in {@code PApplet#draw()} from executing, {@code PApplet#redraw()}
     * causes the code inside {@code PApplet#draw()} to execute once and {@code PApplet#loop()} will causes the code
     * inside {@code PApplet#draw()} to execute continuously again.
     * The number of times {@code PApplet#draw()} executes in each second may be controlled with
     * the {@code PApplet#delay()} and {@code PApplet#frameRate()} functions.
     * There can only be one {@code PApplet#draw()} function for each sketch and {@code PApplet#draw()} must exist
     * if you want the code to run continuously or to process events such as {@code PApplet#mousePressed()}. 
     * Sometimes, you might have an empty call to {@code PApplet#draw()} in your program.
     */
    public final void draw() {
        drawBackSide();
        drawFrontSide();
    }

    /**
     * Specify the font to use
     * @param font PFont font
     */
    public final void setFont(PFont font){
        this.font = font;
    }

    /**
     * Specify the font to use
     * @param path the path to the font
     */
    public final void setFont(String path){
        this.font = loadFont(path);
    }

    /**
     * Return the ideal dimension of the chopapplet
     * @return ideal dimensio
     */
    public abstract Dimension getDimension();

    protected final void crossruling(final int size){
        final Dimension d = this.getSize();
        stroke(ColorConstant.GRAY);
        for(int i = 0; i < d.getHeight(); i+=size){
            line(0, i, (float)d.getWidth(), i);
        }
        for(int i = 0; i < d.getWidth(); i+=size){
            line(i, 0, i,  (float)d.getHeight());
        }

    }

    public Object getParameters() {
        return parameters;
    }

    public PFont getFont(int size){
        PFont font = listFont.get(size);
        if(font == null){
            font = createFont("Serif", size);
            listFont.put(size, font);
        }
        return font;
    }
}
