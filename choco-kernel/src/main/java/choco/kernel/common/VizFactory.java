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

/**
 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
 *
 */
public final class VizFactory {

	/**
	 * empty constructor
	 */
	private VizFactory() {}

	//****************************************************************//
	//********* Dotty *******************************************//
	//****************************************************************//

	private final static String DOT_HEADER="digraph g {\n\n";

	public static File toDotty(final String source) {
		return toDotty(new DotString(source));
	}

	public static File toDotty(final IDotty... sources) {
		return toDotty(createTempFile("dotty", ".dot"),sources);
	}

	public static File toDotty(File f,final IDotty... sources) {
		if(sources!=null && sources.length>0) {
			try {
				final FileWriter fw=new FileWriter(f);
				fw.write(DOT_HEADER);
				for (IDotty s : sources) {
					fw.write(s.toDotty());
					fw.write('\n');
				}
				fw.write("\n}");
				fw.close();
				created(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return f;
		}else {
			VizFactory.error("no object to draw");
			return null;
		}
	}


	//****************************************************************//
	//********* Gnuplot display*******************************************//
	//****************************************************************//

	private static final String GPL_CMD="gnuplot -persist";

	protected static File createFile(String prefix, String suffix, String contents) {
		final File f=createTempFile(prefix,suffix);
		try {
			final FileWriter fw=new FileWriter(f);
			fw.write(contents);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		created(f);
		return f;
	}
	/**
	 * gnuplot should be in the PATH variable
	 */
	public static void displayGnuplot(final String curve) {
		displayGnuplot(createTempFile("gnuplot", ".gpl"));
	}

	/**
	 * gnuplot should be in the PATH variable
	 */
	public static void displayGnuplot(final File curve) {
		final File script=createFile("gnuplot",".gpl","plot \'"+curve.getAbsolutePath()+"\' with lines");
		execGnuplot(script);
	}

	public static void execGnuplot(final File script) {
		exec(GPL_CMD, true, script);
	}

	//****************************************************************//
	//********* Utils *******************************************//
	//****************************************************************//

	protected static void error(String message) {
		System.err.println(message);
	}

	protected static void info(String message) {
		System.out.println(message);
	}

	protected static void created(File f) {
		info("creation "+f.getAbsolutePath()+" [ok]");
	}

	public static File createTempFile(final String name, final String suffix) {
		File f=null;
		try {
			f= File.createTempFile(name+"_",suffix);
		} catch (IOException e) {
			VizFactory.error("can't create temporary file");
			e.printStackTrace();
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
			System.out.println("erreur d'execution " + cmd + e.toString());
		}
	}

	public static void exec(final String cmd, final boolean waitFor,final File data) {
		launchCommand(cmd+" "+data.getAbsolutePath(), waitFor);
	}
}

class DotString implements IDotty {

	public final String contents;

	public DotString(String contents) {
		super();
		this.contents = contents;
	}

	@Override
	public String toDotty() {
		return contents;
	}


}


