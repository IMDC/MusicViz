package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import controller.Controller;
import handlers.ListTransferHandler;
import listeners.AddMidiSongMouseListener;
import listeners.ChangeColourPaletteListener;
import listeners.ListChangeListener;
import listeners.ListKeyListener;
import listeners.ListMouseListener;
import listeners.LoadPlayListMouseListener;
import listeners.LoopingCheckBoxListener;
import listeners.PlayPauseToggleButtonActionListener;
import listeners.SavePlaylistMouseListener;
import listeners.SlideMouseListener;
import listeners.SliderTimeChangeListener;
import listeners.StopButtonListener;
import renderers.FileCellRenderer;
import visualizer.Visualizer;

import com.sun.opengl.util.FPSAnimator;

/**
 * GUIs allow easy communication with the program. They allow users with little technical experience 
 * communicate by creating objects that run code and/or commands that would take some learning. I.E Take
 * Windows 3.1 for example. It was a shell placed over DOS with a GUI, whenever the user ran a file
 * the interpreter translated the command into a DOS command for the system. 
 * 
 * This GUI acts as a simple CD or MP3 player. By offering Play, Pause, Stop and Open song buttons plus menu items
 * for saving and loading play lists. There is also a slider
 * used for simple song indexing.
 * 
 * @author Michael Pouris
 *
 */
public class GUI 
{
	//This is for the MidiPlayer
	private JFrame frame;
	
	private JPanel buttonPanel;
	private JPanel sliderPanel;
	private JPanel playListPanel;
	
	private JScrollPane scrollPane;
	private  DefaultListModel listModel;
	
	private JToggleButton playPauseButton;
	private JCheckBox loopCheckBox;
	private JButton stopButton;
	private JSlider slider;
	private TextField songPositionNumber;
	private JList playList;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu optionsMenu;
	private JMenuItem saveMenuItem;
	private JMenuItem loadMenuItem;
	private JMenuItem openMidiItem;
	private JMenuItem changeColourMenuItem;
	
	private String totalTime;
	
	private Controller controller;
	
	//This is for the OpenGL visualizers.
	private Frame openGLFrame;
	private FPSAnimator animator;
	private GLCanvas canvas;
	private Visualizer visualizer;
	
	/**
	 * Constructor for the GUI class. Sets up a JFrame object with the width and height parameters given for the frame size.
	 * The frame, which has a border layout object, allows panel object to be easily placed within the frame.  There are 2 panel
	 * created in the constructor, one for the top of the window and one for the bottom. The top panel contains the buttons for Opening,
	 * playing and stopping the play back and the bottom panel contains a single slider for song indexing. This also sets the program controller
	 * so the button components can communicate with the back end such as the Player.
	 * 
	 * @param width
	 * @param height
	 * @param c
	 */
	public GUI(int width, int height, Controller c)
	{
        //Sets the program controller which allows communication with the backend.
        controller = c;

        //Create the main window in a seperate thread which holds the controls
        SwingUtilities.invokeLater(
        		new Runnable()
        		{
        			public void run()
        			{
        				startPlayerGUI();
        			}
        		});
 
        //Create the visualizer in a seperate thread
        SwingUtilities.invokeLater(
        		new Runnable()
        		{
        			public void run()
        			{
        				startVisualizer();
        			}
        		});
	}
	
	private void startPlayerGUI()
	{
		//Sets up the JFrame and adds a border layout, so panels can be easily arranged.
		frame = new JFrame();
		frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setTitle("MusicViz: Midi Player");
        
        //Sets up the panels
        buttonPanel = new JPanel();
        sliderPanel = new JPanel();
        playListPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Song Navigation"));
        playListPanel.setBorder(BorderFactory.createTitledBorder("Playlist"));
        
        
        //adds the panels to the frame
        frame.add(buttonPanel,BorderLayout.NORTH);
        frame.add(sliderPanel,BorderLayout.CENTER);
        frame.add(playListPanel, BorderLayout.SOUTH);
        
        //Sets up Buttons
        playPauseButton = new JToggleButton("Play ");
        stopButton = new JButton("Stop");
        loopCheckBox = new JCheckBox("Loop");
        
        totalTime = "0:00";
        
        //Sets a text area for the minutes and seconds in the song
        //we do not want the user to modify this either, so set it to false.
        songPositionNumber = new TextField("0:00/"+totalTime,8);
        songPositionNumber.setEditable(false);
        
        //Set up the index slider and set start point to zero
        //Also the slider is not enabled. Because there is no song added
        slider = new JSlider();
        slider.setValue(0);
        slider.setSnapToTicks(true);
        slider.setEnabled(false);
        slider.setMinimum(0);
        
        
        //Sets up the playlist
        listModel = new DefaultListModel();
        playList = new JList(listModel);
        playList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        playList.setDragEnabled(true);
        playList.setDropMode(DropMode.INSERT);
        
        scrollPane = new JScrollPane(playList);
        scrollPane.setPreferredSize(new Dimension(380,190));
        
        //For the menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");   
        optionsMenu = new JMenu("Options");
        saveMenuItem = new JMenuItem("Save Playlist");
        loadMenuItem = new JMenuItem("Load Playlist");
        openMidiItem = new JMenuItem("Load Midi");
        changeColourMenuItem = new JMenuItem("Change Colours");
        
        
        //Adds the menus to menus and to the frame
        fileMenu.add(openMidiItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        optionsMenu.add(changeColourMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);

        //Adds the buttons and the slider to appropriate panels.
        //The buttons are added to the north panel and the slider to the south
        buttonPanel.add(playPauseButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(loopCheckBox);
        sliderPanel.add(songPositionNumber);
        sliderPanel.add(slider);
        playListPanel.add(scrollPane);
        
        //Add listeners
        stopButton.addActionListener( new StopButtonListener(controller) );
        playList.addMouseListener(new ListMouseListener(controller));
        playList.addKeyListener( new ListKeyListener(controller) );
        saveMenuItem.addMouseListener(new SavePlaylistMouseListener(controller));
        loadMenuItem.addMouseListener( new LoadPlayListMouseListener(controller));
        playList.setTransferHandler(new ListTransferHandler(controller));
        playList.addListSelectionListener( new ListChangeListener(controller) );
        playList.setCellRenderer( new FileCellRenderer(controller) );
        openMidiItem.addMouseListener( new AddMidiSongMouseListener(controller) );
        playPauseButton.addActionListener( new PlayPauseToggleButtonActionListener(controller) );
        loopCheckBox.addActionListener( new LoopingCheckBoxListener() );
        changeColourMenuItem.addMouseListener(new ChangeColourPaletteListener(controller));
        
		SlideMouseListener sml = new SlideMouseListener(controller);
		slider.addMouseListener( sml );
		slider.addMouseMotionListener( sml );
		slider.addChangeListener( new SliderTimeChangeListener(controller) );
		
        frame.setVisible(true);
	}
	
	private void startVisualizer()
	{
        openGLFrame = new Frame("Visualizer");
        canvas = new GLCanvas();
        visualizer = new Visualizer(controller);
	    canvas.addGLEventListener(visualizer);
	    canvas.addMouseMotionListener(visualizer);
	    canvas.addMouseListener(visualizer);
	    canvas.addMouseWheelListener(visualizer);
	    canvas.addKeyListener(visualizer);
	    openGLFrame.add(canvas);
	    openGLFrame.setSize(1024, 768);
	    animator = new FPSAnimator(canvas, 300);
	    animator.setRunAsFastAsPossible(true);
	    openGLFrame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e){
	          new Thread(new Runnable() {
	              public void run() {
	                System.exit(0);
	              }
	            }).start();
	        }
	      });
	    animator.start();
	    openGLFrame.setVisible(true);
	}
	
	public Visualizer getVisualizer()
	{
		return visualizer;
	}
	
	public JCheckBox getLoopCheckBox()
	{
		return loopCheckBox;
	}
	
	public void setSelectedForPlayButton( final boolean isSelected )
	{
		Runnable setSelected = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		playPauseButton.setSelected(isSelected);
	        	}
			};
		SwingUtilities.invokeLater(setSelected);
	}
	
	public void setTextForPlayButton(final String text)
	{
		Runnable setText = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		playPauseButton.setText(text);
	        	}
			};
		SwingUtilities.invokeLater(setText);
	}
	
	public void setMaximumValueForSlider( final int maxTimeInSeconds )
	{
		Runnable setMaximum = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		slider.setMaximum(maxTimeInSeconds);
	        	}
			};
		SwingUtilities.invokeLater(setMaximum);
	}

	public void setCurrentValueForSlider( final int currentValue )
	{
		Runnable setCurrentValue = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		slider.setValue(currentValue);
	        	}
			};
		SwingUtilities.invokeLater(setCurrentValue);
	}
	
	public void enableSlider()
	{
		Runnable enableSlider = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		slider.setEnabled(true);
	        	}
			};
		SwingUtilities.invokeLater(enableSlider);
	}
	
	public void disableSlider()
	{
		Runnable disableSlider = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		slider.setEnabled(false);
	        	}
			};
		SwingUtilities.invokeLater(disableSlider);
	}
	

	public JList getPlaylist()
	{
		return playList;
	}
	

	public void updateTimer(final String time)
	{
		Runnable getTextFieldText = 
		      	new Runnable()
				{
		        	public void run()
		        	{
		        		songPositionNumber.setText(time+"/"+totalTime);
		        	}
				};
		SwingUtilities.invokeLater(getTextFieldText);
	}
	

	public void updateTotalTime( String time )
	{
		totalTime = time;
	}

	public void setEnabledPlayerFrame(final boolean enabled )
	{
		Runnable setEnabled = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		frame.setEnabled(enabled);
	        	}
			};
		SwingUtilities.invokeLater(setEnabled);
	}
	
}
