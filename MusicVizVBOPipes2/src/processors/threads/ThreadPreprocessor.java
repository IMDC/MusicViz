package processors.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import controller.Controller;
import gui.GUI;
import player.MidiNoteReceiver;
import player.Note;
import player.Player;
import processors.ChannelProcessor;
import processors.TonalProcessor;
import utilities.StringComparator;
import utilities.Utils;

/**
 * This Class extends SwingWorker which in theory (at the core) is exactly a thread (like the thread class in java).
 * The only exception is that this can be integrated with the JProgressBar. Initially, there was a class called Preprocessor
 * that preprocessed the Midi file, although there was a problem with this. When a song would be processed in the old class,
 * the Swing thread would denote it's resources to the task. This causes the GUI to lag and appear frozen to the user. Now,
 * with this new preprocessor that is in its own thread, the preprocessing task is handed off to this thread, a new window is
 * opened with a progress bar and this thread will send progress change events to update and animate the progress bar.
 * 
 * @author Michael Pouris
 *
 */
public class ThreadPreprocessor extends SwingWorker<Void,Void>
{
	/**
	 * The important variables that are used outside this object:
	 * 
	 * tickWithBPMChanges: Used by the SlideMouseListener Object to keep timing calculations on track
	 * 
	 * tickWithBPMChangesToTime: Used by the SlideMouseListener Object to keep timing calculations on track
	 * 
	 * notesForTheCurrentSong: This object sets the current notes in the Receiver 
	 * 
	 * channelsUsed: Used by the player to set up the visualiser
	 * 
	 * defaultString: This variable might look odd at first sight but there are many places
	 * 		within this code that describe how it works. I will explain here as well. Hong's idea is to position
	 * 		the instrument families based on the amount of movements greater than a certain amount. This is supposed to
	 * 		help segregate the melody. To do this, we store the at each position in an array, the amount of movements. so channel one
	 * 		has the movements stored in position [0] etc. But we have to sort the movements to get them in descending order, and this will
	 * 		screw up the positioning in the array. Therefore, no long will the value at position 0 correspond to the movements for the first channel.
	 * 		This means, we have a string array with at each position we store <changes>_<channel> (as a string). Then when I sort the array
	 * 		with my own comparator, the channel that corresponds with the changes, is not lost. please look at the comparator in the Utilities
	 * 		package. Note that that the sorting only takes into account the changes and sorts in descending order.
	 * 
	 * 	startOfBarsWithChangesInBarTreeString:
	 * 	  	This variable might be odd, but it makes sense. What it does is break up the song into intervals based
	 * 		on where the time signatures change. These intervals are the keys that store another HashMap. Now, once the 
	 * 		intervals are created, which denote when a time signature changed, we can have a time span where the new time
	 * 		signature is used, for example, at tick 980 we start using bars of length 10 until tick 9800. This means we have to
	 * 		further break up the intervals. 
	 */
	
	
	
	/**
	 * THE FOLLOWING VARIABLES ARE USED ONLY IN THE PREPROCESSOR AND HAVE USE ONLY IN THE PREPROCESSOR.
	 * THESE ARE NOT USED IN THE VISUALIZATION.
	 * 
	 * The reason for the defaultString = -10_-10 is to set a non existing channel and a non existing amount, which 
	 * consequently would never occur in midi for sorting the first number in descending order. This means that
	 * -10_-10 would appear towards the end of a sorted array; the highest amount of changes would be closest to
	 * the beginning of the array. Main use is for the initial positioning of the channels.
	 * 
	 *  autoPlayAfterPreprocessing is set to true if the song should be played DIRECTLY after it is processed. This means that
	 *  if it is true, in the overridden done() method the player is told to play. Anything that must be done after the preprocessor,
	 *  MUST be put in the done() method to guarantee that those processes DO NOT interfere with the doInBackground() method which is the heart
	 *  of the thread.
	 * 
	 * FINE: is the Integer value that denotes a fine pitch change (by musical measurement of cents)
	 * 
	 * COARSE: is the integer value that denotes a coarse pitch change (by music measurement of semitones)
	 */
	public final static String defaultString = "-10_-10";
	public boolean autoPlayAfterPreprocessing = false;
	public final static int FINE = 1;
	public final static int COARSE = 2;
	
	/*
	 * These variables are specifically used for the preprocessor.
	 * The allMidiTracks is an array holding all the midi tracks if the file is a Type 1 midi file (1 track is type 0)
	 * The sequence contains the midi tracks.
	 * lastBPM in the song initially starts off as the first BPM. The reason for this variable is because when
	 * converting midi ticks to real time, it is all based on the lastBPM in the song.
	 * receiver is the object that receives midi events as they are played.
	 * 
	 */
	private Track allMidiTracks[];
	private Sequence sequence;
	private float lastBPMInSong;
	private MidiNoteReceiver receiver;
	Controller controller;
	
	/**
	 * THE FOLLOWING VARIABLES ARE USED IN THE VISUALIZATION AND MIDINOTERECEIVER. THE RESON FOR THE AMOUNT OF VARIABLES IS TO
	 * HOLD ALL THE INFORMATION WE CAN GET FROM A MIDI FILE, THEREFORE WE DON'T HAVE TO REPREPROCESS THE ENTIRE
	 * FILE.
	 */
	
	/*
	 * These 2 variables are used to keep track of EACH note change. The note change pertains 
	 * to the pitch changes only, NOT the volume. So note 10 at volume 120 changed to note 10 volume 100
	 * is not considered a note change. This is used to calculate a threshold that will denote what
	 * instruments will be visualised. 
	 */
	private int[][] changes = new int[16][3];
	private int[][] lastNote = new int[16][3];
	
	/*
	 * This variable holds the length of the smallest bar in the song. This will be used in
	 * the MidiNoteReceiver and be the maximum amount of time to wait before checking to
	 * see if the bar changed.
	 */
	private long smallestLengthOfBarInSong = Integer.MAX_VALUE;
	/*
	 * Like the 2 variables above, these ones also keep track of the note changes except over
	 * a certain amount. Unlike the variables above, where a single note change of 1 will be counted
	 * these variables are used to keep track of note changes of greater than a variable number; in this case, 10.
	 * The main use for these variables are for the placement of the pipes. Therefore, the pipes
	 * with the most movement over the recorded threshold will be placed closer to the front.
	 */
	private int[] changesByAmount = new int[16];
	private String[] changesByAmountString = new String[16];
	
	/*
	 * This set of variables keep track of where the BPM changes occured in the song.
	 * tickWithBPMChanges and tickWithMPQChanges both have a tick as a key and the
	 * corresponding beat saved. The only difference is that one holds the beat in BPM
	 * and the other in MPQ. (Note: both key sets will be the EXACT same).
	 * The tickWithBPMChangesToTime will have the same key set as the other 2 variables and
	 * the corresponding value will have the ticks where the BPM changed, converted to time.
	 * 
	 * These variables are used for converting ticks to real time.
	 */
	private TreeMap<Long, Float> tickWithBPMChanges = new TreeMap<Long, Float>();
	private TreeMap<Long, Long> tickWithMPQChanges = new TreeMap<Long, Long>();
	private HashMap<Long,Double> tickWithBPMChangesToTime = new HashMap<Long, Double>();
	
	/*
	 * These following variables have the same principle as the beat variables above, except they
	 * store the tick where the time signature changed to as well as the new time signature.
	 * tickWithTimeSignatureChanges holds the tick where the signature changed as well as what it changed to.
	 * tickWithTimeSignatureChangesToTime holds the tick where the signature changed and the tick converted
	 * to real time.
	 * tickWithLengthOfBarInSeconds holds how long the duration of the bar at that point in seconds.
	 * tickWithLengthOfBarInTicks holds how long the duration of the bar at that point in ticks.
	 * startOfBarsWithChangesInBarTreeString holds the point where a bar changes and the corresponding
	 * value holds a 16 position array which holds how much that channel changes in that bar.
	 */
	private TreeMap<Long,Float[]> tickWithTimeSignatureChanges = new TreeMap<Long, Float[]>();
	private HashMap<Long,Double> tickWithTimeSignatureChangesToTime = new HashMap<Long, Double>();
	private HashMap<Long, Float> tickWithLengthOfBarInSeconds = new HashMap<Long, Float>(); 
	private HashMap<Long, Long> tickWithLengthOfBarInTicks = new HashMap<Long, Long>(); 
	private TreeMap<Long,  Integer[] > startOfBarsWithChangesInBarTree = new TreeMap<Long,Integer[] >();
	private TreeMap<Long,  String[] > startOfBarsWithChangesInBarTreeString = new TreeMap<Long,String[] >(); //this is what we use for bar changes
	
	/*
	 * These are used for grouping midi messages into actual note objects that hold their duration in ticks and seconds.
	 * The ArrayList notes variable is used to place the new notes into. This is a simple structure used to easily accommodate 
	 * the growth.
	 * sortedNotes just holds the notes in the notes variable. The reason for this is because they need to be sorted and this can only
	 * be done in an array. The notes are sorted based on the comparable method in the Note class.
	 * notesForTheCurrentSong holds all the ticks where a note is turned on as well as the corresponding note. The reason for the LinkedList
	 * is because at a tick, many notes can be played (like in real music). Therefore, the LinkedList holds all those notes.
	 */
	private ArrayList<Note> notes;
	private Note[] sortedNotes;
	private HashMap<Long, LinkedList<Note>> notesForTheCurrentSong;
	
	/*
	 * These variables are solely used to set up the visualiser. The colors array is a size 16 array and each position i corresponds
	 * to channel i. So channel i corresponds to the colour in position i of the array.
	 * The partsMapped HashMap is a extremely efficient way of storing and accessing the pipe for manipulation. This variable is constantly
	 * used by the visualizer.
	 * channelOrderWithXLimit is an array used to hold the x coordinate of the pipes. Every pipe in channel i will be restricted to that
	 * x coordinate.
	 * closeButton is used with the JProgressBar to show the progess of loading a song.
	 * channelsUsed holds the channels used, so if channel 6 is used then 6->true. 6 <maps to> true.
	 * instruments holds the name of the instrument that each channel corresponds to.
	 */
	//private Color colours[] = {Color.BLUE, Color.CYAN,  new Color(200,20,50), Color.GREEN,Color.MAGENTA,Color.ORANGE, Color.PINK, Color.YELLOW, Color.WHITE 
	//		,Color.WHITE, new Color(10,70,150),new Color(10,150,70),new Color(153,0,51),new Color(153,255,0),new Color(70,150,10),new Color(70,10,150) };
	
	//private float[] channelOrderWithXLimit = new float[16];
	private JButton closeButton;
	private TreeMap<Integer, Boolean> channelsUsed = new TreeMap<Integer, Boolean>();
	private String[] instruments = new String[16];
	private int[][] instrumentMinMax = new int[16][2];
	
	 
	/*
	 * These are used for pitch bends in the song; most midi songs do not have pitch bends unless they are professionally
	 * made. 
	 * 
	 * Please look at this: http://www.recordingblogs.com/sa/Wiki/tabid/58/Default.aspx?topic=MIDI+Registered+Parameter+Number+(RPN)
	 * 
	 * Please note that all RPN numbers should be ended with 127 otherwise the sequencer may not know when the RPN controller
	 * sequence is over. Although, it is good practice it is not needed; in order to handle this case, the rangeOfPitchValues,
	 * initialPitchSettings and typeOfTuningInChannel are all filled based on if the channel that the RPN belongs to, is different
	 * than the last. Another reason for checking the channel changes is because setting up the pitch wheel consists of more than
	 * one midi message and have to first group them to figure out what kind of pitch changes will occur (coarse or fine) and the range
	 * of semitones it will follow
	 */
	private int typeOfTuningIntValue = Integer.MIN_VALUE;
	private byte typeMSB = Byte.MIN_VALUE;
	private byte typeLSB = Byte.MIN_VALUE;;
	private int lastChannel = Integer.MIN_VALUE;
	private int[] rangeOfPitchValues = new int[16];
	private int[] initialPitchSettings = new int[16];
	private int[] typeOfTuningInChannel = new int[16];
	
	/*
	 * threshold: is a static and final variable that specifies the length of an interval in a song. If it's 10,
	 * then the song is split in x amount of intervals of size 10. Ex. 120second song has 12 ten second intervals.
	 * 
	 * noteChangesInInterval: 2D array that splits the song into its intervals specified by threshold and then each
	 * interval has its 16 channels. Note: [c] = channel c. This is used to initially keep track of a channel's changes in an interval.
	 * 
	 * noteChangesInIntervalString: This is the exact same thing as the variable above except it is a string. So at [interval][channel]=
	 * <changesInChannel>_<channel>. This is done because I want to sort the channels based on their movements but still have their
	 * channels. I use my own string comparator to sort it.
	 * 
	 * noteChangesInIntervalInteger: This variable is almost the same as the above one but once the above one has their channels sorted,
	 * I just take the sorted channels for a position and turn them back to integers.
	 * 
	 * Example:
	 * noteChangesInInterval[1] = {60,70,65,50,..etc}
	 * noteChangesInIntervalString[1]={"60_0","70_1","65_2","50_3",...etc} Now sort using my own comparator
	 *  noteChangesInIntervalInteger[1]={1,2,0,3,...etc} As you can see, in this variable, the positions
	 *  	no longer act as the channel number in midi but now we have the channels with the most amount
	 *  	of changes closer to the beginning of the array. In this example, the channel with the most
	 *  	amount of changes in interval 1 = channel 1.
	 * 
	 */
	public static final int threshold = 10;
	private int[][] noteChangesInInterval;
	private String[][] noteChangesInIntervalString;
	private int[][] noteChangesInIntervalInteger;
	
	/*
	 * This is used to find out the instruments used in the song: 
	 * http://en.wikipedia.org/wiki/General_MIDI#Program_change_events
	 */
	//int[] intruments = new int[16];
	
	/**
	 * Sets up the thread for use. This thread will be use only for one song. Every time a new song is loaded into the program,
	 * a new Preprocessor thread is created for the song. Therefore one thread per song. The reason why we need to set the variable
	 * "lastBPMInSong" in the constructor is because we need to do all timing calculations based based on the last time/tick where
	 * the beat changed. IT  IS VERY IMPORTANT TO GRAB THE BPM FROM THE SEQUENCER BEFORE THE SONG IS PLAYED. THIS ENSURES
	 * THAT THE BEAT IS CORRECT.
	 * 
	 * @param controller
	 * @param receiver
	 * @param allMidiTracks
	 * @param sequence
	 * @param firstBPMInSong
	 * @param closeButton
	 */
	public ThreadPreprocessor( Controller controller, MidiNoteReceiver receiver, Track allMidiTracks[], 
								Sequence sequence, float firstBPMInSong, JButton closeButton, int colourSetToUse )
	{
		this.allMidiTracks = allMidiTracks;
		this.controller = controller;
		this.receiver = receiver;
		this.sequence = sequence;
		this.lastBPMInSong = firstBPMInSong;
		for( int i = 0; i < instruments.length; i++ )
		{
			instruments[i] = "Not set";
			changesByAmountString[i] = defaultString;
			rangeOfPitchValues[i] = Integer.MIN_VALUE;
			initialPitchSettings[i] = Integer.MIN_VALUE;
			typeOfTuningInChannel[i] = Integer.MIN_VALUE;
			instrumentMinMax[i][0] = 1;
			instrumentMinMax[i][1] = 5;
		}
		this.notes = new ArrayList<Note>();
		this.notesForTheCurrentSong = new HashMap<Long, LinkedList<Note>>(); 
		this.closeButton = closeButton;
	}
	
	/**
	 * Converts the currentTick (a midi tick) to real time in seconds based on the resolution and 
	 * BPM of the song at the time. 
	 * 
	 * In type 1 midi files, all the beat changes occur in the very first track. Therefore, 
	 * the ticks (these ticks represent the tick where the BPM changed) are mapped to their BPM in one data structure named "tickWithBPMChanges" and there is another data structure
	 * which holds the ticks where the BPM changed mapped to the ticks converted to time. This is done because to convert a midi tick 
	 * to seconds, it is done relative to the last BPM, therefore, this method finds where the currentTick fits in with the beat changes
	 * in order to get the last time, last tick and BPM of the changes.
	 * 
	 * @param currentTick
	 * @return The currentTick converted to the time in seconds.
	 */
	private double calculateTime( long currentTick )
	{
		//These are the variables that store where the last BPM change happened
		double lastRelativeTime = 0;
  	    long lastRelativeTick = 0;
  	    float lastRelativeBPM = 0;
  	    
  	    //The tick and the beats are given in natural order from the TreeMap. Therefore, each position in ticks map to its BPM
  	    //at the same position in beats. IE the tick at [10] has its BPM stored at [10] in the beats array.
  	    Object[] ticks = tickWithBPMChanges.keySet().toArray();
  	    Object[] beats = tickWithBPMChanges.values().toArray();
  	    
  	    /*
  	     * This loops finds where the currentTick fits in with the ticks. This gives the time, tick and BPM of the last
  	     * BPM change which is used to calculate the time for this tick. IE: 
  	     * 			BPM Changes are at ticks: 0,  56,  90,100,123,900
  	     * 			The BPM at these changes: 120,150,120,150,120,160
  	     * The currentTick = 110, therefore this loop will check if the currentTick fits between the pairs: (0,56)
  	     * (56,90),(90,100) and (100,123), which is the interval it fits in. At this point lastTick = 100, lastBPM = 150
  	     * and the lastTime for the lastTick is grabbed. Only now can the time for the currentTick be calculated.
  	     */
  	    for( int z = 0; z < ticks.length; z++ )
  	    {
  	    	if( z + 1 < ticks.length )
  	    	{
  	    		if( currentTick >= (Long)ticks[z] && currentTick < (Long)ticks[z+1] )
  	    		{
  	    			lastRelativeTick = (Long)ticks[z];
  	    			lastRelativeBPM = (Float)beats[z];
  	    			lastRelativeTime = tickWithBPMChangesToTime.get(lastRelativeTick);
  	    			break;
  	    		}
  	    	}
  	    	else
  	    	{
  	    		lastRelativeTick = (Long)ticks[z];
  	    		lastRelativeBPM = (Float)beats[z];
  	    		lastRelativeTime = tickWithBPMChangesToTime.get(lastRelativeTick);
  	    		break;
  	    	}
  	    }
  	    return ( 60 *( ( currentTick - lastRelativeTick ) / lastRelativeBPM / sequence.getResolution() ) ) + lastRelativeTime;
	}
	
	/**
	 * This method groups the sorted notes together. In the calling-method, every time a new note is created
	 * it is stored into a list regardless of its natural order. Then the list is exported to an array to be sorted and once they
	 * are sorted (look at the Note class to see how it is done), this method stores all the notes in a HashMap for quick O( 1 ) access time
	 * with the tick as a key and the notes as the corresponding values. 
	 * 
	 * There is a problem with this because a tick can have many notes and a Java Map can have one value mapped to a key, therefore, 
	 * when that happens, the different notes are stored in a list in that position.
	 * 
	 * I.E: At tick 200, Notes:1,2,3,123,126 all play at the same time. So we have 200 map to an linked list that holds
	 * all the notes. It looks like this: 200->[1,2,3,123,126]
	 */
	private void groupNotesByTicks()
	{
		LinkedList<Note> n;
		for( int i = 0; i < sortedNotes.length; i++ )
		{
			long firstTick = sortedNotes[i].getFirstEvent().getTick();
			
			//There are a few songs (i found 2), that dont have an end event
			//in this case i set the time of the noteOn event to the same as
			//the note off event
			if(sortedNotes[i].getEndEvent() == null )
			{
				//Status bytes give the status and the channel the message belongs to.
				//Note on's starts with a hex value of 0x9S and then the second digit S is 0 to f
				//tell us the channel. So, once we get the digit S, then we add 128 to get the
				//same channel with a note off. 0x8S. TADA
				int noteOnchannel = sortedNotes[i].getFirstEvent().getMessage().getStatus() - 144;
				byte[] noteOnByte = sortedNotes[i].getFirstEvent().getMessage().getMessage();
				int noteOffStatus = noteOnchannel + 128;
	
				ShortMessage shortMessage = new ShortMessage();
				try 
				{
					shortMessage.setMessage(noteOffStatus, noteOnByte[1]&0xff , 0);
				} catch (InvalidMidiDataException e){
					System.err.println("ThredPreprocessor::doInBackground : There was an invalid midi message made.");
				}
				MidiEvent tempMidiEvent = new MidiEvent(shortMessage , firstTick);
				sortedNotes[i].setEndEvent(tempMidiEvent);
			}
			
			long endTick = sortedNotes[i].getEndEvent().getTick();
			sortedNotes[i].setTickDuration( endTick - firstTick );
			if( !notesForTheCurrentSong.containsKey(firstTick) )
			{
				n = new LinkedList<Note>();
				n.add(sortedNotes[i]);
				notesForTheCurrentSong.put(firstTick, n);
			}
			else
			{
				notesForTheCurrentSong.get(firstTick).add(sortedNotes[i]);
			}
		}
	}
	
	/**
	 * This method is called when a midi event and midi note need to be paired with their first events. This means
	 * that the event and note passed to this method are note offs. Note objects store their first event where they are turned on
	 * and their end event where they are turned off.
	 * 
	 * The way they are paired is very simple, we loop through all the existing notes checking if the end note is set to null
	 * and if the channel and note number are the exact same, in the case where they are, then the end note is matched.
	 * 
	 * @param midiNote The end midi note that has to be paired up with its starting midi note.
	 * @param channel The midi event's current change
	 * @param midiEvent The end midi event that has to be paired with its starting midi event.
	 */
	private void findCorrespondingFirstNote( int midiNote, int channel, MidiEvent midiEvent )
	{
		for( int k = 0; k < notes.size(); k++ )
		{
			Note n = notes.get(k);
			if( n.getEndEvent() == null && n.getChannel() == channel)
			{
				MidiEvent firstEvent = n.getFirstEvent();
				byte[] b = firstEvent.getMessage().getMessage();
				if( midiNote == ((int) (b[1] & 0xff)) )
				{
					n.setEndEvent( midiEvent );
					//calculate the end time
	   	   	  		double endTime = calculateTime( midiEvent.getTick() );
	   	   	    	 n.setEndTimeInSeconds(endTime);
				}
			}
		}
	}
	
	/**
	 * This method is like the run method in a thread object, except this is a task. In theory, they are the
	 * exact same thing except in Java, only tasks can be associated with JProgress bars. Anything that needs to
	 * be ran and then monitored by the JProgressBar should be placed in this method.
	 * 
	 * @return
	 */
	public Void doInBackground() 
	{
		setProgress(0);
		GUI gui = controller.getGUI();
		//gui.getAnimator().pause();
		gui.pauseAnimator();
		gui.setEnabledPlayerFrame(false);
		
		Track singleTrack;
		MidiMessage midiMessage;
		byte[] midiByte;
		int status;
		int channel;	
		int currentNote;
		MidiEvent midiEvent;
		
		processMidiMetaEvents();
		
		setProgress(10);
		
		processorControllerEvents();
		
		setProgress(25);
		
		/*
		 * A quick and efficient way to randomly grab information from an array is by the 
		 * index of an array. So to store the amount of note changes in an array that a song has at a certain
		 * interval, we divide the length of the song in seconds by a certain threshold (to give us the amount
		 * of intervals) then take the floor of the resultant number. This number is the size of the array.
		 * For example: Just say the length of the song is 176 seconds and we have a threshold of 10, which means
		 * 10 intervals. Therefore takes these steps:
		 * 		1. 176/10 = 17.6
		 * 		2. Math.floor(17.6) = 17
		 * Therefore we have 17 intervals of 10 seconds. Now there is another problem to solve; a size 17 array
		 * has numbers 0 to 16. This means that we can only go to 160 seconds, but what about notes in the 
		 * range of 170seconds to 176? The way to solve this is to make the array one size bigger; therefore the
		 * size of the array is 18 and goes from index 0 to 17.
		 */
		int secondLength = Utils.microSecondsToSeconds(sequence.getMicrosecondLength());
		int intervals = secondLength/threshold + 1;
		noteChangesInInterval = new int[intervals][16];
		noteChangesInIntervalString = new String[intervals][16];
		noteChangesInIntervalInteger = new int[intervals][16];
		
		
		/*
		 * Please look at the comments for processMidiMetaEvents(...) to learn about the types of midi files. This next loop processes
		 * the note on/off and program change events.
		 * 
		 */
		for( int i = 0; i < allMidiTracks.length; i++)
		{
			singleTrack = allMidiTracks[i];
			for( int j = 0; j < singleTrack.size(); j++ )
			{
				midiMessage = singleTrack.get(j).getMessage();
				midiEvent = singleTrack.get(j);
	      		midiByte = midiMessage.getMessage();
	      		status = midiMessage.getStatus();
	      		currentNote = midiByte[1] & 0xff;
	      		
	    	    // if the status is a note on event then 144 is minused other wise its 128
    	    	// able to find the lowest and highest notes in the channels on the fly because I don't care
    	    	// about sorting the notes, only the max and min. O( nm ) running time for this. 
	    	    channel = status > 143 ? status - 144 : status - 128;
	    	    
	    	    int position;
	    	    
	    	    if( channel == 9 )
	    	    {
	    	    	channelsUsed.put(channel, true);
	    	    }
	    	    
	      		//if the status indicates that it is a note on we do preprocessing
	    	    if ( status >= 144 && status <= 159 && channel != 9 )
   	    	    {
	    	    	double startTime; 
	    	    	if( (midiByte[2] & 0xff) != 0 )
	    	    	{
	   	    	    	channelsUsed.put(channel, true);
	   	    	    	/*
	   	    	    	 * Keeping Track of all the note changes
	   	    	    	 */  	
	   	    	    	int pipe = 0;
	   	    			pipe = TonalProcessor.getPipe(currentNote);
	   	    	    	
	   	    			/*
	   	    			 * If the current note is not the last note, then there was a pitch change
	   	    			 * and the changes for that pipe and channel are incremented.
	   	    			 */
	   	    	    	if (currentNote != lastNote[channel][pipe])
	   	    	    	{
	   	    	    		changes[channel][pipe]++;
	   	    	    	}
	   	    	    	
	   	    	    	startTime = calculateTime( midiEvent.getTick() );
	   	    	    	
	   	    	    	//For hong's looming idea (the initial placement of the pipes)
	   	    	    	if( (currentNote - lastNote[channel][pipe]) >= 10 )
	   	    	    	{
	   	    	    		changesByAmount[channel]++;
		   	   	    	    position = (int) (startTime/threshold);
		   	   	    	    noteChangesInInterval[position][channel]++;
	   	    	    	}
	   	    	    	lastNote[channel][pipe] = currentNote;
	   	    	    	
	   	    	    	//This is for the note groupings
		   	    	    Note n = new Note();
	   	   	    	    n.setFirstEvent(midiEvent);
	   	   	    	    n.setChannel(channel);
	   	   	    	    //now we need to set the beginning time
	   	   	    	    //startTime = calculateTime( midiEvent.getTick() );
	   	   	    	    n.setStartTimeInSeconds(startTime);
	   	   	    	    notes.add(n);
	   	   	    	    
   	   	    	    	/* The next part is to calculate the amount of note changes in a 10 second interval.
   	   	    	    	 * In order to figure out what "threshold" second interval a note belongs to, the time (in seconds)
   	   	    	    	 * is divided by "threshold" and the floor of that is taken, this tells us what position in the array 
   	   	    	    	 * the note change should be recorded by. The reason why this can be done is because the 
   	   	    	    	 * noteChangesInInterval array size depends on the length of the song in seconds. Please look
   	   	    	    	 * at the code just before these nested loops.
   	   	    	    	 */
	   	   	    	    //position = (int) (startTime/threshold);
	   	   	    	    //noteChangesInInterval[position][channel]++;
	    	    	}
	    	    	else
	    	    	{
   	    	    		//position = (int)( calculateTime(midiEvent.getTick()) /threshold );
   	    	    		//noteChangesInInterval[position][channel]++;
	    	    		//when a note on with a velocity of 0 is used, that is considered a note off. Therefore it's the end of the note.
	    	    		findCorrespondingFirstNote((midiByte[1] & 0xff), channel, midiEvent);
	    	    	}
	    	    	//findNoteChangesInBarAndChannel(midiEvent, channel);
   	    	    }	  
	    		else if( status >= 128 && status <= 143 && channel != 9) 
	    		{
	    			//Keep track of note changes
	    			currentNote = 0;
	    	     	channel = status - 128;
	    	    	if (currentNote != lastNote[channel][0])
	   		    	{
	   		    		changes[channel][0]++;
	   		    	}
	   		    	lastNote[channel][0] = currentNote;
	   		    	
	    	    	//position = (int)( calculateTime(midiEvent.getTick()) /threshold );
   	    	    	//noteChangesInInterval[position][channel]++;
	   		    	
	   		    	//an actual note off event that we need to pair.
	   		    	findCorrespondingFirstNote((midiByte[1] & 0xff), channel, midiEvent);
	   		    	//findNoteChangesInBarAndChannel(midiEvent, channel);
	    		}
	    	    else if( status >= 192 && status <= 207 )//instrument event 
	    	    {
	    	    	if ((status - 192) != 9 )
   	      			{
	    	    		instruments[(status - 192)] = ChannelProcessor.getChannelName((midiByte[1] & 0xff));
	    	    		ChannelProcessor.setMinMax((midiByte[1] & 0xff), (status - 192), instrumentMinMax);
   	      			}
	    	    	else
	    	    	{
	    	    		instruments[(status - 192)] = "drums";
	    	    	}
	    	    }
			}
		}
		
		/**
		 * This next part is for changing the position of the pipes based on an interval, NOT
		 * bars. Changing position based on musical bars does not always work because Midi
		 * does not work like real music. Therefore, interval changing gives the perceived 
		 * image of bar changing but actually works and is more efficient because of array
		 * indexing.
		 */
		String[] tempArrayForChannels = new String[16];
		StringComparator sc = new StringComparator();
		for( int i = 0 ; i < noteChangesInInterval.length; i++ )
		{
			for( int j = 0; j < noteChangesInInterval[0].length; j++ )
			{
				if( channelsUsed.containsKey(j) )
				{
					tempArrayForChannels[j] = noteChangesInInterval[i][j] + "_" + j;
				}
				else
				{
					tempArrayForChannels[j] = defaultString;
				}
			}
			Arrays.sort(tempArrayForChannels, sc );
			noteChangesInIntervalString[i] = tempArrayForChannels.clone();
		}
		/*
		 * The next loop now takes the sorted channels for each interval, strips the frequency of
		 * note movements and stores only the channel. The channels with the most movements are 
		 * at the beginning of the array, while the ones with less movements are found later in the array.
		 * 
		 * Please remember that the variable noteChangesInIntervalString is 2D and indexed like: [interval][channel]
		 * meaning that the "0-m" in [0-n][0-m] corresponds to the channel. IE [0]=channel 0, [10]=channel 10. But after
		 * it is sorted, "0-m" no longer represents the channels because at [0-n][0-m]=<movementAmount>_<channel> and the array
		 * is sorted by <movementAmount>, hence that is why the channels are out of order. Please look at the comments above
		 * the declaration of the variables to see an example of how this works if you still are confused, otherwise ask me!
		 */
		int c;
		for(int i =0; i < noteChangesInIntervalInteger.length; i++ )
		{
			for( int j =0; j < noteChangesInIntervalInteger[0].length;j++)
			{
				c = Integer.parseInt( noteChangesInIntervalString[i][j].split("_")[1] );
				noteChangesInIntervalInteger[i][j] = c;
			}
		}
		
		
		setProgress(70);
		//Sorts the notes by the tick in ascending order
		sortedNotes = notes.toArray(new Note[0]);
		Arrays.sort(sortedNotes);

		//Once the notes are sorted, this method indexes the HashMap by the tick
		//and if there are many notes to one tick then they are put into a List.
		groupNotesByTicks();

		//Once the groupNotesByTicks method is finished, (which works on the variable "notesForTheCurrentSong")
		//it is set in the receiver for further use.
		//receiver.setNotesForTheCurrentSong(notesForTheCurrentSong);
	
		//Calculate which pipes will be played based on a percent threshold. Please see method description.
		//double[][] calculatedNoteChangePercentage = calculateNoteChangePercentage();
		//receiver.setChannelsToUse(0.0,calculatedNoteChangePercentage);
		
		setProgress(80);
		
		/**
		 * This next part is for changing the position of the pipes based on a musical bar. This 
		 * is not guaranteed to work because how Midi handles music. It handles music without
		 * musical bars but still uses an absurd amount of time signature changes to give the sound of a song
		 * (not always) therfore, changing the position of the pipes based on bars only intermittenly works
		 * and when it does, not very well. Which is probably because some songs change time signatures 25 times
		 * therefore having a bar of length 0.* bars doesn't make logical sense. That song technically had zero bars
		 * which caused crashes.
		 * 
		 * Need to uncomment: findNoteChangesInBarAndChannel(midiEvent, channel); for it work work
		 */
		
		/*
		 * The following for loop is used to create changesByAmountString. The reason why I loop through
		 * the keyset is because it holds (as keys in the TreeMap) the channels that are used. Therefore,
		 * channelsUsed variable allows us to access ONLY the used channels. So if channels 1,2,3 are used
		 * then we can access in O( 1 ) positions 1,2,3 in the arrays without having to worry about the other 
		 * positions. 
		 */
		for( Integer i : channelsUsed.keySet() )
		{
			//get the changes in channel i
			int count = changesByAmount[i];
			//create a string <count>_<channel> in this fashion. Then we can sort with our
			//own comparator and still have reference to the change count. Only used to set up pipes
			changesByAmountString[i] = Integer.toString(count) + "_" + i;
		}
		//Sort the array in descending order so the channels with the highest changes are at the top.
		Arrays.sort(changesByAmountString, new StringComparator() );
		
		setProgress(90);
		
		/*
		 *At this point, since we want to move the pipes every time the bar changes based on how many movements it has,
		 *we have to sort it, but how must we do this? Makes me wonder at the challenges people faced when first
		 *making TRON, might have been difficult. So to overcome a program, we create a string with <amountOfChanges>_<channel>
		 *and sort by the number of changes. 
		 *because just for the sake of having more loops! LOOPS EVERYWHERE. OMG IT's FULL OF STARS
		 *
		 *In all seriousness, you might be asking "isn't this next loop the same thing as the one before?", the answer is no.
		 *To give a quick overview, the loop works with the overall amount of changes over a threshold for a WHOLE channel. This
		 *Loop works with the changes that occur in a BAR. Although, it does work in the same idea as the loop above.
		 */
		Integer[] temp;
		String[] labels = new String[16];
		for( long l : startOfBarsWithChangesInBarTree.keySet() )
		{
			temp = startOfBarsWithChangesInBarTree.get(l);
			for(int i = 0; i < temp.length; i++ )
			{
				if( temp[i] == null )
				{
					temp[i]=0;
				}
				labels[i] = temp[i] +"_"+i;
			}
			Arrays.sort(labels, new StringComparator() );
			startOfBarsWithChangesInBarTreeString.put(l, labels);
			labels = new String[16];
		}
		
		/*
		 * tickWithLengthOfBarInTicks is the data structure that holds as a value, the tick where
		 * the bar changed and at its value, the length of a bar. This length will hold until the 
		 * next position in the song where the time signature changes. This data structure is used
		 * to create the variable startOfBarsWithChangesInBarTree which holds as a key, the point 
		 * where the bar changes no matter what. Just to clarify, just say this tickWithLengthOfBarInTicks
		 * has such values like 0->4000 means that the only time the time signature changes is at tick 0 
		 * and a bar length is 4000 ticks. This means a bar changes every 4000 ticks, therefore 
		 * startOfBarsWithChangesInBarTree will hold values such as: 0,4000,8000,12000 etc and the value
		 * at tick 0 will be the amount of changes in bar 0-4000. etc. The running time of the next
		 * algorithm is a simple "search for largest element", therefore has a running time of 
		 * O( n ). THis is much faster than running a sorting algorithm which is O( n long n ).
		 */
		Object[] o = tickWithLengthOfBarInTicks.values().toArray();
		for(int i =0 ; i < o.length; i++)
		{
			if( (Long)o[i] < smallestLengthOfBarInSong ){ smallestLengthOfBarInSong = (Long)o[i];}
		}
		//receiver.smallestBarInSong = smallestLengthOfBarInSong;

		//Set the data structure in the receiver
		//receiver.setStartOfBarsWithChangesInBarTreeString(startOfBarsWithChangesInBarTreeString);
		
		//receiver.setRangeOfPitchValuesInChannels(rangeOfPitchValues);
		//receiver.setInitialPitchSettingsInChannels(initialPitchSettings);
		receiver.setPitchData(initialPitchSettings, rangeOfPitchValues);

		this.setUpVisualizer();
	
		setProgress(100);
		return null;
	}

	/**
	 * This method does exactly what it says; it processes controller events. Although, the controller events 
	 * it processes are RPN numbers and even further, only RPN numbers that configure the MIDI Pitch wheel.
	 * A brief overview is that a MidiController event is specified in one message with 3 bytes. The first byte
	 * is the status byte which is between 176 and 191. The second byte is the controller number and in this case
	 * we are only interested in controller numbers of 100, 101 and 6. 100 = LSB of RPN, 101 = MSB of PRN and
	 * 6 = MSB of data entry. The third byte is the range of pitch values or the initial value of the slider; 
	 * the role depends on the the number given by the third byte of 100 and 101 bytes.
	 * 
	 * The problem to overcome was that there are multiple messages that are sent at different times that make up
	 * the process of configuring the pitch wheel.
	 * 
	 * Look at this: http://www.recordingblogs.com/sa/Wiki/tabid/58/Default.aspx?topic=MIDI+Registered+Parameter+Number+(RPN)
	 * and this    : http://www.recordingblogs.com/sa/Wiki/tabid/58/Default.aspx?topic=MIDI+Pitch+Wheel+message
	 * 
	 * First we have to get both, the MSB and LSB of the RPN message to tell us if we are setting the initial values
	 * of the pitch wheel or setting the range of wheel. This part it sent in 2 parts:
	 * 
	 * 		0xB0 0x65 0x00
	 * 		0xB0 0x64 0x02
	 * 
	 * These would be the first 2 parts. So I have to wait for both messages (0x64 = 100 and 0x65 = 101) before I
	 * can assemble the meaning from the 3rd bytes. To do this, I wait until both typeMSB and typeLSB are not the minimum
	 * value of a byte. From there one I assemble the message by ORing: 0x00 | 0x02 = 0x0002 (ONLY take
	 * the last 7 bits of each byte , therefore ignoring the first). This tells me I am doing coarse
	 * changes for this channel when pitch change does occur and setting the starting value. The next message is:
	 * 
	 * 		0xB0 0x06 0x40
	 * 
	 * This means that 0x40 is the MSB of the message (0x06 tells me it is the MSB. Look at midi messages).
	 * Now I take the first 7 bytes as the MSB which is:  0x1000000000000 = 0x2000 and tells me the that is 
	 * the initial value.
	 */
	private void processorControllerEvents()
	{
		Track singleTrack;
		MidiMessage midiMessage;
		byte[] midiByte;
		int status;
		int channel;
		
		for( int i = 0; i < allMidiTracks.length; i++)
		{
			singleTrack = allMidiTracks[i];
			for( int j = 0; j < singleTrack.size(); j++ )
			{
				midiMessage = singleTrack.get(j).getMessage();
	      		midiByte = midiMessage.getMessage();
	      		status = midiMessage.getStatus();

	      		if( status >= 176 && status <= 191 )
	      		{
	      			channel = status - 176;
	      			
	      			/*
	      			 * This if statement is used to assemble the type of message received. This is necessary
	      			 * because typeOfTuningIntValue is comprised of 2 bytes that come in 2 seperate
	      			 * messages.
	      			 */
	      			if( (midiByte[1]&0xff) == 101 )
	      			{
	      				typeMSB = (byte) (midiByte[2]&0xff);
	      				if( typeMSB != Byte.MIN_VALUE  && typeLSB != Byte.MIN_VALUE)
	      				{
	      					/*
	      					 * To specify tuning, either range, fine or coarse tuning, the MSB is 0x00
	      					 * and the LSB is 0x00, 0x01, 0x02 respectively. So we can XOR the bits and get 
	      					 * either 0x0000, 0x0001, 0x0002, 0x0001.
	      					 */
	      					typeOfTuningIntValue = typeMSB | typeLSB; 
	      				}
	      			}
	      			else if( (midiByte[1]&0xff) == 100 )
	      			{
	      				typeLSB = (byte) (midiByte[2]&0xff);
	      				if( typeMSB != Byte.MIN_VALUE  && typeLSB != Byte.MIN_VALUE)
	      				{
	      					/*
	      					 * To specify tuning, either range, fine or coarse tuning, the MSB is 0x00
	      					 * and the LSB is 0x00, 0x01, 0x02 respectively. So we can XOR the bits and get 
	      					 * either 0x0000, 0x0001, 0x0002, 0x0001.
	      					 */
	      					typeOfTuningIntValue = typeMSB | typeLSB; 
	      				}
	      			}
	      			
	      			//Setting range of slider
	      			if( lastChannel != channel && typeOfTuningIntValue == 0 && (midiByte[1]&0xff) == 6 )
	      			{
	      				//System.out.println("Setting position " + channel + " with " + (midiByte[2]&0xff));
	      				rangeOfPitchValues[channel] =  (midiByte[2]&0xff);
	      				lastChannel = channel;
	      				typeOfTuningIntValue = Integer.MIN_VALUE;
	      				typeMSB = Byte.MIN_VALUE;
	      				typeLSB = Byte.MIN_VALUE;;
	      			}
	      			else if( lastChannel != channel && typeOfTuningIntValue == 2 && (midiByte[1]&0xff) == 6 )
	      			{
	      				//System.out.println( "Setting preset value for channel: " +  channel + " Preset = " + ( (midiByte[2]&0xff) << 7 ) );
	      				initialPitchSettings[channel] = ( (midiByte[2]&0xff) << 7 );
	      				typeOfTuningInChannel[channel] = COARSE;
	      				lastChannel = channel;
	      				typeOfTuningIntValue = Integer.MIN_VALUE;
	      				typeMSB = Byte.MIN_VALUE;
	      				typeLSB = Byte.MIN_VALUE;;
	      			}
	      			
	      		}
	      		//
	      		else if(status  >= 192 && status < 207)
	      		{
	      			//System.out.println((midiByte[1]&0xff) + " " + status);
	      		}
			}			
		}
		
		//if non are set, I made a default to use because midi likes to apply pitch bends to
		//a channel that isnt even set to use them. If you told midi to jump off a bridge, it
		//probably would do it gladly.
		for( int i = 0 ; i < 16; i++ )
		{
			if( initialPitchSettings[i]  == Integer.MIN_VALUE ||
				typeOfTuningInChannel[i] == Integer.MIN_VALUE ||
				rangeOfPitchValues[i]    == Integer.MIN_VALUE)
			{
				initialPitchSettings[i] = 0x2000;
				typeOfTuningInChannel[i] = COARSE;
				rangeOfPitchValues[i] = 12;
			}
			/*System.out.println("------------------------------");
			System.out.println("Channel " + i);
			System.out.println("Initial Pitch  = " + initialPitchSettings[i]);
			System.out.println("Type of tuning = " + typeOfTuningInChannel[i]);
			System.out.println("Range: " + rangeOfPitchValues[i]);
			System.out.println("------------------------------");*/
		}
	}
	
	/**
	 * This method finds out what bar the "midiEvent" belongs to. This is done the EXACT same way as in calculateTime(...),
	 * findCorrespondingBeatInterval(...) and also in processMidiMetaEvents(...). Please read the comments for those methods because
	 * by now the algorithm should be drilled into our heads.
	 * 
	 * @param midiEvent
	 * @param channel
	 */
	/*private void findNoteChangesInBarAndChannel( MidiEvent midiEvent, int channel )
	{
		Object [] keys = startOfBarsWithChangesInBarTree.keySet().toArray();
  	    long theKey = 0;

  	    for( int i = 0; i < keys.length; i++ )
  	    {
  	        if( i + 1 < keys.length )
  	  	   	{
  	        	if( midiEvent.getTick() >= (Long)keys[i] && midiEvent.getTick() < (Long)keys[i+1] )
  	  	    	{
  	  	    		theKey = (Long) keys[i];
  	  	    		break;
  	  	    	}
  	  	    }
  	  	    else
  	  	    {
  	  	    	theKey = (Long) keys[i];
  	  	    	break;
  	  	    }
  	    }
  	    Integer[] tempArray = startOfBarsWithChangesInBarTree.get(theKey);
  	    if( tempArray[channel] == null )
  	    {
  	    	tempArray[channel] = 1;
  	    }
  	    else
  	    {
  	    	tempArray[channel]++;
  	    }
	}*/
	
	/**
	 * This method solely processes midi meta events. The way Midi Files are classified is as follows:
	 * Type 0: Everything is stored in one single track in order. So all events are placed along a single timeline
	 * 		and they are triggered as the song is played. So think of it as one linear time line=1 track only for 
	 * 		Type 0 midi files. 
	 * Type 1: Song can have multiple tracks. There is one restriction ALL meta events are placed in the VERY
	 * 		first track (track 0).
	 * Tyep 2: No one really cares about, please look online though for the specifications to this type.
	 * 
	 * If the file is a type 1 Midi file, then we process all the meta events BEFORE all the note events are processed.
	 * If the file is a type 0 Midi file, then this method skips over everything but meta events.
	 * 
	 * This method is vital to time calculations because it finds where all the beats change (beat changes
	 * are vital to midi timing calculations). This also figures out where the bars are in a song.
	 */
	private void processMidiMetaEvents()
	{	
		MidiMessage midiMessage;
		MidiEvent midiEvent;
		byte[] midiByte;
		int status;
		long lastTick = 0; double lastTime = 0;
		
		for( int i = 0; i < allMidiTracks[0].size(); i++)
		{
			midiEvent = allMidiTracks[0].get(i);
			midiMessage = midiEvent.getMessage();
      		midiByte = midiMessage.getMessage();
      		status = midiMessage.getStatus();
      		
      		if(status == 255)
      		{
      			if( (midiByte[1] & 0xff) == 81 )
      			{
      				long microSecondsPerQuarterNote = ( midiByte[5] & 0xff)
      							|	((midiByte[4] & 0xff) << 8)
      							|	((midiByte[3] & 0xff) << 16);
      				
      				float BPM =  (float)60000000 / (float) microSecondsPerQuarterNote;
      				//secondsPerQuarterNote = microSecondsPerQuarterNote / 1000000.0f;
      				
      				//At this beat change, I store the new beat along with the tick it occurred at.
      				tickWithBPMChanges.put(midiEvent.getTick(), BPM);
      				tickWithMPQChanges.put(midiEvent.getTick(), microSecondsPerQuarterNote);
      				
      				//I have to set the new "lastTime", which is used by the next beat change. Here, I take the time
      				//at the last beat change(one before this one) and use it to calculate the time THIS beat changed. Midi
      				//is based on delta times so first I isolate the length from the last beat change to the CURRENT Beat change and use
      				//the last time to calculate the time at the current beat change. IF this is not done, the times become screwed up.
      				lastTime = ( 60 *( ( midiEvent.getTick() - lastTick ) / lastBPMInSong / sequence.getResolution() ) ) + lastTime;
      				//This tick where this beat changed occurred is now stored as the last tick for the next beat changed time calculation.
      				lastTick = midiEvent.getTick();
      				//This BPM is now set as the last bpm for the next time calculation for the next beat change.
      				lastBPMInSong = BPM;
      				//set the time this beat change occurred at.
      				tickWithBPMChangesToTime.put(midiEvent.getTick(), lastTime);	
      			}
      			else if( (midiByte[1] & 0xff) == 88 )
      			{
      				Float[] a = {(float)(midiByte[3] & 0xff),(float)(midiByte[4] & 0xff)};
      				tickWithTimeSignatureChanges.put(midiEvent.getTick(), a);
      			}
      		}
		}
		
		/*
		 * For time signature calculations. Set the default values first if non existed
		 */
		if( tickWithTimeSignatureChanges.keySet().isEmpty() )
		{
			Float[] a = {4.0f,4.0f};
			tickWithTimeSignatureChanges.put(0l, a);
		}
		
		//Calculate time for the parts in the song where the time signature changes
		for( Long l  : tickWithTimeSignatureChanges.keySet() )
		{
			//Get the time signature that the song changed to
			Float[] timeSignature = tickWithTimeSignatureChanges.get(l);
			
			//calculate the time at which the time signature changed
			double time = calculateTime(l);
			tickWithTimeSignatureChangesToTime.put(l, time);
			
			//need to find the mpq where the time signature change fits into
			long mpq = findCorrespondingBeatInterval(l);
			
			//calculate the bar size
			float lengthOfBarInSeconds = (timeSignature[0] * (mpq/1000000.0f) *4 )/timeSignature[1];

			//set the bar size in seconds
			tickWithLengthOfBarInSeconds.put(l, lengthOfBarInSeconds);
			//set the bar size in midi ticks
			long beatInterval =  findCorrespondingBeatInterval(l);
			long lengthOfBarInTicks = (long) ((((double)lengthOfBarInSeconds*1000000) * sequence.getResolution()) / beatInterval);
			tickWithLengthOfBarInTicks.put(l,lengthOfBarInTicks);			
		}
		
		/*
		 * With this next part, we calculate the amount of bars in each interval. Atm, we have the ticks where
		 * the time signatures changed and I also stored the length of a bar in midi ticks, which are stored in
		 * the variable: tickWithLengthOfBarInSeconds. The point of this next part is that a time signature 
		 * can change many times in a song, and therefore the length of a bar will change. So, we have to calculate 
		 * the amount of bars in each interval.
		 * 
		 * For an example, take the following information:
		 * <tick> <tick to time> <length of bar in seconds>  <length of bar in ticks>
		 * 	0------0.0------------2.0-----------------------------1920
		 *	960----1.0------------4.0-----------------------------3840
		 *	39360--41.0-----------4.6-----------------------------4479
		 * 
		 * Now, as shown above, are the ticks where the time signature changes in a song and consequently, 
		 * the length of the bar changes. To read this data, look at <tick>=960 the <length of the bar in ticks>=3840
		 * so from <tick>=960 to <tick>=39360, we use the <length of the bar in ticks>=3840 to find out how many of those
		 * bars are in that interval. The math goes: (<ceiling Tick> - <floor tick>)/<length of bar in ticks>. This gives us
		 * the amount of bars tick length 3840 between interval 39360 and 960.
		 */
		Object[] ticksWhereSignatureChanged = tickWithTimeSignatureChanges.keySet().toArray();
		long ceiling;
		long floor;
		long tickChunk;
		int numberOfBarsInInterval;
		//System.out.println("ticksWhereSignatureChanged: " + ticksWhereSignatureChanged.length);
		for( int i = 0; i < ticksWhereSignatureChanged.length; i++)
		{
			if( i + 1 < ticksWhereSignatureChanged.length )
			{
				ceiling = (Long) ticksWhereSignatureChanged[i+1];
				floor = (Long) ticksWhereSignatureChanged[i];
			}
			else
			{
				ceiling = sequence.getTickLength();
				floor =  (Long) ticksWhereSignatureChanged[i];
			}
			
			//System.out.println("ceiling: " + " " + ceiling);
			//System.out.println("floor: " + " " + floor);
			
			tickChunk = ceiling - floor;
			Long lengthOfBarInInterval =  tickWithLengthOfBarInTicks.get(ticksWhereSignatureChanged[i]);
			
			//System.out.println("lengthOfBarInInterval: " + lengthOfBarInInterval);
			numberOfBarsInInterval =  (int) (tickChunk / lengthOfBarInInterval);
			
			//Now, we have the number of bars in this interval, now we have to set up the variable for use
			long resultant = floor + lengthOfBarInInterval;
			for( int j = 0; j < numberOfBarsInInterval; j++ )
			{
				//System.out.println(floor + "---" + resultant);
				//startOfBarsWithChangesInBarHash.put(floor, new Integer[16] );
				startOfBarsWithChangesInBarTree.put(floor, new Integer[16] );
				floor = resultant;
				resultant = resultant + lengthOfBarInInterval;
			}
		}
	}
	
	/**
	 * This method is used to figure out where the parameter "long timeSignatureTick", which is a Midi tick, belongs 
	 * within the beat changes. This is almost the exact same method as the calculateTime method that converts a Midi tick 
	 * to seconds but there is a difference; this method doesn't calculate time. It only finds out where the timeSignatureTick 
	 * fits in with the beat changes and returns the beat in Microseconds per Quarter Notes, not BPM. For example:
	 * 
	 * The beat changed at ticks: 23,45,56,67,89 and the MPQ are (for simplicity):1,2,3,4,5.
	 * TICK		23,45,56,67,89
	 * MPQ  	1, 2, 3, 4, 5
	 * 
	 * Now, based on the data above, we want to find out where the tick 58 fits in and then return the MPQ where
	 * they match. This method will compare the tick to a pair of ticks in this such manner: (23,45)(45,56)(56,67)(67,89) and lastly (89, inf)
	 * * Is 58 >= 23 and < 45? NO *
	 * * Is 58 >= 45 and < 56? NO *
	 * * Is 58 >= 56 and < 67? YES* Therefore, at this point, the method returns 4.
	 * 
	 * @param timeSignatureTick
	 * @return
	 */
	private Long findCorrespondingBeatInterval( long timeSignatureTick )
	{
		Object[] ticks = tickWithBPMChanges.keySet().toArray();
		Object[] mpq = tickWithMPQChanges.values().toArray();
		
		long lastRelativeMPQ = 0;
		
  	    for( int i = 0; i < ticks.length; i++ )
  	    {
  	    	if( i + 1 < ticks.length )
  	    	{
  	    		if( timeSignatureTick >= (Long)ticks[i] && timeSignatureTick < (Long)ticks[i+1] )
  	    		{
  	    			lastRelativeMPQ = (Long)mpq[i];
  	    			break;
  	    		}
  	    	}
  	    	else
  	    	{
  	    		lastRelativeMPQ = (Long)mpq[i];
  	    		break;
  	    	}
  	    }
  	    return lastRelativeMPQ;
	}
	
	/**
	 * The method that calls this, figures out the amount of changes that each pipe has. When this method 
	 * is called, its only job is to figure out how significant each pipe's movement is. IE. Channel 1's pipe 3
	 * only comprises of 2 percent of the total movement in the song. If this change is below a given threshold then 
	 * the pipe isn't played in the visualizer.
	 * The noteChangePercentage is how this is figured out. [channel][pipes].
	 * 
	 * @return A 2D array where i acts as one of the 16 channels and j acts as one of the 3 pipes. [channel][pipe].
	 */
	private double[][] calculateNoteChangePercentage()
	{
		double[][] noteChangePercentage = new double[16][3];
		double largestAmountOfChanges = -100;
		
		//Find the largest amount of changes. Through a O ( n ) running time
		//"search for largest number"
		for(int i = 0; i < changes.length; i++ )
		{
			for(int j =0; j < changes[i].length; j++ )
			{
				if( changes[i][j] > largestAmountOfChanges){ largestAmountOfChanges = changes[i][j];}
			}
		}
		
		//find the ratio of each pipes' movements in ratio to the largest amount of movements
		for(int i = 0; i < changes.length; i++ )
		{
			for(int j =0; j < changes[i].length; j++ )
			{
				noteChangePercentage[i][j] = changes[i][j] / (double) largestAmountOfChanges;
				noteChangePercentage[i][j] *= 100;
			}
		}
		
		return noteChangePercentage;
	}

	/**
	 * During the preprocessing, each channel's role as an instrument was calculated.
	 * 
	 * @return A string array that holds what each channel's role is.
	 */
	public String[] getInstruments()
	{
		return instruments;
	}
	
	/**
	 * This returns a data structure that holds the ticks where a BPM change occurred. The midi tick where this
	 * occurs is the key and the corresponding value is the BPM it was changed to. Therefore, as an example,
	 * at tick 10, the BPM changed to 156.
	 * 
	 * @return
	 */
	public TreeMap<Long, Float> getTicksWithBPMChanges()
	{
		return tickWithBPMChanges;
	}
	
	/**
	 * Returns a data structure that holds the ticks where the BPM changed and the time in second where this occurred.
	 * The key for this HashMap is the midi tick and the value is the midi tick converted to seconds. Therefore, as an example,
	 * at tick 10, which is 15 seconds is where a BPM change occurred. Therefore, 10->15 TADA
	 * 
	 * @return
	 */
	public HashMap<Long, Double> getTicksWithBPMChangesToTime()
	{
		return tickWithBPMChangesToTime;
	}
	
	/**
	 * Returns a String array that holds the note changes for a channel. These note changes must be over a certain threshold
	 * in order to be counted (Please look at the comments in startPreprocessing()). Please take note that a position in the array
	 * does not correspond to the channel. For example, channel 9 is the beat channel but the array at position 9 does not correspond
	 * to the changes in the beat channel. The reason for this is because, when we set the pipes' x-positions in 3d space, we want the
	 * pipes that move the most to be closest to the front. You might be asking "how do I know what change corresponds to what channel!?", well
	 * my friend, I will tell you. In this array I store BOTH the channel WITH it's movements as a string separated with an underscore. So,
	 * when I sort the array, I sort by the first value and afterwards the array is scrambled but because of the second number.
	 * 
	 * <channel> -> <what's stored> THEN SORT    <position isn't the channel anymore> -> <what's stored>
	 * [0]		 -> 13_0														[0]  -> 14_1
	 * [1]		 -> 14_1														[1]  -> 13_0
	 * [2]		 ->  2_2														[2]  ->  4_3
	 * [3]		 ->  4_3														[3]  ->  2_2
	 * 
	 * As it is shown above, after the array is sorted, we can still find out what value corresponds to what channel. 
	 * When the Player.java object sets up the pipes, it will loop through this array from 0 to n-1 while parsing
	 * the string by an underscore. Consequently, on the X-axis, the pipes will be set up in order of 1,0,3,2
	 * 
	 * P.S: I used my own comparator for the sorting. The comparator allows the array to be sorted ONLY by its change amount 
	 * and positioned in descending order. The comparator is in the Utils package
	 * 
	 * @return
	 */
	public String[] getChangesByAmountString()
	{
		return changesByAmountString;
	}
	
	/**
	 * This method specifically sets up the visualization. It handles initial pipes placement, colour 
	 * and dimensions. Please note that the partSize handles the max length of the pipes. An overview of 
	 * how the pipes are as follows: The pipes are set up in order by most amount of movements at the front.
	 * The changesByAmountString variables is what holds the channel number and the amount of movements
	 * which are sorted. So if the movements are: 300_0, 299_10, 200_11, 199_14 etc, then the channels are
	 * set up in this order: 0,10,11,14 etc
	 */
	private void setUpVisualizer()
	{
		GUI gui = controller.getGUI();
		Object[] channels =  channelsUsed.keySet().toArray();
		int[] c = new int[channels.length];
		for( int i = 0; i < channels.length; i++ )
		{
			c[i] = ((Integer)channels[i]).intValue();
		}
		gui.getVisualizer().setChannelsUsed(c);
		gui.getVisualizer().resetVisualizer();
	}
	
    public void done() 
    {
    	GUI gui = controller.getGUI();
    	Player player = controller.getPlayer();
    	gui.setEnabledPlayerFrame(true);
    	//gui.getAnimator().resume();
    	gui.resumeAnimator();
		closeButton.setEnabled(true);
		if( autoPlayAfterPreprocessing ){ player.play();}
    }
}

