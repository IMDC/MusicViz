package Filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import Utilities.Utils;

/**
 * This is a filter that is used by the saving and loading playlist classes. This filters the files shown
 * in such a way that only file of extension .mvp show to the user. 
 * 
 * .mvp files are playlist files for this program only. It stands for music viz(ualization) playlist
 * 
 * @author Michael Pouris
 *
 */
public class MVPFilter extends FileFilter
{
	/**
	 * Checks to see if the file should be filtered or not for the JFileChoosers
	 */
    public boolean accept(File f) 
    {
    	//Gets the extention of the file.
    	String extension = Utils.getExtension(f);

    	//If there is an extention
    	if( extension != null )
    	{
    		//If there was an extension and has the ending of .mvp 
    		//then the file is not filtered, otherwise filter it.
    		if( extension.equalsIgnoreCase(Utils.mvp))
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	else //if there is no extension, then it could be a directory
    	{
    		if( f.isDirectory() )
    		{
    			return true;
    		}
    		return false;
    	}
 
    }

    /**
     * Description of the setting in the JFileChooser.
     */
	public String getDescription()
	{
		return ".mvp files";
	}

}
