package cli.explorer;

import java.io.File;

/**
 * The interface represents a procedure associated with an input file.
 * @author Arnaud Malapert</br> 
 * @since 5 nov. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public interface FileProcedure {
	
	/**
	 * Any procedure associated with a file.
	 * @return <code>true</code> if succeeds.
	 */
	boolean execute(File file);
}
