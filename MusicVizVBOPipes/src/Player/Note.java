package Player;

import javax.sound.midi.MidiEvent;

public class Note implements Comparable<Note>
{
	private int channel = -100;
	private long tickDuration = -100;
	private MidiEvent firstEvent = null;
	private MidiEvent endEvent = null;
	private double startTimeInSeconds = - 100;
	private double endTimeInSeconds = - 100;
	private double durationInSeconds = -100;
	
	public int compareTo(Note other) 
	{
		long thisTick = firstEvent.getTick();
		long otherTick = other.firstEvent.getTick();
		
		if( thisTick < otherTick )
		{
			 return -1;
		}
		else if( thisTick == otherTick )
		{
			return 0;
		}
		else 
		{
			return 1;
		}
	}
	
	public MidiEvent getFirstEvent()
	{
		return firstEvent;
	}
	
	public void setFirstEvent( MidiEvent firstEvent )
	{
		this.firstEvent = firstEvent;
	}
	
	public MidiEvent getEndEvent()
	{
		return endEvent;
	}
	
	public void setEndEvent( MidiEvent endEvent )
	{
		this.endEvent = endEvent;
	}
	
	public int getChannel()
	{
		return channel;
	}
	
	public void setChannel( int channel )
	{
		this.channel = channel;
	}
	
	public long getTickDuration()
	{
		return tickDuration;
	}
	
	public void setTickDuration( long tickDuration )
	{
		this.tickDuration = tickDuration;
	}
	
	public void setStartTimeInSeconds(double startTimeInSeconds)
	{
		this.startTimeInSeconds = startTimeInSeconds;
	}
	
	public void setEndTimeInSeconds( double endTimeInSeconds )
	{
		this.endTimeInSeconds = endTimeInSeconds;
		durationInSeconds = endTimeInSeconds - startTimeInSeconds;
	}
	
	public double getEndTimeInSeconds()
	{
		return endTimeInSeconds;
	}
	
	public double getStartTimeInSeconds()
	{
		return startTimeInSeconds;
	}
	
	public double getDurationInSeconds()
	{
		return durationInSeconds;
	}
}
