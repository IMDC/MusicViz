package ca.ryerson.musicViz.model
{
	import ca.ryerson.musicViz.ApplicationFacade;
	import ca.ryerson.musicViz.model.vo.AnimateVO;
	
	import flash.events.*;
	import flash.net.XMLSocket;
	
	import org.puremvc.as3.interfaces.*;
	import org.puremvc.as3.patterns.proxy.Proxy;
	
	public class MaxProxy extends Proxy implements IProxy
	{
		private var server:String;
		private var port:int;
		private var max:XMLSocket;
		
		public static const TONAL_EVENT:String 	 = "tonalEvent";
		public static const BEAT_EVENT:String 	 = "beatEvent";
		public static const PGM_EVENT:String     ="pgmEvent"
		
		public static const NAME:String = "MaxProxy";
		
		public function MaxProxy()
		{
			//needed to retrieve proxy
			super(NAME);
			
			//initialize flashserver connection
			max = new XMLSocket();
			server = "localhost";
			port = 31337;
			max.connect(server, port);
			
			//event handlers for XML socket
			max.addEventListener(Event.CONNECT,onMaxConnect);
			max.addEventListener(Event.CLOSE, onMaxClose);
			max.addEventListener(DataEvent.DATA,onMaxData);
			
		}
		
		
		private function onMaxConnect (success:String):void 
		{
			if (success) {
				trace("Connected to "+server+" on port "+port);
			} else {
				trace("There has been an error connecting to "+server+" on port "+port);
			}
		}
	
		private function onMaxClose (close:String):void 
		{
			trace("Lost connection to "+server+" on port "+port);
		}
		
		public function sendToMax(transportCommand:String):void
		{
			max.send(transportCommand);
		}
		
		
		/* process data from max */
		private function onMaxData(doc:DataEvent):void {
			
			var argv:Array = new Array(); // create a new array
			var maxString:String = doc.data;
			var maxStringClean:String = maxString.substr(0,maxString.length-1); //clean last semicolon from max message
			argv = maxStringClean.split(" ");

		    switch(argv[0])
		    {
		    	case TONAL_EVENT:
		    	{	var fanID:String =argv[1];
		    		var tnClip:String = argv[2];
		    		var tnFrame:String = argv[3];
		  	
		    		var tnlAnimate:AnimateVO = new AnimateVO( tnClip,tnFrame)
		    		sendNotification(ApplicationFacade.TONAL,tnlAnimate,fanID)
		    		
		    		break;
		    	}	
		    	case BEAT_EVENT:
		    	{	var btClip:String = argv[1];
		    		var btFrame:String = argv[2];
		    		
		    		var btAnimate:AnimateVO = new AnimateVO( btClip,btFrame)
		    		sendNotification(ApplicationFacade.BEAT,btAnimate)

		    		break;
		    	}
		    	case PGM_EVENT:
		    	 {
		    	   fanID = argv[1];
		    	   var instFamily:String = argv[2];
		    	   
		    	   sendNotification(ApplicationFacade.PROGRAM,instFamily,fanID);
		    	 }   
		    }
	
		}

	}
}