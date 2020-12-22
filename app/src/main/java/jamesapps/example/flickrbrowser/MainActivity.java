package jamesapps.example.flickrbrowser;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements GetRawData.OnDownloadComplete {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // tie this class using getRawData (this also triggers the life cycle of GetRawData)
        GetRawData getRawData = new GetRawData(this);

        // running execute starts the lifecycle of GetRawData (derived from ASyncTask) and calls onDownloadComplete()
        // MainActivity calls execute() which in turn leads to a callback with onDownloadComplete() with GetRawData's data
        getRawData.execute(
                "https://www.flickr.com/services/feeds/photos_public.gne?tags=android,nougat&tagmode=any&format=json&nojsoncallback=1");   // see README

        Log.d(TAG, "onCreate: ended");
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

    public void onDownloadComplete(String data, DownloadStatus status){
        if (status == DownloadStatus.OK){
            Log.d(TAG, "onDownloadComplete: data is " + data);
        } else {
            Log.e(TAG, "onDownloadComplete failed with status " + status);
        }
    }
}