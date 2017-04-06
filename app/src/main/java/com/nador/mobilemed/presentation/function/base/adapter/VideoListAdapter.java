package com.nador.mobilemed.presentation.function.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nador.mobilemed.data.entity.Video;
import com.nador.mobilemed.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 05/08/16.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    public interface VideoSelectedListener {
        void videoSelected(final Video video);
    }

    private Context mContext;
    private List<Video> mVideoList;
    private int mCurrentSelectedPos = -1;

    private VideoSelectedListener mListener;

    public VideoListAdapter(final Context context, final VideoSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public VideoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_video, parent, false);
        return new VideoListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoListAdapter.ViewHolder holder, int position) {
        final Video video = mVideoList.get(position);

        holder.mVideoLengthTextview.setText(video.getDurationString());
        holder.mIndicatorView.setVisibility(mCurrentSelectedPos == position ? View.VISIBLE : View.INVISIBLE);
        holder.mVideoListItemContainer.setBackgroundColor(mCurrentSelectedPos == position ? Color.BLACK : Color.TRANSPARENT);
        if (video.getThumbnail() != null) {
            holder.mVideoThumbnailImageView.setImageBitmap(video.getThumbnail());
            holder.mVideoThumbnailImageView.setAdjustViewBounds(true);
        }
        // FIXME: do not hardcode
        holder.mVideoTitleTextView.setText("Pfizer");

        holder.mVideoListItemContainer.setOnClickListener(v -> {
            if (mCurrentSelectedPos != position) {
                mCurrentSelectedPos = position;
//                holder.mIndicatorView.setVisibility(View.VISIBLE);
//                holder.mVideoListItemContainer.setBackgroundColor(Color.BLACK);
                mListener.videoSelected(mVideoList.get(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mVideoList == null) ? 0 : mVideoList.size();
    }

    public void setDataList(final List<Video> videoList) {
        mVideoList = videoList;
        if (!videoList.isEmpty()) {
            mCurrentSelectedPos = 0;
            mListener.videoSelected(mVideoList.get(0));
        }
        notifyDataSetChanged();

    }

    public Video getCurrentSelectedVideo() {
        if (mVideoList != null) {
            return mVideoList.get(mCurrentSelectedPos);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.videoListItemContainer)
        ViewGroup mVideoListItemContainer;
        @Bind(R.id.videoThumbnailImageView)
        ImageView mVideoThumbnailImageView;
        @Bind(R.id.videoTitleTextView)
        TextView mVideoTitleTextView;
        @Bind(R.id.videoLengthTextView)
        TextView mVideoLengthTextview;
        @Bind(R.id.indicatorView)
        ViewGroup mIndicatorView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
