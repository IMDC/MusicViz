package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.Controller;
import gui.GUI;
import player.Player;

/**
 * This class stops the MIDI player.
 * 
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class StopButtonListener implements ActionListener 
{
	private Controller controller;
	
	/**
	 * Initialises the object to handle button pushes on the "Stop" button on the GUI.
	 * <p>
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 */
	public StopButtonListener( Controller controller )
	{
		this.controller = controller;
	}
	
	/**
	 * Handles all button pushes to stop the song.
	 */
	public void actionPerformed(ActionEvent e)
	{
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		player.stop();
		gui.setSelectedJToggleButton(false);
		gui.setTextJToggleButton("Play ");
		gui.getVisualizer().resetVisualizer();
	}

}
