package controller;

import gui.GUI;
import player.Player;

/**
 * This class contains the {@link Player} and {@link GUI} classes.
 * <p>
 * {@link Controller} object is passed in the program to objects, such
 * that modifications can be made to the {@link Player} and {@link GUI}
 * objects during run time. This is essential for updating the GUI,
 * loading songs, etc.
 * 
 * @author Michael Pouris
 * @see Player
 * @see GUI
 *
 */
public class Controller 
{
	private Player player; 
	private GUI gui;
	
	/**
	 * Sets the front-end and back-end of the program.
	 * 
	 * @param player
	 * @param gui
	 */
	public void initController( Player player, GUI gui )
	{
		this.player = player;
		this.gui = gui;
	}
	
	/**
	 * Returns the back-end MIDI player.
	 * 
	 * @return the MIDI player
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Returns the front-end GUIs.
	 * 
	 * @return the GUI
	 */
	public GUI getGUI()
	{
		return gui;
	}
}
