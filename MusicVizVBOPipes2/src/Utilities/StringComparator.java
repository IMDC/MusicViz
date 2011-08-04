package Utilities;

import java.util.Comparator;

/**
 * This class is a comparator used for strings. It is used when the preprocessor collects the 
 * amount of note changes a channel goes through that is greater than a certain amount. More specifically,
 * each channel's frequency of movements are stored in an array and every position in the array
 * holds the amount of changes that position has. IE: Position 0 (array[0]) is channel 1 and the value at that
 * [0] is channel 1's movements. Now, for Hong's movement idea, the channels with the most amount of movements
 * should be in front, therefore we need to sort the array. Sorting brings up a problem of keeping the channels
 * paired up with their values, so we need to store the channel with its value. That is done with the use of 
 * a String array and each value has its channel attached to the very end with an underscore. It looks like this: 789_1
 * This makes sorting for the most amount of movements very simple, the compare(...) method sorts in descending order
 * and only takes into account the channel's change values, which is why we split the 2 strings by the underscore
 * and take ONLY the first part in the resultant array.
 * 
 * @author Michael Pouris
 *
 */
public class StringComparator implements Comparator<String>
{
	public int compare(String str1, String str2) {	
		int subStrInt1 = Integer.parseInt( str1.split("_")[0] );
		int subStrInt2 = Integer.parseInt( str2.split("_")[0] );

		if( subStrInt1 < subStrInt2 ){
			return 1;
		}
		else if( subStrInt1 == subStrInt2 ){
			return 0;
		}
		else {
			return -1;
		}
	}
}
