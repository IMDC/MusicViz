package player.messages;

/**
 * Message is specifically used to send a Pitch change message.<p> This alerts the visualizer that
 * a pitch change has happened for a certain channel, the pitch offset and range of the midi
 * pitch wheel for the affected channel.
 * <p>
 * @author Michael Pouris
 *
 */
public class OpenGLMessagePitchChange extends OpenGLMessage
{
	private double offset;
	private int channel;
	private int rangeOfPitchValues;
	
	/**
	 * Creates a new pitch bend message with the given pitch change offset
	 * for the specific channel and the range of pitches allowed in the channel.
	 * <p>
	 * @param offset the offset to bend all pipes in the channel
	 * @param channel the channel that the pitch bend is dedicated to
	 * @param rangeOfPitchValues the range of pitch values allowed for the channel
	 */
	public OpenGLMessagePitchChange( double offset, int channel, int rangeOfPitchValues)
	{
		this.messageType = PITCHCHANGES;
		this.offset = offset;
		this.channel = channel;
		this.rangeOfPitchValues = rangeOfPitchValues;
	}
	
	/**
	 * Returns the pitch offset.
	 * 
	 * @return The pitch offset
	 */
	public double getOffset()
	{
		return offset;
	}
	
	/**
	 * Returns the channel the message is meant for.
	 * 
	 * @return the channel the message affects
	 */
	public int getChannel()
	{
		return channel;
	}
	
	/**
	 * Returns the range of pitch values for a specific channel
	 * 
	 * @return the range of pitch channels for a channel
	 */
	public int getRangeOfPitchValues()
	{
		return rangeOfPitchValues;
	}
}
