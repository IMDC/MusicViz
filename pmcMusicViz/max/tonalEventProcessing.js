

outlets=1;
var minNote;
var maxNote;
var totalNoteRange;
var bucketArray = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8,0.9,1.0];
var noteOccArray = new Array(10);
var minMaxIndex;


function list(a)
{
    
    //-init
   var nn = arguments[0];
   var velocity = arguments[1];
   var  channel = arguments[2];
   var relativeNote;
   var relRatio;
   var bucketIndex;
   var prevNoteOcc;
   var newNoteOcc;

  //A relative note processing
   relativeNote = nn - minNote
   relRatio =  relativeNote/ totalNoteRange 

  //A1 find which bucket to route note number to
  for(i=0;i<bucketArray.length;i++)
  {
      if (relRatio <=bucketArray[i])
     {
        bucketIndex = i
        break;
     }

  }
        
  //A2 manage note activity for buckets 
     prevNoteOcc = noteOccArray[bucketIndex]
    
    if ( velocity==0)
   {
       newNoteOcc = prevNoteOcc - 1;
    }
   else
   {
      newNoteOcc = prevNoteOcc + 1;
   }

   noteOccArray[bucketIndex] = newNoteOcc

    //B velocity to intensity

       if (velocity> 100)
     { intensity = 5;}

       else if (velocity>75 && 100>= velocity)
      {   intensity=4; }


    else if ( velocity >50 && 75>=velocity)
    {   intensity=3; }


    else if ( velocity > 25 && 50>=velocity)
    { intensity = 2; }

    else if ( velocity > 0  && 25>= velocity)
    {  intensity = 1;}

   else if ( velocity == 0 )
    {  intensity = 0;}


   //C ouput
  // had a few notes that fell out of the range that came up as  undefined
  // don't have the means on the other side to deal with doubling up of bucket
  //events so it is best to filter them out here

if(newNoteOcc<2)
  {
       //post( "fan"+channel +"noteNum:" + nn + " bucket: "+ bucketIndex + " noteOcc: " + newNoteOcc +" int: " + intensity + "\n")
        outlet(0, "tonalEvent "+ "fan"+channel+" "+"blade"+bucketIndex +" "+"int"+intensity )

  }

} 

function init()
{
    minMaxIndex = jsarguments[1];
   minNote = globalRanges.minArray["min"+minMaxIndex];
   maxNote = globalRanges.maxArray["max"+minMaxIndex];
   totalNoteRange = maxNote - minNote;

   //initialize bucket occurance array
  for(i=0;i<noteOccArray.length;i++)
 {
       noteOccArray[i] = 0

 }

   //post("min:" +minNote + " max:" + maxNote+ " total:"+ totalNoteRange+ "\n")
}
