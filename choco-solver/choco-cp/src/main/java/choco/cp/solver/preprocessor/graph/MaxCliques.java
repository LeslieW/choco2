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
// Calcul de Cliques Max dans un graphe
// TP 16/02/2007
// -------------------------------------

package choco.cp.solver.preprocessor.graph;
import choco.kernel.common.logging.ChocoLogging;

import java.util.Random;
import java.util.logging.Logger; // main de test

/**
 * 	Algorithme de Bron and Kerbosch 197 to find maxumum cliques
 */
public class MaxCliques {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

	private ArrayGraph graph; 
	private int[][]  cliques;
	
	public MaxCliques(ArrayGraph g) {
		graph   = g;
		computeCliques();
	}
	
	// Algorithme de Bron and Kerbosch 1973
	public void computeCliques(){
		graph.setNeighbours();
		boolean[] cand   = new boolean[graph.nbNode]; // graph.length = nombre de sommets
		boolean[] K	     = new boolean[graph.nbNode];
		boolean[] gammaK = new boolean[graph.nbNode];
		int[] degrees = graph.degrees();
		boolean empty = true;
		for(int i=0; i<graph.nbNode; i++) {
			if(degrees[i]>0) {
				empty = false;
				cand[i]   = true;    // sommets candidats    
				K[i]      = false;   // sommets de la clique courante
				gammaK[i] = true;    // sommets candidats connect�s � la clique courante
			}
		}
		cliques = new int[0][0];  
		if(!empty) { 
			BronKerbosh(cand, K, gammaK); 
		}
	}
	
	private boolean BronKerbosh(boolean[] cand, 
							 boolean[] K, 
							 boolean[] gammaK){
		int x = getAndRemoveMaxCand(cand);
		boolean[] updatedCand = removeNeighbours(x,cand); 
		if(!empty(updatedCand)) {
			boolean b = BronKerbosh(updatedCand,K.clone(),gammaK.clone());
			if (!b) return false;
		}
		K[x] = true; 
		boolean[] updatedGammaK = updateGammaK(x, gammaK); 
		if(!empty(updatedGammaK)) {
			return BronKerbosh(updatedGammaK,K,updatedGammaK);
		} else {
			cliques = storeCliques(K);
			
			if (cliques.length > 2000) {
				return false;
			} else return true;
		}
	}
	
	// Routines
	
	private static boolean empty(boolean[] array) {
		for(int i=0; i<array.length; i++) {
			if(array[i]) {
				return false;  
			}
		}
		return true;
	}
	
	private int getAndRemoveMaxCand(boolean[] cand) {
		int index = -1; 
		if (!empty(cand)) {     
			int max = 0; 
			int[] degrees = graph.degrees(); 
			for(int i=0; i<cand.length; i++) {
				if(degrees[i]>max && cand[i]) {
					max   = degrees[i]; 
					index = i; 
				}
			}
			cand[index] = false; 
		}
		return index; 
	}
	
	private boolean[] removeNeighbours(int x, boolean[] cand) {
		boolean[] res = cand.clone(); 
		int[] neighbours = graph.neighbours(x); 
		for(int i=0; i<neighbours.length; i++) {
			res[neighbours[i]]=false;
		}
		return res;
	}
	
	private boolean[] updateGammaK(int x, boolean[] gammaK) {
		boolean[] res = gammaK.clone(); 
		for(int i=0; i<gammaK.length; i++) {
			if(res[i]) {
				boolean isIn = graph.isIn(x,i); 
				if(!isIn) {
					res[i] = false; 
				}
			}
		}
		res[x] = false; 
		return res; 
	}
	
	private int[][] storeCliques(boolean[] K) {
		int[][] updated = new int[cliques.length+1][]; 
		for(int i=0; i<updated.length-1; i++) {
			updated[i] = cliques[i]; 
		}
		int size = 0;
		for(int i=0; i<K.length; i++) {
			if(K[i]) {
				size++;
			}
		}
		updated[updated.length-1] = new int[size];  
		int index = 0; 
		for(int i=0; i<K.length; i++) {
			if(K[i]) {
				updated[updated.length-1][index] = i; 
				index ++; 
			}
		}
		return updated; 
	}
	
	// API utilisateur
	
	public int[][] getMaxCliques() {
		return cliques; 
	}
	
	// ******************************************** //
    // **************** Test ********************** //
    // ******************************************** //


    public static String display(int [] array){
		String s ="[";
		for(int i=0; i<array.length; i++) {
			s+=array[i];
			if(i<array.length-1) { 
				s+=",";
			}
		}
		s+="]";
		return s;
	}
	
	public static String display(int [][] array){
		String s ="";
		for(int i=0; i<array.length; i++) {
			s += display(array[i]);
			s += "\n"; 
		}
		return s;
	}
	
	public static ArrayGraph generateGraph(int n, int m, int seed, double start) {
		LOGGER.info("Generating graph... "); 
		ArrayGraph g = new ArrayGraph(n); 	
		if(m>n*(n+1)/2) {
			m = n*(n+1)/2; 
		}
	    Random r = new Random(seed);
	    for(int i=0; i<m; i++) {
	    	int v1 = Math.abs(r.nextInt())%g.nbNode;
	    	int v2 = Math.abs(r.nextInt())%g.nbNode;
	    	while(v1==v2 || g.isIn(v1,v2)) { // no loop 
	    		v1 = Math.abs(r.nextInt())%g.nbNode;
	    		v2 = Math.abs(r.nextInt())%g.nbNode;
	    	}
	    	g.addEdge(v1,v2);
	    }	
	    LOGGER.info("done " + "(" + (System.currentTimeMillis()-start) + " ms).\n");
	    return g;
	}
	
	public static void test(int n, int m, int seed) {
		double start = System.currentTimeMillis();	
		ArrayGraph g = generateGraph(n,m,seed,start); 
	    MaxCliques myCliques = new MaxCliques(g);
	    LOGGER.info("cliques : \n" + display(myCliques.getMaxCliques()));
	    LOGGER.info("Total time : " + (System.currentTimeMillis()-start) + " ms.\n");
	    if(n<=16) { 
	    	LOGGER.info(g.toString());
	    }
	}
	
	public static void testEmptyGraph241108() {
		LOGGER.info("Graph without edges");
		test(6,0,1986);
	}
	
	public static void main(String[] args) {
		int n = 6;
		int m = 10;
		int seed = 1986; 
		if(args.length>=1) {
		    n = Integer.parseInt(args[0]);
		    if(args.length>=2) {
		    	m = Integer.parseInt(args[1]);
		    	if(args.length>=3) {
		    		seed = Integer.parseInt(args[2]);
		    	}
		    }
		}
		test(n,m,seed); 
		testEmptyGraph241108();
	}

}

