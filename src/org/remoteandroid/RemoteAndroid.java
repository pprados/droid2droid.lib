package org.remoteandroid;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

/**
 * The interface to a remote device.
 * 
 * The classic step are:
 * - Discover Androids or use a specific one
 * - Bind to the remote Android
 * - Propose to install the current apk
 * - Bind to a remote objet
 * - Use it
 * 
 * @author Philippe PRADOS
 *
 */
public interface RemoteAndroid
{
	/** Flag to force to replace an existing APK (For debug purpose). 
	 * @see {@link android.content.pm.PackageManager.INSTALL_REPLACE_EXISTING}
	 * */
	public static final int INSTALL_REPLACE_EXISTING=0x00000002; // PackageManager.INSTALL_REPLACE_EXISTING

	/** Status if the remote user refuse this APK.
	 * @see {@link #pushMe(Context, PublishListener, int, long)}
	 */
	public static final int ERROR_INSTALL_REFUSED=-1;
	
	/** Status if the remote user refuse all remote APK.
	 * The remote use must set "Unknown source" in "Application" parameters
	 * @see {@link #pushMe(Context, PublishListener, int, long)}
	 */
	public static final int ERROR_INSTALL_REFUSE_FOR_UNKNOW_SOURCE=-2;
	
	/**
	 * Listener of install process.
	 * 
	 * @author Philippe PRADOS
	 *
	 */
	public static interface PublishListener
	{
		/**
		 * It's necessary to update or install the version present in the remote android.
		 * Ask to user if he/she accept to upload the application. May be refused if use GSM network.
		 * 
		 * @return <code>true</code> if accept.
		 */
		public boolean askIsPushApk();
		
		/**
		 * Inform the progress of the download.
		 * 
		 * @param progress A number between 0 and 100
		 */
		public void onProgress(int progress);
		
		/**
		 * If an error is detected.
		 * 
		 * @param e The exception
		 */
		public void onError(Throwable e);
		
		/**
		 * When the process is finished.
		 * @param status 
		 * 		<0 if the user refuse to install, 
		 * 		0 if the current version is correct, 
		 * 		1 if the application is installed.
		 */
		public void onFinish(int status);
	}
	
	/**
	 * Install my APK in remote android.
	 * 
	 * @param context 			The context
	 * @param listener 			A listener to expose the evolution of the installation process.
	 * @param flags 			Accept zero or INSTALL_REPLACE_EXISTING if you want to force the installation.
	 * @param timeout			The timeout in ms for a user answer to a question.
	 * @throws IOException		It something happens.
	 * @throws RemoteException	If the connection to remote android is broken.
	 */
	public abstract void pushMe(Context context,PublishListener listener,int flags,long timeout) throws IOException,RemoteException;
	
	/**
	 * Set the maximum time out for invoke a remote method before declare it's out.
	 * 
	 * @param bindTimeout The timeout in ms.
	 */
	public abstract void setExecuteTimeout(long bindTimeout);

	/**
	 * Bind a service in remote Android.
	 * 
	 * @see android.content.Context#bindService(Intent, ServiceConnection, int)
	 */
    public abstract boolean bindService(Intent service, ServiceConnection conn, int flags);
    
    /**
     * Close the connection to remote Android.
     */
    public abstract void close();
    
    /**
     * Return informations from remote Android.
     * 
     * @return informations.
     */
    public RemoteAndroidInfo getInfos();
    
    // TODO isClosed
}
