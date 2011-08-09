package visualizer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class Beat
{
	private static final int AMOUNT_BETWEEN_FACES = 4;
	
	private final GLU glu;
	private final GLUquadric quadric;
	private final int amountOfFaces;
	private final float[] initialPlacement;
	private final float[] initialColour;
	private final int maxLengthOfBeat;
	private int radius;
	private int position;
	
	public Beat( float[] initialPlacement, float[] initialColour, int amountOfFaces, int radius )
	{
		this.glu = new GLU();
		this.radius = radius;
		this.quadric = this.glu.gluNewQuadric();
		this.amountOfFaces = amountOfFaces;
		this.initialPlacement = initialPlacement.clone();
		this.initialColour = initialColour.clone();
		this.maxLengthOfBeat = AMOUNT_BETWEEN_FACES * this.amountOfFaces;
		this.position = maxLengthOfBeat;
	}
	
	public void draw(GLAutoDrawable drawable, boolean draw)
	{
		GL2 gl = drawable.getGL().getGL2();
		if( draw )
		{
			position = 0;
		}
		if( position >= maxLengthOfBeat )
		{
			return;
		}
		
		gl.glPushMatrix();
			animate(drawable);
		gl.glPopMatrix();
		
		position = position + AMOUNT_BETWEEN_FACES;
	}
	
	private void animate(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glTranslatef(initialPlacement[0], initialPlacement[1], position);
		gl.glColor4fv(initialColour, 0);
		glu.gluDisk(quadric, radius, radius - 7, 32, 1);
	}
}
