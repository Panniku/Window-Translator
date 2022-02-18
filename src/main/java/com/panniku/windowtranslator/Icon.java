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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;

public class Icon extends Service {

    private View windowView;
    private WindowManager window;

    private RelativeLayout iconView, iconTouch;
    private ImageView icon, inflateView;

    private final String prefName = "iconPref";

    private final String xPos = "xPos";
    private final String yPos = "yPos";

    private int lastXpos;
    private int lastYpos;
    private WindowManager.LayoutParams lastParams;

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
        inflateView = windowView.findViewById(R.id.inflateWindow);
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

        editor.apply();
        editor.commit();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        lastXpos = sharedPreferences.getInt(xPos, 0);
        lastYpos = sharedPreferences.getInt(yPos, 0);
    }
    private void updateView() {
        //
    }
}
