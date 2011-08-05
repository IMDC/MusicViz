package visualizer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import controller.Controller;

public class Part
{
	private GL gl;
	private Settings settings;
	//private Rotation rotation;
	public int channel, pipe;
	
	/*
	 * These variables are specifically used for drawing a part
	 * of the pipe. The reason for these not to be inside the draw()
	 * method is because of performance. OpenGL optimisations
	 * at code level suggests that little variable creation is done
	 * when objects are drawn. Therefore making these variables global
	 * makes sure the garbage collector will not touch them and consequently
	 * slow down OpenGL
	 */
	private static final float SCALE_Z = 1.0f;
	private static final int SLICES = 5;
	//Starting coordinates of the the section of the pipe
    private float[] theseCoords;
    //private float[] thisRotation;
    private float[] theseDimensions;
    private float[] thisColour;
    //Ending coordinates of the section of the pope
    private float[] nextCoords;
   // private float[] nextRotation;
    private float[] nextDimensions;
    private float[] nextColour;
	private float x,y,z;
	//private float rot[];
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public Part(Settings settings, String name, int channel, int pipe, Controller controller )
	{
		this.settings = settings;
		//this.rotation = new Rotation();
		this.pipe = pipe;
		this.channel = channel;
	}
	
	public void setOpenGLComponents(GL gl, GLU glu)
	{
		this.gl = gl;
	}
	
	protected void draw()
	{		
	    //Ending coordinates of the section of the pope
		nextDimensions = settings.recentDimensions;
	    	
		//There is no use rendering something if it is not supposed to be seen anyway.
		//This prevents wasting CPU AND GPU on rendering something that is not there.
	    if(nextDimensions[0] < 1 || nextDimensions[1] < 1)
	    {
	    	return;
	    }		
	    
	    nextCoords = settings.recentCoords;
	    //nextRotation = settings.recentRotation;
	    nextColour = settings.recentColour;
	    
		//Starting coordinates of the the section of the pipe
	    theseCoords = settings.prevCoords;
	    //thisRotation = settings.prevRotation;
	    theseDimensions = settings.prevDimensions;
	    thisColour = settings.prevColour;
	    	    
		gl.glTranslatef(0.0f, 0.0f,SCALE_Z);
		gl.glPushMatrix();
	    
	    // Draw the main part of the current section as a triangle strip.
        gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (int i=0; i<=SLICES; ++i) {
                x = (float)(theseDimensions[0] * Math.sin(i*2*Math.PI/SLICES));
                y = (float)(theseDimensions[1] * Math.cos(i*2*Math.PI/SLICES));
                z = -SCALE_Z/2;

                gl.glNormal3f(x, y, 0.0f); // This normal is probably incorrect after rotation.
                gl.glColor4f(thisColour[0]/255,thisColour[1]/255,thisColour[2]/255,thisColour[3]);

                gl.glVertex3f(x+theseCoords[0], y+theseCoords[1], z);
                
                x = (float)(nextDimensions[0] * Math.sin(i*2*Math.PI/SLICES));
                y = (float)(nextDimensions[1] * Math.cos(i*2*Math.PI/SLICES));
                z = SCALE_Z/2;
  
 
                gl.glColor4f(nextColour[0]/255,nextColour[1]/255,nextColour[2]/255,nextColour[3]);

                gl.glVertex3f(x+nextCoords[0], y+nextCoords[1], z);
            }
        gl.glEnd();
	    
	    // Pop back to the previous matrix.
	    gl.glPopMatrix();
	}
}
