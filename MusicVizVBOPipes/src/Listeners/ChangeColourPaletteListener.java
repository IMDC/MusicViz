package Listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import Controller.Controller;

public class ChangeColourPaletteListener implements MouseListener
{
	private Controller controller;
	public ChangeColourPaletteListener( Controller controller )
	{
		this.controller = controller;
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {
        Object[] possibilities = {1, 2, 3};
        try
        {
	        int choice = (Integer)JOptionPane.showInputDialog(
	        		
	                            null,
	                            
	                            "Please choose a colour palette",
	                            
	                            "Colour Palette",
	                            
	                            JOptionPane.PLAIN_MESSAGE,
	                            
	                            null,
	                            
	                            possibilities,
	                            
	                            possibilities[0]);
	
	        //If a string was returned, say so.
	        controller.setColourSetToUse(choice);
        }
        catch(NullPointerException ex)
        {
        	controller.setColourSetToUse((Integer)possibilities[0]);
        }
	}
}
