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
 * @see gui.GUI#addListeners(Controller)
 */
public class SavePlaylistMouseListener implements MouseListener
{
	private JFileChooser chooser; 
	private PlaylistSaver playlistSaver;
	
	/**
	 * Initialises the class, which allows for the saving of a playlist file.
	 * <p>
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 */
	public SavePlaylistMouseListener( Controller controller )
	{
		 chooser = new JFileChooser();
		 chooser.setFileFilter(new MVPFilter());
		 playlistSaver = new PlaylistSaver( controller );
	}

	/**
	 * Allows the user to hold the mouse button down and only loads a playlist when the mouse is released
	 */
	public void mouseReleased(MouseEvent event) 
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
	
	public void mouseClicked(MouseEvent event) {}

	public void mouseEntered(MouseEvent event) {}

	public void mouseExited(MouseEvent event) {}

	public void mousePressed(MouseEvent event) {}
}
