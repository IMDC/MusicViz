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
		//Sets up the JFrame and adds a border layout, so panels can be easily arranged.
		frame = new JFrame();
		frame.setSize(width, height);
		frame.setVisible(true);
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
        
        //Sets the program controller which allows communication with the backend.
        controller = c;

        //Add listeners
        addStopButtonListener();
        addSliderChangeListener();
        addListMouseListener();
        addListKeyListener();
        addSavePlaylistListener();
        addLoadPlayListListener();
        addTransferHandler();
        addListChangeListener();
        setPlaylistCellRenderer();
        addAddMidiSongMouseListener();
        addPlayPauseToggleButton();
        loopCheckBoxActionListener();
        addChangeColourListener();
        
        //Sometimes the components on the frame do not draw when
        //Initialised. So, i revalidated one component which forces
        //every component to be redrawn.
        buttonPanel.revalidate();        
        
        startVisualizer();
	}
	
	void addChangeColourListener()
	{
		changeColourMenuItem.addMouseListener(new ChangeColourPaletteListener(controller));
	}
	
	private void startVisualizer()
	{
        /**
         * This is all for the OpenGL part of the program
         */
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
	    openGLFrame.setVisible(true);
	    animator.start();
	}
	
	public Frame getOpenGLFrame()
	{
		return openGLFrame;
	}
	
	public FPSAnimator getFPSAnimator()
	{
		return animator;
	}
	
	public Visualizer getVisualizer()
	{
		return visualizer;
	}
	
	public JCheckBox getLoopCheckBox()
	{
		return loopCheckBox;
	}
	
	/**
	 * Returns the Play/Pause Toggle button.
	 * @return
	 */
	public JToggleButton getPlayPauseToggleButton()
	{
		return playPauseButton;
	}
	
	/**
	 * Returns the song index slider.
	 * @return
	 */
	public JSlider getSlider()
	{
		return slider;
	}
	
	/**
	 * Returns the play list.
	 * @return
	 */
	public JList getPlaylist()
	{
		return playList;
	}
	
	/**
	 * Updates the time code display, which is a text field, to display
	 * the current minute and second in the song.
	 * 
	 * @param time
	 */
	public void updateTimer(String time)
	{
		songPositionNumber.setText(time+"/"+totalTime);
	}
	
	/**
	 * Sets the total length of the time of the song.
	 * 
	 * @param time
	 */
	public void updateTotalTime( String time )
	{
		totalTime = time;
	}
	
	/**
	 * Adds an action listener to the stop button and gives the action listener
	 * the program controller for communication with the Player object.
	 */
	private void addStopButtonListener()
	{
		stopButton.addActionListener( new StopButtonListener(controller) );
	}
	
	/**
	 * Adds an change listener to the slider and gives the change listener
	 * the program controller for communication with the Player object.
	 */	
	private void addSliderChangeListener()
	{
		SlideMouseListener sml = new SlideMouseListener(controller);
		slider.addMouseListener( sml );
		slider.addMouseMotionListener( sml );
		slider.addChangeListener( new SliderTimeChangeListener(controller) );
	}
	
	public JFrame getPlayerFrame()
	{
		return frame;
	}
	
	
	/**
	 * In order to be able to drag objects into a list, a mediator is needed such 
	 * as a ListTransferHandler, so this is added to the list in this method.
	 */
	private void addTransferHandler()
	{
		playList.setTransferHandler(new ListTransferHandler(controller));
	}
	
	/**
	 * Adds a mouse listener to the playList so double clicks can be reconized to load a new song.
	 */
	private void addListMouseListener()
	{
		playList.addMouseListener(new ListMouseListener(controller));
	}
	
	/**
	 * Key listener is added to the play list. This used when deleting songs from the playlist
	 */
	private void addListKeyListener()
	{
		playList.addKeyListener( new ListKeyListener(controller) );
	}
	
	/**
	 * Adds a mouse listener to the menu item that saves the current list.
	 */
	private void addSavePlaylistListener()
	{
		saveMenuItem.addMouseListener(new SavePlaylistMouseListener(controller));
	}
	
	/**
	 * Adds a mouse listener to the menu item that loads the play list files.
	 */
	private void addLoadPlayListListener()
	{
		loadMenuItem.addMouseListener( new LoadPlayListMouseListener(controller));
	}
	
	/**
	 * Adds a listener to the play list, that lists for changes to the list.
	 */
	private void addListChangeListener()
	{
		playList.addListSelectionListener( new ListChangeListener(controller) );
	}
	
	/**
	 * Sets a custom renderer for the Jlist object that displays the songs in the list.
	 */
	private void setPlaylistCellRenderer()
	{
		playList.setCellRenderer( new FileCellRenderer(controller) );
	}
	
	/**
	 * Adds a mouse listener to an Menu item called openMidi item.
	 */
	private void addAddMidiSongMouseListener()
	{
		openMidiItem.addMouseListener( new AddMidiSongMouseListener(controller) );
	}
	
	/**
	 * Adds an action listener to the toggle button, which is used for pausing and playing the song
	 */
	private void addPlayPauseToggleButton()
	{
		playPauseButton.addActionListener( new PlayPauseToggleButtonActionListener(controller) );
	}
	
	/**
	 * Adds an action listener to the object the user selects to turn on/off looping
	 * of the playlist
	 */
	private void loopCheckBoxActionListener()
	{
		loopCheckBox.addActionListener( new LoopingCheckBoxListener() );
	}
	
}
