package com.panniku.windowtranslator.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    Context context;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
//        if(mSearchActivity != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
//            KeyEvent.DispatcherState state = getKeyDispatcherState();
//
//            if(state != null){
//                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
//
//                    InputMethodManager mgr = (InputMethodManager)
//                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
//                }
//                else if(event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled() && state.isTracking(event)){
//                    mSearchActivity.onBackPressed();
//                    return true;
//                }
//            }
//        }


        return super.dispatchKeyEventPreIme(event);
    }
}
