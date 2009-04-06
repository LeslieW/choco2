package choco.cp.solver.search.task;

import java.util.Random;

public class Centroid implements PrecValSelector {

	private final Random rnd;
	
	public Centroid() {
		super();
		rnd = new Random();
	}

	public Centroid(long seed) {
		super();
		rnd = new Random(seed);
	}

	@Override
	public int getBestVal(StoredPrecedence precedence) {
		final double c1=precedence.t1.getCentroid();
		final double c2=precedence.t2.getCentroid();
		if(c1<c2) {return 1;}
		else if(c1>c2) {return 0;}
		else {return rnd.nextInt(2);}
	}

}
