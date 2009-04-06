package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.ZebraTest.java, last modified by flaburthe 16 janv. 2004 14:57:02 */

public class ZebraTest extends TestCase {
  private static Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar green, blue, yellow, ivory, red;
  private IntDomainVar diplomat, painter, sculptor, doctor, violinist;
  private IntDomainVar norwegian, english, japanese, spaniard, italian;
  private IntDomainVar wine, milk, coffee, water, tea;
  private IntDomainVar fox, snail, horse, dog, zebra;
  private IntDomainVar[] colors, trades, nationalities, drinks, pets;
  private IntDomainVar[][] arrays;

  protected void setUp() {
    logger.fine("Zebra Testing...");
    pb = new Problem();
    green = pb.makeEnumIntVar("green", 1, 5);
    blue = pb.makeEnumIntVar("blue", 1, 5);
    yellow = pb.makeEnumIntVar("yellow", 1, 5);
    ivory = pb.makeEnumIntVar("ivory", 1, 5);
    red = pb.makeEnumIntVar("red", 1, 5);
    diplomat = pb.makeEnumIntVar("diplomat", 1, 5);
    painter = pb.makeEnumIntVar("painter", 1, 5);
    sculptor = pb.makeEnumIntVar("sculptor", 1, 5);
    doctor = pb.makeEnumIntVar("doctor", 1, 5);
    violinist = pb.makeEnumIntVar("violinist", 1, 5);
    norwegian = pb.makeEnumIntVar("norwegian", 1, 5);
    english = pb.makeEnumIntVar("english", 1, 5);
    japanese = pb.makeEnumIntVar("japanese", 1, 5);
    spaniard = pb.makeEnumIntVar("spaniard", 1, 5);
    italian = pb.makeEnumIntVar("italian", 1, 5);
    wine = pb.makeEnumIntVar("wine", 1, 5);
    milk = pb.makeEnumIntVar("milk", 1, 5);
    coffee = pb.makeEnumIntVar("coffee", 1, 5);
    water = pb.makeEnumIntVar("water", 1, 5);
    tea = pb.makeEnumIntVar("tea", 1, 5);
    fox = pb.makeEnumIntVar("fox", 1, 5);
    snail = pb.makeEnumIntVar("snail", 1, 5);
    horse = pb.makeEnumIntVar("horse", 1, 5);
    dog = pb.makeEnumIntVar("dog", 1, 5);
    zebra = pb.makeEnumIntVar("zebra", 1, 5);
    colors = new IntDomainVar[]{green, blue, yellow, ivory, red};
    trades = new IntDomainVar[]{diplomat, painter, sculptor, doctor, violinist};
    nationalities = new IntDomainVar[]{norwegian, english, japanese, spaniard, italian};
    drinks = new IntDomainVar[]{wine, milk, coffee, water, tea};
    pets = new IntDomainVar[]{fox, snail, horse, dog, zebra};
    arrays = new IntDomainVar[][]{colors, trades, nationalities, drinks, pets};
  }

  protected void tearDown() {
    pb = null;
    green = null;
    blue = null;
    yellow = null;
    ivory = null;
    red = null;
    diplomat = null;
    painter = null;
    sculptor = null;
    doctor = null;
    violinist = null;
    norwegian = null;
    english = null;
    japanese = null;
    spaniard = null;
    italian = null;
    wine = null;
    milk = null;
    coffee = null;
    water = null;
    tea = null;
    fox = null;
    snail = null;
    horse = null;
    dog = null;
    zebra = null;
    colors = null;
    trades = null;
    nationalities = null;
    drinks = null;
    pets = null;
    arrays = null;
  }

  public void checkSolution() {
    for (int a = 0; a < 5; a++) {
      for (int i = 0; i < 5; i++) {
        assertTrue(arrays[a][i].isInstantiated());
      }
    }
    assertEquals(1, norwegian.getVal());
    assertEquals(1, diplomat.getVal());
    assertEquals(1, fox.getVal());
    assertEquals(1, water.getVal());
    assertEquals(1, yellow.getVal());
    assertEquals(2, italian.getVal());
    assertEquals(2, doctor.getVal());
    assertEquals(2, horse.getVal());
    assertEquals(2, tea.getVal());
    assertEquals(2, blue.getVal());
    assertEquals(3, english.getVal());
    assertEquals(3, sculptor.getVal());
    assertEquals(3, snail.getVal());
    assertEquals(3, milk.getVal());
    assertEquals(3, red.getVal());
    assertEquals(4, spaniard.getVal());
    assertEquals(4, violinist.getVal());
    assertEquals(4, dog.getVal());
    assertEquals(4, wine.getVal());
    assertEquals(4, ivory.getVal());
    assertEquals(5, japanese.getVal());
    assertEquals(5, painter.getVal());
    assertEquals(5, zebra.getVal());
    assertEquals(5, coffee.getVal());
    assertEquals(5, green.getVal());
  }

  public void test0() {
    for (int a = 0; a < 5; a++) {
      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 5; j++) {
          pb.post(pb.neq(arrays[a][i], arrays[a][j]));
        }
      }
    }
    pb.post(pb.eq(english, red));
    pb.post(pb.eq(spaniard, dog));
    pb.post(pb.eq(coffee, green));
    pb.post(pb.eq(italian, tea));
    pb.post(pb.eq(sculptor, snail));
    pb.post(pb.eq(diplomat, yellow));
    pb.post(pb.eq(green, pb.plus(ivory, 1)));
    pb.post(pb.eq(milk, 3));
    pb.post(pb.eq(norwegian, 1));
    // pb.post(((doctor - fox == 1) or (doctor - fox == -1)));
    pb.post(pb.eq(pb.minus(doctor, fox), 1));
    pb.post(pb.eq(violinist, wine));
    pb.post(pb.eq(japanese, painter));
    //pb.post(((diplomat - horse == 1) or (diplomat - horse == -1)));
    pb.post(pb.eq(pb.minus(diplomat, horse), -1));
    //pb.post(((norwegian - blue == 1) or (norwegian - blue == -1)));
    pb.post(pb.eq(pb.minus(norwegian, blue), -1));

    pb.solve(true);
    Solver s = pb.getSolver();
    assertEquals(1, s.getNbSolutions());
    checkSolution();
  }

  public void test1() {
    for (int a = 0; a < 5; a++) {
      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 5; j++) {
          pb.post(pb.neq(arrays[a][i], arrays[a][j]));
        }
      }
    }
    pb.post(pb.eq(english, red));
    pb.post(pb.eq(spaniard, dog));
    pb.post(pb.eq(coffee, green));
    pb.post(pb.eq(italian, tea));
    pb.post(pb.eq(sculptor, snail));
    pb.post(pb.eq(diplomat, yellow));
    pb.post(pb.eq(green, pb.plus(ivory, 1)));
    pb.post(pb.eq(milk, 3));
    pb.post(pb.eq(norwegian, 1));
    pb.post(pb.or(pb.eq(pb.minus(doctor, fox), 1), pb.eq(pb.minus(doctor, fox), -1)));
    pb.post(pb.eq(violinist, wine));
    pb.post(pb.eq(japanese, painter));
    pb.post(pb.or(pb.eq(pb.minus(diplomat, horse), 1), pb.eq(pb.minus(diplomat, horse), -1)));
    pb.post(pb.or(pb.eq(pb.minus(norwegian, blue), 1), pb.eq(pb.minus(norwegian, blue), -1)));

    pb.solve(true);
    Solver s = pb.getSolver();
    assertEquals(1, s.getNbSolutions());
    checkSolution();
  }

}