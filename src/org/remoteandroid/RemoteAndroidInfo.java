package org.remoteandroid;

import java.net.InetAddress;
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
	public static final int CAPABILITY_SCREEN	=1<<1;
	public static final int CAPABILITY_KEYBOARD	=1<<2;
	public static final int CAPABILITY_CAMERA	=1<<3;
	public static final int CAPABILITY_SMS		=1<<4;
	
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
	public int getCapability();
	/** Is the remote device paired with this device ? */
	public boolean isBonded();
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
