package player.receivers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;

import processors.BeatProcessor;


import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class MaxMSPCommunication extends Thread implements Receiver
{
	private InetAddress address;
	private OSCPortOut sender;
	private final String messageString = "/element/ChannelNoteVol/";
	
	private HashMap<Integer, Integer> midiInstrumentToMaxMapping;
	private HashMap<Integer, Integer> midiBeatToMaxMapping;
	
	private HashMap<Integer, Integer> midiInstSimultaneous;
	private HashMap<Integer, Integer> midiBeatSimultaneous;
	
	private LinkedBlockingQueue<MidiMessage> handOffQueue;
	private AtomicBoolean sendBeats;
	private AtomicBoolean sendInstruments;
	
	public MaxMSPCommunication() 
			throws UnknownHostException, SocketException
	{
		this.address = InetAddress.getByName("localhost");
		this.sender = new OSCPortOut(this.address,6601);
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
			this.midiBeatToMaxMapping.put(0, i+1);
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
		this.resetMaxMSP();
		this.sender.close();
	}

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
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void processMessage( MidiMessage message ) throws IOException
	{
		if( this.sendBeats.get() && this.sendInstruments.get() )
		{
			//processInstrumentAndBeat( message );
			this.processInstrument( message, this.midiInstSimultaneous );
			this.processBeat(message, this.midiBeatSimultaneous);
		}
		else if( this.sendInstruments.get() && !this.sendBeats.get() )
		{
			//processInstrument( message );
			this.processInstrument(message, this.midiInstrumentToMaxMapping);
		}
		else if( !this.sendInstruments.get() && this.sendBeats.get() )
		{
			//processBeat( message );
			this.processBeat(message, this.midiBeatToMaxMapping);
		}
	}
	
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
			this.sendMessage( midiToMaxMappings.get(channel), midiNoteToFreq(m[1] & 0xFF), m[2] & 0xFF);
		}
	}
	
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
			
			this.sendMessage(midiBeatToMaxMappings.get(drum), midiBeatToFreq(m[1] & 0xFF), m[2] & 0xFF);
		}
	}
	
	private void sendMessage(int maxMspChannel, int frequency, int volume) throws IOException
	{
		String transmission = this.messageString + maxMspChannel;
		Object[] transmissionObject = {frequency,volume};
		OSCMessage oscMsg = new OSCMessage(transmission, transmissionObject);
		this.sender.send(oscMsg);
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
	
	private int midiNoteToFreq( int note )
	{
		int freq;
		
		freq = note*800/127;
		
		return freq;
	}
	
	private int midiBeatToFreq( int drum )
	{
		int oldMax = 47,  oldMin = 34;
		int newMax = 200, newMin = 100;
		int oldRange = (oldMax - oldMin);
		int newRange = (newMax - newMin);
		int newValue = (((drum - oldMin) * newRange) / oldRange) + newMin;
		return newValue;
	}
	
	private void resetMaxMSP()
	{
		OSCMessage oscMsg;
		String transmission = "";
		Object[] transmissionObject = {0,0};
		
		
		for( int i = 0; i < 8; i++ )
		{
			transmission = this.messageString + i;
			oscMsg = new OSCMessage(transmission, transmissionObject);
			try 
			{
				this.sender.send(oscMsg);
			} 
			catch (IOException e)
			{
				System.err.println("Could Not Reset MaxMSP");
			}
		}
	}
	
	public AtomicBoolean getSendInstrumentsToMaxMSP()
	{
		return sendInstruments;
	}
	
	public AtomicBoolean getSendBeatsToMaxMSP()
	{
		return sendBeats;
	}
}
