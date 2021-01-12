package jamesapps.example.flickrbrowser;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
//        Log.d(TAG, "onCreateOptionsMenu: " + getComponentName().toString());
//        Log.d(TAG, "onCreateOptionsMenu: hint: " + mSearchView.getQueryHint());
//        Log.d(TAG, "onCreateOptionsMenu: searchable info: " + searchableInfo.toString());

        // setting to false deselects the icon for search and automatically opens up the keyboard and search fields
        mSearchView.setIconified(false);

        // common pattern when passing a class to a single object (this handles the search)
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: called");

                // prepare the data for mainActivity (or other activities) to retrieve
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(FLICKR_QUERY, query).apply();

                // needed to change focus to mainActivity (mainActivity is called nonetheless)
                mSearchView.clearFocus();

                // close current activity and return to parent activity
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // leave this for the OS to handle
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // close current activity and return to parent activity
                finish();
                return false;
            }
        });

        Log.d(TAG, "onCreateOptionsMenu: returned true");
        return true;
    }
}