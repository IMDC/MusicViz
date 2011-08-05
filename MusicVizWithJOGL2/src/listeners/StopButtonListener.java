package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.Controller;

public class StopButtonListener implements ActionListener 
{
	private Controller controller;
	
	public StopButtonListener( Controller c )
	{
		controller = c;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		controller.stop();
		controller.getPlayPauseToggleButton().setSelected(false);
		controller.getPlayPauseToggleButton().setText("Play ");
	}

}
