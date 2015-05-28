package com.example.bioti.reconbioti.app;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.bioti.reconbioti.util.ExceptionUtils;
import com.example.bioti.reconbioti.util.ToastManager;
import com.example.bioti.reconbioti.view.ErrorDialogFragment;
import com.example.bioti.reconbioti.view.InfoDialogFragment;

public abstract class BaseActivity extends ActionBarActivity {

	// ===========================================================
	// Private fields
	// ===========================================================

	private ProgressDialog mProgressDialog;

	// ===========================================================
	// Protected methods
	// ===========================================================

	protected void showProgress(int messageId) {
		showProgress(getString(messageId));
	}

	protected void showProgress(final String message) {
		hideProgress();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mProgressDialog = ProgressDialog.show(BaseActivity.this, "", message);
			}
		});
	}

	protected void hideProgress() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		});
	}

	protected void showToast(int messageId) {
		showToast(getString(messageId));
	}

	protected void showToast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastManager.show(BaseActivity.this, message);
			}
		});
	}

	protected void showError(String message, boolean close) {
		ErrorDialogFragment.newInstance(message, close).show(getFragmentManager(), "error");
	}

	protected void showError(int messageId) {
		showError(getString(messageId));
	}

	protected void showError(String message) {
		showError(message, false);
	}

	protected void showError(Throwable th) {
		Log.e(getClass().getSimpleName(), "Exception", th);
		showError(ExceptionUtils.getMessage(th), false);
	}

	protected void showInfo(int messageId) {
		showInfo(getString(messageId));
	}

	protected void showInfo(String message) {
		InfoDialogFragment.newInstance(message).show(getFragmentManager(), "info");
	}

	@Override
	protected void onStop() {
		super.onStop();
		hideProgress();
	}
}
