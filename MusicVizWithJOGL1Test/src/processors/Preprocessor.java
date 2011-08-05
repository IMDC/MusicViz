package processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import player.MidiNoteReceiver;
import player.Note;
import utilities.StringComparator;

/**
 * This class provides the functionality to find the minimum and maximum notes
 * that each channel contains for the current song. 
 * 
 * @author Michael Pouris
 *
 */
public class Preprocessor 
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
	 * channelsUsed: Used by the player to set up the visualizer
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
	
	public final static String defaultString = "-10_-10";
	
	private Track allMidiTracks[];
	private TreeMap<Integer, Boolean> channelsUsed = new TreeMap<Integer, Boolean>();
	private String[] instruments = new String[16];
	private Sequence sequence;
	private float lastBPMInSong;

	//Used for keeping track of note changes
	private int[][] changes = new int[16][3];
	private int[][] lastNote = new int[16][3];
	private MidiNoteReceiver receiver;
	//this variable is also used to keep track of the changes by a certain amount. IE 
	//the note changed by 10, this will be used to initially place the instruments. Hopefully
	//we will be able to keep the less changes but overall more important ones more visible.
	//HOFFENTLICH (means "hopefully" for those who speak english) we can see the melody
	private int[] changesByAmount = new int[16];
	private String[] changesByAmountString = new String[16];
	
	//Used for beat changes
	private TreeMap<Long, Float> tickWithBPMChanges = new TreeMap<Long, Float>();
	private TreeMap<Long, Long> tickWithMPQChanges = new TreeMap<Long, Long>();//This is for bars
	private HashMap<Long,Double> tickWithBPMChangesToTime = new HashMap<Long, Double>();
	
	//Time signature changes use for bars
	private TreeMap<Long,Float[]> tickWithTimeSignatureChanges = new TreeMap<Long, Float[]>();
	private HashMap<Long,Double> tickWithTimeSignatureChangesToTime = new HashMap<Long, Double>();
	private HashMap<Long, Float> tickWithLengthOfBarInSeconds = new HashMap<Long, Float>(); 
	private HashMap<Long, Long> tickWithLengthOfBarInTicks = new HashMap<Long, Long>(); 
	//These variables are for when the song is playing
	private TreeMap<Long,  Integer[] > startOfBarsWithChangesInBarTree = new TreeMap<Long,Integer[] >();
	private TreeMap<Long,  String[] > startOfBarsWithChangesInBarTreeString = new TreeMap<Long,String[] >(); //this is what we use for bar changes
	
	//These variables are used for indexing the notes for actual use.
	//This one is just for storing the notes after they are grouped. THEY ARE NOT SORTED IN THE PROPER ORDER
	//OF BEING PLAYED
	private ArrayList<Note> notes;
	//These are the notes from the arraylist but now are sorted. Must be done because cannot sort in an arraylist.
	private Note[] sortedNotes;
	//This is the final data structure that the program will use. THIS IS THE IMPORTANT ONE, the variables named notes and sortedNotes
	//are just steps up to this holy grail of an inefficient algorithm. Please don't kill me Dr. Soutchanksi, I will fix it.
	private HashMap<Long, LinkedList<Note>> notesForTheCurrentSong;
	
	/**
	 * Receives all the midi tracks for the song for use in preprocessing.
	 * @param allMidiTracks
	 */
	public Preprocessor( MidiNoteReceiver receiver, Track allMidiTracks[], Sequence sequence, float firstBPMInSong )
	{
		this.allMidiTracks = allMidiTracks;
		this.receiver = receiver;
		this.sequence = sequence;
		this.lastBPMInSong = firstBPMInSong;
		for( int i = 0; i < instruments.length; i++ )
		{
			instruments[i] = "Not set";
			changesByAmountString[i] = defaultString;
		}
		this.notes = new ArrayList<Note>();
		this.notesForTheCurrentSong = new HashMap<Long, LinkedList<Note>>(); 
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
	 * Searching through the midi tracks for note on/off and program events.
	 * 
	 * @return
	 */
	public void startPreprocessing()
	{
		Track singleTrack;
		MidiMessage midiMessage;
		byte[] midiByte;
		int status;
		int channel;	
		int currentNote;
		MidiEvent midiEvent;
		
		processMidiMetaEvents();
		
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
	    	    
	    	    if( channel == 9 )
	    	    {
	    	    	channelsUsed.put(channel, true);
	    	    }
	    	    
	      		//if the status indicates that it is a note on we do preprocessing
	    	    if ( status >= 144 && status <= 159 && channel != 9 )
   	    	    {
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
	   	    	    	//For hong's looming idea (the initial placement of the pipes)
	   	    	    	if( (currentNote - lastNote[channel][pipe]) >= 10 )
	   	    	    	{
	   	    	    		changesByAmount[channel]++;
	   	    	    		
	   	    	    	}
	   	    	    	lastNote[channel][pipe] = currentNote;
	   	    	    	
	   	    	    	//This is for the note groupings
		   	    	    Note n = new Note();
	   	   	    	    n.setFirstEvent(midiEvent);
	   	   	    	    n.setChannel(channel);
	   	   	    	    //now we need to set the beginning time
	   	   	    	    double startTime = calculateTime( midiEvent.getTick() );
	   	   	    	    n.setStartTimeInSeconds(startTime);
	   	   	    	    notes.add(n);
	    	    	}
	    	    	else
	    	    	{
	    	    		//when a note on with a velocity of 0 is used, that is considered a note off. Therefore it's the end of the note.
	    	    		findCorrespondingFirstNote((midiByte[1] & 0xff), channel, midiEvent);
	    	    	}
	    	    	findNoteChangesInBarAndChannel(midiEvent, channel);
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
	   		    	
	   		    	//an actual note off event that we need to pair.
	   		    	findCorrespondingFirstNote((midiByte[1] & 0xff), channel, midiEvent);
	   		    	findNoteChangesInBarAndChannel(midiEvent, channel);
	    		}
	    	    else if( status >= 192 && status <= 207 )//instrument event 
	    	    {
	    	    	if ((status - 192) != 9 )
   	      			{
	    	    		instruments[(status - 192)] = ChannelProcessor.getChannelName((midiByte[1] & 0xff));
   	      			}
	    	    	else
	    	    	{
	    	    		instruments[(status - 192)] = "drums";
	    	    	}
	    	    }
			}
		}
		
		//Sorts the notes by the tick in ascending order
		sortedNotes = notes.toArray(new Note[0]);
		Arrays.sort(sortedNotes);
		
		//Once the notes are sorted, this method indexes the HashMap by the tick
		//and if there are many notes to one tick then they are put into a List.
		groupNotesByTicks();

		//Once the groupNotesByTicks method is finished, (which works on the variable "notesForTheCurrentSong")
		//it is set in the receiver for further use.
		receiver.setNotesForTheCurrentSong(notesForTheCurrentSong);

		//Calculate which pipes will be played based on a percent threshold. Please see method description.
		double[][] calculatedNoteChangePercentage = calculateNoteChangePercentage();
		receiver.setChannelsToUse(0.0,calculatedNoteChangePercentage);
		
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
					temp[i]=-10;
				}
				labels[i] = temp[i] +"_"+i;
			}
			Arrays.sort(labels, new StringComparator() );
			startOfBarsWithChangesInBarTreeString.put(l, labels);
			labels = new String[16];
		}
		
		//Set the data structure in the receiver for bar changes
		receiver.setStartOfBarsWithChangesInBarTreeString(startOfBarsWithChangesInBarTreeString);
	}

	/**
	 * This method finds out what bar the "midiEvent" belongs to. This is done the EXACT same way as in calculateTime(...),
	 * findCorrespondingBeatInterval(...) and also in processMidiMetaEvents(...). Please read the comments for those methods because
	 * by now the algorithm should be drilled into our heads.
	 * 
	 * @param midiEvent
	 * @param channel
	 */
	private void findNoteChangesInBarAndChannel( MidiEvent midiEvent, int channel )
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
	}
	
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
		 * For time signature calculations
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

			//System.out.println(l + "----"+ tickWithTimeSignatureChangesToTime.get(l) + "----" + lengthOfBarInSeconds + "----" + lengthOfBarInTicks);
			
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
			tickChunk = ceiling - floor;
			Long lengthOfBarInInterval =  tickWithLengthOfBarInTicks.get(ticksWhereSignatureChanged[i]);
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
		
		//Find the largest amount of changes.
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
	 * During pre-processing, the channels that are used are recorded, this is used to setup the visualiser
	 * because the number of active channels are important in setting the position and colour of the pipes.
	 * 
	 * @return A TreeMap which stores the channels used.
	 */
	public TreeMap<Integer, Boolean> getChannelsUsed()
	{
		return channelsUsed;
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
}
