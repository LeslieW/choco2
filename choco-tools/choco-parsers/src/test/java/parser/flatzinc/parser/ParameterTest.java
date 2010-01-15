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

import choco.kernel.model.variables.set.SetConstantVariable;
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
public class ParameterTest {

    @Before
    public void before(){
        FZNParser.init();
    }

    @Test
    public void testBool(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "bool: bb = true;");
        Object o = FZNParser.map.get("bb");
        Assert.assertTrue(Boolean.class.isInstance(o));
        Boolean oi = (Boolean)o;
        Assert.assertTrue(oi);
    }

    @Test
    public void testInt(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "int: n = 4;");
        Object o = FZNParser.map.get("n");
        Assert.assertTrue(Integer.class.isInstance(o));
        Integer oi = (Integer)o;
        Assert.assertEquals(4, oi.intValue());
    }

    @Test
    public void testInt2(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "0..1: n = 4;");
        Object o = FZNParser.map.get("n");
        Assert.assertTrue(Integer.class.isInstance(o));
        Integer oi = (Integer)o;
        Assert.assertEquals(4, oi.intValue());
    }

    @Test
    public void testInt3(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "{0,68}: n = 4;");
        Object o = FZNParser.map.get("n");
        Assert.assertTrue(Integer.class.isInstance(o));
        Integer oi = (Integer)o;
        Assert.assertEquals(4, oi.intValue());
    }

    @Test
    public void testSet1(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "set of int: jobs = 6 .. 8;");
        Object o = FZNParser.map.get("jobs");
        Assert.assertTrue(SetConstantVariable.class.isInstance(o));
        SetConstantVariable oi = (SetConstantVariable)o;
        Assert.assertArrayEquals(new int[]{6,7,8}, oi.getValues());
    }

    @Test
    public void testSet2(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "set of int: jobs = {6,88,99};");
        Object o = FZNParser.map.get("jobs");
        Assert.assertTrue(SetConstantVariable.class.isInstance(o));
        SetConstantVariable oi = (SetConstantVariable)o;
        Assert.assertArrayEquals(new int[]{6,88,99}, oi.getValues());
    }

    @Test
    public void testArray1(){
        TerminalParser.parse(FZNParser.PAR_VAR_DECL, "array[1 .. 10] of int: job_task_duration = [ 23, 82, 84, 45, 38, 50, 41, 29, 18, 21];");
        Object o = FZNParser.map.get("job_task_duration");
        Assert.assertTrue(o.getClass().isArray());
        int[] oi = (int[])o;
        Assert.assertArrayEquals(new int[]{23, 82, 84, 45, 38, 50, 41, 29, 18, 21}, oi);
    }

}
