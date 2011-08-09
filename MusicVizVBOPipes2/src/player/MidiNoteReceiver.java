package player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;

import controller.Controller;
import gui.GUI;
import player.messages.OpenGLMessageBeat;
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
	/*
	 *	These 2 values represent the values that a MIDI device's pitch wheel can have; the lowest
	 *value is 0x0000 and the highest value is 0x3fff.  
	 */
	public final static int MINVALUE = 0x0000;
	public final static int MAXVALUE = 0x3fff;
	
	/*
	 * tonalProcessor: The single tonal processor is used if the the midi message is not from 
	 * the beat channel which is channel 10 but in programming it is channel 9.
	 * 
	 * controller: The controller in the MVC. It links the back end to the front end.
	 * 
	 * bp: Is not related to the oil company, but is a beat processor. This 
	 * processor the drum notes and program events.
	 * 
	 * sequencer: is what plays the song. Please look at the Java api for descriptions
	 * on what this can do.
	 */
	TonalProcessor tonalProcessor;
	Controller controller;
	BeatProcessor bp; 
	Sequencer sequencer;
	
	/*
	 * channelsToUse: is a boolean variable, if the position in the array is true, then we
	 * process that channel's pipe. If it is false, then we do not process that channel's pipe.
	 * When a pipe is not processed, then the visualizer does not show it.
	 * 
	 * lastTick and lastTime: holds the tick and time where the last beat changed. This is set
	 * when the song slider is changed; in this case when the user indexes to a new point,
	 * we have to find out the last tick and time interval.
	 * 
	 * notesForTheCurrentSong: is a variable where the midi messages are grouped into objects.
	 * Therefore, instead of a note on signal then a note off signal later, I have grouped
	 * the note on/offs together.
	 */
	boolean[][] channelsToUse;
	long lastTick = 0;
	double lastTime = 0;
	HashMap<Long, LinkedList<Note> > notesForTheCurrentSong;
	
	/*
	 * These next variables are specifically used for operations that concern the
	 * bars in the song.
	 * 
	 * barsWithChangesInBar: holds as a key, the tick where a new bar starts and has a string array
	 * as a value. The string array is a size 16 array with a format as follows "<amountOfMovement>_<channel>"
	 * the <amountOfMovement> and <channel> are parsed into numbers and the string array is sorted in descending order
	 * by <amountOfMovement>. The 2 following object arrays are just the key set and values set turned to arrays of this 
	 * variable.
	 * 
	 * lastTickWhereBarsChanged: is the tick where the bar changed. At this point, a new thread is spawned to
	 * reposition the pipes.
	 * 
	 * smallestBarInSong: is the smallest length of a bar in this song. 
	 * 
	 * The 2 above variables are used together to check if the bar has changed which if it does change, this
	 * is where we spawn a new thread to reposition the bars in the openGL part. The algorithm works like this:
	 *  if currentTickInSong - lastTickWhereBarsChanged >= smallestBarInSong
	 *  	then spawn new thread and then set lastTickWhereBarChanged to currentTickInSong.
	 */
	TreeMap<Long, String[]> barsWithChangesInBar;
	Object[] barsWithChangesKeySet;
	Object[] barsWithChangesValues;
	long lastTickWhereBarsChanged = 0;
	public long smallestBarInSong;
	
	//Created a new cached thread pool which only takes care of the bar changing.
	//Have my FindCurrentBarThread just to stand by. a new object over writes this variable 
	//everytime a new song is loaded.
	ExecutorService threadPool = Executors.newCachedThreadPool();
	//FindCurrentBarThread findCurrentBarThread;
	
	/**
	 * This variable is important because it is used to keep track of what time interval we are in.
	 * If we change a time interval Example: 0s to 9s -> 10s to 19s then we must reposition the instruments.
	 * It is vital to reset this variable to 0 when a new song is loaded.
	 */
	//private int lastInterval = 0;
	
	private int[] rangeOfPitchValues = new int[16];
	private int[] initialPitchSettings = new int[16];
	private int lastTimeNoteWasPlayed = 0;

		
	public MidiNoteReceiver(Controller controller, Sequencer sequencer )
	{
		tonalProcessor = new  TonalProcessor();
		channelsToUse = new boolean[16][3];
		//set all channelsToUse to false, and only set them to true if the pipes are used.
		for( int i = 0; i < 16; i++ )
		{
			channelsToUse[i][0] = false;
			channelsToUse[i][1] = false;
			channelsToUse[i][2] = false;
		}
		bp = new BeatProcessor();
		this.controller = controller;
		this.sequencer = sequencer;
	}
	
	/**
	 * Mandatory Method required when implementing receiver. Only needs to be used
	 * if underlying devices were opened by the receiver. In this case, I only created
	 * a thread pool for the processing of a song, therefore that is the only thing I need to closed.
	 */
	public void close() 
	{
		threadPool.shutdownNow();
	}

	/**
	 * This method is called by the preprocessor. In the preprocessor, we find the most amount of changes in the song,
	 * and then the ratio of the pipe is calculated based on the most movements. So if a pipe moves a max of 1000 times, and 
	 * our pipe (called pipe 2 just say), has 10 movements, then our pipe's ratio is 10/1000 so 1%. If we set a threshold of 20%
	 * then anything under 20% will not be render. Therefore, pipe 2 will be left out. QED (I like Latin).
	 * 
	 * This method sets the channels and pipes to render based on if the pipe has a percentage of movements greater than the 
	 * one passed in. 
	 * 
	 * 
	 * @param percent
	 * @param noteChangePercentage
	 */
	public void setChannelsToUse( double percent, double[][] noteChangePercentage )
	{
		for( int i = 0; i < 16; i++ )
		{
			for( int j = 0; j < 3; j++ )
			{
				if( noteChangePercentage[i][j] > percent )
				{
					channelsToUse[i][j] = true;
				}
			}
		}
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
	    int seconds = 0;
	    GUI gui = controller.getGUI();
	    
	    if( sequencer.isRunning() )
	    {
		    // Used to update the slider to show current position in the song
		    seconds = Utils.microSecondsToSeconds(sequencer.getMicrosecondPosition());
		    if( (seconds - lastTimeNoteWasPlayed) > 0 )
		    {
		    	gui.setCurrentValueForSlider(seconds);
		    	gui.updateTimer(Utils.secondsToTime(seconds));
		    }
		    lastTimeNoteWasPlayed = seconds;
	    }
	    
	    //if( status >= 224 && status <= 239 )
	    //{
	    //	channel = status - 224;
	    //	int pitchBend = (midiByte[2] & 0xff) << 7 | (midiByte[1] & 0xff);

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
		    //double offset =(((double)pitchBend - (double)initialPitchSettings[channel])/(double)MAXVALUE)*rangeOfPitchValues[channel];
		   // if( (pitchBend - (double)initialPitchSettings[channel]) != 0.0)
		   // {
			   // OpenGLMessagePitchChange pitchChange = new OpenGLMessagePitchChange(offset, channel,rangeOfPitchValues[channel]);
	//		   // controller.getVisualizer().messageQueue.get(channel).add(pitchChange);
		    //}
	    //}
	    //Checks the status for a note on event
	    if ( status >= 144 && status <= 159 )
	    {
	    	// calculates the channel the note belongs to
	    	channel = status > 143 ? status - 144 : status - 128;
	    	
	    	note = (int)(midiByte[1] & 0xff);
	    	velocity = (int)(midiByte[2] & 0xff);
	    	
	    	//Channel 9 is percussion therefore every other channel is notes.
	    	if( channel != 9 )
	    	{
	    		tonalEventProcessing(channel, velocity, note);

	    		/*if(seconds - lastInterval >= ThreadPreprocessor.threshold )
	    		{
		    		int position = seconds/ThreadPreprocessor.threshold;
	///	    		///controller.getVisualizer().barQueue.add(position);
			    	lastInterval = seconds;
	    		}*/
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
	    	else if( channel == 9 )
	    	{
	    		//Beats, with note offs do have a velocity that is needed, tones don't
	    		
    			velocity = (int) (midiByte[2] & 0xff);
    			beatEventProcessing(note, velocity);
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
		OpenGLMessageTonal tonalMessage = tonalProcessor.processNote( note, velocity, channel);
		GUI gui = controller.getGUI();
		if( channelsToUse[channel][tonalMessage.getPipe()] )
   		{
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
		if( beat != null )
		{
	//		controller.getVisualizer().messageQueue.get(9).add(beat);
		}
	}
	
	/**
	 * This method is called by the controller, which in turn is called by the SlideMouseListener. The point
	 * of this is to keep our manual timing calculations on track. (Note that midi does timing calculations in
	 * delta times based on the last position where the BPM changed ). So the SlideMouseListener finds the BPM
	 * change that is closest to the new song position and sets it in this class, which keeps manual timing
	 * calculations on track.
	 * 
	 * @param time
	 */
	public void setLastTimeInSeconds( double time )
	{
		lastTime = time;
	}
	
	/**
	 * This goes hand in hand with the method above which is caleld setLastTimeInSeconds. The method above 
	 * is this tick converted to seconds.
	 * @param tick
	 */
	public void setLastTick( long tick )
	{
		lastTick = tick;
	}
	
	/**
	 * This is called by the preprocessor after it is done with the Midi File. This variable is explained 
	 * in the ThreadedPreprocessor but just for a quick overview, it is a HashMap for very fast O ( 1 )
	 * operations. The HashMap has midi ticks as the key and a LinkedList as a value. The key denotes
	 * the tick where a note was played and the reason for having a linkedlist is because many notes can be 
	 * played at one tick. Therefore, the many notes will be stored in the linkedlist at the tick where they
	 * were signaled on.
	 * 
	 * @param notesForTheCurrentSong
	 */
	public void setNotesForTheCurrentSong( HashMap<Long, LinkedList<Note>> notesForTheCurrentSong )
	{
		this.notesForTheCurrentSong = notesForTheCurrentSong;
		//lastInterval = 0;
		lastTimeNoteWasPlayed = 0;
	}

	/**
	 * After the preprocessor finishes with the midi file. It will load this MidiNoteReceiver 
	 * with the TreeMap that holds the ticks to where the bars changed as well as their value,
	 * which is the amount of movements for the channel in that bar.
	 * 
	 * @param startOfBarsWithChangesInBarTreeString
	 */
	public void setStartOfBarsWithChangesInBarTreeString (TreeMap<Long, String[]> startOfBarsWithChangesInBarTreeString  )
	{
		this.barsWithChangesInBar = startOfBarsWithChangesInBarTreeString;
		barsWithChangesKeySet = startOfBarsWithChangesInBarTreeString.keySet().toArray();
		barsWithChangesValues = startOfBarsWithChangesInBarTreeString.values().toArray();
		//this variable needs to be renewed everytime a new song is loaded, which makes this method
		//a perfect place to do it in because this method is called everytime a new song is loaded.
		//findCurrentBarThread = new FindCurrentBarThread(controller, barsWithChangesKeySet, barsWithChangesValues);
	}
	
	public void setRangeOfPitchValuesInChannels(  int[] rangeOfPitchValues )
	{
		this.rangeOfPitchValues = rangeOfPitchValues;
	}
	public void setInitialPitchSettingsInChannels( int[] initialPitchSettings )
	{
		this.initialPitchSettings = initialPitchSettings;
	}
}
