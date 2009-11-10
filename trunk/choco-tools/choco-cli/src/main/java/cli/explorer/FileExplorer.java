package cli.explorer;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;


import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;


/**
 * Explore a directory and its sub-directories and apply {link {@link FileProcedure}.
 * You can also use wildcard pattern to consider only a subset of files. 
 * @author Arnaud Malapert</br> 
 * @since 5 nov. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class FileExplorer {

	public final static Logger LOGGER= ChocoLogging.getParserLogger();

	public static final FileFilter DIRECTORY_FILTER = new AndFileFilter( Arrays.asList( 
			CanReadFileFilter.CAN_READ,
			DirectoryFileFilter.DIRECTORY,
			HiddenFileFilter.VISIBLE
	));

	public static final FileFilter FILE_FILTER = new AndFileFilter( Arrays.asList( 
			CanReadFileFilter.CAN_READ,
			FileFileFilter.FILE,
			HiddenFileFilter.VISIBLE
	));



	private FileExplorer() {
		super();
	}

	private static FileFilter makeFileFilter(List<String> wildcards) {
		return wildcards.isEmpty() ? FILE_FILTER : new AndFileFilter( Arrays.asList( FILE_FILTER, new WildcardFileFilter(wildcards)) );
	}


	@SuppressWarnings("unchecked")
	private static boolean exploreDirectory(FileProcedure proc, File dir,
			FileFilter instFilter) {
		LOGGER.log(Level.CONFIG,"explore...[dir:{0}] ",dir);
		final File[] instances = dir.listFiles(instFilter);
		Arrays.sort(instances, NameFileComparator.NAME_COMPARATOR);
		//execute instances
		for (File inst : instances) {
			if( ! proc.execute(inst) ) {
				LOGGER.log(Level.SEVERE,"explore...[file:{0}][FAIL]", inst);
				return false;
			}
		}
		//explore child directories
		File[] directories = dir.listFiles(DIRECTORY_FILTER);
		for (File cdir : directories) {
			if( ! exploreDirectory(proc, cdir, instFilter)) return false;
		}
		return true;
	}

	public static void explore(FileProcedure proc, File file, String... wildcards) {
		explore(proc, file, Arrays.asList(wildcards));
	}

	/**
	 * Explore recursivly a directory and apply a procedure to each valid file.  
	 * @param proc the procedure to apply 
	 * @param file the directory or a readable file
	 * @param wildcards wildcard patterns which filter filenames in the directories.
	 * @return <code>true</code> if it succeeds, <code>false</code> if it failed and exploration has been interrupted.
	 */
	public static boolean explore(FileProcedure proc, File file, List<String> wildcards) {
		if(file.isDirectory()) {
			exploreDirectory(proc, file, makeFileFilter(wildcards));
		}else if(file.isFile()) {
			if( ! proc.execute(file) ) {
				LOGGER.log(Level.SEVERE,"explore...[file:{0}][FAIL]", file);
				return false;
			}
		}
		return true;
	}



	public static void main(String[] args) {
		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		final FileProcedure proc = new FileProcedure() {

			Random rnd = new Random();

			@Override
			public boolean execute(File file) {
				LOGGER.log(Level.INFO, "explore...[file:{0}]", file);
				return rnd.nextInt(20) != 0;
			}
		};
		explore(proc , new File(System.getProperty("user.home")), "*.java");
	}


}
