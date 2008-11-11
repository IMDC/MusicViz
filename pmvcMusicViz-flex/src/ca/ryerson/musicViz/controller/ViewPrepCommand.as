package ca.ryerson.musicViz.controller
{
	import org.puremvc.as3.patterns.command.SimpleCommand;
	import org.puremvc.as3.interfaces.*;
	import ca.ryerson.musicViz.view.ApplicationMediator;

	public class ViewPrepCommand extends SimpleCommand
	{
	
		override public function execute( note:INotification ) :void  
		{         // Register the ApplicationMediator
             facade.registerMediator( new ApplicationMediator( note.getBody() ) ); 
             
            
    	}  
	}
}