import java.io.IOException;

import gui.GUI;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;

import player.Player;

import controller.Controller;

public class MainExecutable 
{
	public static void main(String [] args)
	{
		try 
		{
			Controller controller = new Controller();
			Player player = new Player();
			GUI gui = new GUI();
			
			controller.initController(player, gui);
			
			player.init(controller);
			gui.addListeners(controller);
		} 
		catch (InterruptedException e)
		{
			JOptionPane.showMessageDialog(null, "Could not initialize the GUI\n");
			System.exit(0);
		} 
		catch (MidiUnavailableException e)
		{
			JOptionPane.showMessageDialog(null, "Your system's midi player cannot be accessed.\n" +
					"Please close any programs using the midi player\n" +
					"and restart the program. If you are in Windows7, " +
					"please plug in speakers or headphones because windows "+ 
					"thinks there is no sound card on your machine until you do so.");
			System.exit(0);
		} 
		catch (InvalidMidiDataException e)
		{
			JOptionPane.showMessageDialog(null, "Cannot load soundbank because of a problem with the midi data.");
			System.exit(0);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Cannot load soundbank because the file cannot be open or read.");
			System.exit(0);
		}
	}
}
