package com.example.bioti.reconbioti.licenca;

import android.app.Activity;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bioti.reconbioti.R;
import com.example.bioti.reconbioti.app.BaseFragment;
import com.example.bioti.reconbioti.view.QuestionDialogFragment.QuestionDialogListener;

import java.util.ArrayList;
import java.util.List;

public final class ActivationActivity extends BaseFragment implements QuestionDialogListener {

	public interface OnLicenseStateModifiedListener {
		public void onLicensesStateModified();
	}

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = ActivationActivity.class.getSimpleName();

	// ===========================================================
	// Public static fields
	// ===========================================================

	public static final String CODE_IS_MODIFIED = "code_is_modified";

	// ===========================================================
	// Private fields
	// ===========================================================

	private ListView mListView = null;
	private Button mButtonActivate = null;
	private Button mButtonDeactivate = null;
	private BackgroundTask mBackgroundTask = null;
	private boolean isModified = false;
	private OnLicenseStateModifiedListener mCallback;

	// ===========================================================
	// Private methods
	// ===========================================================

	private List<Licenca> getSelectedLicenses() {
		List<Licenca> licenses = new ArrayList<Licenca>();
		SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
		if (checkedItems != null) {
			for (int i = 0; i < checkedItems.size(); i++) {
				if (checkedItems.valueAt(i)) {
					licenses.add((Licenca) mListView.getAdapter().getItem(checkedItems.keyAt(i)));
				}
			}
		}
		return licenses;
	}

	@SuppressWarnings("unchecked")
	private void clearView() {
		mListView.clearChoices();
		((ArrayAdapter<Licenca>) mListView.getAdapter()).notifyDataSetChanged();
		updateLicenses();
	}

	private void updateView() {
		boolean activateEnabled = true;
		boolean deactivateEnabled = true;

		List<Licenca> licenses = getSelectedLicenses();

		if (licenses == null || licenses.isEmpty()) {
			activateEnabled = deactivateEnabled = false;
		} else {
			for (Licenca license : licenses) {
				activateEnabled &= !license.isActivated();
				deactivateEnabled &= license.isActivated();
			}
		}

		mButtonActivate.setVisibility(activateEnabled ? View.VISIBLE : View.GONE);
		mButtonDeactivate.setVisibility(deactivateEnabled ? View.VISIBLE : View.GONE);
	}

	private void updateLicenses() {
		try {
			mListView.setAdapter(null);
			List<Licenca> licenses = GerenciadorServicoLicenca.getLicenses();
			if (licenses == null || licenses.isEmpty()) {
				showToast(R.string.msg_no_licenses);
			} else {
				//mListView.setAdapter(new LicenseListAdapter(getActivity(), R.layout.license_list_item, licenses));
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showToast(e.getMessage());
		}
		updateView();
	}

	private void activate() {
		try {
			if (mBackgroundTask == null) {
				mBackgroundTask = new BackgroundTask();
				//mBackgroundTask.activate(getSelectedLicenses());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showToast(e.getMessage());
		}
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				//TODO inform listeners about this event
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			isModified = false;
			mCallback = (OnLicenseStateModifiedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLicenseStateModifiedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(isModified && mCallback != null) {
			mCallback.onLicensesStateModified();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		updateLicenses();
	}

	@Override
	public void onDestroy() {
		if (mBackgroundTask != null) {
			mBackgroundTask.cancel(true);
			mBackgroundTask = null;
		}
		super.onDestroy();
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onQuestionAnswered(boolean accepted) {
		if (accepted) {
			activate();
		}
	}

	// ===========================================================
	// Private inner classes
	// ===========================================================

	private enum Task {
		ACTIVATE, DEACTIVATE
	}

	private final class BackgroundTask extends AsyncTask<Boolean, String, String> {

		private Task mTask;
		private List<Licenca> mLicenses;

		void activate(List<Licenca> licenses) {
			if (licenses == null) throw new NullPointerException("licenses");
			if (licenses.isEmpty()) throw new IllegalArgumentException("licenses < 0");
			mTask = Task.ACTIVATE;
			mLicenses = licenses;
			showProgress(getString(R.string.msg_activating));
			execute();
		}

		void deactivate(List<Licenca> licenses) {
			if (licenses == null) throw new NullPointerException("licenses");
			if (licenses.isEmpty()) throw new IllegalArgumentException("licenses < 0");
			mTask = Task.DEACTIVATE;
			mLicenses = licenses;
			showProgress(getString(R.string.msg_deactivating));
			execute();
		}

		@Override
		protected String doInBackground(Boolean... params) {
			/*boolean hasInternet = ConnectivityHelper.isConnected(getActivity());
			try {
				if (!isCancelled()) {
					switch (mTask) {
					case ACTIVATE:
						if (hasInternet) {
							isModified = true;
						}
						for (License license : mLicenses) {
							license.activate(hasInternet);
						}
						return getString(hasInternet ? R.string.msg_activation_succeeded : R.string.msg_proceed_activation_online);
					case DEACTIVATE:
						if (hasInternet) {
							isModified = true;
						}
						for (License license : mLicenses) {
							license.deactivate(hasInternet);
						}
						return getString(hasInternet ? R.string.msg_deactivation_succeeded : R.string.msg_proceed_deactivation_online);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Exception", e);
				return e.getMessage();
			}*/
			return null;
		}

		@Override
		protected void onProgressUpdate(String... messages) {
			for (String message : messages) {
				showProgress(message);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			hideProgress();
			if (result != null) {
				showInfo(result);
			}
			mBackgroundTask = null;
			clearView();
		}
	}
}

class LicenseListAdapter extends ArrayAdapter<Licenca> {
	private View mRow;
	private List<Licenca> mLicenses;
	private int mRes;
	private Context context;

	public LicenseListAdapter(Context context, int textViewResourceId, List<Licenca> licenses) {
		super(context, textViewResourceId, licenses);
		this.mLicenses = licenses;
		this.mRes = textViewResourceId;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mRow = convertView;
		if (mRow == null) { // inflate our custom layout. mRes == R.layout.row_team_layout.xml
			LayoutInflater ll = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mRow = ll.inflate(mRes, parent, false);
		}

		Licenca license = mLicenses.get(position); // Produce a mRow for each Team.

		if (license != null) {
			TextView text = (TextView) mRow.findViewById(R.id.list_item_license_text);
			ImageView image = (ImageView) mRow.findViewById(R.id.list_item_license_image);
			text.setText(license.getName());

			if (license.isActivated()) {
				image.setImageResource(android.R.drawable.ic_input_add);
			} else {
				image.setImageResource(android.R.drawable.ic_menu_help);
			}
		}
		return mRow;
	}
}
