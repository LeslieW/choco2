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
package parser.flatzinc.parser;

import junit.framework.Assert;
import org.codehaus.jparsec.Parser;
import org.junit.Before;
import org.junit.Test;
import parser.flatzinc.ast.PredParam;
import parser.flatzinc.ast.declaration.*;
import parser.flatzinc.ast.expression.*;

import java.util.List;

import static parser.flatzinc.parser.FZNParser.init;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class FZNParserTest {

    @Before
    public void before(){
        init();
    }

    @Test
    public void testBool() {
        assertResult(FZNParser.BOOL, "bool", DBool.class, "bool");
    }

    @Test
    public void testInt() {
        assertResult(FZNParser.INT, "int", DInt.class, "int");
    }

    @Test
    public void testInt2() {
        assertResult(FZNParser.INT2, "1..3", DInt2.class, "1..3");
    }

    @Test
    public void testManyInt() {
        assertResult(FZNParser.MANY_INT, "{1,2,3,5}", DManyInt.class, "{1,2,3,5}");
    }

    @Test
    public void testSetOfInt(){
        assertResult(FZNParser.SET_OF_INT, "set of int", DSet.class, "set of int");
        assertResult(FZNParser.SET_OF_INT, "set of 1..3", DSet.class, "set of 1..3");
        assertResult(FZNParser.SET_OF_INT, "set of {1,2,3,5}", DSet.class, "set of {1,2,3,5}");
        assertResult(FZNParser.SET_OF_INT, "set of {}", DSet.class, "set of {}");
    }

    @Test
    public void testArray(){
        assertResult(FZNParser.ARRAY_OF, "array [int] of bool", DArray.class, "array [int] of bool");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of bool", DArray.class, "array [1..3] of bool");
        assertResult(FZNParser.ARRAY_OF, "array [int] of int", DArray.class, "array [int] of int");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of int", DArray.class, "array [1..3] of int");
        assertResult(FZNParser.ARRAY_OF, "array [int] of 2..5", DArray.class, "array [int] of 2..5");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of 3..6", DArray.class, "array [1..3] of 3..6");
        assertResult(FZNParser.ARRAY_OF, "array [int] of {2,4,6}", DArray.class, "array [int] of {2,4,6}");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of {3,5,7}", DArray.class, "array [1..3] of {3,5,7}");
        assertResult(FZNParser.ARRAY_OF, "array [int] of set of int", DArray.class, "array [int] of set of int");
        assertResult(FZNParser.ARRAY_OF, "array [int] of set of 1..3", DArray.class, "array [int] of set of 1..3");
        assertResult(FZNParser.ARRAY_OF, "array [int] of set of {1,2,3,5}", DArray.class, "array [int] of set of {1,2,3,5}");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of set of int", DArray.class, "array [1..3] of set of int");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of set of 1..3", DArray.class, "array [1..3] of set of 1..3");
        assertResult(FZNParser.ARRAY_OF, "array [1..3] of set of {1,2,3,5}", DArray.class, "array [1..3] of set of {1,2,3,5}");
        assertResult(FZNParser.ARRAY_OF, "array \n [1..3] of set of {1,2,3,5}", DArray.class, "array [1..3] of set of {1,2,3,5}");

    }

    @Test
    public void testComment(){
        assertResult(FZNParser.BOOL_CONST, "true % comment", EBool.class, "true");
        assertResult(FZNParser.INT, "int % comment", DInt.class, "int");
        assertResult(FZNParser.INT2, "1..3 % comment", DInt2.class, "1..3");
        assertResult(FZNParser.MANY_INT, "{1,2,3,5} % comment", DManyInt.class, "{1,2,3,5}");
    }

    @Test
    public void testParType() {
        assertResult(FZNParser.TYPE, "bool", Declaration.class, "bool");
        assertResult(FZNParser.TYPE, "int", Declaration.class, "int");
        assertResult(FZNParser.TYPE, "1..3", Declaration.class, "1..3");
        assertResult(FZNParser.TYPE, "{1,2,3,5}", Declaration.class, "{1,2,3,5}");
        assertResult(FZNParser.TYPE, "set of int", Declaration.class, "set of int");
        assertResult(FZNParser.TYPE, "set of 1..3", Declaration.class, "set of 1..3");
        assertResult(FZNParser.TYPE, "set of {1,2,3,5}", Declaration.class, "set of {1,2,3,5}");
        assertResult(FZNParser.TYPE, "array [int] of bool", Declaration.class, "array [int] of bool");
        assertResult(FZNParser.TYPE, "array [1..3] of bool", Declaration.class, "array [1..3] of bool");
        assertResult(FZNParser.TYPE, "array [int] of int", Declaration.class, "array [int] of int");
        assertResult(FZNParser.TYPE, "array [1..3] of int", Declaration.class, "array [1..3] of int");
        assertResult(FZNParser.TYPE, "array [int] of 2..5", Declaration.class, "array [int] of 2..5");
        assertResult(FZNParser.TYPE, "array [1..3] of 3..6", Declaration.class, "array [1..3] of 3..6");
        assertResult(FZNParser.TYPE, "array [int] of {2,4,6}", Declaration.class, "array [int] of {2,4,6}");
        assertResult(FZNParser.TYPE, "array [1..3] of {3,5,7}", Declaration.class, "array [1..3] of {3,5,7}");
        assertResult(FZNParser.TYPE, "array [int] of set of int", Declaration.class, "array [int] of set of int");
        assertResult(FZNParser.TYPE, "array [int] of set of 1..3", Declaration.class, "array [int] of set of 1..3");
        assertResult(FZNParser.TYPE, "array [int] of set of {1,2,3,5}", Declaration.class, "array [int] of set of {1,2,3,5}");
        assertResult(FZNParser.TYPE, "array [1..3] of set of int", Declaration.class, "array [1..3] of set of int");
        assertResult(FZNParser.TYPE, "array [1..3] of set of 1..3", Declaration.class, "array [1..3] of set of 1..3");
        assertResult(FZNParser.TYPE, "array [1..3] of set of {1,2,3,5}", Declaration.class, "array [1..3] of set of {1,2,3,5}");
    }

    @Test
    public void testVarDecl(){
        assertResult(FZNParser.TYPE, "var bool", Declaration.class, "var bool");
        assertResult(FZNParser.TYPE, "var int", Declaration.class, "var int");
        assertResult(FZNParser.TYPE, "var 1..3", Declaration.class, "var 1..3");
        assertResult(FZNParser.TYPE, "var {1,2,3,5}", Declaration.class, "var {1,2,3,5}");
        assertResult(FZNParser.TYPE, "var set of int", Declaration.class, "var set of int");
        assertResult(FZNParser.TYPE, "var set of 1..3", Declaration.class, "var set of 1..3");
        assertResult(FZNParser.TYPE, "var set of {1,2,3,5}", Declaration.class, "var set of {1,2,3,5}");
        assertResult(FZNParser.TYPE, "array [int] of var bool", Declaration.class, "array [int] of var bool");
        assertResult(FZNParser.TYPE, "array [1..3] of var bool", Declaration.class, "array [1..3] of var bool");
        assertResult(FZNParser.TYPE, "array [int] of var int", Declaration.class, "array [int] of var int");
        assertResult(FZNParser.TYPE, "array [1..3] of var int", Declaration.class, "array [1..3] of var int");
        assertResult(FZNParser.TYPE, "array [int] of var 2..5", Declaration.class, "array [int] of var 2..5");
        assertResult(FZNParser.TYPE, "array [1..3] of var 3..6", Declaration.class, "array [1..3] of var 3..6");
        assertResult(FZNParser.TYPE, "array [int] of var {2,4,6}", Declaration.class, "array [int] of var {2,4,6}");
        assertResult(FZNParser.TYPE, "array [1..3] of var {3,5,7}", Declaration.class, "array [1..3] of var {3,5,7}");
        assertResult(FZNParser.TYPE, "array [int] of var set of int", Declaration.class, "array [int] of var set of int");
        assertResult(FZNParser.TYPE, "array [int] of var set of 1..3", Declaration.class, "array [int] of var set of 1..3");
        assertResult(FZNParser.TYPE, "array [int] of var set of {1,2,3,5}", Declaration.class, "array [int] of var set of {1,2,3,5}");
        assertResult(FZNParser.TYPE, "array [1..3] of var set of int", Declaration.class, "array [1..3] of var set of int");
        assertResult(FZNParser.TYPE, "array [1..3] of var set of 1..3", Declaration.class, "array [1..3] of var set of 1..3");
        assertResult(FZNParser.TYPE, "array [1..3] of var set of {1,2,3,5}", Declaration.class, "array [1..3] of var set of {1,2,3,5}");
    }

    @Test
    public void testBoolConst() {
        assertResult(FZNParser.BOOL_CONST, "true", EBool.class, "true");
        assertResult(FZNParser.BOOL_CONST, "false", EBool.class, "false");
    }


    @Test
    public void testIntConst() {
        assertResult(FZNParser.INT_CONST, "1", EInt.class, "1");
        assertResult(FZNParser.INT_CONST, "-1", EInt.class, "-1");
    }

    @Test
    public void testSetConst(){
        assertResult(FZNParser.SET_CONST_1, "1 .. 3", ESetBounds.class, "1..3");
        assertResult(FZNParser.SET_CONST_2, "{1,2,3,5}", ESetList.class, "{1,2,3,5}");
        assertResult(FZNParser.SET_CONST_2, "{}", ESetList.class, "{}");
    }

    @Test
    public void testIdArray() {
        FZNParser.map.put("q", new Object[]{null});
        assertResult(FZNParser.ID_ARRAY, "q[1]", EIdArray.class, "q[1]");
    }

    @Test
    public void testExpression(){
        assertResult(FZNParser.expression(), "true", EBool.class, "true");
        assertResult(FZNParser.expression(), "18", EInt.class, "18");
        assertResult(FZNParser.expression(), "1 .. 16", ESetBounds.class, "1..16");
        assertResult(FZNParser.expression(), "{}", ESetList.class, "{}");
//        assertResult((Parser<EIdArray>) FZNParser.EXPRESSION, "toto[1]", EIdArray.class, "1..16");
    }

    @Test
    public void testExpression2(){
        assertResult(FZNParser.expression(), "true", Expression.class, "true");
        assertResult(FZNParser.expression(), "[1..4]", Expression.class, "[1..4]");
        assertResult(FZNParser.expression(), "[1,2,24]", Expression.class, "[1,2,24]");
        FZNParser.map.put("q", new Object[]{null});
        assertResult(FZNParser.expression(), "q[1]", Expression.class, "q[1]");
    }

    @Test
    public void testT(){
        assertResult(FZNParser.expression(), "q", EIdentifier.class, "q");
        FZNParser.map.put("q", new Object[]{null});
        assertResult(FZNParser.expression(), "q[1]", EIdArray.class, "q[1]");
    }

    @Test
    public void testListExpression(){
        FZNParser.map.put("a", null);
        FZNParser.map.put("b", null);
        Object o = TerminalParser.parse(FZNParser.list(FZNParser.expression()), "(a,b)");
        Assert.assertTrue(List.class.isInstance(o));
        List l = (List)o;
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(Expression.class.isInstance(l.get(0)));
        Assert.assertTrue(EIdentifier.class.isInstance(l.get(0)));
    }

    @Test
    public void testListExpression2(){
        FZNParser.map.put("q", new Object[]{null, null});
        Object o = TerminalParser.parse(FZNParser.list(FZNParser.expression()), "(q[1],q[2])");
        Assert.assertTrue(List.class.isInstance(o));
        List l = (List)o;
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(Expression.class.isInstance(l.get(0)));
        Assert.assertTrue(EIdArray.class.isInstance(l.get(0)));
    }

    @Test
    public void testAnnotation(){
//        assertResult(FZNParser.ANNOTATION, "q", EAnnotation.class, "q");
        assertResult(FZNParser.expression(), "q(1)", EAnnotation.class, "q(1)");
        assertResult(FZNParser.expression(), "q([1..4])", EAnnotation.class, "q([1..4])");
    }

    @Test
    public void testAnnotations(){
        Object o = TerminalParser.parse(FZNParser.ANNOTATIONS, "::q([1..4])::toto");
        Assert.assertTrue(o instanceof List);
        Assert.assertEquals(2, ((List)o).size());
    }

    @Test
    public void testAnnotations2(){
        Object o = TerminalParser.parse(FZNParser.ANNOTATIONS, "::seq_search([ int_search([a],input_order,indomain_min, complete)])");
        Assert.assertTrue(o instanceof List);
        Assert.assertEquals(1, ((List)o).size());
    }

    @Test
    public void testPredicateParam(){
        assertResult(FZNParser.PRED_PARAM, "array [int] of var int: y", PredParam.class, "array [int] of var int: y");
    }

    static <T> void assertResult(
            Parser<T> parser, String source, Class<? extends T> expectedType, String expectedResult) {
        assertToString(expectedType, expectedResult, TerminalParser.parse(parser, source));
    }

    static <T> void assertToString(
            Class<? extends T> expectedType, String expectedResult, T result) {
        Assert.assertTrue(expectedType.isInstance(result));
        Assert.assertEquals(expectedResult, result.toString());
    }
    
}
