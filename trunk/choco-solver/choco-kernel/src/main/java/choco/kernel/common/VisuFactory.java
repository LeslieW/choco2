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
package choco.kernel.common;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;


public final class VisuFactory {

	protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	/**
	 * empty constructor
	 */
	private VisuFactory() {}
	
	//*****************************************************************//
	//*******************  Dotty  ************************************//
	//***************************************************************//

	public static File toDotty(final String source) {
		return toDotty(new IDotty() {
			@Override
			public String toDotty() {
				return source;
			}
		}
		);
	}

	public static File toDotty(final IDotty... sources) {
		final File f = createTempFile("dotty", ".dot");
		toDotty(f,sources);
		return f;
	}

	public static void toDotty(final File f, final IDotty... sources) {
		if(f != null && sources!=null && sources.length>0) {
			try {
				final FileWriter fw=new FileWriter(f);
				fw.write("digraph g {\n\n");
				for (IDotty s : sources) {
					fw.write(s.toDotty());
					fw.write('\n');
				}
				fw.write("\n}");
				fw.close();
				LOGGER.log(Level.CONFIG, "dotty...[dotExport:{0}][OK]",f);
				return;
			} catch (IOException e) {}
		}
		LOGGER.log(Level.WARNING, "dotty...[dotExport:{0}][FAIL]",f);
	}
	
	public static void createAndShowGUI( final IDotty... sources) {
		createAndShowDottyGUI( toDotty(sources));
	}
	public static void createAndShowDottyGUI(File f) {
		launchCommand("dotty "+f.getAbsolutePath(), false); 
	}
	
	
	
	//*****************************************************************//
	//*******************  Gnuplot ***********************************//
	//***************************************************************//
	
	private static void writeGnuplot(final File f, final String content) {
		if(f != null && content!=null && ! content.isEmpty()) {
			try {
				final FileWriter fw=new FileWriter(f);
				fw.write(content);
				fw.close();
				LOGGER.log(Level.CONFIG, "gnuplot...[gplExport:{0}][OK]",f);
				return;
			} catch (IOException e) {}
		}
		LOGGER.log(Level.WARNING, "gnuplot...[gplExport:{0}][FAIL]",f);
	}
	
	public static void createAndShowGnuplotGUI(final String curve) {
		final File dat=createTempFile("gnuplot", ".dat");
		writeGnuplot(dat, curve);
		final File script= new File(dat.getAbsolutePath()+".gpl");
		writeGnuplot(script, "plot "+dat.getAbsolutePath()+" with lines");
		createAndShowGnuplotGUI(script);
	}
	
	
	public static void createAndShowGnuplotGUI(File script) {
		launchCommand("gnuplot --persist "+script.getAbsolutePath(), false); 
	}
	
	//****************************************************************//
	//********* Utils*******************************************//
	//****************************************************************//



	public static File createTempFile(final String name, final String suffix) {
		File f=null;
		try {
			f= File.createTempFile(name+"_",suffix);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING,"tempFile...[create][FAIL]",e);
		}
		return f;
	}

	public static void launchCommand(final String cmd, final boolean waitFor) {
		// Win 95/98/ : pour lancer un .bat
		// cmd = "command.com /c c:\\fichier.bat";

		// Win NT(XP...) : pour lancer un .bat
		// cmd = "cmd /c c:\\fichier.bat";

		// Win 95/98/NT : pour lancer un .exe
		// cmd = "command.com /c c:\\windows\\notepad.exe";

		// Win 95/98/NT : pour lancer une commande dos
		// cmd = "cmd /c copy src.txt dest.txt";


		// UNIX : pour lancer un script en precisant le shell (sh,bash)
		// cmd = "/usr/bin/sh script.sh";

		// UNIX : pour lancer script
		// cmd = "/path_complet/tonscript";

		// EXEMPLES .EXE : NetMeeting
		//cmd = "C:\\Program Files\\NetMeeting\\conf.exe";
		// ainsi on peut lancer des programme tout a fait autonome
		// on peut soit faire p.waitfor() ou pas les deux cas fonctionnent correctement
		// je suppose aussi que c'est tout a fait vrai pour le cas d'UNIX (je ne l'ai pas tester

		try {
			final Runtime r = Runtime.getRuntime();
			final Process p = r.exec(cmd);
			if(waitFor) {
				p.waitFor();//si l'application doit attendre a ce que ce process fini
			}
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, "exec...["+cmd+"][FAIL]", e);
		}
	}

}




