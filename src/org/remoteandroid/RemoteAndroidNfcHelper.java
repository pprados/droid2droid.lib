package org.remoteandroid;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Parcelable;

/**
 * Helper class to integrate the NFC discovering in an activity.
 * Invoke the specific methods during the life cycle of the activity.
 * <pre>
 * public abstract class NfcFragmentActivity extends FragmentActivity
 * implements RemoteAndroidNfcHelper.OnNfcDiscover
 * {
 *    protected RemoteAndroidNfcHelper mNfcIntegration;
 * 	
 *    @Override
 *    protected void onCreate(Bundle arg0)
 * 	  {
 * 		super.onCreate(arg0);
 * 		mNfcIntegration=RemoteAndroidManager.newNfcIntegrationHelper(this);
 *    }
 * 	  @Override
 * 	  protected void onNewIntent(Intent intent)
 * 	  {
 *      super.onNewIntent(intent);
 * 	    mNfcIntegration.onNewIntent(this, intent);
 * 	  }
 * 	  @Override
 * 	  protected void onResume()
 * 	  {
 * 	    super.onResume();
 * 	    mNfcIntegration.onResume(this);
 * 	  }
 * 	  @Override
 * 	  protected void onPause()
 * 	  {
 * 		super.onPause();
 * 		mNfcIntegration.onPause(this);
 * 	  }
 * 	  public abstract void onNfcDiscover(RemoteAndroidInfo info);
 * }
 * </pre>
 * @author pprados
 * @since 1.0
 *
 */
@TargetApi(9)
public interface RemoteAndroidNfcHelper 
{
	/**
	 * 
	 * @author Philippe Prados
	 * @since 1.0
	 *
	 */
	public interface OnNfcDiscover
	{
	    /**
	     * Called when a Remote android NFC tag is detected.
	     * 
	     * @param info The remote android info exposed in the tag.
	     * @since 1.0
	     */
		public abstract void onNfcDiscover(RemoteAndroidInfo info);
	}
	
    /**
     * Create NDeF message to expose.
     * @return NDefMessage with own RemoteAndroidInfo.
     * 
     * @since 1.0
     */
    public abstract NdefMessage createNdefMessage(RemoteAndroidManager manager);

	/**
	 * Extract RemoteAndroidInfo from NFC rawMessages.
	 * 
	 * @param context The context.
	 * @param rawMessages The rawMessages.
	 * 
	 * @return The RemoteAndroidInfo or null.
	 * 
	 * @Since 1.0
	 */
	public RemoteAndroidInfo parseNfcRawMessages(Context context,Parcelable[] rawMessages);
	
	/**
	 * If NFC is accessible, check if a Remote android tag is presented.
	 *  
	 * Must be invoked in {@link android.app.Activity#onNewIntent(android.content.Intent)}.
	 * 
	 * @param activity The owner activity.
	 * @param intent The intent.
	 */
	public void onNewIntent(Activity activity,Intent intent);
	
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
