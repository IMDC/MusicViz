package listeners;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;

public class ChangeVolumeListener implements MouseListener
{
	private JFrame volumeChangeFrame;
	private JPanel controls;
	private GridLayout gridLayout;
	private JTextField instrumentVol;
	private JTextField baseVol;
	private JSlider instrumentVolChanger;
	private JSlider beatVolChanger;
	private Font font1;
	
	public ChangeVolumeListener( final Controller controller )
	{
		
		this.volumeChangeFrame = new JFrame("Change Volume");
		this.volumeChangeFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.volumeChangeFrame.setSize(530, 100);
		this.volumeChangeFrame.setResizable(false);
		
		this.controls = new JPanel();
		this.gridLayout = new GridLayout(2,2);
		this.gridLayout.setHgap(30);
		this.gridLayout.setVgap(30);
		this.controls.setLayout( this.gridLayout );
		
		this.instrumentVol = new JTextField("Instrument Volume (%):");
		this.baseVol = new JTextField("Base Volume (%):");
		this.font1 = new Font("Times New Roman", Font.PLAIN ,  15);
		
		this.instrumentVol.setEditable(false);
		this.instrumentVol.setFont(font1);
		this.baseVol.setEditable(false);
		this.baseVol.setFont(font1);
		
		this.instrumentVolChanger = new JSlider(0,127);
		this.beatVolChanger = new JSlider(0,127);
		this.instrumentVolChanger.setValue(127);
		this.beatVolChanger.setValue(127);
		
		this.controls.add( this.instrumentVol );
		this.controls.add( this.instrumentVolChanger );
		this.controls.add( this.baseVol );
		this.controls.add( this.beatVolChanger );
		
		this.instrumentVolChanger.addChangeListener
				(
					new ChangeListener()
					{
						public void stateChanged( ChangeEvent e  )
						{
							int volume = ((JSlider)e.getSource()).getValue();
							for( int i = 0; i < 16; i++ )
							{
								if( i != 9 )
									controller.getPlayer().changeVolume(i, volume);
								else
									controller.getPlayer().changeVolume(9, beatVolChanger.getValue() );
							}
						}
					}
				);

		this.beatVolChanger.addChangeListener
		(
			new ChangeListener()
			{
				public void stateChanged( ChangeEvent e  )
				{
					int volume = ((JSlider)e.getSource()).getValue();
					controller.getPlayer().changeVolume(9, volume);
				}
			}
		);
		
		this.volumeChangeFrame.add(controls);
		this.volumeChangeFrame.setVisible(false);
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
		//Display the frame that displays the volume controls.
		//It is hidden and when closed it is hidden. This 
		//prevents the recycling of frames.
		volumeChangeFrame.setVisible(true);
	}
}
