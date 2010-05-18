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
package samples.tutorials.scheduling.pack;


/**
 * @author Arnaud Malapert
 *
 */
public class BinPackingExample {

	public final static int OPT_C=23;

	public final static int OPT_S=43;

	public final static int OPT_H=46;

	public final static int OPT_N=25;

	public final static int OPT_0=50;

	public final static int CAPACITY=120;

	public final static int CAPACITY_N=100;




	/**
	 * optimal==heuristic
	 */
	public final static int[] N1C2W1_C={100, 97, 96, 92, 89, 86, 83, 83, 82, 79, 77, 76, 73, 73, 70, 69, 69, 61, 60, 60, 60, 58, 56, 56, 53,
		51, 49, 48, 48, 48, 47, 46, 42, 41, 36, 35, 34, 32, 32, 32, 31, 22, 17, 12, 12, 6, 6, 5, 3, 2};


	/**
	 * optimal !=heuristic
	 */
	public final static int[] N1C1W1_N={99, 98, 95, 95, 95, 94, 94, 91, 88, 87, 86, 85, 76, 74, 73, 71, 68, 60, 55, 54, 51, 45, 42, 40, 39, 39,
		36, 34, 33, 32, 32, 31, 31, 30, 29, 26, 26, 23, 21, 21, 21, 19, 18, 18, 16, 15, 5, 5, 4, 1};

	/**
	 * optimal!=heuristic
	 */

	public final static int[] N2C2W1_H={100, 99, 99, 98, 98, 97, 96, 94, 94, 93, 93, 92, 92, 90, 88, 88, 87, 87, 86, 86, 86, 85, 85, 78, 78,
		77, 77, 77, 74, 71, 71, 68, 68, 67, 66, 65, 65, 62, 62, 60, 59, 59, 55, 55, 54, 53, 52, 52, 51, 51, 50, 49, 49, 48, 47, 46, 46, 46,
		45, 45, 45, 42, 42, 41, 41, 40, 38, 36, 36, 34, 33, 32, 32, 32, 31, 29, 27, 23, 22, 22, 21, 21, 20, 18, 16, 15, 11, 10, 10, 9, 9, 8,
		6, 6, 5, 5, 4, 3, 1, 1};

	/**
	 * optimal!=heuristic
	 */
	public final static int[] N2C2W2_0={100, 100, 98, 98, 97, 97, 97, 95, 93, 93, 89, 89, 88, 87, 86, 84, 83, 82, 81, 80, 79, 79, 79, 77, 75,
		73, 73, 72, 72, 71, 71, 71, 69, 68, 68, 67, 67, 66, 65, 65, 64, 63, 60, 59, 59, 58, 58, 57, 57, 56, 56, 55, 55, 55, 55, 54, 54, 54,
		53, 51, 51, 50, 50, 50, 48, 47, 47, 47, 47, 46, 46, 45, 44, 43, 41, 41, 40, 40, 39, 37, 36, 32, 32, 31, 29, 28, 27, 27, 27, 27, 26,
		25, 25, 25, 25, 24, 24, 22, 21, 20};

	/**
	 * optimal==heuristic : error or bug !
	 */

	public final static int[] N2C2W1_S={99, 98, 97, 96, 95, 94, 93, 93, 91, 90, 89, 88, 87, 87, 86, 86, 85, 84, 83, 82, 79, 79, 78, 77, 77,
		77, 77, 73, 73, 72, 71, 71, 70, 68, 67, 63, 63, 62, 61, 61, 61, 61, 60, 59, 57, 56, 52, 51, 49, 48, 47, 47, 47, 46, 45, 44, 44, 44,
		44, 43, 43, 42, 42, 39, 39, 39, 34, 33, 33, 32, 31, 31, 28, 28, 27, 25, 25, 24, 24, 24, 24, 22, 21, 20, 18, 17, 17, 16, 14, 14, 13,
		10, 10, 9, 9, 7, 7, 7, 7, 6};



	protected static CPpack run(final int[] s,final int c,final int opt,final int tl) {
		CPpack example=new CPpack(s,c,opt);
		example.setTimelimit(tl);
		example.cpPack();
		return example;
	}

	public static void main(String[] args) {
		final int tl=args.length>0 ? Integer.parseInt(args[0]) : -1 ;
		//run(N1C2W1_C,CAPACITY,OPT_C,tl);
		run(N1C1W1_N,CAPACITY_N,OPT_N,tl);
		//run(N2C2W1_H,CAPACITY,OPT_H,tl);
		//run(N2C2W2_0,CAPACITY,OPT_0,tl);
		//run(N2C2W1_S,CAPACITY,OPT_S,tl);
	}


}


