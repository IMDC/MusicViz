package player.receivers;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import player.messages.OpenGLMessagePitchChange;

import controller.Controller;

/**
 * This class receives all incoming MIDI messages; however, it only
 * processes pitch bend messages. Pitch bend messages have statuses
 * from 224 to 239. The MIDI messages are explained here:
 * http://www.midi.org/techspecs/midimessages.php
 * 
 * @author Michael Pouris
 *
 */
public class PitchReceiver implements Receiver
{
	public final static int MINVALUE = 0x0000;
	public final static int MAXVALUE = 0x3fff;
	
	private Controller controller;
	private int[] initialPitchSettings;
	private int[] rangeOfPitchValues;
	
	public PitchReceiver( Controller controller )
	{
		this.controller = controller;
		this.initialPitchSettings = new int[16];
		this.rangeOfPitchValues = new int[16];
	}
	
	public void close()
	{
	}

	public void send(MidiMessage message, long timeStamp)
	{
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
	
	public void setPitchData(int[] initialPitchSettings,int[] rangeOfPitchValues)
	{
		this.initialPitchSettings = initialPitchSettings;
		this.rangeOfPitchValues = rangeOfPitchValues;
	}

}
