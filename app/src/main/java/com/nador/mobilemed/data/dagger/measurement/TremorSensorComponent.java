package com.nador.mobilemed.data.dagger.measurement;

import com.nador.mobilemed.data.dagger.scopes.MeasurementScope;
import com.nador.mobilemed.presentation.presenter.function.management.TremorAssessmentControllerPresenter;

import dagger.Component;

/**
 * Created by nador on 03/06/16.
 */
@MeasurementScope
@Component(modules = {TremorSensorModule.class})
public interface TremorSensorComponent {
    void inject(TremorAssessmentControllerPresenter presenter);
}
