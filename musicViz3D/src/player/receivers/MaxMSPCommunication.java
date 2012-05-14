package player.receivers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;

import processors.BeatProcessor;


import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

/**
 * This class receives all MIDI messages played; however, it 
 * differs from the other receivers by having a connection to
 * a UDP port for communication over an {@link OSCPortOut}.
 * <p>
 * This class allows for the socket to be enabled and disabled
 * as needed such that the socket will not always be transmitting.
 * One can also enable and disable the transmission of different
 * constructs. The issue with enabling and disabling the MAX/MSP
 * socket is that it cannot be closed. The OSC:<p>
 * http://www.illposed.com/software/javaosc.html
 * does not provide enough functionality to do this. Therefore the only
 * way to do it is to close (cannot reopen it), set the variable to null
 * and do a check.
 * <p>
 * Using atomic variables are not enough either, the threads accessing this
 * class must be synchronised. Therefore there are three synchronised methods:
 * {@link #sendMessage(int, int, int)}, {@link #enableMaxMSPCommunication()} and
 * {@link #disableMaxMSPCommunication()}. The reason for synchronisation is that the
 * socket can be disabled after a check to see if it is enabled passes. Therefore
 * a thread will attempt to send a message when it is disabled. Synchronisation stops this.
 * <p>
 * There are different cases as to what can be transmitted. The Emoti-Chair
 * has 8 channels and MIDI has 16. Therefore they have to be mapped. There are
 * 3 cases: (1) all messages sent to the chair. (2) beats sent to the chair and
 * (3) beats sent to the chair. To create these mappings, three {@link HashMap}s
 * are used.
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
 * Please refer to the synchronized keyword and {@link AtomicReference} for 
 * more information on concurrency. 
 * @author Michael Pouris
 *
 */
public class MaxMSPCommunication extends Thread implements Receiver
{
	/*
	 * The basic message sent through the OSC port. The channel is appended.
	 */
	private final String messageString = "/element/ChannelNoteVol/";
	
	private InetAddress address;
	private AtomicReference<OSCPortOut> sender;
	
	/*
	 * Mapping of MIDI Channels to MAX/MSP when only the instruments are sent.
	 */
	private HashMap<Integer, Integer> midiInstrumentToMaxMapping;
	
	/*
	 * Mapping of MIDI Channels to MAX/MSP when only the beats are sent.
	 */
	private HashMap<Integer, Integer> midiBeatToMaxMapping;
	
	/*
	 * Mapping of MIDI Channels to MAX/MSP when Both the instruments 
	 * and beats are sent to the chair.
	 */
	private HashMap<Integer, Integer> midiInstSimultaneous;
	
	/*
	 * Mapping of MIDI Channels to MAX/MSP when Both the instruments 
	 * and beats are sent to the chair.
	 */
	private HashMap<Integer, Integer> midiBeatSimultaneous;
	
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	private AtomicBoolean sendBeats;
	private AtomicBoolean sendInstruments;
	
	public MaxMSPCommunication() throws UnknownHostException 
	{
		this.address = InetAddress.getByName("localhost");
		this.sender = new AtomicReference<OSCPortOut>(null);
		this.handOffQueue = new LinkedBlockingQueue<MidiMessage>();
		this.sendBeats = new AtomicBoolean(true);
		this.sendInstruments = new AtomicBoolean(true);
		
		//Mapping of Instruments when only the instruments are played on the chair
		this.midiInstrumentToMaxMapping = new HashMap<Integer, Integer>();
		for( int i = 0; i < 16; i++ )
		{
			this.midiInstrumentToMaxMapping.put( Integer.valueOf(i), Integer.valueOf( (int)Math.floor( i/2f ) ) );
		}
		
		//Mapping of the beats when only the beats are played on the chair
		this.midiBeatToMaxMapping = new HashMap<Integer, Integer>();
		for( int i = 0; i < 5; i++ )
		{
			this.midiBeatToMaxMapping.put(i, i+1);
		}
		
		//Mapping of the instruments and beats when both are sent to the chair
		this.midiInstSimultaneous = new HashMap<Integer, Integer>();
		this.midiInstSimultaneous.put( 0, 0 );
		this.midiInstSimultaneous.put( 1, 0 );
		this.midiInstSimultaneous.put( 2, 0 );
		
		this.midiInstSimultaneous.put( 3, 1 );
		this.midiInstSimultaneous.put( 4, 1 );
		this.midiInstSimultaneous.put( 5, 1 );
		
		this.midiInstSimultaneous.put( 6, 2 );
		this.midiInstSimultaneous.put( 7, 2 );
		this.midiInstSimultaneous.put( 8, 2 );
		
		this.midiInstSimultaneous.put( 10, 6 );
		this.midiInstSimultaneous.put( 11, 6 );
		this.midiInstSimultaneous.put( 12, 6 );
		
		this.midiInstSimultaneous.put( 13, 7 );
		this.midiInstSimultaneous.put( 14, 7 );
		this.midiInstSimultaneous.put( 15, 7 );
		
		this.midiBeatSimultaneous = new HashMap<Integer, Integer>();
		this.midiBeatSimultaneous.put(0, 3);
		this.midiBeatSimultaneous.put(1, 4);
		this.midiBeatSimultaneous.put(2, 4);
		this.midiBeatSimultaneous.put(3, 4);
		this.midiBeatSimultaneous.put(4, 5);
	}

	public void close() 
	{
	}

	/**
	 * This method runs on the java sound thread, therefore it 
	 * passes all messages into queue, which then allows another
	 * thread to safely access and process the messages.
	 * See {@link ConcurrentLinkedQueue} for more details.
	 */
	public void send(MidiMessage message, long timestamp)
	{
		try 
		{
			this.handOffQueue.put(message);
		}
		catch (InterruptedException e)
		{
			JOptionPane.showMessageDialog(null, "MaxMSPCommunication::send. Could not place " +
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
		MidiMessage message;
		
		while( true )
		{
			try 
			{
				message = this.handOffQueue.take();
				this.processMessage( message );
			}
			catch (InterruptedException e)
			{
				JOptionPane.showMessageDialog(null, "MaxMSPCommunication::send. Could not place " +
						"MidiMessage into LinkedBlockingQueue due to an interrupt exception." );
				System.exit(0);
			} 
			catch (IOException e)
			{
				System.err.println("The socket cannot be written to.");
			}
		}
	}
	
	/**
	 * Process the MIDI message differently depending on what is being sent to the chair.
	 * 
	 * @param message the message to be processed
	 * @throws IOException thrown when the socket cannot be written to
	 */
	private void processMessage( MidiMessage message ) throws IOException
	{
		if( this.sendBeats.get() && this.sendInstruments.get() )
		{
			this.processInstrument( message, this.midiInstSimultaneous );
			this.processBeat(message, this.midiBeatSimultaneous);
		}
		else if( this.sendInstruments.get() && !this.sendBeats.get() )
		{
			this.processInstrument(message, this.midiInstrumentToMaxMapping);
		}
		else if( !this.sendInstruments.get() && this.sendBeats.get() )
		{
			this.processBeat(message, this.midiBeatToMaxMapping);
		}
	}
	
	/**
	 * Process the message as an instrument with a specific mapping. This mapping must be
	 * selected from one of the global variables.
	 * 
	 * @param message message to process
	 * @param midiToMaxMappings the mapping to use from MIDI to MAX/MSP
	 * @throws IOException
	 */
	private void processInstrument( MidiMessage message, HashMap<Integer, Integer> midiToMaxMappings ) throws IOException
	{
		int channel;
		byte[] m;
		
		//Process All messages not for a beat
		if ( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) != 9 )
		{
			channel = message.getStatus() - 128;
			m = message.getMessage();
			this.sendMessage( midiToMaxMappings.get(channel), 0, 0);
		}
		else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) != 9 )
		{
			channel = message.getStatus() - 144;
			m = message.getMessage();
			if(( m[2] & 0xFF) == 0 )
			{
				this.sendMessage( midiToMaxMappings.get(channel), midiNoteToFreq(m[1] & 0xFF), m[2] & 0xFF);
			}
			else
			{
				this.sendMessage( midiToMaxMappings.get(channel), midiNoteToFreq(m[1] & 0xFF), 127);
			}
		}
	}
	
	/**
	 * Process the message as a beat with a specific mapping. This mapping must be select from one of the 
	 * global variables above.
	 * 
	 * @param message
	 * @param midiBeatToMaxMappings
	 * @throws IOException
	 */
	private void processBeat(  MidiMessage message, HashMap<Integer, Integer> midiBeatToMaxMappings ) throws IOException
	{
		byte[] m;
		
		//Process note off messages first then note on
		if( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) == 9 )
		{
			m = message.getMessage();
			int drum = BeatProcessor.getCorrespondingPipeFromNote(m[1] & 0xFF);
			this.sendMessage(midiBeatToMaxMappings.get(drum), 0, 0);
		}
		else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) == 9 )
		{
			m = message.getMessage();
			int drum = BeatProcessor.getCorrespondingPipeFromNote(m[1] & 0xFF);
			if( (m[2] & 0xff) == 0 )
			{
				this.sendMessage(midiBeatToMaxMappings.get(drum),midiBeatToFreq(m[1] & 0xFF), 0);
			}
			else
			{
				this.sendMessage(midiBeatToMaxMappings.get(drum),midiBeatToFreq(m[1] & 0xFF), 127);
			}
		}
	}
	
	/**
	 * Send a message through the OSC port.
	 * <p>
	 * Considering the port can be closed and opened at any time, it is of 
	 * importance to have this method synchronised. For example, a calling
	 * thread may call this method and the check for null allows the method to run.
	 * At the same time another method can disable the socket and the first thread at that
	 * point send an OSC packet. Therefore cauing a null pointer exception. Hence it must
	 * be synchronised on this method and the following 3:
	 * {@link #enableMaxMSPCommunication()}, {@link #disableMaxMSPCommunication()} and
	 * {@link #resetMaxMSP()}.
	 * 
	 * @param maxMspChannel
	 * @param frequency
	 * @param volume
	 * @throws IOException
	 */
	private synchronized void sendMessage(int maxMspChannel, int frequency, int volume) throws IOException
	{
		if( this.sender.get() == null )
		{
			return;
		}
		
		String transmission = this.messageString + maxMspChannel;
		Object[] transmissionObject = {frequency,volume};
		OSCMessage oscMsg = new OSCMessage(transmission, transmissionObject);
		this.sender.get().send(oscMsg);
	}
	
	/**
	 * Resets the frequency and volume of each channel in MAX/MSP,
	 * there are 8 channels. It sets all volumes and frequencies to 0.
	 * @throws IOException 
	 */
	public synchronized void resetMaxMSP()
	{		
		try
		{
			for( int i = 0; i < 8; i++ )
			{
				this.sendMessage(i, 0, 0);
			}
		}
		catch( IOException e )
		{
			System.err.println("Cannot reset the MAX/MSP because the socket I/O is not working.");
		}
	}
	
	/**
	 * Enable the MAX/MSP port.
	 * <p>
	 * Creates a new UDP port and considering this is a synchronized method,
	 * all calls to send a message while this method is running, must wait until
	 * this method is finished.
	 * 
	 * @throws SocketException
	 */
	public synchronized void enableMaxMSPCommunication() throws SocketException
	{
		if( this.sender.get() == null )
		{
			this.sender.set(new OSCPortOut(this.address,6601));
		}
	}
	
	/**
	 * Disables the MAX/MSP port.
	 * <p>
	 * Disables the port by first closing it and then setting the variable to NULL.
	 * All other calls to reopen the port and close the port must wait until this
	 * method is finished in order to maintain Atomicity.
	 */
	public synchronized void disableMaxMSPCommunication()
	{
		if( this.sender.get() != null )
		{
			this.sender.get().close();
			this.sender.set(null);
		}
	}
	
	/**
	 * Enables sending instrument messages to be sent to MAX/MSP.
	 * <p>
	 * Instruments are all channels but channel 9.
	 */
	public void enableInstrumentCommunication()
	{
		this.sendInstruments.set(true);
	}

	/**
	 * Disables sending instrument messages to be sent to MAX/MSP.
	 * <p>
	 * Instruments are all channels but channel 9.
	 */
	public void disableInstrumentCommunication()
	{
		this.sendInstruments.set(false);
	}
	
	/**
	 * Enables sending beat messages to be sent to MAX/MSP.
	 * <p>
	 * Beats are channel 9.
	 */
	public void enableBeatCommunication()
	{
		this.sendBeats.set(true);
	}
	
	/**
	 * Disables sending beat messages to be sent to MAX/MSP.
	 * <p>
	 * Beats are channel 9.
	 */
	public void disableBeatCommunication()
	{
		this.sendBeats.set(false);
	}
	
	/**
	 * Converts a normal instrument (not beat, see {@link #midiBeatToFreq(int)} for beat)
	 * to frequency.
	 * 
	 * @param note MIDI instrument note (all by channel 9) to convert from
	 * @return frequency a new frequency
	 */
	private int midiNoteToFreq( int note )
	{
		/*int freq;
		
		freq = note*800/127;
		
		return freq;*/
		int newValue = 50;
		int oldMax, oldMin;
		int newMax, newMin;
		int oldRange, newRange;
		if( note >= 0 && note <= 33 )
		{
			oldMax = 33;  oldMin = 0;
			newMax = 149; newMin = 0;
			oldRange = (oldMax - oldMin);
			newRange = (newMax - newMin);
			newValue = (((note - oldMin) * newRange) / oldRange) + newMin;
		}
		else if( note >= 34 && note <= 94 )
		{
			
			oldMax = 94;  oldMin = 34;
			newMax = 275; newMin = 150;
			oldRange = (oldMax - oldMin);
			newRange = (newMax - newMin);
			newValue = (((note - oldMin) * newRange) / oldRange) + newMin;
		}
		else if( note >= 95 && note <= 127 )
		{
			
			oldMax = 127;  oldMin = 95;
			newMax = 350; newMin = 276;
			oldRange = (oldMax - oldMin);
			newRange = (newMax - newMin);
			newValue = (((note - oldMin) * newRange) / oldRange) + newMin;
		}
		return newValue;
		
	}
	
	/**
	 * Converts a drum note to a specific frequency by altering the
	 * minimum and maximum yet keeping the same ratios.
	 * 
	 * @param drumNote MIDI channel 9 note to convert from
	 * @return a new frequency
	 */
	private int midiBeatToFreq( int drumNote )
	{
		int oldMax = 47,  oldMin = 34;
		int newMax = 290, newMin = 180;
		int oldRange = (oldMax - oldMin);
		int newRange = (newMax - newMin);
		int newValue = (((drumNote - oldMin) * newRange) / oldRange) + newMin;
		return newValue;
	}
	
	/*private void processInstrument( MidiMessage message ) throws IOException
	{
		int channel;
		byte[] m;
		OSCMessage oscMsg;
		String transmission = "";
		Object[] transmissionObject = {null,null};
		
		//Process All messages not for a beat
		if ( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) != 9 )
		{
			channel = message.getStatus() - 128;
			m = message.getMessage();
			
			transmission = this.messageString + this.midiInstrumentToMaxMapping.get(channel);
			transmissionObject[0] = 0;
			transmissionObject[1] = 0;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
		else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) != 9 )
		{
			channel = message.getStatus() - 144;
			m = message.getMessage();
			
			transmission = this.messageString + this.midiInstrumentToMaxMapping.get(channel);
			transmissionObject[0] = midiNoteToFreq(m[1] & 0xFF);//
			transmissionObject[1] = m[2] & 0xFF;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
	}
	
	private void processBeat( MidiMessage message ) throws IOException
	{
		byte[] m;
		int drum;
		OSCMessage oscMsg;
		String transmission = "";
		Object[] transmissionObject = {null,null};
		
		//Process note off messages first then note on
		if( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) == 9 )
		{
			m = message.getMessage();
			drum = BeatProcessor.getCorrespondingPipeFromNote(m[1] & 0xFF);
			
			transmission = this.messageString + this.midiBeatToMaxMapping.get(drum);
			transmissionObject[0] = 0;
			transmissionObject[1] = 0;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
		else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) == 9 )
		{
			m = message.getMessage();
			drum = BeatProcessor.getCorrespondingPipeFromNote(m[1] & 0xFF);
			
			transmission = this.messageString + this.midiBeatToMaxMapping.get(drum);
			transmissionObject[0] = midiBeatToFreq(drum);
			transmissionObject[1] = m[2] & 0xFF;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
	}
	
	private void processInstrumentAndBeat(MidiMessage message) throws IOException
	{
		int channel;
		int instance;
		byte[] m;
		OSCMessage oscMsg;
		String transmission = "";
		Object[] transmissionObject = {null,null};
		
		//first process the note off and on messages for instruments
		if ( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) != 9 )
		{
			channel = message.getStatus() - 128;
			m = message.getMessage();
			
			transmission = this.messageString + this.midiInstSimultaneous.get(channel);
			transmissionObject[0] = 0;
			transmissionObject[1] = 0;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
		else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) != 9 )
		{
			channel = message.getStatus() - 144;
			m = message.getMessage();
			
			transmission = this.messageString + this.midiInstSimultaneous.get(channel);
			transmissionObject[0] = midiNoteToFreq(m[1] & 0xFF);//
			transmissionObject[1] = m[2] & 0xFF;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
		
		//Process note off messages first then note on for beats
		if( message.getStatus() > 127 && message.getStatus() < 144 && (message.getStatus() - 128) == 9 )
		{
			m = message.getMessage();
			instance = BeatProcessor.getCorrespondingPipeFromNote(m[1] & 0xFF);
			
			transmission = this.messageString + this.midiBeatSimultaneous.get(instance);
			transmissionObject[0] = 0;
			transmissionObject[1] = 0;
			
			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
		else if(message.getStatus() > 143 && message.getStatus() < 160 && (message.getStatus() - 144) == 9 )
		{
			m = message.getMessage();
			instance = BeatProcessor.getCorrespondingPipeFromNote(m[1] & 0xFF);
			
			transmission = this.messageString + this.midiBeatSimultaneous.get(instance);
			transmissionObject[0] = midiBeatToFreq(m[1] & 0xFF);
			transmissionObject[1] = m[2] & 0xFF;
			if((m[2] & 0xFF) == 0 )
			{
				System.out.println("Note Off is note on");
			}

			oscMsg = new OSCMessage(transmission, transmissionObject);
			this.sender.send(oscMsg);
		}
	}*/
}
