package org.remoteandroid;


import android.app.Activity;
import android.content.Intent;

/**
 * Helper class to integrate the NFC discovering in an activity.
 * Invoke the specific methods during the life cycle of the activity.
 * <pre>
 * </pre>
 * @author pprados
 *
 */
public interface RemoteAndroidNfcHelper 
{
	public interface OnNfcDiscover
	{
	    /**
	     * Called when a Remote android NFC tag is detected.
	     * 
	     * @param info The remote android info exposed in the tag.
	     */
		public abstract void onNfcDiscover(RemoteAndroidInfo info);
	}
	/**
	 * If NFC is accessible, check if a Remote android tag is presented.
	 *  
	 * Must be invoked in {@link android.app.Activity#onNewIntent(android.content.Intent)}.
	 * 
	 * @param activity The owner activity.
	 * @param manager The remote android manager.
	 * @param intent The intent.
	 */
	public void onNewIntent(Activity activity,RemoteAndroidManager manager,Intent intent);
	/**
	 * If NFC is accessible, register a listener to receive the Remote Android tags.
	 * 
	 * Must be invoked in {@link android.app.Activity#onResume()}.
	 *  
	 * @param activity The owner activity.
	 */
	public void onResume(Activity activity);
	/**
	 * If NFC is accessible, unregister a listener to receive the Remote Android tags.
	 * 
	 * Must be invoked in {@link android.app.Activity#onPause()}.
	 *  
	 * @param activity
	 */
    public void onPause(Activity activity);

}
