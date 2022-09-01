package com.panniku.windowtranslator.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class CustomRelativeLayout extends RelativeLayout {

    Context context;

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if(event.getAction() == KeyEvent.KEYCODE_BACK){
            Log.d("CustomRelativeLayout", "PRESSED BACK.");
            return true;
        }
        return super.dispatchKeyEventPreIme(event);
    }
}
