package samples.multicostregular.nsp;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 4, 2008
 * Time: 5:12:08 PM
 */
public  class NSPInstance
{
    public int nbNurses;
    public int nbShifts;
    public int nbDays;

    public int[][] coverages;
    public int[][] prefs;

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(nbNurses).append("\t").append(nbDays).append("\t").append(nbShifts).append((char) Character.LINE_SEPARATOR).append((char) Character.LINE_SEPARATOR);
        for (int[] coverage : coverages) {
            for (int aCoverage : coverage) {
                b.append(aCoverage).append("\t");
            }
            b.append((char) Character.LINE_SEPARATOR);
        }
        b.append((char) Character.LINE_SEPARATOR);
        for (int[] pref : prefs) {
            for (int aPref : pref) {
                b.append(aPref).append("\t");
            }
            b.append("\n");
        }
        return b.toString();
    }



}
