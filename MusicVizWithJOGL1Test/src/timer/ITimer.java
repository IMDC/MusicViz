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
 * <H2>Timer</H2>
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
public interface ITimer
{
	/**
	 * Get the current time, in milliseconds unit.
	 */
	public long getTimeMillis();

	/**
	 * Get the current time, in microseconds unit.
	 */
	public long getTimeMicros();

	/**
	 * Get the current time, in nanoseconds unit.
	 */
	public long getTimeNanos();
	
	/**
	 * Update the counter.<BR>
	 * Must be called each frame.
	 */
	public void update();

	/**
	 * Get the time between two call of update, ie duration of a frame.
	 * @return the time passed in milliseconds
	 * @see #update()
	 */
	public long getTimePassedMillis();

	/**
	 * Get the current time, in microseconds unit.
	 */
	public long getTimePassedMicros();

	/**
	 * Get the current time, in nanoseconds unit.
	 */
	public long getTimePassedNanos();

	/**
	 * Count the number of call of update (ie number of frames frame) per second
	 * @return the number of frames per seconds
	 * @see #update()
	 */
	public float getFPS();
}
