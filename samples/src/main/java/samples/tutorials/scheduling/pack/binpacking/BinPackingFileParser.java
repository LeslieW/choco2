/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
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
*                   N. Jussien    1999-2010      *
**************************************************/
package samples.tutorials.scheduling.pack.binpacking;

import parser.instances.InstanceFileParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 8 juil. 2010
 */
final class BinPackingFileParser implements InstanceFileParser {

    File file;
    int capacity;
    int[] weights;

    @Override
    public File getInstanceFile() {
        return file;
    }

    @Override
    public void loadInstance(File file) {
        this.file = file;
    }

    @Override
    public void parse(boolean displayInstance){
        Scanner sc = null;
        try {
            sc = new Scanner(file);
            int nb = sc.nextInt();
            capacity = sc.nextInt();
            weights = new int[nb];
            for (int i=0 ; i < nb ; i++) {
                weights[i] = sc.nextInt();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(displayInstance){
            LOGGER.log(Level.INFO, "capacity : {0},\nWeights : {1}.\n",
                    new String[]{Integer.toString(capacity), Arrays.toString(weights)});
        }
    }

    @Override
    public void cleanup() {
        this.file = null;
        this.capacity = 0;
        this.weights = null;
    }

    public Object getParameters(){
        return new Object[]{weights, capacity};
    }
}
