package listeners;

import java.awt.event.ActionListener;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.swing.DefaultListModel;

import controller.Controller;
import gui.GUI;
import player.Player;
import player.Song;

/**
 * Used to check for meta events occuring in the song. Meta events are events that are useful for the functionality
 * of the sequencer, they contain information such as EndOfSong events, lyrics, etc.
 * 
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 * @see http://www.midi.org/techspecs/midimessages.php
 */
public class MidiMetaEventListener implements MetaEventListener
{
	private Controller controller;
	
	/**
	 * Creates a new MIDI META event listener. Please look at the documentation
	 * in the Java API for further explanation
	 * <p>
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 */
	public MidiMetaEventListener( Controller controller )
	{
		this.controller = controller;
	}

	/**
	 * Method that's called when the sequencer comes accross a metaevent. This method
	 * checks for the end of song event. In that event, the next song is loaded and played.
	 */
	public void meta(MetaMessage event) 
	{
		int type = event.getType();
		Player player = controller.getPlayer();
		//Checks for end of song event.
		if( type == 47 )
		{	
			
			GUI gui = controller.getGUI();
			
			controller.getPlayer().allSoundOff();
			//controller.getPlayer().resetMaxMSPCommunication();
			gui.getVisualizer().resetVisualizer();

			//sets the first item as selected/
			gui.getPlaylist().setSelectedIndex(0);
			
			//Resets processors for new song
			//Gets the list the JList Displays and the name of the song playing atm along with its index
			DefaultListModel dlm = (DefaultListModel) gui.getPlaylist().getModel();

			//index of the current song in the list
			int index = player.currentSongPlaying().getIndex();
			
			//If there is a song that exsists in the next index, then its loaded.
			if( index + 1 <  dlm.getSize() )
			{
				gui.getPlaylist().setSelectedIndex( index + 1 );
				
				//finds the  current song in the list based on the song info in the player
				//and sets it to false, therefore giving the system the clue that it wont be played anymore
				((Song) dlm.get(index)).setIsPlaying(false);
      			
      			//Finds the next song in the list and tells it, that it will be playing
      			((Song) dlm.get(index + 1)).setIsPlaying(true);
      				
      			//finally the song is loaded
				player.openMidiFile( ((Song) dlm.get(index + 1)),true ); 
			}
			else
			{
				 //If there is nothing else in the list
				gui.setCurrentValueForSlider(0);
				gui.updateTimer("0:00");
				player.setPosition(0);
				
				//sets the first song in the list and loads it
				( (Song) dlm.get(index) ).setIsPlaying(false);
				( (Song) dlm.get(0) ).setIsPlaying(true);
				
				//Takes the  variable for the check box, and checks if 
				//it should loop
				ActionListener[] actionListeners = gui.getLoopCheckBox().getActionListeners();
				LoopingCheckBoxListener lcbl = (LoopingCheckBoxListener) actionListeners[0];
				
				player.openMidiFile( ( (Song) dlm.get(0) ),lcbl.getToLoop() ); 
				
				if( lcbl.getToLoop() )
				{
					//controller.play();
				}
				else
				{
					gui.setTextJToggleButton("Play ");
					gui.setSelectedJToggleButton(false);
				}
			}
			
			gui.getPlaylist().repaint();
		}
	}
}
