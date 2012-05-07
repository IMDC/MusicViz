package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
 * @see gui.GUI#addListeners(Controller)
 */
public class SlideMouseListener implements MouseListener, MouseMotionListener
{
	
	private Controller controller;
	private JSlider slider;
	private boolean isRunning;
	
	/**
	 * Initialises a {@link MouseListener} and {@link MouseMotionListener}.
	 * <p>
	 * This class allows for all actions on a slider to be performed.
	 * <p>
	 * @param controller
	 */
	public SlideMouseListener( Controller controller )
	{
		this.controller = controller;
	}

	/**
	 * The first action to be performed is always a mouse pressed.
	 * <p>
	 * This will jump the slider to the appropriate position for use.
	 */
	public void mousePressed(MouseEvent e)
	{
		slider = (JSlider) e.getSource();
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		isRunning = player.isRunning();
		player.pause();
		gui.getVisualizer().resetVisualizer();
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
	
	/**
	 * The second method to be called. The slider must be completely set at 
	 * this point.
	 */
	public void mouseReleased(MouseEvent e) 
	{	
		Player player = controller.getPlayer();
		
		//When mouse is released, the song position is set
		player.setPosition( slider.getValue() );
		
		//Only start the song again, if the song was playing before the user changed position
		if( isRunning )
		{
			player.play();
		}
	}
	
	/**
	 * This is not a mouse pressed or release event, therefore when the mouse
	 * is dragged, the slider must be updated.
	 */
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
