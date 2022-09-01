package com.panniku.windowtranslator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class GalleryTest extends Activity {
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
        startActivity(intent);
    }
}
