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

import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomProblemGenerator {
	protected final static Logger LOGGER  = ChocoLogging.getEngineLogger();

	private Vector<GeostObject> objects;
	private Vector<ShiftedBox> sBoxes;
	private Model m;
	private int nbOfObjects; 
	private int nbOfShapes;
	private int nbOfShiftedBoxes; 
	private int maxLength;
	private int dim;
	public RandomProblemGenerator(int k, int nbOfObjects, int nbOfShapes, int nbOfShiftedBoxes, int maxLength)
	{
		objects = new Vector<GeostObject>();
		sBoxes  = new Vector<ShiftedBox>();
		m = new CPModel();
		this.nbOfObjects = nbOfObjects;
		this.nbOfShapes = nbOfShapes;
		this.nbOfShiftedBoxes = nbOfShiftedBoxes;
		this.maxLength = maxLength;
		this.dim = k;

	}
	
	public void generateProb()
	{
		generateRandomProblem(this.nbOfObjects, this.nbOfShapes, this.nbOfShiftedBoxes, this.maxLength);
	}
	
	private void generateRandomProblem(int nbOfObjects, int nbOfShapes, int nbOfShiftedBoxes, int maxLength)
	{
		if(nbOfShapes > nbOfShiftedBoxes)
		{
			LOGGER.info("The number of shifted boxes should be greater or equal to the number of shapes");
			return;
		}
		
		
		Random rnd = new Random();
		int[] maxDomain = new int [dim]; //maximum value of o.x in each dimension
		
		//first generate the shape IDs
		Vector<Integer> shapeIDS = new Vector<Integer>();
		for(int i = 0; i < nbOfShapes; i++)
		{
			shapeIDS.add(i, i);
		}
		
		//generate the objects
		for(int i = 0; i < nbOfObjects; i++)
		{
			//create the shape id randomly from the list of shape ids
			int index = rnd.nextInt(shapeIDS.size());
			int sid = shapeIDS.elementAt(index);
			shapeIDS.removeElementAt(index);
			
			
			IntegerVariable shapeId = Choco.makeIntVar("sid", sid, sid);
			IntegerVariable[] coords = new IntegerVariable[dim];
			for(int j = 0; j < dim; j++)
			{
				
				int max = rnd.nextInt(maxLength);
				while (max == 0)
					max = rnd.nextInt(maxLength);
				
				int min = rnd.nextInt(max);
				coords[j] = Choco.makeIntVar("x" + j, min, max);
            }
            m.addVariables("cp:bound", coords);
            IntegerVariable start = Choco.makeIntVar("start", 1, 1);
			IntegerVariable duration = Choco.makeIntVar("duration", 1, 1);
			IntegerVariable end = Choco.makeIntVar("end", 1, 1);
			objects.add(new GeostObject(this.dim, i, shapeId, coords, start, duration, end));
		}
		
		
		for(int i = 0; i < dim; i++)
		{
			int max = 0;
			for(int j = 0; j < objects.size(); j++)
			{
				if(max < objects.elementAt(j).getCoordinates()[i].getUppB())
					max = objects.elementAt(j).getCoordinates()[i].getUppB();
			}
			
			maxDomain[i] = max;
		}
		                           
		
		//create the shifted boxes
		
			//create a shifted box for each shape at least
			//then the remaining shifted boxes create them in a correct manner
			//respecting the offset and the previous shifted box created
			
			for(int j = 0; j < nbOfShapes; j++)
			{
				int[] t = new int[dim];
				int[] s = new int[dim];
				for(int k = 0; k < dim; k++)
				{
					t[k] = 0; //for the time being all the shappes have no offset from the origin so the offset of this box should be 0 so that 
					          //any other box we add is above or to the right. this way we know that the origin of the shape is the bottom left corner
					s[k] = rnd.nextInt(maxLength);
					while(s[k] == 0)
						s[k] = rnd.nextInt(maxLength); //All boxes has a minimum size of in every dimension
				}
				sBoxes.add(new ShiftedBox(j,t,s));
			}
			
			//repopulate the ShapeIDS Vector
			for(int j = 0; j < nbOfShapes; j++)
			{
				shapeIDS.add(j, j);
			}
			
			int remainingSBtoCreate = nbOfShiftedBoxes - nbOfShapes;
			while (remainingSBtoCreate > 0)
			{
				//pick a shape
				int index = rnd.nextInt(shapeIDS.size());
				int sid = shapeIDS.elementAt(index);
								
				//get an already created shifted box for that shape
				for (int i = 0; i < sBoxes.size(); i++)
				{
					if(sBoxes.elementAt(i).getShapeId() == sid)
					{
						index = i;
						break;
					}
				}
				
				int[] t = new int[dim];
				int[] s = new int[dim];
				for(int k = 0; k < dim; k++)
				{
					t[k] = rnd.nextInt(sBoxes.elementAt(index).getSize(k)); //so that it stays touching the other box
					s[k] = rnd.nextInt(maxLength);
					while(s[k] == 0)
						s[k] = rnd.nextInt(maxLength);//All boxes has a minimum size of in every dimension
				}
				sBoxes.add(new ShiftedBox(sBoxes.elementAt(index).getShapeId(),t,s));
				
				remainingSBtoCreate--;
			}	
	}


	public Vector<GeostObject> getObjects() {
		return objects;
	}

	public Vector<ShiftedBox> getSBoxes() {
		return sBoxes;
	}

	public Model getModel() {
		return m;
	}
	
}
