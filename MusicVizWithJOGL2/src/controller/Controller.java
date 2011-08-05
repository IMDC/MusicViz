package controller;

import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

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
	
	/**
	 * Returns the JFrame that contains the play/pause, stop buttons, slider, check box etc.
	 * In short, it is the MidiFilePlayer window. The main use for this right now is to 
	 * disable the controls while the preprocessor processes the Midi File.
	 * 
	 * @return
	 */
	public JFrame getPlayerFrame()
	{
		return gui.getPlayerFrame();
	}
	
	/**
	 * Returns the check box that is checked by the user if the user would like the 
	 * play list to loop.
	 * @return
	 */
	public JCheckBox getLoopCheckBox()
	{
		return gui.getLoopCheckBox();
	}
	
	/**
	 * Returns the Front end of the program AKA the OpenGL drawer. This is a thread which 
	 * draws the OpenGL visualisation.
	 * @return
	 */
	public Visualizer getVisualizer()
	{
		return gui.getVisualizer();
	}
	
	/**
	 * Returns the Play/Pause toggle button from the GUI.
	 * 
	 * @return
	 */
	public JToggleButton getPlayPauseToggleButton()
	{
		return gui.getPlayPauseToggleButton();
	}
	
	/**
	 * Returns the JList from the GUI. Which is the playlist.
	 * 
	 * @return
	 */
	public JList getPlaylist()
	{
		return gui.getPlaylist();
	}
	
	/**
	 * Returns the song index slider from the GUI.
	 * 
	 * @return
	 */
	public JSlider getSlider()
	{
		return gui.getSlider();
	}
	
	/**
	 * Updates the time code display, which is a text field, to display
	 * the current minute and second in the song. This will update the numerator
	 * in the timer. IE the 0:00 in the 0:00/3:34
	 * 
	 * @param time
	 */
	public void updateTimer(String time)
	{
		gui.updateTimer(time);
	}
	
	/**
	 * Sets the total length of the time of the song. Sets the
	 * total time in the timer. IE the 3:41 in the 0:00/3:41
	 * 
	 * @param time
	 */
	public void updateTotalTime(String time )
	{
		gui.updateTotalTime(time);
	}
	
	/**
	 * Tells the player to open the file and the parameter "autoPlayAfterPreprocessing"
	 * signals the Preprocessor thread to play the song right after the thread is finished
	 * or not.
	 * 
	 * @param file
	 * @param autoPlayAfterPreprocessing
	 * @return
	 */
	public boolean startPreprocessing( Song file, boolean autoPlayAfterPreprocessing )
	{
		return player.openMidiFile(file,autoPlayAfterPreprocessing);
	}
	
	/**
	 * Tells the midi player to play the song. 
	 */
	public void play()
	{
		player.play();
	}
	
	/**
	 * Tells the midi player to stop playing the song.
	 */
	public void stop()
	{
		player.stop();
	}
	
	/**
	 * Stops the song and keeps it at the current position. Hence Pause.
	 */
	public void pause()
	{
		player.pause();
	}
	
	/**
	 * Tells the midi player to move to the position given by the variable position.
	 * 
	 * @param position
	 */
	public void changeSongPosition( int position )
	{
		player.setPosition(position);
	}
	
	/**
	 * Returns the currently playing song as a File.
	 * 
	 * @return
	 */
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
	}
	
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
