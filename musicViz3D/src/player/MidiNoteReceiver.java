package player;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.swing.SwingUtilities;

import controller.Controller;
import gui.GUI;
import player.messages.OpenGLMessageBeat;
import player.messages.OpenGLMessagePitchChange;
import player.messages.OpenGLMessageTonal;
import processors.BeatProcessor;
import processors.TonalProcessor;
import utilities.Utils;

/**
 * Acts as the receiver of Midi information. As the sequencer plays the file in real time.
 * This class is responsible for calling the appropriate Tonal/Beat Processor which is
 * Dependent on the channel the incoming note belongs to.
 * 
 * @author Michael Pouris
 *
 */
public class MidiNoteReceiver implements Receiver
{
	public AtomicBoolean playInstruments;
	public AtomicBoolean playBeats;
	
	/*
	 *	These 2 values represent the values that a MIDI device's pitch wheel can have; the lowest
	 *value is 0x0000 and the highest value is 0x3fff.  
	 */
	public final static int MINVALUE = 0x0000;
	public final static int MAXVALUE = 0x3fff;
	
	private TonalProcessor tonalProcessor;
	private Controller controller;
	private BeatProcessor bp; 
	private Sequencer sequencer;
	
	/*
	 * lastTick and lastTime: holds the tick and time where the last beat changed. This is set
	 * when the song slider is changed; in this case when the user indexes to a new point,
	 * we have to find out the last tick and time interval.
	 * 
	 * notesForTheCurrentSong: is a variable where the midi messages are grouped into objects.
	 * Therefore, instead of a note on signal then a note off signal later, I have grouped
	 * the note on/offs together.
	 */
	private long lastTick;
	private double lastTime;
	
	private int[] rangeOfPitchValues;
	private int[] initialPitchSettings;
	private int lastTimeNoteWasPlayed;

		
	public MidiNoteReceiver(Controller controller, Sequencer sequencer )
	{
		this.tonalProcessor = new  TonalProcessor();
		this.bp = new BeatProcessor();
		this.controller = controller;
		this.sequencer = sequencer;
		this.lastTick = 0;
		this.lastTime = 0;
		this.lastTimeNoteWasPlayed = 0;
		this.rangeOfPitchValues = new int[16];
		this.initialPitchSettings = new int[16];
		this.playInstruments = new AtomicBoolean(true);
		this.playBeats = new AtomicBoolean(true);
	}
	
	/**
	 * Mandatory Method required when implementing receiver. Only needs to be used
	 * if underlying devices were opened by the receiver.
	 */
	public void close() 
	{
	}
	
	/**
	 * As the midi file is played in real time, this is the method that is called to intercept the current
	 * midi message being processed. 
	 * 
	 * @param m
	 * @param time
	 */
	public void send(MidiMessage m, long time)
	{
		byte midiByte[] = m.getMessage();
	    int status = m.getStatus();
	    int channel = -1;
	    int velocity = -1;
	    int note = -1;
	    GUI gui = controller.getGUI();

	    //Set the timing every time a second changes. The negative check is 
	    //to see if the slider was changed to a previous time and reset the time
	    int seconds = Utils.microSecondsToSeconds(sequencer.getMicrosecondPosition());
		if( (seconds - lastTimeNoteWasPlayed) > 0 )
		{
			gui.setCurrentValueForSlider(seconds);
		    gui.updateTimer(Utils.secondsToTime(seconds));
		    lastTimeNoteWasPlayed = seconds;
		}
		else if( (seconds - lastTimeNoteWasPlayed) < 0 )
		{
			lastTimeNoteWasPlayed = seconds;
		}
		
	    if( status >= 224 && status <= 239 )
	    {
	    	channel = status - 224;
	    	int pitchBend = (midiByte[2] & 0xff) << 7 | (midiByte[1] & 0xff);

	    	/*
	    	 * The way pitch bends work is that there is a pitch wheel; the pitch wheel has a min and max value of 0x0000 and
	    	 * 0x3FFF. These numbers are meaningless until the computer is told how to deal with them. Therefore, in the preprocesor
	    	 * each channels' pitch wheel is assigned a range of semitones that it spans as well as their initial settings; these
	    	 * numbers are stored in rangeOfPitchValues and initialPitchSettings respectively. Once, the preprocessor
	    	 * sets these variables in this class and a pitchwheel change event occurs, then we can make a meaning out of it. To
	    	 * find the change in semitones when the pitch is changed, this equation is used: 
	    	 * 
	    	 * [ ( newPitch - initialPitchInChannel )/MAXVALUEOFWHEEL ] * rangeOfPitchForChannel : This gives us the change in semitones.
	    	 */
		    double offset =(((double)pitchBend - (double)initialPitchSettings[channel])/(double)MAXVALUE)*rangeOfPitchValues[channel];
		    if( (pitchBend - (double)initialPitchSettings[channel]) != 0.0)
		    {
		    	//System.out.println(offset);
			    OpenGLMessagePitchChange pitchChange = new OpenGLMessagePitchChange(offset, channel,rangeOfPitchValues[channel]);
			   // System.out.println("gfhfghfgh");
			    if( gui.getVisualizer().messageQueue.get(channel).size() < 20 )
			    {
			    	gui.getVisualizer().messageQueue.get(channel).add(pitchChange);
			    }
		    }
	    }
	    //Checks the status for a note on event
	    if ( status >= 144 && status <= 159 )
	    {
	    	// calculates the channel the note belongs to
	    	channel = status > 143 ? status - 144 : status - 128;
	    	
	    	note = (int)(midiByte[1] & 0xff);
	    	velocity = (int)(midiByte[2] & 0xff);
	    	
	    	if( channel != 9 )
	    	{
	    		tonalEventProcessing(channel, velocity, note);
	    	}
	    	else if( channel == 9 )
	    	{
	    		beatEventProcessing(note, velocity);
	    	}
	    }
	    //Checks status for a note off event
	    else if( status >= 128 && status <= 143 )
	    {
	    	//Calculates channel
	    	channel = status > 143 ? status - 144 : status - 128;
	    	
	    	note = (int)(midiByte[1] & 0xff);
	    	velocity = 0;
	    	
	    	if( channel != 9 )
	    	{
	    		tonalEventProcessing(channel, velocity, note);
	    	}  
    	}
	}
	
	/**
	 * This method is called when the current note being played is a tonal note on/off event.
	 * The proper message is then processed and sent back to the controller for use.
	 * 
	 * @param channel
	 * @param velocity
	 * @param note
	 */
	private  void tonalEventProcessing( int channel, int velocity, int note) 
   	{
		GUI gui = controller.getGUI();
		if(gui.getVisualizer().messageQueue.get(channel).size() < 20 && playInstruments.get() )
		{
			OpenGLMessageTonal tonalMessage = tonalProcessor.processNote( note, velocity, channel);
			gui.getVisualizer().messageQueue.get(channel).add(tonalMessage);
		}
   	}
	
	/**
	 * This method is called when the note currently being played is a Beat note on/off event.
	 * The proper message is then processed and send back to the controller for use.
	 * 
	 * @param note
	 * @param velocity
	 */
	private void beatEventProcessing( int note, int velocity )
	{
		OpenGLMessageBeat beat = bp.processBeat(note, velocity);
		if( beat != null && playBeats.get() )
		{
			controller.getGUI().getVisualizer().messageQueue.get(9).add(beat);
		}
	}
	
	/**
	 * Sets the tick and time where a BPM change occurred. This is called by the player,
	 * which holds this receiver. This is called by the SlideMouseListener and MidiMetaEventListener.
	 * The SlideMouseListener keeps track of manual song time changes, like when the user changes
	 * the position. When the slider is changed, we must recalculate where the last BPM change was
	 * and then tell the receiver. The MidiMetaEventListener is alerted to the change then 
	 * alerts the receiver.  This is done because Midi does its timing in delta times.
	 * 
	 * @Param The tick position where the BPM changed
	 * @param The time position where the BPM changed
	 */
	public void setLastBPMChangeData( long tickWhereBPMChanged, double timeWhereBPMChanged )
	{
		this.lastTick = tickWhereBPMChanged;
		this.lastTime = timeWhereBPMChanged;
	}
	
	public void setPitchData( int[] initialPitchSettings, int[] rangeOfPitchValues )
	{
		this.initialPitchSettings = initialPitchSettings;
		this.rangeOfPitchValues = rangeOfPitchValues;
		this.lastTimeNoteWasPlayed = 0;
	}
	
	
}
