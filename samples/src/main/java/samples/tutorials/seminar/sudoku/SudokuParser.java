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
package samples.tutorials.seminar.sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Xavier Lorca
 * Date: 4 sept. 2007
 * Time: 09:34:11
 */
public class SudokuParser {
    protected String fileName;
    protected int[][] instance;

    public SudokuParser(String nFic) {
        fileName = nFic;
        instance = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) instance[i][j] = 0;
        }
    }

    public int[][] convert() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line;
            int i = 0;
            while ((line = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, " ");
                int taille = st.countTokens();
                int j = 0;
                while (taille > 0) {
                    int val = Integer.parseInt(st.nextToken());
                    instance[i][j] = val;
                    j++;
                    taille--;
                }
                i++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }
}

