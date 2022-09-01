package com.panniku.windowtranslator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class Icon extends Service {

    private View windowView;
    private WindowManager window;

    private RelativeLayout iconView;
    private ImageView closeView;
    private ImageView inflateView;
    private ImageView setLayout;

    private final String prefName = "iconPref";

    public static final String thisBool = "thisBool";

    private final String xPos = "xPos";
    private final String yPos = "yPos";

    private int lastXpos;
    private int lastYpos;
    private WindowManager.LayoutParams lastParams;

    //
    public static int useLayout; // layout to save
    private boolean ifLarge; // bool to save
    private boolean mLarge; // bool to assign loaded data

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        if(iconView != null){
            iconView.setVisibility(View.VISIBLE);
        }
        super.onCreate();

        int LAYOUT_FLAG_TYPE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG_TYPE = WindowManager.LayoutParams.TYPE_PHONE;
        }

        windowView = LayoutInflater.from(this).inflate(R.layout.icon, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        window = (WindowManager) getSystemService(WINDOW_SERVICE);
        loadData();
        params.x = lastXpos;
        params.y = lastYpos;
        params.alpha = 0.75f;
        window.addView(windowView, params);
        lastParams = params;
//        params.gravity = Gravity.TOP| Gravity.END|Gravity.START|Gravity.BO;
//        params.x = 0;
//        params.y = 100;

        Log.d("window", "View created.");

        iconView = windowView.findViewById(R.id.iconView);
        closeView = windowView.findViewById(R.id.closeIcon);
        inflateView = windowView.findViewById(R.id.inflateWindow);
        setLayout = windowView.findViewById(R.id.setLayout);

        closeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        closeView.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        closeView.setBackgroundColor(Color.parseColor("#00000000"));
                        saveData();
                        stopSelf();
                }
                return false;
            }
        });

        inflateView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        inflateView.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        saveData();
                        stopSelf();
                        Intent i = new Intent(getApplicationContext(), Window.class);
                        startService(i);
                }
                return false;
            }
        });

        setLayout.setOnTouchListener(new View.OnTouchListener() {
            private boolean isLarge = mLarge;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        setLayout.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
                        return true;

                    case MotionEvent.ACTION_UP:
                        setLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        if(!isLarge){
                            isLarge = true;
                            ifLarge = true;
                            useLayout = R.layout.window_large;
                            Toast.makeText(Icon.this, "Window will be LARGE.", Toast.LENGTH_SHORT).show();
                        } else {
                            isLarge = false;
                            ifLarge = false;
                            useLayout = R.layout.window;
                            Toast.makeText(Icon.this, "Window will be SMALL.", Toast.LENGTH_SHORT).show();
                        }
                }
                return false;
            }
        });

        iconView.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.d("onTouch()", "Moving...");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(window != null){
            saveData();
            window.removeView(windowView);
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(xPos, lastParams.x);
        editor.putInt(yPos, lastParams.y);

        //editor.putInt(thisLayout, useLayout);
        editor.putBoolean(thisBool, ifLarge);
        Log.d("saveData()", "toUseLayout: " + useLayout);

        editor.apply();
        editor.commit();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        lastXpos = sharedPreferences.getInt(xPos, 0);
        lastYpos = sharedPreferences.getInt(yPos, 0);

        mLarge = sharedPreferences.getBoolean(thisBool, false);
    }

    public static int getNewLayout(){
        return useLayout;
    }

}
