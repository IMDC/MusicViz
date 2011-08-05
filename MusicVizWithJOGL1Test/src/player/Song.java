package player;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;

/**
 * This class is an extension of the File class. In order for the songs 
 * to work correctly in the play list, such as moving around and adding more than one of
 * the same song, the existing FIle class had to be modified to take care of its own index
 * and whether it was playing.
 * 
 * @author Michael Pouris
 *
 */
public class Song extends File implements Transferable
{
	private static final long serialVersionUID = 9119584661591527826L;
	private boolean isPlaying;
	private int index;
	
	//For making this class transferable
	private static DataFlavor songFlavor = new DataFlavor(Song.class,"Personal Song Object");
	public static DataFlavor[] supportedFlavors = { songFlavor };
 	
	public Song(String pathname) 
	{
		super(pathname);
		this.isPlaying = false;
		index = 0;
	}
	
	public Song( File song )
	{
		super( song.toString() );
		this.isPlaying = false;
		index = 0;
	}
	
	public Song(String pathname, boolean isPlaying)
	{
		super(pathname);
		this.isPlaying = isPlaying;
		index = 0;
	}

	/**
	 * Tells the song it is playing.
	 * @param isPlaying
	 */
	public void setIsPlaying( boolean isPlaying )
	{
		this.isPlaying = isPlaying;
	}
	
	/**
	 * Returns true if the song is playing, false if not.
	 * @return
	 */
	public boolean isPlaying()
	{
		return isPlaying;
	}
	
	/**
	 * Returns the pathname of the file and the status of the song.
	 */
	public String toString()
	{
		return super.toString() + "?" + isPlaying;
	}
	
	/**
	 * Returns the index of the song in the list.
	 * @return
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Sets the index of the song in the list.
	 * @param index
	 */
	public void setIndex( int index )
	{
		this.index = index;
	}

	/**
	 * Returns the song as the specified object given by the variable flavor.
	 * If the user wants the song returned as song, that can happen, otherwise an exception is thrown.
	 * 
	 */
	public Object getTransferData(DataFlavor flavor)
	throws UnsupportedFlavorException
	{
		ArrayList<Song>list = new ArrayList<Song>();
		
		if( flavor.equals(songFlavor) )
		{
			list.add(this);
			return list;
			//return this;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}

	/**
	 * Tells the programmer what kind of objects this Song object can be returned as.
	 */
	public DataFlavor[] getTransferDataFlavors() 
	{
		return supportedFlavors;
	}

	/**
	 * Checks if the given flavor in variable flavor is supported.
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) 
	{
		if( flavor.equals(songFlavor) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
