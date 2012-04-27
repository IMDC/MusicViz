package processors.threads;

import java.util.TreeMap;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;
import javax.swing.SwingWorker;

import controller.Controller;
import gui.GUI;
import player.Player;
import player.receivers.PitchReceiver;

/**
 * This Class extends SwingWorker which in theory (at the core) is exactly a thread (like the thread class in java).
 * The only exception is that this can be integrated with the JProgressBar. Initially, there was a class called Preprocessor
 * that preprocessed the Midi file, although there was a problem with this. When a song would be processed in the old class,
 * the Swing thread would denote it's resources to the task. This causes the GUI to lag and appear frozen to the user. Now,
 * with this new preprocessor that is in its own thread, the preprocessing task is handed off to this thread, a new window is
 * opened with a progress bar and this thread will send progress change events to update and animate the progress bar.
 * 
 * @author Michael Pouris
 *
 */
public class LightThreadPreprocessor extends SwingWorker<Void,Void>
{
	/**
	 * THE FOLLOWING VARIABLES ARE USED ONLY IN THE PREPROCESSOR AND HAVE USE ONLY IN THE PREPROCESSOR.
	 * THESE ARE NOT USED IN THE VISUALIZATION.
	 * 
	 *  autoPlayAfterPreprocessing is set to true if the song should be played DIRECTLY after it is processed. This means that
	 *  if it is true, in the overridden done() method the player is told to play. Anything that must be done after the preprocessor,
	 *  MUST be put in the done() method to guarantee that those processes DO NOT interfere with the doInBackground() method which is the heart
	 *  of the thread.
	 * 
	 * FINE: is the Integer value that denotes a fine pitch change (by musical measurement of cents)
	 * 
	 * COARSE: is the integer value that denotes a coarse pitch change (by music measurement of semitones)
	 */
	public boolean autoPlayAfterPreprocessing = false;
	public final static int FINE = 1;
	public final static int COARSE = 2;
	
	/*
	 * These variables are specifically used for the preprocessor.
	 * The allMidiTracks is an array holding all the midi tracks if the file is a Type 1 midi file (1 track is type 0)
	 * The sequence contains the midi tracks.
	 * lastBPM in the song initially starts off as the first BPM. The reason for this variable is because when
	 * converting midi ticks to real time, it is all based on the lastBPM in the song.
	 * receiver is the object that receives midi events as they are played.
	 * 
	 */
	private Track allMidiTracks[];
	private PitchReceiver receiver;
	private Controller controller;

	
	/*
	 * closeButton is used with the JProgressBar to show the progess of loading a song.
	 * channelsUsed holds the channels used, so if channel 6 is used then 6->true. 6 <maps to> true.
	 */
	//private JButton closeButton;
	//private JFrame frame;
	private TreeMap<Integer, Boolean> channelsUsed = new TreeMap<Integer, Boolean>();
	
	 
	/*
	 * These are used for pitch bends in the song; most midi songs do not have pitch bends unless they are professionally
	 * made. 
	 * 
	 * Please look at this: http://www.recordingblogs.com/sa/Wiki/tabid/58/Default.aspx?topic=MIDI+Registered+Parameter+Number+(RPN)
	 * 
	 * Please note that all RPN numbers should be ended with 127 otherwise the sequencer may not know when the RPN controller
	 * sequence is over. Although, it is good practice it is not needed; in order to handle this case, the rangeOfPitchValues,
	 * initialPitchSettings and typeOfTuningInChannel are all filled based on if the channel that the RPN belongs to, is different
	 * than the last. Another reason for checking the channel changes is because setting up the pitch wheel consists of more than
	 * one midi message and have to first group them to figure out what kind of pitch changes will occur (coarse or fine) and the range
	 * of semitones it will follow
	 */
	private int typeOfTuningIntValue = Integer.MIN_VALUE;
	private byte typeMSB = Byte.MIN_VALUE;
	private byte typeLSB = Byte.MIN_VALUE;;
	private int lastChannel = Integer.MIN_VALUE;
	private int[] rangeOfPitchValues = new int[16];
	private int[] initialPitchSettings = new int[16];
	private int[] typeOfTuningInChannel = new int[16];
	
	/**
	 * Sets up the thread for use. This thread will be use only for one song. Every time a new song is loaded into the program,
	 * a new Preprocessor thread is created for the song. Therefore one thread per song. The reason why we need to set the variable
	 * "lastBPMInSong" in the constructor is because we need to do all timing calculations based based on the last time/tick where
	 * the beat changed. IT  IS VERY IMPORTANT TO GRAB THE BPM FROM THE SEQUENCER BEFORE THE SONG IS PLAYED. THIS ENSURES
	 * THAT THE BEAT IS CORRECT.
	 * 
	 * @param controller
	 * @param receiver
	 * @param allMidiTracks
	 * @param sequence
	 * @param firstBPMInSong
	 * @param closeButton
	 */
	public LightThreadPreprocessor( Controller controller, PitchReceiver receiver, Track allMidiTracks[], float firstBPMInSong )//, JFrame frame )//JButton closeButton )
	{
		this.allMidiTracks = allMidiTracks;
		this.controller = controller;
		this.receiver = receiver;
		for( int i = 0; i < 16; i++ )
		{
			rangeOfPitchValues[i] = Integer.MIN_VALUE;
			initialPitchSettings[i] = Integer.MIN_VALUE;
			typeOfTuningInChannel[i] = Integer.MIN_VALUE;
		}
		//this.closeButton = closeButton;
		//this.frame = frame;
	}
	
	/**
	 * This method is like the run method in a thread object, except this is a task. In theory, they are the
	 * exact same thing except in Java, only tasks can be associated with JProgress bars. Anything that needs to
	 * be ran and then monitored by the JProgressBar should be placed in this method.
	 * 
	 * @return
	 */
	public Void doInBackground() 
	{
		setProgress(0);
		GUI gui = controller.getGUI();
		gui.setEnabledPlayerFrame(false);
		
		Track singleTrack;
		MidiMessage midiMessage;
		int status;
		int channel;	
		
		setProgress(10);
		
		processorControllerEvents();
		
		setProgress(25);
		
		for( int i = 0; i < allMidiTracks.length; i++)
		{
			singleTrack = allMidiTracks[i];
			for( int j = 0; j < singleTrack.size(); j++ )
			{
				midiMessage = singleTrack.get(j).getMessage();
	      		status = midiMessage.getStatus();

	      		if( status > 143 && status < 160)
	      		{
	      			//channel = status > 143 ? status - 144 : status - 128;
	      			channel = status - 144;
	      			
	      			channelsUsed.put(channel, true);
	      		}

			}	  
		}
		
		setProgress(80);
		
		receiver.setPitchData(initialPitchSettings, rangeOfPitchValues);
		this.setUpVisualizer();
	
		setProgress(100);
		return null;
	}
	
	/**
	 * This method specifically sets up the visualization. It handles initial pipes placement, colour 
	 * and dimensions. Please note that the partSize handles the max length of the pipes. An overview of 
	 * how the pipes are as follows: The pipes are set up in order by most amount of movements at the front.
	 * The changesByAmountString variables is what holds the channel number and the amount of movements
	 * which are sorted. So if the movements are: 300_0, 299_10, 200_11, 199_14 etc, then the channels are
	 * set up in this order: 0,10,11,14 etc
	 */
	private void setUpVisualizer()
	{
		GUI gui = controller.getGUI();
		Object[] channels =  channelsUsed.keySet().toArray();
		int[] c = new int[channels.length];
		for( int i = 0; i < channels.length; i++ )
		{
			c[i] = ((Integer)channels[i]).intValue();
		}
		gui.getVisualizer().setChannelsUsed(c);
		gui.getVisualizer().resetVisualizer();
	}
	
    public void done() 
    {
    	GUI gui = controller.getGUI();
    	Player player = controller.getPlayer();
    	gui.setEnabledPlayerFrame(true);
    	//gui.resumeAnimator();
		//closeButton.setEnabled(true);
    	//frame.dispose();
		if( autoPlayAfterPreprocessing ){ player.play();}
    }

	/**
	 * This method does exactly what it says; it processes controller events. Although, the controller events 
	 * it processes are RPN numbers and even further, only RPN numbers that configure the MIDI Pitch wheel.
	 * A brief overview is that a MidiController event is specified in one message with 3 bytes. The first byte
	 * is the status byte which is between 176 and 191. The second byte is the controller number and in this case
	 * we are only interested in controller numbers of 100, 101 and 6. 100 = LSB of RPN, 101 = MSB of PRN and
	 * 6 = MSB of data entry. The third byte is the range of pitch values or the initial value of the slider; 
	 * the role depends on the the number given by the third byte of 100 and 101 bytes.
	 * 
	 * The problem to overcome was that there are multiple messages that are sent at different times that make up
	 * the process of configuring the pitch wheel.
	 * 
	 * Look at this: http://www.recordingblogs.com/sa/Wiki/tabid/58/Default.aspx?topic=MIDI+Registered+Parameter+Number+(RPN)
	 * and this    : http://www.recordingblogs.com/sa/Wiki/tabid/58/Default.aspx?topic=MIDI+Pitch+Wheel+message
	 * 
	 * First we have to get both, the MSB and LSB of the RPN message to tell us if we are setting the initial values
	 * of the pitch wheel or setting the range of wheel. This part it sent in 2 parts:
	 * 
	 * 		0xB0 0x65 0x00
	 * 		0xB0 0x64 0x02
	 * 
	 * These would be the first 2 parts. So I have to wait for both messages (0x64 = 100 and 0x65 = 101) before I
	 * can assemble the meaning from the 3rd bytes. To do this, I wait until both typeMSB and typeLSB are not the minimum
	 * value of a byte. From there one I assemble the message by ORing: 0x00 | 0x02 = 0x0002 (ONLY take
	 * the last 7 bits of each byte , therefore ignoring the first). This tells me I am doing coarse
	 * changes for this channel when pitch change does occur and setting the starting value. The next message is:
	 * 
	 * 		0xB0 0x06 0x40
	 * 
	 * This means that 0x40 is the MSB of the message (0x06 tells me it is the MSB. Look at midi messages).
	 * Now I take the first 7 bytes as the MSB which is:  0x1000000000000 = 0x2000 and tells me the that is 
	 * the initial value.
	 */
	private void processorControllerEvents()
	{
		Track singleTrack;
		MidiMessage midiMessage;
		byte[] midiByte;
		int status;
		int channel;
		
		for( int i = 0; i < allMidiTracks.length; i++)
		{
			singleTrack = allMidiTracks[i];
			for( int j = 0; j < singleTrack.size(); j++ )
			{
				midiMessage = singleTrack.get(j).getMessage();
	      		midiByte = midiMessage.getMessage();
	      		status = midiMessage.getStatus();

	      		if( status >= 176 && status <= 191 )
	      		{
	      			channel = status - 176;
	      			
	      			/*
	      			 * This if statement is used to assemble the type of message received. This is necessary
	      			 * because typeOfTuningIntValue is comprised of 2 bytes that come in 2 seperate
	      			 * messages.
	      			 */
	      			if( (midiByte[1]&0xff) == 101 )
	      			{
	      				typeMSB = (byte) (midiByte[2]&0xff);
	      				if( typeMSB != Byte.MIN_VALUE  && typeLSB != Byte.MIN_VALUE)
	      				{
	      					/*
	      					 * To specify tuning, either range, fine or coarse tuning, the MSB is 0x00
	      					 * and the LSB is 0x00, 0x01, 0x02 respectively. So we can XOR the bits and get 
	      					 * either 0x0000, 0x0001, 0x0002, 0x0001.
	      					 */
	      					typeOfTuningIntValue = typeMSB | typeLSB; 
	      				}
	      			}
	      			else if( (midiByte[1]&0xff) == 100 )
	      			{
	      				typeLSB = (byte) (midiByte[2]&0xff);
	      				if( typeMSB != Byte.MIN_VALUE  && typeLSB != Byte.MIN_VALUE)
	      				{
	      					/*
	      					 * To specify tuning, either range, fine or coarse tuning, the MSB is 0x00
	      					 * and the LSB is 0x00, 0x01, 0x02 respectively. So we can XOR the bits and get 
	      					 * either 0x0000, 0x0001, 0x0002, 0x0001.
	      					 */
	      					typeOfTuningIntValue = typeMSB | typeLSB; 
	      				}
	      			}
	      			
	      			//Setting range of slider
	      			if( lastChannel != channel && typeOfTuningIntValue == 0 && (midiByte[1]&0xff) == 6 )
	      			{
	      				//System.out.println("Setting position " + channel + " with " + (midiByte[2]&0xff));
	      				rangeOfPitchValues[channel] =  (midiByte[2]&0xff);
	      				lastChannel = channel;
	      				typeOfTuningIntValue = Integer.MIN_VALUE;
	      				typeMSB = Byte.MIN_VALUE;
	      				typeLSB = Byte.MIN_VALUE;;
	      			}
	      			else if( lastChannel != channel && typeOfTuningIntValue == 2 && (midiByte[1]&0xff) == 6 )
	      			{
	      				//System.out.println( "Setting preset value for channel: " +  channel + " Preset = " + ( (midiByte[2]&0xff) << 7 ) );
	      				initialPitchSettings[channel] = ( (midiByte[2]&0xff) << 7 );
	      				typeOfTuningInChannel[channel] = COARSE;
	      				lastChannel = channel;
	      				typeOfTuningIntValue = Integer.MIN_VALUE;
	      				typeMSB = Byte.MIN_VALUE;
	      				typeLSB = Byte.MIN_VALUE;;
	      			}
	      			
	      		}
	      		//
	      		else if(status  >= 192 && status < 207)
	      		{
	      			//System.out.println((midiByte[1]&0xff) + " " + status);
	      		}
			}			
		}
		
		//if non are set, I made a default to use because midi likes to apply pitch bends to
		//a channel that isnt even set to use them. If you told midi to jump off a bridge, it
		//probably would do it gladly.
		for( int i = 0 ; i < 16; i++ )
		{
			if( initialPitchSettings[i]  == Integer.MIN_VALUE ||
				typeOfTuningInChannel[i] == Integer.MIN_VALUE ||
				rangeOfPitchValues[i]    == Integer.MIN_VALUE)
			{
				initialPitchSettings[i] = 0x2000;
				typeOfTuningInChannel[i] = COARSE;
				rangeOfPitchValues[i] = 12;
			}
		}
	}
}

