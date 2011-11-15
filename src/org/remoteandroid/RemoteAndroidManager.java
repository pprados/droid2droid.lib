package org.remoteandroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.remoteandroid.ListRemoteAndroidInfo.DiscoverListener;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import dalvik.system.DexClassLoader;


/**
 * The startup class to use Remote android.
 * This class manage all remotes devices.
 * The classical usage it to get an instance, discover remote android, and bind to it.
 *  
 * @author Philippe PRADOS
 *
 */
public abstract class RemoteAndroidManager 
{
	/** The network socket default port. 
	 * @since 1.0
	 */
	public static final int DEFAULT_PORT=19876;

	/** The bootstrap instance. 
	 * @since 1.0
	 */
	private static RemoteAndroidManager sSingleton;
    private static ClassLoader sClassLoader;


    /** Permission to receive a broadcast discover. 
	 * @since 1.0
	 */
    public static final String PERMISSION_DISCOVER_SEND="org.remoteandroid.permission.discover.SEND";
    /** Permission to receive a broadcast discover. 
	 * @since 1.0
	 */
    public static final String PERMISSION_DISCOVER_RECEIVE="org.remoteandroid.permission.discover.RECEIVE";
    /** Intent action when a remote android is discover. 
	 * @since 1.0
	 */
    public static final String ACTION_DISCOVER_ANDROID="org.remoteandroid.DISCOVER";
    /** Intent action when a remote android is discover. 
	 * @since 1.0
	 */
    public static final String ACTION_REMOTE_ANDROID="org.remoteandroid.service.RemoteAndroid";
    
    /** Intent action to connect to another remote android.
     * @since 1.0
     */
    public static final String ACTION_CONNECT_ANDROID="org.remoteandroid.action.Connect";
    
    /** Boolean extra for accept anonymous connection when use the startActivityForResult() with ACTION_CONNECT_ANDROID 
	 * @since 1.0
	 */
    public static final String EXTRA_ACCEPT_ANONYMOUS="anonymous";
    
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
    public static final int FLAG_ACCEPT_ANONYMOUS	=1;
    /** Refuse to connect with bluetooth 
	 * @since 1.0
	 */
    public static final int FLAG_NO_BLUETOOTH		=1 << 1;
    /** Refuse to connect with ethernet 
	 * @since 1.0
	 */
    public static final int FLAG_NO_ETHERNET		=1 << 2;
    
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
     * @param flags Flags to connect to remote android. May be {@link FLAG_ACCEPT_ANONYMOUS}, 
     * {@link FLAG_NO_BLUETOOTH}, {@link FLAG_NO_ETHERNET} or a combination.
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
     * @param timeToDiscover Time in ms to discover devices. May be {@link DISCOVER_INFINITELY}
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
	    	Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=org.remoteandroid"));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			return intent;
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
    public static final int FLAG_LOG_WARN=1<<1;
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
    
    // Current application info

	/**
	 * Return a manager. May be called in {@link android.app.Application}.
	 * 
	 * @param context The application context.
	 * 
	 * @return An instance to use for bind to remote devices.
     * 
	 * @since 1.0
	 */
    public static synchronized RemoteAndroidManager getManager(final Context context)
    {
    	if (sSingleton==null)
    	{
    		// Use the library present in the Remote Android package.
    		// With this approach, it's possible to update the package and all the caller apk.
    		if (USE_SHAREDLIB)
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
		    			sSingleton=(RemoteAndroidManager)sClassLoader.loadClass(BOOTSTRAP_CLASS)
	    					.getConstructor(Context.class).newInstance(context);
						Log.d("test","OK");
					}
					catch (Exception e)
					{
						throw new Error("Install the Remote Android package",e);
					}
				}
//    			sSingleton=(RemoteAndroidManager)getClassLoaderInit().loadClass(BOOTSTRAP_CLASS)
//    				.getConstructor(Context.class).newInstance(context);

    		}
    		else
    		{
    			try
				{
					sSingleton=(RemoteAndroidManager)RemoteAndroidManager.class.getClassLoader().loadClass(BOOTSTRAP_CLASS)
						.getConstructor(Context.class).newInstance(context);
				}
				catch (Exception e)
				{
					throw new Error("Internal error",e);
				}
    		}
    	}
    	return sSingleton;
    }

    private static ClassLoader getClassLoaderSingleton(final Context context) throws Error
	{
		try
		{
    		ClassLoader classLoader=RemoteAndroidManager.class.getClassLoader();
			File dir=context.getApplicationContext().getDir("dexopt", Context.MODE_PRIVATE); 
			final String packageName="org.remoteandroid";
			PackageInfo info=context.getPackageManager().getPackageInfo(packageName, 0/*PackageManager.GET_CONFIGURATIONS*/);
			String jar=info.applicationInfo.dataDir+"/files/"+SHARED_LIB; 
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
	/*package*/ static final boolean USE_SHAREDLIB=false;
	/*package*/static final String SHARED_LIB="sharedlib.jar";

}
