/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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




