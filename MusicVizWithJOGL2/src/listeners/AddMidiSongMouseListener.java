package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

import controller.Controller;
import filters.MidiFilter;
import player.Song;
import utilities.Utils;

/**
 * Class acts as a listener for the menu item: openMidiItem.
 * 
 * @author Michael Pouris
 *
 */
public class AddMidiSongMouseListener implements MouseListener
{
	private Controller controller;
	private  JFileChooser chooser;
	
	public AddMidiSongMouseListener( Controller controller )
	{
		chooser = new JFileChooser(); 
		chooser.setFileFilter( new MidiFilter() );
		this.controller = controller;
	}
	
	public void mouseReleased(MouseEvent event)
	{
		Song song = null;
		DefaultListModel listModel;
		boolean isPlayable;
		
		//Opens a file chooser so the user can select a song
        //JFileChooser chooser = new JFileChooser(); 
        //If the user selects a song then we grab the song
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
        { 
        	song = new Song( chooser.getSelectedFile().toString() );
        }
        
        // if a song was chosen
        if( song != null )
        {
        	listModel = (DefaultListModel) controller.getPlaylist().getModel();
        	//if the size of the list is zero, meaning there is nothing in the playlist,
        	//the song is loaded into the player. If the song is a valid song then its added to the playlist.
        	//If its not a valid song then its not loaded into the player and not added to the list.
	        if( listModel.getSize() == 0 )
	        {
	        	song.setIsPlaying(true);
	        	//Once the preprocessing is done, a boolean is returned indicating the success.
	        	isPlayable=controller.startPreprocessing(song,false);
	        }
	        else
	        {
	        	//Checks if the song is a midi by the extension of the file.
	        	String extension = Utils.getExtension(song);
	        	isPlayable = extension.equalsIgnoreCase(Utils.midi) ? true : false; 
	        }
	        if( isPlayable )
	        {
	        	listModel.addElement(song);
	        	//controller.sendMessage("songEvent " + song.getName() );
	        }
 
        }
	}
	
	public void mouseClicked(MouseEvent arg0){}

	public void mouseEntered(MouseEvent arg0){}

	public void mouseExited(MouseEvent arg0){}

	public void mousePressed(MouseEvent arg0){}
}
