package processors;

import player.Player;
import player.messages.OpenGLMessageBeat;
import player.receivers.BeatReceiver;
import visualizer.ConcurrentVisualizer;

/**
 * This class provides information pertaining to the mappings of MIDI
 * drums to the visualiser.
 * <p>
 * As the MIDI notes are played by the MIDI Sequencer in {@link Player},
 * the notes are passed to each receiver. The {@link BeatReceiver} has
 * an instance of this class and uses it to convert the MIDI beat notes
 * to a construct that the {@link ConcurrentVisualizer} can use.
 * 
 * @author Michael Pouris
 *
 */
public class BeatProcessor 
{
	public static final String KICK = "kick";
	public static final String HAND = "hand";
	public static final String TOMCYN = "tomcyn";
	public static final String SNARE = "snare";
	public static final String HAT = "hat";
	
	/**
	 * Creates a new {@link OpenGLMessageBeat} based on the parameters.
	 * <p>
	 * The MIDI note is converted from a normal MIDI note to a specific
	 * visual construct using the {@link #getCorrespondingPipeFromNote(int)}.
	 * <p>
	 * 
	 * @param note the note to use to convert from MIDI to a visualisation
	 * @param velocity the volume the note was played at.
	 * 
	 * @return A message tailored for processing by the OpenGL visualiser.
	 */
	public OpenGLMessageBeat processBeat( int note, int velocity )
	{
		int pipe = getCorrespondingPipeFromNote(note);
		
		OpenGLMessageBeat beat;
		if(velocity == 0 )
		{
			beat = null;
		}
		beat = new OpenGLMessageBeat(pipe, velocity);
		
		return beat;		
	}
		
	/**
	 * Calculates the drum instance based on the note. Notes range from 0 - 127 therefore making
	 * 128 notes but for percussion, notes 35 - 81 are used.
	 * 
	 * @param note
	 * @return
	 */
	public static String getDrumInstance( int note )
	{
		String typeOfDrum = null;
		
		// For kick
		if( note == 35 || note == 36)
		{
			typeOfDrum = KICK;
		}
		// for snare
		else if( note == 37 || note == 38 || note == 40)
		{
			typeOfDrum = SNARE;
		}
		else if( note == 39 || note == 56 || (note >= 60 && note <= 69) || (note >= 73 && note <= 77) )
		{
			typeOfDrum = HAND;
		}
		else if( note == 41 || note == 43 || note == 45 || (note >= 47 && note <= 53) || note == 55 || note == 57 || note == 59)
		{
			typeOfDrum = TOMCYN;
		}
		else if( note == 42 || note == 44 || note ==  46)
		{
			typeOfDrum = HAT;
		}
		
		if( typeOfDrum != null )
		{
			return typeOfDrum;
		}
		return null;
	}
	
	/**
	 * Converts from a MIDI note to a drum instance.
	 * <p>
	 * MIDI dedicates channel 9 to drums and each note for the channel
	 * constitutes a drums. There are too many drums to map, therefore
	 * they are condensed into 5 different groupings. The visualiser
	 * uses these mappings.
	 * 
	 * @param note the MIDI note to convert into a visual construct
	 * @return the specific index that the beat maps to
	 */
	public static int getCorrespondingPipeFromNote(int note)
	{
		int pipe = 3;
		
		if( note == 35 || note == 36)
		{
			pipe = 0;
		}
		else if( note == 37 || note == 38 || note == 40)
		{
			pipe = 1;
		}
		else if( note == 39 || note == 56 || (note >= 60 && note <= 69) || (note >= 73 && note <= 77) )
		{
			pipe = 2;
		}
		else if( note == 41 || note == 43 || note == 45 || (note >= 47 && note <= 53) || note == 55 || note == 57 || note == 59)
		{
			pipe = 3;
		}
		else if( note == 42 || note == 44 || note ==  46)
		{
			pipe = 4;
		}
		
		return pipe;
	}

}
