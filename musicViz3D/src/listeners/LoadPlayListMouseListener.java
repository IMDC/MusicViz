package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;

import controller.Controller;
import filters.MVPFilter;
import loadersAndSavers.PlaylistLoader;

/**
 * This listener is currently added to the Menu item called Load Playlist. When it's clicked
 * the user can load a playlist file.
 * 
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class LoadPlayListMouseListener  implements MouseListener
{
	private JFileChooser chooser; 
	private PlaylistLoader playlistLoader;
	
	/**
	 * Initialises the class by creating a {@link JFileChooser}
	 * for the user to select a play list file. The play list
	 * files are MVP files. Please see {@link MVPFilter}.
	 * 
	 * @param controller
	 */
	public LoadPlayListMouseListener( Controller controller )
	{
		chooser = new JFileChooser(); 
		chooser.setFileFilter(new MVPFilter());
		playlistLoader = new PlaylistLoader( controller );
	}

	/**
	 * Invoked when the menu item in the GUI is clicked. 
	 * @see gui.GUI#addListeners(Controller)
	 */
	public void mouseReleased(MouseEvent event) 
	{
		File fileToOpen = null;
		
        //If the user selects a song then we grab the song
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
        { 
        	fileToOpen = chooser.getSelectedFile();
        }
		
        if( fileToOpen != null )
        {
        	playlistLoader.addPlaylist(fileToOpen);
        }
	}
	
	public void mouseClicked(MouseEvent event) {}

	public void mouseEntered(MouseEvent event) {}

	public void mouseExited(MouseEvent event) {}

	public void mousePressed(MouseEvent event) {}
}
