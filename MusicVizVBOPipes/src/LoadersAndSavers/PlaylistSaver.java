package LoadersAndSavers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import Controller.Controller;
import GUI.GUI;
import Player.Player;
import Player.Song;
import Utilities.Utils;

/**
 * Saves the play list as a .mvp file.
 * 
 * @author Michael Pouris
 *
 */
public class PlaylistSaver 
{
	private Controller controller;
	private FileOutputStream fileOutputStream = null;
	private ObjectOutputStream objectOutputStream = null ;
	
	public PlaylistSaver(Controller controller)
	{
		this.controller = controller;
	}
	
	public void savePlaylist( File fileToSaveTo )
	{
		GUI gui = controller.getGUI();
		Player player = controller.getPlayer();
		if( gui.getPlaylist().getModel().getSize() != 0 )
		{
	    	try
	    	{
	    		//Gets the extention of the file, the user is saving to.
	        	String extension = Utils.getExtension(fileToSaveTo);
	        	
	        	//If the file has no extension, that means the user typed in a name to
	        	//save the playlist as. So, in this case, the program fills in the extension 
	        	//for the user.
	        	if( extension == null )
	        	{
	        		String newFileString = fileToSaveTo.toString() + ".mvp";
	        		fileToSaveTo = new File (newFileString);
	        	}
	        	//if there was an extension, but it's not of .mvp, then an exception is thrown.
	        	//the point of this, is that the user cannot overwrite other files that are not of
	        	//.mvp. 
	        	else if( !extension.equalsIgnoreCase(Utils.mvp) )
	        	{
	        		throw new IOException();
	        	}
	        	
	        	//This is the index of the song currently playing in the DLM.
	    		int index = 0;
	    		
				fileOutputStream = new FileOutputStream(fileToSaveTo);
				objectOutputStream = new ObjectOutputStream( fileOutputStream );
				
				//Turn all files to false for export to playlist
				DefaultListModel dlm = (DefaultListModel) gui.getPlaylist().getModel();
				for( int i = 0; i < dlm.size(); i++ )
				{
					//checks if the current song in the list is playing, if it is 
					//we take the index down, in order to set it to playing once the save is done.
					if( ( (Song) dlm.get(i) ).isPlaying() == true )
					{
						index = i;
					}
					( (Song) dlm.get(i) ).setIsPlaying(false);
				}
				
				objectOutputStream.writeObject( dlm );
				objectOutputStream.close();
				
				//now since everything in the list was set to false for playing. i must reset the 
				//proper song back
				( (Song) dlm.get(index) ).setIsPlaying(true);
			} 
	    	catch (FileNotFoundException e) 
			{
				JOptionPane.showMessageDialog(null,"Die Datei besteht nicht.");
			} 
	    	catch (IOException e)
			{
	    		JOptionPane.showMessageDialog(null,"Kann nicht die Datei schreiben.");
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Kann nicht die datei retten weil die Liste eine Groesse eins hat.");
		}
	}
}
