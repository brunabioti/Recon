package com.example.bioti.reconbioti.licenca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bioti.reconbioti.R;
import com.example.bioti.reconbioti.app.BaseActivity;
import com.example.bioti.reconbioti.app.BaseListFragment;

import com.example.bioti.reconbioti.licenca.GerenciadorLicencas.LicensingStateCallback;
import com.neurotec.licensing.NLicensingService;

import java.io.IOException;

public final class PreferenciasLicenca extends BaseActivity implements LicensingStateCallback {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = PreferenciasLicenca.class.getSimpleName();
	private static final int REQUEST_CODE_LICENSE_SERVICE_PREFERENCES = 0;
	private static final String SERVICE = "Licensing service";
	private static final String ACTIVATION = "Activation";

	// ===========================================================
	// Public static fields
	// ===========================================================

	public static final boolean SHOW_SEEKBAR_MIN_MAX_VALUES = true;

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new LicensingPreferencesFragment()).commit();
		//TODO: Move reobtaining to callback after LicensingServicePreferences is changed to fragment.
		if (GerenciadorServicoLicenca.getInstance().isOutdated(this)) {
			try {
				GerenciadorLicencas.getInstance().reobtain(this, this);
			} catch (IOException e) {
				Log.e(TAG, "Exception", e);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			switch (requestCode) {
			case REQUEST_CODE_LICENSE_SERVICE_PREFERENCES:
				if (GerenciadorServicoLicenca.getInstance().isOutdated(this)) {
					GerenciadorLicencas.getInstance().reobtain(this, this);
				} break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onLicensingStateChanged(EstadosLicenca state) {
		switch (state) {
		case OBTENDO_LICENCA:
			showProgress(R.string.msg_obtaining_licenses);
			break;
		case LICENCA_OBTIDA:
			hideProgress();
			showToast(R.string.msg_licenses_obtained);
			break;
		case LICENCA_NAO_OBTIDA:
			hideProgress();
			showToast(R.string.msg_licenses_not_obtained);
			break;
		}
	}


	private class LicensingPreferencesFragment extends BaseListFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			try {
				String[] values = null;
				if (!NLicensingService.isTrial()) {
					values = new String[] {SERVICE, ACTIVATION};
				} else {
					values = new String[] {SERVICE};
				}
				setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values));
			} catch (Exception e) {
				showError(e);
			}
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			String item = (String) getListAdapter().getItem(position);
			if (item.equals(SERVICE)) {
				getActivity().startActivityForResult(new Intent(getActivity(), ServicoPreferenciasLicenca.class), REQUEST_CODE_LICENSE_SERVICE_PREFERENCES);
			} else if (item.equals(ACTIVATION)) {
				//getFragmentManager().beginTransaction().replace(android.R.id.content, new ActivationActivity(), "Ativação").addToBackStack(null).commit();
			}
		}

	}

}
