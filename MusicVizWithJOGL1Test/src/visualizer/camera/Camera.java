package visualizer.camera;
import javax.media.opengl.glu.GLU;

public class Camera
{
	private float sphereR, sphereY;
	private float camX,camY,camZ;
	private float radius;
	private float lowerLimit, upperLimit;
	private GLU glu;
	private float lookAtX, lookAtY, lookAtZ;
	
	public Camera(float lowerLimit, float upperLimit, float radius, float lookAtX, float lookAtY, float lookAtZ )
	{
		this.glu = new GLU();
		this.camY = 10;
		this.camX = 0;
		this.camZ = 0;
		this.radius = radius;
		this.sphereR = 0;
		this.sphereY = 0;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.lookAtX = lookAtX;
		this.lookAtY = lookAtY;
		this.lookAtZ = lookAtZ;
	}
	
	public void update()
	{
		calculateCameraPosition();
		glu.gluLookAt(camX, camY, camZ, lookAtX, lookAtY, lookAtZ, 0, 1, 0);
	}
	
	public void positionCamera(float r, float y)
	{
		sphereR += r;
		sphereY += y;
		if(sphereR <= -360 ){ sphereR = 0;}
		if(sphereR >= 360 ){ sphereR = 0;}
		if(sphereY < lowerLimit ){ sphereY = lowerLimit;}
		if(sphereY > upperLimit ){ sphereY = upperLimit;}
	}
	
	public void moveCameraForward()
	{
		float radians;
		float opp, adj;
    	radians = (float) (3.1459* (sphereR)/180.0);
    	opp = (float) Math.sin(radians) * 10f;
    	adj = (float) Math.cos(radians) * 10f;
    	lookAtZ -=opp;
    	lookAtX -=adj;
	}
	
	public void moveCameraBackward()
	{
		float radians;
		float opp, adj;
    	radians = (float) (3.1459* (sphereR)/180.0);
    	opp = (float) Math.sin(radians) *10f;
    	adj = (float) Math.cos(radians) *10f;
    	lookAtZ +=opp;
    	lookAtX +=adj;
	}
	
	private void calculateCameraPosition()
	{
		camY = (float) (radius * Math.sin((Math.PI*sphereY/180.0))) + lookAtY;
		camX = (float) (radius * Math.cos((Math.PI*sphereY/180.0)) * Math.cos(Math.PI*sphereR/180.0)) + lookAtX;
		camZ = (float) (radius * Math.cos((Math.PI*sphereY/180.0)) * Math.sin(Math.PI*sphereR/180.0)) + + lookAtZ;
	}
	
	public void setZoom( float zoom )
	{
		radius += zoom;
	}
	
	public void setLookAtX( float x )
	{
		lookAtX = x;
	}
	
	public void setLookAtY( float y )
	{
		lookAtY = y;
	}
	
	public void setLookAtZ( float z )
	{
		lookAtZ = z;
	}
	
	public float getX()
	{
		return camX;
	}
	
	public float getY()
	{
		return camY;
	}
	
	public float getZ()
	{
		return camZ;
	}
}
