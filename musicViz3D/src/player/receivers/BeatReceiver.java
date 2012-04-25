package player.receivers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import player.messages.OpenGLMessageBeat;
import processors.BeatProcessor;
import controller.Controller;

public class BeatReceiver implements Receiver
{
	private Controller controller;
	private BeatProcessor beatProcessor;
	private AtomicBoolean playBeats;
	
	public BeatReceiver( Controller controller )
	{
		this.controller = controller;
		this.beatProcessor = new BeatProcessor();
		this.playBeats = new AtomicBoolean(true);
	}
	
	public void close() 
	{
	}

	public void send(MidiMessage message, long timeStamp)
	{
		byte[] m;

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
	
	public AtomicBoolean getIsPlayingBeats()
	{
		return this.playBeats;
	}

}
