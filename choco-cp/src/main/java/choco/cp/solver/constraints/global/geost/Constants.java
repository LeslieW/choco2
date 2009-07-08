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

/**
 * This class contains 2 important types of data. The first is a set of variables and access methods that belong to a certain instance of this class
 * like the DIM variable representing the dimension. So to know the dimension of our problem we can just call the constants instance and ask to getDIM().
 * The other type of information is static information that are INTERNAL CONSTRAINTS CONTANTS REPRESENTING THE ICTR ID (Internal Constraint ID) 
 * and EXTERNAL CONSTRAINTS CONSTANTS REPRESENTING THE ECTR ID (External Constraint ID).
 * 
 *
 */
public class Constants {
	
	public Constants(){}

	//Just for tests
	public int nbOfUpdates = 0;
	public int problemNb = 0;
	
	
	//GLOBAL SETTING CONSTANTS
	/**
	 * DIM id a constants indicating the dimension of the space we are working in globally
	 */
	public int DIM = 2;
	//public static final String INPUT_FILE_PATH = "/Users/ridasadek/Documents/workspace/geost/input.txt";
	//public static final String INPUT_FILE_PATH = "/Users/ridasadek/Documents/workspace/geost/PerfectSquareProb1.txt";
	public String INPUT_FILE_PATH = "/Users/ridasadek/Documents/workspace/geost/randomInputGenProb.txt";
	//public static final String OUTPUT_FILE_PATH = "/Users/ridasadek/Documents/workspace/geost/output.txt";
	public String VRML_OUTPUT_FOLDER = "/Users/ridasadek/Documents/workspace/geost/VRMLfiles/";
	public String OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT = "/Users/ridasadek/Documents/workspace/geost/randomInputGenProb.txt";
	public String OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS = "/Users/ridasadek/Documents/workspace/geost/randomHumanGenProb.txt";
	
	//0: all solutions
	//1: first solution
	/**
	 * RUN_MODE is a constant that indicates Whether we want to search for all solutions or a first solution.
	 * value 0 is for all solutions, value 1 is for one solution.
	 */
	public  int RUN_MODE = 0; 
	
	//INTERNAL CONSTRAINTS CONTANTS REPRESENTING THE ICTR ID
	/**
	 * INBOX is a constant that specifies the id of the inbox constraint. So when declaring an internal constraint that is an inbox constraint
	 * we provide the value Constants.INBOX as ictrID. 
	 */
	public static final int INBOX = 1;
	/**
	 * OUTBOX is a constant that specifies the id of the outbox constraint. So when declaring an internal constraint that is an outbox constraint
	 * we provide the value Constants.OUTBOX as ictrID. 
	 */
	public static final int OUTBOX = 2;
	/**
	 * AVOID_HOLES is a constant that specifies the id of the avoid_holes constraint. So when declaring an internal constraint that is an avoid_holes constraint
	 * we provide the value Constants.AVOID_HOLES as ictrID. 
	 */
	public static final int AVOID_HOLES = 3;
	
	
	//EXTERNAL CONSTRAINTS CONSTANTS REPRESENTING THE ECTR ID
	/**
	 * COMPATIBLE is a constant that specifies the id of the compatible constraint. So when declaring an external constraint that is a compatible constraint
	 * we provide the value Constants.COMPATIBLE as ectrID. 
	 */
	public static final int COMPATIBLE = 1;
	/**
	 * INCLUDED is a constant that specifies the id of the included constraint. So when declaring an external constraint that is an included constraint
	 * we provide the value Constants.INCLUDED as ectrID. 
	 */
	public static final int INCLUDED = 2;
	/**
	 * NON_OVERLAPPING is a constant that specifies the id of the non_overlapping constraint. So when declaring an external constraint that is a non_overlapping constraint
	 * we provide the value Constants.NON_OVERLAPPING as ectrID. 
	 */
	public static final int NON_OVERLAPPING = 3;
	/**
	 * VISIBLE is a constant that specifies the id of the visible constraint. So when declaring an external constraint that is a visible constraint
	 * we provide the value Constants.VISIBLE as ectrID. 
	 */
	public static final int VISIBLE = 4;
	
	
	public void setDIM(int d)
	{
		DIM = d;
	}
	public int getDIM()
	{
		return DIM;
	}
	public String getINPUT_FILE_PATH() {
		return INPUT_FILE_PATH;
	}
	public int getRUN_MODE() {
		return RUN_MODE;
	}
	public void setINPUT_FILE_PATH(String input_file_path) {
		INPUT_FILE_PATH = input_file_path;
	}
	public void setRUN_MODE(int run_mode) {
		RUN_MODE = run_mode;
	}
	public String getVRML_OUTPUT_FOLDER() {
		return VRML_OUTPUT_FOLDER;
	}
	public void setVRML_OUTPUT_FOLDER(String vrml_output_folder) {
		VRML_OUTPUT_FOLDER = vrml_output_folder;
	}
	
	public  String getOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS() {
		return OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS;
	}
	public String getOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT() {
		return OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT;
	}
	public void setOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS(
			String output_of_random_gen_prob_to_be_read_by_humans) {
		OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS = output_of_random_gen_prob_to_be_read_by_humans;
	}
	public void setOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT(
			String output_of_random_gen_prob_to_be_used_as_input) {
		OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT = output_of_random_gen_prob_to_be_used_as_input;
	}
	
}