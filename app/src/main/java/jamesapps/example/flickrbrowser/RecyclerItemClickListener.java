package jamesapps.example.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    // TAG must be less than 23 chars in length; this is handled by default
    private static final String TAG = "RecyclerItemClickListen";

    // these will form callbacks in MainActivity
    interface OnRecycleClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private final OnRecycleClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecycleClickListener listener) {
        mListener = listener;

        // a GestureDetector determines what sort of interaction was applied and then call other methods based on the type of gesture
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: started");

                //get the View (ConstrainedView, TextView, etc.) and store in childView (we can now respond based on View type)
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null){
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }

                // declare that the onSingleTap event has been triggered
                // this means that other singleTap events cannot be triggered and run other methods
                // to enable more single taps before the finger is released, return false
                return true;
            }

            // don't return anything here, possibly (?) because there are other events that could
            // be triggered after a long press, e.g. scrolling
            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: started");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null){
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: touch event received");
        if (mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned " + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
    }


}
