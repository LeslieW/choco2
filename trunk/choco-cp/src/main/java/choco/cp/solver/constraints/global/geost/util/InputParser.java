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
import choco.cp.solver.constraints.global.geost.geometricPrim.Shape;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class parses a text file that describes the problem to be solved. While parsing it creates the choco problem, the objects, shapes and shifted boxes and stores them locally to this class. 
 * Then to create the environment that the constraint uses all we need to do is call global.Setup.createEnvironment(parser) and give it this object as an argument.
 */
public class InputParser {

    protected static final Logger LOGGER = ChocoLogging.getSolverLogger();


    public static class GeostProblem{
        public int[][] objects;
        public int[] shapes;
        public int[][] shiftedBoxes;

        public GeostProblem(int[][] objects, int[] shapes, int[][] shiftedBoxes) {
            this.objects = objects;
            this.shapes = shapes;
            this.shiftedBoxes = shiftedBoxes;
        }
    }


    Vector<GeostObject> obj;
	Vector<Shape> sh;
	Vector<ShiftedBox> sb;
    String path;
	int dim;
    GeostProblem gp;

    public InputParser() {
    }

    public InputParser(String path, int dim)
	{
		this.path = path;
		this.dim = dim;
		obj = new Vector<GeostObject>();
		sh  = new Vector<Shape>();
		sb  = new Vector<ShiftedBox>();
	}

    public InputParser(GeostProblem gp, int dim){
		this.dim = dim;
        this.gp = gp;
        obj = new Vector<GeostObject>();
		sh  = new Vector<Shape>();
		sb  = new Vector<ShiftedBox>();
    }


    public Vector<GeostObject> getObjects()
	{
		return this.obj;
	}
	
	public Vector<Shape> getShapes()
	{
		return this.sh;
	}

	public Vector<ShiftedBox> getShiftedBoxes()
	{
		return this.sb;
	}

    public boolean parse() throws Exception{
        if(path!=null){
            return this.parseFile();
        }else if(gp!=null){
            return this.parseGP();
        }
        return false;
    }

    /**
	 * This is the essential function of this class it. It is the function that executes the parsing. The file to be parsed is read 
	 * from the local variable path. The value of path is given to the constructor as parameter.
	 * @return The function returns false if there was an error during the parsing otherwise it returns true.
	 */
	public boolean parseFile() throws Exception
	{

		BufferedReader bin = null;
		try{
			//URL inputFileLocation = new URL(constants.INPUT_FILE_PATH);
			bin = new BufferedReader(new FileReader(this.path));
			//bin = new BufferedReader(new InputStreamReader(inputFileLocation.openStream()));
		}
		catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, "Unable to open data file: {0}\n{1}", new Object[]{this.path, e});
			e.printStackTrace();	
			return false;
		}

		String str = "";
		String temp = "";
		String mode = "";
		while((str = bin.readLine()) != null) 
		{
			if (str.equals("")) continue;
			if (str.startsWith("#")) continue;
			
			if (str.equals("Objects") || str.equals("Shapes") || str.equals("ShiftedBoxes"))
			{
				mode = str;
				str = bin.readLine();
			}
 
			StringTokenizer st = new StringTokenizer(str, " ");
			int tokenNb = 0;
			
			int indice = 0;
			if (mode.equals("Objects"))
			{
				int id = 0;
                IntegerVariable shape = null;
                IntegerVariable[] coord = new IntegerVariable[this.dim];
                IntegerVariable start = null;
                IntegerVariable duration= null;
                IntegerVariable end= null;
                while (st.hasMoreTokens())
				{
			        temp = st.nextToken();
			        tokenNb++; 
			        switch (tokenNb)
			        {
			        	case 1:
			        		id =Integer.valueOf(temp);
			        		break;
			        	case 2:
			        		String temp2 = st.nextToken();
			        		tokenNb++;
			        		shape = Choco.makeIntVar("sid_"+indice, Integer.valueOf(temp).intValue(), Integer.valueOf(temp2).intValue());
			        		break;
			        }
			        
			        if (tokenNb > 3 && tokenNb <= (3 + ( 2 * this.dim)))
			        {
			        	for(int i = 0; i < this.dim; i++)
			        	{
			        		int lowerBound = -1, upperBound = -1;
			        		for (int j = 0; j < 2; j++)
			        		{
			        			if(j==0)
			        			{
			        				lowerBound = Integer.valueOf(temp).intValue();
                                }
			        			if(j==1)
			        			{
			        				upperBound = Integer.valueOf(temp).intValue();
                                }
			        			if(st.hasMoreTokens())
			        			{
			        				temp = st.nextToken();
				        			tokenNb++;
			        			}
			        		}
			        		coord[i] = Choco.makeIntVar("x"+"_"+indice+"_" + i, lowerBound, upperBound, "cp:bound");
			        	}
			        }
			        else if(tokenNb > (3 + ( 2 * this.dim)))
			        {
			        	for(int i = 0; i < 3; i++)
			        	{
			        		int lowerBound = -1, upperBound = -1;
			        		for (int j = 0; j < 2; j++)
			        		{
			        			if(j==0)
			        			{
			        				lowerBound = Integer.valueOf(temp).intValue();
			        			}
			        			if(j==1)
			        			{
			        				upperBound = Integer.valueOf(temp).intValue();
			        			}
			        			if(st.hasMoreTokens())
			        			{
			        				temp = st.nextToken();
				        			tokenNb++;
			        			}
			        		}
			        		if(i == 0)
			        		{
			        			start = Choco.makeIntVar("start_"+indice, lowerBound, upperBound);
			        		}
			        		else if(i == 1)
			        		{
			        			duration = Choco.makeIntVar("duration_"+indice, lowerBound, upperBound);
			        		}	
			        		else if(i == 2)
			        		{
			        			end = Choco.makeIntVar("end_"+(indice++), lowerBound, upperBound);
			        		}
			        	}
			        }
			        
			    }
                GeostObject o = new GeostObject(this.dim, id, shape, coord, start, duration, end);
	        	obj.add(o);	
			}
			
			if (mode.equals("Shapes"))
			{
				Shape s = new Shape();
				temp = st.nextToken();
			    s.setShapeId(Integer.valueOf(temp).intValue());
			    sh.add(s);
			}
			
			if (mode.equals("ShiftedBoxes"))
			{
				ShiftedBox s = new ShiftedBox();
				while (st.hasMoreTokens()) 
				{
			        temp = st.nextToken();
			        tokenNb++; 
			        if (tokenNb == 1)
			        {
			        	s.setShapeId(Integer.valueOf(temp).intValue());
			        }
			        
			        if (tokenNb > 1 && tokenNb <= this.dim + 1)
			        {
			        	int[] off = new int[this.dim];
			        	for(int i = 0; i < this.dim; i++)
			        	{
			        		off[i] = Integer.valueOf(temp).intValue();
			        		if (i != this.dim - 1)
			        		{
			        			temp = st.nextToken();
				        		tokenNb++;
			        		}
			        		
			        	}
			        	s.setOffset(off);
		        	}
			        
			        if(tokenNb > 1 && tokenNb > this.dim + 1)
			        {
			        	int[] size = new int[this.dim];
			        	for(int i = 0; i < this.dim; i++)
			        	{
			        		size[i] = Integer.valueOf(temp).intValue();
			        		if (i != this.dim - 1)
			        		{
			        			temp = st.nextToken();
				        		tokenNb++;
			        		}
			        		
			        	}
			        	s.setSize(size);
			        }
			    }
			    sb.add(s);
			    
			    }
			}
		
		//add the shiftedboxes to their corresponding shapes linked lists
		for(int i = 0; i < sb.size(); i++)
		{
			int index = -1;
			for (int j = 0; j < sh.size(); j++)
			{
				if(sh.elementAt(j).getShapeId() == sb.elementAt(i).getShapeId()) 
					{
						index = j;
						break;
					}
		
			}
			if (index != -1)
				sh.elementAt(index).addShiftedBox(sb.elementAt(i));
		}
		
//		//Calculate the origin domain sizes
//		for (int i = 0; i < obj.size(); i++)
//			obj.elementAt(i).setOriginDomainSize(obj.elementAt(i).calculateOriginDomainSize());
		return true;
	}

    /**
	 * This is the essential function of this class it. It is the function that executes the parsing. Lists to be parsed are read.
	 * @return The function returns false if there was an error during the parsing otherwise it returns true.
	 */
	public boolean parseGP() throws Exception
	{

		// Objects:
        for(int i =0; i < gp.objects.length; i++){
            int j =0;
            int id = gp.objects[i][j++];
            IntegerVariable shape = Choco.makeIntVar("sid_"+i, gp.objects[i][j++], gp.objects[i][j++]);
            IntegerVariable[] coord = new IntegerVariable[this.dim];
            for(int k = 0; k < this.dim; k++){
                coord[k] = Choco.makeIntVar("x_"+i+"_" + k, gp.objects[i][j++], gp.objects[i][j++], "cp:bound");
            }

            IntegerVariable start = Choco.makeIntVar("start_"+i, gp.objects[i][j++], gp.objects[i][j++]);
            IntegerVariable duration= Choco.makeIntVar("duration_"+i, gp.objects[i][j++], gp.objects[i][j++]);
            IntegerVariable end= Choco.makeIntVar("end_"+i, gp.objects[i][j++], gp.objects[i][j]);
            GeostObject o = new GeostObject(this.dim, id, shape, coord, start, duration, end);
	        obj.add(o);
        }
        // Shapes
        for(int i = 0; i < gp.shapes.length; i++){
            Shape s = new Shape();
            s.setShapeId(gp.shapes[i]);
            sh.add(s);
        }
        // Shifted boxes
        for(int i = 0; i < gp.shiftedBoxes.length; i++){
            int j = 0;
            ShiftedBox s = new ShiftedBox();
            s.setShapeId(gp.shiftedBoxes[i][j++]);
            int[] off = new int[this.dim];
            for(int k = 0; k < this.dim; k++){
                off[k] = gp.shiftedBoxes[i][j++];
            }
            s.setOffset(off);
            int[] size = new int[this.dim];
            for(int k = 0; k < this.dim; k++)
            {
                size[k] = gp.shiftedBoxes[i][j++];
            }
            s.setSize(size);
            sb.add(s);
        }
        //add the shiftedboxes to their corresponding shapes linked lists
		for(int i = 0; i < sb.size(); i++)
		{
			int index = -1;
			for (int j = 0; j < sh.size(); j++)
			{
				if(sh.elementAt(j).getShapeId() == sb.elementAt(i).getShapeId())
					{
						index = j;
						break;
					}

			}
			if (index != -1)
				sh.elementAt(index).addShiftedBox(sb.elementAt(i));
		}
		return true;
	}

}
