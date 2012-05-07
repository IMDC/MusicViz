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
 *
 */
public class StopButtonListener implements ActionListener 
{
	private Controller controller;
	
	public StopButtonListener( Controller c )
	{
		controller = c;
	}
	
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
