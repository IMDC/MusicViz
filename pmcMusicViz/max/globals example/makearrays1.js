/*
creates 2 different arrays from an incoming list. one is recalled by name, the other by symbol
*/
myglobal = new Global("data");
myglobal.array1 = new Array();
myglobal.array2 = new Array();

//initializes a couple of arrays in this Global object.
var index = 0;

function anything(){ //add items to arrays using a list input
var a = arrayfromargs(arguments);

//stores by symbolic name, messagename is a global property
myglobal.array1[messagename] = a;

//stores by index
myglobal.array2[index] = messagename;

post("location:",myglobal.array2[index],"\n")
post("data:",myglobal.array1[messagename],"\n")

index++; 
}
