package listeners;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import controller.Controller;

/**
 * This class opens a JOptionPane that allows the user to select how
 * to change what is displaying in the visualizer. The user can
 * display all beats and pipes, or just the beats or just the pipes.
 * 
 * @author Michael Pouris
 *
 */
public class ChangeVisualDisplay implements MouseListener
{
	private Controller controller;
	
	public ChangeVisualDisplay( Controller controller )
	{
		this.controller = controller;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		Object[] possibilities = {"Display Pipes and Beats", "Display Pipes Only", "Display Beats Only"};
		try
		{
			String choice = (String)JOptionPane.showInputDialog(	
									null,
		                            "Please choose a colour palette",
		                            "Colour Palette",
		                            JOptionPane.PLAIN_MESSAGE,
		                            null,
		                            possibilities,
		                            possibilities[0]);
		       
			if( ((String) possibilities[0]).equalsIgnoreCase(choice) )
			{
				this.controller.getPlayer().getIsPlayingBeats().set(true);
				this.controller.getPlayer().getIsPlayingInstruments().set(true);
			}
			else if(((String) possibilities[1]).equalsIgnoreCase(choice))
			{
				this.controller.getPlayer().getIsPlayingBeats().set(false);
				this.controller.getPlayer().getIsPlayingInstruments().set(true);
			}
			else if(((String) possibilities[2]).equalsIgnoreCase(choice))
			{
				this.controller.getPlayer().getIsPlayingBeats().set(true);
				this.controller.getPlayer().getIsPlayingInstruments().set(false);
				this.controller.getGUI().getVisualizer().resetVisualizer();
			}
		}
		catch(NullPointerException ex)
		{
			JOptionPane.showMessageDialog(null, "Cannot Alter the Visualization because there is a null pointer exception");
		}
	}

}
