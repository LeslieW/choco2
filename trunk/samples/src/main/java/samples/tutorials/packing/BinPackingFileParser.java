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
package samples.tutorials.packing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

import parser.instances.InstanceFileParser;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 8 juil. 2010
 */
final class BinPackingFileParser implements InstanceFileParser {

    public File file;
    public int capacity;
    public int[] sizes;

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
            sizes = new int[nb];
            for (int i=0 ; i < nb ; i++) {
                sizes[i] = sc.nextInt();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(displayInstance && LOGGER.isLoggable(Level.INFO)){
            LOGGER.log(Level.INFO, "capacity : {0},\nWeights : {1}.\n",
                    new String[]{Integer.toString(capacity), Arrays.toString(sizes)});
        }
    }
    

    @Override
    public void cleanup() {
        this.file = null;
        this.capacity = 0;
        this.sizes = null;
    }

    public Object getParameters(){
        return new Object[]{sizes, capacity};
    }
}
