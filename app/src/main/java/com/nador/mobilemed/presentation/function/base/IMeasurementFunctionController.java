package com.nador.mobilemed.presentation.function.base;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.entity.Video;

import java.util.List;

/**
 * Created by nador on 27/06/16.
 */
public interface IMeasurementFunctionController extends MvpView {
    void measurementDone();
    void setVideoList(final List<Video> videoList);
    void getVideoListFailed();
}
