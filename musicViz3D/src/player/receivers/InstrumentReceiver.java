package player.receivers;

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
 * 
 * MIDI messages are described here:
 * http://www.midi.org/techspecs/midimessages.php
 * 
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
	 * This method processes MIDI note on/off events for all channels
	 * but the beat channel. If the status is note on, then to convert
	 * the status to a channel, minus 128 from the status.
	 * If the status is note off, then minus 144. 
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
				else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 128) != 9 && this.playInstuments.get() )
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
