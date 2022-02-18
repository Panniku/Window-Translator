package com.panniku.windowtranslator;

import android.app.PictureInPictureParams;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startService(new Intent(MainActivity.this, Icon.class));
            Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
            Log.d("WIDGET_STARTED: ", "ON VER.1");
        } else if(Settings.canDrawOverlays(MainActivity.this)){
            startService(new Intent(MainActivity.this, Icon.class));
            Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
        } else {
            askPermission();
            Toast.makeText(MainActivity.this, "Grant Permission to allow widget!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void askPermission() {
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}