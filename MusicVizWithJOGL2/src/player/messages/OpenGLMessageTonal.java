package player.messages;


/**
 * Can be used as a NoteOn or NoteOff event for tones (every channel but channel 9).
 * 
 * @author Michael Pouris
 *
 */
public class OpenGLMessageTonal extends OpenGLMessage
{
	private int volume;
	private int pipe;
	private int note;
	private int channel;
	
	public OpenGLMessageTonal(int messageType, int volume, int pipe, int note, int channel )
	{
		this.messageType = messageType;
		this.volume = volume;
		this.pipe = pipe;
		this.note = note;
		this.channel = channel;
	}
	
	public int getVolume()
	{
		return volume;
	}
	
	public int getPipe()
	{
		return pipe;
	}
	
	public int getNote()
	{
		return note;
	}

	public int getChannel()
	{
		return channel;
	}
}
