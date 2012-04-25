package player.receivers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import player.messages.OpenGLMessageTonal;
import processors.TonalProcessor;

import controller.Controller;

public class InstrumentReceiver implements Receiver
{
	private Controller controller;
	private TonalProcessor tonalProcessor;
	private AtomicBoolean playInstuments;
	
	public InstrumentReceiver( Controller controller )
	{
		this.controller = controller;
		this.tonalProcessor = new TonalProcessor();
		this.playInstuments = new AtomicBoolean(true);
	}
	
	public void close()
	{
	}

	public void send(MidiMessage message, long timeStamp) 
	{
		int channel;
		byte[] m;
		
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
	
	public AtomicBoolean getIsPlayingInstruments()
	{
		return this.playInstuments;
	}
}
