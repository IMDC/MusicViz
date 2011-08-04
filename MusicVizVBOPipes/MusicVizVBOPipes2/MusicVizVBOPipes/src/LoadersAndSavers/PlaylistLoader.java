package LoadersAndSavers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import Controller.Controller;
import GUI.GUI;
import Player.Player;
import Player.Song;
import Utilities.Utils;


/**
 * Handles the loading of play list files. IE .mvp
 * 
 * @author Michael Pouris
 *
 */
public class PlaylistLoader 
{
	private Controller controller;
	
	public PlaylistLoader( Controller controller )
	{
		this.controller = controller;
	}
	
    /**
     * Used to add a playlist File if dragged on.
     * @param playlistFile
     * @return
     */
    public boolean addPlaylist(File playlistFile)
    {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null ;
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		JList JListPlaylist = gui.getPlaylist();
		DefaultListModel oldListModel = (DefaultListModel) JListPlaylist.getModel();
		
		playlistFile = new File(Utils.getFilePath(playlistFile.toString()));
		
    	try
    	{
    		fileInputStream = new FileInputStream(playlistFile);
    		objectInputStream = new ObjectInputStream( fileInputStream );
    		//reads the file and creates a new default list model
       		DefaultListModel newListModel = (DefaultListModel) objectInputStream.readObject();
       		objectInputStream.close();
    		
       		if( oldListModel.size() == 0 )
       		{
       			//this sets the playing setting for the player but not the actual list
       			Song f = (Song) newListModel.get(0);
      			
       			//if there is nothing in the current list then, the new list is added to the Jlist object
       			player.openMidiFile( f,false );
       			
       			//must set the actual list sometime
       		    ( (Song) newListModel.get(0) ).setIsPlaying(true);
       		    
       			JListPlaylist.setModel(newListModel);
       		}
       		else
       		{
       			//if the list has something in it already, the songs in the playlist file are added to the end.
       			for( int i = 0 ; i < newListModel.size(); i++ )
       			{
       				oldListModel.addElement( newListModel.get(i) );
       			}
       		}
       		
       		return true;
		} 
    	catch (FileNotFoundException e) 
		{
    		JOptionPane.showMessageDialog(null,"Die Datei besteht nicht.");
        	return false;
		} 
    	catch (IOException e)
		{
    		JOptionPane.showMessageDialog(null,"Kann nicht die Datei lesen.");
        	return false;
		} 
    	catch (ClassNotFoundException e)
		{
    		JOptionPane.showMessageDialog(null,"Kann nicht die Klasse finden");
        	return false;
		}
    }
}
