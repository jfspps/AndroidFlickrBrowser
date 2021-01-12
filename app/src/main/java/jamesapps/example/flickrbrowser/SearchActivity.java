package jamesapps.example.flickrbrowser;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

// modifies query parameters for GetFlockJsonData
public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        activateToolbar(true);
        Log.d(TAG, "onCreate: ended");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: started");
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // provide system search services
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);
        Log.d(TAG, "onCreateOptionsMenu: " + getComponentName().toString());
        Log.d(TAG, "onCreateOptionsMenu: hint: " + mSearchView.getQueryHint());
        Log.d(TAG, "onCreateOptionsMenu: searchable info: " + searchableInfo.toString());

        mSearchView.setIconified(true);

        Log.d(TAG, "onCreateOptionsMenu: returned true");
        return true;
    }
}