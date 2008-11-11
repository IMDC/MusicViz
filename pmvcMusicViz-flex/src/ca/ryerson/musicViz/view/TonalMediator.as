package ca.ryerson.musicViz.view
{
	import ca.ryerson.musicViz.ApplicationFacade;
	import ca.ryerson.musicViz.model.vo.AnimateVO;
	import ca.ryerson.musicViz.view.components.TonalVisualize;
	
	import mx.core.UIComponent;
	import mx.flash.UIMovieClip;
	
	import org.puremvc.as3.interfaces.INotification;
	import org.puremvc.as3.patterns.mediator.Mediator;

	public class TonalMediator extends Mediator 
	{
		public static const NAME:String = "TonalMediator";
		
		public function TonalMediator( viewComponent:Object)
		{
			super(NAME, viewComponent);
		}
		
 		override public function getMediatorName():String
		{
			return NAME;
		}
		
 		public function get tonalVisualize():TonalVisualize
		{
			return viewComponent as TonalVisualize;
		}
		
		private function clearFansAndInstrumentList():void
		{
			//tonalVisualize.instFamily.text = "";
			
			
			//maximm 15 fans on visualization fan0 - fan14
			//hide tonal fans when loading a new song.
			for (var i:int=0;i<10;i++)
			{
				var fanComponent:UIMovieClip = tonalVisualize.getChildByName("fan"+i)as UIMovieClip;
				fanComponent.visible = false
			}
			
		}
		
		
		override public function listNotificationInterests():Array
		{
			return [ApplicationFacade.TONAL, ApplicationFacade.PROGRAM, ApplicationFacade.INITFANS];
		} 
		
		 override public function handleNotification(notification:INotification):void
		{
			switch ( notification.getName())
			{
				case ApplicationFacade.TONAL:
				{
					var animate:AnimateVO = notification.getBody() as AnimateVO;
					var fanID:String = notification.getType();
					
					//for use with text area output
					//var fanOut:TextArea = tonalVisualize.fans.getChildByName(fanID) as TextArea;
					//fanOut.text += animate.toString();
					var fanDisplay:UIMovieClip = tonalVisualize.getChildByName(fanID) as UIMovieClip;
					
					switch (animate.clip)
					{
						//int0 is note off, currently we are interpreting note off
						// events to continue to play out the animation, 
						// which brings the animation down to 0 at a prese rate.
						
						case "blade0":
						   
						   if(animate.frame =="int0")
						   {  fanDisplay.blade0.play(); }
						   else
						   { fanDisplay.blade0.gotoAndStop(animate.frame); }
						   break;
						   
					    case "blade1":
					     
					       if(animate.frame =="int0")
						   {  fanDisplay.blade1.play(); }
						   else
						   	fanDisplay.blade1.gotoAndStop(animate.frame);
						   break;
						
						case "blade2":
						   
						   if(animate.frame =="int0")
						   {  fanDisplay.blade2.play(); }
						   else
						   	  fanDisplay.blade2.gotoAndStop(animate.frame);
						   
						   break;
						
						case "blade3":
						    
						   if(animate.frame =="int0")
						   {  fanDisplay.blade3.play(); }
						   else 
						      fanDisplay.blade3.gotoAndStop(animate.frame);
						   break;
						
						case "blade4":
						
						   if(animate.frame =="int0")
						   {  fanDisplay.blade4.play(); }
						   else 
						      fanDisplay.blade4.gotoAndStop(animate.frame);
						   break;
						
						case "blade5":
						
						   if(animate.frame =="int0")
						   {  fanDisplay.blade5.play(); }
						   else 
						   	 fanDisplay.blade5.gotoAndStop(animate.frame);
						   break;
						
						case "blade6":
						
						   if(animate.frame =="int0")
						   {  fanDisplay.blade6.play(); }
						   else 
						   	  fanDisplay.blade6.gotoAndStop(animate.frame);
						   break;
						
						case "blade7":
						
						   if(animate.frame =="int0")
						   {  fanDisplay.blade7.play(); }
						   else 
						      fanDisplay.blade7.gotoAndStop(animate.frame);
						   break;
						
						case "blade8":
						
						   if(animate.frame =="int0")
						   {  fanDisplay.blade8.play(); }
						   else 
						      fanDisplay.blade8.gotoAndStop(animate.frame);
						   break;
						
						case "blade9":
						
						   if(animate.frame =="int0")
						   {  fanDisplay.blade9.play(); }
						   else 
						      fanDisplay.blade9.gotoAndStop(animate.frame);
						   break;
						   	       	   
					}
					
					break;
				
				}
				
				case ApplicationFacade.PROGRAM:
				{   
					fanID = notification.getType();
					fanDisplay = tonalVisualize.getChildByName(fanID) as UIMovieClip;
					fanDisplay.visible = true;
					fanDisplay.instIcon.gotoAndStop(notification.getBody());
					//tonalVisualize.instFamily.text += notification.getType() +" "+ notification.getBody() + "\n"
					
					break;
				}
				
				
				case ApplicationFacade.INITFANS:
				{
					
					clearFansAndInstrumentList();
					break;
				}
			}
		} 
		
	
		
	}
}