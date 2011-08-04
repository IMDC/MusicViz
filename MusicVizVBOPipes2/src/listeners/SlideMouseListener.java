package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JSlider;

import controller.Controller;
import gui.GUI;
import player.Player;

/**
 * Class is used as a listener for the index slider. The main function of this class is to listen for mouse presses,
 * releases and drag events in order for the slider to respond to the user. Such events are, randomly clicking anywhere and allowing
 * the slider to jump to that position and allowing the previous action with further allowing them to keep dragging (This feature is not in java normally ).
 *  It is vital to stop the music while the user is indexing because the Java Sequencer acts funny if it's running.
 * 
 * The java api describes this but The way these events are called are:
 * 	1.MousePressed
 * 	2.MouseReleased
 *  3.MouseClicked(That is why i used mouse pressed and mouse released instead)
 * 
 * The mouse dragged method is use when the user does not click on the little slider button but only called
 * when the user clicks on the slider and drags, therefore, in the windows JVM at least, the change listener
 * is not called when the user takes this action. Also, when the user does use the little button on the slider 
 * and drags, the dragged method isn't called. On a Mac machine, both get called at the same time. 
 * 
 * @author Michael Pouris
 *
 */
public class SlideMouseListener implements MouseListener, MouseMotionListener
{
	
	private Controller controller;
	private JSlider slider;
	private boolean isRunning;
	
	public SlideMouseListener( Controller controller )
	{
		this.controller = controller;
	}

	public void mousePressed(MouseEvent e)
	{
		slider = (JSlider) e.getSource();
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		isRunning = player.isRunning();
		player.pause();
		
		if( ((DefaultListModel) gui.getPlaylist().getModel()).getSize() != 0 )
		{
			//used when the slider is clicked, but the small button isnt.
			//Jumps the slider to the selected position. Even though java does support snap to ticks,
			//there are thousands of ticks on a slider with a midi file. So, I have to implement that myself.
			//The calculation to do this is, 1. Get the X position where the user clicked in pixels, divide that by the 
			//horizontal position of the slider to get a percentage. Then Multiply that by the length of the song. 
			//IE. Imagine the slider is 200, I click position 100 and the length of the song is 2000. (100/200)*2000 = 1000
			//and then I jump the slider to that position
			int value = (int) ( ( (float) e.getX() / (float)slider.getSize().getWidth() ) * (float) slider.getMaximum() );
			slider.setValue( value );
		}

	}
	
	public void mouseReleased(MouseEvent e) 
	{	
		long theTickWhereBeatWasChanged = 0;
		
		Player player = controller.getPlayer();
		
		//When mouse is released, the song position is set
		player.setPosition( slider.getValue() );
		
		//When the beat changes, the ticks are converted to microseconds based on the tick where the last beat change was done.
		//Therefore, in order to keep my manual calculations correct, I have to find the interval where the new time is in so I can
		//use the proper interval for calculations. 
		/*
		 * Calculation is: 
		 * 60*(currentTickPlayer - tickWhereBeatChangeOccured)/BPM/Resolution + timeWhereLastBeatChanged
		 * 
		 * This is all done by Delta time.We are setting the timeWhereLastBeatChanged and tickWhereBeatChangeOccured
		 */
		//The current tick that we use to figure out where the last beat change was, in order to do calculation properly. The Slider
		//was just changed to this value. 
		long currentTick = player.getCurrentTickPositionOfSong();
		//<Tick>,<BPM> = at variable <tick> the beat changed to <BPM>
		TreeMap<Long, Float> ticksWhereBeatChanged = controller.getTicksWithBPMChanges();
		Object ticks[] = ticksWhereBeatChanged.keySet().toArray();
		double theTimeWhereTheBeatChanged = 0;
		for( int i = 0; i < ticks.length; i++ )
		{
			if( i + 1 < ticks.length )
			{
				if( currentTick >= (Long)ticks[i] && currentTick < (Long)ticks[i+1] )
				{
					theTickWhereBeatWasChanged = (Long)ticks[i];
					theTimeWhereTheBeatChanged = controller.getTicksWithBPMChangesToTime().get(theTickWhereBeatWasChanged);
					break;
				}
			}
			else
			{
				theTickWhereBeatWasChanged = (Long)ticks[i];
				theTimeWhereTheBeatChanged = controller.getTicksWithBPMChangesToTime().get(theTickWhereBeatWasChanged);
				break;
			}
		}
		controller.setLastTick(theTickWhereBeatWasChanged);
		controller.setLastTimeInSeconds(theTimeWhereTheBeatChanged);
		
		//Only start the song again, if the song was playing before the user changed position
		if( isRunning )
		{
			player.play();
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		GUI gui = controller.getGUI();
		//This method is used only when the user clicks on the slider line and expects the notch to jump
		//to that and still expects to drag all in one motion. IE click a spot, and then while still holding
		//the mouse down, drag the slider.
		if( ((DefaultListModel) gui.getPlaylist().getModel()).getSize() != 0 )
		{
			//used when the slider is clicked, but the small button isnt.
			int value = (int) ( ( (float) e.getX() / (float)slider.getSize().getWidth() ) * (float) slider.getMaximum() );
			slider.setValue( value );
		}
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e) {}
}
