/* ************************************************
 *           _       _                            *
 *          |   (..)  |                           *
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
* Date : 16 oct. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/

import java.io.*;
import java.util.Scanner;
import java.util.Stack;

public class ToTex {

    /**
     * Extracted code must start with the keyword "totex"  and end with the same keyword
     */
    private static final String KEYWORD = "//totex";

    private static final String APPEND = "//apptex";

    /**
     * file extension
     */
    private static final String EXTENSION = ".j2t";

    /**
     * Example of use
     */
    //totex file0
    public static void foo() {
        //totex file1
        String word = "Hello";
        //totex
        System.out.println(word);
    }
    //totex


    /**
     * Main call
     *
     * @param args array of size 2 excepted : {input file or directory, output directory}
     */
    public static void main(String[] args) {
		if(args.length != 2){
			System.err.println("2 arguments expected : input file or directory, output directory");
		}
        File input = new File(args[0]);
        File output = new File(args[1]);

        scan(input, output);
    }

    /**
     * Scan the input file, and treat every file of the directory
     *
     * @param input  file or directory to scan
     * @param output directory to write down
     */
    private static void scan(File input, File output) {
        if (input.isFile()) {
            ToTex.extract(input, output);
        } else if (input.isDirectory()) {
            for (File file : input.listFiles(new JavaFile())) {
                ToTex.scan(file, output);
            }
        }
    }

    /**
     * Java file filter
     */
    private static class JavaFile implements FilenameFilter {

        /**
         * Tests if a specified file should be included in a file list.
         *
         * @param dir  the directory in which the file was found.
         * @param name the name of the file.
         * @return <code>true</code> if and only if the name should be
         *         included in the file list; <code>false</code> otherwise.
         */
        public boolean accept(File dir, String name) {
            return dir.isDirectory() || (name.length() > 5 && name.substring(name.length() - 5).equals(".java"));
        }
    }

    /**
     * Read a java file and create j2t files for every tagged sub parts of code
     *
     * @param file name of the file to treat
     * @param dir  the directory to put to created j2t files
     */
    private static void extract(final File file, final File dir) {
        Stack<BufferedWriter> buffers = new Stack<BufferedWriter>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(KEYWORD)) {
                    String fileName = line.substring(line.indexOf(KEYWORD) + KEYWORD.length()).trim();
                    // (starting tag)
                    if (fileName.length() > 0) {
                        String absFileName = String.format("%s%s%s%s", dir.getAbsolutePath(), File.separator, fileName, EXTENSION);
                        buffers.add(createBuffer(absFileName, false));
                        line = br.readLine();
                        if (line == null) {
                            break;
                        }
                    }
                    // (ending tag)
                    else {
                        BufferedWriter buffer = buffers.pop();
                        line = "";
                        buffer.close();
                    }
                }else
                // (append tag)
                if (line.contains(APPEND)) {
                    String fileName = line.substring(line.indexOf(APPEND) + APPEND.length()).trim();
                    String absFileName = String.format("%s%s%s%s", dir.getAbsolutePath(), File.separator, fileName, EXTENSION);
                    buffers.add(createBuffer(absFileName, true));
                    line = br.readLine();
                    if (line == null) {
                        break;
                    }
                }
                for (BufferedWriter bf : buffers) {
                    bf.append(line).append("\n");
                }
            }
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            for (BufferedWriter bf : buffers) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private static BufferedWriter createBuffer(final String fileName, final boolean append) throws IOException {
        StringBuilder contents = new StringBuilder(40);
        if (append) {
            read(fileName, contents);
        }
        FileWriter fwriter = new FileWriter(fileName);
        BufferedWriter bwriter = new BufferedWriter(fwriter);
        bwriter.append(contents);
        return bwriter;
    }

    private static void read(final String fileName, final StringBuilder contents) throws IOException {
        Scanner scanner = new Scanner(new File(fileName));
        try {
            while (scanner.hasNextLine()) {
                contents.append(scanner.nextLine());
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            scanner.close();
        }
    }
}
