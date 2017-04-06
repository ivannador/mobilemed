package com.nador.mobilemed.presentation.function.measurement;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.entity.measurement.BloodPressure;

/**
 * Created by nador on 27/06/16.
 */
public interface IBloodpressureMeasurementController extends MvpView {
    void processMeasurementResult(final BloodPressure bloodPressure);
    void processMeasurementFailed();
    void storeResultSuccessful();
    void storeResultFailed();

    void userReauthNeeded();
}
