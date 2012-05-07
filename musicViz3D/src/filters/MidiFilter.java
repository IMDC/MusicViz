package filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import utilities.Utils;

/**
 * Filters all files but the MIDI files. This is used within a {@link javax.swing.JFileChooser}
 * to filter the types of files that are displayed.
 * 
 * @author Michael Pouris
 *
 */
public class MidiFilter extends FileFilter
{
	/**
	 * Tests of the file can be displayed to the user. Makes sure all files
	 * displayed are the MIDI files.
	 */
    public boolean accept(File f) 
    {
    	//Gets the extention of the file.
    	String extension = Utils.getExtension(f);
    	
    	// If there is a extension
    	if( extension != null )
    	{
    		//If there was an extension and has the ending of .mid
    		//then the file is not filtered, otherwise filter it.
    		if( extension.equalsIgnoreCase(Utils.midi) )
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	else	//if there is no extension, then it could be a directory
    	{
    		// Checks if the file is a directory
    		if( f.isDirectory() )
    		{
    			return true;
    		}
    		return false;
    	}
    	
    }

    /**
     * Displays to the user the types of files displayed.
     */
	public String getDescription() 
	{
		return "Midi Files";
	}

}
