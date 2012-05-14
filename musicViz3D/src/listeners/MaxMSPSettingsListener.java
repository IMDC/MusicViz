package listeners;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.SocketException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import controller.Controller;

/**
 * This class displays the controls for altering which
 * MIDI messages are sent through the UDP port.
 * <p>
 * The window is created and stays hidden until the user needs it.
 * When the window is closed, it is hidden, not destroyed.
 * <p>
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class MaxMSPSettingsListener implements MouseListener
{
	private JFrame maxMSPSettingsFrame;
	private JPanel controls;
	private GridLayout gridLayout;
	private JCheckBox toggleMaxMSPBeat;
	private JCheckBox toggleMaxMSPInstrument;
	private JCheckBox toggleMaxMSP;

	/**
	 * Creates the GUI window that contains the controls to alter
	 * what constructs are sent through the UDP port.
	 * <p>
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 */
	public MaxMSPSettingsListener( final Controller controller )
	{
		this.maxMSPSettingsFrame = new JFrame("MaxMSP Communication Settings");
		this.maxMSPSettingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.maxMSPSettingsFrame.setSize(300, 130);
		this.maxMSPSettingsFrame.setResizable(false);
		
		this.controls = new JPanel();
		this.gridLayout = new GridLayout(3,1);
		this.gridLayout.setHgap(0);
		this.gridLayout.setVgap(5);
		this.controls.setLayout( this.gridLayout );
		
		this.toggleMaxMSP = new JCheckBox("Enable/Disable MaxMSp Communication");
		this.toggleMaxMSP.setSelected(false);
		this.toggleMaxMSPBeat = new JCheckBox("Toggle Beat Communication");
		this.toggleMaxMSPBeat.setSelected(true);
		this.toggleMaxMSPInstrument = new JCheckBox("Toggle Instrument Communication");
		this.toggleMaxMSPInstrument.setSelected(true);
		
		this.toggleMaxMSP.addItemListener( new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e) 
			{
				if( e.getStateChange() == ItemEvent.SELECTED )
				{
					try 
					{
						controller.getPlayer().enableMaxMSPCommunication();
					} 
					catch (SocketException e1)
					{
						JOptionPane.showMessageDialog(null, "Cannot enable the MaxMSP socket because of a SocketException");
					}
				}
				else if( e.getStateChange() == ItemEvent.DESELECTED )
				{
					controller.getPlayer().resetMaxMSPCommunication();
					controller.getPlayer().disableMaxMSPCommunication();
				}
			}
		});
		
		this.toggleMaxMSPBeat.addItemListener( new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e) 
			{
				if( e.getStateChange() == ItemEvent.SELECTED )
				{
					controller.getPlayer().enableMaxMSPBeatCommunication();
				}
				else if( e.getStateChange() == ItemEvent.DESELECTED )
				{
					controller.getPlayer().disableMaxMSPBeatCommunication();
					controller.getPlayer().resetMaxMSPCommunication();
				}
			}
		});
		
		this.toggleMaxMSPInstrument.addItemListener( new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e) 
			{
				if( e.getStateChange() == ItemEvent.SELECTED )
				{
					controller.getPlayer().enableMaxMSPInstrumentCommunication();
				}
				else if( e.getStateChange() == ItemEvent.DESELECTED )
				{
					controller.getPlayer().disableMaxMSPInstrumentCommunication();
					controller.getPlayer().resetMaxMSPCommunication();
				}
			}
		});
		
		this.controls.add(this.toggleMaxMSP);
		this.controls.add(this.toggleMaxMSPInstrument);
		this.controls.add(this.toggleMaxMSPBeat);
		
		this.maxMSPSettingsFrame.add(controls);
		this.maxMSPSettingsFrame.setVisible(false);
	}

	/**
	 * When the object is clicked, the window is displayed.
	 */
	public void mouseReleased(MouseEvent e)
	{
		this.maxMSPSettingsFrame.setVisible(true);
	}
	
	public void mouseClicked(MouseEvent e){}

	public void mouseEntered(MouseEvent e){}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e){}
}
