package org.remoteandroid;

import java.security.PublicKey;
import java.util.UUID;

import android.os.Parcelable;

/**
 * Informations of remote device.
 * 
 * @author Philippe Prados
 *
 */
public interface RemoteAndroidInfo extends Parcelable
{
	/** If the remote device have a screen. */
	public static final long FEATURE_SCREEN			=1<<0;
	/** If the remote device have a HP. */
	public static final long FEATURE_HP				=1<<1;
	/** If the remote device have a microphone. */
	public static final long FEATURE_MICROPHONE		=1<<2;
	/** If the remote device have bluetooth. */
	public static final long FEATURE_BT				=1<<3;
	/** If the remote device have a camera. */
	public static final long FEATURE_CAMERA			=1<<4;
	/** If the remote device have NFC. */
	public static final long FEATURE_NFC			=1<<5;
	/** If the remote device have telephony. */
	public static final long FEATURE_TELEPHONY		=1<<6;
	/** If the remote device have Wifi. */
	public static final long FEATURE_WIFI			=1<<7;
	/** If the remote device have Wifi. */
	public static final long FEATURE_WIFI_DIRECT	=1<<8;
	/** If the remote device have network. */
	public static final long FEATURE_NET			=1<<9;
	/** If location.*/
	public static final long FEATURE_LOCATION    	=1<<10;
	/** If location.*/
	public static final long FEATURE_BLUETOOTH		=1<<11;
	/** If location.*/
	public static final long FEATURE_ACCELEROMETER	=1<<12;
	
	
	/** The unique id of the device. */
	public UUID getUuid();
	/** The public name of the device. May be changed. */
	public String getName();
	/** The public key of the device. Can't be changed. */
	public PublicKey getPublicKey();
	/** The remote android version. */
	public int getVersion();
	/** The operating system. Must be "android" now. */
	public String getOs();
	/** The remote capability for pairing process. */
	public long getFeature();
	/** Is the remote device paired with this device ? */
	public boolean isBound();
	/** Is the remote device discovered ? */
	public boolean isDiscover();
	/** Is the remote device not bonded and not discovered ? */
	public boolean isRemovable();
	

	/**
	 * Merge two android info if used the same UUID.
	 * 
	 * @param info The data to inject in this instance.
	 * @param remove <code>true</code> if remove ip address.
	 */
//	public boolean merge(RemoteAndroidInfo info);

	/**
	 * Return the currents connection uris to connect to the remote device.
	 * This uris are ordered. The first one is the best.
	 * @return An array of URIS.
	 */
	public String[] getUris();
	
	/**
	 * Remove an uri if you known it's failed now.
	 * 
	 * @param uri The uri to remove.
	 */
	public void removeUri(String uri); //TODO: remove or remote ?
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object x);
}
