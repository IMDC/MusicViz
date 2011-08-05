package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;

import controller.Controller;
import filters.MVPFilter;
import loadersAndSavers.PlaylistSaver;

/**
 * This is the mouse listener for the menu item that saves the playlists.
 * 
 * @author Michael Pouris
 *
 */
public class SavePlaylistMouseListener implements MouseListener
{
	private JFileChooser chooser; 
	private PlaylistSaver playlistSaver;
	
	public SavePlaylistMouseListener( Controller controller )
	{
		 chooser = new JFileChooser();
		 chooser.setFileFilter(new MVPFilter());
		 playlistSaver = new PlaylistSaver( controller );
	}

	public void mouseClicked(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	/**
	 * Allows the user to hold the mouse button down and only loads a playlist when the mouse is released
	 */
	public void mouseReleased(MouseEvent arg0) 
	{
		File fileToSaveTo = null;
		
	    //If the user selects a song then we grab the song
	    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
	    { 
	      	fileToSaveTo = chooser.getSelectedFile();
	    }
		
	    if( fileToSaveTo != null )
	    {	
	       	playlistSaver.savePlaylist(fileToSaveTo);	        	
	    }
	}
}
