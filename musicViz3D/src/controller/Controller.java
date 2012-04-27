package controller;

import gui.GUI;
import player.Player;


/**
 * The program follows a 3 tier/MVC programming model. This is the class that allows the program to achieve 
 * that programming model. The controller is what separates the front end from the back end but still allows communication between them
 * by acting as a mediator.
 * 
 * I try to limit what each component can access in another component through each method in this class. What ever, I want 
 * a object to access, the method to do so is placed in this class. IE to access the GUI, a class will only be able to use
 * methods in this class that allow GUI access.
 * 
 * @author Michael Pouris
 *
 */
public class Controller 
{
	private Player player; 
	private GUI gui;
	
	public void initController( Player player, GUI gui )
	{
		this.player = player;
		this.gui = gui;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public GUI getGUI()
	{
		return gui;
	}
}
