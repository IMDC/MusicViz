package player.receivers;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;

import utilities.Utils;

import controller.Controller;

public class ControlReceiver implements Receiver
{
	private Controller controller;
	private Sequencer sequencer;
	private int timeTracker;
	
	public ControlReceiver( Controller controller, Sequencer sequencer )
	{
		this.controller = controller;
		this.sequencer = sequencer;
		this.timeTracker = 0;
	}
	
	public void close()
	{
	}

	public void send(MidiMessage message, long timeStamp)
	{
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
}
