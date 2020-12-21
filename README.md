# Flickr Browser

## URL string parsing notes

Use ? to set parameters, separating multiple parameters with &

When requiring standard JSON data, use `format=json&nojsoncallback=1`. Other parameters such as `lang` can be added to give:

`https://www.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1&lang=zh-hk`

Adding tags to narrow results returned is exemplified with:

`https://www.flickr.com/services/feeds/photos_public.gne?tags=android,nougat&tagmode=any&format=json&nojsoncallback=1`
