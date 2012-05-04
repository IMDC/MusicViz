package player.receivers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;

import player.messages.OpenGLMessagePitchChange;

import controller.Controller;

/**
 * This class receives all incoming MIDI messages; however, it only
 * processes pitch bend messages. Pitch bend messages have statuses
 * from 224 to 239. The MIDI messages are explained here:
 * http://www.midi.org/techspecs/midimessages.php
 * <p>
 * Each receiver connected to the java sequencer receives the MIDI messages
 * through the {@link #send(MidiMessage, long)} method. This method runs
 * on the sound thread, therefore any processing within this method will
 * slow down the sound thread and therefore cause bugs. Therefore this class
 * is also a thread. 
 * <p>
 * As {@link #send(MidiMessage, long)} receives the messages, they are automatically
 * added into {@link ConcurrentLinkedQueue}, which allows for more than one thread to
 * safely add and remove from the queue. Therefore the java sound thread can add all
 * messages to the concurrent queue and then the {@link #run()} thread can dequeue 
 * messages and process them without slowing the sequencer thread.
 * <p>
 * @author Michael Pouris
 *
 */
public class PitchReceiver extends Thread implements Receiver
{
	public final static int MINVALUE = 0x0000;
	public final static int MAXVALUE = 0x3fff;
	
	private Controller controller;
	private int[] initialPitchSettings;
	private int[] rangeOfPitchValues;
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	
	public PitchReceiver( Controller controller )
	{
		this.controller = controller;
		this.initialPitchSettings = new int[16];
		this.rangeOfPitchValues = new int[16];
		this.handOffQueue = new LinkedBlockingQueue<MidiMessage>();
	}
	
	public void close()
	{
	}

	/**
	 * This method runs on the java sound thread, therefore it 
	 * passes all messages into queue, which then allows another
	 * thread to safely access and process the messages.
	 * See {@link ConcurrentLinkedQueue} for more details.
	 */
	public void send(MidiMessage message, long timeStamp)
	{
		try 
		{
			this.handOffQueue.put(message);
		}
		catch (InterruptedException e)
		{
			JOptionPane.showMessageDialog(null, "PitchReceiver::send. Could not place " +
					"MidiMessage into LinkedBlockingQueue due to an interrupt exception." );
			System.exit(0);
		}
	}
	
	/**
	 * This method is the thread that dequeues the {@link #handOffQueue}, and processes
	 * the MIDI messages without doing any processing on the sound thread.
	 */
	public void run()
	{
		MidiMessage message;
		while( true )
		{
			try 
			{
				message = this.handOffQueue.take();
				if( message.getStatus() >= 224 && message.getStatus() <= 239 )
			    {
			    	int channel = message.getStatus() - 224;
			    	byte[] m = message.getMessage();
			    	int pitchBend = (m[2] & 0xff) << 7 | (m[1] & 0xff);
			    	double offset =(((double)pitchBend - (double)initialPitchSettings[channel])/(double)MAXVALUE)*rangeOfPitchValues[channel];
			    	OpenGLMessagePitchChange pitchChange = new OpenGLMessagePitchChange(offset, channel,rangeOfPitchValues[channel]);
			    	
				    if( Math.abs(pitchBend - (double)initialPitchSettings[channel]) > 0.5)
				    {
				    	this.controller.getGUI().getVisualizer().concurrentMessageQueue.get(channel).add(pitchChange);
				    }
			    	
			    }
			} 
			catch (InterruptedException e)
			{
				JOptionPane.showMessageDialog(null, "PitchReceiver::run. Could not retrieve " +
						"MidiMessage from LinkedBlockingQueue due to an interrupt exception." );
				System.exit(0);
			}
		}
	}
	
	/**
	 * This class handles pitch change messages. Please see the MIDI standard and
	 * {@link MidiMessage} for more explanation. However, in order to process
	 * these messages, the initial values must be known and the range of values 
	 * must be known as well. This must be set.
	 * <p>
	 * @param initialPitchSettings
	 * @param rangeOfPitchValues
	 */
	public void setPitchData(int[] initialPitchSettings,int[] rangeOfPitchValues)
	{
		this.initialPitchSettings = initialPitchSettings;
		this.rangeOfPitchValues = rangeOfPitchValues;
	}
}
