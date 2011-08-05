package visualizer;

import java.util.LinkedList;

public class Settings
{
	private float limits[] = {0.0f,0.0f,0.0f};

	//protected Transition Transitions = new Transition();
	
	// Holds the up coming values for coordinates, rotation angles, dimensions, and colours, calculated
	// by the tweening functions. Items are removed as they're added to the drawing lists.
	private LinkedList<float[]> coordsTween = new LinkedList<float[]>();
	//private LinkedList<float[]> rotationTween = new LinkedList<float[]>();
	private LinkedList<float[]> dimensionsTween = new LinkedList<float[]>();
	private LinkedList<float[]> colourTween = new LinkedList<float[]>();
	
	float[] recentCoords;
	//float[] recentRotation;
	float[] recentDimensions;
	float[] recentColour;
	float[] prevCoords;
	//float[] prevRotation;
	float[] prevDimensions;
	float[] prevColour;
	
	public void resetTweens()
	{
		coordsTween = new LinkedList<float[]>();
		//rotationTween = new LinkedList<float[]>();
		dimensionsTween = new LinkedList<float[]>();
		colourTween = new LinkedList<float[]>();
	}
	
	/*public void add(float coords[], float rotation[], float dimensions[], float colour[])
	{
		prevCoords = recentCoords;
		//prevRotation = recentRotation;
		prevDimensions = recentDimensions;
		prevColour = recentColour;
		
		recentCoords = coords;
		recentRotation = rotation;
		recentDimensions = dimensions;
		recentColour = colour;
	}*/
	
	public void add(float coords[],float dimensions[], float colour[])
	{
		prevCoords = recentCoords;
		prevDimensions = recentDimensions;
		prevColour = recentColour;
		
		recentCoords = coords;
		recentDimensions = dimensions;
		recentColour = colour;
	}
	
	
	public void tweenCoords(int frames,float[] vals)
	{
		float[] startVal;
		
		//If there are already tweens progress, then start value = end of the tween list
		// If there are no tweens in progress, the start value is the current position.
		if (!coordsTween.isEmpty())
		{
			startVal = coordsTween.getLast();
		}
		else
		{
			startVal = recentCoords;
		}
		for(int i=0;i<frames;++i)
		{
			float tempX = Transition.easeInOutQuad(i,startVal[0],vals[0]-startVal[0],frames);
			float tempY = Transition.easeInOutQuad(i,startVal[1],vals[1]-startVal[1],frames);
			float tempZ = Transition.easeInOutQuad(i,startVal[2],vals[2]-startVal[2],frames);
			float[] retCoords = {tempX,tempY,tempZ};
			
			coordsTween.addLast(retCoords);
		}
	}
	
	/*public void tweenRotation(int frames,float[] vals)
	{
		float[] startVal;
		
		//If there are already tweens progress, then start value = end of the tween list
		// If there are no tweens in progress, the start value is the current position.
		if (!rotationTween.isEmpty())
		{
			startVal = rotationTween.getLast();
		}
		else
		{
			startVal = recentRotation;
		}
		for(int i=0;i<frames;++i)
		{
			float tempX = Transition.easeInOutQuad(i,startVal[0],vals[0]-startVal[0],frames);
			float tempY = Transition.easeInOutQuad(i,startVal[1],vals[1]-startVal[1],frames);
			float tempZ = Transition.easeInOutQuad(i,startVal[2],vals[2]-startVal[2],frames);
			float[] retRotation = {tempX,tempY,tempZ};
			
			rotationTween.addLast(retRotation);
		}
	}*/
	
	public void tweenDimensions(int frames,float[] vals)
	{
		float[] startVal;
		//If there's already tweens in progress, the start value will be at the end of the tweens
		//otherwise the start value is the current position.
		if (!dimensionsTween.isEmpty())
		{
			startVal = dimensionsTween.getLast();
		}
		else
		{
			startVal = recentDimensions;
		}
		for(int i=0;i<frames;++i)
		{
			float tempW = Transition.easeInOutQuad(i,startVal[0],vals[0]-startVal[0],frames);
			float tempH = Transition.easeInOutQuad(i,startVal[1],vals[1]-startVal[1],frames);
			float[] retDimensions = {tempW,tempH};
			
			dimensionsTween.addLast(retDimensions);
		}
	}
	
	public void tweenColours(int frames,float[] vals)
	{
		float[] startVal;
		if (!colourTween.isEmpty())
		{
			startVal = colourTween.getLast();
		}
		else
		{
			startVal = recentColour;
		}
		for(int i=0;i<frames;++i)
		{
			float tempR = Transition.easeInOutQuad(i,startVal[0],vals[0]-startVal[0],frames);
			float tempG = Transition.easeInOutQuad(i,startVal[1],vals[1]-startVal[1],frames);
			float tempB = Transition.easeInOutQuad(i,startVal[2],vals[2]-startVal[2],frames);
			float tempA = Transition.easeInOutQuad(i,startVal[3],vals[3]-startVal[3],frames);
			float[] retColour = {tempR,tempG,tempB,tempA};

			colourTween.addLast(retColour);
		}
	}

	
	public boolean coordsTweening()
	{
		return !coordsTween.isEmpty();
	}
	
	/*public boolean rotationTweening()
	{
		return !rotationTween.isEmpty();
	}*/

	public boolean dimensionsTweening()
	{
		return !dimensionsTween.isEmpty();
	}
	
	public boolean colourTweening()
	{
		return !colourTween.isEmpty();
	}
	
	public void playTweens()
	{
		if (coordsTweening())
		{
			// If there's a coordinate tween in progress, add the next value to the drawing list,
			// remove the old value from the tween list, and make sure the drawing list remains
			// the correct size.
			prevCoords = recentCoords;
			recentCoords = coordsTween.getFirst();
			coordsTween.removeFirst();

		}
		/*if (rotationTweening())
		{
			// If there's a rotation tween in progress, add the next value to the drawing list,
			// remove the old value from the tween list, and make sure the drawing list remains
			// the correct size.
			prevRotation = recentRotation;
			recentRotation = rotationTween.getFirst();
			rotationTween.removeFirst();
		}*/
		if (dimensionsTweening())
		{
			// If there's a dimension tween in progress, add the next value to the drawing list,
			// remove the old value from the tween list, and make sure the drawing list remains
			// the correct size.
			prevDimensions = recentDimensions;
			recentDimensions = dimensionsTween.getFirst();
			dimensionsTween.removeFirst();
		}
		if (colourTweening())
		{
			// If there's a colour tween in progress, add the next value to the drawing list,
			// remove the old value from the tween list, and make sure the drawing list remains
			// the correct size.
			prevColour = recentColour;
			recentColour = colourTween.getFirst();
			colourTween.removeFirst();
		}
	}
	
	//used to restrict movement
	public void setLimit( float limits[] )
	{
		this.limits = limits;
	}
	
	public float[] getLimit()
	{
		return limits;
	}
}