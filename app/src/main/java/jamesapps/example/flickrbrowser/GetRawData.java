package jamesapps.example.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK}

class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";

    private DownloadStatus  mDownloadStatus;    // m for member variable (or use 'downloadStatus')
    private final OnDownloadComplete mCallBack;

    // use a nested interface to ensure getRawData objects can call a method onDownloadComplete
    // onDownloadComplete can be implemented by any class, not just MainActivity
    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callback) {
        mDownloadStatus = DownloadStatus.IDLE;

        // link this class and objects with MainActivity class
        mCallBack = callback;
    }

    // run the method doInBackground on the same background thread as the calling function (i.e. GetFlickrJsonData's doInBackground())
    void runInSameThread(String s) {
        Log.d(TAG, "runInSameThread starts");

        if(mCallBack != null) {
            mCallBack.onDownloadComplete(doInBackground(s), mDownloadStatus);
        }

        Log.d(TAG, "runInSameThread ends");
    }

    @Override
    protected void onPostExecute(String JSONstring) {
        // s should be the json feed, the returned String of doInBackground()
        Log.d(TAG, "GetRawData onPostExecute: JSONstring length = " + JSONstring.length());

        // if mCallback is linked to MainActivity, then let GetRawData object to call MainActivity methods (onDownloadComplete())
        if (mCallBack != null){
            mCallBack.onDownloadComplete(JSONstring, mDownloadStatus);
        }
        Log.d(TAG, "GetRawData onPostExecute: ended");
    }

    // pass an array of Strings (an example of a Varargs, variable-length arguments; note that it is assumed that the array of the same type)
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: started");
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (strings == null){
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        try {
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");     // GET would be default anyway
            connection.connect();

            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: response code: " + response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // transfer reader to result; note that readLine() removes newline characters, hence the added append()
            // using null first is a convention not used in Java; however, it attempts to emphasise what could be null
            String line;
            while (null != (line = reader.readLine())){
                result.append(line).append("\n");
            }

            // alternatively with a for loop
            //   for(String line = reader.readLine(); line != null; line = reader.readLine()){ }


            mDownloadStatus = DownloadStatus.OK;
            return result.toString();

        } catch (MalformedURLException e){
            // note that Log.e messages are error logging and saved at runtime
            // Log.d messages are debug logging (therefore only viewable at compile time)
            Log.e(TAG, "doInBackground: invalid URL " + e.getMessage());
        } catch (IOException e){
            Log.e(TAG, "doInBackground: IO exception reading data " + e.getMessage());
        } catch (SecurityException e){
            Log.e(TAG, "doInBackground: security exception, permission needed " + e.getMessage());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e){
                    Log.e(TAG, "doInBackground: error closing stream" + e.getMessage());
                }
            }
        }

        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }


}
