package Listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

/**
 * Only used by the ThreadPreprocessor. Every time setProgress() is called in the swing worker,
 * a PropertyChangeEvent is fired and the bar is updated.
 * 
 * @author Michael Pouris
 *
 */
public class PreprocessorPropertyChangeListener implements PropertyChangeListener
{
	JProgressBar bar;
	public PreprocessorPropertyChangeListener(JProgressBar bar)
	{
		this.bar= bar;
	}
	
	public void propertyChange(PropertyChangeEvent evt) 
	{
        if ("progress" == evt.getPropertyName()) 
        {
            int progress = (Integer) evt.getNewValue();
            bar.setValue(progress);
        } 
	}

}
