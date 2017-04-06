package com.nador.mobilemed.presentation.presenter.function.measurement;

import android.content.Context;

import com.nador.mobilemed.presentation.function.measurement.IGlucometerFunctionController;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.manager.VideoManager;

import javax.inject.Inject;

/**
 * Created by nador on 14/07/16.
 */
public class GlucometerFunctionControllerPresenter extends MeasurementFunctionControllerPresenter<IGlucometerFunctionController> {

    @Inject
    VideoManager mVideoManager;

    public GlucometerFunctionControllerPresenter(final Context context) {
        super(context);
        MobilemedApp.getUserComponent(context).inject(this);
    }

    @Override
    public void getVideoList() {
        getVideoList(mVideoManager);
    }
}
