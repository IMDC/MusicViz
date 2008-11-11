outlets=2

function list(a)
{
    // 3rd message from midi message is its channel,
   // we want to use this to route note data for tonal or percussion processing

  // init
   var nn =  arguments[0];
   var velocity = arguments[1];
   var channel = arguments[2]

   if (channel ==9) //percussion
   {
        outlet(0, nn,velocity,channel)

    }
   
    else // tonal
    {
        outlet(1, nn,velocity, channel)
    }


}
