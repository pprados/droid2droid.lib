package org.remoteandroid;

import java.util.List;

/**
 * A container, directely connected to the discover process.
 *  
 * When a new device is detected, the container search if the device is already detected with other technology.
 * Then, add the device and invoke the callback to informe of this new device.
 * 
 * If you detect a device is unreachable or disconnected, you must remove the corresponding instrance.
 * 
 * The discovery process broadcast a specific message to all process in the device. The container catch this kind of message
 * and merge or extend the record before inform the application.
 * 
 * @see {@link RemoteAndroidManager#startDiscover(int, long)}
 * @see {@link RemoteAndroidManager#cancelDiscover()}
 * 
 * @since 1.0
 * @author Philippe Prados
 *
 */
public interface ListRemoteAndroidInfo extends List<RemoteAndroidInfo>
{
	/**
	 * A call back interface to signal a new device is detected.
	 * 
	 * @author pprados
	 *
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
		 * @param update 			<code>false<code> if the record is new. <code>true</code> if the record is updated.
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
	 * close the liste.
	 * 
	 * @since 1.0
	 */
	public void close();
}
