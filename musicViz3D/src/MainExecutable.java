import java.io.IOException;

import gui.GUI;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;

import player.Player;

import controller.Controller;

/**
 * This is the main class that creates the the MusicViz program.
 * <p>
 * There are 3 main components: the Player, the GUI and the Controller.
 * The GUI contains 2 main screens, the Java MIDI Player controls, which
 * controls the Player and the Visualizer, which displays the Music
 * Visualization. The controller contains both the GUI and Player
 * objects. This is passed around the program so that objects
 * can access the GUI and Player as needed.
 * 
 * 
 * @author Michael Pouris
 * @see Player
 * @see GUI
 * @see Controller
 *
 */
public class MainExecutable 
{
	public static void main(String [] args)
	{
		try 
		{
			//A empty controller, with no objects initialised
			Controller controller = new Controller();
			
			//Create the Player, which plays the MIDI song
			//Create the GUIs, which show the visualisation and
			//allows for the Player to be controlled.
			Player player = new Player();
			GUI gui = new GUI();
			
			//In order to have objects access each construct, add them to the component
			controller.initController(player, gui);
			
			//Finally initialize the Player's note receivers and
			//add the actionlisteners to the GUI.
			player.init(controller);
			gui.addListeners(controller);
		} 
		catch (InterruptedException e)
		{
			JOptionPane.showMessageDialog(null, "Could not initialize the GUI\n");
			//System.exit(1);
		} 
		catch (MidiUnavailableException e)
		{
			JOptionPane.showMessageDialog(null, "Your system's midi player cannot be accessed.\n" +
					"Please close any programs using the midi player\n" +
					"and restart the program. If you are in Windows7, " +
					"please plug in speakers or headphones because windows "+ 
					"thinks there is no sound card on your machine until you do so.");
			//System.exit(1);
		} 
		/*catch (InvalidMidiDataException e)
		{
			JOptionPane.showMessageDialog(null, "Cannot load soundbank because of a problem with the midi data.");
			//System.exit(1);
		}*/
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Cannot load soundbank because the file cannot be open or read.");
			//System.exit(1);
		}
	}
}
