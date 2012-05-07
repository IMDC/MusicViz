package player.messages;

/**
 * This is the super class of all the messages this program sends between the MidiNoteReceiver and 
 * the visualizer. 
 * <p>
 * All messages have a message type in common therefore this class only has the types
 * of messages as static variables, the message type accessor and variable. The main reason
 * for designing the class this way is because no matter what the message is (tonal, beat, pitch change etc)
 * I have to be able to queue them in the same queue, which guarantees they get processed in the right order.
 * <p>
 * The message type comes in handy when the messages are popped off the queue; at this point no one knows
 * if the message is a OpenGLMessageTonal, OpenGLMessageBeat, OpenGLMessagePitchChange, etc, so to
 * avoid false casting which will crash EVERYTHING I am able to check the message type which is present 
 * in this class, therefore I can cast to the proper message type.
 * <p>
 * @author Michael Pouris
 *
 */
public class OpenGLMessage 
{
	public static final int NOTEON = 144;
	public static final int NOTEOFF = 128;
	public static final int BEAT = 9;
	public static final int PITCHCHANGES = 224;
	
	protected int messageType;
	
	/**
	 * Returns the message type.
	 * 
	 * @return representing the message type
	 */
	public int getMessage()
	{
		return messageType;
	}
}
