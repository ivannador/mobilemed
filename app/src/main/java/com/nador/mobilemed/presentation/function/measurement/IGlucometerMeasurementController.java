package com.nador.mobilemed.presentation.function.measurement;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.entity.measurement.BloodGlucoseLevel;

/**
 * Created by nador on 14/07/16.
 */
public interface IGlucometerMeasurementController extends MvpView {
    void processMeasurementResult(final BloodGlucoseLevel bloodPressure);
    void processMeasurementFailed();
    void storeResultSuccessful();
    void storeResultFailed();

    void userReauthNeeded();
}
