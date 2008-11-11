package ca.ryerson.musicViz.view
{
	import ca.ryerson.musicViz.ApplicationFacade;
	import ca.ryerson.musicViz.model.MaxProxy;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.net.FileReference;
	
	import org.puremvc.as3.patterns.mediator.Mediator;
	 

	public class ApplicationMediator extends Mediator
	{
		private var maxproxy:MaxProxy;
		private var myFileReference:FileReference;
		
		public static const NAME:String = "ApplicationMediator";
		
		public function ApplicationMediator( viewComponent:Object)
		{
			super(NAME, viewComponent);
			facade.registerMediator(new BeatMediator(app.beatVisualize));
			facade.registerMediator(new TonalMediator(app.tonalVisualize));
			
			app.readFile.addEventListener(MouseEvent.CLICK, readFile);
			app.playFile.addEventListener(MouseEvent.CLICK, playFile);
			app.stopFile.addEventListener(MouseEvent.CLICK, stopFile);
			
			maxproxy = facade.retrieveProxy(MaxProxy.NAME) as MaxProxy;
			
			myFileReference = new FileReference(); 
			myFileReference.addEventListener(Event.SELECT, fileSelect);
			
		}
		
		/**
         * Cast the viewComponent to its actual type.
         * 
         * <P>
         * This is a useful idiom for mediators. The
         * PureMVC Mediator class defines a viewComponent
         * property of type Object. </P>
         * 
         * <P>
         * Here, we cast the generic viewComponent to 
         * its actual type in a protected mode. This 
         * retains encapsulation, while allowing the instance
         * (and subclassed instance) access to a 
         * strongly typed reference with a meaningful
         * name.</P>
         * 
         */
         
		protected function get app():MusicViz
		{
            return viewComponent as MusicViz
        }
		
		private function readFile(evt:MouseEvent):void
		{		
			sendNotification(ApplicationFacade.INITFANS)
			
			myFileReference.browse();
			
			
		}
		
		private function fileSelect(evt:Event):void
		{
			maxproxy.sendToMax(";read read "+ myFileReference.name +";")
		}
		
		private function playFile(evt:MouseEvent):void
		{
			
			maxproxy.sendToMax(";play bang;");
		}
		
		private function stopFile(evt:MouseEvent):void
		{
			
			maxproxy.sendToMax(";stop bang;");
		}
	}
}