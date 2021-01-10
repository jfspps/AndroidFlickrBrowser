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
        void onItem(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private final OnRecycleClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecycleClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: touch event received");
        if (mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned" + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
    }


}
