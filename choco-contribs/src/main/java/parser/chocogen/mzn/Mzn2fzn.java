package parser.chocogen.mzn;/* ************************************************
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

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 22 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;

import java.io.*;
import java.util.logging.Logger;

/**
 * For internal uses
 */

public class Mzn2fzn {

    static Logger LOGGER = ChocoLogging.getParserLogger();

    static String _FZNDIR;

    private final static MZNFile _mznfilenamefilter = new MZNFile();
    private static class MZNFile implements FilenameFilter{
        @Override
        public boolean accept(File dir, String name) {
            return name.contains(".mzn");
        }
    }

    private final static DZNFile _dznfilenamefilter = new DZNFile();
    private static class DZNFile implements FilenameFilter{
        @Override
        public boolean accept(File dir, String name) {
            return name.contains(".dzn");
        }
    }

    private final static DIRFilter _isDirectory = new DIRFilter();
    private static class DIRFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        args = new String[]{"/home/charles/Bureau/minizinc-1.0.2/benchmarks/cars", "/home/charles/Choco/minizinc/fzn/"};
        File dir = new File(args[0]);
        _FZNDIR = args[1];
        massiveConvert(dir);
    }

    protected static void massiveConvert(File dir) throws IOException {
        System.out.println("Traiter ce dossier : "+ dir.getName()+" ?");
        DataInputStream example = new DataInputStream(System.in);
        String line = example.readLine();
        if(line.equals("N"))return;

        File[] mznFiles = dir.listFiles(_mznfilenamefilter);
        File[] dznFiles = findDatas(dir);
        if(mznFiles.length>0){
            for(File mzn : mznFiles){
                if(dznFiles.length==0){
                    mzn2fzn(mzn);
                }
                for(File dzn : dznFiles){
                    mzn2fzn(mzn, dzn);
                }
            }

        }
        File[] subDir = dir.listFiles(_isDirectory);
        for(File sDir : subDir){
            massiveConvert(sDir);
        }
    }

    private static File[] findDatas(File dir) {
        File[] dznFiles = dir.listFiles(_dznfilenamefilter);
        File[] subDirs = dir.listFiles(_isDirectory);
        for(File suDir : subDirs){
            dznFiles = ArrayUtils.append(dznFiles , findDatas(suDir));
        }
        return dznFiles;
    }

    protected static void mzn2fzn(File mzn, File dzn){
        String nMZN = mzn.getName();
        String nDZN = dzn.getName();

        String nFZN = nMZN.substring(0, nMZN.length()-4) + "_" + nDZN.substring(0, nDZN.length()-4);
        File fzn = new File(_FZNDIR+nFZN+".fzn");
        if(fzn.exists())return;

        StringBuilder cmd = initCmd();
        cmd.append(" ")
                .append(mzn.getAbsolutePath())
                .append(" -d ").append(dzn.getAbsolutePath())
                .append(" -o ").append(fzn.getAbsolutePath());

        System.out.println(cmd.toString());
        run(cmd.toString());
    }

    protected static void mzn2fzn(File mzn){
        String nMZN = mzn.getName();

        String nFZN = nMZN.substring(0, nMZN.length()-4);
        File fzn = new File(_FZNDIR+nFZN+".fzn");
        if(fzn.exists())return;
        
        StringBuilder cmd = initCmd();
        cmd.append(" ")
                .append(mzn.getAbsolutePath())
                .append(" -o ").append(fzn.getAbsolutePath());

        System.out.println(cmd.toString());
        run(cmd.toString());
    }

    private static StringBuilder initCmd() {
        String osName = System.getProperty("os.name");
        StringBuilder cmd = new StringBuilder();
        if(osName.contains("Linux")){
            cmd.append("/home/charles/Bureau/minizinc-1.0.2/bin/mzn2fzn");
        }
        return cmd;
    }

    /**
     * Run a specific command
     * @param cmd the command
     */
    private static void run(String cmd){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
