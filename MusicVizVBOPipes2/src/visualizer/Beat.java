package visualizer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

public class Beat
{
	private GLU glu;
	private float[] initialPlacement;
	private float[] initialColour;
	
	public Beat( float[] initialPlacement, float[] initialColour )
	{
		this.glu = new GLU();
		this.initialPlacement = initialPlacement.clone();
		this.initialColour = initialColour.clone();
	}
	
	public void draw(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		animate();
		
		gl.glPushMatrix();
		gl.glPopMatrix();
	}
	
	private void animate()
	{
		
	}
}
