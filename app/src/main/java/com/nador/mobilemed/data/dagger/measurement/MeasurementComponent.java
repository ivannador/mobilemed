package com.nador.mobilemed.data.dagger.measurement;

import com.nador.mobilemed.data.dagger.scopes.MeasurementScope;
import com.nador.mobilemed.presentation.presenter.function.measurement.BloodpressureMeasurementControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.GlucometerMeasurementControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.TemperatureMeasurementControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.WeightMeasurementControllerPresenter;

import dagger.Component;

/**
 * Created by nador on 28/06/16.
 */
@MeasurementScope
@Component(dependencies = {BluetoothHealthComponent.class}, modules = {MeasurementModule.class})
public interface MeasurementComponent {
    void inject(WeightMeasurementControllerPresenter presenter);
    void inject(BloodpressureMeasurementControllerPresenter presenter);
    void inject(GlucometerMeasurementControllerPresenter presenter);
    void inject(TemperatureMeasurementControllerPresenter presenter);
}
