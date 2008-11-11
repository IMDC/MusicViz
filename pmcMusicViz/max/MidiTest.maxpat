{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 139.0, 55.0, 759.0, 649.0 ],
		"bglocked" : 0,
		"defrect" : [ 139.0, 55.0, 759.0, 649.0 ],
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
					"maxclass" : "newobj",
					"text" : "pack 0 0 0",
					"fontname" : "Arial",
					"numinlets" : 3,
					"id" : "obj-20",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 354.0, 200.0, 66.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "unpack 0 0 0",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-17",
					"numoutlets" : 3,
					"fontsize" : 12.0,
					"outlettype" : [ "int", "int", "int" ],
					"patching_rect" : [ 353.0, 155.0, 79.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route 1 10",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-16",
					"numoutlets" : 3,
					"fontsize" : 12.0,
					"outlettype" : [ "", "", "" ],
					"patching_rect" : [ 349.0, 234.0, 65.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "noteout 1",
					"fontname" : "Arial",
					"numinlets" : 3,
					"id" : "obj-14",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"patching_rect" : [ 229.0, 276.0, 61.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js channelRouter.js",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-15",
					"numoutlets" : 2,
					"fontsize" : 12.0,
					"outlettype" : [ "", "" ],
					"patching_rect" : [ 496.0, 226.0, 112.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "print tonal",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-13",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"patching_rect" : [ 500.0, 275.0, 63.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "print perc",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-10",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"patching_rect" : [ 603.0, 275.0, 61.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "audition",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-12",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"patching_rect" : [ 275.0, 129.0, 58.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "need to figure out how to route note messages by channel - separate out percussion from regular events",
					"linecount" : 5,
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-6",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"patching_rect" : [ 565.0, 374.0, 150.0, 75.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend broadcast",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-23",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 40.0, 408.0, 111.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "flashserver 31337 128",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-19",
					"numoutlets" : 4,
					"fontsize" : 12.0,
					"outlettype" : [ "anything", "int", "int", "symbol" ],
					"patching_rect" : [ 40.0, 474.0, 129.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "stop",
					"fontname" : "Arial",
					"numinlets" : 2,
					"id" : "obj-18",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 367.0, 65.0, 33.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "p NoteOffMessageRemoval",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-11",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 464.0, 171.0, 158.0, 20.0 ],
					"patcher" : 					{
						"fileversion" : 1,
						"rect" : [ 89.0, 196.0, 640.0, 480.0 ],
						"bglocked" : 0,
						"defrect" : [ 89.0, 196.0, 640.0, 480.0 ],
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
						"boxes" : [ 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "strips out note off events by using velocity 0 as a gate to shut note offs from proceding through",
									"linecount" : 4,
									"fontname" : "Arial",
									"numinlets" : 1,
									"id" : "obj-1",
									"numoutlets" : 0,
									"fontsize" : 12.0,
									"patching_rect" : [ 260.0, 106.0, 154.0, 62.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "pack 0 0 0",
									"fontname" : "Arial",
									"numinlets" : 3,
									"id" : "obj-23",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"patching_rect" : [ 125.0, 140.0, 66.0, 20.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "gate",
									"fontname" : "Arial",
									"numinlets" : 2,
									"id" : "obj-22",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"patching_rect" : [ 80.0, 170.0, 34.0, 20.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "unpack 0 0 0",
									"fontname" : "Arial",
									"numinlets" : 1,
									"id" : "obj-19",
									"numoutlets" : 3,
									"fontsize" : 12.0,
									"outlettype" : [ "int", "int", "int" ],
									"patching_rect" : [ 50.0, 100.0, 79.0, 20.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"numinlets" : 0,
									"id" : "obj-6",
									"numoutlets" : 1,
									"outlettype" : [ "" ],
									"patching_rect" : [ 55.0, 40.0, 25.0, 25.0 ],
									"comment" : "test in message"
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "outlet",
									"numinlets" : 1,
									"id" : "obj-10",
									"numoutlets" : 0,
									"patching_rect" : [ 85.0, 250.0, 25.0, 25.0 ],
									"comment" : ""
								}

							}
 ],
						"lines" : [ 							{
								"patchline" : 								{
									"source" : [ "obj-22", 0 ],
									"destination" : [ "obj-10", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-6", 0 ],
									"destination" : [ "obj-19", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-19", 1 ],
									"destination" : [ "obj-23", 1 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-19", 0 ],
									"destination" : [ "obj-23", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-19", 2 ],
									"destination" : [ "obj-23", 2 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-23", 0 ],
									"destination" : [ "obj-22", 1 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-19", 1 ],
									"destination" : [ "obj-22", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
 ]
					}
,
					"saved_object_attributes" : 					{
						"fontname" : "Arial",
						"default_fontface" : 0,
						"default_fontname" : "Arial",
						"fontface" : 0,
						"fontsize" : 12.0,
						"globalpatchername" : "",
						"default_fontsize" : 12.0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "read testmary-poly.mid",
					"fontname" : "Arial",
					"numinlets" : 2,
					"id" : "obj-7",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 555.0, 3.0, 132.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "init",
					"fontname" : "Arial",
					"numinlets" : 2,
					"id" : "obj-25",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 10.0, 183.0, 32.5, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js tonalEventProcessing.js",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-4",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 40.0, 348.0, 151.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "noteout 10",
					"fontname" : "Arial",
					"numinlets" : 3,
					"id" : "obj-9",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"patching_rect" : [ 320.0, 316.0, 67.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route note",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-8",
					"numoutlets" : 2,
					"fontsize" : 12.0,
					"outlettype" : [ "", "" ],
					"patching_rect" : [ 475.0, 127.0, 65.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "play 0",
					"fontname" : "Arial",
					"numinlets" : 2,
					"id" : "obj-5",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 461.0, 15.0, 43.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "read",
					"fontname" : "Arial",
					"numinlets" : 2,
					"id" : "obj-3",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"patching_rect" : [ 321.0, 9.0, 35.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "mxj midifile",
					"fontname" : "Arial",
					"numinlets" : 1,
					"id" : "obj-1",
					"numoutlets" : 4,
					"fontsize" : 12.0,
					"outlettype" : [ "", "", "", "" ],
					"patching_rect" : [ 477.0, 94.0, 70.0, 20.0 ]
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-16", 1 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-16", 0 ],
					"destination" : [ "obj-14", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-16", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 1 ],
					"destination" : [ "obj-20", 2 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 0 ],
					"destination" : [ "obj-20", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 2 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-17", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-15", 0 ],
					"destination" : [ "obj-13", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-23", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"midpoints" : [ 19.5, 297.0, 49.5, 297.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-1", 0 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-25", 0 ],
					"hidden" : 0,
					"midpoints" : [ 470.5, 45.0, 19.5, 45.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-18", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-23", 0 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
 ]
	}

}
