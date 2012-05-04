package player.receivers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;

import player.messages.OpenGLMessageTonal;
import processors.TonalProcessor;

import controller.Controller;

/**
 * This class acts as the receiver for all MIDI messages. However,
 * this only processes note on/off events for all channels but the
 * beat channel. 
 * <p>
 * MIDI messages are described here:
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
public class InstrumentReceiver extends Thread implements Receiver
{
	private Controller controller;
	private TonalProcessor tonalProcessor;
	private AtomicBoolean playInstuments;
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	
	public InstrumentReceiver( Controller controller )
	{
		this.controller = controller;
		this.tonalProcessor = new TonalProcessor();
		this.playInstuments = new AtomicBoolean(true);
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
			JOptionPane.showMessageDialog(null, "InstrumentReceiver::send. Could not place " +
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
		int channel;
		MidiMessage message;// = this.test.take();
		byte[] m;
		
		while(true)
		{
			try {
				message = this.handOffQueue.take();

				//First check for note offs, then check for note ons
				if ( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) != 9 )
				{
					channel = message.getStatus() - 128;
					m = message.getMessage();
					OpenGLMessageTonal tonalMessage = this.tonalProcessor.processNote( m[1] & 0xff, 0, channel );
					this.controller.getGUI().getVisualizer().concurrentMessageQueue.get( channel ).add( tonalMessage );
				}
				else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) != 9 && this.playInstuments.get() )
				{
					channel = message.getStatus() - 144;
					m = message.getMessage();
					OpenGLMessageTonal tonalMessage = this.tonalProcessor.processNote( m[1] & 0xff, m[2] & 0xff, channel );
					this.controller.getGUI().getVisualizer().concurrentMessageQueue.get( channel ).add( tonalMessage );
				}
			}
			catch (InterruptedException e)
			{
				JOptionPane.showMessageDialog(null, "InstrumentReceiver::run. Could not retrieve " +
						"MidiMessage from LinkedBlockingQueue due to an interrupt exception." );
				System.exit(0);
			}
		}
	}
	
	public AtomicBoolean getIsPlayingInstruments()
	{
		return this.playInstuments;
	}
}
