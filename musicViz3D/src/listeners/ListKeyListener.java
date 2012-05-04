package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import controller.Controller;
import gui.GUI;
import player.Player;
import player.Song;
import utilities.Utils;

/**
 * Used as a listener for the playlist in this program. When the backspace or delete key 
 * it pressed, the selected items in the list are deleted.
 * 
 * @author Michael Pouris
 *
 */
public class ListKeyListener implements KeyListener
{
	private Controller controller;
	private JList list;
	private DefaultListModel listModel;
	
	public ListKeyListener(Controller controller)
	{
		this.controller = controller;
	}
	
	/**
	 * Checks if the keys pressed are either backspace or delete, if so then the selected
	 * items in the list are deleted.
	 */
	public void keyPressed(KeyEvent event) 
	{
		//System.out.println("ListKeyListener");
		//Checks for delete and backspace
		if( event.getKeyCode() == KeyEvent.VK_DELETE || event.getKeyCode() == KeyEvent.VK_BACK_SPACE ) 
		{			
			Player player = controller.getPlayer();
			GUI gui = controller.getGUI();
			//Retrieves the list and list model for use.
			list = (JList) event.getSource();
			listModel = (DefaultListModel) list.getModel();
			//The list of selected objects in the list
			Object[] objects = list.getSelectedValues();
			
			if( objects.length == listModel.getSize() )
			{
				listModel.clear();
			}
			
			//Loops through all the selected items. So if one item is deleted the loop runs once
			//otherwise runs as many times as needed.
			for( int i = 0; i < objects.length; i++)
			{				
				//removes song from list
				listModel.removeElement(objects[i]);
				//gets the name of the song deleted
				String tempName = new File ( Utils.getFilePath( objects[i].toString() ) ).getName();
				
				
				//If there are other items in the list and the item that was deleted was the song actually playing,
				//then stop the current song and load the next one in the list. Otherwise if the song that was deleted was the only
				//one then stop playing.
				if( listModel.getSize() >= 1 && tempName.equalsIgnoreCase(player.currentSongPlaying().getName()) )
				{
					player.stop();
					
					//Create a new Song
					String path = Utils.getFilePath( listModel.get(0).toString() );
					Song song = new Song( path, true );
					
					( (Song) listModel.get(0) ).setIsPlaying(true) ;
					
					player.openMidiFile(song,false);
				}
				else if( listModel.getSize() == 0)
				{
					player.stop();
					gui.updateTotalTime("0:00");
					gui.updateTimer("0:00");
				}
			}
			list.repaint();
		}

	}
	
	public void keyReleased(KeyEvent event){}

	public void keyTyped(KeyEvent event){}
}
