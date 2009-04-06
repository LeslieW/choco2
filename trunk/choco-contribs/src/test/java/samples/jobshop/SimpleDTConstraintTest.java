package samples.jobshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class SimpleDTConstraintTest {
    private static final Random RAND = new Random();
    private IntDomainVar v0;
    private IntDomainVar v1;

    @Before
    public void setUp() {
        final Solver s = new CPSolver();
        v0 = new IntDomainVarImpl(s, "v0", IntDomainVar.BITSET, 0, 99);
        v1 = new IntDomainVarImpl(s, "v0", IntDomainVar.BITSET, 0, 99);
    }

    @Test
    public void nextAllowedTest() {
        for (int i = 10000; --i >= 0;) {
            final SimpleDTConstraint sdt = new SimpleDTConstraint(v0, v1, RAND
                    .nextInt(100), RAND.nextInt(100));

            final int[] tuple = new int[2];
            tuple[0] = RAND.nextInt(100);
            tuple[1] = RAND.nextInt(100);

            final boolean allowed = sdt.check(tuple);
            if (allowed) {
                assertEquals("Did not work for " + Arrays.toString(tuple),
                        tuple[0], sdt.nextAllowed(1, tuple[1], tuple[0]));
                assertEquals("Did not work for " + Arrays.toString(tuple),
                        tuple[1], sdt.nextAllowed(0, tuple[0], tuple[1]));
            } else {
                assertTrue(sdt.check(new int[] { tuple[0],
                        sdt.nextAllowed(0, tuple[0], tuple[1]) }));
                assertTrue(sdt.check(new int[] {
                        sdt.nextAllowed(1, tuple[1], tuple[0]), tuple[1] }));
            }
        }
    }
}
