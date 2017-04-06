package com.nador.mobilemed.presentation.history;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.entity.measurement.Result;

import java.util.List;

/**
 * Created by nador on 29/07/16.
 */
public interface IHistoryController extends MvpView {
    void setResultList(final List<Result> resultList);

    interface IECGHistoryController extends IHistoryController {
        void recordECGMeasurementsDone();
        void recordECGMeasurementsError();

        void pulseCalculated();
    }

    void userReauthNeeded();
}
