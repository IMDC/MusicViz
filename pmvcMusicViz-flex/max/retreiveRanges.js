globalRanges = new Global("ranges");
globalRanges.minArray = new Array();
globalRanges.maxArray = new Array();


var pat = this.patcher;
var myself = pat.getnamed("rtRange");
var collIndex = 0;

function bang()
{


 //this function gets called after all of the coll objects have been populated with raw note data

// iterate through colls to get min values
for( collIndex =1;collIndex<=16;collIndex++)
{

    var currentColl = pat.getnamed("channel"+collIndex);
   var preMinRange = pat.getnamed("preMin")

   pat.connect(currentColl,0,preMinRange,0);
    currentColl.message("min",2);
   pat.disconnect(currentColl,0,preMinRange,0);


}

// iterate through coll to get max values
for( collIndex =1;collIndex<=16;collIndex++)
{

    var currentColl = pat.getnamed("channel"+collIndex);
   var preMaxRange = pat.getnamed("preMax")

   pat.connect(currentColl,0,preMaxRange,0);
    currentColl.message("max",2);
   pat.disconnect(currentColl,0,preMaxRange,0);


}

outlet(0,"bang")



}

function minRange(val)
{
    globalRanges.minArray["min"+collIndex] = val;

    // post("min: "+ globalRanges.minArray["min"+collIndex] + "\n");
  
}

function maxRange(val)
{
       globalRanges.maxArray["max"+collIndex] = val;
      // post("max: "+ globalRanges.maxArray["max"+collIndex] + "\n");
    
}
