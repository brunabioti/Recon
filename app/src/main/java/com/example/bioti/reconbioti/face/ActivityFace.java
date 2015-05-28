package com.example.bioti.reconbioti.face;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bioti.reconbioti.R;
import com.example.bioti.reconbioti.app.BaseActivity;
import com.example.bioti.reconbioti.licenca.AtivadorActivity;
import com.example.bioti.reconbioti.licenca.EstadosLicenca;
import com.example.bioti.reconbioti.licenca.GerenciadorLicencas;
import com.example.bioti.reconbioti.licenca.GerenciadorLicencas.LicensingStateCallback;
import com.example.bioti.reconbioti.licenca.GerenciadorServicoLicenca;
import com.neurotec.biometrics.NBiometric;
import com.neurotec.biometrics.NBiometricCaptureOption;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NFace;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.biometrics.view.NFaceView;
import com.neurotec.devices.NCamera;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.lang.NCore;
import com.neurotec.licensing.NLicense;
import com.neurotec.util.concurrent.CompletionHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public final class ActivityFace extends BaseActivity implements LicensingStateCallback {

	private static final String TAG = ActivityFace.class.getSimpleName();
	private static final String[] LICENSES = {GerenciadorLicencas.LICENSE_FACE_EXTRACTION, GerenciadorLicencas.LICENSE_FACE_DETECTION, GerenciadorLicencas.LICENSE_DEVICES_CAMERAS};

	private Button mButtonExtract;
	private NFaceView mFaceView;
	private TextView mStatus;
	private NBiometricClient mBiometricClient;

	// Prints capturing status.
	private final PropertyChangeListener biometricPropertyChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("Status".equals(evt.getPropertyName())) {
				final NBiometricStatus status = ((NBiometric) evt.getSource()).getStatus();
				runOnUiThread(new Runnable() {
					public void run() {
						showMessage(status.toString());
					}
				});
			}
		}
	};


	private CompletionHandler<NBiometricStatus, NSubject> completionHandler = new CompletionHandler<NBiometricStatus, NSubject>() {
		@Override
		public void completed(NBiometricStatus result, NSubject subject) {
			if (result == NBiometricStatus.OK) {
				showMessage(getString(R.string.msg_template_created));
				Log.i(TAG,getString(R.string.msg_template_created));
				NFace face = subject.getFaces().get(0);

				// This place is reached when you press extract and it succeeds to extract face.

			} else {
				showMessage(getString(R.string.format_extraction_failed, result));
				Log.i(TAG, getString(R.string.format_extraction_failed, result));
			}
			if (result != NBiometricStatus.CANCELED) {
				startCapturing();
			}
		}

		@Override
		public void failed(Throwable exc, NSubject subject) {
			exc.printStackTrace();
			startCapturing();
		}
	};


	private void stopCapturing() {
		mBiometricClient.force();
	}

	private void init(){
		mBiometricClient = new NBiometricClient();
		mBiometricClient.setUseDeviceManager(true);
		NDeviceManager deviceManager = mBiometricClient.getDeviceManager();
		deviceManager.setDeviceTypes(EnumSet.of(NDeviceType.CAMERA));
		deviceManager.initialize();
		Log.i(TAG, "Devices found: " + deviceManager.getDevices().size());

		if(deviceManager.getDevices().size() > 0){
			// On my phone first camera is Rear and second is Front(I choose front - index = 1).
			NCamera camera = (NCamera)deviceManager.getDevices().get(0);
			Log.i(TAG, "Chose camera: "+ camera.getDisplayName());
			mBiometricClient.setFaceCaptureDevice(camera);

			// Initializing client takes some time, but you only have to initialize it once at beginning of application.
			Log.i(TAG, "Initializing NBiometricClient...");
			mBiometricClient.initialize();
			Log.i(TAG, "NBiometricClient is initialized.");

			startCapturing();
		} else {
			Log.i(TAG, "No cameras found.");
		}
	}

	private void startCapturing(){
		NSubject subject = new NSubject();
		NFace face = new NFace();
		face.addPropertyChangeListener(biometricPropertyChanged);
		face.setCaptureOptions(EnumSet.of(NBiometricCaptureOption.MANUAL));
		mFaceView.setFace(face);
		subject.getFaces().add(face);

		Log.i(TAG, "Capturing...");
        try {
            mBiometricClient.capture(subject, subject, completionHandler);
        }catch (Exception e){
            showMessage(e.getMessage());
        }
	}



    private void showMessage(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mStatus.append(message + "\n");
			}
		});
	}


	@Override
	public void onLicensingStateChanged(final EstadosLicenca state) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (state) {
				case OBTENDO_LICENCA:
					Log.i(TAG, getString(R.string.format_obtaining_licenses, Arrays.toString(LICENSES)));
					showProgress(R.string.msg_obtaining_licenses);
					break;
				case LICENCA_OBTIDA:
					hideProgress();
					showMessage(getString(R.string.msg_licenses_obtained));
					init();
					break;
				case LICENCA_NAO_OBTIDA:
					hideProgress();
					showMessage(getString(R.string.msg_licenses_not_obtained));
					break;
				default:
					throw new AssertionError("Unknown state: " + state);
				}
			}
		});
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NCore.setContext(this);
        setContentView(R.layout.camera_view);
        mFaceView = (NFaceView) findViewById(R.id.camera_view);
        mStatus = (TextView) findViewById(R.id.text_view_status);
        mButtonExtract = (Button) findViewById(R.id.button_extract);
        mButtonExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCapturing();
            }
        });

        try {
            obtain(Arrays.asList(LICENSES));

        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_preferences:
                Intent intentpreferencia = new Intent(this, PreferenciasFace.class);
                startActivity(intentpreferencia);
                return false;
            case R.id.action_database:
                return false;
            case R.id.action_licensing:
                Intent intentlicenca = new Intent(this, AtivadorActivity.class);
                startActivity(intentlicenca);
                return false;
            case R.id.action_about:
                Intent intentsobre = new Intent(this, ActivitySobre.class);
                startActivity(intentsobre);
                return false;
            case R.id.action_face:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    //Função para obter licenças de um servidor
    private boolean obtain(List<String> components) throws IOException {
        boolean result = false;
        String address = GerenciadorServicoLicenca.DEFAULT_SERVER_ADDRESS;
        int port = GerenciadorServicoLicenca.DEFAULT_SERVER_PORT;

        Log.i(TAG, String.format("Obtendo licenças do servidor %s:%s", address, port));

        for (String component : components) {
            boolean available = false;
            available = NLicense.obtainComponents(address, port, component);
            result |= available;
            Log.i(TAG, String.format("Obtendo licença '%s' licença %s.", component, available ? "sucesso" : "falha"));
        }
        return result;
    }

}

