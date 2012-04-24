package main;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

public class Main 
{
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException 
	{
		
		Sequencer sequencer = MidiSystem.getSequencer(true);
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		Transmitter transmitter = sequencer.getTransmitter();
		
		Receiver receiver = new MidiReceiver();
		transmitter.setReceiver(receiver);
		File song = new File("Metallica-Fade To Black.mid");
		
		Sequence sequence = MidiSystem.getSequence(song);
		sequencer.setSequence(sequence);
		
		sequencer.open();
		sequencer.start();
		
		Thread.sleep(100);
		Object[] transmitters = sequencer.getTransmitters().toArray();
		ShortMessage myMsg = new ShortMessage();
		for( int i = 0; i < 16; i++ )
		{
				myMsg.setMessage(ShortMessage.CONTROL_CHANGE,i,7,0);
				((Transmitter)transmitters[0]).getReceiver().send(myMsg, -1);
		}
		
		/****/
		// the sound bank file
		/*File soundbank = new File("soundbank-deluxe.gm");
		File song = new File("Metallica-Fade To Black.mid");
		
		
		Sequence sequence = MidiSystem.getSequence(song);
		Sequencer sequencer = MidiSystem.getSequencer(false);
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		synthesizer.loadAllInstruments(MidiSystem.getSoundbank(soundbank));
		
		synthesizer.open();
		Receiver receiver = synthesizer.getReceiver();
		Receiver receiver2 = new MidiReceiver();
		sequencer.open();
		
		// wire up the two
		sequencer.getTransmitter().setReceiver(receiver);
		sequencer.getTransmitter().setReceiver(receiver2);

		sequencer.setSequence(sequence);
		// start the playback
		sequencer.start();
		//Thread.sleep(100);
		//setVolume(50,synthesizer);
		*/
	}
	
	public static void setVolume(int volume, Synthesizer synthesizer) {
		  MidiChannel[] channels = synthesizer.getChannels();
		  // set the master volume for each channel
		  for (int i = 0; i < channels.length; i++) {
		    channels[i].controlChange(7, volume);
		  }
	}
}
