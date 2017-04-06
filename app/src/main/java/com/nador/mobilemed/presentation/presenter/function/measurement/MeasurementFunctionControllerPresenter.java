package com.nador.mobilemed.presentation.presenter.function.measurement;

import android.content.Context;
import android.net.Uri;

import com.nador.mobilemed.data.entity.Video;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.presentation.function.base.IMeasurementFunctionController;
import com.nador.mobilemed.data.manager.VideoManager;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by nador on 27/06/16.
 */
public abstract class MeasurementFunctionControllerPresenter<V extends IMeasurementFunctionController> extends SubscribingPresenter<V> {

    private Context mContext;

    public MeasurementFunctionControllerPresenter(final Context context) {
        mContext = context;
    }

    public abstract void getVideoList();

    protected void getVideoList(final VideoManager manager) {
        List<Video> videoList = new ArrayList<>();
        // Video constructor has some lengthy operations, so do it on io thread
        Observable.fromCallable(() -> {
            Uri videoURI = manager.getVideoURI();
            return videoList.add(new Video(mContext, videoURI));
        }).compose(ReactiveExtensions.applyIOSchedulers())
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
}
