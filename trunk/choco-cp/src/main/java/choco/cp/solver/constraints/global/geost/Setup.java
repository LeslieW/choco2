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
package choco.cp.solver.constraints.global.geost;

import choco.cp.solver.constraints.global.geost.dataStructures.HeapAscending;
import choco.cp.solver.constraints.global.geost.dataStructures.HeapDescending;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.model.variables.geost.ShiftedBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This is a very important class. It contains all the variables and objects the constraint needs.
 * Also it contains functions that the user and the constraint use to access the shapes, objects as well as the external constraints in the Geost.
 */

public class Setup {
	Constants cst;

	/**
	 * Creates a Setup instance for a given Constants class
	 * @param c An instance of the constants class
	 */
	public Setup(Constants c)
	{
		cst = c;
	}

	/**
	 * A hashtable where the key is a shape_id. And for every shape_id there is a pointer to the set of shifted_boxes that belong to this shape.
	 * This hashtable contains all the shapes (and their shifted boxes) of all the objects in the geost constraint.
	 */
	public Hashtable<Integer, Vector<ShiftedBox>> shapes = new Hashtable<Integer, Vector<ShiftedBox>>();
	/**
	 * A hashtable where the key is an object_id. And for every object_id there is a pointer to the actual object.
	 * This hashtable contains all the objects that goest needs to place.
	 */
	public Hashtable<Integer, Obj> objects = new Hashtable<Integer, Obj>();
	/**
	 * A Vector containing ExternalConstraint objects. This vector constains all the external constraints that geost needs to deal with.
	 */
	public Vector<ExternalConstraint> constraints = new Vector<ExternalConstraint>();
	/**
	 * A heap data structure containting elements in ascending order (lexicographically).
	 * This is not used anymore.
	 * It was used inside that pruneMin function and we used to store in it the internal constraints.
	 * This way we coulld extract the active internal constraints at a specific position
	 */
	public HeapAscending  ictrMinHeap = new HeapAscending();
	/**
	 * A heap data structure containting elements in descending order (lexicographically).
	 * This is not used anymore.
	 * It was used inside that pruneMax function and we used to store in it the internal constraints.
	 * This way we coulld extract the active internal constraints at a specific position
	 */
	public HeapDescending ictrMaxHeap = new HeapDescending();


	public void insertShape(int sid, Vector<ShiftedBox> shiftedBoxes)
	{
		shapes.put(Integer.valueOf(sid), shiftedBoxes);
	}

	public void insertObject(int oid, Obj o)
	{
		objects.put(Integer.valueOf(oid), o);
	}


	public Vector<ShiftedBox> getShape(int sid)
	{
		return shapes.get(Integer.valueOf(sid));
	}

	public Obj getObject(int oid)
	{
		return objects.get(Integer.valueOf(oid));
	}

	public int getNbOfObjects()
	{
		return objects.size();
	}


	public int getNbOfShapes()
	{
		return shapes.size();
	}

	/**
	 * This function calculates the number of the domain variables in our problem.
	 */
	public int getNbOfDomainVariables()
	{
		int originOfObjects = getNbOfObjects() * cst.getDIM(); //Number of domain variables to represent the origin of all objects
		int otherVariables = getNbOfObjects() * 4; //each object has 4 other variables: shapeId, start, duration; end
		return originOfObjects + otherVariables;
	}

	/**
	 * Creates the environment and sets up the problem for the geost constraint given a parser object.
	 */
//	public void createEnvironment(InputParser parser)
//	{
//		for(int i = 0; i < parser.getObjects().size(); i++)
//		{
//			insertObject(parser.getObjects().elementAt(i).getObjectId(), parser.getObjects().elementAt(i));
//		}
//
//		for(int i = 0; i < parser.getShapes().size(); i++)
//		{
//			insertShape(parser.getShapes().elementAt(i).getShapeId(), parser.getShapes().elementAt(i).getShiftedBoxes());
//		}
//	}

//	public void SetupTheProblem(Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes)
//	{
//		for(int i = 0; i < objects.size(); i++)
//		{
//			addObject(objects.elementAt(i));
//		}
//
//		for(int i = 0; i < shiftedBoxes.size(); i++)
//		{
//			addShiftedBox(shiftedBoxes.elementAt(i));
//		}
//
//	}
	/**
	 * Given a Vector of Objects and a Vector of shiftedBoxes and a Vector of ExternalConstraints it sets up the problem for the geost constraint.
	 */
	public void SetupTheProblem(Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes, Vector<ExternalConstraint> ectr)
	{
		for(int i = 0; i < objects.size(); i++)
		{
			addObject(objects.elementAt(i));
		}

		for(int i = 0; i < shiftedBoxes.size(); i++)
		{
			addShiftedBox(shiftedBoxes.elementAt(i));
		}

		for(int i = 0; i < ectr.size(); i++)
		{
			addConstraint(ectr.elementAt(i));
			for(int j = 0; j < ectr.elementAt(i).getObjectIds().length; j++)
			{
				getObject(ectr.elementAt(i).getObjectIds()[j]).addRelatedExternalConstraint(ectr.elementAt(i));
			}
		}

	}

	public void addConstraint(ExternalConstraint ectr)
	{
		constraints.add(ectr);
	}

	public Vector<ExternalConstraint> getConstraints()
	{
		return constraints;
	}

	public HeapAscending getIctrMinHeap() {
		return ictrMinHeap;
	}

	public HeapDescending getIctrMaxHeap() {
		return ictrMaxHeap;
	}

	public void addShiftedBox(ShiftedBox sb)
	{
		if (shapes.containsKey(Integer.valueOf(sb.getShapeId()))) {
			shapes.get(Integer.valueOf(sb.getShapeId())).add(sb);
		} else
		{
			Vector<ShiftedBox> v = new Vector<ShiftedBox>();;
			v.add(sb);
			shapes.put(Integer.valueOf(sb.getShapeId()), v);
		}
	}

	public void addObject(Obj o)
	{
		if (objects.containsKey(Integer.valueOf(o.getObjectId()))) {
			System.out.println("Trying to add an already existing object. In addObject in Setup");
		} else {
			objects.put(Integer.valueOf(o.getObjectId()), o);
		}
	}

	public Enumeration<Integer> getObjectKeys()
	{
		return objects.keys();
	}

	public Enumeration<Integer> getShapeKeys()
	{
		return shapes.keys();
	}

	public Set<Integer> getObjectKeySet()
	{
		return objects.keySet();
	}

	public Set<Integer> getShapeKeySet()
	{
		return shapes.keySet();
	}
	/**
	 * Prints to the output console the objects and the shapes of the problem.
	 */
	public void print()
	{
		Iterator itr;
		itr = objects.keySet().iterator();
		while(itr.hasNext())
		{
			int id = ((Integer)itr.next()).intValue();
			Obj o = objects.get(Integer.valueOf(id));
			System.out.println("object id: " + id);
			System.out.println("    shape id: " + o.getShapeId().getInf());
			for (int i = 0; i < cst.getDIM(); i++) {
				System.out.println("    Coords x" + i + " : " + o.getCoord(i).getInf() + "    " + o.getCoord(i).getSup());
			}
		}

		itr = shapes.keySet().iterator();
		while(itr.hasNext())
		{
			int sid = ((Integer)itr.next()).intValue();
			Vector<ShiftedBox> sb = shapes.get(Integer.valueOf(sid));
			System.out.println("shape id: " + sid);
			for(int i = 0; i < sb.size(); i++)
			{
				String offset = "";
				String size = "";
				for (int j = 0; j < cst.getDIM(); j++)
				{
					offset = offset + sb.elementAt(i).getOffset(j) + "  ";
					size = size + sb.elementAt(i).getSize(j) + "  ";
				}
				System.out.println("    sb" + i + ": ");
				System.out.println("       Offset: " +  offset);
				System.out.println("       Size: " +  size);
			}
		}
	}
	/**
	 * Prints to a file that can be easily read by a person the objects and the shapes of the problem.
	 * The file to be written to is specified in the global variable OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS,
	 * present in the global.Constants class.
	 */
	public boolean printToFileHumanFormat(String path)
	{
	    try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(path));

			Iterator itr;
			itr = objects.keySet().iterator();
			while(itr.hasNext())
			{
				int id = ((Integer)itr.next()).intValue();
				Obj o = objects.get(Integer.valueOf(id));
				out.write("object id: " + id + '\n');
				out.write("    shape id: " + o.getShapeId().getInf() + '\n');
				for (int i = 0; i < cst.getDIM(); i++) {
					out.write("    Coords x" + i + " : " + o.getCoord(i).getInf() + "    " + o.getCoord(i).getSup() + '\n');
				}
			}

			itr = shapes.keySet().iterator();
			while(itr.hasNext())
			{
				int sid = ((Integer)itr.next()).intValue();
				Vector<ShiftedBox> sb = shapes.get(Integer.valueOf(sid));
				out.write("shape id: " + sid + '\n');
				for(int i = 0; i < sb.size(); i++)
				{
					String offset = "";
					String size = "";
					for (int j = 0; j < cst.getDIM(); j++)
					{
						offset = offset + sb.elementAt(i).getOffset(j) + "  ";
						size = size + sb.elementAt(i).getSize(j) + "  ";
					}
					out.write("    sb" + i + ": " + '\n');
					out.write("       Offset: " +  offset + '\n');
					out.write("       Size: " +  size + '\n');
				}
			}
			out.close();
	    } catch (IOException e) {
	    }


		return true;
	}

	/**
	 * Prints to a file  the objects and the shapes of the problem. The written file can be read by the InputParser class.
	 * The file to be written to is specified in the global variable OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT,
	 * present in the global.Constants class.
	 */
	public boolean printToFileInputFormat(String path)
	{
	    try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(path));

			Iterator itr;
			itr = objects.keySet().iterator();
			out.write("Objects" + '\n');
			while(itr.hasNext())
			{
				int id = ((Integer)itr.next()).intValue();
				Obj o = objects.get(Integer.valueOf(id));
				out.write(id + " ");
				out.write(o.getShapeId().getInf() + " " + o.getShapeId().getSup() + " ");
				for (int i = 0; i < cst.getDIM(); i++) {
					out.write(o.getCoord(i).getInf() + " " + o.getCoord(i).getSup() + " ");
				}
				//now write the time things
				out.write("1 1 1 1 1 1" + '\n');
			}

			itr = shapes.keySet().iterator();
			out.write("Shapes" + '\n');
			while(itr.hasNext())
			{
				int sid = ((Integer)itr.next()).intValue();
				out.write(sid + "" + '\n');
			}



			itr = shapes.keySet().iterator();
			out.write("ShiftedBoxes" + '\n');
			while(itr.hasNext())
			{
				int sid = ((Integer)itr.next()).intValue();
				Vector<ShiftedBox> sb = shapes.get(Integer.valueOf(sid));

				for(int i = 0; i < sb.size(); i++)
				{
					String offset = "";
					String size = "";
					for (int j = 0; j < cst.getDIM(); j++)
					{
						offset = offset + sb.elementAt(i).getOffset(j) + " ";
						size = size + sb.elementAt(i).getSize(j) + " ";
					}
					out.write(sid + " ");
					out.write(offset +  size + '\n');
				}
			}
			out.close();
	    } catch (IOException e) {
	    }


		return true;
	}

	/**
	 * Clears the Setup object. So basically it removes all the shapes, objects and constraints from the problem.
	 */
	public void clear()
	{
		shapes.clear();
		objects.clear();
		constraints.clear();
		ictrMinHeap.clear();
		ictrMaxHeap.clear();
	}


//	public static void setIctrHeap(HeapAscending ictrHeap) {
//		Setup.ictrHeap = ictrHeap;
//	}

}
