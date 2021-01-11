package jamesapps.example.flickrbrowser;

import android.os.Bundle;

// modifies query parameters for GetFlockJsonData
public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        activateToolbar(true);
    }
}