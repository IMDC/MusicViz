package visualizer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class Pipe 
{
	//Amount of verticies per face
	public static final int AMOUNT_OF_VERTS = 6;
	public static final int FLOATS_USED_PER_COLOUR = 4;
	public static final float INITIAL_RADIUS = 0;
	private static final int AMOUNT_BETWEEN_FACES = 4;
	
	//Amount of FLOATS that make up the vertex and its information.
	//The information for a vertex is placed as follows:
	//VVVCCCCNNN. Therefore there are 10 floats for the information for a vertex.
	//Each V,C and N are a signal number specified as a float.
	private static final int SIZE_OF_VERTEX_INFORMATION = 10;
							
	//The amount of sections the pipe has. Note: 1 section has 2 faces.
	private final int amountOfSections; 
	
	//The amount of faces the pipe has. Note: The amount of faces
	//is the amountOfSections + 1
	private final int amountOfFaces; 
	
	//To make a pipe with 3 sides (AKA triangle), we need 4 verticies.
	//Therefore, the amount of sides we have is: AMOUNT_OF_VERTS - 1
	private final int amountOfSides; 
	
	//Holds the order that the verticies are drawn in
	private ArrayList<Integer> indicies;
	
	//Pointer to the pipe data in graphics memory. This must be bound
	//before use. Usage is altering and drawing. The same is for the index
	//pointer. It holds the pointer to the data in graphics memory.
	//The data specifies the order to draw the verticies in.
	private int pipePointer;
	private int indexPointer;
	
	private boolean isCreated = false;
	
	private float[][] lastFace;
	private LinkedList<float[][]> positionAnimationQueue;
	
	private float lastAlpha;
	private LinkedList<Float> alphaAnimationQueue;

	/*
	 * These are used for the placement of the pipes and random operations
	 * which are used for testing the object.
	 */
	private final float[] initialPlacement;
	private final float[] initialColor;
	
	private final int channel;
	private final int pipe;
	
	
	
	/**
	 * Once the pipe has been initialized, this method must be called right after
	 * in order to create the amount of memory needed in the graphics card. After
	 * the proper amount of memory was allocated by the pipe, the pipe is built
	 * and put into memory. Only after this method has finished, can the pipe be 
	 * drawn and animated.
	 */
	public void createPipe( GLAutoDrawable drawable )
	{
		if( !this.isCreated )
		{
			GL2 gl = drawable.getGL().getGL2();
			
			//Calculate the size (in bytes) that we need for the verticies, colors and normals
			int byteSizeForVerticies = amountOfFaces * AMOUNT_OF_VERTS * 3 * (Float.SIZE/Byte.SIZE);
			int byteSizeForColors = amountOfFaces * AMOUNT_OF_VERTS * 4 * (Float.SIZE/Byte.SIZE);
			int byteSizeForNormals = amountOfFaces * AMOUNT_OF_VERTS * 3 * (Float.SIZE/Byte.SIZE);
			
			//Create the buffer, bind it for use, then allocate the amount of memory needed.
			//The get the data as a float buffer.
			int[] pointers = new int[2];
			gl.glGenBuffers(1, pointers, 0);
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, pointers[0]);
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, byteSizeForVerticies + byteSizeForColors + byteSizeForNormals, null, GL2.GL_DYNAMIC_DRAW);
			FloatBuffer floatBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY).asFloatBuffer();
			
			//Draw the first face.
			//dataCounter is used to keep track of the float we are altering
			int dataCounter = -1;
			float x,y,z,eZ = 0;

			for( int j = 0; j < AMOUNT_OF_VERTS; j++ )
			{
				x = (float) ( INITIAL_RADIUS * Math.sin(2*j*Math.PI/(amountOfSides))) +  initialPlacement[0];
				y = (float) ( INITIAL_RADIUS * Math.cos(2*j*Math.PI/(amountOfSides))) +  initialPlacement[1];
				z = 0 + initialPlacement[2];
				
				//Setting the vertex
				dataCounter++;
				floatBuffer.put(dataCounter, x);
				dataCounter++;
				floatBuffer.put(dataCounter, y);
				dataCounter++;
				floatBuffer.put(dataCounter, z );
				
				//Setting the color
				dataCounter++;
				floatBuffer.put(dataCounter, initialColor[0]);
				dataCounter++;
				floatBuffer.put(dataCounter, initialColor[1]);
				dataCounter++;
				floatBuffer.put(dataCounter, initialColor[2]);
				dataCounter++;
				floatBuffer.put(dataCounter, initialColor[3]);
				
				//The Normal
				dataCounter++;
				floatBuffer.put(dataCounter, x);
				dataCounter++;
				floatBuffer.put(dataCounter, y);
				dataCounter++;
				floatBuffer.put(dataCounter, 0);
			}
			
			//The first face is done, therefore I have to make a second face that is 
			//one away from the first. Each consecutive face is one away.
			for( int i = 0; i < this.amountOfSections; i++ )
			{	
				for( int j = 0; j < AMOUNT_OF_VERTS; j++ )
				{
					x = (float) ( INITIAL_RADIUS * Math.sin(2*j*Math.PI/(amountOfSides))) + initialPlacement[0];
					y = (float) ( INITIAL_RADIUS * Math.cos(2*j*Math.PI/(amountOfSides))) + initialPlacement[1];
					z = 0  + initialPlacement[2];
					
					//The Vertex
					dataCounter++;
					floatBuffer.put(dataCounter, x);
					dataCounter++;
					floatBuffer.put(dataCounter, y);
					dataCounter++;
					floatBuffer.put(dataCounter, z + eZ);
					
					//The Color
					dataCounter++;
					floatBuffer.put(dataCounter, initialColor[0]);
					dataCounter++;
					floatBuffer.put(dataCounter, initialColor[1]);
					dataCounter++;
					floatBuffer.put(dataCounter, initialColor[2]);
					dataCounter++;
					floatBuffer.put(dataCounter, initialColor[3]);
					
					//The Normal
					dataCounter++;
					floatBuffer.put(dataCounter, x);
					dataCounter++;
					floatBuffer.put(dataCounter, y);
					dataCounter++;
					floatBuffer.put(dataCounter, 0);
				}	
				eZ = eZ + AMOUNT_BETWEEN_FACES;
			}
			
			float lf[][] = getFace(floatBuffer, 0);
			for( int i = 0; i < lf.length; i++ )
			{
				for( int j = 0; j < lf[0].length; j++ )
				{
					lastFace[i][j] = lf[i][j];
				}
			}
			
			gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
			
			//The vertices and their respective color and normal information are now stored.
			//Now we have to store the order at which the verticies need to be drawn.
			//For example, just say I have 2 faces. I Stored the information as:
			//Face 0: 0,1,2,3,4,5 Face 1: 6,7,8,9,10,11. To draw a cylinder, we need to use
			//a triangle strip. Therefore, I cannot draw the verticies from 0 to 10,
			//I must draw it 0,6,1,7,2,8,3,9,4,10,5,11
			this.indicies = new ArrayList<Integer>();
			for( int i = 0; i < amountOfFaces - 1; i++ )
			{
				for( int j = i * AMOUNT_OF_VERTS; j < (i+1)*AMOUNT_OF_VERTS; j++ )
				{
					this.indicies.add(j);
					this.indicies.add(j+AMOUNT_OF_VERTS);
				}
			}
			
			//Now Generate the IBO. This explains to the video card how to access the veritices
			gl.glGenBuffers(1, pointers,1);
			gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, pointers[1]);
			gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, (Integer.SIZE/Byte.SIZE) * indicies.size(), null, GL.GL_STATIC_DRAW);
			IntBuffer intBuff = gl.glMapBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, GL.GL_WRITE_ONLY).asIntBuffer();
			for( int i = 0; i < indicies.size(); i++ )
			{
				intBuff.put(i, indicies.get(i));
			}
			gl.glUnmapBuffer(GL.GL_ELEMENT_ARRAY_BUFFER);
			
			//This is the pointers that I will use to access the buffer memory on the graphics card.
			this.pipePointer = pointers[0];
			this.indexPointer = pointers[1];
			this.isCreated = true;
		}
	}
	
	/**
	 *	Sets the primitives that the pipe object uses. It does not create the memory space
	 * in the graphics card and it does not initialize the pipe object in graphics memory.
	 * This constructor tells the pipe: 1. How many sections is needed, the color and the placement in the world.
	 * To initialize the pipe fully (create the pipe in graphics memory space so it can be drawn), obj.createPipe(...)
	 * must be called.
	 *
	 * @param int amountOfSections: The amount of sections the pipe has. The amount of faces
	 * a pipe has is one more than the amount of sections. For example, if a pipe with one section
	 * is wanted, then it has 2 face: the beginning face and the end face. For 2 sections, there are
	 * 3 faces: The beginning face, the middle face and the end face.
	 * 
	 * @param float color: The color of the pipe in RGBA
	 * 
	 * @param float translations: The initial placement of the pipe in the world.
	 */
	public Pipe( int channel, int pipe, int amountOfSections, float[] color, float[] translations )
	{
		//A flag stating that the pipe has not been created in graphics memory
		this.isCreated = false;
	
		this.channel = channel;
		this.pipe = pipe;

		//Copies the colours and placement of the pipe
		this.initialPlacement = translations.clone();
		this.initialColor = color.clone();
		
		//Sets the amount of sections wanted, sets the amount of faces
		//as well as the number of sides that is needed.
		this.amountOfSections = amountOfSections;
		this.amountOfFaces = amountOfSections + 1;
		this.amountOfSides = Pipe.AMOUNT_OF_VERTS - 1;
		
		this.positionAnimationQueue = new LinkedList<float[][]>();
		this.alphaAnimationQueue = new LinkedList<Float>();
		
		this.lastFace= new float[AMOUNT_OF_VERTS][3];
		this.lastAlpha = 1f;
	}
	
	public void draw(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		animate(drawable);
		
		gl.glPushMatrix();
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, pipePointer);
	        gl.glVertexPointer(3, GL2.GL_FLOAT, 40, 0);
	        gl.glColorPointer(4, GL2.GL_FLOAT, 40, 12);
	        gl.glNormalPointer(GL2.GL_FLOAT, 40, 28);
	        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexPointer);
	        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, indicies.size()  , GL.GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
	}
	
	/**
	 * Cascades each face from the 0th face to the (n-1)th face for n faces.
	 * This accomplishes animating the pipe. For example, the 0th face's x and
	 * y coordinates are given to the 1st faces, then the 1st is given to the 2nd.
	 * After the faces were cascaded, the new face is removed from the queue and
	 * set to the very first face. If there is nothing in the queue, then 
	 * the saved last face is repeated.
	 * 
	 * @param drawable
	 */
	private void animate(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		//To animate the pipe, I need to get the data that is within the buffer and alter it.
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, pipePointer);
		FloatBuffer floatBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY).asFloatBuffer();
		
		//Start cascading the positions from the start (0th face) to the last face.
		//When the positions are finished being cascaded down, the new face is 
		//set as the 0th face. Therefore, completing the animation
		//Algorithm: Save the 0th face. (This face will be the 1st face)
		float[][] currentFace;
		float currentAlpha;
		float[][] beforeFace = getFace(floatBuffer, 0);
		float beforeAlpha = getAlphaForFace(floatBuffer, 0);
		
		//Algorithm: We are on the ith face, we save this face because we want
		//to set the ith face to the i-1 face. After we set ith face to the i-1th
		//face, we set the old value of the ith face to the i-1th face and move on.
		for( int i = 1; i < amountOfFaces; i++ )
		{
			currentFace = getFace(floatBuffer, i);
			currentAlpha = getAlphaForFace(floatBuffer, i);
			
			if( i >= 150 )
			{
				beforeAlpha = 0.75f - ((i/(float)50) - (150.0f/50.0f));
			}
			setFace(floatBuffer, beforeFace, i);
			setAlphaForFace(floatBuffer, beforeAlpha, i);
			
			
			beforeFace = currentFace.clone();
			beforeAlpha = currentAlpha;
		}
		
		//The Rest of the faces have been changed, now it is time
		//to alter the first face (0th face). If there is
		//nothing in the queue, then the pipe needs to do what 
		//it was doing before. Hence it must play the last face.
		//Therefore something is always in the queue
		if(positionAnimationQueue.isEmpty())
		{
			positionAnimationQueue.addLast(lastFace);
		}
		if(alphaAnimationQueue.isEmpty())
		{
			alphaAnimationQueue.addLast(lastAlpha);
		}
		
		//Remove what must be played next and set it to the
		//last face. This is in case of the queue being empty 
		float[][] newFace = positionAnimationQueue.removeFirst();
		lastFace = newFace;
		setFace(floatBuffer,newFace,0);
		
		//Do the same for the alpha
		float newAlpha = alphaAnimationQueue.removeFirst();
		lastAlpha = newAlpha;
		setAlphaForFace(floatBuffer, newAlpha, 0);
		
		gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
	}
	
	/**
	 * This creates a new face for the pipe which is placed at the origin
	 * of the world with a radius that is specified. After each vertex is
	 * created we apply a translation to put it in the proper part of
	 * the world. This returns a 2D array of primitive floats
	 * 
	 * @param radius: The pipe's radius
	 * @param x: X axis translation
	 * @param y: Y axis translation
	 * @param z: z axis translation
	 * @return
	 */
	public float[][] createNewFace(float radius, float x, float y, float z)
	{
		float[][] newFace = new float[AMOUNT_OF_VERTS][3];
		
		for( int i = 0; i < AMOUNT_OF_VERTS; i++ )
		{
			newFace[i][0] = (float) ( radius * Math.sin(2*i*Math.PI/(amountOfSides))) + x;
			newFace[i][1] = (float) ( radius * Math.cos(2*i*Math.PI/(amountOfSides))) + y;
			newFace[i][2] = 0 + z;
		}
		
		return newFace;
	}
	
	/**
	 * This method is private because it is only supposed to be used by the object.
	 * Preferably this is called once the buffer is received and then the buffer is
	 * passed to this function. For example, when animating the pipe starts, the
	 * buffer is grabbed and then after the animation is complete, we return the
	 * buffer to memory. This method is used in between the grabbing and returning 
	 * the buffer.
	 * 
	 * @param buffer: A float buffer that holds all the data for this pipe.
	 * @param face: The face needed. If there are 5 sections, there are 6 faces labelled 0 to 5
	 * @return 2D array of floats [amount of verts][3 points]
	 */
	private float[][] getFace( FloatBuffer buffer, int face )
	{
		float[][] verticiesValues = new float[AMOUNT_OF_VERTS][3];
		
		for( int i = face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION, j =0 ; 
    	i < face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION + (AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION);
    	i+=SIZE_OF_VERTEX_INFORMATION, j++ )
		{
			verticiesValues[j][0] = buffer.get((i + 0));
			verticiesValues[j][1] = buffer.get((i + 1));
			verticiesValues[j][2] = buffer.get((i + 2));
		}
		
		return verticiesValues;
	}
	
	
	private float[][] getColourForFace( FloatBuffer buffer, int face )
	{
		float[][] colourValues = new float[AMOUNT_OF_VERTS][FLOATS_USED_PER_COLOUR];
		
		for( int i = face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION, j =0 ; 
    	i < face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION + (AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION);
    	i+=SIZE_OF_VERTEX_INFORMATION, j++ )
		{
			colourValues[j][0] = buffer.get((i + 3));
			colourValues[j][1] = buffer.get((i + 4));
			colourValues[j][2] = buffer.get((i + 5));
			colourValues[j][3] = buffer.get((i + 6));
		}
		
		return colourValues;
	}
	
	private float getAlphaForFace( FloatBuffer buffer, int face )
	{
		float alpha = -1;
		
		for( int i = face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION, j =0 ; 
    	i < face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION + (AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION);
    	i+=SIZE_OF_VERTEX_INFORMATION, j++ )
		{
			alpha = buffer.get((i + 6));
		}
		
		return alpha;
	}
	
	private void setAlphaForFace( FloatBuffer buffer, float alpha, int face )
	{
		for( int i = face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION, j =0 ; 
    	i < face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION + (AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION);
    	i+=SIZE_OF_VERTEX_INFORMATION, j++ )
		{
			buffer.put((i+6),alpha);
		}
	}
	
	/**
	 * Like the getFace() method above, this method is intented for use between
	 * grabbing the binded buffer and releasing the binded buffer. The reason for
	 * creating the method like that is for performance. If this method bounded
	 * and grabbed the buffer from memory everytime it was called, it would be
	 * inefficient. This can only be called if the buffer was grabbed from memory
	 * then passed as a FloatBuffer Object. Considering this is used to 
	 * animate the pipe, it does not change the Z coordinate.
	 * 
	 * @param buffer: FloatBuffer The buffer that holds the pipe's data
	 * @param values: a 2D array of floats that hold the new face. If a face has 6
	 * verts, then the array is 6 rows by 3 columns.
	 * @param face: The face one wants to change. If there are 4 sections, then there are
	 * 5 faces labelled 0 to 4.
	 */
	private void setFace( FloatBuffer buffer, float[][] values , int face )
	{
		for( int i = face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION, j =0 ; 
    	i < face * AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION + (AMOUNT_OF_VERTS * SIZE_OF_VERTEX_INFORMATION);
    	i+=SIZE_OF_VERTEX_INFORMATION, j++ )
		{
			buffer.put((i + 0),values[j][0]);
			buffer.put((i + 1),values[j][1]);
			//buffer.put((i + 2),values[j][2]);
		}
	}
	
	public boolean isCreated()
	{
		return isCreated;
	}
	
	public float[] getInitialPlacement()
	{
		return initialPlacement.clone();
	}
	
	public int getChannel()
	{
		return channel;
	}
	
	public int getPipe()
	{
		return pipe;
	}
	
	public LinkedList<float[][]> getPositionAnimationList()
	{
		return positionAnimationQueue;
	}
	
	public LinkedList<Float> getAlphaAnimationList()
	{
		return alphaAnimationQueue;
	}
	
	public void resetPipeAnimation()
	{
		positionAnimationQueue.clear();
		alphaAnimationQueue.clear();
		lastFace = createNewFace(INITIAL_RADIUS, initialPlacement[0], initialPlacement[1], initialPlacement[2]);
		lastAlpha = 1;	
	}
	
}
