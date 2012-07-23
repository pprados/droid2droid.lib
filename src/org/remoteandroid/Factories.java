package org.remoteandroid;

import org.remoteandroid.ListRemoteAndroidInfo.DiscoverListener;
import org.remoteandroid.RemoteAndroidManager.ManagerListener;

import android.content.Context;

/**
 * @hide
 * @author Philippe Prados
 */
abstract class Factories
{
    public abstract ListRemoteAndroidInfo newDiscoveredAndroid(Context context,DiscoverListener callback);
    
    public abstract void newManager(final Context context,final ManagerListener listener);
}
