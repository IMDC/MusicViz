{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 29.0, 75.0, 678.0, 629.0 ],
		"bglocked" : 0,
		"defrect" : [ 29.0, 75.0, 678.0, 629.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 0,
		"default_fontsize" : 12.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 1,
		"boxanimatetime" : 200,
		"imprint" : 0,
		"metadata" : [  ],
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "initializes note ranges for each channnel",
					"linecount" : 2,
					"id" : "obj-29",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 519.0, 98.0, 150.0, 34.0 ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "init",
					"id" : "obj-25",
					"numinlets" : 2,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 498.0, 53.0, 32.5, 18.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "inlet",
					"id" : "obj-15",
					"numinlets" : 0,
					"numoutlets" : 1,
					"patching_rect" : [ 499.0, 11.0, 25.0, 25.0 ],
					"outlettype" : [ "" ],
					"comment" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "we are splitting out notes by their channels in order that we don get cross talk and confuse the interval calculations.",
					"linecount" : 5,
					"id" : "obj-30",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 66.0, 47.0, 150.0, 75.0 ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "in from channel router",
					"id" : "obj-28",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 61.0, 15.0, 150.0, 20.0 ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "output to flashserver",
					"id" : "obj-26",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 327.0, 568.0, 150.0, 20.0 ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "skip 10 - percussion",
					"id" : "obj-24",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 371.0, 426.0, 150.0, 20.0 ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 9",
					"id" : "obj-22",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 349.0, 466.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 11",
					"id" : "obj-21",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 383.0, 398.0, 166.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 12",
					"id" : "obj-20",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 395.0, 359.0, 167.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 13",
					"id" : "obj-19",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 414.0, 325.0, 167.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 14",
					"id" : "obj-18",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 425.0, 290.0, 167.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 15",
					"id" : "obj-17",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 442.0, 254.0, 167.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 16",
					"id" : "obj-16",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 450.0, 206.0, 167.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 8",
					"id" : "obj-14",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 144.0, 481.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 7",
					"id" : "obj-13",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 123.0, 447.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 6",
					"id" : "obj-12",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 106.0, 409.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 5",
					"id" : "obj-11",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 80.0, 372.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 4",
					"id" : "obj-10",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 59.0, 332.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 3",
					"id" : "obj-9",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 43.0, 293.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 2",
					"id" : "obj-8",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 24.0, 252.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "gate 16",
					"id" : "obj-7",
					"numinlets" : 2,
					"fontsize" : 12.0,
					"numoutlets" : 16,
					"patching_rect" : [ 220.0, 140.0, 221.5, 20.0 ],
					"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "+ 1",
					"id" : "obj-6",
					"numinlets" : 2,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 324.0, 89.0, 32.5, 20.0 ],
					"outlettype" : [ "int" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "pack 0 0 0",
					"id" : "obj-5",
					"numinlets" : 3,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 228.0, 88.0, 66.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "unpack 0 0 0",
					"id" : "obj-3",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 3,
					"patching_rect" : [ 253.0, 52.0, 79.0, 20.0 ],
					"outlettype" : [ "int", "int", "int" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "inlet",
					"id" : "obj-2",
					"numinlets" : 0,
					"numoutlets" : 1,
					"patching_rect" : [ 220.0, 12.0, 25.0, 25.0 ],
					"outlettype" : [ "" ],
					"comment" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "outlet",
					"id" : "obj-1",
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 290.0, 558.0, 25.0, 25.0 ],
					"comment" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js 1",
					"id" : "obj-4",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 10.0, 217.0, 161.0, 20.0 ],
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-15", 0 ],
					"destination" : [ "obj-25", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 2 ],
					"destination" : [ "obj-5", 2 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 1 ],
					"destination" : [ "obj-5", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 0 ],
					"destination" : [ "obj-5", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 2 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-7", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-7", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 0 ],
					"destination" : [ "obj-3", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 19.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"midpoints" : [ 229.5, 204.0, 19.5, 204.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 33.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 1 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"midpoints" : [ 243.0, 213.0, 197.0, 213.0, 197.0, 242.0, 33.5, 242.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 2 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [ 256.5, 279.0, 52.5, 279.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-9", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 52.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 3 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"midpoints" : [ 270.0, 318.0, 68.5, 318.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-10", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 68.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 4 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"midpoints" : [ 283.5, 357.0, 90.0, 357.0, 90.0, 369.0, 89.5, 369.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-11", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 89.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 5 ],
					"destination" : [ "obj-12", 0 ],
					"hidden" : 0,
					"midpoints" : [ 297.0, 396.0, 115.5, 396.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-12", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 115.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-12", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 6 ],
					"destination" : [ "obj-13", 0 ],
					"hidden" : 0,
					"midpoints" : [ 310.5, 435.0, 132.5, 435.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-13", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 132.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-13", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 7 ],
					"destination" : [ "obj-14", 0 ],
					"hidden" : 0,
					"midpoints" : [ 324.0, 471.0, 153.5, 471.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-14", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 153.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-14", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-22", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 358.5, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 8 ],
					"destination" : [ "obj-22", 0 ],
					"hidden" : 0,
					"midpoints" : [ 337.5, 447.0, 358.5, 447.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-22", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 392.5, 420.0, 557.0, 420.0, 557.0, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 10 ],
					"destination" : [ "obj-21", 0 ],
					"hidden" : 0,
					"midpoints" : [ 364.5, 393.0, 392.5, 393.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-21", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 404.5, 381.0, 570.0, 381.0, 570.0, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 11 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [ 378.0, 345.0, 404.5, 345.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-19", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 423.5, 345.0, 581.0, 345.0, 581.0, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 12 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 0,
					"midpoints" : [ 391.5, 321.0, 423.5, 321.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-18", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 434.5, 312.0, 595.0, 312.0, 595.0, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 13 ],
					"destination" : [ "obj-18", 0 ],
					"hidden" : 0,
					"midpoints" : [ 405.0, 276.0, 434.5, 276.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-18", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-17", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 14 ],
					"destination" : [ "obj-17", 0 ],
					"hidden" : 0,
					"midpoints" : [ 418.5, 249.0, 451.5, 249.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 451.5, 276.0, 608.0, 276.0, 608.0, 543.0, 299.5, 543.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-16", 0 ],
					"hidden" : 0,
					"color" : [ 0.811765, 0.372549, 0.372549, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 15 ],
					"destination" : [ "obj-16", 0 ],
					"hidden" : 0,
					"midpoints" : [ 432.0, 198.0, 459.5, 198.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-16", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 459.5, 231.0, 622.0, 231.0, 622.0, 543.0, 299.5, 543.0 ]
				}

			}
 ]
	}

}
