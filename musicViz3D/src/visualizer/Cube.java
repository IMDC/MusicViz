package visualizer;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
 * A simple class to draw a cube that can be textured and lighted.
 * <p>
 * This is used for testing and trouble shooting purposes.
 * 
 * @author Michael Pouris
 *
 */
public class Cube
{
	public static void draw( GLAutoDrawable gLDrawable, float size )
	{
		GL2 gl = gLDrawable.getGL().getGL2();
		size = size/2;
		gl.glBegin(GL2.GL_QUADS);
				// Front Face
				gl.glNormal3d(0,0,1);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-size, -size,  size);	// Bottom Left Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( size, -size,size);	// Bottom Right Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( size,  size,  size);	// Top Right Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-size,  size,  size);	// Top Left Of The Texture and Quad
				// Back Face
				gl.glNormal3d(0,0,-1);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-size, -size, -size);	// Bottom Right Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-size,  size, -size);	// Top Right Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( size,  size, -size);	// Top Left Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( size, -size, -size);	// Bottom Left Of The Texture and Quad
				// Top Face
				gl.glNormal3d(0,1,0);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-size,  size, -size);	// Top Left Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-size,  size,  size);	// Bottom Left Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( size,  size,  size);	// Bottom Right Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( size,  size, -size);	// Top Right Of The Texture and Quad
				// Bottom Face
				gl.glNormal3d(0,-1,0);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-size, -size, -size);	// Top Right Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( size, -size, -size);	// Top Left Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( size, -size,  size);	// Bottom Left Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-size, -size,  size);	// Bottom Right Of The Texture and Quad
				// Right face
				gl.glNormal3d(1,0,0);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( size, -size, -size);	// Bottom Right Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( size,  size, -size);	// Top Right Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( size,  size,  size);	// Top Left Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( size, -size,  size);	// Bottom Left Of The Texture and Quad
				// Left Face
				gl.glNormal3d(-1,0,0);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-size, -size, -size);	// Bottom Left Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-size, -size,  size);	// Bottom Right Of The Texture and Quad
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-size,  size,  size);	// Top Right Of The Texture and Quad
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-size,  size, -size);	// Top Left Of The Texture and Quad
				gl.glEnd();
	}
}
