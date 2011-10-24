import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;

import controller.Controller;

public class MainExecutable 
{
	public static void main(String [] args)
	{
		try 
		{
			new Controller();
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
	}
}
