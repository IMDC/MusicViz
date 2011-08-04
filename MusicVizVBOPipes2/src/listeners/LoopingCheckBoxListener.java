package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * Used as the listener class for the jcheckbox labeled loop. and 
 * @author Michael Pouris
 *
 */
public class LoopingCheckBoxListener implements ActionListener
{
	private boolean loop;
	
	public LoopingCheckBoxListener()
	{
		loop = false;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		loop = ( (JCheckBox) event.getSource() ).isSelected();
	}
	
	public boolean getToLoop()
	{
		return loop;
	}
}
