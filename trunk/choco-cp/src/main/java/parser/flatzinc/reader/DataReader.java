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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package parser.flatzinc.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class DataReader {

    static HashMap datas;

    /**
     * Read the data file
     * @param dataF
     * @throws java.io.IOException
     */
    public static HashMap readDataFile(String dataF) throws IOException {
        FileReader fr      = new FileReader(dataF);
        BufferedReader br  = new BufferedReader(fr);

        String line = br.readLine();
        while(line!=null){
            if(!line.startsWith("%") && !line.equals("")){
                if(!line.endsWith(";")){
                    String linetmp = br.readLine();
                    while(!linetmp.endsWith(";")){
                        line = line.concat(linetmp.replace("\t", ""));
                        linetmp = br.readLine();
                    }
                    line = line.concat(linetmp.replace("\t", ""));
                }
                buildDatas(line);
            }
            line = br.readLine();
        }
        fr.close();
        br.close();
        return datas;
    }

    private static void buildDatas(String line){
        String[] parts = line.replace(" ","").replaceFirst(";","").split("=");
        if(parts[1].contains("array1d")|| parts[1].contains("array2d")){
            parts[1] = line.substring(line.indexOf("["), line.indexOf("]"));
        }
        if(parts[1].contains("|")){
            //build a matrix
            String[] stlines = parts[1].replace("[","").replace("]","").split("\\|");
            //the first cell is empty
            Object[][] values = new Object[stlines.length-1][];
            for(int j = 1; j < stlines.length; j++){
                String[] stvalues = stlines[j].split(",");
                values[j-1] = new Object[stvalues.length];
                for(int i = 0; i < stvalues.length; i++){
                    values[j-1][i] = getValue(stvalues[i]);
                }
            }
            datas.put(parts[0], values);
        }else
        if(parts[1].contains("[")){
            //build an array
            String[] stvalues = parts[1].replace("[","").replace("]","").split(",");
            Object[] values = new Object[stvalues.length];
            for(int i = 0; i < stvalues.length; i++){
                values[i] = getValue(stvalues[i]);
            }
            datas.put(parts[0], values);
        }else{
            datas.put(parts[0], getValue(parts[1]));
        }
    }

    private static Object getValue(String st){
        try{
            return Integer.parseInt(st);
        }catch(NumberFormatException nfe){
            //Not a integer...
        }
        try{
            return Float.parseFloat(st);
        }catch(NumberFormatException nfe){
            //Not a float...
        }
        return st;
    }

}
