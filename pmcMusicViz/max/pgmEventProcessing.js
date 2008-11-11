
function list(a)
{

    //A init
   var instFamily = arguments[0];
   var channel = arguments[1];


 //C output
 // finler out percussion channl
    if ( channel !=9)
    {
         outlet(0, "pgmEvent " +"fan"+channel+ " " + instFamily)
    }
}
