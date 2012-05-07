package listeners;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;


import controller.Controller;

/**
 * This class opens a {@link javax.swing.JOptionPane} that allows the user to select how
 * to change what is displaying in the visualizer. The user can
 * display all beats and pipes, or just the beats or just the pipes.
 * <p>
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class ChangeVisualDisplay implements MouseListener
{
	private Controller controller;
	
	/**
	 * Creates a new listener to receive action events. If invoked, the class
	 * will show a {@link javax.swing.JOptionPane}. The user can select
	 * the constructs which the {@link visualizer.ConcurrentVisualizer} will display.
	 * 
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 * @see {@link player.receivers.BeatReceiver}
	 * @see {@link player.receivers.InstrumentReceiver}
	 */
	public ChangeVisualDisplay( Controller controller )
	{
		this.controller = controller;
	}

	/**
	 * The options displayed to the user allow
	 * them to select what is visualised.
	 */
	public void mouseReleased(MouseEvent e) 
	{
		Object[] possibilities = {"Display Pipes and Beats", "Display Pipes Only", "Display Beats Only"};
		try
		{
			String choice = (String)JOptionPane.showInputDialog(	
									null,
		                            "Please choose what to send to the visualization",
		                            "Visual Constructs",
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

	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mousePressed(MouseEvent e){}
}
