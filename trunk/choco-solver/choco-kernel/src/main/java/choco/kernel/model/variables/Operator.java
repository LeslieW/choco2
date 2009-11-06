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
package choco.kernel.model.variables;

/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 2 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public enum Operator {
  ABS("abs", "operator.abs", 1),
  COS("cos", "operator.cos", 1),
  DISTANCEEQ("distanceEQ"),
  DISTANCEGT("distanceGT"),
  DISTANCELT("distanceLT"),
  DISTANCENEQ("distanceEQ"),
  DIV("div", "operator.div", 2),
  IFTHENELSE("ifthenelse", "operator.ifthenelse", 2),
  MINUS("minus", "operator.minus", 2),
  MAX("max", "operator.max", 0),
  MIN("min", "operator.min", 0),
  MOD("mod", "operator.mod", 2),
  MULT("mult", "operator.mult", 2),
  NEG("neg", "operator.neg", 1),
  NONE("none"),
  PLUS("plus", "operator.plus", 2),
  POWER("power", "operator.power", 2),
  SCALAR("scalar", "operator.scalar", 0),
  SIN("sin", "operator.sin", 1),
  SUM("sum", "operator.sum", 0),
  ;

  public final String name;
  public final String property;
  public final int parameters; // 0 means 1 or more parameters

    Operator(String name, String property, int paramaters) {
    this.name = name;
      this.property = property;
      this.parameters = paramaters;
  }

    Operator(String name) {
        this(name, null, -1);
    }
}
