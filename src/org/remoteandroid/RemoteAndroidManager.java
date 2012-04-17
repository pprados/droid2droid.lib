package org.remoteandroid;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.remoteandroid.ListRemoteAndroidInfo.DiscoverListener;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.os.Parcelable;
import dalvik.system.DexClassLoader;


/**
 * The startup class to use Remote android.
 * This is an asynchronous framework.
 * This class manage all remotes devices.
 * The classical usage is:
 * - bind to a RemoteAndroidManager
 * - discover remote androids
 * - bind to a RemoteAndroid
 * - bind to a remote instance
 * - invoke aidl methods
 *  
 * @author Philippe PRADOS
 *
 */
public abstract class RemoteAndroidManager implements Closeable
{
	/**
	 * Listener to manage the life cycle of remote android manager.
	 * @since 1.0
	 */
	public static interface ManagerListener
	{
		/**
		 * Remote android manager was binded.
		 * @param manager The manager.
		 * @since 1.0
		 */
		void bind(RemoteAndroidManager manager);
		/**
		 * Remote android manager was disconnected.
		 * @param manager The manager.
		 * @since 1.0
		 */
		void unbind(RemoteAndroidManager manager);
	}
	
	/** The network socket default port. 
	 * @since 1.0
	 */
	public static final int DEFAULT_PORT=19876;

	/** The bootstrap instance. 
	 * @since 1.0
	 */
    private static ClassLoader sClassLoader;


    /** Permission to receive a broadcast discover. 
	 * @since 1.0
	 */
    public static final String PERMISSION_DISCOVER_SEND="org.remoteandroid.permission.discover.SEND";
    /** Permission to receive a broadcast discover. 
	 * @since 1.0
	 */
    public static final String PERMISSION_DISCOVER_RECEIVE="org.remoteandroid.permission.discover.RECEIVE";
    /** Intent action when start remote android service. 
	 * @since 1.0
	 */
    public static final String ACTION_START_REMOTE_ANDROID="org.remoteandroid.START";
    /** Intent action when start remote android service. 
	 * @since 1.0
	 */
    public static final String ACTION_STOP_REMOTE_ANDROID="org.remoteandroid.STOP";
    /** Intent action when a remote android is discover. 
	 * @since 1.0
	 */
    public static final String ACTION_DISCOVER_ANDROID="org.remoteandroid.DISCOVER";
    /** Intent action when a remote android is discover.
	 * @since 1.0
	 */
    public static final String ACTION_BIND_REMOTE_ANDROID="org.remoteandroid.service.RemoteAndroidBinder";
    
    /** Intent action to connect to another remote android.
     * @since 1.0
     */
    public static final String ACTION_CONNECT_ANDROID="org.remoteandroid.action.Connect";
    
    /** Boolean extra for accept anonymous connection when use the startActivityForResult() with ACTION_CONNECT_ANDROID 
	 * @since 1.0
	 */
    public static final String EXTRA_ACCEPT_ANONYMOUS="anonymous";
    
    /** 
     * Flags to connect for {@link ACTION_CONNECT_ANDROID}. May be {@link FLAG_PROPOSE_PAIRING}.
     */
    public static final String EXTRA_FLAGS			= "flags";
    
    /** 
     * Use current application icon for {@link ACTION_CONNECT_ANDROID}. 
     */
	public static final String	EXTRA_ICON_ID		= "icon.id";
	/** 
	 * Use this title for {@link ACTION_CONNECT_ANDROID}.
     */
	public static final String	EXTRA_TITLE			= "title";
	/** 
	 * Use this title for {@link ACTION_CONNECT_ANDROID}.
     */
	public static final String	EXTRA_SUBTITLE		= "subtitle";
	/** 
	 * Use this standard theme for {@link ACTION_CONNECT_ANDROID}.
	 * May be :
	 * <ul>
	 * <li>{@link android.R.style.Theme_Black}
	 * <li>{@link android.R.style.Theme_Holo}
	 * <li>{@link android.R.style.Theme_DeviceDefault}
	 * <li>{@link android.R.style.Theme_Black_NoTitleBar}
	 * <li>{@link android.R.style.Theme_Black_NoTitleBar_Fullscreen}
	 * <li>{@link android.R.style.Theme_NoTitleBar}
	 * <li>{@link android.R.style.Theme_NoTitleBar_Fullscreen}
	 * <li>{@link android.R.style.Theme_NoTitleBar_OverlayActionModes}
	 * <li>{@link android.R.style.Theme_DeviceDefault_NoActionBar}
	 * <li>{@link android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen}
	 * <li>{@link android.R.style.Theme_Light}
	 * <li>{@link android.R.style.Theme_Holo_Light}
	 * <li>{@link android.R.style.Theme_DeviceDefault_Light}
	 * <li>{@link android.R.style.Theme_Holo_Light_DarkActionBar}
	 * <li>{@link android.R.style.Theme_DeviceDefault_Light_DarkActionBar}
	 * <li>{@link android.R.style.Theme_Holo_Light_NoActionBar}
	 * <li>{@link android.R.style.Theme_DeviceDefault_Light_NoActionBar}
	 * <li>{@link android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen}
	 * <li>{@link android.R.style.Theme_Light_NoTitleBar}
	 * </ul>
     */
	public static final String	EXTRA_THEME_ID		= "theme.id";
    
    /** Extra in intent to get the URL of the remote android. 
	 * @since 1.0
	 */
    public static final String EXTRA_DISCOVER="discover";
    
    /** Extra in intent is device is updated (bonded, undetected, ...)
	 * @since 1.0
	 */
    public static final String EXTRA_UPDATE="update";

    /** Extra in intent to get the URL of the remote android. 
	 * @since 1.0
	 */
//FIXME    public static final String EXTRA_REMOVE="remove";
    /** Intent action when a remote android is discover. 
	 * @since 1.0
	 */
    public static final String ACTION_START_DISCOVER_ANDROID="org.remoteandroid.START_DISCOVER";
    /** Intent action when a remote android is discover. 
	 * @since 1.0
	 */
    public static final String ACTION_STOP_DISCOVER_ANDROID="org.remoteandroid.STOP_DISCOVER";

    /** 
     * The delay to discover remote android infinitely.
     * @see {@link #bindRemoteAndroid(Intent, ServiceConnection, int)}
	 * @since 1.0
	 */
    public static final long DISCOVER_INFINITELY=Long.MAX_VALUE;
    public static final long DISCOVER_BEST_EFFORT=Long.MAX_VALUE-1;

    /** Propose pairing during the connection process.
     * @since 1.0
     */
    public static final int FLAG_PROPOSE_PAIRING	=1 << 0;

    /** 
     * Flag to accept anonymous connection.
     *  
     * <p>Then, the bluetooth process start to discover all visible bluetooth devices, and try to connection anonymously 
     * to each one and read the {@link RemoteAndroidInfo}. Else, try to connect only to the pairing devices.</p>
     * 
     * <p>The IP process, broadcast an UDP to discover remote device in the same sub network.
     * Then, wait to receive the {@link RemoteAndroidInfo}.</p>
     * 
	 * @since 1.0
	 */
    public static final int FLAG_ACCEPT_ANONYMOUS	=1 << 1;
    
    /** Refuse to connect with bluetooth 
	 * @since 1.0
	 */
    public static final int FLAG_NO_BLUETOOTH		=1 << 2;
    
    /** Refuse to connect with ethernet 
	 * @since 1.0
	 */
    public static final int FLAG_NO_ETHERNET		=1 << 3;
    

    /**
     * Uri to create a bitmap with QRcode.
     * <pre>
     * InputStream in=getContentResolver()
	 *  .openTypedAssetFileDescriptor(RemoteAndroidManager.QRCODE_URI, "image/png", null)
	 *  .createInputStream();
	 *  if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
	 *  {
	 *	  in=getContentResolver()
	 *		 .openTypedAssetFileDescriptor(RemoteAndroidManager.QRCODE_URI, "image/png", null)
	 *		.createInputStream();
	 *	}
	 *	else
	 *	{
	 *	  in=getContentResolver().openInputStream(RemoteAndroidManager.QRCODE_URI);
	 *	}
	 *  Bitmap bitmap=BitmapFactory.decodeStream(in);
	 * in.close();
	 * </pre>
     * @since 1.0
     */
	public static final Uri QRCODE_URI=Uri.parse("content://org.remoteandroid/qrcode");
    /**
     * Mime type for QRCODE_URI.
     * @since 1.0
     */
	public static final String QRCODE_MIME_TYPE="image/png";
    /**
     * Return the version of this library.
     * 
     * @return The version number.
     * 
     * @since 1.0
     */
    public abstract int getVersion();
    /**
     * Return the context.
     * 
     * @return The associated application context.
     * 
	 * @since 1.0
	 */
    public abstract Context getContext();
    
    /**
     * Bind to a remote android.
     * 
     * @param service An intent with an URL with all the information to connect to a remote device.
     * @param conn The {@link ServiceConnection connection manager}. 
     * 	The method {@link ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder) onServiceConnected} 
     *  receive a binder. You must cast it to {@link RemoteAndroid} and use it.
     * @param flags Flags to connect to remote android. May be {@link FLAG_PROPOSE_PAIRING}.
     * @return True if the binding process is started.
     * 
	 * @since 1.0
	 */
    public abstract boolean bindRemoteAndroid(Intent service, ServiceConnection conn, int flags);
    
    /**
     * Start the discovery process. 
     * 
     * You must have the Remote Android service in the device to use this method.
     * 
     * @param flags Flags to connect to remote android. May be {@link FLAG_ACCEPT_ANONYMOUS}, 
     * @param timeToDiscover Time in ms to discover devices. 
     * * May be {@link DISCOVER_INFINITELY}, {@link DISCOVER_BEST_EFFORT} 
     * {@link FLAG_NO_BLUETOOTH}, {@link FLAG_NO_ETHERNET} or a combination.
     * 
	 * @since 1.0
	 */
    // TODO: garder l'historique des start, et gérer les deconnexions des apps.
    public abstract void startDiscover(int flags,long timeToDiscover);
    
    /**
     * Cancel the current discovery process.
     * 
	 * @since 1.0
	 */
    public abstract void cancelDiscover();
    
    /**
     * Return true if the manager is currently in the device discovery process.
     * 
	 * @since 1.0
	 */
    public abstract boolean isDiscovering();
    
    /**
     * Return the local android infos.
     * 
     * @return The infos.
     * 
	 * @since 1.0
	 */
    public abstract RemoteAndroidInfo getInfos();
    
    /**
     * Return the bounded devices
     * 
     * @return A list with bounded devices. It's possible to register a listener to be informed when the device 
     * 			IP address is detected or others updateds informations.
     * 
	 * @since 1.0
	 */
    public abstract ListRemoteAndroidInfo getBoundedDevices();
    
    /**
     * Create a List&lt;RemoteAndroidInfo&gt; connected to the discovery process.
     * @see {@link ListRemoteAndroidInfo}
     * 
     * @param callback The callback to use to inform a new device is detected in main thread.
     * @return An instance of DiscoveredAndroids container.
     * 
	 * @since 1.0
	 */
    public abstract ListRemoteAndroidInfo newDiscoveredAndroid(DiscoverListener callback);
    
    /**
     * Return intent for download Remote Android from the Google Market.
     * <pre>
     * startActivity(remoteAndroidManager.getIntentForMarket());
     * </pre>
     * @param context The context
     * @return The intent for start activity.
     * 
	 * @since 1.0
	 */
    public static Intent getIntentForMarket(Context context)
    {
    	try
    	{
    		context.getPackageManager().getApplicationInfo("org.remoteandroid",0);
    		return null;
    	}
    	catch (NameNotFoundException e)
    	{
	    	return new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=org.remoteandroid"))
	    		.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	}
    }
    
    /**
     * Set Log for local and RemoteAndroid.apk.
     * 
     * @param type Bit mask FLAG_LOG_ERROR, FLAG_LOG_WARN, FLAG_LOG_INFO, FLAG_LOG_DEBUG, FLAG_LOG_VERBOSE or FLAG_LOG_ALL.
     * @param state true or false
     */
    @Deprecated
    public abstract void setLog(int type,boolean state);
    /** Flag for set/unset error log. */
    @Deprecated
    public static final int FLAG_LOG_ERROR=1<<0;
    /** Flag for set/unset warn log. */
    @Deprecated
    public static final int FLAG_LOG_WARN=1<<1;// Current application info
    /** Flag for set/unset info log. */
    @Deprecated
    public static final int FLAG_LOG_INFO=1<<2;
    /** Flag for set/unset debug log. */
    @Deprecated
    public static final int FLAG_LOG_DEBUG=1<<3;
    /** Flag for set/unset verbose log. */
    @Deprecated
    public static final int FLAG_LOG_VERBOSE=1<<4;
    /** Flag for set/unset all logs. */
    @Deprecated
    public static final int FLAG_LOG_ALL=FLAG_LOG_ERROR|FLAG_LOG_WARN|FLAG_LOG_INFO|FLAG_LOG_DEBUG|FLAG_LOG_VERBOSE;
    
    /**
     * Close the manager.
     * 
	 * @since 1.0
     */
    public abstract void close();
    
    public abstract NdefMessage createNdefMessage();
    public abstract RemoteAndroidInfo parseNfcRawMessages(Context context,Parcelable[] rawMessage);

    /**
     * Bind to a RemoteAndroidManager.
     * 
     * @param context The context.
     * @param listener The listener.
     * 
	 * @since 1.0
     */
    public static void bindManager(final Context context,final ManagerListener listener)
    {
		// Use the library present in the Remote Android package.
		// With this approach, it's possible to update the package and all the caller apk.
		if (USE_SHAREDLIB) // FIXME: Must be validated with Parcelable objet
		{
			// Strict mode
			final Object lock=new Object();
			synchronized (lock)
			{
				new Thread()
				{
					@Override
					public void run() 
					{
						try
						{
							sClassLoader=getClassLoaderSingleton(context);
    		    			synchronized (lock)
							{
        						lock.notify();
							}
						}
						catch (Exception e)
						{
							throw new Error("Install the Remote Android package",e);
						}
					}
				}.start();
    			try
				{
					lock.wait();
					RemoteAndroidManager.class.getClassLoader().loadClass(BOOTSTRAP_CLASS)
							.getMethod(BOOTSTRAP_METHOD, Context.class,RemoteAndroidManager.ManagerListener.class).invoke(null, context,listener);
					
				}
				catch (Exception e)
				{
					throw new Error("Install the Remote Android package",e);
				}
			}
		}
		else
		{
			try
			{
				RemoteAndroidManager.class.getClassLoader().loadClass(BOOTSTRAP_CLASS)
						.getMethod("bootStrap", Context.class,RemoteAndroidManager.ManagerListener.class).invoke(null, context,listener);
			}
			catch (Exception e)
			{
				throw new Error("Internal error",e);
			}
		}
    }

    private static ClassLoader getClassLoaderSingleton(final Context context) throws Error
	{
		try
		{
    		ClassLoader classLoader=RemoteAndroidManager.class.getClassLoader();
			File dir=context.getApplicationContext().getDir("dexopt", Context.MODE_PRIVATE); 
			final String packageName="org.remoteandroid";
			PackageInfo info=context.getPackageManager().getPackageInfo(packageName, 0/*PackageManager.GET_CONFIGURATIONS*/);
			String jar=info.applicationInfo.dataDir+"/files/"+SHARED_LIB+".jar";
			InputStream in=new FileInputStream(jar); in.read(); in.close(); // Check if is readable
			classLoader=
				new DexClassLoader(jar,
						dir.getAbsoluteFile().getAbsolutePath(),null,
						classLoader
						);
			return classLoader;
		}
		catch (Exception e)
		{
			throw new Error("Install the Remote Android package",e);
		}
	}
    
    /** Bootstrap implementation. */
    private static final String BOOTSTRAP_CLASS="org.remoteandroid.internal.RemoteAndroidManagerImpl";
    private static final String BOOTSTRAP_METHOD="bootStrap";
	/*package*/ static final boolean USE_SHAREDLIB=false;
	/*package*/static final String SHARED_LIB="sharedlib";

}
