package com.nador.mobilemed.presentation.function.base;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.nador.mobilemed.data.entity.Video;
import com.nador.mobilemed.presentation.function.base.adapter.VideoListAdapter;
import com.nador.mobilemed.presentation.presenter.function.measurement.MeasurementFunctionControllerPresenter;
import com.nador.mobilemed.presentation.widget.DividerItemDecoration;
import com.nador.mobilemed.presentation.widget.FunctionButton;
import com.nador.mobilemed.R;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 27/06/16.
 */
public abstract class MeasurementFunctionController<V extends IMeasurementFunctionController, P extends MeasurementFunctionControllerPresenter<V>> extends FunctionController<V, P> implements IMeasurementFunctionController, VideoListAdapter.VideoSelectedListener {

    @Bind(R.id.measurementTitleTextView)
    protected TextView mMeasurementTitleTextView;
    @Bind(R.id.methodHintTextView)
    protected TextView mMethodHintTextView;
    @Bind(R.id.automaticMethodTextView)
    protected TextView mAutomaticMethodTextView;
    @Bind(R.id.manualMethodTextView)
    protected TextView mManualMethodTextView;
    @Bind(R.id.tutorialTextView)
    protected TextView mTutorialTextView;
    @Bind(R.id.automaticButton)
    protected FunctionButton mAutomaticButton;
    @Bind(R.id.manualButton)
    protected FunctionButton mManualButton;
    @Bind(R.id.tutorialButton)
    protected FunctionButton mTutorialButton;

    @Bind(R.id.videoView)
    VideoView mVideoView;
    @Bind(R.id.videoListView)
    RecyclerView mVideoListView;
    @Bind(R.id.thumbnailImageView)
    ImageView mThumbnailImageView;

    @OnClick(R.id.thumbnailImageView)
    protected void onVideoViewTapped() {
        startCurrentVideo();
    }

    @OnClick(R.id.automaticButton)
    protected void onAutomaticButtonTapped() {
        if (isChildInTransition()) {
            return;
        }
        navigateToAutomaticMeasurement();
    }
    @OnClick(R.id.manualButton)
    protected void onManualButtonTapped() {
        if (isChildInTransition()) {
            return;
        }
        navigateToManualMeasurement();
    }
    @OnClick(R.id.tutorialButton)
    protected void onTutorialButtonTapped() {
        if (isChildInTransition()) {
            return;
        }
    }

    protected VideoListAdapter mAdapter;

    private MediaController mMediaController;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_measurement_function, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        initLayout();

        getChildRouter(mContainerLayout, null).addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(Controller to, Controller from, boolean isPush, ViewGroup container, ControllerChangeHandler handler) {
                setChildInTransition(true);
            }

            @Override
            public void onChangeCompleted(Controller to, Controller from, boolean isPush, ViewGroup container, ControllerChangeHandler handler) {
                setChildInTransition(false);
            }
        });
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        showLoading();
        getPresenter().getVideoList();
    }

    @Override
    public void measurementDone() {
        onBackButtonTapped();
    }

    protected abstract void setAutomaticTitle();
    protected abstract void setManualTitle();

    protected void navigateToAutomaticMeasurement() {
        mBackButton.setText(getActivity().getString(R.string.FUNCTION_BUTTON_BACK));
        setAutomaticTitle();
    }
    protected void navigateToManualMeasurement() {
        mBackButton.setText(getActivity().getString(R.string.FUNCTION_BUTTON_BACK));
        setManualTitle();
    }

    private void initLayout() {
        Context context = getActivity();
        mMethodHintTextView.setText(Html.fromHtml(context.getString(R.string.FUNCTION_TEXT_HINT_METHOD)));
        mAutomaticMethodTextView.setText(Html.fromHtml(context.getString(R.string.FUNCTION_TEXT_HINT_AUTOMATIC)));
        mManualMethodTextView.setText(Html.fromHtml(context.getString(R.string.FUNCTION_TEXT_HINT_MANUAL)));
        mTutorialTextView.setText(Html.fromHtml(context.getString(R.string.FUNCTION_TEXT_HINT_TUTORIAL)));

        mAutomaticButton.setActive();
        mManualButton.setActive();
        mTutorialButton.setActive();

        mVideoListView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_divider_simple)));
        mVideoListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new VideoListAdapter(getActivity(), this);
        mVideoListView.setAdapter(mAdapter);

//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mMediaController = new MediaController(getActivity());
        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);
    }

    @Override
    public void videoSelected(Video video) {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
        Drawable[] layers = new Drawable[2];
        layers[0] = new BitmapDrawable(getResources(), video.getThumbnail());
        layers[1] = ContextCompat.getDrawable(getActivity(), R.drawable.button_video_play);
        ((BitmapDrawable) layers[1]).setGravity(Gravity.CENTER);
        LayerDrawable drawable = new LayerDrawable(layers);
        mThumbnailImageView.setImageDrawable(drawable);
        mThumbnailImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVideoList(final List<Video> videoList) {
        hideLoading();
        mAdapter.setDataList(videoList);
    }

    @Override
    public void getVideoListFailed() {
        hideLoading();
    }

    public void startCurrentVideo() {
        Video video = mAdapter.getCurrentSelectedVideo();
        if (video != null && !mVideoView.isPlaying()) {
            mThumbnailImageView.setVisibility(View.GONE);
            mVideoView.setVideoURI(video.getURI());
            mVideoView.requestFocus();
            mVideoView.start();
        }
    }
}
