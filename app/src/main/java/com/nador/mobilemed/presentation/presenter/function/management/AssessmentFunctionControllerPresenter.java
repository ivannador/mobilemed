package com.nador.mobilemed.presentation.presenter.function.management;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.nador.mobilemed.data.entity.Video;
import com.nador.mobilemed.data.manager.MeasurementManager;
import com.nador.mobilemed.data.manager.VideoManager;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.presentation.function.management.IAssessmentFunctionController;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 22/07/16.
 */
public class AssessmentFunctionControllerPresenter extends SubscribingPresenter<IAssessmentFunctionController> {

    @Inject
    VideoManager mVideoManager;
    @Inject
    MeasurementManager mMeasurementManager;

    private Context mContext;

    public AssessmentFunctionControllerPresenter(final Context context) {
        mContext = context;
        MobilemedApp.getUserComponent(context).inject(this);
    }

    // TODO
    public void getVideoList() {
        Uri videoURI = mVideoManager.getVideoURI();
        List<Video> videoList = new ArrayList<>();
        // Video constructor has some lengthy operations, so do it on io thread
        Observable.fromCallable(() -> videoList.add(new Video(mContext, videoURI)))
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(aBoolean -> {
                    if (isViewAttached()) {
                        if (aBoolean) {
                            getView().setVideoList(videoList);
                        } else {
                            getView().getVideoListFailed();
                        }
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().getVideoListFailed();
                    }
                });
    }

    public void storeAssessment(final Result assessmentResult) {
        mMeasurementManager.storeResult(assessmentResult)
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(aBoolean -> {
                    if (isViewAttached()) {
                        if (aBoolean) {
                            getView().storeResultSuccessful();
                        } else {
                            getView().storeResultFailed();
                        }
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        Timber.e(throwable, "Store error");
                        if (throwable instanceof NotAuthorizedException) {
                            getView().userReauthNeeded();
                        } else {
                            getView().storeResultFailed();
                        }
                    }
                });
    }

    public void storeAssessmentList(final ArrayList<Result> assessmentResult) {
        mMeasurementManager.storeResultList(assessmentResult)
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(aBoolean -> {
                    if (isViewAttached()) {
                        if (aBoolean) {
                            getView().storeResultSuccessful();
                        } else {
                            getView().storeResultFailed();
                        }
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        Timber.e(throwable, "Store error");
                        if (throwable instanceof NotAuthorizedException) {
                            getView().userReauthNeeded();
                        } else {
                            getView().storeResultFailed();
                        }
                    }
                });
    }
}
