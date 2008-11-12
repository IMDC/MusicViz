package ca.ryerson.musicViz.model.vo
{
	public class TonalAnimateVO
	{
		private var _bladeIndex:int;
		private var _velocityRatio:Number;
		private var _eventDuration:Number;
		
		
		public function TonalAnimateVO(bladeIndex:int, veloctityRatio:Number, eventDuration:Number)
		{
			_bladeIndex = bladeIndex;
			_velocityRatio = veloctityRatio;
			_eventDuration = eventDuration;
			
		}
		
		public function get bladeIndex():int
		{
			return _bladeIndex as int;
		}
		
		public function get velocityRatio():Number
		{
			return _velocityRatio as Number;
		}
		
		public function get eventDuration():Number
		{
			return _eventDuration as Number;
		}

	}
}