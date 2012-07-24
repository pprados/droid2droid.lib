/******************************************************************************
 *
 * droid2droid - Distributed Android Framework
 * ==========================================
 *
 * Copyright (C) 2012 by Atos (http://www.http://atos.net)
 * http://www.droid2droid.org
 *
 ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
******************************************************************************/
package org.droid2droid;

import java.util.List;

/**
 * A container, directly connected to the discover process.
 *  
 * When a new device is detected, the container search if the device is already 
 * detected with other technology.
 * Then, add the device and invoke the callback to inform of this new device.
 * 
 * If you detect a device is unreachable or disconnected, you must remove the corresponding instance.
 * 
 * The discovery process broadcast a specific message to all process in the device. 
 * The container catch this kind of message
 * and merge or extend the record before inform the application.
 * 
 * @see {@link Droid2DroidManager#startDiscover(int, long)}
 * @see {@link Droid2DroidManager#cancelDiscover()}
 * 
 * @since 1.0
 * @author Philippe PRADOS
 *
 */
public interface ListRemoteAndroidInfo extends List<RemoteAndroidInfo>
{
	/**
	 * A call back interface to signal a new device is detected.
	 * 
	 * @since 1.0
	 */
	public interface DiscoverListener
	{
		/**
		 * Called when the discovery process start.
	     * 
		 * @since 1.0
		 */
		public void onDiscoverStart();
		
		/**
		 * Called when the discovery process stop.
	     * 
		 * @since 1.0
		 */
		public void onDiscoverStop();
		
		/**
		 * Called when a new device is detected.
		 * 
		 * @param remoteAndroidInfo The information of the remote android.
		 * @param update <code>false<code> if the record is new. <code>true</code> if the record is updated.
	     * 
		 * @since 1.0
		 */
		void onDiscover(RemoteAndroidInfo remoteAndroidInfo,boolean update);
	}

	/**
	 * Register a listener.
	 * 
	 * @param listener The listener invoked in the UI thread when a new device is detected or an parameter is updated.
     * 
	 * @since 1.0
	 */
	public void setListener(DiscoverListener listener);

	/**
	 * Close the list.
	 * 
	 * @since 1.0
	 */
	public void close();
}
