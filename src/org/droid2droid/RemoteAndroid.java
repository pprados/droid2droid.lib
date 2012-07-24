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

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

/**
 * The interface to a remote device.
 * 
 * The classic step are:
 * - Discover Androids&#8482; or use a specific one
 * - Bind to the remote Android&#8482;
 * - Propose to install the current APK
 * - Bind to a remote object
 * - Use it
 * 
 * @author Philippe PRADOS
 * @since 1.0 
 */
public interface RemoteAndroid
{
	/** Flag to force to replace an existing APK (For debug purpose). 
	 * @see {@link android.content.pm.PackageManager.INSTALL_REPLACE_EXISTING}
	 * @since 1.0 
	 */
	public static final int INSTALL_REPLACE_EXISTING=0x00000002;

	/** Status if the remote user refuse this APK.
	 * @see {@link #pushMe(Context, PublishListener, int, long)}
	 * @since 1.0 
	 */
	public static final int ERROR_INSTALL_REFUSED=-1;
	
	/** Status if the remote user refuse all remote APK.
	 * The remote use must set "Unknown source" in "Application" parameters
	 * @see {@link #pushMe(Context, PublishListener, int, long)}
	 * @since 1.0 
	 */
	public static final int ERROR_INSTALL_REFUSE_FOR_UNKNOW_SOURCE=-2;
	
	/**
	 * Listener of install process.
	 * 
	 * @author Philippe PRADOS
	 * @since 1.0 
	 */
	public static interface PublishListener
	{
		/**
		 * It's necessary to update or install the version present in the remote Android&#8482;.
		 * Ask to user if he/she accept to upload the application. May be refused if use GSM network.
		 * 
		 * @return <code>true</code> if accept.
		 * @since 1.0 
		 */
		public boolean askIsPushApk();
		
		/**
		 * Inform the progress of the download.
		 * 
		 * @param progress A number between 0 and 100.
		 * @since 1.0 
		 */
		public void onProgress(int progress);
		
		/**
		 * If an error is detected during the installation process.
		 * 
		 * @param e The exception
		 * @since 1.0 
		 */
		public void onError(Throwable e);
		
		/**
		 * When the process is finished.
		 * @param status 
		 * 		<0 if the user refuse to install, 
		 * 		0 if the current version is correct, 
		 * 		1 if the application is installed.
		 * @since 1.0 
		 */
		public void onFinish(int status);
	}
	
	/**
	 * Install current APK in remote Android&#8482;.
	 * 
	 * @param context 			The context
	 * @param listener 			A listener to expose the evolution of the installation process.
	 * @param flags 			Accept zero or {@link INSTALL_REPLACE_EXISTING} if you want to force the installation.
	 * @param timeout			The timeout in milliseconds for a user answer to a question.
	 * @throws IOException		It something happens.
	 * @throws RemoteException	If the connection to Droid2Droid is broken.
	 * @since 1.0 
	 */
	public abstract void pushMe(Context context,PublishListener listener,int flags,long timeout) throws IOException,RemoteException;
	
	/**
	 * Set the maximum time out for invoke a remote method before declare it's out.
	 * 
	 * @param bindTimeout The timeout in milliseconds.
	 * @since 1.0 
	 */
	public abstract void setExecuteTimeout(long bindTimeout);

	/**
	 * Bind a service in remote Android&#8482; with Droid2Droid activated.
	 * 
	 * @see android.content.Context#bindService(Intent, ServiceConnection, int)
	 * @since 1.0 
	 */
    public abstract boolean bindService(Intent service, ServiceConnection conn, int flags);
    
    /**
     *  Unbind a service in remote Android&#8482; with Droid2Droid activated.
     * 
     * @see android.content.Context#unbindService(ServiceConnection)
	 * @since 1.0 
     */
    public abstract boolean unbindService(ServiceConnection conn);
    
    /**
     * Close the connection to remote Android&#8482;.
	 * @since 1.0 
     */
    public abstract void close();
    
    /**
     * Return informations from remote Android&#8482;.
     * 
     * @return informations.
	 * @since 1.0 
     */
    public RemoteAndroidInfo getInfos();

    /**
     * Check if the connection is closed.
     * @return <code>true</code> if the connection is closed.
	 * @since 1.0 
     */
    boolean isClosed(); 

}
