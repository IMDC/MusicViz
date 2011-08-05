package filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import utilities.Utils;

public class MidiFilter extends FileFilter
{

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


	public String getDescription() 
	{
		return "Midi Files";
	}

}
