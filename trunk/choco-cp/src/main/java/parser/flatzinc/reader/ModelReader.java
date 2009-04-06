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

import choco.cp.model.CPModel;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.HashMap;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ModelReader {

    static CPModel model;

    public static CPModel readModelFile(String modelF, HashMap datas) throws IOException {
        FileReader fr      = new FileReader(modelF);
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
                buildModel(line);
            }
            line = br.readLine();
        }
        fr.close();
        br.close();
        return model;
    }

    private static void buildModel(String line) {
        if(line.contains("include")){
            return;
        }

        

    }

}
