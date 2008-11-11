package ca.ryerson.musicViz.controller
{
	import org.puremvc.as3.patterns.command.SimpleCommand;
	import org.puremvc.as3.interfaces.*;
	import ca.ryerson.musicViz.model.*;

	public class ModelPrepCommand extends SimpleCommand
	{

        override public function execute( note:INotification ) :void    
		{
            facade.registerProxy(new MaxProxy());
			
        }
		
	}
}