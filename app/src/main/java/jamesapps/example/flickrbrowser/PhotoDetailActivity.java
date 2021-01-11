package jamesapps.example.flickrbrowser;

import android.os.Bundle;

// Displays a single photo image
public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        activateToolbar(true);
    }
}