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
package samples.multicostregular.carsequencing.parser;


import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 28, 2009
 * Time: 3:12:14 PM
 */
public class CarSeqInstance {

    public String name;

    public int nbCars;
    public int nbOptions;
    public int nbClasses;

    public int[] maxPerBlock;
    public int[] blockSize;

    public int[][] optionRequirement;
    public int[] nbOptionsPerClasse;


    public CarSeqInstance(String filename)
    {
        try {
            this.parse(filename);
        } catch (IOException e) {
            System.err.println("Unable to parse file");
        }
    }

    private void parse(String filename) throws IOException {
        BufferedReader read = new BufferedReader(new FileReader(filename));

        String r;
        int i = 0;

        while ((r= read.readLine()) != null)
        {
            if (r.length() < 3) break;
            if (i < 3)
            {
                if (i == 1)
                {
                    this.name = r.substring(2,r.length());
                }
            }
            else
            {
                String[] stmp = r.split(" ");
                int[] tmp = new int[stmp.length];
                for (int l = 0 ; l < stmp.length;l++) tmp[l] = Integer.parseInt(stmp[l]);

                if (i == 3)
                {
                    this.nbCars = tmp[0];
                    this.nbOptions = tmp[1];
                    this.nbClasses = tmp[2];
                    this.optionRequirement = new int[this.nbClasses][];
                }
                else if (i == 4)
                {
                    this.maxPerBlock = new int[tmp.length];
                    System.arraycopy(tmp,0,this.maxPerBlock,0,tmp.length);
                }
                else if (i == 5)
                {
                    this.blockSize = new int[tmp.length];
                    System.arraycopy(tmp,0,this.blockSize,0,tmp.length);
                }
                else
                {
                    int[] or = new int[this.nbOptions+2];
                    System.arraycopy(tmp,0,or,0,tmp.length);
                    this.optionRequirement[i-6] = or;
                }
            }
            i++;
        }
    }

    public static void main(String[] args) {

        int nb = 10;

        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter("monFichier.txt"));

            buff.write(nb+"");
            buff.newLine();
            buff.write((nb+1)+"");
            buff.newLine();
            buff.close();



        } catch (IOException e) {
            System.err.println("impossible d'ouvrir le fichier");
        }


    }


}