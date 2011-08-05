package listeners;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import gui.GUI;

/**
 * Listens for a change for the slider. In windows, this class listens but only picks up on changes
 * when the little button on the slider is clicked and dragged. So, if the user does not want to use the 
 * slider to seek at first, they can click and then drag, but that whole function is implemented in the SlideMouseListener
 * class.
 * 
 * @author Michael Pouris
 *
 */
public class SliderTimeChangeListener implements ChangeListener
{
	private Controller controller;
	
	public SliderTimeChangeListener(Controller controller)
	{
		this.controller = controller;
	}
	
	public void stateChanged(ChangeEvent e) 
	{
		JSlider slider = (JSlider) e.getSource();
		GUI gui = controller.getGUI();
		//if the user changes the slider by use of the slider button.
		//getValueIsAdjusting method is a boolean, and is true if it's
		//the user making changes.
		if( slider.getValueIsAdjusting() )
		{
			//converts seconds to a time code. IE 90 seconds to 1:30
			int seconds = slider.getValue();
			int min = seconds/60;				//gets a whole number which gives the whole minutes ie the mintues
			int secondsOfAMin = seconds % 60;	//gets the remainder ie the seconds
			String secondsOfAMinStr = "";
			secondsOfAMinStr += secondsOfAMin;
	     
			//if the song is 3 min and 2 seconds, we dont want it to show up as: 3:2, so 
			//it's checked and a zero is added just incase. The end result is 3:02
			if(secondsOfAMinStr.length() != 2)
			{
				gui.updateTimer(min+":"+"0"+secondsOfAMin);
			}
			else
			{
				gui.updateTimer(min+":"+secondsOfAMin);
			}
		}

	}

}
