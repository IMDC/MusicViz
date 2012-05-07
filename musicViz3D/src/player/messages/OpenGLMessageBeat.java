package player.messages;

/**
 * Contains the attributes used to display a beat even in the visualizer. <p> Since this
 * class inherits from the OpenGLMessage class, it will have a message type that is automatically set 
 * in the constructor which is used to identify that this is a beat event. 
 * <p>
 * Please note that in this context for this class, a pipe does represent a instrument pipe, it
 * represents a beat instance number, which is displayed as a doughnut shape. 
 * <p>
 * @author Michael Pouris
 *
 */
public class OpenGLMessageBeat extends OpenGLMessage
{
	private int pipe;
	private int volume;
	
	/**
	 * Creates a new message that pertains to the given pipe with
	 * the given volume.
	 * <p>
	 * @param pipe
	 * @param volume
	 */
	public OpenGLMessageBeat(int pipe, int volume)
	{
		this.messageType = BEAT;
		this.pipe = pipe;
		this.volume = volume;
	}
	
	/**
	 * Returns the beat instance.
	 * <p>
	 * @return the beat instance
	 */
	public int getPipe()
	{
		return pipe;
	}
	
	/**
	 * Returns the volume.
	 * <p>
	 * @return the volume
	 */
	public int getVolume()
	{
		return volume;
	}
}
