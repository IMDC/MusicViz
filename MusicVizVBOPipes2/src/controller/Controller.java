package controller;

import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JToggleButton;


import com.jogamp.opengl.util.Animator;

import gui.GUI;
import player.Player;
import player.Song;
import visualizer.Visualizer;

/**
 * The program follows a 3 tier/MVC programming model. This is the class that allows the program to achieve 
 * that programming model. The controller is what separates the front end from the back end but still allows communication between them
 * by acting as a mediator.
 * 
 * I try to limit what each component can access in another component through each method in this class. What ever, I want 
 * a object to access, the method to do so is placed in this class. IE to access the GUI, a class will only be able to use
 * methods in this class that allow GUI access.
 * 
 * @author Michael Pouris
 *
 */
public class Controller 
{
	private Player player; 
	private GUI gui;
	
	/**
	 * Creates a new GUI, FlashServer and MidiPlayer. The front end and back end respectively.
	 */
	public Controller()
	{		
		player = new Player(this);
		gui = new GUI(400,400, this);
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public GUI getGUI()
	{
		return gui;
	}

	/*public void setEnabledPlayerFrame(final boolean enabled )
	{
		gui.setEnabledPlayerFrame(enabled);
	}
	

	public JCheckBox getLoopCheckBox()
	{
		return gui.getLoopCheckBox();
	}
	

	public Visualizer getVisualizer()
	{
		return gui.getVisualizer();
	}
	

	public JToggleButton getPlayPauseToggleButton()
	{
		return gui.getPlayPauseToggleButton();
	}
	

	public JList getPlaylist()
	{
		return gui.getPlaylist();
	}
	
	public void setMaximumValueForSlider( final int maxTimeInSeconds )
	{
		gui.setMaximumValueForSlider(maxTimeInSeconds);
	}

	public void setCurrentValueForSlider( final int currentValue )
	{
		gui.setCurrentValueForSlider(currentValue);
	}
	
	public void enableSlider()
	{
		gui.enableSlider();
	}
	
	public void disableSlider()
	{
		gui.disableSlider();
	}
	

	public void updateTimer(String time)
	{
		gui.updateTimer(time);
	}
	

	public void updateTotalTime(String time )
	{
		gui.updateTotalTime(time);
	}
	

	public boolean startPreprocessing( Song file, boolean autoPlayAfterPreprocessing )
	{
		return player.openMidiFile(file,autoPlayAfterPreprocessing);
	}

	public void play()
	{
		player.play();
	}
	

	public void stop()
	{
		player.stop();
	}
	

	public void pause()
	{
		player.pause();
	}
	

	public void changeSongPosition( int position )
	{
		player.setPosition(position);
	}
	

	public Song getCurrentSongPlaying()
	{
		return player.currentSongPlaying();
	}
	
	public boolean isRunning()
	{
		return player.isRunning();
	}
	
	public long getCurrentTickPositionOfSong()
	{
		return player.getCurrentTickPositionOfSong();
	}*/
	
	/**
	 * The pre processor finds all the places in the song where the BPM changes. The changes are
	 * stored in the data structure returned by this method. IE, at tick 10 the BPM was changed to 150.
	 * 
	 * Where as in the other method, a data structure holds the tick and the tick converted to time as well.
	 * These 2 data structures ensure all the tools are there for MIDI's stupid timing calculations.
	 * 
	 * This data struture is a TreeMap because it orders the data based on its natural order, which is useful
	 * when searching where the tick we want to convert, fits in. Even though, the running time of get/delete/add 
	 * is O ( log n ), thus being too slow for real time processing, it is only used when the slider is changed.
	 * 
	 * @return
	 */
	public TreeMap<Long, Float> getTicksWithBPMChanges()
	{
		return player.getTicksWithBPMChanges();
	}
	
	/**
	 * After the preprocessing is done, the ticks and ticks converted to time are stored in the
	 * HashMap returned from this method. IE, tick 10, which is 7 seconds is where a BPM change
	 * happened. A tick is the key and the time is the value.
	 * 
	 * This data structure is stored in the preprocessor, which the player needs to get for the controller.
	 * 
	 * @return
	 */
	public HashMap<Long, Double> getTicksWithBPMChangesToTime()
	{
		return player.getTicksWithBPMChangesToTime();
	}
	
	/**
	 * When a beat change event is caught by the MidiMetaEventListener object, it sets
	 * the last time in seconds for the receiver so all time calculations can be done correctly.
	 * If you all forgot, MIDI is done by delta times based on the time where the last BPM change
	 * occurred and also the tick where the last BPM occurred.
	 * 
	 * @param time un-rouded time in seconds.
	 */
	public void setLastTimeInSeconds( double time )
	{
		player.setLastTimeInSeconds(time);
	}
	
	/**
	 * When a beat change event is caught by the MidiMetaEventListener object, it sets
	 * the last tick where the change occurred. It is set in the MIDI receiver. This is
	 * done so all time calculations can be done correctly. 
	 * If you all forgot, MIDI is done by delta times based on the time where the last BPM change
	 * occurred and also the tick where the last BPM occurred.
	 * 
	 * @param tick 
	 */
	public void setLastTick( long tick )
	{
		player.setLastTick(tick);
	}
	
	public void setColourSetToUse( int colourSet )
	{
		player.setColourSetToUse(colourSet);
	}
	
}
