package player.messages;

/**
 * Contains the attributes used to display a beat even in the visualizer. <p> Since this
 * class inherits from the OpenGLMessage class, it will have a message type that is automatically set 
 * in the constructor which is used to identify that this is a beat event. 
 * <p>
 * @author Michael Pouris
 *
 */
public class OpenGLMessageBeat extends OpenGLMessage
{
	private int pipe;
	private int volume;
	
	public OpenGLMessageBeat(int pipe, int volume)
	{
		this.messageType = BEAT;
		this.pipe = pipe;
		this.volume = volume;
	}
	
	public int getPipe()
	{
		return pipe;
	}
	
	public int getVolume()
	{
		return volume;
	}
}
