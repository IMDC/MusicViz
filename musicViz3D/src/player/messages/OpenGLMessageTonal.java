package player.messages;


/**
 * Can be used as a NoteOn or NoteOff event for tones (every channel but channel 9).
 * <p>
 * @author Michael Pouris
 *
 */
public class OpenGLMessageTonal extends OpenGLMessage
{
	private int volume;
	private int pipe;
	private int note;
	private int channel;
	
	/**
	 * Creates a new tonal message such as note on or off with the specific
	 * volume for a specific pipe on a certain channel.
	 * <p>
	 * @param messageType the message such as note on of off
	 * @param volume the volume of the note played
	 * @param pipe the pipe that the message is for
	 * @param note the note played
	 * @param channel the channel the note is for
	 */
	public OpenGLMessageTonal(int messageType, int volume, int pipe, int note, int channel )
	{
		this.messageType = messageType;
		this.volume = volume;
		this.pipe = pipe;
		this.note = note;
		this.channel = channel;
	}
	
	/**
	 * Returns the volume for the MIDI note.
	 * <p>
	 * @return the volume
	 */
	public int getVolume()
	{
		return volume;
	}
	
	/**
	 * Returns the specific pipe in the channel that the message is meant for.
	 * <p>
	 * The visualisation has 16 channels and each channel has 2 pipes.
	 * 
	 * @return the pipe
	 */
	public int getPipe()
	{
		return pipe;
	}
	
	/**
	 * Returns the MIDI message's note.
	 * <p>
	 * @return
	 */
	public int getNote()
	{
		return note;
	}

	/**
	 * Returns the channel that the message is for.
	 * <p>
	 * @return the channel
	 */
	public int getChannel()
	{
		return channel;
	}
}
