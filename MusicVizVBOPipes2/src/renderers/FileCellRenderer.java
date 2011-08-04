package renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import controller.Controller;
import player.Song;
import utilities.Utils;

/**
 * This class is essentially the renderer for the playlist. It governs the look of each action
 * the user invokes on the items. IE, how a currently playing song should be highlighted, how a selected song
 * should be shown. So, this class does exactly what the interface says it does - renders each cell.
 * 
 * @author Michael Pouris
 *
 */
public class FileCellRenderer extends JLabel implements ListCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	public FileCellRenderer(Controller controller)
	{
		super();
		setOpaque(true);
	}

	/**
	 * Governs the exact look of each cell.
	 */
	public Component getListCellRendererComponent(
	         JList list,			//The JList that uses this class as a cell renderer
	         Object value,			//The object in the JList's DefaultListModel that is being rendered
	         int index,				//The index of the object in the DefaultListModel
	         boolean isSelected,	//If selected by the user
	         boolean cellHasFocus)
	     {
		
			//Takes the object currently being rendered, considering it's a file, a new file is created.
			//setText() sets how the List will render this file. So, instead of the whole path showing up in the list,
			//only the name of the song will be displayed.
			boolean isPlaying = Utils.getIsPlaying( value.toString() );
  			String path = Utils.getFilePath( value.toString() );
  			Song file = new Song( path, isPlaying);		
			
			//If the file being rendered is the song playing, then it's rendered differently than 
			//a file that is waiting to be played.
			if(  file.isPlaying() )
			{
				setText( ">" + file.getName() );
				setForeground(isSelected ? Color.black : new Color(0,0,150) );
				setBackground(isSelected ? Color.gray : Color.white);
			}
			else 
			{
				setText( " " + file.getName() );
				setForeground(isSelected ? Color.black : new Color(0,0,150) );
				setBackground(isSelected ? Color.gray : Color.white);

			}
			 list.repaint();
	         return this;
	     }

}
