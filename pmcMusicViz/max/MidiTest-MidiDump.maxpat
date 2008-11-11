{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 4.0, 50.0, 955.0, 692.0 ],
		"bglocked" : 0,
		"defrect" : [ 4.0, 50.0, 955.0, 692.0 ],
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
					"maxclass" : "message",
					"text" : "min 2",
					"numoutlets" : 1,
					"id" : "obj-55",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 522.0, 592.0, 40.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "testPrint",
					"text" : "print test",
					"numoutlets" : 0,
					"id" : "obj-53",
					"fontsize" : 12.0,
					"patching_rect" : [ 819.0, 610.0, 57.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "rtRange",
					"text" : "js retreiveRanges.js",
					"numoutlets" : 1,
					"id" : "obj-52",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 418.0, 441.0, 116.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "preMin",
					"text" : "prepend minRange",
					"numoutlets" : 1,
					"id" : "obj-51",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 444.0, 371.0, 113.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "preMax",
					"text" : "prepend maxRange",
					"numoutlets" : 1,
					"id" : "obj-50",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 301.0, 369.0, 116.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel10",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-28",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 780.0, 404.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel14",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-47",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 862.0, 310.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "clear",
					"numoutlets" : 1,
					"id" : "obj-45",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 541.0, 230.0, 37.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel15",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-36",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 880.0, 288.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel11",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-37",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 803.0, 382.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel12",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-39",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 824.0, 356.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel13",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-40",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 845.0, 333.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel16",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-43",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 890.0, 262.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel6",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-35",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 691.0, 503.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel9",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-34",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 760.0, 428.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel8",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-33",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 736.0, 450.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel7",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-32",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 716.0, 477.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel5",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-31",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 673.0, 526.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel4",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-30",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 651.0, 552.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel3",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-21",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 633.0, 581.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel2",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-20",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 620.0, 610.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"varname" : "channel1",
					"text" : "coll",
					"numoutlets" : 4,
					"id" : "obj-1",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 602.0, 642.0, 59.5, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"save" : [ "#N", "coll", ";" ],
					"saved_object_attributes" : 					{
						"embed" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "+ 1",
					"numoutlets" : 1,
					"id" : "obj-10",
					"outlettype" : [ "int" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 775.0, 166.0, 32.5, 20.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "pack 0 0 0 0 0",
					"numoutlets" : 1,
					"id" : "obj-7",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 619.0, 173.0, 86.0, 20.0 ],
					"numinlets" : 5,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "gate 16",
					"numoutlets" : 16,
					"id" : "obj-2",
					"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 648.0, 213.0, 221.5, 20.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "unpack 0 0 0 0 0",
					"numoutlets" : 5,
					"id" : "obj-42",
					"outlettype" : [ "int", "int", "int", "int", "int" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 629.0, 140.0, 99.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route note",
					"numoutlets" : 2,
					"id" : "obj-41",
					"outlettype" : [ "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 566.0, 111.0, 65.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "dump",
					"numoutlets" : 1,
					"id" : "obj-25",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 458.0, 40.0, 41.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "init note counter to 0\nfor proper tonal reset",
					"linecount" : 2,
					"numoutlets" : 0,
					"id" : "obj-38",
					"fontsize" : 12.0,
					"patching_rect" : [ 139.0, 95.0, 123.0, 34.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "abs.TonalProcessing",
					"numoutlets" : 1,
					"id" : "obj-4",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 40.0, 385.0, 121.0, 20.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "mxj MidiFileMusicViz",
					"numoutlets" : 4,
					"id" : "obj-6",
					"outlettype" : [ "", "", "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 329.0, 88.0, 122.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "abs.MIDIPlayback",
					"numoutlets" : 0,
					"id" : "obj-9",
					"fontsize" : 12.0,
					"patching_rect" : [ 306.0, 236.0, 107.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "p NoteOffMessageRemoval",
					"numoutlets" : 1,
					"id" : "obj-11",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 77.0, 152.0, 158.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
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
									"numoutlets" : 0,
									"id" : "obj-1",
									"fontsize" : 12.0,
									"patching_rect" : [ 260.0, 106.0, 154.0, 62.0 ],
									"numinlets" : 1,
									"fontname" : "Arial"
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "pack 0 0 0",
									"numoutlets" : 1,
									"id" : "obj-23",
									"outlettype" : [ "" ],
									"fontsize" : 12.0,
									"patching_rect" : [ 125.0, 140.0, 66.0, 20.0 ],
									"numinlets" : 3,
									"fontname" : "Arial"
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "gate",
									"numoutlets" : 1,
									"id" : "obj-22",
									"outlettype" : [ "" ],
									"fontsize" : 12.0,
									"patching_rect" : [ 80.0, 170.0, 34.0, 20.0 ],
									"numinlets" : 2,
									"fontname" : "Arial"
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "unpack 0 0 0",
									"numoutlets" : 3,
									"id" : "obj-19",
									"outlettype" : [ "int", "int", "int" ],
									"fontsize" : 12.0,
									"patching_rect" : [ 50.0, 100.0, 79.0, 20.0 ],
									"numinlets" : 1,
									"fontname" : "Arial"
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"numoutlets" : 1,
									"id" : "obj-6",
									"outlettype" : [ "" ],
									"patching_rect" : [ 55.0, 40.0, 25.0, 25.0 ],
									"numinlets" : 0,
									"comment" : "test in message"
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "outlet",
									"numoutlets" : 0,
									"id" : "obj-10",
									"patching_rect" : [ 85.0, 250.0, 25.0, 25.0 ],
									"numinlets" : 1,
									"comment" : ""
								}

							}
 ],
						"lines" : [ 							{
								"patchline" : 								{
									"source" : [ "obj-19", 1 ],
									"destination" : [ "obj-22", 0 ],
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
									"source" : [ "obj-19", 2 ],
									"destination" : [ "obj-23", 2 ],
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
									"source" : [ "obj-19", 1 ],
									"destination" : [ "obj-23", 1 ],
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
									"source" : [ "obj-22", 0 ],
									"destination" : [ "obj-10", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
 ]
					}
,
					"saved_object_attributes" : 					{
						"fontface" : 0,
						"default_fontface" : 0,
						"fontsize" : 12.0,
						"globalpatchername" : "",
						"default_fontname" : "Arial",
						"default_fontsize" : 12.0,
						"fontname" : "Arial"
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "js channelRouter.js",
					"numoutlets" : 2,
					"id" : "obj-15",
					"outlettype" : [ "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 135.0, 298.0, 112.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "audition",
					"numoutlets" : 0,
					"id" : "obj-12",
					"fontsize" : 12.0,
					"patching_rect" : [ 333.0, 259.0, 58.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "flashserver 31337 128",
					"numoutlets" : 4,
					"id" : "obj-19",
					"outlettype" : [ "anything", "int", "int", "symbol" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 257.0, 628.0, 129.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "stop",
					"numoutlets" : 1,
					"id" : "obj-18",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 402.0, 40.0, 33.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route note",
					"numoutlets" : 2,
					"id" : "obj-8",
					"outlettype" : [ "", "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 276.0, 167.0, 65.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "play 0",
					"numoutlets" : 1,
					"id" : "obj-5",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 354.0, 41.0, 43.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "read",
					"numoutlets" : 1,
					"id" : "obj-3",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"patching_rect" : [ 310.0, 40.0, 35.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial"
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-4", 1 ],
					"hidden" : 0,
					"midpoints" : [ 363.5, 75.0, 120.0, 75.0, 120.0, 372.0, 151.5, 372.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-55", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 3 ],
					"destination" : [ "obj-52", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-28", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 9 ],
					"destination" : [ "obj-28", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-45", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-47", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 13 ],
					"destination" : [ "obj-47", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-43", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-36", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-40", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-39", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-37", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-34", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-33", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-32", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-35", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-31", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-30", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-21", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 12 ],
					"destination" : [ "obj-40", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 11 ],
					"destination" : [ "obj-39", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 10 ],
					"destination" : [ "obj-37", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 14 ],
					"destination" : [ "obj-36", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 15 ],
					"destination" : [ "obj-43", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 8 ],
					"destination" : [ "obj-34", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 7 ],
					"destination" : [ "obj-33", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 6 ],
					"destination" : [ "obj-32", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 5 ],
					"destination" : [ "obj-35", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 4 ],
					"destination" : [ "obj-31", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 3 ],
					"destination" : [ "obj-30", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 2 ],
					"destination" : [ "obj-21", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 1 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 4 ],
					"destination" : [ "obj-7", 4 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 4 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 0 ],
					"destination" : [ "obj-2", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 0 ],
					"destination" : [ "obj-7", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 1 ],
					"destination" : [ "obj-7", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 2 ],
					"destination" : [ "obj-7", 2 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 3 ],
					"destination" : [ "obj-7", 3 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-41", 0 ],
					"destination" : [ "obj-42", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-10", 0 ],
					"destination" : [ "obj-2", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 1 ],
					"destination" : [ "obj-41", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 0 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-18", 0 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-51", 0 ],
					"destination" : [ "obj-52", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-50", 0 ],
					"destination" : [ "obj-52", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-15", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-15", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
 ]
	}

}
