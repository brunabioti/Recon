package com.example.bioti.reconbioti.licenca;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.bioti.reconbioti.R;
import com.example.bioti.reconbioti.app.BaseActivity;
import com.example.bioti.reconbioti.face.ActivityFace;
import com.example.bioti.reconbioti.face.ActivitySobre;
import com.example.bioti.reconbioti.face.PreferenciasFace;
import com.example.bioti.reconbioti.net.ConnectivityHelper;
import com.example.bioti.reconbioti.util.FileUtils;
import com.example.bioti.reconbioti.util.IOUtils;
import com.neurotec.lang.NCore;
import com.neurotec.licensing.NLicense;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BioTi on 09/05/2015.
 */
public class AtivadorActivity extends BaseActivity {

    private static final String TAG = AtivadorActivity.class.getSimpleName();

    private AlertDialog alerta;
    private ListView mListView = null;
    private Ativacao mativacao = null;


    String folderPath = GerenciadorServicoLicenca.LICENSES_DIRECTORY;
    String name = null;
    String DeviceIDPath = null;
    String SerialNumberPath = null;
    String LicenseFilePath = null;


    //Cria um arraylist para as licenças que foram selecionadas ou ticadas na lista
    private List<Licenca> getSelectedLicenses() {
        List<Licenca> licenses = new ArrayList<Licenca>();
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        if (checkedItems != null) {
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    licenses.add((Licenca) mListView.getAdapter().getItem(checkedItems.keyAt(i)));
                }
            }
        } else {
            showInfo(R.string.selecione_licenca);
        }
        return licenses;
    }

    //Função que realiza a ativação das licenças selecionadas
    private void activate(List<Licenca> lista) {
        try {
            if (mativacao == null) {
                mativacao = new Ativacao();
                mativacao.activate(lista);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            showToast(e.getMessage());
        }
    }

    //localiza as licenças presentes no diretório e exibe na lista
    private void updateLicenses() {
        try {

            mListView.setAdapter(null);
            List<Licenca> licenses = GerenciadorServicoLicenca.getLicenses();
            if (licenses == null || licenses.isEmpty()) {
                showToast(R.string.msg_no_licenses);
            } else {
                mListView.setAdapter(new LicenseListAdapter(this, R.layout.license_list_item, licenses));
            }
        } catch (Exception e) {

            showToast(e.getMessage());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NCore.setContext(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ativador);
        mListView = (ListView) findViewById(R.id.list_view_serial_numbers);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //Pega as licenças do diretório de licenças e exibe na tela
        updateLicenses();
        findViewById(R.id.btn_ativar).setOnClickListener(geral_OnClickListener);


    }//fim onCreate()

    //Global On click listener for all views
    final View.OnClickListener geral_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {

            //Verifica se há conexão com a internet
            boolean conectado = ConnectivityHelper.isConnected(getApplicationContext());
            if (conectado == false) {
                showInfo(R.string.verifica_ativacao);
            }
            if (conectado == true) {

                switch (v.getId()) {
                    case R.id.btn_ativar:

                        List<Licenca> licenses = new ArrayList<Licenca>();
                        licenses = getSelectedLicenses();

                        //Remove da lista as licenças que já estão ativadas
                        for (int i = 0; i < licenses.size(); i++) {
                            String nome = licenses.get(i).toString();
                            LicenseFilePath = IOUtils.combinePath(folderPath, nome + GerenciadorServicoLicenca.EXTENSION_LICENSE_FILE);
                            if (new File(LicenseFilePath).exists()) {
                                showInfo("A licença" + nome + "já está ativada.");
                                licenses.remove(i);
                            }

                        }

                        //Se existe licenças para serem ativadas realiza a ativação
                        if (licenses != null && licenses.size() > 0) {
                            activate(licenses);
                        } else {
                            showInfo("Não há licenças para realizar a ativação");
                        }


                        break;

                }


            }
        }};


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_preferences:
                Intent intentpreferencia = new Intent(this, PreferenciasFace.class);
                startActivity(intentpreferencia);
                return false;
            case R.id.action_database:
                return false;
            case R.id.action_licensing:
                return false;
            case R.id.action_about:
                Intent intentsobre = new Intent(this, ActivitySobre.class);
                startActivity(intentsobre);
                return false;
            case R.id.action_face:
                Intent intentface = new Intent(this, ActivityFace.class);
                startActivity(intentface);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }


    private enum Task {
        ATIVADO, DESATIVADO, VERIFICANDO
    }

    private final class Ativacao extends AsyncTask<Boolean, String, String> {

        private Task mTask;
        private List<Licenca> mLicenses;


        void activate(List<Licenca> licenses) {
            if (licenses == null) throw new NullPointerException("licenses");
            if (licenses.isEmpty()) throw new IllegalArgumentException("licenses < 0");
            mTask = Task.ATIVADO;
            mLicenses = licenses;
            showProgress(getString(R.string.msg_activating));
            execute();
        }


        @Override
        protected String doInBackground(Boolean... params) {
            try {
                if (!isCancelled()) {
                    switch (mTask) {

                        case ATIVADO:

                            for (Licenca license : mLicenses) {
                                //license.activate(true);
                                name = license.toString();
                                DeviceIDPath = IOUtils.combinePath(folderPath, name + GerenciadorServicoLicenca.EXTENSION_DEVICE_ID_FILE);
                                SerialNumberPath = IOUtils.combinePath(folderPath, name + GerenciadorServicoLicenca.EXTENSION_SERIAL_NUMBER_FILE);
                                LicenseFilePath = IOUtils.combinePath(folderPath, name + GerenciadorServicoLicenca.EXTENSION_LICENSE_FILE);
                                try {
                                    String serialNumber = FileUtils.readPrintableCharacters(SerialNumberPath);
                                    String deviceID = NLicense.generateID(serialNumber);
                                    if (deviceID != null) {
                                        FileUtils.write(DeviceIDPath, deviceID);
                                        String licenca = NLicense.activateOnline(deviceID);
                                        if (license != null) {
                                            FileUtils.write(LicenseFilePath, licenca);
                                        }
                                    }
                                } catch (IOException e) {
                                    showError(e.getMessage());
                                }
                            }
                            return getString(true ? R.string.msg_activation_succeeded : R.string.msg_proceed_activation_online);

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
                return e.getMessage();
            }
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
            // mBackgroundTask = null;
             clearView();
        }
    }

    private void clearView() {
        mListView.clearChoices();
        updateLicenses();
    }
}







