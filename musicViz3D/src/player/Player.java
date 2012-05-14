package player;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.swing.JOptionPane;

import controller.Controller;
import gui.GUI;
import listeners.MidiMetaEventListener;
import player.receivers.BeatReceiver;
import player.receivers.ControlReceiver;
import player.receivers.InstrumentReceiver;
import player.receivers.MaxMSPCommunication;
import player.receivers.PitchReceiver;
import processors.threads.LightThreadPreprocessor;
import utilities.Utils;

/**
 * This class represents the {@link MidiSystem}'s {@link Sequencer}.
 * <p>
 * This class wraps {@link Sequencer}'s calls and extends it. The MIDI standard
 * allows for multiple objects or devices to receive MIDI messages in real time.
 * These receivers are {@link Receiver}, therefore any object that is a receiver
 * must implement the interface.
 * <p>
 * There are 5 receivers: {@link BeatReceiver}, {@link InstrumentReceiver},
 * {@link ControlReceiver}, {@link PitchReceiver} and {@link MaxMSPCommunication}.
 * Please see these classes for documentation and explanation.
 * 
 * @author Michael Pouris
 *
 */
public class Player 
{
	private Sequence sequence;
	private Sequencer sequencer;
	private Controller controller;
	private LightThreadPreprocessor threadedPreprocessor;
	private Song currentSong;
	
	private BeatReceiver beatReceiver;
	private InstrumentReceiver instrumentReceiver;
	private ControlReceiver controlReceiver;
	private PitchReceiver pitchReceiver;
	private Transmitter instrumentTransmitter;
	private Transmitter beatTransmitter;
	private Transmitter controlTransmitter;
	private Transmitter pitchTransmitter;
	private Transmitter synthTransmitter;
	
	private MaxMSPCommunication maxReceiver;
	private Transmitter maxTransmitter;
	
	private Synthesizer synthesizer;
	
	/**
	 * Constructs a new {@link Sequencer} object and a new sound {@link Synthesizer}.
	 * <p>
	 * In order to intercept {@link MidiMessage}s in real time, {@link Transmitter}'s
	 * are retrieved and linked to {@link Receiver}s. See {@link #init(Controller)} 
	 * for the initialisation of the receivers.
	 * 
	 * @throws MidiUnavailableException the MIDI seqencer is not available on your system
	 */
	public Player() throws MidiUnavailableException 
	{
		//Get the MIDI sequencer without it attached to the default sound synthesizer
		sequencer = MidiSystem.getSequencer(false);
		synthesizer = MidiSystem.getSynthesizer();
		
		//Get the synthesizer and load a default sound bank from file
		try 
		{
			
			synthesizer.loadAllInstruments(MidiSystem.getSoundbank(new File("soundbank-deluxe.gm")));
		} 
		catch (InvalidMidiDataException e)
		{
			JOptionPane.showMessageDialog(null, "Cannot load soundbank because of a problem with the soundbank.\nThe program will run but there might not be sound.\n This is a normal occurance with Java 7. The program will function perfectly");
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Cannot load soundbank because the file cannot be open or read.\nThe program will run but there might not be sound.\n This is a normal occurance with Java 7. The program will function perfectly");
		}
		
		//Get 5 transmitters from the sequencer. These will transmit
		//MIDI messages in real-time as the sequencer plays them
		instrumentTransmitter = sequencer.getTransmitter();
		beatTransmitter = sequencer.getTransmitter();
		controlTransmitter = sequencer.getTransmitter();
		pitchTransmitter = sequencer.getTransmitter();
		synthTransmitter = sequencer.getTransmitter();
		maxTransmitter = sequencer.getTransmitter();
		
		synthesizer.open();
		sequencer.open();
	}
	
	/**
	 * Initialises the {@link Receiver}s. These receivers intercept messages played
	 * in real time by the sequencer.
	 * <p>
	 * Each receiver runs on the Java Sound Thread, therefore the calculations add
	 * a computational load to the sound thread. This can slow down the sequencer
	 * and cause bugs. Therefore, they are created as threads. Please see
	 * the classes for each receiver for a thorough explanation.
	 * 
	 * @param controller
	 * @throws MidiUnavailableException
	 * @throws UnknownHostException
	 */
	public void init( Controller controller ) throws MidiUnavailableException, UnknownHostException
	{
		this.controller = controller;

		//Create new receiver objects, these are also threads
		beatReceiver = new BeatReceiver(controller);
		instrumentReceiver = new InstrumentReceiver(controller);
		controlReceiver = new ControlReceiver(controller, sequencer);
		pitchReceiver = new PitchReceiver(controller);
		maxReceiver = new MaxMSPCommunication();
		
		//Set the transmitters to transmit to the receivers
		instrumentTransmitter.setReceiver(instrumentReceiver);
		beatTransmitter.setReceiver(beatReceiver);
		controlTransmitter.setReceiver(controlReceiver);
		pitchTransmitter.setReceiver(pitchReceiver);
		synthTransmitter.setReceiver(synthesizer.getReceiver());
		maxTransmitter.setReceiver(maxReceiver);
		
		//the thread method processes the midi notes as to note overload the sequencer.
		instrumentReceiver.setDaemon(true);
		( (Thread) instrumentReceiver).start();
		
		beatReceiver.setDaemon(true);
		( (Thread) beatReceiver).start();
		
		pitchReceiver.setDaemon(true);
		( (Thread) pitchReceiver).start();
		
		maxReceiver.setDaemon(true);
		( (Thread) maxReceiver ).start();
		
		controlReceiver.setDaemon(true);
		( (Thread) controlReceiver ).start();
		
		sequencer.addMetaEventListener( new MidiMetaEventListener(controller ));
	}
	
	/**
	 * This method loads a song into the midi player.
	 * <p>
	 * Considering this class has more functionality than the basic play,
	 * pause and stop functions, this method must set the maximum time
	 * in the GUI.
	 * 
	 * @param song
	 * @param autoPlayAfterPreprocessing
	 * @return
	 */
	public synchronized boolean openMidiFile(Song song, boolean autoPlayAfterPreprocessing)
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
	 * Start playing the MIDI song.
	 */
	public synchronized void play()
	{
		GUI gui = controller.getGUI();
		try		
		{
			//Only play if there is something in the playlist to play
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
	 * Stop playing the MIDI sequence. Reset the sequencer to start
	 * from the beginning of the song.
	 * <p>
	 * This class has some more functionality, hence it communicates with
	 * MAX/MSP through a socket, therefore a stop signal needs to be sent through.
	 * This is done by calling {@link #resetMaxMSPCommunication()}.
	 * <p>
	 * It is noted that the MIDI sequencer in Java is not perfect and as 
	 * a consequence, sound can keep playing even when the sequencer is not running. 
	 * To solve this, turn off all sounds in the channels. {@link #allSoundOff()}.
	 */
	public synchronized void stop()
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
				this.allSoundOff();
				this.resetMaxMSPCommunication();
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
	 * Pauses the MIDI sequencer and keeps the sequencer at the same position.
	 * <p>
	 * See the documentation for {@link #stop()}, the method is the same such that
	 * a off signal must be sent to the MAX/MSP program.
	 */
	public synchronized void pause()
	{
		try
		{
			if( sequencer.isRunning() )
			{
				sequencer.stop();
				this.allSoundOff();
				this.resetMaxMSPCommunication();
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
	 * Updates the sequencer to play from the given time in seconds.
	 * 
	 * @param positionInSeconds move the sequencer to the time provided in seconds
	 */
	public synchronized void setPosition( int positionInSeconds )
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
	 * Returns the current song played by the sequencer.
	 * 
	 * @return the song played the sequencer.
	 */
	public synchronized Song currentSongPlaying()
	{
		return currentSong;
	}
	
	/**
	 * Returns whether the sequencer is running or not.
	 * 
	 * @return true if the sequencer is running and false if not
	 */
	public synchronized boolean isRunning()
	{
		return sequencer.isRunning();
	}
	
	/**
	 * Changes the volume for the channel specified to the volume specified.
	 * <p>
	 * There are 16 channels in MIDI. Please see the MIDI standard
	 * @param channel
	 * @param volume
	 */
	public synchronized void changeVolume( int channel, int volume )
	{
		synthesizer.getChannels()[channel].controlChange(7, volume);
	}
	
	/**
	 * Turns off all sounds for all channels.
	 */
	public synchronized void allSoundOff()
	{
		for( int i = 0; i < 16; i++ )
		{
			synthesizer.getChannels()[i].controlChange(120, 0);
		}
	}
	
	/**
	 * Please see {@link BeatReceiver} for an explanation.
	 * @return
	 */
	public AtomicBoolean getIsPlayingInstruments()
	{
		return this.instrumentReceiver.getIsPlayingInstruments();
	}
	
	/**
	 * Please see {@link BeatReceiver} for an explanation.
	 * @return
	 */
	public AtomicBoolean getIsPlayingBeats()
	{
		return this.beatReceiver.getIsPlayingBeats();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 */
	public void disableMaxMSPCommunication()
	{
		this.maxReceiver.disableMaxMSPCommunication();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 * @throws SocketException 
	 */
	public void enableMaxMSPCommunication() throws SocketException
	{
		this.maxReceiver.enableMaxMSPCommunication();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 */
	public void enableMaxMSPInstrumentCommunication()
	{
		this.maxReceiver.enableInstrumentCommunication();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 */
	public void disableMaxMSPInstrumentCommunication()
	{
		this.maxReceiver.disableInstrumentCommunication();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 */
	public void enableMaxMSPBeatCommunication()
	{
		this.maxReceiver.enableBeatCommunication();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 */
	public void disableMaxMSPBeatCommunication()
	{
		this.maxReceiver.disableBeatCommunication();
	}
	
	/**
	 * See {@link MaxMSPCommunication} for an explanation.
	 */
	public void resetMaxMSPCommunication()
	{
		this.maxReceiver.resetMaxMSP();
	}
	
	/**
	 * Returns the song length in seconds.
	 * <p>
	 * This method does not need to be synchronized because
	 * it is only accessed by one method.
	 * 
	 * @return the song length in seconds
	 */
	private int getSongLengthInSeconds()
	{
		return Utils.microSecondsToSeconds( sequencer.getMicrosecondLength() );
	}
	
	/**
	 * Starts the preprocessing of the song.
	 * <p>
	 * Please see the documentation for {@link LightThreadPreprocessor} for an explanation
	 * of how the class functions. Considering this is a private method, it will not
	 * be called by other threads and therefore does not need to be synchronized.
	 * 
	 * @param song to be played
	 * @param autoPlayAfterPreprocessing true to play the song automatically after the song is preprocessed
	 */
	private void startProgressBarEnabledPreprocessor(Song song,boolean autoPlayAfterPreprocessing)
	{
		threadedPreprocessor = new LightThreadPreprocessor(controller, pitchReceiver, sequence.getTracks(), sequencer.getTempoInBPM() );//, frame); 
		threadedPreprocessor.autoPlayAfterPreprocessing = autoPlayAfterPreprocessing;
		threadedPreprocessor.execute();
	}
	
	/**
	 * Sets the total time in the GUI.
	 * <p>
	 * This is a private method and is not called by other threads.
	 * Hence it does not need to by synchronized.
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
}
