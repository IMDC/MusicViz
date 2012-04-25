package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import controller.Controller;
import gui.GUI;
import player.Player;

/**
 * Listens for any clicks on the toggle button and based on the state of the song (Playing/paused)
 * changes what the action does to the player. IE If the song is playing, the button will display
 * Pause and if the song is paused, then the button will display play. Each state will act accordingly
 * 
 * @author Michael Pouris
 *
 */
public class PlayPauseToggleButtonActionListener implements ActionListener
{
	private Controller controller;
	
	public PlayPauseToggleButtonActionListener ( Controller controller )
	{
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		JToggleButton tb = (JToggleButton) event.getSource();
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		//Only toggles if there is something in the list.
		if( gui.getPlaylist().getModel().getSize() != 0 )
		{
			//If the button is pressed, that means the song is playing and 
			//the option to pause the song is displayed.
			//Otherwise, the song is paused and the word Play should show up.
			if( tb.isSelected() )
			{
				tb.setText("Pause");
				player.play();
			}
			else
			{
				tb.setText("Play ");
				player.pause();
			}
		}
		else
		{
			//If there is nothing in the list we have to set the button to some sort of default
			tb.setText("Play ");
			tb.setSelected(false);
		}
	}
}
