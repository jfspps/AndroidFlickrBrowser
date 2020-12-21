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

    public GetRawData() {
        mDownloadStatus = DownloadStatus.IDLE;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: parameter = " + s);
    }

    @Override
    protected String doInBackground(String... strings) {
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

            String line;
            // transfer reader to result; note that readLine() removes newline characters, hence the added append()
            // using null first is a convention not used in Java; however, it attempts to emphasise what could be null
            while (null != (line = reader.readLine())){
                result.append(line).append("\n");
            }

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
