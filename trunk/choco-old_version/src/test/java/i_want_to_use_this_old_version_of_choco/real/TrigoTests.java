package i_want_to_use_this_old_version_of_choco.real;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.real.exp.RealCos;
import i_want_to_use_this_old_version_of_choco.real.exp.RealMinus;
import i_want_to_use_this_old_version_of_choco.real.exp.RealSin;
import i_want_to_use_this_old_version_of_choco.real.search.AssignInterval;
import i_want_to_use_this_old_version_of_choco.real.search.CyclicRealVarSelector;
import i_want_to_use_this_old_version_of_choco.real.search.RealIncreasingDomain;
import junit.framework.TestCase;

public class TrigoTests extends TestCase {
  public void test1() {
    Problem pb = new Problem();
    RealVar alpha = pb.makeRealVar("alpha", -Math.PI, Math.PI);

    RealExp exp = new RealMinus(pb,
        new RealCos(pb, alpha),
        new RealSin(pb, alpha));
    Constraint c = pb.makeEquation(exp, pb.cst(0.0));
    System.out.println("c = " + c.pretty());
    pb.post(pb.makeEquation(exp, pb.cst(0.0)));

    boolean first = false;
    Solver solver = pb.getSolver();
    solver.setFirstSolution(first);
    solver.generateSearchSolver(pb);
    solver.addGoal(new AssignInterval(new CyclicRealVarSelector(pb), new RealIncreasingDomain()));
    solver.launch();

    assertTrue(solver.getNbSolutions() >= 2);
    assertTrue(Math.abs(Math.cos(alpha.getInf()) - Math.sin(alpha.getInf())) < 1e-8);
  }

  public void test2() {
    Problem pb = new Problem();

    RealVar alpha = pb.makeRealVar("alpha", -5.5 * Math.PI, -1.5 * Math.PI);

    RealExp exp = new RealCos(pb, alpha);
    pb.post(pb.makeEquation(exp, pb.cst(1.0)));

    boolean first = false;
    Solver solver = pb.getSolver();
    solver.setFirstSolution(first);
    solver.generateSearchSolver(pb);
    solver.addGoal(new AssignInterval(new CyclicRealVarSelector(pb), new RealIncreasingDomain()));
    solver.launch();

    assertTrue(solver.getNbSolutions() >= 2);
  }
}
