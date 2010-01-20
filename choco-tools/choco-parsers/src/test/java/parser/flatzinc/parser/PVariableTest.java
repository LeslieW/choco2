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

import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class PVariableTest {

    FZNParser fzn;
    @Before
    public void before(){
        fzn = new FZNParser();
    }

    @Test
    public void testBool(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var bool: bb::var_is_introduced::is_defined_var;");
        Object o = fzn.map.get("bb");
        Assert.assertTrue(IntegerVariable.class.isInstance(o));
        IntegerVariable oi = (IntegerVariable)o;
        Assert.assertTrue(oi.isBoolean());
    }

    @Test
    public void testBound(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 0 .. 9: A::var_is_introduced;");
        Object o = fzn.map.get("A");
        Assert.assertTrue(IntegerVariable.class.isInstance(o));
        IntegerVariable oi = (IntegerVariable)o;
        Assert.assertNull(oi.getValues());
        Assert.assertEquals(0, oi.getLowB());
        Assert.assertEquals(9, oi.getUppB());
        Assert.assertArrayEquals(new int[]{0,1,2,3,4,5,6,7,8,9}, oi.enumVal());
    }

    @Test
    public void testEnum(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var {0,3,18}: B::var_is_introduced;");
        Object o = fzn.map.get("B");
        Assert.assertTrue(IntegerVariable.class.isInstance(o));
        IntegerVariable oi = (IntegerVariable)o;
        Assert.assertNotNull(oi.getValues());
        Assert.assertArrayEquals(new int[]{0,3,18}, oi.enumVal());
    }

    @Test
    public void testSetBound(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var set of 0..9: S::var_is_introduced;");
        Object o = fzn.map.get("S");
        Assert.assertTrue(SetVariable.class.isInstance(o));
        SetVariable oi = (SetVariable)o;
        Assert.assertNull(oi.getValues());
        Assert.assertEquals(0, oi.getLowB());
        Assert.assertEquals(9, oi.getUppB());
    }

    @Test
    public void testSetEnum(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var set of {0,3,18}: S::var_is_introduced;");
        Object o = fzn.map.get("S");
        Assert.assertTrue(SetVariable.class.isInstance(o));
        SetVariable oi = (SetVariable)o;
        Assert.assertNotNull(oi.getValues());
        Assert.assertArrayEquals(new int[]{0,3,18}, oi.getValues());
    }

    @Test
    public void testArray(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "array[1 .. 3] of var 0 .. 9: C::output_array([ 1 .. 3 ]);");
        Object o = fzn.map.get("C");
        Assert.assertTrue(o.getClass().isArray());
        IntegerVariable[] oi = (IntegerVariable[])o;
        Assert.assertEquals(3, oi.length);
        Assert.assertEquals("C_1", oi[0].getName());
        Assert.assertArrayEquals(new int[]{0,1,2,3,4,5,6,7,8,9}, oi[0].enumVal());
    }

    @Test
    public void testArray2(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 5: a ::output_var;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 5: b::output_var;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 5: c::output_var;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "array[1 .. 3] of var 1 .. 5: alpha = [ a, b, c];");
        Object o = fzn.map.get("alpha");
        Assert.assertTrue(o.getClass().isArray());
        IntegerVariable[] oi = (IntegerVariable[])o;
        Assert.assertEquals(3, oi.length);
        Assert.assertEquals("a", oi[0].getName());
        Assert.assertArrayEquals(new int[]{1,2,3,4,5}, oi[0].enumVal());

    }

}
