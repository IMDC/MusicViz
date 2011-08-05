package visualizer;

public class Rotation
{
	protected float[] rotatePoint(float x, float y, float z, float xA, float yA, float zA)
	{
		float newX = x;
		float newY = y;
		float newZ = z;
		if (xA != 0)
		{
			// If there is a X rotation angle, rotate around the X axis.
			float[] xRot = rotatePointAroundX(newX,newY,newZ,xA);
			newX = xRot[0];
			newY = xRot[1];
			newZ = xRot[2];
		}
	
		if (yA != 0)
		{
			// If there is a Y rotation angle, rotate around the Y axis.
			float[] yRot = rotatePointAroundY(newX,newY,newZ,yA);
			// Save the coordinates.
			newX = yRot[0];
			newY = yRot[1];
			newZ = yRot[2];
		}
		
		if (zA != 0)
		{
			// If there is a Z rotation angle, rotate around the Z axis.
			float[] zRot = rotatePointAroundZ(newX,newY,newZ,zA);
			// Save the coordinates.
			newX = zRot[0];
			newY = zRot[1];
			newZ = zRot[2];
		}

		float all[] = {newX,newY,newZ};
		return all;	}
	
	protected float[] rotatePointAroundX(float x, float y, float z, float a)
	{
		System.out.println("x");
		float newX = x, newY = y, newZ = z;
		float all[] = {x,y,z};		
		newX = x;
		newY = (float) (Math.cos(a)*y - Math.sin(a)*z);
		newZ = (float) (Math.sin(a)*y + Math.cos(a)*z);
		all[0] = newX;
		all[1] = newY;
		all[2] = newZ;
		return all;
	}
	protected float[] rotatePointAroundY(float x, float y, float z, float a)
	{
		System.out.println("y");
		float newX = x, newY = y, newZ = z;
		float all[] = {x,y,z};		
		newY = y;
		newZ = (float) (Math.cos(a)*z - Math.sin(a)*x);
		newX = (float) (Math.sin(a)*z + Math.cos(a)*x);
		all[0] = newX;
		all[1] = newY;
		all[2] = newZ;
		return all;
	}
	protected float[] rotatePointAroundZ(float x, float y, float z, float a)
	{
		System.out.println("z");
		float newX = x, newY = y, newZ = z;
		float all[] = {x,y,z};	
		newZ = z;
		newX = (float) (Math.cos(a)*x - Math.sin(a)*y);
		newY = (float) (Math.sin(a)*x + Math.cos(a)*y);
		all[0] = newX;
		all[1] = newY;
		all[2] = newZ;
		return all;
	}
}
