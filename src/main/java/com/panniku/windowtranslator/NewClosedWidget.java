//package com.panniku.windowtranslator;
//
//import androidx.annotation.Nullable;
//
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.PixelFormat;
//import android.os.Build;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.ImageView;
//
//public class NewClosedWidget extends Service {
//    private WindowManager mWindowManager;
//    private View mFloatingView;
//    private ImageView closedImage;
//
//
//    //null NewClosedWidget
//    public NewClosedWidget() {
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        Log.d("C-WIDGET_CREATED: ", "TRUE");
//        super.onCreate();
//
//
//        int LAYOUT_FLAG_TYPE;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LAYOUT_FLAG_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG_TYPE = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//
//        mFloatingView = LayoutInflater.from(this).inflate(R.layout.icon, null);
//        //layout params
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                LAYOUT_FLAG_TYPE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT
//        );
//        Log.d("WINDOW_CREATED", "YES!");
//        Log.d("WINDOW_MANAGER", "VALUES: " + WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
////        params.gravity = Gravity.TOP| Gravity.RIGHT;
////        params.x = 0;
////        params.y = 100;
//
//        //window services
//        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        mWindowManager.addView(mFloatingView, params);
//
//        try{
//            closedImage = mFloatingView.findViewById(R.id.smallLayoutIcon);
//        } catch (Error e) {
//            throw e;
//        }
//
//        mFloatingView.findViewById(R.id.smallLayoutIcon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                closedImage.setVisibility(View.GONE);
//            }
//        });
//
//        mFloatingView.findViewById(R.id.smallLayoutIcon).setOnTouchListener(new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//
//            //private TextView inputText;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //viewNum = 0;
//                //Log.d("VIEW_NUM_ON_TOUCH", "val: " + viewNum);
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        return true;
//
//                    case MotionEvent.ACTION_UP:
//                        return true;
//
//                    case MotionEvent.ACTION_MOVE:
//                        //movement
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        mWindowManager.updateViewLayout(mFloatingView, params);
//                        return true;
//                }
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(mFloatingView != null) mWindowManager.removeView(mFloatingView);
//    }
//}