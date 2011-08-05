package visualizer;

public class Transition
{
	/**
	 * This is used to perform quadratic animation for curves of the pipes.
	 * 
	 * @param currentFrame: The current frame that is being animated
	 * @param starVal: The value that we are animating FROM
	 * @param changeVal: The value that we are animating TO
	 * @param totalFrames: The amount of frame that the animation takes place over
	 */
	public static float easeInOutQuad(float currentFrame, float startVal, float changeVal, float totalFrames)
	{
		float returnVal = 0;
		if (currentFrame < totalFrames/2) {
			returnVal = 2*changeVal*currentFrame*currentFrame/(totalFrames*totalFrames) + startVal;
		} else {
			float ts = currentFrame - totalFrames/2;
			returnVal = -2*changeVal*ts*ts/(totalFrames*totalFrames) + 2*changeVal*ts/totalFrames + changeVal/2 + startVal;
		}
		return returnVal;
	}
	
	// Linear tween.
	public static float linear(float currentFrame, float startVal, float changeVal, float totalFrames)
	{
		float returnVal = currentFrame*(changeVal/totalFrames) + startVal;
		return returnVal;
	}
	
	// Quadratic ease in tween.
	public static float easeInQuad(float currentFrame, float startVal, float changeVal, float totalFrames)
	{
		float returnVal = changeVal*currentFrame*currentFrame/(totalFrames*totalFrames) + startVal;
		return returnVal;
	}
	
	// Quadratic ease out tween.
	public static float easeOutQuad(float currentFrame, float startVal, float changeVal, float totalFrames)
	{
		float returnVal = -changeVal*currentFrame*currentFrame/(totalFrames*totalFrames) + 2*changeVal*currentFrame/totalFrames + startVal;
		return returnVal;
	}
	
	// Sinusoidal ease in tween.
	public static float easeInSine(float currentFrame, float startVal, float changeVal, float totalFrames)
	{	
		float returnVal = (float)(-changeVal*Math.cos(currentFrame/totalFrames*Math.PI/2)+changeVal+startVal);
		return returnVal;
	}
	
	// Sinusoidal ease out tween.
	public static float easeOutSine(float currentFrame, float startVal, float changeVal, float totalFrames)
	{	
		float returnVal = (float)(changeVal*Math.sin(currentFrame/totalFrames*Math.PI/2)+startVal);
		return returnVal;
	}
	
	// Sinusoidal ease in and out tween.
	public static float easeInOutSine(float currentFrame, float startVal, float changeVal, float totalFrames)
	{	
		float returnVal = (float)(-changeVal/2*(Math.cos(Math.PI*currentFrame/totalFrames)-1)+startVal);
		return returnVal;
	}
	
	// Exponential ease in tween.
	public static float easeInExpo(float currentFrame, float startVal, float changeVal, float totalFrames)
	{	
		int flip = 1;
		if (changeVal < 0)
		{
			flip *= -1;
			changeVal *= -1;
		}
		float returnVal = (float)(flip*(Math.exp(Math.log(changeVal)/totalFrames*currentFrame))+startVal);
		return returnVal;
	}
	
	// Exponential ease out tween.
	public static float easeOutExpo(float currentFrame, float startVal, float changeVal, float totalFrames)
	{
		int flip = 1;
		if (changeVal < 0)
		{
			flip *= -1;
			changeVal *= -1;
		}
		float returnVal = (float)(flip*(-Math.exp(-Math.log(changeVal)/totalFrames*(currentFrame-totalFrames))+changeVal+1)+startVal);
		return returnVal;
	}
	
	// Exponential ease in and out tween.
	public static float easeInOutExpo(float currentFrame, float startVal, float changeVal, float totalFrames)
	{	
		float returnVal = 0;
		int flip = 1;
		if (changeVal < 0) {
			flip *= -1;
			changeVal *= -1;
		}
		if (currentFrame < totalFrames/2)
		{
			returnVal = (float)(flip*(Math.exp(Math.log(changeVal/2)/(totalFrames/2)*currentFrame))+startVal);
		} else {
			returnVal =  (float)(flip*(-Math.exp(-2*Math.log(changeVal/2)/totalFrames*(currentFrame-totalFrames))+changeVal+1)+startVal);
		}
		return returnVal;
	}
}