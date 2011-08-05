package player;

public class MidiNoteConverter 
{
	/**
	 * Takes a midi note as input, the midi note is between 0-127 and converts it to the real note and
	 * octave.
	 * 
	 * From: http://stackoverflow.com/questions/712679/convert-midi-note-numbers-to-name-and-octave
	 * 
	 * @param midiNote which ranges between 0-127 inclusive.
	 * @return An Object array of size 2. In the first position, is the real note such as C, C#, etc and
	 * in the second position is the octave.
	 */
	public static Object[] midiNotesToRealNotes( int midiNote )
	{
		 String notes = "C C#D D#E F F#G G#A A#B ";
		 int octave;
		 String realNote;
		 
		 octave = midiNote/12-1;
		 realNote = notes.substring( (midiNote%12) *2,  (midiNote%12) *2 + 2 );
		 
		 Object[] a = { realNote, octave };
		 return a;
	}
}
