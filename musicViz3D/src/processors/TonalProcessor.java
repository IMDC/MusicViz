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
		else if( (currentNote >= 42 && currentNote <= 127)  )
		{
			pipe = 1;
		}
		return pipe;
	}

}
