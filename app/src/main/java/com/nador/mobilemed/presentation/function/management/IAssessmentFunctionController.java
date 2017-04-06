package com.nador.mobilemed.presentation.function.management;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.entity.Video;

import java.util.List;

/**
 * Created by nador on 22/07/16.
 */
public interface IAssessmentFunctionController extends MvpView {
    void setVideoList(final List<Video> videoList);
    void getVideoListFailed();
    void storeResultSuccessful();
    void storeResultFailed();

    void userReauthNeeded();
}
