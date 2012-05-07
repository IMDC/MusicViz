package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * Used as the listener class for the jcheckbox labeled loop.
 * <p>
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class LoopingCheckBoxListener implements ActionListener
{
	private boolean loop;
	
	/**
	 * Creates a new checkbox listner, if it is selected, the song should be
	 * repeated.
	 */
	public LoopingCheckBoxListener()
	{
		loop = false;
	}
	
	/**
	 * Sets {@link #loop} to false or true depending on if the check
	 * box is clicked or not.
	 */
	public void actionPerformed(ActionEvent event)
	{
		loop = ( (JCheckBox) event.getSource() ).isSelected();
	}
	
	/**
	 * Returns true or false depending on if the song is clicked.
	 * @return
	 */
	public boolean getToLoop()
	{
		return loop;
	}
}
