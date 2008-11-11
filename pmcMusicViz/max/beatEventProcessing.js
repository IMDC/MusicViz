

function list(a)
{

   // init
  var percInst = arguments[0];
   var velocity = arguments[1];
   



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


     //C ouput
outlet(0, "beatEvent"+" "+ percInst +" "+ "int"+intensity );

}