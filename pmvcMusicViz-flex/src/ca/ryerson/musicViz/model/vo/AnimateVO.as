package ca.ryerson.musicViz.model.vo
{
	
	public class AnimateVO
	{
		private var _movieClip:String;
		private var _frame:String;
		
		public function AnimateVO( movieClip:String, frame:String)
		{
			this._movieClip = movieClip;
			this._frame = frame;
		}

		public function toString():String
		{
			return "m: " + _movieClip + " f: " + _frame + "\n"
		}
		
		public function get clip():String
		{
			return _movieClip as String
		
		}
		
		public function get frame():String
		{
			return _frame as String
		}
		
		
	}
}