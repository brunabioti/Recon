package com.example.bioti.reconbioti.licenca;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.neurotec.licensing.NLicense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class GerenciadorLicencas {
 /*
    Esta classe realiza o papel de gerenciados de licenças
    Retorna o estado de ativação de uma licença e quais licenças esão ativadas. .
 */

	// ===========================================================
	// Public nested class
	// ===========================================================

	public interface LicensingStateCallback {
		void onLicensingStateChanged(EstadosLicenca state);
	}

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = GerenciadorLicencas.class.getSimpleName();

	private static GerenciadorLicencas sInstance;

	// ===========================================================
	// Public static fields
	// ===========================================================

    //Tipos de Licença Disponíveis, no caso será utilizado apenas licença da parte de reconhecimento facial
	public static final String LICENSE_MEDIA = "Media";

	public static final String LICENSE_DEVICES_CAMERAS = "Devices.Cameras";
	public static final String LICENSE_FACE_DETECTION = "Biometrics.FaceDetection";
	public static final String LICENSE_FACE_EXTRACTION = "Biometrics.FaceExtraction";
	public static final String LICENSE_FACE_MATCHING = "Biometrics.FaceMatching";
	public static final String LICENSE_FACE_MATCHING_FAST = "Biometrics.FaceMatchingFast";
	public static final String LICENSE_FACE_SEGMENTATION = "Biometrics.FaceSegmentation";
	public static final String LICENSE_FACE_STANDARDS = "Biometrics.Standards.Faces";
	public static final String LICENSE_FACE_SEGMENTS_DETECTION = "Biometrics.FaceSegmentsDetection";

	public static final String LICENSING_PREFERENCES = "com.example.bioti.reconbioti.licenca.GerenciadorLicencas";
	public static final String LICENSING_SERVICE = "com.example.bioti.reconbioti.licenca.GerenciadorLicencas";

	public static final int REQUEST_CODE_LICENSING_PREFERENCES = 10;

	// ===========================================================
	// Public static method
	// ===========================================================

	//
    public static synchronized GerenciadorLicencas getInstance() {
		if (sInstance == null) {
			sInstance = new GerenciadorLicencas();
		}
		return sInstance;
	}

	//Passo uma licença e por meio de uma função da biblioteca neurotecnology verifico se essa licença está ativa
    public static boolean isActivated(String license) {
		if (license == null) throw new NullPointerException("licenca");
		try {
			return NLicense.isComponentActivated(license);
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
			return false;
		}
	}

    //Verifica se a licença de extração de face está ativada
	public static boolean isFaceExtractionActivated() {
		return isActivated(LICENSE_FACE_EXTRACTION);
	}

	//Verifica se a licença de reconhecimento e operações com as caracteristicas da face esta ativada
    public static boolean isFaceMatchingActivated() {
		return isActivated(LICENSE_FACE_MATCHING) || isActivated(LICENSE_FACE_MATCHING_FAST);
	}

	public static boolean isFaceStandardsActivated() {
		return isActivated(LICENSE_FACE_STANDARDS);
	}

	// ===========================================================
	// Private fields
	// ===========================================================

    //Cria uma lista de strings chamada mCOmponents
	private List<String> mComponents;

	// ===========================================================
	// Private constructor
	// ===========================================================

	private GerenciadorLicencas() {
		mComponents = new ArrayList<String>();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	//Função para obter licenças de um servidor
    private boolean obtain(List<String> components) throws IOException {
		boolean result = false;
		String address = GerenciadorServicoLicenca.DEFAULT_SERVER_ADDRESS;
		int port = GerenciadorServicoLicenca.DEFAULT_SERVER_PORT;

		Log.i(TAG, String.format("Obtendo licenças do servidor %s:%s", address, port));
		mComponents.addAll(components);
		for (String component : components) {
			boolean available = false;
			available = NLicense.obtainComponents(address, port, component);
			result |= available;
			Log.i(TAG, String.format("Obtendo licença '%s' licença %s.", component, available ? "sucesso" : "falha"));
		}
		return result;
	}

	// ===========================================================
	// Public methods
	// ===========================================================

    //Função para reobter licença - utilizada no método trial quando a conexão com a internet deve ser constante
	public void reobtain(final Context context, final LicensingStateCallback callback) throws IOException {
		if (callback == null) throw new NullPointerException("callback");
		new AsyncTask<Boolean, Boolean, Boolean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				callback.onLicensingStateChanged(EstadosLicenca.OBTENDO_LICENCA);
			}
			@Override
			protected Boolean doInBackground(Boolean... params) {
				try {
					return reobtain(context);
				} catch (Exception e) {
					Log.e(TAG, "Exception", e);
					return false;
				}
			}
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onLicensingStateChanged(result ? EstadosLicenca.LICENCA_OBTIDA : EstadosLicenca.LICENCA_NAO_OBTIDA);
			}
		}.execute();
	}

	public boolean reobtain(Context context) throws IOException {
		List<String> reobtainedComponents = new ArrayList<String>(mComponents);
		release(reobtainedComponents);
		return obtain(context, reobtainedComponents);
	}

	public void obtain(Context context, LicensingStateCallback callback, List<String> components) {
		if (context == null) throw new NullPointerException("context");
		obtain(callback, components, GerenciadorServicoLicenca.getLicensingMode(context), GerenciadorServicoLicenca.DEFAULT_SERVER_ADDRESS, GerenciadorServicoLicenca.DEFAULT_SERVER_PORT);
	}

	public void obtain(final LicensingStateCallback callback, final List<String> components, final ModoLicenca mode, final String address, final int port) {
		if (callback == null) throw new NullPointerException("callback");
		new AsyncTask<Boolean, Boolean, Boolean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				callback.onLicensingStateChanged(EstadosLicenca.OBTENDO_LICENCA);
			}
			@Override
			protected Boolean doInBackground(Boolean... params) {
				try {
					return obtain(components, mode, address, port);
				} catch (Exception e) {
					Log.e(TAG, "Exception", e);
					return false;
				}
			}
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onLicensingStateChanged(result ? EstadosLicenca.LICENCA_OBTIDA : EstadosLicenca.LICENCA_NAO_OBTIDA);
			}
		}.execute();
	}

	public boolean obtain(Context context, List<String> components) {
		if (context == null) throw new NullPointerException("context");
		return obtain(components, GerenciadorServicoLicenca.getLicensingMode(context), GerenciadorServicoLicenca.getServerAddress(context), GerenciadorServicoLicenca.getServerPort(context));
	}

	public boolean obtain(List<String> components, ModoLicenca modo, String address, int port) {
		if (components == null) throw new NullPointerException("components");
		if (components.isEmpty()) throw new IllegalArgumentException("List of components is empty");
		boolean result = false;
		try {
			GerenciadorServicoLicenca.getInstance().start(modo, address, port);
			result = obtain(components);
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
		return result;
	}

	public void release(List<String> components) {
		try {
			if (components != null && !components.isEmpty()) {
				Log.i(TAG, "Releasing licenses: " + components);
				NLicense.releaseComponents(components.toString().replace("[", "").replace("]", "").replace(" ", ""));
				mComponents.removeAll(components);
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
	}

}
