package jamesapps.example.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotoList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photoList) {
        mContext = context;
        mPhotoList = photoList;
    }

    // create a new ViewHolder in preparation for the LayoutInflater
    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        // do not add to the parent layout, hence pass false
        // (passing null as a second parameter signifies the parent is unknown, preventing LayoutInflater from knowing the parent styling)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    // use Picasso (image download and caching library)
    // used by the layout manager to add data to an existing row
    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        if ((mPhotoList == null) || (mPhotoList.size() == 0)){
            // nothing found, inform the user
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(R.string.empty_photo_list);
        } else {
            // photos found, build the list
            Photo photoItem = mPhotoList.get(position);

            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " : " + position);

            //Picasso is a singleton; load the image, set errors if necessary, the placeholder and then insert
            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            holder.title.setText(photoItem.getTitle());
        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: called");

        // minor lie, add one card with the 'not found' feedback!
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.size() : 1);
    }

    void loadNewData(List<Photo> photos){
        mPhotoList = photos;

        // inform the observers to be prepared for new changes
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }

    // behaves like all other top-level classes
    // handles and recycles the ImageViewHolders
    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail;
        TextView title;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: started");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.imageTitle);
        }
    }
}
