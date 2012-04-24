package main;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class MidiReceiver implements Receiver
{
	public void close()
	{
	}

	public void send(MidiMessage message, long l)
	{
		

		if ( message.getStatus() >= 152 )
		{
			System.out.println("Beat");
		}
	}
}
