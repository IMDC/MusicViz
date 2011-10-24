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
 *
 */
public class LoadPlayListMouseListener  implements MouseListener
{
	private JFileChooser chooser; 
	private PlaylistLoader playlistLoader;
	
	public LoadPlayListMouseListener( Controller controller )
	{
		chooser = new JFileChooser(); 
		chooser.setFileFilter(new MVPFilter());
		playlistLoader = new PlaylistLoader( controller );
	}

	public void mouseClicked(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) 
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
}
