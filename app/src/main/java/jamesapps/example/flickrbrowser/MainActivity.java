package jamesapps.example.flickrbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable,
        RecyclerItemClickListener.OnRecycleClickListener {
    private static final String TAG = "MainActivity";
    private FlickrRecyclerViewAdapter mFlickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // pass false since the Home button is not required
        activateToolbar(false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        mFlickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(mFlickrRecyclerViewAdapter);

        Log.d(TAG, "onCreate: ended");
    }

    // note the Android lifecycle: onCreate then [onStart**, onResume* ... , onPause*, onStop (branch to onRestart**)] then onDestroy
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: started");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(
                this,"https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
        getFlickrJsonData.execute("android,nougat");
        Log.d(TAG, "onResume: ended");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // this is a callback function that is called outside of MainActivity
    public void onDataAvailable(List<Photo> data, DownloadStatus status){
        Log.d(TAG, "onDataAvailable: started");
        if (status == DownloadStatus.OK){
            mFlickrRecyclerViewAdapter.loadNewData(data);
        } else {
            Log.e(TAG, "onDataAvailable failed with status " + status);
        }

        Log.d(TAG, "onDataAvailable: ended");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItem: started");
        Toast.makeText(MainActivity.this, "Normal tap at " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: started");

        // intents are operations to be performed
        Intent intent = new Intent(this, PhotoDetailActivity.class);

        // add data to intent, to link the key with the selected photo; the key can be used to retrieve the photo later
        // the data passed must be serialisable
        intent.putExtra(PHOTO_TRANSFER, mFlickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}