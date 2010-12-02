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
package choco.common;


import static choco.kernel.common.opres.graph.GraphDTC.ADDED;
import static choco.kernel.common.opres.graph.GraphDTC.CYCLE;
import static choco.kernel.common.opres.graph.GraphDTC.TRANSITIVE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import choco.kernel.common.VisuFactory;
import choco.kernel.common.opres.graph.DagDTC;





@SuppressWarnings({"PMD.LocalVariableCouldBeFinal","PMD.MethodArgumentCouldBeFinal"})
/**
 * @author Arnaud Malapert
 *
 */
public class TestGraphDTC {


	private final static int NUMBER=6;


	private final static int[][] edges={ {0,1,2,1,3,3,4,3,5,5,1,0,1},
										 {2,2,4,4,5,2,3,1,3,2,0,5,5} };

	private final static int[] states={ADDED,ADDED,ADDED,TRANSITIVE,ADDED,ADDED,
										CYCLE,ADDED,CYCLE,ADDED,ADDED,ADDED,TRANSITIVE};

	private static boolean[][] matrix;

	private static int step;

	private static DagDTC graph;
	//private static GraphDTC graph;

	@BeforeClass
	public static void initialize() {
		graph=new DagDTC(NUMBER);
		//graph=new GraphDTC(NUMBER);
		step=0;
		matrix=new boolean[NUMBER][NUMBER];
		for (int i = 0; i < NUMBER; i++) {
			matrix[i][i]=true;
		}
	}


	private static final void update(final boolean state) {
		switch (step) {
		case 0: matrix[0][2]=state;break;
		case 1:	matrix[1][2]=state;break;
		case 2: {
		matrix[2][4]=state;
		matrix[1][4]=state;
		matrix[0][4]=state;
		break;
		}
		case 3 : matrix[3][5]=state;break;
		case 4: {
		matrix[3][2]=state;
		matrix[3][4]=state;
		break;}
		case 5:matrix[3][1]=state;break;
		case 6: {
		matrix[5][2]=state;
		matrix[5][4]=state;
		break;}
		case 7 :{
		matrix[3][0]=state;
		matrix[1][0]=state;
		break;}
		case 8 : {
		matrix[0][5]=state;
		matrix[1][5]=state;
		break;
		}
		default:
			break;
		}
		boolean[][] tmp = graph.toTreeNodeMatrix();
		for (int i = 0; i < matrix.length; i++) {
			assertTrue("matrix equality", Arrays.equals(matrix[i], tmp[i]));
		}
	}


	@Test
	public void testGraph() {
		//add
		for (int i = 0; i < states.length; i++) {
			final int result=graph.add(edges[0][i], edges[1][i]);
			assertEquals("add arc", states[i],result);
			if(result==ADDED) {
			update(true);
			step++;
			}
		}
		VisuFactory.toDotty(graph);
		//remove
		assertFalse("rm", graph.remove(0,3));
		for (int i = states.length-1; i >=0; i--) {
			final boolean result=graph.remove(edges[0][i], edges[1][i]);
			assertEquals("remove arc",  states[i] == ADDED,result);
			if(result) {
			step--;
			update(false);
			}
		}
	}
}
