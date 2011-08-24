package visualizer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class Beat
{
	private static final int AMOUNT_BETWEEN_FACES = 4;
	private static final int SLICES = 15;
	
	private final GLU glu;
	private final GLUquadric quadric;
	private final int amountOfFaces;
	private final float[] initialPlacement;
	private final float[] initialColour;
	private final int maxLengthOfBeat;
	private int radius;
	private int innerRadius;
	private int position;
	
	/**
	 * Creates a new beat object. The object stores the initialColour, initialPlacement
	 * and amountOfFaces for its whole life; these variables are final and CANNOT be changed.
	 * 
	 * @param initialPlacement: Stores the X,Y and Z position of the pipe. It is good to note
	 * 		that the Z position is not used by the object because it is used to animate the pipe.
	 * @param initialColour: Stores the initial RGBA as floats (0 -> 1).
	 * @param amountOfFaces: This is specifically used with the pipes. Because the pipes 
	 * 		have a set distance between their faces, it must be kept track of in order to
	 * 		animate the beat objects in the same amount of time.
	 * @param radius: The radius of the beat.
	 */
	public Beat( float[] initialPlacement, float[] initialColour, int amountOfFaces, int radius, int innerRadius )
	{
		this.glu = new GLU();
		this.radius = radius;
		this.innerRadius = innerRadius;
		this.quadric = this.glu.gluNewQuadric();
		this.amountOfFaces = amountOfFaces;
		this.initialPlacement = initialPlacement.clone();
		this.initialColour = initialColour.clone();
		this.maxLengthOfBeat = AMOUNT_BETWEEN_FACES * this.amountOfFaces;
		this.position = maxLengthOfBeat;
		this.glu.gluQuadricNormals(this.quadric,GLU.GLU_SMOOTH);
	}
	
	/**
	 * Draws the beat on the screen. When the object is
	 * initially created, the beat is not animating. When
	 * a boolean value of true is sent, the beat is animated
	 * from the 0th position to the nth position. If a draw = true
	 * is passed, then the beat is forced back to the beginning to
	 * animate. This creates a snapping image like a real drum.
	 *  
	 * @param drawable: The openGL context
	 * @param draw: Is a boolean that alerts the beat that a new
	 * 		beat signal was sent to the object, which in turn
	 * 		animates the beat.
	 */
	public void draw(GLAutoDrawable drawable, boolean draw)
	{
		GL2 gl = drawable.getGL().getGL2();
		//Start animating from the beginning (0th position)
		if( draw )
		{
			position = 0;
		}
		//The beat is finished animating because it is at the end
		//therefore do not draw the beat. Do this by returning.
		if( position >= maxLengthOfBeat )
		{
			return;
		}
		
		//Animate the beat
		gl.glPushMatrix();
			animate(drawable);
		gl.glPopMatrix();
		
		//Move the beat back. This is what achieves the animation effect.
		position = position + AMOUNT_BETWEEN_FACES;
	}
	
	/**
	 * Sets the colour, position and draws the physical beat.
	 * @param drawable
	 */
	private void animate(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		float currentAlpha = 1f - (position / (float) maxLengthOfBeat);

		//float emissiveLight0[] = {0.1f, 0.1f, 0.1f, 1.0f};
		float emissiveLight1[] = {1f, 0f, 0f, 1.0f};
		if( position <= 25 )
		{
			emissiveLight1[0] = 0.75f*initialColour[0] - (position / (float) 25);
			emissiveLight1[1] = 0.75f*initialColour[1] - (position / (float) 25);
			emissiveLight1[2] = 0.75f*initialColour[2] - (position / (float) 25);
			gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emissiveLight1, 0);
		}
		
		//Position and draw it
		gl.glTranslatef(initialPlacement[0], initialPlacement[1], position);
		gl.glColor4f(initialColour[0],initialColour[1],initialColour[2], currentAlpha);
		glu.gluDisk(quadric, radius, innerRadius, SLICES, 1);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emissiveLight0, 0);
	}
}
