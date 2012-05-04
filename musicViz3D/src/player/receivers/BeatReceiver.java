package player.receivers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;

import player.messages.OpenGLMessageBeat;
import processors.BeatProcessor;
import controller.Controller;

/**
 * This class receives all MIDI notes played; however, it only processes
 * note on events for the beat channel. The beat channel is 10 or 9.
 * It is 10 when referring to the channels as 1 to 16. It is 9, when
 * referring to the channels as 0 to 15.
 * <p>
 * Here is the reference to the MIDI standard.
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
public class BeatReceiver extends Thread implements Receiver
{
	private Controller controller;
	private BeatProcessor beatProcessor;
	private AtomicBoolean playBeats;
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	
	/**
	 * Initialises the object.
	 * @param controller
	 */
	public BeatReceiver( Controller controller )
	{
		this.controller = controller;
		this.beatProcessor = new BeatProcessor();
		this.playBeats = new AtomicBoolean(true);
		this.handOffQueue = new LinkedBlockingQueue<MidiMessage>();
	}
	
	public void close() 
	{
	}

	/**
	 * This method receives a MIDI message in real time and only
	 * processes the note on messages sent to channel 9.
	 * <p>
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
			JOptionPane.showMessageDialog(null, "BeatReceiver::send. Could not place " +
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
		byte[] m;
		
		while( true )
		{
			try 
			{
				message = handOffQueue.take();
				
				if( !this.playBeats.get() )
				{
					return;
				}

				//First check for note ons. There is no need to handle note offs
				if ( message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) == 9 )
				{
					m = message.getMessage();
					OpenGLMessageBeat beat = beatProcessor.processBeat(m[1] & 0xff, m[2] & 0xff);
					this.controller.getGUI().getVisualizer().concurrentMessageQueue.get(9).add(beat);
				}
			} 
			catch (InterruptedException e)
			{
				JOptionPane.showMessageDialog(null, "BeatReceiver::run. Could not retrieve " +
						"MidiMessage from LinkedBlockingQueue due to an interrupt exception." );
				System.exit(0);
			}
		}
	}
	
	/**
	 * Returns the AtomicBoolean that allows for multiple threads to enable
	 * and disable the beats from playing into the visualzer.
	 * @return
	 */
	public AtomicBoolean getIsPlayingBeats()
	{
		return this.playBeats;
	}
}
