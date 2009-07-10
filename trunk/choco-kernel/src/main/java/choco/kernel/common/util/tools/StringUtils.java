/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.common.util.tools;

import choco.IPretty;
import choco.kernel.model.constraints.automaton.FA.Automaton;

import java.util.Collection;
import java.util.Iterator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 3 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*
* Provides some short and usefull methods to deal with String object
* and pretty print of IPretty objects.
*
*/
public class StringUtils {
    /**
     * Pads out a string upto padlen with pad chars
     *
     * @param str    string to be padded
     * @param padlen length of pad (+ve = pad on right, -ve pad on left)
     * @param pad    character
     */
    public static String pad(String str, int padlen, String pad) {
        String padding = new String();
        int len = Math.abs(padlen) - str.length();
        if (len < 1) {
            return str;
        }
        for (int i = 0; i < len; ++i) {
            padding = padding + pad;
        }
        return (padlen < 0 ? padding + str : str + padding);
    }//*****************************************************************//

    //*******************  Pretty  ********************************//
    //***************************************************************//
    public static String pretty(final IPretty[] elems, int begin, int end) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("{ ");
        for (int i = begin; i < end; i++) {
            buffer.append(elems[i].pretty()).append(", ");
        }
        buffer.deleteCharAt(buffer.length() - 2);
        buffer.append("}");
        return new String(buffer);
    }

    public static String pretty(final IPretty... elems) {
        return pretty(elems, 0, elems.length);
    }

    public static String prettyOnePerLine(final Collection<? extends IPretty> elems) {
        return prettyOnePerLine(elems.iterator());
    }

    /**
     * @param iter
     */
    public static String prettyOnePerLine(Iterator<? extends IPretty> iter) {
        final StringBuilder buffer = new StringBuilder();
        while (iter.hasNext()) {
            buffer.append(iter.next().pretty()).append('\n');
        }
        return new String(buffer);
    }

    public static String pretty(final Collection<? extends IPretty> elems) {
        return pretty(elems.iterator());
    }

    public static String pretty(final Iterator<? extends IPretty> iter) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("{ ");
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                buffer.append(iter.next().pretty()).append(", ");
            }
            buffer.deleteCharAt(buffer.length() - 2);
        }
        buffer.append("}");
        return new String(buffer);
    }

    public static String pretty(int[] lval) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int i = 0; i < lval.length - 1; i++) {
            sb.append(lval[i]);
            sb.append(",");
        }
        sb.append(lval[lval.length - 1]);
        sb.append("}");
        return sb.toString();
    }

    public static String pretty(int[][] lvals) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int i = 0; i < lvals.length; i++) {
            if (i > 0) sb.append(", ");
            int[] lval = lvals[i];
            sb.append("{");
            for (int j = 0; j < lval.length; j++) {
                if (j > 0) sb.append(",");
                int val = lval[j];
                sb.append(val);
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    public static String pretty(int c) {
        StringBuffer sb = new StringBuffer();
        if (c > 0) {
            sb.append(" + ");
            sb.append(c);
        } else if (c < 0) {
            sb.append(" - ");
            sb.append(-(c));
        }
        return sb.toString();
    }

    /**
	 * Convert a regexp formed with integer charachter into a char formed regexp
	 * for instance, "12%12%" which stands for 1 followed by 2 followed by 12 would be misinterpreted by regular
	 * regular expression parser. We use here the asci code to encode everything as a single char.
	 * Due to char encoding limits, we cannot parse int greater than 2^16-1
	 * @param strRegExp a regexp of integer
	 * @return a char regexp
	 */
	public static String toCharExp(String strRegExp) {
		StringBuffer b = new StringBuffer();
		for (int i =0 ;i < strRegExp.length() ;i++)
		{
			char c = strRegExp.charAt(i);
			if (c == '<')
			{
				int out = strRegExp.indexOf('>',i+1);
				int tmp = Integer.parseInt(strRegExp.substring(i+1,out));
				b.append((char) Automaton.getCharFromInt(tmp));
				i = out;
			}
			else if (Character.isDigit(c))
			{
				b.append((char) Automaton.getCharFromInt(Character.getNumericValue(c)));

			}
			else
			{
				b.append(c);
			}
		}

		return b.toString();

	}

    /**
	 * Transform a char regexp into an int regexp w.r.t. the asci code of each character.
	 * @param charExp a char regexp
	 * @return an int regexp
	 */
	public static String toIntExp (String charExp)
	{
		StringBuffer b = new StringBuffer();
		for (int i = 0 ; i < charExp.length() ; i++)
		{
			char c = charExp.charAt(i);
			if (c == '(' || c == ')' || c == '*' || c == '+' || c == '|')
			{
				b.append(c);
			}
			else
			{
				int n = (int) c;
				if (n >= 35) n--;
				if (n < 10) b.append(n);
				else b.append('<').append(n).append('>');
			}
		}

		return b.toString();
	}
}