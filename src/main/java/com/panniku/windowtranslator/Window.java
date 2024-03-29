package com.panniku.windowtranslator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.panniku.windowtranslator.Utils.CustomRelativeLayout;

public class Window extends Service {

    private RelativeLayout windowFrame, mainLayout;
    private ImageView minimizeButton, closeButton;

    private View windowView;
    private WindowManager window;

    private EditText inputText;
    private TextView resultText;
    private ImageView translate_textImage, clearInputImage, copyImage;

    Spinner lang1Spinner, lang2Spinner;

    // TO SAVE // DATA

    private final String prefName = "windowPref";

    private final String windowTheme = "windowTheme";
    //
    private final String lang1SpinnerLastCode = "lang1SpinnerLastCode";
    private final String lang2SpinnerLastCode = "lang2SpinnerLastCode";
    private final String lastInputText = "lastInputText";
    private final String lastResultText = "lastResultText";

    private final String thisLayout = "thisLayout";

    // TO LOAD // DATA //
    private static int lang1code, lang2code;
    private static String inpText, resText;

    private int toUseLayout;

    //misc
    WindowManager.LayoutParams lastParams;
    static boolean keyboardIsVisible = false;



    //
    //
    //
    String[] languageString = {
            "Select a language...",
            "Afrikaans",
            "Arabic",
            "Belarusian",
            "Bulgarian",
            "Bengali",
            "Catalan",
            "Czech",
            "Welsh",
            "Hindi",
            "Danish",
            "German",
            "Greek",
            "English",
            "Esperanto",
            "Spanish",
            "Estonian",
            "Persian",
            "Finnish",
            "French",
            "Irish",
            "Galician",
            "Gujarati",
            "Hebrew",
            "Hindi",
            "Croatian",
            "Haitian",
            "Hungarian",
            "Indonesian",
            "Icelandic",
            "Italian",
            "Japanese",
            "Georgian",
            "Kannada",
            "Korean",
            "Lithuanian",
            "Latvian",
            "Macedonian",
            "Marathi",
            "Malay",
            "Maltese",
            "Dutch",
            "Norwegian",
            "Polish",
            "Portuguese",
            "Romanian",
            "Russian",
            "Slovak",
            "Slovenian",
            "Albanian",
            "Swedish",
            "Swahili",
            "Tamil",
            "Telugu",
            "Thai",
            "Tagalog",
            "Turkish",
            "Ukrainian",
            "Urdu",
            "Vietnamese"
    };

    private static final int REQUEST_PERMISSION_CODE = 1;
    int Language1Code,Language2Code = 0;
    //

    //
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("col", "f: " +  Color.parseColor("#2196F3"));
        Log.d("col", "b: " +  Color.parseColor("#1B4565"));

        int LAYOUT_FLAG_TYPE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG_TYPE = WindowManager.LayoutParams.TYPE_PHONE;
        }

        toUseLayout = Icon.getNewLayout();

        int USE_LAYOUT = R.layout.window;
        if(toUseLayout == R.layout.window){
            USE_LAYOUT = R.layout.window;
            Log.d("USE_LAYOUT", "IS WINDOW");
        } else if(toUseLayout == R.layout.window_large) {
            USE_LAYOUT = R.layout.window_large;
            Log.d("USE_LAYOUT", "IS LARGE WINDOW");
        } else {
            Log.d("USE_LAYOUT", "INVALID");
        }

        windowView = LayoutInflater.from(this).inflate(USE_LAYOUT, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        window = (WindowManager) getSystemService(WINDOW_SERVICE);
        params.x = 0;
        params.y = 100;
        lastParams = params;
        window.addView(windowView, params);


        Log.d("windowManager", "params: " + params);

//        params.gravity = Gravity.TOP| Gravity.END|Gravity.START|Gravity.BO;
//        params.x = 0;
//        params.y = 100;

        Log.d("window", "View created.");

        windowFrame = windowView.findViewById(R.id.layoutContainer);
        mainLayout = windowView.findViewById(R.id.mainView);

        minimizeButton = windowView.findViewById(R.id.minimizeLayout);
        closeButton = windowView.findViewById(R.id.closeLayout);

        lang1Spinner = windowView.findViewById(R.id.windowLang1Spinner);
        lang2Spinner = windowView.findViewById(R.id.windowLang2Spinner);
        translate_textImage = windowView.findViewById(R.id.windowToTranslate);
        inputText = windowView.findViewById(R.id.toTranslate);

        resultText = windowView.findViewById(R.id.translatedResult);
        clearInputImage = windowView.findViewById(R.id.clearInputButton);
        copyImage = windowView.findViewById(R.id.copyButton);



        ArrayAdapter lang1Adapter = new ArrayAdapter(this, R.layout.spinner, languageString);
        lang1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang1Spinner.setAdapter(lang1Adapter);

        ArrayAdapter lang2Adapter = new ArrayAdapter(this, R.layout.spinner, languageString);
        lang2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang2Spinner.setAdapter(lang2Adapter);

        loadData();
        updateView();

        minimizeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        minimizeButton.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        minimizeButton.setBackgroundColor(Color.parseColor("#00000000"));
                        saveData();
                        stopSelf();
                        Intent i = new Intent(getApplicationContext(), Icon.class);
                        startService(i);
                }
                return false;
            }
        });
        closeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        closeButton.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        closeButton.setBackgroundColor(Color.parseColor("#00000000"));
                        saveData();
                        stopSelf();
                }
                return false;
            }
        });

        //

        lang1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language1Code = getLanguageCode(languageString[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lang2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Language2Code = getLanguageCode(languageString[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        translate_textImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        translate_textImage.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        translate_textImage.setBackgroundColor(Color.parseColor("#00000000"));
                        if(!keyboardIsVisible){
                            translate();
                        } else {
                            inputText.clearFocus();
                            hideKeyboard(inputText, params);
                            keyboardIsVisible = false;
                            translate();
                        }

                        return true;
                }
                return false;
            }
        });

        copyImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        copyImage.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        copyImage.setBackgroundColor(Color.parseColor("#00000000"));
                        //
                        if(!resultText.getText().toString().equals("")) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("resultText", resultText.getText().toString());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(Window.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Window.this, "Nothing to copy!", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                }
                return false;
            }
        });

        clearInputImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        copyImage.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        copyImage.setBackgroundColor(Color.parseColor("#00000000"));
                        //
                        inputText.setText("");
                        resultText.setText("");

                        return true;
                }
                return false;
            }
        });

        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("inputText", "Clicked input.");
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                window.updateViewLayout(windowView, params);
                //showKeyboard(inputText, params);
            }
        });
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.d("editorAction", "onEditorAction: Event: " + keyEvent);
                if(i == EditorInfo.IME_ACTION_DONE){
                    Log.d("inputText", "Exiting input.");
                    //hideKeyboard(inputText, params);
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    window.updateViewLayout(windowView, params);
                    if(inputText.getText().toString().isEmpty()){
                        resultText.setText("");
                    } else if(Language1Code == 0){
                        resultText.setText("Select the language to translate from!");
                    } else if(Language2Code == 0){
                        resultText.setText("Select the language to translate to!");
                    } else {
                        translate(Language1Code, Language2Code, inputText.getText().toString());
                    }
                }
                return false;
            }
        });

        // MOVEMENT
        windowFrame.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.d("onTouch()", "Moving...");
                Log.d("onTouch()", "Event: " + motionEvent);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        Log.d("onTouch()", "ACTION_DOWN");
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        window.updateViewLayout(windowView, params);
                        Log.d("onTouch()", "ACTION_MOVE");
                        return true;
                }
                return false;
            }
        });
        inputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("keyListener", "onKey: Event: "+ keyEvent);
                Log.d("keyListener", "onKey: EventAction: "+ keyEvent.getAction());
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Log.d("keyListener", "onKey: Pressed \"Back\"");
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    window.updateViewLayout(windowView, params);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(windowView != null){
            saveData();
            window.removeView(windowView);
        }
    }

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(windowTheme, toUseLayout);

        editor.putString(lastInputText, inputText.getText().toString());
        editor.putString(lastResultText, resultText.getText().toString());

        editor.putInt(lang1SpinnerLastCode, lang1Spinner.getSelectedItemPosition());
        editor.putInt(lang2SpinnerLastCode, lang2Spinner.getSelectedItemPosition());

        editor.putInt(thisLayout, toUseLayout);

        editor.apply();
        editor.commit();

        Log.d("saveData()", "1Code: " + lang1Spinner.getSelectedItemPosition());
        Log.d("saveData()", "2Code: " + lang2Spinner.getSelectedItemPosition());

        Log.d("saveData()", "Layout: " + toUseLayout);

        Log.d("saveData()", "Saving data...");
    }
    private void loadData() {
        SharedPreferences preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        inpText = preferences.getString(lastInputText, "");
        resText = preferences.getString(lastResultText, "");

        lang1code = preferences.getInt(lang1SpinnerLastCode, 0);
        lang2code = preferences.getInt(lang2SpinnerLastCode, 0);

        toUseLayout = preferences.getInt(thisLayout, R.layout.window);
        Log.d("loadData()", "toUseLayout: " + toUseLayout);

        Log.d("loadData()", "Loading data...");

    }

    private void updateView() {

        lang1Spinner.setSelection(lang1code);
        lang2Spinner.setSelection(lang2code);
        Log.d("updateView()", "Set spinner selection.");

        inputText.setText(inpText);
        resultText.setText(resText);

//        Log.d("updateView()", "fU: " + toLoadFrameCol);
//        Log.d("updateView()", "bU: " + toLoadBaseCol);
        Log.d("updateView()", "Updating view...");
    }

    //

    //
    public int getLanguageCode(String language) {
        int LanguageCode = 0;
        switch (language){
            //longest cases of languageCode
            //help me im tired
            case "Afrikaans":
                LanguageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                LanguageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Belarusian":
                LanguageCode = FirebaseTranslateLanguage.BE;
                break;
            case "Bulgarian":
                LanguageCode = FirebaseTranslateLanguage.BG;
                break;
            case "Bengali":
                LanguageCode = FirebaseTranslateLanguage.BN;
                break;
            case "Catalan":
                LanguageCode = FirebaseTranslateLanguage.CA;
                break;
            case "Czech":
                LanguageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Welsh":
                LanguageCode = FirebaseTranslateLanguage.CY;
                break;
            case "Danish":
                LanguageCode = FirebaseTranslateLanguage.DA;
                break;
            case "German":
                LanguageCode = FirebaseTranslateLanguage.DE;
                break;
            case "Greek":
                LanguageCode = FirebaseTranslateLanguage.EL;
                break;
            case "English":
                LanguageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Esperanto":
                LanguageCode = FirebaseTranslateLanguage.EO;
                break;
            case "Spanish":
                LanguageCode = FirebaseTranslateLanguage.ES;
                break;
            case "Estonian":
                LanguageCode = FirebaseTranslateLanguage.ET;
                break;
            case "Persian":
                LanguageCode = FirebaseTranslateLanguage.FA;
                break;
            case "Finnish":
                LanguageCode = FirebaseTranslateLanguage.FI;
                break;
            case "French":
                LanguageCode = FirebaseTranslateLanguage.FR;
                break;
            case "Irish":
                LanguageCode = FirebaseTranslateLanguage.GA;
                break;
            case "Galician":
                LanguageCode = FirebaseTranslateLanguage.GL;
                break;
            case "Gujarati":
                LanguageCode = FirebaseTranslateLanguage.GU;
                break;
            case "Hebrew":
                LanguageCode = FirebaseTranslateLanguage.HE;
                break;
            case "Hindi":
                LanguageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Croatian":
                LanguageCode = FirebaseTranslateLanguage.HR;
                break;
            case "Haitian":
                LanguageCode = FirebaseTranslateLanguage.HT;
                break;
            case "Hungarian":
                LanguageCode = FirebaseTranslateLanguage.HU;
                break;
            case "Indonesian":
                LanguageCode = FirebaseTranslateLanguage.ID;
                break;
            case "Icelandic":
                LanguageCode = FirebaseTranslateLanguage.IS;
                break;
            case "Italian":
                LanguageCode = FirebaseTranslateLanguage.IT;
                break;
            case "Japanese":
                LanguageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Georgian":
                LanguageCode = FirebaseTranslateLanguage.KA;
                break;
            case "Kannada":
                LanguageCode = FirebaseTranslateLanguage.KN;
                break;
            case "Korean":
                LanguageCode = FirebaseTranslateLanguage.KO;
                break;
            case "Lithuanian":
                LanguageCode = FirebaseTranslateLanguage.LT;
                break;
            case "Latvian":
                LanguageCode = FirebaseTranslateLanguage.LV;
                break;
            case "Macedonian":
                LanguageCode = FirebaseTranslateLanguage.MK;
                break;
            case "Marathi":
                LanguageCode = FirebaseTranslateLanguage.MR;
                break;
            case "Malay":
                LanguageCode = FirebaseTranslateLanguage.MS;
                break;
            case "Maltese":
                LanguageCode = FirebaseTranslateLanguage.MT;
                break;
            case "Dutch":
                LanguageCode = FirebaseTranslateLanguage.NL;
                break;
            case "Norwegian":
                LanguageCode = FirebaseTranslateLanguage.NO;
                break;
            case "Polish":
                LanguageCode = FirebaseTranslateLanguage.PL;
                break;
            case "Portuguese":
                LanguageCode = FirebaseTranslateLanguage.PT;
                break;
            case "Romanian":
                LanguageCode = FirebaseTranslateLanguage.RO;
                break;
            case "Russian":
                LanguageCode = FirebaseTranslateLanguage.RU;
                break;
            case "Slovak":
                LanguageCode = FirebaseTranslateLanguage.SK;
                break;
            case "Slovenian":
                LanguageCode = FirebaseTranslateLanguage.SL;
                break;
            case "Albanian":
                LanguageCode = FirebaseTranslateLanguage.SQ;
                break;
            case "Swedish":
                LanguageCode = FirebaseTranslateLanguage.SV;
                break;
            case "Swahili":
                LanguageCode = FirebaseTranslateLanguage.SW;
                break;
            case "Tamil":
                LanguageCode = FirebaseTranslateLanguage.TA;
                break;
            case "Telugu":
                LanguageCode = FirebaseTranslateLanguage.TE;
                break;
            case "Thai":
                LanguageCode = FirebaseTranslateLanguage.TH;
                break;
            case "Tagalog":
                LanguageCode = FirebaseTranslateLanguage.TL;
                break;
            case "Turkish":
                LanguageCode = FirebaseTranslateLanguage.TR;
                break;
            case "Ukrainian":
                LanguageCode = FirebaseTranslateLanguage.UK;
                break;
            case "Urdu":
                LanguageCode = FirebaseTranslateLanguage.UR;
                break;
            case "Vietnamese":
                LanguageCode = FirebaseTranslateLanguage.VI;
                break;

        }
        return LanguageCode;
    }

    private void translate(int lang1code, int lang2code, String text) {
        resultText.setText("Downloading Modals...");

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(lang1code)
                .setTargetLanguage(lang2code)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                resultText.setText("");
                translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        resultText.setText(s);
                        //Toast.makeText(MainActivity.this, "Translated: " + s, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultText.setText("Failed to translate!\nPlease try again later...");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                resultText.setText("Failed to download modals!\nModals are the \"Models\" that translate the text!\nPlease do not stop their installation, as it will cause this error.");
            }
        });
    }

    private void showKeyboard(EditText toEdit, WindowManager.LayoutParams params){
        keyboardIsVisible = true;
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(toEdit, InputMethodManager.SHOW_IMPLICIT);
        toEdit.requestFocus();
        Log.d("showKeyboard()", "Keyboard initialized.");
    }
    private void hideKeyboard(EditText toEdit, WindowManager.LayoutParams params){
        keyboardIsVisible = false;
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(toEdit.getWindowToken(), 0);
        toEdit.clearFocus();
        Log.d("showKeyboard()", "Keyboard terminated.");
    }
    private void translate(){
        if(inputText.getText().toString().isEmpty()){
            resultText.setText("");
        } else if(Language1Code == 0){
            resultText.setText("Select the language to translate from!");
        } else if(Language2Code == 0){
            resultText.setText("Select the language to translate from!");
        } else {
            translate(Language1Code, Language2Code, inputText.getText().toString());
        }
    }
}
