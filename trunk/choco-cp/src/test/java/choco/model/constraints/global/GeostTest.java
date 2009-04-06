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
package choco.model.constraints.global;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.externalConstraints.NonOverlapping;
import choco.cp.solver.constraints.global.geost.util.InputParser;
import choco.cp.solver.constraints.global.geost.util.RandomProblemGenerator;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.IExternalConstraint;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Vector;


public class GeostTest {

    int dim;
    int mode;


    @Before
    public void before() {
        dim = 3;
        mode = 0;
    }


    @Test
    public void testCustomProblem() {

        int lengths[] = {5, 3, 2};
        int widths[] = {2, 2, 1};
        int heights[] = {1, 1, 1};

        int nbOfObj = 3;

        for (int seed = 0; seed < 20; seed++) {
            //create the choco problem
            Model m = new CPModel();

            //Create Objects
            Vector<GeostObject> obj2 = new Vector<GeostObject>();

            for (int i = 0; i < nbOfObj; i++) {
                IntegerVariable shapeId = Choco.makeIntVar("sid", i, i);
                IntegerVariable coords[] = new IntegerVariable[this.dim];
                for (int j = 0; j < coords.length; j++) {
                    coords[j] = Choco.makeIntVar("x" + j, 0, 2);
                }
                IntegerVariable start = Choco.makeIntVar("start", 1, 1);
                IntegerVariable duration = Choco.makeIntVar("duration", 1, 1);
                IntegerVariable end = Choco.makeIntVar("end", 1, 1);
                obj2.add(new GeostObject(dim, i, shapeId, coords, start, duration, end));
            }

            //create shiftedboxes and add them to corresponding shapes
            Vector<ShiftedBox> sb2 = new Vector<ShiftedBox>();
            int h = 0;
            while (h < nbOfObj) {

                int[] l = {lengths[h], heights[h], widths[h]};
                int[] t = {0, 0, 0};


                sb2.add(new ShiftedBox(h, t, l));
                h++;
            }

            //Create the external constraints vecotr
            Vector<IExternalConstraint> ectr2 = new Vector<IExternalConstraint>();
            //create the list od dimensions for the external constraint
            int[] ectrDim2 = new int[this.dim];
            for (int d = 0; d < 3; d++)
                ectrDim2[d] = d;

            //create the list of object ids for the external constraint
            int[] objOfEctr2 = new int[nbOfObj];
            for (int d = 0; d < nbOfObj; d++) {
                objOfEctr2[d] = obj2.elementAt(d).getObjectId();
            }

            //create the external constraint of type non overlapping
            NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ectrDim2, objOfEctr2);
            //add the external constraint to the vector
            ectr2.add(n2);

            //create the geost constraint object
            Constraint geost = Choco.geost(this.dim, obj2, sb2, ectr2);
            m.addConstraint(geost);
            //post the geost constraint to the choco problem
            Solver s = new CPSolver();
            s.read(m);
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.solveAll();
            Assert.assertEquals("number of solutions", 9828, s.getNbSolutions());
        }

    }

    @Test
    @Ignore
    public void RandomProblemGeneration() {

        for (int seed = 0; seed < 20; seed++) {
            //nb of objects, shapes, shifted boxes and maxLength respectively
            //The nb of Obj should be equal to nb Of shapes for NOW. as For the number of the shifted Boxes it should be greater or equal to thhe nb of Objects

            RandomProblemGenerator rp = new RandomProblemGenerator(this.dim, 7, 7, 9, 25);
            rp.generateProb();

            Model m = rp.getModel();

            Vector<IExternalConstraint> ectr = new Vector<IExternalConstraint>();
            int[] ectrDim = new int[this.dim];
            for (int i = 0; i < this.dim; i++)
                ectrDim[i] = i;

            int[] objOfEctr = new int[rp.getObjects().size()];
            for (int i = 0; i < rp.getObjects().size(); i++) {
                objOfEctr[i] = rp.getObjects().elementAt(i).getObjectId();
            }

            NonOverlapping n = new NonOverlapping(Constants.NON_OVERLAPPING, ectrDim, objOfEctr);
            ectr.add(n);


            Constraint geost = Choco.geost(this.dim, rp.getObjects(), rp.getSBoxes(), ectr);
            m.addConstraint(geost);
            Solver s = new CPSolver();
            s.read(m);
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.solveAll();
            Assert.assertEquals("number of solutions", 0, s.getNbSolutions());
        }
    }

    @Test
    public void PolyMorphicTest() {
        int[][] objects = new int[][]{
                {0, 0, 1, 0, 3, 0, 4, 1, 1, 1, 1, 1, 1},
                {1, 0, 1, 0, 3, 0, 4, 1, 1, 1, 1, 1, 1},
                {2, 0, 1, 0, 3, 0, 4, 1, 1, 1, 1, 1, 1},
                {3, 0, 1, 0, 3, 0, 4, 1, 1, 1, 1, 1, 1},
                {4, 0, 1, 0, 3, 0, 4, 1, 1, 1, 1, 1, 1},
                {5, 2, 2, 0, 0, 6, 6, 1, 1, 1, 1, 1, 1}
        };
        int[] shapes = new int[]{2, 1, 0};
        int[][] shiftedBoxes = new int[][]{
                {0, 0, 0, 3, 2},
                {1, 0, 0, 2, 3},
                {2, 0, 0, 5, 1},
                {2, 5, -6, 1, 7},
        };

        this.dim = 2;

        InputParser parser;
        new InputParser();

        InputParser.GeostProblem gp = new InputParser.GeostProblem(objects, shapes, shiftedBoxes);
        for (int seed = 0; seed < 20; seed++) {
            parser = new InputParser(gp, dim);
            try {
                parser.parse();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Model m = new CPModel();

            // create a vector to hold in it all the external constraints we want to add to geost
            Vector<IExternalConstraint> ectr = new Vector<IExternalConstraint>();

            // ////////////Create the needed external constraints//////////////

            // first of all create a array of intergers containing all the dimensions where the constraint will be active
            int[] ectrDim = new int[dim];
            for (int i = 0; i < dim; i++)
                ectrDim[i] = i;

            // Create an array of object ids representing all the objects that the external constraint will be applied to
            int[] objOfEctr = new int[parser.getObjects().size()];
            for (int i = 0; i < parser.getObjects().size(); i++) {
                objOfEctr[i] = parser.getObjects().elementAt(i).getObjectId();
            }

            // Create the external constraint, in our case it is the NonOverlapping
            // constraint (it is the only one implemented for now)
            NonOverlapping n = new NonOverlapping(Constants.NON_OVERLAPPING, ectrDim, objOfEctr);

            // add the created external constraint to the vector we created
            ectr.add(n);

            // /////////////Create the array of variables to make choco happy//////////////
            // vars will be stored as follows: object 1 coords(so k coordinates), sid, start, duration, end,
            //                                 object 2 coords(so k coordinates), sid, start, duration, end and so on ........
            // To retrieve the index of a certain variable, the formula is (nb of the object in question = objId assuming objIds are consecutive and
            // start from 0) * (k + 4) + number of the variable wanted the number of the variable wanted is decided as follows: 0 ... k-1
            // (the coords), k (the sid), k+1 (start), k+2 (duration), k+3 (end)

            int originOfObjects = parser.getObjects().size() * dim;
            // Number of domain  variables  to  represent the origin of all  objects
            int otherVariables = parser.getObjects().size() * 4; // each object  has 4 other variables: shapeId, start,  duration; end
            IntegerVariable[] vars = new IntegerVariable[originOfObjects + otherVariables];

            for (int i = 0; i < parser.getObjects().size(); i++) {
                for (int j = 0; j < dim; j++) {
                    vars[(i * (dim + 4)) + j] = parser.getObjects().elementAt(i).getCoordinates()[j];
                }
                vars[(i * (dim + 4)) + dim] = parser.getObjects().elementAt(i).getShapeId();
                vars[(i * (dim + 4)) + dim + 1] = parser.getObjects().elementAt(i).getStartTime();
                vars[(i * (dim + 4)) + dim + 2] = parser.getObjects().elementAt(i).getDurationTime();
                vars[(i * (dim + 4)) + dim + 3] = parser.getObjects().elementAt(i).getEndTime();
            }


            Constraint geost = Choco.geost(dim, parser.getObjects(), parser.getShiftedBoxes(), ectr);

            // /////////////Add the constraint to the choco problem//////////////
            m.addConstraint(geost);

            for (int i = 0; i < parser.getObjects().size() - 2; i++) {
                m.addConstraint(Choco.lex(parser.getObjects().get(i).getCoordinates(), parser.getObjects().get(i + 1).getCoordinates()));
            }

            Solver s = new CPSolver();
            s.read(m);


            s.setValIntSelector(new RandomIntValSelector(seed));
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));

            s.solveAll();
            Assert.assertEquals(s.getNbSolutions(), 2);
        }

    }

    @Test
    public void testOfSharingShape() {

        int lengths[] = {5, 3, 6};
        int widths[] = {2, 4, 1};
        int heights[] = {1, 2, 4};

        int nbOfObj = 3;
        Model m = new CPModel();

        //Create Objects
        Vector<GeostObject> obj2 = new Vector<GeostObject>();

        for (int i = 0; i < nbOfObj; i++) {
            IntegerVariable shapeId = Choco.makeIntVar("sid", 0, 0);
            IntegerVariable coords[] = new IntegerVariable[this.dim];
            for (int j = 0; j < coords.length; j++) {
                coords[j] = Choco.makeIntVar("x" + j, 0, 2);
            }
            IntegerVariable start = Choco.makeIntVar("start", 1, 1);
            IntegerVariable duration = Choco.makeIntVar("duration", 1, 1);
            IntegerVariable end = Choco.makeIntVar("end", 1, 1);
            obj2.add(new GeostObject(dim, i, shapeId, coords, start, duration, end));
        }
        for (int i = 0; i < obj2.size(); i++) {
            for (int d = 0; d < this.dim; d++) {
                System.out.println("" + obj2.elementAt(i).getCoordinates()[d].getLowB() + "    " + obj2.elementAt(i).getCoordinates()[d].getUppB());
            }

        }

        //create shiftedboxes and add them to corresponding shapes
        Vector<ShiftedBox> sb2 = new Vector<ShiftedBox>();

        int[] l = {lengths[0], heights[0], widths[0]};
        int[] t = {0, 0, 0};


        sb2.add(new ShiftedBox(0, t, l));


        Vector<IExternalConstraint> ectr2 = new Vector<IExternalConstraint>();
        int[] ectrDim2 = new int[this.dim];
        for (int d = 0; d < 3; d++)
            ectrDim2[d] = d;


        int[] objOfEctr2 = new int[nbOfObj];
        for (int d = 0; d < nbOfObj; d++) {
            objOfEctr2[d] = obj2.elementAt(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ectrDim2, objOfEctr2);
        ectr2.add(n2);

        Constraint geost2 = Choco.geost(this.dim, obj2, sb2, ectr2);
        m.addConstraint(geost2);
        Solver s = new CPSolver();
        s.read(m);
        //Here the solve will only do a test for the first constraint and not the second.
        //However for our purposes this is not important. If it is just change the code
        //of solve to take 2 constraints as parameters and then run the two solution testers
        s.solveAll();
        Assert.assertEquals(s.getNbSolutions(), 7290);
    }


    @Test
    public void exp2DTest() {

        //The data
        int dim = 2;
        int[][] domOrigins = {
                {0, 5, 0, 3},
                {0, 5, 0, 5},
                {0, 6, 0, 4},
                {0, 6, 0, 5},
                {0, 5, 0, 5},
                {0, 7, 0, 4},
                {0, 6, 0, 5},
                {0, 6, 0, 5},
                {0, 5, 0, 6},
                {0, 7, 0, 5}
        };


        int[][] shBoxes = {
                {0, 0, 0, 2, 3},
                {0, 1, 2, 2, 2},
                {1, 0, 0, 3, 2},
                {2, 0, 0, 2, 3},
                {3, 0, 0, 2, 2},
                {4, 0, 0, 3, 1},
                {4, 1, 0, 1, 2},
                {5, 0, 0, 1, 3},
                {6, 0, 0, 1, 2},
                {6, 0, 1, 2, 1},
                {7, 0, 0, 2, 1},
                {7, 1, 0, 1, 2},
                {8, 0, 0, 3, 1},
                {9, 0, 0, 1, 2}
        };
        int[] v0 = {-1, -2, -3};
        int[] v1 = {-1, 2, 3};
        int[] v2 = {-1, 2, -3};
        int[] v3 = {-1, 3, -2};

        int nbOfObj = 10;

        //create the choco problem
        Model m = new CPModel();

        //Create Objects
        Vector<GeostObject> obj = new Vector<GeostObject>();

        for (int i = 0; i < nbOfObj; i++) {
            IntegerVariable shapeId = Choco.makeIntVar("sid", i, i);
            IntegerVariable coords[] = new IntegerVariable[dim];
            coords[0] = Choco.makeIntVar("x", domOrigins[i][0], domOrigins[i][1]);
            coords[1] = Choco.makeIntVar("y", domOrigins[i][2], domOrigins[i][3]);

            IntegerVariable start = Choco.makeIntVar("start", 1, 1);
            IntegerVariable duration = Choco.makeIntVar("duration", 1, 1);
            IntegerVariable end = Choco.makeIntVar("end", 1, 1);
            obj.add(new GeostObject(dim, i, shapeId, coords, start, duration, end));


        }

        //create shiftedboxes and add them to corresponding shapes
        Vector<ShiftedBox> sb = new Vector<ShiftedBox>();
        for (int i = 0; i < shBoxes.length; i++) {
            int[] offset = {shBoxes[i][1], shBoxes[i][2]};
            int[] sizes = {shBoxes[i][3], shBoxes[i][4]};
            sb.add(new ShiftedBox(shBoxes[i][0], offset, sizes));
        }

        //Create the external constraints vecotr
        Vector<IExternalConstraint> ectr = new Vector<IExternalConstraint>();
        //create the list of dimensions for the external constraint
        int[] ectrDim = new int[dim];
        for (int d = 0; d < dim; d++)
            ectrDim[d] = d;

        //create the list of object ids for the external constraint
        int[] objOfEctr = new int[nbOfObj];
        for (int d = 0; d < nbOfObj; d++) {
            objOfEctr[d] = obj.elementAt(d).getObjectId();
        }

//		create the external constraint of type non overlapping
        NonOverlapping n = new NonOverlapping(Constants.NON_OVERLAPPING, ectrDim, objOfEctr);
        //add the external constraint to the vector
        ectr.add(n);

        //create the list of controlling vectors
        Vector<int[]> ctrlVs = new Vector<int[]>();
        ctrlVs.add(v0);
        //ctrlVs.add(v1);
        //ctrlVs.add(v2);
        //ctrlVs.add(v3);

        //create the geost constraint
        Constraint geost = Choco.geost(dim, obj, sb, ectr, ctrlVs);

        //NOTA: you can choose to not take into account of the greedy mode by creating the geost constraint as follows:
        //Geost_Constraint geost = new Geost_Constraint(vars, dim, obj, sb, ectr);

        //post the geost constraint to the choco problem
        m.addConstraint(geost);

        Solver s = new CPSolver();
        s.read(m);

        // solve the probem
        s.solve();

        for (int i = 0; i < obj.size(); i++) {
            GeostObject o = obj.elementAt(i);
            System.out.print("Object " + o.getObjectId() + ": ");
            for (int j = 0; j < dim; j++)
                System.out.print(s.getVar(o.getCoordinates()[j]) + " ");
            System.out.println();
        }
    }

	public static int[][] domOrigins = { { 0, 1, 0, 1 }, { 0, 1, 0, 1 },
			{ 0, 1, 0, 3 }, };

	public static int[][] domShapes = { { 0, 1 }, { 2, 2 }, { 3, 3 } };

	public static int[][] shBoxes = { { 0, 0, 0, 1, 3 }, { 0, 0, 0, 2, 1 },
			{ 1, 0, 0, 2, 1 }, { 1, 1, 0, 1, 3 }, { 2, 0, 0, 2, 1 },
			{ 2, 1, 0, 1, 3 }, { 2, 0, 2, 2, 1 }, { 3, 0, 0, 2, 1 }, };

    @Test
	public void exp2D2Test() {

        dim = 2;
        int nbOfObj = 3;

		// create the choco problem
		Model m = new CPModel();

		// Create Objects
		Vector<GeostObject> objects = new Vector<GeostObject>();

		for (int i = 0; i < nbOfObj; i++) {
			IntegerVariable shapeId = Choco.makeIntVar("sid_" +i,  domShapes[i][0], domShapes[i][1]);
			IntegerVariable coords[] = new IntegerVariable[dim];
			coords[0] = Choco.makeIntVar("x_" +i, domOrigins[i][0], domOrigins[i][1]);
			coords[1] = Choco.makeIntVar("y_" +i, domOrigins[i][2], domOrigins[i][3]);

			// ++ Modification
			// Additional Constraint
			m.addConstraint(Choco.geq(coords[0], 1));
			// -- Modification

			IntegerVariable start = Choco.makeIntVar("start", 0, 0);
			IntegerVariable duration = Choco.makeIntVar("duration", 1, 1);
			IntegerVariable end = Choco.makeIntVar("end", 1, 1);
			objects.add(new GeostObject(dim, i, shapeId, coords, start, duration, end));
		}

		// create shiftedboxes and add them to corresponding shapes
		Vector<ShiftedBox> sb =
		    new Vector<ShiftedBox>();

		for (int i = 0; i < shBoxes.length; i++) {
			int[] offset = { shBoxes[i][1], shBoxes[i][2] };
			int[] sizes = { shBoxes[i][3], shBoxes[i][4] };
			sb.add(new ShiftedBox(shBoxes[i][0], offset, sizes));
		}

		// Create the external constraints vecotr
		Vector<IExternalConstraint> ectr = new Vector<IExternalConstraint>();

		// create the list of dimensions for the external constraint
		int[] ectrDim = new int[dim];
		for (int d = 0; d < dim; d++)
			ectrDim[d] = d;

		// create the list of object ids for the external constraint
		int[] objOfEctr = new int[nbOfObj];
		for (int d = 0; d < nbOfObj; d++) {
			objOfEctr[d] = objects.elementAt(d).getObjectId();
		}

		// create the external constraint of non overlapping type
		NonOverlapping n = new NonOverlapping(Constants.NON_OVERLAPPING,
				ectrDim, objOfEctr);
		// add the external constraint to the ectr vector
		ectr.add(n);

		// create the geost constraint
		Constraint geost = Choco.geost(dim, objects, sb, ectr);

		// post the geost constraint to the choco problem
		m.addConstraint(geost);

		// build a solver
		Solver s = new CPSolver();
		// read the problem
		s.read(m);

		// solve the probem
		s.solve();

        Assert.assertSame("No solution expected", Boolean.FALSE, s.isFeasible());

		// print the solution
		System.out.println(s.pretty());
	}
}
