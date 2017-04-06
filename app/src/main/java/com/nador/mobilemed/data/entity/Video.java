package com.nador.mobilemed.data.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

/**
 * Created by nador on 06/08/16.
 */
public class Video {

    private Uri mURI;
    // Duration in seconds
    private long mDuration;
    // Video thumbnail
    private Bitmap mThumbnail;

    public Video(final Context context, final Uri url) {
        mURI = url;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(mURI.toString)(, new HashMap<>());
        retriever.setDataSource(context, mURI);
        mDuration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        mThumbnail = retriever.getFrameAtTime();
        retriever.release();
    }

    public Uri getURI() {
        return mURI;
    }

    public void setURI(Uri uri) {
        this.mURI = uri;
    }

    public long getDuration() {
        return mDuration;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public String getDurationString() {
        return String.valueOf(mDuration / 60) + ":" + String.valueOf(mDuration % 60);
    }
}
