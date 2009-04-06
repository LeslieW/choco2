package parser.chocogen;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Feb 16, 2009
 * Time: 2:24:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlClause {
    public int[] poslits;
    public int[] neglits;

    public XmlClause(int[] pl, int[] nl) {
        this.poslits = pl;
        this.neglits = nl;
    }

}
