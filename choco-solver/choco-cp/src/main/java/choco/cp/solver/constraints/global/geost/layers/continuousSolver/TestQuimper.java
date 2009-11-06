package choco.cp.solver.constraints.global.geost.layers.continuousSolver;

class TestQuimper {

    public static void main(String[] args) {

	Quimper q = new Quimper("/Users/szampelli/Research/EssaisGeost/src/Ibex/ring.qpr");
	
	System.out.println(q.get_lb("x[1]")+" "+q.get_ub("x[2]"));

	q.contract("sat");

	System.out.println(q.get_lb("x[1]")+" "+q.get_ub("x[2]"));
    }


}