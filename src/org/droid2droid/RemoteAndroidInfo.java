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

import java.security.PublicKey;
import java.util.UUID;

import android.os.Parcelable;

/**
 * Informations of remote device.
 * 
 * @author Philippe PRADOS
 * @since 1.0
 */
public interface RemoteAndroidInfo extends Parcelable
{
	/** If the remote device have a screen. 
	 * @since 1.0
	 */
	public static final long FEATURE_SCREEN			=1<<0;
	/** If the remote device have a HP.  
	 * @since 1.0
	 */
	public static final long FEATURE_HP				=1<<1;
	/** If the remote device have a microphone.  
	 * @since 1.0
	 */
	public static final long FEATURE_MICROPHONE		=1<<2;
	/** If the remote device have bluetooth.  
	 * @since 1.0
	 */
	public static final long FEATURE_BT				=1<<3;
	/** If the remote device have a camera.  
	 * @since 1.0
	 */
	public static final long FEATURE_CAMERA			=1<<4;
	/** If the remote device have NFC.  
	 * @since 1.0
	 */
	public static final long FEATURE_NFC				=1<<5;
	/** If the remote device have telephony.  
	 * @since 1.0
	 */
	public static final long FEATURE_TELEPHONY		=1<<6;
	/** If the remote device have Wifi.  
	 * @since 1.0
	 */
	public static final long FEATURE_WIFI				=1<<7;
	/** If the remote device have Wifi.  
	 * @since 1.0
	 */
	public static final long FEATURE_WIFI_DIRECT		=1<<8;
	/** If the remote device have network.  
	 * @since 1.0
	 */
	public static final long FEATURE_NET				=1<<9;
	/** If location. 
	 * @since 1.0
	 */
	public static final long FEATURE_LOCATION    		=1<<10;
	/** If Bluetooth. 
	 * @since 1.0
	 */
	public static final long FEATURE_BLUETOOTH		=1<<11;
	/** If accelerometer. 
	 * @since 1.0
	 */
	public static final long FEATURE_ACCELEROMETER	=1<<12;
	
	
	/** The unique id of the device.  
	 * @since 1.0
	 */
	public UUID getUuid();
	/** The public name of the device. May be changed.  
	 * @since 1.0
	 */
	public String getName();
	/** The public key of the device. Can't be changed.  
	 * @since 1.0
	 */
	public PublicKey getPublicKey();
	/** The remote android version.  
	 * @since 1.0
	 */
	public int getVersion();
	/** The operating system. Must be "android" now.  
	 * @since 1.0
	 */
	public String getOs();
	/** The remote capability for pairing process.  
	 * @since 1.0
	 */
	public long getFeature();
	/** Is the remote device paired with this device ?  
	 * @since 1.0
	 */
	public boolean isBound();
	/** Is the remote device discovered ?  
	 * @since 1.0
	 */
	public boolean isDiscover();
	/** Is the remote device not bonded and not discovered ?  
	 * @since 1.0
	 */
	public boolean isRemovable();
	
	/**
	 * Return the currents connection URIs to connect to the remote device.
	 * This URIs are ordered. The first one is the best.
	 * 
	 * @return An array of URIS.
	 * @since 1.0
	 */
	public String[] getUris();
	
	/**
	 * Remove an URI if you known it's failed now.
	 * 
	 * @param uri The URI to remove.
	 * @since 1.0
	 */
	public void removeUri(String uri);
	
	/**
	 * {@inheritDoc}
	 * @since 1.0
	 */
	@Override
	public boolean equals(Object x);
	
	/**
	 * {@inheritDoc}
	 * @since 1.0
	 */
	@Override
	public int hashCode();
}
