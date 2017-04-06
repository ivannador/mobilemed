package com.nador.mobilemed.presentation.function.management;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.nador.mobilemed.data.entity.Video;
import com.nador.mobilemed.presentation.function.base.FunctionController;
import com.nador.mobilemed.presentation.function.base.adapter.VideoListAdapter;
import com.nador.mobilemed.presentation.widget.DividerItemDecoration;
import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.MoodLevel;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.data.entity.measurement.StressLevel;
import com.nador.mobilemed.presentation.MainActivity;
import com.nador.mobilemed.presentation.presenter.function.management.AssessmentFunctionControllerPresenter;
import com.nador.mobilemed.presentation.widget.BulkyDialog;
import com.nador.mobilemed.presentation.widget.BulkySliderDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 22/07/16.
 */
public class AssessmentFunctionController extends FunctionController<IAssessmentFunctionController, AssessmentFunctionControllerPresenter> implements IAssessmentFunctionController, VideoListAdapter.VideoSelectedListener {

    @Bind(R.id.videoView)
    VideoView mVideoView;
    @Bind(R.id.videoListView)
    RecyclerView mVideoListView;
    @Bind(R.id.videoLoadingView)
    ViewGroup mVideoLoadingView;
    @Bind(R.id.thumbnailImageView)
    ImageView mThumbnailImageView;

    @OnClick(R.id.thumbnailImageView)
    protected void onVideoViewTapped() {
        startCurrentVideo();
    }
    @OnClick(R.id.painButton)
    protected void onPainButtonTapped() {
        navigateToAssessmentController(new PainAssessmentController());
    }
    @OnClick(R.id.stressButton)
    protected void onStressButtonTapped() {
        final Context context = getActivity();
        BulkySliderDialog dialog = new BulkySliderDialog(context, context.getString(R.string.ASSESSMENT_DIALOG_HINT_2_STRESS));
        dialog.addAffirmativeAction(getActivity().getString(R.string.ASSESSMENT_DIALOG_BUTTON_SEND), v -> {
            dialog.dismiss();
            assessmentDone(new StressLevel(dialog.getSliderProgress()));
        });
        dialog.addCancelAction(v -> dialog.dismiss());
        dialog.show();
    }
    @OnClick(R.id.moodButton)
    protected void onMoodButtonTapped() {
        final Context context = getActivity();
        BulkySliderDialog dialog = new BulkySliderDialog(context, context.getString(R.string.ASSESSMENT_DIALOG_HINT_2_MOOD));
        dialog.addAffirmativeAction(getActivity().getString(R.string.ASSESSMENT_DIALOG_BUTTON_SEND), v -> {
            dialog.dismiss();
            assessmentDone(new MoodLevel(dialog.getSliderProgress()));
        });
        dialog.addCancelAction(v -> dialog.dismiss());
        dialog.show();
    }
    @OnClick(R.id.tremorButton)
    protected void onTremorButtonTapped() {
        navigateToAssessmentController(new TremorAssessmentController());
    }

    private VideoListAdapter mAdapter;
    private MediaController mMediaController;

    private boolean mAssessmentViewVisible = false;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_assessment, container, false);
    }

    @NonNull
    @Override
    public AssessmentFunctionControllerPresenter createPresenter() {
        return new AssessmentFunctionControllerPresenter(getActivity());
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

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
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mVideoLoadingView.setVisibility(View.VISIBLE);
        getPresenter().getVideoList();
    }

    @Override
    protected void setMainTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.FUNCTION_TITLE_ASSESSMENT));
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
    public void storeResultSuccessful() {
        hideLoading();
        final Context context = getActivity();
        BulkyDialog dialog = new BulkyDialog(context, context.getString(R.string.ASSESSMENT_DIALOG_TEXT_SENT));
        dialog.addAffirmativeAction(getActivity().getString(R.string.ASSESSMENT_DIALOG_BUTTON_OK), v -> {
            dialog.dismiss();
            if (mAssessmentViewVisible) {
                mAssessmentViewVisible = false;
                onBackButtonTapped();
            }
        });
        dialog.show();
    }

    @Override
    public void storeResultFailed() {
        hideLoading();
        Context context = getActivity();
        BulkyDialog dialog = new BulkyDialog(context, context.getString(R.string.ASSESSMENT_DIALOG_TEXT_SENT));
        dialog.addAffirmativeAction(context.getString(R.string.ASSESSMENT_DIALOG_BUTTON_OK), v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void assessmentDone(final Result result) {
        showLoading();
        getPresenter().storeAssessment(result);
    }

    public void assessmentDone(final ArrayList<Result> resultList) {
        showLoading();
        getPresenter().storeAssessmentList(resultList);
    }

    private void navigateToAssessmentController(final Controller controller) {
        mBackButton.setText(getActivity().getString(R.string.FUNCTION_BUTTON_BACK));
        mAssessmentViewVisible = true;
        getChildRouter(mContainerLayout, null).setPopsLastView(true).pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void setVideoList(final List<Video> videoList) {
        mVideoLoadingView.setVisibility(View.GONE);
        mAdapter.setDataList(videoList);

    }

    @Override
    public void getVideoListFailed() {
        mVideoLoadingView.setVisibility(View.GONE);
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

    @Override
    public void userReauthNeeded() {
        ((MainActivity) getActivity()).showSplashForReauth();
    }
}
