package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

import controller.Controller;
import filters.MidiFilter;
import gui.GUI;
import player.Player;
import player.Song;
import utilities.Utils;

/**
 * An implementation of selecting a single MIDI song to add to
 * the playlist. Please see {@link GUI} for this class's use.
 * <p>
 * When {@link #mouseReleased(MouseEvent)} is triggered on the related
 * JComponent, the window is displayed. It is hidden when the user closes it.
 * The window is never disposed of until the end of the program.
 * <p>
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class AddMidiSongMouseListener implements MouseListener
{
	private Controller controller;
	private  JFileChooser chooser;
	
	/**
	 * Creates a file chooser without displaying it.
	 * 
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 */
	public AddMidiSongMouseListener( Controller controller )
	{
		chooser = new JFileChooser(); 
		chooser.setFileFilter( new MidiFilter() );
		this.controller = controller;
	}
	
	/**
	 * When the item is clicked and the mouse is released, this method asks the user
	 * to search through their file system for a MIDI file to load into the program.
	 */
	public void mouseReleased(MouseEvent event)
	{
		Song song = null;
		DefaultListModel listModel;
		boolean isPlayable;
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		
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
        	listModel = (DefaultListModel) gui.getPlaylist().getModel();
        	//if the size of the list is zero, meaning there is nothing in the playlist,
        	//the song is loaded into the player. If the song is a valid song then its added to the playlist.
        	//If its not a valid song then its not loaded into the player and not added to the list.
	        if( listModel.getSize() == 0 )
	        {
	        	song.setIsPlaying(true);
	        	//Once the preprocessing is done, a boolean is returned indicating the success.
	        	isPlayable=player.openMidiFile(song,false);
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
	        }
 
        }
	}
	
	public void mouseClicked(MouseEvent arg0){}

	public void mouseEntered(MouseEvent arg0){}

	public void mouseExited(MouseEvent arg0){}

	public void mousePressed(MouseEvent arg0){}
}
