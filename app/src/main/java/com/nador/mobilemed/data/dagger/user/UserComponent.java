package com.nador.mobilemed.data.dagger.user;

import com.nador.mobilemed.data.dagger.AppComponent;
import com.nador.mobilemed.presentation.presenter.function.management.AppointmentFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.history.HistorySelectorControllerPresenter;
import com.nador.mobilemed.data.dagger.scopes.UserScope;
import com.nador.mobilemed.data.manager.MeasurementManager;
import com.nador.mobilemed.presentation.presenter.HomeControllerPresenter;
import com.nador.mobilemed.presentation.presenter.contacts.ContactManagementControllerPresenter;
import com.nador.mobilemed.presentation.presenter.contacts.ContactsControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.management.AssessmentFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.management.MedicationFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.BloodpressureFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.GlucometerFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.TemperatureFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.function.measurement.WeightFunctionControllerPresenter;
import com.nador.mobilemed.presentation.presenter.history.HistoryControllerPresenter;

import javax.inject.Named;

import dagger.Component;

/**
 * Created by nador on 27/07/16.
 */
@UserScope
@Component(dependencies = {AppComponent.class}, modules = {UserModule.class})
public interface UserComponent {
    @Named("username") String username();

    MeasurementManager measurementManager();

    void inject(HomeControllerPresenter presenter);
    void inject(HistorySelectorControllerPresenter presenter);

    void inject(ContactsControllerPresenter presenter);
    void inject(ContactManagementControllerPresenter presenter);

    void inject(AppointmentFunctionControllerPresenter presenter);

    void inject(MedicationFunctionControllerPresenter presenter);

    void inject(BloodpressureFunctionControllerPresenter presenter);
    void inject(GlucometerFunctionControllerPresenter presenter);
    void inject(WeightFunctionControllerPresenter presenter);
    void inject(TemperatureFunctionControllerPresenter presenter);
    void inject(AssessmentFunctionControllerPresenter presenter);

    void inject(HistoryControllerPresenter.ECGHistoryControllerPresenter presenter);
    void inject(HistoryControllerPresenter.BloodpressureHistoryControllerPresenter presenter);
    void inject(HistoryControllerPresenter.TemperatureHistoryControllerPresenter presenter);
    void inject(HistoryControllerPresenter.WeightHistoryControllerPresenter presenter);
    void inject(HistoryControllerPresenter.GlucometerHistoryControllerPresenter presenter);
}
