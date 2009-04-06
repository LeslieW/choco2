//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Jan 13, 2004
 * Time: 10:22:26 AM
 * To change this template use Options | File Templates.
 */
public class SendMoney extends TestCase {
  public void testSendMoneyOne() {
    PalmProblem myPb = new PalmProblem();

    IntDomainVar s = myPb.makeEnumIntVar("S", 1, 9);
    IntDomainVar e = myPb.makeEnumIntVar("E", 0, 9);
    IntDomainVar n = myPb.makeEnumIntVar("N", 0, 9);
    IntDomainVar d = myPb.makeEnumIntVar("D", 0, 9);
    IntDomainVar m = myPb.makeEnumIntVar("M", 1, 9);
    IntDomainVar o = myPb.makeEnumIntVar("O", 0, 9);
    IntDomainVar r = myPb.makeEnumIntVar("R", 0, 9);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 0, 9);

    IntDomainVar[] letters = new IntDomainVar[]{s, e, n, d, m, o, r, y};

    IntDomainVar word1 = myPb.makeBoundIntVar("Send", 0, 1000000);
    IntDomainVar word2 = myPb.makeBoundIntVar("More", 0, 1000000);
    IntDomainVar word3 = myPb.makeBoundIntVar("Money", 0, 1000000);

    // Alldiff sur les variables
    for (int i = 1; i < 8; i++)
      for (int j = 0; j < i; j++) {
        myPb.post(myPb.neq(letters[i], letters[j]));
      }

    // Maintien des mots en fonction des lettres
    myPb.post(myPb.eq(word1, myPb.scalar(new int[]{1000, 100, 10, 1}, new IntDomainVar[]{s, e, n, d})));
    myPb.post(myPb.eq(word2, myPb.scalar(new int[]{1000, 100, 10, 1}, new IntDomainVar[]{m, o, r, e})));
    myPb.post(myPb.eq(word3, myPb.scalar(new int[]{10000, 1000, 100, 10, 1}, new IntDomainVar[]{m, o, n, e, y})));

    // Contrainte d'addition
    myPb.post(myPb.eq(word3, myPb.plus(word1, word2)));

    myPb.solve();

    assertEquals(word3.getVal(), word2.getVal() + word1.getVal());
    assertEquals(word2.getVal(), 1000 * m.getVal() + 100 * o.getVal() + 10 * r.getVal() + e.getVal());
  }

  public void testSendMoneyAll() {
    PalmProblem myPb = new PalmProblem();

    IntDomainVar s = myPb.makeEnumIntVar("S", 1, 9);
    IntDomainVar e = myPb.makeEnumIntVar("E", 0, 9);
    IntDomainVar n = myPb.makeEnumIntVar("N", 0, 9);
    IntDomainVar d = myPb.makeEnumIntVar("D", 0, 9);
    IntDomainVar m = myPb.makeEnumIntVar("M", 1, 9);
    IntDomainVar o = myPb.makeEnumIntVar("O", 0, 9);
    IntDomainVar r = myPb.makeEnumIntVar("R", 0, 9);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 0, 9);

    IntDomainVar[] letters = new IntDomainVar[]{s, e, n, d, m, o, r, y};

    IntDomainVar word1 = myPb.makeBoundIntVar("Send", 0, 1000000);
    IntDomainVar word2 = myPb.makeBoundIntVar("More", 0, 1000000);
    IntDomainVar word3 = myPb.makeBoundIntVar("Money", 0, 1000000);

    // Alldiff sur les variables
    for (int i = 1; i < 8; i++)
      for (int j = 0; j < i; j++) {
        myPb.post(myPb.neq(letters[i], letters[j]));
      }

    // Maintien des mots en fonction des lettres
    myPb.post(myPb.eq(word1, myPb.scalar(new int[]{1000, 100, 10, 1}, new IntDomainVar[]{s, e, n, d})));
    myPb.post(myPb.eq(word2, myPb.scalar(new int[]{1000, 100, 10, 1}, new IntDomainVar[]{m, o, r, e})));
    myPb.post(myPb.eq(word3, myPb.scalar(new int[]{10000, 1000, 100, 10, 1}, new IntDomainVar[]{m, o, n, e, y})));

    // Contrainte d'addition
    myPb.post(myPb.eq(word3, myPb.plus(word1, word2)));

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(1, nbSolutions);
  }
}
