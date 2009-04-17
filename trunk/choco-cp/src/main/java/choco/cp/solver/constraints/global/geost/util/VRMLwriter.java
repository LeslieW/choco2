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
package choco.cp.solver.constraints.global.geost.util;


import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.Setup;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.geost.ShiftedBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides a function that writes a certain solution (after solving and getting a solution) 
 * to a wrl file in the Virtual Reality Modeling Language (VRML). This file can later be visualized using the VRMLviewer tool.
 * The ouput file is written in a folder specified in the VRML_OUTPUT_FOLDER variable in the global.Constants class.
 */
public class VRMLwriter {


    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

	public static final String VRML_OUTPUT_FOLDER = "";


    public static boolean printVRML3D(Setup s, Constants c, String name){
        return printVRML3D(s, c, VRML_OUTPUT_FOLDER, name);
    }

    public static boolean printVRML3D(Setup s, Constants c, String outPut, String name)
	{
		String str;
    	str = ""+ outPut+ name + ".wrl";
	    try 
	    {
	   		BufferedWriter out = new BufferedWriter(new FileWriter(str));
	        LOGGER.log(Level.INFO, "writing the VRML to : {0}",  str);
			Iterator itr;
			itr = s.objects.keySet().iterator();
			int ObjectIterationNb = 0;
			
			int kdim= 0;
			if (c.getDIM() == 2)
				kdim = 2;
			else if (c.getDIM() == 3)
				kdim = 3;
			else
				kdim = 0;
			
			out.write("#VRML V2.0 utf8" + '\n');
			
			if (kdim > 1 && kdim < 4)
			{
				while(itr.hasNext())
				{
					ObjectIterationNb++;
					int id = ((Integer)itr.next()).intValue();
					//Obj o = s.objects.get(new Integer(id));
					Obj o = s.getObject(Integer.valueOf(id));
					
					String temp = "";
					for(int i = 0; i < c.getDIM(); i++)
					{
						temp = temp + o.getCoord(i).getInf() + " " ;
					}
					
					if (kdim == 2)
						temp = temp + " 0.0 ";
					
					
					out.write("Transform { translation " + temp + '\n' );
					out.write("children [ " + '\n');
					Vector<ShiftedBox> sb = s.shapes.get(Integer.valueOf(o.getShapeId().getInf()));
					
					Random rnd = new Random();
					float fDiff1 = rnd.nextFloat();
					float fDiff2 = rnd.nextFloat();
					float fDiff3 = rnd.nextFloat();
					float fSpec1 = rnd.nextFloat();
					float fSpec2 = rnd.nextFloat();
					float fSpec3 = rnd.nextFloat();
					float shine = rnd.nextFloat();
					String appearance = "appearance  Appearance {" +
							"material  Material { "+
							" ambientIntensity  0.25 "+
							" diffuseColor  " + fDiff1 + " " + fDiff2 + " " + fDiff3 + " "+
							" specularColor  " + fSpec1 + " " + fSpec2 + " " + fSpec3 + " "+
							" emissiveColor  0 0 0 "+
							" shininess  " + shine +" "+
							" transparency  0.40 }} ";
					
					double sizeOnZOfFirstShiftedBoxOfObject = 0.0;
					for(int i = 0; i < sb.size(); i++)
					{
						
						//out.write("Shape { geometry Box { size ");
						temp = "";
						for (int j = 0; j < c.getDIM(); j++)
						{
							float k = (sb.elementAt(i).getOffset(j) + (sb.elementAt(i).getSize(j) / 2.0f));
							temp = temp + "" +  k + " ";
						}
					
//						the translation (on the z-axis) for the text label on the box
						sizeOnZOfFirstShiftedBoxOfObject = sb.elementAt(0).getSize(c.getDIM() - 1);
						
						if (kdim == 2)
							temp = temp + " 0.0 ";
						
						out.write("Transform { translation " + temp + '\n' );
						out.write("children [ ");
						temp = "";
						for (int j = 0; j < c.getDIM(); j++)
						{
							int k = sb.elementAt(i).getSize(j);
							temp = temp + "" +  k + " ";
						}
						
						if (kdim == 2)
							temp = temp + " 0.0 ";
						
						out.write("Shape { " + appearance); 
						
						out.write(" geometry Box { size " + temp + "}}]}" + '\n' );
//						out.write(" geometry Box { size " + temp + "}}");
						
						
							

					}
					out.write(" Transform { translation .0 .0 " + sizeOnZOfFirstShiftedBoxOfObject + " \n" );
					out.write("children [ ");
					out.write("   Shape {appearance Appearance { material Material { diffuseColor .0 .0 .0  transparency  0}} " +
							"geometry Text { string [ \" " + o.getObjectId() + "\" ");
					out.write("]  fontStyle FontStyle { style \"BOLD\" size 2.0 }}}" + '\n');
					out.write(" ]} " + '\n');
					out.write(" ]} " + '\n');
				}
				
				String temp = "";
				
				if (kdim == 3)
				{
					temp = " 25.0 0.1 0.1 ";
//					the x-axis
					out.write("Transform { translation " + temp + '\n' );
					out.write("children [ ");
					out.write("Shape { " + "appearance  Appearance {" +
							"material  Material { "+
							" diffuseColor  1.0 0.0 0.0 }} ");
					out.write(" geometry Box { size 50.0 0.2 0.2 }}]}" + '\n' );
					
					temp = " 0.1 25.0 0.1 ";
//					the y-axis
					out.write("Transform { translation " + temp + '\n' );
					out.write("children [ ");
					out.write("Shape { " + "appearance  Appearance {" +
							"material  Material { "+
							" diffuseColor  0.0 1.0 0.0 }} ");
					out.write(" geometry Box { size 0.2 50.0 0.2 }}]}" + '\n' );
					
					temp = " 0.1 0.1 25.0 ";
//					the z-axis
					out.write("Transform { translation " + temp + '\n' );
					out.write("children [ ");
					out.write("Shape { " + "appearance  Appearance {" +
							"material  Material { "+
							" diffuseColor  0.0 0.0 1.0 }} ");
					out.write(" geometry Box { size 0.2 0.2 50.0 }}]}" + '\n' );
				}
				
				else if(kdim == 2)
				{
					temp = " 25.0 0.1 0.0 ";
//					the x-axis
					out.write("Transform { translation " + temp + '\n' );
					out.write("children [ ");
					out.write("Shape { " + "appearance  Appearance {" +
							"material  Material { "+
							" diffuseColor  1.0 0.0 0.0 }} ");
					out.write(" geometry Box { size 50.0 0.2 0.0 }}]}" + '\n' );
					
					temp = " 0.1 25.0 0.0 ";
//					the y-axis
					out.write("Transform { translation " + temp + '\n' );
					out.write("children [ ");
					out.write("Shape { " + "appearance  Appearance {" +
							"material  Material { "+
							" diffuseColor  0.0 1.0 0.0 }} ");
					out.write(" geometry Box { size 0.2 50.0 0.0 }}]}" + '\n' );
				}

				out.close();
			}
			else 
				LOGGER.severe("Dimension is not 2 nor 3 therefore no VRML file was written.");
			
			
	    }
	    catch (IOException e) {
	    	LOGGER.log(Level.SEVERE, "ERROR; Couldn't write VRML file");
	    }


		return true;
	}

	public static boolean printVRML3D(Setup s, Constants c,String prefix, int solNb)
	{
		
		String str =  ""+ prefix + "_" + solNb ;
		
		return printVRML3D(s, c, str);
	}

    public static boolean printVRML3D(Setup s, Constants c,String output, String prefix, int solNb)
	{

		String str =  ""+ prefix + "_" + solNb ;

		return printVRML3D(s, c, output, str);
	}


}
