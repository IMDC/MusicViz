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

import timer.FPSTimer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedList;

import player.messages.OpenGLMessage;
import player.messages.OpenGLMessageBeat;
import player.messages.OpenGLMessageTonal;
import visualizer.camera.Camera;


public class Visualizer implements GLEventListener, MouseMotionListener, KeyListener, MouseWheelListener
{
	public static final int MAX_CHANNELS = 16;
	public static final int MAX_BEAT_PIPES = 5;
	public static final int MAX_PIPES_PER_CHANNEL = 3;
	public static final int FLOATS_USED_PER_3D_POINT = 3;
	public static final int FLOATS_USED_PER_COLOUR = 4;
	
	private GLU glu;
	private Camera camera;
	
	//Used to assist the mouse
	private float prevX, prevY;

	private FPSTimer timer;
	
	private Pipe[][] pipes;
	private Beat[] beats;
	private int[] pipesToUse = {0} ;
	
	private Color colours[] = { 
			new Color(255, 0, 0),  new Color(255, 72, 0), new Color(255, 145, 0),new Color(255, 217, 0),
			new Color(217, 255, 0),new Color(145, 255, 0),new Color(72, 255, 0) ,new Color(0, 255, 217),
			new Color(0, 217, 255),new Color(0, 145, 255),new Color(0, 72, 255) ,new Color(0, 0, 255),
			new Color(145, 0, 255),new Color(217, 0, 255),new Color(255, 0, 217),new Color(255, 0, 145)
			};
	
	//Setting up the lights: the light position
	private float lightPosition0[] = {-200,50,200};//{100,75,200,1};
    private float[] lightAmbient0 = {1f,1f,1f,1.0f};
    private float[] lightDiffuse0 = {1.0f,1.0f,1.0f,1.0f};
    private float emissiveLight0[] = {0.1f, 0.1f, 0.1f, 0f};
    private float specularMaterial[] ={ 0.5f, 0.5f, 0.5f, 1.0f };
	
	private int[][] lastPitchInChannelAndPipe;
	private int[][] lastVolumeInChannelAndPipe;
	private float[][] lastLoomingPositionInChannelAndPipe;
	private float[][][] lastCoorindates;
	private float[][][] lastDimensions;
	
	public HashMap<Integer, LinkedList<OpenGLMessage>> messageQueue;
	
	public Visualizer()
	{
		this.messageQueue = new HashMap<Integer, LinkedList<OpenGLMessage>>();
		
		this.lastPitchInChannelAndPipe = new int[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];
		this.lastVolumeInChannelAndPipe = new int[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];
		this.lastLoomingPositionInChannelAndPipe = new float[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];
		this.lastCoorindates = new float[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL][FLOATS_USED_PER_3D_POINT];
		this.lastDimensions = new float[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL][FLOATS_USED_PER_3D_POINT];
		
		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
			{
				this.lastPitchInChannelAndPipe[i][j] = 0;
				this.lastVolumeInChannelAndPipe[i][j] = 0;
				this.lastLoomingPositionInChannelAndPipe[i][j] = 0;
				
				for( int k = 0; k < FLOATS_USED_PER_3D_POINT; k++ )
				{
					this.lastCoorindates[i][j][k] = 0;
					this.lastDimensions[i][j][k] = 0;
				}
			}
		}
		
		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			this.messageQueue.put(i, new LinkedList<OpenGLMessage>());
		}
	}
	
	public void display(GLAutoDrawable drawable)
	{
		final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        timer.update();
        
        camera.update();
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPosition0, 0);
        
	    for( int j = 0 ; j < 20; j++ )
	    {
		    for( int i = 0; i < 16; i++ )
		    {
		    	LinkedList<OpenGLMessage> queue = messageQueue.get(i);
		    	if( queue.peek() != null )
		    	{
		    		OpenGLMessage message = queue.poll();
		    		if( message.getMessage() == OpenGLMessage.NOTEON || message.getMessage() == OpenGLMessage.NOTEOFF )
		    		{
		    			processTones((OpenGLMessageTonal)message, drawable);
		    		}
		    		else if( message.getMessage() == OpenGLMessage.BEAT )
		    		{
		    			beats[((OpenGLMessageBeat)message).getPipe()].draw(drawable, true);
		    		}
		    	}
		    }
	    }

	    /*Cube.draw(drawable, 10);
	    gl.glPushMatrix();
	    	gl.glTranslatef(20, 0, 0);
	    	Cube.draw(drawable, 10);
	    	gl.glTranslatef(20, 0, 0);
	    	Cube.draw(drawable, 10);
	    gl.glPopMatrix();*/
	    
	    for( int i = 0; i < pipesToUse.length ; i++ )
	    {	
	    	for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
	    	{
	    		pipes[ pipesToUse[i] ][j].draw(drawable);
	    	}
	    }
	    
	    for( int i = 0; i < MAX_BEAT_PIPES; i++ )
	    {
	    	beats[i].draw(drawable, false);
	    }
	}

	
	private void processTones( OpenGLMessageTonal message, GLAutoDrawable drawable )
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
		Pipe p = pipes[channel][pipe];
		
		//Calculate the alpha for the new face
		alpha = 0.003937f*velocity +0.5f;
		
		//Calulate the size of the new face
		xAndY = (0.03149f * (float) velocity + 1.0f);
		
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
			lastLoomingPositionInChannelAndPipe[channel][pipe] = loomingPosition;
			coordinates =  p.getInitialPlacement();
			coordinates[0] += loomingPosition;
			coordinates[1] = relativeNoteOnGrid;
			coordinates[2] = 0;
			
			
			lastCoorindates[channel][pipe] = coordinates;
			
			float newFace[][] =  p.createNewFace(xAndY, coordinates[0], coordinates[1], 0);
			
			p.getPositionAnimationList().addLast(newFace);
			p.getAlphaAnimationList().addLast(alpha);
		}
		else if( message.getMessage() == OpenGLMessageTonal.NOTEOFF )
		{
			coordinates =  p.getInitialPlacement();
			coordinates[1] = lastCoorindates[channel][pipe][1];
			coordinates[2] = 0;
			
			float newFace[][] =  p.createNewFace(0, coordinates[0], coordinates[1], 0);

			p.getPositionAnimationList().addLast(newFace);
			p.getAlphaAnimationList().addLast(alpha);
		}
	}
	
	
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
        
	    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emissiveLight0, 0);
	    gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, specularMaterial, 0);
	    gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 60.0f);
	    
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
	
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient0,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse0,0);
		gl.glEnable(GL2.GL_LIGHT0);
		
		glu = new GLU();
		
        ((Component) drawable).addKeyListener(this);
       
        camera = new Camera(0, 80,500,0,0,0);

		//Create the pipes and position them properly with their appropriate color
		float[] initialColour = {1,0,0,1f};
		float[] initialPosition = {10,0,0};
		pipes = new Pipe[MAX_CHANNELS][MAX_PIPES_PER_CHANNEL];

		for( int i = 0; i < MAX_CHANNELS; i++ )
		{
			for( int j = 0; j < MAX_PIPES_PER_CHANNEL; j++ )
			{
				initialColour[0] = (float) (colours[i].getRed() / 255.0);
				initialColour[1] = (float) (colours[i].getGreen() / 255.0);
				initialColour[2] = (float) (colours[i].getBlue() / 255.0);
				pipes[i][j] = new Pipe(0,0, 200, initialColour, initialPosition);
				pipes[i][j].createPipe(drawable);
				initialPosition[1] -= 7;
			}
			initialPosition[1] = 0;
			initialPosition[0] = initialPosition[0]+25;
		}
		
		initialPosition[0] = 0;
		initialPosition[1] = 0;
		initialPosition[2] = 0;
		beats = new Beat[MAX_BEAT_PIPES];
		for( int i = 0; i < MAX_BEAT_PIPES; i++ )
		{
			beats[i] = new Beat(initialPosition, initialColour, 200, 15);
			initialPosition[0] += 40;
		}
		
		
		timer = new FPSTimer();
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,int height)
	{
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0)
        {
            height = 1;
        }
        float h = (float) width / (float) height;
        
        
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
	
	public void setChannelsUsed(int[] pipesToUse)
	{
		this.pipesToUse = pipesToUse;
	}
	
	public void mouseDragged(MouseEvent e)
	{
		camera.positionCamera(e.getX()-prevX, e.getY()-prevY);
		prevX = e.getX();
		prevY = e.getY();
	}

	public void mouseMoved(MouseEvent e)
	{
		prevX = e.getX();
		prevY = e.getY();
	}
	
	public void dispose(GLAutoDrawable drawable)
	{	
	}
	
	@Override
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

	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent arg0) {
	}

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
	
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		camera.setZoom( 10 * e.getWheelRotation() );
	}
}
