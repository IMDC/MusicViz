package visualizer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import timer.FPSTimer;

import controller.Controller;
import player.messages.OpenGLMessage;
import player.messages.OpenGLMessageBeat;
import player.messages.OpenGLMessagePitchChange;
import player.messages.OpenGLMessageTonal;
import visualizer.camera.Camera;

public class Visualizer implements GLEventListener, MouseMotionListener, MouseListener, MouseWheelListener, KeyListener
{	
	/**
	 * Some clarification of variables
	 * 
	 * lastCoords: Are used by the 5 beats and 3 pipes in each channel (channel 9 has 5 beats, other channels have 3 pipes). 
	 * Note the beats are the tones are very different. 
	 * 		Therefore, we need the array to be 16x5x"whatever". We cannot make it size 3 because we neglect the tonal pipes
	 * 
	 * lastDimensions: The same idea as the lastCoords variable
	 * 
	 * lastColourChannel: Unlike the last 2 variables above, this one is specifically ONLY for tonal channels, so we can just
	 *  	care about the 3 pipes per channels because this variable doesn't do anything with the beat channel. ONLY SERVES TONAL CHANNELs
	 *  
	 *  lastColourBeat: This variable only serves the beat channel and since the beat channel has ONLY 5 instruments,
	 *  	this variable can have a length of 5. ONLY SERVES BEATS
	 *  
	 *  lastPitchInChannelAndPipe: This variable is only to serve the tonal channels, NOT THE BEATS so they second
	 *  	dimension can stay size 3 and doesn't need to be 5 for the beats.
	 *  
	 *  lastVolumeInChannelAndPipe: Like the variable right above, it only serves tonal channels! ONLY TONAL CHANNELS
	 * 
	 */
	// Create new Settings, Part, and Camera objects.
	//Camera Camera[] = new Camera[3];
	Camera camera = new Camera(0,85,500,100,0,250);
	 float light_position[] = {100,75,200,1};//{100, 50, 200, 1.0f};
	 final static int noPipe = -10;
	 final static int noChannel = -10;
	/*
	 * partMapped is a HashMap used for extremely quick access to the parts of the visualization.
	 * For further explanation, please read a head. The visualization has 16 channels and everyone corresponds
	 * to a midi channel, the midi channel is broken into parts and each part in the channel is shown to the user.
	 * Please note that these parts are not a part of a Midi, this is a structure that we made for this project.
	 * So partsMapped will store the separate parts of the midi channel with a key string, which looks like this:
	 * "channel_<channelNumber>_pipe_<pipeNumber>". Since it is trivial to grab the channel from a midi event
	 * and also find out the pipe number, we can quickly assemble the string and grab the object to be manipulated in OpenGL.
	 * 
	 * channelOrderWithXLimit is a array that has the x coordinates of the channels in ascending order. So the channel
	 * with the lowest x coordinate (the coordinate closest to the user) is stored in position 0. This is used when the 
	 * bar in the song changes; we reorder the channels based on this variable.
	 */
	private HashMap<String, Part> partsMapped;
	private HashMap<String, BeatPart> beatPartsMapped;
	private float[] channelOrderWithXLimit = new float[16];
	
	//Used for last coords and dimensions
	//the 3D arrays are like this because there are 16 channels and each channel has 5 pipes. The 3rd dimension
	//comes in for the coordinates. So Position [10][2] holds an array of size 3.
	private float[][][] lastCoords = new float[16][5][3];
	private float[][][] lastDimensions = new float[16][5][];
	private float[][] lastColourChannel = new float[16][3];
	
	//for hong's looming idea
	private int[][] lastPitchInChannelAndPipe = new int[16][3];
	private int[][] lastVolumeInChannelAndPipe = new int[16][3];
	private float[][] lastLoomingPositionInChannelAndPipe = new float[16][3];
	
	/*
	 * Used to store the pitch bends
	 */
	private float[] pitchBends = new float[16];
	
	/*
	 * I use this variable as safe guard against errors that occur when playing a new song after the old
	 * is done. The error that occurs is a NullPointerException. It is caused by some residual data left over that
	 * tries to index a channel that was used in last song but not in the current song. There is more comments in the code
	 * where this key is used.
	 *  
	 */
	private Set<Integer> key;
	
	// The length (number of parts) of the pipes.
	private int partSize = 300;
	private Display display;

	
	// Past mouse position, used for camera rotation using the mouse.
	private int prevX = 0;
	private int prevY = 0;

	private FPSTimer timer;
	
	/*
	 *	Variables store the data to display the precompiled flashes for the beats.
	 *Each drum must have a variable for the display list, one to keep track of the frames
	 *and one to keep track of the coordinates.
	 *
	 * kick = pipe0, snare = pipe1, hand = pipe 2, tomcyn = 3, hat = 4. These pipes numbers
	 * refer to the position in the array
	 * 
	 * beatList: The program has "n" amount of drums. Therefore, we need to have the flashes stored in
	 * a display list for fast access and draw time. In our program, there are 5 drums and one drum
	 * has one pipe, each pipe corresponds to a type of drum; to figure this out, please look above.
	 * Example, beatList[0] is the 0th pipe (of "n" pipes) and it is the kick drum (look at BeatProcessor.java)
	 */	
	int[] beatList= new int[5];
	int[] beatFrames = new int[5];
	float[][] beatCoords = new float[5][3];
	int[] beatSizes = {160,50,40,35,30};
	
	private int noteChangesInInterval[][];
	
	private int[][] instrumentMinMax = new int[16][2];
	
	private Controller controller;
	private GLU glu = new GLU();
	
	/*
	 * This LinkedList should only be used as a queue. By NO MEANS should it be used as
	 * a stack or just to store data. The point of this data structure is to keep track of the messages made
	 * by the TonalProcessors in the MidiNoteReceiver object and make sure NO message is lost. Otherwise meaning,
	 * all notes will be processed. The LinkedList class offers very efficient methods for making the data structure act 
	 * like a queue; the methods poll(), peek(), add() perform these queue operations. These methods offer
	 * O ( 1 ) running time because no indexing is needed.
	 */
	public LinkedList<Integer> barQueue = new LinkedList<Integer>(); 
	
	public HashMap<Integer, LinkedList<OpenGLMessage>> messageQueue = new HashMap<Integer, LinkedList<OpenGLMessage>>();
	
	private int lastWaveUsed[][] = new int[16][3];
	private static final int waveForm = 2;
	
	public Visualizer( Controller controller )
	{
		this.controller = controller;
		for( int i = 0; i < 16; i++ )
		{
			messageQueue.put(i, new LinkedList<OpenGLMessage>());
		}
		for( int i = 0 ;i < lastWaveUsed.length; i++)
		{
			for( int j = 0; j < lastWaveUsed[0].length;j++)
			{
				lastWaveUsed[i][j] = waveForm;
			}
		}
		
	}
	
	public void display(GLAutoDrawable drawable)
	{	
		GL gl = drawable.getGL();
		
		gl.glLoadIdentity();
		gl.glPushMatrix(); //Removed June 30th
		//timer.update();
		//System.out.println(timer.getFPS());
		//Camera[0].update();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		camera.update();
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
	    for( int j = 0 ; j < 20; j++ )
	    {
		    for( int i = 0; i < 16; i++ )
		    {
		    	if(barQueue.peek() != null)
		    	{
		    		repositionChannels(barQueue.poll());
		    	}
		    	LinkedList<OpenGLMessage> queue = messageQueue.get(i);
		    	if( queue.peek() != null )
		    	{
		    		OpenGLMessage message = queue.poll();
		    		if( message.getMessage() == OpenGLMessage.NOTEON || message.getMessage() == OpenGLMessage.NOTEOFF )
		    		{
		    			//name = processTones((OpenGLMessageTonal)message, gl, glu);
		    			processTones((OpenGLMessageTonal)message, gl, glu);
		    		}
		    		else if( message.getMessage() == OpenGLMessage.PITCHCHANGES )
		    		{
		    			processPitchChanges((OpenGLMessagePitchChange)message, gl, glu);
		    		}
		    		else if( message.getMessage() == OpenGLMessage.BEAT )
		    		{
		    			processBeat((OpenGLMessageBeat)message, gl, glu);
		    		}
		    	}
		    }
	    }
	    
	    if( controller.isRunning() && partsMapped != null  )
		{
		    for( String s : partsMapped.keySet() )
			{
		    	int channel = partsMapped.get(s).channel;
		    	int pipe = partsMapped.get(s).pipe;

				//I want the pipes that are not tweened in this frame to still do what they were doing before. AKA their last tween.
				//if this isn't the one we just animated
		    	//System.out.println(s + " " + name);
				//if( !s.equalsIgnoreCase(name) )
				//{
					//Note that all were tweened in 5 frames before
					if( !partsMapped.get(s).getSettings().coordsTweening() && lastCoords[channel][pipe] != null )
					{ 
						/*
						 * Ellen's wave idea. The idea is that whenever she feels sound on a speaker, even
						 * if the frequency is constant, there is still movement. Therefore, just having
						 * a solid bar when the sound is constant doesn't mean anything, therefore Ellen
						 * suggested a wave. So when the pipe is doing nothing, it shall oscillate. 
						 */
						int wave=0;
						wave = -lastWaveUsed[channel][pipe];
						lastWaveUsed[channel][pipe] = wave;
						float[] tempLimit = partsMapped.get(s).getSettings().getLimit();
						float[] newLimit = {tempLimit[0],lastCoords[channel][pipe][1]+pitchBends[channel]+wave,lastCoords[channel][pipe][2]};
						//float[] newLimit = {tempLimit[0],lastCoords[channel][pipe][1]+pitchBends[channel],lastCoords[channel][pipe][2]};
						partsMapped.get(s).getSettings().tweenCoords(2, newLimit);
						pitchBends[channel] = 0;
					}
					if(!partsMapped.get(s).getSettings().dimensionsTweening() && lastDimensions[channel][pipe] != null) 
					{
						partsMapped.get(s).getSettings().tweenDimensions(2,lastDimensions[channel][pipe]);
					}
					if(!partsMapped.get(s).getSettings().colourTweening() && lastColourChannel[channel] != null) 
					{
						partsMapped.get(s).getSettings().tweenColours(2, lastColourChannel[channel]);
					}
				partsMapped.get(s).getSettings().playTweens();
			}
		    /*
		     * This is used to render the flashes for when the beats are initially hit.
		     */
		    for( int i = 0; i < beatFrames.length; i++ )
		    {
			    if(  (beatFrames[i] % 6) !=0 )
			    {
			    	gl.glPushMatrix();
						gl.glTranslatef(beatCoords[i][0], beatCoords[i][1], beatCoords[i][2] + display.getLists().size());
						gl.glCallList(beatList[i]);
					gl.glPopMatrix();
					beatFrames[i]++;
			    }
		    }
		    
	    	int theList = gl.glGenLists(1);
	    	gl.glNewList(theList, GL.GL_COMPILE);
				for(String s : partsMapped.keySet() )
				{
						partsMapped.get(s).setOpenGLComponents(gl, glu);
						gl.glPushMatrix();
						partsMapped.get(s).draw();
						gl.glPopMatrix();
				}
				
				for(String s : beatPartsMapped.keySet() )
				{
					beatPartsMapped.get(s).setOpenGLComponents(gl, glu);
					gl.glPushMatrix();
					beatPartsMapped.get(s).draw();
					gl.glPopMatrix();
				}
			gl.glEndList();
			display.add(theList);
			display.draw(0,0,1.0f);//1
			gl.glPopMatrix(); //Taken out on June 30th
			gl.glPopMatrix(); //Taken out on June 30th
		}
	    else if ( !controller.isRunning() && partsMapped != null  )
	    {
	    	gl.glPushMatrix();
	    	gl.glLoadIdentity();
	    	display.draw(0,0,1.0f);
	    	gl.glPopMatrix();
	    }
	}

	/**
	 * This method is called when a pitchchange message is sent through the Queues, which connect
	 * the back end and front end. The message contains (as a string): pitchchange_<channel>_<pitchOffset>_<rangeOfWheelForChannel> 
	 * and is then converted to a usable number, which is then added to all the pipes' pitches in that channel.
	 * The way it works is a pitch bend in MIDI is a applied to the WHOLE channel; therefore, since MIDI has 16 channels,
	 * I store the calculated value (to be added to the current pitch) in a size 16 array, where a position represents a channel.
	 * 
	 * Please take note that I do not allow the MidiNoteReciever object to reset the pitch bends in the Visualizer, therefore,
	 * I ignore the reset. This is because the pitch bends happen so quickly that they are hardly noticed, so I only apply the
	 * reset change after all the pipes in the channel are tween. They are reset in the display() method.
	 * 
	 * @param tokens
	 * @param gl
	 * @param glu
	 */
	private void processPitchChanges(OpenGLMessagePitchChange pitchChange, GL gl, GLU glu)
	{
		/*
		 * channel: the channel being affected by the bend
		 * 
		 * pitchOffset: the value that the pitch wheel was turned to and converted to the pitch number
		 * 
		 * rangeOfPitch: is the range of the slider. In Coarse tuning there are only semitone changes.
		 */
		int channel = pitchChange.getChannel();
		double pitchOffset = pitchChange.getOffset();
		int rangeOfPitch = pitchChange.getRangeOfPitchValues();
		
		//What the pitchOffset needs to be multiplied by in order to make the pitchOffset have a
		//max of 10 and min of -10. This is done to make the change see more visible.
		double scale = (double)10/(double)(rangeOfPitch/2);
		double pitchWheelValue =  scale * pitchOffset;

		pitchBends[channel] = (float)(pitchWheelValue*3.5);
	}
	
	private void processBeat( OpenGLMessageBeat beat , GL gl, GLU glu )
	{
		String name = null;
		int pipe = beat.getPipe();
		int velocity = beat.getVolume();
		float alpha;
		
		if( pipe != noPipe )
		{
			name = "channel_" + 9 + "_pipe_" + pipe;
			beatPartsMapped.get(name).setSize(beatSizes[pipe]);
			beatCoords[pipe] = beatPartsMapped.get(name).getCoordinates();
			beatFrames[pipe] = 1;
			
			alpha = 0.0011811f* (float)velocity;
			beatPartsMapped.get(name).setAlpha(alpha);
			beatPartsMapped.get(name).draw = true;
		}
	}
	
	//private String processTones( OpenGLMessageTonal message, GL gl, GLU glu )
	private void processTones( OpenGLMessageTonal message, GL gl, GLU glu )
	{
		String name = null;
		
		int channel = message.getChannel();
		int pipe = message.getPipe();
		name = "channel_"+channel+"_pipe_"+pipe;
		int velocity =  message.getVolume();
		int note = message.getNote();
		float relativeNoteOnGrd = - 100;
		float alpha;
			
		//To explain why this is here, I will explain the threading in this program first. The sequencer, GUI and Visualiser (OpenGL)
		//are all their own threads; meaning they all run separately with Java IPC for communication. Now, this method processes the tones 
		//it receives based on the channel but there is a point where the method screws up. This point happens when a song is playing and the user
		//clicks on another song to play. The reason why this happens, is because the data of the previous song is replaced with the new song's data
		//but a message from the previous song is left over. This left over message could contain a reference to a channel used in the old song
		//but is not used in the new song; causing a null pointer exception.
		if ( !key.contains(channel) )
		{
			return;
		}
		
		relativeNoteOnGrd = note - 64;
		relativeNoteOnGrd *=5.5;
		
		alpha = 0.003937f*velocity +0.5f;

		//This part is used to change the value of the current note to correspond to the pitch bend wheel.
		relativeNoteOnGrd = relativeNoteOnGrd + pitchBends[channel];
		
		//Hong's looming algorithm. The idea is that the current data to use is based on the current song's volume and pitch
		//minus the last 
		int noteDifference = note - lastPitchInChannelAndPipe[channel][pipe];
		float loomingPosition = 0;
		if( Math.abs(noteDifference) >= 4 && Math.abs(noteDifference) <=15 )
		{
			loomingPosition =(float)( 1.5*( Math.abs(noteDifference) ) + 0.5*( velocity - lastVolumeInChannelAndPipe[channel][pipe]) );
		}
		
		//Must set the current notes and volume to the last ones for the next iteration
		lastPitchInChannelAndPipe[channel][pipe] = note;
		lastVolumeInChannelAndPipe[channel][pipe] = velocity;
		
		float limits[] = partsMapped.get(name).getSettings().getLimit();
	
		//Pipe size based on the volume
		//size of the pipe depending on the volume
		//float xAndY = (0.03149f * (float) velocity + 1.0f);
		//float size[] = {xAndY,xAndY};
		
		//Pipe size based on the pitch
		/*float temp = 1f - ((float)note/127f);
		float xAndY = 4f * temp + 2f;
		if( temp < 0.5f )
		{
			xAndY = 3f * temp + 1.5f;
		}
		else
		{
			xAndY = 4f * temp + 3f;
		}*/
		
		/*if( note >= 64 )
		{
			//xAndY = ( (2f/63f) * note ) - (2f/63f);
			float zeroToOne = 1f - (((float)note/63f) - (64f/63f));
			xAndY = zeroToOne * (instrumentMinMax[channel][1] - instrumentMinMax[channel][0]) + instrumentMinMax[channel][0];
		}
		else
		{
			//xAndY = ( (3f/63f) * note ) + (252/63f);

			float zeroToOne = 1f - ((float)note / 63f);
			xAndY = zeroToOne * (instrumentMinMax[channel][1] - instrumentMinMax[channel][0]) + instrumentMinMax[channel][0];
			
		}*/
		//I want to map Low notes to big sizes. So If we get a low note, then I invert it to make it high
		//then I map the size over using intervals.
		//float zeroToOne = (1f - ((float)note / 127f));
		float xAndY = (1f - ((float)note / 127f)) * (instrumentMinMax[channel][1] - instrumentMinMax[channel][0]) + instrumentMinMax[channel][0];
		//System.out.println(channel);
		float size[] = {xAndY,xAndY};
		/*
		 * Ellen's idea is that everything should be more curvy therefore, we should not squeeze the pipe
		 * before showing another note on. The reason why we did this is because if we'd have another
		 * note on that is the exact same note as the the previous one, it would seem as nothing is happening
		 * to the pipe. So to show movement we squeezed first. But Ellen's idea is to constantly have the
		 * pipe ocsillating to show some kind of movement when the pipe is on. So her idea makes squeezing
		 * obsolete.
		 */
		//float firstSize[] = {0.0f,0.0f};
			
		//Add the new tween hopefully something happens that is good
		if( message.getMessage() == OpenGLMessageTonal.NOTEON )
		{
			//last looming position in pipe and channel is used for turning off the instrument only
			lastLoomingPositionInChannelAndPipe[channel][pipe] = loomingPosition;
			
			//sets the new coordinates within the x limit
			//float coords[] = {limits[0],relativeNoteOnGrd,0 };
			float coords[] = {loomingPosition + limits[0],relativeNoteOnGrd,0 };
	
			//set last coords so I can pull them out when i need to and start tweening
			lastCoords[channel][pipe] = coords;		
			partsMapped.get(name).getSettings().tweenCoords(2, coords);//2
			
			//for colour tweening
			float oldColour[] = lastColourChannel[channel];
			//the new colour just has a different alpha
			float newColour[] = {oldColour[0],oldColour[1],oldColour[2],alpha};
			partsMapped.get(name).getSettings().tweenColours(2, newColour);
			//set last color
			lastColourChannel[channel] = newColour;
			
			//just incase the note is constantly on, it will show up as a long pipe. In order to show a
			//change in the note, we squeeze it to nothing and then bring it back up in the next part
			//partsMapped.get(name).getSettings().tween(2, "dimensions", firstSize, false);
			
			//remember last size
			lastDimensions[channel][pipe] = size;
			partsMapped.get(name).getSettings().tweenDimensions(2, size);
		}
		else if(  message.getMessage() == OpenGLMessageTonal.NOTEOFF )
		{
			//float coords[] = {limits[0],0,0 };
			float coords[] = {limits[0],lastCoords[channel][pipe][1],0 };
			
			//set last coords so I can pull them out when i need to.
			lastCoords[channel][pipe] = coords;	
			partsMapped.get(name).getSettings().tweenCoords(2, coords);
			
			float oldColour[] = lastColourChannel[channel];
			float newColour[] = {oldColour[0],oldColour[1],oldColour[2],alpha};
			partsMapped.get(name).getSettings().tweenColours(2, newColour);
			//set last color
			lastColourChannel[channel] = newColour;
		
			float[] fl = {0.0f,0.0f};
			partsMapped.get(name).getSettings().tweenDimensions(2, fl);
			lastDimensions[channel][pipe] = fl;
		}
		
		//return name;
	}
	
	/**
	 * This method is called internally to reposition the pipes along the X-Axis. The pipes are only repositioned
	 * every interval; the intervals are specified by the programmer. A brief overview: the pipes are
	 * initially placed along the x-axis in descending order by the amount of note movements >= to a certain number. This
	 * means that the pipe family (instrument family) with the most amount of OVERALL movements >= 10 midi notes will be closest
	 * to the user. Although, we split the song into intervals and kept track of any note changes in each interval;
	 * This means for intervals 0s to 9s, 10s to 19s etc we reposition the pipe families to have the most prominent family 
	 * in that interval closets to the user. We do this very easily. At the beginning during preprocessing, we keep track
	 * of where the most prominent instruments are placed and then we save the x values in the variable called: "channelOrderWithXLimit".
	 * That variable has the closest x coordinate in position 0, and the x positions move farther away from the screen as you move 
	 * away from the front of the array.
	 * 
	 * The variable "noteChangesInInterval" is a 2D because we split the song into its intervals and channels. The rows signify 
	 * the 10 second intervals and the array at each interval signifies the separate channels in midi but position [c] != channel c, the 
	 * contents of [c] is the channel. It is done like this because [0 to n] has the channel with the most movements in position 0
	 * and it decreses as you reach n, if many channels are note used, then the channels is negative 10. Please look at the preprocessor 
	 * for more information on how this is done. Here is an example:
	 * 
	 * If we are at the interval called position, we get an array of channels and the channels are sorted in descending order 
	 * in relevance to the most amount of movements. So it could look like this: {6,2,8,4,3,9,-10,...}=noteChangesInInterval[position]
	 * this means that 6 has the most movements in this interval while channel 2 has the second most movements etc. Note: once -10 it hit
	 * then there are no more channels to reposition. Now since, we have an array with the x coordinates of the current pipes and 
	 * these coordinates are stored [0 to n] = [closest to farthest (relative to user)] then we loop through the x coordinates and
	 * coords[0] = x coords of channel 6's family (position 0 in that array)
	 * 
	 * @param position
	 */
	private void repositionChannels( int position )
	{
		int[] channels = noteChangesInInterval[position];
		int channel;
		String name;
		float[] coordinates;
		
		for(int i=0; i < channels.length; i++ )
		{
			channel = channels[i];
			if(channel == noChannel)
			{
				break;
			}
			name = "channel_" +channel + "_pipe_0";

			if( partsMapped.containsKey(name) && channel != 9)
			{
				coordinates = partsMapped.get(name).getSettings().getLimit();
				coordinates[0] = channelOrderWithXLimit[i];
				partsMapped.get(name).getSettings().tweenCoords(2,coordinates.clone());
				partsMapped.get(name).getSettings().getLimit()[0] = coordinates[0];
				
				name = "channel_" +channel + "_pipe_1";
				coordinates = partsMapped.get(name).getSettings().getLimit();
				coordinates[0] = channelOrderWithXLimit[i];
				partsMapped.get(name).getSettings().tweenCoords(2,coordinates.clone());
				partsMapped.get(name).getSettings().getLimit()[0] = coordinates[0];
				
				name = "channel_" +channel + "_pipe_2";
				coordinates = partsMapped.get(name).getSettings().getLimit();
				coordinates[0] = channelOrderWithXLimit[i];
				partsMapped.get(name).getSettings().tweenCoords(2,coordinates.clone());
				partsMapped.get(name).getSettings().getLimit()[0] = coordinates[0];
			}
			
		}
	}
	
	
	public void init(GLAutoDrawable drawable)
	{
		final GL gl = drawable.getGL();
		final GLU glu = new GLU();
		display = new Display(gl, glu, partSize);
		gl.glLoadIdentity();
		
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		gl.glDisable(GL.GL_LINE_SMOOTH);
	    gl.glEnable (GL.GL_BLEND);
	    gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	    gl.glEnable(GL.GL_NORMALIZE);
	    gl.glEnable(GL.GL_LIGHTING);
	    gl.glEnable(GL.GL_COLOR_MATERIAL);
	    gl.glEnable(GL.GL_CULL_FACE);
	    gl.glCullFace(GL.GL_BACK);

		//Enable the first light and then set the Materials for the pipes
	    gl.glEnable(GL.GL_LIGHT0);
	    
	    float emissiveMaterial[] = {0.1f, 0.1f, 0.1f, 0f};
	    float mat_specular[] ={ 1.0f, 1.0f, 1.0f, 10.0f };
	    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, emissiveMaterial, 0);
	    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
	    gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 90.0f);
	    
	    this.timer = new FPSTimer();
	    
	    /*
	     * Precompile the flashes which will be displayed when a drum is hit. This will be played ONLY
	     * when a drum is it.
	     */
	    beatList[0] = gl.glGenLists(1);
    	gl.glNewList(beatList[0], GL.GL_COMPILE);
			GLUquadric qobj = glu.gluNewQuadric();
			gl.glColor4f(255f,0f,0f,0.65f);
			glu.gluDisk(qobj, beatSizes[0]-7, beatSizes[0], 32, 1);
    	gl.glEndList();
    	
    	beatList[1] = gl.glGenLists(1);
    	gl.glNewList(beatList[1], GL.GL_COMPILE);
			qobj = glu.gluNewQuadric();
			gl.glColor4f(0f,255f,0f,0.65f);
			glu.gluDisk(qobj, beatSizes[1]-7, beatSizes[1], 32, 1);
		gl.glEndList();
		
		beatList[2] = gl.glGenLists(1);
    	gl.glNewList(beatList[2], GL.GL_COMPILE);
			qobj = glu.gluNewQuadric();
			gl.glColor4f(0f,0f,255f,0.65f);
			glu.gluDisk(qobj, beatSizes[2]-7, beatSizes[2], 32, 1);
		gl.glEndList();
		
		beatList[3] = gl.glGenLists(1);
    	gl.glNewList(beatList[3], GL.GL_COMPILE);
			qobj = glu.gluNewQuadric();
			gl.glColor4f(0f,255f,255f,0.65f);
			glu.gluDisk(qobj, beatSizes[3]-7, beatSizes[3], 32, 1);
		gl.glEndList();
		
		beatList[4] = gl.glGenLists(1);
    	gl.glNewList(beatList[4], GL.GL_COMPILE);
			qobj = glu.gluNewQuadric();
			gl.glColor4f(255f,255f,0f,0.65f);
			glu.gluDisk(qobj, beatSizes[4]-7, beatSizes[4], 32, 1);
		gl.glEndList();
	}	
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		// Initialize OpenGL objects.
		final GL gl = drawable.getGL();
		final GLU glu = new GLU();
		
		// Speed up render times by rendering off-screen.
		gl.setSwapInterval(0);

		// Set the viewport to the window size.
		gl.glViewport(0, 0, width, height);
		
		// Change the matrix mode to projection and change perspective.
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(45.0f, (double) width / (double) height, 10f,2000f);
		
		// Change the matrix mode to modelview for object positioning and transforms.
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		
	}

	public void mouseDragged(MouseEvent e)
	{
		camera.positionCamera(e.getX() - prevX, e.getY() - prevY);
		
		prevX = e.getX();
		prevY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		camera.setZoom( 10 * e.getWheelRotation() );
	}

	public void keyPressed(KeyEvent e)
	{
	    switch (e.getKeyCode())
	    {
	    	case KeyEvent.VK_UP:
	    		camera.moveCameraForward();
	    		break;

	    	case KeyEvent.VK_DOWN:
	    		camera.moveCameraBackward();
	    		break;
	    }
	}

	public void keyReleased(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mouseClicked(MouseEvent e){}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e)
	{	
		prevX = e.getX();
		prevY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
	}
	
	public void setNoteChangesInInterval( int[][] noteChangesInInterval )
	{
		this.noteChangesInInterval = noteChangesInInterval;
	}
	
	public void setMappedChannels( HashMap<String, Part> partsMapped )
	{
		this.partsMapped = partsMapped;
	}
	
	public void setMappedBeats( HashMap<String, BeatPart> beatPartsMapped )
	{
		this.beatPartsMapped = beatPartsMapped;
	}
	
	public void setLastCoordinates( int channel, int pipe, float[] coords )
	{
		lastCoords[channel][pipe] = coords;
	}
	
	public void setLastDimensions( int channel, int pipe, float[] dimensions )
	{
		lastDimensions[channel][pipe] = dimensions;
	}
	
	public void setLastPitchInChannelAndPipe( int channel, int pipe, int pitch )
	{
		lastPitchInChannelAndPipe[channel][pipe] = pitch;
	}
	
	public void setLastVolumeInChannelAndPipe( int channel, int pipe, int volume )
	{
		lastVolumeInChannelAndPipe[channel][pipe] = volume;
	}
	
	public void setLastColourChannel( int channel, float[] colour )
	{
		lastColourChannel[channel] = colour; 
	}

	public void setLastLoomingPositionInChannelAndPipe( int channel, int pipe, float loomingPosition )
	{
		lastLoomingPositionInChannelAndPipe[channel][pipe] = loomingPosition;
	}
	
	public float[] getLastDimensions( int channel, int pipe )
	{
		return lastDimensions[channel][pipe];
	}
	
	public void setChannelOrderWithXLimit( float[] channelOrderWithXLimit )
	{
		this.channelOrderWithXLimit = channelOrderWithXLimit;
	}
	
	public void resetVisualizerInformation()
	{
		partsMapped = null;
		channelOrderWithXLimit = new float[16];
		lastCoords =  new float[16][5][];
		lastDimensions =  new float[16][5][];
		lastPitchInChannelAndPipe = new int[16][3];
		lastColourChannel = new float[16][1];
		lastLoomingPositionInChannelAndPipe = new float[16][3];
		key = null;
		for( int i = 0; i < 16; i++ )
		{
			messageQueue.put(i, new LinkedList<OpenGLMessage>());
		}
		pitchBends = new float[16];
		barQueue = new LinkedList<Integer>();
	}
	
	public void setInstrumentMinMax(int[][] minMax)
	{
		instrumentMinMax = minMax;
	}
	
	public void setKeys( Set<Integer> keys )
	{
		this.key = keys;
	}
}