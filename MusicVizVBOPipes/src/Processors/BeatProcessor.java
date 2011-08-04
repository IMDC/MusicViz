package Processors;

import Player.Messages.OpenGLMessageBeat;

/**
 * This class is specifically used to process a beat note on/off event. The two main behaviors of this class 
 * is to calculate the starting interval of the beat object and to calculate which beat the note actually belongs to.
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
	 * The main method in this class. It's used to calculate the intensity of the current beat and return 
	 * the message that will be used.
	 * 
	 * @param note
	 * @param velocity
	 * @return
	 */
	/*public String processBeat( int note, int velocity )
	{
		String message = null;
		int pipe = getCorrespondingPipeFromNote(note);
		
		if( velocity == 0 )
		{
			return null;
		}

		message = "beatEvent_pipe_" + pipe + "_velcoity_" + velocity;
		
		OpenGLMessageBeat beat = new OpenGLMessageBeat(pipe, velocity);
		
		return message;		
	}*/
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
	
	public static int getCorrespondingPipeFromNote(int note)
	{
		int pipe = -10;
		
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
