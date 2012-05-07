package player.receivers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.swing.JOptionPane;

import utilities.Utils;

import controller.Controller;

/**
 * This class receives all MIDI notes played; however, it only
 * processes MIDI ticks in order to keep track of the time using
 * the JSlider.
 * <p>
 * The {@link #send(MidiMessage, long)} method adds every {@link MidiMessage} into
 * a {@link ConcurrentLinkedQueue}. This allows the other Thread, the processing
 * thread to take over.
 * <p>
 * @author Michael Pouris
 *
 */
public class ControlReceiver extends Thread implements Receiver
{
	private Controller controller;
	private Sequencer sequencer;
	private int timeTracker;
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	
	public ControlReceiver( Controller controller, Sequencer sequencer )
	{
		this.controller = controller;
		this.sequencer = sequencer;
		this.timeTracker = 0;
		this.handOffQueue = new LinkedBlockingQueue<MidiMessage>();
	}
	
	public void close()
	{
	}

	/**
	 * This method only processes MIDI ticks. It converts the microseconds
	 * to seconds and moves the slider in the GUI accordingly.
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
	 * <p>
	 * This is a separate thread that is always running and waits for a message to be 
	 * added into the queue, therefore not wasting CPU time.
	 */
	public void run()
	{
		while( true )
		{
			try 
			{
				this.handOffQueue.take();
			    int seconds = Utils.microSecondsToSeconds(sequencer.getMicrosecondPosition());
				if( (seconds - this.timeTracker) > 0 )
				{
					this.controller.getGUI().setCurrentValueForSlider(seconds);
				    this.controller.getGUI().updateTimer(Utils.secondsToTime(seconds));
				    this.timeTracker = seconds;
				}
				else if( (seconds - this.timeTracker ) < 0 )
				{
					this.timeTracker = seconds;
				}
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
