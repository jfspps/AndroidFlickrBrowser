# Flickr Browser

## URL string parsing notes

Use ? to set parameters, separating multiple parameters with &

When requiring standard JSON data, use `format=json&nojsoncallback=1`. Other parameters such as `lang` can be added to give:

`https://www.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1&lang=zh-hk`

Adding tags to narrow results returned is exemplified with:

`https://www.flickr.com/services/feeds/photos_public.gne?tags=android,nougat&tagmode=any&format=json&nojsoncallback=1`

## The Android lifecycle

More info related to the execution of activities and associated callbacks are provided [here](https://developer.android.com/guide/components/activities/activity-lifecycle)

In summary, an activity launch triggers a set of callbacks, in order, `onCreate()`, `onStart()`, `onResume()`*, ACTIVITY RUNNING, `onPause()`, `onStop()` and `onDestroy()`. Methods `onPause()` and `onResume()` are paired, as are `onStop()` and `onStart()`. When `onStop()` is called, another callback `onRestart()` is called before returning control to `onStart()`. The callback `onPause()` is called when another activity comes into focus.

In FlickrBrowser, the lifecycle is exemplified by the following methods:

+ [MainActivity](/app/src/main/java/jamesapps/example/flickrbrowser/MainActivity.java) UI thread
  + onCreate() starts and terminates
  + onResume() builds an instance of [GetFlickrJsonData](/app/src/main/java/jamesapps/example/flickrbrowser/GetFlickrJsonData.java). Since GetFlickrJsonData extends ASyncTask it starts a separate thread (below)
  + GetFlickrJsonData objects include a property that represents an object with access to onDataAvailable(), which is passed as a MainActivity object that defines onDataAvailable()
  + Other properties in GetFlickrJsonData are the feed URL, language options and match parameters (match all tags, i.e. AND, or match any, i.e. OR)
  + GetFlickrJsonData's execute() runs on its own thread (below)
  + Concurrently, MainActivity's onResume() then terminates (often before GetFlickJsonData thread is completed)

+ GetFlickrJsonData (extends ASyncTask) separate thread
  + The construction of GetFlickrJsonData triggers doInBackground() on its own thread. Essentially, it uses [GetRawData](/app/src/main/java/jamesapps/example/flickrbrowser/GetRawData.java) to retrieve raw JSON using the URL given. The GetRawData object holds a reference to GetFlickrJsonData and can call GetFlickrJsonData's onDownloadComplete().
  + GetRawData (extends AsyncTask)
  + The construction of GetRawData triggers nothing special.
  + GetRawData's runInSameThread() runs its doInBackground() method on the same thread as GetFlickrJsonData's doInBackground().
  + GetRawData doInBackground() makes a HTTP GET request and stores the download into a BufferedReader and returns the JSON string to postExecute()
  + GetRawData postExecute() then calls GetFlickrJsonData's onDownloadComplete
  + GetFlickrJsonData onDownloadComplete() parses the JSON 'array structure' and builds a List<Photo>, each element with properties based on JSON properties (title, author, authorId, link, tags, photoUrl)
  + Program control then terminates from GetRawData
  + GetFlickrJsonData then terminates its doInBackground()
  + GetFlickrJsonData's onPostExecute() then runs
  + The last callback function is MainActivity's onDataAvailable()
  + Program control then terminates from GetFlickrJsonData
