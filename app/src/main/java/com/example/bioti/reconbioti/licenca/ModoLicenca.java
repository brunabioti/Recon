/*
    Identifica e retorna se o tipo de licença é trial, de servidor, número de série, etc
 */
package com.example.bioti.reconbioti.licenca;

import android.content.Context;
import android.util.Log;

import com.neurotec.licensing.NLicensingService;
import com.example.bioti.reconbioti.util.ResourceUtils;

import java.util.EnumSet;

public enum ModoLicenca {

	DIRETO(true, true, false),
	DO_PC(false, true, true),
	DO_ARQUIVO(true, true, false);

	private static final String TAG = ModoLicenca.class.getSimpleName();

	// ===========================================================
	// Public static methods
	// ===========================================================

	public static ModoLicenca get(int ordinal) {
		return ModoLicenca.values()[ordinal];
	}

	public static ModoLicenca get(String name) {
		return Enum.valueOf(ModoLicenca.class, name);
	}

	public static EnumSet<ModoLicenca> getAvailable() {
		try {
			return NLicensingService.isTrial() ? EnumSet.of(DIRETO, DO_PC) : EnumSet.of(DO_PC, DO_ARQUIVO);
		} catch (Throwable e) {
			Log.e(TAG, "Exception", e);
			return EnumSet.allOf(ModoLicenca.class);
		}
	}

	public static ModoLicenca getDefault() {
		try {
			return DO_ARQUIVO;
		} catch (Throwable e) {
			Log.e(TAG, "Exception", e);
			return DO_ARQUIVO;
		}
	}

	// ===========================================================
	// Private fields
	// ===========================================================

	private boolean mPGRequired;
	private boolean mInternetBased;
	private boolean mServerConfigurable;

	// ===========================================================
	// Private constructor
	// ===========================================================

	private ModoLicenca(boolean pgRequired, boolean internetBased, boolean serverConfigurable) {
		this.mPGRequired = pgRequired;
		this.mInternetBased = internetBased;
		this.mServerConfigurable = serverConfigurable;
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public boolean isPGRequired() {
		return mPGRequired;
	}

	public boolean isInternetBased() {
		return mInternetBased;
	}

	public boolean isServerConfigurable() {
		return mServerConfigurable;
	}

	public String getName(Context context) {
		return ResourceUtils.getEnum(context, this);
	}
}
