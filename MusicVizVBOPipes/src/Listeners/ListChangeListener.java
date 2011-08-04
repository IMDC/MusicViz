package Listeners;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controller.Controller;
import GUI.GUI;
import Player.Player;
import Player.Song;

/**
 * This class listens for any change that occurs to the list. Considering each item 
 * in the DefaultListModel is a Song Object that contains and keeps track of its own index, everytime
 * a change is made, those numbers must be changed to correspond to the new position in the list.
 * This class is useful for the MidiEventListener class. This because, the list accepts multiples
 * of the same files, and without this class to update the internal index of each song, it would be impossible
 * to load the next appropriate song without screw ups.
 * 
 * IE imagine a list:
 * 	
 * 	FF7 Song1
 * 	Fade to black
 * 	Four Horsement
 *  FF7 Song 1 (Playing)
 *  Fade to black
 *  
 *  The Player keeps track of the loaded song and to load the next song, the index of the current song must be found.
 *  The problem occurrs when two of the same songs exsist. In the example above, if each song didnt keep track of it's own
 *  index, then the player wouldnt know if it should load the last song or the second. This class over comes that problem.
 *  
 */
public class ListChangeListener implements ListSelectionListener
{
	private JList list;
	private DefaultListModel dlm;
	private Controller controller;
	
	public ListChangeListener(Controller controller)
	{
		this.controller = controller;
	}
	
	/**
	 * Listens for any change to the list. Such as clicking, or adding files to the list.
	 */
	public void valueChanged(ListSelectionEvent e) 
	{
		list = (JList) e.getSource();
		dlm = (DefaultListModel) list.getModel();
		Player player = controller.getPlayer();
		GUI gui = controller.getGUI();
		Song currentSong = player.currentSongPlaying();
		Song tempSong;

		//updates each song's internal index.
		//This is needed for when the object is being dragged in the list and there are 
		//multiple of the same.
		for( int i = 0; i < dlm.size(); i++ )
		{				
			tempSong =( (Song) dlm.get(i) );
			
			// if the current song in the list is the song that is playing, then we have to change 
			//the  song variable in the player to correspond this change.
      	    if( tempSong.toString().equals(currentSong.toString()) )
      	    {
      	    	player.currentSongPlaying().setIndex(i);
      	    }
      	    tempSong.setIndex(i);
		}
		
		//This is for enabling and disabling the slider and toggle buttons when there is nothing in the play list
		if( dlm.size() == 0 )
		{
			//disables the slider 
			gui.disableSlider();
			gui.setCurrentValueForSlider(0);
			gui.getPlayPauseToggleButton().setText("Play ");	//tells the toggle button to be reset to play
			gui.getPlayPauseToggleButton().setSelected(false); //deselects the button because nothing is playing
		}
		else
		{
			gui.enableSlider();
		}
	}

}
