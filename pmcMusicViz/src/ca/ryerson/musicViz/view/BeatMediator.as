package ca.ryerson.musicViz.view
{
	import ca.ryerson.musicViz.ApplicationFacade;
	import ca.ryerson.musicViz.model.vo.AnimateVO;
	import ca.ryerson.musicViz.view.components.BeatVisualize;
	
	import org.puremvc.as3.interfaces.IMediator;
	import org.puremvc.as3.interfaces.INotification;
	import org.puremvc.as3.patterns.mediator.Mediator;

	public class BeatMediator extends Mediator 
	{
		
		public static const NAME:String = "BeatMediator";
		
		public function BeatMediator( viewComponent:Object)
		{
			super(NAME, viewComponent);
		}
		
		override public function getMediatorName():String
		{
			return NAME;
		}
		
		public function get beatVisualize():BeatVisualize
		{
			return viewComponent as BeatVisualize;
		}
		

		
		override public function listNotificationInterests():Array
		{
			return [ApplicationFacade.BEAT];
		}
		
		override public function handleNotification(notification:INotification):void
		{
			switch ( notification.getName())
			{
				case ApplicationFacade.BEAT:
				{
					var animate:AnimateVO = notification.getBody() as AnimateVO;
					//beatVisualize.beat.text += animate.toString();
					
					switch(animate.clip)
					{
						case "kick":
							beatVisualize.percViz.kick.gotoAndPlay(animate.frame); 
							break;
							
						case "snare":
							beatVisualize.percViz.snare.gotoAndPlay(animate.frame);						
							break;
							
						case "hat":
						  	beatVisualize.percViz.hats.gotoAndPlay(animate.frame);
						   break;
						   
						case "tomcym":
							beatVisualize.percViz.tomcym.gotoAndPlay(animate.frame);
						   break; 
						  
						case "hand":
							beatVisualize.percViz.hand.gotoAndPlay(animate.frame);
						   break;
						        	 
						case "misc":
							beatVisualize.percViz.misc.gotoAndPlay(animate.frame);
						   break;
					}
					
					break;
				}
			}
		}
		
	
		
	}
}