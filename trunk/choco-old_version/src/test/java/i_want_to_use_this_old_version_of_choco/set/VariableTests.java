package i_want_to_use_this_old_version_of_choco.set;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class VariableTests extends TestCase {
	private Logger logger = Logger.getLogger("choco.currentElement");

	public void test1() {
		logger.finer("test1");
		Problem pb = new Problem();
		SetVar x = pb.makeSetVar("X", 1, 5);
		try {
			x.addToKernel(2, -1);
			x.addToKernel(4, -1);
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		IntIterator it = x.getDomain().getOpenDomainIterator();
		while (it.hasNext()) {
			int val = it.next();
			System.out.println("" + val);
			assertTrue(val != 2);
			assertTrue(val != 4);
		}
	}

	public void test2() {
		Problem pb = new Problem();
		SetVar set = pb.makeSetVar("X", 1, 5);
		boolean bool = true;
		System.out.println("" + set.pretty());
		for (IntIterator it0 = set.getDomain().getEnveloppeIterator();
		     it0.hasNext();) {
			int x = it0.next();
			bool = !bool;
			try {
				if (bool) {
					set.remFromEnveloppe(x, 0);
				}
			} catch (ContradictionException e) {

			}
		}
		System.out.println("" + set.pretty());
		assertTrue(!set.isInDomainKernel(2));
		assertTrue(!set.isInDomainKernel(4));
	}
}
