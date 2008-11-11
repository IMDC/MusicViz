package ca.ryerson.musicViz
{
	 
    import org.puremvc.as3.interfaces.*;
    import org.puremvc.as3.patterns.facade.*;
    import org.puremvc.as3.patterns.proxy.*;	 
    
    import ca.ryerson.musicViz.controller.ApplicationStartupCommand
	 
	 
	 /**
     * A concrete <code>Facade</code> for the <code>MusicViz</code> application.
     * <P>
     * The main job of the <code>ApplicationFacade</code> is to act as a single 
     * place for mediators, proxies and commands to access and communicate
     * with each other without having to interact with the Model, View, and
     * Controller classes directly. All this capability it inherits from 
     * the PureMVC Facade class.</P>
     * 
     * <P>
     * This concrete Facade subclass is also a central place to define 
     * notification constants which will be shared among commands, proxies and
     * mediators, as well as initializing the controller with Command to 
     * Notification mappings.</P>
     */
	
	public class ApplicationFacade extends Facade
	{
		// Notification name constants
        public static const STARTUP:String 	 = "startup";
        public static const TONAL:String	 = "tonal";
        public static const BEAT:String      = "beat";
        public static const PROGRAM:String   = "program";
	    public static const INITFANS:String  = "initFans";
		
		
		/**
         * Singleton ApplicationFacade Factory Method
         */
        public static function getInstance() : ApplicationFacade 
		{
            if ( instance == null ) instance = new ApplicationFacade( );
            return instance as ApplicationFacade;
        }

        // initialize controller
         override protected function initializeController( ) : void 
        {
            super.initializeController(); 
            registerCommand( STARTUP, ApplicationStartupCommand );
        }
        
        // startup command
        public function startup( app:MusicViz ):void
        {
        	sendNotification( STARTUP, app );
        }
        


	}
}