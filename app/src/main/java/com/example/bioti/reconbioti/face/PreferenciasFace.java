package com.example.bioti.reconbioti.face;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import com.example.bioti.reconbioti.R;
import com.example.bioti.reconbioti.licenca.AtivadorActivity;
import com.example.bioti.reconbioti.view.BasePreferenceFragment;
import com.neurotec.biometrics.NBiometricEngine;
import com.neurotec.biometrics.NMatchingSpeed;
import com.neurotec.biometrics.NTemplateSize;

public class PreferenciasFace extends PreferenceActivity {

    //Definição dos parâmetros do reconhecimento facial

    public static final String FACE_ENROLLMENT_CHECK_FOR_DUPLICATES = "face_enrollment_check_for_duplicates";
    public static final String MATCHING_SPEED = "face_matching_speed";   //velocidade da extração
    public static final String MATCHING_THRESHOLD = "face_matching_threshold"; //percentual aceitável de falsos positivos

    public static final String TEMPLATE_SIZE = "face_template_size"; //
    public static final String MIN_IOD = "face_min_iod"; //minima distancia interocular

    public static final String CONFIDENCE_THRESHOLD = "face_confidence_threshold"; //Define o threshold de confiança da distancia interocular
    // Reconhecimentos com valores de threshold abaixo desse valor serão ignorados.

    public static final String QUALITY_THRESHOLD = "face_quality_threshold";//Define o limite mínimo de qualidade da imagem.
    // Valores abaixo desse limite não serão considerados.

    public static final String MAXIMAL_YAW = "face_maximal_yaw";//Define o máximo ângulo yaw.
    public static final String MAXIMAL_ROLL = "face_maximal_roll";//Define o máximo ângulo roll

    public static final String DETECT_ALL_FEATURE_POINTS = "face_detect_all_feature_points"; //Define se todos os pontos característicos devem ser detectados.
    // O valor padrão é falso.
    public static final String DETECT_BASE_FEATURE_POINTS = "face_detect_base_feature_points";

    public static final String DETERMINE_GENDER = "face_determine_gender";//Define se o gênero do rosto deve ser detectado.
    public static final String DETECT_PROPERTIES = "face_detect_properties";//- Define se as propriedades faciais devem ser detectado.
    public static final String RECOGNIZE_EXPRESSION = "face_recognize_expression";
    public static final String RECOGNIZE_EMOTION = "face_recognize_emotion";

    public static final String CREATE_THUMBNAIL = "face_create_thumbnail";
    public static final String THUMBNAIL_WIDTH = "face_thumbnail_width";

    //	public static final String CAPTURE_DEVICE = "face_capture_device";
    public static final String SET_DEFAULT_PREFERENCES = "face_set_default_preferences";


    public static void updateClient(NBiometricEngine biometric, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        biometric.setFacesMatchingSpeed(NMatchingSpeed.get(Integer.valueOf(preferences.getString(MATCHING_SPEED, String.valueOf(NMatchingSpeed.LOW.getValue()) ))));
        biometric.setMatchingThreshold(Integer.valueOf(preferences.getString(MATCHING_THRESHOLD, "24")));
        biometric.setFacesTemplateSize(NTemplateSize.get(Integer.valueOf(preferences.getString(TEMPLATE_SIZE, String.valueOf(NTemplateSize.SMALL.getValue())))));
        biometric.setFacesMinimalInterOcularDistance(preferences.getInt(MIN_IOD, 40));
        biometric.setFacesConfidenceThreshold((byte) preferences.getInt(CONFIDENCE_THRESHOLD, 50));
        biometric.setFacesQualityThreshold((byte) preferences.getInt(QUALITY_THRESHOLD, 50));

        biometric.setFacesMaximalYaw(Float.valueOf(preferences.getInt(MAXIMAL_YAW, 15)));
        biometric.setFacesMaximalRoll(Float.valueOf(preferences.getInt(MAXIMAL_ROLL, 15)));

        biometric.setFacesDetectAllFeaturePoints(preferences.getBoolean(DETECT_ALL_FEATURE_POINTS, true));
        biometric.setFacesDetectBaseFeaturePoints(preferences.getBoolean(DETECT_BASE_FEATURE_POINTS, true));
        biometric.setFacesDetermineGender(preferences.getBoolean(DETERMINE_GENDER, true));
        biometric.setFacesDetectProperties(preferences.getBoolean(DETECT_PROPERTIES, false));
        biometric.setFacesRecognizeExpression(preferences.getBoolean(RECOGNIZE_EXPRESSION, false));
        biometric.setFacesRecognizeEmotion(preferences.getBoolean(RECOGNIZE_EMOTION, false));

        biometric.setFacesCreateThumbnailImage(preferences.getBoolean(CREATE_THUMBNAIL, false));
        biometric.setFacesThumbnailImageWidth(preferences.getInt(THUMBNAIL_WIDTH, 120));

    }

    public static boolean isCheckForDuplicates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(FACE_ENROLLMENT_CHECK_FOR_DUPLICATES, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FacePreferencesFragment()).commit();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_preferences:
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
                Intent intentface = new Intent(this, ActivityFace.class);
                startActivity(intentface);
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FacePreferencesFragment extends BasePreferenceFragment {

        // ===========================================================
        // Public methods
        // ===========================================================

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
			getPreferenceManager().setSharedPreferencesName("my_preferences");
     		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.face_preferences);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals(SET_DEFAULT_PREFERENCES)) {
                //TO DO: Check for better UI update method
                preferenceScreen.getEditor().clear().commit();
                getFragmentManager().beginTransaction().replace(android.R.id.content, new FacePreferencesFragment()).commit();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}


