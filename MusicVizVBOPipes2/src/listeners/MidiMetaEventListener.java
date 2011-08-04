package listeners;

import java.awt.event.ActionListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
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
 *
 */
public class MidiMetaEventListener implements MetaEventListener
{
	private Controller controller;
	private Sequencer sequencer;
	
	public MidiMetaEventListener( Controller controller, Sequencer sequencer )
	{
		this.controller = controller;
		this.sequencer = sequencer;
	}

	/**
	 * Method that's called when the sequencer comes accross a metaevent. This method
	 * checks for the end of song event. In that event, the next song is loaded and played.
	 */
	public void meta(MetaMessage event) 
	{
		int type = event.getType();
		//Checks for end of song event.
		if( type == 47 )
		{	
			Player player = controller.getPlayer();
			GUI gui = controller.getGUI();
			/*
			 * The next 2 for loops are actually here for a good reason. Throughout the development of this
			 *program, when a end of a song occurs, all the notes are turned off. Although, in some midi files,
			 *this doesn't occur which consequently leaves the last note constantly playing. So, to counter-act
			 *this problem, I send sound off events to all channels on all receivers that the sequencer has.
			 */
			Object[] transmitters = sequencer.getTransmitters().toArray();
			ShortMessage myMsg = new ShortMessage();
			for( int i = 0; i < transmitters.length; i++)
			{
				for( int j = 0 ; j < 16; j++ )
				{
					try
					{
					   	myMsg = new ShortMessage();
					   	myMsg.setMessage(ShortMessage.CONTROL_CHANGE,j,120,0);//sounds off
					   	((Transmitter)transmitters[i]).getReceiver().send(myMsg, -1);
					}
					catch (InvalidMidiDataException e)
					{
						System.err.println("Problem when clearing Controllers, turning off all notes and turning off sounds");
						System.exit(1);
					}
				}
			}
		
			player.stop();

			//sets the first item as selected/
			gui.getPlaylist().setSelectedIndex(0);
			
			//Resets processors for new song
			//controller.resetProcessors();
			//Gets the list the JList Displays and the name of the song playing atm along with its index
			DefaultListModel dlm = (DefaultListModel) gui.getPlaylist().getModel();

			//index of the current song in the list
			int index = player.currentSongPlaying().getIndex();
			
			//If there is a song that exsists in the next index, then its loaded.
			if( index + 1 <  dlm.getSize() )
			{
				//controller.setSelectedIndex(index + 1);
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
					gui.getPlayPauseToggleButton().setText("Play ");
					gui.getPlayPauseToggleButton().setSelected(false);
				}
			}
			
			gui.getPlaylist().repaint();
		}
		else if( type == 81 )
		{
			//Sets the last tick and time in seconds in the MidiNoteReceiver so the time calculations can be correct.
			controller.setLastTick(sequencer.getTickPosition());
			controller.setLastTimeInSeconds(sequencer.getMicrosecondPosition()/1000000.0);
		}
	}

}
