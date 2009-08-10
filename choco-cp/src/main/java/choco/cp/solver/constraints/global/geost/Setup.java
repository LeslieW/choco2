package choco.cp.solver.constraints.global.geost;

import choco.cp.solver.constraints.global.geost.dataStructures.HeapAscending;
import choco.cp.solver.constraints.global.geost.dataStructures.HeapDescending;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.geometricPrim.Point;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.solver.Solver;

import java.io.*;
import java.util.*;

/**
 * This is a very important class. It contains all the variables and objects the constraint needs.
 * Also it contains functions that the user and the constraint use to access the shapes, objects as well as the external constraints in the Geost.
 */

public class Setup {
	Constants cst;



    public static long timeFixObj=0L;
    public static long timeFixAllObj=0L;
    public static long timePruneFix=0L;
    public static long timeGetFR=0L;
    public static long timeIsFeasible=0L;

    public static long GetFRCalled=0L;
    public static long PruneFixCalled=0L;
    public static long timefilterWithGreedyMode=0L;
    public static long timefilterWithoutGreedyMode=0L;
    public static long handleSolution1 = 0L;
    public static long handleSolution2 = 0L;
    public static long handleSolution3 = 0L;
    public static boolean[][] memo_objects;
    public static boolean clipping=false;
    public static int interval_size=-1;//size of the interval in IntervalBranching
//    public static long sum_jumps=0; 
    public static long GetFRCalls=0;
    public static long deltaOne=0;
    public static long deltaZero=0;
    public static HashMap<Integer,HashMap<Integer,Integer>> delta= new HashMap<Integer, HashMap<Integer,Integer>>();
    public static HashMap<Integer,HashMap<Integer,Vector<Integer>>> succDelta= new HashMap<Integer, HashMap<Integer,Vector<Integer>>>();

    public static ObjectOutput serial=null;
    public static FileOutputStream fileSerial=null;
    public static double[][] prop={{0.66,0.66,0.66}};
    //public static double[][] prop={{0.25,0.75,1.0},{0.75,0.25,1.0},{0.33,0.66,1.0},{0.66,0.33,1.0},{0.33,0.33},{0.66,0.66},{0.25,0.25},{0.75,0.75},{0.2,0.8},{0.8,0.2},{1.0,0.5},{0.5,1.0},{1.0,0.2},{0.2,1.0},{1.0,0.8},{0.8,1.0}};
    //public static double[][] prop={{1.0,0.8}};

    //public static double[] prop={0.1,0.2,0.33,0.4,0.5,0.66,0.75,0.8,0.9};
    //public static double[] prop={0.1,0.2,0.33,0.4,0.5,0.66,0.75,0.8,0.9};
    //public static double[] prop={0.001,0.002,0.003,0.004,0.005,0.006,0.007,0.008,0.009};
    //public static double[] prop={0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09};

    public static boolean boxModeOnly=true;
    public static boolean propModeOnly=false;
    public static boolean deltaModeOnly=false;
    public static boolean circleRandom=false;
    public static boolean vizuRandom=false;
    public static boolean firstTimeGetDeltaFR=false;
    public static boolean debug=false;
    public static boolean findboxinterout =false;
    public static boolean findboxinteroutonly =false;
    public static boolean findboxtriangle=false;
    public static boolean singleboxonly=false;
    public static boolean mixmode=false;
    public static boolean intersection=false;
    public static boolean deltasucc=false;
    public static boolean usevectorbox=false;
    public static boolean useinterbox=false;
    public static boolean processing=false;
    public static boolean unaryCirclePackingHeuristic=false;
    public static boolean viewsol=false;


    public static int phase=1;
    public static int nbr_jumps=0;
    public static int max_nbr_jumps=0; //maximum nbr of jumps for all propagations steps
    public static int sum_jumps=0; //maximum nbr of jumps for all propagations steps
    public static long sum_square_jumps=0; //maximum nbr of jumps for all propagations steps
    public static int nbr_propagations=0;

    public static Point worst_point=null; //sweep point where the maximum nbr of jumps has been observed 
    public static boolean worst_increase=true; //associated with 'worst_point'; indicates wether increase was true or false when worst point was detected

    public static int state_FR=0;
    public static int nbr_steps=0;

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
	transient public HeapAscending  ictrMinHeap = new HeapAscending();
	/**
	 * A heap data structure containting elements in descending order (lexicographically).
	 * This is not used anymore.
	 * It was used inside that pruneMax function and we used to store in it the internal constraints.
	 * This way we coulld extract the active internal constraints at a specific position
	 */
	transient public HeapDescending ictrMaxHeap = new HeapDescending();


	public void insertShape(int sid, Vector<ShiftedBox> shiftedBoxes)
	{
		shapes.put(new Integer(sid), shiftedBoxes);
	}

	public void insertObject(int oid, Obj o)
	{
		objects.put(new Integer(oid), o);
	}


	public Vector<ShiftedBox> getShape(int sid)
	{
		return shapes.get(new Integer(sid));
	}

	public Obj getObject(int oid)
	{
		return objects.get(new Integer(oid));
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
		if (shapes.containsKey(new Integer(sb.getShapeId()))) {
			shapes.get(new Integer(sb.getShapeId())).add(sb);
		} else
		{
			Vector<ShiftedBox> v = new Vector<ShiftedBox>();;
			v.add(sb);
			shapes.put(new Integer(sb.getShapeId()), v);
		}
	}

	public void addObject(Obj o)
	{
		if (objects.containsKey(new Integer(o.getObjectId()))) {
			System.out.println("Trying to add an already existing object. In addObject in Setup");
		} else {
			objects.put(new Integer(o.getObjectId()), o);
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
			Obj o = objects.get(new Integer(id));
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
			Vector<ShiftedBox> sb = shapes.get(new Integer(sid));
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
				Obj o = objects.get(new Integer(id));
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
				Vector<ShiftedBox> sb = shapes.get(new Integer(sid));
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
				Obj o = objects.get(new Integer(id));
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
				Vector<ShiftedBox> sb = shapes.get(new Integer(sid));

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

    public Solver getSolver() {
        if (debug) if (getObject(0)==null) { System.out.println("Setup:getSolver():no object defined, unable to return solver."); System.exit(-1); }
        if (debug) if (getObject(0).getCoord(0)==null) { System.out.println("Setup:getSolver():no coord associated with object 0 defined, unable to return solver."); System.exit(-1); }
        return getObject(0).getCoord(0).getSolver();
    }


//	public static void setIctrHeap(HeapAscending ictrHeap) {
//		Setup.ictrHeap = ictrHeap;
//	}

}
