package visualizer;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;



import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import player.messages.OpenGLMessage;
import player.messages.OpenGLMessageBeat;
import player.messages.OpenGLMessagePitchChange;
import player.messages.OpenGLMessageTonal;
import visualizer.camera.Camera;


public class ConcurrentVisualizer extends Thread implements GLEventListener, MouseMotionListener, KeyListener, MouseWheelListener
{
	public static final int MAX_CHANNELS = 16;
	public static final int MAX_BEAT_PIPES = 5;
	public static final int MAX_PIPES_PER_CHANNEL = 2;
	public static final int FLOATS_USED_PER_3D_POINT = 3;
	public static final int FLOATS_USED_PER_COLOUR = 4;
	public static final float MIN_SIZE_FOR_RADIUS = 1;
	
	private GLU glu;
	private Camera camera;
	
	//Used to assist the mouse
	private float prevX, prevY;

	//private FPSTimer timer;
	
	private ConcurrentPipe[][] pipes;
	private Beat[] beats;
	private ConcurrentHashMap<Integer, Boolean> pipesToUse;
	
	private float[][] beatColours = {{1,0,0,1},{0,1,0,1},{0,0,1,1},{0,1,1,1},{1,1,0,1}};
	private Color colours[] = { 
			new Color(255, 0, 0),  new Color(255, 72, 0), new Color(255, 145, 0),new Color(255, 217, 0),
			new Color(217, 255, 0),new Color(145, 255, 0),new Color(72, 255, 0) ,new Color(0, 255, 217),
			new Color(0, 217, 255),new Color(0, 145, 255),new Color(0, 72, 255) ,new Color(0, 0, 255),
			new Color(145, 0, 255),new Color(217, 0, 255),new Color(255, 0, 217),new Color(255, 0, 145)
			};
	
	//Setting up the lights: the light position
	private float lightPosition0[] = {30,0,-30,1};
    private float[] lightAmbient0 = {0.1f,0.1f,0.1f,1.0f};
    private float[] lightDiffuse0 = {1.0f,1.0f,1.0f,1.0f};
    
    private float lightEmissiveMaterial[] = {0.1f, 0.1f, 0.1f, 1.0f};
    private float brassAmbientMaterial[] = {0.33f, 0.22f, 0.03f, 1.0f};
    private float brassDiffuseMaterial[] = {0.78f, 0.57f, 0.11f, 1.0f};
    private float brassSpecularMaterial[] = {1,1,1,1};
	
	private int[][] lastPitchInChannelAndPipe;
	private int[][] lastVolumeInChannelAndPipe;
	private float[] pitchBends;
	private float[][][] lastCoorindates;
	private float[][] lastDimensions;
	
	public ConcurrentHashMap<Integer, ConcurrentLinkedQueue<OpenGLMessage>> concurrentMessageQueue;
	private AtomicBoolean[] activatedBeat;

	public ConcurrentVisualizer()
	{
		this.concurrentMessageQueue = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<OpenGLMessage>>();
		this.activatedBeat = new AtomicBoolean[MAX_BEAT_PIPES];
		for( int i = 0; i < MAX_BEAT_PIPES; i++  )
		{
			this.activatedBeat[i] = new AtomicBoolean(false);
		}
		
		this.lastPitchInChannelAndPipe = new int[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];
		this.lastVolumeInChannelAndPipe = new int[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];
		this.lastCoorindates = new float[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL][FLOATS_USED_PER_3D_POINT];
		this.lastDimensions = new float[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];
		this.pitchBends = new float[MAX_CHANNELS];
		
		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
			{
				this.lastPitchInChannelAndPipe[i][j] = 0;
				this.lastVolumeInChannelAndPipe[i][j] = 0;
				this.lastDimensions[i][j] = 0;
				
				for( int k = 0; k < FLOATS_USED_PER_3D_POINT; k++ )
				{
					this.lastCoorindates[i][j][k] = 0;
				}
			}
			pitchBends[i] = 0;
		}
		
		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			this.concurrentMessageQueue.put(i, new ConcurrentLinkedQueue<OpenGLMessage>() );
		}
		
		this.pipesToUse = new ConcurrentHashMap<Integer, Boolean>(2, 0.9f, 2);
	}
	
	public void display(GLAutoDrawable drawable)
	{
		final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        camera.update();
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, lightEmissiveMaterial, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPosition0, 0);

	    for( Integer i : this.pipesToUse.keySet() )
	    {
	    	for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
	    	{
	    		pipes[i][j].draw(drawable);
	    	}
	    }
	    
	    for( int i = 0; i < MAX_BEAT_PIPES; i++ )
	    {
	    	beats[i].draw(drawable, activatedBeat[i].get());
	    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, lightEmissiveMaterial, 0);
	    	activatedBeat[i].set(false);
	    }
	}

	/**
	 * Considering the JOGL system runs the OpenGL thread on the EventDispatch thread, 
	 * any calculations will slow the whole system down. The solution to this problem
	 * is the same solution used with the receivers, where a thread is created that
	 * handles the processing therefore to keep the GUI speed.
	 * 
	 * @see player.receivers.BeatReceiver
	 * @see player.receivers.ControlReceiver
	 * @see player.receivers.InstrumentReceiver
	 * @see player.receivers.PitchReceiver
	 */
	public void run()
	{
		while(true)
		{
			for( int i = 0; i < 10; i++ )
			{
				for( int j = 0; j < MAX_CHANNELS; j++ )
				{
					ConcurrentLinkedQueue<OpenGLMessage> queue = concurrentMessageQueue.get(j);
					OpenGLMessage message = queue.poll();
				    if(message == null )
				    {
				    	continue;
				    }
				   if( message.getMessage() == OpenGLMessage.NOTEON  || message.getMessage() == OpenGLMessage.NOTEOFF )
				   {
					   processTones((OpenGLMessageTonal)message );
				   }
				   else if( message.getMessage() == OpenGLMessage.BEAT )
				   {
					   activatedBeat[((OpenGLMessageBeat)message).getPipe()].set(true);
				   }
				   else if( message.getMessage() == OpenGLMessage.PITCHCHANGES )
				   {
					   processPitchChanges( (OpenGLMessagePitchChange)message );
				   }
				}
			}
			
			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Processes all tonal messages {@link OpenGLMessageTonal} and creates
	 * the proper animations.
	 * <p>
	 * @param message the message to process
	 */
	private void processTones( OpenGLMessageTonal message )
	{
		int channel = message.getChannel();
		int pipe = message.getPipe();
		int note = message.getNote();
		int velocity = message.getVolume();
		int noteDifference = 0;
		float alpha;
		float loomingPosition = 0;
		float relativeNoteOnGrid;
		float xAndY;
		float[] coordinates = new float[3];
		
		//The pipe that we are altering
		ConcurrentPipe p = pipes[channel][pipe];
		
		//Calculate the alpha for the new face
		//alpha = 0.003937f*velocity +0.5f;
		alpha = (0.5f/127f)*velocity + 0.5f;
		
		//Create a new position on the screen
		relativeNoteOnGrid = 5.5f * (note - 64);

		//Alter the positioning on the screen based 
		noteDifference = note - lastPitchInChannelAndPipe[channel][pipe];
		if( Math.abs(noteDifference) >= 4 && Math.abs(noteDifference) <=15 )
		{
			loomingPosition =(float)( 1.5*( Math.abs(noteDifference) ) + 0.5*( velocity - lastVolumeInChannelAndPipe[channel][pipe]) );
			loomingPosition = loomingPosition / 4;
		}
		
		//Save the last note and velocity it was played at. This will aide in figuring out how to position the new face
		lastPitchInChannelAndPipe[channel][pipe] = note;
		lastVolumeInChannelAndPipe[channel][pipe] = velocity;
		
		if( message.getMessage() == OpenGLMessageTonal.NOTEON )
		{
			//Calulate the size of the new face
			//xAndY = (0.03149f * (float) velocity + MIN_SIZE_FOR_RADIUS);
			xAndY = (4f/127f)*velocity + MIN_SIZE_FOR_RADIUS;
			
			//lastLoomingPositionInChannelAndPipe[channel][pipe] = loomingPosition;
			coordinates =  p.getInitialPlacement();
			coordinates[0] += loomingPosition;
			coordinates[1] = relativeNoteOnGrid;
			coordinates[2] = 0;
			
			lastCoorindates[channel][pipe] = coordinates;
			lastDimensions[channel][pipe] = xAndY;
			
			float newFace[][] =  p.createNewFace(xAndY, coordinates[0], coordinates[1], 0);
	
			//Must set the firstFaceData in order for the pipe to show the animations.
			p.getPositionAnimationList().add(newFace);
			p.getAlphaAnimationList().add(alpha);
			p.setFirstFaceData(xAndY);
		}
		else if( message.getMessage() == OpenGLMessageTonal.NOTEOFF )
		{
			coordinates =  p.getInitialPlacement();
			coordinates[1] = lastCoorindates[channel][pipe][1];
			coordinates[2] = 0;
			
			lastDimensions[channel][pipe] = 0;
			lastCoorindates[channel][pipe] = coordinates;
			
			float newFace[][] =  p.createNewFace(0, coordinates[0], coordinates[1], 0);

			//Must set the firstFaceData in order for the pipe to show the animations.
			p.getPositionAnimationList().add(newFace);
			p.getAlphaAnimationList().add(alpha);
			p.setFirstFaceData(0);
		}
	}
	
	/**
	 * This method processes the pitch changes from the Midi Sequencer.
	 * When a midi pitch change event is passed to the visualizer, it
	 * is processed, which tells the channel how to act. Then the pitch
	 * change needs to be applied to all the pipes in the channel. Therefore
	 * the method works in 2 parts:
	 * 1. Calculates the pitch change value. (At this point the change event
	 * 		is not applied to any pipes)
	 * 
	 * 2. Apply the pitch changes to the channel's pipe.
	 * 		This is done in the same way was a NoteOn/Off event 
	 * 		is applied to a pipe. 
	 * 
	 * @param pitchMessage: OpenGLMEssagePitchChange sent from the 
	 * 		MidiNoteReceiver
	 */
	private void processPitchChanges( OpenGLMessagePitchChange pitchMessage )
	{
		//1. Set the pitch bends for that one channel
		int channel = pitchMessage.getChannel();
		double scale = (double)15/(double)(pitchMessage.getRangeOfPitchValues());
		double pitchWheelValue =  scale * pitchMessage.getOffset();
		pitchBends[channel] = (float)(pitchWheelValue);//*3.5);
		
		
		//2. Apply same note for the channel affected
		ConcurrentPipe p;
		float[][] newFace;
		for( int i = 0; i < MAX_PIPES_PER_CHANNEL; i++)
		{
			p = pipes[channel][i];
			
			//Take the note the was last played for the channel's pipe, then apply the pitch bend to the y axis
			newFace = p.createNewFace(lastDimensions[channel][i], lastCoorindates[channel][i][0], lastCoorindates[channel][i][1]+pitchBends[channel] , 0);
			
			//Add the new face to the animation list
			p.getPositionAnimationList().add(newFace);
			//p.getAlphaAnimationList().addLast(1f);
			lastCoorindates[channel][i][1] += pitchBends[channel];
			p.setFirstFaceData(lastDimensions[channel][i]);
			lastCoorindates[channel][i][1] -= pitchBends[channel];
		}
	}
	
	/**
	 * This method is called first when the visualizer OpenGL is started.
	 * Set up opengl's state machine. These are enabled/disabled here because
	 * they are staying constant throughout the life of the program. Therefore
	 * the overhead is needed only once. The meaning of the parameters and the
	 * functions are all normal and basic openGL calls, they can be looked up in
	 * the JOGL API or the OpenGL api.
	 */
	public void init(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
        gl.glDisable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable (GL.GL_BLEND);
	    gl.glEnable(GL2.GL_LIGHTING);
	    gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	    gl.glEnable(GL.GL_CULL_FACE);
	    gl.glCullFace(GL.GL_BACK);
        
	    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, brassAmbientMaterial,0);
	    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, brassDiffuseMaterial,0);
	    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, brassSpecularMaterial,0);
	    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, lightEmissiveMaterial, 0);
	    gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 27);
	    
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
	
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient0,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse0,0);
		gl.glEnable(GL2.GL_LIGHT0);
		
		glu = new GLU();
		camera = new Camera(0, 80,500,-50,0,0);

        ((Component) drawable).addKeyListener(this);

		//Create the pipes and position them properly with their appropriate color
        int amountOfSections = 200;
		float[] initialColour = {1,0,0,1f};
		float[] initialPosition = {0,0,0};
		pipes = new ConcurrentPipe[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];

		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
			{
				initialColour[0] = (float) (colours[i].getRed() / 255.0);
				initialColour[1] = (float) (colours[i].getGreen() / 255.0);
				initialColour[2] = (float) (colours[i].getBlue() / 255.0);
				pipes[i][j] = new ConcurrentPipe(0,0, amountOfSections, initialColour, initialPosition);
				pipes[i][j].createPipe(drawable);
				initialPosition[1] -= 7;
			}
			initialPosition[1] = 0;
			initialPosition[0] = initialPosition[0]-25;
		}
		
		initialPosition[0] = 0;
		initialPosition[1] = 0;
		initialPosition[2] = 0;

		int positions[] = {-130,-60,-240,0,-200};
		int sizes[] = {250,33,15,15,33};
		int iSizes[] = {235,20,11,11,22};
		beats = new Beat[MAX_BEAT_PIPES];
		for( int i = 0; i < MAX_BEAT_PIPES; i++ )
		{
			initialPosition[0] = positions[i];
			beats[i] = new Beat(initialPosition, beatColours[i], amountOfSections, sizes[i],iSizes[i]);
		}
	}
	
	/**
	 * This method is called right after the Init() method, when the screen is created. 
	 * Therefore set up the projection and model/view matrix. Set the model/view matrix
	 * last because the draw method is called right after. When the Visualizer is 
	 * started, the methods are called in this order:
	 * 		1. init()
	 * 		2. reshape()
	 * 		3. draw()
	 * The methods are apart of GLEventListener in JOGL
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,int height)
	{
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0)
        {
            height = 1;
        }
        float h = (float) width / (float) height;
        
        //Disables vertical sync and therefore increases the speed
        gl.setSwapInterval(0);
        
        //Set the Projection matrix. Please note that this method is called right after the
        //the init method. Therefore I just place this in the reshape function
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(50.0f, h, 1.0, 3000.0);
        
        //Set the model view matrix
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
	}
	
	/**
	 * Sets the channels to animate.
	 * 
	 * @param pipesToUse
	 */
	public void setChannelsUsed(int[] pipesToUse)
	{
		for( int i = 0; i < 16; i++ )
		{
			this.pipesToUse.remove(i);
		}
		for( int i = 0; i < pipesToUse.length ; i++ )
		{
			this.pipesToUse.put(pipesToUse[i], true);
		}
		
	}
	
	/**
	 * This is the next part in the mouse movement that
	 * actually moves the camera around.
	 * When the mouse is clicked then dragged the camera is
	 * moved around.
	 */
	public void mouseDragged(MouseEvent e)
	{
		camera.positionCamera(e.getX()-prevX, e.getY()-prevY);
		prevX = e.getX();
		prevY = e.getY();
	}

	/**
	 * This method is used to help move the camera around
	 * the visualization in a circular motion. The method
	 * records the position of the mouse on the window every time 
	 * it moves.  
	 */
	public void mouseMoved(MouseEvent e)
	{
		prevX = e.getX();
		prevY = e.getY();
	}
	
	/**
	 * Handles the changes to the vizualization when keys are pressed.
	 * So far it handles: When the camera is moved forward and
	 * and the camera is moved back.
	 */
	public void keyPressed(KeyEvent e)
	{
        if( e.getKeyCode() == KeyEvent.VK_UP	)
        {
        	camera.moveCameraForward();
        }
        if( e.getKeyCode() == KeyEvent.VK_DOWN	)
        {
        	camera.moveCameraBackward();
        }

	}

	/**
	 * Resets the animation
	 */
	public void resetVisualizer()
	{
		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
			{
				pipes[i][j].resetPipeAnimation();
			}
		}
	}
	
	/**
	 * Whenever the mouse wheel on a physical mouse is rolled,
	 * the camera will zoom in or zoom out. That depends
	 * on which way the mouse is rolled.
	 */
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		camera.setZoom( 10 * e.getWheelRotation() );
	}
	
	public void dispose(GLAutoDrawable drawable){}
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
}
