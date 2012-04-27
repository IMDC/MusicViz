package player;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import controller.Controller;
import gui.GUI;
import listeners.MidiMetaEventListener;
import listeners.PreprocessorPropertyChangeListener;
import player.receivers.BeatReceiver;
import player.receivers.ControlReceiver;
import player.receivers.InstrumentReceiver;
import player.receivers.PitchReceiver;
import processors.threads.LightThreadPreprocessor;
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
	private Controller controller = null;
	private LightThreadPreprocessor threadedPreprocessor = null;
	private Song currentSong=null;
	
	
	private BeatReceiver beatReceiver;
	private InstrumentReceiver instrumentReceiver;
	private ControlReceiver controlReceiver;
	private PitchReceiver pitchReceiver;
	private Transmitter instrumentTransmitter;
	private Transmitter beatTransmitter;
	private Transmitter controlTransmitter;
	private Transmitter pitchTransmitter;
	/**
	 * Gives the class the program controller for back end to front end communication.
	 * 
	 * @param c
	 * @throws MidiUnavailableException 
	 */
	public Player() throws MidiUnavailableException 
	{

		sequencer = MidiSystem.getSequencer();
		
		instrumentTransmitter = sequencer.getTransmitter();
		beatTransmitter = sequencer.getTransmitter();
		controlTransmitter = sequencer.getTransmitter();
		pitchTransmitter = sequencer.getTransmitter();
		
		sequencer.open();
	}
	
	public void init( Controller controller )
	{
		this.controller = controller;

		beatReceiver = new BeatReceiver(controller);
		instrumentReceiver = new InstrumentReceiver(controller);
		controlReceiver = new ControlReceiver(controller, sequencer);
		pitchReceiver = new PitchReceiver(controller);
		
		instrumentTransmitter.setReceiver(instrumentReceiver);
		beatTransmitter.setReceiver(beatReceiver);
		controlTransmitter.setReceiver(controlReceiver);
		pitchTransmitter.setReceiver(pitchReceiver);
		
		( (Thread) instrumentReceiver).start();
		( (Thread) beatReceiver).start();
		( (Thread) pitchReceiver).start();
		
		sequencer.addMetaEventListener( new MidiMetaEventListener(controller, sequencer ) );
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
		JFrame frame = new JFrame("Processing: " + song.getName());
		frame.setSize(400,120);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		JProgressBar progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBorder(BorderFactory.createTitledBorder("Progress"));
		frame.add(progressBar, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.SOUTH);
		
		threadedPreprocessor = new LightThreadPreprocessor(controller, pitchReceiver, sequence.getTracks(), sequencer.getTempoInBPM(), frame); 
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
	
	public AtomicBoolean getIsPlayingInstruments()
	{
		return this.instrumentReceiver.getIsPlayingInstruments();
	}
	
	public AtomicBoolean getIsPlayingBeats()
	{
		return this.beatReceiver.getIsPlayingBeats();
	}
	
	public void changeVolume( int channel, int volume )
	{
		Object[] transmitters = sequencer.getTransmitters().toArray();
		ShortMessage myMsg = new ShortMessage();
		
		for( int i = 0; i < transmitters.length; i++)
		{
			try
			{
				myMsg = new ShortMessage();
				myMsg.setMessage(ShortMessage.CONTROL_CHANGE,channel,7,volume);//sounds off
			   	((Transmitter)transmitters[i]).getReceiver().send(myMsg, -1);
			}
			catch (InvalidMidiDataException e)
			{
				System.err.println("Problem when clearing Controllers, turning off all notes and turning off sounds");
				System.exit(1);
			}
		}
	
	}
}
