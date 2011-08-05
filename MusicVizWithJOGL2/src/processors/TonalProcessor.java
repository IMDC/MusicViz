package processors;

import player.messages.OpenGLMessageTonal;

/**
 * There is one tonal processor per channel and each tonal processor allows for 10 separate notes to be displayed on the screen at one time.
 * 
 * 
 * @author Michael Pouris
 *
 */
public class TonalProcessor 
{	
	/**
	 * This method calculates two major things in this order, the blade number and 2. whether the blade will be a note on 
	 * or note off blade.
	 * 
	 * @param currentNote
	 * @param velocity
	 * @param channel
	 * @return
	 */
	/*public String processNote(int currentNote, int velocity, int channel )
	{
		OpenGLMessageTonal tonalMessage;
		
		String noteMessage = null;
		int pipe = 0;
		String message = null;

		pipe = getPipe(currentNote);
		
		if( velocity == 0 )
		{ 
			noteMessage = "noteOff";
			tonalMessage = new OpenGLMessageTonal(OpenGLMessageTonal.NOTEOFF, 0, pipe, currentNote);
		}
		else
		{
			noteMessage = "noteOn";
			tonalMessage = new OpenGLMessageTonal(OpenGLMessageTonal.NOTEON, velocity, pipe, currentNote);
		}
		
		
		
		message =  noteMessage + "_" + "fan_" + channel + "_" + pipe + "_" + velocity + "_" + currentNote;
		return message;
	}*/
	
	public OpenGLMessageTonal processNote(int currentNote, int velocity, int channel )
	{
		OpenGLMessageTonal tonalMessage = null;
		int pipe = getPipe(currentNote);
		
		if( velocity == 0 )
		{ 
			tonalMessage = new OpenGLMessageTonal(OpenGLMessageTonal.NOTEOFF, 0, pipe, currentNote, channel);
		}
		else
		{
			tonalMessage = new OpenGLMessageTonal(OpenGLMessageTonal.NOTEON, velocity, pipe, currentNote, channel);
		}

		return tonalMessage;
	}
	
	public static int getPipe( int currentNote )
	{
		int pipe = -10;
		if( currentNote >= 0 && currentNote <= 41 )
		{
			pipe = 0;
		}
		else if( currentNote >= 42 && currentNote <= 83 )
		{
			pipe = 1;
		}
		else if( currentNote >= 83 && currentNote <= 127 )
		{
			pipe = 2;
		}
		return pipe;
	}

}
