package timer;
/* 
 * Graphic Engine
 * Copyright © 2004-2010 Jérôme JOUVIE (Jouvieje)
 * 
 * PROJECT INFORMATIONS
 * ====================
 * Author   Jérôme JOUVIE (Jouvieje)
 * Email    jerome.jouvie@gmail.com
 * Site     http://jerome.jouvie.free.fr/
 * Homepage http://jerome.jouvie.free.fr/OpenGl/Projects/GraphicEngineCore.php
 * Version  GraphicEngineCore 1.1.2 Build 28/02/2010
 * 
 * LICENSE
 * =======
 * 
 * GNU LESSER GENERAL PUBLIC LICENSE (LGPL)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
 * <H2></H2>
 * <HR>
 * Description goes here. If you see this message, please contact me and the description will be filled.<BR>
 * <BR>
 * @author    Jérôme JOUVIE (Jouvieje)
 * @site      http://jerome.jouvie.free.fr/
 * @mail      jerome.jouvie@gmail.com
 * @project   Graphic Engine Core API
 * @version   1.1.2
 * @homepage  http://jerome.jouvie.free.fr/OpenGl/Projects/GraphicEngineCore.php
 * @copyright 2004-2010 Jérôme JOUVIE (Jouvieje)
 */
public class FPSTimer implements ITimer {
	/* Enable the counter */
	private boolean enabled = true;

	/* Time passed calculation */
	private long timePassedNanos = 0;
	private long lastTime = -1;		//Previous time

	private long accumulatedTimeNanos = 0;
	
	/* FPS calculation */
	private float fps = 60;
	private int frames = 0;
	private long firstFrameTime = 0;
	public int fpsRefreshTimeNanos = 500 * 1000 * 1000;	//each 500ms

	public FPSTimer() {
		setEnabled(true);
	}

	/* Timer interface */

	public final long getTimeNanos() {
		return System.nanoTime();
	}

	public final long getTimeMicros() {
		return getTimeNanos() / 1000;
	}

	public final long getTimeMillis() {
		return getTimeNanos() / 1000000;
	}

	/* Counter interface */

	/**
	 * This method calculates the time that OpenGl takes to draw frames.
	 */
	public void update() {
		//Counter enabled ?
		if(!enabled) return;

		if(lastTime == -1) {
			//Initialization of the counter
			lastTime = getTimeNanos();
			timePassedNanos = 0;

			//Initialization for FPS calculation
			fps = 0;
			frames = 0;
			firstFrameTime = lastTime;
		}
		else {
			//Get the current time
			long currentTime = getTimeNanos();
			//Time passed
			timePassedNanos = currentTime-lastTime;
			//Update last time, it is now the current for next frame calculation
			lastTime = currentTime;
			//Accumulate time
			accumulatedTimeNanos += timePassedNanos;

			//FPS
			frames++;

			//Calculate fps
			long dt = currentTime-firstFrameTime;
			if(dt >= fpsRefreshTimeNanos) {
				fps = (float)(1000*frames)/(float)(dt / 1000000);
				frames = 0;
				firstFrameTime = currentTime;
			}
		}
	}

	/**
	 * Get the time to draw last frame
	 * @return the time in milliseconds that the last frame takes to be drawn
	 */
	public final long getTimePassedMillis() {
		return timePassedNanos / 1000000;
	}

	public final long getTimePassedMicros() {
		return timePassedNanos / 1000;
	}

	public final long getTimePassedNanos() {
		return timePassedNanos;
	}

	/**
	 * @return the number of frames per seconds
	 */
	public final float getFPS() {
		return fps;
	}

	/**
	 * Enable the counter
	 * @param enable
	 * @see #getTimePassedMillis()
	 */
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}
	/**
	 * @return a boolean value that define the frame's time to be drawn is calculated or not
	 * @see #getTimePassedMillis()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Stop the counter and
	 */
	public void reset() {
		//reset time

		lastTime = -1;
		timePassedNanos = 0;
		accumulatedTimeNanos = 0;

		fps = 0;
		frames = 0;
		firstFrameTime = lastTime;
	}
}
