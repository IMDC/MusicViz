package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.awt.GLCanvas;
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
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;


import controller.Controller;
import handlers.ListTransferHandler;
import listeners.AddMidiSongMouseListener;
import listeners.ChangeColourPaletteListener;
import listeners.ChangeVisualDisplay;
import listeners.ChangeVolumeListener;
import listeners.ListChangeListener;
import listeners.ListKeyListener;
import listeners.ListMouseListener;
import listeners.LoopingCheckBoxListener;
import listeners.MaxMSPSettingsListener;
import listeners.PlayPauseToggleButtonActionListener;
import listeners.SlideMouseListener;
import listeners.SliderTimeChangeListener;
import listeners.StopButtonListener;
import renderers.FileCellRenderer;
import visualizer.ConcurrentVisualizer;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * This class initialises and displays the {@link ConcurrentVisualizer} and the MIDI
 * player GUI. It also contains thread-safe methods for altering GUI objects during 
 * runtime. This consists of requesting the {@link SwingUtilities} to invoke threads
 * to update the GUI during runtime in a thread-safe way. Please see Swing's threading
 * policy.
 * 
 * @author Michael Pouris
 *
 */
public class GUI 
{
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
	private JMenuItem openMidiItem;
	private JMenuItem changeColourMenuItem;
	private JMenuItem changeDisplayMenuItem;
	private JMenuItem changeMaxMSPCommunication;
	private JMenuItem changeVolumes;
	
	private String totalTime;
	
	private Frame openGLFrame;
	private FPSAnimator animator;
	private GLCanvas canvas;
	private ConcurrentVisualizer visualizer;
	
	/**
	 * The default and only constructor. 
	 * <p>
	 * This creates the 2 GUI windows, which
	 * are the MIDI player controls and the {@link ConcurrentVisualizer}.
	 * Swing is not thread-safe, therefore the last call when initialising a GUI
	 * must always be {@code setVisible(boolean)}. Therefore, the {@link ConcurrentVisualizer}
	 * is created by its own thread and the {@code playerGUI} is created in its own thread.
	 * To keep the program initialisation n a predictable fashion as well as thread-safe,
	 * the threads are joined, such that the constructor does not complete before
	 * the threads do.
	 * 
	 * @throws InterruptedException
	 */
	public GUI() throws InterruptedException
	{
        Runnable openGL = new Runnable()
        		{
        			public void run()
        			{
        				startVisualizer();
        			}
        		};
        Thread t1 = new Thread(openGL);//new Thread(openGL).start();
        t1.start();
		t1.join();

        
        //startPlayerGUI();
        Runnable playerGUI = new Runnable()
		{
			public void run()
			{
				startPlayerGUI();
			}
		};
		Thread t2 = new Thread(playerGUI);//new Thread(openGL).start();
		t2.start();
		t2.join();
	}
	
	/**
	 * Creates the {@link JFrame}, which contains the MIDI player controls.
	 * <p>
	 * This is invoked by {@line #GUI()} during initialisation in a thread. 
	 * Swing is not thread-safe, therefore a thred is used such that the last
	 * call is always {@link JFrame}'s {@code setVisible(boolean)}.
	 */
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
        openMidiItem = new JMenuItem("Load Midi");
        changeColourMenuItem = new JMenuItem("Change Colours");
        changeDisplayMenuItem = new JMenuItem("Change Display");
        changeMaxMSPCommunication = new JMenuItem("MaxMSP Communication Settings");
        changeVolumes = new JMenuItem("Change Volume");
        
        //Adds the menus to menus and to the frame
        fileMenu.add(openMidiItem);
        optionsMenu.add(changeColourMenuItem);
        optionsMenu.add(changeDisplayMenuItem);
        optionsMenu.add(changeMaxMSPCommunication);
        optionsMenu.add(changeVolumes);
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
        
        frame.setEnabled(false);
        
        System.out.println("Variables for Player Initialized Correctly");
        frame.setVisible(true);
	}
	
	/**
	 * This method adds listeners for the GUI. 
	 * <p>
	 * The listeners are essential for the functionality because they
	 * communicate with the front and back-ends of the program, therefore
	 * they are given the {@link Controller} to use.
	 * <p>
	 * @param controller
	 */
	public void addListeners(Controller controller)
	{
		stopButton.addActionListener( new StopButtonListener(controller) );
        
        playList.addMouseListener(new ListMouseListener(controller));
        playList.addKeyListener( new ListKeyListener(controller) );
        playList.setTransferHandler(new ListTransferHandler(controller));
        playList.addListSelectionListener( new ListChangeListener(controller) );
        playList.setCellRenderer( new FileCellRenderer(controller) );
        
        //saveMenuItem.addMouseListener(new SavePlaylistMouseListener(controller));
        //loadMenuItem.addMouseListener( new LoadPlayListMouseListener(controller));
        
        openMidiItem.addMouseListener( new AddMidiSongMouseListener(controller) );
        playPauseButton.addActionListener( new PlayPauseToggleButtonActionListener(controller) );
        loopCheckBox.addActionListener( new LoopingCheckBoxListener() );
        changeColourMenuItem.addMouseListener(new ChangeColourPaletteListener(controller));
        changeDisplayMenuItem.addMouseListener( new ChangeVisualDisplay(controller) );
        changeMaxMSPCommunication.addMouseListener( new MaxMSPSettingsListener(controller));
        changeVolumes.addMouseListener( new ChangeVolumeListener(controller) );
        
		SlideMouseListener sml = new SlideMouseListener(controller);
		slider.addMouseListener( sml );
		slider.addMouseMotionListener( sml );
		slider.addChangeListener( new SliderTimeChangeListener(controller) );
		System.out.println("Player GUI: Initialized Correctly");
		frame.setEnabled(true);
	}
	
	/**
	 * Creates the OpenGL visualiser.
	 * <p>
	 * This is invoked by {@line #GUI()} during initialisation in a thread. 
	 * Swing is not thread-safe, therefore a thred is used such that the last
	 * call is always {@link JFrame}'s {@code setVisible(boolean)}.
	 */
	private void startVisualizer()
	{
        openGLFrame = new Frame("Visualizer");
        canvas = new GLCanvas();
        visualizer = new ConcurrentVisualizer();
	    canvas.addGLEventListener(visualizer);
	    canvas.addMouseMotionListener(visualizer);
	    canvas.addMouseWheelListener(visualizer);
	    canvas.addKeyListener(visualizer);
	    openGLFrame.add(canvas);
	    openGLFrame.setSize(1024, 768);
	    animator = new FPSAnimator(canvas,60);
	    openGLFrame.addWindowListener(
	    		new WindowAdapter()
	    		{
	    			public void windowClosing(WindowEvent e)
	    			{
	    				new Thread(
	    						new Runnable() 
	    						{
	    							public void run()
	    							{
	    								System.exit(0);
	    							}
	    						}).start();
	    			}
	    		});
	    visualizer.setDaemon(true);
	    ((Thread) visualizer).start();
	    animator.start();
	    System.out.println("Concurrent Visualizer Initialized Correctly");
	    openGLFrame.setVisible(true);
	}
	
	/**
	 * Updates the maximum value for the {@code JSlider}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * 
	 * @param maxTimeInSeconds the maximum value the slider has
	 */
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

	/**
	 * Updates the current value for the {@code JSlider}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * 
	 * @param maxTimeInSeconds the maximum value the slider has
	 */
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
	
	/**
	 * Enables the {@code JSlider}, such that it can be used.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * <p>
	 * @param maxTimeInSeconds the maximum value the slider has
	 */
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
	
	/**
	 * Disables the {@code JSlider}, such that it cannot be used.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * <p>
	 * @param maxTimeInSeconds the maximum value the slider has
	 */
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
	
	/**
	 * Updates the {@link JTextField} to display the current time value.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * <p>
	 * @param time the current time
	 */
	public void updateTimer(final String time)
	{
		Runnable updateTimer = 
		      	new Runnable()
				{
		        	public void run()
		        	{
		        		songPositionNumber.setText(time+"/"+totalTime);
		        	}
				};
		SwingUtilities.invokeLater(updateTimer);
	}
	
	/**
	 * Enables/Disables {@link #frame}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * <p>
	 * @param enabled
	 */
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
	
	/**
	 * Returns the {@link #visualizer} object.
	 * <p>
	 * @return the {@link #visualizer} object
	 */
	public ConcurrentVisualizer getVisualizer()
	{
		return visualizer;
	}
	
	/**
	 * Pauses the {@link #visualizer}'s {@link #animator}.
	 * <p>
	 * The {@link #animator} is the main object that times the {@link #visualizer}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 */
	public void pauseAnimator()
	{
		Runnable pauseAnimator = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		animator.pause();
	        	}
			};
		SwingUtilities.invokeLater(pauseAnimator);
	}
	
	/**
	 * Resumes the {@link #visualizer}'s {@link #animator}.
	 * <p>
	 * The {@link #animator} is the main object that times the {@link #visualizer}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 */
	public void resumeAnimator()
	{
		Runnable resumeAnimator = 
	      	new Runnable()
			{
	        	public void run()
	        	{
	        		animator.resume();
	        	}
			};
		SwingUtilities.invokeLater(resumeAnimator);
	}
	
	/**
	 * Returns the {@link #loopCheckBox} object, which can be altered.
	 * <p>
	 * This is not a thread-safe call, when changing the object. However,
	 * this object is only read, therefore it is okay.
	 * <p>
	 * @return the checkbox, which states if the playlist should repeat.
	 */
	public JCheckBox getLoopCheckBox()
	{
		return loopCheckBox;
	}

	/**
	 * Selects or deselect the {@link GUI#playPauseButton}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * <p>
	 * @param isSelected true to select the {@link #playPauseButton}, false to deselect
	 */
	public void setSelectedJToggleButton(final boolean isSelected )
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
	
	/**
	 * Sets the text for the {@link GUI#playPauseButton}.
	 * <p>
	 * This is done through a call to the {@link SwingUtilities},
	 * which queues an update thread. This is needed for updates to
	 * {@link JFrame} to be thread-safe.
	 * <p>
	 * @param text the text to update the {@link GUI#playPauseButton}
	 */
	public void setTextJToggleButton(final String text )
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
	
	/**
	 * Returns the object, which represents the playlist.
	 * <p>
	 * @return {@link #playList}
	 */
	public JList getPlaylist()
	{
		return playList;
	}
	
	/**
	 * Updates the total time displayed. This does not update the {@link #frame}.
	 * To update the {@link #frame}, call {@link #updateTimer(String)}.
	 * <p>
	 * @param time the total time
	 */
	public void updateTotalTime( String time )
	{
		totalTime = time;
	}	
}
