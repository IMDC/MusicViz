package processors;

/**
 * This class is a static class that calculates what instrument each channel represents 
 * for that given song based on the program number.
 * 
 * @author Michael Pouris
 *
 */
public class ChannelProcessor 
{
	/**
	 * Calculates what each channel represents based on the program number.
	 * 
	 * @param programNumber
	 * @return
	 */
	public static String getChannelName( int programNumber )
	{
		String name = null;
		
		if( programNumber >= 0 && programNumber <= 7 )
		{
			name = "piano";
		}
		else if( programNumber >= 8 && programNumber <= 15 || programNumber == 108 || programNumber == 112 )
		{
			name = "chrperc";
		}
		else if( programNumber >= 16 && programNumber <= 23  )
		{
			name = "organ";
		}
		else if( programNumber >= 24 && programNumber <= 31  )
		{
			name = "guitar";
		}
		else if( programNumber >= 32 && programNumber <= 39  )
		{
			name = "bass";
		}
		else if( programNumber >= 40 && programNumber <= 51  || programNumber == 55|| programNumber == 110)
		{
			name = "string";
		}		
		else if( programNumber >= 52 && programNumber <= 54  )
		{
			name = "voice";
		}	
		else if( programNumber >= 56 && programNumber <= 63  )
		{
			name = "brass";
		}	
		else if( programNumber >= 64 && programNumber <= 71  )
		{
			name = "reed";
		}	
		else if( programNumber >= 72 && programNumber <= 79 || programNumber == 109 )
		{
			name = "wind";
		}	
		else if( programNumber >= 80 && programNumber <= 95 )
		{
			name = "synth";
		}	
		else if( programNumber >= 104 && programNumber <= 111 || programNumber == 46) 
		{
			name = "plkstr";
		}
		else if( programNumber >= 113 && programNumber <= 118 || programNumber == 47) 
		{
			name = "tnlprc";
		}
		else if( programNumber >= 96 && programNumber <= 103 || programNumber >= 119 && programNumber <=127) 
		{
			name = "sfx";
		}
		
		if( name != null )
		{
			return name;
		}
		return null;
	}
	
	public static void setMinMax( int programNumber, int channel, int[][] minMax )
	{
		if( programNumber >= 0 && programNumber <= 7 )
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 4;
		}
		else if( programNumber >= 8 && programNumber <= 15 || programNumber == 108 || programNumber == 112 )
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 4;
		}
		else if( programNumber >= 16 && programNumber <= 23  )
		{
			minMax[channel][0] = 4;
			minMax[channel][1] = 7;
		}
		else if( programNumber >= 24 && programNumber <= 31  )
		{
			minMax[channel][0] = 2;
			minMax[channel][1] = 7;
		}
		else if( programNumber >= 32 && programNumber <= 39  )
		{
			minMax[channel][0] = 5;
			minMax[channel][1] = 7;
		}
		else if( programNumber >= 40 && programNumber <= 51  || programNumber == 55|| programNumber == 110)
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 4;
		}		
		else if( programNumber >= 52 && programNumber <= 54  )
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 4;
		}	
		else if( programNumber >= 56 && programNumber <= 63  )
		{
			minMax[channel][0] = 2;
			minMax[channel][1] = 4;
		}	
		else if( programNumber >= 64 && programNumber <= 71  )
		{
			minMax[channel][0] = 2;
			minMax[channel][1] = 4;
		}	
		else if( programNumber >= 72 && programNumber <= 79 || programNumber == 109 )
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 4;
		}	
		else if( programNumber >= 80 && programNumber <= 95 )
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 5;
		}	
		else if( programNumber >= 104 && programNumber <= 111 || programNumber == 46) 
		{
			minMax[channel][0] = 2;
			minMax[channel][1] = 4;
		}
		else if( programNumber >= 113 && programNumber <= 118 || programNumber == 47) 
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 4;
		}
		else if( programNumber >= 96 && programNumber <= 103 || programNumber >= 119 && programNumber <=127) 
		{
			minMax[channel][0] = 2;
			minMax[channel][1] = 4;
		}
		else
		{
			minMax[channel][0] = 1;
			minMax[channel][1] = 3;
		}
	}
}
