package visualizer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class BeatPart
{
	private GL gl;
	private GLU glu;
	private float[] colourAndAlpha;
	private float[] coordinates;
	private float size;
	public boolean draw = false;
	
	public void setOpenGLComponents( GL gl, GLU glu )
	{ 
		this.gl = gl;
		this.glu = glu;
	}
	
	public float getSize()
	{
		return size;
	}
	
	public void setSize( float size )
	{
		this.size = size;
	}
	
	public float[] getColourAndAlpha()
	{
		return colourAndAlpha;
	}
	
	public void setColourAndAlpha( float[] colourAndAlpha )
	{
		this.colourAndAlpha = colourAndAlpha;
	}
	
	public void setAlpha( float alpha )
	{
		colourAndAlpha[3] = alpha;
	}
	
	public float[] getCoordinates()
	{
		return coordinates;
	}
	
	public void setCoordinates( float[] coordinates )
	{
		this.coordinates = coordinates;
	}
	
	public void draw()
	{
		if( draw )
		{
			GLUquadric qobj = glu.gluNewQuadric();
		
			gl.glColor4f(colourAndAlpha[0],colourAndAlpha[1],colourAndAlpha[2],colourAndAlpha[3]);
			gl.glPushMatrix();
			gl.glTranslatef(coordinates[0], coordinates[1], coordinates[2]);
				glu.gluDisk(qobj, size-7, size, 32, 1);
			gl.glPopMatrix();
			draw = false;
			//glu.gluDeleteQuadric(qobj);
		}
	}
}
