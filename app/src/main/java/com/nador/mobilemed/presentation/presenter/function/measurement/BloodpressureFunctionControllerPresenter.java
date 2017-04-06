package com.nador.mobilemed.presentation.presenter.function.measurement;

import android.content.Context;

import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.manager.VideoManager;
import com.nador.mobilemed.presentation.function.measurement.IBloodpressureFunctionController;

import javax.inject.Inject;

/**
 * Created by nador on 27/06/16.
 */
public class BloodpressureFunctionControllerPresenter extends MeasurementFunctionControllerPresenter<IBloodpressureFunctionController> {

    @Inject
    VideoManager mVideoManager;

    public BloodpressureFunctionControllerPresenter(final Context context) {
        super(context);
        MobilemedApp.getUserComponent(context).inject(this);
    }

    @Override
    public void getVideoList() {
        getVideoList(mVideoManager);
    }
}
