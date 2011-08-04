package handlers;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import controller.Controller;
import listeners.ListMouseListener;
import loadersAndSavers.PlaylistLoader;
import player.Player;
import player.Song;
import utilities.Utils;

/**
 * This custom class is an extension of the default transfer class that is used when dragging file/text 
 * or any object into a table or list. The class is programmed to only accept files and even further, only accept
 * Song Objects. Otherwise, the object dragged into the list will be rejected. I like to look at this class as 
 * the mediator between the OS and my program. When objects go from the OS to this program, this class checks
 * if the object is suitable.
 * 
 * @author Michael Pouris
 *
 */
public class ListTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 5654515524099472532L;
	private Controller controller;
	private PlaylistLoader playlistLoader;
	
	public ListTransferHandler(Controller controller)
	{
		this.controller = controller;
		playlistLoader = new PlaylistLoader(controller);
	}
	
	/**
	 * Checks if the file/files being dragged over the list is/are supported by the program.
	 */
	public boolean canImport(TransferHandler.TransferSupport info)
	{
		Transferable transferable = info.getTransferable();
		
		//If the transferable object can be returned as a File.
		if( transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) )
		{
			return true;
		}
		//If the transferable object can be returned as a custom class called Song.
		else if( transferable.isDataFlavorSupported(Song.supportedFlavors[0]))
		{
			return true;
		}
		//If the transferable object can't be supported.
		return false;
	}
	
	/**
	 * Creates a transferable object when the selected object, is dragged out of the list. Allows the JVM
	 * to pass information between another JVM and/or the OS.
	 */
    protected Transferable createTransferable(JComponent c)
    {
    	//Gets the list where the song objects are.
        JList list = (JList)c;
        //Right now, I only want the user to be able to drag one object around at a time.
        Object value = list.getSelectedValue();
        //The song is a transferable object, so the value is casted straight to a Transferable object.
        //So this object is a song, this song is a transferable object. Therefore this object is a transferable object.
        Transferable song = (Transferable) value;
        return song;
    }
    
    /**
     * Tells the List that the supported actions are movements, at least I think.
     */
    public int getSourceActions(JComponent c) 
    {
        return TransferHandler.MOVE;
    }
    
    /**
     * After the object is verified as a song or file, this method does what it says,
     * it imports the data. If there are multiple midi songs and play lists, the midi songs
     * are added first and then the playlists are added.
     */
    public boolean importData(TransferHandler.TransferSupport info) 
    {
        if (!info.isDrop())
        {
            return false;
        }
        Player player = controller.getPlayer();
        //The transferable data, is always a list when imported from the operating system, that data is thrown into this list for use
    	List<?> mlist;	
    	Song song;
    	//Whether or not the song is playable.
    	boolean isPlayable;
        JList list = (JList)info.getComponent();
        DefaultListModel listModel = (DefaultListModel)list.getModel();
        JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
        //The index where the data was dropped in the list.
        int index = dl.getIndex();
        //The transferable data
        Transferable t = info.getTransferable();
        //A generic object for general use.
        Object obj = new Object();
        //If the file is an playlist, then they are thrown into this arraylist for addition later.
        ArrayList<File> playlists = new ArrayList<File>();
        
        try
        {
	        //dragging file from OS
	        if( t.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) )
	        {
	        		//Casts the objects to a list.
					obj = t.getTransferData(DataFlavor.javaFileListFlavor);
					mlist = (List<?>) obj;
					
					//Extension of the file.
					String extension;
					//The progress of adding the files.
					boolean isDone;
					
					for( int i = 0; i < mlist.size(); i++)
					{
						song = new Song( (File) mlist.get(i) );
						extension = Utils.getExtension( song );
						
						if( listModel.getSize() ==0 && extension.equalsIgnoreCase(Utils.midi) )
						{
							song.setIsPlaying(true);
							isPlayable = player.openMidiFile(song,false);
						}
						else if( listModel.getSize() != 0 && extension.equalsIgnoreCase(Utils.midi)) 
						{
							isPlayable = true;
						}
						else if( extension.equalsIgnoreCase(Utils.mvp) ) 
						{
							isPlayable = false;
							playlists.add(song);
							index--; //A VITAL LINE! BECAUSE IF FILE IS A PLAYLIST, THEN NO FILE IS ADDED TO THE 
									 //THE CURRENT INDEX IN LIST, THERFORE THROWING OFF THE REST OF THE SONGS.
									 //IE: S1: added at 0, S2: added at 1, S3 added at 2, MVP added at 3, S4 added at 4
									 // The problem lies when the MVP is skipped and we tell the program to add
							         // add at index 4 when index 3 doesn't exist. Therefore this is needed.
						}
						else
						{
							isPlayable = false;
							index--;
						}
						if (isPlayable) 
						{
							listModel.add(index+i, song);  
						}  
					}
					//Completed adding all midi songs. Now must add playlists if there are any.
					isDone = true;
					
					//adds playlists last
					for( int i = 0; i < playlists.size(); i++ )
					{
						isDone = playlistLoader.addPlaylist( playlists.get(i) );
					}
					list.repaint();
			        return isDone;
					
	        }
	        else if( t.isDataFlavorSupported( Song.supportedFlavors[0] ) )
	        {//for dragging with in list
	        	    obj = t.getTransferData(Song.supportedFlavors[0]);
	        	    mlist = (List<?>) obj;
	        	    song = (Song) mlist.get(0);
	        	    
	    			MouseListener[] m = list.getMouseListeners();
	    			ListMouseListener lml = (ListMouseListener) m[2];
	    			int oldIndex  = lml.getFirstIndex();
	        	    
	    			if( index > oldIndex )
	    			{
	    				index--;
	    			}
	 
	    			listModel.remove(oldIndex);
	   	            listModel.add(index, song);       
	   	            
	   	            return true;
	        }
	        
	        return false;

        }
        catch(Exception e)
        {
        	return false;
        }
    }
}
