package player;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import controller.Controller;
import gui.GUI;
import listeners.MidiMetaEventListener;
import listeners.PreprocessorPropertyChangeListener;
import processors.threads.ThreadPreprocessor;
import utilities.Utils;

/**
 * This class acts as a simple CD or MP3 controls. It has functionality such as, choosing songs, playing selected song,
 * stopping the currently playing song and jumping to different parts on the fly.
 * 
 * @author Michael Pouris
 *
 */
public class Player 
{
	private Sequence sequence = null;
	private Sequencer sequencer = null;
	private Transmitter transmitter = null;
	private Receiver receiver = null;
	private Controller controller = null;
	private ThreadPreprocessor threadedPreprocessor = null;
	private Song currentSong=null;
	int colourSetToUse;
	JFrame frame;
	//Synthesizer synthesizer;
	//Receiver synthReceiver;
	
	/**
	 * Gives the class the program controller for back end to front end communication.
	 * 
	 * @param c
	 * @throws MidiUnavailableException 
	 * @throws MidiUnavailableException 
	 */
	public Player(Controller c) 
	{
		controller = c;

		try 
		{
			sequencer = MidiSystem.getSequencer();
			transmitter = sequencer.getTransmitter();
			receiver = new MidiNoteReceiver(controller,sequencer);
			transmitter.setReceiver(receiver);
			sequencer.addMetaEventListener( new MidiMetaEventListener(controller, sequencer ) );
			colourSetToUse = 0;
			/*synthesizer = MidiSystem.getSynthesizer(false);
			synthReceiver = synthesizer.getReceiver();
			transmitter = sequencer.getTransmitter();
			transmitter.setReceiver(synthReceiver);
			synthesizer.open();
			Soundbank soundBank = MidiSystem.getSoundbank(new File("C:\\Program Files (x86)\\Java\\jre6\\lib\\audio\\soundbank-deluxe.gm"));
			synthesizer.loadAllInstruments(soundBank);
			System.out.println( synthesizer.getDefaultSoundbank() );*/
			
			sequencer.open();
		} 
		catch (MidiUnavailableException e) 
		{
			JOptionPane.showMessageDialog(null, "Your system's midi player cannot be accessed.\n" +
												"Please close any programs using the midi player\n" +
												"and restart the program. If you are in Windows7, " +
												"please plug in speakers or headphones because windows "+ 
												"thinks there is no sound card on your machine until you do so.");
			System.exit(0);
		} 
	}
	
	/**
	 * Any time a song is loaded into the program, whether from dragging to the list or adding from the menu, 
	 * the song must be preprocessed before it can be played. The same thing goes for clicking on a song 
	 * in the play list or for automatic sequential playing of the songs in the list. Therefore, every song 
	 * goes through this method to be preprocessed.
	 * 
	 * @param song The song to be played.
	 * @param autoPlayAfterPreprocessing Whether to play the song automatically after preprocessing is done or not.
	 * @return
	 */
	public boolean openMidiFile(Song song, boolean autoPlayAfterPreprocessing)
	{
		GUI gui = controller.getGUI();
		if( sequencer != null )
		{
			if(sequencer.isRunning() )
			{
				sequencer.stop();
			}
		}
		
		try 
		{
			sequence = MidiSystem.getSequence( song );
			sequencer.setSequence(sequence);	
			startProgressBarEnabledPreprocessor(song,autoPlayAfterPreprocessing);
			//controller.getSlider().setMaximum( getSongLengthInSeconds() );
			gui.setMaximumValueForSlider( getSongLengthInSeconds());
			setTotalTime();
			currentSong = song;
			currentSong.setIsPlaying(true);
			
			return true;
		} 
		catch (InvalidMidiDataException e) 
		{
			JOptionPane.showMessageDialog(null,"This is an invalid file format.\n"+
												"Please choose a valid file.");
			return false;
		} 
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"Could not read the file.\n"+
												"There is an IO Problem.");
			return false;
		}
	}
	
	/**
	 * This method sets up the JProgress bar's window and shows it while the song is processed. It also
	 * sets up the PreprocessorThread. The window encompasses a JprogressBar for keeping track of the progress and
	 * a JButton for closing the window manually after the preprocessor is finished. The preprocessor is a separate
	 * thread, which sends progress change events to the JProgressBar.
	 * 
	 * @param song
	 * @param autoPlayAfterPreprocessing
	 */
	private void startProgressBarEnabledPreprocessor(Song song,boolean autoPlayAfterPreprocessing)
	{
		if(frame != null )
		{
			frame.dispose();
		}
		//Set up the main window where the JProgressBar and JButton is placed.
		//The x button (top right in Windows OS and left in MAC OS X)
		//is set to be disabled. Therefore, forcing the user to let
		//the preprocessor finish.
		//JFrame frame = new JFrame("Processing: " + song.getName());
		frame = new JFrame("Processing: " + song.getName());
		frame.setSize(400,120);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		
		/**
		 * Main and only use of this class is to close the JFrame that contains
		 * the progress bar. The JButton called closeButton has an instance of this
		 * class as its actionListener.
		 * 
		 * @author Michael Pouris
		 *
		 */
		class CloseButtonActionListener implements ActionListener 
		{
			JFrame progressFrame;
			public CloseButtonActionListener(JFrame progressFrame)
			{
				this.progressFrame = progressFrame;
			}
			public void actionPerformed(ActionEvent event)
			{
				progressFrame.dispose();
			}	
		}
		JButton closeButton = new JButton("Close");
		closeButton.setEnabled(false);
		closeButton.addActionListener(new CloseButtonActionListener(frame));
		
		JPanel panel = new JPanel();
		panel.add(closeButton);
		JProgressBar progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBorder(BorderFactory.createTitledBorder("Progress"));
		frame.add(progressBar, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.SOUTH);
		
		closeButton.revalidate();
		
		/*
		 * Create a new ThreadedPreprocessor and give it all the information it needs to process the Midi file.
		 * I also give the SwingWorker a property change listener which will listen to property change events.
		 * Property change events are triggered by the build in method in the SwingWorker called "setProgress()" method
		 */
		threadedPreprocessor = new ThreadPreprocessor(controller,(MidiNoteReceiver) receiver, sequence.getTracks(), sequence, sequencer.getTempoInBPM(),closeButton,colourSetToUse);
		threadedPreprocessor.addPropertyChangeListener(new PreprocessorPropertyChangeListener(progressBar));
		threadedPreprocessor.autoPlayAfterPreprocessing = autoPlayAfterPreprocessing;
		threadedPreprocessor.execute();
	}
	
	/**
	 * An internal method that calculates the time in a SMPTE format IE( 3:54 for 3 minutes and 54 seconds) and sets
	 * the GUI text box to show the calculated time.
	 */
	private void setTotalTime( )
	{
		GUI gui = controller.getGUI();
	     double seconds = (double)sequencer.getMicrosecondLength()/(double)1000000;
	     int min = (int)seconds/60;
	     int secondsOfAMin = (int)seconds % 60;
	     String secondsOfAMinStr = "";
	     secondsOfAMinStr += secondsOfAMin;

	     if(secondsOfAMinStr.length() != 2)
	     {
	    	 gui.updateTotalTime(min+":"+"0"+secondsOfAMin);
	     }
	     else
	     {
	    	 gui.updateTotalTime(min+":"+secondsOfAMin);
	     }

	     gui.updateTimer("0:00");
	     gui.enableSlider();
	}
	
	/**
	 * Highly self explanatory. This should not need my 500000000+ lines of comments
	 * to explain like the preprocessor and other parts of this program did.
	 */
	public void play()
	{
		GUI gui = controller.getGUI();
		try		
		{
			//if statement ensures something is in the play list which means soemthing is loaded into the
			//player and ensures that the player isn't aleady running.
			if( !sequencer.isRunning() && gui.getPlaylist().getModel().getSize() != 0)
			{
				sequencer.start();
			}
		}
		catch(NullPointerException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened in order to be played.");
		}
		catch(IllegalStateException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened in order to be played.");
		}
	}
	
	/**
	 * Another highly self explanatory method. Just like above, it doesn't need
	 * 5348239874239+ lines of comments to explain. Just something small. When this method
	 * is called, I want it to act like a real stop button, which means reset to the beginning. Otherwise,
	 * telling the sequencer to stop keeps the song at the same position.
	 */
	public void stop()
	{
		GUI gui = controller.getGUI();
		try
		{
			if( sequencer.isRunning() )
			{
				sequencer.stop();
				sequencer.setMicrosecondPosition(0);
				gui.setCurrentValueForSlider(0);
				gui.updateTimer("0:00");
			}
		}
		catch(NullPointerException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened in order to be stopped.");
		}	
		catch(IllegalStateException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened in order to be stopped.");
		}		
	}
	
	/**
	 * If you do not get it by now, you don't know code. This is a 3rd method that needs no explaining,
	 * which is very nice considering the other methods have enough comments to rival the code in the windows
	 * OS.
	 */
	public void pause()
	{
		try
		{
			if( sequencer.isRunning() )
			{
				sequencer.stop();
			}
		}
		catch(NullPointerException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened in order to be stopped.");	
		}	
		catch(IllegalStateException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened in order to be stopped.");
		}	
	}
	
	/**
	 * Sets the position of the song in seconds.
	 * @param positionInSeconds
	 */
	public void setPosition( int positionInSeconds )
	{
		try
		{
			sequencer.setMicrosecondPosition( Utils.secondsToMicroseconds(positionInSeconds) );
		}
		catch(NullPointerException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened.");
		}	
		catch(IllegalStateException e){
			JOptionPane.showMessageDialog(null,"A midi file must be opened.");
		}	
	}
	
	/**
	 * Returns the current song playing/loaded into the midi player/sequencer.
	 * @return
	 */
	public Song currentSongPlaying()
	{
		return currentSong;
	}
	
	/**
	 * Returns the lengh of the song in microseconds.
	 * @return
	 */
	private int getSongLengthInSeconds()
	{
		return Utils.microSecondsToSeconds( sequencer.getMicrosecondLength() );
	}
	
	/**
	 * Returns whether the song is playing or not.
	 * @return
	 */
	public boolean isRunning()
	{
		return sequencer.isRunning();
	}
	
	/**
	 * Returns the current position in ticks of where we are in the song. This is used by the
	 * SlideMouseListener object to find the beat interval the current tick belongs in.
	 * @return
	 */
	public long getCurrentTickPositionOfSong()
	{
		return sequencer.getTickPosition();
	}
	
	/**
	 * Returns a TreeMap (guaranteed O( log n ) running time for get()/remove()/add() ). The TreeMap
	 * holds Midi Ticks as a key and BPM as a value. The key is where the BPM changed and the value
	 * is the BPM that the song was changed to at that tick.
	 * @return
	 */
	public TreeMap<Long, Float> getTicksWithBPMChanges()
	{
		return threadedPreprocessor.getTicksWithBPMChanges();
	}
	
	/**
	 * The HashMap has Midi ticks as keys and the ticks turned to time as values. The ticks
	 * are where the BPM changed and the values are those ticks converted to time.
	 * @return
	 */
	public HashMap<Long, Double> getTicksWithBPMChangesToTime()
	{
		return threadedPreprocessor.getTicksWithBPMChangesToTime();
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
		MidiNoteReceiver temp = (MidiNoteReceiver) receiver;
		temp.setLastTick(tick);
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
		MidiNoteReceiver temp = (MidiNoteReceiver) receiver;
		temp.setLastTimeInSeconds(time);
	}
	
	public void setColourSetToUse( int colourSet )
	{
		colourSetToUse = colourSet;
	}
}
