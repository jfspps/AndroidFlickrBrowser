package jamesapps.example.flickrbrowser;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;

    private final OnDataAvailable mCallBack;

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

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
                    // _m is smaller for browsers with carousels, _b is larger
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
            // send callback if all done (update the caller of GetFlickrJsonData)
            mCallBack.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete: ended");
    }

    public GetFlickrJsonData(OnDataAvailable callBack, String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }

    // called by MainActivity on the same thread
    void executeOnSameThread(String search){
        Log.d(TAG, "executeOnSameThread: started");

        // get the JSON link
        String destinationUri = createUri(search, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: ended");
    }

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


}
