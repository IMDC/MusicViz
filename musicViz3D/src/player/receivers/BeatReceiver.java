package player.receivers;

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
 * 
 * Here is the reference to the MIDI standard.
 * http://www.midi.org/techspecs/midimessages.php
 * 
 * @author Michael Pouris
 *
 */
public class BeatReceiver extends Thread implements Receiver
{
	private Controller controller;
	private BeatProcessor beatProcessor;
	private AtomicBoolean playBeats;
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	
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
	
	public AtomicBoolean getIsPlayingBeats()
	{
		return this.playBeats;
	}

}
