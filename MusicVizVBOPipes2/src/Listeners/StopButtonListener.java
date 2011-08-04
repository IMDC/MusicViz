package Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Controller.Controller;
import GUI.GUI;
import Player.Player;

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
		gui.getPlayPauseToggleButton().setSelected(false);
		gui.getPlayPauseToggleButton().setText("Play ");
	}

}
