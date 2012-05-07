package listeners;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import controller.Controller;
import gui.GUI;
import player.Player;
import player.Song;

/**
 * Used to check for mouse clicks on the list, such as a double click which will load the song clicked on.
 * The reason why the method mouseClicked is not used, is because it seems more appropriate to use  mousePressed and released
 * methods considering they are the first methods called (Look at documentation in API). Also, It gives more control
 * as to what happens when the mouse is used on the implementing object. IE. when to get the JList and the list model.
 * 
 * @author Michael Pouris
 * @see gui.GUI#addListeners(Controller)
 */
public class ListMouseListener implements MouseListener
{
	private JList list;
	private DefaultListModel listModel;
	private int firstIndex;
	private Controller controller;
	
	/**
	 * This initialises a listener to listen to double clicking events.
	 * <p>
	 * the class will play the song that was double clicked.
	 * <p>
	 * @param controller controller allowing the listener to communicate with the back-end and front-end
	 */
	public ListMouseListener(Controller controller)
	{
		this.controller = controller;
	}

	/**
	 * This is the first method triggered, it saves the song that was clicked.
	 */
	public void mousePressed(MouseEvent event) 
	{
		list = (JList) event.getSource();
		listModel  = (DefaultListModel)list.getModel();
		firstIndex = list.getSelectedIndex();
	}

	/**
	 * This is the second method triggered, it will play the song selected in {@link #mousePressed(MouseEvent)}
	 * if it was double clicked.
	 */
	public void mouseReleased(MouseEvent event)
	{
		//System.out.println("ListMouseListener");
		if( event.getClickCount() == 2 && event.getButton() == 1 && firstIndex != -1)
		{
			Player player = controller.getPlayer();
			GUI gui = controller.getGUI();

			Song song = (Song) listModel.getElementAt(firstIndex);
	
			
			//finds the index of the current song in the list based on the song info in the player
			//and sets it to false, therefore giving the system the clue that it wont be played anymore
			int i =   player.currentSongPlaying().getIndex();
			((Song) listModel.get(i)).setIsPlaying(false);
  			
  			//Finds the object in the actual list and tells the list that it will be played
  			((Song) listModel.get(firstIndex)).setIsPlaying(true);
  			
  			//Takes the new song loaded by the user and tells the song it is playing and then
  			//loads it into the player
  			song.setIsPlaying(true);
  			player.openMidiFile(song,true);
			
			//Now i have to make the JToggleButton toggle to show this change.
			gui.setSelectedJToggleButton(true);
			gui.setTextJToggleButton("Pause");
		}	
		list.repaint();
	}
	
	public int getFirstIndex()
	{
		return firstIndex;
	}
	
	public void mouseClicked(MouseEvent event){}

	public void mouseEntered(MouseEvent event){}

	public void mouseExited(MouseEvent event){}
}
