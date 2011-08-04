package Utilities;

import java.io.File;

/**
 * This class cannot be instantiated, its a static class with certain utilities for the programmer
 * 
 * @author Michael Pouris
 *
 */
public class Utils 
{
    public final static String mvp = "mvp";
    public final static String midi = "mid";

    /*
     * A Utility for retrieving the extension of a file.
     */
    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1)
        {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static boolean getIsPlaying(String toString)
    {
    	boolean isp = false;
    	int i = toString.lastIndexOf('?');
    	
    	if( i > 0 && i < toString.length() - 1)
    	{
    		isp = new Boolean ( toString.substring(i+1).toLowerCase() );
    	}

    	return isp;
    }
    
    public static String getFilePath(String s)
    {
    	String path = null;
    	int i = s.lastIndexOf('?');
    	
    	if( i > 0 && i < s.length() - 1)
    	{
    		path = s.substring(0, i);
    	}
    	else
    	{
    		return s;
    	}

    	return path;
    }
    
    /**
     * Will change microseconds to second but with rounding errors. For more precise conversions
     * were the fractions matter, please use the other converting method.
     * @param microSeconds
     * @return
     */
    public static int microSecondsToSeconds( long microSeconds )
    {
    	int seconds;
    	double temp;
    	
    	temp = (double) microSeconds / (double)1000000;
    	seconds = (int) temp;
    	
    	return seconds;
    }
    
    public static double microSecondsToSecondsPrecise( long microSeconds )
    {
    	//int seconds;
    	double temp;
    	
    	temp = (double) microSeconds / (double)1000000;
    	//seconds = (int) temp;
    	
    	return temp;
    }
    
    public static long secondsToMicroseconds( int seconds )
    {
    	long microSeconds;
    	
    	microSeconds = seconds * 1000000;

    	return microSeconds;
    }
    
    public static String secondsToTime( int seconds )
    {
	    //Math for current second time code.
	     int min = seconds/60;
	     int secondsOfAMin = seconds % 60;
	     String secondsOfAMinStr = "";
	     String timeSig;
	     secondsOfAMinStr += secondsOfAMin;
	     
	     //if the seconds are single digit, then a zero is added before hand, otherwise nothing is added
	     if(secondsOfAMinStr.length() != 2)
	     {
	    	 timeSig = min+":"+"0"+secondsOfAMin; 
	     }
	     else
	     {
	    	 timeSig = min+":"+secondsOfAMin;
	     }
	     return timeSig;
    }
    
}
