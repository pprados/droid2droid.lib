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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.droid2droid.ListRemoteAndroidInfo.DiscoverListener;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.nfc.NdefMessage;
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
@TargetApi(9)
public abstract class Droid2DroidManager implements Closeable
{
	/**
	 * Listener to manage the life cycle of Droid2Droid manager.
	 * 
	 * @since 1.0
	 */
	public static interface ManagerListener
	{
		/**
		 * Droid2Droid manager was binded.
		 * @param manager The manager.
		 * 
		 * @since 1.0
		 */
		void bind(Droid2DroidManager manager);
		/**
		 * Droid2Droid manager was disconnected.
		 * @param manager The manager.
		 * 
		 * @since 1.0
		 */
		void unbind(Droid2DroidManager manager);
	}
	
	/** The network socket default port. 
	 * 
	 * @since 1.0
	 */
	public static final int DEFAULT_PORT=19876;

	/** The bootstrap instance. 
	 * 
	 * @since 1.0
	 */
    private static ClassLoader sClassLoader;


    /** Permission to send a broadcast discover. 
     * 
	 * @since 1.0
	 */
    public static final String PERMISSION_DISCOVER_SEND="org.droid2droid.permission.discover.SEND";
    
    /** Permission to receive a broadcast discover. 
     * 
	 * @since 1.0
	 */
    public static final String PERMISSION_DISCOVER_RECEIVE="org.droid2droid.permission.discover.RECEIVE";
    
    /** Intent action when start Droid2Droid service. 
     * 
	 * @since 1.0
	 */
    public static final String ACTION_START_REMOTE_ANDROID="org.droid2droid.START";
    
    /** Intent action when stop Droid2Droid service. 
     * 
	 * @since 1.0
	 */
    public static final String ACTION_STOP_REMOTE_ANDROID="org.droid2droid.STOP";
    
    /** Intent action when a Droid2Droid is discover. 
     * 
	 * @since 1.0
	 */
    public static final String ACTION_DISCOVER_ANDROID="org.droid2droid.DISCOVER";
    
    /** Intent action to bind a Droid2Droid.
     * 
	 * @since 1.0
	 */
    public static final String ACTION_BIND_REMOTE_DROID2DROID="org.droid2droid.service.RemoteAndroidBinder";
    
    /** Intent action to connect to another Droid2Droid.
     * 
     * @see {@link #EXTRA_THEME_ID}
     * @see {@link #EXTRA_ICON_ID}
     * @see {@link #EXTRA_TITLE}
     * @see {@link #EXTRA_SUBTITLE}
     * @see {@link #EXTRA_FLAGS}
     * @since 1.0
     */
    public static final String ACTION_CONNECT_ANDROID="org.droid2droid.action.Connect";
    
    /** 
     * Flags to connect for {@link ACTION_CONNECT_ANDROID}. 
     * May be {@link FLAG_PROPOSE_PAIRING}, {@link FLAG_FORCE_PAIRING}
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
    
    /** Extra in intent to get the URL of the Droid2Droid. 
     * 
     * @see {@link #ACTION_DISCOVER_ANDROID}
	 * @since 1.0
	 */
    public static final String EXTRA_DISCOVER="discover";
    
    /** Extra in intent is device is updated (bonded, undetected, ...)
     * 
     * @see {@link #ACTION_DISCOVER_ANDROID}
	 * @since 1.0
	 */
    public static final String EXTRA_UPDATE="update";

    /** Intent action when a Droid2Droid is discover. 
     * 
     * @see {@link #EXTRA_DISCOVER}
     * @see {@link #EXTRA_UPDATE}
	 * @since 1.0
	 */
    public static final String ACTION_START_DISCOVER_ANDROID="org.droid2droid.START_DISCOVER";
    
    /** Intent action when a Droid2Droid is discover. 
     * 
	 * @since 1.0
	 */
    public static final String ACTION_STOP_DISCOVER_ANDROID="org.droid2droid.STOP_DISCOVER";

    /** 
     * The delay to discover Droid2Droid infinitely.
     * 
     * @see {@link #startDiscover(int, long)}
     * 
	 * @since 1.0
	 */
    public static final long DISCOVER_INFINITELY=Long.MAX_VALUE;
    
    /** 
     * The delay to discover Droid2Droid during normal delay.
     * 
     * @see {@link #startDiscover(int, long)}
     * 
	 * @since 1.0
	 */
    public static final long DISCOVER_BEST_EFFORT=Long.MAX_VALUE-1;

    /** Propose pairing during the connection process.
     * 
     * @see {@link Droid2DroidManager#bindRemoteAndroid(Intent, ServiceConnection, int)}
     * 
     * @since 1.0
     */
    public static final int FLAG_PROPOSE_PAIRING	=1 << 0;

    /** Pair device even if the remote device accept anonymous.
     * 
     * @see {@link Droid2DroidManager#bindRemoteAndroid(Intent, ServiceConnection, int)}
     * 
     * @since 1.0
     */
    public static final int FLAG_FORCE_PAIRING	=1 << 1;

    /** Pair device.
     * 
     * @see {@link Droid2DroidManager#bindRemoteAndroid(Intent, ServiceConnection, int)}
     * 
     * @since 1.0
     */
    public static final int FLAG_REMOVE_PAIRING	=1 << 2;

    /** 
     * Flag to accept anonymous connection.
     *  
     * <p>The IP process, broadcast an UDP to discover remote device in the same sub network.
     * Then, wait to receive the {@link RemoteAndroidInfo}.</p>
     * 
     * @see {@link #startDiscover(int, long)}
     * 
	 * @since 1.0
	 */
    /*
    * <p>Then, the bluetooth process start to discover all visible bluetooth devices, and try to connection anonymously 
    * to each one and read the {@link RemoteAndroidInfo}. Else, try to connect only to the pairing devices.</p>
    */ 
    public static final int FLAG_ACCEPT_ANONYMOUS	=1 << 3;
    
    /** Refuse to connect with Bluetooth.
     * @see {@link #startDiscover(int, long)}
     * 
	 * @since 1.0
	 */
    public static final int FLAG_NO_BLUETOOTH		=1 << 4;
    
    /** Refuse to connect with Ethernet.
     * @see {@link #startDiscover(int, long)}
     * 
	 * @since 1.0
	 */
    public static final int FLAG_NO_ETHERNET		=1 << 5;
    
    /**
     * Uri to create a bitmap with QRcode.
     * <pre>
     * InputStream in=getContentResolver()
	 *   .openTypedAssetFileDescriptor(RemoteAndroidManager.QRCODE_URI, "image/png", null)
	 *   .createInputStream();
	 * if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
	 * {
	 *   in=getContentResolver()
	 *		 .openTypedAssetFileDescriptor(RemoteAndroidManager.QRCODE_URI, "image/png", null)
	 *		.createInputStream();
	 * }
	 * else
	 * {
	 *   in=getContentResolver().openInputStream(RemoteAndroidManager.QRCODE_URI);
	 * }
	 * Bitmap bitmap=BitmapFactory.decodeStream(in);
	 * in.close();
	 * </pre>
	 * 
     * @since 1.0
     */
	public static final Uri QRCODE_URI=Uri.parse("content://org.droid2droid/qrcode");
	
    /**
     * Mime type for QRCODE_URI.
     * 
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
     * Bind to a remote Android&#8482;.
     * 
     * @param service An intent with an URL with all the information to connect to a remote device.
     * @param conn The {@link ServiceConnection connection manager}. 
     * 	The method {@link ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder) onServiceConnected} 
     *  receive a binder. You must cast it to {@link RemoteAndroid} and use it.
     * @param flags Flags to connect to remote Android&#8482;. 
     * May be {@link FLAG_PROPOSE_PAIRING}, {@link FLAG_FORCE_PAIRING} or a combination.
     * @return True if the binding process is started.
     * 
	 * @since 1.0
	 */
    public abstract boolean bindRemoteAndroid(Intent service, ServiceConnection conn, int flags);
    
    /**
     * Start the discovery process. 
     * 
     * You must have the Droid2Droid service in the device to use this method.
     * 
     * @param flags Flags to connect to remote Android&#8482;. 
     * May be {@link FLAG_ACCEPT_ANONYMOUS}, {@link FLAG_PROPOSE_PAIRING}, {@link FLAG_FORCE_PAIRING}
     * {@link FLAG_NO_BLUETOOTH}, {@link FLAG_NO_ETHERNET} or a combination.
     * @param timeToDiscover Time in millisecond to discover devices. 
     * May be {@link DISCOVER_INFINITELY} or {@link DISCOVER_BEST_EFFORT} 
     * 
	 * @since 1.0
	 */
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
     * Return the local Android&#8482; informations.
     * 
     * @return The informations.
     * 
	 * @since 1.0
	 */
    public abstract RemoteAndroidInfo getInfos();
    
    /**
     * Return the bonded devices
     * 
     * @return A list with bonded devices. 
     * 			It's possible to register a listener to be informed when the device 
     * 			IP address is detected or others updated informations.
     * 
	 * @since 1.0
	 */
    public abstract ListRemoteAndroidInfo getBondedDevices();
    
    /**
     * Return intent for download Droid2Droid from the Google Play&#8482;.
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
    		context.getPackageManager().getApplicationInfo("org.droid2droid",0);
    		return null;
    	}
    	catch (NameNotFoundException e)
    	{
	    	return new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=org.droid2droid"))
	    		.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	}
    }
    
    /**
     * Set Log for local and Droid2Droid.apk.
     * 
     * @param type Bit mask FLAG_LOG_ERROR, FLAG_LOG_WARN, FLAG_LOG_INFO, FLAG_LOG_DEBUG, FLAG_LOG_VERBOSE or FLAG_LOG_ALL.
     * @param state <code>true</code> or <code>false</code>
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
    @Override
	public abstract void close();
    
    /**
     * Create NDeF message to expose.
     * @return NDefMessage with own RemoteAndroidInfo.
     * 
     * @since 1.0
     */
    public abstract NdefMessage createNdefMessage();

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
    	bootstrap();
    	sFactory.newManager(context,listener);
    }

    /**
     * Create a List&lt;{@link RemoteAndroidInfo}&gt; connected to the discovery process.
     * @see {@link ListRemoteAndroidInfo}
     * 
     * @param context The context.
     * @param callback The callback to use to inform a new device is detected in main thread. 
     * May be <code>null</code>.
     * @return An instance of DiscoveredAndroids container.
     * 
	 * @since 1.0
	 */
    public static ListRemoteAndroidInfo newDiscoveredAndroid(Context context,DiscoverListener callback)
    {
    	bootstrap();
    	return sFactory.newDiscoveredAndroid(context,callback);
    }
    
    // Hack to manage shared library with Android
    private static ClassLoader getClassLoaderSingleton(final Context context) throws Error
	{
		try
		{
    		ClassLoader classLoader=Droid2DroidManager.class.getClassLoader();
			File dir=context.getApplicationContext().getDir("dexopt", Context.MODE_PRIVATE); 
			final String packageName="org.droid2droid";
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
			throw new Error("Install the RemoteAndroid package",e);
		}
	}
    
    // ---------------------------
    /** Bootstrap implementation. */
    private static final String BOOTSTRAP_CLASS="org.droid2droid.internal.FactoriesImpl";
	/*package*/ static final boolean USE_SHAREDLIB=false; // true if use shared library.
	/*package*/static final String SHARED_LIB="sharedlib"; // Library name.

    private static Factories sFactory;
    
    private static boolean sBootstraped;
    private static synchronized void bootstrap()
    {
    	if (!sBootstraped)
    	{
//		if (USE_SHAREDLIB) // FIXME: Must be validated with Parcelable objet
//		{
//			// Strict mode
//			final Object lock=new Object();
//			synchronized (lock)
//			{
//				new Thread()
//				{
//					@Override
//					public void run() 
//					{
//						try
//						{
//							sClassLoader=getClassLoaderSingleton(context);
//    		    			synchronized (lock)
//							{
//        						lock.notify();
//							}
//						}
//						catch (Exception e)
//						{
//							throw new Error("Install the RemoteAndroid package",e);
//						}
//					}
//				}.start();
//    			try
//				{
//					lock.wait();
//					RemoteAndroidManager.class.getClassLoader().loadClass(BOOTSTRAP_CLASS)
//							.getMethod(BOOTSTRAP_METHOD, Context.class,RemoteAndroidManager.ManagerListener.class).invoke(null, context,listener);
//					
//				}
//				catch (Exception e)
//				{
//					throw new Error("Install the Remote Android package",e);
//				}
//			}
//		}
//		else
			{
				try
				{
					sFactory=(Factories)Droid2DroidManager.class.getClassLoader().loadClass(BOOTSTRAP_CLASS).newInstance();
				}
				catch (Exception e)
				{
					throw new Error("Internal error",e);
				}
			}
    	}
    }
    
}
