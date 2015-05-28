package com.example.bioti.reconbioti.face;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bioti.reconbioti.R;
import com.example.bioti.reconbioti.app.BaseActivity;
import com.example.bioti.reconbioti.licenca.AtivadorActivity;

/**
 * Created by BioTi on 15/05/2015.
 */
public class ActivitySobre extends BaseActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);
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
                return false;
            case R.id.action_face:
                Intent intentface = new Intent(this, ActivityFace.class);
                startActivity(intentface);
                return false;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
