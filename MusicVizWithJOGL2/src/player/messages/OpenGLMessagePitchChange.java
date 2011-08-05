package player.messages;

/**
 * Message is specifically used to send a Pitch change message. This alerts the visualizer that
 * a pitch change has happened for a certain channel, the pitch offset and range of the midi
 * pitch wheel for the affected channel.
 * 
 * @author Michael Pouris
 *
 */
public class OpenGLMessagePitchChange extends OpenGLMessage
{
	private double offset;
	private int channel;
	private int rangeOfPitchValues;
	
	public OpenGLMessagePitchChange( double offset, int channel, int rangeOfPitchValues)
	{
		this.messageType = PITCHCHANGES;
		this.offset = offset;
		this.channel = channel;
		this.rangeOfPitchValues = rangeOfPitchValues;
	}
	
	public double getOffset()
	{
		return offset;
	}
	
	public int getChannel()
	{
		return channel;
	}
	
	public int getRangeOfPitchValues()
	{
		return rangeOfPitchValues;
	}
}
