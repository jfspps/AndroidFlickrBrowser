package jamesapps.example.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null;
    private final String mBaseURL;
    private final String mLanguage;
    private final boolean mMatchAll;

    private final OnDataAvailable mCallBack;

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(OnDataAvailable callBack, String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData object built");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }

    // this is called by GetRawData when the URL has been processed
    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: started with status " + status);

        if (status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(data);     // primary level
                JSONArray itemsArray = jsonData.getJSONArray("items");

                // the entire JSON file is comprised of an array of images, so cycle through them with i
                for (int i = 0; i < itemsArray.length(); i++){
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);

                    // see Flickr JSON return for properties
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    // edit the link so that the image is more suitable for mobile screens
                    // _m is a postfix for smaller photos for browsers with carousels, _b is larger
                    // the period signifies the end of the file name
                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete() called, " + photoObject.toString());
                }
            }
            catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: error processing JSON data: " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mCallBack != null){
            Log.d(TAG, "onDownloadComplete: mCallback from GetFlickrJsonData");
            // send callback if all done (update the caller of GetFlickrJsonData)
            mCallBack.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete: ended");
    }

    // runs when GetFlickrJsonData is instantiated
    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: started");
        String desinationUri = createUri(params[0], mLanguage, mMatchAll);
        GetRawData getRawData = new GetRawData(this);

        // runInSameThread() passes destinationUri to GetRawData.doInBackground() to build a JSONString,
        // which is then passed to onDownloadComplete(), above, to build this class' mPhotoList, all on GetFlickrJsonData's thread
        getRawData.runInSameThread(desinationUri);

        Log.d(TAG, "doInBackground: ended");
        return mPhotoList;
    }

    // helper to doInBackground()
    private String createUri(String searchCriteria, String language, boolean matchAll) {
        Log.d(TAG, "createUri: started");

        // see JSON format for more properties
        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagMode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", language)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }

    //runs after doInBackground, passing mPhotoList to MainActivity
    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: started");

        if (mCallBack != null){
            Log.d(TAG, "onPostExecute: mCallback from GetFlickrJsonData");
            mCallBack.onDataAvailable(mPhotoList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: finished");
    }
}
